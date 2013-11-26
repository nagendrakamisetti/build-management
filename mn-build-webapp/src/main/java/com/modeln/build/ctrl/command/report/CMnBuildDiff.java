/*
 * Login.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.ctrl.command.report;

import com.modeln.testfw.reporting.CMnAcceptanceTestTable;
import com.modeln.testfw.reporting.CMnBuildTable;
import com.modeln.testfw.reporting.CMnFlexTestTable;
import com.modeln.testfw.reporting.CMnMetricTable;
import com.modeln.testfw.reporting.CMnUitTable;
import com.modeln.testfw.reporting.CMnUnittestTable;
import com.modeln.build.ctrl.forms.CMnBuildDiffForm;

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

/**
 * The diff command analyzes the differences between two builds to determine
 * what type of unit test failure differences exist between the two builds. 
 * 
 * @version            $Revision: 1.1.1.1 $  
 * @author             Shawn Stafford
 */
public class CMnBuildDiff extends TrustedHostCommand {

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

            CMnUnittestTable utTable = CMnUnittestTable.getInstance();
            CMnUitTable uitTable = CMnUitTable.getInstance();
            CMnAcceptanceTestTable actTable = CMnAcceptanceTestTable.getInstance();
            CMnFlexTestTable flexTable = CMnFlexTestTable.getInstance();

            try {
                // Collect the query results from the build diff page
                CMnBuildDiffForm form = new CMnBuildDiffForm(new URL("http://localhost/CMnBuildDiff"), new URL("http://localhost/images"), new Vector());
                form.setValues(req); 
                app.debug("CMnBuildDiff: finished parsing form parameters");

                // Construct a data object to hold the build results
                Vector buildList = new Vector();
		
                // Obtain build information
                rc = app.getRepositoryConnection();
		String[] buildIds = form.getSelectedIds();
                app.debug("CMnBuildDiff: Iterating through " + buildIds.length + " builds");
                for (int idx = 0; idx < buildIds.length; idx++) {
                    app.debug("CMnBuildDiff: querying database for build " + buildIds[idx]);
		    CMnDbBuildData build = CMnBuildTable.getBuild(rc.getConnection(), buildIds[idx]);
                    if (build != null) {
                        // Obtain the metrics for the current build
                        CMnMetricTable metricTable = CMnMetricTable.getInstance();
                        Vector metrics = metricTable.getMetrics(rc.getConnection(), build.getBuildVersion());
                        if ((metrics != null) && (metrics.size() > 0)) {
                            app.debug("CMnBuildDiff: adding " + metrics.size() + " metrics to build " + buildIds[idx]);
                            build.setMetrics(metrics);
                        }

                        // Obtain the test results for the build
                        Vector tests = new Vector();
                        tests.addAll(utTable.getTestsByBuild(rc.getConnection(), buildIds[idx]));
                        tests.addAll(flexTable.getTestsByBuild(rc.getConnection(), buildIds[idx]));
                        tests.addAll(actTable.getTestsByBuild(rc.getConnection(), buildIds[idx]));
                        tests.addAll(uitTable.getTestsByBuild(rc.getConnection(), buildIds[idx]));
                        app.debug("CMnBuildDiff: found " + tests.size() + " tests associated with Build ID " + buildIds[idx]);
                        build.setTestData(tests);

                        app.debug("CMnBuildDiff: adding build " + buildIds[idx] + " to the list");
		        buildList.add(build);
                    }		    
		}
                req.setAttribute("BUILD_LIST", buildList);
		
                // Display the diff result
                app.debug("CMnBuildDiff: added list of builds to the request attribute");
                result.setDestination("reports/build_diff.jsp");

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


