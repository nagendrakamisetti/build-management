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

import com.modeln.build.common.data.product.CMnBuildReviewData;
import com.modeln.testfw.reporting.CMnTable;

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

import com.modeln.build.common.data.account.UserData;


/**
 * The feature owner table interface defines a list of product areas and
 * the list of features that belong to them. 
 * 
 * @author  Shawn Stafford
 */
public class CMnReviewTable extends CMnTable {


    /** Name of the table used for the product area reaviews */
    public static final String REVIEW_TABLE = "feature_review";

    /** Name of the column that identifies the area associated with the review */
    public static final String AREA_ID = "area_id";

    /** Name of the column that identifies the build associated with the review */
    public static final String BUILD_ID = "build_id";

    /** Name of the column that identifies the user associated with the review */
    public static final String USER_ID = "user_id";

    /** Name of the column that identifies the date when the review was made */
    public static final String REVIEW_DATE = "review_date";

    /** Name of the column that identifies the status of the item being reviewed */
    public static final String REVIEW_STATUS = "status"; 

    /** Name of the column that identifies the review comment */
    public static final String REVIEW_COMMENT = "comment";




    /** Singleton instance of the table class */
    private static CMnReviewTable instance;


    /**
     * Return the singleton instance of the class.
     */
    public static CMnReviewTable getInstance() {
        if (instance == null) {
            instance = new CMnReviewTable();

            // Enable debbuging to a file
            String logfile = "/var/tmp/CMnReviewTable.txt";
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
     * Retrieve a list of reviews for the specified build. 
     * 
     * @param   conn    Database connection
     * @param   buildId Build ID used to find reviews 
     *
     * @return  List of reviews 
     */
    public synchronized static Vector<CMnBuildReviewData> getReviews(
            Connection conn,
            String buildId)
        throws SQLException
    {
        Vector<CMnBuildReviewData> reviews = new Vector<CMnBuildReviewData>();

        StringBuffer sql = new StringBuffer();
        sql.append(
            "SELECT * FROM " + REVIEW_TABLE +
            " WHERE " + BUILD_ID + "=" + buildId
        );

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            getInstance().debugWrite("Attempting to execute query: " + sql.toString());
            rs = st.executeQuery(sql.toString());
            if (rs != null) {
                while (rs.next()) {
                    reviews.add(parseReviewData(rs));
                }
            } else {
                getInstance().debugWrite("Unable to obtain the feature data.");
            }
        } catch (SQLException ex) {
            getInstance().debugWrite("Failed to obtain review data: " + sql.toString());
            getInstance().debugWrite(ex);
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return reviews;
    }


    /**
     * Retrieve a list of reviews for the specified build. 
     * 
     * @param   conn    Database connection
     * @param   buildId Build ID used to find reviews 
     * @param   areaId  Area ID
     *
     * @return  List of reviews 
     */
    public synchronized static Vector<CMnBuildReviewData> getReviews(
            Connection conn,
            String buildId,
            String areaId)
        throws SQLException
    {
        Vector<CMnBuildReviewData> reviews = new Vector<CMnBuildReviewData>();

        StringBuffer sql = new StringBuffer();
        sql.append(
            "SELECT * FROM " + REVIEW_TABLE +
            " WHERE " + BUILD_ID + "=" + buildId +
            " AND " + AREA_ID + "=" + areaId
        );

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            getInstance().debugWrite("Attempting to execute query: " + sql.toString());
            rs = st.executeQuery(sql.toString());
            if (rs != null) {
                getInstance().debugWrite("Returned query results for area " + areaId);
                while (rs.next()) {
                    reviews.add(parseReviewData(rs));
                }
            } else {
                getInstance().debugWrite("Unable to obtain the feature data.");
            }
        } catch (SQLException ex) {
            getInstance().debugWrite("Failed to obtain feature data: " + sql.toString());
            getInstance().debugWrite(ex);
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return reviews;
    }

    /**
     * Add a new review to the database.
     *
     * @param  conn     Database connection
     * @param  review   Review information 
     * @return TRUE if the data was added successfully
     */
    public synchronized static boolean addReview(Connection conn, CMnBuildReviewData review)
        throws SQLException
    {
        boolean success = true;

        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO " + REVIEW_TABLE + " ");

        sql.append("(" + AREA_ID);
        sql.append(", " + BUILD_ID);
        sql.append(", " + USER_ID);
        sql.append(", " + REVIEW_STATUS);
        sql.append(", " + REVIEW_DATE);
        if (review.getComment() != null) {
            sql.append(", " + REVIEW_COMMENT);
        }

        sql.append(") VALUES ");

        sql.append("(\"" + review.getAreaId() + "\"");
        sql.append(", \"" + review.getBuildId() + "\"");
        sql.append(", \"" + review.getUser().getUid() + "\"");
        sql.append(", \"" + review.getStatus().toString() + "\"");
        sql.append(", NOW() ");
        if (review.getComment() != null) {
            sql.append(", \"" + escapeQueryText(review.getComment()) + "\"");
        }
        sql.append(")");

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            getInstance().debugWrite("Attempting to execute: " + sql.toString());
            st.execute(sql.toString());
        } catch (SQLException ex) {
            success = false;
            getInstance().debugWrite("Failed to add review data: " + sql.toString());
            getInstance().debugWrite(ex);
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return success;
    }


    /**
     * Update an existing review in the database.
     *
     * @param  conn     Database connection
     * @param  review   Review information 
     * @return TRUE if the data was updated successfully
     */
    public synchronized static boolean updateReview(Connection conn, CMnBuildReviewData review)
        throws SQLException
    {
        boolean success = true;

        StringBuffer sql = new StringBuffer();
        sql.append("UPDATE " + REVIEW_TABLE + " ");
        sql.append("SET " + REVIEW_STATUS + "=\"" + review.getStatus().toString() + "\"");
        sql.append(", " + REVIEW_DATE + "= NOW() ");
        if (review.getComment() != null) {
            sql.append(", " + REVIEW_COMMENT + "=\"" + review.getComment() + "\"");
        }
        sql.append("WHERE " + BUILD_ID + "=\"" + review.getBuildId() + "\"");
        sql.append(" AND " + AREA_ID + "=\"" + review.getAreaId() + "\"");
        sql.append(" AND " + USER_ID + "=\"" + review.getUser().getUid() + "\""); 

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            getInstance().debugWrite("Attempting to execute: " + sql.toString());
            st.execute(sql.toString());
        } catch (SQLException ex) {
            success = false;
            getInstance().debugWrite("Failed to update review data: " + sql.toString());
            getInstance().debugWrite(ex);
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return success;
    }



    /**
     * Parse the result to obtain feature review information.
     * 
     * @param   rs   Result set containing the review data
     *
     * @return  Review information
     */
    public static CMnBuildReviewData parseReviewData(ResultSet rs) 
        throws SQLException
    {
        CMnBuildReviewData data = new CMnBuildReviewData();

        int buildId = rs.getInt(REVIEW_TABLE + "." + BUILD_ID);
        data.setBuildId(buildId);

        int areaId = rs.getInt(REVIEW_TABLE + "." + AREA_ID);
        data.setAreaId(areaId);

        Date date = rs.getTimestamp(REVIEW_DATE);
        data.setDate(date);

        int userId = rs.getInt(REVIEW_TABLE + "." + USER_ID);
        UserData user = new UserData();
        user.setUid(Integer.toString(userId));
        data.setUser(user);

        String status = rs.getString(REVIEW_TABLE + "." + REVIEW_STATUS);
        data.setStatus(status);

        String text = rs.getString(REVIEW_TABLE + "." + REVIEW_COMMENT);
        data.setComment(text);

        return data;
    }


}


