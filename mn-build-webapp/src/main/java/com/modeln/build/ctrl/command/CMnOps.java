/*
 * Login.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.ctrl.command; 

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.Vector;

import com.modeln.build.web.errors.ApplicationError;
import com.modeln.build.web.errors.ApplicationException;
import com.modeln.build.web.application.CommandResult;
import com.modeln.build.web.application.AdminCommand;
import com.modeln.build.web.application.WebApplication;
import com.modeln.build.web.errors.ErrorMap;


/**
 * The ops command is used to display operational information about 
 * the application for use in system administration. 
 * 
 * @author             Shawn Stafford
 */
public class CMnOps extends AdminCommand {

    /** Save alert button */
    public static final String ALERT_SAVE_BUTTON = "alertsave";

    /** Clear alert button */
    public static final String ALERT_CLEAR_BUTTON = "alertclear";
 
    /** System alert message */
    public static final String ALERT_LABEL = "alertmsg";


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
                String save = (String) req.getParameter(ALERT_SAVE_BUTTON);
                String clear = (String) req.getParameter(ALERT_CLEAR_BUTTON);
                String alert = (String) req.getParameter(ALERT_LABEL);
                if (save != null) {
                    if ((alert != null) && (alert.trim().length() > 0)) {
                        app.debug("Setting global alert: " + alert);
                        app.setAlertMessage(alert);
                    } else {
                        app.debug("Setting global alert to null.");
                        app.setAlertMessage(null);
                    }
                } else if (clear != null) {
                    app.debug("Clearing global alert.");
                    app.setAlertMessage(null);
                }

                // Place a reference to the application in the session so the 
                // application settings can be displayed
                req.setAttribute("APP", app);

                result.setDestination("ops.jsp");
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
