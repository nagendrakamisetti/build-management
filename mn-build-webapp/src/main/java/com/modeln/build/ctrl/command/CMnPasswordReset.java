/*
 * Login.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.ctrl.command; 

import com.modeln.build.ctrl.forms.CMnPasswordResetForm;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Vector;

import com.modeln.build.common.data.account.UserData;
import com.modeln.build.common.database.LoginTable;
import com.modeln.build.web.errors.ApplicationError;
import com.modeln.build.web.errors.ApplicationException;
import com.modeln.build.util.StringUtility;
import com.modeln.build.web.application.CommandResult;
import com.modeln.build.web.application.UnprotectedCommand;
import com.modeln.build.web.application.WebApplication;
import com.modeln.build.web.database.RepositoryConnection;
import com.modeln.build.web.errors.ErrorMap;
import com.modeln.build.web.util.HttpUtility;
import com.modeln.build.web.util.SessionUtility;


/**
 * The password reset command is used to assign a random
 * password to the currently logged in user and e-mail the
 * temporary password to the user. 
 * 
 * @author             Shawn Stafford
 */
public class CMnPasswordReset extends UnprotectedCommand {

    /**
     * This is the primary method which will be used to perform the command
     * actions.  The application will use this method to service incoming
     * requests.  You must pass a reference to the calling application into
     * the service method to allow callback method calls to be performed.
     *
     * @param   app     Application which called the command
     * @param   req     HttpServletRequest object
     * @param   res     HttpServletResponse object
     */
    public CommandResult execute(WebApplication app, HttpServletRequest req, HttpServletResponse res) 
        throws ApplicationException
    {
        // Execute the generic actions for all commands
        CommandResult result = super.execute(app, req, res);

        // Execute the actions for the command
        if (!result.containsError()) {
            ApplicationException exApp = null;
            ApplicationError error = null;
            RepositoryConnection rc = null;
            RepositoryConnection ac = null;
            LoginTable lt = LoginTable.getInstance();
            try {
                rc = app.getRepositoryConnection();
                ac = app.getAccountConnection();

                String username = CMnPasswordResetForm.getUsername(req);
                String pwold    = CMnPasswordResetForm.getPassword(req);
                String pw1      = CMnPasswordResetForm.getNewPassword1(req);
                String pw2      = CMnPasswordResetForm.getNewPassword2(req);

                // Attempt to look up the user data by username
                UserData user = null;
                if ((username != null) && (username.length() > 0)) {
                    user = lt.getUserByName(ac.getConnection(), username);
                }

                // Make sure the user has a vaild e-mail address
                if ((user != null) && (user.getEmailAddress() != null) && (pwold == null)) {
                    // Store the user information in the session
                    req.setAttribute(CMnPasswordResetForm.USER_DATA, user);

                    // Update the user password with a temporary value
                    String newpass = StringUtility.getRandomString(8);
                    user.setPassword(newpass, UserData.UNENCRYPTED_PASSWORD);
                    lt.updateUser(ac.getConnection(), user);

                    // Notify the user of the new password
                    String url = HttpUtility.getApplicationUrl(req) + 
                        "/command/CMnPasswordReset" +
                        "?" + CMnPasswordResetForm.USERNAME_LABEL + "=" + username +
                        "&" + CMnPasswordResetForm.PASSWORD_LABEL + "=" + newpass;

                    StringBuffer sb = new StringBuffer();
                    sb.append("You have requested a password reset for the following account:\n\n");
                    sb.append("   username: " + username + "\n");
                    sb.append("   password: " + newpass  + "\n");
                    sb.append("Please use the Password Reset page to update your password:\n");
                    sb.append(url);

                    String subject = "Password Reset";
                    app.sendMailMessage(user.getEmailAddress(), subject, sb.toString());

                    result.setDestination("password.jsp");
                } else if ((user != null) && (pwold != null) && (pw1 != null) && (pw2 != null)) {
                    // Validate the reset request form
                    int formError = CMnPasswordResetForm.validate(req, res); 
                    if (formError == ErrorMap.NO_ERROR) {
                        // Ensure that the user authentication is correct
                        error = app.authenticate(req.getSession(), username, pwold);
                        if (error != null) {
                            result.setError(error);
                            result.setDestination("password.jsp");
                        } else {
                            // Update the user password
                            String newpass = CMnPasswordResetForm.getNewPassword1(req);
                            user.setPassword(newpass, UserData.UNENCRYPTED_PASSWORD);
                            lt.updateUser(ac.getConnection(), user);
                            // TODO: direct the user to an error page if the new password was not set
                            result.setDestination(app.getHomePage());
                        }
                    } else {
                        // TODO: construct an error for form validation
                        result.setDestination("password.jsp");
                    }
                } else {
                    result.setDestination("password.jsp");
                }

            } catch (Exception ex) {
                exApp = new ApplicationException(
                                ErrorMap.APPLICATION_DISPLAY_FAILURE,
                                "Unable to forward to the login page.");
                exApp.setStackTrace(ex);
            } finally {
                app.releaseRepositoryConnection(rc);
                app.releaseRepositoryConnection(ac);

                // Throw any exceptions once the database connections have been cleaned up
                if (exApp != null) {
                    throw exApp;
                }
            }
        }

        return result;
    }


}
