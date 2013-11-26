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

/**
 * Provides methods for accessing data in the UIT data tables.
 * 
 * @author  Shawn Stafford, Karen An
 */
public class CMnUitTable extends CMnTestTable {

    /** Name of the column for the test name */
    public static final String TEST_NAME = "test_name";

    /** Name of the column for the number of steps processed in the UIT. */
    public static final String TEST_STEP_COUNT = "step_count";

    /** Name of the column for the number of passing steps in the UIT. */
    public static final String TEST_STEP_SUCCESSES = "success_count";

    /** Name of the column for the number of passing steps in the UIT. */
    public static final String TEST_STEP_FAILURES = "failure_count";


    /** Name of the column that identifies the application server */
    public static final String APP_SERVER = "app_server";

    /** Name of the column that identifies the web server */
    public static final String WEB_SERVER = "web_server";

    /** Name of the column that identifies the client */
    public static final String CLIENT = "client";

    /** Name of the column that identifies the application server of verification environment */
    public static final String APP_SERVER_VER = "app_server_ver";

    /** Name of the column that identifies the web server of verification environment */
    public static final String WEB_SERVER_VER = "web_server_ver";

    /** Name of the column that identifies the client of verification environment */
    public static final String CLIENT_VER = "client_ver";



    /** Name of the table used for individual UIT test steps */
    public static final String STEP_TABLE = "uit_step";

    /** Name of the column for the step primary key */
    public static final String STEP_ID = "step_id";

    /** Name of the column for the step primary key */
    public static final String STEP_NAME = "step_name";

    /** Name of the column for the step start time */
    public static final String STEP_START_DATE = "start_date";

    /** Name of the column for the step end time */
    public static final String STEP_END_DATE = "end_date";

    /** Name of the column for the step execution message. */
    public static final String STEP_MESSAGE = "message";

    /** Name of the column for the overall completion status of the UIT step. */
    public static final String STEP_STATUS = "status";

    public static final String STEP_STATUS_PASS = "PASS";
    public static final String STEP_STATUS_ERROR = "ERROR";
    public static final String STEP_STATUS_FAIL = "FAIL";


    /** Singleton instance of the table class */
    private static CMnUitTable instance;


    /**
     * Construct the abstract class and set the required table names.
     *
     * @param  suite  Suite table name
     * @param  test   Test table name
     */
    protected CMnUitTable(String suite, String test) {
        SUITE_TABLE = suite;
        TEST_TABLE = test;
    }


    /**
     * Return the singleton instance of the class.
     */
    public static CMnUitTable getInstance() {
        if (instance == null) {
            // Construct a new singleton instance
            instance = new CMnUitTable("uit_suite", "uit");

/*
            // Enable debbuging to a file
            String logfile = "/var/tmp/CMnUitTable.txt";
            try {
                instance.debugEnable(new PrintStream(logfile));
            } catch (FileNotFoundException nfex) {
                System.out.println("Unable to enable debugging.  Failed to open log file: " + logfile);
            }
*/

        }

        return instance;
    }



    /**
     * Store a UIT test result in the database.
     * 
     * @param   conn    Database connection
     * @param   suiteId Foreign key that links the test to the suite
     * @param   test    Test data 
     *
     * @return  Auto-generated key that identifies the test
     */
    public synchronized String addTest(
            Connection conn,
            String jdbcDriver,
            String suiteId,
            CMnDbUit test) 
        throws SQLException
    {
        boolean isOracle = jdbcDriver.indexOf("OracleDriver") >= 0;
        String testId = null;

        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO " + TEST_TABLE + " ");
        sql.append("(" + SUITE_ID);
        sql.append(", " + TEST_NAME);
        if (test.getStartTime() != null) sql.append(", " + TEST_START_DATE); 
        if (test.getEndTime()   != null) sql.append(", " + TEST_END_DATE);   
        sql.append(", " + TEST_STEP_COUNT); 
        sql.append(", " + TEST_STEP_FAILURES); 
        sql.append(", " + TEST_STEP_SUCCESSES); 
        sql.append(", " + TEST_MESSAGE);
        sql.append(", " + TEST_STATUS + ") ");

        sql.append("VALUES ");
        if (isOracle) {
            sql.append("(" + suiteId);
            sql.append(", " + test.getTestName());
            if (test.getStartTime() != null) sql.append(", " + DATETIME.format(test.getStartTime()) );
            if (test.getEndTime()   != null) sql.append(", " + DATETIME.format(test.getEndTime()) );
            sql.append(", " + test.getStepCount());
            sql.append(", " + test.getFailedStepCount());
            sql.append(", " + test.getSuccessfulStepCount());
            sql.append(", '" + escapeQueryText(test.getStepResults(), TEXT_SIZE) + "'");
            sql.append(", '" + getTestStatus(test) + "')");
        }
        else {
            // assume mysql
            sql.append("(\"" + suiteId + "\"");
            sql.append(", \"" + test.getTestName() + "\"");
            if (test.getStartTime() != null) sql.append(", \"" + DATETIME.format(test.getStartTime()) + "\"");
            if (test.getEndTime()   != null) sql.append(", \"" + DATETIME.format(test.getEndTime())   + "\"");
            sql.append(", \"" + test.getStepCount() + "\"");
            sql.append(", \"" + test.getFailedStepCount() + "\"");
            sql.append(", \"" + test.getSuccessfulStepCount() + "\"");
            sql.append(", \"" + escapeQueryText(test.getStepResults(), TEXT_SIZE) + "\"");
            sql.append(", \"" + getTestStatus(test) + "\")");
        }

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            getInstance().debugWrite("Attempting to execute: " + sql.toString());
            st.execute(sql.toString(), Statement.RETURN_GENERATED_KEYS);
            if (!isOracle) {
                //rs = st.getGeneratedKeys();
                rs = getGeneratedKeys(st);
                if (rs != null) {
                    rs.first();
                    testId = rs.getString(1);
                } else {
                     System.err.println("Unable to obtain generated key.");
                }
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
     * Retrieve a list of all unit tests associated with a particular suite ID.
     *
     * @param   conn    Database connection
     * @param   suiteId Suite ID used as the foreign key
     *
     * @return  List of UIT information objects
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
                CMnDbUit test = null;
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
     * Retrieve a list of all unit tests associated with a particular build ID.
     *
     * @param   conn    Database connection
     * @param   buildId Build ID used as the foreign key
     *
     * @return  List of UIT information objects
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
                CMnDbUit test = null;
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
     * Retrieve unit test information from the database.
     *
     * @param   conn    Database connection
     * @param   testId  Primary key used to locate the test info
     *
     * @return  Test information
     */
    public synchronized CMnDbUit getTest(
            Connection conn,
            String testId)
        throws SQLException
    {
        CMnDbUit test = null;

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
     * Retrieve unit test step information from the database.
     *
     * @param   conn    Database connection
     * @param   test    UIT test information 
     *
     * @return  Test information
     */
    public synchronized Vector getTestSteps(
            Connection conn,
            String testId)
        throws SQLException
    {
        Vector steps = new Vector();

        StringBuffer sql = new StringBuffer();
        sql.append(
            "SELECT * FROM " + STEP_TABLE +
            " WHERE " + TEST_ID + "=" + testId +
            " ORDER BY " + STEP_START_DATE + " DESC"
        );

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            getInstance().debugWrite("Attempting to execute: " + sql.toString());
            rs = st.executeQuery(sql.toString());
            if (rs != null) {
                CMnDbUitStep currentStep = null;
                while (rs.next()) {
                    currentStep = parseStepData(rs);
                    steps.add(currentStep);
                }

            } else {
                 System.err.println("Unable to obtain the step data.");
            }
        } catch (SQLException ex) {
            System.err.println("Failed to obtain step data: " + sql.toString());
            ex.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return steps;
    }




    /**
     * Retrieve all unit test results that are comparable to the 
     * specified test.  A comparable test is on that was executed
     * against the same product version, JVM, and OS. 
     *
     * @param   conn    Database connection
     * @param   test    Unit test information
     *
     * @return  List of all similar tests 
     */
    public synchronized Vector getTestHistory(
            Connection conn,
            CMnDbUit test)
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
            "SELECT * FROM " + TEST_TABLE + ", " + SUITE_TABLE + ", " + CMnBuildTable.BUILD_TABLE +
            " WHERE " + SUITE_TABLE + "." + SUITE_ID + " = " + TEST_TABLE + "." + SUITE_ID +
            " AND " + SUITE_TABLE + "." + CMnHostTable.HOST_NAME + " = '" + host.getHostname() + "'" +		// restrict verification stack (app server)
            " AND " + SUITE_TABLE + "." + CMnHostTable.JDK_VERSION + " = '" + host.getJdkVersion() + "'" +	
            " AND " + SUITE_TABLE + "." + CMnHostTable.JDK_VENDOR + " = '" + host.getJdkVendor() + "'" +
            " AND " + SUITE_TABLE + "." + CMnHostTable.OS_NAME + " = '" + host.getOSName() + "'" +
            " AND " + SUITE_TABLE + "." + CMnHostTable.OS_ARCHITECTURE + " = '" + host.getOSArchitecture() + "'" +
            " AND " + SUITE_TABLE + "." + CMnHostTable.OS_VERSION + " = '" + host.getOSVersion() + "'" +
            " AND " + TEST_TABLE + "." + TEST_NAME + " = '" + test.getTestName() + "'" +						// restrict UIT name 
            " AND " + CMnBuildTable.BUILD_TABLE + "." + CMnBuildTable.BUILD_ID + " = " +					
                      SUITE_TABLE + "." + CMnBuildTable.BUILD_ID +
            " AND " + CMnBuildTable.BUILD_TABLE + "." + CMnBuildTable.BUILD_VERSION + " LIKE '" + version + "'" +	// restrict vertical & version
            " ORDER BY " + TEST_TABLE + "." + TEST_START_DATE + " DESC");
        
        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            getInstance().debugWrite("Attempting to execute: " + sql.toString());
            rs = st.executeQuery(sql.toString());
            if (rs != null) {
                CMnDbUit currentTest = null;
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
     * Retrieve the unit test record for the last time the 
     * unit test passed.  If the unit test has never passed, 
     * a null will be returned. 
     *
     * @param   conn    Database connection
     * @param   test    Unit test information
     *
     * @return  Most recent matching test that passed 
     */
    public synchronized CMnDbUit getLastPass(
            Connection conn,
            CMnDbUit test)
        throws SQLException
    {
        CMnDbUit data = null; 

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
	        " WHERE " + SUITE_TABLE + "." + SUITE_ID + " = " + TEST_TABLE + "." + SUITE_ID +
	        " AND " + SUITE_TABLE + "." + CMnHostTable.HOST_NAME + " = '" + host.getHostname() + "'" +		// restrict verification stack (app server)
	        " AND " + SUITE_TABLE + "." + CMnHostTable.JDK_VERSION + " = '" + host.getJdkVersion() + "'" +	
	        " AND " + SUITE_TABLE + "." + CMnHostTable.JDK_VENDOR + " = '" + host.getJdkVendor() + "'" +
	        " AND " + SUITE_TABLE + "." + CMnHostTable.OS_NAME + " = '" + host.getOSName() + "'" +
	        " AND " + SUITE_TABLE + "." + CMnHostTable.OS_ARCHITECTURE + " = '" + host.getOSArchitecture() + "'" +
	        " AND " + SUITE_TABLE + "." + CMnHostTable.OS_VERSION + " = '" + host.getOSVersion() + "'" +
	        " AND " + TEST_TABLE + "." + TEST_NAME + " = '" + test.getTestName() + "'" +						// restrict UIT name 
	        " AND " + TEST_TABLE + "." + TEST_STATUS + " = '" + STATUS_PASS + "'" +		// restrict verification stack (app server)
	        " AND " + TEST_TABLE + "." + TEST_START_DATE + " < '" + DATETIME.format(test.getStartTime()) + "'" + 
	        " AND " + CMnBuildTable.BUILD_TABLE + "." + CMnBuildTable.BUILD_ID + " = " +					
	                  SUITE_TABLE + "." + CMnBuildTable.BUILD_ID +
	        " AND " + CMnBuildTable.BUILD_TABLE + "." + CMnBuildTable.BUILD_VERSION + " LIKE '" + version + "'" +	// restrict vertical & version
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
                 System.err.println("Unable to obtain the last time the test passed:"+ TEST_ID + "=" + test.getId());
                 data = new CMnDbUit();
            }
        } catch (SQLException ex) {
            System.err.println("Failed to obtain the most recently passing test instance.");
            ex.printStackTrace();
            data = new CMnDbUit();
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return data;
    }


    /**
     * Retrieve the unit test record for the last time the 
     * unit test passed.  If the unit test has never passed, 
     * a null will be returned. 
     *
     * @param   conn    Database connection
     * @param   test    Unit test information
     *
     * @return  Most recent matching test that passed 
     */
    public synchronized CMnDbUit getLastFailure(
            Connection conn,
            CMnDbUit test)
        throws SQLException
    {
        CMnDbUit data = null; 

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
	        " WHERE " + SUITE_TABLE + "." + SUITE_ID + " = " + TEST_TABLE + "." + SUITE_ID +
	        " AND " + SUITE_TABLE + "." + CMnHostTable.HOST_NAME + " = '" + host.getHostname() + "'" +		// restrict verification stack (app server)
	        " AND " + SUITE_TABLE + "." + CMnHostTable.JDK_VERSION + " = '" + host.getJdkVersion() + "'" +	
	        " AND " + SUITE_TABLE + "." + CMnHostTable.JDK_VENDOR + " = '" + host.getJdkVendor() + "'" +
	        " AND " + SUITE_TABLE + "." + CMnHostTable.OS_NAME + " = '" + host.getOSName() + "'" +
	        " AND " + SUITE_TABLE + "." + CMnHostTable.OS_ARCHITECTURE + " = '" + host.getOSArchitecture() + "'" +
	        " AND " + SUITE_TABLE + "." + CMnHostTable.OS_VERSION + " = '" + host.getOSVersion() + "'" +
	        " AND " + TEST_TABLE + "." + TEST_NAME + " = '" + test.getTestName() + "'" +						// restrict UIT name 
	        " AND " + TEST_TABLE + "." + TEST_STATUS + " = '" + STATUS_FAIL + "'" +		// restrict verification stack (app server)
	        " AND " + TEST_TABLE + "." + TEST_START_DATE + " < '" + DATETIME.format(test.getStartTime()) + "'" + 
	        " AND " + CMnBuildTable.BUILD_TABLE + "." + CMnBuildTable.BUILD_ID + " = " +					
	                  SUITE_TABLE + "." + CMnBuildTable.BUILD_ID +
	        " AND " + CMnBuildTable.BUILD_TABLE + "." + CMnBuildTable.BUILD_VERSION + " LIKE '" + version + "'" +	// restrict vertical & version
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
                 System.err.println("Unable to obtain the last time the test failed:"+ TEST_ID + "=" + test.getId());
                 data = new CMnDbUit();
            }
        } catch (SQLException ex) {
            System.err.println("Failed to obtain the most recently failing test instance.");
            ex.printStackTrace();
            data = new CMnDbUit();
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
        CMnDbTestSuite data = new CMnDbTestSuite(CMnDbTestSuite.SuiteType.UIT);
        parseSuiteData(rs, data);

        String options = rs.getString(SUITE_OPTIONS);
        data.setOptions(parseOptions(options));

        return data;
    }

    /**
     * Parse the result set to obtain test information.
     *
     * @param   rs    Result set containing test data
     *
     * @return  Unit Test information
     */
    public CMnDbUit parseTestData(ResultSet rs)
        throws SQLException
    {
        CMnDbUit data = new CMnDbUit();
        parseTestData(rs, data);

        String name = rs.getString(TEST_TABLE + "." + TEST_NAME);
        data.setTestName(name);
        
        String stepCount = rs.getString(TEST_TABLE + "." + TEST_STEP_COUNT);
        // data.setStepCount(stepCount);

        String stepSuccess = rs.getString(TEST_TABLE + "." + TEST_STEP_SUCCESSES);
        // data.setStepCount(stepSuccess);

        String stepFailure = rs.getString(TEST_TABLE + "." + TEST_STEP_FAILURES);
        // data.setStepCount(stepFailure);

        return data;
    }

    /**
     * Parse the result set to obtain test step  information.
     *
     * @param   rs    Result set containing step data
     *
     * @return  Unit Test step information
     */
    public CMnDbUitStep parseStepData(ResultSet rs)
        throws SQLException
    {
        CMnDbUitStep data = new CMnDbUitStep();

        int id = rs.getInt(STEP_TABLE + "." + STEP_ID);
        data.setId(id);

        int parentId = rs.getInt(STEP_TABLE + "." + TEST_ID);
        data.setParentId(parentId);

        String name = rs.getString(STEP_TABLE + "." + STEP_NAME);
        data.setStepName(name);

        Date startTime = rs.getTimestamp(STEP_TABLE + "." + STEP_START_DATE);
        data.setStartTime(startTime);

        Date endTime = rs.getTimestamp(STEP_TABLE + "." + STEP_END_DATE);
        data.setEndTime(endTime);

        String message = rs.getString(STEP_TABLE + "." + STEP_MESSAGE);
        data.addMessage(message);

        int status = getTestStatus(rs.getString(STEP_TABLE + "." + STEP_STATUS));
        data.setStatus(status);

        return data;
    }



}
