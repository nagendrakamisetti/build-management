/*
 * Login.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.ctrl.command.report; 

import com.modeln.build.ctrl.forms.CMnBaseForm;
import com.modeln.build.ctrl.forms.CMnBuildDataForm;
import com.modeln.build.ctrl.forms.CMnBuildStatusForm;
import com.modeln.build.ctrl.forms.CMnBuildStatusNoteForm;
import com.modeln.testfw.reporting.CMnDbBuildData;
import com.modeln.testfw.reporting.CMnDbBuildStatusData;
import com.modeln.testfw.reporting.CMnDbBuildStatusNote;
import com.modeln.testfw.reporting.CMnBuildTable;
import com.modeln.testfw.reporting.CMnReleaseTable;

import java.util.Vector;
import java.io.*;
import java.net.URL;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.Connection;
import java.sql.SQLException;

import com.modeln.build.web.errors.ApplicationError;
import com.modeln.build.web.errors.ApplicationException;
import com.modeln.build.web.application.CommandResult;
import com.modeln.build.web.application.TrustedHostCommand;
import com.modeln.build.web.application.WebApplication;
import com.modeln.build.web.database.RepositoryConnection;
import com.modeln.build.web.errors.ErrorMap;

/**
 * The status notes command retrieves status information about a specific build.
 * 
 * @version            $Revision: 1.1.1.1 $  
 * @author             Shawn Stafford
 */
public class CMnBuildStatusNotes extends TrustedHostCommand {

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
                String buildId = (String) req.getParameter(CMnBuildDataForm.BUILD_ID_LABEL);
                // Fall back to the request attributes in case the ID was set by another command
                if (buildId == null) {
                    buildId = (String) req.getAttribute(CMnBuildDataForm.BUILD_ID_LABEL);
                }

                rc = app.getRepositoryConnection();

                // Obtain information about the selected build
                CMnDbBuildData build = CMnBuildTable.getBuild(rc.getConnection(), buildId);

                // Determine the input mode of the form
                String inputMode = (String) req.getParameter(CMnBaseForm.FORM_STATUS_LABEL);
                if (inputMode == null) {
                    inputMode = (String) req.getAttribute(CMnBaseForm.FORM_STATUS_LABEL);
                }
                // Perform any updates that were submitted
                if ((inputMode != null) && (inputMode.equalsIgnoreCase(CMnBaseForm.UPDATE_DATA)) && app.hasPermission(req)) {
                    URL dummyUrl = new URL("http://localhost");
                    CMnBuildStatusNoteForm noteForm = new CMnBuildStatusNoteForm(dummyUrl, dummyUrl);
                    noteForm.setValues(req);
                    CMnDbBuildStatusNote noteData = noteForm.getValues();
                    if ((build != null) && (noteData != null)) {
                        CMnReleaseTable.updateStatusNote(rc.getConnection(), build, noteData);
                    }
                }

                // Obtain the latest status notes from the database
                CMnDbBuildStatusData status = null;
                if (build != null) {
                    status = CMnReleaseTable.getStatus(rc.getConnection(), build);
                    build.setStatus(status);
                }

                req.setAttribute(CMnBuildDataForm.BUILD_OBJECT_LABEL, build);
                result.setDestination("reports/status_note.jsp");
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
