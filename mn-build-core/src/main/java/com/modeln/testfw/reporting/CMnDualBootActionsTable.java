/* 
* Copyright 2000-2003 by Model N, Inc.  All Rights Reserved. 
* 
* This software is the confidential and proprietary information 
* of Model N, Inc ("Confidential Information").  You shall not 
* disclose such Confidential Information and shall use it only 
* in accordance with the terms of the license agreement you 
* entered into with Model N, Inc. 
*/
package com.modeln.testfw.reporting;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Vector;


/**
 * The actions table interface defines all of the table and column names
 * used to create and manipulate the list of dual boot actions. 
 * 
 * @author  Shawn Stafford
 */
public class CMnDualBootActionsTable extends CMnTable {


    /** Name of the table used for the action information */
    public static final String ACTIONS_TABLE = "dualboot_actions";

    /** Name of the column that identifies the action identifier */
    public static final String ACTION_ID = "action_id";

    /** Name of the column that identifies the host */
    public static final String ACTION_HOSTNAME = "hostname";

    /** Name of the column that identifies the target operating system type */
    public static final String ACTION_TARGET_TYPE = "target_type";

    /** Name of the column that identifies the date when the action should be performed */
    public static final String ACTION_TARGET_DATE = "target_date";

    /** Name of the column that identifies the execution order of conflicting actions */
    public static final String ACTION_ORDER = "target_order";

    /** Name of the column that identifies the type of action to be performed */
    public static final String ACTION_NAME = "action_name";

    /** Name of the column that identifies the action arguments */
    public static final String ACTION_ARGS = "action_args";

    /** Singleton instance of the table class */
    private static CMnHostTable instance;


    /**
     * Return the singleton instance of the class.
     */
    public static CMnHostTable getInstance() {
        if (instance == null) {
            instance = new CMnHostTable();
        }
        return instance;
    }

    /**
     * Retrieve a list of all actions associated with the specified host.
     * 
     * @param   conn       Database connection
     * @param   hostname   Name of the host
     *
     * @return  List of action information objects
     */
    public synchronized static Vector getActions(Connection conn, String hostname) 
        throws SQLException
    {
        Vector list = new Vector();

        StringBuffer sql = new StringBuffer();
        sql.append(
                "SELECT * FROM " + ACTIONS_TABLE + 
                " WHERE " + ACTION_HOSTNAME + "='" + hostname + "'" +
                " ORDER BY " + ACTION_TARGET_DATE + " ASC" 
            );

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            getInstance().debugWrite("Attempting to execute: " + sql.toString());
            rs = st.executeQuery(sql.toString());
            if (rs != null) {
                CMnDbHostActionData action = null;
                while (rs.next()) {
                    action = parseActionData(rs);
                    list.add(action);
                }
            } else {
                 System.err.println("Unable to obtain the host data.");
            }
        } catch (SQLException ex) {
            System.err.println("Failed to obtain host data: " + sql.toString());
            ex.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return list;
    }

    /**
     * Parse the result set to obtain action information.
     * 
     * @param   rs    Result set containing action data
     *
     * @return  Action information
     */
    public static CMnDbHostActionData parseActionData(ResultSet rs) 
        throws SQLException 
    {
        CMnDbHostActionData data = new CMnDbHostActionData();

        String id = rs.getString(ACTION_ID);
        data.setId(Integer.parseInt(id));

        String hostname = rs.getString(ACTION_HOSTNAME);
        data.setHostname(hostname);

        String type = rs.getString(ACTION_TARGET_TYPE);
        data.setType(type);

        Date date = rs.getDate(ACTION_TARGET_DATE);
        data.setTargetDate(date);

        String order = rs.getString(ACTION_ORDER);
        data.setOrder(Integer.parseInt(order));

        String name = rs.getString(ACTION_NAME);
        data.setName(name);

        String args = rs.getString(ACTION_ARGS);
        data.setArguments(args);

        return data;

    }

}

