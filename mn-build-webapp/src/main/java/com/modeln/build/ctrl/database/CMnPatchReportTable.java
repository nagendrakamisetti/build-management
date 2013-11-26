/*
* Copyright 2000-2003 by Model N, Inc.  All Rights Reserved.
*
* This software is the confidential and proprietary information
* of Model N, Inc ("Confidential Information").  You shall not
* disclose such Confidential Information and shall use it only
* in accordance with the terms of the license agreement you
* entered into with Model N, Inc.
*/
package com.modeln.build.ctrl.database;

import com.modeln.build.common.data.account.CMnAccount;
import com.modeln.build.common.data.account.CMnEnvironment;
import com.modeln.build.common.data.product.*;
import com.modeln.build.common.database.CMnCustomerTable;
import com.modeln.build.common.enums.CMnServicePatch;
import com.modeln.testfw.reporting.CMnBuildTable;
import com.modeln.testfw.reporting.CMnDbBuildData;
import com.modeln.testfw.reporting.CMnTable;
import com.modeln.testfw.reporting.search.CMnSearchCriteria;
import com.modeln.testfw.reporting.search.CMnSearchGroup;
import com.modeln.build.common.data.account.GroupData;
import com.modeln.build.common.data.account.UserData;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;


/**
 * The patch report table queries multiple tables to generate data that
 * is useful for statistical analysis and reports on service patch usage. 
 *
 * @author  Shawn Stafford
 */
public class CMnPatchReportTable extends CMnTable {


    /** Singleton instance of the table class */
    private static CMnPatchReportTable instance;

    /**
     * Return the singleton instance of the class.
     */
    public static CMnPatchReportTable getInstance() {
        if (instance == null) {
            instance = new CMnPatchReportTable();

            // Enable debbuging to a file
            String logfile = "/var/tmp/CMnPatchReportTable.txt";
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
     * Retrieve a list of all customers from the database and the number
     * of patches for each customer.
     *
     * @param   conn    Database connection
     * @param   start   Lowest patch request date
     * @param   end     Highest patch request date
     *
     * @return  List of customer account objects and the associated patch count
     */
    public synchronized Hashtable<CMnAccount, Integer> getCustomerPatchCount(Connection conn, Date start, Date end)
        throws SQLException
    {
        Hashtable<CMnAccount, Integer> list = new Hashtable<CMnAccount, Integer>();

        String countName = "patch_count";

        StringBuffer sql = new StringBuffer();
        sql.append("SELECT " + CMnCustomerTable.ACCOUNT_TABLE + ".*, count(*) AS " + countName);
        sql.append(" FROM " + CMnPatchTable.REQUEST_TABLE + ", " + CMnCustomerTable.ACCOUNT_TABLE);
        sql.append(" WHERE " + CMnPatchTable.REQUEST_TABLE + "." + CMnPatchTable.ACCOUNT_ID + " = " + CMnCustomerTable.ACCOUNT_TABLE + "." + CMnCustomerTable.ACCOUNT_ID);
        if (start != null) {
            sql.append("  AND  " + CMnPatchTable.REQUEST_DATE + " > '" + DATETIME.format(start) + "'");
        }
        if (end != null) {
            sql.append("  AND  " + CMnPatchTable.REQUEST_DATE + " < '" + DATETIME.format(end) + "'");
        }
        sql.append(" GROUP BY " + CMnCustomerTable.ACCOUNT_ID);

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            rs = executeQuery(st, "getCustomerPatchCount", sql.toString());
            if (rs != null) {
                while (rs.next()) {
                    CMnAccount account = CMnCustomerTable.parseCustomerData(rs);
                    int count = rs.getInt(countName);
                    list.put(account, new Integer(count));
                }
            } else {
                getInstance().debugWrite("Unable to obtain the customer data.");
            }
        } catch (SQLException ex) {
            getInstance().debugWrite("Failed to obtain customer data: " + ex.toString());
            getInstance().debugWrite(ex);
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return list;
    }


    /**
     * Retrieve a list of all major releases from the database and the number
     * of patches for each release.
     *
     * @param   conn    Database connection
     * @param   version Version string used in SQL query (i.e. MN-PHARMA-%)
     * @param   start   Lowest patch request date
     * @param   end     Highest patch request date
     *
     * @return  List of releases and the associated patch count
     */
    public synchronized Hashtable<String, Integer> getReleasePatchCount(Connection conn, String version, Date start, Date end)
        throws SQLException
    {
        Hashtable<String, Integer> list = new Hashtable<String, Integer>();

        String releaseName = "release_name";
        String countName = "patch_count";
        String leftClause = "left(" + CMnBuildTable.BUILD_VERSION + ", " + version.length() + ")";

        StringBuffer sql = new StringBuffer();
        sql.append("SELECT " + leftClause + " AS " + releaseName + ", count(*) AS " + countName);
        sql.append(" FROM " + CMnBuildTable.BUILD_TABLE + ", " + CMnPatchTable.REQUEST_TABLE);
        sql.append(" WHERE " + CMnBuildTable.BUILD_TABLE + "." + CMnBuildTable.BUILD_ID + " = " + CMnPatchTable.REQUEST_TABLE + "." + CMnPatchTable.BUILD_ID);
        sql.append(" AND " + CMnBuildTable.BUILD_VERSION + " LIKE '" + version + "'");
        if (start != null) {
            sql.append("  AND  " + CMnPatchTable.REQUEST_DATE + " > '" + DATETIME.format(start) + "'");
        }
        if (end != null) {
            sql.append("  AND  " + CMnPatchTable.REQUEST_DATE + " < '" + DATETIME.format(end) + "'");
        }
        sql.append(" GROUP BY " + leftClause);

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            rs = executeQuery(st, "getReleasePatchCount", sql.toString());
            if (rs != null) {
                while (rs.next()) {
                    String release = rs.getString(releaseName);
                    int count = rs.getInt(countName);
                    list.put(release, new Integer(count));
                }
            } else {
                getInstance().debugWrite("Unable to obtain the release data.");
            }
        } catch (SQLException ex) {
            getInstance().debugWrite("Failed to obtain release data: " + ex.toString());
            getInstance().debugWrite(ex);
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return list;
    }



    /**
     * Retrieve a list of all customers from the database and the number
     * of SDRs for each customer.  The number of SDRs is calculated by
     * considering only the 
     *
     * @param   conn    Database connection
     * @param   start   Lowest patch request date
     * @param   end     Highest patch request date
     *
     * @return  List of customer account objects and the associated SDR count
     */
    public synchronized Hashtable<CMnAccount, Integer> getCustomerFixCount(Connection conn, Date start, Date end)
        throws SQLException
    {
        Hashtable<CMnAccount, Integer> list = new Hashtable<CMnAccount, Integer>();

        String countName = "fix_count";

        StringBuffer sql = new StringBuffer();

        sql.append("SELECT " + CMnCustomerTable.ACCOUNT_TABLE + ".*, count(*) AS " + countName);
        sql.append(" FROM " + CMnPatchTable.REQUEST_TABLE + ", " + CMnCustomerTable.ACCOUNT_TABLE + ", " + CMnPatchTable.FIX_TABLE);
        sql.append(" WHERE " + CMnPatchTable.REQUEST_TABLE + "." + CMnPatchTable.ACCOUNT_ID + " = " + CMnCustomerTable.ACCOUNT_TABLE + "." + CMnCustomerTable.ACCOUNT_ID);
        sql.append(" AND " + CMnPatchTable.FIX_TABLE + "." + CMnPatchTable.FIX_ORIGIN + " = " + CMnPatchTable.FIX_TABLE + "." + CMnPatchTable.REQUEST_ID); 

        sql.append(" AND " + CMnPatchTable.REQUEST_TABLE + "." + CMnPatchTable.REQUEST_ID + " = " + CMnPatchTable.FIX_TABLE + "." + CMnPatchTable.REQUEST_ID);

        if (start != null) {
            sql.append("  AND  " + CMnPatchTable.REQUEST_DATE + " > '" + DATETIME.format(start) + "'");
        }
        if (end != null) {
            sql.append("  AND  " + CMnPatchTable.REQUEST_DATE + " < '" + DATETIME.format(end) + "'");
        }
        sql.append(" GROUP BY " + CMnCustomerTable.ACCOUNT_ID);


        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            rs = executeQuery(st, "getCustomerFixCount", sql.toString());
            if (rs != null) {
                while (rs.next()) {
                    CMnAccount account = CMnCustomerTable.parseCustomerData(rs);
                    int count = rs.getInt(countName);
                    list.put(account, new Integer(count));
                }
            } else {
                getInstance().debugWrite("Unable to obtain the customer data.");
            }
        } catch (SQLException ex) {
            getInstance().debugWrite("Failed to obtain customer data: " + ex.toString());
            getInstance().debugWrite(ex);
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return list;
    }


    /**
     * Retrieve a list of fixes associated with any patch requests created
     * within the date boundaries. 
     *
     * @param   conn    Database connection
     * @param   start   Lowest patch request date
     * @param   end     Highest patch request date
     */
    public synchronized Vector<Integer> getFixes(Connection conn, Date start, Date end)
        throws SQLException
    {
        Vector<Integer> list = new Vector<Integer>();

        StringBuffer sql = new StringBuffer();
        sql.append("SELECT DISTINCT " + CMnPatchTable.FIX_BUG_ID);
        sql.append(" FROM " + CMnBuildTable.BUILD_TABLE + ", " + CMnPatchTable.REQUEST_TABLE + ", " + CMnPatchTable.FIX_TABLE);
        sql.append(" WHERE " + CMnPatchTable.REQUEST_TABLE + "." + CMnPatchTable.REQUEST_ID + " = " + CMnPatchTable.FIX_TABLE + "." + CMnPatchTable.REQUEST_ID); 
        if (start != null) {
            sql.append("  AND  " + CMnPatchTable.REQUEST_DATE + " > '" + DATETIME.format(start) + "'");
        }
        if (end != null) {
            sql.append("  AND  " + CMnPatchTable.REQUEST_DATE + " < '" + DATETIME.format(end) + "'");
        }


        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            rs = executeQuery(st, "getFixes", sql.toString());
            if (rs != null) {
                while (rs.next()) {
                    int bugId = rs.getInt(CMnPatchTable.FIX_BUG_ID);
                    list.add(new Integer(bugId)); 
                }
            } else {
                getInstance().debugWrite("Unable to obtain the fix data.");
            }
        } catch (SQLException ex) {
            getInstance().debugWrite("Encountered exception while parsing fix data: " + ex.toString());
            getInstance().debugWrite(ex);
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return list;
    }


    /**
     * Retrieve a list of service patch dates. 
     *
     * @param   conn    Database connection
     * @param   start   Lowest patch request date
     * @param   end     Highest patch request date
     */
    public synchronized Vector<Date> getRequestDate(Connection conn, Date start, Date end)
        throws SQLException
    {
        Vector<Date> list = new Vector<Date>();

        StringBuffer sql = new StringBuffer();
        sql.append("SELECT " + CMnPatchTable.REQUEST_DATE);
        sql.append(" FROM " + CMnPatchTable.REQUEST_TABLE);
        sql.append(" WHERE " + CMnPatchTable.REQUEST_DATE + " > '" + DATETIME.format(start) + "'");
        sql.append("  AND  " + CMnPatchTable.REQUEST_DATE + " < '" + DATETIME.format(end) + "'");

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            rs = executeQuery(st, "getRequestDate", sql.toString());
            if (rs != null) {
                while (rs.next()) {
                    Date requestDate = rs.getDate(CMnPatchTable.REQUEST_DATE);
                    list.add(requestDate);
                }
            } else {
                getInstance().debugWrite("Unable to obtain the request date.");
            }
        } catch (SQLException ex) {
            getInstance().debugWrite("Encountered exception while parsing request date: " + ex.toString());
            getInstance().debugWrite(ex);
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return list;
    }


}

