/*
 * Login.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.ctrl.command.patch; 

import com.modeln.build.common.data.product.CMnPatch;
import com.modeln.build.common.enums.CMnServicePatch;
import com.modeln.build.ctrl.database.CMnPatchTable;
import com.modeln.build.ctrl.forms.IMnPatchForm;
import com.modeln.testfw.reporting.search.CMnSearchCriteria;
import com.modeln.testfw.reporting.search.CMnSearchGroup;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Vector;

import com.modeln.build.common.data.account.UserData;
import com.modeln.build.web.errors.ApplicationError;
import com.modeln.build.web.errors.ApplicationException;
import com.modeln.build.web.application.CommandResult;
import com.modeln.build.web.application.ProtectedCommand;
import com.modeln.build.web.application.WebApplication;
import com.modeln.build.web.database.RepositoryConnection;
import com.modeln.build.web.errors.ErrorMap;
import com.modeln.build.web.util.SessionUtility;


/**
 * This command displays a list of service patches requested by
 * the current user. 
 * 
 * @author             Shawn Stafford
 */
public class CMnPatchQueue extends CMnBasePatchRequest {

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
            // Get the login information from the session
            UserData user = SessionUtility.getLogin(req.getSession());

            ApplicationException exApp = null;
            ApplicationError error = null;
            RepositoryConnection rc = null;
            RepositoryConnection ac = null;
            try {
                rc = app.getRepositoryConnection();
                ac = app.getAccountConnection();
                CMnPatchTable patchTable = CMnPatchTable.getInstance();


                // Query for patch that match the version string
                CMnSearchCriteria pendingCriteria = new CMnSearchCriteria(
                    CMnPatchTable.REQUEST_TABLE,
                    CMnPatchTable.REQUEST_STATUS,
                    CMnSearchCriteria.EQUAL_TO,
                    CMnServicePatch.RequestStatus.PENDING.toString());
                CMnSearchCriteria runningCriteria = new CMnSearchCriteria(
                    CMnPatchTable.REQUEST_TABLE,
                    CMnPatchTable.REQUEST_STATUS,
                    CMnSearchCriteria.EQUAL_TO,
                    CMnServicePatch.RequestStatus.RUNNING.toString());
                CMnSearchGroup searchGroup = new CMnSearchGroup(CMnSearchGroup.OR);
                searchGroup.add(pendingCriteria);
                searchGroup.add(runningCriteria);
                Vector<CMnPatch> patches = patchTable.getAllRequests(rc.getConnection(), searchGroup, 0, false);
                if ((patches != null) && (patches.size() > 0)) {
                    getPatchUserData(ac.getConnection(), patches);
                }
                req.setAttribute(IMnPatchForm.PATCH_LIST_DATA, patches);

                result.setDestination("patch/patch_queue.jsp");

            } catch (ApplicationException aex) {
                exApp = aex;
            } catch (Exception ex) {
                exApp = new ApplicationException(
                    ErrorMap.APPLICATION_DISPLAY_FAILURE,
                    "Failed to process command.");
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
