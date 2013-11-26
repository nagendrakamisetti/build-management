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
public class CMnBuildStatusForm extends CMnBaseForm implements IMnBuildForm {

    /** Default title used when displaying a title and border */
    private static final String DEFAULT_TITLE = "Release Information";

    /** Build status object */
    public static final String STATUS_OBJECT_LABEL = "buildStatusObject";

    /** Build status (tested, released, etc) */
    public static final String STATUS_LABEL = "buildStatus";

    /** Build status comments */
    public static final String STATUS_COMMENT_LABEL = "buildStatusComment";


    /** Designates that the build passed all of the build criteria. */
    public static final int PASSING_STATUS = 0;

    /** Designates that the build was verified to work in the verification environment */
    public static final int VERIFIED_STATUS = 1;

    /** Designates that the build was designated as a stable development build. */
    public static final int STABLE_STATUS = 2;

    /** Designates that the build was tested by a QA team. */
    public static final int TESTED_STATUS = 3;

    /** Designates that the build was approved for release */
    public static final int RELEASED_STATUS = 4;


    /** Note status keys */
    public static final String[] statusKeys = { "passing", "verified", "stable", "tested", "released" };

    /** Note status display values */
    public static final String[] statusValues = {
        "Passing Automated Tests",
        "Deployed to Product Certification Environment",
        "Designated Stable Build",
        "Deployed to QA Environment",
        "Released for Customer Use"
    };

    /** List of icons associated with each status value */
    public static final String[] statusIcons = {
        "icons_small/cd.gif",
        "icons_small/zap.png",
        "icons_small/lock.png",
        "icons_small/bug_ok.png",
        "icons_small/cd_ok.png"
    };


    /** Build changelist field */
    private TextTag buildIdTag = new TextTag(BUILD_ID_LABEL);

    /** Build status field */
    private OptionTag statusTag = new OptionTag(STATUS_LABEL);

    /** Status comment field */
    private TextTag statusCommentTag = new TextTag(STATUS_COMMENT_LABEL);

    /** URL for the status notes command */
    private URL notesUrl;

    /** Build data object that has been reconstructed from the form input. */
    private CMnDbBuildStatusData statusData = null;



    /**
     * Construct a status form.
     *
     * @param  form   URL to use when submitting form input
     * @param  images URL to use when referencing images
     */
    public CMnBuildStatusForm(URL form, URL images) {
        super(form, images);
        setInputMode(false);

        statusCommentTag.setWidth(40);

        // Set the list of status options
        Hashtable options = new Hashtable(statusKeys.length);
        for (int idx = 0; idx < statusKeys.length; idx++) {
            options.put(statusKeys[idx], statusValues[idx]);
        }
        statusTag.setOptions(options);
        statusTag.setKeyOrder(statusKeys);
        statusTag.setMultiple(true);

    }


    /**
     * Set the build data that should be rendered in the form.
     *
     * @param  data   Build data
     */
    public void setValues(CMnDbBuildStatusData data) {
        statusData = data;
        if (data != null) {
            buildIdTag.setValue(Integer.toString(data.getBuildId()));
            statusCommentTag.setValue(data.getComments());

            // Determine which status items have been set
            statusTag.removeAllSelections();
            if (data.getBuildStatus(CMnDbBuildStatusData.PASSING_STATUS)) {
                statusTag.addSelected(statusKeys[PASSING_STATUS]);
            } 
            if (data.getBuildStatus(CMnDbBuildStatusData.VERIFIED_STATUS)) {
                statusTag.addSelected(statusKeys[VERIFIED_STATUS]);
            }
            if (data.getBuildStatus(CMnDbBuildStatusData.STABLE_STATUS)) {
                statusTag.addSelected(statusKeys[STABLE_STATUS]);
            }
            if (data.getBuildStatus(CMnDbBuildStatusData.TESTED_STATUS)) {
                statusTag.addSelected(statusKeys[TESTED_STATUS]);
            }
            if (data.getBuildStatus(CMnDbBuildStatusData.RELEASED_STATUS)) {
                statusTag.addSelected(statusKeys[RELEASED_STATUS]);
            }
        } else {
            // Clear all of the field values
            buildIdTag.setValue("");
            statusCommentTag.setValue("");
            statusTag.removeAllSelections();
        }
    }

    /** 
     * Set the build status.
     *
     * @param   data   Status object
     * @param   status Status to be set
     */
    public static void setStatus(CMnDbBuildStatusData data, String status) {
        for (int idx = 0; idx < statusKeys.length; idx++) {
            if (statusKeys[idx].equalsIgnoreCase(status)) {
                data.setBuildStatus(idx, true);
            }
        } 
    } 

    /**
     * Set the input fields by examining the HTTP request to see if
     * a value was submitted. 
     *
     * @param   req     HTTP request
     */
    public void setValues(HttpServletRequest req) {
        CMnDbBuildStatusData data = (CMnDbBuildStatusData) req.getAttribute(STATUS_OBJECT_LABEL);
        if (data == null) {
            String buildId = req.getParameter(BUILD_ID_LABEL);
            String[] statusList = req.getParameterValues(STATUS_LABEL);
            String statusComment = req.getParameter(STATUS_COMMENT_LABEL);
            if (buildId != null) {
                data = new CMnDbBuildStatusData(Integer.parseInt(buildId));
                data.setComments(statusComment);
                if (statusList != null) {
                    for (int idx = 0; idx < statusList.length; idx++) {
                        setStatus(data, statusList[idx]);
                    }
                }
            }
        }
        setValues(data);
    }


    /**
     * Obtain the build data from the fields.  The values must first be populated by
     * calling the setValues method.  A null object may be returned if no data was
     * found in the request.
     *
     * @return   Build data found in the request
     */
    public CMnDbBuildStatusData getValues() {
        return statusData;
    }
 
    /**
     * Sets the URL to be used when linking to status notes.  This allows
     * the status notes to be hyperlinked.
     *
     * @param  url   Link to the status notes command 
     */
    public void setNotesUrl(URL url) {
        notesUrl = url;
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

        // Comments
        html.append("  <tr>\n");
        html.append("    <td width=\"30%\" valign=\"top\"><b>Comments:</b></td>\n");
        html.append("    <td width=\"70%\">");
        if (inputEnabled) {
            html.append(statusCommentTag.toString());
        } else {
            if (statusData != null) {
                html.append(statusData.getComments());
            }
        }
        html.append("</td>\n");
        html.append("  </tr>\n");


        // Build release status
        if (notesUrl != null) {
            statusTag.setDefaultLink(notesUrl.toString() + "?" + BUILD_ID_LABEL + "=" + statusData.getBuildId() + "&");
        } else {
            statusTag.setDefaultLink(null);
        }
        html.append("  <tr>\n");
        html.append("    <td width=\"30%\" valign=\"top\"><b>Release Status:</b></td>\n");
        html.append("    <td width=\"70%\">");
        if (inputEnabled) {
            html.append(statusTag.toString());
        } else {
            String[] selected = statusTag.getSelected();
            if (selected.length > 1) html.append("<ul COMPACT>\n");
            for (int idx = 0; idx < selected.length; idx++) {
                if (selected.length > 1) html.append("<li>");
                if (statusTag.getDefaultLink() != null) {
                    html.append("<a href=\"" + statusTag.getDefaultLink() +  
                        STATUS_LABEL + "=" + selected[idx] + "\">");
                    html.append(buildStatusToText(selected[idx]));
                    html.append("</a>");
                } else {
                    html.append(buildStatusToText(selected[idx]));
                }
                if (selected.length > 1) html.append("</li>\n");
            }
            if (selected.length > 1) html.append("</ul>\n");
        }
        html.append("</td>\n");
        html.append("  </tr>\n");


        html.append("</table>\n");

        return html.toString();
    }

    /**
     * Render the build status as a string.  The status should be one of the
     * status key values:  passing, verified, stable, tested, released
     *
     * @param   status     Build status value
     * @return  Full status text
     */
    public static String buildStatusToText(String status) {
        String text = null;
        for (int idx = 0; idx < statusKeys.length; idx++) {
            if (statusKeys[idx].equalsIgnoreCase(status)) {
                text = statusValues[idx];
            }
        }

        return text;
    }


    /**
     * Render the build status as an icon.  The status should be one of the 
     * status key values:  passing, verified, stable, tested, released
     *
     * @param   baseUrl    Base image URL
     * @param   status     Build status value
     */
    public static String buildStatusToIcon(String baseUrl, String status) {
        String html = null;
        for (int idx = 0; idx < statusKeys.length; idx++) { 
            if (statusKeys[idx].equalsIgnoreCase(status)) {
                html = buildStatusToIcon(baseUrl, idx); 
            }
        } 

        return html;
    }
 

    /** 
     * Render the build status as an icon.  The status should be one of the 
     * defined status values: 
     *   PASSING_STATUS, VERIFIED_STATUS, STABLE_STATUS, TESTED_STATUS, RELEASED_STATUS
     * 
     * @param   baseUrl    Base image URL
     * @param   status     Build status value
     */
    public static String buildStatusToIcon(String baseUrl, int status) {
        return "<img src=\"" + baseUrl + "/" + statusIcons[status] + "\" alt=\"" + statusValues[status] + "\" border=0>"; 
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
