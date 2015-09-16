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
 * Created on 07/02/2007
 */
package br.com.auster.common.xml.sax;

import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.AttributesImpl;

/**
 * A helper class to work with SAX content-handlers, specifically with elements,
 * attributes and characters.
 * 
 * <p>
 * You must already be aware that every XML document has a root element.
 * SAXStylesheet differs from a pure content-handler implementation in that a
 * root node is initialized and updated at every SAX event received, but no
 * duplicated childs will be stored (means: only one child with the same
 * local-name will be stored). This is done through a {@link XMLPath} instance,
 * acting as the root.
 * </p>
 * 
 * <p>
 * After that, instead of implementing startElement, endElement and characters,
 * you just have to create a SAXTemplate class and assign it to a XMLPath that
 * is accessible from the root. You can even assign more that one SAXTemplate to
 * the same XMLPath.
 * </p>
 * 
 * <p>
 * During the processing of the content-handler, whenever SAX events are
 * captured, the stylesheet will traverse the XMLPath from the root and, if a
 * SAXTemplate is configured, fire the template's appropriate event:
 * </p>
 * 
 * <ul>
 * <li><code>startElement</code>: Triggers
 * {@link SAXTemplate#onStartElement(SAXStylesheet, String, String, String, Attributes)} -
 * at this point, only the context attributes will be available in the current
 * XMLPath.</li>
 * <li><code>characters</code>: Triggers
 * {@link SAXTemplate#onCharacters(SAXStylesheet, char[], int, int)}. Remember
 * that SAX may send partial characters at any time - the only reliable way to
 * fetch all the character data is to wait for SAXTemplate.onEndElement event
 * and get it from the XMLPath context.</li>
 * <li><code>endElement</code>: Triggers
 * {@link SAXTemplate#onEndElement(SAXStylesheet, String, String, String)}</li>
 * </ul>
 * 
 * <p>
 * Note that a reference to the current stylesheet is provided in all
 * SAXTemplate methods, so that you can use it as a context to hold global
 * variables (between templates) and common helper methods.
 * </p>
 * 
 * <p>
 * All other events are handled directly by SAXStylesheet, which just has
 * a default implementation for everything (that does nothing). If you're
 * interested in any of these other event, just override the appropriate
 * method. The only exception for this are the following events, which cannot
 * be overriden but have their own listener-methods:
 * </p>
 * <ul>
 * <li>startDocument: Triggers {@link #onStartDocument()}</li>
 * <li>endDocument: Triggers {@link #onEndDocument()}</li>
 * </ul>
 * 
 * <p>
 * For example, suppose you have the following XML document...
 * <p>
 * 
 * <pre>
 *       &lt;my-xml&gt;
 *         &lt;person name=&quot;John&quot;&gt;
 *           &lt;address&gt;John's Address 1&lt;/address&gt;
 *           &lt;address&gt;John's Address 2&lt;/address&gt;
 *         &lt;/person&gt;
 *         &lt;person name=&quot;Jack&quot;&gt;
 *           &lt;address&gt;Jack's Address 1&lt;/address&gt;
 *           &lt;address&gt;Jack's Address 2&lt;/address&gt;
 *         &lt;/person&gt;
 *       &lt;/my-xml&gt;
 * </pre>
 * 
 * <p>
 * ...and you and to trigger some logic everytime a /my-xml/person/address path
 * is found:
 * </p>
 * 
 * <pre>
 * public class MySAXStylesheet extends SAXStylesheet {
 * 
 *   class AddressTemplate implements SAXTemplate {
 *     public void onStartElement(SAXStylesheet stylesheet, String uri, String localName,
 *                                String qName, Attributes atts) {
 *       // not interested in any attributes
 *     }
 * 
 *     public void onCharacters(SAXStylesheet handler, char[] ch, int start, int length) {
 *       // let's wait until the element has ended - that's when all text has been captured
 *     }
 * 
 *     public void onEndElement(SAXStylesheet stylesheet, String uri, String localName, String qName) {
 *       // we know that person is the parent node (or could just find out...)
 *       XMLPath person = handler.getCurrentPath().getParent();
 *       String name = person.getContextAttributes.getValue(&quot;name&quot;);
 *       String address = person.getContextCharacters();
 * 
 *       // let's create a Map of name/adresses
 *       List&lt;String&gt; addressList = stylesheet.addressesByPerson.get(name);
 *       if (addressList == null) {
 *         addressList = new ArrayList&lt;String&gt;();
 *         stylesheet.put(name, addressList);
 *       }
 *       addressList.add(address);
 *     }
 *   }
 * 
 *   private Map&lt;String, List&lt;String&gt;&gt; addressesByPerson;
 * 
 *   public MySAXStylesheet() {
 *     // build a XMLPath tree
 *     XMLPath root = XMLPath.createRoot();
 *     XMLPAth child = root.addChild(&quot;my-xml&quot;).addChild(&quot;person&quot;);
 *     child.addChild(&quot;address&quot;).addTemplate( new AddressTemplate() );
 *   }
 * 
 *   public void onStartDocument() {
 *     this.addressesByPerson = new HashMap&lt;String, List&lt;String&gt;&gt;()
 *   }
 *   
 *   public void onEndDocument() {
 *     // tell someone that the parsing is complete...
 *   }
 * }
 * </pre>
 * 
 * @author rbarone
 * @version $Id$
 */
public abstract class SAXStylesheet extends DefaultHandler {

  
  private XMLPath currentPath;
  
  private XMLPath root = XMLPath.createRoot();
  
  /**
   * Does nothing.
   * 
   * @param config
   *          configuration element for this stylesheet - this abstract class
   *          has no configuration options, so you can just pass
   *          <code>null</code>.
   */
  protected SAXStylesheet(Element config) {
    // no config options
  }
  
  /**
   * Defines a new XMLPath root for this stylesheet. This operation is not
   * permitted while a document is being processed - you must call it before
   * starting to parse the document.
   * 
   * @param root
   *          the new XMLPath root for this stylesheet
   */
  protected void setRoot(XMLPath root) {
    if (this.currentPath != null) {
      throw new IllegalStateException("Cannot set root after startDocument event.");
    }
    this.root = root;
  }

  /**
   * Return the stylesheet's root.
   * 
   * @return The XMLPath of this stylesheet.
   */
  public XMLPath getRoot() {
    return this.root;
  }
  
  /**
   * Returns the current path being processed.
   * 
   * @return a XMLPath representing the current path.
   */
  public XMLPath getCurrentPath() {
    return this.currentPath;
  }
  
  
  //######################################
  // org.xml.sax.ContentHandler methods
  //######################################
  
  /**
   * {@inheritDoc}
   */
  public final void startDocument() throws SAXException {
    this.currentPath = this.root;
    onStartDocument();
  }
  
  /**
   * {@inheritDoc}
   */
  public final void endDocument() throws SAXException {
    onEndDocument();
    this.currentPath = null;
  }

  /**
   * {@inheritDoc}
   */
  public final void startElement(String uri, String localName, String qName, Attributes atts)
      throws SAXException {
    XMLPath path = this.currentPath.getChild(localName);
    if (path == null) {
      path = this.currentPath.addChild(localName);
    }
    this.currentPath = path;
    if (path.getParent().isIgnoreChildren()) {
        path.ignoreChildren();
    } else {
    	AttributesImpl att = new AttributesImpl(atts);
        path.setContextAttributes(att);
        for (SAXTemplate t : path.getTemplates()) {
          t.onStartElement(this, uri, localName, qName, atts);
        }
    }
  }

  /**
   * {@inheritDoc}
   */
  public final void endElement(String uri, String localName, String qName) throws SAXException {
    if (this.currentPath == null) {
      throw new SAXException("endElement called but no element was started: " + localName);
    }
    if (!this.currentPath.isIgnoreChildren()) {
        for (SAXTemplate t : this.currentPath.getTemplates()) {
          t.onEndElement(this, uri, localName, qName);
        }
    }
    this.currentPath.clearContext();
    this.currentPath = this.currentPath.getParent();
  }
  
  /**
   * {@inheritDoc}
   */
  public final void characters(char[] ch, int start, int length) throws SAXException {
    if (this.currentPath == null) {
      throw new SAXException("No current element to handle characters.");
    }
    if (!this.currentPath.isIgnoreChildren()) {
        this.currentPath.addContextCharacters(ch, start, length);
        for (SAXTemplate t : this.currentPath.getTemplates()) {
          t.onCharacters(this, ch, start, length);
        }
    }
  }
  
  
  //######################################
  // Abstract methods
  //######################################
  
  /**
   * Called after the stylesheet received the
   * {@link org.xml.sax.ContentHandler#startDocument()} event.
   */
  public abstract void onStartDocument();
  
  /**
   * Called after the stylesheet received the
   * {@link org.xml.sax.ContentHandler#endDocument()} event.
   */
  public abstract void onEndDocument();

}
