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
 * The release list form is used to display the list of builds that
 * have been released to customers or are being considered for release. 
 *
 * @author  Shawn Stafford
 */
public class CMnReleaseListForm extends CMnBaseReleaseForm {

    /** Default text of the titled border */
    public static final String DEFAULT_TITLE = "Release List";


    /** List of unit test suites to be displayed */
    private Vector releaseList;

    /**  URL for accessing build status information */
    private URL statusUrl = null;

    /**  URL for accessing build summary information */
    private URL summaryUrl = null;

    /**
     * Construct the form from a list of releases. 
     *
     * @param  form      URL to use when submitting form input
     * @param  images    URL to use when referencing images
     * @param  list      Entire list of releases to be displayed
     */
    public CMnReleaseListForm(URL form, URL images, Vector list) {
        super(form, images);
        releaseList = list;
    }

    /**
     * Set the URL used to retrieve build status notes.
     *
     * @param  url   Link to the command related to build status notes
     */
    public void setStatusUrl(URL url) {
        statusUrl = url;
    }

    /**
     * Set the URL used to retrieve build summary information.
     *
     * @param  url   Link to the command related to build summary
     */
    public void setSummaryUrl(URL url) {
        summaryUrl = url;
    }


    /**
     * Render the build data form.
     */
    public String toString() {
        StringBuffer html = new StringBuffer();

        html.append("<table border=\"0\" cellspacing=\"2\" cellpadding=\"2\" width=\"100%\">\n"); 

        CMnDbReleaseSummaryData release = null;
        boolean checkDoubleBorder = false;
        for (int idx = 0; idx < releaseList.size(); idx++) {
            release = (CMnDbReleaseSummaryData) releaseList.get(idx);
            Vector buildList = release.getBuilds();

            // Display the release name and some general comments
            html.append("<tr>\n");
            html.append("  <td valign=\"top\" rowspan=\"2\">\n");
            String verString = StringUtility.stringToURL(release.getBuildVersion());
            html.append("    <a href=\"" + formUrl + "?buildVersion=" + verString + "\">" +
                             release.getName() + "</a>\n");
            html.append("  </td>\n");
            html.append("  <td valign=top>");
            html.append(release.getText());
            if (release.getReleaseStatus() == release.RETIRED_STATUS) {
                html.append(" (<a href=\"javascript:void\" onclick=\"toggleElement('" + release.getName() + "'); return false;\">view builds</a>)");
            }
            html.append("</td>\n");
            html.append("</tr>\n");

            html.append("<tr>\n");
            html.append("  <td valign=\"top\">\n");

            // Determine if the release should be displayed as active
            html.append("\n<!-- Starting group for release: " + release.getName() + " -->\n");
            html.append("      <div id=\"" + release.getName() + "\"");
            if (release.getReleaseStatus() == release.RETIRED_STATUS) {
                html.append(" style=\"display: none\"");
            }
            html.append(">\n");

            // Display the list of releases 
            String groupId = null;
            CMnDbBuildData build = null;
            for (int idxBuild = 0; idxBuild < buildList.size(); idxBuild++) {
                build = (CMnDbBuildData) buildList.get(idxBuild);
                groupId = build.getVersionControlId();

                // Determine if there will be additional builds in the group
                boolean moreInGroup = false;
                if ((groupId != null) && (idxBuild + 1 < buildList.size())) {
                    CMnDbBuildData nextBuild = (CMnDbBuildData) buildList.get(idxBuild + 1);
                    if ((groupId != null) && groupId.equals(nextBuild.getVersionControlId())) {
                        moreInGroup = true;
                    }
                }


                // Determine if the current build is in a different group that the previous 
                boolean continueGroup = false;
                if ((groupId != null) && (idxBuild - 1 >= 0)) {
                    CMnDbBuildData prevBuild = (CMnDbBuildData) buildList.get(idxBuild - 1);
                    if ((groupId != null) && groupId.equals(prevBuild.getVersionControlId())) {
                        continueGroup = true;
                    }
                }

                if (!continueGroup) {
                    // Try to obtain the numeric version number from the version string
                    String shortVersion = getShortVersion(build.getBuildVersion());
                    int idxVerNum = shortVersion.lastIndexOf(DEFAULT_SEPARATOR_CHAR);
                    String verNum = null;
                    if (idxVerNum > 0) {
                        verNum = shortVersion.substring(idxVerNum + 1);
                    } else {
                        verNum = shortVersion;
                    }

//                    html.append("\n<!-- Starting group for changelist #" + groupId + " -->\n");

                    // Create a link to the group of builds that correspond to the current changelist
//                    html.append("<input type=checkbox onclick=\"toggleElement('" + build.getBuildVersion() + "')\">");
                    html.append("<a href=\"" + summaryUrl + "?" + 
                        IMnBuildForm.BUILD_VERSION_LABEL + "=" + "%25" + verNum + "%25" + "&" +
                        IMnBuildForm.BUILD_CHANGELIST_OP_LABEL + "=" + CMnSearchCriteria.EQUAL_TO + "&" + 
                        IMnBuildForm.BUILD_CHANGELIST_LABEL + "=" + build.getVersionControlId() + "\">");
                    html.append(verNum + " " + build.getReleaseId());
                    html.append("</a><br/>\n");

                    // Provide a mechanism to toggle the list of visible builds in the group
/*
                    html.append("<div style=\"display: none\" id=\"" + build.getBuildVersion() + "\">\n");
                    html.append("<table cellspacing=\"0\" cellpadding=\"0\" width=\"100%\" border=\"0\"");
                    if (moreInGroup) {
                        html.append(" cellpadding=\"0\"");
                        html.append(" style=\"");
                        if (!checkDoubleBorder) {
                            html.append(" border-top:    1px solid #CCCCCC;");
                            checkDoubleBorder = true;
                        }
                        html.append(" border-bottom: 1px solid #CCCCCC;");
                        html.append(" border-left:   1px solid #CCCCCC;");
                        html.append(" border-right:  1px solid #CCCCCC;");
                        html.append("\"");
                    } else {
                        html.append(" cellpadding=\"1\"");
                        checkDoubleBorder = false;
                    }
                    html.append(">\n");
*/
                } 

/*
                html.append("<tr>\n");

                // Build version string
                html.append("<td width=\"30%\"><a href=\"" + formUrl + "?" + 
                    IMnBuildForm.BUILD_VERSION_LABEL + "=" + build.getBuildVersion() + "\">" + 
                    build.getBuildVersion() + "</a></td>\n");

                // Build status icons
                html.append("<td width=\"5%\">");
                if (build.getStatus().getBuildStatus(CMnDbBuildStatusData.TESTED_STATUS)) {
                    if (statusUrl != null)
                        html.append("<a href=\"" + getStatusUrl(build, CMnBuildStatusForm.TESTED_STATUS) + "\">");
                    html.append(CMnBuildStatusForm.buildStatusToIcon(getImageUrl(), CMnBuildStatusForm.TESTED_STATUS));
                    if (statusUrl != null)
                        html.append("</a>");
                } else {
                    html.append("&nbsp;");
                }
                html.append("</td>\n");
                html.append("<td width=\"5%\">");
                if (build.getStatus().getBuildStatus(CMnDbBuildStatusData.RELEASED_STATUS)) {
                    if (statusUrl != null)
                        html.append("<a href=\"" + getStatusUrl(build, CMnBuildStatusForm.RELEASED_STATUS) + "\">");
                    html.append(CMnBuildStatusForm.buildStatusToIcon(getImageUrl(), CMnBuildStatusForm.RELEASED_STATUS));
                    if (statusUrl != null)
                        html.append("</a>");
                } else {
                    html.append("&nbsp;");
                }
                html.append("</td>\n");

                // Build status comments
                html.append("<td width=\"60%\">");
                html.append(build.getStatus().getComments());
                html.append("</td>\n");

                html.append("</tr>\n");

                if (!moreInGroup) {
                    html.append("</table>\n");
                    html.append("</div>\n");
                    html.append("<!-- Ending group for changelist #" + groupId + " -->\n\n");
                }
*/
            }

            html.append("    </div>\n");
            html.append("    <!-- Ending group for release: " + release.getName() + " -->\n\n");

            html.append("  </td>\n");
            html.append("</tr>\n");

        }
        html.append("</table>\n");

        return html.toString();
    }

    /**
     * Return the status notes URL for a specific build.
     *
     * @param   build    Build to be queried on the URL
     * @param   status   Status to query
     *
     * @return  Link to the build status notes
     */
    private String getStatusUrl(CMnDbBuildData build, int status) {
        StringBuffer url = new StringBuffer();
        url.append(statusUrl + "?");
        url.append(IMnBuildForm.BUILD_ID_LABEL + "=" + build.getId());
        url.append("&");
        url.append(CMnBuildStatusForm.STATUS_LABEL + "=" + CMnBuildStatusForm.statusKeys[status]);
        return url.toString();
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

    /**
     * Iterate through the list of builds and determine the version string
     * which represents the builds associated with the specified changelist.
     *
     * @param   release      List of builds
     * @param   changelist   Changelist number
     * @return  Version string
     */
    public String getVersionByChangelist(CMnDbReleaseSummaryData release, String changelist) {
        String version = null;

        // Iterate through the list of builds examining the version strings
        Vector buildList = release.getBuilds();
        if ((changelist != null) && (buildList != null) && (buildList.size() > 0)) {
            Enumeration list = buildList.elements();
            while (list.hasMoreElements()) {
                CMnDbBuildData build = (CMnDbBuildData) list.nextElement();
                // Determine if the current build matches the required changelist
                if (changelist.equals(build.getVersionControlId())) {
                    // Try to find a common version format
                    if (version != null) {
                        version = getMergedVersion(version, build.getBuildVersion()); 
                    } else {
                        version = getShortVersion(build.getBuildVersion());
                    }
                }
            }
        }

        return version;
    }


}

