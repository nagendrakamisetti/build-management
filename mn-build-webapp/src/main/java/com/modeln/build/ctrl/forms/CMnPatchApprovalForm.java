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

import com.modeln.build.common.data.product.CMnPatch;
import com.modeln.build.common.data.product.CMnPatchApproval;
import com.modeln.build.common.data.product.CMnPatchApproverGroup;
import com.modeln.build.common.enums.CMnServicePatch;

import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import javax.servlet.http.HttpServletRequest;

import com.modeln.build.common.data.account.GroupData;
import com.modeln.build.common.data.account.UserData;
import com.modeln.build.web.tags.TextTag;
import com.modeln.build.web.tags.DateTag;
import com.modeln.build.web.tags.OptionTag;
import com.modeln.build.web.tags.SelectTag;
import com.modeln.build.web.tags.TagGroup;


/**
 * The build form provides an HTML interface to the service patch approval list. 
 * The class manages transfering the data through the HTTP request.
 * Form data can be rendered in either a read-only or input mode.
 *
 * @author  Shawn Stafford
 */
public class CMnPatchApprovalForm extends CMnBaseForm implements IMnPatchForm {

    /** Default title used when displaying a title and border */
    private static final String DEFAULT_TITLE = "Patch Approval";

    /** Form parameter value used to indicate the patch is approved by the user */
    public static final String PATCH_APPROVED_VALUE = "approved";

    /** Form parameter value used to indicate the patch is rejected by the user */
    public static final String PATCH_REJECTED_VALUE = "rejected";



    /** List of approvals submitted for the service patch */
    private Hashtable<CMnServicePatch.RequestStatus, Vector<CMnPatchApproval>> approvals;

    /** List of approvals required for the service patch */
    private Hashtable<CMnServicePatch.RequestStatus, Vector<CMnPatchApproverGroup>> approvers;

    /** Patch being approved */
    private CMnPatch patch;

    /** User who is viewing the information */
    private UserData user;


    /**
     * Construct a form for recording approvals for the service patch 
     *
     * @param    form     URL for submitting form data
     * @param    images   URL for displaying HTML images
     */
    public CMnPatchApprovalForm(URL form, URL images) {
        super(form, images);
        setInputMode(true);
    }

    /**
     * Set the patch being approved.
     *
     * @param   patch    Patch information
     */
    public void setPatch(CMnPatch patch) {
        this.patch = patch;
    }


    /**
     * Set the list of approvals. 
     *
     * @param  list    Approval entries 
     */
    public void setApprovals(Vector<CMnPatchApproval> list) {
        // Initialize the list of approvals
        approvals = new Hashtable<CMnServicePatch.RequestStatus, Vector<CMnPatchApproval>>();

        // Group the list by patch status
        CMnServicePatch.RequestStatus status = null;
        CMnPatchApproval approval = null;
        Enumeration e = list.elements();
        while (e.hasMoreElements()) {
            approval = (CMnPatchApproval) e.nextElement();
            if (approval != null) {
                status = approval.getPatchStatus();
            }

            // Add the list to the approvals
            Vector<CMnPatchApproval> existingList = (Vector<CMnPatchApproval>) approvals.get(status);
            if (existingList == null) {
                existingList = new Vector<CMnPatchApproval>();
            }
            existingList.add(approval);
            approvals.put(status, existingList);
        }
    }



    /**
     * Set the list of required approval groups.
     *
     * @param  list     Approval groups 
     */
    public void setApprovers(Vector<CMnPatchApproverGroup> list) {
        approvers = new Hashtable<CMnServicePatch.RequestStatus, Vector<CMnPatchApproverGroup>>();

        // Group the list by patch status
        CMnServicePatch.RequestStatus status = null;
        CMnPatchApproverGroup group = null;
        Enumeration e = list.elements();
        while (e.hasMoreElements()) {
            group = (CMnPatchApproverGroup) e.nextElement();
            if (group != null) {
                status = group.getStatus();
            }

            // Add the list to the approvals
            Vector<CMnPatchApproverGroup> existingList = (Vector<CMnPatchApproverGroup>) approvers.get(status);
            if (existingList == null) {
                existingList = new Vector<CMnPatchApproverGroup>();
            }
            existingList.add(group);
            approvers.put(status, existingList);
        }

    }



    /**
     * Set information about the user currently viewing the approval form.
     *
     * @param   user   Active user
     */
    public void setUser(UserData user) {
        this.user = user;
    }


    /**
     * Extend the base method for enabling the input mode.  All input fields
     * must be enabled or disabled when this method is called.
     *
     * @param enabled  TRUE to enable form input
     */
    public void setInputMode(boolean enabled) {
        super.setInputMode(enabled);
    }


    /**
     * Set the input fields by examining the HTTP request to see if
     * a value was submitted.
     *
     * @param   req     HTTP request
     */
    public void setValues(HttpServletRequest req) {
        super.setValues(req);
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
     * Render the table header as HTML.
     */
    public String getHeader() {
        StringBuffer html = new StringBuffer();

        html.append("  <tr>\n");
        html.append("    <td bgcolor=\"#CCCCCC\" width=\"10%\">Status</td>\n");
        html.append("    <td bgcolor=\"#CCCCCC\" width=\"10%\">Group</td>\n");
        html.append("    <td bgcolor=\"#CCCCCC\" width=\"10%\">User</td>\n");
        html.append("    <td bgcolor=\"#CCCCCC\" width=\"10%\">Status</td>\n");
        html.append("    <td bgcolor=\"#CCCCCC\" width=\"60%\">Comments</td>\n");
        html.append("  </tr>\n");

        return html.toString();
    }


    /**
     * Render the patch approval form as HTML.
     */
    public String toString() {
        StringBuffer html = new StringBuffer();

        CMnServicePatch.RequestStatus currentStatus = null;
        Vector<CMnPatchApproval> currentApprovals = null;
        Vector<CMnPatchApproverGroup> currentApprovers = null;
        int approverCount = 0;
        int approvalCount = 0;

        // Build a list of the patch status for all approvals and approvers 
        Vector<CMnServicePatch.RequestStatus> status = new Vector<CMnServicePatch.RequestStatus>();
        if (approvers != null) {
            Enumeration approverStatusList = approvers.keys();
            while (approverStatusList.hasMoreElements()) {
                currentStatus = (CMnServicePatch.RequestStatus) approverStatusList.nextElement();
                if (!status.contains(currentStatus)) {
                    status.add(currentStatus);
                }
            }
        }
        if (approvals != null) {
            Enumeration approvalStatusList = approvals.keys();
            while (approvalStatusList.hasMoreElements()) {
                currentStatus = (CMnServicePatch.RequestStatus) approvalStatusList.nextElement();
                if (!status.contains(currentStatus)) {
                    status.add(currentStatus);
                }
            }
        }

        // Display the table of approval information
        if (status.size() > 0) {
            html.append("<!-- Rendering a table with " + status.size() + " status rows -->\n");
            html.append("<table border=0 width=\"100%\">\n");

            // Display the table header
            html.append(getHeader());

            // Iterate through each patch status
            Enumeration statusList = status.elements();
            while (statusList.hasMoreElements()) {
                currentStatus = (CMnServicePatch.RequestStatus) statusList.nextElement();
                html.append("<!-- Rendering " + currentStatus + " patch status -->\n");
                if (approvers != null) {
                    currentApprovers = approvers.get(currentStatus);
                    if (currentApprovers != null) {
                        html.append("<!-- Found " + currentApprovers.size() + " approvers for the current patch status -->\n");
                    }
                }
                if (approvals != null) {
                    currentApprovals = approvals.get(currentStatus);
                    if (currentApprovals != null) {
                        html.append("<!-- Found " + currentApprovals.size() + " approvals for the current patch status -->\n");
                    }
                }

                // Iterate through each approver for the current status
                CMnPatchApproverGroup currentGroup = null;
                if ((currentApprovers != null) && (currentApprovers.size() > 0)) {
                    html.append("<!-- Rendering " + currentApprovers.size() + " approval groups -->\n");
                    Iterator i = currentApprovers.iterator();
                    while (i.hasNext()) {
                        currentGroup = (CMnPatchApproverGroup) i.next();
                        html.append(toString(currentStatus, currentGroup));
                    }
                }
            }

            html.append("</table>\n");
        } else {
            html.append("No approval data.");
        }

        return html.toString();

    }


    /**
     * Render the approval group as a row in the table.
     *
     * @param   status  Service patch status
     * @param   group   Approval group
     *
     * @return  HTML representation of the approval group
     */
    private String toString(CMnServicePatch.RequestStatus status, CMnPatchApproverGroup group) {
        StringBuffer html = new StringBuffer();

        Vector<CMnPatchApproval> currentApprovals = null;
        if (approvals != null) {
            currentApprovals = approvals.get(status);
        }

        String bgcolor = "#FFFF99";

        // Keep track of whether the group name has been displayed
        boolean showGroupName = true;

        // Determine if the user can provide approval for this group
        boolean canApprove = (inputEnabled && (user != null) && user.isPartOf(group.getGroup()));

        // Determine how many rows will be in this group
        Vector<CMnPatchApproval> groupApprovals = new Vector<CMnPatchApproval>();
        if ((currentApprovals != null) && (currentApprovals.size() > 0)) {
            Iterator i = currentApprovals.iterator();
            while (i.hasNext()) {
                CMnPatchApproval current = (CMnPatchApproval) i.next();
                if (current.getUser().isPartOf(group.getGroup())) {
                    groupApprovals.add(current);
                }
            }
        }

        // Construct a list of approvals that belong to this group
        if (groupApprovals.size() > 0) {
            Iterator i = groupApprovals.iterator();
            while (i.hasNext()) {
                CMnPatchApproval current = (CMnPatchApproval) i.next();
                html.append("<tr>\n");

                html.append("<td>" + status + "</td>\n");

                // Only show the group name for the first row
                if (showGroupName) {
                    html.append("<td bgcolor=\"" + bgcolor + "\"");
                    if (groupApprovals.size() > 0) {
                        html.append(" rowspan=" + groupApprovals.size());
                    }
                    html.append(">" + group.getGroup().getName() + "</td>\n");
                    showGroupName = false;
                }

                // Display the approval information
                if (canApprove) {
                    // TODO Allow the user to edit their own approval 
                    //html.append(showApprovalFields(current));
                    html.append(toString(current));
                    canApprove = false;
                } else {
                    html.append(toString(current));
                }
                html.append("</tr>\n");
            }
        } else {
            html.append("<tr>\n");
            html.append("<td>" + status + "</td>\n");
            html.append("<td bgcolor=\"" + bgcolor + "\">" + group.getGroup().getName() + "</td>\n");
            if (canApprove) {
                CMnPatchApproval dummyApproval = new CMnPatchApproval();
                if (patch != null) {
                    dummyApproval.setPatchStatus(patch.getStatus());
                }
                html.append(showApprovalFields(dummyApproval));
            } else {
                html.append("<td bgcolor=\"" + bgcolor + "\" colspan=\"3\" align=\"center\">PENDING</td>\n");
            }
        }

        return html.toString();
    }

    /**
     * Render the patch approval to a row in the table of fixes.
     *
     * @param  approval  Patch approval data
     *
     * @return  HTML representation of the patch approval data
     */
    private String toString(CMnPatchApproval approval) {
        StringBuffer html = new StringBuffer();

        String bgcolor = "#FFFFCC";

        // Render the user information 
        html.append("  <td bgcolor=\"" + bgcolor + "\" NOWRAP valign=\"top\">");
        if (approval.getUser() != null) {
            html.append(approval.getUser().getUsername());
        }
        html.append("</td>\n");

        // Render the approval status 
        html.append("  <td bgcolor=\"" + bgcolor + "\" NOWRAP valign=\"top\">");
        if (approval.getStatus() != null) {
            html.append(approval.getStatus());
        }
        html.append("</td>\n");

        html.append("  <td bgcolor=\"" + bgcolor + "\" valign=\"top\">");
        html.append(approval.getComment());
        html.append("</td>\n");

        return html.toString();
    }

    /**
     * Render the patch approval input fields as a row in the approval table.
     *
     * @param   approval   Existing approval information
     *
     * @return  HTML representation of the patch approval input fields
     */
    private String showApprovalFields(CMnPatchApproval approval) {
        StringBuffer html = new StringBuffer();

        // If we do not have any patch information then don't bother
        // displaying any input fields
        if (patch != null) {

            // Define the background color to be used for these table cells
            String bgcolor = "#CCCCCC"; 

            // Determine if the input fields should be rendered as read-only or not
            boolean localInputEnabled = false;

            SelectTag statusTag = new SelectTag(IMnPatchForm.APPROVAL_STATUS);
            statusTag.addOption(CMnServicePatch.ApprovalStatus.REJECTED.name(), PATCH_REJECTED_VALUE);
            statusTag.addOption(CMnServicePatch.ApprovalStatus.APPROVED.name(), PATCH_APPROVED_VALUE);
            statusTag.setSorting(true);
            statusTag.setDefault(CMnServicePatch.ApprovalStatus.APPROVED.name());

            TextTag messageTag = new TextTag(IMnPatchForm.APPROVAL_COMMENT);
            messageTag.setHeight(3);
            messageTag.setWidth(30);

            // Set the form values if present
            if (approval != null) {
                // Set the value of the approval status (approved or rejected)
                CMnServicePatch.ApprovalStatus approvalStatus = approval.getStatus();
                if (approvalStatus != null) {
                    statusTag.setSelected(approvalStatus.name());
                }

                // Set the comment text associated with the approval
                if (approval.getComment() != null) {
                    messageTag.setValue(approval.getComment());
                }

                // Determine whether the input should be active or inactive based
                // on the status of the patch relative to the patch status for this approval
                if (approval.getPatchStatus() == patch.getStatus()) {
                    bgcolor = "#FFFFCC";
                    localInputEnabled = true;
                }
            }

            // Disable the input fields if the user cannot submit the text
            statusTag.setDisabled(!localInputEnabled);
            messageTag.setDisabled(!localInputEnabled);

            // Render the user information
            html.append("<td bgcolor=\"" + bgcolor + "\" NOWRAP valign=\"top\">");
            html.append("<input type=\"hidden\" name=\"" + IMnPatchForm.PATCH_ID_LABEL + "\" value=\"" + patch.getId() + "\"/>");
            html.append("</td>");

            // Render the approval status
            html.append("  <td bgcolor=\"" + bgcolor + "\" NOWRAP valign=\"top\">");
            html.append(statusTag.toString());
            html.append("</td>\n");

            html.append("  <td bgcolor=\"" + bgcolor + "\" valign=\"top\">");
            html.append(messageTag.toString());
            html.append("</td>\n");

        } else {
            html.append("<!-- Patch information is null.  Unable to render approval input fields. -->\n");
        }

        return html.toString();
    }

}


