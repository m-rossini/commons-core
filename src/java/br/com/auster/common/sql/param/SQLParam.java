package br.com.auster.common.sql.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * This class represents a parameter for a SQL prepared statement.
 */
public class SQLParam {
    /**
     * Sets the i-th parameter of the prepared statement with the value.
     * @param ps the prepared statement.
     * @param i the position of the parameter in the prepared statement.
     * @param value the value of the parameter, which is an object compatible to the i-th argument of the <code>ps</code>.
     * @return itself.
     * @exception SQLException if an error ocurrs while setting the parameter in the prepared statement.
     * @exception IllegalArgumentException if the argument could not be parsed to the type expected to this SQL parameter.
     */
    public SQLParam setParam(PreparedStatement ps, int i, Object value)
        throws SQLException, IllegalArgumentException
    {
        ps.setObject(i, value);
        return this;
    }
}
