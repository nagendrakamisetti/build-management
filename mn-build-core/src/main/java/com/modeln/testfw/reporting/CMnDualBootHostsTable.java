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
import java.sql.Statement;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;
import java.text.SimpleDateFormat;


/**
 * The hosts table interface defines all of the table and column names
 * used to create and manipulate the list of dual boot hosts. 
 * 
 * @author  Shawn Stafford
 */
public class CMnDualBootHostsTable extends CMnTable {


    /** Name of the table used for the build information */
    public static final String HOSTS_TABLE = "dualboot_hosts";

    /** Name of the column that identifies the host */
    public static final String HOST_NAME = "hostname";

    /** Name of the column that identifies the host type to boot */
    public static final String HOST_TYPE = "boot_os";

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
     * Retrieve host information from the database.
     * 
     * @param   conn    Database connection
     * @param   hostname Primary key used to locate the host info
     *
     * @return  Host information
     */
    public synchronized static CMnDbHostActionData getHost(
            Connection conn, 
            String hostname) 
        throws SQLException
    {
        CMnDbHostActionData host = null;

        StringBuffer sql = new StringBuffer();
        sql.append(
            "SELECT * FROM " + HOSTS_TABLE + 
            " WHERE " + HOST_NAME + "=" + hostname +
            " ORDER BY " + HOST_NAME + " ASC" 
        );

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            getInstance().debugWrite("Attempting to execute query: " + sql.toString());
            rs = st.executeQuery(sql.toString());
            if (rs != null) {
                rs.first();
                host = parseHostData(rs);
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

        return host;
    }
    
    /**
     * Retrieve a list of all hosts from the database.
     * 
     * @param   conn    Database connection
     *
     * @return  List of host information objects
     */
    public synchronized static Vector getAllHosts(Connection conn) 
        throws SQLException
    {
        Vector list = new Vector();

        StringBuffer sql = new StringBuffer();
        sql.append("SELECT * FROM " + HOSTS_TABLE + " ORDER BY " + HOST_NAME + " ASC");

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            getInstance().debugWrite("Attempting to execute: " + sql.toString());
            rs = st.executeQuery(sql.toString());
            if (rs != null) {
                CMnDbHostActionData host = null;
                while (rs.next()) {
                    host = parseHostData(rs);
                    list.add(host);
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
     * Parse the result set to obtain host information.
     * 
     * @param   rs    Result set containing host data
     *
     * @return  Host information
     */
    public static CMnDbHostActionData parseHostData(ResultSet rs) 
        throws SQLException 
    {
        CMnDbHostActionData data = new CMnDbHostActionData();

        String hostname = rs.getString(HOST_NAME);
        data.setHostname(hostname);

        String hostType = rs.getString(HOST_TYPE);
        data.setType(hostType);

        return data;

    }

}

