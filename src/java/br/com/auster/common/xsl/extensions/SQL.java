package br.com.auster.common.xsl.extensions;


import java.sql.SQLException;

import javax.naming.NamingException;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Node;

import br.com.auster.common.lang.StringUtils;
import br.com.auster.common.sql.SQLConnectionManager;

/**
 * This class is used as a SQL XSL extension.
 */
public final class SQL {
    
	  /**
	   * {@value} - char used to split arguments
	   */
		public static final char SPLIT_CHAR = '?';
	
    /**
     * Gets a connection from the pool named 'poolName', runs a query using the statement
     * named 'statementName', read from a configuration file, and returns the result.
     * @param poolName the pool name.
     * @param statementName the statement name to be read from the query file.
     * @param args the arguments to be passed to the connection to prepare the statement.
     * @return the result of the query, in the form:
     *         <result>
     *             <record fieldName1="value1" fieldName2="value2" ... />
     *             ...
     *         </result>
     */
    public static final Node query(String poolName, String statementName, String args) 
        throws SQLException, NamingException, ParserConfigurationException
    {
        final SQLConnectionManager manager = SQLConnectionManager.getInstance(poolName); 
        final String[] argList;
        if (args == null) {
        	argList = null; 
        } else {
        	argList = StringUtils.split(args, SPLIT_CHAR);
        }
        return manager.queryDOM(statementName, null, argList);
    }

    /**
     * First checks if we already have the result (using an internal cache). If not runs the query.
     * @see #query(String,String,String)
     */
    public static final Node query(String poolName, String statementName) 
        throws SQLException, NamingException, ParserConfigurationException
    {
        return query(poolName, statementName, null);
    }

    /**
     * Gets a connection from the pool named 'poolName', runs a update/insert/delete 
     * using the statement named 'statementName' (read from a configuration file), 
     * and returns the number of rows affected.
     * @param poolName the pool name.
     * @param statementName the statement name to be read from the query file.
     * @param args the arguments to be passed to the connection to prepare the statement.
     * @return the number of rows affected.
     */
    public static final int executeUpdate(String poolName, String statementName, String args) 
        throws SQLException, NamingException
    {
        final SQLConnectionManager manager = SQLConnectionManager.getInstance(poolName);
        final String[] argList;
        if (args == null) {
        	argList = null; 
        } else {
        	argList = StringUtils.split(args, SPLIT_CHAR);
        }
        return manager.executeUpdate(statementName, argList);
    }
}
