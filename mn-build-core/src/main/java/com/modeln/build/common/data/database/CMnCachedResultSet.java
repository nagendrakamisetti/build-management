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

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;

/**
 * Store ResultSet data in a cached (offline) object that does not maintain
 * a connection to the database. 
 *
 * @author  Shawn Stafford
 */
public class CMnCachedResultSet {

    private CMnCachedMetaData metaData;
    private Vector rows = new Vector();
    private int currentRow = 0;

    public CMnCachedResultSet(ResultSet rs) throws SQLException {
        metaData = new CMnCachedMetaData(rs.getMetaData());
        int columns = metaData.getColumnCount();
        Object value = null;
        while (rs.next()) {
            CMnCachedRowSet row = new CMnCachedRowSet();
            for (int idx = 1; idx <= columns; idx++) {
                row.add(rs.getString(idx));
            }
            rows.add(row);
        }

    }

    public CMnCachedMetaData getMetaData() {
        return metaData;
    }

    public boolean hasNext() {
        return (currentRow < rows.size());
    }

    public CMnCachedRowSet next() {
        CMnCachedRowSet current = (CMnCachedRowSet) rows.get(currentRow);
        currentRow++;
        return current;
    }

}
