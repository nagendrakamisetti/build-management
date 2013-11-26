/*
* Copyright 2000-2003 by Model N, Inc.  All Rights Reserved.
*
* This software is the confidential and proprietary information
* of Model N, Inc ("Confidential Information").  You shall not
* disclose such Confidential Information and shall use it only
* in accordance with the terms of the license agreement you
* entered into with Model N, Inc.
*/
package com.modeln.testfw.reporting.search;

/**
 * The data object represents a search criteria that will be used to join
 * two database tables.
 *
 * @author             Shawn Stafford
 */
public class CMnTableJoin {

    /** Unique identifier for the current criteria */
    private String system_id;


    /** Name of the first table in the join */
    private String left_table;

    /** Name of the column in the first table of the join */
    private String left_column;

    /** Name of the second table in the join */
    private String right_table;

    /** Name of the column in the second table of the join */
    private String right_column;

    /**
     * Construct the table join.
     *
     * @param   t1      First table
     * @param   c1      Column in the first table
     * @param   t2      Second table
     * @param   c2      Column in the second table
     */
    public CMnTableJoin(String t1, String c1, String t2, String c2) {
        left_table = t1;
        left_column = c1;

        right_table = t2;
        right_column = c2;
    }

    /**
     * Sets a unique identifier that can be used to identify the group.
     */
    public void setSystemId(String id) {
        system_id = id;
    }

    /**
     * Returns the unique identifier that can be used to identify the group.
     */
    public String getSystemId() {
        return system_id;
    }

    /**
     * Returns the left table.
     *
     * @return  left table name
     */
    public String getLeftTable() {
        return left_table;
    }

    /**
     * Returns the column in the left table to be joined.
     *
     * @return  left column name
     */
    public String getLeftColumn() {
        return left_column;
    }

    /**
     * Returns the right table.
     *
     * @return  right table name
     */
    public String getRightTable() {
        return right_table;
    }

    /**
     * Returns the column in the right table to be joined.
     *
     * @return  right column name
     */
    public String getRightColumn() {
        return right_column;
    }

    /**
     * Determines whether any of the search criteria require the specified
     * table.
     *
     * @param   table   Name of the table
     * @return  TRUE if the table is required, FALSE otherwise
     */
    public boolean requiresTable(String table) {
        boolean found = false;

        if (table != null) {
            if (table.equalsIgnoreCase(left_table)) {
                found = true;
            } else if (table.equalsIgnoreCase(right_table)) {
                found = true;
            }
        }

        return found;
    }

    /** 
     * Convert the object to an SQL query string.
     *
     * @return  SQL query string
     */
    public String toSql() {
        StringBuffer criteria = new StringBuffer();
    
        criteria.append(left_table + "." + left_column);
        criteria.append("=");
        criteria.append(right_table + "." + right_column);

        return criteria.toString();
    }

}
