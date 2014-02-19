/*
 * Login.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.ctrl.command.patch; 

import com.modeln.build.common.database.CMnCustomerTable;
import com.modeln.build.ctrl.database.CMnPatchTable;
import com.modeln.build.ctrl.forms.CMnPatchBugForm;
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
 * This command displays a list of all service patches. 
 * 
 * @author             Shawn Stafford
 */
public class CMnPatchBugs extends CMnBasePatchRequest {

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
                // Collect the query results from the patch list page
                CMnPatchBugForm form = new CMnPatchBugForm(new URL("http://localhost/CMnBuildList"), new URL("http://localhost/images"), new Vector());
                form.setValues(req);

                rc = app.getRepositoryConnection();
                ac = app.getAccountConnection();

                CMnPatchTable patchTable = CMnPatchTable.getInstance();
                CMnCustomerTable custTable = CMnCustomerTable.getInstance();

                // Query the database for the list of patches that contain the fixes
                Vector patches = 
                    patchTable.getAllRequestsByFix(
                        rc.getConnection(), 
                        form.getFixes(),      // List of fixes provided by the user 
                        form.getValues(),     // Additional search criteria 
                        form.getMaxRows(),    // Max number of rows to return 
                        true                  // Deep query of sub-object data
                    );

                int patchCount = 0;
                if ((patches != null) && (patches.size() > 0)) {
                    getPatchUserData(ac.getConnection(), patches);
                    app.debug("Returned " + patches.size() + " results from the patch table.");
                } else {
                    app.debug("Returned no results from the patch table.");
                }
                req.setAttribute(IMnPatchForm.PATCH_LIST_DATA, patches); 

                Vector customers = custTable.getAllCustomers(rc.getConnection());
                if (customers != null) {
                    app.debug("Returned " + customers.size() + " results from the customer table.");
                } else {
                    app.debug("Returned no results from the customer table.");
                }
                req.setAttribute(IMnPatchForm.CUSTOMER_LIST_DATA, customers);


                result.setDestination("patch/patch_bugs.jsp");

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
