/*
 *
 * Copyright (c) 2004-2008 Auster Solutions. All Rights Reserved.
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
 * Created on Apr 11, 2008
 *
 * @(#)Audit.java Apr 11, 2008
 */
package br.com.auster.common.log;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class Audit {

	private static final String DATE_FORMAT_PATTERN = "dd/MM/yyyy HH:mm:ss";

	// Our fully qualified class name.
	static String FQCN = Audit.class.getName();
	static boolean JDK14 = false;

	static {
		String version = System.getProperty("java.version");
		if (version != null) {
			JDK14 = version.startsWith("1.4");
		}
	}

	private Logger logger;

	public Audit(String name) {
		this.logger = Logger.getLogger(name);
	}

	public Audit(Class<?> clazz) {
		this(clazz.getName());
	}

	public void trace(Object msg) {
		logger.log(FQCN, Level.TRACE, msg, null);
	}

	public void trace(Object msg, Throwable t) {
		logger.log(FQCN, Level.TRACE, msg, t);
		logNestedException(Level.TRACE, msg, t);
	}

	public boolean isTraceEnabled() {
		return logger.isEnabledFor(Level.TRACE);
	}

	public void debug(Object msg) {
		logger.log(FQCN, Level.DEBUG, msg, null);
	}

	public void debug(Object msg, Throwable t) {
		logger.log(FQCN, Level.DEBUG, msg, t);
		logNestedException(Level.DEBUG, msg, t);
	}

	public boolean isDebugEnabled() {
		return logger.isDebugEnabled();
	}

	public void info(Object msg) {
		logger.log(FQCN, Level.INFO, msg, null);
	}

	public void info(Object msg, Throwable t) {
		logger.log(FQCN, Level.INFO, msg, t);
		logNestedException(Level.INFO, msg, t);
	}

	public boolean isInfoEnabled() {
		return logger.isInfoEnabled();
	}

	public void warn(Object msg) {
		logger.log(FQCN, Level.WARN, msg, null);
	}

	public void warn(Object msg, Throwable t) {
		logger.log(FQCN, Level.WARN, msg, t);
		logNestedException(Level.WARN, msg, t);
	}

	public void error(Object msg) {
		logger.log(FQCN, Level.ERROR, msg, null);
	}

	public void error(Object msg, Throwable t) {
		logger.log(FQCN, Level.ERROR, msg, t);
		logNestedException(Level.ERROR, msg, t);
	}

	public void fatal(Object msg) {
		logger.log(FQCN, Level.FATAL, msg, null);
	}

	public void fatal(Object msg, Throwable t) {
		//logger.log(FQCN, Level.FATAL, msg, t);
		//logNestedException(Level.FATAL, msg, t);
	}

	void logNestedException(Level level, Object msg, Throwable t) {
		if (t == null)
			return;

		try {
			Class<? extends Throwable> tC = t.getClass();
			Method mA[] = tC.getMethods();
			Method nextThrowableMethod = null;
			for (int i = 0; i < mA.length; i++) {
				if (("getCause".equals(mA[i].getName()) && !JDK14)
						|| "getRootCause".equals(mA[i].getName())
						|| "getNextException".equals(mA[i].getName())
						|| "getException".equals(mA[i].getName())) {
					// check param types
					Class<?> params[] = mA[i].getParameterTypes();
					if (params == null || params.length == 0) {
						// just found the getter for the nested throwable
						nextThrowableMethod = mA[i];
						break; // no need to search further
					}
				}
			}

			if (nextThrowableMethod != null) {
				// get the nested throwable and log it
				Throwable nextT = (Throwable) nextThrowableMethod.invoke(t,
						new Object[0]);
				if (nextT != null) {
					this.logger.log(FQCN, level, "Previous log CONTINUED: ",
							nextT);
				}
			}
		} catch (Exception e) {
			// do nothing
		}
	}

	/**
	 * Is responsible in
	 * @param msg Message
	 */
	public void audit(Object msg) {
		// logger.log(FQCN, LevelAudit.AUDIT, msg, null);
		logger.log(FQCN, LevelAudit.AUDIT, msg, null);
		// logger.log(LevelAudit.AUDIT, msg);
	}

	/**
	 * Is responsible in
	 * @param operation
	 * @param user
	 * @param ip
	 * @param msg
	 */
	public void audit(String operation, String value, String user, String ip, String additionalMessage) {
		audit(operation, value, user, ip, null, additionalMessage);
	}

	/**
	 * Is responsible in
	 * @param operation
	 * @param user
	 * @param ip
	 */
	public void audit(String operation, String value, String user, String ip) {
		audit(operation, value, user, ip, null, null);
	}

	/**
	 * Responsável por imprimir uma mensagem "AUDIT" no appender do Log4J
	 * @param operation Nome da Operação
	 * @param user Usuário da Operação
	 * @param ip IP da máquinaa Cliente
	 * @param msg Mensagem que se deseja adicionar na Auditoria (não é necessária)
	 * @param t Exception de Erro
	 */
	public void audit(String operation, String value, String user, String ip, Throwable t) {
		audit(operation, value, user, ip, t, null);
	}

	/**
	 * Responsável por imprimir uma mensagem "AUDIT" no appender do Log4J
	 * @param operation Nome da Operação
	 * @param user Usuário da Operação
	 * @param ip IP da máquinaa Cliente
	 * @param msg Mensagem que se deseja adicionar na Auditoria (não é necessária)
	 * @param t Exception de Erro
	 */
	public void audit(String operation, String value, String user, String ip, Throwable t, String additionalMessage) {
		String message = mountMessage(operation, value, user, ip, t, additionalMessage);
		audit(message);
	}

	/**
	 * Is responsible in mount Message in Defined format
	 * EX:
	 *	Sucesso
	 *	<dd/mm/aaaa HH:mm:ss> - <Operação> efetuada com sucesso - <usuário que efetuou a operação> - <IP do usuário que efetuou a Operação>
	 *	Erro	
	 *	<dd/mm/aaaa HH:mm:ss> - Erro ao efetuar <Operação> - <Motivo do Erro> - <usuário que efetuou a operação> - <IP do usuário que efetuou a Operação>
	 *
	 * @param operation Name of Oparation
	 * @param user User of Operation
	 * @param ip IP of client machine
	 * @param t Error message
	 * @param msg Aditional message
	 * @return Message Message was formated
	 */
	private String mountMessage(String operation, String value, String user, String ip, Throwable t, String additionalMessage) {

		String SEPARATOR = " - ";
		String BLANK = " ";

		//String date = formatDate(new GregorianCalendar(), DATE_FORMAT_PATTERN);
		//String message = date + SEPARATOR;
		String message = "";

		if (value != null && !value.equalsIgnoreCase("")) {
			operation += BLANK;
			operation += value;
		}

		if (t != null) {
			message += "Erro ao efetuar ";
			message += operation;
			message += SEPARATOR;
			message += t.getMessage();
		} else {
			message += operation;
			message += " efetuada com sucesso";
		}
		if (additionalMessage != null && !additionalMessage.equalsIgnoreCase("")) {
			message += BLANK;
			message += additionalMessage;
		}
		message += SEPARATOR;
		message += user;
		message += SEPARATOR;
		message += ip;
		return message;
	}
	
	/** Busca a mensagem de erro respectiva ao codico informado
	 * <p>Example:
	 * <pre>
	 * 999 = Usuário não informado
	 * </pre>
	 * </p>
	 * @param eventCode Código do evento do Erro
	 * @return Mensagem do erro Respectivo
	 */
	private static String getResource(String eventCode) {
		return findInMessageApplication(eventCode);
	}

	/**
	 * Metodo responsável em localizar o valor no arquivo
	 * properties.messageApplication.properties.
	 * @param key chave que será localizada.
	 * @return objeto encontrado
	 */
	private static String findInMessageApplication(String key) {
		String retorno = null;
		try {
			Locale currentLocale;

			currentLocale = java.util.Locale.getDefault();
			//currentLocale = new Locale(language, country);
			ResourceBundle bundle = ResourceBundle.getBundle("MessagesBundle", currentLocale);
			retorno = bundle.getString(key);
		} catch (Exception e) {
			retorno = null;
		}
		return retorno;
	}

	/**
	 * Is responsible in in format date in pattern format
	 * @param date Date of event
	 * @param pattern Pattern letters
	 * @return Date formated
	 */
	private static String formatDate(Calendar date, String pattern) {
		return formatDate(date.getTime(), pattern);
	}

	/**
	 * Is responsible in format date in pattern format
	 * @param date Date of event
	 * @param pattern pattern letters
	 * @return Date formated
	 */
	private static String formatDate(Date date, String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat();
		try {
			sdf.applyPattern(pattern);
			return sdf.format(date);
		} catch (Exception e) {
			return date.toString();
		}
	}
}