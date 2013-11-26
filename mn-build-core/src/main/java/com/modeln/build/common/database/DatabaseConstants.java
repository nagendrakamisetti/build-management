/*
 * DatabaseConstants.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.common.database;

import java.util.Hashtable;
import java.sql.*;

/**
 * The UserDataParser loads user data from the database scheme
 * by mapping database fields to the correct data members.
 *
 * @version            $Revision: 1.1.1.1 $
 * @author             Shawn Stafford
 *
 */
public class DatabaseConstants {

    /**
     * Construct the constants object.
     */
    private DatabaseConstants() {
    }

    /**
     * Returns a hashtable containing the column name as the key
     * and the contents of the column as the data.
     * 
     * @param   rs      The result set returned by a query
     * @throws  SQLException if an error occurs while parsing the result set
     */
    public static Hashtable getValues(ResultSet rs) 
        throws SQLException
    {
        Hashtable values = new Hashtable();

        ResultSetMetaData md = rs.getMetaData();
        int colCount = md.getColumnCount();
        for (int col = 1; col <= colCount; col++) {
            values.put(md.getColumnName(col), rs.getObject(col));
        }

        return values;
    }

    /**
     * Returns a hashtable containing the column name as the key
     * and the type name as the data value.
     */
    public static Hashtable getColumnTypes(ResultSet rs) 
        throws SQLException
    {
        Hashtable values = new Hashtable();

        ResultSetMetaData md = rs.getMetaData();
        int colCount = md.getColumnCount();
        for (int col = 1; col <= colCount; col++) {
            values.put(md.getColumnName(col), md.getColumnTypeName(col));
        }

        return values;
    }
}
