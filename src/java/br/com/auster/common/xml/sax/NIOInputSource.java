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
package br.com.auster.common.xml.sax;

import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.xml.sax.InputSource;

/**
 * @author Ricardo Barone
 * @version $Id: NIOInputSource.java 91 2005-04-07 21:13:55Z framos $
 */
public class NIOInputSource extends InputSource
{

   /**
    * Create a new input source with an input stream. The input stream will be
    * handled with new IO features (Channels).
    * 
    * @param byteStream
    *           the input stream containing the source document.
    * @see #NIOInputSource(ReadableByteChannel)
    */
   public NIOInputSource(InputStream byteStream)
   {
      setInputStream(byteStream);
   }

   protected ReadableByteChannel byteChannel;

   /**
    * Create a new input source with a readable byte channel.
    * 
    * <p>
    * Application writers may use setSystemId to provide a base for resolving
    * relative URIs, setPublicId to include a public identifier, and/or
    * setEncoding to specify the object's character encoding.
    * </p>
    * 
    * @param byteChannel
    *           The raw byte channel containing the document.
    * @see #setPublicId
    * @see #setSystemId
    * @see #setEncoding
    * @see #setCharacterStream
    * @see #setReadableByteChannel
    */
   public NIOInputSource(ReadableByteChannel byteChannel)
   {
      setReadableByteChannel(byteChannel);
   }

   /**
    * Set the readable byte channel for this input source.
    * 
    * <p>
    * If the application knows the character encoding of the byte channel, it
    * should set it with the setEncoding method.
    * </p>
    * 
    * @param byteChannel
    *           A readable byte channel containing an XML document or other
    *           entity.
    * @see #setEncoding
    * @see #getReadableByteChannel
    * @see #getEncoding
    * @see java.io.InputStream
    */
   public final void setReadableByteChannel(ReadableByteChannel byteChannel)
   {
      this.byteChannel = byteChannel;
   }

   /**
    * Get the readable byte channel for this input source.
    * 
    * <p>
    * The getEncoding method will return the character encoding for this byte
    * channel, or null if unknown.
    * </p>
    * 
    * @return The readable byte channel, or null if none was supplied.
    * @see #getEncoding
    * @see #setReadableByteChannel
    */
   public final ReadableByteChannel getReadableByteChannel()
   {
      return byteChannel;
   }

   /**
    * Set the byte stream as a readable byte channel for this input source.
    * 
    * <p>
    * If the application knows the character encoding of the byte channel, it
    * should set it with the setEncoding method.
    * </p>
    * 
    * @param byteStream
    *           A inputStream containing an XML document or other entity.
    * @see #setEncoding
    * @see #getEncoding
    * @see java.io.InputStream
    */
   public final void setInputStream(InputStream byteStream)
   {
      this.byteChannel = Channels.newChannel(byteStream);
   }

   /**
    * Get the byte stream from the readable byte channel for this input source.
    * 
    * <p>
    * The getEncoding method will return the character encoding for this byte
    * stream, or null if unknown.
    * </p>
    * 
    * @return The byte stream, or null if none was supplied.
    * @see #getEncoding
    * @see #setByteStream
    */
   public final InputStream getByteStream()
   {
      return Channels.newInputStream(byteChannel);
   }

}