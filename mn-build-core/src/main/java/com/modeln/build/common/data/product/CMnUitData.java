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


/**
 * Data object used to represent a single UIT in the database.  This 
 * object provides the object/relational mapping.
 * 
 * @author  Shawn Stafford
 */
public class CMnUitData extends CMnTestData {


    /** Name of the UIT, which also corresponds to the script filename without a uit extension. */
    private String testName;

    /** Number of steps executed during the test. */
    private int stepCount = 0;

    /** Number of steps that failed during the test. */
    private int stepFailures = 0;

    /** Number of steps that completed successfully during the test. */
    private int stepSuccesses = 0;

    /** Output of the test steps. */
    private StringBuffer stepResults = new StringBuffer();

    /** Identifies the browser session on the server */
    private String browserSession;


    /**
     * Set the name of the test.
     * 
     * @param   name    Name of the test
     */
    public void setTestName(String name) {
        testName = name;
    }

    /**
     * Return the name of the test.
     * 
     * @return  Test name
     */
    public String getTestName() {
        return testName;
    }


    /**
     * Increment the number of test steps by one. 
     */
    public void addStep() {
        stepCount++;
    }

    /**
     * Return the number of test steps processed. 
     *
     * @return  Number of completed steps 
     */
    public int getStepCount() {
        return stepCount;
    }

    /**
     * Increment the number of successful test steps by one.  The message string
     * will be appended to the step output to allow step execution tracking.
     * 
     * @param   msg   Information about the step
     */
    public void addSuccessfulStep(String msg) {
        stepSuccesses++;
        stepResults.append(msg);
    }

    /**
     * Return the number of test steps completed successfully.
     *
     * @return  Number of completed steps
     */
    public int getSuccessfulStepCount() {
        return stepSuccesses;
    }

    /**
     * Increment the number of failed test steps by one.  The message string
     * will be appended to the step output to allow step execution tracking.
     */
    public void addFailedStep(String msg) {
        stepFailures++;
        stepResults.append(msg);
    }

    /**
     * Return the number of test steps that failed.
     *
     * @return  Number of steps that failed
     */
    public int getFailedStepCount() {
        return stepFailures;
    }

    /**
     * Return the results of the step execution.
     *
     * @return    Step execution results
     */
    public String getStepResults() {
        return stepResults.toString();
    }

    /**
     * Set the browser session ID associated with the test. 
     *
     * @param   session    Browser session ID 
     */
    public void setSessionId(String session) {
        browserSession = session;
    }

    /**
     * Return the browser session ID associated with the test. 
     *
     * @return  Browser session ID 
     */
    public String getSessionId() {
        return browserSession;
    }


}
