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
import com.modeln.build.common.data.product.CMnPatchOwner;

import java.net.URL;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import javax.servlet.http.HttpServletRequest;

import com.modeln.build.common.data.account.UserData;
import com.modeln.build.web.tags.TextTag;
import com.modeln.build.web.tags.DateTag;
import com.modeln.build.web.tags.OptionTag;
import com.modeln.build.web.tags.SelectTag;



/**
 * The patch owner form provides a way to assign ownership of a 
 * patch to someone on the team.  The owner will be responsible
 * for resolving the current patch issues. 
 * The class manages transfering the data through the HTTP request.
 * Form data can be rendered in either a read-only or input mode.
 * 
 * @author  Shawn Stafford
 */
public class CMnPatchOwnerForm extends CMnBaseForm implements IMnPatchForm {

    /** Default title used when displaying a title and border */
    private static final String DEFAULT_TITLE = "Patch Owner";


    /** Label to identify the patch owner value in the http request */
    public static final String USER_LABEL = "user_id";

    /** Label to identify the request priority value in the http request */
    public static final String PRIORITY_LABEL = "priority";

    /** Label to identify the work start date value in the http request */
    public static final String START_DATE_LABEL = "startDate";

    /** Label to identify the work end date value in the http request */
    public static final String END_DATE_LABEL = "endDate";

    /** Label to identify the request deadline value in the http request */
    public static final String DEADLINE_LABEL = "deadline";

    /** Label to identify the assignment comment value in the http request */
    public static final String COMMENT_LABEL = "comments";


    /** Label to identify an owner data object in the session */
    public static final String OWNER_DATA = "owner";

    /** Label to identify the list of possible owners in the session */
    public static final String USER_LIST = "users";



    /** Patch ID */
    private TextTag patchIdTag = new TextTag(PATCH_ID_LABEL); 

    /** Patch owner */
    private SelectTag userTag = new SelectTag(USER_LABEL);

    /** Patch priority */
    private SelectTag priorityTag = new SelectTag(PRIORITY_LABEL); 

    /** Patch assignment start date */
    private DateTag startDateTag = null;

    /** Patch assignment end date */
    private DateTag endDateTag = null;

    /** Patch completion deadline */
    private DateTag deadlineTag = null;


    /** Assignment comments */
    private TextTag commentTag = new TextTag(COMMENT_LABEL);


    /** Save the owner data in case the actual values are needed */
    private CMnPatchOwner owner = null;


    /**
     * Construct a form for editing user information. 
     *
     * @param    form     URL for submitting form data
     * @param    images   URL for displaying HTML images
     * @param    patch    Patch information
     * @param    users    List of all possible users
     */
    public CMnPatchOwnerForm(URL form, URL images, CMnPatch patch, Hashtable users) {
        super(form, images);

        if ((patch != null) && (patch.getId() != null)) {
            patchIdTag.setValue(patch.getId().toString());
        }

        priorityTag.setOptions(CMnPatchOwner.getPriorityList());
        priorityTag.setSorting(true);
        priorityTag.setDefault(CMnPatchOwner.PatchPriority.LOW.toString());

        commentTag.setWidth(30);
        commentTag.setHeight(5);

        userTag.setOptions(users);

        GregorianCalendar now = new GregorianCalendar();
        GregorianCalendar startRange = new GregorianCalendar();
        GregorianCalendar endRange = new GregorianCalendar();
        int year = now.get(GregorianCalendar.YEAR);
        startRange.set(GregorianCalendar.YEAR, year - 1);
        endRange.set(GregorianCalendar.YEAR, year + 1);

        startDateTag = new DateTag(START_DATE_LABEL, startRange.getTime(), endRange.getTime(), false);
        endDateTag   = new DateTag(END_DATE_LABEL,   startRange.getTime(), endRange.getTime(), false);
        deadlineTag  = new DateTag(DEADLINE_LABEL,   startRange.getTime(), endRange.getTime(), false);

        startDateTag.setTimeHidden(true);
        endDateTag.setTimeHidden(true);
        deadlineTag.setTimeHidden(true);
    }


    /**
     * Extend the base method for enabling the input mode.  All input fields
     * must be enabled or disabled when this method is called.
     *
     * @param enabled  TRUE to enable form input
     */
    public void setInputMode(boolean enabled) {
        super.setInputMode(enabled);
        priorityTag.setDisabled(!enabled);
        userTag.setDisabled(!enabled);
        commentTag.setDisabled(!enabled);
        startDateTag.setDisabled(!enabled);
        endDateTag.setDisabled(!enabled);
        deadlineTag.setDisabled(!enabled);
    }


    /**
     * Set the build data that should be rendered in the form.
     *
     * @param  data   Build data
     */
    public void setValues(CMnPatchOwner data) {
        owner = data;
        if (data != null) {
            if (data.getUser() != null) {
                userTag.setSelected(data.getUser().getUid());
            }

            if (data.getDeadline() != null) {
                GregorianCalendar deadlineDate = new GregorianCalendar();
                deadlineDate.setTime(data.getDeadline());
                deadlineTag.setDate(deadlineDate);
            }

            if (data.getStartDate() != null) {
                GregorianCalendar startDate = new GregorianCalendar();
                startDate.setTime(data.getStartDate());
                startDateTag.setDate(startDate);
            }

            if (data.getEndDate() != null) {
                GregorianCalendar endDate = new GregorianCalendar();
                endDate.setTime(data.getEndDate());
                endDateTag.setDate(endDate);
            }

            if (data.getPriority() != null) {
                priorityTag.setSelected(data.getPriority().toString());
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
     * Render the build data form.
     */
    public String toString() {
        StringBuffer html = new StringBuffer();

        boolean allowInput = getInputMode();

        patchIdTag.setHidden(true);
        html.append(patchIdTag.toString());

        html.append("<table width=\"100%\" cellspacing=\"0\" cellpadding=\"2\">\n");

        html.append("  <tr>\n");
        html.append("    <td width=\"20%\" align=\"right\">User:</td>\n");
        html.append("    <td width=\"80%\">");
        if (allowInput) {
            html.append(userTag.toString());
        } else {
            String[] user = userTag.getSelectedText();
            int idx = 0;
            while ((user != null) && (idx < user.length)) {
                html.append(user[idx] + " ");
                idx++;
            }
        }
        html.append("</td>\n");
        html.append("  </tr>\n");

        html.append("  <tr>\n");
        html.append("    <td width=\"20%\" align=\"right\">Priority:</td>\n");
        html.append("    <td width=\"80%\">"); 
        if (allowInput) { 
            html.append(priorityTag.toString()); 
        } else {
            String[] priority = priorityTag.getSelectedText();
            int idx = 0;
            while ((priority != null) && (idx < priority.length)) {
                html.append(priority[idx] + " ");
                idx++;
            }
        } 
        html.append("  </tr>\n");

        html.append("  <tr>\n");
        html.append("    <td width=\"20%\" align=\"right\">Deadline:</td>\n");
        html.append("    <td width=\"80%\">"); 
        if (allowInput) { 
            html.append(deadlineTag.toString());
        } else {
            if (deadlineTag.isComplete() && (deadlineTag.getDate() != null)) {
                Date deadline = deadlineTag.getDate();
                html.append(deadline.toString());
            }
        } 
        html.append("  </tr>\n");

        html.append("  <tr>\n");
        html.append("    <td width=\"20%\" align=\"right\">Start Date:</td>\n");
        html.append("    <td width=\"80%\">"); 
        if (allowInput) { 
            html.append(startDateTag.toString());
        } else {
            if (startDateTag.isComplete() && (startDateTag.getDate() != null)) {
                Date startDate = startDateTag.getDate();
                html.append(startDate.toString());
            }
        } 
        html.append("  </tr>\n");

        html.append("  <tr>\n");
        html.append("    <td width=\"20%\" align=\"right\">Completion Date:</td>\n");
        html.append("    <td width=\"80%\">"); 
        if (allowInput) { 
            html.append(endDateTag.toString());
        } else {
            if (endDateTag.isComplete() && (endDateTag.getDate() != null)) {
                Date endDate = endDateTag.getDate();
                html.append(endDate.toString());
            }
        } 
        html.append("  </tr>\n");

        html.append("  <tr>\n");
        html.append("    <td width=\"20%\" align=\"right\" valign=\"top\">Comments:</td>\n");
        html.append("    <td width=\"80%\">"); 
        if (allowInput) { 
            html.append(commentTag.toString()); 
        } else {
            html.append(commentTag.getValue()); 
        } 
        html.append("  </tr>\n");


        html.append("</table>\n");

        return html.toString();
    }

}

