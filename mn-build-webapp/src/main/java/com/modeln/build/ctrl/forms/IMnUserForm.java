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


/**
 * Specify the form interface for all user login fields.
 *
 * @author  Shawn Stafford
 */
public interface IMnUserForm {

    // ========================================================================
    // The following section contains request attribute names
    // ========================================================================

    /** User data object */
    public static final String USER_DATA = "USER";

    /** List of user data objects */
    public static final String USER_LIST_DATA = "USER_LIST";

    /** List of group data objects */
    public static final String GROUP_LIST_DATA = "GROUP_LIST";
 

    // ========================================================================
    // The following section contains URL parameter names
    // ========================================================================

    /** User ID number */
    public static final String USER_ID_LABEL = "uid";

    /** Username */
    public static final String USERNAME_LABEL = "username";

    /** User password */
    public static final String PASSWORD_LABEL = "password";

    /** Password encryption type */
    public static final String PASSWORD_TYPE_LABEL = "pwtype";

    /** User account status */
    public static final String USER_STATUS_LABEL = "ustate";

    /** User primary group */
    public static final String GROUP_ID_LABEL = "gid";

    /** User e-mail address */
    public static final String USER_EMAIL_LABEL = "email";

    /** User first name */
    public static final String USER_FIRSTNAME_LABEL = "fname";

    /** User last name */
    public static final String USER_LASTNAME_LABEL = "lname";

    /** User middle name */
    public static final String USER_MIDDLENAME_LABEL = "mname";

    /** User title */
    public static final String USER_TITLE_LABEL = "title";

    /** User language */
    public static final String USER_LANG_LABEL = "lang";

    /** User country */
    public static final String USER_COUNTRY_LABEL = "country";




    // ========================================================================
    // The following section contains button values
    // ========================================================================

    /** Submit user data information */
    public static final String USER_DATA_BUTTON = "userinfo";


}

