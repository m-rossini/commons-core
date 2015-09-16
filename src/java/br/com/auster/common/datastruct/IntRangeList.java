/*
 * Copyright (c) 2006 Auster Solutions. All Rights Reserved.
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
 * Created on Nov 20, 2006
 */
package br.com.auster.common.datastruct;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Represents a list of <code>int</code> ranges ("from","to"). Both start and
 * end limits of the range are inclusive.
 *
 * <p>
 * Despite the name, this class is not an implementation of the
 * <code>java.util.List</code> interface - neither is a
 * <code>java.util.Collection</code>.
 *
 * <p>
 * Instances of this classes will maintain a list of ranges without overlaps.
 * This is done by checking each added node for duplication or overlapping and,
 * if detected, moving them to a separate list and creating a new control range
 * to represent the whole overlapped range.
 *
 * <p>
 * Additionally, the range nodes may hold a related object for convenience. See
 * {@link IntRangeNode} for a full description of a range.
 *
 * <p>
 * The list of ranges is backed by a LinkedList and the range nodes are always
 * sorted in ascending order according to it's "from" value.
 *
 * <p>
 * After a series of {@link #add(int, int, Object)} calls, you may use the
 * {@link #getOverlaps()} method to fetch all nodes that overlapped with each
 * other. There is no way to know which node overlapped with any other node, but
 * you can call the {@link #getSortedOverlaps()} method to get a sorted list in
 * ascending order according to the "from" values (similar to the main range
 * node list). Another option is to use {@link #getSortedOverlaps(Comparator)}
 * by providing an {@link IntRangeNode} comparator to sort the list.
 *
 * <p>
 * <strong>Note that this implementation is not synchronized.</strong> If
 * multiple threads access an <tt>IntRangeList</tt> instance concurrently, and
 * at least one of the threads modifies the list structurally, it <i>must</i>
 * be synchronized externally. (A structural modification is any operation that
 * adds or deletes one or more elements, or explicitly resizes the backing
 * array; merely setting the value of an element is not a structural
 * modification.) This is typically accomplished by synchronizing on some object
 * that naturally encapsulates the list.
 *
 * @author rbarone
 * @version $Id$
 */
public class IntRangeList {

  /**
   * Default range node comparator for sorting.
   *
   * @see IntRangeComparator
   */
  protected static Comparator<IntRangeNode> DEFAULT_COMPARATOR = new IntRangeComparator();

  /**
   * Unordered list of overlapped range nodes.
   */
  protected List<IntRangeNode> overlaps = new ArrayList<IntRangeNode>();

  /**
   * Ordered list of range nodes.
   */
  protected LinkedList<IntRangeNode> nodes = new LinkedList<IntRangeNode>();

  /**
   * Internal control variables to aid in node searching.
   */
  private int smallestFrom, highestFrom, length;


  /**
   * Default constructor - just creates a new instance.
   */
  public IntRangeList() {
    // does nothing
  }

  /**
   * Returns an unmodifiable list view of the current range nodes.
   *
   * @return a list with the current range nodes - it is never <code>null</code>.
   */
  public List<IntRangeNode> getNodes() {
    return Collections.unmodifiableList(this.nodes);
  }

  /**
   * Returns an unmodifiable list view of the current overlapped/duplicated
   * nodes.
   *
   * @return a list with the current overlapped nodes - it is never
   *         <code>null</code>.
   */
  public List<IntRangeNode> getOverlaps() {
    return Collections.unmodifiableList(this.overlaps);
  }

  /**
   * Returns a sorted list of the current overlapped/duplicated nodes.
   *
   * <p>
   * The nodes will be sorted in ascendant order according to the "from" value.
   *
   * @return a sorted set with the current overlapped nodes - it is never
   *         <code>null</code>.
   */
  public List<IntRangeNode> getSortedOverlaps() {
    return getSortedOverlaps(DEFAULT_COMPARATOR);
  }

  /**
   * Returns a sorted list of the current overlapped/duplicated nodes.
   *
   * <p>
   * The nodes will be sorted according to the provided comparator.
   *
   * @param comparator
   *          comparator that will be used to sort the nodes.
   * @return A sorted set with the current overlapped nodes - it is never
   *         <code>null</code>.
   */
  public List<IntRangeNode> getSortedOverlaps(Comparator<IntRangeNode> comparator) {
	LinkedList<IntRangeNode> set = new LinkedList<IntRangeNode>();
    set.addAll(this.overlaps);
    Collections.sort(set, comparator);
    return set;
  }

  /**
   * Add a new range to this list, with the specified related object.
   *
   * <p>
   * This method will check for overlapping nodes and add it to the list of
   * ranges.
   *
   * <p>
   * It is illegal to provide a range where "from" > "to".
   *
   * @param from
   *          range start.
   * @param to
   *          range end.
   * @param value
   *          Related object.
   * @throws IllegalArgumentException
   *           if "from" > "to".
   */
  public void add(int from, int to, Object value) {
    if (from > to) {
      throw new IllegalArgumentException("Error while adding range [" +
                                         from + "," + to +
                                         "]: 'to' must be greater or equals 'from'.");
    }

    if (this.nodes.size() == 0) {
      // first entry
      IntRangeNode n = new IntRangeNode(from, to, value);
      this.nodes.add(n);
      this.smallestFrom = from;
      this.highestFrom = from;
      this.length = from;
      return;
    }

    int indexFrom = findGreaterThanOrEquals(from);
    IntRangeNode nFrom = indexFrom == -1 ? null : (IntRangeNode) this.nodes.get(indexFrom);
    int indexTo = from == to ? indexFrom : findGreaterThanOrEquals(to);

    if (indexFrom != 0) {
      // in order to find overlaps, we need to look at the previous node
      IntRangeNode prevNode;
      if (indexFrom < 0) {
        prevNode = (IntRangeNode) this.nodes.getLast();
      } else {
        prevNode = (IntRangeNode) this.nodes.get(indexFrom - 1);
      }
      if (prevNode.to >= from) {
        indexFrom = indexFrom < 0 ? this.nodes.size() - 1 : indexFrom - 1;
        nFrom = prevNode;
      }
    }

    // RICARDO - nao sei porque antes tinha o teste: nFrom.isOverlappingControl. Isso nao
    // funciona pois força o range novo a ser considerado um overlapp (problema), mesmo
    // que nao seja, mas soh porque o nFrom eh de controle!!
    //if (indexFrom != indexTo || (nFrom != null && (nFrom.isOverlappingControl || nFrom.from == from))) {
    if (indexFrom != indexTo || (nFrom != null && nFrom.from == from)) {

      // overlaps
      int lastIndex = indexTo < 0 ? this.nodes.size() - 1 : (indexFrom == indexTo ? indexTo : indexTo - 1);
      // create node for this range
      IntRangeNode newNode = new IntRangeNode(from, to, value);
      // Handle overlapping nodes
      IntRangeNode n = null, controlNode = null;
      for (int i = indexFrom; i <= lastIndex; i++) {
        n = this.nodes.remove(indexFrom);
        if (n.isOverlappingControl) {
          controlNode = n;
        } else {
          this.overlaps.add(n);
          if (n.from == from && n.to == to) {
            n.isDuplicated = true;
            newNode.isDuplicated = true;
          }
        }
      }
      // add this node to overlaps (last to be added)
      this.overlaps.add(newNode);
      // create 'fake' overlaped node to check further entries
      int newFrom = from < nFrom.from ? from : nFrom.from;
      int newTo = n != null && n.to > to ? n.to : to;
      if (controlNode != null) {
        n = controlNode;
        n.from = newFrom;
        n.to = newTo;
      } else {
        n = new IntRangeNode(newFrom, newTo);
      }
      n.isOverlappingControl = true;
      this.nodes.add(indexFrom, n);

    } else {

      // doesn't overlap
      nFrom = new IntRangeNode(from, to);
      if (indexFrom < 0) {
        this.nodes.add(nFrom);
      } else {
        this.nodes.add(indexFrom, nFrom);
      }
      nFrom.value = value;
      if (from < this.smallestFrom) {
        this.smallestFrom = from;
        this.length = this.highestFrom - this.smallestFrom;
      } else if (from > this.highestFrom) {
        this.highestFrom = from;
        this.length = this.highestFrom - this.smallestFrom;
      }

    }
  }

  /**
   * This method will return the index of first range node which "from" is
   * greater than or equal to the specified position value.
   *
   * <p>
   * If no range node has a "from" greater than the <code>position</code>
   * parameter, it will return -1.
   *
   * @param position
   *          the value that will be used to compare and find a greater than or
   *          equal "from".
   * @return The index of the first range node which "from" value is greater
   *         than or equal to the position, or -1 if none was found.
   */
  protected int findGreaterThanOrEquals(int position) {
    if (position <= this.smallestFrom) {
      return 0;
    } else if (position >= this.highestFrom) {
      return -1;
    }
    double guess = ((0d + position - this.smallestFrom) / this.length) * this.nodes.size();
    int guessedIndex = (int) guess;
    IntRangeNode guessedNode = (IntRangeNode) this.nodes.get(guessedIndex);
    int result = guessedIndex;
    if (position < guessedNode.from) {
      for (int i = guessedIndex - 1; i >= 0; i--) {
        IntRangeNode n = (IntRangeNode) this.nodes.get(i);
        if (position > n.from) {
          result = i + 1;
          break;
        }
      }
    } else {
      for (int i = guessedIndex + 1; i < this.nodes.size(); i++) {
        IntRangeNode n = (IntRangeNode) this.nodes.get(i);
        if (position < n.from) {
          result = i;
          break;
        }
      }
    }
    return result;
  }

  /**
   * {@inheritDoc}
   */
  public String toString() {
    StringBuilder s = new StringBuilder();
    Iterator it = this.nodes.iterator();
    while (it.hasNext()) {
      s.append( ((IntRangeNode) it.next()).toString() );
    }
    return s.toString();
  }

  /**
   * Represents a range in an IntRangeList. The range consists of:
   * <ul>
   * <li>"from": range start
   * <li>"to": range end
   * <li>"value": related object (optional use)
   * </ul>
   *
   * <p>
   * Use {@link #getIsDuplicated()} to check if the node represents a duplicated
   * range.
   *
   * <p>
   * Use {@link #getIsOverlappingControl()} to check if this is a control range -
   * which means the range is fake and only used to represent a list of nodes
   * that overlapped. Currently there is no way to find out which nodes are
   * related to a specific control range.
   *
   * @author rbarone
   *
   */
  public class IntRangeNode {
    protected int from, to;
    protected boolean isOverlappingControl = false;
    protected boolean isDuplicated = false;
    protected Object value = null;
    /**
     * Creates a new range with no related object.
     *
     * @param from
     *          the range start.
     * @param to
     *          the range end.
     */
    public IntRangeNode(int from, int to) {
      this.from = from;
      this.to = to;
    }
    /**
     * Creates a new range holding the provided related object.
     *
     * @param from
     *          the range start.
     * @param to
     *          the range end.
     * @param value
     *          the related object.
     */
    public IntRangeNode(int from, int to, Object value) {
      this(from, to);
      this.value = value;
    }
    /**
     * Return the start position of this range.
     *
     * @return The "from" (or range start position).
     */
    public int getTo() {
      return this.to;
    }
    /**
     * Return the end position of this range.
     *
     * @return The "to" (or range end position).
     */
    public int getFrom() {
      return this.from;
    }
    /**
     * Indicates if this is a control range.
     *
     * <p>
     * Control ranges are never added by an user, instead they're automatically
     * created when an overlap is detected.
     *
     * @return <code>true</code> if this is a control range,
     *         <code>false</code> otherwise.
     */
    public boolean getIsOverlappingControl() {
      return this.isOverlappingControl;
    }
    /**
     * Indicates if this is a duplicated range.
     *
     * @return <code>true</code> if this range is duplicated with another one,
     *         <code>false</code> otherwise.
     */
    public boolean getIsDuplicated() {
      return this.isDuplicated;
    }
    /**
     * Return the object related to this range.
     *
     * @return The object related to this range, or <code>null</code> if the
     *         range contains no object.
     */
    public Object getValue() {
      return this.value;
    }
    /**
     * {@inheritDoc}
     */
    public String toString() {
      StringBuilder s = new StringBuilder();
      return s.append("[").append(from).append(",").append(to).append("]").toString();
    }
  }

  /**
   * Default comparator class used to sort range nodes in "from" ascending
   * order.
   *
   * @author rbarone
   */
  protected static class IntRangeComparator implements Comparator<IntRangeNode> {
    public int compare(IntRangeNode o1, IntRangeNode o2) {
      return Integer.valueOf(o1.from).compareTo( Integer.valueOf(o2.from) );
    }

  }

}
