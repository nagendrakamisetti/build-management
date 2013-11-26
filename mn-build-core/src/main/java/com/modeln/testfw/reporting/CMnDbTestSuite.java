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
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.text.SimpleDateFormat;

/**
 * Data object used to represent a UIT suite in the database. 
 * 
 * @author  Shawn Stafford
 */
public class CMnDbTestSuite implements Comparable {

    /** Timestamp used to prefix every test message line */
    private static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("HH:mm:ss.SSS - ");

    /** List of possible suite types */
    public static enum SuiteType {
        JUNIT, ACT, UIT, FLEX
    }

    /** Specify the type of test suite */
    private SuiteType type;

    /** ID used to group suites together */
    private long groupId = 0;

    /** Name used to identify similar suites */
    private String groupName;

    /** Unique key value used to identify a suite */
    private int suiteId;

    /** Unique key value used to identify the parent build */
    private int buildId;

    /** Record the starting time of the test suite */
    private Date startTime;

    /** Record the completion time of the test suite */
    private Date endTime;

    /** Execution time of the individual tests within the suite */
    private long execTime = 0;

    /** Name of the test suite */
    private String suiteName;

    /** Total number of tests which are still waiting to be executed */
    private int pendingTestCount = 0;

    /** Total number of tests currently executing */
    private int runningTestCount = 0;

    /** Total number of tests in the suite that passed */
    private int passingTestCount = 0;

    /** Total number of tests in the suite that were skipped */
    private int skippedTestCount = 0;

    /** Total number of tests in the suite that ran longer than max time */
    private int longTestCount = 0;

    /** Total number of tests that have been executed up to this point */
    private int execTestCount = 0;

    /** Total number of tests in the suite that have failed */
    private int failingTestCount = 0;

    /** Total number of tests in the suite that have exited with an error */
    private int errorTestCount = 0;

    /** Total number of tests in the suite that have been killed */
    private int killedTestCount = 0;

    /** Total number of tests in the suite that have not been executed due to blacklisting */
    private int blacklistTestCount = 0;

    /** Total number of tests in the suite */
    private int testCount = 0;

    /** An optional URL for connecting to the Model N database */
    private String jdbcUrl;

    /** Name of the environment where the tests were executed */
    private String environment;

    /** Maximum number of threads that can be used to execute tests within this suite */
    private int maxThreads = 1;

    /** List of options associated with the suite */
    private HashMap _options = new HashMap();

    /** Host environment information */
    private CMnDbHostData hostEnv;


    /**
     * Contruct the suite class.
     *
     * @param  type  Type of suite
     */
    public CMnDbTestSuite(SuiteType type) {
        this.type = type;
    }


    /**
     * Return the type of test suite.
     *
     * @return  Suite type
     */
    public SuiteType getSuiteType() {
        return type;
    }

    /**
     * Determine if the suite is of the specified type.
     *
     * @param   type   Suite type
     * @return  TRUE if the suite matches the specified type
     */
    public boolean isSuiteType(SuiteType type) {
        return (this.type == type);
    }

    /**
     * Compares this object to the specified object for order.
     * Objects are compared using their ID values.
     * Returns negative if this object is less than the specified object.
     * Returns positive if this object is greater than the specified object.
     * Returns zero if the objects are equal.
     * 
     * @return a negative integer, zero, or a positive integer as this 
     *         object is less than, equal to, or greater than the specified object 
     */
    public int compareTo(Object data) throws ClassCastException {
        if (suiteId > ((CMnDbTestSuite) data).getId()) {
            return 1;
        } else if (suiteId < ((CMnDbTestSuite) data).getId()) {
            return -1;
        } else {
            return 0;
        }
    }


    /**
     * Set a unique ID that can be used to identify the suite group.  The ID is
     * intended to be set at the time when the suite is being generated,
     * and would most likely correspond to a system timestamp or random 
     * number that would distinguish the suite from a similar suite 
     * generated by a subsequent use of the suite generator.  The ID should
     * match another suite ID only if the suites were generated by the same
     * instance of the suite generator but split into multiple parts due to 
     * the size of the suite or the need to distribute the parts to different
     * execution nodes.
     *
     * @param  id   Suite generation group ID
     */
    public void setGroupId(long id) {
        groupId = id;
    }

    /**
     * Return the ID used to identify the instance of the suite generator.
     *
     * @return Suite generation group ID
     */
    public long getGroupId() {
        return groupId;
    }

    /**
     * Set a name that can be used to identify the suite group.  This name is
     * intended to identify like suites that were created by the same suite
     * generator instance but split into different suites due to the size of 
     * the suite or the need to distribute the parts to different execution
     * nodes.
     *
     * @param  name   Suite generation group name
     */
    public void setGroupName(String name) {
        groupName = name;
    }

    /**
     * Return the name used to identify the instance of the suite generator.
     *
     * @return  Suite generation group name
     */
    public String getGroupName() {
        return groupName;
    }


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
     * Set an optional JDBC URL that can be used to specify the database
     * connection information for the current test suite.  This information
     * can be used by developers to query the database directly to determine
     * the data against which the suite was executed.
     *
     * @param   url   JDBC URL
     */
    public void setJdbcUrl(String url) {
        jdbcUrl = url;
    }

    /**
     * Return the JDBC URL for the database against which the test suite was executed.
     *
     * @return JDBC URL
     */
    public String getJdbcUrl() {
        return jdbcUrl;
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
     * Returns TRUE if the test is complete, FALSE otherwise.
     * A suite is considered complete when both the start and end date are set.
     *
     * @return TRUE if the suite is complete
     */
    public boolean isComplete() {
        return ((startTime != null) && (endTime != null));
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
        } else {
            elapsedTime = execTime;
        }
        return elapsedTime;
    }

    /**
     * Set the start and end time of the suite using an elapsed time value.
     * This method is really used when constructing a dummy suite where only
     * the elapsed execution time is known.  In this case, the start and
     * end date have no relation to the real execution time, but are used
     * purely as a means to calculate the elapsed time of the suite.
     *
     * @param  time   Elapsed time
     */
    public void setElapsedTime(long time) {
        execTime = time;
/*
        // Pick an arbitrary end time
        endTime = new Date();

        // Calculate the start time using the elapsed time
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(endTime.getTime() - time);
        startTime = calendar.getTime();
*/
    }

    /**
     * Adjust the specified time by the specified number of milliseconds.
     *
     * @param   date   Time to be adjusted
     * @param   ms     Number of milliseconds to adjust the time
     * @return  Adjusted time
     */
    public static Date adjustTime(Date date, long ms) {
        Date newdate = null;
        if (date != null) {
            GregorianCalendar calendar = new GregorianCalendar();
            long adjustedTime = date.getTime() + ms;
            calendar.setTimeInMillis(adjustedTime);
            newdate = calendar.getTime();
        }
        return newdate;
    }

    /**
     * Adjust the start time by the specified number of milliseconds.
     *
     * @param   ms     Number of milliseconds to adjust the time
     */
    public void adjustStartTime(long ms) {
        startTime = adjustTime(startTime, ms);
    }

    /**
     * Adjust the end time by the specified number of milliseconds.
     *
     * @param   ms     Number of milliseconds to adjust the time
     */
    public void adjustEndTime(long ms) {
        endTime = adjustTime(endTime, ms);
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
    public void setHostData(CMnDbHostData data) {
        hostEnv = data;
    }

    /**
     * Return the host environment information.
     *
     * @return  Host environment data
     */
    public CMnDbHostData getHostData() {
        return hostEnv;
    }

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
     * Set the total number of tests that have failed or exited
     * with errors. 
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
     * Set the total number of tests that have exited with errors.
     *
     * @param  count   Total number of tests with errors 
     */
    public void setErrorCount(int count) {
        errorTestCount = count;
    }

    /**
     * Return the total number of tests with errors. 
     *
     * @return Total number of tests with errors. 
     */
    public int getErrorCount() {
        return errorTestCount;
    }

    /**
     * Set the total number of tests that have been killed. 
     *
     * @param  count   Total number of tests that were killed 
     */
    public void setKilledCount(int count) {
        killedTestCount = count;
    }

    /**
     * Return the total number of tests that have been killed. 
     *
     * @return Total number of tests that were killed 
     */
    public int getKilledCount() {
        return killedTestCount;
    }


    /**
     * Set the total number of skipped tests.
     *
     * @param  count   Total number of skipped tests
     */
    public void setSkipCount(int count) {
        skippedTestCount = count;
    }

    /** 
     * Return the total number of skipped tests.
     *
     * @return Total number of skipped tests
     */
    public int getSkipCount() {
        return skippedTestCount;
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

    /**
     * Set the total number of executed tests.
     *
     * @param  count   Total number of tests
     */
    public void setExecutedCount(int count) {
        execTestCount = count;
    }

    /**
     * Return the total number of executed tests.
     *
     * @return Total number of tests
     */
    public int getExecutedCount() {
        return execTestCount;
    }

    /**
     * Set the name of the environment where the tests were executed.
     * This information will be used to group related test results 
     * together.  For example, it may be desirable to view all of the 
     * results from the build, certification, or migration environments
     * independently.
     *
     * @param   name   Environment name
     */
    public void setEnvironmentName(String name) {
        environment = name;
    }

    /**
     * Return the name of the environment where the tests were executed.
     *
     * @return  Environment name
     */
    public String getEnvironmentName() {
        return environment;
    }

    /**
     * Set the maximum number of threads that can be used to execute tests
     * in this suite. 
     *
     * @param  count   Maximum number of threads 
     */
    public void setMaxThreadCount(int count) {
        maxThreads = count;
    }

    /**
     * Return the maximum number of threads that can be used to execute tests. 
     *
     * @return Maximum number of threads 
     */
    public int getMaxThreadCount() {
        return maxThreads;
    }


    /**
     * Set the list of options used to construct the suite.
     *
     * @param  options  List of options
     */
    public void setOptions(Map options) {
        _options.putAll(options);
    }

    /**
     * Add an option to the list of JavaDoc test options that are used to
     * construct the suite.  The options are used to classify and group
     * test suites by option type.  Option values are typically either
     * "true" or "false."
     *
     * @param  name     Option name
     * @param  value    Option value
     */
    public void addOption(String name, String value) {
        _options.put(name, value);
    }

    /**
     * Returns the list of options used to construct the test suite.
     *
     * @return List of option name/value pairs
     */
    public Map getOptions() {
        return _options;
    }

    /** 
     * Returns true if the name/value pair of every entry in the 
     * specified set matches the name/value pair in the current list
     * of test suite options.
     *
     * @param  options  List of options to compare to the current suite
     * @return TRUE if the list of options is the same as the current suite
     */
    public boolean optionsEqual(Map options) {
        return _options.equals(options);
    }

    /**
     * Compares each option to the current suite options.  Returns true
     * if the suite contains all of the specified options and if all
     * of the corresponding option values are equal. 
     *
     * @param  options   List of options to check
     * @return TRUE if all options are found in the suite
     */
    public boolean hasAllOptions(Map options) {
        boolean hasAll = true;

        Iterator keys = options.keySet().iterator();
        while (keys.hasNext()) {
            Object opName = keys.next();
            Object opValue = options.get(opName);
            
            // Determine if the suite contains the current option
            if (_options.containsKey(opName)) {
                Object val = _options.get(opName);
                if (!opValue.equals(val)) {
                    hasAll = false;
                }
            } else {
                hasAll = false;
            }
        }

        return hasAll;
    }

    /**
     * Compare each option from the current suite options.  Return the
     * differences between the options.  This is primarily used to 
     * debug the hasAllOptions method. 
     *
     * @param  options   List of options to check
     * @return Formatted text comparing the options 
     */
    public String compareOptions(Map options) {
        StringBuffer diff = new StringBuffer();
        diff.append("Option           \t This       \t Compared To\n");
        diff.append("=================\t ===========\t ===========\n");

        Iterator keys = options.keySet().iterator();
        while (keys.hasNext()) {
            Object opName = keys.next();
            Object opValue = options.get(opName);

            // Determine if the suite contains the current option
            diff.append(opName + "\t " + opValue + "\t ");
            if (_options.containsKey(opName)) {
                Object val = _options.get(opName);
                diff.append(val + "\n");
            } else {
                diff.append("N/A\n");
            }

        }

        return diff.toString();
    }


}
