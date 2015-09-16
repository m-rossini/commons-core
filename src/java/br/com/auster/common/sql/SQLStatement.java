package br.com.auster.common.sql;

import gnu.trove.TIntObjectHashMap;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import br.com.auster.common.sql.param.SQLDoubleParam;
import br.com.auster.common.sql.param.SQLLongParam;
import br.com.auster.common.sql.param.SQLParam;
import br.com.auster.common.sql.param.SQLTimestampParam;
import br.com.auster.common.util.I18n;
import br.com.auster.common.xml.DOMUtils;

/**
 * This class represents a prepared statement and its possible parameters.
 */
public class SQLStatement {
	// The statement XML configuration
	public static final String STATEMENT_TAG = "statement";
	public static final String STATEMENTS_CONFIG_ELEMENT = "statements";
	protected static final String QUERY_TAG = "query";
	protected static final String PARAM_TAG = "param";
	protected static final String INDEX_ATT = "index";
	protected static final String TYPE_ATT = "type";
	protected static final String FORMAT_ATT = "format";

	// Static attributes
	protected static final Map statementsByFile = Collections
			.synchronizedMap(new WeakHashMap());

	// Instance attributes
	protected final String query, name;
	protected TIntObjectHashMap params;
	protected boolean generateFile;

	private final I18n i18n = I18n.getInstance(SQLStatement.class);
	protected final Logger log = Logger.getLogger(this.getClass());

	protected SQLStatement(Element element) throws SAXException {

		this(DOMUtils.getAttribute(element, "name", true), DOMUtils.getText(
				DOMUtils
						.getElement(element,
								SQLConnectionManager.SQL_NAMESPACE_URI,
								QUERY_TAG, true)).toString(), null, DOMUtils
				.getBooleanAttribute(element, "generateFile", false));

		this.params = this.getStatementParams(element);
	}

	protected SQLStatement(String name, String query, TIntObjectHashMap params) {
		this(name, query, params, false);
	}

	protected SQLStatement(String name, String query, TIntObjectHashMap params,
			boolean generateFile) {
		this.name = name;
		this.query = query;
		if (params != null) {
			this.params = params;
		} else {
			this.params = new TIntObjectHashMap();
		}
		this.generateFile = generateFile;
	}

	/**
	 * Gets the parameters for a statement from the document tree representing
	 * the statement.
	 * 
	 * @param root
	 *            the element that represents the root of a statement.
	 * @see #parseStatements(Element)
	 */
	protected TIntObjectHashMap getStatementParams(Element root)
			throws SAXException {
		final TIntObjectHashMap params = new TIntObjectHashMap();
		final NodeList list = DOMUtils.getElements(root,
				SQLConnectionManager.SQL_NAMESPACE_URI, PARAM_TAG);
		int max = 0;
		for (int i = 0, size = list.getLength(); i < size; i++) {
			Element element = (Element) list.item(i);
			int index;
			try {
				index = Integer.parseInt(element.getAttribute(INDEX_ATT));
			} catch (NumberFormatException e) {
				throw new SAXException(i18n.getString("invalidParamValue",
						INDEX_ATT, this.name));
			}

			// Makes some math to check consistency after all the operation
			if (index > max) {
				max = index;
			}

			// Checks if there is 2 or more equal indexes
			if (params.containsKey(index)) {
				throw new SAXException(i18n.getString("twoParamSameIndex",
						this.name));
			}

			// Creates the appropriate parameter type
			final String type = element.getAttribute(TYPE_ATT);
			if (type == null || type.length() == 0) {
				throw new SAXException(i18n.getString("missingParam", TYPE_ATT,
						this.name));
			}

			final String format = element.getAttribute(FORMAT_ATT);

			final SQLParam param;
			if (type.equalsIgnoreCase("Integer")
					|| type.equalsIgnoreCase("Long")) {
				param = new SQLLongParam();
			} else if (type.equalsIgnoreCase("Float")
					|| type.equalsIgnoreCase("Double")) {
				param = new SQLDoubleParam();
			} else if (type.equalsIgnoreCase("Date")
					|| type.equalsIgnoreCase("Timestamp")) {
				param = new SQLTimestampParam(format);
			} else if (type.equalsIgnoreCase("String")
					|| type.equalsIgnoreCase("Object")) {
				param = new SQLParam();
			} else {
				throw new SAXException(i18n.getString("paramWrongType",
						new Integer(index), type, this.name));
			}

			params.put(index, param);
		}

		// Checks if there it's missing some parameter
		if (params.size() != max) {
			throw new SAXException(i18n
					.getString("missingParamStat", this.name));
		}

		return params;
	}

	public String getStatementText() {
		return this.query;
	}

	/**
	 * Using the configuration for this statement, creates a SQL prepared
	 * statement, with all its parameters set, ready to be run.
	 * 
	 * @param connection
	 *            the connection that the statement will be run.
	 * @param psParams
	 *            the parameters to be used as the statement parameters. Null if
	 *            no parameters.
	 * @return A new prepared statement for the connection given.
	 * @exception IllegalArgumentException
	 *                if some of the <code>params</code> are not compatible
	 *                with this statement.
	 */
	public final PreparedStatement prepareStatement(Connection connection,
			Object[] psParams) throws SQLException, IllegalArgumentException {
		PreparedStatement ps = connection.prepareStatement(this.query);

		// checks if the parameters are enough for this statement
		if ((psParams == null && this.params.size() > 0)
				|| (psParams != null && (psParams.length != this.params.size()))) {
			throw new IllegalArgumentException(i18n.getString("wrongNumParam",
					this.name, new Integer(this.params.size()), new Integer(
							psParams == null ? 0 : psParams.length)));
		} else if (psParams != null) {
			// Sets the statement parameters values, if any, depending on its
			// type
			for (int i = 1, size = this.params.size(); i <= size; i++) {
				((SQLParam) this.params.get(i))
						.setParam(ps, i, psParams[i - 1]);
			}
		}
		return ps;
	}

	/**
	 * Using the configuration for this statement, executes a query to the SQL
	 * server.
	 * 
	 * @param connection
	 *            the connection to use for this query.
	 * @param psParams
	 *            the parameters to be used as the statement parameters. Null if
	 *            no parameters.
	 * @return A result set with the rows returned by the query.
	 * @exception IllegalArgumentException
	 *                if some of the <code>params</code> are not compatible
	 *                with this statement.
	 * @deprecated All executeXXX methods are unsafe since they don't close the
	 *             statemente after use, besides it doesn't reuse prepared
	 *             statements.
	 */
	public final ResultSet executeQuery(Connection connection, Object[] psParams)
			throws SQLException, IllegalArgumentException {
		try {
			if (psParams == null || psParams.length == 0) {
				return connection.createStatement().executeQuery(this.query);
			}

			return this.prepareStatement(connection, psParams).executeQuery();
		} catch (SQLException e) {
			log.error(i18n.getString("sqlError", this.name, psParams), e);
			throw e;
		}
	}

	/**
	 * Using the configuration for this statement, executes a query to the SQL
	 * server.
	 * 
	 * @param connection
	 *            the connection to use for this query.
	 * @param psParams
	 *            the parameters to be used as the statement parameters. Null if
	 *            no parameters.
	 * @return true if OK, false otherwise.
	 * @exception IllegalArgumentException
	 *                if some of the <code>params</code> are not compatible
	 *                with this statement.
	 * @deprecated All executeXXX methods are unsafe since they don't close the
	 *             statemente after use, besides it doesn't reuse prepared
	 *             statements.
	 */
	public final boolean execute(Connection connection, Object[] psParams)
			throws SQLException, IllegalArgumentException {
		try {
			if (psParams == null || psParams.length == 0) {
				return connection.createStatement().execute(this.query);
			}

			return this.prepareStatement(connection, psParams).execute();
		} catch (SQLException e) {
			log.error(i18n.getString("sqlError", this.name, psParams), e);
			throw e;
		}
	}

	/**
	 * Using the configuration for this statement, executes an update on the SQL
	 * server.
	 * 
	 * @param connection
	 *            the connection to use for this query.
	 * @param psParams
	 *            the parameters to be used as the statement parameters. Null if
	 *            no parameters.
	 * @return the number of rows affected.
	 * @exception IllegalArgumentException
	 *                if some of the <code>params</code> are not compatible
	 *                with this statement.
	 * @deprecated All executeXXX methods are unsafe since they don't close the
	 *             statemente after use, besides it doesn't reuse prepared
	 *             statements.
	 */
	public final int executeUpdate(Connection connection, Object[] psParams)
			throws SQLException, IllegalArgumentException {
		try {
			if (psParams == null || psParams.length == 0) {
				return connection.createStatement().executeUpdate(this.query);
			}

			return this.prepareStatement(connection, psParams).executeUpdate();
		} catch (SQLException e) {
			log.error(i18n.getString("sqlError", this.name, psParams), e);
			throw e;
		}
	}

	/**
	 * Parses a XML tree to get the SQL statements hashed by the element name.
	 * 
	 * The XML will be like this example:
	 * 
	 * <sql:statements xmlns:sql="http://www.auster.com.br/common/sql">
	 * <sql:statement name="first_name"> <sql:query> SELECT first_name FROM user
	 * WHERE last_name like ? last_update = ? </sql:query> <sql:param index="1"
	 * type="String"/> <sql:param index="2" type="Date" format="yyyyMMdd"/>
	 * </sql:statement> <sql:statement name="complete_name"> <sql:query> SELECT
	 * first_name, last_name FROM user </sql:query> </sql:statement>
	 * </sql:statements>
	 * 
	 * @param root
	 *            the root of the XML tree that contains the statements
	 *            definition.
	 * @return SQLStatement objects hashed by their names. An empty map if the
	 *         root is null.
	 * @throws IllegalArgumentException
	 *             if could not find the initial tag for sql statements
	 *             definition.
	 */
	public static Map parseStatements(Element root) throws SAXException,
			IOException, ParserConfigurationException {
		final Logger log = Logger.getLogger(SQLStatement.class);
		final I18n i18n = I18n.getInstance(SQLStatement.class);

		final Map statements = new HashMap();
		if (root == null) {
			return statements;
		}

		// Searchs for the statements tag from the root tree node
		final NodeList configList = DOMUtils.getElements(root,
				SQLConnectionManager.SQL_NAMESPACE_URI,
				STATEMENTS_CONFIG_ELEMENT);
		for (int i = 0, configSize = configList.getLength(); i < configSize; i++) {
			// For each statement element of the tree, gets the statement name
			// and the SQL query.
			Element config = (Element) configList.item(i);
			NodeList nodeList = DOMUtils.getElements(config,
					SQLConnectionManager.SQL_NAMESPACE_URI, STATEMENT_TAG);
			for (int j = 0, listSize = nodeList.getLength(); j < listSize; j++) {
				// Translate the statement from XML to objects
				Element element = (Element) nodeList.item(j);
				String statementName = DOMUtils.getAttribute(element, "name",
						true);

				statements.put(statementName, new SQLStatement(element));
				log.debug(i18n.getString("name", statementName));
			}
			log.info(i18n.getString("done"));

			// If found a reference to a external statements file, read it.
			String statementsFileName = config.getAttribute("path");
			if (statementsFileName.length() > 0) {
				statements
						.putAll(parseStatements(new File(statementsFileName)));
			}
		}
		return statements;
	}

	/**
	 * Parses a XML file to get the statements strings (the element data),
	 * hashed by the element name.
	 * 
	 * @param file
	 *            the XML file path to be processed.
	 * @return a hash of SQLStatement objects hashed by its elements names.
	 * @throws IOException
	 *             if occurs some error while opening or reading the file.
	 * @see #parseStatements(Element)
	 */
	public static Map parseStatements(File file) throws IOException,
			ParserConfigurationException, SAXException {
		final Logger log = Logger.getLogger(SQLStatement.class);
		final I18n i18n = I18n.getInstance(SQLStatement.class);

		String fileName = file.getCanonicalPath();
		if (!file.exists() || !file.isFile()) {
			throw new IOException(i18n.getString("invalidFileName", fileName));
		}

		Map statements = (Map) statementsByFile.get(fileName);
		if (statements == null) {
			log.info(i18n.getString("addStatFile", fileName));

			// Creates a DOM tree and gets the root element
			Element root = null;
			try {
				root = DOMUtils.openDocument(fileName, false);
			} catch (GeneralSecurityException e) {
				e.printStackTrace();
				throw new IOException(e.getMessage());
			}
			try {
				root = DOMUtils.getElement(root,
						SQLConnectionManager.SQL_NAMESPACE_URI,
						SQLConnectionManager.CONFIG_ELEMENT, true);
			} catch (IllegalArgumentException e) {
			}

			statements = parseStatements(root);
			statementsByFile.put(fileName, statements);
		} else {
			log.info(i18n.getString("fileAlreadyParsed", fileName));
		}
		return statements;
	}

	/**
	 * Return the value of a attribute<code>generateFile</code>.
	 * 
	 * @return return the value of <code>generateFile</code>.
	 */
	public boolean isGenerateFile() {
		return generateFile;
	}

	/**
	 * Set a value of <code>generateFile</code>.
	 * 
	 * @param generateFile
	 */
	public void setGenerateFile(boolean generateFile) {
		this.generateFile = generateFile;
	}
}
