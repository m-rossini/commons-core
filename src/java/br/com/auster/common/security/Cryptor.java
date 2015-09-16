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

import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;


/**
 * @author Ricardo Barone
 * @version $Id: Cryptor.java 91 2005-04-07 21:13:55Z framos $
 */
abstract class Cryptor
{
   
   static final byte[] DEFAULT_IV_PARAMETER = { (byte) 0x9c, (byte) 0x4f, (byte) 0x6e,
         (byte) 0x9a, (byte) 0x1b, (byte) 0x7a, (byte) 0x3d, (byte) 0x62, (byte) 0xda,
         (byte) 0xff, (byte) 0x3b, (byte) 0x5c, (byte) 0x32, (byte) 0x1d, (byte) 0x23,
         (byte) 0xfb };

   static final char[] CIPHER_DEFAULT_ALGORITHM = { 'A', 'E', 'S' };
   static final char[] CIPHER_DEFAULT_MODE = { 'C', 'B', 'C' };
   static final char[] CIPHER_DEFAULT_PADDING = { 'P', 'K', 'C', 'S', '5', 'P', 'a', 'd',
         'd', 'i', 'n', 'g' };

   static final String CIPHER_DEFAULT_TRANSFORMATION = 
           String.valueOf(CIPHER_DEFAULT_ALGORITHM)
         + "/"
         + String.valueOf(CIPHER_DEFAULT_MODE)
         + "/"
         + String.valueOf(CIPHER_DEFAULT_PADDING);
   
   
   Cipher getCipher() throws GeneralSecurityException
   {
      return Cipher.getInstance(CIPHER_DEFAULT_TRANSFORMATION);
   }
   
   AlgorithmParameters getParameters() throws GeneralSecurityException
   {
      IvParameterSpec spec = new IvParameterSpec(DEFAULT_IV_PARAMETER);
      String algorithm = String.valueOf(CIPHER_DEFAULT_ALGORITHM);
      AlgorithmParameters params = AlgorithmParameters.getInstance(algorithm);
      params.init(spec);
      return params;
   }
   
}
