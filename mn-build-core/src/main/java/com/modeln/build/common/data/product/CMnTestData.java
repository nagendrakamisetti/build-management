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

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Data object used to represent a single test in the database.  This 
 * object provides the object/relational mapping.
 * 
 * @author  Shawn Stafford
 */
public class CMnTestData {

    /** Timestamp used to prefix every test message line */
    private static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("HH:mm:ss.SSS - ");

    /** Indicates that the completion status of the test is unknown */
    public static final int UNKNOWN_STATUS = -1;

    /** Test status to indicate that the test passed */
    public static final int PASS = 0;

    /** Test status to indicate that the test failed */
    public static final int FAIL = 1;

    /** Test status to indicate that the test encountered an unexpected error */
    public static final int ERROR = 2;



    /** Unique primary key which identifies the test */
    private int testId;

    /** Unique foreign key with identifies the parent test suite */
    private int suiteId;

    /** Record the starting time of the current test */
    private Date startTime;

    /** Record the completion time of the current test */
    private Date endTime;

    /** Debugging output and stack traces associated with the current test */
    private StringBuffer message = new StringBuffer();

    /** Record the status of the test */
    private int testStatus = UNKNOWN_STATUS;


    /**
     * Set the unique key value used to identify a single test entry.
     * This value is typically an auto-incremented value created by the database when
     * a new entry is inserted into the table.
     *
     * @param    id     Key value
     */
    public void setId(int id) {
        testId = id;
    }

    /**
     * Return the unique key used to identify the test.  A null key value indicates that
     * the test has not been inserted into the database.
     *
     * @return  Key value
     */ 
    public int getId() {
        return testId;
    }

    /**
     * Set the unique key value used to identify the parent test suite. 
     * This value is typically an auto-incremented value created by the database when
     * a new entry is inserted into the table.
     *
     * @param    id     Key value
     */
    public void setParentId(int id) {
        suiteId = id;
    }

    /**
     * Return the unique key used to identify the parent test suite.  A null key value 
     * indicates that a parent does not exist.
     *
     * @return  Key value
     */
    public int getParentId() {
        return suiteId;
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
     * Append test output to the message.
     *
     * @param   text    Message content to be appended
     */
    public void addMessage(String text) {
        String timestamp = TIMESTAMP_FORMAT.format(new Date());
        message.append(timestamp + text);
    }

    /**
     * Remove all content from the message string.
     */
    public void clearMessage() {
        int length = message.length();
        if (length > 0) {
            message.delete(0, length - 1);
        }
    }

    /**
     * Return the messages, exceptions, and debug statements associated
     * with the test.
     *
     * @return  Message text
     */
    public String getMessage() {
        return message.toString();
    }


    /**
     * Set the execution status of the test to PASS, FAIL, or ERROR.
     * This method does not protect the user from changing the status in
     * an incorrect manner, such as changing the status from ERROR to PASS
     * or from ERROR to FAIL.  The test status should always indicate the 
     * most appropriate status, not just the most recent.  Use the 
     * updateStatus method when it is important that the previous status
     * be considered.
     * 
     * Since JUnit only reports the first failure, and an error condition
     * effectively negates the reliability of the test, it is appropriate
     * to have a single indicator of the completion status rather than
     * a tally of errors or failures for a single test.
     *
     * @param   status  Test completion status (PASS, FAIL, ERROR)
     */
    public void setStatus(int status) {
        if ((status == PASS) || (status == FAIL) || (status == ERROR)) {
            testStatus = status;
        } else {
            testStatus = UNKNOWN_STATUS;
        }
    }

    /**
     * Set the execution status of the test the most significant status
     * based on the new and previous status.  For example, if the previous
     * status is ERROR, a call to updateStatus should not change the status
     * value regardless of the updated value.  If the previous value is PASS,
     * any status update should be applied.  This method ensures that the 
     * most significant status is always preserved.
     * 
     * Since JUnit only reports the first failure, and an error condition
     * effectively negates the reliability of the test, it is appropriate
     * to have a single indicator of the completion status rather than
     * a tally of errors or failures for a single test.
     *
     * @param   status  Test completion status (PASS, FAIL, ERROR)
     */
    public void updateStatus(int status) {
        switch (status) {
            case ERROR:
                testStatus = status;
                break;
            case PASS: 
                if (testStatus == UNKNOWN_STATUS) {
                    testStatus = status;
                }
                break;
            case FAIL:
                if ((testStatus == PASS) || (testStatus == UNKNOWN_STATUS)) {
                    testStatus = status;
                }
                break;
            case UNKNOWN_STATUS:
                // Don't do anything.  If it's already set to unknown then we 
                // don't need to change it.  And if it's set to something else,
                // it IS known and we don't need to change it.
                break;
            default:
                testStatus = UNKNOWN_STATUS;
                break;
        }

    }


    /**
     * Return the completion status of the test.
     *
     * @return  Completion status (PASS, FAIL, ERROR)
     */
    public int getStatus() {
        return testStatus;
    }

    /**
     * Return the number of milliseconds elapsed between the start and 
     * end time.
     * 
     * @return Elapsed time in milliseconds
     */
    public long getElapsedTime() {
        long elapsedTime = 0;
        if ((startTime != null) && (endTime != null)) {
            elapsedTime = endTime.getTime() - startTime.getTime();
        }
        return elapsedTime;
    }

}
