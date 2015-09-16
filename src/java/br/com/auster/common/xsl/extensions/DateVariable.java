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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.apache.xalan.extensions.XSLProcessorContext;
import org.w3c.dom.Element;

/**
 * @author Ricardo Barone
 * @version $Id: DateVariable.java 91 2005-04-07 21:13:55Z framos $
 */
public class DateVariable extends Variable {

   protected static final String TAG_PATTERN = "pattern";

   protected static final int OPERATOR_ADD = 0;
   protected static final int OPERATOR_SUBTRACT = 1;

   protected static final long MILLISECONDS_PER_DAY = 1000 * 60 * 60 * 24;

   protected SimpleDateFormat defaultFormatter = null;

   protected HashMap variables = new HashMap();

   protected Calendar calendario = null;

   // ##############################
   // ELEMENTS
   // ##############################
   public void reset(XSLProcessorContext context, Element elem)
   {
      String pattern = elem.getAttribute(TAG_PATTERN);
      if(pattern == null) {
         throw new IllegalArgumentException("[" + this.getClass().getName() + "/init] "
               + "The '" + TAG_PATTERN + "' attribute is mandatory.");
      }
      this.defaultFormatter = new SimpleDateFormat(pattern.replace('%', '\''));
      this.variables.clear();
   }

   public String format(XSLProcessorContext context, Element elem)
         throws java.text.ParseException
   {
      String name = elem.getAttribute(TAG_NAME);
      if(name != null) {
         String date = (String) this.variables.get(name);
         if(date == null) {
            throw new IllegalArgumentException("[" + this.getClass().getName()
                  + "/format] " + "Variable '" + name + "' does not exist.");
         } else if(date.length() == 0) {
            return "";
         } else {
            return this.format(this.defaultFormatter.parse(date),
                               elem.getAttribute(TAG_PATTERN));
         }
      } else {
         return this.format(elem.getAttribute(TAG_VALUE), elem.getAttribute(TAG_PATTERN));
      }
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

   public long getTime(String date) throws java.text.ParseException
   {
      if(date == null || date.length() == 0) {
         return 0L;
      } else if(date.equals("now")) {
         return (new Date()).getTime();
      } else {
         return this.defaultFormatter.parse(date).getTime();
      }
   }

   public long getTimeFromVar(String name) throws java.text.ParseException
   {
      return this.getTime(this.getValue(name));
   }

   /**
    * 
    * @param value
    *           Data em milisegundos
    * @param days
    *           Quantidade de dias a serem decrementados da data passada
    * @return Data decrementada da quantidade de dias passado.
    * @throws java.text.ParseException
    */
   public String getDecrementTime(long value, long days) throws java.text.ParseException
   {

      Date data = new Date();
      long val = value - days * MILLISECONDS_PER_DAY;
      data.setTime(val);
      return this.defaultFormatter.format(data);

   }

   /**
    * Retorna string com valor da hora atual no formato: HHMMSS.
    * 
    * @return String com valor de hora atual
    */
   public String getTime()
   {

      SimpleDateFormat formato = new SimpleDateFormat("HHmmss");
      return formato.format(new Date());

   }

   public double getNumberOfDays(long milliseconds)
   {
      return milliseconds / (24 * 3600 * 1000);
   }

   public String getDefaultPattern()
   {
      return this.defaultFormatter.toPattern().replace('\'', '%');
   }

   public String setValue(String name, String value, String inPattern)
   {
      this.store(name, value, inPattern);
      return this.getValue(name);
   }

   public String format(String date, String pattern) throws java.text.ParseException
   {
      pattern = pattern.replace('%', '\'');
      if(date == null || date.length() == 0) {
         return "";
      } else if(date.equals("now")) {
         return this.format(new Date(), pattern);
      } else {
         return this.format(this.defaultFormatter.parse(date), pattern);
      }
   }

   public String format(String date, String inPattern, String outPattern)
         throws java.text.ParseException
   {
      inPattern = inPattern.replace('%', '\'');
      outPattern = outPattern.replace('%', '\'');
      if(date == null || date.length() == 0) {
         return "";
      } else if(date.equals("now")) {
         return this.format(new Date(), outPattern);
      } else if(inPattern.length() == 0
            || this.defaultFormatter.toPattern().equals(inPattern)) {
         return this.format(this.defaultFormatter.parse(date), outPattern);
      } else {
         return this.format((new SimpleDateFormat(inPattern)).parse(date), outPattern);
      }
   }

   public String format(long milliseconds, String pattern)
         throws java.text.ParseException
   {
      return this.format(new Date(milliseconds), pattern);
   }

   public int compare(String date1, String date2) throws java.text.ParseException
   {
      boolean isDate1Empty = false;
      boolean isDate2Empty = false;
      if(date1 == null || date1.length() == 0) {
         isDate1Empty = true;
      }
      if(date2 == null || date2.length() == 0) {
         isDate2Empty = true;
      }

      if(isDate1Empty && isDate2Empty) {
         return 0;
      } else if(isDate1Empty) {
         return -1;
      } else if(isDate2Empty) {
         return 1;
      }

      return this.defaultFormatter.parse(date1).compareTo(this.defaultFormatter.parse(date2));
   }

   // ##############################
   // PROTECTED METHODS
   // ##############################
   protected void store(String name, String value)
   {
      this.store(name, value, "");
   }

   protected void store(String name, String value, String inPattern)
   {
      if(name == null || inPattern == null) {
         throw new IllegalArgumentException("[" + this.getClass().getName() + "/store] '"
               + TAG_NAME + "' and '" + TAG_PATTERN + "' cannot be null.");
      }
      String val;
      if(value == null || value.length() == 0) {
         val = "";
      } else if(value.equals("now")) {
         val = this.defaultFormatter.format(new Date());
      } else {
         try {
            // to test if it is a valid date...
            if(inPattern.length() == 0
                  || this.defaultFormatter.toPattern().equals(inPattern)) {
               this.defaultFormatter.parse(value);
            } else {
               (new SimpleDateFormat(inPattern)).parse(value);
            }
         } catch(java.text.ParseException e) {
            throw new IllegalArgumentException("[" + this.getClass().getName()
                  + "/store] " + "A valid date must be supplied.");
         }
         val = value;
      }
      this.variables.put(name, val);
   }

   protected String format(Date date, String pattern)
   {
      if(pattern == null) {
         throw new IllegalArgumentException("[" + this.getClass().getName() + "/format] "
               + "The '" + TAG_PATTERN + "' attribute is mandatory.");
      }
      return (date != null ? (new SimpleDateFormat(pattern)).format(date) : "");
   }

}