/* 
* Copyright 2000-2003 by Model N, Inc.  All Rights Reserved. 
* 
* This software is the confidential and proprietary information 
* of Model N, Inc ("Confidential Information").  You shall not 
* disclose such Confidential Information and shall use it only 
* in accordance with the terms of the license agreement you 
* entered into with Model N, Inc. 
*/
package com.modeln.build.common.data.product;

import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * Data object used to represent a UIT suite in the database. 
 * 
 * @author  Shawn Stafford
 */
public class CMnTestSuite {

    /** Timestamp used to prefix every test message line */
    private static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("HH:mm:ss.SSS - ");

    /** Unique key value used to identify a suite */
    private int suiteId;

    /** Unique key value used to identify the parent build */
    private int buildId;

    /** Record the starting time of the test suite */
    private Date startTime;

    /** Record the completion time of the test suite */
    private Date endTime;

    /** Name of the test suite */
    private String suiteName;

    /** Total number of tests in the suite that passed */
    private int passingTestCount = 0;

    /** Total number of tests in the suite */
    private int testCount = 0;


    /** Host environment information */
    private CMnHostData hostEnv;


    /**
     * Set the unique key value used to identify a single test suite entry.
     * This value is typically an auto-incremented value created by the database when
     * a new entry is inserted into the table.
     *
     * @param    id     Key value
     */
    public void setId(int id) {
        suiteId = id;
    }

    /**
     * Return the unique key used to identify the test suite. 
     *
     * @return  Key value
     */
    public int getId() {
        return suiteId;
    }

    /**
     * Set the unique key value used to identify the parent build. 
     * This value is typically an auto-incremented value created by the database when
     * a new entry is inserted into the table.
     *
     * @param    id     Key value
     */
    public void setParentId(int id) {
        buildId = id;
    }

    /**
     * Return the unique key used to identify the build. 
     *
     * @return  Key value
     */
    public int getParentId() {
        return buildId;
    }



    /**
     * Set the starting time of the test.
     * 
     * @param   date    Starting time
     */
    public void setStartTime(Date date) {
        startTime = date;
    }

    /**
     * Return the starting time of the test.
     * 
     * @return  Starting time
     */
    public Date getStartTime() {
        return startTime;
    }

    /**
     * Set the ending time of the test.
     * 
     * @param   date    Ending time
     */
    public void setEndTime(Date date) {
        endTime = date;
    }

    /**
     * Return the ending time of the test.
     * 
     * @return  Ending time
     */
    public Date getEndTime() {
        return endTime;
    }

    /**
     * Set the name of the test suite.
     * 
     * @param   name    Name of the test suite
     */
    public void setSuiteName(String name) {
        suiteName = name;
    }

    /**
     * Return the name of the test suite.
     * 
     * @return  Suite name
     */
    public String getSuiteName() {
        return suiteName;
    }

    /**
     * Set the host environment information.
     *
     * @param  data    Host environment data
     */
    public void setHostData(CMnHostData data) {
        hostEnv = data;
    }

    /**
     * Return the host environment information.
     *
     * @return  Host environment data
     */
    public CMnHostData getHostData() {
        return hostEnv;
    }

    /**
     * Set the total number of passing tests.
     *
     * @param  count   Total number of passing tests
     */
    public void setPassingCount(int count) {
        passingTestCount = count;
    }

    /** 
     * Return the total number of passing tests.
     *
     * @return Total number of passing tests
     */
    public int getPassingCount() {
        return passingTestCount;
    }


    /** 
     * Set the total number of tests.
     *
     * @param  count   Total number of tests
     */
    public void setTestCount(int count) {
        testCount = count;
    }

    /**
     * Return the total number of tests.
     *
     * @return Total number of tests
     */
    public int getTestCount() {
        return testCount;
    }

}
