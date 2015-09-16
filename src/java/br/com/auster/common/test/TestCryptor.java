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
 * Created on 03/11/2004
 */
package br.com.auster.common.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import br.com.auster.common.security.Decryptor;
import br.com.auster.common.security.Encryptor;

import junit.framework.Assert;
import junit.framework.TestCase;



/**
 * @author Ricardo Barone
 * @version $Id: TestCryptor.java 91 2005-04-07 21:13:55Z framos $
 */
public class TestCryptor extends TestCase
{

   public TestCryptor(String method)
   {
      super(method);
   }

   public void testTextCryptor() throws Exception
   {
      String text = "This is a test!!";
      String hexText = getHexString(text.getBytes());

      Encryptor encryptor = Encryptor.getInstance();

      byte[] cryptedText1 = encryptor.encrypt(text.getBytes());
      String cryptedHexText1 = getHexString(cryptedText1);
      Assert.assertFalse("Original text is equal to encrypted text!",
                         hexText.equals(cryptedHexText1));

      byte[] cryptedText2 = encryptor.encrypt(text.getBytes());
      String cryptedHexText2 = getHexString(cryptedText2);
      //Assert.assertEquals("Second encryption run is not the same as the
      // first!", cryptedHexText1, cryptedHexText2);

      Decryptor decryptor = Decryptor.getInstance();
      
      String decryptedText1 = new String(decryptor.decrypt(cryptedText1));
      Assert.assertEquals("1st decrypted text is not equal to the original!",
                          text,
                          decryptedText1);
      
      String decryptedText2 = new String(decryptor.decrypt(cryptedText2));
      Assert.assertEquals("2nd decrypted text is not equal to the original!",
                          text,
                          decryptedText2);
   }
   
   public void testFileCryptor() throws Exception
   {
      String text = "This is a test!!";
      
      File encryptedFile = File.createTempFile("CRYPTOR_TEST", null);

      Encryptor encryptor = Encryptor.getInstance();
      OutputStream output = new FileOutputStream(encryptedFile);
      OutputStream encryptedOutput = encryptor.encryptStream(output);
      encryptedOutput.write(text.getBytes());
      encryptedOutput.close();
      
      Decryptor decryptor = Decryptor.getInstance();
      InputStream input = new FileInputStream(encryptedFile);
      InputStream encryptedInput = decryptor.decryptStream(input);
      byte[] buffer = new byte[(int)encryptedFile.length()];
      int actualLength = encryptedInput.read(buffer);
      Assert.assertTrue("File content length is not the same as the original!",
                        actualLength == text.length());
      byte[] content = new byte[actualLength];
      System.arraycopy(buffer, 0, content, 0, content.length);
      String decryptedText = new String(content);
      Assert.assertEquals("Decrypted content is not the same as the original!",
                          text,
                          decryptedText);
   }

   public static String getHexString(byte[] input)
   {
      String[] hex = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C",
            "D", "E", "F" };
      StringBuffer buf = new StringBuffer(32);
      for(int i = 0; i < input.length; i++)
      {
         buf.append(hex[((input[i] & 0xF0) >>> 4) & 0x0F]); // high nibble
         buf.append(hex[input[i] & 0x0F]); // low nibble
      }
      return buf.toString();
   }

}