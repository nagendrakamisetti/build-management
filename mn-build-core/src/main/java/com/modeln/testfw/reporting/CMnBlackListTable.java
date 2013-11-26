package com.modeln.testfw.reporting;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: vmalley
 * Date: 9/16/11
 * Time: 1:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class CMnBlackListTable extends CMnTable {


        /** Name of the table used for individual tests */
    protected String TEST_TABLE;

        /** Name of the column for the test timeout */
    public static final String TEST_TIMEOUT = "timeout";

    /** Name of the column for the test start date */
    public static final String TEST_START_DATE = "start_date";

    /** Name of the column for the test end date */
    public static final String TEST_END_DATE = "end_date";

    /** Name of the column for the test source control location  */
    public static final String TEST_VERSION_CTRL = "version_ctrl_root";

    /** Name of the column for the test host name  */
    public static final String TEST_HOSTNAME = "hostname";

    /** Name of the column for an additionnal message  */
    public static final String TEST_MESSAGE = "message";


    /** Singleton instance of the table class */
    private static CMnBlackListTable instance;


    /**
     * Construct the abstract class and set the required table names.
     *
     * @param  test   Test table name
     */
    protected CMnBlackListTable(String test) {
        TEST_TABLE = test;
    }

        /**
     * Constructor to prevent the class from being constructed without table names.
     */
    protected CMnBlackListTable() {
        TEST_TABLE = "blacklist";
    }

    /**
     * Return the singleton instance of the class.
     */
    public static CMnBlackListTable getInstance() {
        if (instance == null) {
            // Construct a new singleton instance
            instance = new CMnBlackListTable();
        }

        return instance;
    }

    /**
     * Retrieve a list of all blacklisted tests. 
     *
     * @param   conn    Database connection
     * @param   testVersion  Source code branch 
     *
     * @return  List of blacklisted tests 
     */
    public synchronized Vector<CMnDbBlackListTestData> getAllTests(
            Connection conn,
            String testVersion)
            throws SQLException
    {
        Vector<CMnDbBlackListTestData> list = new Vector<CMnDbBlackListTestData>();

        StringBuffer sql = new StringBuffer();
        sql.append(
            "SELECT * FROM " + TEST_TABLE +
            " WHERE " + TEST_VERSION_CTRL + "=\"" + testVersion + "\"" +
            " ORDER BY " + TEST_START_DATE + " DESC"
        );

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            rs = executeQuery(st, "getAllTests", sql.toString());
            while ((rs != null) && rs.next()) {
                list.add(parseTestData(rs));
            }
        } catch (SQLException ex) {
            System.err.println("Failed to obtain blacklist data: " + sql.toString());
            ex.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return list;
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
        CMnDbBlackListTestData data = new CMnDbBlackListTestData();
        parseTestData(rs, data);

        return data;
    }



    /**
     * Parse the values in the result set and populate the test data object.
     *
     * @param   rs    Result set containing test data
     * @param   data  Data object
     */
    public void parseTestData(ResultSet rs, CMnDbBlackListTestData data)
        throws SQLException
    {
        int time = rs.getInt(TEST_TIMEOUT);
        data.setTimeout(time);

        Date startTime = rs.getTimestamp(TEST_START_DATE);
        data.setStartTime(startTime);

        Date endTime = rs.getTimestamp(TEST_END_DATE);
        data.setEndTime(endTime);

        String message = rs.getString(TEST_MESSAGE);
        data.addMessage(message);

        String host = rs.getString(TEST_HOSTNAME);
        data.setHostname(host);

        String version = rs.getString(TEST_VERSION_CTRL);
        data.setVersionCtrl(version);
    }



}
