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

import org.xml.sax.Attributes;


/**
 * Represents a template that will handle events generated by a {@link SAXStylesheet} for
 * a specific {@link XMLPath}.
 * 
 * To use it, first implement a template, instantiate it and added it to a XMLPath
 * using {@link XMLPath#addTemplate(SAXTemplate)}. More than one template may be
 * added to the same XMLPath.
 *
 * @see SAXStylesheet
 * @see XMLPath
 * @author rbarone
 * @version $Id$
 */
public interface SAXTemplate {
  
  /**
   * Called after the stylesheet has received a
   * {@link org.xml.sax.ContentHandler#startElement(String, String, String, Attributes)}
   * event. The current XMLPath accessed from
   * {@link SAXStylesheet#getCurrentPath()} will be this element.
   * 
   * @param stylesheet
   *          the SAXStylesheet that triggered this event
   * @param uri
   *          see
   *          {@linkplain org.xml.sax.ContentHandler#startElement(String, String, String, Attributes)}
   * @param localName
   *          see
   *          {@link org.xml.sax.ContentHandler#startElement(String, String, String, Attributes)}
   * @param qName
   *          see
   *          {@link org.xml.sax.ContentHandler#startElement(String, String, String, Attributes)}
   * @param atts
   *          see
   *          {@link org.xml.sax.ContentHandler#startElement(String, String, String, Attributes)}
   */
  public void onStartElement(SAXStylesheet stylesheet, String uri, String localName, 
                             String qName, Attributes atts);
  
  /**
   * Called after the stylesheet has received a
   * {@link org.xml.sax.ContentHandler#characters(char[], int, int) event. The
   * current XMLPath accessed from {@link SAXStylesheet#getCurrentPath()} will
   * be the element that is receiving the characters.
   * 
   * <p>
   * Please notice that SAX parsers may send more than one {@code characters}
   * event for the same element, so this may not be the all the text of this
   * element. Maybe you could wait {@link #onEndElement(SAXStylesheet, String, String, String)}
   * is called to get all the text from the current path's context.
   * </p>
   * 
   * @param stylesheet
   *          the SAXStylesheet that triggered this event
   * @param ch
   *          see
   *          {@linkplain org.xml.sax.ContentHandler#characters(char[], int, int)}
   * @param start
   *          see
   *          {@link org.xml.sax.ContentHandler#characters(char[], int, int)}
   * @param length
   *          see
   *          {@link org.xml.sax.ContentHandler#characters(char[], int, int)}
   */
  public void onCharacters(SAXStylesheet stylesheet, char[] ch, int start, int length);
  
  /**
   * Called after the stylesheet has received a
   * {@link org.xml.sax.ContentHandler#endElement(String, String, String)}
   * event. The current XMLPath accessed from
   * {@link SAXStylesheet#getCurrentPath()} will contain all attributes and text
   * received for this element.
   * 
   * @param stylesheet
   *          the SAXStylesheet that triggered this event
   * @param uri
   *          see
   *          {@link org.xml.sax.ContentHandler#endElement(String, String, String)}
   * @param localName
   *          see
   *          {@link org.xml.sax.ContentHandler#endElement(String, String, String)}
   * @param qName
   *          see
   *          {@link org.xml.sax.ContentHandler#endElement(String, String, String)}
   */
  public void onEndElement(SAXStylesheet stylesheet, String uri, 
                           String localName, String qName);
  
}
