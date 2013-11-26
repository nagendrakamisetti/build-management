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

import com.modeln.testfw.reporting.CMnBuildTable;
import com.modeln.testfw.reporting.CMnDbBuildData;
import com.modeln.testfw.reporting.CMnDbBuildStatusData;
import com.modeln.testfw.reporting.CMnDbHostData;
import com.modeln.testfw.reporting.CMnDbTestSummaryData;
import com.modeln.testfw.reporting.search.CMnSearchCriteria;
import com.modeln.testfw.reporting.search.CMnSearchGroup;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;
import java.util.Hashtable;
import javax.servlet.http.HttpServletRequest;
import com.modeln.build.web.tags.TextTag;
import com.modeln.build.web.tags.DateTag;
import com.modeln.build.web.tags.SelectTag;
import com.modeln.build.web.tags.TagGroup;
import com.modeln.build.web.tags.ToggleTag;


/**
 * The build form provides an HTML interface to the build data object.
 * The class manages transfering the data through the HTTP request.
 * Form data can be rendered in either a read-only or input mode.
 * 
 * @author  Shawn Stafford
 */
public class CMnBuildListForm extends CMnBaseTableForm implements IMnBuildForm {

    /** Date format used when constructing SQL queries */
    protected static final SimpleDateFormat DATE = new SimpleDateFormat("yyyy-MM-dd");


    /** Build version field */
    private TextTag buildVersionTag = new TextTag("buildVersion");

    /** Build status field (GA, Agile, SP1, etc) */
    private TextTag releaseIdTag = new TextTag("releaseId");

    /** Build date field */
    private DateTag buildDateTag = new DateTag("buildDate");

    /** Build date query operator field */
    private SelectTag buildDateOp;

    /** Tag group to contain the date fields. */
    private TagGroup buildDateGroup = new TagGroup("buildDateGroup");

    /** Build changelist field. */
    private TextTag buildChangelistTag = new TextTag(BUILD_CHANGELIST_LABEL);

    /** Build changelist query operator field */
    private SelectTag buildChangelistOp;

    /** Non-query criteria for excluding results that do not contain unit tests */
    private ToggleTag requireTestsTag = new ToggleTag("reqTests");

    /** Non-query criteria for displaying charts to summarize the results */
    private ToggleTag showChartsTag = new ToggleTag("charts");



    /** List of build data objects. */
    private Vector buildList = null;


    /** URL for deleting a table entry */
    private URL deleteUrl = null;

    /**  URL for accessing build status information */
    private URL statusUrl = null;


    /** Define the list of test result groups (JUnit, Flex, ACT, etc) */
    private Hashtable testGroups = new Hashtable();


    /**
     * Construct a build data list.  When input is enabled, the form
     * will be rendered with input fields.
     *
     * @param    builds          List of builds
     */
    public CMnBuildListForm(URL form, URL images, Vector builds) {
        super(form, images);
        buildList = builds;
        buildVersionTag.setWidth(30);
        buildChangelistTag.setWidth(8);
        releaseIdTag.setWidth(3);

        // Construct a list of date comparison operators
        Hashtable dateOps = new Hashtable(5);
        dateOps.put(CMnSearchCriteria.GREATER_THAN, ">");
        dateOps.put(CMnSearchCriteria.LESS_THAN, "<");
        dateOps.put(CMnSearchCriteria.LIKE, "="); 
        buildDateOp = new SelectTag("build_date_op", dateOps);

        // Construct a list of changelist comparison oparators
        Hashtable changeOps = new Hashtable(4);
        changeOps.put(CMnSearchCriteria.GREATER_THAN, ">");
        changeOps.put(CMnSearchCriteria.LESS_THAN, "<");
        changeOps.put(CMnSearchCriteria.EQUAL_TO, "=");
        changeOps.put(CMnSearchCriteria.LIKE, "like");
        buildChangelistOp = new SelectTag(BUILD_CHANGELIST_OP_LABEL, changeOps);

        // Group the date tags
        buildDateGroup.add(buildDateOp);
        buildDateGroup.add(buildDateTag);
        buildDateGroup.setUserDisable(true);
        buildDateGroup.setSubmitOnChange(true);
        buildDateGroup.enableGroup(false);

        // Define the list of test result groups
        testGroups.put("JUNIT", "JUnit");
        testGroups.put("FLEX", "Flex");
        testGroups.put("UIT", "UIT");
        testGroups.put("ACT", "ACT");
    }

    /**
     * Set the URL used to delete build entries.
     *
     * @param  url   Link to the command related to build deletion
     */
    public void setDeleteUrl(URL url) {
        deleteUrl = url;
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
     * Extend the base method for enabling the input mode.  All input fields
     * must be enabled or disabled when this method is called. 
     *
     * @param enabled  TRUE to enable form input
     */
    public void setInputMode(boolean enabled) {
        super.setInputMode(enabled);
        buildVersionTag.setDisabled(!enabled);
        releaseIdTag.setDisabled(!enabled);
        buildDateGroup.setDisabled(!enabled);
        buildChangelistTag.setDisabled(!enabled);
        buildChangelistOp.setDisabled(!enabled);
        requireTestsTag.setDisabled(!enabled); 
        showChartsTag.setDisabled(!enabled);
        inputEnabled = enabled;
    }

    /**
     * Set the input fields by examining the HTTP request to see if
     * a value was submitted.
     *
     * @param   req     HTTP request
     */
    public void setValues(HttpServletRequest req) {
        super.setValues(req);
        buildVersionTag.setValue(req);
        releaseIdTag.setValue(req);
        buildDateGroup.setValue(req);
        buildChangelistTag.setValue(req);
        buildChangelistOp.setValue(req);
        requireTestsTag.setValue(req);
        showChartsTag.setValue(req);
    }

    /**
     * Returns true if the results should only include builds which had
     * unit tests.
     *
     * @return true if the build list should only contain builds which had tests
     */
    public boolean requireTests() {
        return requireTestsTag.getValue();
    }

    /**
     * Returns true if the resulting page should include visual charts.
     *
     * @return true if the page should display charts
     */
    public boolean showCharts() {
        return showChartsTag.getValue();
    }

    /**
     * Return a search group that describes all of the selected form input.
     *
     * @return Form values as a database search group
     */
    public CMnSearchGroup getValues() {
        CMnSearchGroup group = new CMnSearchGroup(CMnSearchGroup.AND);

        // Construct the search criteria for the build version
        if (buildVersionTag.isComplete()) {
            CMnSearchCriteria versionCriteria = new CMnSearchCriteria(
                CMnBuildTable.BUILD_TABLE,
                CMnBuildTable.BUILD_VERSION,
                CMnSearchCriteria.LIKE,
                buildVersionTag.getValue()
            );
            group.add(versionCriteria);
        }

        // Construct the search criteria for the build status
        if (releaseIdTag.isComplete()) {
            CMnSearchCriteria statusCriteria = new CMnSearchCriteria(
                CMnBuildTable.BUILD_TABLE,
                CMnBuildTable.BUILD_STATUS,
                CMnSearchCriteria.LIKE,
                releaseIdTag.getValue()
            );
            group.add(statusCriteria);
        }

        // Construct the search criteria for the build date
        if (buildDateGroup.isComplete()) {
            String[] dateOp = buildDateOp.getSelected();
            CMnSearchCriteria dateCriteria = new CMnSearchCriteria(
                CMnBuildTable.BUILD_TABLE,
                CMnBuildTable.BUILD_START_DATE,
                dateOp[0],
                DATE.format(buildDateTag.getDate())
            );
            group.add(dateCriteria);
        }

        // Construct the search criteria for the build changelist
        if (buildChangelistTag.isComplete()) {
            String[] changelistOp = buildChangelistOp.getSelected();
            CMnSearchCriteria changelistCriteria = new CMnSearchCriteria(
                CMnBuildTable.BUILD_TABLE,
                CMnBuildTable.BUILD_SOURCE_ID,
                changelistOp[0],
                buildChangelistTag.getValue()
            );
            group.add(changelistCriteria);
        }

        return group;
    }


    /**
     * Render the build data form.
     */
    public String toString() {
        StringBuffer html = new StringBuffer();

        try {
            html.append("<form action=" + getFormUrl() + ">\n");
            html.append("<table width=\"100%\" cellspacing=\"0\" cellpadding=\"1\">\n");
            html.append(getHeader());
            if (inputEnabled) {
                html.append(getInputFields());
            }
            html.append(getBuildList());
            html.append("</table>\n");
            html.append("</form>\n");
        } catch (Exception ex) {
           html.append(ex.getMessage());
        }

        return html.toString();
    }


    /**
     * Construct a table header.
     */
    private String getHeader() {
        StringBuffer html = new StringBuffer();
        html.append("<tr>\n");
        html.append("  <td nowrap colspan=\"9\"></td>\n");

        // Display the header for each test group
        if (testGroups != null) {
            Enumeration keys = testGroups.keys();
            while ((keys != null) && (keys.hasMoreElements())) {
                String groupName = (String) testGroups.get(keys.nextElement());
                html.append("  <td nowrap colspan=\"2\" align=\"center\" bgcolor=\"#CCCCCC\">");
                if (groupName != null) {
                    html.append("<b>" + groupName + "</b>");
                }
                html.append("</td>\n");
            }
        }

        html.append("</tr>\n");
        html.append("<tr>\n");
        html.append("  <td nowrap width=\"2%\"  bgcolor=\"#CCCCCC\">&nbsp;</td>\n");
        html.append("  <td nowrap width=\"4%\"  bgcolor=\"#CCCCCC\">&nbsp;</td>\n");
        html.append("  <td nowrap width=\"4%\"  bgcolor=\"#CCCCCC\" colspan=2>&nbsp;</td>\n");
        html.append("  <td nowrap width=\"5%\" bgcolor=\"#CCCCCC\">Build ID</td>\n");
        html.append("  <td nowrap width=\"20%\" bgcolor=\"#CCCCCC\">Build Version</td>\n");
        html.append("  <td nowrap width=\"5%\"  bgcolor=\"#CCCCCC\">Status</td>\n");
        //html.append("  <td nowrap width=\"30%\" bgcolor=\"#CCCCCC\">Date</td>\n");
        html.append("  <td nowrap width=\"30%\" bgcolor=\"#CCCCCC\">JDK</td>\n");
        html.append("  <td nowrap width=\"10%\" bgcolor=\"#CCCCCC\" align=\"right\">Changelist</td>\n");

        // Display the pass/fail sub-header for each test group
        if (testGroups != null) {
            Enumeration resultKeys = testGroups.keys();
            // Calculate the width of pass/fail columns as a percentage of the remaining 20% of the table
            int width = 10 / testGroups.size();
            while ((resultKeys != null) && (resultKeys.hasMoreElements())) {
                html.append("  <td nowrap width=\"" + width + "%\"  bgcolor=\"#CCCCCC\" align=\"center\">P</td>\n");
                html.append("  <td nowrap width=\"" + width + "%\"  bgcolor=\"#CCCCCC\" align=\"center\">F</td>\n");
                resultKeys.nextElement();
            }
        }

        html.append("</tr>\n");

        return html.toString();
    }


    /**
     * Construct a table header containing input fields.
     */
    private String getInputFields() {
        StringBuffer html = new StringBuffer();

        html.append("<tr>\n");
        html.append("  <td align=\"center\"><input type=\"submit\" value=\"Go\"></a></td>\n");
        html.append("  <td nowrap>" + resultSizeTag.toString() + "</td>\n");
        html.append("  <td nowrap colspan=2><!-- Status --></td>\n");
        html.append("  <td nowrap>" + showChartsTag.toString() + " Charts</td>\n");
        html.append("  <td nowrap>" + buildVersionTag.toString() + "</td>\n");
        html.append("  <td nowrap>" + releaseIdTag.toString() + "</td>\n");
        //html.append("  <td nowrap>" + buildDateGroup.toString() + "</td>\n");
        html.append("  <td nowrap><!-- JDK --></td>\n");
        html.append("  <td nowrap align=\"right\">" + buildChangelistOp.toString() + buildChangelistTag.toString() + "</td>\n");
        html.append("  <td nowrap colspan=8>" + requireTestsTag.toString() + " Require Tests</td>\n");
        html.append("</tr>\n");

        return html.toString();
    }


    /**
     * Generate the table rows containing the list of builds.
     */
    private String getBuildList() {
        StringBuffer html = new StringBuffer();

        CMnDbBuildData currentBuild = null;
        CMnDbHostData currentHost = null;
        String groupId = null;
        boolean checkDoubleBorder = false;
        for (int idx = 0; idx < buildList.size(); idx++) {
            currentBuild = (CMnDbBuildData) buildList.get(idx);
            currentHost = currentBuild.getHostData();
            groupId = currentBuild.getVersionControlId();

            // Determine if there will be additional builds in the group
            boolean moreInGroup = false;
            if ((groupId != null) && (idx + 1 < buildList.size())) {
                CMnDbBuildData nextBuild = (CMnDbBuildData) buildList.get(idx + 1);
                if ((groupId != null) && groupId.equals(nextBuild.getVersionControlId())) {
                    moreInGroup = true;
                }
            }

            // Determine if the current build is in a different group that the previous
            boolean continueGroup = false;
            if ((groupId != null) && (idx - 1 >= 0)) {
                CMnDbBuildData prevBuild = (CMnDbBuildData) buildList.get(idx - 1);
                if ((groupId != null) && groupId.equals(prevBuild.getVersionControlId())) {
                    continueGroup = true;
                }
            }

            // Construct the border style used for visual grouping of changelists 
            StringBuffer leftRowStyle = new StringBuffer();
            StringBuffer rightRowStyle = new StringBuffer();
            StringBuffer centerRowStyle = new StringBuffer();
            if (continueGroup || moreInGroup) {
                leftRowStyle.append(" border-left: 1px solid #CCCCCC;");
                rightRowStyle.append(" border-right: 1px solid #CCCCCC;");
                if (!continueGroup && !checkDoubleBorder) {
                    leftRowStyle.append(" border-top: 1px solid #CCCCCC;");
                    centerRowStyle.append(" border-top: 1px solid #CCCCCC;");
                    rightRowStyle.append(" border-top: 1px solid #CCCCCC;");
                    checkDoubleBorder = false;
                }
                if (!moreInGroup) {
                    leftRowStyle.append(" border-bottom: 1px solid #CCCCCC;");
                    centerRowStyle.append(" border-bottom: 1px solid #CCCCCC;");
                    rightRowStyle.append(" border-bottom: 1px solid #CCCCCC;");
                    checkDoubleBorder = true;
                }
            } else {
                checkDoubleBorder = false;
            }

            html.append("<tr>\n");

            // Display the edit/delete controls for administrators
            html.append("  <td align=\"center\" style=\"" + leftRowStyle + "\">\n");
            if (adminEnabled) {
                String deleteHref = deleteUrl + "?" + BUILD_ID_LABEL + "=" + currentBuild.getId();
                html.append("    <a href=\"" + deleteHref + "\"><img border=\"0\" src=\"" + getImageUrl() + "/icons_small/trashcan_red.png\" alt=\"Delete\"></a>\n");
            } else { 
                html.append("    &nbsp;\n");
            } 
            html.append("  </td>\n");
            html.append("  <td align=\"right\" style=\"" + centerRowStyle + "\">" + (idx + 1) + "</td>\n");

            // Display the status of each build
            String statusImg = null;
            if (currentBuild.getStatus() != null) {
                // Tested builds
                if (currentBuild.getStatus().getBuildStatus(CMnDbBuildStatusData.TESTED_STATUS)) {
                    statusImg = CMnBuildStatusForm.buildStatusToIcon(getImageUrl(), CMnBuildStatusForm.TESTED_STATUS);
                } else {
                    statusImg = "";
                }
                html.append("  <td align=\"center\" style=\"" + centerRowStyle + "\">");
                if (statusUrl != null) {
                    html.append("<a href=\"" + getStatusUrl(currentBuild, CMnBuildStatusForm.TESTED_STATUS) + "\">" + statusImg + "</a>");
                } else {
                    html.append(statusImg);
                }
                html.append("</td>\n");

                // Released builds
                if (currentBuild.getStatus().getBuildStatus(CMnDbBuildStatusData.RELEASED_STATUS)) {
                    statusImg = CMnBuildStatusForm.buildStatusToIcon(getImageUrl(), CMnBuildStatusForm.RELEASED_STATUS);
                } else {
                    statusImg = "";
                }
                html.append("  <td align=\"center\" style=\"" + centerRowStyle + "\">");
                if (statusUrl != null) {
                    html.append("<a href=\"" + getStatusUrl(currentBuild, CMnBuildStatusForm.RELEASED_STATUS) + "\">" + statusImg + "</a>");
                } else {
                    html.append(statusImg);
                }
                html.append("</td>\n");

            } else {
                html.append("  <td style=\"" + centerRowStyle + "\">&nbsp;</td><td style=\"" + centerRowStyle + "\">&nbsp;</td>\n");
            }

            // Display the build ID
            html.append("  <td style=\"" + centerRowStyle + "\">" + currentBuild.getId() + "</td>\n");

            // Display the build version number 
            String versionHref = getFormUrl() + "?" + buildVersionTag.getName() + "=" + currentBuild.getBuildVersion();
            boolean groupByArea = true;
            if (groupByArea) {
                versionHref = versionHref + "&" + FORM_GROUP_LABEL + "=area";
            }
            html.append("  <td style=\"" + centerRowStyle + "\"><tt><a href=\"" + versionHref + "\">" + currentBuild.getBuildVersion() + "</a></tt></td>\n");

            // Display the release ID 
            html.append("  <td style=\"" + centerRowStyle + "\">" + currentBuild.getReleaseId() + "</td>\n");

            // Display the build date
            //html.append("  <td style=\"" + centerRowStyle + "\"><tt>" + shortDateFormat.format(currentBuild.getStartTime()) + "</tt></td>\n");

            // Display the JDK
            html.append("  <td style=\"" + centerRowStyle + "\">" + currentHost.getJdkVendor() + " " + currentHost.getJdkVersion() + "</td>\n");

            // Display the Perforce changelist number of each build
            String changelistHref = getFormUrl() + "?" + 
                buildChangelistOp.getName() + "=" + CMnSearchCriteria.EQUAL_TO + "&" +
                buildChangelistTag.getName() + "=" + currentBuild.getVersionControlId();
            html.append("  <td align=\"right\" style=\"" + centerRowStyle + "\"><a href=\"" + changelistHref + "\">" + currentBuild.getVersionControlId() + "</a></td>\n");

            // Display the test results for each build
            Enumeration keys = testGroups.keys();
            while (keys.hasMoreElements()) {
                String group = (String) keys.nextElement();
                CMnDbTestSummaryData data = null;
                if (group != null) {
                    data = currentBuild.getTestSummary(group);
                }
                if (data != null) {
                    int failures = data.getFailingCount() + data.getErrorCount();
                    String failureBg = "#FFFFFF";
                    if (failures > 0) {
                        failureBg = "#FF3333";
                    }
                    html.append("  <td align=\"right\" style=\"" + centerRowStyle + "\">" + data.getPassingCount() + "</td>\n");
                    html.append("  <td align=\"right\" style=\"" + rightRowStyle + "\" bgcolor=\"" + failureBg + "\">" + failures + "</td>\n");
                } else {
                    html.append("  <td align=\"right\" style=\"" + centerRowStyle + "\">0</td>\n");
                    html.append("  <td align=\"right\" style=\"" + rightRowStyle + "\" bgcolor=\"#FFFFFF\">0</td>\n");
                }
            }

            html.append("</tr>\n");
        }

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
        url.append(BUILD_ID_LABEL + "=" + build.getId());
        url.append("&");
        url.append(CMnBuildStatusForm.STATUS_LABEL + "=" + CMnBuildStatusForm.statusKeys[status]);
        return url.toString();
    } 

}
