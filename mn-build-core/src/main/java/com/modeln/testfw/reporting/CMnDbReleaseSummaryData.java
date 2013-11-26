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


/**
 * This is a data class which provides the object relational mapping for an
 * entry in the database. 
 *
 * @author             Shawn Stafford
 */
public class CMnDbReleaseSummaryData {
    /** Designates that the release type is unknown. */
    public static final int UNKNOWN_RELEASE = 0;

    /** Designates a release that is built nightly. */
    public static final int NIGHTLY_RELEASE = 1;

    /** Designates a release that is built on an incremental basis throughout the day. */
    public static final int INCREMENTAL_RELEASE = 2;

    /** Designates a release that has been designated as stable but not yet released. */
    public static final int STABLE_RELEASE = 3;

    /** Designates a release that has been tested and designated for release. */
    public static final int FINAL_RELEASE = 4;


    /** Designates that the release is currently active */
    public static final int ACTIVE_STATUS = 0;

    /** Designates that the release is no longer available */
    public static final int RETIRED_STATUS = 1;


    /** Uniquely identifies a release summary. */
    private String summaryId;

    /** Display order for the release */
    private int releaseOrder;

    /** A descriptive, project, or code name for a release.  */
    private String releaseName;

    /** A text description or message associated with the release */
    private String releaseText;

    /** Designation for the summary table entry. */
    private int releaseType;

    /** Status of the release. */
    private int releaseStatus;

    /** Name of the build version associated with this release. */
    private String buildVersion;

    /** List of build objects associated with the release */
    private Vector buildList = new Vector();


    /**
     * Construct a summary object. 
     */
    public CMnDbReleaseSummaryData(String name) {
        releaseName = name;
    }

    /**
     * Set the ID of the summary.
     *
     * @param id  Summary ID
     */
    public void setId(String id) {
        summaryId = id;
    }

    /**
     * Return the ID of the summary.
     *
     * @return Summary ID
     */
    public String getId() {
        return summaryId;
    }

    /**
     * Set the name of the release being summarized. 
     *
     * @param name   Release name 
     */
    public void setName(String name) {
        releaseName = name;
    }

    /**
     * Return the name of the release being summarized. 
     *
     * @return Release name 
     */
    public String getName() {
        return releaseName;
    }

    /**
     * Set a text message associated with the release. 
     *
     * @param text   Release message 
     */
    public void setText(String text) {
        releaseText = text;
    }

    /**
     * Return the text message associated with the release. 
     *
     * @return Release message
     */
    public String getText() {
        return releaseText;
    }

    /**
     * Set the display order of the release. 
     *
     * @param order   Display order
     */
    public void setOrder(int order) {
        releaseOrder = order;
    }

    /**
     * Return the display order of the release. 
     *
     * @return Display order
     */
    public int getOrder() {
        return releaseOrder;
    }


    /**
     * Set the type of release being summarized. 
     *
     * @param type  Release type 
     */
    public void setReleaseType(int type) {
        releaseType = type;
    }

    /**
     * Return the type of release being summarized. 
     *
     * @return Release type 
     */
    public int getReleaseType() {
        return releaseType;
    }

    /**
     * Set the status of the release being summarized.
     *
     * @param status  Release status
     */
    public void setReleaseStatus(int status) {
        releaseStatus = status;
    }

    /**
     * Return the status of the release being summarized.
     *
     * @return Release status
     */
    public int getReleaseStatus() {
        return releaseStatus;
    }

    /**
     * Set the build version associated with the release.  The build version may
     * be a specific version number if associated with a released build, or simply
     * a substring of a recurring version number in the case of nightly or 
     * incremental builds. 
     *
     * @param version  Build version string 
     */
    public void setBuildVersion(String version) {
        buildVersion = version;
    }

    /**
     * Return the build version associated with the release.  The build version
     * may not indicate a unique version number if the summary refers to a 
     * recurring release. 
     *
     * @return Build version 
     */
    public String getBuildVersion() {
        return buildVersion;
    }

    /**
     * Add a build data object to the release summary.
     *
     * @param   build    Build status data
     */
    public void addBuild(CMnDbBuildData build) {
        buildList.add(build);
    }

    /**
     * Return the list of build objects associated with the release.
     *
     * @return  List of build data
     */
    public Vector getBuilds() {
        return buildList;
    }

    /** 
     * Set the list of builds associated with the release.
     *
     * @param  builds   List of builds
     */
    public void setBuilds(Vector builds) {
        buildList = builds;
    }

}

