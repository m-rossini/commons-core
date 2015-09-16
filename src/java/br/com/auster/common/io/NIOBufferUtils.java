/*
 * Copyright (c) 2004-2005 Auster Solutions do Brasil. All Rights Reserved.
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
 * Created on Aug 22, 2005
 */
package br.com.auster.common.io;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;


/**
 * TODO class description
 *
 * <p><b>Title:</b> NIOBufferUtils</p>
 * <p><b>Copyright:</b> Copyright (c) 2004-2005</p>
 * <p><b>Company:</b> Auster Solutions</p>
 *
 * @author rbarone
 * @version $Id: NIOBufferUtils.java 320 2006-09-19 14:02:38Z mtengelm $
 */
public class NIOBufferUtils {

  /**
   * Should never be used.
   */
  private NIOBufferUtils() {
    super();
  }
  
  /**
   * Searches for the specified token in the given buffer and returns the index
   * of the first byte of the token inside the buffer.
   * 
   * It is valid until it reads limit bytes. If it reads limit bytes and do not find 
   * the token, it returns -1 regardless of buffer size.
   * 
   * If buffer ends before limit, it returns -1.
   * If limit is negative, it is the same of calling with buffer remaning as limit.
   * 
   * <p>
   * This method will start reading from the current buffer's position and will
   * return the buffer with it's position and limit intact, but any
   * previously-definied mark will be discarded.
   * </p>
   * 
   * @param buffer
   *          The buffer to be read; any mark will be discarded!
   * @param token
   *          The token to be searched for in the buffer.
   * @return the index of the first byte for the token in the buffer, or -1 if
   *         the token was not found.
   */
  public static final int findToken(final ByteBuffer buffer, final byte[] token, int limit) {
  	if (limit <= 0) {
  		return findToken(buffer, token);
  	}
    buffer.mark();
    int tokenPos = -1;
    int i = 0;
    int toRead = limit;
    while ( buffer.hasRemaining() && toRead != 0) {
    	toRead--;
      if (buffer.get() == token[i]) {
        if (++i == token.length) {
          tokenPos = buffer.position() - i;
          break;
        }
      } else if (i > 0) {
        i = 0;
      }
    }
    
    buffer.reset();
    return tokenPos;
  }
  
  /**
   * Searches for the specified token in the given buffer and returns the index
   * of the first byte of the token inside the buffer.
   * <p>
   * This method will start reading from the current buffer's position and will
   * return the buffer with it's position and limit intact, but any
   * previously-definied mark will be discarded.
   * </p>
   * 
   * @param buffer
   *          The buffer to be read; any mark will be discarded!
   * @param token
   *          The token to be searched for in the buffer.
   * @return the index of the first byte for the token in the buffer, or -1 if
   *         the token was not found.
   */
  public static final int findToken(final ByteBuffer buffer, final byte[] token) {
    buffer.mark();
    int tokenPos = -1;
    int i = 0;
    while (buffer.hasRemaining()) {
      if (buffer.get() == token[i]) {
        if (++i == token.length) {
          tokenPos = buffer.position() - i;
          break;
        }
      } else if (i > 0) {
        i = 0;
      }
    }
    buffer.reset();
    return tokenPos;
  }
  
  /**
   * Searches for the specified token in the given buffer and returns the index
   * of the first byte of the token inside the buffer.
   * <p>
   * This method will start reading from the current buffer's position and will
   * return the buffer with it's position and limit intact, but any
   * previously-definied mark will be discarded.
   * </p>
   * 
   * @param buffer
   *          The buffer to be read; any mark will be discarded!
   * @param token
   *          The token to be searched for in the buffer.
   * @return the index of the first byte for the token in the buffer, or -1 if
   *         the token was not found.
   */
  public static final int findToken(final CharBuffer buffer, final char[] token) {
    buffer.mark();
    int tokenPos = -1;
    int i = 0;
    while (buffer.hasRemaining()) {
      if (buffer.get() == token[i]) {
        if (++i == token.length) {
          tokenPos = buffer.position() - i;
          break;
        }
      } else if (i > 0) {
        i = 0;
      }
    }
    buffer.reset();
    return tokenPos;
  }
  
  /**
   * Searches for the specified tokens in the given buffer and returns the index
   * of the first byte of the first token found inside the buffer.
   * <p>
   * This method will start reading from the current buffer's position and will
   * return the buffer with it's position and limit intact, but any
   * previously-definied mark will be discarded.
   * </p>
   * This method works with two tokens (ex: one is a delimiter and the other 
   * is an escape). As soon as the algorithm finds either token1 or token2, 
   * it will return.
   * 
   * @param buffer
   *          The buffer to be read; any mark will be discarded!
   * @param token1
   *          One of the tokens to be searched for in the buffer.
   * @param token2
   *          The other token to be searched for in the buffer.
   * @return [token1 index, token2 index] - the indexes of the first 
   *         byte for each token found, or -1 if the token was not found.
   */
  public static final int[] findToken(final CharBuffer buffer, 
                                      final char[] token1,
                                      final char[] token2) {
    buffer.mark();
    int token1Pos = -1, token2Pos = -1;
    int i1 = 0, i2 = 0;
    while (buffer.hasRemaining()) {
    	final char value = buffer.get();
	    if (token1Pos < 0 && value == token1[i1]) {
				if (++i1 == token1.length) {
					token1Pos = buffer.position() - i1;
					if (token2Pos >= 0 || i2 == 0) {
						break;
					}
				}
			} else if (token2Pos >= 0) {
				break;
			} else if (i1 > 0) {
				i1 = 0;
			}
	    if (token2Pos < 0 && value == token2[i1]) {
				if (++i2 == token2.length) {
					token2Pos = buffer.position() - i2;
					if (token1Pos >= 0 || i1 == 0) {
						break;
					}
				}
			} else if (token1Pos >= 0) {
				break;
			} else if (i2 > 0) {
				i2 = 0;
			}
    }
    buffer.reset();
    if (token2Pos >= 0 && token1Pos > token2Pos) {
    	token1Pos = -1;
    } else if (token1Pos >= 0 && token2Pos > token1Pos) {
    	token2Pos = -1;
    }
    return new int[] {token1Pos, token2Pos};
  }
  
  public static final int findToken(final CharBuffer buffer, final char token) {
		buffer.mark();
		int tokenPos = -1;
		while (buffer.hasRemaining()) {
			if (buffer.get() == token) {
				tokenPos = buffer.position() - 1;
				break;
			}
		}
		buffer.reset();
		return tokenPos;
	}
  
  public static final int[] findToken(final CharBuffer buffer, 
                                      final char token1,
                                      final char token2) {
  	buffer.mark();
  	int token1Pos = -1, token2Pos = -1;
  	while (buffer.hasRemaining()) {
  		final char value = buffer.get();
  		if (value == token1) {
  			token1Pos = buffer.position() - 1;
  			break;
  		} else if (value == token2) {
  			token2Pos = buffer.position() - 1;
  			break;
  		}
  	}
  	buffer.reset();
  	return new int[] {token1Pos, token2Pos};
  }
  
  public static final int countAdjacentOccurrencesBackwards(final CharBuffer buffer, 
                                                            final char token,
                                                            final int index) {
    int count = 0;
    for (int i = index; buffer.get(i) == token && i >= buffer.position(); count++, i--);
    return count;
  }
  
  public static final CharBuffer removeToken (final CharBuffer input, final char token) {
	  final CharBuffer output = CharBuffer.allocate(input.length());
	  input.mark();
	  int tokenPos = -1;
	  while (input.hasRemaining()) {
		  final char value = input.get();
		  if (value == token) {
			  final int pos = input.position() - 1;
			  if (pos > 0 && tokenPos == pos - 1) {
				  // previous char was an escape, so add it!
				  output.append(value);
				  tokenPos = -1;
			  } else {
				  tokenPos = pos;
			  }
		  } else {
			  output.append(value);
		  }
	  }
	  output.flip();
	  input.reset();
	  return output;
  }

}
