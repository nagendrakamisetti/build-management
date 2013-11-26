/*
 * Login.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.ctrl.command.report; 

import com.modeln.build.ctrl.forms.CMnBuildDataForm;
import com.modeln.build.ctrl.forms.CMnBuildEventForm;
import com.modeln.build.ctrl.forms.CMnBuildListForm;
import com.modeln.testfw.reporting.CMnBuildTable;
import com.modeln.testfw.reporting.CMnDbBuildData;
import com.modeln.testfw.reporting.CMnReportTable;

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
 * The list command retrieves a list of build events from the database and passes it to 
 * the JSP through a session attribute. 
 * 
 * @version            $Revision: 1.1.1.1 $  
 * @author             Shawn Stafford
 */
public class CMnBuildEvents extends TrustedHostCommand {

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
                // If an event ID is specified, then it is assumed that only the event context 
                // for the specified event should be retrieved
                String eventId = (String) req.getParameter(CMnBuildEventForm.EVENT_ID_LABEL);
                if (eventId == null) {
                    eventId = (String) req.getAttribute(CMnBuildEventForm.EVENT_ID_LABEL);
                }

                // If a build ID is specified, then it is assumed that the entire list of events
                // should be retrieved
                String buildId = (String) req.getParameter(CMnBuildEventForm.BUILD_ID_LABEL);
                if (buildId == null) {
                    buildId = (String) req.getAttribute(CMnBuildEventForm.BUILD_ID_LABEL);
                }


                rc = app.getRepositoryConnection();

                // Look up information about the specified build
                CMnDbBuildData build = null;
                CMnReportTable reportTable = new CMnReportTable();
                if (buildId != null) {
                    build = CMnBuildTable.getBuild(rc.getConnection(), buildId);
                    req.setAttribute(CMnBuildEventForm.BUILD_OBJECT_LABEL, build);

                    // Look up the events
                    Vector eventList = reportTable.getEventList(rc.getConnection(), build.getBuildVersion());
                    req.setAttribute(CMnBuildEventForm.EVENT_LIST_LABEL, eventList);

                    result.setDestination("reports/event_list.jsp");
                } else if (eventId != null) {
                    String buildVersion = reportTable.getBuildVersion(rc.getConnection(), Integer.parseInt(eventId));
                    if (buildVersion != null) {
                        Vector builds = CMnBuildTable.getBuildsByVersion(rc.getConnection(), buildVersion);
                        if ((builds != null) && (builds.size() > 0)) {
                            build = (CMnDbBuildData) builds.get(0);
                        }
                    }
                    req.setAttribute(CMnBuildEventForm.BUILD_OBJECT_LABEL, build);

                    // Look up the event context
                    Vector eventList = reportTable.getEventContext(rc.getConnection(), Integer.parseInt(eventId), 50);
                    req.setAttribute(CMnBuildEventForm.EVENT_LIST_LABEL, eventList);

                    result.setDestination("reports/event_context.jsp");
                } else {
                    // Should not get here, but just in case...
                    result.setDestination("reports/event_list.jsp");
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


}
