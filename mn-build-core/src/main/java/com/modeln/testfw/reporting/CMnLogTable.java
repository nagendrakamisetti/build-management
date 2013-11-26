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

import com.modeln.testfw.reporting.search.CMnSearchGroup;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;
import java.text.SimpleDateFormat;


/**
 * The log table interface defines all of the table and column names
 * used to create the build log table.
 * 
 * @author  Shawn Stafford
 */
public class CMnLogTable extends CMnTable {

    /** Indicates that the log type is HTML */
    public static final String HTML_FORMAT = "html";

    /** Indicates that the log type is plain text. */
    public static final String TEXT_FORMAT = "text";

    /** Name of the table used for the log information */
    public static final String LOG_TABLE = "build_log";

    /** Name of the column that identifies the log */
    public static final String LOG_ID = "log_id";

    /** Name of the column that identifies the log title */
    public static final String LOG_NAME = "log_name";

    /** Name of the column that identifies the log content */
    public static final String LOG_TEXT = "log_text";

    /** Name of the column that indicates the file format of the log text (html, text, etc) */
    public static final String LOG_TYPE = "log_type";

    /** Singleton instance of the table class */
    private static CMnLogTable instance;

    /**
     * Return the singleton instance of the class.
     */
    public static CMnLogTable getInstance() {
        if (instance == null) {
            instance = new CMnLogTable();
        }
        return instance;
    }



    /**
     * Retrieve all logs associated with a specific build.  The log
     * data is returned as a list of CMnDbLogData objects. 
     * 
     * @param   conn    Database connection
     * @param   buildId Primary key used to locate the build info
     *
     * @return  Log entries 
     */
    public synchronized static Vector getLogs(
            Connection conn, 
            String buildId) 
        throws SQLException
    {
        Vector list = new Vector();

        StringBuffer sql = new StringBuffer();
        sql.append(
            "SELECT * FROM " + LOG_TABLE + 
            " WHERE " + CMnBuildTable.BUILD_ID + "=" + buildId +
            " ORDER BY " + LOG_ID + " ASC" 
        );

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            getInstance().debugWrite("Attempting to execute query: " + sql.toString());
            rs = st.executeQuery(sql.toString());
            if (rs != null) {
                CMnDbLogData log = null;
                while (rs.next()) {
                    log = parseLogData(rs);
                    list.add(log);
                }
            } else {
                 System.err.println("Unable to obtain the build logs.");
            }
        } catch (SQLException ex) {
            System.err.println("Failed to obtain build data: " + sql.toString());
            ex.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return list;
    }


    /**
     * Retrieve the log information associated with a specific log ID. 
     *
     * @param   conn    Database connection
     * @param   logId   Primary key used to locate the log info
     *
     * @return  Log entries
     */
    public synchronized static CMnDbLogData getLog(
            Connection conn,
            String logId)
        throws SQLException
    {
        CMnDbLogData log = null;

        StringBuffer sql = new StringBuffer();
        sql.append(
            "SELECT * FROM " + LOG_TABLE +
            " WHERE " + CMnLogTable.LOG_ID + "=" + logId
        );

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            getInstance().debugWrite("Attempting to execute query: " + sql.toString());
            rs = st.executeQuery(sql.toString());
            if (rs != null) {
                rs.first();
                log = parseLogData(rs);
            } else {
                 System.err.println("Unable to obtain the log data.");
            }
        } catch (SQLException ex) {
            System.err.println("Failed to obtain log data: " + sql.toString());
            ex.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return log;
    }

    /**
     * Parse the result set to obtain log information.
     * 
     * @param   rs    Result set containing log data
     *
     * @return  Log information
     */
    public static CMnDbLogData parseLogData(ResultSet rs) 
        throws SQLException 
    {
        CMnDbLogData data = new CMnDbLogData();

        int id = rs.getInt(LOG_ID);
        data.setId(id);

        String name = rs.getString(LOG_NAME);
        data.setName(name);

        String type = rs.getString(LOG_TYPE);
        setLogFormat(data, type);

        String text = rs.getString(LOG_TEXT);
        data.setText(text);

        return data;
    }

    /**
     * Parse the log format field and set the format.
     *
     * @param   log      Log data object
     * @param   format   Log format string from the database
     */
    private static void setLogFormat(CMnDbLogData log, String format) {
        if (HTML_FORMAT.equals(format)) {
            log.setFormat(CMnDbLogData.HTML_FORMAT);
        } else {
            log.setFormat(CMnDbLogData.TEXT_FORMAT);
        }
    }

    /**
     * Convert the log format to a string.
     *
     * @param   log   Log data object 
     * @return  Log format 
     */
    private static String getLogFormat(CMnDbLogData log) {
        switch (log.getFormat()) {
            case CMnDbLogData.HTML_FORMAT:
                return HTML_FORMAT;
            default:
                // If we don't know what to do, assume text 
                return TEXT_FORMAT;
        }
    }

}

