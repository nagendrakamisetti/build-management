/*
 * Login.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.ctrl.command.environment; 

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Vector;

import com.modeln.build.web.errors.ApplicationError;
import com.modeln.build.web.errors.ApplicationException;
import com.modeln.build.web.application.CommandResult;
import com.modeln.build.web.application.TrustedHostCommand;
import com.modeln.build.web.application.WebApplication;
import com.modeln.build.web.database.RepositoryConnection;
import com.modeln.build.web.errors.ErrorMap;

import com.modeln.testfw.reporting.CMnDualBootHostsTable;
import com.modeln.testfw.reporting.CMnDbHostActionData;


/**
 * This command displays a list of all dual boot hosts. 
 * 
 * @author             Shawn Stafford
 */
public class CMnDualBootList extends TrustedHostCommand {

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
            try {
                // Perform the query
                rc = app.getRepositoryConnection();
                Vector list = CMnDualBootHostsTable.getAllHosts(rc.getConnection());
                if ((list != null) && (list.size() == 1)) {
                    CMnDbHostActionData hostData = (CMnDbHostActionData) list.get(0);
                    //req.setAttribute("HOST_ID", Integer.toString(hostData.getId()));
                    result = app.forwardToCommand(req, res, "/environment/CMnDualBootHostData");
                } else {
                    req.setAttribute("HOST_LIST", list);
                    result.setDestination("environment/dualboot_list.jsp");
                }
            } catch (ApplicationException aex) {
                exApp = aex;
            } catch (Exception ex) {
                exApp = new ApplicationException(
                    ErrorMap.APPLICATION_DISPLAY_FAILURE,
                    "Failed to process command.");
                exApp.setStackTrace(ex);
            } finally {
                app.releaseRepositoryConnection(rc);

                // Throw any exceptions once the database connections have been cleaned up
                if (exApp != null) {
                    throw exApp;
                }
            }
        }

        return result;
    }


}
