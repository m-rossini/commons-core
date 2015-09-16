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
* Created on 27/06/2005
*/
package br.com.auster.common.xsl.extensions;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * <p><b>Title:</b> BarCode</p>
 * <p><b>Description:</b> </p>
 * <p><b>Copyright:</b> Copyright (c) 2004-2005</p>
 * <p><b>Company:</b> Auster Solutions</p>
 *
 * @author mtengelm
 * @version $Id$
 */
public class BarCode {
   protected static final String WIDTH  = "width";
   protected static final String COLOR  = "color";
   protected static final String BLACK  = "1";
   protected static final String WHITE  = "0";
   protected static final String NARROW = "0";
   protected static final String WIDE   = "1";
       

   protected final static char[][] digitCodes = {  {'0','0','1','1','0'},
                                                   {'1','0','0','0','1'},
                                                   {'0','1','0','0','1'},
                                                   {'1','1','0','0','0'},
                                                   {'0','0','1','0','1'},
                                                   {'1','0','1','0','0'},
                                                   {'0','1','1','0','0'},
                                                   {'0','0','0','1','1'},
                                                   {'1','0','0','1','0'},
                                                   {'0','1','0','1','0'}  };

   // ##############################
   // FUNCTIONS
   // ##############################
   
   /**
    * Builds a new I-25 (2 of 5 interleaved) barcode in the format "wWnN"
    * "n" is a narrow-bar + narrow-space,
    * "N" is a narrow-bar + wide-space,
    * "w" is a wide-bar + narrow-space,
    * "W" is a wide-bar + wide-space.
    *
    * @param code code of digits that will be translated to a bar code (the length should be 44 - if it isn't, it will be filled with trailing zeros)
    * @return the translated bar code
    */
   public String buildBarCode (String code) {

       if (code == null) {
           System.out.println("[BarCode/buildBarCode] code is null!!! Ignoring...");
           return "";
       }

       // fill the code with trailing zeros until is 44 chars length
       for (int i = code.length(); i < 44; i++) {
           code += "0";
       }

       // control digit calculation
       int sum = 0;
       int j = 2;
       for (int i = 43; i >= 0; i--) {
           if (i == 4) continue;
           char digit = code.charAt(i);
           if (digit == ' ')
               digit = '0';
           sum += Character.digit(digit, 10) * j++;
           if (j > 9) j = 2;     
       }
       int lastDigit = 11 - (sum % 11);
       if (lastDigit < 1 || lastDigit > 9) lastDigit = 1;
       code = code.substring(0, 4) + lastDigit + code.substring(5);

       // barcode generation logic
       String barCode = "";
       for (int i = 0; i < 44; i += 2) {
           for (int k = 0; k < 5; k++) {
               char digit = code.charAt(i);
               if (digit == ' ')
                   digit = '0';
               char digit1 = digitCodes[Character.digit(digit, 10)][k];
               digit = code.charAt(i + 1);
               if (digit == ' ')
                   digit = '0';
               char digit2 = digitCodes[Character.digit(digit, 10)][k];
               if (digit1 == '0') {
                   if (digit2 == '0')
                       barCode += "n";
                   else
                       barCode += "N";
               } else {
                   if (digit2 == '0')
                       barCode += "w";
                   else
                       barCode += "W";
               }
           }
       }
       return barCode;
   }


   /**
    * Builds the a line of numberic fields that can be typed to represent a bar code
    *
    * @param code code of digits that represents the bar code (length = 44)
    *
    * @return the originated line of numeric fields
    */
   public String buildFieldLine (String code) {
       return buildFieldLine( code.substring(0, 3),
                              code.substring(3, 4),
                              code.substring(5, 9),
                              code.substring(9, 19),
                              code.substring(19),
                              code.substring(4, 5) );
   }


   /**
    * Builds the a line of numberic fields that can be typed to represent a bar code
    *
    * @param bankCode printed at the start of the assembled field line (length = 2)
    * @param currencyCode printed after the bankCode (length = 2)
    * @param deadlineFactor a factor representing the deadline date (length = 4)
    * @param value the bill's value (length = 10)
    * @param freeField anything can be typed int here (length = 25)
    * @param controlDigit digit printed before the lastValue (length = 1)
    *
    * @return the originated line of numeric fields
    */
   public String buildFieldLine (String bankCode,       String currencyCode, 
                                 String deadlineFactor, String value,
                                 String freeField,      String controlDigit) {

       // wCampoLivre deve ser uma String com length = 25

       // primeiro campo...
       String field1 = assembleField( bankCode + currencyCode + freeField.substring(0, 1),
                                      freeField.substring(1, 5) );
       // segundo campo...
       String field2 = assembleField( freeField.substring(5, 10),
                                      freeField.substring(10, 15) );
       // terceiro campo...
       String field3 = assembleField( freeField.substring(15, 20),
                                      freeField.substring(20, 25) );

       return field1 + "  " + field2 + "  " + field3 + "  " + controlDigit + "  " + deadlineFactor + value;
   }


   private String assembleField (String subField1, String subField2) {
       return subField1 + "." + subField2 + calculateLastDigit(subField1 + subField2);
   }

   private int calculateLastDigit(String field) {
       int lastDigit = 0;
       for (int i = field.length() - 1; i >= 0; --i) {
           // multiplier: starts with 2 and must alternate between 1 and 2
           int multiplier = 2 - ( (field.length() - 1 - i) % 2);
           int result = Character.digit(field.charAt(i), 10) * multiplier;
           if (result > 9) result -= 9;
           lastDigit += result;
       }
       lastDigit = 10 - (lastDigit % 10);
       if (lastDigit == 10) lastDigit = 0;
       return lastDigit;
   }

   /**
    * Creates a node list of items like the following:
    * <barcode chars="w">
    *     <bar color="x" width="y"/>
    * </barcode>
    * where:
    * w = Start chars + data chars + stop chars
    * x = 1 means white, x = 0 means black
    * y = 1 means wide, y = 0 means narrow
    */
   public Node drawBarCode(String barcode) 
   throws ParserConfigurationException
   {
       // Start chars + data chars + stop chars
       barcode = "nn"+this.buildBarCode(barcode)+"wn";
       Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
       Element root = document.createElement("barcode");
       root.setAttribute("chars", barcode);

       for (int i = 0; i < barcode.length(); i++) {
           char barChar = barcode.charAt(i);

           // N or n: space is narrow, W or w: space is wide
           Element bar = document.createElement("bar");
           root.appendChild(bar);
           if (Character.toString(barChar).equalsIgnoreCase("n")) {
               bar.setAttribute(WIDTH, NARROW);
               bar.setAttribute(COLOR, BLACK);
           } else {
               bar.setAttribute(WIDTH, WIDE);
               bar.setAttribute(COLOR, BLACK);
           }

           // Lower case: black bar is narrow
           bar = document.createElement("bar");
           root.appendChild(bar);
           if (Character.isLowerCase(barChar)) {
               bar.setAttribute(WIDTH, NARROW);
               bar.setAttribute(COLOR, WHITE);
           } else {
               bar.setAttribute(WIDTH, WIDE);
               bar.setAttribute(COLOR, WHITE);
           }
       }
       return root;
   }

   public static void main (String[] args) {

       //String code = "34197181400005777631090064750422938008246000";
       String code = "";
       for (int i = 0; i < args.length; i++) {
           if ( args[i].equals("-code") ) {
               code = args[i + 1];
               break;
           }
       }

       if (code.length() == 0) {
           System.out.println("usage: java BarCode -code [code to be translated]");
           System.exit(0);
       }

       BarCode barCode = new BarCode();
       System.out.println( barCode.buildBarCode(code) );
       System.out.println( barCode.buildFieldLine(code) );
   }

}
 