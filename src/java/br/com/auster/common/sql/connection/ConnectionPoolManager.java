package br.com.auster.common.sql.connection;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import br.com.auster.common.util.SyncQueue;
import br.com.auster.common.xml.DOMUtils;

/**
 * This class represents a connection manager that can keep an list of
 * opened server connections.  By calling the
 * <code>getConnection()</code> method, this manager will try to
 * return an opened connection to the server returned by the
 * <code>getNextServer()</code> method. If there is no connection in
 * the list for this server, a new connection will be created and
 * returned. To give a connection back to the list of opened
 * connections, the <code>giveConnection(connection)</code> method
 * must be called.
 */
public class ConnectionPoolManager extends ConnectionManager {

    protected final Map connectionMap = new Hashtable();
    
    /**
     * Creates a lot of connection managers, each one using its own configuration.
     * @throws Exception if some error occurs (like instantiating,
     * checking configuration, etc) while initializing the managers.
     */
    public static synchronized void initMultiManagers(Element config) 
        throws Exception
    {
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
            ConnectionFactory conFactory = (ConnectionFactory) DOMUtils.getInstance(conFactoryConf);
            managerHash.put(poolName, new ConnectionManager(poolConfig, conFactory));
        }
    }

    public ConnectionPoolManager(Element config, ConnectionFactory conFactory) {
        super(config, conFactory);
    }

    /**
     * This implementation uses a list of opened connections, so we
     * don't need to open a connection to the server if already there
     * is an opened one in the list.
     * @param server the server which we want to connect to.
     * @return an opened connection to the server.
     * @exception Exception if can not open a connection to this server.
     */
    protected Connection tryToConnect(Server server) 
        throws Exception
    {
        Connection connection = null;
        
        // Tries to find an opened connection before trying to create another one
        if (this.connectionMap.containsKey(server)) {
            while ((connection = (Connection) ((SyncQueue) this.connectionMap.get(server)).get()) != null) {
                if (connection.test()) {
                    log.debug("Getting an OPENED connection: " + connection);
                    return connection;
                } else {
                    log.debug("The connection " + connection + " can not be used: Test failed.");
                }
            }
        }
        
        connection = conFactory.createConnection(server, soTimeout, conTimeout);
        
        log.debug("Getting a NEW connection: " + connection);
        return connection;
    }
    
    /**
     * Just put the connection on the list of opened connections
     * @param connection the connection
     */
    public void giveConnection(Connection connection) {
        if (connection != null) {
            log.debug("Putting the connection " + connection + " in the opened connection list.");
            Server server = connection.getServer();
            SyncQueue queue = (SyncQueue) this.connectionMap.get(server);
            if (queue == null) {
                // If we don't have an entry to this server in the hash map, we create an queue for it
                queue = new SyncQueue();
                this.connectionMap.put(server, queue);
            }
            queue.put(connection);
        }
    }
    
    /**
     * Closes all the opened connections with the SMS servers
     */
    public void shutdown() {
        log.warn(i18n.getString("closingOpenedServerConns"));
        Iterator it = this.connectionMap.values().iterator();
        // for all servers in the map
        while (it.hasNext()) {
            SyncQueue queue = (SyncQueue) it.next();
            // for every connection to each server
            while (!queue.isEmpty()) {
                Connection connection = (Connection) queue.get();
                try {
                    if (connection != null) {
                        log.debug("Closing connection " + connection);
                        connection.disconnect();
                    }
                } catch (Exception e) {
                    log.error(i18n.getString("problemClosingConnection", connection), e);
                }
            }
        }
        log.warn(i18n.getString("done"));
    }
}
