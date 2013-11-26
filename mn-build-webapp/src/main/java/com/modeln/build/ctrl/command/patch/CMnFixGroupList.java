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
import com.modeln.build.ctrl.forms.CMnFixGroupListForm;
import com.modeln.build.ctrl.forms.CMnPatchListForm;
import com.modeln.build.ctrl.forms.IMnPatchForm;
import com.modeln.testfw.reporting.search.CMnSearchGroup;


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
 * This command displays a list of all service patch fix groups. 
 * 
 * @author             Shawn Stafford
 */
public class CMnFixGroupList extends ProtectedCommand {

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

                // Parse the search criteria
                CMnFixGroupListForm form = new CMnFixGroupListForm(null, null, null);
                form.setValues(req);

                Vector<CMnPatchGroup> groups = patchTable.getFixGroups(rc.getConnection(), form.getValues(), form.getMaxRows(), form.getBug());
                if ((groups != null) && (groups.size() > 0)) {
                    app.debug("Adding " + groups.size() + " fix groups to the session."); 
                    req.setAttribute(IMnPatchForm.FIX_GROUP_LIST_DATA, groups); 
                } else {
                    app.debug("No fix groups found in the database.");
                }

                result.setDestination("patch/fix_group_list.jsp");

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
