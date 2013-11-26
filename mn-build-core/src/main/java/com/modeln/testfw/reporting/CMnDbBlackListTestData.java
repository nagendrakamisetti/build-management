package com.modeln.testfw.reporting;

import com.modeln.build.common.data.product.CMnTestData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: vmalley
 * Date: 9/16/11
 * Time: 3:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class CMnDbBlackListTestData {


    /** Timestamp used to prefix every test message line */
    protected static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("HH:mm:ss.SSS - ");


    /** the value of timeout that made this test fail */
    private int timeout;

    /** Record the starting time of the current test */
    private Date startDate;

    /** Record the completion time of the current test */
    private Date endDate;

    /** Source control location of the build */
    private String version_ctrl_root;

    /** Name of host where the test was executed */
    private String hostname;

    /** Reason of blacklisting */
    private String message;

    CMnDbBlackListTestData(){

    }


    /** constructor based on a parent class already existing
     * @param data test already existing
     *
     * */
    CMnDbBlackListTestData(CMnDbTestData data){
        startDate = data.getStartTime();
        endDate = data.getEndTime();
        message = data.getMessage();
    }


    /**
     * Compares this object to the specified object for order.
     * Objects are compared using their running time.
     * Returns negative if this object is less than the specified object.
     * Returns positive if this object is greater than the specified object.
     * Returns zero if the objects are equal.
     *
     * @return a negative integer, zero, or a positive integer as this
     *         object is less than, equal to, or greater than the specified object
     */
    public int compareTo(Object data) throws ClassCastException {
        CMnDbBlackListTestData compData = (CMnDbBlackListTestData) data;

        // Sort by execution time if they are not the same
        long elapsed = getElapsedTime();
        long compElapsed = compData.getElapsedTime();
        if (elapsed > compElapsed) {
            return -1;
        } else if (elapsed < compElapsed) {
            return 1;
        } else{
            return 0;
        }


    }

    /**
     * Determines if the specified test matches the current test.
     *
     * @param   test  Test information
     * @return  TRUE if the objects match
     */
    public boolean matches(CMnDbBlackListTestData test) {
        boolean versionmatch = false;
        if ((getVersionCtrl() != null) && (test.getVersionCtrl() != null)) {
            versionmatch = getVersionCtrl().equalsIgnoreCase(test.getVersionCtrl());
        } else if ((getVersionCtrl() == null) && (test.getVersionCtrl() == null)) {
            versionmatch = true;
        }
        return versionmatch;
    }


    public int getTimeout() {
        return timeout;
    }

        /**
     * Set the timeout of the test.
     *
     * @param   time    maximum time a test can run
     */
        public void setTimeout(int time) {
        timeout = time;
    }

    /**
     * Set the source control location of the build.
     *
     * @param   version    source control location
     */
    public void setVersionCtrl(String version) {
        version_ctrl_root = version;
    }

    public String getVersionCtrl() {
        return version_ctrl_root;
    }

    /**
     * Set the name of the host.
     *
     * @param   host    name of the host
     */
    public void setHostname(String host) {
        hostname = host;
    }

    public String getHostname() {
        return hostname;
    }

    /**
     * Set the starting time of the test.
     *
     * @param   date    Starting time
     */
    public void setStartTime(Date date) {
        startDate = date;
    }

    /**
     * Return the starting time of the test.
     *
     * @return  Starting time
     */
    public Date getStartTime() {
        return startDate;
    }

    /**
     * Set the ending time of the test.
     *
     * @param   date    Ending time
     */
    public void setEndTime(Date date) {
        endDate = date;
    }

    /**
     * Return the ending time of the test.
     *
     * @return  Ending time
     */
    public Date getEndTime() {
        return endDate;
    }

    /**
     * Append test output to the message.
     *
     * @param   text    Message content to be appended
     */
    public void addMessage(String text) {
        String timestamp = TIMESTAMP_FORMAT.format(new Date());
        message += (timestamp + text);
    }

    /**
     * Remove all content from the message string.
     */
    public void clearMessage() {
        int length = message.length();
        if (length > 0) {
            message = "";
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
     * Return the number of milliseconds elapsed between the start and
     * end time.
     *
     * @return Elapsed time in milliseconds
     */
    public long getElapsedTime() {
        long elapsedTime = 0;
        if ((startDate != null) && (endDate != null)) {
            elapsedTime = endDate.getTime() - startDate.getTime();
        }
        return elapsedTime;
    }

    /**
     * Returns a string representing the display name for the test.
     *
     * @return String representing the name of the test
     */
    public String toString() {
        return version_ctrl_root;
    }


}
