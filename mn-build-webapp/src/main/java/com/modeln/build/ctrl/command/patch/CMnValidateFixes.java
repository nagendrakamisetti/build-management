/*
 * Login.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.ctrl.command.patch; 

import com.modeln.build.common.data.product.CMnPatch;
import com.modeln.build.common.data.product.CMnPatchFix;
import com.modeln.build.ctrl.database.CMnPatchTable;
import com.modeln.build.ctrl.forms.IMnPatchForm;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import com.modeln.build.web.errors.ApplicationError;
import com.modeln.build.web.errors.ApplicationException;
import com.modeln.build.web.application.CommandResult;
import com.modeln.build.web.application.ProtectedCommand;
import com.modeln.build.web.application.WebApplication;
import com.modeln.build.web.database.RepositoryConnection;
import com.modeln.build.web.errors.ErrorMap;


/**
 * This command validates that all the fixes specified by the user
 * are included in the patch. 
 * 
 * @author             Shawn Stafford
 */
public class CMnValidateFixes extends CMnBasePatchFixes {

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

                // Keep track of field validation errors
                Hashtable<String,String> inputErrors = new Hashtable<String,String>();

                // Fall back to the request attributes in case the data was set by another command
                String patchId = (String) req.getParameter(IMnPatchForm.PATCH_ID_LABEL);
                if (patchId == null) {
                    patchId = (String) req.getAttribute(IMnPatchForm.PATCH_ID_LABEL);
                }

                CMnPatch patch = null;
                if ((patchId != null) && (patchId.length() > 0)) {
                    patch = patchTable.getRequest(rc.getConnection(), patchId, true);
                    app.debug("CMnPatchRequest: obtained data for patch ID " + patchId);
                }

                // Update the base fixes with bug information
                if (patch != null) {
                    getSDTrackerFixes(app, patch.getFixes());
                    //setOrigin(basePatch, baseFixes, false);

                    // Parse the list of fixes submitted by the user (comma-delmited list)
                    String bulkFixes = (String) req.getParameter(IMnPatchForm.BULK_FIX_LIST);
                    if (bulkFixes == null) {
                        bulkFixes = (String) req.getAttribute(IMnPatchForm.BULK_FIX_LIST);
                    }
                    if ((bulkFixes != null) && (bulkFixes.trim().length() > 0)) {
                        Vector<CMnPatchFix> validFixes = new Vector<CMnPatchFix>();
                        Vector<CMnPatchFix> invalidFixes = new Vector<CMnPatchFix>();
                        StringBuffer msgBuffer = new StringBuffer();
                        StringTokenizer st = new StringTokenizer(bulkFixes, ",");
                        while (st.hasMoreTokens()) {
                            boolean bulkIdFound = false;
                            String bulkBugIdStr = st.nextToken();
                            try {
                                // Validate the list of bulk bugs
                                int bulkBugId = Integer.parseInt(bulkBugIdStr.trim());
                                CMnPatchFix fix = patch.getFix(bulkBugId);
                                if (fix != null) {
                                    validFixes.add(fix);
                                } else {
                                    fix = new CMnPatchFix();
                                    fix.setBugId(bulkBugId);
                                    invalidFixes.add(fix);
                                }
                            } catch (NumberFormatException nfex) {
                                app.debug("CMnPatchRequestFixes: Unable to format bulk ID as a number: " + bulkBugIdStr);
                                msgBuffer.append(bulkBugIdStr + " - C'mon, that isn't even a number!\n");
                            }
                        }
                        if (msgBuffer.length() > 0) {
                            app.debug("CMnPatchRequestFixes: User has entered invalid fixes: " + msgBuffer.toString());
                            inputErrors.put(IMnPatchForm.BULK_FIX_LIST, "Invalid fixes: " + msgBuffer.toString());
                        }

                        // Attempt to load bug information for the invalid fixes
                        if (invalidFixes.size() > 0) {
                            getSDTrackerFixes(app, invalidFixes);
                        }

                        // Save the list of valid and invalid fixes in the session for display
                        req.setAttribute("VALID_FIXES", validFixes);
                        req.setAttribute("INVALID_FIXES", invalidFixes);
                    }
                }

                // Continue with the request if all parameters have been provided
                if ((patch != null) && (inputErrors.size() == 0)) {
                    req.setAttribute(IMnPatchForm.PATCH_DATA, patch);
                }

                result.setDestination("patch/validate_fixes.jsp");
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
