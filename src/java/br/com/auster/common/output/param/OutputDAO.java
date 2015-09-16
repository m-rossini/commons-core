/**
 * 
 */
package br.com.auster.common.output.param;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.auster.common.util.OutputParamException;

/**
 * @author jaraujo
 * 
 */
public class OutputDAO {
	
	public List<OutputParam> findByAll(Connection connection) throws OutputParamException{
		
		ResultSet result = null;
		PreparedStatement sqlStm = null;
		OutputParam outputParam = null;
		List<OutputParam> listParam = new ArrayList<OutputParam>();
		try{
			
			if (connection.isClosed()){
				throw new OutputParamException("Falha na Conexão");
			}
			
			StringBuffer findSql = new StringBuffer();
			findSql.append("SELECT * FROM AST_PARAM_CONFIG");
			sqlStm = connection.prepareStatement(findSql.toString());
			result = sqlStm.executeQuery();

			while (result.next()) {
				outputParam = new OutputParam();
				outputParam.setObjID((result.getLong("OBJID")));
				outputParam.setName((result.getString("VALUE")));
				outputParam.setModuleName((result.getString("MODULE_NAME")));
				outputParam.setDescription((result.getString("DESCRIPTION")));
				outputParam.setShortDescription((result.getString("SHORT_DESCRIPTION")));
				listParam.add(outputParam);
			}
			
			result.close();
			sqlStm.close();

		} catch (SQLException sqlEx) {
			throw new OutputParamException(sqlEx.getMessage());
		}
		if(listParam != null && listParam.size() > 0){
			return listParam;
		}else{
			return null;
		}
	}
	
	public List<OutputParam> findByID(Connection connection, Long id) throws OutputParamException{
		
		ResultSet result = null;
		PreparedStatement sqlStm = null;
		OutputParam outputParam = null;
		List<OutputParam> listParam = new ArrayList<OutputParam>();
		try{
			
			if (connection.isClosed()){
				throw new OutputParamException("Falha na Conexão");
			}
			
			StringBuffer findSql = new StringBuffer();
			findSql.append("SELECT * FROM AST_PARAM_CONFIG WHERE OBJID = ?");
			sqlStm = connection.prepareStatement(findSql.toString());
			sqlStm.setLong(1, id);
			result = sqlStm.executeQuery();

			while (result.next()) {
				outputParam = new OutputParam();
				outputParam.setObjID((result.getLong("OBJID")));
				outputParam.setName((result.getString("VALUE")));
				outputParam.setModuleName((result.getString("MODULE_NAME")));
				outputParam.setDescription((result.getString("DESCRIPTION")));
				outputParam.setShortDescription((result.getString("SHORT_DESCRIPTION")));
				listParam.add(outputParam);
			}
			
			result.close();
			sqlStm.close();

		} catch (SQLException sqlEx) {
			throw new OutputParamException(sqlEx.getMessage());
		}
		if(listParam != null && listParam.size() > 0){
			return listParam;
		}else{
			return null;
		}
	}
	
	public void update(Connection connection, OutputParam outPut) throws OutputParamException{	
	
		PreparedStatement sqlStm = null;
		
		try{
			
			if (connection.isClosed()){
				throw new OutputParamException("Falha na Conexão");
			}
			
			StringBuffer updateSql = new StringBuffer();
			updateSql.append(" UPDATE AST_PARAM_CONFIG ");
			updateSql.append(" SET VALUE = ?,");
			updateSql.append(" MODULE_NAME = ?,");
			updateSql.append(" DESCRIPTION = ?,");
			updateSql.append(" SHORT_DESCRIPTION = ?");
			updateSql.append(" WHERE OBJID = ? ");
			
			sqlStm = connection.prepareStatement(updateSql.toString());

			sqlStm.setString(1, outPut.getName());
			sqlStm.setString(2, outPut.getModuleName());
			sqlStm.setString(3, outPut.getDescription());
			sqlStm.setString(4, outPut.getShortDescription());
			sqlStm.setLong(5, outPut.getObjID());
			sqlStm.executeUpdate();
			sqlStm.close();

		} catch (SQLException sqlEx) {
			throw new OutputParamException(sqlEx.getMessage());
		}
	}
	
}
