/*
 * Login.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.ctrl.command.environment; 

import com.modeln.testfw.reporting.CMnBuildTable;
import com.modeln.build.ctrl.forms.CMnBuildHostForm;

import java.util.Date;
import java.util.Enumeration;
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

import com.modeln.testfw.reporting.CMnBuildTable;
import com.modeln.testfw.reporting.CMnDbBuildData;
import com.modeln.testfw.reporting.CMnMetricTable;
import com.modeln.testfw.reporting.CMnDbMetricData;
import com.modeln.build.ctrl.forms.CMnBuildDataForm;

/**
 * The list command retrieves a list of builds from the database and passes it to 
 * the JSP through a session attribute. 
 * 
 * @version            $Revision: 1.1.1.1 $  
 * @author             Shawn Stafford
 */
public class CMnBuildList extends TrustedHostCommand {

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
                // Collect the query results from the build list page
                CMnBuildHostForm form = new CMnBuildHostForm(new URL("http://localhost/CMnBuildList"), new URL("http://localhost/images"), new Vector());
                form.setValues(req); 

                // Perform the query
                rc = app.getRepositoryConnection();
                Vector list = CMnBuildTable.getBuildsByDate(rc.getConnection(), form.getWindowStart(), form.getWindowEnd());

                // Loop through the list of builds and collect build metrics
                CMnDbBuildData currentBuild = null;
                Enumeration buildlist = list.elements();
                while (buildlist.hasMoreElements()) {
                    currentBuild = (CMnDbBuildData) buildlist.nextElement(); 
                    CMnMetricTable metricTable = CMnMetricTable.getInstance();
                    Vector metrics = metricTable.getMetrics(rc.getConnection(), currentBuild.getBuildVersion()); 
                    currentBuild.setMetrics(metrics);
                }

                req.setAttribute("BUILD_LIST", list);
                result.setDestination("environment/build_list.jsp");
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
