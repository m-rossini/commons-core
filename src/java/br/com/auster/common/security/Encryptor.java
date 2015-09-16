/*
 * Copyright (c) 2004 TTI Tecnologia. All Rights Reserved.
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
 * Created on Oct 6, 2004
 */
package br.com.auster.common.security;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import br.com.auster.common.io.NIOUtils;



/**
 * @author Ricardo Barone
 * @version $Id: Encryptor.java 91 2005-04-07 21:13:55Z framos $
 */
public final class Encryptor extends Cryptor
{

   private final static Logger log = Logger.getLogger(Encryptor.class);
   private static Encryptor INSTANCE = null;

   private SecretKeySpec secretKey;

   private Encryptor()
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

   public OutputStream encryptStream(OutputStream output) throws GeneralSecurityException
   {
      return new CipherOutputStream(output, getEncryptionCipher());
   }

   public OutputStream encryptStream(String URI) throws GeneralSecurityException,
         IOException
   {
      OutputStream output = new FileOutputStream(URI);
      return encryptStream(output);
   }

   public byte[] encrypt(byte[] originalData) throws GeneralSecurityException
   {
      Cipher cipher = getEncryptionCipher();
      return cipher.doFinal(originalData);
   }

   private Cipher getEncryptionCipher() throws GeneralSecurityException
   {
      Cipher cipher = getCipher();
      cipher.init(Cipher.ENCRYPT_MODE, this.secretKey, getParameters());
      return cipher;
   }

   public static final Encryptor getInstance()
   {
      if(INSTANCE == null)
      {
         INSTANCE = new Encryptor();
      }
      return INSTANCE;
   }

   private final static void encryptFile(File sourceFile, File destFile)
         throws GeneralSecurityException, IOException
   {
      // open source file
      ReadableByteChannel source = NIOUtils.openFileForRead(sourceFile);

      // open destination file
      WritableByteChannel dest = NIOUtils.openFileForWrite(destFile, false, true);

      // copy data to encrypted destination file
      log.info("Encrypting file [" + sourceFile.getPath() + "] to file ["
            + destFile.getPath() + "]...");
      final int bufferSize = 16384;
      NIOUtils.copyStream(source, dest, bufferSize);
      source.close();
      dest.close();
   }

   /**
    * Encrypts the source file, given by the first argument, to the destination
    * file, given by the second argument.
    * 
    * Usage: java com.tti.common.security.Encryptor [source file] [dest file]
    * 
    * @param args
    *           first argument is the source file to be encrypted; second
    *           argument is the destination file that will be created.
    */
   public final static void main(String[] args)
   {
      try
      {
         // Set up a simple configuration that logs on the console.
         BasicConfigurator.configure();
         // Encrypt file to destination
         Encryptor.encryptFile(new File(args[0]), new File(args[1]));
      }
      catch(Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }
   }

}