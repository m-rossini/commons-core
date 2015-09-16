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
package br.com.auster.common.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import br.com.auster.common.io.NIOUtils;



/**
 * @author Ricardo Barone
 * @version $Id: Decryptor.java 91 2005-04-07 21:13:55Z framos $
 */
public final class Decryptor extends Cryptor
{

   private final static Logger log = Logger.getLogger(Decryptor.class);
   private static Decryptor INSTANCE = null;

   private SecretKeySpec secretKey;

   private Decryptor()
   {
      try
      {
         // initialize the secret key
         byte[] rawKey = SecretKeyUtils.readSecretKey();
         String algorithm = String.valueOf(CIPHER_DEFAULT_ALGORITHM);
         this.secretKey = new SecretKeySpec(rawKey, algorithm);
      }
      catch(Exception e)
      {
         e.printStackTrace();
         throw new RuntimeException(e);
      }
   }

   public InputStream decryptStream(InputStream input) throws GeneralSecurityException
   {
      return new CipherInputStream(input, getDecryptionCipher());
   }

   public InputStream decryptStream(String URI) throws GeneralSecurityException,
         IOException
   {
      InputStream input = new FileInputStream(URI);
      return decryptStream(input);
   }

   public byte[] decrypt(byte[] encryptedData) throws GeneralSecurityException
   {
      Cipher cipher = getDecryptionCipher();
      return cipher.doFinal(encryptedData);
   }

   private Cipher getDecryptionCipher() throws GeneralSecurityException
   {
      Cipher cipher = getCipher();
      cipher.init(Cipher.DECRYPT_MODE, this.secretKey, getParameters());
      return cipher;
   }

   public static final Decryptor getInstance()
   {
      if(INSTANCE == null)
      {
         INSTANCE = new Decryptor();
      }
      return INSTANCE;
   }

   private final static void decryptFile(File sourceFile, File destFile)
         throws GeneralSecurityException, IOException
   {
      // open source file
      ReadableByteChannel source = NIOUtils.openFileForRead(sourceFile, true);

      // open destination file
      WritableByteChannel dest = NIOUtils.openFileForWrite(destFile, false);

      // copy data to decrypted destination file
      log.info("Decrypting file [" + sourceFile.getPath() + "] to file ["
            + destFile.getPath() + "].");
      final int bufferSize = 16384;
      NIOUtils.copyStream(source, dest, bufferSize);
      source.close();
      dest.close();
   }

   public final static void main(String args[])
   {
      try
      {
         // Set up a simple configuration that logs on the console.
         BasicConfigurator.configure();
         // Decrypt file to destination
         Decryptor.decryptFile(new File(args[0]), new File(args[1]));
      }
      catch(Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }
   }

}