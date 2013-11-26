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

import java.util.Vector;

/**
 * Store ResultSet row data in a cached (offline) object that does not maintain
 * a connection to the database. 
 *
 * @author  Shawn Stafford
 */
public class CMnCachedRowSet {

    /** Column data for the current row */
    private Vector columns = new Vector();

    /**
     * Construct an object representing a row of data.
     */
    public CMnCachedRowSet() {
    }

    /**
     * Add a column of data to the current row.
     *
     * @param  data   Data to be added to the row
     */
    public void add(String data) {
        columns.add(data);
    }

    /**
     * Return the data object corresponding to the specified column number.
     * Column numbering begins at zero.
     *
     * @param    idx   Column number
     * @return   Data found at the specified column number
     */
    public String get(int idx) {
        return (String) columns.get(idx);
    }

}
