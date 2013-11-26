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


import java.util.Vector;


/**
 * This is a data class which provides the object relational mapping for an
 * entry in the database. 
 *
 * @author             Shawn Stafford
 */
public class CMnDbBuildStatusData {

    /** Designates that the build passed all of the build criteria. */
    public static final int PASSING_STATUS = 0;

    /** Designates that the build was verified to work in the verification environment */
    public static final int VERIFIED_STATUS = 1;

    /** Designates that the build was designated as a stable development build. */
    public static final int STABLE_STATUS = 2;

    /** Designates that the build was tested by a QA team. */
    public static final int TESTED_STATUS = 3;

    /** Designates that the build was approved for release */
    public static final int RELEASED_STATUS = 4;


    /** Designates that the build is under active support */
    public static final int ACTIVE_SUPPORT = 0;

    /** Designates that the build is no longer actively support */
    public static final int INACTIVE_SUPPORT = 1;

    /** Designates that the build is under extended technical support */
    public static final int EXTENDED_SUPPORT = 2;




    /** List of all possible build status values (does not indicate order or progression) */
    public static final int[] STATUS_LIST = {
        PASSING_STATUS, VERIFIED_STATUS, STABLE_STATUS, TESTED_STATUS, RELEASED_STATUS
    };


    /** Identifies the build associated with the current status. */
    private int buildId;

    /** User who set the current status. */
    private int userId;

    /** Support status of the build */
    private int supportStatus = ACTIVE_SUPPORT;

    /** Status of the build. */
    private boolean[] buildStatus = {false, false, false, false, false};

    /** List of notes associated with build status */
    private Vector[] statusNotes = {
        new Vector(), new Vector(), new Vector(), new Vector(), new Vector()
    };

    /** Comments associated with the status change */
    private StringBuffer comments = new StringBuffer();


    /**
     * Construct a summary object. 
     *
     * @param  build   ID of the build being described 
     */
    public CMnDbBuildStatusData(int build) {
        buildId = build;
    }

    /**
     * Set the ID of the build that the status describes. 
     *
     * @param id  Build ID
     */
    public void setBuildId(int id) {
        buildId = id;
    }

    /**
     * Return the ID of the build that the status describes. 
     *
     * @return Build ID
     */
    public int getBuildId() {
        return buildId;
    }


    /**
     * Set the ID of the user who set the status. 
     *
     * @param id  Summary ID
     */
    public void setUserId(int id) {
        userId = id;
    }

    /**
     * Return the ID of the user who set the status. 
     *
     * @return User ID 
     */
    public int getUserId() {
        return userId;
    }


    /**
     * Set the support status of the build.
     *
     * @param status  Support status
     */
    public void setSupportStatus(int status) {
        supportStatus = status; 
    }

    /**
     * Return the support status of the build.
     *
     * @return Support status (ACTIVE_SUPPORT, INACTIVE_SUPPORT, EXTENDED_SUPPORT) 
     */
    public int getSupportStatus() {
        return supportStatus; 
    }


    /**
     * Clear all of the existing status values, resetting them to the
     * specified boolean value.  
     *
     * @param value  Value that will be set for all build status options
     */
    public void resetBuildStatus(boolean value) {
        for (int idx = 0; idx < buildStatus.length; idx++) {
            buildStatus[idx] = value;
        }
    }

    /**
     * Set the status of the build. 
     *
     * @param status  Build status 
     * @param enabled Determines whether the status should be set or unset
     */
    public void setBuildStatus(int status, boolean enabled) {
        buildStatus[status] = enabled; 
    }

    /**
     * Return true to indicate if the status field has been set.
     *
     * @param status Build status field
     * @return TRUE if the status field is enabled, FALSE otherwise
     */
    public boolean getBuildStatus(int status) {
        return buildStatus[status];
    }

    /**
     * Clear all of the existing status notes, resetting them to an
     * empty list. 
     */
    public void resetStatusNotes() {
        for (int idx = 0; idx < statusNotes.length; idx++) {
            statusNotes[idx] = new Vector();
        }
    }

    /**
     * Add a note to the specified status. 
     *
     * @param status  Build status
     * @param note    Note to be associated with the status
     */
    public void addStatusNote(int status, CMnDbBuildStatusNote note) {
        statusNotes[status].add(note);
    }

    /**
     * Return the list of notes associated with a status. 
     *
     * @param status Build status field
     * @return A list of build status notes 
     */
    public Vector getStatusNotes(int status) {
        return statusNotes[status];
    }


    /**
     * Clears any existing comments and sets the comments to the given text.
     *
     * @param   text   Comments
     */
    public void setComments(String text) {
        comments = new StringBuffer(text);
    }

    /**
     * Returns the comments associated with the current status.
     *
     * @return Text comments
     */
    public String getComments() {
        return comments.toString();
    }

    /**
     * Adds the text to the end of the status comments. 
     *
     * @param  text  Additional comment text
     */
    public void addComments(String text) {
        comments.append(text);
    }

}

