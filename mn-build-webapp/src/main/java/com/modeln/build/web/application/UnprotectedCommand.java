/*
 * UnprotectedCommand.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.web.application;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

import com.modeln.build.common.data.account.*;
import com.modeln.build.web.errors.*;
import com.modeln.build.common.logging.SessionLog;
import com.modeln.build.web.data.*;
import com.modeln.build.web.errors.*;
import com.modeln.build.web.util.*;

/**
 * The UnprotectedCommand provides command methods which allow the
 * WebApplication to perform specific tasks based upon the
 * user request.  An UnprotectedCommand does not require the user
 * to be logged in to perform it's task.
 * 
 * @version            $Revision: 1.1.1.1 $  
 * @author             Shawn Stafford
 */
public class UnprotectedCommand extends BaseCommand implements Command {


    /**
     * Perform any actions which must be performed before the command
     * can be executed.
     *
     * @param   app     Application which called the command
     * @param   req     HttpServletRequest object
     * @param   res     HttpServletResponse object
     */
    public CommandResult prepare(WebApplication app, HttpServletRequest req, HttpServletResponse res) 
        throws ApplicationException
    {
        // Make sure the application is supplied before proceeding
        if (app == null) {
            throw new ApplicationException(
                ErrorMap.INVALID_APPLICATION, "Unable to access the calling application.");
        }

        CommandResult result = new CommandResult();

        // Log the current action
        commonLog = app.getLogInstance();

        return result;
    }


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
        CommandResult result = prepare(app, req, res);

        // Execute the actions for the command

        return result;
    }

    /**
     * Perform any actions which must be performed after the command
     * has been executed.  This method is not automatically called at the
     * end of the execute method, so you must explicitly call it within
     * every implementation of the execute method.
     *
     * @param   app     Application which called the command
     * @param   req     HttpServletRequest object
     * @param   res     HttpServletResponse object
     * @param   cmdInfo Command response which determines final actions
     */
    public final void finalize(WebApplication app, HttpServletRequest req, HttpServletResponse res, CommandResult cmdInfo) 
        throws ApplicationException
    {
        // Send any pending cookies
        Cookie[] cookies = SessionUtility.getCookies(req.getSession());
        if ((cookies != null) && (cookies.length > 0)) {
            HttpUtility.addCookies(req, res, cookies);
        }

        // Set any temporary attributes
        if (cmdInfo.containsError()) {
            HttpUtility.setError(req, cmdInfo.getError());

            // If no destination page has been specified, display default error page
            if (cmdInfo.getDestination() == null) {
                cmdInfo.setDestination(app.getErrorPage());
            }
        }

        // Create a timestamp cookie
        HttpUtility.setTicket(req, res, new SessionTicket());

        // Set a global alert message
        HttpUtility.setAlert(req, res, app.getAlertMessage());

        // Update the session information
        SessionTracker.updateActivity(req);

        // Log the error so at least we know what's going on
        if ((cmdInfo != null) && (cmdInfo.getError() != null)) {
            app.log(cmdInfo.getError());
        }


        try {
            if (cmdInfo.useRedirect()) {
                res.sendRedirect(cmdInfo.getDestination());
            } else {
                app.forwardToFile(req, res, cmdInfo.getDestination());
            }
        } catch (Exception ex) {
            ApplicationException aex = new ApplicationException(ErrorMap.SERVER_REDIRECTION_FAILED, 
                "Could not forward to the appropriate page: " + cmdInfo.getDestination());
            aex.setStackTrace(ex);
            throw aex;
        }

    }


}
