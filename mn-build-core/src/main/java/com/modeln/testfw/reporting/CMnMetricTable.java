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

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;
import java.text.SimpleDateFormat;

/**
 * The metric table provides start and end dates for a predifined list of 
 * build activities. 
 * 
 * @author  Shawn Stafford
 */
public class CMnMetricTable extends CMnTable {

    /** Name of the metric table. */
    private static final String METRIC_TABLE = "build_metrics";


    /** Name of the column that identifies the associated build. */
    public static final String BUILD_VERSION = "build_version";

    /** Name of the column that identifies the username on the host */
    public static final String METRIC_USER_NAME = "username";

    /** Name of the column that identifies the machine hostname where the event occurred */
    public static final String METRIC_HOST_NAME = "hostname";

    /** Name of the column for the build start time */
    public static final String METRIC_START_DATE = "start_date";

    /** Name of the column for the build end time */
    public static final String METRIC_END_DATE = "end_date";

    /** Name of the column that identifies the event type */
    public static final String METRIC_TYPE = "activity";



    /** Singleton instance of the table class */
    private static CMnMetricTable instance;


    /**
     * Return the singleton instance of the class.
     */
    public static CMnMetricTable getInstance() {
        if (instance == null) {
            instance = new CMnMetricTable();

            // Enable debbuging to a file
            String logfile = "/var/tmp/CMnMetricTable.txt";
            try {
                instance.setDebugOutput(new PrintStream(logfile));
                instance.debugEnable(true);
            } catch (FileNotFoundException nfex) {
                System.out.println("Unable to enable debugging.  Failed to open log file: " + logfile);
            }

        }
        return instance;
    }


    /**
     * Retrieve a list of metrics from the build metric table.
     *
     * @param  conn     Database connection
     * @param  version  Build version string
     * 
     * @return List of metrics
     */
    public Vector getMetrics(Connection conn, String version) throws SQLException {
        Vector list = new Vector();

        // Construct the query to obtain a list of all releases
        String sql = "SELECT * FROM " + METRIC_TABLE + 
                     " WHERE " + BUILD_VERSION + " = '" + version + "'" +
                     " ORDER BY " + METRIC_START_DATE + " DESC";

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            // Execute the query
            getInstance().debugWrite(sql);
            rs = st.executeQuery(sql);
            if (rs != null) {
                while (rs.next()) {
                    list.add(parseMetricData(rs));
                }
            } else {
                 System.err.println("Unable to obtain a list of releases.");
            }

        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return list;

    }


    /**
     * Parse the result set to obtain event metric information.
     * 
     * @param   rs    Result set containing metric data
     *
     * @return  Metric information
     */
    public CMnDbMetricData parseMetricData(ResultSet rs) 
        throws SQLException 
    {
        CMnDbMetricData data = new CMnDbMetricData();

        //String username = rs.getString(METRIC_USER_NAME);
        //data.setUsername(username);

        //String hostname = rs.getString(METRIC_HOST_NAME);
        //data.setHostname(hostname);

        Date startTime = rs.getTimestamp(METRIC_START_DATE);
        data.setStartTime(startTime);

        Date endTime = rs.getTimestamp(METRIC_END_DATE);
        data.setEndTime(endTime);

        String type = rs.getString(METRIC_TYPE);
        data.setType(data.getMetricType(type));

        return data;

    }


}

