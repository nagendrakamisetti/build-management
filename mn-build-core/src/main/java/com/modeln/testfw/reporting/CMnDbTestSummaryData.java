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

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Data object used to represent a summary of the test execution. 
 * 
 * @author  Shawn Stafford
 */
public class CMnDbTestSummaryData {


    /** Total number of tests in the suite that passed */
    private int passingTestCount = 0;

    /** Total number of tests in the suite that failed */
    private int failingTestCount = 0;

    /** Total number of tests in the suite that had errors */
    private int errorTestCount = 0;

    /** Total number of tests in the suite that were killed */
    private int killedTestCount = 0;

    /** Total number of tests in the suite that were skipped */
    private int skippedTestCount = 0;

    /** Total number of tests in the suite that are still pending */
    private int pendingTestCount = 0;

    /** Total number of tests in the suite that are currently running */
    private int runningTestCount = 0;

    /** Total number of tests in the suite that were blacklisted */
    private int blacklistTestCount = 0;

    /** Total number of tests in the suite that ran longer than max time */
    private int longTestCount = 0;

    /** Total number of tests in the suite */
    private int totalTestCount = 0;



    /**
     * Set the total number of long tests.
     *
     * @param  count   Total number of long tests
     */
    public void setLongCount(int count) {
        longTestCount = count;
    }
    
    /** 
     * Return the total number of long tests.
     *
     * @return Total number of long tests
     */
    public int getLongCount() {
        return longTestCount; 
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
    public void setTotalCount(int count) {
        totalTestCount = count;
    }

    /**
     * Return the total number of tests.
     *
     * @return Total number of tests
     */
    public int getTotalCount() {
        return totalTestCount;
    }

    /**
     * Set the total number of failing tests.
     *
     * @param  count   Total number of failing tests
     */
    public void setFailingCount(int count) {
        failingTestCount = count;
    }

    /**
     * Return the total number of failing tests.
     *
     * @return Total number of failing tests
     */
    public int getFailingCount() {
        return failingTestCount;
    }

    /**
     * Set the total number of tests with errors.
     *
     * @param  count   Total number of error tests
     */
    public void setErrorCount(int count) {
        errorTestCount = count;
    }

    /**
     * Return the total number of tests with errors.
     *
     * @return Total number of error tests
     */
    public int getErrorCount() {
        return errorTestCount;
    }

    /**
     * Set the total number of killed tests.
     *
     * @param  count   Total number of killed tests
     */
    public void setKilledCount(int count) {
        killedTestCount = count;
    }

    /**
     * Return the total number of killed tests.
     *
     * @return Total number of killed tests
     */
    public int getKilledCount() {
        return killedTestCount;
    }

    /**
     * Set the total number of skipped tests.
     *
     * @param  count   Total number of skipped tests
     */
    public void setSkippedCount(int count) {
        skippedTestCount = count;
    }

    /**
     * Return the total number of skipped tests.
     *
     * @return Total number of skipped tests
     */
    public int getSkippedCount() {
        return skippedTestCount;
    }

    /**
     * Set the total number of blacklisted tests.
     *
     * @param  count   Total number of blacklisted tests
     */
    public void setBlacklistCount(int count) {
        blacklistTestCount = count;
    }

    /**
     * Return the total number of blacklisted tests.
     *
     * @return Total number of blacklisted tests
     */
    public int getBlacklistCount() {
        return blacklistTestCount;
    }

    /**
     * Set the total number of pending tests.
     *
     * @param  count   Total number of pending tests
     */
    public void setPendingCount(int count) {
        pendingTestCount = count;
    }

    /**
     * Return the total number of pending tests.
     *
     * @return Total number of pending tests
     */
    public int getPendingCount() {
        return pendingTestCount;
    }

    /**
     * Set the total number of running tests.
     *
     * @param  count   Total number of running tests
     */
    public void setRunningCount(int count) {
        runningTestCount = count;
    }

    /**
     * Return the total number of running tests.
     *
     * @return Total number of running tests
     */
    public int getRunningCount() {
        return runningTestCount;
    }



}
