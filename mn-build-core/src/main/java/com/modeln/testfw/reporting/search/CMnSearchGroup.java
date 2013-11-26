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


import java.util.Vector;
import java.util.Collection;

/**
 * A collection of search criteria that will be used to construct
 * query from the logical grouping of the criteria.  This should
 * probably be implemented as a tree, but is currently done using
 * vectors.
 *
 * @author             Shawn Stafford
 *
 */
public class CMnSearchGroup {

    public static final String AND          = "AND";
    public static final String OR           = "OR";


    /** Unique identifier for the current search group */
    private String group_id;

    /** List of search criteria in the group */
    private Vector criteria_list;

    /** Logical operator that is applied to all group criteria */
    private String operator;

    /** List of sub-groups that contain search criteria as well */
    private Vector subgroups;

    /** List of table join operations */
    private Vector joins;


    /** 
     * Construct the search group.
     *
     * @param   op      Logical operator to apply to elements of the group
     */
    public CMnSearchGroup(String op) {
        criteria_list = new Vector();
        subgroups = new Vector();
        joins = new Vector();

        if (AND.equalsIgnoreCase(op)) {
            operator = AND;
        } else if (OR.equalsIgnoreCase(op)) {
            operator = OR;
        } else {
            // Default to an AND operator
            operator = AND;
        }
    }

    /**
     * Returns the operator for the group
     *
     * @return  Operator
     */
    public String getOperator() {
        return operator;
    }
    
    /**
     * Sets a unique identifier that can be used to identify the group.
     */
    public void setSystemId(String id) {
        group_id = id;
    }

    /**
     * Returns the unique identifier that can be used to identify the group.
     */
    public String getSystemId() {
        return group_id;
    }


    /**
     * Returns the list of subgroups.
     *
     * @return  Subgroups
     */
    public Vector getSubGroups() {
        return subgroups;
    }

    /**
     * Returns the list of table joins.
     *
     * @return  Table joins
     */
    public Vector getJoins() {
        return joins;
    }

    /**
     * Returns the list of search criteria for the group
     * 
     * @return  Search Criteria
     */
    public Vector getCriteria() {
        return criteria_list;
    }

    /**
     * Add a new search criteria to the list.
     */
    public void add(CMnSearchCriteria criteria) {
        criteria_list.add(criteria);
    }

    /** 
     * Add a new sub group.
     */
    public void add(CMnSearchGroup group) {
        subgroups.add(group);
    }

    /**
     * Add a table join operation
     *
     * @param   join    Table join operation
     */
    public void add(CMnTableJoin join) {
        joins.add(join);
    }


    /**
     * Determines whether any of the search criteria require the specified
     * table.
     *
     * @param   table   Name of the table
     * @return  TRUE if the table is required, FALSE otherwise
     */
    public boolean requiresTable(String table) {
        // Check the list of tables in the search criteria
        CMnSearchCriteria current;
        for (int idx = 0; idx < criteria_list.size(); idx++) {
            current = (CMnSearchCriteria)criteria_list.get(idx);
            if (table.equalsIgnoreCase(current.getTable())) {
                return true;
            }
        }

        // Check the list of tables in the join criteria
        CMnTableJoin join;
        for (int idx = 0; idx < joins.size(); idx++) {
            join = (CMnTableJoin)joins.get(idx);
            if (join.requiresTable(table)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Adds the list of unique tables to the list of current tables supplied
     * to the method by recursively inspecting the criteria, joins, and 
     * subgroups.  All new tables will be added to the list.
     *
     * @param   tables      List of existing tables.
     */
    public void addTables(Vector tables) {
        // Cycle through all of the criteria tables and add unique entries
        CMnSearchCriteria current;
        for (int idx = 0; idx < criteria_list.size(); idx++) {
            current = (CMnSearchCriteria)criteria_list.get(idx);
            // Check to see of the table already exists in the list
            if (!tables.contains(current.getTable())) {
                tables.add(current.getTable());
            }
        }

        // Cycle through all of the join tables and add unique entries
        CMnTableJoin join;
        for (int idx = 0; idx < joins.size(); idx++) {
            join = (CMnTableJoin)joins.get(idx);
            // Check to see of the table already exists in the list
            if (!tables.contains(join.getLeftTable())) {
                tables.add(join.getLeftTable());
            }
            if (!tables.contains(join.getRightTable())) {
                tables.add(join.getRightTable());
            }
        }

        // Process the subgroups
        CMnSearchGroup group;
        for (int sub = 0; sub < subgroups.size(); sub++) {
            group = (CMnSearchGroup)subgroups.get(sub);
            if (group != null) {
                group.addTables(tables);
            }
        }


    }

    /** 
     * Convert the object to an SQL query string.
     *
     * @return  SQL query string
     */
    public String toSql() {
        StringBuffer sql = new StringBuffer();
        boolean isFirst = true;

        // Format the criteria at the current level
        CMnSearchCriteria current;
        for (int idx = 0; idx < criteria_list.size(); idx++) {
            current = (CMnSearchCriteria)criteria_list.get(idx);
            if (!isFirst) {
                sql.append(" " + operator + " ");
            } else {
                isFirst = false;
            }
            sql.append(current.toSql());
        }

        // Format the joins at the current level
        CMnTableJoin join;
        for (int idx = 0; idx < joins.size(); idx++) {
            join = (CMnTableJoin)joins.get(idx);
            if (!isFirst) {
                sql.append(" " + operator + " ");
            } else {
                isFirst = false;
            }
            sql.append(join.toSql());
        }

        // Format the subgroups
        CMnSearchGroup group;
        for (int sub = 0; sub < subgroups.size(); sub++) {
            group = (CMnSearchGroup)subgroups.get(sub);
            // Be careful!  The subgroup may return a non-null string that
            // contains only whitespace.  We wouldn't want to append an
            // operator to that!
            String subSql = group.toSql();
            if ((subSql != null) && (subSql.length() > 0)) {
                subSql = subSql.trim();
                if (! isFirst) {
                    sql.append(" " + operator + " ");
                } else if ((subSql != null) && (subSql.length() > 0)) {
                    isFirst = false;
                }
                sql.append(group.toSql());
            }
        }

        // Make sure that our current string isn't just a bunch of whitespace
        String sqlStr = sql.toString();
        if (sqlStr != null) {
            sqlStr = sqlStr.trim();
        }

        // Return the SQL string
        if ((!isFirst) && (sqlStr != null) && (sqlStr.length() > 0)) {
            return "(" + sqlStr + ")";
        } else {
            return sqlStr;
        }

    }

}
