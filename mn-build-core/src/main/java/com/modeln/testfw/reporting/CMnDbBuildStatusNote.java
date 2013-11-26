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


/**
 * This is a data class which provides the object relational mapping for an
 * entry in the database. 
 *
 * @author             Shawn Stafford
 */
public class CMnDbBuildStatusNote {

    /** Status that the note corresponds to */
    private int buildStatus;

    /** Identifies the note in the data table. */
    private int noteId;

    /** Comments associated with the status */
    private StringBuffer comments = new StringBuffer();

    /** Binary attachment associated with the status */
    private Blob attachment;

    /**
     * Construct a note object. 
     *
     * @param  id   ID of the note being described 
     */
    public CMnDbBuildStatusNote(int id) {
        noteId = id;
    }

    /**
     * Return the ID that identifies the note object in the database.
     *
     * @return  Id value
     */
    public int getId() {
        return noteId;
    }

    /**
     * Set the status of the build. 
     *
     * @param status  Build status 
     */
    public void setBuildStatus(int status) {
        buildStatus = status; 
    }

    /**
     * Return the build status that the note corresponds to. 
     *
     * @return Value indicating the build status 
     */
    public int getBuildStatus() {
        return buildStatus;
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

    /**
     * Set a binary attachment. 
     *
     * @param blob  Binary attachment 
     */
    public void setAttachment(Blob blob) {
        attachment = blob;
    }

    /**
     * Return the attachment to the note. 
     *
     * @return Binary attachment 
     */
    public Blob getAttachment() {
        return attachment;
    }


}

