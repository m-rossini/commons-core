package br.com.auster.common.sql.connection;

import org.w3c.dom.Element;

/**
 * This class is a server and connection instances factory. It's used
 * in the ConnectionManager to instanciate the servers and to open new
 * connections.
 */
public interface ConnectionFactory {
    
    /**
     * Creates a server instance, using the given configuration.
     * @param config the needed configuration to create this server.
     * @return a Server instance, representing a server with
     * configuration config.
     */
    public Server createServer(Element config) throws IllegalArgumentException;
    
    /**
     * Opens a new connection to the server.
     * @param server the server we want to connect to.
     * @param soTimeout the SO timeout value in milliseconds.
     * @param conTimeout the amount of time (in milliseconds) that
     * this method will wait for the connection to be stabilished.
     * @return a new connection to the given server.
     * @throws Exception if the connection could not be stabilished
     */
    public Connection createConnection(Server server, int soTimeout, int conTimeout) throws Exception;
}
