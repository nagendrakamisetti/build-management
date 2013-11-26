
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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;
import java.text.SimpleDateFormat;

/**
 * The ACT test table provides an interface to ACT test data.
 *
 * @author  Shawn Stafford
 */
public class CMnAcceptanceTestTable extends CMnTestTable {


    /** Name of the column for the test script file */
    public static final String TEST_FILE = "filename";

    /** Name of the column for the test description */
    public static final String TEST_SUMMARY = "summary";

    /** Name of the column for the test author */
    public static final String TEST_AUTHOR = "author";




    /** Name of the table used for mapping ACT tests to agile stories */
    public static final String STORY_TABLE = "act_story_map";

    /** Name of the column for the ACT ID */
    public static final String STORY_ACT = "test_id";

    /** Name of the column for the story name */
    public static final String STORY_NAME = "story";


    /** Name of the table used for mapping ACT tests to QA test cases */
    public static final String TESTCASE_TABLE = "act_testcase_map";

    /** Name of the column for the ACT ID */
    public static final String TESTCASE_ACT = "test_id";

    /** Name of the column for the story name */
    public static final String TESTCASE_NAME = "testcase";




    /** Singleton instance of the table class */
    private static CMnAcceptanceTestTable instance;


    /**
     * Construct the abstract class and set the required table names.
     *
     * @param  suite  Suite table name
     * @param  test   Test table name
     */
    protected CMnAcceptanceTestTable(String suite, String test) {
        SUITE_TABLE = suite;
        TEST_TABLE = test;
    }


    /**
     * Return the singleton instance of the class.
     */
    public static CMnAcceptanceTestTable getInstance() {
        if (instance == null) {
            instance = new CMnAcceptanceTestTable("act_suite", "act");

            // Make sure the TEST_TABLE.TEST_GROUP_NAME column is used for ACT tests
            instance.enableTestGroupName(true);

            // Enable debbuging to a file
            String logfile = "/var/tmp/CMnAcceptanceTestTable.txt";
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
     * Store an ACT test result in the database.
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
            CMnDbAcceptanceTestData test)
        throws SQLException
    {
        String testId = null;

        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO " + TEST_TABLE + " ");
        sql.append("(" + SUITE_ID);
        if (test.getStartTime() != null) sql.append(", " + TEST_START_DATE);
        if (test.getEndTime()   != null) sql.append(", " + TEST_END_DATE);
        if ((test.getGroupName() != null) && (test.getGroupName().length() > 0)) {
            sql.append(", " + TEST_GROUP_NAME);
        }
        sql.append(", " + TEST_FILE);
        sql.append(", " + TEST_SUMMARY);
        sql.append(", " + TEST_AUTHOR);
        sql.append(", " + TEST_MESSAGE);
        sql.append(", " + TEST_STATUS + ") ");

        sql.append("VALUES ");
        sql.append("(\"" + suiteId + "\"");
        if (test.getStartTime() != null) sql.append(", \"" + DATETIME.format(test.getStartTime()) + "\"");
        if (test.getEndTime()   != null) sql.append(", \"" + DATETIME.format(test.getEndTime())   + "\"");
        if ((test.getGroupName() != null) && (test.getGroupName().length() > 0)) {
            sql.append(", \"" + test.getGroupName() + "\"");
        }
        sql.append(", \"" + test.getScriptName() + "\"");
        sql.append(", \"" + escapeQueryText(test.getSummary(), TEXT_SIZE) + "\"");
        sql.append(", \"" + test.getAuthor() + "\"");
        sql.append(", \"" + escapeQueryText(test.getMessage(), TEXT_SIZE) + "\"");
        sql.append(", \"" + getTestStatus(test) + "\")");

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            executeInsert(st, "addTest", sql.toString());
            //st.execute(sql.toString(), Statement.RETURN_GENERATED_KEYS);
            //rs = st.getGeneratedKeys();
            rs = getGeneratedKeys(st);
            if (rs != null) {
                rs.first();
                testId = rs.getString(1);

                // Add the mapping between stories and test ID
                Enumeration stories = test.getStories();
                while ((stories != null) && stories.hasMoreElements()) {
                    String story = (String) stories.nextElement();
                    try {
                        // Only add the story if the string is not null or blank
                        if (story != null) {
                            story = story.trim();
                            if (story.length() > 0) {
                                addStory(conn, testId, story); 
                            }
                         }
                     } catch (SQLException exStory) {
                        System.err.println("Unable to add story: testId = " + testId + ", story = " + story);
                     }
                }

                // Add the mapping between testcase and test ID
                Enumeration testcases = test.getTestCases();
                while ((testcases != null) && testcases.hasMoreElements()) {
                    String testcase = (String) testcases.nextElement();
                    try {
                        // Only add the testcase if the string is not null or blank
                        if (testcase != null) {
                            testcase = testcase.trim();
                            if (testcase.length() > 0) {
                                addTestCase(conn, testId, testcase);
                            }
                        }
                     } catch (SQLException exTestCase) {
                        System.err.println("Unable to add test case: testId = " + testId + ", testcase = " + testcase);
                     }
                }
 
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
     * Update an ACT test result in the database.
     *
     * @param   conn    Database connection
     * @param   test    Test data
     *
     * @return  TRUE if the test data was updated 
     */
    public synchronized boolean updateTest(
            Connection conn,
            CMnDbAcceptanceTestData test)
        throws SQLException
    {
        boolean result = false;

        StringBuffer sql = new StringBuffer();
        sql.append("UPDATE " + TEST_TABLE + " SET ");
        sql.append(SUITE_ID + "=\"" + test.getParentId() + "\"");
        if ((test.getGroupName() != null) && (test.getGroupName().length() > 0)) {
            sql.append(", " + TEST_GROUP_NAME + "=\"" + test.getGroupName() + "\"");
        }
        if (test.getStartTime() != null) sql.append(", " + TEST_START_DATE + "=\"" + DATETIME.format(test.getStartTime()) + "\"");
        if (test.getEndTime()   != null) sql.append(", " + TEST_END_DATE   + "=\"" + DATETIME.format(test.getEndTime()) + "\"");
        sql.append(", " + TEST_FILE    + "=\"" + test.getScriptName() + "\"");
        sql.append(", " + TEST_SUMMARY + "=\"" + escapeQueryText(test.getSummary(), TEXT_SIZE) + "\"");
        sql.append(", " + TEST_AUTHOR  + "=\"" + test.getAuthor() + "\"");
        sql.append(", " + TEST_MESSAGE + "=\"" + escapeQueryText(test.getMessage(), TEXT_SIZE) + "\"");
        sql.append(", " + TEST_STATUS  + "=\"" + getTestStatus(test) + "\"");

        sql.append(" WHERE " + TEST_ID + "=\"" + test.getId() + "\"");

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            execute(st, "updateTest", sql.toString());
            result = true;
        } catch (SQLException ex) {
            System.err.println("Failed to update test: " + sql.toString());
            ex.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }


        return result;
    }



    /**
     * Store a mapping between an ACT and agile story in the database.
     *
     * @param   conn    Database connection
     * @param   testId  Foreign key that links the test to the story
     * @param   story   Story name 
     */
    public synchronized void addStory(
            Connection conn,
            String testId,
            String story)
        throws SQLException
    {
        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO " + STORY_TABLE + " ");
        sql.append("(" + STORY_ACT);
        sql.append(", " + STORY_NAME + ") ");

        sql.append("VALUES ");
        sql.append("(\"" + testId + "\"");
        sql.append(", \"" + story + "\")");

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            getInstance().debugWrite("Attempting to execute: " + sql.toString());
            st.execute(sql.toString());
        } catch (SQLException ex) {
            System.err.println("Failed to add story.");
            System.err.println("SQL Query: " + sql.toString());
            ex.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }


    }


    /**
     * Store a mapping between an ACT and test case in the database.
     *
     * @param   conn    Database connection
     * @param   testId  Foreign key that links the test to the story
     * @param   testcase   Test case name 
     */
    public synchronized void addTestCase(
            Connection conn,
            String testId,
            String testcase)
        throws SQLException
    {
        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO " + TESTCASE_TABLE + " ");
        sql.append("(" + TESTCASE_ACT);
        sql.append(", " + TESTCASE_NAME + ") ");

        sql.append("VALUES ");
        sql.append("(\"" + testId + "\"");
        sql.append(", \"" + testcase + "\")");

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            getInstance().debugWrite("Attempting to execute: " + sql.toString());
            st.execute(sql.toString());
        } catch (SQLException ex) {
            System.err.println("Failed to add testcase.");
            System.err.println("SQL Query: " + sql.toString());
            ex.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }


    }


    /**
     * Retrieve a list of all ACT tests associated with a particular suite ID.
     *
     * @param   conn    Database connection
     * @param   suiteId Suite ID used as the foreign key
     *
     * @return  List of ACT test information objects
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
            rs = getInstance().executeQuery(st, "getTestsBySuite", sql.toString());
            if (rs != null) {
                CMnDbAcceptanceTestData test = null;
                while (rs.next()) {
                    test = parseTestData(rs);
                    list.add(test);
                }
            } else {
                 System.err.println("Unable to obtain the test list data.");
            }
        } catch (SQLException ex) {
            System.err.println("Failed to obtain test list data.");
            ex.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return list;
    }

    /**
     * Retrieve a list of all unit tests associated with a particular suite group.
     *
     * @param   conn    Database connection
     * @param   groupId Suite group ID used as the foreign key
     *
     * @return  List of unittest information objects
     */
    public synchronized Vector getTestsBySuiteGroup(
            Connection conn,
            String groupId)
        throws SQLException
    {
        Vector list = new Vector();

        StringBuffer sql = new StringBuffer();
        sql.append(
            "SELECT * FROM " + SUITE_TABLE + ", " + TEST_TABLE +
            " WHERE " + SUITE_TABLE + "." + SUITE_GROUP_ID + "=" + groupId +
            " AND " + SUITE_TABLE + "." + SUITE_ID + "=" + TEST_TABLE + "." + SUITE_ID +
            " ORDER BY " + TEST_TABLE + "." + TEST_START_DATE + " DESC"
        );

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            rs = getInstance().executeQuery(st, "getTestsBySuiteGroup", sql.toString());
            if (rs != null) {
                CMnDbAcceptanceTestData test = null;
                while (rs.next()) {
                    test = parseTestData(rs);
                    list.add(test);
                }
            } else {
                 System.err.println("Unable to obtain the test list by group ID.");
            }
        } catch (SQLException ex) {
            System.err.println("Failed to obtain test list by group ID.");
            ex.printStackTrace();
        // Exception handling for the query debugging
        //} catch (FileNotFoundException fex) {
        //    System.err.println("Failed to open debug file.");
        //    fex.printStackTrace();

        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return list;
    }


    /**
     * Retrieve a list of all unit tests associated with a particular suite group.
     *
     * @param   conn    Database connection
     * @param   buildId Build ID used as the foreign key
     * @param   groupName Suite group name used as the foreign key
     *
     * @return  List of unittest information objects
     */
    public synchronized Vector getTestsByGroupName(
            Connection conn,
            String buildId,
            String groupName)
        throws SQLException
    {
        Vector list = new Vector();

        StringBuffer sql = new StringBuffer();
        sql.append(
            "SELECT * FROM " + SUITE_TABLE + ", " + TEST_TABLE +
            " WHERE " + SUITE_TABLE + "." + SUITE_GROUP_NAME + "='" + groupName + "'" +
            "   AND " + SUITE_TABLE + "." + CMnBuildTable.BUILD_ID + "=" + buildId +
            "   AND " + SUITE_TABLE + "." + SUITE_ID + "=" + TEST_TABLE + "." + SUITE_ID +
            " ORDER BY " + TEST_TABLE + "." + TEST_START_DATE + " DESC"
        );
        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            rs = getInstance().executeQuery(st, "getTestsByGroupName", sql.toString());
            if (rs != null) {
                CMnDbAcceptanceTestData test = null;
                while (rs.next()) {
                    test = parseTestData(rs);
                    list.add(test);
                }
            } else {
                 System.err.println("Unable to obtain the test list by group name.");
            }
        } catch (SQLException ex) {
            System.err.println("Failed to obtain test list by group name.");
            ex.printStackTrace();

        // Exception handling for the query debugging
        //} catch (FileNotFoundException fex) {
        //    System.err.println("Failed to open debug file.");
        //    fex.printStackTrace();

        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return list;
    }



    /**
     * Retrieve a list of all ACT tests associated with a particular build ID.
     *
     * @param   conn    Database connection
     * @param   buildId Build ID used as the foreign key
     *
     * @return  List of ACT test information objects
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
            rs = getInstance().executeQuery(st, "getTestsByBuild", sql.toString());
            if (rs != null) {
                CMnDbAcceptanceTestData test = null;
                while (rs.next()) {
                    test = parseTestData(rs);
                    list.add(test);
                }
            } else {
                 System.err.println("Unable to obtain the test list by build ID.");
            }
        } catch (SQLException ex) {
            System.err.println("Failed to obtain test list by build ID.");
            ex.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return list;
    }


    /**
     * Retrieve ACT test information from the database.
     *
     * @param   conn    Database connection
     * @param   testId  Primary key used to locate the test info
     *
     * @return  Test information
     */
    public synchronized CMnDbAcceptanceTestData getTest(
            Connection conn,
            String testId)
        throws SQLException
    {
        CMnDbAcceptanceTestData test = null;

        StringBuffer sql = new StringBuffer();
        sql.append(
            "SELECT * FROM " + TEST_TABLE +
            " WHERE " + TEST_ID + "=" + testId +
            " ORDER BY " + TEST_START_DATE + " DESC"
        );

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            rs = getInstance().executeQuery(st, "getTest", sql.toString());
            if (rs != null) {
                rs.first();
                test = parseTestData(rs);
            } else {
                 System.err.println("Unable to obtain the test data.");
            }
        } catch (SQLException ex) {
            System.err.println("Failed to obtain test data: " + sql.toString());
            ex.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return test;
    }


    /**
     * Retrieve ACT test case information from the database.
     *
     * @param   conn    Database connection
     * @param   test    ACT test information 
     *
     * @return  Test information
     */
    public synchronized Vector getTestCases(
            Connection conn,
            String testId)
        throws SQLException
    {
        Vector list = new Vector();

        StringBuffer sql = new StringBuffer();
        sql.append(
            "SELECT * FROM " + TESTCASE_TABLE +
            " WHERE " + TESTCASE_ACT + "=" + testId +
            " ORDER BY " + TESTCASE_NAME + " ASC"
        );

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            rs = getInstance().executeQuery(st, "getTestCases", sql.toString());
            if (rs != null) {
                String testcase = null;
                while (rs.next()) {
                    list.add(rs.getString(TESTCASE_NAME));
                }

            } else {
                 System.err.println("Unable to obtain the testcase data.");
            }
        } catch (SQLException ex) {
            System.err.println("Failed to obtain step data: " + sql.toString());
            ex.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return list;
    }


    /**
     * Retrieve ACT story information from the database.
     *
     * @param   conn    Database connection
     * @param   test    ACT test information 
     *
     * @return  Test information
     */
    public synchronized Vector getStories(
            Connection conn,
            String testId)
        throws SQLException
    {
        Vector list = new Vector();

        StringBuffer sql = new StringBuffer();
        sql.append(
            "SELECT * FROM " + STORY_TABLE +
            " WHERE " + STORY_ACT + "=" + testId +
            " ORDER BY " + STORY_NAME + " ASC"
        );

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            rs = getInstance().executeQuery(st, "getStories", sql.toString());
            if (rs != null) {
                String testcase = null;
                while (rs.next()) {
                    list.add(rs.getString(STORY_NAME));
                }

            } else {
                 System.err.println("Unable to obtain the testcase data.");
            }
        } catch (SQLException ex) {
            System.err.println("Failed to obtain step data: " + sql.toString());
            ex.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return list;
    }


    /**
     * Update the suite end time in the database.
     *
     * @param   conn    Database connection
     * @param   buildId Foreign key that links the suite to the build
     * @param   suite   Suite information 
     *
     * @return  results
     */
    public synchronized String updateSuiteEndTime(
            Connection conn,
            String jdbcDriver,
            String suiteIdFromDB,
            Date endTime)
        throws SQLException
    {
        boolean isOracle = false;
        if (jdbcDriver != null) {
            isOracle = jdbcDriver.indexOf("OracleDriver") >= 0;
        }
        String results = "";

        StringBuffer sql = new StringBuffer();
        sql.append("UPDATE " + SUITE_TABLE ); 
        sql.append(" SET " + SUITE_END_DATE + " = \"" + DATETIME.format(endTime) + "\"");
        sql.append(" WHERE " + SUITE_ID + " = \"" + suiteIdFromDB + "\"");

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            st.execute(sql.toString(), Statement.RETURN_GENERATED_KEYS);
            if (!isOracle) {
                //rs = st.getGeneratedKeys();
                rs = getGeneratedKeys(st);
                if (rs != null) {
                    rs.first();
                    results = rs.getString(1);
                } else {
                     System.err.println("Unable to obtain generated key.");
                }
            }
        } catch (SQLException ex) {
            System.err.println("Failed to add test suite.");
            System.err.println("SQL Query: " + sql.toString());
            ex.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return results;
    }








    /**
     * Retrieve all ACT test results that are comparable to the 
     * specified test.  A comparable test is on that was executed
     * against the same product version, JVM, and OS. 
     *
     * @param   conn    Database connection
     * @param   test    ACT test information
     *
     * @return  List of all similar tests 
     */
    public synchronized Vector getTestHistory(
            Connection conn,
            CMnDbAcceptanceTestData test)
        throws SQLException
    {
        Vector history = new Vector();

        // Obtain the test suite containing the current test
        String pid = Integer.toString(test.getParentId());
        CMnDbTestSuite testSuite = getSuite(conn, pid);
        if (testSuite != null) {
            CMnDbHostData host = testSuite.getHostData();

            // Obtain the version string of the build that contained the test suite
            CMnDbBuildData build = getBuild(conn, Integer.toString(testSuite.getParentId()));
            String version = build.getBuildVersion();

            // Replace the timestamp in the version string with a meta character that will
            // be used to search for all builds that match this version
            version = version.replaceAll(CMnBuildTable.TIMESTAMP_REGEX, "%");

            StringBuffer sql = new StringBuffer();
            sql.append(
                "SELECT * " +
                " FROM " + TEST_TABLE + ", " + SUITE_TABLE + ", " + CMnBuildTable.BUILD_TABLE +
                " WHERE " + TEST_TABLE + "." + TEST_FILE + " = '" + test.getScriptName() + "'" +
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
                rs = executeQuery(st, "getTestHistory", sql.toString());
                if (rs != null) {
                    CMnDbAcceptanceTestData currentTest = null;
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

        } // if (testSuite != null) 

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
    public synchronized CMnDbAcceptanceTestData getLastPass(
            Connection conn,
            CMnDbAcceptanceTestData test)
        throws SQLException
    {
        CMnDbAcceptanceTestData data = null; 

        // Obtain the test suite containing the current test
        CMnDbTestSuite testSuite = getSuite(conn, Integer.toString(test.getParentId()));
        CMnDbHostData host = testSuite.getHostData();

        // Obtain the build that contained the test suite
        CMnDbBuildData build = getBuild(conn, Integer.toString(testSuite.getParentId()));
        String version = build.getBuildVersion();

        // Replace the timestamp in the version string with a meta character that will 
        // be used to search for all builds that match this version 
        version = version.replaceAll(CMnBuildTable.TIMESTAMP_REGEX, "%");

        StringBuffer sql = new StringBuffer();
        sql.append(
            "SELECT * FROM " + TEST_TABLE + ", " + SUITE_TABLE + ", " + CMnBuildTable.BUILD_TABLE +
            " WHERE " + TEST_TABLE + "." + TEST_FILE + " = '" + test.getScriptName() + "'" + 
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
            rs = getInstance().executeQuery(st, "getLastPass", sql.toString());
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
     * Retrieve the unit test record for the last time the
     * unit test did not pass (includes failures and errors).  
     * If the unit test has never failed, a null will be returned.
     *
     * @param   conn    Database connection
     * @param   test    Unit test information
     *
     * @return  Most recent matching test that failed 
     */
    public synchronized CMnDbAcceptanceTestData getLastFailure(
            Connection conn,
            CMnDbAcceptanceTestData test)
        throws SQLException
    {
        CMnDbAcceptanceTestData data = null;

        // Obtain the test suite containing the current test
        CMnDbTestSuite testSuite = getSuite(conn, Integer.toString(test.getParentId()));
        CMnDbHostData host = testSuite.getHostData();

        // Obtain the build that contained the test suite
        CMnDbBuildData build = getBuild(conn, Integer.toString(testSuite.getParentId()));
        String version = build.getBuildVersion();

        // Replace the timestamp in the version string with a meta character that will
        // be used to search for all builds that match this version
        version = version.replaceAll(CMnBuildTable.TIMESTAMP_REGEX, "%");

        StringBuffer sql = new StringBuffer();
        sql.append(
            "SELECT * FROM " + TEST_TABLE + ", " + SUITE_TABLE + ", " + CMnBuildTable.BUILD_TABLE + 
            " WHERE " + TEST_TABLE + "." + TEST_FILE + " = '" + test.getScriptName() + "'" +
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
            rs = getInstance().executeQuery(st, "getLastFailure", sql.toString());
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
     * @parma   data  Empty data object to populate with data
     * @param   rs    Result set containing suite data
     *
     * @return  Test suite information
     */
    public CMnDbTestSuite parseSuiteData(ResultSet rs) 
        throws SQLException 
    {
        CMnDbTestSuite data = new CMnDbTestSuite(CMnDbTestSuite.SuiteType.ACT);
        parseSuiteData(rs, data);
        return data;
    }

    /**
     * Parse the result set to obtain test information.
     *
     * @parma   data  Empty data object to populate with data
     * @param   rs    Result set containing test data
     * 
     * @return  Test suite data
     */
    public CMnDbAcceptanceTestData parseTestData(ResultSet rs)
        throws SQLException
    {
        CMnDbAcceptanceTestData data = new CMnDbAcceptanceTestData();
        parseTestData(rs, data);

        String filename = rs.getString(TEST_FILE);
        data.setScriptName(filename);

        String author = rs.getString(TEST_AUTHOR);
        data.setAuthor(author);

        String text = rs.getString(TEST_SUMMARY);
        data.setSummary(text);

        return data;
    }


}

