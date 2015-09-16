package br.com.auster.common.sql.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * This class represents a parameter for a SQL prepared statement.
 */
public class SQLLongParam extends SQLParam {

    /**
     * Just set the value as a long at the i-th parameter of the prepared statement.
     */
    public SQLParam setParam(PreparedStatement ps, int i, Object value) 
        throws SQLException, IllegalArgumentException
    {
        if (value instanceof String)
            ps.setLong(i, Long.parseLong(value.toString()));
        else
            super.setParam(ps, i, value);

        return this;
    }
}
