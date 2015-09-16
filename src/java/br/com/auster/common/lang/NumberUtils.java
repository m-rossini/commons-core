/*
 * Copyright (c) 2004 Auster Solutions do Brasil. All Rights Reserved.
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
 * Created on 13/10/2004
 */
package br.com.auster.common.lang;

/**
 * @author Ricardo Barone
 * @version $Id: NumberUtils.java 389 2007-07-06 01:51:03Z mtengelm $
 */
public final class NumberUtils extends org.apache.commons.lang.math.NumberUtils
{

   /**
    * <code>NumberUtils</code> instances should NOT be constructed in standard
    * programming. Instead, the class should be used as
    * <code>NumberUtils.stringToInt("6");</code>.
    */
   protected NumberUtils()
   {
      super();
   }

   /**
    * Converts a string to a number. Returns 0 if the string is null or empty.
    * 
    * @param value
    *           the String representing the number to be converted.
    * @return the converted number.
    * @throws NumberFormatException
    *            if the value is not a valid number.
    */
   public static final double toNumber(String value) throws NumberFormatException
   {
      return toNumber(value, 0D);
   }

   /**
    * Converts a string to a number. 
    * Returns <code>dValue</code> if the string is <code>null</code> or empty.
    * 
    * @param value
    *           The String representing the number to be converted. 
    *           Can be <code>null</code> or an empty string.
    * @param dvalue
    *           A value to be returned if the string is empty.
    * @return the converted number.
    * @throws NumberFormatException
    *            if the value is not a valid number.
    */
   public static final double toNumber(String value, double dValue)
         throws NumberFormatException
   {
      return (StringUtils.isBlank(value) ? dValue : Double.parseDouble(value));
   }

   /***
    * 
    * Truncates a number to a given number of decimals
    * <p>
    * Example:
    * <pre>
    *    Supose one have:
    *    
    *    double num = 1.266666666666D;
    *    then
    *    truncate(num,0) -> 1.0
    *    truncate(num,1) -> 1.2
    *    truncate(num,2) -> 1.26
    *    truncate(num,3) -> 1.266
    *    
    *    and so forth.
    * </pre>
    * </p>
    * 
    * 
    * @param num The double number you want to truncate
    * @param decimals The number of decimals you except as result of truncation
    * @return
    */
 	public static double truncate(double num, int decimals) {
		double results = 0;
		
		double power = Math.pow(10,decimals);
		results = Math.floor(num * power) / power;
		
		return results;
	}
}