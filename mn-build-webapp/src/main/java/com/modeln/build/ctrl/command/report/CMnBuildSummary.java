/*
 * CMnBuildSummary.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.ctrl.command.report; 

import com.modeln.testfw.reporting.CMnBuildTable;
import com.modeln.build.ctrl.forms.CMnBuildSummaryForm;
import com.modeln.testfw.reporting.CMnUitTable;
import com.modeln.testfw.reporting.CMnUnittestTable;

import java.util.Hashtable;
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
import com.modeln.testfw.reporting.CMnDbHostData;
import com.modeln.build.ctrl.forms.CMnBuildDataForm;
import com.modeln.testfw.reporting.CMnDbTestSuite;

/**
 * The list command retrieves a list of builds from the database and passes it to 
 * the JSP through a session attribute. 
 * 
 * @version            $Revision: 1.1.1.1 $  
 * @author             Shawn Stafford
 */
public class CMnBuildSummary extends TrustedHostCommand {

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

            try {
                // Collect the query results from the build list page
                CMnBuildSummaryForm form = new CMnBuildSummaryForm(new URL("http://localhost/CMnBuildSummary"), new URL("http://localhost/images"), new Vector(), new Hashtable());
                form.setValues(req); 

                // Perform the query
                rc = app.getRepositoryConnection();
                Vector list = CMnBuildTable.getAllBuilds(rc.getConnection(), form.getValues(), 0, form.getMaxRows(), true);
                if ((list != null) && (list.size() == 1)) {
                    CMnDbBuildData buildData = (CMnDbBuildData) list.get(0);
                    req.setAttribute(CMnBuildDataForm.BUILD_ID_LABEL, Integer.toString(buildData.getId()));
                    result = app.forwardToCommand(req, res, "/report/CMnBuildData");
                } else {
                	Hashtable results = new Hashtable();
                	
                	// Iterate through each build and obtain a list of UIT and unit tests
                	CMnDbBuildData currentBuild = null;
                	CMnDbTestSuite currentSuite = null;
                	for (int idx = 0; idx < list.size(); idx++) {
                	    currentBuild = (CMnDbBuildData) list.get(idx);
                	    
                	    // Obtain the list of unit tests
                	    Vector unittests = utTable.getSuitesByBuild(rc.getConnection(), Integer.toString(currentBuild.getId()));
                	    for (int idxUnittest = 0; idxUnittest < unittests.size(); idxUnittest++) {
                	    	currentSuite = (CMnDbTestSuite) unittests.get(idxUnittest);
                	    	if (!excludeSuite(currentBuild, currentSuite)) {
                	            Vector tests = uitTable.getTestsBySuite(rc.getConnection(), Integer.toString(currentSuite.getId()));
                	            results.put(currentSuite, tests);
                	    	}
                	    }
                	    
                	    // Obtain the list of UIT tests
                	    Vector uits = uitTable.getSuitesByBuild(rc.getConnection(), Integer.toString(currentBuild.getId()));
                	    for (int idxUit = 0; idxUit < uits.size(); idxUit++) {
                	    	currentSuite = (CMnDbTestSuite) uits.get(idxUit);
                	    	if (!excludeSuite(currentBuild, currentSuite)) {
                	            Vector tests = uitTable.getTestsBySuite(rc.getConnection(), Integer.toString(currentSuite.getId()));
                	            results.put(currentSuite, tests);
                	    	}
                	    }
                	    
                	}
                    req.setAttribute("BUILD_LIST", list);
                    req.setAttribute("SUITE_LIST", results);
                    result.setDestination("reports/build_summary.jsp");
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
     * Determine if the test suite should be added to the summary.
     * Test suites should not be added to the summary in the following cases:
     * 1) if the build and the test suite were executed on the same host
     * 
     * @param build   Information about the build
     * @param suite   Information about the test suite
     */
    private boolean excludeSuite(CMnDbBuildData build, CMnDbTestSuite suite) {
        boolean exclude = false;
        
        CMnDbHostData buildHost = build.getHostData();
        CMnDbHostData suiteHost = suite.getHostData();
        if (buildHost.getHostname().equals(suiteHost.getHostname())) {
            exclude = true;
        }
        
        return exclude;
    }

}

