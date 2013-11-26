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
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;



/**
 * This command is used to kick off the execution of a Jenkins job. 
 *
 * @author             Shawn Stafford
 */
public class CMnRunJob extends ProtectedCommand {

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
                app.debug("CMnRunJob: obtained a connection to the build database");

                // Get the login information from the session
                UserData user = SessionUtility.getLogin(req.getSession());

                // Fall back to the request attributes in case the data was set by another command
                String patchId = (String) req.getParameter(IMnPatchForm.PATCH_ID_LABEL);
                if (patchId == null) {
                    patchId = (String) req.getAttribute(IMnPatchForm.PATCH_ID_LABEL);
                }
                if ((patchId != null) && (patchId.length() > 0)) {
                    patch = patchTable.getRequest(rc.getConnection(), patchId, false);
                    app.debug("CMnRunJob: obtained data for patch ID " + patchId);
                }

                // Start the build process
                if (patch != null) {
                    // Update the patch status to indicate a change in state
                    boolean updateOk = patchTable.updateRequestStatus(rc.getConnection(), patchId, user.getUid(), CMnServicePatch.RequestStatus.PENDING);

                    //call notify users
                    sendEmailViaNotifyUsersAPI(updateOk, app, req, patch, error);

                    // Schedule the build job
                    runJob(app, patch);
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

        //result = app.forwardToCommand(req, res, "/patch/CMnPatchRequest");

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


    /**
     * Execute the Jenkins build. 
     *
     * @param   app     Application which called the command
     * @param   patch   Service patch information
     */
    private void runJob(WebApplication app, CMnPatch patch)
        throws ApplicationException
    {
            ApplicationException exApp = null;
            ApplicationError error = null;


            // Send the job request to Jenkins
            String url = app.getConfigValue("patch.jenkins.url");
            app.debug("Loaded Jenkins URL from config: " + url);
            if ((patch != null) && (url != null)) {
                // Collect information about the patch
                String jobname = CMnPatchUtil.getJobName(patch);

                try {
                    // Construct a Jenkins API instance
                    URL jenkinsUrl = new URL(url);
                    XmlApi jenkins = new XmlApi(jenkinsUrl);
                    app.debug("CMnRunJob: Invoking the Jenkins job...");
                    int jobStatus = jenkins.runJob(jobname);

                    if (jobStatus != 200) {
                        app.debug("CMnRunJob: job failure status: " + jobStatus);
                        exApp = new ApplicationException(
                            ErrorMap.APPLICATION_DISPLAY_FAILURE, 
                            "Failed to run Jenkins job " + jobname + "(HTTP Response Code " + jobStatus + ")");
                    }

                } catch (MalformedURLException mfex) {
                    app.debug("CMnRunJob: failed to parse Jenkins URL: " + mfex.getMessage());
                    exApp = new ApplicationException(
                        ErrorMap.APPLICATION_DISPLAY_FAILURE,
                        "Unable to parse the Jenkins URL: " + mfex.getMessage());
                    exApp.setStackTrace(mfex);
                } catch (IOException ioex) {
                    app.debug("CMnRunJob: failed to connect to Jenkins instance: " + ioex.getMessage());
                    exApp = new ApplicationException(
                        ErrorMap.APPLICATION_DISPLAY_FAILURE,
                        "Unable to connect to the Jenkins instance: " + ioex.getMessage());
                    exApp.setStackTrace(ioex);
                }
            } else if (url != null) {
                app.debug("CMnRunJob: skipping Jenkins call due to null patch data.");
            } else {
                app.debug("CMnRunJob: skipping Jenkins call due to null Jenkins URL.");
            }

            // Throw any exceptions once the database connections have been cleaned up
            if (exApp != null) {
                throw exApp;
            }

    }


}

