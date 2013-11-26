/*
 * Login.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.ctrl.command.patch;

import com.modeln.build.common.data.account.UserData;
import com.modeln.build.common.data.product.CMnPatch;
import com.modeln.build.common.enums.CMnServicePatch;
import com.modeln.build.common.tool.CMnPatchUtil;
import com.modeln.build.ctrl.database.CMnPatchTable;
import com.modeln.build.ctrl.forms.IMnPatchForm;
import com.modeln.build.jenkins.XmlApi;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import javax.servlet.*;
import javax.servlet.http.*;

import com.modeln.build.web.errors.ApplicationError;
import com.modeln.build.web.errors.ApplicationException;
import com.modeln.build.web.application.CommandResult;
import com.modeln.build.web.application.AdminCommand;
import com.modeln.build.web.application.WebApplication;
import com.modeln.build.web.database.RepositoryConnection;
import com.modeln.build.web.errors.ErrorMap;
import com.modeln.build.web.util.SessionUtility;

import org.w3c.dom.Document;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



/**
 * This command is used to update the status of the service patch request. 
 *
 * @author             Shawn Stafford
 */
public class CMnUpdateStatus extends AdminCommand {

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
            CMnPatch patch = null;

            RepositoryConnection rc = null;
            try {
                rc = app.getRepositoryConnection();
                CMnPatchTable patchTable = CMnPatchTable.getInstance();
                app.debug("CMnUpdateStatus: obtained a connection to the build database");

                // Get the login information from the session
                UserData user = SessionUtility.getLogin(req.getSession());

                // Fall back to the request attributes in case the data was set by another command
                String patchId = (String) req.getParameter(IMnPatchForm.PATCH_ID_LABEL);
                if (patchId == null) {
                    patchId = (String) req.getAttribute(IMnPatchForm.PATCH_ID_LABEL);
                }

                String newStatus = (String) req.getParameter(IMnPatchForm.PATCH_STATUS_LABEL);
                if (newStatus == null) {
                    newStatus = (String) req.getAttribute(IMnPatchForm.PATCH_STATUS_LABEL);
                }

                // Obtain the previous patch status so the command
                // can trigger the correct notifications and actions
                // based on the old and new status
                if ((patchId != null) && (patchId.length() > 0)) {
                    patch = patchTable.getRequest(rc.getConnection(), patchId, true);
                    if (patch != null) {
                        req.setAttribute(IMnPatchForm.PATCH_DATA, patch);
                    } else {
                        throw new ApplicationException(ErrorMap.APPLICATION_DISPLAY_FAILURE, 
                            "Failed to obtain patch data for patch ID " + patchId);
                    }
                }

                boolean updateOk = false;
                CMnServicePatch.RequestStatus status = CMnServicePatch.getRequestStatus(newStatus);
                if (status != null) {
                    updateOk = patchTable.updateRequestStatus(rc.getConnection(), patchId, user.getUid(), status);
                }

                // Forward the user to the correct page following the update
                if (updateOk) {
                    result = app.forwardToCommand(req, res, "/patch/CMnPatchRequest");
                } else {
                    result.setDestination("patch/patch_status.jsp");
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


            // Throw any exceptions once the database connections have been cleaned up
            if (exApp != null) {
                throw exApp;
            }

        }

        // Send the user to the patch information page
        if (result.getDestination() == null) {
            result = app.forwardToCommand(req, res, "/patch/CMnPatchRequest"); 
        }

        return result;
    }


}

