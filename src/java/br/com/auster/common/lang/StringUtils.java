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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Ricardo Barone
 * @version $Id: StringUtils.java 129 2005-07-20 19:50:07Z etirelli $
 */
public final class StringUtils extends org.apache.commons.lang.StringUtils
{

   /**
    * <code>StringUtils</code> instances should NOT be constructed in standard
    * programming. Instead, the class should be used as
    * <code>StringUtils.trim(" foo ");</code>.
    */
   protected StringUtils()
   {
      super();
   }
   
   /**
    * Returns a string of length <code>size</code>, containing the string
    * <code>str</code> aligned to the left and padded with the char
    * <code>padChar</code>.
    * 
    * @param str
    *           the string to work on.
    * @param size
    *           the size of the string to be returned.
    * @param padChar
    *           the character to pad with.
    * @return If the length of <code>str</code> is greater than
    *         <code>size</code>, returns it truncated. If the length of
    *         <code>str</code> is smaller than <code>size</code>, returns
    *         it concated with <code>padChar</code> characters on its right to
    *         fill the field.
    */
   public static final String alignToLeft(String str, int size, char padChar)
   {
      return left(rightPad(str, size, padChar), size);
   }
   
   /**
    * Returns a string of length <code>size</code>, containing the string
    * <code>str</code> aligned to the right and padded with the char
    * <code>padChar</code>.
    * 
    * @param str
    *           the string to work on.
    * @param size
    *           the size of the string to be returned.
    * @param padChar
    *           the character to pad with.
    * @return If the length of <code>str</code> is greater than
    *         <code>size</code>, returns it truncated. If the length of
    *         <code>str</code> is smaller than <code>size</code>, returns
    *         it concated with <code>padChar</code> characters on its left to
    *         fill the field.
    */
   public static final String alignToRight(String str, int size, char padChar)
   {
      return right(leftPad(str, size, padChar), size);
   }

   /**
    * Splits the given string into an array of substrings using the given
    * separator and join it together into a new string using the same separator.
    * The <code>from</code> and <code>to</code> parameters specify the range
    * of substrings that will be used. Negative indexes are allowed and will act
    * as counting backwards. For example:
    * 
    * <code>TextUtils.getSlice("a b c d e", 1, -2, " ");</code> returns:
    * <code>"b c d"</code>
    * 
    * If both <code>from</code> and <code>to</code> indexes overlap, then
    * the substring at <code>from</code> will be returned. For example:
    * 
    * <code>TextUtils.getSlice("a b c d e", 3, -3, " ");</code>
    * <code>                        ^ ^                 </code>
    * <code>                       -3 3                 </code>
    * returns: <code>"d"</code>
    * 
    * @param value
    *           the source string to be manipulated.
    * @param from
    *           the index (-length..0..length-1) indicating the beggining of the
    *           range.
    * @param to
    *           the index (-length..0..length-1) indicating the beggining of the
    *           range.
    * @param separator
    *           the separator to be used.
    * @return the resulting slice or <code>value</code> if it's null or empty.
    * @throws IllegalArgumentException
    *            if <code>abs(from) > length</code> or
    *            <code>abs(to) > length</code>
    */
   public static final String getSlice(String value, int from, int to, String separator)
   {
      String[] values = value.split(separator);
      if(values == null || values.length == 0)
      {
         return value;
      }

      if(from >= values.length)
      {
         from = values.length - 1;
      }
      else if(from < 0)
      {
         if(Math.abs(from) > values.length)
         {
            from = 0;
         }
         else
         {
            from += values.length;
         }
      }
      if(Math.abs(to) > values.length)
      {
      }
      else if(to >= values.length)
      {
         to = values.length - 1;
      }
      else if(to < 0)
      {
         if(Math.abs(from) > values.length)
         {
            from = 0;
         }
         else
         {
            to += values.length;
         }
      }
      if(to < from)
      {
         return values[from];
      }

      final StringBuffer result = new StringBuffer();
      for(int i = from; i <= to; i++)
      {
         if(i > from)
         {
            result.append(" ");
         }
         result.append(values[i]);
      }
      return result.toString();
   }

   /**
    * Calls <code>getSlice(value, from, to, separator)</code> using a space as
    * the separator char.
    * 
    * @see #getSlice(String value, int from, int to, String separator)
    */
   public static final String getSlice(String value, int from, int to)
   {
      return getSlice(value, from, to, " ");
   }
   
   /**
    * Converts the given <code>byte[]<code> input in an Hex String
    *
    * @param input the binary input byte array  
    *
    * @return Hex String representing the binary input byte array
    */
   public static final String getHexString(byte[] input) {
     String[]     hex = {
                          "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A",
                          "B", "C", "D", "E", "F"
                        };
     StringBuffer buf = new StringBuffer(input.length * 2);

     for (int i = 0; i < input.length; i++) {
       buf.append(hex[((input[i] & 0xF0) >>> 4) & 0x0F]); // high nibble
       buf.append(hex[input[i] & 0x0F]); // low nibble
     }

     return buf.toString();
   }

   /**
    * Calculates and returns an Hex String for MD5 Digest of the input String
    *
    * @param text Input text String
    *
    * @return MD5 Digest Hex String 
    *
    * @throws NoSuchAlgorithmException if the MD5 digest algorithm is not present
    */
   public static final String getMD5Digest(String text)
                              throws NoSuchAlgorithmException {
     MessageDigest md5     = MessageDigest.getInstance("MD5");
     byte[]        bdigest = md5.digest(text.getBytes());

     return StringUtils.getHexString(bdigest);
   }

}