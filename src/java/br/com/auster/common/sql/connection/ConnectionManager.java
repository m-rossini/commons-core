package br.com.auster.common.sql.connection;

import java.net.ConnectException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import br.com.auster.common.util.I18n;
import br.com.auster.common.xml.DOMUtils;

/**
 * This class represents a connection manager. It needs a list of servers, and
 * every time the method getConnection() is called a new connection to one of
 * the servers is opened. When the giveConnection(connection) is called, the
 * connection to the server will be closed.
 */
public class ConnectionManager {

	// The connection manager configuration parameters
	protected static final String POOL_ELEMENT = "pool";

	protected static final String HOST_ELEMENT = "host";

	protected static final String NAME_ATTR = "name";

	protected static final String TRIES_ATTR = "max-connection-tries";

	protected static final String SLEEP_TIME_ATTR = "sleep-between-tries";

	protected static final String SO_TIMEOUT_ATTR = "connection-idle-timeout";

	protected static final String CON_TIMEOUT_ATTR = "open-connection-timeout";

	// The connection factory configuration parameters
	protected static final String CON_FACTORY_ELEMENT = "connection-factory";

	// The class attributes
	protected static final Map managerHash = new HashMap();

	// The instance attributes
	protected int tries, sleepTime, soTimeout, conTimeout;

	protected final List servers = new LinkedList();

	protected ConnectionFactory conFactory;

	protected final Logger log = Logger.getLogger(this.getClass());

	protected final I18n i18n = I18n.getInstance(ConnectionManager.class);

	/**
	 * Creates a lot of connection managers, each one using its own configuration.
	 * 
	 * @throws Exception
	 *           if some error occurs (like instantiating, checking configuration,
	 *           etc) while initializing the managers.
	 */
	public static synchronized void initMultiManagers(Element config)
			throws Exception {
		if (!managerHash.isEmpty()) {
			shutdownManagers();
		}
		NodeList poolList = DOMUtils.getElements(config, POOL_ELEMENT);
		for (int i = 0; i < poolList.getLength(); i++) {
			Element poolConfig = (Element) poolList.item(i);
			String poolName = DOMUtils.getAttribute(poolConfig, NAME_ATTR, true);

			Element conFactoryConf = DOMUtils.getElement(poolConfig,
																									 CON_FACTORY_ELEMENT,
																									 true);
			ConnectionFactory conFactory = (ConnectionFactory) 
				DOMUtils.getInstance(conFactoryConf);
			managerHash.put(poolName, new ConnectionManager(poolConfig, conFactory));
		}
	}

	/**
	 * Gets a connection manager named 'key'.
	 * 
	 * @param key
	 *          the connection manager name.
	 * @return a connection manager associated to the key, null if it could not be
	 *         found.
	 * 
	 */
	public static synchronized ConnectionManager getManager(String key) {
		return (ConnectionManager) managerHash.get(key);
	}

	/**
	 * Shuts down the connection managers that was initialized by the
	 * <code>initMultiManagers()</code> method.
	 */
	public static synchronized void shutdownManagers() {
		// Stops the managers
		Iterator it = managerHash.values().iterator();
		while (it.hasNext())
			((ConnectionManager) it.next()).shutdown();
		managerHash.clear();
	}

	/** Creates a new instance of ConnectionManager */
	public ConnectionManager(Element config, ConnectionFactory conFactory) {
		this.initialize(config);
	}

	/**
	 * Reloads the configuration of the Connection Pool Manager
	 */
	public synchronized void reload(Element config) {
		log.info(i18n.getString("reloadConnPoolConf"));
		shutdown();
		initialize(config);
		log.info(i18n.getString("done"));
	}

	/**
	 * Initializes some variables using the properties values
	 */
	protected void initialize(Element config) {
		this.initServers(config);
		this.tries = DOMUtils.getIntAttribute(config, TRIES_ATTR, true);
		// In case the user wants to try forever
		if (this.tries <= 0)
			this.tries = Integer.MAX_VALUE;
		this.sleepTime = DOMUtils.getIntAttribute(config, SLEEP_TIME_ATTR, true);
		if (this.sleepTime < 0)
			this.sleepTime = 0;
		try {
			this.soTimeout = DOMUtils.getIntAttribute(config, SO_TIMEOUT_ATTR, true);
			if (this.soTimeout < 0)
				this.soTimeout = 0;
		} catch (IllegalArgumentException e) {
			this.soTimeout = 0;
		}
		try {
			this.conTimeout = DOMUtils.getIntAttribute(config, CON_TIMEOUT_ATTR, true) / 1000;
			if (this.conTimeout < 0)
				this.conTimeout = 0;
		} catch (IllegalArgumentException e) {
			this.conTimeout = 0;
		}
	}

	/**
	 * Reads the name of the servers from the properties file
	 */
	protected void initServers(Element config) {
		// Gets the name of the servers
		NodeList hostList = DOMUtils.getElements(config, HOST_ELEMENT);
		for (int i = 0; i < hostList.getLength(); i++) {
			Element hostConfig = (Element) hostList.item(i);
			try {
				Server server = conFactory.createServer(hostConfig);
				log.info(i18n.getString("addingServer", server));
				this.servers.add(server);
			} catch (IllegalArgumentException e) {
				log.error(i18n.getString("couldntAddServer", hostConfig
						.getAttribute(NAME_ATTR)), e);
			}
		}
	}

	/**
	 * Gets an open connection from a server.
	 */
	public Connection getConnection() throws ConnectException {
		boolean connected = false;
		Connection con = null;
		int retries = 0;

		// Tries to connect some times
		while (!connected && retries < tries) {
			retries++;

			// Tries to connect to each server. The trying order may be overloaded by
			// subclasses by implementing getNextServer
			for (int i = 0; i < servers.size() && !connected; i++) {
				Server server = getNextServer(i);
				try {
					con = tryToConnect(server);
					log.debug("Connected to the server " + server);
					connected = true;
				} catch (Exception e) {
					log.error(i18n.getString("errorConnecting", server), e);
				}
			}
			if (!connected) {
				if (retries < tries) {
					// Problems with the servers. Let's try again in a few seconds
					log.warn(i18n.getString("serversDown", new Long(tries - retries),
																	new Long(sleepTime)));
					try {
						Thread.sleep(sleepTime);
					} catch (InterruptedException e111) {
					}
				}
			}
		}
		if (con == null)
			throw new ConnectException(i18n.getString("serversDown"));
		else
			return con;
	}

	/**
	 * Returns a server. It is used to search for a working server.
	 * 
	 * @param i
	 *          the step of the search (i.e. the i-th search for a server)
	 */
	protected Server getNextServer(int i) {
		return (Server) servers.get(i);
	}

	/**
	 * Returns a connection to the server.
	 * 
	 * @param server
	 *          the server.
	 * @return an opened connection to the server.
	 * @exception Exception
	 *              if can not open an connection to the server.
	 */
	protected Connection tryToConnect(Server server) throws Exception {
		return conFactory.createConnection(server, soTimeout, conTimeout);
	}

	/**
	 * Gives the connection back to the manager. This implementation will only
	 * disconnect it.
	 */
	public void giveConnection(Connection con) {
		try {
			con.disconnect();
			log.debug("Disconnected from the server " + con.getServer());
		} catch (Exception e) {
			log.error(i18n.getString("problemDisconnectingServer", con.getServer()),
								e);
		}
	}

	/**
	 * Gets the maximum number of tries this manager will loop before giving up to
	 * return an opened connection.
	 */
	public int getTries() {
		return tries;
	}

	/**
	 * Shuts down this connection manager.
	 */
	public synchronized void shutdown() {
		// Don't need to do anything
	}
}
