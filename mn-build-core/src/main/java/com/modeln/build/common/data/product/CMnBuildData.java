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
import java.util.Set;
import java.util.Vector;
import java.text.SimpleDateFormat;

/**
 * Data object used to represent a compilation and packaging of the product. 
 * 
 * @hibernate.class table="build"
 *
 * @author  Shawn Stafford
 */
public class CMnBuildData {

    /** Timestamp used to prefix every test message line */
    private static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("HH:mm:ss.SSS - ");

    /** Unique key value used to identify a build */
    private int buildId;

    /** Record the starting time of the test suite */
    private Date startTime;

    /** Record the completion time of the test suite */
    private Date endTime;

    /** Version string which identifies the build */
    private String buildVersion;

    /** URI which stores the build */
    private String download_uri;

    /** URL of the Jenkins job used to invoke the build */
    private String job_url;

    /** Specifies the type of version control system used */
    private String versionControlType;

    /** Provides a version control ID which can be used to obtain the build source from version control */
    private String versionControlId;

    /** Provides a version control path which can be used to obtain the build source from version control */
    private String versionControlRoot;
    
    /** User generated comments about the build */
    private String comments;

    /** Username of the build account on the host */
    private String username;

    /** Name of the build host computer */
    private String hostname;

    /** Version of the JDK used for the build */
    private String jdkVersion;

    /** Distributor of the JDK implementation */
    private String jdkVendor;

    /** Name of the build operating system */
    private String osName;

    /** CPU architecture of the build operating system */
    private String osArch;

    /** Version of the build operating system */
    private String osVersion;

    /** List of logs associated with the build. */
    private Set logs;
    
    /** Status of the build */
    private String buildVersionStatus;

    /** Total number of tests in the suite that passed */
    private int passingTestCount = 0;

    /** Total number of tests in the suite */
    private int testCount = 0;


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
     * @hibernate.id column="build_id" generator-class="native"
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
     * @hibernate.property column="start_date" type="timestamp" not-null="true"
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
     * @hibernate.property column="end_date" type="timestamp" not-null="true"
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
     * @hibernate.property column="build_version" type="string" length="127" not-null="true"
     *
     * @return  Version string 
     */
    public String getBuildVersion() {
        return buildVersion;
    }

    /**
     * Set the download uri for the build. 
     * 
     * @param  uri    Download URI 
     */
    public void setDownloadUri(String uri) {
        download_uri = uri;
    }

    /**
     * Return the build version. 
     * 
     * @hibernate.property column="download_uri" type="string" length="127" not-null="true"
     *
     * @return  Download URI 
     */
    public String getDownloadUri() {
        return download_uri;
    }

    /**
     * Set the Jenkins URL for the build.
     *
     * @param  url    Jenkins job URL 
     */
    public void setJobUrl(String url) {
        job_url = url;
    }

    /**
     * Return the Jenkins job URL. 
     *
     * @hibernate.property column="job_url" type="string" length="127" not-null="false"
     *
     * @return  Jenkins job URL 
     */
    public String getJobUrl() {
        return job_url;
    }


    /** 
     * Set the release status information for the build.  The status is usually
     * a version-related string describing the current status of the build, 
     * such as "Patch 1" or "Release Candidate".
     *
     * @param  status  Build status
     */
    public void setVersionStatus(String status) {
        buildVersionStatus = status;
    }

    /**
     * Return release status information for the build.
     * 
     * @hibernate.property column="build_status" type="string" length="127" not-null="true"
     *
     * @return Build status information
     */
    public String getVersionStatus() {
        return buildVersionStatus;
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
     * @hibernate.property column="version_ctrl_id" type="string" length="127"
     *
     * @return  Version control identifier
     */
    public String getVersionControlId() {
        return versionControlId;
    }

    /**
     * Set the version control type that is used to interpret the version
     * control values.
     *
     * @param  type   Version control type
     */
    public void setVersionControlType(String type) {
        versionControlType = type;
    }

    /**
     * Return the version control type.
     *
     * @return Version control type
     */
    public String getVersionControlType() {
        return versionControlType;
    }


    /**
     * Set the version control path that is used to identify the source control
     * location from which the source code was obtained for the build.  In perforce,
     * the root path refers to a depot location.  In CVS, a similar path refers to
     * the source location relative to the root of the server.
     *
     * @param  path     Version control root path 
     */
    public void setVersionControlRoot(String path) {
        versionControlRoot = path;
    }

    /**
     * Return an identifier that can be used to sync the source code and recreate
     * the build. 
     *
     * @hibernate.property column="version_ctrl_root" type="string" length="255"
     *
     * @return  Version control root path
     */
    public String getVersionControlRoot() {
        return versionControlRoot;
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
     * @hibernate.property column="comments" type="text"
     *
     * @return  Description of the build 
     */
    public String getComments() {
        return comments;
    }

    /**
     * Set the name of the account used to perform the build.
     *
     * @param  name   System account name
     */
    public void setUsername(String name) {
        username = name;
    }

    /**
     * Return the name of the account used to perform the build.
     *
     * @hibernate.property column="username" type="string" length="127" not-null="true"
     *
     * @return  System account name
     */
    public String getUsername() {
        return username;
    }

    /**
     * Set the host on which the build was performed.
     *
     * @param  host   Name of the host computer
     */
    public void setHostname(String host) {
        hostname = host;
    }

    /**
     * Return the name of the host on which the build was created.
     *
     * @hibernate.property column="hostname" type="string" length="127" not-null="true"
     *
     * @return  Hostname
     */
    public String getHostname() {
        return hostname;
    }

    /**
     * Set the version of the JDK implementation. 
     *
     * @param  version   JDK version number 
     */
    public void setJdkVersion(String version) {
        jdkVersion = version;
    }

    /**
     * Return the version number of the JDK implementation. 
     *
     * @hibernate.property column="jdk_version" type="string" length="127" not-null="true"
     *
     * @return  JDK version number 
     */
    public String getJdkVersion() {
        return jdkVersion;
    }

    /**
     * Set the vendor of the JDK implementation.
     *
     * @param  vendor   JDK vendor name 
     */
    public void setJdkVendor(String vendor) {
        jdkVendor = vendor;
    }

    /**
     * Return the vendor of the JDK implementation.
     *
     * @hibernate.property column="jdk_vendor" type="string" length="127" not-null="true"
     *
     * @return  JDK vendor name 
     */
    public String getJdkVendor() {
        return jdkVendor;
    }

    /**
     * Set the operating system name. 
     *
     * @param  name   Operating system name 
     */
    public void setOSName(String name) {
        osName = name;
    }

    /**
     * Return the operating system name. 
     *
     * @hibernate.property column="os_name" type="string" length="127" not-null="true"
     *
     * @return  Operating system name 
     */
    public String getOSName() {
        return osName;
    }


    /**
     * Set the operating system CPU architecture.
     *
     * @param  arch   Chip architecture 
     */
    public void setOSArchitecture(String arch) {
        osArch = arch;
    }

    /**
     * Return the operating system CPU architecture.
     *
     * @hibernate.property column="os_arch" type="string" length="127" not-null="true"
     *
     * @return  Operating system architecture
     */
    public String getOSArchitecture() {
        return osArch;
    }

    /**
     * Set the operating system version.
     *
     * @param  version   Operating system version
     */
    public void setOSVersion(String version) {
        osVersion = version;
    }

    /**
     * Return the operating system version.
     *
     * @hibernate.property column="os_version" type="string" length="127" not-null="true"
     *
     * @return  Operating system version
     */
    public String getOSVersion() {
        return osVersion;
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

    /**
     * Add a log file to the list of logs associated with the build.
     *
     * @param   log   Log entry
     */
    public void addLog(CMnLogData log) {
        logs.add(log);
    }

    /**
     * Set the list of log files associated with the build.
     *
     * @param  list   List of logs
     */
    public void setLogs(Set list) {
        logs = list;
    }

    /**
     * Return the list of logs associated with the build.
     * 
     * @hibernate.set table="build_log" cascade="all" lazy="true"
     * @hibernate.collection-key column="build_id"
     * @hibernate.collection-one-to-many class="com.modeln.build.common.data.product.CMnLogData"
     *
     * @return List of build logs
     */
    public Set getLogs() {
        return logs;
    }

}
