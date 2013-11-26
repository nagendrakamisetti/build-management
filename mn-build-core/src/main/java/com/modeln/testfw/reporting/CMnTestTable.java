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
 * The test table defines access methods for mapping data between Java and 
 * test data tables. 
 * 
 * @author  Shawn Stafford
 */
public abstract class CMnTestTable extends CMnTable {

    /** Name of the table used for test suites */
    protected String SUITE_TABLE;

    /** Name of the table used for individual tests */
    protected String TEST_TABLE;



    /* ============================== TEST SUITE TABLE ========================== */

    /** Name of the column that identifies the test suite */
    public static final String SUITE_ID = "suite_id";

    /** Name of the column for the test start time */
    public static final String SUITE_START_DATE = "start_date";

    /** Name of the column for the test end time */
    public static final String SUITE_END_DATE = "end_date";

    /** Name of the column for the suite name */
    public static final String SUITE_NAME = "suite_name";

    /** Name of the column for the number of tests in the suite */
    public static final String SUITE_TEST_COUNT = "test_count";

    /** Name of the column for the app JDBC URL */
    public static final String SUITE_JDBC_URL = "jdbc_url";

    /** Name of the column for the suite options */
    public static final String SUITE_OPTIONS = "suite_options";

    /** Name of the column that identifies the suite group ID */
    public static final String SUITE_GROUP_ID = "suite_group_id";

    /** Name of the column that identifies the suite group name */
    public static final String SUITE_GROUP_NAME = "suite_group_name";

    /** Name of the column for the environment name */
    public static final String SUITE_ENVIRONMENT = "env_name";

    /** Name of the column for the maximum number of threads */
    public static final String SUITE_MAX_THREADS = "max_threads";


    /* ================================ TEST TABLE ============================= */

    /** Name of the column for the test primary key */
    public static final String TEST_ID = "test_id";

    /** Name of the column for the test start time */
    public static final String TEST_START_DATE = "start_date";

    /** Name of the column for the test end time */
    public static final String TEST_END_DATE = "end_date";

    /** Name of the column for the test status */
    public static final String TEST_STATUS = "status";

    /** Name of the column that identifies the test suite associated with a test */
    public static final String TEST_GROUP_NAME = "test_group_name";

    /** Name of the column for the test message */
    public static final String TEST_MESSAGE = "message";


    /* ================================ CONSTANTS ============================== */

    /** Amount of time if test exceeds marked as failure */
    public static final String TIME_EXCEED = "300";

    /** Determine whether the elapsed time should be calculated in the database or in the Java code */
    private static final boolean calcTimeInDatabase = true;


    /* =============================== ENUMERATIONS ============================ */

    public static final String STATUS_PASS = "PASS";
    public static final String STATUS_ERROR = "ERROR";
    public static final String STATUS_FAIL = "FAIL";
    public static final String STATUS_SKIP = "SKIP";
    public static final String STATUS_KILL = "KILL";
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_RUNNING = "RUNNING";
    public static final String STATUS_BLACKLIST = "BLACKLIST";

    /* ================================ VARIABLES ============================== */

    private boolean useTestGroupName = false;


    /**
     * Constructor to prevent the class from being constructed without table names.
     */
    protected CMnTestTable() {
        SUITE_TABLE = "unknown";
        TEST_TABLE = "unknown";
    }


    /**
     * Construct the abstract class and set the required table names.
     *
     * @param  suite  Suite table name
     * @param  test   Test table name
     */
    protected CMnTestTable(String suite, String test) { 
        SUITE_TABLE = suite;
        TEST_TABLE = test;
    }

    /**
     * Returns the name of the suite table.
     *
     * @return  Suite table name
     */
    public String getSuiteTable() {
        return SUITE_TABLE;
    }

    /**
     * Returns the name of the test table.
     *
     * @return  Test table name
     */
    public String getTestTable() {
        return TEST_TABLE;
    }

    /**
     * Enable the use of the TEST_TABLE.TEST_GROUP_NAME column in query results.
     * This column may or may not make sense depending on the suite and test
     * relationship.  If the suite always contains a single group of tests, then
     * this column should be disabled.  If the suite can contains a mix of tests 
     * from different groups, then this column should be enabled.
     *
     * @param  enable   TRUE if the column should be enabled
     */
    protected void enableTestGroupName(boolean enable) {
        useTestGroupName = enable;
    }

    /**
     * Parse the result set to obtain suite information.
     *
     * @parma   data  Empty data object to populate with data
     * @param   rs    Result set containing suite data
     *
     * @return  Test suite information
     */
    public abstract CMnDbTestSuite parseSuiteData(ResultSet rs)
        throws SQLException;


    /**
     * Store a test suite information in the database.
     *
     * @param   conn    Database connection
     * @param   buildId Foreign key that links the suite to a build
     * @param   suite   Suite data
     *
     * @return  Auto-generated key that identifies the test suite
     */
    public synchronized String addSuite(
            Connection conn,
            String buildId,
            CMnDbTestSuite suite)
        throws SQLException
    {
        String suiteId = null;
        String groupId = Long.toString(suite.getGroupId());
        String groupName = suite.getGroupName();
        String jdbcUrl = suite.getJdbcUrl();
        Date startTime = suite.getStartTime();
        Date endTime = suite.getEndTime();
        String envName = suite.getEnvironmentName();
        CMnDbHostData host = suite.getHostData();
        Map suiteOptions = suite.getOptions();

        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO " + SUITE_TABLE + " ");
        sql.append("(" + CMnBuildTable.BUILD_ID);
        sql.append(", " + SUITE_GROUP_ID);
        sql.append(", " + SUITE_TEST_COUNT);
        sql.append(", " + SUITE_MAX_THREADS);
        if (groupName != null) sql.append(", " + SUITE_GROUP_NAME);
        if (jdbcUrl   != null) sql.append(", " + SUITE_JDBC_URL);
        if (startTime != null) sql.append(", " + SUITE_START_DATE);
        if (endTime   != null) sql.append(", " + SUITE_END_DATE);
        if (envName   != null) sql.append(", " + SUITE_ENVIRONMENT);
        if (host != null) {
            sql.append(", " + CMnHostTable.HOST_ACCOUNT);
            sql.append(", " + CMnHostTable.HOST_NAME);
            sql.append(", " + CMnHostTable.JDK_VERSION);
            sql.append(", " + CMnHostTable.JDK_VENDOR);
            sql.append(", " + CMnHostTable.OS_NAME);
            sql.append(", " + CMnHostTable.OS_ARCHITECTURE);
            sql.append(", " + CMnHostTable.OS_VERSION);
        }
        if (suiteOptions.size() > 0) {
            sql.append(", " + SUITE_OPTIONS);
        }
        sql.append(", " + SUITE_NAME + ") ");

        sql.append("VALUES ");
        sql.append("(\"" + buildId + "\"");
        sql.append(", \"" + groupId + "\"");
        sql.append(", " + suite.getTestCount());
        sql.append(", " + suite.getMaxThreadCount());
        if (groupName != null) sql.append(", \"" + groupName + "\"");
        if (jdbcUrl   != null) sql.append(", \"" + jdbcUrl + "\"");
        if (startTime != null) sql.append(", \"" + DATETIME.format(startTime) + "\"");
        if (endTime   != null) sql.append(", \"" + DATETIME.format(endTime) + "\"");
        if (envName   != null) sql.append(", \"" + envName + "\"");
        if (host != null) {
            sql.append(", \"" + host.getUsername() + "\"");
            sql.append(", \"" + host.getHostname() + "\"");
            sql.append(", \"" + host.getJdkVersion() + "\"");
            sql.append(", \"" + host.getJdkVendor() + "\"");
            sql.append(", \"" + host.getOSName() + "\"");
            sql.append(", \"" + host.getOSArchitecture() + "\"");
            sql.append(", \"" + host.getOSVersion() + "\"");
        }
        if (suiteOptions.size() > 0) {
            sql.append(", \"" + getOptionsAsString(suiteOptions) + "\"");
        }
        sql.append(", \"" + suite.getSuiteName() + "\")");

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            executeInsert(st, "addSuite", sql.toString());
            //rs = st.getGeneratedKeys();
            rs = getGeneratedKeys(st);
            if (rs != null) {
                rs.first();
                suiteId = rs.getString(1);
            } else {
                 System.err.println("Unable to obtain generated key.");
            }
        } catch (SQLException ex) {
            System.err.println("Failed to add test: " + sql.toString());
            ex.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return suiteId;
    }


    /**
     * Retrieve suite information from the database.
     *
     * @param   conn    Database connection
     * @param   suiteId Primary key used to locate the suite info
     *
     * @return  suite information
     */
    public synchronized CMnDbTestSuite getSuite(
            Connection conn,
            String suiteId)
        throws SQLException
    {
        CMnDbTestSuite suite = null;

        StringBuffer sql = new StringBuffer();
        sql.append(
            "SELECT * FROM " + SUITE_TABLE +
            " WHERE " + SUITE_ID + "=" + suiteId +
            " ORDER BY " + SUITE_START_DATE + " DESC"
        );

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            rs = executeQuery(st, "getSuite", sql.toString());
            if (rs != null) {
                rs.first();
                suite = parseSuiteData(rs);
                updateTestSummary(conn, suite);
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

        return suite;
    }

    /**
     * Retrieve a list of all failing tests from the database, limiting 
     * the result set to a specified number of rows. 
     *
     * @param   conn      Database connection
     * @param   criteria  Set of limiting criteria
     * 
     * @return  List of suite information objects
     */
    public synchronized Vector<CMnDbTestData> getTestFailures(
            Connection conn,
            CMnSearchGroup criteria)
        throws SQLException
    {
        Vector list = new Vector();

        StringBuffer sql = new StringBuffer();
        sql.append(
            "SELECT * FROM " + TEST_TABLE + " WHERE " + criteria.toSql() +
            " ORDER BY " + TEST_ID + " DESC"
        );

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            rs = executeQuery(st, "getTestFailures", sql.toString());
            if (rs != null) {
                CMnDbTestData test = null;
                while (rs.next()) {
                    test = parseTestData(rs);
                    list.add(test);
                }
            } else {
                 System.err.println("Unable to obtain the test data.");
            }
        } catch (SQLException ex) {
            System.err.println("Failed to obtain test data.");
            ex.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return list;
    }


    /**
     * Retrieve a list of all suites associated with a particular build ID.
     *
     * @param   conn    Database connection
     * @param   buildId Build ID used as the foreign key
     *
     * @return  List of suite information objects
     */
    public synchronized Vector getSuitesByBuild(
            Connection conn,
            String buildId)
        throws SQLException
    {
        return getSuitesByBuild(conn, buildId, null);
    }

    /**
     * Retrieve a list of all suites from the database, limiting the result
     * set to a specified number of rows. 
     *
     * @param   conn      Database connection
     * @param   criteria  Set of limiting criteria
     * @param   buildId   Build ID used as the foreign key
     * 
     * @return  List of suite information objects
     */
    public synchronized Vector<CMnDbTestSuite> getSuitesByBuild(
            Connection conn,
            String buildId,
            CMnSearchGroup criteria)
        throws SQLException
    {
        Vector list = new Vector();

        StringBuffer sql = new StringBuffer();
        sql.append(
            "SELECT * FROM " + SUITE_TABLE +
            " WHERE " + CMnBuildTable.BUILD_ID + "=" + buildId 
        );
        // Make sure we actually have some criteria to add
        if (criteria != null) {
            String crStr = criteria.toSql();
            if ((crStr != null) && (crStr.length() > 0)) {
                sql.append(" AND " + criteria.toSql());
            }
        }
        sql.append(" ORDER BY " + SUITE_START_DATE + " DESC");

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            rs = executeQuery(st, "getSuitesByBuild", sql.toString());
            if (rs != null) {
                CMnDbTestSuite suite = null;
                while (rs.next()) {
                    suite = parseSuiteData(rs);
                    updateTestSummary(conn, suite);
                    list.add(suite);
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
     * Retrieve a count of all tests in a single execution of a test suite with
     * the given status value.
     *
     * @param   conn    Database connection
     * @param   suite   Information about a single test suite execution
     * @param   status  Test status to be counted
     *
     * @return  Total number of tests with the given status
     */
    public synchronized int getTestCount(
            Connection conn,
            CMnDbTestSuite suite,
            String status)
        throws SQLException
    {
        int count = 0;

        StringBuffer sql = new StringBuffer();
        sql.append(
            "SELECT COUNT(*) FROM " + TEST_TABLE +
            " WHERE " + SUITE_ID + "=" + suite.getId());
        if (status != null) {
            sql.append(" AND " + TEST_STATUS + "='" + status + "'");
        }

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            rs = executeQuery(st, "getTestCount(suite,status)", sql.toString());
            if (rs != null) {
                rs.first();
                count = rs.getInt(1);
            } else {
                 System.err.println(
                     "Unable to obtain the total number of tests with " +
                     SUITE_ID + "=" + suite.getId() + " and " + TEST_STATUS + "=" + status);
            }
        } catch (SQLException ex) {
            System.err.println("Failed to obtain the total number of unit tests.");
            ex.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return count;
    }

    /**
     * Retrieve a total count of all tests in a single execution of a test suite.
     *
     * @param   conn    Database connection
     * @param   suite   Information about a single test suite execution
     *
     * @return  Total number of tests 
     */
    public synchronized int getTestCount(
            Connection conn,
            CMnDbTestSuite suite)
        throws SQLException
    {
        int count = 0;

        StringBuffer sql = new StringBuffer();
        sql.append(
            "SELECT COUNT(*) FROM " + TEST_TABLE +
            " WHERE " + SUITE_ID + "=" + suite.getId());

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            rs = executeQuery(st, "getTestCount(suite)", sql.toString());
            if (rs != null) {
                rs.first();
                count = rs.getInt(1);
            } else {
                 System.err.println(
                     "Unable to obtain the total number of tests for " +
                     SUITE_ID + "=" + suite.getId());
            }
        } catch (SQLException ex) {
            System.err.println("Failed to obtain the total number of flex tests.");
            ex.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return count;
    }



    /**
     * Retrieve a total count of all tests in a single build. 
     *
     * @param   conn    Database connection
     * @param   build   Build information 
     * @param   nobug   Exclude bug suites if TRUE
     *
     * @return  Total number of tests
     */
    public synchronized int getTestCount(
            Connection conn,
            CMnDbBuildData build,
            boolean nobug)
        throws SQLException
    {
        int count = 0;

        StringBuffer sql = new StringBuffer();

        // Join the flex test and suite tables and count the number of rows 
        // with the matching build ID.
        sql.append(
            "SELECT COUNT(*) FROM " + TEST_TABLE + ", " + SUITE_TABLE +
            " WHERE " + SUITE_TABLE + "." + CMnBuildTable.BUILD_ID + " = " + build.getId() +
            " AND " + SUITE_TABLE + "." + SUITE_ID + " = " + TEST_TABLE + "." + SUITE_ID);

        // Exclude any bug suites
        if (nobug) {
            sql.append(" AND ((" + SUITE_OPTIONS + " NOT LIKE '%bug=true%') OR (" + SUITE_OPTIONS + " IS NULL))");
        }

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            rs = executeQuery(st, "getTestCount(build)", sql.toString());
            if (rs != null) {
                rs.first();
                count = rs.getInt(1);
            } else {
                 System.err.println(
                     "Unable to obtain the total number of tests for " +
                     CMnBuildTable.BUILD_ID + "=" + build.getId());
            }
        } catch (SQLException ex) {
            System.err.println("Failed to obtain the total number of flex tests.");
            ex.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return count;
    }


    /**
     * Retrieve a total count of all tests in a single build. 
     *
     * @param   conn    Database connection
     * @param   build   Build information 
     * @param   status  Unit test status that we would like to count
     * @param   nobug   Exclude bug suites if TRUE
     *
     * @return  Total number of tests
     */
    public synchronized int getTestCount(
            Connection conn,
            CMnDbBuildData build,
            String status,
            boolean nobug)
        throws SQLException
    {
        int count = 0;

        StringBuffer sql = new StringBuffer();

        // Join the ACT test and suite tables and count the number of rows 
        // with the matching build ID.
        sql.append(
            "SELECT COUNT(*) FROM " + TEST_TABLE + ", " + SUITE_TABLE +
            " WHERE " + SUITE_TABLE + "." + CMnBuildTable.BUILD_ID + " = " + build.getId() +
            " AND " + SUITE_TABLE + "." + SUITE_ID + " = " + TEST_TABLE + "." + SUITE_ID);
        if (status != null) {
            sql.append(" AND " + TEST_TABLE + "." + TEST_STATUS + " = '" + status + "'");
        }

        // Exclude any bug suites
        if (nobug) {
            sql.append(" AND ((" + SUITE_OPTIONS + " NOT LIKE '%bug=true%') OR (" + SUITE_OPTIONS + " IS NULL))");
        }


        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            rs = executeQuery(st, "getTestCount(build,status)", sql.toString());
            if (rs != null) {
                rs.first();
                count = rs.getInt(1);
            } else {
                 System.err.println(
                     "Unable to obtain the total number of tests for " +
                     CMnBuildTable.BUILD_ID + "=" + build.getId());
            }
        } catch (SQLException ex) {
            System.err.println("Failed to obtain the total number of flex tests.");
            ex.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return count;
    }


    /**
     * Retrieve a count of all tests in a build which correspond to 
     * the given group and status value.
     * 
     * @param   conn    Database connection
     * @param   buildId Build ID to use as foreign key
     * @param   group   Name of the test group 
     * @param   status  Test status to be counted
     *
     * @return  Total number of tests with the given status and group name
     */
    public synchronized int getTestCount(
            Connection conn,
            String buildId,
            String group,
            String status)
        throws SQLException
    {
        int count = 0;

        StringBuffer sql = new StringBuffer();
        sql.append(
            "SELECT COUNT(*) FROM " + SUITE_TABLE + ", " + TEST_TABLE +
            " WHERE " + CMnBuildTable.BUILD_ID + "=" + buildId +
            " AND " + SUITE_TABLE + "." + SUITE_ID + " = " + TEST_TABLE + "." + SUITE_ID);

        sql.append(getGroupCriteria(group));

        // Include the status in the query
        if (status != null) {
            sql.append(" AND " + TEST_STATUS + "='" + status + "'");
        }

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            rs = executeQuery(st, "getTestCount(buildid,group,status)", sql.toString());
            if (rs != null) {
                rs.first();
                count = rs.getInt(1);
            } else {
                 System.err.println(
                     "Unable to obtain the total number of tests with " +
                     CMnBuildTable.BUILD_ID + "=" + buildId + " and " + TEST_STATUS + "=" + status);
            }
        } catch (SQLException ex) {
            System.err.println("Failed to obtain the total number of unit tests.");
            ex.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return count;
    }



    /**
     * Retrieve a count of all tests which exceeded a given 
     * amount of time
     * 
     * @param   conn    Database connection
     * @param   suite   Information about a single test suite execution
     *
     * @return  Total number of tests with the given status
     */
    public synchronized int getLongCount(
            Connection conn,
            CMnDbTestSuite suite)
        throws SQLException
    {
        int count = 0;

        StringBuffer sql = new StringBuffer();
        sql.append(
            "SELECT COUNT(*) FROM " + TEST_TABLE +
            " WHERE " + SUITE_ID + "=" + suite.getId() +
            " AND (UNIX_TIMESTAMP(" + SUITE_END_DATE + ") - UNIX_TIMESTAMP(" + SUITE_START_DATE + ") ) > " + TIME_EXCEED);

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            rs = executeQuery(st, "getLongCount(suite)", sql.toString());
            if (rs != null) {
                rs.first();
                count = rs.getInt(1);
            } else {
                 System.err.println(
                     "Unable to obtain the total number of tests with " +
                     SUITE_ID + "=" + suite.getId() + " and " + SUITE_END_DATE + "-" + SUITE_START_DATE);
            }
        } catch (SQLException ex) {
            System.err.println("Failed to obtain the total number of flex tests exceeded time allowed.");
            ex.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return count;
    }


    /**
     * Retrieve a count of all tests for a single build and group which
     * exceeded a given amount of time
     *
     * @param   conn    Database connection
     * @param   suite   Information about a single test suite execution
     * @param   group   Test group
     *
     * @return  Total number of tests with the given status
     */
    public synchronized int getLongCount(
            Connection conn,
            String buildId,
            String group)
        throws SQLException
    {
        int count = 0;

        StringBuffer sql = new StringBuffer();
        sql.append(
            "SELECT COUNT(*) FROM " + SUITE_TABLE + ", " + TEST_TABLE +
            " WHERE " + CMnBuildTable.BUILD_ID + "=" + buildId +
            " AND " + SUITE_TABLE + "." + SUITE_ID + " = " + TEST_TABLE + "." + SUITE_ID +
            " AND (UNIX_TIMESTAMP(" + TEST_TABLE + "." + TEST_END_DATE + ") - UNIX_TIMESTAMP(" + TEST_TABLE + "." + TEST_START_DATE + ") ) > " + TIME_EXCEED);

        // Exclude any bug suites
        sql.append(" AND ((" + SUITE_OPTIONS + " NOT LIKE '%bug=true%') OR (" + SUITE_OPTIONS + " IS NULL))");

        sql.append(getGroupCriteria(group));

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            rs = executeQuery(st, "getLongCount(buildid,group)", sql.toString());
            if (rs != null) {
                rs.first();
                count = rs.getInt(1);
            } else {
                 System.err.println(
                     "Unable to obtain the total number of tests with " +
                     CMnBuildTable.BUILD_ID + "=" + buildId + " and " + SUITE_END_DATE + "-" + SUITE_START_DATE);
            }
        } catch (SQLException ex) {
            System.err.println("Failed to obtain the total number of unit tests exceeded time allowed.");
            ex.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return count;
    }


    /**
     * Retrieve the total amount of time elapsed while executing tests.
     *
     * @param   conn    Database connection
     * @param   suite   Test suite information
     *
     * @return  Total amount of elapsed time (in milliseconds)
     */
    public synchronized long getElapsedTime(
            Connection conn,
            CMnDbTestSuite suite)
        throws SQLException
    {
        long elapsedTime = 0;

        // TODO:  If we don't need to exclude the bug suites, we could get away with
        //        querying only the test table instead of joining with the suite table

        StringBuffer sql = new StringBuffer();
        sql.append(
            "SELECT SUM(TIMESTAMPDIFF(SECOND, " + TEST_TABLE + "." + TEST_START_DATE + ", " + TEST_TABLE + "." + TEST_END_DATE + "))" +
            "  FROM " + SUITE_TABLE + ", " + TEST_TABLE +
            " WHERE " + TEST_TABLE + "." + SUITE_ID + "=" + suite.getId() +
            "   AND " + SUITE_TABLE + "." + SUITE_ID + "=" + TEST_TABLE + "." + SUITE_ID);

        // Exclude any bug suites
        sql.append(" AND ((" + SUITE_OPTIONS + " NOT LIKE '%bug=true%') OR (" + SUITE_OPTIONS + " IS NULL))");

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            rs = executeQuery(st, "getElapsedTime(suite)", sql.toString());
            if (rs != null) {
                rs.first();
                int time = rs.getInt(1);
                if (time > 0) { 
                    elapsedTime = time;
                } else {
                    debugWrite("SSDEBUG: Query returned a negative value for the elapsed time: " + time);
                }
            } else {
                 System.err.println(
                     "Unable to obtain the elapsed time with " + SUITE_ID + "=" + suite.getId());
            }
        } catch (SQLException ex) {
            System.err.println("Failed to obtain the total elapsed time.");
            ex.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return elapsedTime * 1000;
    }



    /**
     * Retrieve the total amount of time elapsed while executing tests.
     *
     * @param   conn    Database connection
     * @param   buildId Build ID to use as foreign key
     * @param   group   Name of the test group
     *
     * @return  Total amount of elapsed time (in milliseconds)
     */
    public synchronized long getElapsedTime(
            Connection conn,
            String buildId,
            String group)
        throws SQLException
    {
        long elapsedTime = 0;

        StringBuffer sql = new StringBuffer();
        // Determine whether the query should return a single result or
        // whether the java code should perform the calculation
        if (calcTimeInDatabase) {
            sql.append("SELECT SUM(TIMESTAMPDIFF(SECOND, " + TEST_TABLE + "." + TEST_START_DATE + ", " + TEST_TABLE + "." + TEST_END_DATE + "))");
        } else {
            sql.append("SELECT " + TEST_TABLE + "." + TEST_START_DATE + ", " + TEST_TABLE + "." + TEST_END_DATE);
        }
        sql.append(
            "  FROM " + SUITE_TABLE + ", " + TEST_TABLE +
            " WHERE " + CMnBuildTable.BUILD_ID + "=" + buildId +
            "   AND " + SUITE_TABLE + "." + SUITE_ID + " = " + TEST_TABLE + "." + SUITE_ID);

        sql.append(getGroupCriteria(group));

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            // Attempt to write some debugging info to a file
            rs = executeQuery(st, "getElapsedTime(buildid,group)", sql.toString());
            if (rs != null) {
                while (rs.next()) {
                    if (calcTimeInDatabase) {
                        elapsedTime = elapsedTime + rs.getInt(1);
                    } else {
                        Date startTime = rs.getTimestamp(TEST_TABLE + "." + TEST_START_DATE);
                        Date endTime = rs.getTimestamp(TEST_TABLE + "." + TEST_END_DATE);
                        if ((startTime != null) && (endTime != null) && (endTime.getTime() > startTime.getTime())) {
                            long testTime = endTime.getTime() - startTime.getTime();
                            elapsedTime = elapsedTime + testTime;
                        }
                    }
                }

            } else {
                 System.err.println(
                     "Unable to obtain the elapsed time with " + CMnBuildTable.BUILD_ID + "=" + buildId);
            }
        } catch (SQLException ex) {
            System.err.println("Failed to obtain the total elapsed time.");
            ex.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        // Adjust the elapsed time so it is in milliseconds
        // since the DB query returns the time in seconds,
        if (calcTimeInDatabase) {
            elapsedTime = elapsedTime * 1000;
        }

        return elapsedTime;
    }



    /**
     * Retrieve build information from the database.  This method is necessary 
     * because as a static class with syncronized methods, this class must have
     * the ability to obtain build information without making calls outside of
     * itself.  Making calls to other classes to obtain build information can
     * result in deadlocks.
     *
     * @param   conn    Database connection
     * @param   buildId Primary key used to locate the build info
     *
     * @return  Build information
     */
    public synchronized CMnDbBuildData getBuild(
            Connection conn,
            String buildId)
        throws SQLException
    {
        CMnDbBuildData build = null;

        StringBuffer sql = new StringBuffer();
        sql.append(
            "SELECT * FROM " + CMnBuildTable.BUILD_TABLE +
            " WHERE " + CMnBuildTable.BUILD_ID + "=" + buildId +
            " ORDER BY " + CMnBuildTable.BUILD_START_DATE + " DESC"
        );

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            rs = executeQuery(st, "getBuild", sql.toString());
            if (rs != null) {
                rs.first();
                build = parseBuildData(rs);
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
     * Update the test suite data with an end date. 
     * This method is called once a suite has completed execution so that
     * the total execution time of the suite can be determined.  The suite
     * end time can also be used by outside programs to determine when suite
     * execution has completed.
     *
     * @param   conn    Database connection
     * @param   suiteId Primary key that identifies the test suite
     * @param   enddate Suite execution end date 
     *
     * @return  TRUE if the date was updated, false otherwise 
     */
    public synchronized boolean setSuiteEndDate(
            Connection conn,
            String suiteId,
            Date enddate)
        throws SQLException
    {
        boolean result = false;

        // If the date is null, we have nothing to do
        if (enddate != null) {
            StringBuffer sql = new StringBuffer();
            sql.append("UPDATE " + SUITE_TABLE + " ");
            sql.append("SET " + SUITE_END_DATE + "=\"" + DATETIME.format(enddate) + "\" ");
            sql.append("WHERE " + SUITE_ID + "=\"" + suiteId + "\""); 

            Statement st = conn.createStatement();
            ResultSet rs = null;
            try {
                execute(st, "setSuiteEndDate", sql.toString());
                result = true;
            } catch (SQLException ex) {
                System.err.println("Failed to set suite end date: " + sql.toString());
                ex.printStackTrace();
            } finally {
                if (rs != null) rs.close();
                if (st != null) st.close();

            }

        }

        return result;
    }


    /**
     * Update the test suite data with the total test count. 
     * This method is called to update the suite information with the  
     * total number of tests executed or expected to be executed.
     *
     * @param   conn    Database connection
     * @param   suiteId Primary key that identifies the test suite
     * @param   count   Number of tests in the suite 
     *
     * @return  TRUE if the date was updated, false otherwise 
     */
    public synchronized boolean setTestCount(
            Connection conn,
            String suiteId,
            int count)
        throws SQLException
    {
        boolean result = false;

        StringBuffer sql = new StringBuffer();
        sql.append("UPDATE " + SUITE_TABLE + " ");
        sql.append("SET " + SUITE_TEST_COUNT + "=\"" + count + "\" ");
        sql.append("WHERE " + SUITE_ID + "=\"" + suiteId + "\"");

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            execute(st, "setTestCount", sql.toString());
            result = true;
        } catch (SQLException ex) {
            System.err.println("Failed to set test count: " + sql.toString());
            ex.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return result;
    }



    /**
     * Analyze the suite and tests for a given build and return a unique list
     * of group names.
     *
     * @param   conn    Database connection
     * @param   buildId Build ID used as the foreign key 
     *
     * @return  List of group names
     */
    public synchronized Collection getGroupNames(
            Connection conn,
            String buildId)
        throws SQLException
    {
        HashSet<String> groupList = new HashSet<String>();

        StringBuffer sqlSuite = new StringBuffer();
        sqlSuite.append(
            "SELECT DISTINCT " + SUITE_GROUP_NAME + " FROM " + SUITE_TABLE + 
            " WHERE " + CMnBuildTable.BUILD_ID + "=" + buildId +
            " ORDER BY " + SUITE_GROUP_NAME + " ASC"
        );

        StringBuffer sqlTests = new StringBuffer();
        sqlTests.append(
            "SELECT DISTINCT " + TEST_TABLE + "." + TEST_GROUP_NAME + 
            " FROM " + SUITE_TABLE + ", " + TEST_TABLE +
            " WHERE " + SUITE_TABLE + "." + CMnBuildTable.BUILD_ID + "=" + buildId +
            " AND " + SUITE_TABLE + "." + SUITE_ID + " = " + TEST_TABLE + "." + SUITE_ID + 
            " ORDER BY " + TEST_TABLE + "." + TEST_GROUP_NAME + " ASC" 
        );

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            // Query the suite table for group names
            rs = executeQuery(st, "getGroupNames", sqlSuite.toString());
            if (rs != null) {
                String groupName = null; 
                while (rs.next()) {
                    groupName = rs.getString(SUITE_GROUP_NAME); 
                    if ((groupName != null) && (!groupList.contains(groupName))) {
                        groupList.add(groupName);
                    }
                }
            } else {
                 System.err.println("Unable to obtain the suite data.");
            }

            // Query the unittest table for group names
            rs = executeQuery(st, "getGroupNames", sqlTests.toString());
            if (rs != null) {
                String groupName = null;
                while (rs.next()) {
                    groupName = rs.getString(TEST_GROUP_NAME);
                    if ((groupName != null) && (!groupList.contains(groupName))) {
                        groupList.add(groupName);
                    }
                }
            } else {
                 System.err.println("Unable to obtain the test data.");
            }

        } catch (SQLException ex) {
            System.err.println("Failed to obtain suite data.");
            ex.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return groupList;
    }


    /**
     * Delete the unit test suite and all associated unit tests 
     *
     * @param   conn    Database connection
     * @param   suiteId Primary key used to locate the suite info
     *
     * @return  Deleted suite information
     */
    public synchronized CMnDbTestSuite deleteSuite(
            Connection conn,
            String suiteId)
        throws SQLException
    {
        // Obtain the suite data that will be deleted
        CMnDbTestSuite suite = getSuite(conn, suiteId);

        // SQL for deleting all unit tests associated with the suite
        StringBuffer sqlTests = new StringBuffer();
        sqlTests.append(
            "DELETE FROM " + TEST_TABLE +
            " WHERE " + SUITE_ID + "=" + suiteId
        );

        // SQL for deleting the suite
        StringBuffer sqlSuite = new StringBuffer();
        sqlSuite.append(
            "DELETE FROM " + SUITE_TABLE +
            " WHERE " + SUITE_ID + "=" + suiteId
        );

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            execute(st, "deleteSuite", sqlTests.toString());
            execute(st, "deleteSuite", sqlSuite.toString());
        } catch (SQLException ex) {
            suite = null;
            ex.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return suite;
    }


    /**
     * Delete all unit test suites and all associated unit tests
     * for the specified build.
     *
     * @param   conn    Database connection
     * @param   buildId Primary key used to locate the suite info
     */
    public synchronized void deleteAllSuites(
            Connection conn,
            String buildId)
        throws SQLException
    {
        // SQL for deleting all unit tests associated with the suite
        StringBuffer sql = new StringBuffer();
        sql.append(
            "DELETE FROM " + SUITE_TABLE + ", " + TEST_TABLE + 
            " WHERE " + SUITE_TABLE + "." + CMnBuildTable.BUILD_ID + " = " + buildId +
            "  AND  " + SUITE_TABLE + "." + SUITE_ID + " = " + TEST_TABLE + "." + SUITE_ID 
        );

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            execute(st, "deleteAllSuites", sql.toString());
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }
    }


    /**
     * Return the grouping criteria used in a WHERE clause to determine
     * if a test belongs to the specified group.
     *
     * @param  group   Group name
     * @return Portion of the where clause to be used in a SQL query
     */
    private String getGroupCriteria(String group) {
        StringBuffer sql = new StringBuffer();

        // Include the group criteria
        if (group != null) {
            String testGroupNull = "";
            String testGroupNotNull = "";
            if (useTestGroupName) {
                testGroupNull = "(" + TEST_TABLE + "." + TEST_GROUP_NAME + " IS NULL) AND ";
                testGroupNotNull = "(" + TEST_TABLE + "." + TEST_GROUP_NAME + " IS NOT NULL) AND ";
            }
            sql.append(
                "   AND (" +
                "     (" + testGroupNull + "(" + SUITE_TABLE + "." + SUITE_GROUP_NAME + "='" + group + "'"  + ")) " +
                "     OR " +
                "     (" + testGroupNotNull + "(" + TEST_TABLE + "." + TEST_GROUP_NAME + " ='" + group + "')) " +
                "   )");
        } else {
            if (useTestGroupName) {
                sql.append(" AND " + TEST_TABLE + "." + TEST_GROUP_NAME + " IS NULL ");
            }
            sql.append(" AND " + SUITE_TABLE + "." + SUITE_GROUP_NAME + " IS NULL ");
        }

        return sql.toString();
    }


    /**
     * Convert the test execution status to a string.
     *
     * @param   test    test data
     * @return  Completion status
     */
    public static String getTestStatus(CMnDbTestData test) {
        switch (test.getStatus()) {
            case CMnDbTestData.ERROR:
                return "ERROR";
            case CMnDbTestData.PASS:
                return "PASS";
            case CMnDbTestData.FAIL:
                return "FAIL";
            case CMnDbTestData.SKIP:
                return "SKIP";
            case CMnDbTestData.KILL:
                return "KILL";
            case CMnDbTestData.PENDING:
                return "PENDING";
            case CMnDbTestData.RUNNING:
                return "RUNNING";
            case CMnDbTestData.BLACKLIST:
                return "BLACKLIST";
            default:
                // If we don't know what to do, signal error
                return "ERROR";
        }
    }

    /**
     * Convert the unit test status from a string to an integer.
     * The integer values are defined in CMnDbUnitTestData.
     *
     * @param   status   Status string
     * @return  Integer value of the test status
     */
    public static int getTestStatus(String status) {
        if (status.equalsIgnoreCase(STATUS_PASS)) {
            return CMnDbTestData.PASS;
        } else if (status.equalsIgnoreCase(STATUS_FAIL)) {
            return CMnDbTestData.FAIL;
        } else if (status.equalsIgnoreCase(STATUS_ERROR)) {
            return CMnDbTestData.ERROR;
        } else if (status.equalsIgnoreCase(STATUS_SKIP)) {
            return CMnDbTestData.SKIP;
        } else if (status.equalsIgnoreCase(STATUS_KILL)) {
            return CMnDbTestData.KILL;
        } else if (status.equalsIgnoreCase(STATUS_PENDING)) {
            return CMnDbTestData.PENDING;
        } else if (status.equalsIgnoreCase(STATUS_RUNNING)) {
            return CMnDbTestData.RUNNING;
        } else if (status.equalsIgnoreCase(STATUS_BLACKLIST)) {
            return CMnDbTestData.BLACKLIST;
        } else {
            return CMnDbTestData.UNKNOWN_STATUS;
        }
    }


    /**
     * Returns the list of options as a string of name/value pairs.
     *
     * @param  options  List of name value pairs
     * @return List of option name/value pairs
     */
    public static String getOptionsAsString(Map options) {
        StringBuffer list = new StringBuffer();

        Object name = null;
        Object value = null;
        Iterator itKeys = options.keySet().iterator();
        while (itKeys.hasNext()) {
            name = itKeys.next();
            value = options.get(name);
            list.append(name.toString() + "=" + value.toString() + "\n");
        }

        return list.toString();
    }

    /**
     * Parses an option string, splitting the name value pairs into the
     * internal option representation.  The name/value pairs must be in
     * the format of:
     * <pre>
     *   name1=value1
     *   name2=value2
     * </pre>
     * Each name/value pair must be delimited by a newline.
     *
     * @param  options  Delimited list of option name/value pairs
     * @return List of name value pairs
     */
    public static Map parseOptions(String options) {
        HashMap map = new HashMap();
        if ((options != null) && (options.length() > 0)) {
            StringTokenizer pairs = new StringTokenizer(options, "\n");

            String current = null;
            String name = null;
            String value = null;
            while (pairs.hasMoreTokens()) {
                current = pairs.nextToken();
                if ((current != null) && (current.length() > 0)) {
                    int idxEq = current.indexOf("=");
                    if ((idxEq > 0) && (idxEq < current.length())) {
                        // Format: name=value
                        name = current.substring(0, idxEq);
                        value = current.substring(idxEq + 1);
                        if (value == null) {
                            value = "";
                        }
                    } else if (idxEq < 0) {
                        // Format: name
                        name = current;
                        value = "";
                    } else {
                        // Format: =value
                        name = "";
                        value = current.substring(1);
                    }
 
                    // Add the current name/value pair to the list
                    map.put(name, value);

                } // if pair length > 0

            } // while has pairs 

        } // if length > 0

        return map;
    }



    /**
     * Query the database and populate the test summary information.
     *
     * @param  conn   Database connection
     * @param  suite  Test suite
     */
    public void updateTestSummary(Connection conn, CMnDbTestSuite suite) throws SQLException {
        suite.setPassingCount(getTestCount(conn, suite, STATUS_PASS));
        suite.setFailingCount(getTestCount(conn, suite, STATUS_FAIL));
        suite.setErrorCount(getTestCount(conn, suite, STATUS_ERROR));
        suite.setKilledCount(getTestCount(conn, suite, STATUS_KILL));
        suite.setSkipCount(getTestCount(conn, suite, STATUS_SKIP));
        suite.setPendingCount(getTestCount(conn, suite, STATUS_PENDING));
        suite.setRunningCount(getTestCount(conn, suite, STATUS_RUNNING));
        suite.setBlacklistCount(getTestCount(conn, suite, STATUS_BLACKLIST));
        suite.setExecutedCount(getTestCount(conn, suite, null));
        suite.setLongCount(getLongCount(conn, suite));
        suite.setElapsedTime(getElapsedTime(conn, suite));

        // Compensate for older builds which don't have a
        // test count associated with a suite
        if (suite.getTestCount() == 0) {
            suite.setTestCount(suite.getExecutedCount());
        }

    }


    /**
     * Parse the result set to obtain build information.
     *
     * @param   rs    Result set containing build data
     *
     * @return  Build information
     */
    public CMnDbBuildData parseBuildData(ResultSet rs)
        throws SQLException
    {
        CMnDbBuildData data = new CMnDbBuildData();

        int id = rs.getInt(CMnBuildTable.BUILD_TABLE + "." + CMnBuildTable.BUILD_ID);
        data.setId(id);

        Date startTime = rs.getTimestamp(CMnBuildTable.BUILD_TABLE + "." + CMnBuildTable.BUILD_START_DATE);
        data.setStartTime(startTime);

        Date endTime = rs.getTimestamp(CMnBuildTable.BUILD_TABLE + "." + CMnBuildTable.BUILD_END_DATE);
        data.setEndTime(endTime);

        String version = rs.getString(CMnBuildTable.BUILD_TABLE + "." + CMnBuildTable.BUILD_VERSION);
        data.setBuildVersion(version);

        String status = rs.getString(CMnBuildTable.BUILD_TABLE + "." + CMnBuildTable.BUILD_STATUS);
        data.setReleaseId(status);

        String srcType = rs.getString(CMnBuildTable.BUILD_TABLE + "." + CMnBuildTable.BUILD_SOURCE_TYPE);
        data.setVersionControlType(srcType);

        String srcRoot = rs.getString(CMnBuildTable.BUILD_TABLE + "." + CMnBuildTable.BUILD_SOURCE_ROOT);
        data.setVersionControlRoot(srcRoot);

        String srcId = rs.getString(CMnBuildTable.BUILD_TABLE + "." + CMnBuildTable.BUILD_SOURCE_ID);
        data.setVersionControlId(srcId);

        String downloadUri = rs.getString(CMnBuildTable.BUILD_TABLE + "." + CMnBuildTable.BUILD_DOWNLOAD_URI);
        data.setDownloadUri(downloadUri);

        String jobUrl = rs.getString(CMnBuildTable.BUILD_TABLE + "." + CMnBuildTable.BUILD_JOB_URL);
        data.setJobUrl(jobUrl);

        String comments = rs.getString(CMnBuildTable.BUILD_TABLE + "." + CMnBuildTable.BUILD_COMMENTS);
        data.setComments(comments);

        CMnDbHostData host = CMnHostTable.parseHostData(rs);
        data.setHostData(host);

        return data;
    }


    /**
     * Parse the values in the result set and populate the test data object.
     *
     * @param   rs    Result set containing test data
     * @param   data  Data object
     */
    public void parseTestData(ResultSet rs, CMnDbTestData data)
        throws SQLException
    {
        int id = rs.getInt(TEST_ID);
        data.setId(id);

        int parentId = rs.getInt(TEST_TABLE + "." + SUITE_ID);
        data.setParentId(parentId);

        if (useTestGroupName) {
            String groupName = rs.getString(TEST_TABLE + "." + TEST_GROUP_NAME);
            data.setGroupName(groupName);
        }

        Date startTime = rs.getTimestamp(TEST_TABLE + "." + TEST_START_DATE);
        data.setStartTime(startTime);

        Date endTime = rs.getTimestamp(TEST_TABLE + "." + TEST_END_DATE);
        data.setEndTime(endTime);

        String message = rs.getString(TEST_TABLE + "." + TEST_MESSAGE);
        data.addMessage(message);

        String strStatus = rs.getString(TEST_TABLE + "." + TEST_STATUS);
        int status = getTestStatus(strStatus);
        data.setStatus(status);
    }

    /**
     * Parse the result set to obtain test information.
     *
     * @param   rs    Result set containing test data
     * 
     * @return  Test suite data
     */
    public CMnDbTestData parseTestData(ResultSet rs)
        throws SQLException
    {
        CMnDbTestData data = new CMnDbTestData();
        parseTestData(rs, data);
        return data;
    }


    /**
     * Parse the result set to obtain suite information.
     * 
     * @param   rs    Result set containing suite data
     * @param   data  Suite data
     */
    public void parseSuiteData(ResultSet rs, CMnDbTestSuite data) 
        throws SQLException 
    {
        int id = rs.getInt(SUITE_TABLE + "." + SUITE_ID);
        data.setId(id);

        long groupId = rs.getLong(SUITE_TABLE + "." + SUITE_GROUP_ID);
        data.setGroupId(groupId);

        String groupName = rs.getString(SUITE_TABLE + "." + SUITE_GROUP_NAME);
        data.setGroupName(groupName);

        int parentId = rs.getInt(CMnBuildTable.BUILD_ID);
        data.setParentId(parentId);

        Date startTime = rs.getTimestamp(SUITE_TABLE + "." + SUITE_START_DATE);
        data.setStartTime(startTime);

        Date endTime = rs.getTimestamp(SUITE_TABLE + "." + SUITE_END_DATE);
        data.setEndTime(endTime);

        int testCount = rs.getInt(SUITE_TABLE + "." + SUITE_TEST_COUNT);
        data.setTestCount(testCount);

        String name = rs.getString(SUITE_TABLE + "." + SUITE_NAME);
        data.setSuiteName(name);

        String url = rs.getString(SUITE_TABLE + "." + SUITE_JDBC_URL);
        data.setJdbcUrl(url);

        String options = rs.getString(SUITE_TABLE + "." + SUITE_OPTIONS);
        data.setOptions(parseOptions(options));

        CMnDbHostData host = CMnHostTable.parseHostData(rs);
        data.setHostData(host);

        String envName = rs.getString(SUITE_TABLE + "." + SUITE_ENVIRONMENT);
        data.setEnvironmentName(envName);

        int maxThreads = rs.getInt(SUITE_TABLE + "." + SUITE_MAX_THREADS);
        data.setMaxThreadCount(maxThreads);
    }


}

