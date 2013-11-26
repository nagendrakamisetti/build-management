/*
 * Login.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.ctrl.command.patch; 

import com.modeln.build.common.data.account.CMnAccount;
import com.modeln.build.common.data.product.CMnPatch;
import com.modeln.build.common.data.product.CMnPatchOwner;
import com.modeln.build.common.enums.CMnServicePatch;
import com.modeln.build.common.database.CMnCustomerTable;
import com.modeln.build.ctrl.database.CMnPatchTable;
import com.modeln.build.ctrl.forms.CMnPatchOwnerForm;
import com.modeln.build.ctrl.forms.IMnPatchForm;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import com.modeln.build.common.data.account.UserData;
import com.modeln.build.common.database.LoginTable;
import com.modeln.build.web.errors.ApplicationError;
import com.modeln.build.web.errors.ApplicationException;
import com.modeln.build.web.application.CommandResult;
import com.modeln.build.web.application.ProtectedCommand;
import com.modeln.build.web.application.WebApplication;
import com.modeln.build.web.database.RepositoryConnection;
import com.modeln.build.web.errors.ErrorMap;
import com.modeln.build.web.util.SessionUtility;


/**
 * This command creates and updates patch assignment information. 
 * 
 * @author             Shawn Stafford
 */
public class CMnPatchAssignment extends ProtectedCommand {

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
                CMnCustomerTable custTable = CMnCustomerTable.getInstance();

                // Fall back to the request attributes in case the data was set by another command
                String patchId = (String) req.getParameter(IMnPatchForm.PATCH_ID_LABEL);
                if (patchId == null) {
                    patchId = (String) req.getAttribute(IMnPatchForm.PATCH_ID_LABEL);
                }

                if ((patchId != null) && (patchId.length() > 0)) {
                    CMnPatch patch = patchTable.getRequest(rc.getConnection(), patchId, true);
                    if (patch != null) {
                        req.setAttribute(IMnPatchForm.PATCH_DATA, patch);
                    } else {
                        throw new ApplicationException(ErrorMap.APPLICATION_DISPLAY_FAILURE, "Failed to obtain patch data for patch ID " + patchId);
                    }

                    // Obtain the patch owner information from the database
                    CMnPatchOwner owner = patchTable.getPatchOwner(rc.getConnection(), patchId);
                    if (owner != null) {
                        req.setAttribute(CMnPatchOwnerForm.OWNER_DATA, owner);
                    }


                    // Obtain the patch owner information from the database
                    Vector<UserData> users = LoginTable.getInstance().getUsers(ac.getConnection());
                    if (users != null) {
                        Hashtable userhash = new Hashtable(users.size());
                        Enumeration userlist = users.elements();
                        while (userlist.hasMoreElements()) {
                            UserData currentUser = (UserData) userlist.nextElement();
                            if (currentUser.isAdmin()) {
                                String uid = currentUser.getUid();
                                StringBuffer name = new StringBuffer();
                                if (currentUser.getFirstName() != null) {
                                    name.append(currentUser.getFirstName());
                                }
                                if (currentUser.getMiddleName() != null) {
                                    name.append(" " + currentUser.getMiddleName());
                                }
                                if (currentUser.getLastName() != null) {
                                    name.append(" " + currentUser.getLastName());
                                }
                                userhash.put(uid, name.toString());
                            }
                        }
                        req.setAttribute(CMnPatchOwnerForm.USER_LIST, userhash);
                    }

                } // has patchId

                result.setDestination("patch/patch_assignment.jsp");

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
