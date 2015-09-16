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
 * Created on 26/10/2004
 */
package br.com.auster.common.io;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import br.com.auster.common.security.Decryptor;
import br.com.auster.common.security.Encryptor;


/**
 * @author Ricardo Barone
 * @version $Id: IOUtils.java 369 2007-02-15 20:49:06Z rbarone $
 */
public class IOUtils {

   private static final int DEFAULT_BUFFER_SIZE = 1024 * 64;


  /**
    * Opens a file for reading. If the file name ends with some known suffix,
    * this method opens using GZIPInputStrem, ZipInputStream, etc.
    * 
    * @param file
    *           the file to be opened.
    * @return a reader for that file.
    * @exception IOException
    *               if some error occurs while opening the file.
    */
   public static final InputStream openFileForRead(File file) 
         throws IOException
   {
      final String fileName = file.getAbsolutePath();
      InputStream input = new FileInputStream(file);
      return handleCompressedInput(fileName, input);
   }
   
   /**
    * Opens a file for reading. If the file name ends with some known suffix,
    * this method opens using GZIPInputStrem, ZipInputStream, etc.
    * 
    * @param fileName
    *           the file to be opened.
    * @return a reader for that file.
    * @exception IOException
    *               if some error occurs while opening the file.
    */
   public static final InputStream openFileForRead(String fileName) 
         throws IOException
   {
      InputStream input;
      try {
        input = new FileInputStream(fileName);
      } catch (FileNotFoundException e) {
        if (fileName.charAt(0) != '/') {
          fileName = "/" + fileName;
        }
        input = IOUtils.class.getResourceAsStream(fileName);
        if (input == null) {
          throw e;
        }
      }
      return handleCompressedInput(fileName, input);
   }
   
   public static final InputStream handleCompressedInput(String fileName, InputStream input) 
         throws IOException
   {
      if(fileName.endsWith(".gz") || fileName.endsWith(".GZ")) {
         input = new GZIPInputStream(input);
      } else if(fileName.endsWith(".zip") || fileName.endsWith(".ZIP")) {
         input = new ZipInputStream(input);
         // by default, fetch the very first entry in the ZIP file
         // [this could be enhanced to fetch a specific entry...]
         ((ZipInputStream) input).getNextEntry();
      }
      return input;
   }

   /**
    * Opens a file for reading. If the file name ends with some known suffix,
    * this method opens using GZIPInputStrem, ZipInputStream, etc.
    * 
    * If the file is encrypted by the
    * {@linkplain br.com.auster.common.security.Encryptor#encryptStream(OutputStream) Encryptor class},
    * this method will return the decrypted stream.
    * 
    * @param file
    *           the file to be opened.
    * @param isEncrypted
    *           <code>'true'</code> if the file is encrypted,
    *           <code>'false'</code> otherwise.
    * @return an input stream for the file.
    * @exception IOException
    *               if some error occurs while opening the file.
    * @exception GeneralSecurityException
    *               if some error occurs while decrypting the file.
    */
   public static final InputStream openFileForRead(File file, boolean isEncrypted)
         throws IOException, GeneralSecurityException
   {
      InputStream input = openFileForRead(file);
      if(isEncrypted) {
         Decryptor decryptor = Decryptor.getInstance();
         input = decryptor.decryptStream(input);
      }
      return input;
   }
   
   /**
    * Opens a file for reading. If the file name ends with some known suffix,
    * this method opens using GZIPInputStrem, ZipInputStream, etc.
    * 
    * If the file is encrypted by the
    * {@linkplain br.com.auster.common.security.Encryptor#encryptStream(OutputStream) Encryptor class},
    * this method will return the decrypted stream.
    * 
    * @param file
    *           the file to be opened.
    * @param isEncrypted
    *           <code>'true'</code> if the file is encrypted,
    *           <code>'false'</code> otherwise.
    * @return an input stream for the file.
    * @exception IOException
    *               if some error occurs while opening the file.
    * @exception GeneralSecurityException
    *               if some error occurs while decrypting the file.
    */
   public static final InputStream openFileForRead(String fileName, boolean isEncrypted)
         throws IOException, GeneralSecurityException
   {
      InputStream input = openFileForRead(fileName);
      if(isEncrypted) {
         Decryptor decryptor = Decryptor.getInstance();
         input = decryptor.decryptStream(input);
      }
      return input;
   }

   /**
    * Opens a file for writing. If the file name ends with some known suffix,
    * this method opens using GZIPInputStrem, ZipInputStream, etc.
    * 
    * @param file
    *           the file to be opened.
    * @param append
    *           if this file will be opened to writing in append mode.
    * @return a writer for that file.
    * @exception IOException
    *               if some error occurs while opening the file.
    */
   public static final OutputStream openFileForWrite(File file, boolean append)
         throws IOException
   {
      return openFileForWrite(file, DEFAULT_BUFFER_SIZE, append);
   }
   
   
   public static final OutputStream openFileForWrite(File file, 
                                                     int bufferSize,
                                                     boolean append) throws IOException {
    final String fileName = file.getAbsolutePath();
    FileOutputStream fos = new FileOutputStream(file, append);
    OutputStream output = new BufferedOutputStream(fos, bufferSize);
    if (fileName.endsWith(".gz")) {
      output = new GZIPOutputStream(output);
    } else if (fileName.endsWith(".zip")) {
      output = new ZipOutputStream(output);
      // by default, create a new entry with the same name as
      // the ZIP file.
      ((ZipOutputStream) output)
          .putNextEntry(new ZipEntry(file.getName().replaceAll("\\.zip$", "")));
    }

    return output;
  }

   /**
    * Opens a file for writing. If the file name ends with some known suffix,
    * this method opens using GZIPInputStrem, ZipInputStream, etc.
    * 
    * If the file is to be encrypted by the
    * {@linkplain br.com.auster.common.security.Encryptor#encryptStream(OutputStream) Encryptor class},
    * this method will return the encrypted stream.
    * 
    * @param file
    *           the file to be opened.
    * @param append
    *           if this file will be opened to writing in append mode.
    * @param encrypt
    *           <code>'true'</code> if the file is to be encrypted,
    *           <code>'false'</code> otherwise.
    * @return an output stream for that file, encrypted depending on the value
    *         of the <code>encrypt</code> parameter.
    * @exception IOException
    *               if some error occurs while opening the file.
    * @exception GeneralSecurityException
    *               if some error occurs while encrypting the file.
    */
   public static final OutputStream openFileForWrite(File file,
                                                     boolean append,
                                                     boolean encrypt) 
         throws IOException, GeneralSecurityException
   {
      return openFileForWrite(file, DEFAULT_BUFFER_SIZE, append, encrypt);
   }
   
   public static final OutputStream openFileForWrite(File file,
                                                     int bufferSize,
                                                     boolean append,
                                                     boolean encrypt) 
         throws IOException, GeneralSecurityException
   {
     OutputStream output = openFileForWrite(file, bufferSize, append);
     if(encrypt) {
        Encryptor encryptor = Encryptor.getInstance();
        output = encryptor.encryptStream(output);
     }
     return output;
   }

   
   /**
     * Creates all non-existing directories from the path that contains the
     * specified <code>File</code>.
     * 
     * @param file
     *          The file whose parent directories must be created.
     * @return true if and only if the directory was created, along with all
     *         necessary parent directories; false otherwise (including if the
     *         parent directories already existed)
     */
   public static boolean createParentDirs(File file) throws IOException {
     boolean result = false;
     
     File dir = file.getParentFile();
     if (dir != null) {
       File cDir = dir.getCanonicalFile();
       // retry at most 3 times to create parent dirs
       for (int i = 3; i > 0 && !cDir.exists(); i--) {
         result = cDir.mkdirs();
       }
       if (!dir.exists()) {
         throw new IOException("Could not create parent directories for the file: " +
                               file.getPath());
       }
     }
     
     return result;
   }
   
}