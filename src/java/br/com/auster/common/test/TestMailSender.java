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
 * Created on Jul 18, 2005
 */
package br.com.auster.common.test;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

import br.com.auster.common.mail.MailSender;

import junit.framework.TestCase;

/**
 * @author framos
 * @version $Id: TestMailSender.java 128 2005-07-18 21:42:46Z framos $
 */
public class TestMailSender extends TestCase {

	
	private static class MyAuthenticator extends Authenticator {
		
		protected PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication("tracker@auster.com.br", "aust3r01d3");
		}
		
	}
	
	
	public void testSendEmailThroughAuster() {

		Properties p = new Properties();
		// setting protocols
		p.put("mail.transport.protocol", "smtp");
		p.put("mail.store.protocol", "pop3");
		// setting servers
		p.put("mail.smtp.host", "smtp.auster.com.br");
		p.put("mail.pop3.host", "pop3.auster.com.br");
		// setting user
		p.put("mail.user", "tracker@auster.com.br");
		
		// up to here, all parms. are mandatory!!!
		
		p.put("mail.from", "tracker@auster.com.br");
		
		p.put("mail.smtp.auth", "true");
		
		try {
			Session s = Session.getDefaultInstance(p, new MyAuthenticator());
			MailSender.sendMail(new String[] { "frederico.ramos@auster.com.br" }, "Test", "Go Go Go", null, null, s);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}


