
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
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;
import java.text.SimpleDateFormat;

/**
 * The unit test table provides an interface to unit test data.
 * 
 * @author  Shawn Stafford
 */
public class CMnUnittestTable extends CMnTestTable {


    /** Name of the column for the test class */
    public static final String TEST_CLASS = "class";

    /** Name of the column for the test method */
    public static final String TEST_METHOD = "method";


    /** Singleton instance of the table class */
    private static CMnUnittestTable instance;


    /**
     * Construct the abstract class and set the required table names.
     *
     * @param  suite  Suite table name
     * @param  test   Test table name
     */
    protected CMnUnittestTable(String suite, String test) {
        SUITE_TABLE = suite;
        TEST_TABLE = test;
    }


    /**
     * Return the singleton instance of the class.
     */
    public static CMnUnittestTable getInstance() {
        if (instance == null) {
            // Construct a new singleton instance
            instance = new CMnUnittestTable("unittest_suite", "unittest");

            // Make sure the TEST_TABLE.TEST_GROUP_NAME column is used for unit tests
            instance.enableTestGroupName(true);

            // Enable debbuging to a file
            String logfile = "/var/tmp/CMnUnittestTable.txt";
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
     * Store a unit test result in the database.
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
            CMnDbUnitTestData test) 
        throws SQLException
    {
        String testId = null;

        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO " + TEST_TABLE + " ");
        sql.append("(" + SUITE_ID);
        if ((test.getGroupName() != null) && (test.getGroupName().length() > 0)) {
            sql.append(", " + TEST_GROUP_NAME);
        }
        if (test.getStartTime() != null) sql.append(", " + TEST_START_DATE); 
        if (test.getEndTime()   != null) sql.append(", " + TEST_END_DATE);   
        sql.append(", " + TEST_CLASS); 
        sql.append(", " + TEST_METHOD);
        sql.append(", " + TEST_MESSAGE); 
        sql.append(", " + TEST_STATUS + ") ");

        sql.append("VALUES ");
        sql.append("(\"" + suiteId + "\"");
        if ((test.getGroupName() != null) && (test.getGroupName().length() > 0)) {
            sql.append(", \"" + test.getGroupName() + "\"");
        }
        if (test.getStartTime() != null) sql.append(", \"" + DATETIME.format(test.getStartTime()) + "\"");
        if (test.getEndTime()   != null) sql.append(", \"" + DATETIME.format(test.getEndTime())   + "\"");
        sql.append(", \"" + test.getClassName() + "\"");
        sql.append(", \"" + test.getMethodName() + "\"");
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
     * Update a unit test result in the database. 
     * 
     * @param   conn    Database connection
     * @param   test    Test data 
     *
     * @return  TRUE if the test data was updated 
     */
    public synchronized boolean updateTest(
            Connection conn, 
            CMnDbUnitTestData test)
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
        sql.append(", " + TEST_CLASS   + "=\"" + test.getClassName() + "\"");
        sql.append(", " + TEST_METHOD  + "=\"" + test.getMethodName() + "\"");
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
     * Retrieve a list of all suites associated with a particular build ID. 
     *
     * @param   conn    Database connection
     * @param   buildId Build ID used as the foreign key 
     * @param   suites  List of suites to reorganize
     *
     * @return  List of suite information objects
     */
    public synchronized Vector organizeTestsByGroup(
            Connection conn,
            String buildId,
            Vector suites)
        throws SQLException
    {
        Hashtable<String, CMnDbTestSuite> groups = new Hashtable<String, CMnDbTestSuite>();

        // TODO: cover the case where some or all of the suites have null group names
        // i.e.   getTestCount(conn, buildId, null)

        // Iterate through the groups to create "custom" suites with the correct test grouping
        Collection groupList = getGroupNames(conn, buildId);   
        Iterator groupIter = groupList.iterator();
        while (groupIter.hasNext()) {
            String groupName = (String) groupIter.next();

            // Get the adjusted test counts based on group name
            int longCount = getLongCount(conn, buildId, groupName);
            int passCount = getTestCount(conn, buildId, groupName, STATUS_PASS);
            int skipCount = getTestCount(conn, buildId, groupName, STATUS_SKIP);
            int failCount = getTestCount(conn, buildId, groupName, STATUS_FAIL);
            int killCount = getTestCount(conn, buildId, groupName, STATUS_KILL);
            int errorCount = getTestCount(conn, buildId, groupName, STATUS_ERROR);
            int pendingCount = getTestCount(conn, buildId, groupName, STATUS_PENDING);
            int runningCount = getTestCount(conn, buildId, groupName, STATUS_RUNNING);
            int blackCount = getTestCount(conn, buildId, groupName, STATUS_BLACKLIST);
            int execCount = getTestCount(conn, buildId, groupName, null);

            // Calculate the total elapsed time for all tests in the group
            long elapsedMillis = getElapsedTime(conn, buildId, groupName);

            // Create a dummy start date that can be stored in the test suite
            if (groups.containsKey(groupName)) {
                // Update an existing suite with information about the group
                CMnDbTestSuite groupSuite = groups.get(groupName);
                // Add the test count for the current suite
                groupSuite.setLongCount(longCount + groupSuite.getLongCount());
                groupSuite.setPassingCount(passCount + groupSuite.getPassingCount());
                groupSuite.setFailingCount(failCount + groupSuite.getFailingCount());
                groupSuite.setErrorCount(errorCount + groupSuite.getErrorCount());
                groupSuite.setSkipCount(skipCount + groupSuite.getSkipCount());
                groupSuite.setPendingCount(pendingCount + groupSuite.getPendingCount());
                groupSuite.setRunningCount(runningCount + groupSuite.getRunningCount());
                groupSuite.setBlacklistCount(blackCount + groupSuite.getBlacklistCount());
                groupSuite.setExecutedCount(execCount + groupSuite.getExecutedCount()); 
                groupSuite.setKilledCount(killCount + groupSuite.getKilledCount());
                groupSuite.setElapsedTime(elapsedMillis + groupSuite.getElapsedTime());

                // We really don't have a way to determine the expected total
                // test count because this method operates on individual tests rather
                // than the suites they belong to.
                groupSuite.setTestCount(groupSuite.getExecutedCount());
            } else {
                // Create a new test suite to represent the group
                CMnDbTestSuite groupSuite = new CMnDbTestSuite(CMnDbTestSuite.SuiteType.JUNIT);
                groupSuite.setSuiteName(groupName);
                groupSuite.setGroupName(groupName);
                groupSuite.setParentId(Integer.parseInt(buildId));
                // Set the test count to the current values
                groupSuite.setLongCount(longCount);
                groupSuite.setPassingCount(passCount);
                groupSuite.setFailingCount(failCount);
                groupSuite.setErrorCount(errorCount);
                groupSuite.setSkipCount(skipCount);
                groupSuite.setPendingCount(pendingCount);
                groupSuite.setRunningCount(runningCount);
                groupSuite.setBlacklistCount(blackCount);
                groupSuite.setExecutedCount(execCount);
                groupSuite.setKilledCount(killCount);
                groupSuite.setElapsedTime(elapsedMillis);

                // We really don't have a way to determine the expected total
                // test count because this method operates on individual tests rather
                // than the suites they belong to.  
                groupSuite.setTestCount(execCount);

                groups.put(groupName, groupSuite);
            }

        }

        // Return the list of items in the revised list
        Vector newSuites = new Vector();
        Enumeration newList = groups.elements();
        while (newList.hasMoreElements()) {
            newSuites.add(newList.nextElement());
        }

        return newSuites;
    }




    /**
     * Retrieve a list of all unit tests associated with a particular suite ID.
     *
     * @param   conn    Database connection
     * @param   suiteId Suite ID used as the foreign key
     *
     * @return  List of unittest information objects
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
            rs = executeQuery(st, "getTestsBySuite", sql.toString());
            if (rs != null) {
                CMnDbUnitTestData test = null;
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
            rs = executeQuery(st, "getTestsBySuiteGroup", sql.toString());
            if (rs != null) {
                CMnDbUnitTestData test = null;
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
            " WHERE " + SUITE_TABLE + "." + CMnBuildTable.BUILD_ID + "=" + buildId +
            "   AND (" + SUITE_TABLE + "." + SUITE_ID + "=" + TEST_TABLE + "." + SUITE_ID + ")");

        // Make sure that the query can be constructed to return results
        // for legacy builds which contain no group data
        if (groupName != null) {
            sql.append(
                "   AND (" + 
                "     ((" + TEST_TABLE + "." + TEST_GROUP_NAME + " IS NULL) AND (" + SUITE_TABLE + "." + SUITE_GROUP_NAME + "='" + groupName + "'"  + ")) " +
                "     OR " +
                "     ((" + TEST_TABLE + "." + TEST_GROUP_NAME + " IS NOT NULL) AND (" + TEST_TABLE + "." + TEST_GROUP_NAME + " ='" + groupName + "')) " +
                "   )");
        } else {
            sql.append(
                "   AND " + TEST_TABLE + "." + TEST_GROUP_NAME + " IS NULL" +
                "   AND " + SUITE_TABLE + "." + SUITE_GROUP_NAME + " IS NULL");
        }
        sql.append(" ORDER BY " + TEST_TABLE + "." + TEST_START_DATE + " DESC");


        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            rs = executeQuery(st, "getTestsByGroupName", sql.toString());
            if (rs != null) {
                CMnDbUnitTestData test = null;
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
     * Retrieve a list of all unit tests associated with a particular build ID.
     *
     * @param   conn    Database connection
     * @param   buildId Build ID used as the foreign key
     *
     * @return  List of unittest information objects
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
            rs = executeQuery(st, "getTestsByBuild", sql.toString());
            if (rs != null) {
                CMnDbUnitTestData test = null;
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
    public synchronized CMnDbUnitTestData getTest(
            Connection conn,
            String testId)
        throws SQLException
    {
        CMnDbUnitTestData test = null;

        StringBuffer sql = new StringBuffer();
        sql.append(
            "SELECT * FROM " + TEST_TABLE + 
            " WHERE " + TEST_ID + "=" + testId +
            " ORDER BY " + TEST_START_DATE + " DESC"
        );

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            rs = executeQuery(st, "getTest", sql.toString());
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
     * Retrieve the test suite with the longest execution time. 
     * 
     * @param   conn    Database connection
     * @param   buildId Build ID to use as foreign key
     * @param   group   Name of the test group 
     *
     * @return  Test suite with the longest time 
     */
    public synchronized CMnDbTestSuite getLongestSuite(
            Connection conn,
            String buildId,
            String group)
        throws SQLException
    {
        CMnDbTestSuite longestSuite = null;

        StringBuffer sql = new StringBuffer();
        sql.append(
            "SELECT * FROM " + SUITE_TABLE + 
            " WHERE " + CMnBuildTable.BUILD_ID + "=" + buildId); 

        if ((group != null) && (group.length() > 0)) {
            sql.append("   AND " + SUITE_GROUP_NAME + "='" + group + "'");
        }

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            rs = executeQuery(st, "getLongestSuite", sql.toString());
            if (rs != null) {
                CMnDbTestSuite suite = null;
                long longestTime = 0;
                while (rs.next()) {
                    suite = parseSuiteData(rs);
                    if ((suite.getStartTime() != null) && (suite.getEndTime() != null)) {
                        long elapsedTime = suite.getEndTime().getTime() - suite.getStartTime().getTime();
                        if (elapsedTime > longestTime) {
                            longestTime = elapsedTime;
                            longestSuite = suite;
                        }
                    }
                }
            } else {
                 System.err.println(
                     "Unable to obtain the longest running test suite with " +
                     CMnBuildTable.BUILD_ID + "=" + buildId + " and " + SUITE_GROUP_NAME + "=" + group);
            }
        } catch (SQLException ex) {
            System.err.println("Failed to obtain the total number of unit tests.");
            ex.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return longestSuite;
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
            CMnDbUnitTestData test)
        throws SQLException
    {
        Vector history = new Vector();

        // Obtain the test suite containing the current test
        CMnDbTestSuite testSuite = getSuite(conn, Integer.toString(test.getParentId()));
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
            " ORDER BY " + TEST_TABLE + "." + TEST_ID + " DESC");

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            rs = executeQuery(st, "getTestHistory", sql.toString());
            if (rs != null) {
                CMnDbUnitTestData currentTest = null;
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
    public synchronized CMnDbUnitTestData getLastPass(
            Connection conn,
            CMnDbUnitTestData test)
        throws SQLException
    {
        CMnDbUnitTestData data = null; 

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
            rs = executeQuery(st, "getLastPass", sql.toString());
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
    public synchronized CMnDbUnitTestData getLastFailure(
            Connection conn,
            CMnDbUnitTestData test)
        throws SQLException
    {
        CMnDbUnitTestData data = null;

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
            rs = executeQuery(st, "getLastFailure", sql.toString());
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
        CMnDbTestSuite data = new CMnDbTestSuite(CMnDbTestSuite.SuiteType.JUNIT);
        super.parseSuiteData(rs, data);

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
    public CMnDbUnitTestData parseTestData(ResultSet rs)
        throws SQLException
    {
        CMnDbUnitTestData data = new CMnDbUnitTestData();
        super.parseTestData(rs, data);

        String classname = rs.getString(TEST_CLASS);
        data.setClassName(classname);

        String methodname = rs.getString(TEST_METHOD);
        data.setMethodName(methodname);

        return data;
    }


}

