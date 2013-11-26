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

import java.sql.Blob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.StringTokenizer;
import java.util.Vector;


/**
 * This is a data manager class which provides the object relational mapping between
 * the database and the data objects.
 * 
 * @author             Shawn Stafford
 */
public class CMnReleaseTable extends CMnTable {

    /** Name of the build status table. */
    private static final String STATUS_TABLE = "build_status";

    /** Identifies a build entry in the status table. */
    private static final String BUILD_ID = "build_id";

    /** User who performed the status action.  */
    private static final String USER_ID = "user_id";

    /** Current status of the build being referenced.  */
    private static final String BUILD_STATUS = "status";

    /** Current support status of the build being referenced.  */
    private static final String BUILD_SUPPORT = "support";

    /** Text comments associated with the status change */
    private static final String COMMENTS = "comments";


    /** Designates a build that passed the build criteria. */
    private static final String PASSING_STATUS = "passing";

    /** Designates a build that was verified on the verification environment. */
    private static final String VERIFIED_STATUS = "verified";

    /** Designates a build that was designated as a stable development build. */
    private static final String STABLE_STATUS = "stable";

    /** Designates a build that was tested by a QA team */
    private static final String TESTED_STATUS = "tested";

    /** Designates a build that was approved for release. */
    private static final String RELEASED_STATUS = "released";



    /** Designates a build that is still actively under support */
    private static final String ACTIVE_SUPPORT = "active";

    /** Designates a build that is no longer actively under support */
    private static final String INACTIVE_SUPPORT = "inactive";

    /** Designates a build that is under extended technical support */
    private static final String EXTENDED_SUPPORT = "extended";



    /** Name of the status notes table */
    private static final String STATUS_NOTE_TABLE = "build_status_notes";

    /** Identifies the column that contains the unique ID for each row of the notes table */
    private static final String NOTE_ID = "note_id";

    /** Identifies the column that contains the build status associated with the note */
    private static final String NOTE_STATUS = "status";

    /** Identifies the column that contains comments associated with a status note entry */
    private static final String NOTE_TEXT = "comments";

    /** Identifies the column that contains a binary attachment to the status note */
    private static final String NOTE_ATTACHMENT = "attachment";
 

    /** Singleton instance of the table class */
    private static CMnReleaseTable instance;

    /**
     * Return the singleton instance of the class.
     */
    public static CMnReleaseTable getInstance() {
        if (instance == null) {
            instance = new CMnReleaseTable();
        }
        return instance;
    }


    /**
     * Retrieve the build status associated with a specific build ID.  If no
     * status information is found, null will be returned.  Not every build
     * will have status information associated with it, so a null return object
     * is expected to be a common return result. 
     *
     * @param  conn     Database connection
     * @param  build    Build information
     * @return Status of the build 
     */
    public static CMnDbBuildStatusData getStatus(Connection conn, CMnDbBuildData build) throws SQLException {
        CMnDbBuildStatusData status = null;

        // Construct the query to obtain the status information 
        StringBuffer sql = new StringBuffer();
        sql.append(
            "SELECT * FROM " + STATUS_TABLE + 
            " WHERE " + STATUS_TABLE + "." + BUILD_ID + "=" + build.getId()
        );

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            getInstance().debugWrite("Attempting to execute query: " + sql.toString());
            rs = st.executeQuery(sql.toString());
            if ((rs != null) && (rs.first())) {
                status = parseStatus(rs);
                if (status != null) {
                    getStatusNotes(conn, build, status);
                }
            }
        } catch (SQLException ex) {
            System.err.println("Failed to obtain status data: " + sql.toString());
            ex.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return status;
    }


    /**
     * Store build status information in the database.  The updateStatus method
     * ensures that the status already exists and creates a new entry if neccessary. 
     *
     * @param   conn    Database connection
     * @param  build    Build information
     * @param   status  Status information
     */
    public synchronized static void updateStatus(
            Connection conn,
            CMnDbBuildData build,
            CMnDbBuildStatusData status)
        throws SQLException
    {
        CMnDbBuildStatusData existingStatus = getStatus(conn, build);
        if (existingStatus != null) {
            updateStatus(conn, status);
        } else {
            addStatus(conn, status);
        }
    }

    /**
     * Store build status information in the database.  The updateStatus method
     * should be used if it is not known whether the status already exists.
     *
     * @param   conn    Database connection
     * @param   status  Status information
     */
    public synchronized static void updateStatus(
            Connection conn,
            CMnDbBuildStatusData status)
        throws SQLException
    {
        //String statusId = null;

        StringBuffer sql = new StringBuffer();
        sql.append("UPDATE " + STATUS_TABLE + " SET ");
        sql.append(USER_ID + "='" + status.getUserId() + "'");
        sql.append(", " + COMMENTS + "='");
        if (status.getComments() != null) {
            sql.append(escapeQueryText(status.getComments(), TEXT_SIZE));
        }
        sql.append("'");
        sql.append(", " + BUILD_STATUS + "='" + getBuildStatus(status) + "'");
        sql.append(" WHERE " + BUILD_ID + "='" + status.getBuildId() + "'");

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            getInstance().debugWrite("Attempting to execute: " + sql.toString());
            st.execute(sql.toString(), Statement.RETURN_GENERATED_KEYS);
            //rs = st.getGeneratedKeys();
            rs = getGeneratedKeys(st);
            //if (rs != null) {
            //    rs.first();
            //    suiteId = rs.getString(1);
            //} else {
            //     System.err.println("Unable to obtain generated key.");
            //}
        } catch (SQLException ex) {
            System.err.println("Failed to update build status.");
            System.err.println("SQL Query: " + sql.toString());
            ex.printStackTrace();
            throw ex;
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        //return statusId;
    }


    /**
     * Store build status information in the database.  The updateStatus method
     * should be used if it is not known whether the status already exists. 
     *
     * @param   conn    Database connection
     * @param   status  Status information
     */
    public synchronized static void addStatus(
            Connection conn,
            CMnDbBuildStatusData status)
        throws SQLException
    {
        //String statusId = null;

        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO " + STATUS_TABLE + " ");
        sql.append("(" + BUILD_ID);
        sql.append(", " + USER_ID);
        if (status.getComments() != null) sql.append(", " + COMMENTS);
        sql.append(", " + BUILD_STATUS + ") ");

        sql.append("VALUES ");
        sql.append("('" + status.getBuildId() + "'");
        sql.append(", '" + status.getUserId() + "'");
        if (status.getComments() != null) sql.append(", '" + escapeQueryText(status.getComments(), TEXT_SIZE) + "'");
        sql.append(", '" + getBuildStatus(status) + "')");

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            getInstance().debugWrite("Attempting to execute: " + sql.toString());
            st.execute(sql.toString(), Statement.RETURN_GENERATED_KEYS);
            //rs = st.getGeneratedKeys();
            rs = getGeneratedKeys(st);
            //if (rs != null) {
            //    rs.first();
            //    suiteId = rs.getString(1);
            //} else {
            //     System.err.println("Unable to obtain generated key.");
            //}
        } catch (SQLException ex) {
            System.err.println("Failed to add build status.");
            System.err.println("SQL Query: " + sql.toString());
            ex.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        //return statusId;
    }


    /**
     * Store build status note information in the database.  The updateStatusNote method
     * ensures that the note already exists and creates a new entry if neccessary.
     *
     * @param   conn    Database connection
     * @param  build    Build information
     * @param   note    Note information
     */
    public synchronized static void updateStatusNote(
            Connection conn,
            CMnDbBuildData build,
            CMnDbBuildStatusNote note)
        throws SQLException
    {
        CMnDbBuildStatusNote existingNote = getStatusNote(conn, build, note.getId());
        if (existingNote != null) {
            updateStatusNote(conn, build.getId(), note);
        } else {
            addStatusNote(conn, build.getId(), note);
        }
    }

    /**
     * Store build status note information in the database.  The updateStatusNote method
     * should be used if it is not known whether the status already exists.
     *
     * @param   conn    Database connection
     * @param   buildId Build ID
     * @param   note    Note information
     */
    public synchronized static void updateStatusNote(
            Connection conn,
            int buildId,
            CMnDbBuildStatusNote note)
        throws SQLException
    {
        //String statusId = null;

        StringBuffer sql = new StringBuffer();
        sql.append("UPDATE " + STATUS_NOTE_TABLE + " SET ");
        sql.append(NOTE_STATUS + "='" + note.getBuildStatus() + "'");
        sql.append(", " + NOTE_TEXT + "='");
        if (note.getComments() != null) {
            sql.append(escapeQueryText(note.getComments(), TEXT_SIZE));
        }
        sql.append("'");
        sql.append(", " + BUILD_ID + "='" + buildId + "'");
        sql.append(", " + BUILD_STATUS + "='" + getBuildStatus(note.getBuildStatus()) + "'");
        sql.append(" WHERE " + NOTE_ID + "='" + note.getId() + "'");

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            getInstance().debugWrite("Attempting to execute: " + sql.toString());
            st.execute(sql.toString(), Statement.RETURN_GENERATED_KEYS);
            //rs = st.getGeneratedKeys();
            rs = getGeneratedKeys(st);
            //if (rs != null) {
            //    rs.first();
            //    suiteId = rs.getString(1);
            //} else {
            //     System.err.println("Unable to obtain generated key.");
            //}
        } catch (SQLException ex) {
            System.err.println("Failed to update build status.");
            System.err.println("SQL Query: " + sql.toString());
            ex.printStackTrace();
            throw ex;
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        //return statusId;
    }


    /**
     * Store build status note information in the database.  The updateStatusNote method
     * should be used if it is not known whether the status already exists.
     *
     * @param   conn    Database connection
     * @param  buildId  Build ID 
     * @param   note    Note information
     */
    public synchronized static void addStatusNote(
            Connection conn,
            int buildId,
            CMnDbBuildStatusNote note)
        throws SQLException
    {
        //String statusId = null;

        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO " + STATUS_NOTE_TABLE + " ");
        sql.append("(" + BUILD_ID);
        if (note.getComments() != null) sql.append(", " + NOTE_TEXT);
        sql.append(", " + NOTE_STATUS + ") ");

        sql.append("VALUES ");
        sql.append("('" + buildId + "'");
        if (note.getComments() != null) sql.append(", '" + escapeQueryText(note.getComments(), TEXT_SIZE) + "'");
        sql.append(", '" + getBuildStatus(note.getBuildStatus()) + "')");

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            getInstance().debugWrite("Attempting to execute: " + sql.toString());
            st.execute(sql.toString(), Statement.RETURN_GENERATED_KEYS);
            //rs = st.getGeneratedKeys();
            rs = getGeneratedKeys(st);
            //if (rs != null) {
            //    rs.first();
            //    suiteId = rs.getString(1);
            //} else {
            //     System.err.println("Unable to obtain generated key.");
            //}
        } catch (SQLException ex) {
            SQLException myEx = new SQLException(
               ex.getMessage() + ": Failed to add build status: " + sql.toString(),
               ex.getSQLState(),
               ex.getErrorCode()
            );
            myEx.setNextException(ex);
            ex.printStackTrace();
            throw myEx;
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        //return statusId;
    }


    /**
     * Retrieve the status note associated with a specific note ID. 
     * If the note ID does not exist, a null will be returned.
     *
     * @param  conn     Database connection
     * @param  build    Build information
     * @param  noteId   Status note ID 
     */
    public static CMnDbBuildStatusNote getStatusNote(
            Connection conn,
            CMnDbBuildData build,
            int noteId)
        throws SQLException
    {
        CMnDbBuildStatusNote note = null;
 
        Statement st = conn.createStatement();
        ResultSet rs = null;

        try {
            // Construct the query to obtain the status information
            StringBuffer sql = new StringBuffer();
            sql.append(
                "SELECT * FROM " + STATUS_NOTE_TABLE +
                " WHERE " + NOTE_ID + "=" + noteId
            );

            // Execute the query
            getInstance().debugWrite(sql.toString());
            rs = st.executeQuery(sql.toString());
            if (rs != null) {
                while (rs.next()) {
                    note = parseStatusNote(rs);
                }
            }

        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return note;
    }


    /**
     * Retrieve the status notes associated with a specific build.  Any notes
     * found will be added to the status object. 
     *
     * @param  conn     Database connection
     * @param  build    Build information
     * @param  status   Build status information
     */
    public static void getStatusNotes(
            Connection conn, 
            CMnDbBuildData build, 
            CMnDbBuildStatusData status) 
        throws SQLException 
    {

        Statement st = conn.createStatement();
        ResultSet rs = null;

        try {
            // Construct the query to obtain the status information
            StringBuffer sql = new StringBuffer();
            sql.append(
                "SELECT * FROM " + STATUS_NOTE_TABLE +
                " WHERE " + BUILD_ID + "=" + build.getId() 
            );

            // Execute the query
            getInstance().debugWrite(sql.toString());
            rs = st.executeQuery(sql.toString());
            if (rs != null) {
                // Iterate through each status and obtain the list of notes for each
                while (rs.next()) {
                    String buildStatus = rs.getString(BUILD_STATUS);
                    status.addStatusNote(getBuildStatus(buildStatus), parseStatusNote(rs));
                }
            }

        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

    }



    /**
     * Retrieve a list of builds that were released to customers.
     *
     * @param  conn     Database connection
     * @return List of released builds
     */
    public static Vector<CMnDbBuildData> getReleaseList(Connection conn) throws SQLException {
        Vector<CMnDbBuildData> list = new Vector<CMnDbBuildData>();

        // Construct the query to obtain a list of all releases
        StringBuffer sql = new StringBuffer();
        sql.append(
            "SELECT * FROM " + STATUS_TABLE + ", " + CMnBuildTable.BUILD_TABLE +
            " WHERE " + STATUS_TABLE + "." + BUILD_ID + " = " + CMnBuildTable.BUILD_TABLE + "." + CMnBuildTable.BUILD_ID +
            " AND " +  STATUS_TABLE + "." + BUILD_SUPPORT + " != '" + INACTIVE_SUPPORT + "'" + 
            " AND FIND_IN_SET('" + RELEASED_STATUS + "', " + BUILD_STATUS + ")" +
            " ORDER BY " + STATUS_TABLE + "." + BUILD_ID + " DESC"
        );

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            // Execute the query
            getInstance().debugWrite(sql.toString());
            rs = st.executeQuery(sql.toString());
            if (rs != null) {
                while (rs.next()) {
                    CMnDbBuildData build = CMnBuildTable.parseBuildData(rs);
                    build.setStatus(parseStatus(rs));
                    list.add(build);
                }
            } else {
                 System.err.println("Unable to obtain a list of released builds.");
            }

        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return list;

    }



    /**
     * Retrieve a list of builds that were released to a customer. 
     *
     * @param  conn     Database connection
     * @param  version  Build version string
     * @return List of released builds
     */
    public static Vector getReleaseList(Connection conn, String version) throws SQLException {
        Vector list = new Vector();

        // Construct the query to obtain a list of all releases
        StringBuffer sql = new StringBuffer();
        sql.append(
            "SELECT * FROM " + STATUS_TABLE + ", " + CMnBuildTable.BUILD_TABLE + 
            " WHERE " + STATUS_TABLE + "." + BUILD_ID + "=" + CMnBuildTable.BUILD_TABLE + "." + CMnBuildTable.BUILD_ID + 
            " AND " +  STATUS_TABLE + "." + BUILD_SUPPORT + " != '" + INACTIVE_SUPPORT + "'" +
            " AND " + CMnBuildTable.BUILD_TABLE + "." + CMnBuildTable.BUILD_VERSION + " LIKE '%" + version + "%'" +
            " AND FIND_IN_SET('" + RELEASED_STATUS + "', " + BUILD_STATUS + ")" +
            " ORDER BY " + STATUS_TABLE + "." + BUILD_ID + " DESC"
        ); 
            
        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            // Execute the query
            getInstance().debugWrite(sql.toString());
            rs = st.executeQuery(sql.toString());
            if (rs != null) {
                while (rs.next()) {
                    CMnDbBuildData build = CMnBuildTable.parseBuildData(rs);
                    build.setStatus(parseStatus(rs));
                    list.add(build);
                }
            } else {
                 System.err.println("Unable to obtain a list of released builds.");
            }

        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return list;

    }


    /** 
     * Parse the fields of an entry in the status table and return the corresponding
     * data as a data object.
     *
     * @param    rs    Result set containing the status data
     * @return   Status data for the current row
     */
    public static CMnDbBuildStatusData parseStatus(ResultSet rs) throws SQLException {
        int buildId = rs.getInt(BUILD_ID);
        CMnDbBuildStatusData data = new CMnDbBuildStatusData(buildId);

        int userId = rs.getInt(STATUS_TABLE + "." + USER_ID);
        data.setUserId(userId);

        String status = rs.getString(STATUS_TABLE + "." + BUILD_STATUS);
        setBuildStatus(data, status);

        String support = rs.getString(STATUS_TABLE + "." + BUILD_SUPPORT);
        data.setSupportStatus(getSupportStatus(support));

        String comments = rs.getString(STATUS_TABLE + "." + COMMENTS);
        data.setComments(comments); 

        return data; 
    }

    /**
     * Parse the fields of an entry in the status notes table and return the 
     * corresponding data as a data object.
     *
     * @param    rs    Result set containing the status data
     * @return   Status note data for the current row
     */
    public static CMnDbBuildStatusNote parseStatusNote(ResultSet rs) throws SQLException {
        int noteId = rs.getInt(NOTE_ID);
        CMnDbBuildStatusNote data = new CMnDbBuildStatusNote(noteId);

        String status = rs.getString(NOTE_STATUS);
        data.setBuildStatus(getBuildStatus(status));

        String comments = rs.getString(NOTE_TEXT);
        data.setComments(comments);

        Blob attachment = rs.getBlob(NOTE_ATTACHMENT);
        data.setAttachment(attachment);

        return data;
    }


    /**
     * Return the support status by mapping the type string to a data object representation.
     *
     * @param   status    Status type
     */
    private static int getSupportStatus(String status) {
        int statusValue = CMnDbBuildStatusData.ACTIVE_SUPPORT;
        if (status.equals(ACTIVE_SUPPORT)) {
            statusValue = CMnDbBuildStatusData.ACTIVE_SUPPORT;
        } else if (status.equals(INACTIVE_SUPPORT)) {
            statusValue = CMnDbBuildStatusData.INACTIVE_SUPPORT;
        } else if (status.equals(EXTENDED_SUPPORT)) {
            statusValue = CMnDbBuildStatusData.EXTENDED_SUPPORT;
        }

        return statusValue;
    }


    /**
     * Set the build status by mapping the type string to a data object representation.
     *
     * @param   data      Data object
     * @param   status    Status type 
     */
    private static void setBuildStatus(CMnDbBuildStatusData data, String status) {
        data.resetBuildStatus(false);
        if ((status != null) && (status.length() > 0)) {
            StringTokenizer st = new StringTokenizer(status, ",");
            String currentStatus = null;
            while (st.hasMoreTokens()) {
                currentStatus = st.nextToken();
                data.setBuildStatus(getBuildStatus(currentStatus), true);
            }
        }
    }


    /**
     * Returns the integer value associated with a particular build status.
     *
     * @param   status   Build status string
     * @return  Status code
     */
    private static int getBuildStatus(String status) {
        int statusValue = CMnDbBuildStatusData.PASSING_STATUS; 
        if (status.equals(PASSING_STATUS)) {
            statusValue = CMnDbBuildStatusData.PASSING_STATUS;
        } else if (status.equals(VERIFIED_STATUS)) {
            statusValue = CMnDbBuildStatusData.VERIFIED_STATUS;
        } else if (status.equals(STABLE_STATUS)) {
            statusValue = CMnDbBuildStatusData.STABLE_STATUS;
        } else if (status.equals(TESTED_STATUS)) {
            statusValue = CMnDbBuildStatusData.TESTED_STATUS;
        } else if (status.equals(RELEASED_STATUS)) {
            statusValue = CMnDbBuildStatusData.RELEASED_STATUS;
        }

        return statusValue;
    }

    /**
     * Return the set of release status values that are enabled.  The
     * set consists of a comma-delimited set of values. 
     *
     * @param   data   Data object
     * @return  Release type
     */
    private static String getBuildStatus(CMnDbBuildStatusData data) {
        StringBuffer status = new StringBuffer();
        if (data.getBuildStatus(CMnDbBuildStatusData.PASSING_STATUS)) {
            if (status.length() > 0) status.append(",");
            status.append(PASSING_STATUS);
        }
        if (data.getBuildStatus(CMnDbBuildStatusData.VERIFIED_STATUS)) {
            if (status.length() > 0) status.append(",");
            status.append(VERIFIED_STATUS);
        }
        if (data.getBuildStatus(CMnDbBuildStatusData.STABLE_STATUS)) {
            if (status.length() > 0) status.append(",");
            status.append(STABLE_STATUS);
        }
        if (data.getBuildStatus(CMnDbBuildStatusData.TESTED_STATUS)) {
            if (status.length() > 0) status.append(",");
            status.append(TESTED_STATUS);
        }
        if (data.getBuildStatus(CMnDbBuildStatusData.RELEASED_STATUS)) {
            if (status.length() > 0) status.append(",");
            status.append(RELEASED_STATUS);
        }

        return status.toString();
    }

    /**
     * Return the build status as a string.
     *
     * @param  status  Build status value
     * @return Build status string
     */
    private static String getBuildStatus(int status) {
        switch (status) {
            case CMnDbBuildStatusData.PASSING_STATUS:  return PASSING_STATUS;
            case CMnDbBuildStatusData.VERIFIED_STATUS: return VERIFIED_STATUS;
            case CMnDbBuildStatusData.STABLE_STATUS:   return STABLE_STATUS;
            case CMnDbBuildStatusData.TESTED_STATUS:   return TESTED_STATUS;
            case CMnDbBuildStatusData.RELEASED_STATUS: return RELEASED_STATUS;
            default: return PASSING_STATUS;
        }
    }

    /**
     * Return the build support status as a string.
     *
     * @param  status  Build status value
     * @return Build status string
     */
    private static String getBuildSupport(int status) {
        switch (status) {
            case CMnDbBuildStatusData.ACTIVE_SUPPORT:     return ACTIVE_SUPPORT;
            case CMnDbBuildStatusData.INACTIVE_SUPPORT:   return INACTIVE_SUPPORT;
            case CMnDbBuildStatusData.EXTENDED_SUPPORT:   return EXTENDED_SUPPORT;
            default: return ACTIVE_SUPPORT;
        }
    }


}
