/*
 * Copyright (c) 2004-2006 Auster Solutions. All Rights Reserved.
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
 * Created on 22/09/2006
 */
package br.com.auster.common.sql;

import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.commons.dbcp.DriverManagerConnectionFactory;

import br.com.auster.common.io.IOUtils;

/**
 * This class is intended to resolve some security issues regarding JOCL configuration files.
 * <p>
 * To properly configure the database pool, the database password should be informed inside 
 *   these files. Since this might trigger some security issues under production environment,
 *   we decided to create a secure version.
 * <p>
 * The idea here is to use stop sending the password as parameter in the constructor of the
 *   {@value DriverManagerConnectionFactory} class, and start identifying a file (in the filesystem
 *   or bundled in some JAR archive) which will be encrypted and containing only the user's password.
 * <p>
 * Internally, this implementation will decrypt the content of the file, and use it as password
 *   to the parent Class. This will avoid setting explicitly the password in the configuration file.
 * 
 * @author framos
 * @version $Id$
 *
 */
public class SecureDriverManagerConnectionFactory extends DriverManagerConnectionFactory {

	
	
	public static final String ENCRYPT_PASSWORD_FILE = "password.file";
	
	
	/**
	 * This constructor will decrypt the password file, get its content and use it as password
	 *   when calling the parent class.
	 *   
	 * @param _url the database URL
	 * @param _user the username to use when opening connections
	 * @param _passwordFile the file containing the encrypted password
	 */
	public SecureDriverManagerConnectionFactory(String _url, String _user, String _passwordFile) {
		super(_url, _user, readEncriptedPassword(_passwordFile));
	}
	
	/**
	 * This constructor will decrypt the password file, indicated in the key {@value #ENCRYPT_PASSWORD_FILE}.
	 *   If this property is not found, then the property "as-is" is sent to the parent class.
	 * <p>
	 * If the file is found, the decripted password will be added to the property object
	 *   in the JDBC pre-defined key <code>password</code>.
	 *   
	 * @param _url the database URL
	 * @param _properties the list of properties for the selected database
	 */
	public SecureDriverManagerConnectionFactory(String _url, Properties _properties) {
		super(_url, readEncryptedPasswordFromProperties(_properties));
	}
	
	
	protected static String readEncriptedPassword(String _passwordFile) {
		try {
			InputStreamReader reader = new InputStreamReader(IOUtils.openFileForRead(_passwordFile, true));
			StringBuffer buffer = new StringBuffer();
			char[] readBuffer = new char[128];
			int readCount = 0;
			while ((readCount = reader.read(readBuffer)) > 0) {
				buffer.append(readBuffer, 0, readCount);
			}
			return buffer.toString();
		} catch (Exception e) {
			throw new RuntimeException("Could not read encrypted password file", e); 
		}
	}
	
	protected static Properties readEncryptedPasswordFromProperties(Properties _properties) {
		if ((_properties == null) || (!_properties.containsKey(ENCRYPT_PASSWORD_FILE))) {
			return _properties;
		}
		try {
			String passwordFile = _properties.getProperty(ENCRYPT_PASSWORD_FILE);
			String password = readEncriptedPassword(passwordFile);
			_properties.put("password", password);
			return _properties;
		} catch (Exception e) {
			throw new RuntimeException("Could not read encrypted password file", e); 
		}
		
	}
}
