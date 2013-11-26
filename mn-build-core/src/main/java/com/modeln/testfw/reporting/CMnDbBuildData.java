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
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.text.SimpleDateFormat;

/**
 * Data object used to represent a compilation and packaging of the product. 
 * 
 * @author  Shawn Stafford
 */
public class CMnDbBuildData implements Comparable {

    /** Timestamp used to prefix every test message line */
    private static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("HH:mm:ss.SSS - ");

    /** List of possible source control types */
    public static enum VersionControlType {
        PERFORCE, GIT 
    }

    /** Unique key value used to identify a build */
    private int buildId;

    /** Record the starting time of the test suite */
    private Date startTime;

    /** Record the completion time of the test suite */
    private Date endTime;

    /** Version string which identifies the build */
    private String buildVersion;

    /** Status string which identifies the build release (GA, P1, etc) */
    private String releaseId;

    /** Specify the type of source control system */
    private VersionControlType versionControlType;

    /** Root version control location where the build was obtained from */
    private String versionControlRoot;

    /** Provides a version control ID which can be used to obtain the build source from version control */
    private String versionControlId;

    /** Provides a directory location where the build binaries are archived */
    private String downloadUri;

    /** Jenkins URL for the job used to invoke the build */
    private String jobUrl;

    /** User generated comments about the build */
    private String comments;

    /** Information about the build environment */
    private CMnDbHostData buildEnv;

    /** Release information associated with the build. */
    private CMnDbBuildStatusData buildStatus; 

    /** List of build metrics associated with the build */
    private Vector metrics = new Vector();

    /** List of logs associated with the build. */
    private Vector logs = new Vector();

    /** List of test summaries for the build */
    private Hashtable testSummary = new Hashtable();

    /** List of test results for the build */
    private Vector<CMnDbTestData> testData = new Vector(); 


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
        if (buildId > ((CMnDbBuildData) data).getId()) {
            return 1;
        } else if (buildId < ((CMnDbBuildData) data).getId()) {
            return -1;
        } else {
            return 0;
        }
    }

    /**
     * Set the unique key value used to identify a single build.
     * This value is typically an auto-incremented value created by the database when
     * a new entry is inserted into the table.
     *
     * @param    id     Key value
     */
    public void setId(int id) {
        buildId = id;
    }

    /**
     * Return the unique key used to identify the build. 
     *
     * @return  Key value
     */
    public int getId() {
        return buildId;
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
     * Set the version string for the build. 
     * 
     * @param  ver    Version string 
     */
    public void setBuildVersion(String ver) {
        buildVersion = ver;
    }

    /**
     * Return the build version. 
     * 
     * @return  Version string 
     */
    public String getBuildVersion() {
        return buildVersion;
    }

    /**
     * Set the version control type that is used to interpret the version
     * control values.
     *
     * @param  type   Version control type
     */
    public void setVersionControlType(String type) {
        if (type != null) {
            versionControlType = VersionControlType.valueOf(type.toUpperCase());
        } else {
            versionControlType = null;
        }
    }

    /**
     * Set the version control type that is used to interpret the version
     * control values. 
     *
     * @param  type   Version control type
     */
    public void setVersionControlType(VersionControlType type) {
        versionControlType = type;
    }

    /**
     * Return the version control type. 
     *
     * @return Version control type
     */
    public VersionControlType getVersionControlType() {
        return versionControlType;
    }

    /**
     * Set the version control root that is used to identify the location from 
     * which the source code was obtained for the build.  In perforce, the 
     * version control root refers to the depot.  In CVS, this might refer to
     * a respository directory.
     *
     * @param  root   Version control root
     */
    public void setVersionControlRoot(String root) {
        versionControlRoot = root;
    }

    /**
     * Return a root location that can be used to locate and obtain the source
     * code from the version control system.
     *
     * @return Version control root
     */
    public String getVersionControlRoot() {
        return versionControlRoot;
    }
 
    /**
     * Set the version control identifier that is used to identify the point in
     * time at which the source code was obtained for the build.  In perforce,
     * the version control ID refers to the Changelist number.  In CVS, this might
     * refer to a tag or similar marker. 
     *
     * @param  id     Version control identifier 
     */
    public void setVersionControlId(String id) {
        versionControlId = id;
    }

    /**
     * Return an identifier that can be used to sync the source code and recreate
     * the build. 
     *
     * @return  Version control identifier
     */
    public String getVersionControlId() {
        return versionControlId;
    }

    /**
     * Set location of the build binaries. 
     *
     * @param  uri   Location of the build
     */
    public void setDownloadUri(String uri) {
        downloadUri = uri;
    }

    /**
     * Return the location of the build binaries. 
     *
     * @return  Location of the build
     */
    public String getDownloadUri() {
        return downloadUri;
    }

    /**
     * Set the Jenkins job URL for the build. 
     *
     * @param  url   Jenkins job URL 
     */
    public void setJobUrl(String url) {
        jobUrl = url;
    }

    /**
     * Return the Jenkins job URL. 
     *
     * @return  Jenkins job URL 
     */
    public String getJobUrl() {
        return jobUrl;
    }

    /**
     * Set user comments that describe why the build was performed. 
     *
     * @param  text   Description of the build 
     */
    public void setComments(String text) {
        comments = text;
    }

    /**
     * Return a description of the build. 
     *
     * @return  Description of the build 
     */
    public String getComments() {
        return comments;
    }

    /**
     * Set the host environment information. 
     *
     * @param  data    Host environment data 
     */
    public void setHostData(CMnDbHostData data) {
        buildEnv = data;
    }

    /**
     * Return the host environment information. 
     *
     * @return  Host environment data 
     */
    public CMnDbHostData getHostData() {
        return buildEnv;
    }

    /**
     * Provide a summary of the test execution for this build.
     *
     * @param   group   Test category
     * @param   data    Test data
     */
    public void setTestSummary(String group, CMnDbTestSummaryData data) {
        testSummary.put(group, data);
    }

    /**
     * Return a summary of the test execution for this build.
     *
     * @param   group    Test category
     *
     * @return  Test data
     */
    public CMnDbTestSummaryData getTestSummary(String group) {
        CMnDbTestSummaryData data = null;

        if (testSummary.containsKey(group)) {
            data = (CMnDbTestSummaryData) testSummary.get(group);
        }

        return data;
    }

    /**
     * Return the complete list of test data organized by group.
     *
     * @return Hashtable which maps groups to the corresponding test data
     */
    public Hashtable getTestSummary() {
        return testSummary;
    }

    /**
     * Return a list of the test groups.
     * @return  List of test groups
     */
    public Enumeration getTestGroups() {
        return testSummary.keys();
    }

    /**
     * Set the list of test results.
     *
     * @param   tests   List of tests
     */
    public void setTestData(Vector<CMnDbTestData> tests) {
        testData = tests;
    }

    /**
     * Return the list of test results.
     *
     * @return  Test results
     */
    public Enumeration<CMnDbTestData> getTestData() {
        if (testData != null) {
            return testData.elements();
        } else {
            return null;
        }
    }

    /**
     * Return the total number of tests associated with the build.
     *
     * @return Number of test results
     */
    public int getTestCount() {
        return testData.size();
    }

    /**
     * Return the total number of tests associated with the build based on the
     * test summary information.
     *
     * @return Number of test results
     */
    public int getSummaryTestCount() {
        int count = 0;

        Enumeration results = testSummary.elements();
        while (results.hasMoreElements()) {
            CMnDbTestSummaryData summary = (CMnDbTestSummaryData) results.nextElement();
            count = count + summary.getTotalCount(); 
        }

        return count;
    }


    /**
     * Add a metric to the list of metrics associated with the build.
     *
     * @param   metric   Build metric 
     */
    public void addMetric(CMnDbMetricData metric) {
        metrics.add(metric);
    }

    /**
     * Set the list of metrics associated with the build.
     *
     * @param  list   List of metrics
     */
    public void setMetrics(Vector list) {
        metrics = list;
    }

    /**
     * Return the list of metrics associated with the build.
     *
     * @return List of build metrics
     */
    public Vector getMetrics() {
        return metrics;
    }


    /**
     * Add a log file to the list of logs associated with the build.
     *
     * @param   log   Log entry
     */
    public void addLog(CMnDbLogData log) {
        logs.add(log);
    }

    /**
     * Set the list of log files associated with the build.
     *
     * @param  list   List of logs
     */
    public void setLogs(Vector list) {
        logs = list;
    }

    /**
     * Return the list of logs associated with the build.
     * 
     * @return List of build logs
     */
    public Vector getLogs() {
        return logs;
    }

    /**
     * Set the release identifier for the build.  A release identifier
     * is an indicator such as GA or P1 that indicates what release of
     * the current version the build is being targeted for.
     *
     * @param id  Release identifier
     */
    public void setReleaseId(String id) {
        releaseId = id;
    }

    /**
     * Return the release identifier for the build.  The release 
     * identifier indicates a target for the build, such as GA
     * or P1.
     *
     * @return Release identifier
     */
    public String getReleaseId() {
        return releaseId;
    }

    /** 
     * Set the release status information for the build.
     *
     * @param  status  Build status
     */
    public void setStatus(CMnDbBuildStatusData status) {
        buildStatus = status;
    }

    /**
     * Return release status information for the build.
     * 
     * @return Build status information
     */
    public CMnDbBuildStatusData getStatus() {
        return buildStatus;
    }

}
