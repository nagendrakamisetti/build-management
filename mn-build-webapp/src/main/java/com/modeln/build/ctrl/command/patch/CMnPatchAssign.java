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
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;

import com.modeln.build.common.data.account.UserData;
import com.modeln.build.common.database.LoginTable;
import com.modeln.build.web.errors.ApplicationError;
import com.modeln.build.web.errors.ApplicationException;
import com.modeln.build.web.application.CommandResult;
import com.modeln.build.web.application.AdminCommand;
import com.modeln.build.web.application.WebApplication;
import com.modeln.build.web.database.RepositoryConnection;
import com.modeln.build.web.errors.ErrorMap;
import com.modeln.build.web.tags.DateTag;
import com.modeln.build.web.util.SessionUtility;


/**
 * This command creates and updates patch assignment information. 
 * 
 * @author             Shawn Stafford
 */
public class CMnPatchAssign extends AdminCommand {

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
                LoginTable loginTable = LoginTable.getInstance();
                CMnPatchTable patchTable = CMnPatchTable.getInstance();

                //
                // Parse the user input parameters
                //  
                String patchId = (String) req.getParameter(IMnPatchForm.PATCH_ID_LABEL);
                if (patchId == null) {
                    patchId = (String) req.getAttribute(IMnPatchForm.PATCH_ID_LABEL);
                }

                String userId = (String) req.getParameter(CMnPatchOwnerForm.USER_LABEL);
                if (userId == null) {
                    userId = (String) req.getAttribute(CMnPatchOwnerForm.USER_LABEL);
                }

                String priority = (String) req.getParameter(CMnPatchOwnerForm.PRIORITY_LABEL);
                if (priority == null) {
                    priority = (String) req.getAttribute(CMnPatchOwnerForm.PRIORITY_LABEL);
                }

                String comment = (String) req.getParameter(CMnPatchOwnerForm.COMMENT_LABEL);
                if (comment == null) {
                    comment = (String) req.getAttribute(CMnPatchOwnerForm.COMMENT_LABEL);
                }

                //
                // Parse the assignment dates
                // 
                DateTag startTag = new DateTag(CMnPatchOwnerForm.START_DATE_LABEL);
                Date startDate = startTag.parse(req);

                DateTag endTag = new DateTag(CMnPatchOwnerForm.END_DATE_LABEL);
                Date endDate = endTag.parse(req);

                DateTag deadlineTag = new DateTag(CMnPatchOwnerForm.DEADLINE_LABEL);
                Date deadline = deadlineTag.parse(req);


                CMnPatch patch = null;
                if ((patchId != null) && (patchId.length() > 0) && (userId != null)) {
                    // Obtain the patch information from the database
                    patch = patchTable.getRequest(rc.getConnection(), patchId, true);
                    if (patch != null) {
                        app.debug("CMnPatchRequest: obtained data for patch ID " + patchId);
                    } else {
                        throw new ApplicationException(ErrorMap.APPLICATION_DISPLAY_FAILURE, "Failed to obtain patch data for patch ID " + patchId);
                    }

                    // Query the database to see if an ownership record already exists
                    CMnPatchOwner existingData = patchTable.getPatchOwner(rc.getConnection(), patchId);
                    if ((existingData != null) && (existingData.getUser() != null) && (existingData.getUser().getUid() != null)) {
                        UserData existingUser = loginTable.getUserByUid(ac.getConnection(), existingData.getUser().getUid()); 
                        if (existingUser != null) {
                            existingData.setUser(existingUser);
                        }
                    }

                    // Query the login table for user contact information
                    UserData ownerInfo = loginTable.getUserByUid(ac.getConnection(), userId); 
 
                    // Construct a data object containing the user input
                    CMnPatchOwner owner = new CMnPatchOwner();
                    owner.setPatchId(Integer.parseInt(patchId));
                    owner.setUser(ownerInfo);
                    owner.setPriority(priority);
                    owner.setComment(comment);
                    owner.setStartDate(startDate);
                    owner.setEndDate(endDate);
                    owner.setDeadline(deadline);

                    // Update the data in the database
                    if (existingData != null) {
                        patchTable.updatePatchOwner(rc.getConnection(), owner);
                    } else {
                        patchTable.addPatchOwner(rc.getConnection(), owner);
                    }
                    req.setAttribute(CMnPatchOwnerForm.OWNER_DATA, owner);

                    // Send e-mail notification when the owner changes
                    notifyOwner(app, req, res, patch, existingData, owner); 

                    // Send the user to the patch summary page
                    result = app.forwardToCommand(req, res, "/patch/CMnPatchRequest");
                } else {
                    result = app.forwardToCommand(req, res, "/patch/CMnPatchAssignment");
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
                app.releaseRepositoryConnection(ac);

                // Throw any exceptions once the database connections have been cleaned up
                if (exApp != null) {
                    throw exApp;
                }
            }
        }

        return result;
    }



    /**
     * Notify the patch owner when ownership changes.
     */
    private void notifyOwner(
            WebApplication app, 
            HttpServletRequest req, 
            HttpServletResponse res, 
            CMnPatch patch, 
            CMnPatchOwner oldOwner, 
            CMnPatchOwner newOwner) 
        throws ApplicationException
    {
        boolean sendEmail = false;
        if ((oldOwner != null) && (newOwner != null)) {
            UserData oldUser = oldOwner.getUser();
            UserData newUser = newOwner.getUser();

            // Send email if the owner has changed
            if (oldUser.getUid() != newUser.getUid()) {
                sendEmail = true;
            } else {
                app.debug("SSDEBUG: notifyOwner - old (" + oldUser.getUid() + ") and new (" + newUser.getUid() + ") owner match");
            }

            // Send email if the deadline has changed
            // ???
        } else if (newOwner != null) {
            // Send email if this is the first time an owner has been identified
            sendEmail = true;
        } else {
            app.debug("SSDEBUG: notifyOwner - both old and new owner are null");
        }

        // Send the email notification to the owner
        if (sendEmail) {
            CMnEmailNotification.notifyOwner(app, req, patch, oldOwner, newOwner);
        } else {
            app.debug("SSDEBUG: notifyOwner - skipping e-mail notification of patch owner.");
        }
    }

}
