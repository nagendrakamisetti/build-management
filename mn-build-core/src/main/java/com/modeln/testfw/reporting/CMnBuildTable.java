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
 * The build table interface defines all of the table and column names
 * used to create the build table.
 * 
 * @author  Shawn Stafford
 */
public class CMnBuildTable extends CMnTable {


    /** Name of the table used for the build information */
    public static final String BUILD_TABLE = "build";

    /** Name of the column that identifies the build */
    public static final String BUILD_VERSION = "build_version";

    /** Status field which describes the build (such as GA or P1) */
    public static final String BUILD_STATUS = "build_status";

    /** Name of the column that identifies the build */
    public static final String BUILD_ID = "build_id";

    /** Name of the column for the build start time */
    public static final String BUILD_START_DATE = "start_date";

    /** Name of the column for the build end time */
    public static final String BUILD_END_DATE = "end_date";

    /** Name of the column for the source control type */
    public static final String BUILD_SOURCE_TYPE = "version_ctrl_type";

    /** Name of the column for the build source control root */
    public static final String BUILD_SOURCE_ROOT = "version_ctrl_root";

    /** Name of the column for the build source control ID */
    public static final String BUILD_SOURCE_ID = "version_ctrl_id";

    /** Name of the column for the build archive directory */
    public static final String BUILD_DOWNLOAD_URI = "download_uri";

    /** Name of the column for the Jenkins job URL */
    public static final String BUILD_JOB_URL = "job_url";

    /** Name of the column for the build comments */
    public static final String BUILD_COMMENTS = "comments";


    /** Regular expression that can be used to match the timestamp within a version string */
    public static final String TIMESTAMP_REGEX = 
        "[0-9]{4}"   +    // Four digit Year 
        "[0-1][0-9]" +    // Month (1-12)
        "[0-3][0-9]" +    // Day   (1-31)
        "\\."        +    // Separator character (.)
        "[0-2][0-9]" +    // Hour (0-23)
        "[0-5][0-9]" +    // Minute (0-59)
        "[0-5][0-9]" ;    // Second (0-59)


    /** Singleton instance of the table class */
    private static CMnBuildTable instance;

    /**
     * Return the singleton instance of the class.
     */
    public static CMnBuildTable getInstance() {
        if (instance == null) {
            instance = new CMnBuildTable();

            // Enable debbuging to a file
            String logfile = "/var/tmp/CMnBuildTable.txt";
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
     * Store a test suite information in the database.
     *
     * @param   conn    Database connection
     * @param   build   Build data
     *
     * @return  Auto-generated key that identifies the build 
     */
    public synchronized String addBuild(
            Connection conn,
            CMnDbBuildData build)
        throws SQLException
    {
        String buildId = null;

        CMnDbHostData host = build.getHostData();

        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO " + BUILD_TABLE + " ");
        sql.append("(" + BUILD_VERSION);
        sql.append(", " + BUILD_SOURCE_TYPE);
        sql.append(", " + BUILD_SOURCE_ROOT);
        sql.append(", " + BUILD_SOURCE_ID);
        sql.append(", " + BUILD_DOWNLOAD_URI);
        sql.append(", " + BUILD_JOB_URL);
        if (build.getStartTime() != null) {
            sql.append(", " + BUILD_START_DATE);
        }
        if (build.getEndTime() != null) {
            sql.append(", " + BUILD_END_DATE);
        }
        if (build.getComments() != null) {
            sql.append(", " + BUILD_COMMENTS);
        }
        if (host != null) {
            sql.append(", " + CMnHostTable.HOST_ACCOUNT);
            sql.append(", " + CMnHostTable.HOST_NAME);
            sql.append(", " + CMnHostTable.JDK_VERSION);
            sql.append(", " + CMnHostTable.JDK_VENDOR);
            sql.append(", " + CMnHostTable.OS_NAME);
            sql.append(", " + CMnHostTable.OS_ARCHITECTURE);
            sql.append(", " + CMnHostTable.OS_VERSION);
        }
        sql.append(", " + BUILD_STATUS + ") ");

        sql.append("VALUES ");
        sql.append("(\"" + build.getBuildVersion() + "\"");
        sql.append(", \"" + build.getVersionControlType() + "\"");
        sql.append(", \"" + build.getVersionControlRoot() + "\"");
        sql.append(", \"" + build.getVersionControlId() + "\"");
        sql.append(", \"" + build.getDownloadUri() + "\"");
        sql.append(", \"" + build.getJobUrl() + "\"");
        if (build.getStartTime() != null) {
            sql.append(", \"" + DATETIME.format(build.getStartTime()) + "\"");
        }
        if (build.getEndTime() != null) {
            sql.append(", \"" + DATETIME.format(build.getEndTime()) + "\"");
        }
        if (build.getComments() != null) {
            sql.append(", \"" + build.getComments() + "\"");
        }
        if (host != null) {
            sql.append(", \"" + host.getUsername() + "\"");
            sql.append(", \"" + host.getHostname() + "\"");
            sql.append(", \"" + host.getJdkVersion() + "\"");
            sql.append(", \"" + host.getJdkVendor() + "\"");
            sql.append(", \"" + host.getOSName() + "\"");
            sql.append(", \"" + host.getOSArchitecture() + "\"");
            sql.append(", \"" + host.getOSVersion() + "\"");
        }
        sql.append(", \"" + build.getReleaseId() + "\")");

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            executeInsert(st, "addBuild", sql.toString());
            //rs = st.getGeneratedKeys();
            rs = getGeneratedKeys(st);
            if (rs != null) {
                rs.first();
                buildId = rs.getString(1);
            } else {
                 System.err.println("Unable to obtain generated build ID.");
            }
        } catch (SQLException ex) {
            System.err.println("Failed to add build: " + sql.toString());
            ex.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return buildId;
    }



    /**
     * Retrieve build information from the database.
     * 
     * @param   conn    Database connection
     * @param   buildId Primary key used to locate the build info
     *
     * @return  Build information
     */
    public synchronized static CMnDbBuildData getBuild(
            Connection conn, 
            String buildId) 
        throws SQLException
    {
        CMnDbBuildData build = null;

        StringBuffer sql = new StringBuffer();
        sql.append(
            "SELECT * FROM " + BUILD_TABLE + 
            " WHERE " + BUILD_ID + "=" + buildId +
            " ORDER BY " + BUILD_START_DATE + " DESC" 
        );

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            getInstance().debugWrite("Attempting to execute query: " + sql.toString());
            rs = st.executeQuery(sql.toString());
            if (rs != null) {
                rs.first();
                build = parseBuildData(rs);
                getTestSummary(conn, build);
                build.setStatus(CMnReleaseTable.getStatus(conn, build));
                CMnMetricTable metricTable = CMnMetricTable.getInstance();
                Vector metrics = metricTable.getMetrics(conn, build.getBuildVersion());
                if ((metrics != null) && (metrics.size() > 0)) {
                    build.setMetrics(metrics);
                }
            } else {
                 System.err.println("Unable to obtain the build data.");
            }
        } catch (SQLException ex) {
            System.err.println("Failed to obtain build data: " + sql.toString());
            ex.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return build;
    }



    /**
     * Retrieve a summary of the test results for various groups of tests associated
     * with the specified build.  Results will be added to the build data object. 
     * This is a helper function and assumes that the database connection is already
     * open and ready to be used.
     * 
     * @param   conn    Database connection
     * @param   build   Build data 
     */
    private synchronized static void getTestSummary(
            Connection conn,
            CMnDbBuildData build) 
        throws SQLException
    {
        CMnUnittestTable utTable = CMnUnittestTable.getInstance();
        CMnAcceptanceTestTable actTable = CMnAcceptanceTestTable.getInstance();
        CMnUitTable uitTable = CMnUitTable.getInstance();
        CMnFlexTestTable flexTable = CMnFlexTestTable.getInstance();

        // Obtain a summary of the Unit test results
        CMnDbTestSummaryData unittests = new CMnDbTestSummaryData();
        int utTotal = utTable.getTestCount(conn, build, false);
        if (utTotal > 0) {
            unittests.setPassingCount(utTable.getTestCount(conn, build, CMnTestTable.STATUS_PASS, false));
            unittests.setFailingCount(utTable.getTestCount(conn, build, CMnTestTable.STATUS_FAIL, false));
            unittests.setErrorCount(utTable.getTestCount(conn, build, CMnTestTable.STATUS_ERROR, false));
            unittests.setKilledCount(utTable.getTestCount(conn, build, CMnTestTable.STATUS_KILL, false));
            unittests.setSkippedCount(utTable.getTestCount(conn, build, CMnTestTable.STATUS_SKIP, false));
            unittests.setBlacklistCount(utTable.getTestCount(conn, build, CMnTestTable.STATUS_BLACKLIST, false));
            unittests.setPendingCount(utTable.getTestCount(conn, build, CMnTestTable.STATUS_PENDING, false));
            unittests.setRunningCount(utTable.getTestCount(conn, build, CMnTestTable.STATUS_RUNNING, false));
            unittests.setTotalCount(utTotal); 
        }
        build.setTestSummary("JUNIT", unittests);

        // Obtain a summary of the UIT results
        CMnDbTestSummaryData uits = new CMnDbTestSummaryData();
        int uitTotal = uitTable.getTestCount(conn, build, false);
        if (uitTotal > 0) {
            uits.setPassingCount(uitTable.getTestCount(conn, build, CMnTestTable.STATUS_PASS, false));
            uits.setFailingCount(uitTable.getTestCount(conn, build, CMnTestTable.STATUS_FAIL, false));
            uits.setErrorCount(uitTable.getTestCount(conn, build, CMnTestTable.STATUS_ERROR, false));
            uits.setKilledCount(uitTable.getTestCount(conn, build, CMnTestTable.STATUS_KILL, false));
            uits.setSkippedCount(uitTable.getTestCount(conn, build, CMnTestTable.STATUS_SKIP, false));
            uits.setBlacklistCount(uitTable.getTestCount(conn, build, CMnTestTable.STATUS_BLACKLIST, false));
            uits.setPendingCount(uitTable.getTestCount(conn, build, CMnTestTable.STATUS_PENDING, false));
            uits.setRunningCount(uitTable.getTestCount(conn, build, CMnTestTable.STATUS_RUNNING, false));
            uits.setTotalCount(uitTotal); 
        }
        build.setTestSummary("UIT", uits);

        // Obtain a summary of the ACT results
        CMnDbTestSummaryData acts = new CMnDbTestSummaryData();
        int actTotal = actTable.getTestCount(conn, build, false);
        if (actTotal > 0) {
            acts.setPassingCount(actTable.getTestCount(conn, build, CMnTestTable.STATUS_PASS, false));
            acts.setFailingCount(actTable.getTestCount(conn, build, CMnTestTable.STATUS_FAIL, false));
            acts.setErrorCount(actTable.getTestCount(conn, build, CMnTestTable.STATUS_ERROR, false));
            acts.setKilledCount(actTable.getTestCount(conn, build, CMnTestTable.STATUS_KILL, false));
            acts.setSkippedCount(actTable.getTestCount(conn, build, CMnTestTable.STATUS_SKIP, false));
            acts.setBlacklistCount(actTable.getTestCount(conn, build, CMnTestTable.STATUS_BLACKLIST, false));
            acts.setPendingCount(actTable.getTestCount(conn, build, CMnTestTable.STATUS_PENDING, false));
            acts.setRunningCount(actTable.getTestCount(conn, build, CMnTestTable.STATUS_RUNNING, false));
            acts.setTotalCount(actTotal); 
        }
        build.setTestSummary("ACT", acts);

        // Obtain a summary of the Flex results
        CMnDbTestSummaryData flex = new CMnDbTestSummaryData();
        int flexTotal = flexTable.getTestCount(conn, build, false);
        if (flexTotal > 0) {
            flex.setPassingCount(flexTable.getTestCount(conn, build, CMnTestTable.STATUS_PASS, false));
            flex.setFailingCount(flexTable.getTestCount(conn, build, CMnTestTable.STATUS_FAIL, false));
            flex.setErrorCount(flexTable.getTestCount(conn, build, CMnTestTable.STATUS_ERROR, false));
            flex.setKilledCount(flexTable.getTestCount(conn, build, CMnTestTable.STATUS_KILL, false));
            flex.setSkippedCount(flexTable.getTestCount(conn, build, CMnTestTable.STATUS_SKIP, false));
            flex.setBlacklistCount(flexTable.getTestCount(conn, build, CMnTestTable.STATUS_BLACKLIST, false));
            flex.setPendingCount(flexTable.getTestCount(conn, build, CMnTestTable.STATUS_PENDING, false));
            flex.setRunningCount(flexTable.getTestCount(conn, build, CMnTestTable.STATUS_RUNNING, false));
            flex.setTotalCount(flexTotal); 
        }
        build.setTestSummary("FLEX", flex);

    }

    /**
     * Look up any builds which match the build version number. 
     * The build version number may be a full version number or a partial string.
     * The database query will use the LIKE operator to locate any entries that
     * match the string.
     *
     * @param   conn    Database connection
     * @param   version Build version string  
     *
     * @return  List of build information objects
     */
    public synchronized static Vector getBuildsByVersion(
            Connection conn, 
            String version)
        throws SQLException
    {
        Vector list = new Vector();

        StringBuffer sql = new StringBuffer();
        sql.append(
            "SELECT * FROM " + BUILD_TABLE + 
            " WHERE " + BUILD_VERSION + " LIKE '%" + version + "%'" +
            " ORDER BY " + BUILD_START_DATE + " DESC"
        );

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            getInstance().debugWrite("Attempting to execute: " + sql.toString());
            rs = st.executeQuery(sql.toString());
            if (rs != null) {
                CMnDbBuildData build = null;
                while (rs.next()) {
                    build = parseBuildData(rs); 
                    getTestSummary(conn, build);
                    build.setStatus(CMnReleaseTable.getStatus(conn, build));
                    list.add(build);
                }
            } else {
                 System.err.println("Unable to obtain the build data.");
            }
        } catch (SQLException ex) {
            System.err.println("Failed to obtain build data.");
            ex.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return list;
    }


    /**
     * Look up any builds which match the Job url. 
     * The job URL may be a full URL or a partial string.  If specifying a 
     * partial string, use a SQL pattern matching character like the percent
     * (%) symbol to search for a match.  The database query will use the 
     * LIKE operator to locate any entries that match the string.
     *
     * @param   conn    Database connection
     * @param   jobUrl  Job URL string 
     *
     * @return  List of build information objects
     */
    public synchronized static Vector getBuildsByJob(
            Connection conn,
            String jobUrl)
        throws SQLException
    {
        Vector list = new Vector();

        StringBuffer sql = new StringBuffer();
        sql.append(
            "SELECT * FROM " + BUILD_TABLE +
            " WHERE " + BUILD_JOB_URL + " LIKE '" + jobUrl + "%'" +
            " ORDER BY " + BUILD_START_DATE + " DESC"
        );

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            getInstance().debugWrite("Attempting to execute: " + sql.toString());
            rs = st.executeQuery(sql.toString());
            if (rs != null) {
                CMnDbBuildData build = null;
                while (rs.next()) {
                    build = parseBuildData(rs);
                    getTestSummary(conn, build);
                    build.setStatus(CMnReleaseTable.getStatus(conn, build));
                    list.add(build);
                }
            } else {
                 System.err.println("Unable to obtain the build data.");
            }
        } catch (SQLException ex) {
            System.err.println("Failed to obtain build data.");
            ex.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return list;
    }




    /**
     * Retrieve a list of all builds from the database.
     * 
     * @param   conn    Database connection
     *
     * @return  List of build information objects
     */
    public synchronized static Vector getAllBuilds(Connection conn) 
        throws SQLException
    {
        Vector list = new Vector();

        StringBuffer sql = new StringBuffer();
        sql.append("SELECT * FROM " + BUILD_TABLE + " ORDER BY " + BUILD_START_DATE + " DESC");

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            getInstance().debugWrite("Attempting to execute: " + sql.toString());
            rs = st.executeQuery(sql.toString());
            if (rs != null) {
                CMnDbBuildData build = null;
                while (rs.next()) {
                    build = parseBuildData(rs);
                    getTestSummary(conn, build);
                    build.setStatus(CMnReleaseTable.getStatus(conn, build));
                    CMnMetricTable metricTable = CMnMetricTable.getInstance();
                    Vector metrics = metricTable.getMetrics(conn, build.getBuildVersion());
                    if ((metrics != null) && (metrics.size() > 0)) {
                        build.setMetrics(metrics);
                    }
                    list.add(build);
                }
            } else {
                 System.err.println("Unable to obtain the build data.");
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
     * Retrieve a list of all builds from the database, limiting the result
     * set to a specified date range.  This only populates the build data,
     * not related information such as unit test results.
     *
     * @param   conn      Database connection
     * @param   start     Date range to search for running builds
     * @param   end       Date range to search for running builds 
     * @return  List of build information objects
     */
    public synchronized static Vector getBuildsByDate(
            Connection conn,
            Date start,
            Date end)
        throws SQLException
    {
        Vector list = new Vector();

        StringBuffer sql = new StringBuffer();
        sql.append("SELECT * FROM " + BUILD_TABLE);

        // WHERE ((BUILD_START_DATE >= start AND BUILD_START_DATE <= end) OR
        //        (BUILD_END_DATE >= start AND BUILD_END_DATE <= end))
        sql.append(" WHERE ((" + BUILD_START_DATE + " >= '" + DATETIME.format(start) + "' AND ");
        sql.append("         " + BUILD_START_DATE + " <= '" + DATETIME.format(end) + "') OR "); 
        sql.append("        (" + BUILD_END_DATE + " >= '" + DATETIME.format(start) + "' AND ");
        sql.append("         " + BUILD_END_DATE + " <= '" + DATETIME.format(end) + "'))");

        sql.append(" ORDER BY " + BUILD_START_DATE + " DESC");

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            getInstance().debugWrite("Attempting to execute: " + sql.toString());
            rs = st.executeQuery(sql.toString());
            if (rs != null) {
                CMnDbBuildData build = null;
                while (rs.next()) {
                    build = parseBuildData(rs);
                    list.add(build);
                }
            } else {
                 System.err.println("Unable to obtain the build data.");
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
     * Retrieve a list of all builds from the database, limiting the result
     * set to a specified number or rows.  The rows will be selected either 
     * before the given build ID or after, depending on the boolean selector
     * passed to the method.
     * 
     * @param   conn      Database connection
     * @param   criteria  Set of limiting criteria
     * @param   buildId   Identifies the build to start the search from
     * @param   count     Number of rows to return
     * @param   forward   Determines whether the results should be before or 
     *                    after the specified build ID (forward or reverse search).
     * @return  List of build information objects
     */
    public synchronized static Vector getAllBuilds(
            Connection conn, 
            CMnSearchGroup criteria, 
            int buildId, 
            int count, 
            boolean forward) 
        throws SQLException
    {
        Vector list = new Vector();

        // Determine whether to query forward or back of the build ID 
        String fwdOp = null;
        if (forward) {
            fwdOp = ">";
        } else {
            fwdOp = "<";
        }

        StringBuffer sql = new StringBuffer();
        sql.append("SELECT * FROM " + BUILD_TABLE);
        sql.append(" WHERE " + BUILD_ID + fwdOp + "=" + buildId);
        // Make sure we actually have some criteria to add
        if (criteria != null) {
            String crStr = criteria.toSql();
            if ((crStr != null) && (crStr.length() > 0)) {
                sql.append(" AND " + criteria.toSql());
            }
        }
        sql.append(" ORDER BY " + BUILD_START_DATE + " DESC LIMIT " + count);

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            getInstance().debugWrite("Attempting to execute: " + sql.toString());
            rs = st.executeQuery(sql.toString());
            if (rs != null) {
                CMnDbBuildData build = null;
                while (rs.next()) {
                    build = parseBuildData(rs);
                    getTestSummary(conn, build);
                    build.setStatus(CMnReleaseTable.getStatus(conn, build));
                    CMnMetricTable metricTable = CMnMetricTable.getInstance();
                    Vector metrics = metricTable.getMetrics(conn, build.getBuildVersion());
                    if ((metrics != null) && (metrics.size() > 0)) {
                        build.setMetrics(metrics);
                    }
                    list.add(build);
                }
            } else {
                 System.err.println("Unable to obtain the build data.");
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
     * Delete all build information and associated unit test information.  This
     * emulates the behavior of a cascading delete. 
     *
     * @param   conn    Database connection
     * @param   buildId Identifies a unique build 
     *
     * @return Number of rows deleted 
     */
    public synchronized static void deleteBuild(
            Connection conn,
            String buildId)
        throws SQLException
    {
        CMnUnittestTable utTable = CMnUnittestTable.getInstance();
        CMnAcceptanceTestTable actTable = CMnAcceptanceTestTable.getInstance();
        CMnFlexTestTable flexTable = CMnFlexTestTable.getInstance();
        CMnUitTable uitTable = CMnUitTable.getInstance();

        // Delete all tests associated with the build
        utTable.deleteAllSuites(conn, buildId);
        uitTable.deleteAllSuites(conn, buildId);
        actTable.deleteAllSuites(conn, buildId);
        flexTable.deleteAllSuites(conn, buildId);

        StringBuffer sql = new StringBuffer();
        sql.append("DELETE FROM " + BUILD_TABLE +
                   " WHERE " + BUILD_TABLE + "." + BUILD_ID + " = '" + buildId + "' ");

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            getInstance().debugWrite("Attempting to execute: " + sql.toString());
            st.execute(sql.toString());
        } catch (SQLException ex) {
            System.err.println("Failed to delete build: " + buildId);
            ex.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

    }



    /**
     * Parse the result set to obtain build information.
     * 
     * @param   rs    Result set containing build data
     *
     * @return  Build information
     */
    public static CMnDbBuildData parseBuildData(ResultSet rs) 
        throws SQLException 
    {
        CMnDbBuildData data = new CMnDbBuildData();

        int id = rs.getInt(BUILD_TABLE + "." + BUILD_ID);
        data.setId(id);

        Date startTime = rs.getTimestamp(BUILD_TABLE + "." + BUILD_START_DATE);
        data.setStartTime(startTime);

        Date endTime = rs.getTimestamp(BUILD_TABLE + "." + BUILD_END_DATE);
        data.setEndTime(endTime);

        String version = rs.getString(BUILD_TABLE + "." + BUILD_VERSION);
        data.setBuildVersion(version);

        String status = rs.getString(BUILD_TABLE + "." + BUILD_STATUS);
        data.setReleaseId(status);

        String srcType = rs.getString(BUILD_TABLE + "." + BUILD_SOURCE_TYPE);
        data.setVersionControlType(srcType);

        String srcRoot = rs.getString(BUILD_TABLE + "." + BUILD_SOURCE_ROOT);
        data.setVersionControlRoot(srcRoot);

        String srcId = rs.getString(BUILD_TABLE + "." + BUILD_SOURCE_ID);
        data.setVersionControlId(srcId);

        String downloadUri = rs.getString(BUILD_TABLE + "." + BUILD_DOWNLOAD_URI);
        data.setDownloadUri(downloadUri);

        String jobUrl = rs.getString(BUILD_TABLE + "." + BUILD_JOB_URL);
        data.setJobUrl(jobUrl);

        String comments = rs.getString(BUILD_TABLE + "." + BUILD_COMMENTS);
        data.setComments(comments);

        CMnDbHostData host = CMnHostTable.parseHostData(rs);
        data.setHostData(host);

        return data;
    }


}

