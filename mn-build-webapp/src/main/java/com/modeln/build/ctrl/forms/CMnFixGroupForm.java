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

import com.modeln.build.common.data.product.CMnBaseFix;
import com.modeln.build.common.data.product.CMnPatchGroup;
import com.modeln.build.common.enums.CMnServicePatch;

import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import javax.servlet.http.HttpServletRequest;

import com.modeln.build.web.tags.TextTag;
import com.modeln.build.web.tags.DateTag;
import com.modeln.build.web.tags.OptionTag;
import com.modeln.build.web.tags.SelectTag;
import com.modeln.build.web.tags.TagGroup;



/**
 * The fix group form provides an HTML interface to the patch
 * fix group data.  It is used to display the list of fix groups.  
 * The class manages transfering the data through the HTTP request.
 * Form data can be rendered in either a read-only or input mode.
 *
 * @author  Shawn Stafford
 */
public class CMnFixGroupForm extends CMnBaseForm implements IMnPatchForm {

    /** Default title used when displaying a title and border */
    private static final String DEFAULT_TITLE = "Patch Fix Group";


    /** Group ID field */
    private TextTag groupIdTag = new TextTag(GROUP_ID_LABEL);


    /** Patch request status field */
    private SelectTag statusTag = new SelectTag(FIX_GROUP_STATUS_LABEL);

    /** Build version field */
    private TextTag buildVersionTag = new TextTag(BUILD_VERSION_LABEL);

    /** Group name field */
    private TextTag groupNameTag = new TextTag(FIX_GROUP_NAME_LABEL);

    /** Group description field */
    private TextTag groupDescriptionTag = new TextTag(FIX_GROUP_DESC_LABEL);

    /** Field for submitting a new fix to the group */
    private TextTag newBugsTag = new TextTag(FIX_GROUP_BUGS_LABEL);


    /** The URL used for adding new fixes */
    protected URL addFixUrl = null;


    /** Save a copy of the data locally */
    private CMnPatchGroup fixGroup = null;


    /**
     * Construct a form for displaying groups of fixes for service patches.
     *
     * @param    form     URL for submitting form data
     * @param    images   URL for displaying HTML images
     * @param    addfix   URL for adding new fixes
     */
    public CMnFixGroupForm(URL form, URL images, URL addfix) {
        super(form, images);
        addFixUrl = addfix;

        buildVersionTag.setWidth(40);

        groupNameTag.setWidth(40);

        groupDescriptionTag.setHeight(5);
        groupDescriptionTag.setWidth(40);

        // Construct the status list
        Hashtable statusList = new Hashtable();
        for (CMnServicePatch.FixRequirement status : CMnServicePatch.FixRequirement.values()) {
            statusList.put(status.name(), status.name());
        }
        statusTag.setOptions(statusList);
        statusTag.setDefault(CMnServicePatch.FixRequirement.REQUIRED.toString());
        //statusTag.setSorting(true);
        statusTag.sortByValue();

        newBugsTag.setWidth(20);
    }


    /**
     * Enables or disables administrative functionality. 
     *
     * @param enabled  TRUE to enable administrative functionality
     */
    public void setAdminMode(boolean enabled) {
        super.setAdminMode(enabled);
        setInputMode(enabled);
    }


    /**
     * Extend the base method for enabling the input mode.  All input fields
     * must be enabled or disabled when this method is called. 
     *
     * @param enabled  TRUE to enable form input
     */
    public void setInputMode(boolean enabled) {
        super.setInputMode(enabled);
        groupIdTag.setDisabled(!enabled);
        buildVersionTag.setDisabled(!enabled);
        groupNameTag.setDisabled(!enabled);
        groupDescriptionTag.setDisabled(!enabled);
        statusTag.setDisabled(!enabled);
        newBugsTag.setDisabled(!enabled);
    }

    /**
     * Set the input fields by examining the HTTP request to see if
     * a value was submitted.
     *
     * @param   req     HTTP request
     */
    public void setValues(HttpServletRequest req) {
        super.setValues(req);

        // Check the request attribute for a data object with the default/previous values
        CMnPatchGroup group = (CMnPatchGroup) req.getAttribute(FIX_GROUP_DATA);
        setValues(group);

        //
        // Override the request attribute data with form input values
        //
        groupIdTag.setValue(req);
        buildVersionTag.setValue(req);
        groupNameTag.setValue(req);
        groupDescriptionTag.setValue(req);
        statusTag.setValue(req);
    }


    /**
     * Set the input fields using the data from the data object.
     *
     * @param   group   Group information
     */
    public void setValues(CMnPatchGroup group) {
        fixGroup = group;
        if (group != null) {
            groupIdTag.setValue(Integer.toString(group.getId()));
            buildVersionTag.setValue(group.getBuildVersion());
            groupNameTag.setValue(group.getName());
            groupDescriptionTag.setValue(group.getDescription());
            if (group.getStatus() != null) {
                statusTag.setSelected(group.getStatus().toString());
            }
        }
    }


    /**
     * Convert the form values into a data object.
     *
     * @return Data object containing the form values
     */
    public CMnPatchGroup getValues() {
        CMnPatchGroup group = new CMnPatchGroup();

        String id = groupIdTag.getValue();
        if (id != null) {
            try {
                group.setId(Integer.parseInt(id));
            } catch (NumberFormatException nfe) {
            }
        }

        String name = groupNameTag.getValue();
        if ((name != null) && (name.trim().length() > 0)) {
            group.setName(name.trim());
        }

        String version = buildVersionTag.getValue();
        if ((version != null) && (version.trim().length() > 0)) {
            group.setBuildVersion(version);
        }

        String description = groupDescriptionTag.getValue();
        if ((description != null) && (description.trim().length() > 0)) {
            group.setDescription(description);
        }

        String[] status = statusTag.getSelected();
        if ((status != null) && (status.length > 0) && (status[0] != null)) {
            group.setStatus(status[0]);
        }

        return group;
    }


    /**
     * Render the patch request form as HTML. 
     */
    public String toString() {
        StringBuffer html = new StringBuffer();

        // Determine the form submission method
        String method = "get";
        if (postMethodEnabled) {
            method = "post";
        }

        if (inputEnabled) {
            html.append("<form method=\"" + method + "\" action=\"" + getFormUrl() + "\">\n");
        }

        // Always render the group ID as a hidden field since a primary key should never change
        // But don't render the value if it is null
        String action = ACTION_ADD;
        if (groupIdTag.isComplete()) {
            groupIdTag.setHidden(true);
            html.append(groupIdTag.toString());
            action = ACTION_EDIT;
        }

        html.append("<table width=\"100%\" cellspacing=\"0\" cellpadding=\"1\">\n");

        html.append("  <tr>\n");
        html.append("    <td nowrap width=\"20%\" align=\"right\">Group Name:</td>\n");
        html.append("    <td nowrap width=\"80%\" align=\"left\">");
        html.append(groupNameTag.toString());
        // Display the submit button
        if (inputEnabled) {
            html.append("<input type=\"submit\" name=\"" + action + "\" value=\"" + action + "\"/>\n");
        }
        html.append("    </td>\n");
        html.append("  </tr>\n");

        html.append("  <tr>\n");
        html.append("    <td nowrap width=\"20%\" align=\"right\">Build Version:</td>\n");
        html.append("    <td nowrap width=\"80%\" align=\"left\">");
        html.append(buildVersionTag.toString());
        html.append("    </td>\n");
        html.append("  </tr>\n");

        html.append("  <tr>\n");
        html.append("    <td nowrap width=\"20%\" align=\"right\">Group Status:</td>\n");
        html.append("    <td nowrap width=\"80%\" align=\"left\">");
        html.append(statusTag.toString());
        html.append("    </td>\n");
        html.append("  </tr>\n");

        html.append("  <tr>\n");
        html.append("    <td nowrap width=\"20%\" align=\"right\" valign=\"top\">Group Description:</td>\n");
        html.append("    <td nowrap width=\"80%\" align=\"left\"  valign=\"top\">");
        html.append(groupDescriptionTag.toString());
        html.append("    </td>\n");
        html.append("  </tr>\n");

        html.append("</table>\n");

        if (inputEnabled) {
            html.append("</form>\n");
        }



        //
        // Display an additional table containing the list of SDRs
        //
        if (groupIdTag.isComplete()) {
            if (inputEnabled) {
                html.append("<form method=\"" + method + "\" action=\"" + addFixUrl + "\">\n");
                html.append(groupIdTag.toString());
            }
            String firstRow = "";
            String secondRow = "";
            html.append("<table width=\"100%\" cellspacing=\"0\" cellpadding=\"1\">\n");
            if (inputEnabled) {
                firstRow = "Bug List:";
                html.append("  <tr>\n");
                html.append("    <td nowrap width=\"20%\" align=\"right\">" + firstRow + "</td>\n");
                html.append("    <td nowrap width=\"80%\" align=\"left\">");
                html.append(newBugsTag.toString());
                html.append("<input type=\"submit\" name=\"" + ACTION_ADD + "\" value=\"" + ACTION_ADD + "\"/>\n");
                html.append("    </td>\n");
                html.append("  </tr>\n");
            } else {
                secondRow = "Bug List:";
            }

            if ((fixGroup != null) && (fixGroup.getFixes() != null)) {
                html.append("  <tr>\n");
                html.append("    <td nowrap width=\"20%\" align=\"right\">" + secondRow + "</td>\n");
                html.append("    <td nowrap width=\"80%\" align=\"left\">");
                Enumeration fixList = fixGroup.getFixes().elements();
                while (fixList.hasMoreElements()) {
                    CMnBaseFix fix = (CMnBaseFix) fixList.nextElement();
                    html.append("<a target=\"_blank\" href=\"" + getBugUrl(Integer.toString(fix.getBugId())) + "\">" + fix.getBugId() + "</a>");
                    if (fix.getBugName() != null) {
                        html.append(" (" + fix.getBugName() + ")");
                    }
                    if (fixList.hasMoreElements()) {
                        html.append(", ");
                    }
                }
                html.append("    </td>\n");
                html.append("  </tr>\n");
            }

            html.append("</table>\n");

            // Complete the input form
            if (inputEnabled) {
                html.append("</form>\n");
            }
        }


        return html.toString();
    }
}

