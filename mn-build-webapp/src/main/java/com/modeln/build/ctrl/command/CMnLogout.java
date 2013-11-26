/*
 * CMnLogout.java
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
import com.modeln.build.web.application.UnprotectedCommand;
import com.modeln.build.web.application.WebApplication;
import com.modeln.build.web.errors.ErrorMap;
import com.modeln.build.web.util.SessionUtility;
import com.modeln.build.common.data.account.Password;


/**
 * The logout command removes the user session. 
 * 
 * @version            $Revision: 1.1.1.1 $  
 * @author             Shawn Stafford
 */
public class CMnLogout extends UnprotectedCommand {

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
                SessionUtility.logout(req.getSession());
                result.setDestination(app.getLoginPage());
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
