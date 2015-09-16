/*
 * Copyright (c) 2004-2005 Auster Solutions. All Rights Reserved.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Created on May 3, 2005
 */
package br.com.auster.common.datastruct;

import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;

/**
 * <p><b>Title:</b> RangeMap</p>
 * <p><b>Description:</b> A class that maps ranges of longs to lists of objects</p>
 * <p><b>Copyright:</b> Copyright (c) 2004-2005 </p>
 * <p><b>Company:</b> Auster Solutions </p>
 * 
 * This class will map intervals of longs to a list of values. 
 * Example:
 * 
 *  [ 10, 50 ) => { "A", "B", "C" }
 *  
 * This is a map from range 10 inclusive to 50 exclusive to a list of strings with strings 
 * "A", "B" and "C".
 * 
 * If you execute a rangeMap.get(32) for example, it will know that 32 is in the above 
 * interval and it will return the associated list.
 * 
 * Internally this class uses a red black tree to do the mapping with an efficient insert
 * (O(log n)), and efficient get.
 * 
 * It implements almost all methods regular maps implements, but it does not implement the
 * Map interface itself. 
 * 
 * This class is not synchronized and as so, does not support concurrent calls.
 *  
 * @author etirelli
 * @version $Id: RangeMap.java 304 2005-11-01 18:31:11Z etirelli $
 */
public class RangeMap {
  // -------------------------------------
  // Attributes  
  // -------------------------------------
	
  public static final int UNLIMITED_RECURSIONS = Integer.MAX_VALUE;
  
  
  private transient int modCount = 0;
  protected RangeNode root;
  protected int size = 0;
  // recursions control
  protected int maxRecursions;
  protected boolean exaustedException;
  
  
  
  private boolean rangeAddOverlap = false;
  // An ordered set of ranges where overlap occured over several add() calls
  private Set overlappingRanges = new TreeSet(new Comparator() {
	public int compare(Object o1, Object o2) {
		RangeEntry entry1 = (RangeEntry) o1;
		RangeEntry entry2 = (RangeEntry) o2;
		return (entry1.getFrom() < entry2.getFrom()) ? -1 :
			   (entry1.getFrom() > entry2.getFrom()) ? 1 : 0;
	}
  });
  
  // -------------------------------------
  // Inner classes  
  // -------------------------------------
  private class EntryIterator extends PrivateEntryIterator {
    public Object next() {
      return nextEntry();
    }
  }

  private abstract class PrivateEntryIterator implements Iterator {
    private int expectedModCount = RangeMap.this.modCount;

    private RangeNode lastReturned = null;

    RangeNode next;

    PrivateEntryIterator() {
      next = firstEntry();
    }

    PrivateEntryIterator(RangeNode first) {
      next = first;
    }

    public boolean hasNext() {
      return next != null;
    }

    final RangeEntry nextEntry() {
      if (next == null)
        throw new NoSuchElementException();
      if (modCount != expectedModCount)
        throw new ConcurrentModificationException();
      lastReturned = next;
      next = successor(next);
      return lastReturned.getEntry();
    }

    public void remove() {
      if (lastReturned == null)
        throw new IllegalStateException();
      if (modCount != expectedModCount)
        throw new ConcurrentModificationException();
      if (lastReturned.left != null && lastReturned.right != null)
        next = lastReturned;
      deleteNode(lastReturned);
      expectedModCount++;
      lastReturned = null;
    }
  }


  // -------------------------------------
  // Constructors
  // -------------------------------------
  /**
   * Default constructor
   */
  public RangeMap() {
	  this(UNLIMITED_RECURSIONS, true);
  }
  
  public RangeMap(int _maxRecursions) {
	  this(_maxRecursions, true);
  }

  public RangeMap(boolean _throwEx) {
	  this(UNLIMITED_RECURSIONS, _throwEx);
  }
  
  public RangeMap(int _maxRecursions, boolean _throwEx) {
	  this.exaustedException = _throwEx;
	  this.maxRecursions = Math.abs(_maxRecursions);
  }

  
  
  // -------------------------------------
  // Public methods
  // -------------------------------------
  /**
   * <P>Adds a mapping from range [ "from", "to") to "value". </P> 
   * <P>If the range already exists, it will just add the value to the already 
   * existing list of values. </P>
   * <P>If the range overlaps integrally or partially with existing ranges, it
   * will break it as much as needed to keep the exactly match of intervals to 
   * lists of values. Example:</P>
   * 
   * <P>If you have a RangeMap with the following range:</P>
   * 
   * [ 10, 20 ) => "A"
   * 
   * <P>And you add the range [ 15, 25 ) => "B", the result will be:</P>
   * 
   * [ 10, 15 ) => "A"
   * [ 15, 20 ) => "A", "B"
   * [ 20, 25 ) => "B"
   * 
   * An exception will be raised if someone tries to add a range with end 
   * lesser than or equals to start, like [ 10, 10 ).
   * 
   * @param from  range starting value (inclusive)
   * @param to    range ending   value (exclusive)
   * @param value the value to add associated to the current range
   * 
   * @return true if any overlap occured between the new range and the previously existing ranges
   * 
   * @exception InvalidRangeException in case "to" is lesses or equal to "from" parameter
   */
  public boolean add(long from, long to, Object value) {
	rangeAddOverlap = false;
	if(to <= from) {
		throw new InvalidRangeException(
				"Trying to add an invalid range to the RangeMap(): [ "+from+", "+to+")");
	}
	
	// if any overlap occurs, the private attribute "rangeAddOverlap" will 
	// be set in the following call
    this.add(from, to, value, false, 0);
    
    return rangeAddOverlap;
  }

  /**
   * The same as method {@link #add(long, long, Object)} but adding a list of 
   * values instead of a single value.
   * 
   * @param from the range starting value (inclusive)
   * @param to   the range final value (exclusive)
   * @param values a list of values to be assigned to the range
   * 
   * @return true if any overlap occured between the new range and the previously existing ranges
   * 
   * @exception InvalidRangeException in case "to" is lesses or equal to "from" parameter
   */
  public boolean addAll(long from, long to, List values) {
	rangeAddOverlap = false;
	if(to <= from) {
		throw new InvalidRangeException(
				"Trying to add an invalid range to the RangeMap(): [ "+from+", "+to+")");
	}
	// if any overlap occurs, the private attribute "rangeAddOverlap" will 
	// be set in the following call
    this.add(from, to, values, true, 0);
    
    return rangeAddOverlap;
  }

  /**
   * Clears the Range Map erasing all ranges previously in the map
   */
  public void clear() {
    modCount++;
    size = 0;
    root = null;
    this.overlappingRanges.clear();
  }

  /**
   * Returns the list of values associated to the range in which the 
   * key is included or null in case there is no range with the given
   * key.
   * 
   * @param key
   * @return a list with values or null if the range does not exist
   */
  public List get(long key) {
    RangeNode node = getNode(key);
    return (node == null ? null : node.getEntry().getValues());
  }
  
  /**
   * Removes the range containing the given key.
   * 
   * @param key
   * @return the list of elements previously associated with that range
   */
  public List removeRange(long key) {
    List values = null;
    RangeNode node = getNode(key);
    if(node != null) {
      values = node.getValues(); 
      deleteNode(node);
    }
    return values;
  }

  /**
   * Returns the number of elements (ranges) in the RangeMap. It is important to 
   * note that in cases of overlapping ranges being added to the map, a different
   * number of ranges can be created than the number of ranges added to the map. 
   * See the example in the {@link #add(long, long, Object)} method for more informations.
   * 
   * @return the current map size or 0 for empty map
   */
  public int getSize() {
    return this.size;
  }

  /**
   * Returns an iterator to this map. It is garanteed that the iterator will 
   * iterate through the map in ascending order of the ranges.
   * 
   * @return Iterator
   */
  public Iterator iterator() {
    return new EntryIterator();
  }

  /**
   * Returns true in case the last add all caused a range overlap
   * @return
   */
  public boolean isLastAddOverlap() {
	  return this.rangeAddOverlap;
  }
  
  /**
   * Returns an unmodifiable set of current overlapping ranges
   * @return
   */
  public Set getOverlappingRanges() {
	  return Collections.unmodifiableSet(this.overlappingRanges);
  }
  
  // -------------------------------------
  // Private / Protected methods
  // -------------------------------------
  
  /**
   * Add a value or a list of values associated to the current range. 
   * See {@link #add(long, long, Object)} for further explanation.
   */
  private RangeEntry add(long from, long to, Object value, boolean isAddAll, int _recursion) {
	  
    RangeNode pointer = root;
    boolean found = false;

    _recursion++;
	if (_recursion > this.maxRecursions) {
		if (this.exaustedException) {
			throw new RangeRecursionExaustedException(this.maxRecursions);
		} else {
			return (pointer == null ? null : pointer.getEntry());
		}
	}

    if (pointer == null) {
      // if tree is empty, just create an element for it
      this.incrementSize();
      root = new RangeNode(from, to);
      pointer = root;
      if (isAddAll) {
        pointer.getValues().addAll((List) value);
      } else {
        pointer.addValue(value);
      }
    } else {
      // if tree is not empty, find the place for this range
      while (!found) {
        if (from == pointer.getFrom()) {
          // if start matches with a predefined range start, 
          // add elements to it and fix ranges if needed 
          fixRanges(from, to, value, isAddAll, pointer, _recursion);
          if (isAddAll) {
            pointer.getValues().addAll((List) value);
          } else {
            pointer.addValue(value);
          }
          found = true;
          this.rangeAddOverlap = true;
          this.overlappingRanges.add(pointer.getEntry());
        } else if (from < pointer.getFrom()) {
          // if the range should be placed before current node, 
          if (pointer.getLeft() != null) {
        	// if there is a previous child element, move current pointer to it
            pointer = pointer.getLeft();
          } else {
        	// if not, add the range there
            this.incrementSize();
            pointer.setLeft(new RangeNode(from, to, pointer));
            pointer = pointer.getLeft();
            if (isAddAll) {
              pointer.getValues().addAll((List) value);
            } else {
              pointer.addValue(value);
            }
            fixAfterInsertion(pointer);
            fixRanges(from, to, value, isAddAll, pointer, _recursion);
            found = true;
          }
        } else { // from > pointer.getFrom()
          if (pointer.getRight() != null) {
            pointer = pointer.getRight();
          } else {
            incrementSize();
            pointer.setRight(new RangeNode(from, to, pointer));
            pointer = pointer.getRight();
            if (isAddAll) {
              pointer.getValues().addAll((List) value);
            } else {
              pointer.addValue(value);
            }
            fixAfterInsertion(pointer);
            fixRanges(from, to, value, isAddAll, pointer, _recursion);
            found = true;
          }
        }
      }
    }
    return pointer.getEntry();
  }

  /**
   * Returns the color of the given node, returning BLACK in 
   * case the node is null.
   * 
   * @param node
   * @return
   */
  private boolean colorOf(RangeNode node) {
    return (node == null ? RangeNode.BLACK : node.getColor());
  }

  /**
   * Decrement the size of the map
   */
  protected void decrementSize() {
    this.size--;
    this.modCount++;
  }

  /**
   * Removes the given node from the map
   * @param node
   */
  private void deleteNode(RangeNode node) {
    decrementSize();
    overlappingRanges.remove(node.getEntry());

    if (node.left != null && node.right != null) {
      RangeNode s = successor(node);
      node.entry = s.entry;
      node = s;
    } 

    RangeNode replacement = (node.left != null ? node.left : node.right);

    if (replacement != null) {
      replacement.parent = node.parent;
      if (node.parent == null)
        root = replacement;
      else if (node == node.parent.left)
        node.parent.left = replacement;
      else
        node.parent.right = replacement;

      node.left = node.right = node.parent = null;

      if (node.color == RangeNode.BLACK)
        fixAfterDeletion(replacement);
    } else if (node.parent == null) { 
      root = null;
    } else { 
      if (node.color == RangeNode.BLACK)
        fixAfterDeletion(node);

      if (node.parent != null) {
        if (node == node.parent.left)
          node.parent.left = null;
        else if (node == node.parent.right)
          node.parent.right = null;
        node.parent = null;
      }
    }
  }

  /**
   * Returns the first node in the RangeMap
   * Returns null if the TreeMap is empty.
   */
  private RangeNode firstEntry() {
    RangeNode node = root;
    if (node != null)
      while (node.left != null)
        node = node.left;
    return node;
  }

  /**
   * Rebalance the internal Red Black Tree after a node removal
   * @param node
   */
  private void fixAfterDeletion(RangeNode node) {
    while (node != root && colorOf(node) == RangeNode.BLACK) {
      if (node == leftOf(parentOf(node))) {
        RangeNode sib = rightOf(parentOf(node));

        if (colorOf(sib) == RangeNode.RED) {
          setColor(sib, RangeNode.BLACK);
          setColor(parentOf(node), RangeNode.RED);
          rotateLeft(parentOf(node));
          sib = rightOf(parentOf(node));
        }

        if (colorOf(leftOf(sib)) == RangeNode.BLACK
            && colorOf(rightOf(sib)) == RangeNode.BLACK) {
          setColor(sib, RangeNode.RED);
          node = parentOf(node);
        } else {
          if (colorOf(rightOf(sib)) == RangeNode.BLACK) {
            setColor(leftOf(sib), RangeNode.BLACK);
            setColor(sib, RangeNode.RED);
            rotateRight(sib);
            sib = rightOf(parentOf(node));
          }
          setColor(sib, colorOf(parentOf(node)));
          setColor(parentOf(node), RangeNode.BLACK);
          setColor(rightOf(sib), RangeNode.BLACK);
          rotateLeft(parentOf(node));
          node = root;
        }
      } else { // symmetric
        RangeNode sib = leftOf(parentOf(node));

        if (colorOf(sib) == RangeNode.RED) {
          setColor(sib, RangeNode.BLACK);
          setColor(parentOf(node), RangeNode.RED);
          rotateRight(parentOf(node));
          sib = leftOf(parentOf(node));
        }

        if (colorOf(rightOf(sib)) == RangeNode.BLACK
            && colorOf(leftOf(sib)) == RangeNode.BLACK) {
          setColor(sib, RangeNode.RED);
          node = parentOf(node);
        } else {
          if (colorOf(leftOf(sib)) == RangeNode.BLACK) {
            setColor(rightOf(sib), RangeNode.BLACK);
            setColor(sib, RangeNode.RED);
            rotateLeft(sib);
            sib = leftOf(parentOf(node));
          }
          setColor(sib, colorOf(parentOf(node)));
          setColor(parentOf(node), RangeNode.BLACK);
          setColor(leftOf(sib), RangeNode.BLACK);
          rotateRight(parentOf(node));
          node = root;
        }
      }
    }

    setColor(node, RangeNode.BLACK);
  }

  /**
   * Rebalances the internal Red Black Tree after the insert of a new range
   * @param node
   */
  private void fixAfterInsertion(RangeNode node) {
    node.setColor(RangeNode.RED);

    while (node != null && node != root
        && node.getParent().getColor() == RangeNode.RED) {
      if (parentOf(node) == leftOf(parentOf(parentOf(node)))) {
        RangeNode uncle = rightOf(parentOf(parentOf(node)));
        if (colorOf(uncle) == RangeNode.RED) {
          setColor(parentOf(node), RangeNode.BLACK);
          setColor(uncle, RangeNode.BLACK);
          setColor(parentOf(parentOf(node)), RangeNode.RED);
          node = parentOf(parentOf(node));
        } else {
          if (node == rightOf(parentOf(node))) {
            node = parentOf(node);
            rotateLeft(node);
          }
          setColor(parentOf(node), RangeNode.BLACK);
          setColor(parentOf(parentOf(node)), RangeNode.RED);
          if (parentOf(parentOf(node)) != null)
            rotateRight(parentOf(parentOf(node)));
        }
      } else {
        RangeNode uncle = leftOf(parentOf(parentOf(node)));
        if (colorOf(uncle) == RangeNode.RED) {
          setColor(parentOf(node), RangeNode.BLACK);
          setColor(uncle, RangeNode.BLACK);
          setColor(parentOf(parentOf(node)), RangeNode.RED);
          node = parentOf(parentOf(node));
        } else {
          if (node == leftOf(parentOf(node))) {
            node = parentOf(node);
            rotateRight(node);
          }
          setColor(parentOf(node), RangeNode.BLACK);
          setColor(parentOf(parentOf(node)), RangeNode.RED);
          if (parentOf(parentOf(node)) != null)
            rotateLeft(parentOf(parentOf(node)));
        }
      }
    }
    root.setColor(RangeNode.BLACK);
  }

  /**
   * Fixes ranges to asure the internal tree properties are met. This is,
   * no overlapping ranges and the correct list of values for each range
   * 
   * @param from
   * @param to
   * @param value
   * @param isAddAll
   * @param node
   */
  private void fixRanges(long from, long to, Object value, boolean isAddAll,
      RangeNode node, int _recursion) {

    // Node já existe, não criou novo
    if (to != node.getTo()) {
      long newFrom = Math.min(to, node.getTo());
      long newTo = Math.max(to, node.getTo());
      node.setTo(newFrom);
      if (to > newFrom) {
        add(newFrom, newTo, value, isAddAll, _recursion);
      } else {
        addAll(newFrom, newTo, node.getValues());
      }
      this.rangeAddOverlap = true;
      this.overlappingRanges.add(node.getEntry());
    } else {
      RangeNode pred = predecessor(node);
      if ((pred != null) && (pred.getTo() > node.getFrom())) {
        long newFrom = Math.min(pred.getTo(), node.getTo());
        long newTo = Math.max(pred.getTo(), node.getTo());
        pred.setTo(node.getFrom());
        if (node.getTo() > newFrom) {
          node.setTo(newFrom);
          add(newFrom, newTo, value, isAddAll, _recursion);
        } else if (newFrom != newTo) {
          addAll(newFrom, newTo, pred.getValues());
        }
        node.getValues().addAll(pred.getValues());
        this.rangeAddOverlap = true;
        this.overlappingRanges.add(node.getEntry());
      } else {
        RangeNode succ = successor(node);
        if ((succ != null) && (succ.getFrom() < node.getTo())) {
          node.setTo(succ.getFrom());
          add(succ.getFrom(), to, value, isAddAll, _recursion);
        }
      }
    }
  }

  /**
   * Returns the range that contains the given key or null if no
   * range contains the key
   * 
   * @param key
   * @return
   */
  private RangeNode getNode(long key) {
    RangeNode node = root;
    RangeNode previous = null;
    while (node != null) {
      if (key == node.getFrom()) {
        previous = node;
        node = null;
      } else if (key < node.getFrom()) {
        node = node.left;
      } else {
        previous = node;
        node = node.right;
      }
    }
    return ((previous != null) && (previous.getTo() > key)) ? previous : null;
  }

  protected void incrementSize() {
    this.size++;
    this.modCount++;
  }

//  /**
//   * Returns the last node in the RangeMap. Returns null if the TreeMap is empty.
//   */
//  private RangeNode lastEntry() {
//    RangeNode node = root;
//    if (node != null)
//      while (node.right != null)
//        node = node.right;
//    return node;
//  }

  /**
   * Returns the left child of the given node or null in case node is null.
   * 
   * @param node
   * @return
   */
  private RangeNode leftOf(RangeNode node) {
    return (node == null) ? null : node.getLeft();
  }

  /**
   * Returns the right child of the given node or null in case node is null.
   * 
   * @param p
   * @return
   */
  private RangeNode rightOf(RangeNode p) {
    return (p == null) ? null : p.getRight();
  }

  /**
   * Returns the parent of the given node or null in case node is null.
   * 
   * @param node
   * @return
   */
  private RangeNode parentOf(RangeNode p) {
    return (p == null ? null : p.getParent());
  }

  /**
   * Returns the predecessor of the given node, or null if no such.
   */
  private RangeNode predecessor(RangeNode current) {
    if (current == null)
      return null;
    else if (current.left != null) {
      RangeNode node = current.left;
      while (node.right != null)
        node = node.right;
      return node;
    } else {
      RangeNode node = current.parent;
      RangeNode child = current;
      while (node != null && child == node.left) {
        child = node;
        node = node.parent;
      }
      return node;
    }
  }

  /**
   * Does the prepare step (rotate to left) before rebalancing the tree
   * @param node
   */
  private void rotateLeft(RangeNode node) {
    RangeNode r = node.right;
    node.right = r.left;
    if (r.left != null)
      r.left.parent = node;
    r.parent = node.parent;
    if (node.parent == null)
      root = r;
    else if (node.parent.left == node)
      node.parent.left = r;
    else
      node.parent.right = r;
    r.left = node;
    node.parent = r;
  }

  /**
   * Does the prepare step (rotate to right) before rebalancing the tree
   * @param node
   */
  private void rotateRight(RangeNode node) {
    RangeNode l = node.left;
    node.left = l.right;
    if (l.right != null)
      l.right.parent = node;
    l.parent = node.parent;
    if (node.parent == null)
      root = l;
    else if (node.parent.right == node)
      node.parent.right = l;
    else
      node.parent.left = l;
    l.right = node;
    node.parent = l;
  }

  /**
   * Sets the color of the node, in case the node is not null
   * @param p
   * @param c
   */
  private void setColor(RangeNode p, boolean c) {
    if (p != null)
      p.setColor(c);
  }

  /**
   * Returns the successor of the specified Entry, or null if no such.
   */
  private RangeNode successor(RangeNode current) {
    if (current == null)
      return null;
    else if (current.right != null) {
      RangeNode node = current.right;
      while (node.left != null)
        node = node.left;
      return node;
    } else {
      RangeNode node = current.parent;
      RangeNode child = current;
      while (node != null && child == node.right) {
        child = node;
        node = node.parent;
      }
      return node;
    }
  }

//  /**
//   * Returns true in case both objects are equal or both are null
//   * @param o1
//   * @param o2
//   * @return
//   */
//  private boolean valEquals(Object o1, Object o2) {
//    return (o1 == null ? o2 == null : o1.equals(o2));
//  }


}
