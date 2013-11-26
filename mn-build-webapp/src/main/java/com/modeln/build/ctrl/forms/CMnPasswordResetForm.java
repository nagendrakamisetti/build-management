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

import javax.servlet.http.*;
import com.modeln.build.web.forms.ValidateUserData;
import com.modeln.build.web.errors.ErrorMap;

/**
 * The password reset form validates the existing user password
 * and allows them to specify a new password. 
 *
 * @author             Shawn Stafford
 *
 */
public class CMnPasswordResetForm implements IMnUserForm {

    /** Password reset form attribute */
    public static final String NEW_PASSWORD_1 = "pw1";

    /** Password reset form attribute */
    public static final String NEW_PASSWORD_2 = "pw2";


    /**
     * Construct the login form.
     */
    private CMnPasswordResetForm(HttpServletRequest req, HttpServletResponse res) {
    }


    /**
     * Validate the data submitted by the user and return any error codes.
     *
     * @param   req     HTTP request
     * @param   res     HTTP response
     * 
     * @return  Error code if any errors were found.
     */
    public static final int validate(HttpServletRequest req, HttpServletResponse res) {
        String username = getUsername(req);
        String pwold = getPassword(req);
        String pw1 = getNewPassword1(req);
        String pw2 = getNewPassword2(req);

        // Validate the username
        int usernameError = ValidateUserData.validateUsername(username);
        if (usernameError != ErrorMap.NO_ERROR) {
            return usernameError;
        }

        // Validate the old password
        int passwordError = ValidateUserData.validatePassword(pwold);
        if (passwordError != ErrorMap.NO_ERROR) {
            return passwordError;
        }
        if (ValidateUserData.isCircular(pwold, username)) {
            return ErrorMap.PASSWORD_USERNAME_CIRCULAR;
        }
        if (pwold.indexOf(username) >= 0) {
            return ErrorMap.PASSWORD_CONTAINS_NAME;
        }

        // Validate the new password
        int pw1Error = ValidateUserData.validatePassword(pw1);
        if (pw1Error != ErrorMap.NO_ERROR) {
            return pw1Error;
        }
        if (ValidateUserData.isCircular(pw1, username)) {
            return ErrorMap.PASSWORD_USERNAME_CIRCULAR;
        }
        if (pw1.indexOf(username) >= 0) {
            return ErrorMap.PASSWORD_CONTAINS_NAME;
        }

        // Make sure that the password confirmation matches
        if (!pw1.equals(pw2)) {
            return ErrorMap.PASSWORDS_DONT_MATCH;
        }

        // Make sure the pasword has been updated
        if (pwold.equals(pw1)) {
            return ErrorMap.PASSWORD_ALREADY_EXISTS;
        }

        return ErrorMap.NO_ERROR;
    }

    /**
     * Return the form as an HTML representation.
     *
     * @param   req     HTTP request
     * @param   res     HTTP response
     *
     * @return  HTML form rendering
     */
    public static final String render(HttpServletRequest req, HttpServletResponse res) {
        String uname = getUsername(req);
        String pwold = getPassword(req);

        StringBuffer html = new StringBuffer();
        html.append("<table border='0' cellspacing='2' cellpadding='2' align='center'>\n");
        html.append("  <tr>\n");
        html.append("    <td align='right'>Username:</td>\n");
        html.append("    <td align='left'>");
        html.append("<input tabindex='1' name='" + USERNAME_LABEL + "' type='text'");
        if ((uname != null) && (uname.length() > 0)) {
            html.append(" value='" + uname + "'");
        }
        html.append("></td>\n");
        html.append("    <td align='left'><input tabindex='5' type='submit' name='submit' value='Reset'></td>\n");
        html.append("  </tr>\n");
        if ((uname != null) && (uname.length() > 0)) {
            html.append("  <tr>\n");
            html.append("    <td align='right'>Old Password:</td>\n");
            html.append("    <td align='left'>");
            html.append("<input tabindex='2' name='" + PASSWORD_LABEL + "' type='password'");
            if ((pwold != null) && (pwold.length() > 0)) {
                html.append("value='" + pwold + "'");
            }
            html.append("></td>\n");
            html.append("    <td></td>\n");
            html.append("  </tr>\n");
            html.append("  <tr>\n");
            html.append("    <td align='right'>New Password:</td>\n");
            html.append("    <td align='left'><input tabindex='3' name='" + NEW_PASSWORD_1 + "' type='password'></td>\n");
            html.append("    <td></td>\n");
            html.append("  </tr>\n");
            html.append("  <tr>\n");
            html.append("    <td align='right'>Confirm Password:</td>\n");
            html.append("    <td align='left'><input tabindex='4' name='" + NEW_PASSWORD_2 + "' type='password'></td>\n");
            html.append("    <td></td>\n");
            html.append("  </tr>\n");
        }
        html.append("</table>\n");

        return html.toString();
    }

    /**
     * Return the username collected from the form submission.
     *
     * @param   req     HTTP request
     *
     * @return  Username string
     */
    public static final String getUsername(HttpServletRequest req) {
        return (String) req.getParameter(USERNAME_LABEL);
    }

    /**
     * Return the password collected from the form submission.
     *
     * @param   req     HTTP request
     *
     * @return  Password string
     */
    public static final String getPassword(HttpServletRequest req) {
        return (String) req.getParameter(PASSWORD_LABEL);
    }

    /**
     * Return the new password collected from the form submission.
     *
     * @param   req     HTTP request
     *
     * @return  Password string
     */
    public static final String getNewPassword1(HttpServletRequest req) {
        return (String) req.getParameter(NEW_PASSWORD_1);
    }

    /**
     * Return the new password confirmation collected from the form submission.
     *
     * @param   req     HTTP request
     *
     * @return  Password string
     */
    public static final String getNewPassword2(HttpServletRequest req) {
        return (String) req.getParameter(NEW_PASSWORD_2);
    }

}
