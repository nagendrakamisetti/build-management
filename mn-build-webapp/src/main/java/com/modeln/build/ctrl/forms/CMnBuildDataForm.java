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
import com.modeln.testfw.reporting.CMnDbHostData;
import com.modeln.testfw.reporting.CMnDbBuildStatusData;
import com.modeln.testfw.reporting.search.CMnSearchCriteria;

import java.net.URL;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import javax.servlet.http.HttpServletRequest;

import com.modeln.build.web.tags.TextTag;
import com.modeln.build.web.tags.DateTag;
import com.modeln.build.web.tags.OptionTag;
import com.modeln.build.web.tags.SelectTag;
import com.modeln.build.web.tags.TagGroup;



/**
 * The build form provides an HTML interface to the build data object.
 * The class manages transfering the data through the HTTP request.
 * Form data can be rendered in either a read-only or input mode.
 * 
 * @author  Shawn Stafford
 */
public class CMnBuildDataForm extends CMnBaseTestForm implements IMnBuildForm {

    /** Default title used when displaying a title and border */
    private static final String DEFAULT_TITLE = "Build Information";

    /** Build changelist field */
    private TextTag buildIdTag = new TextTag(BUILD_ID_LABEL);

    /** Build version field */
    private TextTag buildVersionTag = new TextTag(BUILD_VERSION_LABEL);

    /** Build changelist field */
    private TextTag buildChangelistTag = new TextTag("buildChangelist");

    /** Build depot field */
    private TextTag buildDepotTag = new TextTag("buildDepot");

    /** Build date field */
    private DateTag buildDateTag = new DateTag("buildDate");

    /** Build host information */
    private TextTag buildHostTag = new TextTag("buildHost");

    /** Build JVM vendor information */
    private TextTag buildJvmTag = new TextTag("buildJvm");

    /** Build host information */
    private TextTag buildOSTag = new TextTag("buildOS");

    /** Build archive location */
    private TextTag buildUriTag = new TextTag("buildUri");

    /** Jenkins job URL */
    private TextTag buildJobTag = new TextTag("jobUrl");

    /** Release status information */
    private TextTag releaseIdTag = new TextTag("releaseId");

    /** Build data object that has been reconstructed from the form input. */
    private CMnDbBuildData buildData = null;

    /** Enable or disable related links */
    private boolean showRelatedLinks = false;

    /** The URL used when querying by build version number. */
    private URL versionUrl = null;

    /** The URL used when displaying build summary results */
    private URL summaryUrl = null;

    /** The URL used when displaying build download links */
    private URL downloadUrl = null;


    /**
     * Construct a build form.
     *
     * @param  form   URL to use when submitting form input
     * @param  images URL to use when referencing images
     */
    public CMnBuildDataForm(URL form, URL images) {
        super(form, images);
        setInputMode(false);

        buildVersionTag.setWidth(40);
        //buildVersionTag.setDisabled(true);

        buildDateTag.setDisabled(true);

        //buildChangelistTag.setDisabled(true);
        //buildDepotTag.setDisabled(true);

        buildHostTag.setWidth(40);
        //buildHostTag.setDisabled(true);

        buildJvmTag.setWidth(40);
        //buildJvmTag.setDisabled(true);

        buildOSTag.setWidth(40);
        //buildOSTag.setDisabled(true);

        buildUriTag.setWidth(40);
        //buildUriTag.setDisabled(true);

        buildJobTag.setWidth(40);
        //buildJobTag.setDisabled(true);

        releaseIdTag.setWidth(20);
        //releaseIdTag.setDisabled(true);
    }

    /**
     * Sets the URL to be used when querying by build version number.  This allows
     * the build version to be hyperlinked.
     *
     * @param  url   Link to the build version command
     */ 
    public void setVersionUrl(URL url) {
        versionUrl = url;
    }

    /**
     * Returns the URL to be used when querying by build version number.  This allows
     * the build version to be hyperlinked.
     *
     * @return   Link to the build version command
     */
    public URL getVersionUrl() {
        return versionUrl;
    }


    /**
     * Sets the URL to be used when displaying build summary results.  This allows
     * the build changelist number to be hyperlinked so the user can navigate
     * to the build summary for all builds on this changelist number.
     *
     * @param  url   Link to the build summary command
     */
    public void setSummaryUrl(URL url) {
        summaryUrl = url;
    }

    /**
     * Returns the URL to be used when displaying build summary results.  This allows
     * the build changelist number to be hyperlinked so the user can navigate
     * to the build summary for all builds on this changelist number.
     *
     * @return   Link to the build summary command
     */
    public URL getSummaryUrl() {
        return summaryUrl;
    }


    /**
     * Sets the URL to be used when displaying build download links.  This allows
     * the build binary location to be hyperlinked so the user can navigate
     * to the build binaries. 
     *
     * @param  url   Link to the build file server 
     */
    public void setDownloadUrl(URL url) {
        downloadUrl = url;
    }

    /**
     * Returns the URL to be used when displaying build download links.  This allows
     * the build binary location to be hyperlinked so the user can navigate
     * to the build binaries. 
     *
     * @return   Link to the build file server 
     */
    public URL getDownloadUrl() {
        return downloadUrl;
    }

    /**
     * Enable or disable the display of related links.
     * 
     * @param  enable   TRUE to enable display, FALSE to hide
     */
    public void setRelatedLinks(boolean enable) {
        showRelatedLinks = enable;
    }

    /**
     * Parse the version string and return the prefix.  For example,
     * a version string MN-PHARMA-5.6-20110412.123108 would have a 
     * prefix of MN-PHARMA-5.6.
     *
     * @return The first part of the version string
     */
    public String getVersionName() {
        String ver = buildVersionTag.getValue();

        // Verify that the version number is not null and contains at least one dash
        if ((ver != null) && (ver.lastIndexOf('-') >= 0)) {
            // Trim off the timestamp located after the last dash 
            ver = ver.substring(0, ver.lastIndexOf('-'));
        }

        return ver;
    }



    /**
     * Parse the version string and return the version number.
     *
     * @return  Version number
     */
    public String getVersionNumber() {
        String ver = getVersionName(); 

        // Verify that the version number is not null and contains at least one dash
        if ((ver != null) && (ver.lastIndexOf('-') >= 0)) {
            // Trim off the leading release info up to the last dash
            int startIdx = ver.lastIndexOf('-') + 1;
            if ((ver != null) && (startIdx > 0)) {
                ver = ver.substring(startIdx);
            }
        }

        return ver;
    }

    /**
     * Parse the version string and return the product name. For example,
     * a version string MN-PHARMA-5.6-20110412.123108 would return a value
     * of PHARMA.
     *
     * @return  Product name 
     */
    public String getProductName() {
        String ver = getVersionName();

        // Verify that the version number is not null and contains at least one dash
        if ((ver != null) && (ver.indexOf('-') < ver.lastIndexOf('-'))) {
            // Grab the product name between the two dashes 
            int startIdx = ver.indexOf('-') + 1;
            int endIdx = ver.lastIndexOf('-');
            ver = ver.substring(startIdx, endIdx);
        }

        return ver;
    }

    /**
     * Parse the version string and return the product name. For example,
     * a version string MN-PHARMA-5.6-20110412.123108 would return a value
     * of PHARMA.
     *
     * @return  Product name
     */
    public String getProductAbreviation() {
        String name = getProductName();
        if (name != null) {
            if (name.equalsIgnoreCase("PHARMA")) {
                return "PH";
            } else if (name.equalsIgnoreCase("MEDDEV")) {
                return "MD";
            } else {
                return name;
            }
        } else {
            return name;
        }
    }


    /**
     * Return the full version string.
     *
     * @return  Full version string
     */
    public String getVersionString() {
        StringBuffer buffer = new StringBuffer();

        if (inputEnabled) {
            buffer.append(buildVersionTag.toString());
        } else {
            if ((versionUrl != null) && (buildData != null)) {
                buffer.append("<a href=\"" + versionUrl.toString() + "?" + BUILD_ID_LABEL + "=" + buildData.getId() + "\">");
                buffer.append(buildVersionTag.getValue());
                buffer.append("</a>");
            } else {
                buffer.append(buildVersionTag.getValue());
            }
        }

        return buffer.toString();
    }

    /**
     * Set the build data that should be rendered in the form.
     *
     * @param  data   Build data
     */
    public void setValues(CMnDbBuildData data) {
        buildData = data;
        if (data != null) {
            // Pull the field data from the object
            buildIdTag.setValue(Integer.toString(data.getId()));
            buildVersionTag.setValue(data.getBuildVersion());
            GregorianCalendar buildDate = new GregorianCalendar();
            buildDate.setTime(data.getStartTime());
            buildDateTag.setDate(buildDate);
            buildChangelistTag.setValue(data.getVersionControlId());
            buildDepotTag.setValue(data.getVersionControlRoot());
            buildUriTag.setValue(data.getDownloadUri());
            buildJobTag.setValue(data.getJobUrl());
            releaseIdTag.setValue(data.getReleaseId());

            CMnDbHostData host = data.getHostData();
            if (host != null) {
                buildHostTag.setValue(host.getUsername() + "@" + host.getHostname());
                buildJvmTag.setValue(host.getJdkVendor() + " " + host.getJdkVersion());
                buildOSTag.setValue(host.getOSName() + " " + host.getOSVersion() + " (" + host.getOSArchitecture() + ")");
            }
        } else {
            // Clear all of the field values
            buildIdTag.setValue("");
            buildVersionTag.setValue("");
            GregorianCalendar buildDate = new GregorianCalendar();
            buildDateTag.setDate(buildDate);
            buildChangelistTag.setValue("");
            buildDepotTag.setValue("");
            buildHostTag.setValue("");
            buildJvmTag.setValue("");
            buildOSTag.setValue("");
            buildUriTag.setValue("");
            buildJobTag.setValue("");
            releaseIdTag.setValue("");
        }
    }

    /**
     * Set the input fields by examining the HTTP request to see if
     * a value was submitted.
     *
     * @param   req     HTTP request
     */
    public void setValues(HttpServletRequest req) {
        super.setValues(req);
        CMnDbBuildData data = (CMnDbBuildData) req.getAttribute(BUILD_OBJECT_LABEL);
        setValues(data);
    }


    /**
     * Obtain the build data from the fields.  The values must first be populated by
     * calling the setValues method.  A null object may be returned if no data was
     * found in the request.
     *
     * @return   Build data found in the request
     */
    public CMnDbBuildData getValues() {
        return buildData;
    }
 

    /**
     * Render the build data form.
     */
    public String toString() {
        StringBuffer html = new StringBuffer();

        // Don't display the ID value to the user
        buildIdTag.setHidden(true);
        html.append(buildIdTag.toString());

        html.append("<table border=\"0\" cellspacing=\"0\" cellpadding=\"2\" width=\"100%\" bgcolor=\"#FFFFFF\">\n");

        // Build version string
        html.append("  <tr>\n");
        html.append("    <td width=\"30%\"><b>Build Version:</b></td>\n");
        html.append("    <td width=\"70%\">" + getVersionString() + "</td>\n");
        html.append("  </tr>\n");

        // Build date
        html.append("  <tr>\n");
        html.append("    <td><b>Build Date:</b></td>\n");
        html.append("    <td>");
        if (inputEnabled) {
            html.append(buildDateTag.toString());
        } else {
            html.append(fullDateFormat.format(buildDateTag.getDate()));
        }
        html.append("</td>\n");
        html.append("  </tr>\n");

        // Build source control depot
        html.append("  <tr>\n");
        html.append("    <td><b>Build Branch:</b></td>\n");
        html.append("    <td>");
        if (inputEnabled) {
            html.append(buildDepotTag.toString());
        } else {
            html.append(buildDepotTag.getValue());
        }
        html.append("@");
        if (inputEnabled) {
            html.append(buildChangelistTag.toString());
        } else {
            if (getSearchUrl() != null) {
                html.append("<a href=\"" + getSearchUrl() + "?" +
                    BUILD_CHANGELIST_OP_LABEL + "=" + CMnSearchCriteria.EQUAL_TO + "&" +
                    BUILD_CHANGELIST_LABEL + "=" + buildChangelistTag.getValue() + "\">");
                html.append(buildChangelistTag.getValue());
                html.append("</a>");
            } else {
                html.append(buildChangelistTag.getValue());
            }
        }
        html.append("</td>\n");
        html.append("  </tr>\n");

        // Host on which the build was performed
        html.append("  <tr>\n");
        html.append("    <td><b>Host:</b></td>\n");
        html.append("    <td>");
        if (inputEnabled) {
            html.append(buildHostTag.toString());
        } else {
            html.append(buildHostTag.getValue());
        }
        html.append("</td>\n");
        html.append("  </tr>\n");

        // JDK used to perform the build
        html.append("  <tr>\n");
        html.append("    <td><b>JDK:</b></td>\n");
        html.append("    <td>");
        if (inputEnabled) {
            html.append(buildJvmTag.toString());
        } else {
            html.append(buildJvmTag.getValue());
        }
        html.append("</td>\n");
        html.append("  </tr>\n");

        // Operating system on which the build was performed
        html.append("  <tr>\n");
        html.append("    <td><b>OS:</b></td>\n");
        html.append("    <td>");
        if (inputEnabled) {
            html.append(buildOSTag.toString());
        } else {
            html.append(buildOSTag.getValue());
        }
        html.append("</td>\n");
        html.append("  </tr>\n");

        // Build archive location
        html.append("  <tr>\n");
        html.append("    <td><b>Archive location:</b></td>\n");
        html.append("    <td>");
        if (inputEnabled) {
            html.append(buildUriTag.toString());
        } else {
            if (downloadUrl != null) {
                html.append("<a href=\"" + downloadUrl.toString() + buildUriTag.getValue() + "\">");
                html.append(buildUriTag.getValue());
                html.append("</a>"); 
            } else {
                html.append(buildUriTag.getValue());
            }
        }
        html.append("</td>\n");
        html.append("  </tr>\n");

        // Jenkins Job URL 
        if ((buildJobTag.getValue() != null) && (buildJobTag.getValue().length() > 0)) {
            html.append("  <tr>\n");
            html.append("    <td><b>Build Process:</b></td>\n");
            html.append("    <td>");
            if (inputEnabled) {
                html.append(buildJobTag.toString());
            } else {
                if ((buildJobTag.getValue() != null) && (buildJobTag.getValue().length() > 0)) {
                    html.append("<a href=\"" + buildJobTag.getValue() + "\">");
                    html.append(buildJobTag.getValue());
                    html.append("</a>");
                } else {
                    html.append(buildJobTag.getValue());
                }
            }
            html.append("</td>\n");
            html.append("  </tr>\n");
        }

        // Related links 
        if (showRelatedLinks) {
            html.append("  <tr>\n");
            html.append("    <td><b>Related information:</b></td>\n");
            html.append("    <td>");
            if (getSummaryUrl() != null) {
                html.append("<a href=\"" + getSummaryUrl() + "?" +
                    BUILD_VERSION_LABEL + "=" + "%25" + getVersionNumber() + "%25&" +
                    BUILD_CHANGELIST_OP_LABEL + "=" + CMnSearchCriteria.EQUAL_TO + "&" +
                    BUILD_CHANGELIST_LABEL + "=" + buildChangelistTag.getValue() + "\">");
                html.append("Certification Summary");
                html.append("</a>");
            }
            html.append("</td>\n");
            html.append("  </tr>\n");
        }

        html.append("</table>\n");

        return html.toString();
    }


    /**
     * Create a table which defines a title and border for the contents.
     *
     * @param   title   Text to be placed in the title bar
     * @param   content Content of the table
     */
    public String getTitledBorder(String content) {
        return getTitledBorder(DEFAULT_TITLE, content);
    }

}
