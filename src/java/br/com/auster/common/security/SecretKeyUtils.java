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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

/**
 * @author Ricardo Barone
 * @version $Id: SecretKeyUtils.java 475 2010-03-05 19:06:02Z caoque $
 */
public class SecretKeyUtils {

  private final static Logger log = Logger.getLogger(SecretKeyUtils.class);

  private static final String DEFAULT_KEY_FILENAME = "key.bin";
  private static final String KEY_FILENAME_PROPERTY = "br.com.auster.key.path";
  public static final String KEY_FILENAME;
  static {
    String filename = System.getProperty(KEY_FILENAME_PROPERTY);
    if (filename == null) {
      filename = DEFAULT_KEY_FILENAME;
    }
    KEY_FILENAME = filename;
  }

  static final int SECRET_KEY_MAX_LENGTH = 32; // 256 bits

  private SecretKeyUtils() {
    // not available
  }

  public static final byte[] createSecretKey(String algorithm, int keyLength)
      throws GeneralSecurityException {
    // Verify the length
    if (keyLength <= 0) {
      throw new InvalidKeySpecException("Key length must be greater than 0.");
    } else if (keyLength > SECRET_KEY_MAX_LENGTH) {
      throw new InvalidKeySpecException("Key length cannot be greater than "
                                        + SECRET_KEY_MAX_LENGTH + ".");
    }

    // Get the KeyGenerator
    KeyGenerator kgen = KeyGenerator.getInstance(algorithm);
    kgen.init(keyLength * 8);

    // Generate the secret key specs.
    SecretKey skey = kgen.generateKey();
    return skey.getEncoded();
  }

  public static final void storeSecretKey(byte[] rawKey) throws IOException {
    OutputStream out = new FileOutputStream(KEY_FILENAME);
    out.write(rawKey);
    out.close();
  }

  public static final byte[] readSecretKey() throws IOException {
    // initialize the buffer and read the secret file content
    final byte[] rawKeyBuffer = new byte[SECRET_KEY_MAX_LENGTH];
    final InputStream in = Cryptor.class.getResourceAsStream("/" + KEY_FILENAME);

    // verify if we really found the file
    if (in == null) {
      throw new FileNotFoundException(KEY_FILENAME);
    }
    
    int size = 0, value = 0;
    while ((value = in.read()) >= 0) {
      rawKeyBuffer[size] = (byte) value;
      size++;
      // check the file size (must not be greater thatn the rawKeyBuffer)
      if (size > SECRET_KEY_MAX_LENGTH) {
        throw new SecurityException("License file has an invalid key with length = ["
                                    + in.available() + "]");
      }
    }
    in.close();

    // create the raw key from the buffer (compact the buffer)
    final byte[] rawKey = new byte[size];
    System.arraycopy(rawKeyBuffer, 0, rawKey, 0, size);
    return rawKey;
  }

  public static final void main(String[] args) {
    try {
      // Set up a simple configuration that logs on the console.
      BasicConfigurator.configure();

      // create key
      log.info("Creating a new key...");
      String algorithm = String.valueOf(Cryptor.CIPHER_DEFAULT_ALGORITHM);
      int keyLength = 16; // 128 bits 'cause 192 and 256 may not be available...
      byte[] rawKey = SecretKeyUtils.createSecretKey(algorithm, keyLength);
      log.info("done!");
      log.info("Storing key in file [" + SecretKeyUtils.KEY_FILENAME + "]...");
      SecretKeyUtils.storeSecretKey(rawKey);
      log.info("done!");
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

}
