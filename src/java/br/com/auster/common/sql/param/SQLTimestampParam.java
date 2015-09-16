package br.com.auster.common.sql.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * This class represents a parameter for a SQL prepared statement.
 */
public class SQLTimestampParam extends SQLParam {
    protected static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";

    protected SimpleDateFormat formatter;

    public SQLTimestampParam(String format) {
        if (format == null || format.length() == 0)
            format = DEFAULT_FORMAT;
        this.formatter = new SimpleDateFormat(format);
    }

    /**
     * Formats the value as a timestamp, using the format specified in the constructor.
     */
    protected synchronized SQLParam setParam(PreparedStatement ps, int i, String value) 
        throws SQLException, IllegalArgumentException
    {
        try {
            ps.setTimestamp(i, new Timestamp(this.formatter.parse(value).getTime()));
        } catch (ParseException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        return this;
    }

    /**
     * This method overrides the <code>setParam</code> from SQLParam. This one has
     * an implementation to direct map <code>java.util.Date</code> values to 
     * <code>java.sql.Timestamp</code> values.
     */
    public SQLParam setParam(PreparedStatement ps, int i, Object value) 
        throws SQLException
    {
        if (value instanceof java.util.Date) {
            final Timestamp date = new Timestamp(((java.util.Date) value).getTime());
            ps.setTimestamp(i, date);

        } else if (value instanceof String) {
            this.setParam(ps, i, value.toString());

        } else
            super.setParam(ps, i, value);

        return this;
    }
}
