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
import com.modeln.build.common.enums.CMnServicePatch;

import java.net.URL;
import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpServletRequest;



/**
 * The form provides an HTML interface to the patch status. 
 *
 * @author  Shawn Stafford
 */
public class CMnPatchStatusForm extends CMnBaseForm implements IMnPatchForm {

    /** List of possible status footprints */
    public static enum DisplaySize {
        SMALL, MEDIUM, LARGE
    }

    /** Size of the form footprint */
    protected DisplaySize footprint = DisplaySize.MEDIUM;

    /** Patch information */
    protected CMnPatch patch = null;


    /**
     * Construct a form for displaying the service patch status. 
     *
     * @param    form     Form submit URL
     * @param    images   URL for displaying HTML images 
     */
    public CMnPatchStatusForm(URL form, URL images) {
        super(form, images);
    }

    /**
     * Set the display size of the status form.
     *
     * @param    size    Display size
     */
    public void setDisplaySize(DisplaySize size) {
        footprint = size;
    } 

    /**
     * Set the form information using patch data.
     *
     * @param   patch   Patch information
     */
    public void setValues(CMnPatch patch) {
        this.patch = patch;
    }

    /**
     * Render the patch request form as HTML. 
     */
    public String toString() {
        StringBuffer html = new StringBuffer();

        boolean allowInput = true;
        boolean showLabels = true;

        String iconDir = "icons_medium";
        int padding = 0;
        int tableWidth = 320;
        int iconWidth = 50;
        int iconSpace = 40; 

        // Define icons for each of the states 
        String imgEdit         = "blank.png";
        String imgApproval     = "blank.png";
        String imgRunning      = "blank.png";
        String imgRelease      = "blank.png";

        // Define the label text for each of the states
        String labelEdit     = "Save";
        String labelApproval = "Approve";
        String labelRunning  = "Build";
        String labelRelease  = "Release";

        // Define button areas below each of the icons
        String btn01 = "&nbsp;";
        String btn02 = "&nbsp;";
        String btn03 = "&nbsp;";
        String btn04 = "&nbsp;";

        // Define the HTML used to introduce space between the icons
        String spacer = "<hr/>";

        // Define action buttons
        String btnEdit = "<input type=\"submit\" name=\"" + IMnPatchForm.PATCH_REQUEST_BUTTON + "\" value=\"Edit\"/>";
        String btnApproval = "<input type=\"submit\" name=\"" + IMnPatchForm.PATCH_APPROVAL_BUTTON + "\" value=\"Request\"/>";
        String btnRunning = "<input type=\"submit\" name=\"" + IMnPatchForm.PATCH_JOB_BUTTON + "\" value=\"Run\"/>";

        // Define the display characteristics based on the display footprint 
        switch (footprint) {
            case SMALL:
                padding = 0;
                iconDir = "icons_small";
                tableWidth = 180;
                iconWidth = 30;
                iconSpace = 20;
                spacer = "<hr/>";
                allowInput = false;
                showLabels = false;
                break;
            case MEDIUM:
                padding = 2;
                iconDir = "icons_medium";
                tableWidth = 320;
                iconWidth = 50;
                iconSpace = 40;
                spacer = "<hr/>";
                allowInput = true;
                showLabels = true;
                break;
            case LARGE:
                padding = 5;
                iconDir = "icons_large";
                tableWidth = 640;
                iconWidth = 100;
                iconSpace = 80;
                spacer = "<hr/>";
                allowInput = true;
                showLabels = true;
                break;
        }


        if (allowInput) {
            html.append("<form method=\"POST\" action=\"" + getFormUrl() + "\">\n");
        }
        if (patch != null) {
            html.append("<input type=\"hidden\" name=\"" + IMnPatchForm.PATCH_ID_LABEL + "\" value=\"" + patch.getId() + "\"/>\n");
            switch (patch.getStatus()) {
                case SAVED: 
                    imgEdit = "success.png"; 
                    btn01 = btnEdit;
                    btn02 = btnApproval;
                    break;
                case APPROVAL:
                    imgEdit = "success.png";
                    imgApproval = "padlock.png";
                    btn01 = btnEdit;
                    btn02 = "(pending)";
                    break;
                case REJECTED:
                    imgEdit = "success.png";
                    imgApproval = "hand.png";
                    btn01 = btnEdit;
                    break;
                case PENDING:
                    imgEdit = "success.png";
                    imgApproval = "success.png";
                    imgRunning = "merge.png";
                    break;
                case CANCELED:
                    imgEdit = "success.png";
                    imgApproval = "success.png";
                    imgRunning = "hand.png";
                    btn01 = btnEdit;
                    btn03 = btnRunning;
                    break;
                case BRANCHING:
                    imgEdit = "success.png";
                    imgApproval = "success.png";
                    imgRunning = "merge.png";
                    break;
                case BRANCHED:
                    imgEdit = "success.png";
                    imgApproval = "success.png";
                    imgRunning = "merge.png";
                    break;
                case BUILDING:
                    imgEdit = "success.png";
                    imgApproval = "success.png";
                    imgRunning = "merge.png";
                    break;
                case BUILT:
                    imgEdit = "success.png";
                    imgApproval = "success.png";
                    imgRunning = "merge.png";
                    break;
                case RUNNING:
                    imgEdit = "success.png";
                    imgApproval = "success.png";
                    imgRunning = "merge.png";
                    break;
                case FAILED:
                    imgEdit = "success.png";
                    imgApproval = "success.png";
                    imgRunning = "important.png";
                    btn01 = btnEdit;
                    btn03 = btnRunning;
                    break;
                case COMPLETE:
                    imgEdit = "success.png";
                    imgApproval = "success.png";
                    imgRunning = "success.png";
                    imgRelease = "query.png";
                    break;
                case RELEASE: 
                    imgEdit = "success.png";
                    imgApproval = "success.png";
                    imgRunning = "success.png";
                    imgRelease = "success.png";
                    break;
            }
        }

        html.append("<table width=\"" + tableWidth + "\" cellspacing=\"0\" cellpadding=\"" + padding + "\">\n");

        html.append("  <tr>\n");
        html.append("    <td nowrap width=\"" + iconWidth + "\" align=\"center\">\n");
        html.append("      <img alt=\"" + labelEdit + "\" title=\"" + labelEdit + "\" src=\"" + getImageUrl() + "/" + iconDir + "/" + imgEdit + "\"/>\n");
        html.append("    </td>\n");
        html.append("    <td nowrap width=\"" + iconSpace + "\" align=\"center\">" + spacer + "</td>\n");
        html.append("    <td nowrap width=\"" + iconWidth + "\" align=\"center\">\n");
        html.append("      <img alt=\"" + labelApproval + "\" title=\"" + labelApproval + "\" src=\"" + getImageUrl() + "/" + iconDir + "/" + imgApproval + "\"/>\n");
        html.append("    </td>\n");
        html.append("    <td nowrap width=\"" + iconSpace + "\" align=\"center\">" + spacer + "</td>\n");
        html.append("    <td nowrap width=\"" + iconWidth + "\" align=\"center\">\n");
        html.append("      <img alt=\"" + labelRunning + "\" title=\"" + labelRunning + "\" src=\"" + getImageUrl() + "/" + iconDir + "/" + imgRunning + "\"/>\n");
        html.append("    </td>\n");
        html.append("    <td nowrap width=\"" + iconSpace + "\" align=\"center\">" + spacer + "</td>\n");
        html.append("    <td nowrap width=\"" + iconWidth + "\" align=\"center\">\n");
        html.append("      <img alt=\"" + labelRelease + "\" title=\"" + labelRelease + "\" src=\"" + getImageUrl() + "/" + iconDir + "/" + imgRelease + "\"/>\n");
        html.append("    </td>\n");
        html.append("  <tr>\n");

        if (showLabels) {
            html.append("  <tr>\n");
            html.append("    <td nowrap width=\"" + iconWidth + "\" align=\"center\">Save</td>\n");
            html.append("    <td nowrap width=\"" + iconSpace + "\" align=\"center\">&nbsp;</td>\n");
            html.append("    <td nowrap width=\"" + iconWidth + "\" align=\"center\">Approve</td>\n");
            html.append("    <td nowrap width=\"" + iconSpace + "\" align=\"center\">&nbsp;</td>\n");
            html.append("    <td nowrap width=\"" + iconWidth + "\" align=\"center\">Build</td>\n");
            html.append("    <td nowrap width=\"" + iconSpace + "\" align=\"center\">&nbsp;</td>\n");
            html.append("    <td nowrap width=\"" + iconWidth + "\" align=\"center\">Release</td>\n");
            html.append("  <tr>\n");
        }

        if (allowInput) {
            html.append("  <tr>\n");
            html.append("    <td nowrap width=\"" + iconWidth + "\" align=\"center\">" + btn01 + "</td>\n");
            html.append("    <td nowrap width=\"" + iconSpace + "\" align=\"center\">&nbsp;</td>\n");
            html.append("    <td nowrap width=\"" + iconWidth + "\" align=\"center\">" + btn02 + "</td>\n");
            html.append("    <td nowrap width=\"" + iconSpace + "\" align=\"center\">&nbsp;</td>\n");
            html.append("    <td nowrap width=\"" + iconWidth + "\" align=\"center\">" + btn03 + "</td>\n");
            html.append("    <td nowrap width=\"" + iconSpace + "\" align=\"center\">&nbsp;</td>\n");
            html.append("    <td nowrap width=\"" + iconWidth + "\" align=\"center\">" + btn04 + "</td>\n");
            html.append("  <tr>\n");
        }


        html.append("</table>\n");

        if (allowInput) {
            html.append("</form>\n");
        }

        return html.toString();
    }

}

