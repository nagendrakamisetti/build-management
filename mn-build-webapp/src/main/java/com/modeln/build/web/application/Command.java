/*
 * Command.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.web.application;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

import com.modeln.build.web.errors.*;
import com.modeln.build.web.errors.*;

/**
 * The Command interface specifies methods which allow the
 * WebApplication to perform specific tasks based upon the
 * user request.
 * 
 * @version            $Revision: 1.1.1.1 $  
 * @author             Shawn Stafford
 */
public interface Command {

    /**
     * Perform any actions which must be performed before the command
     * can be executed.
     *
     * @param   app     Application which called the command
     * @param   req     HttpServletRequest object
     * @param   res     HttpServletResponse object
     */
    public CommandResult prepare(WebApplication app, HttpServletRequest req, HttpServletResponse res) 
        throws ApplicationException;

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
        throws ApplicationException;

    /**
     * Perform any actions which must be performed after the command
     * has been executed.
     *
     * @param   app     Application which called the command
     * @param   req     HttpServletRequest object
     * @param   res     HttpServletResponse object
     * @param   cmdInfo Command response which determines final actions
     */
    public void finalize(WebApplication app, HttpServletRequest req, HttpServletResponse res, CommandResult cmdInfo) 
        throws ApplicationException;

    /**
     * Return the name of the command.  This is useful for logging purposes
     * so the application can identify the command being executed.
     *
     * @return  Name of the command
     */
    public String getCommandName();

}
