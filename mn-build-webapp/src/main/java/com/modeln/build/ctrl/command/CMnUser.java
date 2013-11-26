/*
 * Login.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.ctrl.command; 

import com.modeln.build.ctrl.forms.CMnUserForm;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Vector;

import com.modeln.build.common.data.account.UserData;
import com.modeln.build.common.database.LoginTable;
import com.modeln.build.common.database.LoginGroupTable;
import com.modeln.build.web.errors.ApplicationError;
import com.modeln.build.web.errors.ApplicationException;
import com.modeln.build.web.application.CommandResult;
import com.modeln.build.web.application.AdminCommand;
import com.modeln.build.web.application.WebApplication;
import com.modeln.build.web.database.RepositoryConnection;
import com.modeln.build.web.errors.ErrorMap;


/**
 * The user command is used to edit user login information. 
 * 
 * @version            $Revision: 1.1.1.1 $  
 * @author             Shawn Stafford
 */
public class CMnUser extends AdminCommand {

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
            try {
                LoginTable lt = LoginTable.getInstance();
                rc = app.getRepositoryConnection();
                ac = app.getAccountConnection();
                UserData user = null;

                String uid = (String) req.getParameter(CMnUserForm.USER_ID_LABEL);
                if (uid == null) {
                    uid = (String) req.getAttribute(CMnUserForm.USER_ID_LABEL);
                }

                // Determine if the user has submitted the information
                // Don't fall back to the request attributes because we don't want to
                // consider this field if the request was forwarded from another command
                String submitValue = (String) req.getParameter(CMnUserForm.USER_DATA_BUTTON);
                if (submitValue != null) {
                    user = CMnUserForm.getValues(req);
                } else if (uid != null) {
                    user = lt.getUserByUid(ac.getConnection(), uid);
                }

                // If userId is specified it means we are editing an existing customer
                if ((submitValue != null) && (user != null) && (user.getUid() != null)) {
                    // Update the user data using the submitted information
                    boolean success = lt.updateUser(ac.getConnection(), user);
                    if (success) {
                        result = app.forwardToCommand(req, res, "/CMnUserList");
                    } else {
                        result.setError(app.getError(ErrorMap.DATABASE_TRANSACTION_FAILURE));
                        result.setDestination("error.jsp");
                    }
                } else if ((submitValue != null) && (user != null)) {
                    boolean success = false;

                    // Make sure a user does not already exist with that username
                    UserData existingUser = lt.getUserByName(rc.getConnection(), user.getUsername());
                    if (existingUser != null) { 
                        result.setError(app.getError(ErrorMap.LOGIN_EXISTS));
                    } else {
                        // Add the submitted user to the database
                        success = lt.addUser(rc.getConnection(), user);
                        if (!success) {
                            result.setError(app.getError(ErrorMap.DATABASE_TRANSACTION_FAILURE));
                        }
                    }

                    // Send the user to the next page
                    if (success) {
                        result = app.forwardToCommand(req, res, "/CMnUserList");
                    } else {
                        result.setDestination("error.jsp");
                    }

                } else {
                    // Obtain a list of user groups
                    Vector groups = LoginGroupTable.getGroups(rc.getConnection());
                    req.setAttribute(CMnUserForm.GROUP_LIST_DATA, groups);

                    // Display the form for editing or adding a new user
                    if (user != null) {
                        req.setAttribute(CMnUserForm.USER_DATA, user);
                    }
                    result.setDestination("user.jsp");
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
