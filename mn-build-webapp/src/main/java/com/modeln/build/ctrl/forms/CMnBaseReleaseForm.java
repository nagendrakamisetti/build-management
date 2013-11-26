/*
* Copyright 2000-2003 by Model N, Inc.  All Rights Reserved.
*
* This software is the confidential and proprietary information
* of Model N, Inc ("Confidential Information").  You shall not
* disclose such Confidential Information and shall use it only
* in accordance with the terms of the license agreement you
* entered into with Model N, Inc.
*/
package com.modeln.build.ctrl.forms;

import com.modeln.testfw.reporting.CMnDbBuildData;
import com.modeln.testfw.reporting.CMnDbBuildStatusData;
import com.modeln.testfw.reporting.CMnDbReleaseSummaryData;
import com.modeln.testfw.reporting.search.CMnSearchCriteria;

import java.net.URL;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Vector;
import javax.servlet.http.HttpServletRequest;

import com.modeln.build.util.StringUtility;
import com.modeln.build.web.tags.TextTag;


/**
 * The release list form provides some common functions for parsing
 * release data and displaying it to the user. 
 *
 * @author  Shawn Stafford
 */
public class CMnBaseReleaseForm extends CMnBaseForm {

    /** Default character used to separate version number strings */
    public static final char DEFAULT_SEPARATOR_CHAR = '-';

    /**
     * Construct a base form.
     *
     * @param  url    URL to use when submitting form input
     */
    public CMnBaseReleaseForm(URL form, URL images) {
        super(form, images);
    }


    /**
     * Obtain the shortened version of a version string by trimming off the
     * timestamp.
     *
     * @param  version   Version string
     * @return Shortened version string
     */
    public static String getShortVersion(String version) {
        String shortVersion = null;

        if (version != null) {
            int lastSeparator = version.lastIndexOf(DEFAULT_SEPARATOR_CHAR);
            if (lastSeparator > 0) {
                shortVersion = version.substring(0, lastSeparator);
            } else {
                shortVersion = version;
            }
        }

        return shortVersion;
    }


    /**
     * Obtain the version number from the full version string.
     *
     * @param  version   Version string
     * @return Version number
     */
    public static String getVersionNumber(String version) {
        String verNum = null;

        // Try to obtain the numeric version number from the version string
        if (version != null) {
            String shortVersion = getShortVersion(version);
            int idxVerNum = shortVersion.lastIndexOf(DEFAULT_SEPARATOR_CHAR);
            if (idxVerNum > 0) {
                verNum = shortVersion.substring(idxVerNum + 1);
            } else {
                verNum = shortVersion;
            }
        }

        return verNum;
    }


    /**
     * Attempt to merge two shortened version strings into a common version
     * string.
     *
     * @param  ver1   Version string
     * @param  ver2   Version string
     * @return Common version string
     */
    public static String getMergedVersion(String ver1, String ver2) {
        StringBuffer mergedVersion = new StringBuffer();

        String strVer1 = ver1;
        String strVer2 = ver2;
        int idxVer1 = strVer1.indexOf(DEFAULT_SEPARATOR_CHAR);
        int idxVer2 = strVer2.indexOf(DEFAULT_SEPARATOR_CHAR);
        while ((idxVer1 > 0) && (idxVer2 > 0)) {
            String part1 = strVer1.substring(0, idxVer1);
            String part2 = strVer2.substring(0, idxVer2);
            if (part1.equals(part2)) {
                if (mergedVersion.length() > 0) {
                    mergedVersion.append(DEFAULT_SEPARATOR_CHAR);
                }
                mergedVersion.append(part1);
            }
            strVer1 = strVer1.substring(idxVer1);
            strVer2 = strVer2.substring(idxVer2);
        }

        return mergedVersion.toString();
    }


}
