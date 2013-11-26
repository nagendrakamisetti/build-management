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
 * The user form provides an HTML interface to the login data object.
 * The class manages transfering the data through the HTTP request.
 * Form data can be rendered in either a read-only or input mode.
 *
 * @author  Shawn Stafford
 */
public class CMnUserForm implements IMnUserForm {


    /** Value indicating that the password is not encrypted */
    private static final String UNENCRYPTED_PASSWORD = "none";

    /** User ID field */
    private TextTag userIdTag;

    private TextTag usernameTag;
    private TextTag passwordTag;
    private SelectTag passwordTypeTag;
    private SelectTag userStatusTag;
    private SelectTag groupIdTag;
    private TextTag emailTag;
    private TextTag firstnameTag;
    private TextTag lastnameTag;
    private TextTag middlenameTag;
    private SelectTag titleTag;
    private SelectTag languageTag;
    private SelectTag countryTag;

    /** Determines whether the form uses the get or post method to submit data */
    protected boolean postMethodEnabled = true;

    /** Determines whether the form will be rendered with input fields. */
    protected boolean inputEnabled = true;

    /** Determines whether the form will be rendered with admin functionality. */
    protected boolean adminEnabled = false;

    /** The URL used when submitting form input. */
    protected URL formUrl = null;

    /** The URL used when accessing images */
    protected URL imageUrl = null;



    /**
     * Construct a form for editing user information. 
     *
     * @param    form     URL for submitting form data
     * @param    images   URL for displaying HTML images
     */
    public CMnUserForm(URL form, URL images) {
        formUrl = form;
        imageUrl = images;

        userIdTag = new TextTag(USER_ID_LABEL);
        userIdTag.setWidth(10);

        usernameTag = new TextTag(USERNAME_LABEL);
        usernameTag.setWidth(30);

        passwordTag = new TextTag(PASSWORD_LABEL);
        passwordTag.setWidth(30);

        passwordTypeTag = new SelectTag(PASSWORD_TYPE_LABEL);
        passwordTypeTag.setOptions(LoginTable.PASSWORD_TYPE_VALUES);
        passwordTypeTag.addOption(UNENCRYPTED_PASSWORD, UNENCRYPTED_PASSWORD);
        passwordTypeTag.setSorting(true);
        passwordTypeTag.setDefault(UNENCRYPTED_PASSWORD);

        userStatusTag = new SelectTag(USER_STATUS_LABEL);
        userStatusTag.setOptions(LoginTable.STATUS_VALUES);
        userStatusTag.setSorting(true);
        userStatusTag.setDefault(LoginTable.STATUS_ACTIVE);

        // Dynamically populate the list of groups from the database
        groupIdTag = new SelectTag(GROUP_ID_LABEL);

        emailTag = new TextTag(USER_EMAIL_LABEL);
        emailTag.setWidth(30);

        firstnameTag = new TextTag(USER_FIRSTNAME_LABEL);
        firstnameTag.setWidth(30);

        lastnameTag = new TextTag(USER_LASTNAME_LABEL);
        lastnameTag.setWidth(30);

        middlenameTag = new TextTag(USER_MIDDLENAME_LABEL);
        middlenameTag.setWidth(30);

        titleTag = new SelectTag(USER_TITLE_LABEL);
        titleTag.setOptions(LoginTable.TITLE_VALUES);
        titleTag.setSorting(true);
        titleTag.setDefault(LoginTable.TITLE_MR);

        // Dynamically populate the language and country from the java.util.Locale class
        languageTag = new SelectTag(USER_LANG_LABEL);
        countryTag = new SelectTag(USER_COUNTRY_LABEL);
        setLocaleOptions();
    }


    /**
     * Populate the language and country select boxes with values from the 
     * java.util.Locale class.
     */
    private void setLocaleOptions() {
        Hashtable<String,String> lang = new Hashtable<String,String>();
        Hashtable<String,String> ctry = new Hashtable<String,String>();

        Locale[] list = Locale.getAvailableLocales();
        Locale locale = null;
        for (int idx = 0; idx < list.length; idx++) {
            locale = list[idx];
            ctry.put(locale.getCountry(), locale.getDisplayCountry());
            lang.put(locale.getLanguage(), locale.getDisplayLanguage()); 
        }

        languageTag.setOptions(lang);
        countryTag.setOptions(ctry);

        // Set the default selection
        languageTag.setDefault(Locale.getDefault().getLanguage());
        countryTag.setDefault(Locale.getDefault().getCountry());
    }


    /**
     * Hide the form elements
     *
     * @param  hidden   TRUE if the form elements should be hidden
     */
    public void setHidden(boolean hidden) {
        userIdTag.setHidden(hidden);
        usernameTag.setHidden(hidden);
        passwordTag.setHidden(hidden);
        passwordTypeTag.setHidden(hidden);
        userStatusTag.setHidden(hidden);
        groupIdTag.setHidden(hidden);
        emailTag.setHidden(hidden);
        firstnameTag.setHidden(hidden);
        lastnameTag.setHidden(hidden);
        middlenameTag.setHidden(hidden);
        titleTag.setHidden(hidden);
        languageTag.setHidden(hidden);
        countryTag.setHidden(hidden);
    }


    /**
     * Extend the base method for enabling the input mode.  All input fields
     * must be enabled or disabled when this method is called.
     *
     * @param enabled  TRUE to enable form input
     */
    public void setInputMode(boolean enabled) {
        userIdTag.setDisabled(!enabled);
        usernameTag.setDisabled(!enabled);
        passwordTag.setDisabled(!enabled);
        passwordTypeTag.setDisabled(!enabled);
        userStatusTag.setDisabled(!enabled);
        groupIdTag.setDisabled(!enabled);
        emailTag.setDisabled(!enabled);
        firstnameTag.setDisabled(!enabled);
        lastnameTag.setDisabled(!enabled);
        middlenameTag.setDisabled(!enabled);
        titleTag.setDisabled(!enabled);
        languageTag.setDisabled(!enabled);
        countryTag.setDisabled(!enabled);
        inputEnabled = enabled;
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
     * Determines if form data is submitted using the POST method.
     *
     * @return TRUE if form data is submitted using the POST method
     */
    public boolean getPostEnabled() {
        return postMethodEnabled;
    }


    /**
     * Set the group selection options.
     *
     * @param  groups   List of groups
     */
    public void setGroups(Vector groups) {
        groupIdTag.removeAllOptions();
        Enumeration list = groups.elements();
        while (list.hasMoreElements()) {
            GroupData group = (GroupData) list.nextElement();
            groupIdTag.addOption(group.getName(), group.getGid());
        }
    }


    /**
     * Parses the form values and returns a user data object.
     * 
     * @param  req   HTTP request
     */
    public static UserData getValues(HttpServletRequest req) {
        UserData user = new UserData();

        String uid = (String) req.getParameter(USER_ID_LABEL);
        if (uid == null) uid = (String) req.getAttribute(USER_ID_LABEL);
        if (uid != null) user.setUid(uid);

        String username = (String) req.getParameter(USERNAME_LABEL);
        if (username == null) username = (String) req.getAttribute(USERNAME_LABEL);
        if (username != null) user.setUsername(username);

        String email = (String) req.getParameter(USER_EMAIL_LABEL);
        if (email == null) email = (String) req.getAttribute(USER_EMAIL_LABEL);
        if (email != null) user.setEmailAddress(email);

        String firstname = (String) req.getParameter(USER_FIRSTNAME_LABEL);
        if (firstname == null) firstname = (String) req.getAttribute(USER_FIRSTNAME_LABEL);
        if (firstname != null) user.setFirstName(firstname);

        String middlename = (String) req.getParameter(USER_MIDDLENAME_LABEL);
        if (middlename == null) middlename = (String) req.getAttribute(USER_MIDDLENAME_LABEL);
        if (middlename != null) user.setMiddleName(middlename);

        String lastname = (String) req.getParameter(USER_LASTNAME_LABEL);
        if (lastname == null) lastname = (String) req.getAttribute(USER_LASTNAME_LABEL);
        if (lastname != null) user.setLastName(lastname);

        String gid = (String) req.getParameter(GROUP_ID_LABEL);
        if (gid == null) gid = (String) req.getAttribute(GROUP_ID_LABEL);
        if (gid != null) user.setPrimaryGroup(new GroupData(gid));

        String password = (String) req.getParameter(PASSWORD_LABEL);
        String encryption = (String) req.getParameter(PASSWORD_TYPE_LABEL);
        if (password == null) password = (String) req.getAttribute(PASSWORD_LABEL);
        if (encryption == null) encryption = (String) req.getAttribute(PASSWORD_TYPE_LABEL);
        if ((password != null) && (encryption != null)) {
            int type = LoginTable.getEncryptionType(encryption);
            if (type > 0) {
                user.setPassword(password, type);
            } else {
                user.setPassword(password, UserData.UNENCRYPTED_PASSWORD);
            }
        }

        String lang = (String) req.getParameter(USER_LANG_LABEL);
        String ctry = (String) req.getParameter(USER_COUNTRY_LABEL);
        if (lang == null) lang = (String) req.getAttribute(USER_LANG_LABEL);
        if (ctry == null) ctry = (String) req.getAttribute(USER_COUNTRY_LABEL);
        if ((lang != null) && (ctry != null)) {
            user.setLocale(new Locale(lang, ctry));
        }

        String status = (String) req.getParameter(USER_STATUS_LABEL);
        if (status == null) req.getAttribute(USER_STATUS_LABEL);
        if (status != null) LoginTable.setUserStatus(user, status);

        return user;
    }

    /**
     * Set the form information using user data.
     *
     * @param   user   User information
     */
    public void setValues(UserData user) {
        userIdTag.setValue(user.getUid());
        usernameTag.setValue(user.getUsername());
        passwordTag.setValue(user.getPassword());

        switch (user.getPasswordEncryption()) {
            case UserData.CRYPT_PASSWORD:
                passwordTypeTag.setSelected(LoginTable.PASSWORD_TYPE_CRYPT);
                break;
            case UserData.MD5_PASSWORD:
                passwordTypeTag.setSelected(LoginTable.PASSWORD_TYPE_MD5);
                break;
            case UserData.UNENCRYPTED_PASSWORD:
                passwordTypeTag.setSelected(UNENCRYPTED_PASSWORD);
                break;
            default:
                passwordTypeTag.setSelected(UNENCRYPTED_PASSWORD);
        }

        switch (user.getStatus()) {
            case UserData.ACCOUNT_ACTIVE:
                userStatusTag.setSelected(LoginTable.STATUS_ACTIVE);
                break;
            case UserData.ACCOUNT_INACTIVE:
                userStatusTag.setSelected(LoginTable.STATUS_INACTIVE);
                break;
            case UserData.ACCOUNT_DELETED:
                userStatusTag.setSelected(LoginTable.STATUS_DELETED);
                break;
            case UserData.ACCOUNT_ABUSE:
                userStatusTag.setSelected(LoginTable.STATUS_ABUSE);
                break;
            default:
                userStatusTag.setSelected(LoginTable.STATUS_ACTIVE);
        }

        GroupData primaryGroup = user.getPrimaryGroup();
        if (primaryGroup != null) {
            groupIdTag.setSelected(primaryGroup.getGid());
        }

        emailTag.setValue(user.getEmailAddress());
        firstnameTag.setValue(user.getFirstName());
        lastnameTag.setValue(user.getLastName());
        middlenameTag.setValue(user.getMiddleName());

        //titleTag.setSelected();

        Locale locale = user.getLocale();
        if (locale != null) {
            languageTag.setSelected(locale.getLanguage());
            countryTag.setSelected(locale.getCountry());
        }

    }

    /**
     * Set the input fields by examining the HTTP request to see if
     * a value was submitted.
     *
     * @param   req     HTTP request
     */
    public void setValues(HttpServletRequest req) {
        // Populate the list of groups
        Vector groups = (Vector) req.getAttribute(GROUP_LIST_DATA);
        if (groups != null) {
            setGroups(groups);
        }

        // Check the request attribute for a data object with the default/previous values
        UserData data = (UserData) req.getAttribute(USER_DATA);
        if (data != null) {
            setValues(data);
        }
    }


    /**
     * Render the patch request form as HTML.
     */
    public String toString() {
        StringBuffer html = new StringBuffer();

        if (inputEnabled) {
            String method = null;
            if (postMethodEnabled) {
                method = "post";
            } else {
                method = "get";
            }
            html.append("<form method=\"" + method + "\" action=\"" + getFormUrl() + "\">\n");
        }

        // Always render the user ID as a hidden field since a primary key should never change
        // But don't render the value if it is null
        if (userIdTag.isComplete()) {
            userIdTag.setHidden(true);
            html.append(userIdTag.toString());
        }

        html.append("<table width=\"100%\" cellspacing=\"0\" cellpadding=\"1\">\n");

        html.append("  <tr>\n");
        html.append("    <td nowrap width=\"20%\" align=\"right\">First Name:</td>\n");
        html.append("    <td nowrap width=\"80%\" align=\"left\">");
        html.append(firstnameTag.toString());
        html.append("    </td>\n");
        html.append("  </tr>\n");

        html.append("  <tr>\n");
        html.append("    <td nowrap width=\"20%\" align=\"right\">Middle Name:</td>\n");
        html.append("    <td nowrap width=\"80%\" align=\"left\">");
        html.append(middlenameTag.toString());
        html.append("    </td>\n");
        html.append("  </tr>\n");

        html.append("  <tr>\n");
        html.append("    <td nowrap width=\"20%\" align=\"right\">Last Name:</td>\n");
        html.append("    <td nowrap width=\"80%\" align=\"left\">");
        html.append(lastnameTag.toString());
        html.append("    </td>\n");
        html.append("  </tr>\n");

        html.append("  <tr>\n");
        html.append("    <td nowrap width=\"20%\" align=\"right\">E-mail Address:</td>\n");
        html.append("    <td nowrap width=\"80%\" align=\"left\">");
        html.append(emailTag.toString());
        html.append("    </td>\n");
        html.append("  </tr>\n");

        html.append("  <tr>\n");
        html.append("    <td nowrap width=\"20%\" align=\"right\">Language:</td>\n");
        html.append("    <td nowrap width=\"80%\" align=\"left\">");
        html.append(languageTag.toString());
        html.append("    </td>\n");
        html.append("  </tr>\n");

        html.append("  <tr>\n");
        html.append("    <td nowrap width=\"20%\" align=\"right\">Country:</td>\n");
        html.append("    <td nowrap width=\"80%\" align=\"left\">");
        html.append(countryTag.toString());
        html.append("    </td>\n");
        html.append("  </tr>\n");

        html.append("  <tr>\n");
        html.append("    <td nowrap width=\"20%\" align=\"right\">Username:</td>\n");
        html.append("    <td nowrap width=\"80%\" align=\"left\">");
        html.append(usernameTag.toString());
        html.append("    </td>\n");
        html.append("  </tr>\n");

        if (adminEnabled) {
            html.append("  <tr>\n");
            html.append("    <td nowrap width=\"20%\" align=\"right\">Password:</td>\n");
            html.append("    <td nowrap width=\"80%\" align=\"left\">");
            html.append(passwordTag.toString());
            html.append(passwordTypeTag.toString());
            html.append("    </td>\n");
            html.append("  </tr>\n");

            html.append("  <tr>\n");
            html.append("    <td nowrap width=\"20%\" align=\"right\">Primary Group:</td>\n");
            html.append("    <td nowrap width=\"80%\" align=\"left\">");
            html.append(groupIdTag.toString());
            html.append("    </td>\n");
            html.append("  </tr>\n");

            html.append("  <tr>\n");
            html.append("    <td nowrap width=\"20%\" align=\"right\">Account Status:</td>\n");
            html.append("    <td nowrap width=\"80%\" align=\"left\">");
            html.append(userStatusTag.toString());
            html.append("    </td>\n");
            html.append("  </tr>\n");
        }

        // Display the submit button
        html.append("  <tr><td>&nbsp;</td><td><input type=\"submit\" name=\"" + USER_DATA_BUTTON + "\" value=\"Continue\"/></td></tr>\n");


        html.append("</table>\n");


        // Complete the input form
        if (inputEnabled) {
            html.append("</form>\n");
        }

        return html.toString();
    }



}


