/*
 * Login.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.ctrl.command; 

import com.modeln.build.ctrl.forms.IMnUserForm;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Vector;

import com.modeln.build.common.database.LoginTable;
import com.modeln.build.web.errors.ApplicationError;
import com.modeln.build.web.errors.ApplicationException;
import com.modeln.build.web.application.CommandResult;
import com.modeln.build.web.application.AdminCommand;
import com.modeln.build.web.application.WebApplication;
import com.modeln.build.web.database.RepositoryConnection;
import com.modeln.build.web.errors.ErrorMap;


/**
 * The user command is used to display a list of users. 
 * 
 * @version            $Revision: 1.1.1.1 $  
 * @author             Shawn Stafford
 */
public class CMnUserDelete extends AdminCommand {

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

                String uid = (String) req.getParameter(IMnUserForm.USER_ID_LABEL);
                if (uid == null) {
                    uid = (String) req.getAttribute(IMnUserForm.USER_ID_LABEL);
                }

                // Delete the specified user
                boolean success = false;
                if (uid != null) {
                    success = lt.deleteUser(ac.getConnection(), uid);
                }

                if (success) {
                    result = app.forwardToCommand(req, res, "/CMnUserList");
                } else {
                    result.setError(app.getError(ErrorMap.DATABASE_TRANSACTION_FAILURE));
                    result.setDestination("error.jsp");
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
