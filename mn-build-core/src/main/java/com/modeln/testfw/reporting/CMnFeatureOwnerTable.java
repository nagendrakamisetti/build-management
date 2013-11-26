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
import java.util.StringTokenizer;
import java.util.Vector;
import java.text.SimpleDateFormat;


/**
 * The feature owner table interface defines a list of product areas and
 * the list of features that belong to them. 
 * 
 * @author  Shawn Stafford
 */
public class CMnFeatureOwnerTable extends CMnTable {


    /** Name of the table used for the product areas */
    public static final String AREA_TABLE = "feature_area";

    /** Name of the column that identifies the area by ID */
    public static final String AREA_ID = "area_id";

    /** Name of the column that identifies the area by name */
    public static final String AREA_NAME = "area_name";

    /** Name of the column that identifies the area description */
    public static final String AREA_DESC = "area_desc";

    /** Name of the column that identifies the email addresses for this area */
    public static final String AREA_EMAIL = "email";



    /** Name of the table used for the product feature mapping */
    public static final String FEATURE_TABLE = "feature_area_map";

    /** Name of the column that identifies the feature by name */
    public static final String FEATURE_NAME = "feature";




    /** Singleton instance of the table class */
    private static CMnFeatureOwnerTable instance;


    /**
     * Return the singleton instance of the class.
     */
    public static CMnFeatureOwnerTable getInstance() {
        if (instance == null) {
            instance = new CMnFeatureOwnerTable();

            // Enable debbuging to a file
            String logfile = "/var/tmp/CMnFeatureOwnerTable.txt";
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
     * Retrieve a list of all product areas from the database.
     * 
     * @param   conn    Database connection
     *
     * @return  List of area information objects
     */
    public synchronized static Vector<CMnDbFeatureOwnerData> getAllAreas(Connection conn)
        throws SQLException
    {
        Vector<CMnDbFeatureOwnerData> list = new Vector<CMnDbFeatureOwnerData>();

        StringBuffer sql = new StringBuffer();
        sql.append("SELECT * FROM " + AREA_TABLE + " ORDER BY " + AREA_NAME + " ASC");

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            getInstance().debugWrite("Attempting to execute: " + sql.toString());
            rs = st.executeQuery(sql.toString());
            if (rs != null) {
                CMnDbFeatureOwnerData area = null;
                while (rs.next()) {
                    area = parseAreaData(rs);

                    // Obtain a list of features for the current area
                    String areaId = Integer.toString(area.getId()); 
                    Vector<String> features = getFeatures(conn, areaId);
                    for (int idx = 0; idx < features.size(); idx++) {
                        area.addFeature(features.get(idx));
                    } 

                    list.add(area);
                }
            } else {
                 System.err.println("Unable to obtain the area data.");
            }
        } catch (SQLException ex) {
            System.err.println("Failed to obtain area data: " + sql.toString());
            ex.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return list;
    }


    /**
     * Retrieve the product area identified by the specified ID. 
     * 
     * @param   conn    Database connection
     * @param   id      Area ID
     *
     * @return  List of area information objects
     */
    public synchronized static CMnDbFeatureOwnerData getArea(Connection conn, String id)
        throws SQLException
    {
        CMnDbFeatureOwnerData data = null;

        StringBuffer sql = new StringBuffer();
        sql.append("SELECT * FROM " + AREA_TABLE + " WHERE " + AREA_ID + "=" + id);

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            getInstance().debugWrite("Attempting to execute: " + sql.toString());
            rs = st.executeQuery(sql.toString());
            if (rs != null) {
                while (rs.next()) {
                    data = parseAreaData(rs);

                    // Obtain a list of features for the current area
                    Vector<String> features = getFeatures(conn, id);
                    for (int idx = 0; idx < features.size(); idx++) {
                        data.addFeature(features.get(idx));
                    }

                }
            } else {
                 System.err.println("Unable to obtain the area data.");
            }
        } catch (SQLException ex) {
            System.err.println("Failed to obtain area data: " + sql.toString());
            ex.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return data;
    }



    /**
     * Retrieve a list of features that belong to a specific area. 
     * 
     * @param   conn    Database connection
     * @param   areaId  Foreign key used to look up features by area 
     *
     * @return  List of product areas
     */
    public synchronized static Vector<String> getFeatures(
            Connection conn,
            String areaId) 
        throws SQLException
    {
        Vector<String> features = new Vector<String>();

        StringBuffer sql = new StringBuffer();
        sql.append(
            "SELECT * FROM " + FEATURE_TABLE +
            " WHERE " + AREA_ID + "=" + areaId +
            " ORDER BY " + FEATURE_NAME + " ASC"
        );

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            getInstance().debugWrite("Attempting to execute query: " + sql.toString());
            rs = st.executeQuery(sql.toString());
            if (rs != null) {
                while (rs.next()) {
                    features.add(rs.getString(FEATURE_NAME));
                }
            } else {
                 System.err.println("Unable to obtain the feature data.");
            }
        } catch (SQLException ex) {
            System.err.println("Failed to obtain feature data: " + sql.toString());
            ex.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return features;
    }


    /**
     * Parse the result set to obtain area information.
     * 
     * @param   rs    Result set containing area data
     *
     * @return  Area information
     */
    public static CMnDbFeatureOwnerData parseAreaData(ResultSet rs)
        throws SQLException
    {
        CMnDbFeatureOwnerData data = new CMnDbFeatureOwnerData();

        int id = rs.getInt(AREA_ID);
        data.setId(id);

        String areaname = rs.getString(AREA_NAME);
        data.setDisplayName(areaname);

        String emailstr = rs.getString(AREA_EMAIL);
        if ((emailstr != null) && (emailstr.length() > 0)) {
            StringTokenizer st = new StringTokenizer(emailstr, ",");
            while (st.hasMoreTokens()) {
                data.addEmail(st.nextToken().trim());
            }
        }

        return data;
    }



}


