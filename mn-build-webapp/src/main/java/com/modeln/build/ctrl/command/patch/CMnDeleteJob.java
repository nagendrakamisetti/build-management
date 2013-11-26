/*
 * Login.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.ctrl.command.patch;

import com.modeln.build.common.data.product.CMnPatch;
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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



/**
 * This command is used to delete a Jenkins job. 
 *
 * @author             Shawn Stafford
 */
public class CMnDeleteJob extends AdminCommand {

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
                app.debug("CMnDeleteJob: obtained a connection to the build database");

                // Fall back to the request attributes in case the data was set by another command
                String patchId = (String) req.getParameter(IMnPatchForm.PATCH_ID_LABEL);
                if (patchId == null) {
                    patchId = (String) req.getAttribute(IMnPatchForm.PATCH_ID_LABEL);
                }
                if ((patchId != null) && (patchId.length() > 0)) {
                    patch = patchTable.getRequest(rc.getConnection(), patchId, false);
                }

                // Delete the job from Jenkins 
                if (patch != null) {
                    app.debug("CMnDeleteJob: obtained data for patch ID " + patchId);
                    deleteJob(app, patch);
                } else {
                    app.debug("CMnDeleteJob: patch data is null for patch ID " + patchId);
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

        } else {
            app.debug("CMnDeleteJob: skipping execution due to pre-existing error: " + result.getError());
        }

        return result;
    }



    /**
     * Delete the Jenkins job. 
     *
     * @param   app     Application which called the command
     * @param   patch   Service patch information
     */
    private void deleteJob(WebApplication app, CMnPatch patch)
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
                    app.debug("CMnDeleteJob: Deleting the Jenkins job...");
                    int jobStatus = jenkins.deleteJob(jobname);
                    if (jobStatus == 200) {
                        app.debug("CMnDeleteJob: successfully deleted Jenkins job: " + jobname);
                    } else if (jobStatus == 404) {
                        app.debug("CMnDeleteJob: Jenkins job does not exist: " + jobname); 
                    } else {
                        app.debug("CMnDeleteJob: failed to delete jenkins job: status=" + jobStatus + ", url=" + jenkins.getJobDeleteUrl(jobname));
                        exApp = new ApplicationException(
                            ErrorMap.APPLICATION_DISPLAY_FAILURE, 
                            "Failed to delete Jenkins job " + jobname + " (HTTP Response Code " + jobStatus + ")");
                    }
                } catch (MalformedURLException mfex) {
                    app.debug("CMnDeleteJob: failed to parse Jenkins URL: " + mfex.getMessage());
                    exApp = new ApplicationException(
                        ErrorMap.APPLICATION_DISPLAY_FAILURE,
                        "Unable to parse the Jenkins URL: " + mfex.getMessage());
                    exApp.setStackTrace(mfex);
                } catch (IOException ioex) {
                    app.debug("CMnDeleteJob: failed to connect to Jenkins instance: " + ioex.getMessage());
                    exApp = new ApplicationException(
                        ErrorMap.APPLICATION_DISPLAY_FAILURE,
                        "Unable to connect to the Jenkins instance: " + ioex.getMessage());
                    exApp.setStackTrace(ioex);
                }
            } else if (url != null) {
                app.debug("CMnDeleteJob: skipping Jenkins call due to null patch data.");
            } else {
                app.debug("CMnDeleteJob: skipping Jenkins call due to null Jenkins URL.");
            }

            // Throw any exceptions once the database connections have been cleaned up
            if (exApp != null) {
                throw exApp;
            }

    }


}

