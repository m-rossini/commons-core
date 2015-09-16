/*
 * Copyright (c) 2004 Auster Solutions do Brasil LTDA. All Rights Reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * Created on 03/11/2004
 */
package br.com.auster.common.xsl.extensions;

import java.util.HashMap;

import org.apache.xalan.extensions.XSLProcessorContext;
import org.w3c.dom.Element;

/**
 * @author Ricardo Barone
 * @version $Id: StringVariable.java 91 2005-04-07 21:13:55Z framos $
 */
public class StringVariable extends Variable {

   protected HashMap variables = new HashMap();

   // ##############################
   // ELEMENTS
   // ##############################
   public void reset(XSLProcessorContext context, Element elem)
   {
      this.variables.clear();
   }

   // ##############################
   // FUNCTIONS
   // ##############################
   public String getValue(String name)
   {
      if(!this.variables.containsKey(name)) {
         throw new IllegalArgumentException("[" + this.getClass().getName()
               + "/getValue] " + "Variable '" + name + "' does not exist.");
      }
      return (String) this.variables.get(name);
   }

   // ##############################
   // PROTECTED METHODS
   // ##############################
   protected void store(String name, String value)
   {
      if(name == null) {
         throw new IllegalArgumentException("[" + this.getClass().getName() + "/store] '"
               + TAG_NAME + "' cannot be null.");
      }
      if(value == null) {
         value = "";
      }
      this.variables.put(name, value);
   }

}