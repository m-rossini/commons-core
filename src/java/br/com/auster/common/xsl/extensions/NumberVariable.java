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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.xalan.extensions.XSLProcessorContext;
import org.w3c.dom.Element;

/**
 * @author Ricardo Barone
 * @version $Id: NumberVariable.java 273 2006-07-25 23:18:13Z rbarone $
 */
public class NumberVariable extends Variable {
	
   protected Map variables = MapUtils.typedMap(new HashMap(), String.class, Double.class);

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
      return MapUtils.getDouble(this.variables, name, new Double(0)).toString();
   }

   public static double parse(String value)
   {
      double val;

      /****
       if (value == null || value.length() == 0) {
       val = 0.00;
       } else {
       //BigDecimal big = new BigDecimal(value);
       //big = big.setScale(5, BigDecimal.ROUND_FLOOR);
       //val = big.doubleValue();
       
       val = Double.parseDouble(value);
       }
       **/

      val = round(value, 5);
      return val;
   }

   public static double round(String value, int decimalPlaces)
   {
      if(decimalPlaces < 0) {
         throw new IllegalArgumentException("[NumberVariable/round] "
               + "'decimalPlaces' must be greater than zero.");
      }
      double val;
      if(value == null || value.length() == 0) {
         val = 0.00;
      } else {
         val = Double.parseDouble(value);
      }
      int constant = (int) Math.pow(10, decimalPlaces);
      return Math.round(val * constant) / (double) constant;
   }

   public static double floor(String value, int decimalPlaces)
   {
      if(decimalPlaces < 0) {
         throw new IllegalArgumentException("[NumberVariable/floor] "
               + "'decimalPlaces' must be greater than zero.");
      }
      double val;
      if(value == null || value.length() == 0) {
         val = 0.00;
      } else {
         val = Double.parseDouble(value);
      }
      int constant = (int) Math.pow(10, decimalPlaces);
      return Math.floor(Math.round((val * constant) / (double) constant));
   }

   public static double ceil(String value, int decimalPlaces)
   {
      if(decimalPlaces < 0) {
         throw new IllegalArgumentException("[NumberVariable/floor] "
               + "'decimalPlaces' must be greater than zero.");
      }
      double val;
      if(value == null || value.length() == 0) {
         val = 0.00;
      } else {
         val = Double.parseDouble(value);
      }
      int constant = (int) Math.pow(10, decimalPlaces);
      return ((Math.ceil(val * constant)) / (double) constant);
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
      this.variables.put(name, new Double(NumberVariable.parse(value)));
   }

}