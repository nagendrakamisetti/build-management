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
 * The BaseCommand provides common command methods which allow the
 * WebApplication to perform specific tasks based upon the
 * user request. 
 * 
 * @version            $Revision: 1.1.1.1 $  
 * @author             Shawn Stafford
 */
public class BaseCommand extends Object {

    /** Obtain a reference to the common log just for convenience */
    protected SessionLog commonLog = null;


    /**
     * Return the name of the command.  This is useful for logging purposes
     * so the application can identify the command being executed.
     *
     * @return  Name of the command
     */
    public String getCommandName() {
        return getClass().getName();
    }



    /**
     * Verify that the user has a valid login session.
     *
     * @param   app     Application which called the command
     * @param   req     HttpServletRequest object
     * @param   res     HttpServletResponse object
     * @param   Application error if the user does not have a valid login, null otherwise
     */
    protected ApplicationError verifyUser(WebApplication app, HttpServletRequest req, HttpServletResponse res)
        throws ApplicationException
    {
        ApplicationError error = null;

        // Get the login information from the session
        UserData user = SessionUtility.getLogin(req.getSession());
        if (user != null) {
            // Check the ticket timestamp if available
            error = app.validateTicket(req);
        } else {
            error = app.getError(ErrorMap.NOT_LOGGED_ON);
        }

        return error;
    }



}
