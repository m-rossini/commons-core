/*
 * Copyright (c) 2004 Auster Solutions do Brasil LTDA. All Rights Reserved.
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
 * Created on 03/11/2004
 */
package br.com.auster.common.xsl.extensions;

import org.apache.xalan.extensions.XSLProcessorContext;
import org.w3c.dom.Element;

/**
 * @author Ricardo Barone
 * @version $Id: Variable.java 91 2005-04-07 21:13:55Z framos $
 */
public abstract class Variable {

  protected static final String TAG_NAME  = "name";
  protected static final String TAG_VALUE = "value";

  // ##############################
  // ELEMENTS
  // ##############################
  public void setValue(XSLProcessorContext context, Element elem) {
    this.store( elem.getAttribute(TAG_NAME),
		elem.getAttribute(TAG_VALUE) );
  }

  public String getValue(XSLProcessorContext context, Element elem) {
    return this.getValue( elem.getAttribute(TAG_NAME) );
  }


  // ##############################
  // FUNCTIONS
  // ##############################
  public abstract String getValue(String name);

  public String setValue(String name, String value) { 
    this.store(name, value);
    return this.getValue(name);
  }


  // ##############################
  // PROTECTED METHODS
  // ##############################
  protected abstract void store(String name, String value);

}
