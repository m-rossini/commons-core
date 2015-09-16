package br.com.auster.common.jms;

import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

/**
 * Helper class for sending JMS messages.
 */
public class QueueHelper
{
	private QueueConnectionFactory factory;
	private Queue queue;

	private Logger log = Logger.getLogger(this.getClass());

	/**
	 * Creates a QueueHelper for a local queue.
	 * @param factoryName the name of the queue factory
	 * @param queueName the name of the queue
	 */
	public QueueHelper(String factoryName, String queueName) throws NamingException {
		this(new InitialContext(), factoryName, queueName);
	}

	/**
	 * Creates a QueueHelper for a remote queue.
	 * @param context the initial context
	 * @param factoryName the name of the queue factory
	 * @param queueName the name of the queue
	 */
	public QueueHelper(Context context, String factoryName, String queueName) throws NamingException {
		this.factory = lookupFactory(context, factoryName);
		this.queue = lookupQueue(context, queueName);
	}

	private QueueConnectionFactory lookupFactory(Context context, String factoryName) throws NamingException {
		return (QueueConnectionFactory) context.lookup(factoryName);
	}

	private Queue lookupQueue(Context context, String queueName) throws NamingException {
		log.debug("Looking up queue " + queueName + "...");
		return (Queue) context.lookup(queueName);
	}

	/**
	 * Sends an object message to a JMS queue.
	 * @param object the object to be sent
	 */
	public void sendObjectMessage(Serializable object) throws JMSException {
		QueueConnection connection = null;
		try {
			connection = openConnection(this.factory);
			QueueSession session = openSession(connection);
			QueueSender sender = createSender(session, this.queue);
			Message message = createObjectMessage(session, object);
			sendMessage(sender, message);
		} finally {
			closeConnection(connection);
		}
	}

	/**
	 * Sends a text message to a JMS queue.
	 * @param object the object to be sent
	 */
	public void sendTextMessage(String text) throws JMSException {
		QueueConnection connection = null;
		try {
			connection = openConnection(this.factory);
			QueueSession session = openSession(connection);
			QueueSender sender = createSender(session, this.queue);
			Message message = createTextMessage(session, text);
			sendMessage(sender, message);
		} finally {
			closeConnection(connection);
		}
	}

	/**
	 * Sends a map message to a JMS queue.
	 * @param map the map to be sent
	 */
	public void sendMapMessage(Map<String, ?> map) throws JMSException {
		QueueConnection connection = null;
		try {
			connection = openConnection(this.factory);
			QueueSession session = openSession(connection);
			QueueSender sender = createSender(session, this.queue);
			Message message = createMapMessage(session, map);
			sendMessage(sender, message);
		} finally {
			closeConnection(connection);
		}
	}

	private QueueConnection openConnection(QueueConnectionFactory factory) throws JMSException {
		return factory.createQueueConnection();
	}

	private QueueSession openSession(QueueConnection connection) throws JMSException {
		return connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
	}

	private QueueSender createSender(QueueSession session, Queue queue) throws JMSException {
		QueueSender sender = session.createSender(queue);
		return sender;
	}

	private Message createObjectMessage(QueueSession session, Serializable object) throws JMSException {
		log.debug("Creating message from " + object + "...");
		return session.createObjectMessage(object);
	}

	private Message createTextMessage(QueueSession session, String text) throws JMSException {
		log.debug("Creating message from \"" + text + "\"...");
		return session.createTextMessage(text);
	}

	private Message createMapMessage(QueueSession session, Map<String, ?> map) throws JMSException {
		log.debug("Creating message from \"" + map + "\"...");
		MapMessage mapMessage = session.createMapMessage();
		for (Entry<String, ?> entry : map.entrySet()) {
			mapMessage.setObject(entry.getKey(), entry.getValue());
		}
		return mapMessage;
	}

	private void sendMessage(QueueSender sender, Message message) throws JMSException {
		log.debug("Sending message...");
		sender.send(message);
	}

	private void closeConnection(QueueConnection connection) throws JMSException {
		if (connection != null) {
			log.debug("Closing connection...");
			connection.close();
		}
	}
}
