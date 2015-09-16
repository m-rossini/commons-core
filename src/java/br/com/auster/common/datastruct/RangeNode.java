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

import java.util.List;

/**
 * <p><b>Title:</b> RangeNode</p>
 * <p><b>Description:</b> A node in the RangeMap's internal red black tree</p>
 * <p><b>Copyright:</b> Copyright (c) 2004-2005</p>
 * <p><b>Company:</b> Auster Solutions</p>
 *
 * @author etirelli
 * @version $Id: RangeNode.java 304 2005-11-01 18:31:11Z etirelli $
 */
public class RangeNode {
  public static final boolean RED   = false;
  public static final boolean BLACK = true;
  
  protected RangeEntry     entry;
  protected RangeNode   left;
  protected RangeNode   right;
  protected RangeNode   parent;
  protected boolean        color;
//  private   boolean        modified;

  public RangeNode() {
    this((RangeEntry) null, (RangeNode) null);
  }

  public RangeNode(RangeEntry value) {
    this(value, (RangeNode) null);
  }

  public RangeNode(RangeEntry x, RangeNode parent) {
    this.entry    = x;
    this.left     = null;
    this.right    = null;
    this.parent   = parent;
//    this.modified = true;
    this.color    = BLACK;
  }
  
  public RangeNode(long from, long to) {
    this(from, to, (RangeNode) null);
  }

  public RangeNode(long from, long to, RangeNode parent) {
    this.entry    = new RangeEntry(from, to);
    this.left     = null;
    this.right    = null;
    this.parent   = parent;
//    this.modified = true;
    this.color    = BLACK;
  }

  public RangeNode(long from, long to, List values) {
    this(from, to, values, (RangeNode) null);
  }

  public RangeNode(long from, long to, List values, RangeNode parent) {
    this.entry    = new RangeEntry(from, to, values);
    this.left     = null;
    this.right    = null;
    this.parent   = parent;
//    this.modified = true;
    this.color    = BLACK;
  }

  /**
   * @return Returns the color.
   */
  public boolean getColor() {
    return color;
  }

  /**
   * @param color The color to set.
   */
  public void setColor(boolean color) {
    this.color = color;
  }

  /**
   * @return Returns the left.
   */
  public RangeNode getLeft() {
    return left;
  }

  /**
   * @param left The left to set.
   */
  public void setLeft(RangeNode left) {
    this.left = left;
  }

  /**
   * @return Returns the parent.
   */
  public RangeNode getParent() {
    return parent;
  }

  /**
   * @param parent The parent to set.
   */
  public void setParent(RangeNode parent) {
    this.parent = parent;
  }

  /**
   * @return Returns the right.
   */
  public RangeNode getRight() {
    return right;
  }

  /**
   * @param right The right to set.
   */
  public void setRight(RangeNode right) {
    this.right = right;
  }

  /**
   * @return Returns the entry.
   */
  public RangeEntry getEntry() {
    return entry;
  }

  /**
   * @param entry The entry to set.
   */
  public void setEntry(RangeEntry value) {
    this.entry = value;
  }
  
  public long getFrom() {
    return this.entry.getFrom();
  }
  
  public long getTo() {
    return this.entry.getTo();
  }
  
  public List getValues() {
    return this.entry.getValues();
  }
  
  public void setFrom(long from) {
    this.entry.setFrom(from);
  }
  
  public void setTo(long to) {
    this.entry.setTo(to);
  }
  
  public void addValue(Object value) {
    this.entry.addValue(value);
  }
  

}
