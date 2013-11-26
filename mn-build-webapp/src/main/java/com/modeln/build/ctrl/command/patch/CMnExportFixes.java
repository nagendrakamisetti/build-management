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
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
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
 * This command exports the list of fixes in the requested
 * output format. 
 * 
 * @author             Shawn Stafford
 */
public class CMnExportFixes extends CMnBasePatchFixes {

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

                String exportFormat = (String) req.getParameter(IMnPatchForm.EXPORT_FORMAT_LABEL);
                if (exportFormat == null) {
                    exportFormat = (String) req.getAttribute(IMnPatchForm.EXPORT_FORMAT_LABEL);
                }

                // Obtain information about the patch
                CMnPatch patch = null;
                if ((patchId != null) && (patchId.length() > 0)) {
                    patch = patchTable.getRequest(rc.getConnection(), patchId, true);
                    app.debug("CMnPatchRequest: obtained data for patch ID " + patchId);
                }

                // Continue with the request if all parameters have been provided
                if ((patch != null) && (inputErrors.size() == 0)) {
                    // Determine how to present the data to the user
                    if (IMnPatchForm.EXPORT_CSV.equalsIgnoreCase(exportFormat)) {
                        // Obtain detailed information about each fix 
                        Vector fixes = patchTable.getFixes(rc.getConnection(), patchId, true);
                        if ((fixes != null) && (fixes.size() > 0)) {
                            getSDTrackerFixes(app, fixes);
                        }
                        patch.setFixes(fixes);
                        streamAsSpreadsheet(app, req, res, patch);

                        // Keep the user on the current page after they download the spreadsheet
                        req.setAttribute(IMnPatchForm.PATCH_DATA, patch);
                        result.setDestination("patch/export_fixlist.jsp");
                    } else if (IMnPatchForm.EXPORT_EXE.equalsIgnoreCase(exportFormat)) {
                        req.setAttribute(IMnPatchForm.PATCH_DATA, patch);
                        result.setDestination("patch/export_cmdline.jsp");
                    } else {
                        req.setAttribute(IMnPatchForm.PATCH_DATA, patch);
                        result.setDestination("patch/export_fixlist.jsp");
                    }

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

        return result;
    }


    /**
     * Export the results as a spreadsheet. 
     */
    protected static void streamAsSpreadsheet(WebApplication app, HttpServletRequest req, HttpServletResponse res, CMnPatch patch)
        throws ApplicationException
    {
        Vector<CMnPatchFix> fixes = patch.getFixes();
        if ((fixes != null) && (fixes.size() > 0)) {
            // spreadsheet content
            List<List<String>> content = new ArrayList<List<String>>(fixes.size());

            // Represent each SDR as a row in the spreadsheet
            // SDR,origin,release,status
            Iterator fixIter = fixes.iterator();
            while (fixIter.hasNext()) {
                CMnPatchFix currentFix = (CMnPatchFix) fixIter.next();
                if (currentFix != null) {
                    ArrayList<String> row = new ArrayList(2);

                    String sdr = null; 
                    if (currentFix.getBugId() > 0) {
                        sdr = Integer.toString(currentFix.getBugId());
                    }
                    row.add(sdr);

                    String origin = null;
                    if (currentFix.getOrigin() != null) {
                        origin = currentFix.getOrigin().getName();
                    }
                    row.add(origin);

                    row.add(currentFix.getStatus());
                    row.add(currentFix.getRelease());
                    row.add(currentFix.getType());
                    row.add(currentFix.getSubType());

                    content.add(row);
                }
            }

            // Stream the spreadsheet content to the user
            try {
                app.streamAsSpreadsheet(req, res, content);
            } catch (Exception ex) {
                throw new ApplicationException(
                    ErrorMap.APPLICATION_DISPLAY_FAILURE,
                    "Failed to create spreadsheet content.");
            }
        }
    }

}
