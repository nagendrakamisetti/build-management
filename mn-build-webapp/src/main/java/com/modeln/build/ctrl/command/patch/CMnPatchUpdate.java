/*
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.ctrl.command.patch; 

import com.modeln.build.common.data.product.CMnPatch;
import com.modeln.build.ctrl.CMnControlApp;
import com.modeln.build.ctrl.database.CMnPatchTable;
import com.modeln.build.ctrl.forms.CMnBaseForm;
import com.modeln.build.ctrl.forms.IMnPatchForm;
import com.modeln.testfw.reporting.CMnBuildTable;
import com.modeln.testfw.reporting.CMnDbBuildData;


import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.*;
import java.net.URL;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.Connection;
import java.sql.SQLException;

import com.modeln.build.web.errors.ApplicationError;
import com.modeln.build.web.errors.ApplicationException;
import com.modeln.build.web.application.CommandResult;
import com.modeln.build.web.application.AdminCommand;
import com.modeln.build.web.application.WebApplication;
import com.modeln.build.web.database.RepositoryConnection;
import com.modeln.build.web.errors.ErrorMap;


/**
 * This command allows a user to update a service patch request. 
 * 
 * @author             Shawn Stafford
 */
public class CMnPatchUpdate extends AdminCommand {

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
            RepositoryConnection pc = null;
            try {
                rc = app.getRepositoryConnection();
                pc = ((CMnControlApp) app).getPatchRepositoryConnection();
                CMnBuildTable buildTable = CMnBuildTable.getInstance();
                CMnPatchTable patchTable = CMnPatchTable.getInstance();
                app.debug("CMnPatchUpdate: obtained a connection to the build database");

                // Fall back to the request attributes in case the data was set by another command
                String patchId = (String) req.getParameter(IMnPatchForm.PATCH_ID_LABEL);
                if (patchId == null) {
                    patchId = (String) req.getAttribute(IMnPatchForm.PATCH_ID_LABEL);
                }


                // Obtain the previous patch data and update with user input 
                CMnPatch patch = null;
                if ((patchId != null) && (patchId.length() > 0)) {
                    patch = patchTable.getRequest(rc.getConnection(), patchId, true);
                    if (patch != null) {
                        boolean refreshPatchBuildInfo = false;

                        // Record the original patch build ID
                        int oldId = 0;
                        if (patch.getPatchBuild() != null) {
                            oldId = patch.getPatchBuild().getId();
                        }

                        // Update the patch information with form input values
                        update(app, req, patch);

                        // Record the new patch build ID
                        int newId = 0;
                        if (patch.getPatchBuild() != null) {
                            newId = patch.getPatchBuild().getId();
                        }

                        // Refresh the patch build information if necessary
                        if ((newId > 0) && (newId != oldId)) {
                            app.debug("CMnPatchUpdate: Querying the patch repository for Build ID " + newId);
                            CMnDbBuildData patchBuild = buildTable.getBuild(pc.getConnection(), Integer.toString(newId));
                            if (patchBuild != null) {
                                patch.setPatchBuild(patchBuild);
                            } else {
                                app.debug("CMnPatchUpdate:  Failed to locate patch build for Build ID " + newId);
                                throw new ApplicationException(ErrorMap.APPLICATION_DISPLAY_FAILURE,
                                    "Unable to obtain patch build information."); 
                            }
                        }

                        req.setAttribute(IMnPatchForm.PATCH_DATA, patch);
                    } else {
                        throw new ApplicationException(ErrorMap.APPLICATION_DISPLAY_FAILURE,
                            "Failed to obtain patch data for patch ID " + patchId);
                    }
                }

                // Determine if the user has submitted the information
                // Don't fall back to the request attributes because we don't want to
                // consider this field if the request was forwarded from another command
                boolean updateOk = false;
                String requestModeValue = (String) req.getParameter(CMnBaseForm.FORM_STATUS_LABEL);
                if ((requestModeValue != null) && requestModeValue.equalsIgnoreCase(CMnBaseForm.UPDATE_DATA)) {
                    app.debug("CMnPatchUpdate: Attempting to update the patch data for Patch ID " + patchId); 
                    updateOk = patchTable.updateRequest(rc.getConnection(), patch);
                } else {
                    app.debug("CMnPatchUpdate: No update submitted.");
                }

                // Forward the user to the correct page following the update
                if (updateOk) {
                    result = app.forwardToCommand(req, res, "/patch/CMnPatchRequest");
                } else {
                    result.setDestination("patch/patch_update.jsp");
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
            app.debug("CMnPatchDelete: skipping execution due to pre-existing error: " + result.getError());
        }

        return result;
    }


    /**
     * Helper method for updating the patch data with input from the user.
     */
    private void update(WebApplication app, HttpServletRequest req, CMnPatch patch) throws ApplicationException {
        // Pull the information from the request
        String custId = (String) req.getParameter(IMnPatchForm.CUSTOMER_ID_LABEL);
        if (custId == null) {
            custId = (String) req.getAttribute(IMnPatchForm.CUSTOMER_ID_LABEL);
        }
        String envId = (String) req.getParameter(IMnPatchForm.ENV_ID_LABEL);
        if (envId == null) {
            envId = (String) req.getAttribute(IMnPatchForm.ENV_ID_LABEL);
        }
        String buildId = (String) req.getParameter(IMnPatchForm.BUILD_ID_LABEL);
        if (buildId == null) {
            buildId = (String) req.getAttribute(IMnPatchForm.BUILD_ID_LABEL);
        }
        String patchBuildId = (String) req.getParameter(IMnPatchForm.PATCH_BUILD_LABEL);
        if (patchBuildId == null) {
            patchBuildId = (String) req.getAttribute(IMnPatchForm.PATCH_BUILD_LABEL);
        }
        String basePatchId = (String) req.getParameter(IMnPatchForm.BASE_PATCH_LABEL);
        if (basePatchId == null) {
            basePatchId = (String) req.getAttribute(IMnPatchForm.BASE_PATCH_LABEL);
        }
        String custUse = (String) req.getParameter(IMnPatchForm.PATCH_USE_LABEL);
        if (custUse == null) {
            custUse = (String) req.getAttribute(IMnPatchForm.PATCH_USE_LABEL);
        }
        String patchName = (String) req.getParameter(IMnPatchForm.PATCH_NAME_LABEL);
        if (patchName == null) {
            patchName = (String) req.getAttribute(IMnPatchForm.PATCH_NAME_LABEL);
        }
        String notification = (String) req.getParameter(IMnPatchForm.NOTIFY_LABEL);
        if (notification == null) {
            notification = (String) req.getAttribute(IMnPatchForm.NOTIFY_LABEL);
        }
        String justification = (String) req.getParameter(IMnPatchForm.JUSTIFY_LABEL);
        if (justification == null) {
            justification = (String) req.getAttribute(IMnPatchForm.JUSTIFY_LABEL);
        }


        if (patchName != null) {
            patch.setName(patchName);
        }

        if (justification != null) {
            patch.setJustification(justification);
        }

        // Update the internal/external status
        if (custUse != null) {
            patch.setForExternalUse(Boolean.parseBoolean(custUse));
        }

        // Update the previous patch ID
        if (basePatchId != null) {
            try {
                Integer basePid = new Integer(basePatchId);

                // Determine if the previous patch info is correct
                if ((patch.getPreviousPatch() == null) ||
                    (basePid != patch.getPreviousPatch().getId())) 
                {
                    CMnPatch previousPatch = new CMnPatch();
                    previousPatch.setId(basePid);
                    patch.setPreviousPatch(previousPatch);
                }
            } catch (NumberFormatException nfex) {
                // Don't fail the request because of an invalid patch ID
                //throw new ApplicationException(ErrorMap.APPLICATION_DISPLAY_FAILURE,
                //    "Invalid previous patch ID: " + basePatchId);
            }
        }

        // Update the patch build ID
        if ((patchBuildId != null) && (patchBuildId.trim().length() > 0)) {
            try {
                CMnDbBuildData patchBuild = patch.getPatchBuild();
                int pid = Integer.parseInt(patchBuildId);
                if ((patchBuild == null) || ((patchBuild != null) && (pid != patchBuild.getId()))) {
                    patchBuild = new CMnDbBuildData();
                    patchBuild.setId(pid);
                    patch.setPatchBuild(patchBuild);
                }
            } catch (NumberFormatException nfex) {
                // Don't fail the request because of an invalid patch ID
                //throw new ApplicationException(ErrorMap.APPLICATION_DISPLAY_FAILURE,
                //    "Invalid patch build ID: " + patchBuildId);
            }
        }

        // Update the CC list
        if (notification != null) {
            try {
                patch.setCCList(InternetAddress.parse(notification));
            } catch (AddressException aex) {
                throw new ApplicationException(ErrorMap.APPLICATION_DISPLAY_FAILURE,
                    "Invalid e-mail address list.");
            }
        }


    }

}
