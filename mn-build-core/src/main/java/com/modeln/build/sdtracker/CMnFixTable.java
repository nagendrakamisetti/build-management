package com.modeln.build.sdtracker;

import com.modeln.build.sourcecontrol.CMnCheckIn;
import com.modeln.build.sourcecontrol.CMnGitCheckIn;
import com.modeln.build.sourcecontrol.CMnPerforceCheckIn;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.Date;
import java.util.Vector;



/* SDTracker database table structure
SQL> desc changelist
 Name					   Null?    Type
 ----------------------------------------- -------- ----------------------------
 OID					   NOT NULL NUMBER
 CREATED_DATE				   NOT NULL DATE
 CREATED_BY				   NOT NULL VARCHAR2(80 CHAR)
 MODIFIED_DATE					    DATE
 GUI_MODIFIED_DATE				    DATE
 MODIFIED_BY					    VARCHAR2(80 CHAR)
 OBSOLETE_FLAG				   NOT NULL NUMBER
 SDR_OID				   NOT NULL NUMBER
 CHANGELIST				   NOT NULL NUMBER
 DESCRIPTION					    VARCHAR2(255 CHAR)
 CLIENT 					    VARCHAR2(80 CHAR)
 PF_DATE					    DATE
 PF_USER					    VARCHAR2(80 CHAR)
 GIT_SHA					    VARCHAR2(255 CHAR)
 GIT_PROJECT					    VARCHAR2(80 CHAR)
*/


/**
 * Utility class used to query the database for data 
 * representing the association between bugs and check-ins.
 *
 * @version      1.0
 * @author       Shawn Stafford (sstafford@modeln.com)
 */
public class CMnFixTable extends CMnOracleTable {

    /** Name of the database table containing the fix information */
    protected static final String TABLE_NAME = "changelist";

    /** Name of the column representing the foreign key to the SDR */
    protected static final String COLUMN_BUG_ID = "sdr_oid";

    /** Name of the column representing the Perforce changelist */
    protected static final String COLUMN_PERFORCE_CHANGELIST = "changelist";

    /** Name of the column representing the Perforce client name */
    protected static final String COLUMN_PERFORCE_CLIENT = "client";

    /** Name of the column representing the Git commit hash */
    protected static final String COLUMN_GIT_SHA = "git_sha";

    /** Name of the column representing the git project */
    protected static final String COLUMN_GIT_PROJECT = "git_project";

    /** Name of the column representing the the check-in description */
    protected static final String COLUMN_DESCRIPTION = "description";


    /** Singleton instance of the table class */
    private static CMnFixTable instance;

    /**
     * Return the singleton instance of the class.
     */
    public static CMnFixTable getInstance() {
        if (instance == null) {
            instance = new CMnFixTable();

            // Enable debbuging to a file
            String logfile = "/var/tmp/SDTrackerFixTable.txt";
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
     * Retrieve the list of check-ins associated with the bug. 
     *
     * @param   conn      Database connection
     * @param   bugId     SDTracker bug ID 
     *
     * @return  Check-in information
     */
    public synchronized Vector<CMnCheckIn> getCheckIns(Connection conn, String bugId)
        throws SQLException
    {
        Vector<CMnCheckIn> checkins = new Vector<CMnCheckIn>();
        int p4count = 0;
        int gitcount = 0;

        StringBuffer sql = new StringBuffer();
        sql.append("SELECT * FROM " + TABLE_NAME + 
                           " WHERE " + COLUMN_BUG_ID + " = '" + bugId + "'");

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            getInstance().debugWrite("Attempting to execute: " + sql.toString());
            rs = executeQuery(st, "getCheckIns", sql.toString());
            while ((rs != null) && rs.next()) {
                Vector<CMnCheckIn> checkin = parseCheckInData(rs);
                for (int idx = 0; idx < checkin.size(); idx++) {
                    CMnCheckIn current = checkin.get(idx);
                    checkins.add(current);
                    if (current instanceof CMnPerforceCheckIn) {
                        p4count++;
                    } else if (current instanceof CMnGitCheckIn) {
                        gitcount++;
                    }
                } 
            }

            // Write some debugging info to the log
            if (checkins.size() > 0) {
                getInstance().debugWrite("Returned " + checkins.size() + " check-ins (p4=" + p4count + ", git=" + gitcount + ")");
            } else {
                getInstance().debugWrite("Returned zero check-ins.");
            }

        } catch (SQLException ex) {
            System.err.println("Failed to obtain the changelist data: " + sql.toString());
            ex.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return checkins;
    }



    /**
     * Parse the result set to obtain check-in information.
     *
     * @param  rs   Result set containing fix data
     *
     * @return Fix information
     */
    public static Vector<CMnCheckIn> parseCheckInData(ResultSet rs)
        throws SQLException
    {
        Vector<CMnCheckIn> list = new Vector<CMnCheckIn>();

        // Parse the generic checkin information
        String desc = rs.getString(COLUMN_DESCRIPTION);

        // Parse the Perforce changelist information
        String changelist = rs.getString(COLUMN_PERFORCE_CHANGELIST);
        if ((changelist != null) && (changelist.length() > 0)) {
            CMnPerforceCheckIn cl = new CMnPerforceCheckIn(CMnCheckIn.State.SUBMITTED);
            cl.setId(changelist);

            String clientspec = rs.getString(COLUMN_PERFORCE_CLIENT);
            cl.setClient(clientspec);
            cl.setDescription(desc);

            list.add(cl);
        }

        // Parse the git commit information
        String sha = rs.getString(COLUMN_GIT_SHA);
        if ((sha != null) && (sha.length() > 0)) {
            CMnGitCheckIn commit = new CMnGitCheckIn(CMnCheckIn.State.SUBMITTED);
            commit.setId(sha);

            String project = rs.getString(COLUMN_GIT_PROJECT);
            commit.setRepository(project);
            commit.setDescription(desc);

            list.add(commit);
        }

        return list;
    }


}

