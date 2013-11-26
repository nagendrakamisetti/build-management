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
import com.modeln.build.common.data.product.CMnPatchComment;
import com.modeln.build.common.data.product.CMnPatchOwner;
import com.modeln.build.common.enums.CMnServicePatch;

import java.net.URL;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import javax.servlet.http.HttpServletRequest;

import com.modeln.build.common.data.account.UserData;
import com.modeln.build.web.tags.TextTag;
import com.modeln.build.web.tags.DateTag;
import com.modeln.build.web.tags.OptionTag;
import com.modeln.build.web.tags.SelectTag;



/**
 * The patch comment form provides a way to add comments to a 
 * patch request. 
 * The class manages transfering the data through the HTTP request.
 * Form data can be rendered in either a read-only or input mode.
 * 
 * @author  Shawn Stafford
 */
public class CMnPatchCommentForm extends CMnBaseForm implements IMnPatchForm {

    /** Default title used when displaying a title and border */
    private static final String DEFAULT_TITLE = "Patch Comment";


    /** Label to identify the patch owner value in the http request */
    public static final String USER_LABEL = "user_id";

    /** Label to identify the comment status value in the http request */
    public static final String STATUS_LABEL = "status";

    /** Label to identify the  comment value in the http request */
    public static final String COMMENT_LABEL = "comments";



    /** Patch ID */
    private TextTag patchIdTag = new TextTag(PATCH_ID_LABEL);

    /** Comment status */
    private SelectTag statusTag = new SelectTag(STATUS_LABEL);



    /** Assignment comments */
    private TextTag commentTag = new TextTag(COMMENT_LABEL);


    /** Save the patch data in case the actual data values are needed */
    private CMnPatch patch = null;

    /** Save the comment data in case the actual values are needed */
    private CMnPatchComment comment = null;



    /**
     * Construct a form for editing user information. 
     *
     * @param    form     URL for submitting form data
     * @param    images   URL for displaying HTML images
     * @param    patch    Patch information
     */
    public CMnPatchCommentForm(URL form, URL images, CMnPatch patch) {
        super(form, images);

        this.patch = patch;
        if ((patch != null) && (patch.getId() != null)) {
            patchIdTag.setValue(patch.getId().toString());
        }

        statusTag.setOptions(CMnServicePatch.getCommentStatusList());
        statusTag.setSorting(true);
        statusTag.setDefault(CMnServicePatch.CommentStatus.SHOW.toString());

        commentTag.setWidth(30);
        commentTag.setHeight(5);
    }


    /**
     * Extend the base method for enabling the input mode.  All input fields
     * must be enabled or disabled when this method is called.
     *
     * @param enabled  TRUE to enable form input
     */
    public void setInputMode(boolean enabled) {
        super.setInputMode(enabled);
        statusTag.setDisabled(!enabled);
        commentTag.setDisabled(!enabled);
    }


    /**
     * Set the comment data that should be rendered in the form.
     *
     * @param  data   Comment data
     */
    public void setValues(CMnPatchComment data) {
        comment = data;
        if (data != null) {
            if (data.getStatus() != null) {
                statusTag.setSelected(data.getStatus().toString());
            }

            commentTag.setValue(data.getComment());
        }
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
        html.append("    <td bgcolor=\"#CCCCCC\" width=\"15%\">Date</td>\n");
        html.append("    <td bgcolor=\"#CCCCCC\" width=\"15%\">User</td>\n");
        html.append("    <td bgcolor=\"#CCCCCC\" width=\"60%\">Comment</td>\n");
        html.append("  </tr>\n");

        return html.toString();
    }


    /**
     * Render a comment input form.
     */
    public String getCommentForm() {
        StringBuffer html = new StringBuffer();

        html.append("<form method=\"post\" action=\"" + getFormUrl() + "\">\n");

        patchIdTag.setHidden(true);
        html.append(patchIdTag.toString());

        html.append("<table width=\"100%\" cellspacing=\"0\" cellpadding=\"2\">\n");

        html.append("  <tr>\n");
        html.append("  <td>\n");
        html.append("Status: ");
        statusTag.setDisabled(false);
        html.append(statusTag.toString());
        html.append(" <input type=\"submit\" name=\"" + PATCH_COMMENT_BUTTON + "\" value=\"Submit\"/>");
        html.append("  </td>\n");
        html.append("  </tr>\n");


        html.append("  <tr>\n");
        html.append("  <td>\n");
        commentTag.setDisabled(false);
        html.append(commentTag.toString());
        html.append("  </td>\n");
        html.append("  </tr>\n");

        html.append("</table>\n");
        html.append("</form>\n");

        return html.toString();
    }



    /**
     * Render the build data form.
     */
    public String getCommentTable() {
        StringBuffer html = new StringBuffer();
        StringBuffer table = new StringBuffer();

        // Display the table of comments 
        if ((patch != null) && (patch.getCommentList() != null) && (patch.getCommentList().size() > 0)) {
            table.append("<table border=0 width=\"100%\">\n");
            table.append("<!-- Rendering a table with " + patch.getCommentList().size() + " comments -->\n");

            // Display the table header
            table.append(getHeader());

            // Iterate through each patch comment
            int visibleCount = 0;
            CMnPatchComment currentComment = null;
            Enumeration<CMnPatchComment> commentList = patch.getCommentList().elements();
            while (commentList.hasMoreElements()) {
                currentComment = (CMnPatchComment) commentList.nextElement();
                CMnServicePatch.CommentStatus status = currentComment.getStatus();
                if ((status == CMnServicePatch.CommentStatus.SHOW) || adminEnabled) {
                    visibleCount++;
                    // Display status
                    // Display date
                    // Display user
                    // Display comment
                    table.append("  <tr>\n");
                    table.append("    <td valign=\"top\">" + currentComment.getStatus() + "</td>\n");
                    table.append("    <td valign=\"top\">" + currentComment.getDate() + "</td>\n");
                    table.append("    <td valign=\"top\">");
                    if (currentComment.getUser() != null) {
                        table.append(currentComment.getUser().getFullName());
                    }
                    table.append("</td>\n");
                    table.append("    <td valign=\"top\">" + currentComment.getComment() + "</td>\n");
                    table.append("  </tr>\n");
                }
            }
            table.append("</table>\n");

            if (visibleCount > 0) {
                html.append(table.toString());
            } else {
                html.append("No visible comments.");
            }
        } else {
            html.append("No comments.");
        }


        return html.toString();
    }

}


