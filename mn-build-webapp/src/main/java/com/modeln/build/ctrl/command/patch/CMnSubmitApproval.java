/*
 * Login.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.ctrl.command.patch;

import com.modeln.build.common.data.product.CMnPatch;
import com.modeln.build.common.data.product.CMnPatchApproval;
import com.modeln.build.common.data.product.CMnPatchApprover;
import com.modeln.build.common.data.product.CMnPatchApproverGroup;
import com.modeln.build.common.enums.CMnServicePatch;
import com.modeln.build.ctrl.database.CMnPatchTable;
import com.modeln.build.ctrl.forms.IMnPatchForm;
import com.modeln.build.common.data.account.UserData;
import com.modeln.build.web.errors.ApplicationError;
import com.modeln.build.web.errors.ApplicationException;
import com.modeln.build.web.application.CommandResult;
import com.modeln.build.web.application.ProtectedCommand;
import com.modeln.build.web.application.WebApplication;
import com.modeln.build.web.database.RepositoryConnection;
import com.modeln.build.web.errors.ErrorMap;
import com.modeln.build.web.util.SessionUtility;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.Vector;


/**
 * This command processes a patch approval submission. 
 * 
 * @author             Shawn Stafford
 */
public class CMnSubmitApproval extends CMnBasePatchRequest {

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

                // Get the user information from the session
                HttpSession session = req.getSession();
                UserData user = SessionUtility.getLogin(session);

                // Parse the user input 
                String patchId = (String) req.getParameter(IMnPatchForm.PATCH_ID_LABEL);
                if (patchId == null) {
                    patchId = (String) req.getAttribute(IMnPatchForm.PATCH_ID_LABEL);
                }
                String status = (String) req.getParameter(IMnPatchForm.APPROVAL_STATUS);
                if (status == null) {
                    status = (String) req.getAttribute(IMnPatchForm.APPROVAL_STATUS);
                }
                String comment = (String) req.getParameter(IMnPatchForm.APPROVAL_COMMENT);
                if (comment == null) {
                    comment = (String) req.getAttribute(IMnPatchForm.APPROVAL_COMMENT);
                }

                // Determine if the user belongs to the appropriate approval group
                CMnPatch patch = null;
                if ((patchId != null) && (patchId.length() > 0)) {

                    patch = patchTable.getRequest(rc.getConnection(), patchId, /*deep*/true);
                    if (patch != null) {
                        getPatchUserData(ac.getConnection(), patch);
                    }
                    app.debug("CMnPatchApproval: obtained data for patch ID " + patchId);
                    boolean updateOk = false;

                    Vector<CMnPatchApproval> approvals = null;
                    Vector<CMnPatchApprover> approvers = null;
                    Vector<CMnPatchApproverGroup> groups = null;

                    if ((patch != null) && (patch.getBuild() != null) && (patch.getBuild().getBuildVersion() != null)) {
                        boolean canApprove = false;
                        String buildString = patch.getBuild().getBuildVersion(); 

                        approvals = patchTable.getApprovals(rc.getConnection(), patchId, patch.getStatus());
                        if ((approvals != null) && (approvals.size() > 0)) {
                            getApprovalUserData(ac.getConnection(), approvals);
                        }

                        approvers = patchTable.getApproversForBuild(rc.getConnection(), buildString, patch.getStatus());
                        if ((approvers != null) && (approvers.size() > 0)) {
                            getApproverUserData(ac.getConnection(), approvers);
                        }

                        groups = getApproverGroupsForBuild(app, patch); 
                        if ((groups != null) && (groups.size() > 0)) {
                            app.debug("CMnPatchApproval: adding " + groups.size() + " approval groups to the session.");
                            Iterator iter = groups.iterator();
                            while (iter.hasNext()) {
                                CMnPatchApproverGroup approverGroup = (CMnPatchApproverGroup) iter.next();
                                if (user.isPartOf(approverGroup.getGroup())) {
                                    app.debug("CMnPatchApproval: user is part of approval group " + approverGroup.getGroup().getGid());
                                    canApprove = true;
                                }
                            }
                        } else {
                            app.debug("CMnPatchApproval: no approval groups found for " + buildString);
                        }

                        // Submit the approval to the database
                        if (canApprove) {
                            app.debug("CMnPatchApproval: attempting to add approval to the database");
                            CMnPatchApproval approval = new CMnPatchApproval();
                            approval.setUser(user);
                            approval.setStatus(status);
                            approval.setComment(comment);
                            String approvalId = patchTable.addApproval(rc.getConnection(), patch, approval);
                            if (approvalId == null) {
                                ApplicationException badApprovalEx = new ApplicationException(
                                    ErrorMap.APPLICATION_DISPLAY_FAILURE,
                                    "Failed to add the approval to the database.");
                            } else {
                                // Add the approval to the in-memory list of approvals
                                if (approvals == null) {
                                    approvals = new Vector<CMnPatchApproval>(); 
                                }
                                approvals.add(approval);

                                if ((status != null) && status.equalsIgnoreCase("rejected")) {
                                    // Update the patch status to indicate a change in state
                                    updateOk = patchTable.updateRequestStatus(rc.getConnection(), patchId, user.getUid(), CMnServicePatch.RequestStatus.REJECTED);
                                    app.debug("CMnPatchApproval: updateOk" + updateOk);
                                    //call notify users
                                    sendEmailViaNotifyUsersAPI(updateOk, app, req, patch, error);
                                }
                            }
                        }
                    }
                    app.debug("CMnPatchApproval: patchStatus: " + patch.getStatus());

                    //if status is rejected and the last update of patch table is true
                    //then we need not call the below code to set the status to running.
                    if(!updateOk && ((status != null) && !status.equalsIgnoreCase("rejected"))){
                        app.debug("Reached this block because status is : " + status );

                        // Initiate the build if all approval requirements have been satisfied
                        boolean approved = patchTable.isApproved(rc.getConnection(), approvals, groups, patchId);

                        updateOk = false;
                        app.debug("CMnPatchApproval: Determining whether to trigger build: approved = " + approved + ", status=" + patch.getStatus());
                        if (approved && (patch.getStatus() == CMnServicePatch.RequestStatus.APPROVAL)) {
                            // Send the user to command which creates the Jenkins job
                            app.debug("CMnPatchApproval: Initiating the Jenkins job to perform the service patch.");
                            /*
                            try {
                                result = app.forwardToCommand(req, res, "/patch/CMnRunJob");
                                // TODO set an APPROVED status
                                patch.setStatus(CMnServicePatch.RequestStatus.PENDING);
                            } catch (ApplicationException appex) {
                                // TODO set an ERROR status
                                patch.setStatus(CMnServicePatch.RequestStatus.PENDING);
                            }
                            */
                            patch.setStatus(CMnServicePatch.RequestStatus.PENDING);
                            updateOk = patchTable.updateRequestStatus(rc.getConnection(), patchId, user.getUid(), patch.getStatus());
                            //call notify users
                            sendEmailViaNotifyUsersAPI(updateOk, app, req, patch, error);
                        } else {
                            if (patch.getStatus() == CMnServicePatch.RequestStatus.COMPLETE) {
                                patch.setStatus(CMnServicePatch.RequestStatus.RELEASE);
                                updateOk = patchTable.updateRequestStatus(rc.getConnection(), patchId, user.getUid(), patch.getStatus());
                                //call notify users
                                sendEmailViaNotifyUsersAPI(updateOk, app, req, patch, error);
                            }
                        }
                    }
                    // Display the updated patch information
                    result = app.forwardToCommand(req, res, "/patch/CMnPatchRequest");

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

    private void sendEmailViaNotifyUsersAPI(boolean updateOk, WebApplication app, HttpServletRequest req, CMnPatch patch, ApplicationError error ) {
        if (updateOk) {
            try {
                CMnEmailNotification.notifyUsers(app, req, patch);
            } catch (ApplicationException apex) {
                ApplicationError mailError = app.getError(apex);
                if (error == null) {
                    error = mailError;
                }
                app.log(mailError);
            }
        }
    }

}
