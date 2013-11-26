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

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The data object represents a search criteria that will be used to locate
 * matching records for a column in a database table.
 *
 * @author             Shawn Stafford
 *
 */
public class CMnSearchCriteria {

    /** Date format used when constructing SQL queries */
    protected static final SimpleDateFormat DATETIME = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static final String LESS_THAN    = "lt";
    public static final String GREATER_THAN = "gt";
    public static final String EQUAL_TO     = "eq";
    public static final String NOT_EQUAL    = "ne";
    public static final String LESS_THAN_OR_EQUAL_TO = "le";
    public static final String GREATER_THAN_OR_EQUAL_TO = "ge";

    /** The "like" operator is used to perform a partial match on a string. */
    public static final String LIKE = "like";
    
    /** Unique identifier for the current criteria */
    private String system_id;


    /** Name of the table containing the column being queried */
    private String table_name;

    /** Name of the column that contains the value to be queried */
    private String column_name;

    /** Operator to use for comparing column values */
    private String operator;

    /** Value to be used for comparison */
    private Object value;

    /**
     * Construct the search criteria.
     *
     * @param   table   Name of the database table
     * @param   col     Name of the table column
     */
    public CMnSearchCriteria(String table, String col) {
        table_name = table;
        column_name = col;
    }

    /**
     * Construct the search criteria.
     * 
     * @param   table   Name of the database table
     * @param   col     Name of the table column
     * @param   op      Comparison operator
     * @param   val     Value to use for comparison
     */
    public CMnSearchCriteria(String table, String col, String op, Object val) {
        table_name = table;
        column_name = col;
        operator = op;
        value = val;
    }

    /**
     * Sets a unique identifier that can be used to identify the criteria.
     */
    public void setSystemId(String id) {
        system_id = id;
    }

    /**
     * Returns the unique identifier that can be used to identify the criteria.
     */
    public String getSystemId() {
        return system_id;
    }


    /**
     * Returns the name of the table being searched.
     *
     * @return  The name of the table
     */
    public String getTable() {
        return table_name;
    }

    /**
     * Returns the name of the table column being searched.
     *
     * @return  The name of the table column
     */
    public String getColumn() {
        return column_name;
    }

    /**
     * Returns the operator to apply to the column
     *
     * @return  Operator
     */
    public String getOperator() {
        return operator;
    }

    /** 
     * Set the operator to apply to the column
     *
     * @param   op
     */
    public void setOperator(String op) {
        operator = op;
    }

    /**
     * Returns the value used for comparison.
     *
     * @return  The criteria value
     */
    public Object getValue() {
        return value;
    }

    /**
     * Sets the object used for comparison.
     *
     * @param   val     Value
     */
    public void setValue(Object val) {
        value = val;
    }

    /** 
     * Convert the object to an SQL query string.
     *
     * @return  SQL query string
     */
    public String toSql() {
        StringBuffer criteria = new StringBuffer();

        criteria.append(table_name + "." + column_name);
        if (LESS_THAN.equalsIgnoreCase(operator)) {
            criteria.append("<");
        } else if (GREATER_THAN.equalsIgnoreCase(operator)) {
            criteria.append(">");
        } else if (NOT_EQUAL.equalsIgnoreCase(operator)) {
            criteria.append("<>");
        } else if (LESS_THAN_OR_EQUAL_TO.equalsIgnoreCase(operator)) {
            criteria.append("<=");
        } else if (GREATER_THAN_OR_EQUAL_TO.equalsIgnoreCase(operator)) {
            criteria.append(">=");
        } else if (LIKE.equalsIgnoreCase(operator)) {
            criteria.append(" LIKE ");
        } else {
            criteria.append("=");
        }

        // Obtain the query value
        String valueStr = null; 
        if (value instanceof Date) {
            valueStr = DATETIME.format(value);
        } else {
            valueStr = value.toString();
        }

        // Make sure that the correct escape strings are used for the LIKE operator
        if (LIKE.equalsIgnoreCase(operator)) {
            criteria.append("'%" + valueStr + "%'");
        } else {
            criteria.append("'" + valueStr + "'");
        }

        return criteria.toString();

    }

}
