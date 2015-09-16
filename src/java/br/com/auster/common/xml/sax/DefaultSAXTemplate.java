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
 * Created on 15/02/2007
 */
package br.com.auster.common.xml.sax;

import org.xml.sax.Attributes;

/**
 * Default base class for {@link SAXTemplate} event handlers.
 * 
 * <p>
 * This class is available as a convenience base class for SAXTemplate, as it
 * provides default implementations (does nothing) for all of the callbacks. 
 * </p>
 * 
 * <p>
 * Template writers can extend this class when they need to implement only part
 * of an interface.
 * </p>
 * 
 * @author rbarone
 * @version $Id$
 */
public class DefaultSAXTemplate implements SAXTemplate {

  /**
   * {@inheritDoc}
   */
  public void onCharacters(SAXStylesheet stylesheet, char[] ch, int start, int length) {
  }

  /**
   * {@inheritDoc}
   */
  public void onEndElement(SAXStylesheet stylesheet, String uri, String localName, String qName) {
  }

  /**
   * {@inheritDoc}
   */
  public void onStartElement(SAXStylesheet stylesheet, String uri, String localName, String qName,
                             Attributes atts) {
  }

}
