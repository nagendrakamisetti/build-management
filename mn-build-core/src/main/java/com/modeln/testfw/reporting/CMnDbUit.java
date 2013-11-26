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
import java.util.Enumeration;


/**
 * Data object used to represent a single UIT in the database.  This 
 * object provides the object/relational mapping.
 * 
 * @author  Shawn Stafford, Karen An
 */
public class CMnDbUit extends CMnDbTestData {

    /** Name of the UIT, which also corresponds to the script filename without a uit extension. */
    private String testName;

    /** Number of steps that failed during the test. */
    private int stepFailures = 0;

    /** Number of steps that completed successfully during the test. */
    private int stepSuccesses = 0;

    /** Test step objects. */
    private Vector steps = new Vector();

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
     * Return the list of UIT steps.
     *
     * @return Enumeration of test steps
     */
    public Vector getSteps() {
        return steps;
    }

    /**
     * Set the list of UIT steps.
     *
     * @param  list   Vector of CMnDbUitStep objects
     */
    public void setSteps(Vector list) {
        steps = list;
    }

    /**
     * Increment the number of test steps by one. 
     */
    public void addStep(CMnDbUitStep step) {
        // Calculate the pass/fail count as the steps are added
        switch (step.getStatus()) {
            case CMnDbUitStep.PASS:  stepSuccesses++; break;
            case CMnDbUitStep.FAIL:  stepFailures++;  break;
            case CMnDbUitStep.ERROR: stepFailures++;  break;
        }
        steps.add(step);
    }

    /**
     * Return the number of test steps processed. 
     *
     * @return  Number of completed steps 
     */
    public int getStepCount() {
        return steps.size();
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
        StringBuffer stepResults = new StringBuffer();

        // Iterate through each step and convert to a string
        Enumeration stepList = steps.elements();
        for (Enumeration e = steps.elements(); e.hasMoreElements(); ) {
            CMnDbUitStep currentStep = (CMnDbUitStep) e.nextElement();
            stepResults.append(currentStep.getMessage() + "\n");
        }
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

    /**
     * Append test output to the message.
     *
     * @param   text    Message content to be appended
     */
    public void addMessage(String text) {
        //String timestamp = TIMESTAMP_FORMAT.format(new Date());
        message.append("\n"+text);
    }


}
