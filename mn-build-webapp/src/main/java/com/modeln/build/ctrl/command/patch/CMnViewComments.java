/*
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.ctrl.command.patch; 

import com.modeln.build.common.data.product.CMnPatch;
import com.modeln.build.common.data.product.CMnPatchComment;
import com.modeln.build.ctrl.database.CMnPatchTable;
import com.modeln.build.ctrl.forms.IMnPatchForm;


import java.io.*;
import java.net.URL;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Vector;

import com.modeln.build.web.errors.ApplicationError;
import com.modeln.build.web.errors.ApplicationException;
import com.modeln.build.web.application.CommandResult;
import com.modeln.build.web.application.ProtectedCommand;
import com.modeln.build.web.application.WebApplication;
import com.modeln.build.web.database.RepositoryConnection;
import com.modeln.build.web.errors.ErrorMap;


/**
 * This command allows a user to add a comment to a service patch request. 
 * 
 * @author             Shawn Stafford
 */
public class CMnViewComments extends CMnBasePatchRequest {

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
                rc = app.getRepositoryConnection();
                ac = app.getAccountConnection();
                CMnPatchTable patchTable = CMnPatchTable.getInstance();
                app.debug("CMnViewPatchComments: obtained a connection to the build database");

                // Fall back to the request attributes in case the data was set by another command
                String patchId = (String) req.getParameter(IMnPatchForm.PATCH_ID_LABEL);
                if (patchId == null) {
                    patchId = (String) req.getAttribute(IMnPatchForm.PATCH_ID_LABEL);
                }

                if ((patchId != null) && (patchId.length() > 0)) {
                    CMnPatch patch = patchTable.getRequest(rc.getConnection(), patchId, true);
                    if (patch != null) {
                        // Populate the comments with user data from the authenication repository
                        Vector<CMnPatchComment> comments = patch.getCommentList();
                        if (comments != null) {
                            app.debug("CMnPatchRequest: returned " + comments.size() + " comments for this patch.");
                            getCommentUserData(ac.getConnection(), comments);
                        }

                        req.setAttribute(IMnPatchForm.PATCH_DATA, patch);
                    } else {
                        throw new ApplicationException(ErrorMap.APPLICATION_DISPLAY_FAILURE, "Failed to obtain patch data for patch ID " + patchId);
                    }
                }

                result.setDestination("patch/patch_comment.jsp");
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
        } else {
            app.debug("CMnPatchDelete: skipping execution due to pre-existing error: " + result.getError());
        }

        return result;
    }



}
