/*
 * ProtectedCommand.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.web.application;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

import com.modeln.build.web.errors.*;
import com.modeln.build.common.data.account.*;
import com.modeln.build.web.errors.*;
import com.modeln.build.web.util.*;

/**
 * The ProtectedCommand provides command methods which allow the
 * WebApplication to perform specific tasks based upon the
 * user request.  A ProtectedCommand requires the user
 * to be logged in to perform it's task.
 * 
 * @version            $Revision: 1.1.1.1 $  
 * @author             Shawn Stafford
 */
public class ProtectedCommand extends UnprotectedCommand {

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
        CommandResult result = super.prepare(app, req, res);

        // Return the error immediately if one is encountered
        if (!result.containsError()) {
            ApplicationError error = verifyUser(app, req, res);
            if (error != null) {
                app.setLandingPage(req);
                result.setDestination(app.getLoginPage());
                result.setError(error);
            }
        }

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

        return result;
    }

}
