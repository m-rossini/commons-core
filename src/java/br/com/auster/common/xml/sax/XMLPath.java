/*
 * Copyright (c) 2004-2006 Auster Solutions. All Rights Reserved.
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
 * Created on 02/02/2007
 */
package br.com.auster.common.xml.sax;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.xml.sax.Attributes;


/**
 * Represents an element inside a XML-Tree, starting from a single root.
 * <p>
 * <b>NOTE: Instances of this class are not Thread-safe.</b>
 * </p>
 * 
 * @author rbarone
 * @version $Id$
 */
public class XMLPath {
  
  /**
   * {@value}
   */
  public static final char PATH_SEP_CHAR = '/'; 
  
  /**
   * Creates a new instance of XMLPath with no parent and no localname - a root
   * XMLPath.
   * 
   * @return a new root XMLPath instance.
   */
  public static final XMLPath createRoot() {
    return new XMLPath();
  }
  
  
  private String localName;
  
  private Map<String,XMLPath> childs = new HashMap<String,XMLPath>();
  
  private Set<SAXTemplate> templates = new HashSet<SAXTemplate>(4); 
  
  private WeakReference<XMLPath> parent;
  
  private Attributes atts;
  
  private StringBuilder text;
  
  private boolean isIgnoreChildren = false;
  
  private XMLPath() {
    // create a new root XMLPath with no parent
    this.localName = "";
  }
  
  protected XMLPath(String localName, XMLPath parent) {
    if (parent == null) {
      throw new IllegalArgumentException("XMLPath's parent cannot be null.");
    } else if (localName == null || localName.length() == 0) {
      throw new IllegalArgumentException("XMLPath's localName cannot be null or empty.");
    }
    this.parent = new WeakReference<XMLPath>(parent);
    this.localName = localName;
  }

  /**
   * @return the localName
   */
  public String getLocalName() {
    return localName;
  }
  
  /**
   * Adds a new child to this XMLPath.
   * 
   * @param child the <code>XMLPath</code> child to add
   */
  public XMLPath addChild(String childLocalName) {
    if (childLocalName == null) {
      throw new IllegalArgumentException("XMLPath child localName cannot be null.");
    }
    XMLPath child = new XMLPath(childLocalName, this);
    this.childs.put(childLocalName, child);
    return child;
  }
  
  /**
   * Finds and returns a child of this XMLPath that has the given localName.
   * 
   * @param localName
   *          the localName of the XMLPath child to search for
   * @return the child that has the specified localName, or <code>null</code>
   *         it there is no such child of this XMLPath
   */
  public XMLPath getChild(String localName) {
    return this.childs.get(localName);
  }
  
  /**
   * Returns the parent of this XMLPath.
   * 
   * @return the XMLPath that is the parent of this instance, or
   *         <code>null</code> if this is the root.
   */
  public XMLPath getParent() {
    return this.parent == null ? null : this.parent.get();
  }
  
  /**
   * Ignore all events for this path and it's children.
   * <P>
   * Ignored paths will be re-enabled when the context returns to it's parent. 
   */
  public void ignoreChildren() {
      this.isIgnoreChildren = true;
  }
  
  /**
   * Indicates if this path is currently disabled (won't send/receive any events)
   * 
   * @return {@code true} if this path is disabled.
   */
  public boolean isIgnoreChildren() {
      return this.isIgnoreChildren;
  }
  
  public void clearContext() {
    this.atts = null;
    this.text = null;
    this.isIgnoreChildren = false;
  }
  
  public void setContextAttributes(Attributes atts) {
    this.atts = atts;
  }
  
  /**
   * Return the current context attributes, if any. Can be <code>null</code>.
   * 
   * @return the context attributes, or <code>null</code> if the current
   *         context of this XMLPath has no attributes set.
   */
  public Attributes getContextAttributes() {
    return this.atts;
  }
  
  public void addContextCharacters(char[] ch, int start, int length) {
    if (this.text == null) {
      this.text = new StringBuilder();
    }
    this.text.append(ch, start, length);
  }
  
  /**
   * Return the current context text, if any. Can be <code>null</code>.
   * 
   * @return the context text, or <code>null</code> if the current
   *         context of this XMLPath has no text set.
   */
  public String getContextCharacters() {
    return this.text.toString();
  }
  
  
  /**
   * Returns the list of {@link SAXTemplate} instances related to this XMLPath.
   * 
   * @return the list of templates in this XMLPath - will never be
   *         <code>null</code>.
   */
  public Set<SAXTemplate> getTemplates() {
    return this.templates;
  }
  
  /**
   * Adds a new {@link SAXTemplate} to this XMLPath.
   * 
   * @param template
   *          the template to add - duplicated templates will be ignored.
   * @return This XMLPath instance.
   */
  public XMLPath addTemplate(SAXTemplate template) {
    this.templates.add(template);
    return this;
  }
  
  /**
   * A <code>String</code> representation of this XMLPath. This is the same as
   * calling {@link #toStringBuilder() toStringBuilder().toString()}.
   */
  @Override
  public String toString() {
    return toStringBuilder().toString(); 
  }
  
  /**
   * Returns a <code>StringBuilder</code> populated with the full path that
   * represents this XMLPath.
   */
  protected StringBuilder toStringBuilder() {
    StringBuilder sb;
    if (this.parent == null) {
      sb = new StringBuilder(PATH_SEP_CHAR);
    } else {
      sb = this.parent.get().toStringBuilder();
    }
    return sb.append(PATH_SEP_CHAR).append(this.localName);
  }
  
}
