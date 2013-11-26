/*
* Copyright 2000-2003 by Model N, Inc.  All Rights Reserved.
*
* This software is the confidential and proprietary information
* of Model N, Inc ("Confidential Information").  You shall not
* disclose such Confidential Information and shall use it only
* in accordance with the terms of the license agreement you
* entered into with Model N, Inc.
*/
package com.modeln.build.common.data.database;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;

/**
 * Store ResultSet Meta Data in a cached (offline) object that does not maintain
 * a connection to the database. 
 *
 * @author  Shawn Stafford
 */
public class CMnCachedMetaData {

    private Vector columnNames = new Vector();
    private int columnCount = 0;

    /**
     * Construct a meta data object by querying the database and storing the
     * values in an offline object.
     */
    public CMnCachedMetaData(ResultSetMetaData metadata) throws SQLException {
        columnCount = metadata.getColumnCount();
        for (int idx = 1; idx <= columnCount; idx++) {
            columnNames.add(metadata.getColumnName(idx));
        }

    }

    /**
     * Return the number of columns.
     *
     * @return Number of columns
     */
    public int getColumnCount() {
        return columnCount;
    }

    /**
     * Returns the name of the column as returned by the database metadata.
     * 
     * @param idx   Column index with numbers beginning at zero
     * @param Name of the column corresponding to the index number
     */
    public String getColumnName(int idx) {
        return (String) columnNames.get(idx);
    }

}
