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
 * Created on Jan 01, 2005
 *
 */

package br.com.auster.common.mail;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import br.com.auster.common.util.I18n;
import br.com.auster.common.xml.DOMUtils;

/**
 * <P>
 * This helper class sends emails using a mail service configured and acessible
 * via JNDI.
 * </P>
 * 
 * @author Frederico A Ramos
 * @version $Id: MailSender.java 428 2008-05-28 17:54:51Z gbrandao $
 */
public final class MailSender {

	private static I18n i18n = I18n.getInstance(MailSender.class);

	// ########################################
	// static methods
	// ########################################

	/**
	 * <P>
	 * Sends an email using the parameters specified. The not-null parameters
	 * are the list of destinations, which must contain at least one address,
	 * the subject of the message, its content and the properties to locate the
	 * smpt/pop/imap server. If these properties are not set, a
	 * <code>InvalidArgumentException</code> will be thrown.
	 * </P>
	 * <P>
	 * If the content type is not specified, it will be assumed as
	 * <code>text/plain</code>.
	 * </P>
	 * 
	 * @param _to
	 *            the list of destination addresses
	 * @param _subject
	 *            the subject of this message
	 * @param _message
	 *            the body content of this message
	 * @param _from
	 *            the address which will be used as sender
	 * @param _contentType
	 *            the type of the content body
	 * @param _properties
	 *            the properties expected by JavaMail API to locate and create a
	 *            session to the smpt/pop/imap server
	 * @param _authenticator
	 *            the authenticator object. Can be null
	 * 
	 * @throws MessagingException
	 *             exceptions thrown by the message service
	 * @throws NamingException
	 *             exceptions thrown when requesting the messaging service
	 *             session
	 */
	public static final void sendMail(String[] _to, String _subject,
			String _message, String _from, String _contentType,
			Properties _properties, Authenticator _authenticator)
			throws MessagingException, NamingException {

		Session session = null;
		if (_properties != null) {
			session = Session.getDefaultInstance(_properties, _authenticator);
		} else {
			throw new IllegalArgumentException(i18n
					.getString("mailServerConfigExpected"));
		}
		sendMail(_to, _subject, _message, _from, _contentType, session);
	}

	/**
	 * <P>
	 * Sends an email using the parameters specified. The not-null parameters
	 * are the list of destinations, which must contain at least one address,
	 * the subject of the message, its content and the jndi name to request a
	 * session of the messaging service. If the jndi name is not specified, the
	 * class will not be able to get this reference and an exception will be
	 * thrown.
	 * </P>
	 * <P>
	 * If the content type is not specified, it will be assumed as
	 * <code>text/plain</code>.
	 * </P>
	 * 
	 * @param _to
	 *            the list of destination addresses
	 * @param _subject
	 *            the subject of this message
	 * @param _message
	 *            the body content of this message
	 * @param _from
	 *            the address which will be used as sender
	 * @param _contentType
	 *            the type of the content body
	 * @param _mailJndi
	 *            the jndi name to get a reference to the service session. In
	 *            case a jndi name is not provided (null), a default one will be
	 *            requested to the system using System.getProperties to
	 *            configure it
	 * 
	 * @throws MessagingException
	 *             exceptions thrown by the message service
	 * @throws NamingException
	 *             exceptions thrown when requesting the messaging service
	 *             session
	 */
	public static final void sendMail(String[] _to, String _subject,
			String _message, String _from, String _contentType, String mailJndi)
			throws MessagingException, NamingException {

		Session session = null;
		if (mailJndi != null) {
			session = (Session) PortableRemoteObject.narrow(
					new InitialContext().lookup(mailJndi), Session.class);
		} else {
			throw new IllegalArgumentException(i18n.getString("jndiExpected"));
		}
		sendMail(_to, _subject, _message, _from, _contentType, session);
	}

	/**
	 * <P>
	 * Sends an email using the parameters specified. The not-null parameters
	 * are the list of destinations, which must contain at least one address,
	 * the subject of the message, its content and the session to the mail
	 * service. If this reference is null, then an exception will be thrown.
	 * </P>
	 * <P>
	 * If the content type is not specified, it will be assumed as
	 * <code>text/plain</code>.
	 * </P>
	 * 
	 * @param _to
	 *            the list of destination addresses
	 * @param _subject
	 *            the subject of this message
	 * @param _message
	 *            the body content of this message
	 * @param _from
	 *            the address which will be used as sender
	 * @param _contentType
	 *            the type of the content body
	 * @param _session
	 *            the previously acquired session to the mail service
	 * 
	 * @throws MessagingException
	 *             exceptions thrown by the message service
	 * @throws NamingException
	 *             exceptions thrown when requesting the messaging service
	 *             session
	 */
	public static final void sendMail(String[] to, String _subject,
			String _message, String _from, String _contentType, Session _session)
			throws MessagingException, NamingException {

		// create a message
		Message msg = new MimeMessage(_session);

		// set the 'from' address
		if (_from != null) {
			InternetAddress addressFrom = new InternetAddress(_from);
			msg.setFrom(addressFrom);
		} else {
			msg.setFrom();
		}

		// set the 'to' addresses
		InternetAddress[] addressTo = new InternetAddress[to.length];
		for (int i = 0; i < to.length; i++) {
			addressTo[i] = new InternetAddress(to[i]);
		}
		msg.setRecipients(Message.RecipientType.TO, addressTo);

		// Setting the Subject and Content Type
		msg.setSubject(_subject);

		String mimeType = _contentType != null ? _contentType : "text/plain";
		msg.setContent(_message, mimeType);
		Transport.send(msg);
	}

	/**
	 * <P>
	 * Sends an email using the parameters specified. The not-null parameters
	 * are the list of destinations, which must contain at least one address,
	 * the subject of the message, its content and the jndi name to request a
	 * session of the messaging service.
	 * </P>
	 * <P>
	 * This version of the method leaves the sender address and the body content
	 * type as <code>null</code>. It also simplifies the destination
	 * addresses list, by acepting just one address.
	 * </P>
	 * 
	 * @param to
	 *            the list of destination addresses
	 * @param subject
	 *            the subject of this message
	 * @param message
	 *            the body content of this message
	 * @param mailJndi
	 *            the jndi name to get a reference to the service session
	 * 
	 * @throws MessagingException
	 *             exceptions thrown by the message service
	 * @throws NamingException
	 *             exceptions thrown when requesting the messaging service
	 *             session
	 */
	public static final void sendMail(String to, String subject,
			String message, String mailJndi) throws MessagingException,
			NamingException {

		sendMail(new String[] { to }, subject, message, null, null, mailJndi);
	}

	/**
	 * <P>
	 * Sends an email using the parameters specified. The not-null parameters
	 * are the list of destinations, which must contain at least one address,
	 * the subject of the message, its content and the jndi name to request a
	 * session of the messaging service.
	 * </P>
	 * <P>
	 * This version of the method does leaves the sender address and the body
	 * content type as <code>null</code>.
	 * </P>
	 * 
	 * @param to
	 *            the list of destination addresses
	 * @param subject
	 *            the subject of this message
	 * @param message
	 *            the body content of this message
	 * @param mailJndi
	 *            the jndi name to get a reference to the service session
	 * 
	 * @throws MessagingException
	 *             exceptions thrown by the message service
	 * @throws NamingException
	 *             exceptions thrown when requesting the messaging service
	 *             session
	 */
	public static final void sendMail(String[] to, String subject,
			String message, String mailJndi) throws MessagingException,
			NamingException {

		sendMail(to, subject, message, null, null, mailJndi);
	}

	/**
	 * 
	 * <P>
	 * Sends an email using the parameters specified in xml file.
	 * </P>
	 * 
	 * @param subject
	 *            the subject of this message
	 * @param message
	 *            the body content of this message
	 * @param mailConfig
	 *            Element with configurations params
	 * @throws MessagingException
	 *             exceptions thrown by the message service
	 * @throws NamingException
	 *             exceptions thrown when requesting the messaging service
	 *             session
	 * 
	 */

	public static final void sendMail(String subject, String message,
			Element mailConfig) throws MessagingException, NamingException {

		final String MAIL_CONFIGURATIONS = "mailConfigurations";
		final String MAIL_CONFIGURATION = "mailConfiguration";
		final String TRANSPORT_PROTOCOL = "transpProtocol";
		final String STORE_PROTOCOL = "storeProtocol";
		final String SMTP_HOST = "smtpHost";
		final String POP_HOST = "popHost";
		final String MAIL_USER = "mailUser";
		final String MAIL_FROM = "mailFrom";
		final String MAIL_TOS = "mailTos";
		final String MAIL_TO = "mailTo";
		final String SMTP_AUTH = "smtpAuth";
		final String MAIL_PASSWD = "mailPasswd";

		String transportProtocol;
		String storeProtocol;
		String smtpHost;
		String popHost;
		String mailUser;
		String mailFrom;
		String[] mailTo;
		String smtpAuth;
		String mailPasswd;

		try {
			Element mailConfigElem = DOMUtils.getElement(mailConfig, null,
					MAIL_CONFIGURATIONS, true);

			NodeList configElements = DOMUtils.getElements(mailConfigElem,
					null, MAIL_CONFIGURATION);

			Element configElem = DOMUtils.getElement(mailConfigElem,
					MAIL_CONFIGURATION, true);

			transportProtocol = DOMUtils.getText(
					DOMUtils.getElement(configElem, TRANSPORT_PROTOCOL, true))
					.toString();
			storeProtocol = DOMUtils.getText(
					DOMUtils.getElement(configElem, STORE_PROTOCOL, true))
					.toString();
			smtpHost = DOMUtils.getText(
					DOMUtils.getElement(configElem, SMTP_HOST, true))
					.toString();
			popHost = DOMUtils.getText(
					DOMUtils.getElement(configElem, POP_HOST, true)).toString();
			mailUser = DOMUtils.getText(
					DOMUtils.getElement(configElem, MAIL_USER, true))
					.toString();
			mailFrom = DOMUtils.getText(
					DOMUtils.getElement(configElem, MAIL_FROM, true))
					.toString();
			mailPasswd = DOMUtils.getText(
					DOMUtils.getElement(configElem, MAIL_PASSWD, true))
					.toString();

			mailTo = DOMUtils.getText(
					DOMUtils.getElement(configElem, MAIL_TO, true)).toString()
					.split(";");

			smtpAuth = DOMUtils.getText(
					DOMUtils.getElement(configElem, SMTP_AUTH, true))
					.toString();

			//
			PasswordAuthentication passAuth = new PasswordAuthentication(
					mailUser, mailPasswd);

			Properties p = new Properties();
			// setting protocols
			p.put("mail.transport.protocol", transportProtocol);
			p.put("mail.store.protocol", storeProtocol);
			// setting servers
			p.put("mail.smtp.host", smtpHost);
			p.put("mail.pop3.host", popHost);
			// setting user
			p.put("mail.user", mailUser);
			// up to here, all parms. are mandatory!!!
			p.put("mail.from", mailFrom);
			p.put("mail.smtp.auth", smtpAuth);

			//

			Session s = Session.getDefaultInstance(p, new MyAuthenticator(
					mailUser, mailPasswd));
			MailSender.sendMail(mailTo, subject, message, null, null, s);
		} catch (MessagingException e) {
			throw e;
		} catch (NamingException e) {
			throw e;
		}
	}

	private static class MyAuthenticator extends Authenticator {

		String mailUser;
		String mailPasswd;

		public MyAuthenticator(String mailUser, String mailPasswd) {
			this.mailUser = mailUser;
			this.mailPasswd = mailPasswd;
		}

		protected PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(this.mailUser, this.mailPasswd);
		}
	}

}
