package br.com.auster.common.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.NoInitialContextException;
import javax.sql.DataSource;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import br.com.auster.common.util.I18n;
import br.com.auster.common.xml.DOMUtils;

/**
 * This class is used to encapsulate the usage of a JNDI based SQL connection
 * pool. It contains methods that are very usefull for those who don't want to
 * care about getting and giving SQL connections.
 * 
 * It also contains methods to parse and get SQL statements from a XML
 * configuration file. This is used to remove them from the Java code and to
 * group them all in a single place.
 */
public class SQLConnectionManager {
	public static final String SQL_NAMESPACE_URI = "http://www.auster.com.br/common/sql/";
	public static final String CONFIG_ELEMENT = "configuration";
	public static final String DB_ELEMENT = "database";
	public static final String NAME_ATTR = "name";

	/**
	 * {@value} - must be in the classpath to be found
	 */
	public static final String CFG_RESOURCE_ATTR = "config-resource";

	// The static attributes
	protected static final Map managerByPool = new Hashtable();
	protected static final Map globalStatements = new Hashtable();

	// The instance attributes
	protected final Logger log = Logger.getLogger(SQLConnectionManager.class);
	private final I18n i18n = I18n.getInstance(SQLConnectionManager.class);
	protected DataSource ds;
	private final String poolName, url;
	protected final Map statements;

	/**
	 * This method is used to intialize Apache's DBCP SQL Connection Pool.
	 * Calling it is needed if it is wanted to use the
	 * <code>SQLConnectionManager</code> outside J2EE application servers.
	 */
	public static synchronized void init(Element root) throws Exception {
		Logger log = Logger.getLogger(SQLConnectionManager.class);
		log.debug("Loading SQL Connection Manager configuration.");

		managerByPool.clear();
		globalStatements.clear();

		Element cfgElt = DOMUtils.getElement(root, SQL_NAMESPACE_URI,
				CONFIG_ELEMENT, true);
		globalStatements.putAll(SQLStatement.parseStatements(cfgElt));

		NodeList dbElts = DOMUtils.getElements(cfgElt, SQL_NAMESPACE_URI,
				DB_ELEMENT);
		for (int i = 0; i < dbElts.getLength(); i++) {
			Element dbElt = (Element) dbElts.item(i);
			String name = DOMUtils.getAttribute(dbElt, NAME_ATTR, true);

			String url = "jdbc:apache:commons:dbcp:/";
			String resourceName = DOMUtils.getAttribute(dbElt,
					CFG_RESOURCE_ATTR, false);
			if (resourceName != null && resourceName.length() > 0) {
				url += resourceName;
			} else {
				url += name;
			}

			final Map stmts = SQLStatement.parseStatements(dbElt);
			SQLConnectionManager manager = new SQLConnectionManager(name, url,
					stmts);

			// test the configuration
			manager.getConnection().close();

			managerByPool.put(name, manager);
		}
	}

	/**
	 * Creates a <code>SQLConnectionManager</code> for a given pool name. This
	 * name is the JNDI name that contains a DataSource for this pool.
	 * 
	 * @param poolName
	 *            the JNDI name for the connection pool.
	 */
	protected SQLConnectionManager(String poolName, String url, Map statements)
			throws NamingException {
		this.poolName = poolName;
		this.statements = statements;
		this.url = url;
		try {
			// Try to use JNDI, for J2EE compatibility
			Context ctx = new InitialContext();
			this.ds = (DataSource) ctx.lookup(poolName);
		} catch (NoInitialContextException e) {
			log.warn(i18n.getString("noInitialContext"));
			log.debug(e);
			// If could not use the application server pool,
			// use DBCP PoolingDriver.
		}

	}

	/**
	 * This method is used to get a <code>SQLConnectionManager</code>
	 * instance. This instance is unique for a given pool name, and it is shared
	 * by other threads and objects to get connections and run queries.
	 * 
	 * @throws NamingException
	 *             if cound not find the pool informed.
	 */
	public static final SQLConnectionManager getInstance(String poolName)
			throws NamingException {
		return (SQLConnectionManager) managerByPool.get(poolName);
	}

	/***************************************************************************
	 * Method getAllManagedPools() was created due to ticket #20 in common-base
	 * project. This method, returns a Set with all names of managed pools. In
	 * the case SQLConnection Manger has not being initialized, the pool will be
	 * null, BUT this method will return and Empty Set.
	 */
	public static Set getAllManagedPools() {
		return (SQLConnectionManager.managerByPool == null) ? Collections
				.emptySet() : SQLConnectionManager.managerByPool.keySet();
	}

	/** ******************************************************** */
	/* METHODS TO GET SQL RESULTS FROM A PRE-DEFINED STATEMENT */
	/** ******************************************************** */

	/**
	 * Returns a SQL connection from the pool.
	 * 
	 * @return a SQL connection from the pool.
	 * @throws SQLException
	 *             if an error ocurred while talking to the SQL server.
	 */
	public final Connection getConnection() throws SQLException {
		return this.ds != null ? this.ds.getConnection() : DriverManager
				.getConnection(this.url);
	}

	/**
	 * Runs the query named 'statementName', using the parameters passed as
	 * <code>args</code>, and returns all the result as a child of
	 * <code>root</code>.
	 * 
	 * @param statementName
	 *            the name of the SQLStatement to be used for this query (they
	 *            are defined in the statements XML file).
	 * @param root
	 *            the node that will be the root for the entire node tree
	 *            created. If <code>null</code> a new one will be created.
	 * @param args
	 *            the arguments to be set in the prepared statement.
	 * @return the same node passed as argument, if it's not <code>null</code>.
	 *         Otherwise a new one is returned. Bellow is a sample of the tree
	 *         set created as the result.
	 * 
	 * <pre>
	 *         &lt;node&gt;
	 *             &lt;record fieldName1=&quot;value1&quot; fieldName2=&quot;value2&quot; ... /&gt;
	 *             ...
	 *         &lt;/node&gt;
	 * </pre>
	 * 
	 * @throws SQLException
	 *             if an error ocurred while talking to the SQL server.
	 * @throws IllegalArgumentException
	 *             if the statement does not exist, or some of the parameters
	 *             are wrong.
	 */
	public final Node queryDOM(String statementName, Node root, Object[] args)
			throws SQLException, ParserConfigurationException {
		SQLStatement ss = this.getStatement(statementName);
		final Connection con = getConnection();
		PreparedStatement stmt = null;
		try {
			stmt = ss.prepareStatement(con, args);
			return DOMUtils.resultSet2NodeSet(stmt.executeQuery(), root);
		} finally {
			if (stmt != null)
				stmt.close();
			if (con != null)
				con.close();
		}
	}

	/**
	 * Runs the query named 'statementName', using the parameters passed as
	 * <code>args</code>, and returns all the result as SAX events.
	 * 
	 * @param statementName
	 *            the name of the SQLStatement to be used for this query (they
	 *            are defined in the statements XML file).
	 * @param handler
	 *            the content handler that will be used to receive the SAX
	 *            events.
	 * @param atts
	 *            the root element attributes to be shown. May be null.
	 * @param args
	 *            the arguments to be set in the prepared statement.
	 * @throws SQLException
	 *             if an error ocurred while talking to the SQL server.
	 * @throws IllegalArgumentException
	 *             if the statement does not exist, or some of the parameters
	 *             are wrong.
	 */
	public final void querySAX(String statementName, ContentHandler handler,
			Attributes atts, Object[] args) throws SQLException, SAXException {
		SQLStatement ss = this.getStatement(statementName);
		final Connection con = getConnection();
		PreparedStatement stmt = null;
		try {
			stmt = ss.prepareStatement(con, args);
			DOMUtils.resultSet2ContentHandler(stmt.executeQuery(), handler,
					atts);
		} finally {
			if (stmt != null)
				stmt.close();
			if (con != null)
				con.close();
		}
	}

	/**
	 * Runs the query named 'statementName', using the parameters passed as
	 * <code>args</code>, and returns all the result in a list.
	 * 
	 * @param statementName
	 *            the name of the SQLStatement to be used for this query (they
	 *            are defined in the statements XML file).
	 * @param args
	 *            the arguments to be set in the prepared statement.
	 * @return a list of lists, representing a matrix of the results.
	 * @throws SQLException
	 *             if an error ocurred while talking to the SQL server.
	 * @throws IllegalArgumentException
	 *             if the statement does not exist, or some of the parameters
	 *             are wrong.
	 */
	public final List queryList(String statementName, Object[] args)
			throws SQLException {
		SQLStatement ss = this.getStatement(statementName);
		final Connection con = getConnection();
		PreparedStatement stmt = null;
		try {
			stmt = ss.prepareStatement(con, args);
			return resultSet2List(stmt.executeQuery());
		} finally {
			if (stmt != null)
				stmt.close();
			if (con != null)
				con.close();
		}
	}

	/**
	 * Runs the query 'statementName' and returns the values for the field
	 * 'fieldName'.
	 * 
	 * @param statementName
	 *            the statement to use as the query.
	 * @param fieldName
	 *            the name of the field to be returned.
	 * @param args
	 *            the arguments to be set in the prepared statement.
	 * @return the values for the field <code>fieldName</code>.
	 * @throws SQLException
	 *             if an error ocurred while talking to the SQL server.
	 * @throws IllegalArgumentException
	 *             if the statement does not exist, or some of the parameters
	 *             are wrong.
	 */
	public final List queryField(String statementName, String fieldName,
			Object[] args) throws SQLException {
		SQLStatement ss = this.getStatement(statementName);
		final Connection con = getConnection();
		PreparedStatement stmt = null;
		try {
			stmt = ss.prepareStatement(con, args);
			return resultSet2List(stmt.executeQuery(), fieldName);
		} finally {
			if (stmt != null)
				stmt.close();
			if (con != null)
				con.close();
		}
	}

	/**
	 * Runs the query 'statementName' and returns the values for the i-th
	 * column.
	 * 
	 * @param statementName
	 *            the statement to use as the query.
	 * @param i
	 *            the position of the column to be returned.
	 * @param args
	 *            the arguments to be set in the prepared statement.
	 * @return the values for the i-th column.
	 * @throws SQLException
	 *             if an error ocurred while talking to the SQL server.
	 * @throws IllegalArgumentException
	 *             if the statement does not exist, or some of the parameters
	 *             are wrong.
	 */
	public final List queryField(String statementName, int i, Object[] args)
			throws SQLException {
		SQLStatement ss = this.getStatement(statementName);
		final Connection con = getConnection();
		PreparedStatement stmt = null;
		try {
			stmt = ss.prepareStatement(con, args);
			return resultSet2List(stmt.executeQuery(), i);
		} finally {
			if (stmt != null)
				stmt.close();
			if (con != null)
				con.close();
		}
	}

	/**
	 * Creates a statement named 'statementName', sets their parameters and
	 * executes it.
	 * 
	 * @param statementName
	 *            the statement name defined in the XML configuration..
	 * @param params
	 *            the parameters to be set in the prepared statement named
	 *            'statementName'. Each position of this array correspond to
	 *            each parameter position of the statement.
	 * @return true if the execution was OK, false otherwise.
	 * @throws IllegalArgumentException
	 *             if the statement does not exist, or some of the parameters
	 *             are wrong.
	 */
	public final boolean execute(String statementName, Object[] params)
			throws SQLException {
		SQLStatement ss = this.getStatement(statementName);
		final Connection con = getConnection();
		PreparedStatement stmt = null;
		try {
			stmt = ss.prepareStatement(con, params);
			return stmt.execute();
		} finally {
			if (stmt != null)
				stmt.close();
			if (con != null)
				con.close();
		}
	}

	/**
	 * Creates a statement named 'statementName', sets their parameters and
	 * executes it.
	 * 
	 * @param statementName
	 *            the statement name defined in the XML configuration..
	 * @param params
	 *            the parameters to be set in the prepared statement named
	 *            'statementName'. Each position of this array correspond to
	 *            each parameter position of the statement.
	 * @return the number of rows affected by this execution.
	 * @throws IllegalArgumentException
	 *             if the statement does not exist, or some of the parameters
	 *             are wrong.
	 */
	public final int executeUpdate(String statementName, Object[] params)
			throws SQLException {
		SQLStatement ss = this.getStatement(statementName);
		final Connection con = getConnection();
		PreparedStatement stmt = null;
		try {
			stmt = ss.prepareStatement(con, params);
			return stmt.executeUpdate();
		} finally {
			if (stmt != null)
				stmt.close();
			if (con != null)
				con.close();
		}
	}

	/**
	 * Looks for the SQL statement named 'statementName' and returns it.
	 * 
	 * @param statementName
	 *            the statement name defined in the XML configuration to return.
	 * @return the SQL Statement object named 'statementName'.
	 * @throws IllegalArgumentException
	 *             if the statement does not exist.
	 */
	public final SQLStatement getStatement(String statementName) {
		SQLStatement statement = (SQLStatement) this.statements
				.get(statementName);
		if (statement == null) {
			statement = (SQLStatement) globalStatements.get(statementName);
			if (statement == null) {
				throw new IllegalArgumentException(i18n.getString(
						"couldntFindStat", statementName));
			}
		}
		return statement;
	}

	/** ********************************************************* */
	/* METHODS TO GET SQL RESULTS FROM A GIVEN SQL STRING QUERY */
	/** ********************************************************* */

	/**
	 * Runs the query and returns all the result as a child of <code>root</code>.
	 * 
	 * @param statementName
	 *            the name of the SQLStatement to be used for this query (they
	 *            are defined in the statements XML file).
	 * @param root
	 *            the node that will be the root for the entire node tree
	 *            created. If <code>null</code> a new one will be created.
	 * @return the same node passed as argument, if it's not <code>null</code>.
	 *         Otherwise a new one is returned. Bellow is a sample of the tree
	 *         set created as the result.
	 * 
	 * <pre>
	 *         &lt;result&gt;
	 *             &lt;record fieldName1=&quot;value1&quot; fieldName2=&quot;value2&quot; ... /&gt;
	 *             ...
	 *         &lt;/result&gt;
	 * </pre>
	 * 
	 * @exception SQLException
	 *                if an error ocurred while talking to the SQL server.
	 */
	public final Node queryDOM(String query, Node root) throws SQLException,
			ParserConfigurationException {
		final Connection con = getConnection();
		Statement stmt = null;
		try {
			stmt = con.createStatement();
			return DOMUtils.resultSet2NodeSet(stmt.executeQuery(query), root);
		} finally {
			if (stmt != null)
				stmt.close();
			if (con != null)
				con.close();
		}
	}

	/**
	 * Runs the query and returns all the result as SAX events.
	 * 
	 * @param statementName
	 *            the name of the SQLStatement to be used for this query (they
	 *            are defined in the statements XML file).
	 * @param handler
	 *            the content handler that will be used to receive the SAX
	 *            events.
	 * @param atts
	 *            the root element attributes to be shown. May be null.
	 * @exception SQLException
	 *                if an error ocurred while talking to the SQL server.
	 */
	public final void querySAX(String query, ContentHandler handler,
			Attributes atts) throws SQLException, SAXException {
		final Connection con = getConnection();
		Statement stmt = null;
		try {
			stmt = con.createStatement();
			DOMUtils.resultSet2ContentHandler(stmt.executeQuery(query),
					handler, atts);
		} finally {
			if (stmt != null)
				stmt.close();
			if (con != null)
				con.close();
		}
	}

	/**
	 * Runs the query and returns all the result as a list.
	 * 
	 * @param statementName
	 *            the name of the SQLStatement to be used for this query (they
	 *            are defined in the statements XML file).
	 * @param handler
	 *            the content handler that will be used to receive the SAX
	 *            events.
	 * @return a list of lists, representing a matrix of the results.
	 * @exception SQLException
	 *                if an error ocurred while talking to the SQL server.
	 */
	public final List queryList(String query) throws SQLException {
		final Connection con = getConnection();
		Statement stmt = null;
		try {
			stmt = con.createStatement();
			return resultSet2List(stmt.executeQuery(query));
		} finally {
			if (stmt != null)
				stmt.close();
			if (con != null)
				con.close();
		}
	}

	/**
	 * Runs the query and returns the values for the field 'fieldName'.
	 * 
	 * @param query
	 *            the query to be executed.
	 * @param fieldName
	 *            the name of the field to be returned.
	 * @return the values for the field <code>fieldName</code>.
	 * @exception SQLException
	 *                if an error ocurred while talking to the SQL server.
	 */
	public final List queryField(String query, String fieldName)
			throws SQLException {
		final Connection con = getConnection();
		Statement stmt = null;
		try {
			stmt = con.createStatement();
			return resultSet2List(stmt.executeQuery(query), fieldName);
		} finally {
			if (stmt != null)
				stmt.close();
			if (con != null)
				con.close();
		}
	}

	/**
	 * Runs the query and returns the values for the i-th column.
	 * 
	 * @param query
	 *            the query to be executed.
	 * @param i
	 *            the position of the column to be returned.
	 * @return the values for the i-th column.
	 * @exception SQLException
	 *                if an error ocurred while talking to the SQL server.
	 */
	public final List queryField(String query, int i) throws SQLException {
		final Connection con = getConnection();
		Statement stmt = null;
		try {
			stmt = con.createStatement();
			return resultSet2List(stmt.executeQuery(query), i);
		} finally {
			if (stmt != null)
				stmt.close();
			if (con != null)
				con.close();
		}
	}

	/**
	 * Executes a SQL command.
	 * 
	 * @return true if OK, false on error.
	 */
	public final boolean execute(String strSQL) throws SQLException {
		final Connection con = getConnection();
		Statement stmt = null;
		try {
			stmt = con.createStatement();
			return stmt.execute(strSQL);
		} finally {
			if (stmt != null)
				stmt.close();
			if (con != null)
				con.close();
		}
	}

	/**
	 * Executes the SQL Update command.
	 * 
	 * @return the number of lines changed.
	 */
	public final int executeUpdate(String strSQL) throws SQLException {
		final Connection con = getConnection();
		Statement stmt = null;
		try {
			stmt = con.createStatement();
			return stmt.executeUpdate(strSQL);
		} finally {
			if (stmt != null)
				stmt.close();
			if (con != null)
				con.close();
		}
	}

	/**
	 * Creates a list of results, where each result will be a list, containing
	 * the result object for each column returned.
	 * 
	 * @param rs
	 *            the result set.
	 * @return a list of lists, that corresponds to a matrix of the results.
	 *         Each element of the list is a row. Each row is a list, where each
	 *         element is an object returned by the method
	 *         <code>ResultSet:getObject(i)</code>.
	 */
	public static final List resultSet2List(ResultSet rs) throws SQLException {
		// Tries to discover the name of each column
		final ResultSetMetaData metaData = rs.getMetaData();
		final List results = new ArrayList();

		// Puts each line in the result
		while (rs.next()) {
			final List row = new ArrayList();
			results.add(row);

			// For each field, creates a entry with the column name and its
			// value
			for (int i = 1, size = metaData.getColumnCount(); i <= size; i++) {
				row.add(rs.getObject(i));
			}
		}
		return results;
	}

	/**
	 * Given a result set, return a list containing all the objects returned for
	 * the field <code>fieldName</code> from all the records returned.
	 * 
	 * @param rs
	 *            the result set.
	 * @param fieldName
	 *            the name of the field to be returned in the list.
	 * @return a list of the values for the given field name.
	 */
	public static final List resultSet2List(ResultSet rs, String fieldName)
			throws SQLException {
		final List list = new ArrayList();
		while (rs.next()) {
			list.add(rs.getObject(fieldName));
		}
		return list;
	}

	/**
	 * Given a result set, return a list containing all the objects returned for
	 * the i-th field from all the records returned.
	 * 
	 * @param rs
	 *            the result set.
	 * @param i
	 *            the position of the field on the result set to be returned.
	 * @return a list of the values for the given field position.
	 */
	public static final List resultSet2List(ResultSet rs, int i)
			throws SQLException {
		final List list = new ArrayList();
		while (rs.next()) {
			list.add(rs.getObject(i));
		}
		return list;
	}
}
