/**
 * 
 */
package br.com.auster.common.output.param;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import br.com.auster.common.util.OutputParamException;

/**
 * @author jaraujo
 * 
 */
public class OutputParam {

	private Long objID;
	private String name;
	private String description;
	private String shortDescription;
	private String moduleName;
	private Connection connection;
	
	/** default constructor */
	public OutputParam() {
	}

	public OutputParam(Connection jdbcConnection) throws OutputParamException {

		try {
			if (jdbcConnection.isClosed())
				throw new OutputParamException("Falha na Conexão");

		} catch (SQLException sqlEx) {
			throw new OutputParamException(sqlEx.getMessage());
		}

		this.connection = jdbcConnection;
	}
	
	public List<OutputParam> getAllParam() throws OutputParamException{
		
		OutputDAO outputDao = new OutputDAO();
		
		return outputDao.findByAll(this.connection);		
		
	}
	
	public List<OutputParam> getParamID(Long objID2) throws OutputParamException{
		
		OutputDAO outputDao = new OutputDAO();
		
		return outputDao.findByID(this.connection, objID2);		
		
	}
	
	public void updateParam(OutputParam outPut) throws OutputParamException{
		
		OutputDAO outputDao = new OutputDAO();		
		
		outputDao.update(this.connection, outPut);
	}

	/**
	 * @return the objID
	 */
	public Long getObjID() {
		return objID;
	}

	/**
	 * @param objID
	 *            the objID to set
	 */
	public void setObjID(Long objID) {
		this.objID = objID;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the moduleName
	 */
	public String getModuleName() {
		return moduleName;
	}

	/**
	 * @param moduleName
	 *            the moduleName to set
	 */
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	/**
	 * @return the connection
	 */
	public Connection getConnection() {
		return connection;
	}

	/**
	 * @param connection
	 *            the connection to set
	 */
	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	/**
	 * @return the shortDescription
	 */
	public String getShortDescription() {
		return shortDescription;
	}

	/**
	 * @param shortDescription the shortDescription to set
	 */
	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

}
