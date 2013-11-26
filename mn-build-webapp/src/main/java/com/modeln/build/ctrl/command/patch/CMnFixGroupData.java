/*
 * Login.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.ctrl.command.patch; 

import com.modeln.build.common.data.product.CMnPatchGroup;
import com.modeln.build.common.database.CMnCustomerTable;
import com.modeln.build.ctrl.database.CMnPatchTable;
import com.modeln.build.ctrl.forms.CMnFixGroupForm;
import com.modeln.build.ctrl.forms.IMnPatchForm;


import java.io.*;
import java.net.URL;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Vector;

import com.modeln.build.web.errors.ApplicationError;
import com.modeln.build.web.errors.ApplicationException;
import com.modeln.build.web.application.CommandResult;
import com.modeln.build.web.application.ProtectedCommand;
import com.modeln.build.web.application.WebApplication;
import com.modeln.build.web.database.RepositoryConnection;
import com.modeln.build.web.errors.ErrorMap;


/**
 * This command displays information about a single fix group. 
 * 
 * @author             Shawn Stafford
 */
public class CMnFixGroupData extends ProtectedCommand {

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
                rc = app.getRepositoryConnection();
                CMnPatchTable patchTable = CMnPatchTable.getInstance();

                // Parse the form values if available
                CMnFixGroupForm groupForm = new CMnFixGroupForm(null, null, null);
                groupForm.setValues(req);
                CMnPatchGroup group = groupForm.getValues();

                // Update or query the fix group data depending on the user action 
                String editAction = (String) req.getParameter(IMnPatchForm.ACTION_EDIT);
                String addAction = (String) req.getParameter(IMnPatchForm.ACTION_ADD);
                if (editAction != null) {
                    boolean success = patchTable.updateFixGroup(rc.getConnection(), group);
                    if (success) {
                        // Successfully added new group, show list of groups
                        result = app.forwardToCommand(req, res, "/patch/CMnFixGroupList");
                    } else {
                        // Failed to add new group, display edit form again
                        req.setAttribute(IMnPatchForm.FIX_GROUP_DATA, group);
                        result.setDestination("patch/fix_group.jsp");
                    }
                } else if (addAction != null) {
                    String groupId = patchTable.addFixGroup(rc.getConnection(), group);
                    if ((groupId != null) && (groupId.length() > 0)) {
                        // Successfully added new group, show list of groups
                        result = app.forwardToCommand(req, res, "/patch/CMnFixGroupList");
                    } else {
                        // Failed to add new group, display edit form again
                        req.setAttribute(IMnPatchForm.FIX_GROUP_DATA, group);
                        result.setDestination("patch/fix_group.jsp");
                    }
                } else {
                    // If no action was submitted, display information to the user
                    String groupId = (String) req.getParameter(IMnPatchForm.GROUP_ID_LABEL);
                    if ((groupId != null) && (groupId.length() > 0)) {
                        group = patchTable.getFixGroup(rc.getConnection(), groupId);
                        req.setAttribute(IMnPatchForm.FIX_GROUP_DATA, group); 
                    }

                    result.setDestination("patch/fix_group.jsp");
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
