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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.security.GeneralSecurityException;

import org.apache.log4j.Logger;

/**
 * This class has a lot of utilities to manipulate and debug new IO streams.
 * 
 * @author Ricardo Barone
 * @version $Id: NIOUtils.java 298 2006-08-28 19:56:54Z framos $
 */
public class NIOUtils {

   private static final Logger log = Logger.getLogger(NIOUtils.class);

   /**
    * Reads the input stream until no more data is available and writes it to
    * the output defined.
    * 
    * @param input
    *           the input stream.
    * @param output
    *           the output stream.
    * @param bufferSize
    *           the size of the buffer used in this operation (in bytes).
    * @exception IOException
    *               if some error occurs while reading or writing data.
    */
   public static final void copyStream(ReadableByteChannel input,
                                       WritableByteChannel output,
                                       int bufferSize) throws IOException
   {
      copyStream(input, output, ByteBuffer.allocateDirect(bufferSize));
   }

   /**
    * Reads the input stream until no more data is available and writes it to
    * the output defined.
    * 
    * @param input
    *           the input stream.
    * @param output
    *           the output stream.
    * @param buffer
    *           the buffer to be used in this operation.
    * @exception IOException
    *               if some error occurs while reading or writing data.
    */
   public static final void copyStream(ReadableByteChannel input,
                                       WritableByteChannel output,
                                       ByteBuffer buffer) throws IOException
   {
      // Outputs all the input
      for(int size = 0; (size = read(input, buffer)) > 0;) {
         output.write(buffer);
         buffer.compact();
      }

      // Flushes the remaining data and free resources
      flush(output, buffer);
   }

   /**
    * Reads from the input until it completes the buffer or no more data is
    * available. The buffer will be compacted before reading (this is usefull if
    * it already contains some data), and at the end of this method it will be
    * ready for reading (no <code>flip()</code> is necessary).
    * 
    * @param input
    *           the input stream that contains the data to be read.
    * @param buffer
    *           the output buffer that will have the data read.
    * @return the number of bytes read.
    * @exception IOException
    *               if some error occurs while reading data.
    */
   public static final int read(ReadableByteChannel input, ByteBuffer buffer)
         throws IOException
   {
      int total = buffer.position();
      final int position = total;
      for(int size = 0; (total < buffer.capacity()) && ((size = input.read(buffer)) >= 0);) {
         total += size;
      }
      buffer.flip();
      return total - position;
   }

   /**
    * Writes to the output until all buffer data is written or an error occurs.
    * The <code>compact()</code> method will be called when exiting.
    * 
    * @param output
    *           the output stream that will receive the buffer data.
    * @param buffer
    *           the buffer that contains the data to be written.
    * @return the number of bytes written.
    * @exception IOException
    *               if some error occurs while writing data.
    */
   public static final int flush(WritableByteChannel output, ByteBuffer buffer)
         throws IOException
   {
      int remaining = buffer.remaining();
      while(remaining > 0) {
         remaining -= output.write(buffer);
      }
      buffer.clear();
      return remaining;
   }

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
   public static final ReadableByteChannel openFileForRead(File file) throws IOException
   {
     InputStream input = IOUtils.openFileForRead(file);
     if (input instanceof FileInputStream) {
       return ((FileInputStream) input).getChannel();
     }
     return Channels.newChannel(input);
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
   public static final ReadableByteChannel openFileForRead(String fileName) throws IOException
   {
     InputStream input = IOUtils.openFileForRead(fileName);
     if (input instanceof FileInputStream) {
       return ((FileInputStream) input).getChannel();
     }
     return Channels.newChannel(input);
   }
   
   /**
    * Opens a file for reading. If the file name ends with some known suffix,
    * this method opens using GZIPInputStrem, ZipInputStream, etc.
    * 
    * If the file is encrypted by the
    * {@linkplain br.com.auster.security.Encryptor#encryptStream(OutputStream) Encryptor class},
    * this method will return the decrypted stream.
    * 
    * @param file
    *           the file to be opened.
    * @param isEncrypted
    *           <code>'true'</code> if the file is encrypted,
    *           <code>'false'</code> otherwise.
    * @return a reader for the file.
    * @exception IOException
    *               if some error occurs while opening the file.
    * @exception GeneralSecurityException
    *               if some error occurs while decrypting the file.
    */
   public static final ReadableByteChannel openFileForRead(File file, boolean isEncrypted)
         throws IOException, GeneralSecurityException
   {
     InputStream input = IOUtils.openFileForRead(file, isEncrypted);
     if (input instanceof FileInputStream) {
       return ((FileInputStream) input).getChannel();
     }
     return Channels.newChannel(input);
   }
   
   /**
    * Opens a file for reading. If the file name ends with some known suffix,
    * this method opens using GZIPInputStrem, ZipInputStream, etc.
    * 
    * If the file is encrypted by the
    * {@linkplain br.com.auster.common.security.Encryptor#encryptStream(OutputStream) Encryptor class},
    * this method will return the decrypted stream.
    * 
    * @param fileName
    *           the file to be opened.
    * @param isEncrypted
    *           <code>'true'</code> if the file is encrypted,
    *           <code>'false'</code> otherwise.
    * @return a reader for the file.
    * @exception IOException
    *               if some error occurs while opening the file.
    * @exception GeneralSecurityException
    *               if some error occurs while decrypting the file.
    */
   public static final ReadableByteChannel openFileForRead(String fileName, boolean isEncrypted)
         throws IOException, GeneralSecurityException
   {
     InputStream input = IOUtils.openFileForRead(fileName, isEncrypted);
     if (input instanceof FileInputStream) {
       return ((FileInputStream) input).getChannel();
     }
     return Channels.newChannel(input);
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
   public static final WritableByteChannel openFileForWrite(File file, 
                                                            boolean append)
         throws IOException
   {
      return Channels.newChannel(IOUtils.openFileForWrite(file, append));
   }
   
   public static final WritableByteChannel openFileForWrite(File file, 
                                                            int bufferSize, 
                                                            boolean append)
      throws IOException 
  {
     return Channels.newChannel(IOUtils.openFileForWrite(file, append));
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
    * @return a writer for that file, encrypted depending on the value of the
    *         <code>encrypt</code> parameter.
    * @exception IOException
    *               if some error occurs while opening the file.
    * @exception GeneralSecurityException
    *               if some error occurs while encrypting the file.
    */
   public static final WritableByteChannel openFileForWrite(File file,
                                                            boolean append,
                                                            boolean encrypt)
         throws IOException, GeneralSecurityException
   {
      return Channels.newChannel(IOUtils.openFileForWrite(file, append, encrypt));
   }
   
   
   public static final WritableByteChannel openFileForWrite(File file,
                                                            int bufferSize,
                                                            boolean append,
                                                            boolean encrypt)
         throws IOException, GeneralSecurityException
   {
      return Channels.newChannel(IOUtils.openFileForWrite(file, bufferSize, append, encrypt));
   }

}