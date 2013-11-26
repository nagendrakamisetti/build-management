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
public class CMnDbProgressGroupData {

    /** Timestamp used to prefix every test message line */
    private static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("HH:mm:ss.SSS - ");

    /** Unique key value used to identify a group */
    private int groupId;

    /** Record the time of the progress group was added to the database */
    private Date startTime;

    /** Name of the host where the progress is being monitored */
    private String hostname;

    /** Version string which identifies the build being monitored */
    private String buildVersion;


    /**
     * Set the unique key value used to identify a single group entry.
     * This value is typically an auto-incremented value created by the database when
     * a new entry is inserted into the table.
     *
     * @param    id     Key value
     */
    public void setId(int id) {
        groupId = id;
    }


    /**
     * Return the unique key used to identify the group.
     *
     * @return  Key value
     */
    public int getId() {
        return groupId;
    }

    /**
     * Set the version string for the build being monitored. 
     *
     * @param   version    Build version string 
     */
    public void setBuildVersion(String version) {
        buildVersion = version;
    }

    /**
     * Return the version string for the build being monitored. 
     *
     * @return  Build version string 
     */
    public String getBuildVersion() {
        return buildVersion;
    }

    /**
     * Set the time when the group was entered into the database. 
     *
     * @param   date    Starting time
     */
    public void setStartTime(Date date) {
        startTime = date;
    }

    /**
     * Return the time when the group was entered into the database. 
     *
     * @return  Starting time
     */
    public Date getStartTime() {
        return startTime;
    }


    /**
     * Set the hostname where the progress is being monitored. 
     *
     * @param   host    Hostname 
     */
    public void setHostname(String host) {
        hostname = host;
    }

    /**
     * Return the hostname where the progress is being monitored. 
     *
     * @return  Hostname 
     */
    public String getHostname() {
        return hostname;
    }
}

