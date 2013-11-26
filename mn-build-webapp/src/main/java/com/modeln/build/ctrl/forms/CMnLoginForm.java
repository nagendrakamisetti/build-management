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
import javax.servlet.http.*;
import com.modeln.build.common.data.account.UserData;
import com.modeln.build.web.forms.ValidateUserData;
import com.modeln.build.web.errors.ErrorMap;

/**
 * The LoginForm validates user input from the login screen before
 * attempting to perform authentication.
 *
 * @author             Shawn Stafford
 *
 */
public class CMnLoginForm {

    /** The URL used when submitting form input. */
    protected URL formUrl = null;

    /**  URL for resetting the user password */
    private URL pwResetUrl = null;


    /**
     * Construct the login form.
     */
    public CMnLoginForm(URL url) {
        formUrl = url;
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
     * Validate the data submitted by the user and return any error codes.
     *
     * @param   req     HTTP request
     * @param   res     HTTP response
     * 
     * @return  Error code if any errors were found.
     */
    public static final int validate(HttpServletRequest req, HttpServletResponse res) {
        String username = getUsername(req);
        String password = getPassword(req);

        // Validate the username
        int usernameError = ValidateUserData.validateUsername(username);
        if (usernameError != ErrorMap.NO_ERROR) {
            return usernameError;
        }

        // Validate the password
        int passwordError = ValidateUserData.validatePassword(password);
        if (passwordError != ErrorMap.NO_ERROR) {
            return passwordError;
        }
        if (ValidateUserData.isCircular(password, username)) {
            return ErrorMap.PASSWORD_USERNAME_CIRCULAR;
        }
        if (password.indexOf(username) >= 0) {
            return ErrorMap.PASSWORD_CONTAINS_NAME;
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
    public String render(HttpServletRequest req, HttpServletResponse res) {
        StringBuffer html = new StringBuffer();
        html.append("<form action=\"" + formUrl + "\" method=\"post\">\n");
        html.append("<table border='0' cellspacing='2' cellpadding='2' align='center'>\n");
        html.append("  <tr>\n");
        html.append("    <td align='right'>Username:</td>\n");
        html.append("    <td align='left'><input tabindex='1' name='" + UserData.USERNAME + "' type='text'></td>\n");
        html.append("    <td align='left'><input tabindex='3' type='submit' name='submit' value='Login'></td>\n");
        html.append("  </tr>\n");
        html.append("  <tr>\n");
        html.append("    <td align='right'>Password:</td>\n");
        html.append("    <td align='left'><input tabindex='2' name='" + UserData.PASSWORD + "' type='password'></td>\n");
        html.append("    <td></td>\n");
        html.append("  </tr>\n");
        if (pwResetUrl != null) {
            html.append("  <tr>\n");
            html.append("    <td></td>\n");
            html.append("    <td><a href=\"" + pwResetUrl + "\">Forgot your password?</a></td>\n");
            html.append("    <td></td>\n");
            html.append("  </tr>\n");
        }
        html.append("</table>\n");

        URL landingPage = (URL) req.getAttribute(UserData.LANDING_PAGE);
        if (landingPage != null) {
            html.append("<input type='hidden' name='" + UserData.LANDING_PAGE + "' value='" + landingPage + "'/>\n");
        }

        html.append("</form>\n");

        return html.toString();
    }

    /**
     * Set the URL used to reset the user password. 
     *
     * @param  url   Link to the password reset command
     */
    public void setPasswordResetUrl(URL url) {
        pwResetUrl = url;
    }


    /**
     * Return the username collected from the form submission.
     *
     * @param   req     HTTP request
     *
     * @return  Username string
     */
    public static final String getUsername(HttpServletRequest req) {
        return (String) req.getParameter(UserData.USERNAME);
    }

    /**
     * Return the password collected from the form submission.
     *
     * @param   req     HTTP request
     *
     * @return  Password string
     */
    public static final String getPassword(HttpServletRequest req) {
        return (String) req.getParameter(UserData.PASSWORD);
    }

    /**
     * Return the landing page collected from the form submission.
     *
     * @param   req     HTTP request
     *
     * @return  Password string
     */
    public static final String getLandingPage(HttpServletRequest req) {
        return (String) req.getParameter(UserData.LANDING_PAGE);
    }

}
