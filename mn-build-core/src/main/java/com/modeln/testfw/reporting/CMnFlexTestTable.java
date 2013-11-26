
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
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;
import java.text.SimpleDateFormat;

/**
 * The flex test table provides an interface to flex test data.
 * 
 * @author  Shawn Stafford
 */
public class CMnFlexTestTable extends CMnTestTable {


    /** Name of the column for the test class */
    public static final String TEST_CLASS = "class";

    /** Name of the column for the test method */
    public static final String TEST_METHOD = "method";



    /** Singleton instance of the table class */
    private static CMnFlexTestTable instance;


    /**
     * Construct the abstract class and set the required table names.
     *
     * @param  suite  Suite table name
     * @param  test   Test table name
     */
    protected CMnFlexTestTable(String suite, String test) {
        SUITE_TABLE = suite;
        TEST_TABLE = test;
    }


    /**
     * Return the singleton instance of the class.
     */
    public static CMnFlexTestTable getInstance() {
        if (instance == null) {
            // Construct a new singleton instance
            instance = new CMnFlexTestTable("flextest_suite", "flextest");

            // Enable debbuging to a file
            String logfile = "/var/tmp/CMnFlexTestTable.txt";
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
     * Store a flex test result in the database.
     * 
     * @param   conn    Database connection
     * @param   suiteId Foreign key that links the test to the suite
     * @param   test    Test data 
     *
     * @return  Auto-generated key that identifies the test
     */
    public synchronized String addTest(
            Connection conn, 
            String suiteId, 
            CMnDbFlexTestData test) 
        throws SQLException
    {
        String testId = null;

        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO " + TEST_TABLE + " ");
        sql.append("(" + SUITE_ID);
        if (test.getStartTime() != null) sql.append(", " + TEST_START_DATE); 
        if (test.getEndTime()   != null) sql.append(", " + TEST_END_DATE);   
        sql.append(", " + TEST_CLASS); 
        sql.append(", " + TEST_METHOD);
        sql.append(", " + TEST_MESSAGE); 
        sql.append(", " + TEST_STATUS + ") ");

        sql.append("VALUES ");
        sql.append("(\"" + suiteId + "\"");
        if (test.getStartTime() != null) sql.append(", \"" + DATETIME.format(test.getStartTime()) + "\"");
        if (test.getEndTime()   != null) sql.append(", \"" + DATETIME.format(test.getEndTime())   + "\"");
        sql.append(", \"" + test.getClassName() + "\"");
        sql.append(", \"" + test.getMethodName() + "\"");
        sql.append(", \"" + escapeQueryText(test.getMessage(), TEXT_SIZE) + "\"");
        sql.append(", \"" + getTestStatus(test) + "\")");

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            getInstance().debugWrite("Attempting to execute: " + sql.toString());
            st.execute(sql.toString(), Statement.RETURN_GENERATED_KEYS);
            //rs = st.getGeneratedKeys();
            rs = getGeneratedKeys(st);
            if (rs != null) {
                rs.first();
                testId = rs.getString(1);
            } else {
                 System.err.println("Unable to obtain generated key.");
            }
        } catch (SQLException ex) {
            System.err.println("Failed to add test.");
            System.err.println("SQL Query: " + sql.toString());
            ex.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return testId;
    }



    /**
     * Retrieve a list of all flex tests associated with a particular suite ID.
     *
     * @param   conn    Database connection
     * @param   suiteId Suite ID used as the foreign key
     *
     * @return  List of flex test information objects
     */
    public synchronized Vector getTestsBySuite(
            Connection conn,
            String suiteId)
        throws SQLException
    {
        Vector list = new Vector();

        StringBuffer sql = new StringBuffer();
        sql.append(
            "SELECT * FROM " + TEST_TABLE + 
            " WHERE " + SUITE_ID + "=" + suiteId +
            " ORDER BY " + TEST_START_DATE + " DESC"
        );

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            getInstance().debugWrite("Attempting to execute: " + sql.toString());
            rs = st.executeQuery(sql.toString());
            if (rs != null) {
                CMnDbFlexTestData test = null;
                while (rs.next()) {
                    test = parseTestData(rs);
                    list.add(test);
                }
            } else {
                 System.err.println("Unable to obtain the suite data.");
            }
        } catch (SQLException ex) {
            System.err.println("Failed to obtain suite data.");
            ex.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return list;
    }

    /**
     * Retrieve a list of all flex tests associated with a particular build ID.
     *
     * @param   conn    Database connection
     * @param   buildId Build ID used as the foreign key
     *
     * @return  List of flex test information objects
     */
    public synchronized Vector getTestsByBuild(
            Connection conn,
            String buildId)
        throws SQLException
    {
        Vector list = new Vector();

        StringBuffer sql = new StringBuffer();
        sql.append(
            "SELECT * FROM " + TEST_TABLE + ", " + SUITE_TABLE +
            " WHERE " + SUITE_TABLE + "." + SUITE_ID + "=" + TEST_TABLE + "." + SUITE_ID +
            "   AND " + SUITE_TABLE + "." + CMnBuildTable.BUILD_ID + "=" + buildId +
            " ORDER BY " + TEST_TABLE + "." + TEST_START_DATE + " DESC"
        );

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            getInstance().debugWrite("Attempting to execute: " + sql.toString());
            rs = st.executeQuery(sql.toString());
            if (rs != null) {
                CMnDbFlexTestData test = null;
                while (rs.next()) {
                    test = parseTestData(rs);
                    list.add(test);
                }
            } else {
                 System.err.println("Unable to obtain the suite data.");
            }
        } catch (SQLException ex) {
            System.err.println("Failed to obtain suite data.");
            ex.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return list;
    }


    /**
     * Retrieve flex test information from the database.
     *
     * @param   conn    Database connection
     * @param   testId  Primary key used to locate the test info
     *
     * @return  Test information
     */
    public synchronized CMnDbFlexTestData getTest(
            Connection conn,
            String testId)
        throws SQLException
    {
        CMnDbFlexTestData test = null;

        StringBuffer sql = new StringBuffer();
        sql.append(
            "SELECT * FROM " + TEST_TABLE + 
            " WHERE " + TEST_ID + "=" + testId +
            " ORDER BY " + TEST_START_DATE + " DESC"
        );

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            getInstance().debugWrite("Attempting to execute: " + sql.toString());
            rs = st.executeQuery(sql.toString());
            if (rs != null) {
                rs.first();
                test = parseTestData(rs);
            } else {
                 System.err.println("Unable to obtain the suite data.");
            }
        } catch (SQLException ex) {
            System.err.println("Failed to obtain suite data: " + sql.toString());
            ex.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return test;
    }



    /**
     * Retrieve all flex test results that are comparable to the 
     * specified test.  A comparable test is on that was executed
     * against the same product version, JVM, and OS. 
     *
     * @param   conn    Database connection
     * @param   test    Flex test information
     *
     * @return  List of all similar tests 
     */
    public synchronized Vector getTestHistory(
            Connection conn,
            CMnDbFlexTestData test)
        throws SQLException
    {
        Vector history = new Vector();

        // Obtain the test suite containing the current test
        CMnDbTestSuite testSuite = getSuite(conn, Integer.toString(test.getParentId()));
        CMnDbHostData host = testSuite.getHostData();

        // Obtain the build that contained the test suite
        CMnDbBuildData build = CMnBuildTable.getBuild(conn, Integer.toString(testSuite.getParentId()));
        String version = build.getBuildVersion();

        // Replace the timestamp in the version string with a meta character that will
        // be used to search for all builds that match this version
        version = version.replaceAll(CMnBuildTable.TIMESTAMP_REGEX, "%");

        StringBuffer sql = new StringBuffer();
        sql.append(
            "SELECT " + TEST_TABLE + "." + TEST_ID + ", " +
                        TEST_TABLE + "." + SUITE_ID + ", " +
                        TEST_TABLE + "." + TEST_START_DATE + ", " +
                        TEST_TABLE + "." + TEST_END_DATE + ", " +
                        TEST_TABLE + "." + TEST_CLASS + ", " +
                        TEST_TABLE + "." + TEST_METHOD + ", " +
                        TEST_TABLE + "." + TEST_MESSAGE + ", " +
                        TEST_TABLE + "." + TEST_STATUS + 
            " FROM " + TEST_TABLE + ", " + SUITE_TABLE + ", " + CMnBuildTable.BUILD_TABLE +
            " WHERE " + TEST_TABLE + "." + TEST_CLASS + " = '" + test.getClassName() + "'" +
            " AND " + TEST_TABLE + "." + TEST_METHOD + " = '" + test.getMethodName() + "'" +
            " AND " + SUITE_TABLE + "." + SUITE_ID + " = " + TEST_TABLE + "." + SUITE_ID +
            " AND " + SUITE_TABLE + "." + CMnHostTable.JDK_VERSION + " = '" + host.getJdkVersion() + "'" +
            " AND " + SUITE_TABLE + "." + CMnHostTable.JDK_VENDOR + " = '" + host.getJdkVendor() + "'" +
            " AND " + SUITE_TABLE + "." + CMnHostTable.OS_NAME + " = '" + host.getOSName() + "'" +
            " AND " + SUITE_TABLE + "." + CMnHostTable.OS_ARCHITECTURE + " = '" + host.getOSArchitecture() + "'" +
            " AND " + SUITE_TABLE + "." + CMnHostTable.OS_VERSION + " = '" + host.getOSVersion() + "'" +
            " AND " + CMnBuildTable.BUILD_TABLE + "." + CMnBuildTable.BUILD_ID + " = " +
                      SUITE_TABLE + "." + CMnBuildTable.BUILD_ID +
            " AND " + CMnBuildTable.BUILD_TABLE + "." + CMnBuildTable.BUILD_VERSION + " LIKE '" + version + "'" +
            " ORDER BY " + TEST_TABLE + "." + TEST_START_DATE + " DESC");

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            getInstance().debugWrite("Attempting to execute: " + sql.toString());
            rs = st.executeQuery(sql.toString());
            if (rs != null) {
                CMnDbFlexTestData currentTest = null;
                while (rs.next()) {
                    currentTest = parseTestData(rs);
                    history.add(currentTest);
                }
            } else {
                 System.err.println("Unable to obtain the test history.");
            }
        } catch (SQLException ex) {
            System.err.println("Failed to obtain the test history.");
            ex.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return history;
    }



    /**
     * Retrieve the flex test record for the last time the 
     * flex test passed.  If the flex test has never passed, 
     * a null will be returned. 
     *
     * @param   conn    Database connection
     * @param   test    Flex test information
     *
     * @return  Most recent matching test that passed 
     */
    public synchronized CMnDbFlexTestData getLastPass(
            Connection conn,
            CMnDbFlexTestData test)
        throws SQLException
    {
        CMnDbFlexTestData data = null; 

        // Obtain the test suite containing the current test
        CMnDbTestSuite testSuite = getSuite(conn, Integer.toString(test.getParentId()));
        CMnDbHostData host = testSuite.getHostData();

        // Obtain the build that contained the test suite
        CMnDbBuildData build = CMnBuildTable.getBuild(conn, Integer.toString(testSuite.getParentId()));
        String version = build.getBuildVersion();

        // Replace the timestamp in the version string with a meta character that will 
        // be used to search for all builds that match this version 
        version = version.replaceAll(CMnBuildTable.TIMESTAMP_REGEX, "%");

        StringBuffer sql = new StringBuffer();
        sql.append(
            "SELECT * FROM " + TEST_TABLE + ", " + SUITE_TABLE + ", " + CMnBuildTable.BUILD_TABLE +
            " WHERE " + TEST_TABLE + "." + TEST_CLASS + " = '" + test.getClassName() + "'" + 
            " AND " + TEST_TABLE + "." + TEST_METHOD + " = '" + test.getMethodName() + "'" + 
            " AND " + TEST_TABLE + "." + TEST_STATUS + " = '" + STATUS_PASS + "'" +
            " AND " + SUITE_TABLE + "." + SUITE_ID + " = " + TEST_TABLE + "." + SUITE_ID +
            " AND " + SUITE_TABLE + "." + CMnHostTable.JDK_VERSION + " = '" + host.getJdkVersion() + "'" + 
            " AND " + SUITE_TABLE + "." + CMnHostTable.JDK_VENDOR + " = '" + host.getJdkVendor() + "'" +
            " AND " + SUITE_TABLE + "." + CMnHostTable.OS_NAME + " = '" + host.getOSName() + "'" +
            " AND " + SUITE_TABLE + "." + CMnHostTable.OS_ARCHITECTURE + " = '" + host.getOSArchitecture() + "'" +
            " AND " + SUITE_TABLE + "." + CMnHostTable.OS_VERSION + " = '" + host.getOSVersion() + "'" +
            " AND " + CMnBuildTable.BUILD_TABLE + "." + CMnBuildTable.BUILD_ID + " = " + 
                      SUITE_TABLE + "." + CMnBuildTable.BUILD_ID + 
            " AND " + CMnBuildTable.BUILD_TABLE + "." + CMnBuildTable.BUILD_VERSION + " LIKE '" + version + "'" + 
            " AND " + TEST_TABLE + "." + TEST_START_DATE + " < '" + DATETIME.format(test.getStartTime()) + "'" + 
            " ORDER BY " + TEST_TABLE + "." + TEST_START_DATE + " DESC" +
            " LIMIT 1");

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            getInstance().debugWrite("Attempting to execute: " + sql.toString());
            rs = st.executeQuery(sql.toString());
            if (rs != null) {
                rs.first();
                data = parseTestData(rs); 
            } else {
                 System.err.println(
                     "Unable to obtain the last time the test passed: " +
                     TEST_ID + "=" + test.getId());
            }
        } catch (SQLException ex) {
            System.err.println("Failed to obtain the most recently passing test instance.");
            ex.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return data;
    }



    /**
     * Retrieve the flex test record for the last time the
     * flex test did not pass (includes failures and errors).  
     * If the flex test has never failed, a null will be returned.
     *
     * @param   conn    Database connection
     * @param   test    Flex test information
     *
     * @return  Most recent matching test that failed 
     */
    public synchronized CMnDbFlexTestData getLastFailure(
            Connection conn,
            CMnDbFlexTestData test)
        throws SQLException
    {
        CMnDbFlexTestData data = null;

        // Obtain the test suite containing the current test
        CMnDbTestSuite testSuite = getSuite(conn, Integer.toString(test.getParentId()));
        CMnDbHostData host = testSuite.getHostData();

        // Obtain the build that contained the test suite
        CMnDbBuildData build = CMnBuildTable.getBuild(conn, Integer.toString(testSuite.getParentId()));
        String version = build.getBuildVersion();

        // Replace the timestamp in the version string with a meta character that will
        // be used to search for all builds that match this version
        version = version.replaceAll(CMnBuildTable.TIMESTAMP_REGEX, "%");

        StringBuffer sql = new StringBuffer();
        sql.append(
            "SELECT * FROM " + TEST_TABLE + ", " + SUITE_TABLE + ", " + CMnBuildTable.BUILD_TABLE + 
            " WHERE " + TEST_TABLE + "." + TEST_CLASS + " = '" + test.getClassName() + "'" +
            " AND " + TEST_TABLE + "." + TEST_METHOD + " = '" + test.getMethodName() + "'" +
            " AND " + TEST_TABLE + "." + TEST_STATUS + " != '" + STATUS_PASS + "'" +
            " AND " + SUITE_TABLE + "." + SUITE_ID + " = " + TEST_TABLE + "." + SUITE_ID +
            " AND " + SUITE_TABLE + "." + CMnHostTable.JDK_VERSION + " = '" + host.getJdkVersion() + "'" +
            " AND " + SUITE_TABLE + "." + CMnHostTable.JDK_VENDOR + " = '" + host.getJdkVendor() + "'" +
            " AND " + SUITE_TABLE + "." + CMnHostTable.OS_NAME + " = '" + host.getOSName() + "'" +
            " AND " + SUITE_TABLE + "." + CMnHostTable.OS_ARCHITECTURE + " = '" + host.getOSArchitecture() + "'" +
            " AND " + SUITE_TABLE + "." + CMnHostTable.OS_VERSION + " = '" + host.getOSVersion() + "'" +
            " AND " + CMnBuildTable.BUILD_TABLE + "." + CMnBuildTable.BUILD_ID + " = " +
                      SUITE_TABLE + "." + CMnBuildTable.BUILD_ID +
            " AND " + CMnBuildTable.BUILD_TABLE + "." + CMnBuildTable.BUILD_VERSION + " LIKE '" + version + "'" +
            " AND " + TEST_TABLE + "." + TEST_START_DATE + " < '" + DATETIME.format(test.getStartTime()) + "'" +
            " ORDER BY " + TEST_TABLE + "." + TEST_START_DATE + " DESC" +
            " LIMIT 1");

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            getInstance().debugWrite("Attempting to execute: " + sql.toString());
            rs = st.executeQuery(sql.toString());
            if (rs != null) {
                rs.first();
                data = parseTestData(rs);
            } else {
                 System.err.println(
                     "Unable to obtain the last time the test failed: " +
                     TEST_ID + "=" + test.getId());
            }
        } catch (SQLException ex) {
            System.err.println("Failed to obtain the most recently failing test instance.");
            ex.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return data;
    }





    /**
     * Parse the result set to obtain suite information.
     * 
     * @param   rs    Result set containing suite data
     *
     * @return  Suite information
     */
    public CMnDbTestSuite parseSuiteData(ResultSet rs) 
        throws SQLException 
    {
        CMnDbTestSuite data = new CMnDbTestSuite(CMnDbTestSuite.SuiteType.FLEX);
        parseSuiteData(rs, data);
        return data;
    }

    /**
     * Parse the result set to obtain test information.
     *
     * @param   rs    Result set containing test data
     *
     * @return  Flex Test information
     */
    public CMnDbFlexTestData parseTestData(ResultSet rs)
        throws SQLException
    {
        CMnDbFlexTestData data = new CMnDbFlexTestData();
        parseTestData(rs, data);

        String classname = rs.getString(TEST_CLASS);
        data.setClassName(classname);

        String methodname = rs.getString(TEST_METHOD);
        data.setMethodName(methodname);

        return data;
    }



}

