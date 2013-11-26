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

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.Vector;
import java.util.Hashtable;
import javax.servlet.http.HttpServletRequest;

import com.modeln.build.common.data.account.GroupData;
import com.modeln.build.common.data.account.UserData;
import com.modeln.build.common.database.LoginTable;
import com.modeln.build.web.tags.TextTag;
import com.modeln.build.web.tags.DateTag;
import com.modeln.build.web.tags.OptionTag;
import com.modeln.build.web.tags.SelectTag;
import com.modeln.build.web.tags.TagGroup;

/**
 * The user form provides an HTML interface to a list of login data objects.
 * The class manages transfering the data through the HTTP request.
 * Form data can be rendered in either a read-only or input mode.
 *
 * @author  Shawn Stafford
 */
public class CMnUserListForm implements IMnUserForm {

    /** List of users */
    private Vector<UserData> userList = null;

    /** Determines whether the form uses the get or post method to submit data */
    protected boolean postMethodEnabled = true;

    /** Determines whether the form will be rendered with input fields. */
    protected boolean inputEnabled = false;

    /** Determines whether the form will be rendered with admin functionality. */
    protected boolean adminEnabled = false;


    /** The URL used when submitting form input. */
    protected URL formUrl = null;

    /** The URL used when accessing images */
    protected URL imageUrl = null;

    /** The URL used to view individual user data */
    protected URL userUrl = null;

    /** The URL used to delete users */
    protected URL deleteUrl = null;

    /**
     * Construct a user data list.  When input is enabled, the form
     * will be rendered with input fields.
     *
     * @param    users          List of users
     */
    public CMnUserListForm(URL form, URL images, Vector<UserData> users) {
        userList = users;
        formUrl = form;
        imageUrl = images;
    }


    /**
     * Return the URL for form submissions.
     *
     * @return  Form submission URL
     */
    public String getFormUrl() {
        return formUrl.toString();
    }

    /**
     * Return the base URL for retrieving images.
     *
     * @return  Base image URL
     */
    public String getImageUrl() {
        return imageUrl.toString();
    }


    /**
     * Set the URL used to view individual user data. 
     *
     * @param  url   Link to the command related to user data 
     */
    public void setUserUrl(URL url) {
        userUrl = url;
    }

    /**
     * Return the base URL for viewing and individual user. 
     *
     * @return  User URL
     */
    public String getUserUrl() {
        return userUrl.toString();
    }


    /**
     * Set the URL used to delete user entries.
     *
     * @param  url   Link to the command related to user deletion
     */
    public void setDeleteUrl(URL url) {
        deleteUrl = url;
    }

    /**
     * Return the URL for deleting users. 
     *
     * @return  Delete URL 
     */
    public String getDeleteUrl() {
        return deleteUrl.toString();
    }


    /**
     * Enables or disables administrative functionality.
     *
     * @param enabled  TRUE to enable administrative functionality
     */
    public void setAdminMode(boolean enabled) {
        adminEnabled = enabled;
    }

    /**
     * Determines if the administrative functionality is enabled.
     *
     * @return TRUE if administrative functionality is enabled.
     */
    public boolean getAdminMode() {
        return adminEnabled;
    }


    /**
     * Determines if form input is allowed.
     *
     * @return TRUE if form input is enabled.
     */
    public boolean getInputMode() {
        return inputEnabled;
    }

    /**
     * Enables or disables the POST method for submitting form data.
     *
     * @param enabled  TRUE to use the POST method, FALSE to use the GET method
     */
    public void setPostEnabled(boolean enabled) {
        postMethodEnabled = enabled;
    }



    /**
     * Construct a table header.
     */
    private String getHeader() {
        StringBuffer html = new StringBuffer();
        html.append("<tr class=\"spreadsheet-header\">\n");
        html.append("  <td nowrap width=\"2%\">&nbsp;</td>\n");
        html.append("  <td nowrap width=\"2%\">UID</td>\n");
        html.append("  <td nowrap width=\"13%\">Username</td>\n");
        html.append("  <td nowrap width=\"20%\">First Name</td>\n");
        html.append("  <td nowrap width=\"20%\">Last Name</td>\n");
        html.append("  <td nowrap width=\"20%\">E-mail</td>\n");
        html.append("  <td nowrap width=\"13%\">Primary Group</td>\n");
        html.append("  <td nowrap width=\"10%\">Status</td>\n");
        html.append("</tr>\n");
        return html.toString();
    }

    /**
     * Construct a table footer.
     */
    private String getFooter() {
        StringBuffer html = new StringBuffer();
        if (userUrl != null) {
            html.append("<tr class=\"spreadsheet-footer\">\n");
            html.append("  <td colspan=\"8\">");
            html.append("<a href=\"" + userUrl + "\">Add User</a>");
            html.append("</td>\n");
            html.append("</tr>\n");
        }
        return html.toString();
    }



    /**
     * Construct a table header containing input fields.
     */
    private String getInputFields() {
        StringBuffer html = new StringBuffer();

/*
        html.append("<tr class=\"spreadsheet-subheader\">\n");
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
*/

        return html.toString();
    }


    /**
     * Render the list of users as rows in a table.
     */
    private String getUserList() {
        StringBuffer html = new StringBuffer();

        Enumeration users = userList.elements();
        while (users.hasMoreElements()) {
            UserData user = (UserData) users.nextElement();
            html.append("<tr class=\"spreadsheet-shaded\">\n");

            // Column: Delete
            html.append("  <td NOWRAP>");
            if (adminEnabled) {
                String deleteHref = deleteUrl + "?" + USER_ID_LABEL + "=" + user.getUid();
                html.append("<a href=\"" + deleteHref + "\"><img border=\"0\" src=\"" + getImageUrl() + "/icons_small/trashcan_red.png\" alt=\"Delete\"></a>");
            } else {
                html.append("&nbsp;");
            }
            html.append("</td>\n");

            String username = null;
            if (userUrl != null) {
                String href = userUrl + "?" + IMnUserForm.USER_ID_LABEL + "=" + user.getUid();
                username = "<a href=\"" + href + "\">" + user.getUsername() + "</a>";
            } else {
                username = user.getUsername();
            }
            html.append("  <td NOWRAP>" + user.getUid() + "</td>\n");
            html.append("  <td NOWRAP>" + username + "</td>\n");
            html.append("  <td NOWRAP>" + user.getFirstName() + "</td>\n");
            html.append("  <td NOWRAP>" + user.getLastName() + "</td>\n");
            html.append("  <td NOWRAP>" + user.getEmailAddress() + "</td>\n");
            html.append("  <td NOWRAP>");
            GroupData group = user.getPrimaryGroup();
            if (group != null) {
                if (group.getName() != null) {
                    html.append(group.getName());
                } else {
                    html.append("GID: " + group.getGid());
                }
            }
            html.append("</td>\n");
            html.append("<td NOWRAP>" + LoginTable.getUserStatus(user) + "</td>\n");
            html.append("</tr>\n");
        }

        return html.toString();
    }


    /**
     * Render the patch request form as HTML.
     */
    public String toString() {
        StringBuffer html = new StringBuffer();

        try {
            html.append("<form action=" + getFormUrl() + ">\n");
            html.append("<table width=\"100%\" class=\"spreadsheet\">\n");
            html.append(getHeader());
            if (inputEnabled) {
                html.append(getInputFields());
            }
            html.append(getUserList());
            html.append(getFooter());
            html.append("</table>\n");
            html.append("</form>\n");
        } catch (Exception ex) {
           html.append(ex.getMessage());
        }

        return html.toString();
    }


}


