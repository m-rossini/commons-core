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
 * Created on Jul 13, 2005
 */
package br.com.auster.common.io;

import java.util.Map;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import br.com.auster.common.lang.StringUtils;
import br.com.auster.common.xml.DOMUtils;

/**
 * <p><b>Title:</b> FilenameBuilder</p>
 * <p><b>Description:</b> Utility class used to create output 
 * filenames from Requests</p>
 * <p><b>Copyright:</b> Copyright (c) 2004-2005</p>
 * <p><b>Company:</b> Auster Solutions</p>
 * 
 * <b>NOTE:</b> XML Namespace is ignored.
 * 
 * Example: 
 * <code>
 * <xxxx> ---> config Element passed in constructor
 *   <token>
 *     <format align="right" width="15" fill-char="0">
 *       <insert-request-field name="myName"/>
 *     </format>
 *   </token>
 *   <token>_test.txt</token>
 * </xxxx>
 * </code>
 * 
 * @author rbarone
 * @version $Id: FilenameBuilder.java 98 2005-08-19 19:09:54Z rbarone $
 */
public class BaseFilenameBuilder {

  protected static final String TOKEN_ELEMENT = "token";

  protected static final String FORMAT_ELEMENT = "format";

  protected static final String FIELD_ELEMENT = "insert-request-field";

  protected static final String NAME_ATT = "name";

  protected static final String ALIGN_ATT = "align";

  protected static final String FILL_CHAR_ATT = "fill-char";

  protected static final String WIDTH_ATT = "width";
  

  protected class TokenFormat {

    protected static final int ALIGN_LEFT = 0;

    protected static final int ALIGN_RIGHT = 1;

    protected static final String ALIGN_RIGHT_NAME = "right";

    protected static final char DEFAULT_FILL_CHAR = ' ';

    protected int align, width;

    protected char fillChar;

    protected TokenFormat(String align, int width, String fillChar) {
      // ALIGN_LEFT is default, even if "align" value is not valid.
      if (align == null || !align.equals(ALIGN_RIGHT_NAME)) {
        this.align = ALIGN_LEFT;
      } else {
        this.align = ALIGN_RIGHT;
      }
      this.width = width;
      if (fillChar == null || fillChar.length() == 0) {
        this.fillChar = DEFAULT_FILL_CHAR;
      } else {
        this.fillChar = fillChar.charAt(0);
      }
    }
  }
  

  protected class FileNameToken {

    protected String token;

    protected TokenFormat format;

    protected boolean isRequestField;

    protected FileNameToken(String token, TokenFormat format, boolean isRequestField) {
      this.token = token;
      this.format = format;
      this.isRequestField = isRequestField;
    }
  }

  
  // Instance variables
  protected FileNameToken[] fileNameTokens;

  /**
   * Returns a new FilenameBuilder configured fro the specified
   * <code>Element</code>.
   * 
   * @param config
   *          The configuration <code>Element</code> that contains a
   *          {@link #FILENAME_ELEMENT} and childs.
   */
  public BaseFilenameBuilder(final Element config) {
    if (config == null) {
      // Config is null so let's get out of here before a NPE occurs.
      // This is a desired situation since the 'tokens' variable will remain
      // empty (not null) and 'getFilename' will return an empty String.
      return;
    }
    NodeList tokens = DOMUtils.getElements(config, TOKEN_ELEMENT);
    this.fileNameTokens = new FileNameToken[tokens.getLength()];
    for (int i = 0; i < tokens.getLength(); i++) {
      Element token = (Element) tokens.item(i);
      Element fieldParent = token;
      TokenFormat tokenFormat = null;
      Element format = DOMUtils.getElement(token, FORMAT_ELEMENT, false);
      if (format != null) {
        fieldParent = format;
        String align = DOMUtils.getAttribute(format, ALIGN_ATT, true);
        int width = DOMUtils.getIntAttribute(format, WIDTH_ATT, true);
        String fillChar = DOMUtils.getAttribute(format, FILL_CHAR_ATT, false);
        tokenFormat = new TokenFormat(align, width, fillChar);
      }
      Element field = DOMUtils.getElement(fieldParent, FIELD_ELEMENT, false);
      if (field != null) {
        String name = DOMUtils.getAttribute(field, NAME_ATT, true);
        this.fileNameTokens[i] = new FileNameToken(name, tokenFormat, true);
      } else {
        String value = DOMUtils.getText(fieldParent).toString();
        this.fileNameTokens[i] = new FileNameToken(value, tokenFormat, false);
      }
    }
  }


  /***
   * 
	 * Creates the file that will be opened for writing as the output.
   * 
   * @param attributes A Map<String, String> with the attributes to be used in building the file name
   * @return A String representing a file name.
   * @throws Exception If something goes wrong
   */
  public String getFilename(Map<String, String> attributes) throws Exception {
  	if (attributes == null) {
  		return null;
  	}
    final StringBuffer fileName = new StringBuffer();
    int count = (this.fileNameTokens == null) ? 0 : this.fileNameTokens.length;
    for (int i = 0; i < count; i++) {
      String token = this.fileNameTokens[i].token;
      if (this.fileNameTokens[i].isRequestField) {
        final Object field = attributes.get(token);
        if (field == null) {
          token = "";
        } else {
          token = field.toString().trim();
        }
      }
      if (this.fileNameTokens[i].format != null) {
        TokenFormat format = this.fileNameTokens[i].format;
        if (format.align == TokenFormat.ALIGN_LEFT) {
          token = StringUtils.alignToLeft(token, format.width, format.fillChar);
        } else {
          token = StringUtils.alignToRight(token, format.width, format.fillChar);
        }
      }
      fileName.append(token);
    }
    return fileName.toString();
  	
  }
}
