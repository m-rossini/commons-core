package br.com.auster.common.sql.connection;


/**
 * This class represents a connection to some server.
 */
public interface Connection {
    
    /**
     * Disconnect from the server.
     */
    public void disconnect();
    
    /**
     * Returns the server object.
     */
    public Server getServer();

    /**
     * Tests the connection.
     */
    public boolean test();
}
