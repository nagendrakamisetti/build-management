/*
 * Login.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.ctrl.command; 

import com.modeln.build.ctrl.forms.CMnLoginForm;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.Vector;

import com.modeln.build.web.errors.ApplicationError;
import com.modeln.build.web.errors.ApplicationException;
import com.modeln.build.web.application.CommandResult;
import com.modeln.build.web.application.UnprotectedCommand;
import com.modeln.build.web.application.WebApplication;
import com.modeln.build.web.errors.ErrorMap;
import com.modeln.build.common.data.account.Password;


/**
 * The Login command attempts to authenticate the user and 
 * create a login session if successful.
 * 
 * @version            $Revision: 1.1.1.1 $  
 * @author             Shawn Stafford
 */
public class CMnLogin extends UnprotectedCommand {

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
            try {
                app.debug("Executing user login command.");
                String username = CMnLoginForm.getUsername(req);
                String password = CMnLoginForm.getPassword(req);
                if ((username != null) && (password != null)) {
                    int formError = CMnLoginForm.validate(req, res);
                    if (formError == ErrorMap.NO_ERROR) {
                        app.debug("Login information complete.  Performing user authentication.");
                        error = app.authenticate(req.getSession(), username, password);
                        if (error != null) {
                            app.debug("Authentication failure: " + error.getErrorCode());
                            result.setError(error);
                            result.setDestination(app.getLoginPage());
                        } if (app.getLandingPage(req) != null) {
                            app.debug("Login successful.  Forwarding user to landing page: " + app.getLandingPage(req));
                            result.setDestination(app.getLandingPage(req));
                        } else {
                            app.debug("Login successful.  Forwarding user to home page: " + app.getHomePage());
                            result.setDestination(app.getHomePage());
                        }
                    } else {
                        app.debug("CMnLoginForm validation error: " + formError);
                        result.setError(app.getError(formError));
                        result.setDestination(app.getLoginPage());
                    }
                } else {
                    System.out.println("Required value not found: username=" + username + ", password=" + password);
                    result.setDestination(app.getLoginPage());
                }
            } catch (Exception ex) {
                exApp = new ApplicationException(
                            ErrorMap.APPLICATION_DISPLAY_FAILURE,
                            "Unable to forward to the login page.");
                exApp.setStackTrace(ex);
            } finally {
                // Throw any exceptions once the database connections have been cleaned up
                if (exApp != null) {
                    throw exApp;
                }
            }
        }

        return result;
    }


}
