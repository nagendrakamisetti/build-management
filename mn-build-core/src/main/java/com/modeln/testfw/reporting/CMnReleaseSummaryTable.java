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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;


/**
 * This is a data manager class which provides the object relational mapping between
 * the database and the data objects.
 * 
 * @author             Shawn Stafford
 */
public class CMnReleaseSummaryTable extends CMnTable {

    /** Name of the release summary table. */
    private static final String SUMMARY_TABLE = "release_summary";

    /** Identifies a release entry in the summary table. */
    private static final String SUMMARY_ID = "summary_id";

    /** Determine the order in which the summary results should be displayed */
    private static final String SUMMARY_ORDER = "summary_order";

    /** A descriptive, project, or code name for a release.  */
    private static final String RELEASE_NAME = "release_name";

    /** Descriptive message or text associated with the release */
    private static final String RELEASE_TEXT = "release_text";

    /** Designation for the summary table entry. */
    private static final String RELEASE_TYPE = "release_type";

    /** Status of the release in the summary table. */
    private static final String RELEASE_STATUS = "release_status";

    /** Identifies the build version associated with the release summary. */
    private static final String BUILD_VERSION = "build_version";


    /** Designates a release that is built nightly. */
    private static final String NIGHTLY_RELEASE = "nightly";

    /** Designates a release that is built on an incremental basis throughout the day. */
    private static final String INCREMENTAL_RELEASE = "incremental";

    /** Designates a release that has been designated as stable but not yet released. */
    private static final String STABLE_RELEASE = "stable";

    /** Designates a release that has been tested and designated for release. */
    private static final String FINAL_RELEASE = "released";


    /** Designates a release as currently available */
    private static final String ACTIVE_STATUS = "active";

    /** Designates a release as no longer available */
    private static final String RETIRED_STATUS = "retired";


    /** Name of the release links table. */
    private static final String LINKS_TABLE = "release_links";


    /** Singleton instance of the table class */
    private static CMnReleaseSummaryTable instance;

    /**
     * Return the singleton instance of the class.
     */
    public static CMnReleaseSummaryTable getInstance() {
        if (instance == null) {
            instance = new CMnReleaseSummaryTable();
        }
        return instance;
    }



    /**
     * Retrieve a list of releases from the release summary table.
     *
     * @return List of releases
     */
    public static Vector<CMnDbReleaseSummaryData> getSummaryList(Connection conn) throws SQLException {
        Vector<CMnDbReleaseSummaryData> list = new Vector();

        // Construct the query to obtain a list of all releases
        String sql = "SELECT * FROM " + SUMMARY_TABLE + " ORDER BY " + SUMMARY_ORDER + " DESC";

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            // Execute the query
            getInstance().debugWrite(sql);
            rs = st.executeQuery(sql);
            if (rs != null) {
                while (rs.next()) {
                    CMnDbReleaseSummaryData summary = parseSummary(rs); 
                    summary.setBuilds(CMnReleaseTable.getReleaseList(conn, summary.getBuildVersion()));
                    list.add(summary);
                }
            } else {
                 System.err.println("Unable to obtain a list of releases.");
            }

        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return list;

    }


    /**
     * Retrieve an entry from the release summary table.
     *
     * @param  conn   Database connection
     * @param  id     Summary ID
     *
     * @return Summary information 
     */
    public static CMnDbReleaseSummaryData getSummary(Connection conn, String id) throws SQLException {
        CMnDbReleaseSummaryData summary = null;

        // Construct the query to obtain a list of all releases
        String sql = "SELECT * FROM " + SUMMARY_TABLE + " WHERE " + SUMMARY_ID + "=" + id;

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            // Execute the query
            getInstance().debugWrite(sql);
            rs = st.executeQuery(sql);
            if (rs != null) {
                rs.first();
                summary = parseSummary(rs);
                summary.setBuilds(CMnReleaseTable.getReleaseList(conn, summary.getBuildVersion()));
            } else {
                 System.err.println("Unable to obtain a list of releases.");
            }

        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return summary;

    }



    /** 
     * Parse the fields of an entry in the summary table and return the corresponding
     * data as a data object.
     *
     * @param    rs    Result set containing the summary data
     * @return   Summary data for the current row
     */
    public static CMnDbReleaseSummaryData parseSummary(ResultSet rs) throws SQLException {
        String name = rs.getString(RELEASE_NAME);
        CMnDbReleaseSummaryData data = new CMnDbReleaseSummaryData(name);

        String text = rs.getString(RELEASE_TEXT);
        data.setText(text);

        String id = rs.getString(SUMMARY_ID);
        data.setId(id);

        int order = rs.getInt(SUMMARY_ORDER);
        data.setOrder(order);

        String version = rs.getString(BUILD_VERSION);
        data.setBuildVersion(version);

        String type = rs.getString(RELEASE_TYPE);
        setReleaseType(data, type);

        String status = rs.getString(RELEASE_STATUS);
        setReleaseStatus(data, status);

        return data; 
    }


    /**
     * Set the release type by mapping the type string to a data object representation.
     *
     * @param   summary   Data object
     * @param   type      Release type
     */
    private static void setReleaseType(CMnDbReleaseSummaryData summary, String type) {
        if (type.equals(NIGHTLY_RELEASE)) {
            summary.setReleaseType(summary.NIGHTLY_RELEASE);
        } else if (type.equals(INCREMENTAL_RELEASE)) {
            summary.setReleaseType(summary.INCREMENTAL_RELEASE);
        } else if (type.equals(STABLE_RELEASE)) {
            summary.setReleaseType(summary.STABLE_RELEASE);
        } else if (type.equals(FINAL_RELEASE)) {
            summary.setReleaseType(summary.FINAL_RELEASE);
        } else {
            summary.setReleaseType(summary.UNKNOWN_RELEASE);
        }
    }

    /**
     * Return the type of release specified by the data object.
     *
     * @param   summary   Data object
     * @return  Release type
     */
    private static String getReleaseType(CMnDbReleaseSummaryData summary) {
        switch (summary.getReleaseType()) {
          case CMnDbReleaseSummaryData.NIGHTLY_RELEASE:     return NIGHTLY_RELEASE;
          case CMnDbReleaseSummaryData.INCREMENTAL_RELEASE: return INCREMENTAL_RELEASE;
          case CMnDbReleaseSummaryData.STABLE_RELEASE:      return STABLE_RELEASE;
          case CMnDbReleaseSummaryData.FINAL_RELEASE:       return FINAL_RELEASE;
          default: return "unknown";
        }
    }


    /**
     * Set the release status by mapping the status string to a data object representation.
     *
     * @param   summary   Data object
     * @param   status    Release status
     */
    private static void setReleaseStatus(CMnDbReleaseSummaryData summary, String status) {
        if (status.equals(ACTIVE_STATUS)) {
            summary.setReleaseStatus(summary.ACTIVE_STATUS);
        } else if (status.equals(RETIRED_STATUS)) {
            summary.setReleaseStatus(summary.RETIRED_STATUS);
        } else {
            summary.setReleaseStatus(summary.ACTIVE_STATUS);
        }
    }

    /**
     * Return the status of the release specified by the data object.
     *
     * @param   summary   Data object
     * @return  Release status
     */
    private static String getReleaseStatus(CMnDbReleaseSummaryData summary) {
        switch (summary.getReleaseStatus()) {
          case CMnDbReleaseSummaryData.ACTIVE_STATUS:     return ACTIVE_STATUS;
          case CMnDbReleaseSummaryData.RETIRED_STATUS:    return RETIRED_STATUS;
          default: return "unknown";
        }
    }


}
