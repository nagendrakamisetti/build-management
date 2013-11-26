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
import com.modeln.build.common.database.CMnCustomerTable;
import com.modeln.build.common.enums.CMnServicePatch;
import com.modeln.build.ctrl.CMnControlApp;
import com.modeln.build.ctrl.database.CMnPatchTable;
import com.modeln.build.ctrl.forms.CMnBaseForm;
import com.modeln.build.ctrl.forms.CMnPatchOwnerForm;
import com.modeln.build.ctrl.forms.IMnPatchForm;
import com.modeln.testfw.reporting.CMnBuildTable;
import com.modeln.testfw.reporting.CMnDbBuildData;
import com.modeln.testfw.reporting.CMnDbFeatureOwnerData;
import com.modeln.testfw.reporting.CMnFeatureOwnerTable;


import java.io.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
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
 * This command creates sends an e-mail notification to the
 * technical owners to request that they review the build results. 
 * 
 * @author             Shawn Stafford
 */
public class CMnUpdateAndNotify extends AdminCommand {

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
            RepositoryConnection pc = null;
            try {
                rc = app.getRepositoryConnection();
                pc = ((CMnControlApp) app).getPatchRepositoryConnection();
                ac = app.getAccountConnection();
                LoginTable loginTable = LoginTable.getInstance();
                CMnPatchTable patchTable = CMnPatchTable.getInstance();
                CMnBuildTable buildTable = CMnBuildTable.getInstance();

                //
                // Parse the user input parameters
                //  
                String patchId = (String) req.getParameter(IMnPatchForm.PATCH_ID_LABEL);
                if (patchId == null) {
                    patchId = (String) req.getAttribute(IMnPatchForm.PATCH_ID_LABEL);
                }

                CMnPatch patch = null;
                if ((patchId != null) && (patchId.length() > 0)) {
                    // Obtain the patch information from the database
                    patch = patchTable.getRequest(rc.getConnection(), patchId, true);
                    if (patch != null) {
                        app.debug("CMnNotifyReviewers: obtained data for patch ID " + patchId);

                        // Populate the patch requester information
                        UserData ruser = patch.getRequestor();
                        UserData existingUser = loginTable.getUserByUid(ac.getConnection(), ruser.getUid());
                        if (existingUser != null) {
                            patch.setRequestor(existingUser);
                        }

                        // Populate the service patch build information from the patch repository
                        if (patch.getPatchBuild() != null) {
                            String patchBuildId = Integer.toString(patch.getPatchBuild().getId());
                            app.debug("CMnUpdateAndNotify: Querying the patch repository for Build ID " + patchBuildId);
                            CMnDbBuildData patchBuild = buildTable.getBuild(pc.getConnection(), patchBuildId);
                            patch.setPatchBuild(patchBuild);
                        } else {
                            app.debug("CMnUpdateAndNotify: No patch build associated with the patch request.");
                        }

                        // Update the service patch status if necessary
                        if (patch.getStatus() == CMnServicePatch.RequestStatus.BUILT) {
                            patch.setStatus(CMnServicePatch.RequestStatus.COMPLETE);
                            boolean updateOk = patchTable.updateRequestStatus(rc.getConnection(), patchId, user.getUid(), patch.getStatus());

                            // Send e-mail notification to the reviewers 
                            Vector areas = CMnFeatureOwnerTable.getAllAreas(pc.getConnection());
                            Vector<InternetAddress> reviewers = getRecipientList(areas);
                            CMnEmailNotification.notifyReviewers(app, req, patch, reviewers);

                        } else if (patch.getStatus() == CMnServicePatch.RequestStatus.COMPLETE) {
                            patch.setStatus(CMnServicePatch.RequestStatus.RELEASE);
                            boolean updateOk = patchTable.updateRequestStatus(rc.getConnection(), patchId, user.getUid(), patch.getStatus());

                            // Send e-mail notification to release build to requester
                            CMnEmailNotification.notifyOfRelease(app, req, patch);
                        }

                        // Send the user to the patch summary page
                        result = app.forwardToCommand(req, res, "/patch/CMnPatch");
                    } else {
                        throw new ApplicationException(ErrorMap.APPLICATION_DISPLAY_FAILURE, "Failed to obtain patch data for patch ID " + patchId);
                    }

                    // Send the user to the patch summary page
                    result = app.forwardToCommand(req, res, "/patch/CMnPatchRequest");
                } else {
                    result = app.forwardToCommand(req, res, "/patch/CMnPatch");
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
     * Get a list of e-mail addresses associated with the feature areas.
     *
     * @param  areas    List of feature owners
     * @return List of email addresses
     */
    private Vector<InternetAddress> getRecipientList(Vector<CMnDbFeatureOwnerData> areas) {
        // Collect the unique list of e-mail addresses
        HashSet set = new HashSet();
        if ((areas != null) && (areas.size() > 0)) {
            Enumeration areaList = areas.elements();
            while (areaList.hasMoreElements()) {
                CMnDbFeatureOwnerData area = (CMnDbFeatureOwnerData) areaList.nextElement();
                if (area.getEmailList() != null) {
                    Iterator iter = area.getEmailList().iterator();
                    while (iter.hasNext()) {
                        String addr = (String) iter.next();
                        // Make sure the address is valid
                        if ((addr != null) && (addr.trim().length() > 0)) {
                            set.add(addr.trim()); 
                        }
                    }
                }
            }
        }

        // Convert the address list to e-mail addresses
        Vector<InternetAddress> list = new Vector<InternetAddress>();
        Iterator iterSet = set.iterator();
        while (iterSet.hasNext()) {
            try {
                list.add(new InternetAddress((String) iterSet.next()));
            } catch (AddressException aex) {
            }
        } 
        return list;
    }

}
