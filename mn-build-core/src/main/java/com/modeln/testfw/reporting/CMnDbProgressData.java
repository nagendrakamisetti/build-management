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

import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * Data object used to represent a group of progress indicators.
 *
 * @author  Shawn Stafford
 */
public class CMnDbProgressData {

    /** Timestamp used to prefix every test message line */
    private static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("HH:mm:ss.SSS - ");


    /** Unique key value used to identify a group */
    private int progressId;

    /** Foreign key value used to identify a group */
    private int groupId;

    /** Record the time of the progress entry was added to the database */
    private Date startTime;

    /** Record the time of progress was marked as completed in the database */
    private Date endTime;

    /** Display name of the progress entry */
    private String displayName;



    /**
     * Set the unique key value used to identify a single progress entry.
     * This value is typically an auto-incremented value created by the database when
     * a new entry is inserted into the table.
     *
     * @param    id     Key value
     */
    public void setId(int id) {
        progressId = id;
    }


    /**
     * Return the unique key used to identify the progress entry.
     *
     * @return  Key value
     */
    public int getId() {
        return progressId;
    }

    /**
     * Set the unique key value used to identify a single group entry.
     * This value is a foreign key linking progress events to a group. 
     *
     * @param    id     Key value
     */
    public void setGroupId(int id) {
        groupId = id;
    }


    /**
     * Return the unique key used to identify the group.
     *
     * @return  Key value
     */
    public int getGroupId() {
        return groupId;
    }

    /**
     * Set the starting time of the build.
     *
     * @param   date    Starting time
     */
    public void setStartTime(Date date) {
        startTime = date;
    }

    /**
     * Return the starting time of the build.
     *
     * @return  Starting time
     */
    public Date getStartTime() {
        return startTime;
    }

    /**
     * Set the ending time of the build.
     *
     * @param   date    Ending time
     */
    public void setEndTime(Date date) {
        endTime = date;
    }

    /**
     * Return the ending time of the build.
     *
     * @return  Ending time
     */
    public Date getEndTime() {
        return endTime;
    }


    /**
     * Set the display name for the progress entry. 
     *
     * @param   name    Display name 
     */
    public void setDisplayName(String name) {
        displayName = name;
    }

    /**
     * Return the display name for the progress entry. 
     *
     * @return  Display name 
     */
    public String getDisplayName() {
        return displayName;
    }

}
