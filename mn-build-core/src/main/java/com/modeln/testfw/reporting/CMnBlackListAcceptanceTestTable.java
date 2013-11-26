package com.modeln.testfw.reporting;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: vmalley
 * Date: 9/16/11
 * Time: 2:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class CMnBlackListAcceptanceTestTable extends CMnBlackListTable {



    /** Name of the column for the test file */
    public static final String TEST_FILE = "filename";

    /** Singleton instance of the table class */
    private static CMnBlackListAcceptanceTestTable instance;


    /**
     * Constructor to prevent the class from being constructed without table names.
     */
    protected CMnBlackListAcceptanceTestTable() {
        TEST_TABLE = "act_blacklist";
    }

    /**
     * Return the singleton instance of the class.
     */
    public static CMnBlackListAcceptanceTestTable getInstance() {
        if (instance == null) {
            // Construct a new singleton instance
            instance = new CMnBlackListAcceptanceTestTable();

            // Enable debbuging to a file
            String logfile = "/var/tmp/CMnBlackListAcceptanceTestTable.txt";
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
     * @param   test    Test data
     *
     * @return  Auto-generated key that identifies the test
     */
    public synchronized String addTest(
            Connection conn,
            CMnDbBlackListAcceptanceTestData test)
            throws SQLException
    {
        String testId = null;

        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO " + TEST_TABLE + " ");
        sql.append("(");

        sql.append(TEST_FILE);
        sql.append(", " + TEST_TIMEOUT);
        if (test.getStartTime() != null) sql.append(", " + TEST_START_DATE);
        if (test.getEndTime()   != null) sql.append(", " + TEST_END_DATE);
        if (test.getMessage() != null) sql.append(", " + TEST_MESSAGE);
        if (test.getHostname() != null) sql.append(", " + TEST_HOSTNAME);
        if (test.getVersionCtrl() != null) sql.append(", " + TEST_VERSION_CTRL);

        sql.append(") VALUES (");
        sql.append("\"" + test.getFileName() + "\"");
        sql.append(", \"" + test.getTimeout() + "\"");
        if (test.getStartTime() != null) sql.append(", \"" + DATETIME.format(test.getStartTime()) + "\"");
        if (test.getEndTime()   != null) sql.append(", \"" + DATETIME.format(test.getEndTime())   + "\"");
        sql.append(", \"" + escapeQueryText(test.getMessage(), TEXT_SIZE) + "\"");
        sql.append(", \"" + test.getHostname() + "\"");
        sql.append(", \"" + test.getVersionCtrl() + "\"");
        sql.append(")");

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            testId = executeInsert(st, "addTest", sql.toString());
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
     * Remove an ACT test result from the database.
     *
     * @param   conn    Database connection
     * @param   testFile  Primary key used to locate the test info
     * @param   testVersion  Source control branch 
     */
    public synchronized void deleteTest(
            Connection conn,
            String testFile,
            String testVersion)
            throws SQLException
    {

        StringBuffer sql = new StringBuffer();
        sql.append(
            "DELETE FROM " + TEST_TABLE +
            " WHERE " + TEST_FILE + "=\"" + testFile + "\"" +
            "  AND " + TEST_VERSION_CTRL + "=\"" + testVersion + "\""
        );

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            execute(st, "deleteTest", sql.toString());
        } catch (SQLException ex) {
            System.err.println("Failed to remove blacklist data: " + sql.toString());
            ex.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }
    }


    /**
     * Return the number of tests that match this criteria. 
     *
     * @param   conn    Database connection
     * @param   testFile  Primary key used to locate the test info
     * @param   testVersion  Source control branch 
     *
     * @return  Test count
     */
    public synchronized int getTestCount(
            Connection conn,
            String testFile,
            String testVersion)
            throws SQLException
    {
        int count = 0;

        StringBuffer sql = new StringBuffer();
        sql.append(
            "SELECT COUNT(*) FROM " + TEST_TABLE +
            " WHERE " + TEST_FILE + "=\"" + testFile + "\"" +
            "  AND " + TEST_VERSION_CTRL + "=\"" + testVersion + "\""
        );

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            rs = executeQuery(st, "getTestCount", sql.toString());
            if (rs != null) {
                rs.first();
                count = rs.getInt(1);
            } else {
                 System.err.println(
                     "Unable to obtain the total number of tests for " +
                     testFile + " @ " + testVersion);
            }
        } catch (SQLException ex) {
            System.err.println("Failed to obtain blacklist data: " + sql.toString());
            ex.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return count;
    }



    /**
     * Retrieve ACT test information from the database.
     *
     * @param   conn    Database connection
     * @param   testFile  Primary key used to locate the test info
     * @param   testVersion  Primary key used to locate the test info
     *
     * @return  Test information
     */
    public synchronized CMnDbBlackListTestData getTest(
            Connection conn,
            String testFile,
            String testVersion)
            throws SQLException
    {
        CMnDbBlackListTestData test = null;

        StringBuffer sql = new StringBuffer();
        sql.append(
            "SELECT * FROM " + TEST_TABLE +
            " WHERE " + TEST_FILE + "=\"" + testFile + "\"" +
            "  AND " + TEST_VERSION_CTRL + "=\"" + testVersion + "\"" +
            " ORDER BY " + TEST_START_DATE + " DESC"
        );

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            rs = executeQuery(st, "getTest", sql.toString());
            if ((rs != null) && (rs.first() == true)) {
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
     * Parse the result set to obtain test information.
     *
     * @param   rs    Result set containing test data
     *
     * @return  Unit Test information
     */

    public CMnDbBlackListTestData parseTestData(ResultSet rs)
            throws SQLException
    {
        CMnDbBlackListAcceptanceTestData data = new CMnDbBlackListAcceptanceTestData();
        parseTestData(rs, data);

        String filename = rs.getString(TEST_FILE);
        data.setFileName(filename);

        return data;
    }


}

