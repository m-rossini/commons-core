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
 * Created on Sep 11, 2004
 *
 */
package br.com.auster.common.ejb;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * <P>
 * 	This helper class enables EJBs to get information from its environment entries.   
 * </P>
 * @author Frederico A Ramos
 * @version $Id: EJBUtils.java 91 2005-04-07 21:13:55Z framos $
 */
public abstract class EJBUtils {
	
    
    //########################################
    // static methods
    //########################################	
	
	/**
	 * <P>
	 * 	Searches the environment of the EJB and returns the configured entry. If there is no
	 * 		entry defined for the <code>_jndiName</code> then a exception will be thrown.
	 * </P>
	 * 
	 * @param _jndiName the jndi path, relative to <code>java:comp/env</code>
	 * 
	 * @return the environment entry requested
	 * 
	 * @throws NamingException if there is no entry for the specified jndi path
	 */
	public static Object getEnvironmentEntry(String _jndiName) throws NamingException {
		Context initial = new InitialContext();
		Context environment = (Context)initial.lookup("java:comp/env");
		return environment.lookup(_jndiName);
	}
}
