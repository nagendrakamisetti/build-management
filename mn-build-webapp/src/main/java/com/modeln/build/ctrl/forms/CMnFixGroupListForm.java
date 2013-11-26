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

import com.modeln.build.common.data.account.CMnAccount;
import com.modeln.build.common.data.account.CMnEnvironment;
import com.modeln.build.common.data.product.CMnBaseFix;
import com.modeln.build.common.data.product.CMnPatch;
import com.modeln.build.common.data.product.CMnPatchGroup;
import com.modeln.build.common.enums.CMnServicePatch;
import com.modeln.build.ctrl.database.CMnPatchTable;
import com.modeln.testfw.reporting.CMnBuildTable;
import com.modeln.testfw.reporting.CMnDbBuildData;
import com.modeln.testfw.reporting.search.CMnSearchCriteria;
import com.modeln.testfw.reporting.search.CMnSearchGroup;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;
import java.util.Hashtable;
import javax.servlet.http.HttpServletRequest;

import com.modeln.build.common.data.account.UserData;
import com.modeln.build.web.tags.TextTag;
import com.modeln.build.web.tags.DateTag;
import com.modeln.build.web.tags.SelectTag;
import com.modeln.build.web.tags.TagGroup;


/**
 * The patch list form provides an HTML interface to the patch requset data object.
 * The class manages transfering the data through the HTTP request.
 * Form data can be rendered in either a read-only or input mode.
 * 
 * @author  Shawn Stafford
 */
public class CMnFixGroupListForm extends CMnBaseTableForm implements IMnPatchForm {

    /** Fix group status field */
    private SelectTag statusTag = new SelectTag(FIX_GROUP_STATUS_LABEL);


    /** Bug ID field */
    private TextTag bugTag = new TextTag(FIX_GROUP_BUGS_LABEL);

    /** Fix group name field */
    private TextTag nameTag = new TextTag(FIX_GROUP_NAME_LABEL);

    /** Build version field */
    private TextTag buildVersionTag = new TextTag(BUILD_VERSION_LABEL);


    /** List of fix group data objects. */
    private Vector<CMnPatchGroup> groupList = null;




    /** URL for deleting a table entry */
    private URL deleteUrl = null;

    /** URL for editing group information */
    private URL groupUrl = null;

    /** URL for viewing bug information */
    private URL bugUrl = null;


    /**
     * Construct a patch request data list.  When input is enabled, the form
     * will be rendered with input fields.
     *
     * @param    patches          List of patches
     */
    public CMnFixGroupListForm(URL form, URL images, Vector<CMnPatchGroup> groups) {
        super(form, images);
        groupList = groups;
        buildVersionTag.setWidth(30);
        nameTag.setWidth(30);
        bugTag.setWidth(10);

        // Construct the status list
        Hashtable statusList = new Hashtable();
        statusList.put("", "...All...");
        for (CMnServicePatch.FixRequirement status : CMnServicePatch.FixRequirement.values()) {
            statusList.put(status.name(), status.name());
        }
        statusTag.setOptions(statusList);
        statusTag.setDefault("");
        //statusTag.setSorting(true);
        statusTag.sortByValue();

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
     * Set the URL used to link to bug information.
     *
     * @param  url   Link to the bug information 
     */
    public void setBugUrl(URL url) {
        bugUrl = url;
    }

    /**
     * Set the URL used to view/edit group information
     *
     * @param  url   Link to the command related to group information
     */
    public void setGroupUrl(URL url) {
        groupUrl = url;
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
        nameTag.setDisabled(!enabled);
        bugTag.setDisabled(!enabled);
        statusTag.setDisabled(!enabled);
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
        nameTag.setValue(req);
        bugTag.setValue(req);
        statusTag.setValue(req);
    }


    /**
     * Return the bugs the user has entered in the search field 
     *
     * @return Bug ID or null if none specified 
     */
    public String getBug() {
        if (bugTag.isComplete()) {
            return bugTag.getValue(); 
        } else {
            return null; 
        }
    }


    /**
     * Return a search group that describes all of the selected form input.
     *
     * @return Form values as a database search group
     */
    public CMnSearchGroup getValues() {
        CMnSearchGroup group = new CMnSearchGroup(CMnSearchGroup.AND);

        // Construct the search criteria for the status
        if (statusTag.isComplete()) {
            String[] selectedStatus = statusTag.getSelected();
            if ((selectedStatus[0] != null) && (selectedStatus[0].length() > 0)) {
                CMnSearchCriteria statusCriteria = new CMnSearchCriteria(
                    CMnPatchTable.FIX_GROUP_TABLE,
                    CMnPatchTable.FIX_GROUP_STATUS,
                    CMnSearchCriteria.EQUAL_TO,
                    selectedStatus[0] 
                );
                group.add(statusCriteria);
            }
        }

        // Construct the search criteria for the build version
        if (buildVersionTag.isComplete()) {
            CMnSearchCriteria versionCriteria = new CMnSearchCriteria(
                CMnPatchTable.FIX_GROUP_TABLE,
                CMnPatchTable.BUILD_VERSION,
                CMnSearchCriteria.LIKE,
                buildVersionTag.getValue()
            );
            group.add(versionCriteria);
        }

        // Construct the search criteria for the group name
        if (nameTag.isComplete()) {
            CMnSearchCriteria nameCriteria = new CMnSearchCriteria(
                CMnPatchTable.FIX_GROUP_TABLE,
                CMnPatchTable.FIX_GROUP_NAME,
                CMnSearchCriteria.LIKE,
                nameTag.getValue()
            );
            group.add(nameCriteria);
        }

        return group;
    }



    /**
     * Render the build data form.
     */
    public String toString() {
        StringBuffer html = new StringBuffer();

        html.append("<form action=" + getFormUrl() + ">\n");
        html.append("<table class=\"spreadsheet\" width=\"100%\">\n");
        html.append(getHeader());
        if (inputEnabled) {
            html.append(getInputFields());
        }
        if (groupList != null) {
            html.append(getGroupList());
        } else {
            html.append("<tr><td align=\"left\" colspan=\"6\">No fix groups</td></tr>\n");
        }
        html.append(getFooter());
        html.append("</table>\n");
        html.append("</form>\n");

        return html.toString();
    }


    /**
     * Construct a table header.
     */
    private String getHeader() {
        StringBuffer html = new StringBuffer();
        html.append("<tr class=\"spreadsheet-header\">\n");
        html.append("  <td nowrap width=\"2%\"  bgcolor=\"#CCCCCC\">&nbsp;</td>\n");
        html.append("  <td nowrap width=\"3%\"  bgcolor=\"#CCCCCC\">&nbsp;</td>\n");
        html.append("  <td nowrap width=\"15%\" bgcolor=\"#CCCCCC\">Status</td>\n");
        html.append("  <td nowrap width=\"20%\" bgcolor=\"#CCCCCC\">Group Name</td>\n");
        html.append("  <td nowrap width=\"20%\" bgcolor=\"#CCCCCC\">Product Version</td>\n");
        html.append("  <td nowrap width=\"40%\"  bgcolor=\"#CCCCCC\">Bug List</td>\n");
        html.append("</tr>\n");

        return html.toString();
    }


    /**
     * Construct a table footer.
     */
    private String getFooter() {
        StringBuffer html = new StringBuffer();
        html.append("<tr class=\"tablefooter\">\n");
        html.append("  <td nowrap colspan=\"6\" bgcolor=\"#CCCCCC\">");
        if (adminEnabled) {
            html.append("<a href=\"" + groupUrl + "\">New Group</a>");
        } else {
            html.append("&nbsp;");
        }
        html.append("</td>\n");
        html.append("</tr>\n");
        return html.toString();
    }


    /**
     * Construct a table header containing input fields.
     */
    private String getInputFields() {
        StringBuffer html = new StringBuffer();

        html.append("<tr class=\"spreadsheet-subheader\">\n");
        html.append("  <td align=\"center\"><input type=\"submit\" value=\"Go\"></a></td>\n");
        html.append("  <td nowrap>" + resultSizeTag.toString() + "</td>\n");
        html.append("  <td nowrap>" + statusTag.toString() + "</td>\n");
        html.append("  <td nowrap><!-- Group Name --></td>\n");
        html.append("  <td nowrap>" + buildVersionTag.toString() + "</td>\n");
        html.append("  <td nowrap>" + bugTag.toString() + "</td>\n");
        html.append("</tr>\n");

        return html.toString();
    }


    /**
     * Generate the table rows containing the list of groups.
     */
    private String getGroupList() {
        StringBuffer html = new StringBuffer();

        if (groupList != null) {
            CMnPatchGroup currentGroup = null;
            for (int idx = 0; idx < groupList.size(); idx++) {
                currentGroup = (CMnPatchGroup) groupList.get(idx);
                html.append("<tr class=\"spreadsheet-shaded\">\n");

                // Display the edit/delete controls for administrators
                html.append("  <td align=\"center\">\n");
                if (adminEnabled) {
                    String deleteHref = deleteUrl + "?" + GROUP_ID_LABEL + "=" + currentGroup.getId();
                    html.append("    <a href=\"" + deleteHref + "\"><img border=\"0\" src=\"" + getImageUrl() + "/icons_small/trashcan_red.png\" alt=\"Delete\"></a>\n");
                } else { 
                    html.append("    &nbsp;\n");
                } 
                html.append("  </td>\n");
                html.append("  <td align=\"right\">" + (idx + 1) + "</td>\n");

                // Display the group status (optional/recommended/required)
                html.append("  <td class=\"" + currentGroup.getStatus() + "\">" + currentGroup.getStatus() + "</td>\n");

                // Display the patch name
                String groupHref = groupUrl + "?" + IMnPatchForm.GROUP_ID_LABEL + "=" + currentGroup.getId();
                html.append("  <td><a href=\"" + groupHref + "\">" + currentGroup.getName() + "</a></td>\n");

                // Display the build version number 
                html.append("  <td NOWRAP>" + currentGroup.getBuildVersion() + "</td>\n");

                // Display the list of bugs
                StringBuffer strFixes = new StringBuffer();
                html.append("  <td>\n");
                if (currentGroup.getFixes() != null) {
                    Enumeration fixList = currentGroup.getFixes().elements();
                    while (fixList.hasMoreElements()) {
                        CMnBaseFix fix = (CMnBaseFix) fixList.nextElement();
                        if (strFixes.length() > 0) strFixes.append(", ");
                        strFixes.append("<a target=\"_blank\" href=\"" + getBugUrl(Integer.toString(fix.getBugId())) + "\">" + fix.getBugId() + "</a>");
                        if (fix.getBugName() != null) {
                            html.append(" (" + fix.getBugName() + ")");
                        }
                    }
                }
                html.append(strFixes.toString());
                html.append("  </td>\n");

                html.append("</tr>\n");
            }
        } else {
            html.append("<tr><td colspan=\"6\">No groups found.</td></tr>");
        }

        return html.toString();
    }



}
