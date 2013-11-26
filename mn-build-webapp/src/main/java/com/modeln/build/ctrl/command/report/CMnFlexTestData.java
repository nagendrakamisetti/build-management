/*
 * Login.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.ctrl.command.report; 

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Vector;

import com.modeln.build.common.logging.SecureLog;
import com.modeln.build.web.errors.ApplicationError;
import com.modeln.build.web.errors.ApplicationException;
import com.modeln.build.web.application.CommandResult;
import com.modeln.build.web.application.TrustedHostCommand;
import com.modeln.build.web.application.WebApplication;
import com.modeln.build.web.database.RepositoryConnection;
import com.modeln.build.web.errors.ErrorMap;

import com.modeln.build.ctrl.forms.CMnBuildDataForm;
import com.modeln.build.ctrl.forms.IMnTestForm;
import com.modeln.testfw.reporting.CMnBuildTable;
import com.modeln.testfw.reporting.CMnFlexTestTable;
import com.modeln.testfw.reporting.CMnDbBuildData;
import com.modeln.testfw.reporting.CMnDbTestSuite;
import com.modeln.testfw.reporting.CMnDbFlexTestData;

/**
 * The Login command attempts to authenticate the user and 
 * create a login session if successful.
 * 
 * @version            $Revision: 1.1.1.1 $  
 * @author             Shawn Stafford
 */
public class CMnFlexTestData extends TrustedHostCommand {

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

            CMnFlexTestTable flexTable = CMnFlexTestTable.getInstance();

            try {
                rc = app.getRepositoryConnection();

                // Obtain the test information from the database
                String testId = (String) req.getParameter(IMnTestForm.TEST_ID_LABEL);
                CMnDbFlexTestData testData = flexTable.getTest(rc.getConnection(), testId); 
                req.setAttribute(IMnTestForm.TEST_OBJECT_LABEL, testData);

                // Determine the test which last ran prior to the current state change
                if (testData.getStartTime() != null) {
                    CMnDbFlexTestData lastFail = flexTable.getLastFailure(rc.getConnection(), testData);
                    req.setAttribute(IMnTestForm.LASTFAIL_OBJECT_LABEL, lastFail);
                
                    CMnDbFlexTestData lastPass = flexTable.getLastPass(rc.getConnection(), testData);
                    req.setAttribute(IMnTestForm.LASTPASS_OBJECT_LABEL, lastPass);
                }

                // Obtain the test suite data from the database
                String suiteId = (String) req.getParameter(IMnTestForm.SUITE_ID_LABEL);
                if (suiteId == null) {
                    suiteId = Integer.toString(testData.getParentId()); 
                }
                CMnDbTestSuite suiteData = flexTable.getSuite(rc.getConnection(), suiteId);
                if (suiteData != null) {
                    req.setAttribute(IMnTestForm.SUITE_OBJECT_LABEL, suiteData);
                }

                // Obtain the build data from the database
                String buildId = (String) req.getParameter(CMnBuildDataForm.BUILD_ID_LABEL);
                if (buildId == null) {
                    buildId = Integer.toString(suiteData.getParentId());
                }
                CMnDbBuildData buildData = CMnBuildTable.getBuild(rc.getConnection(), buildId);
                if (buildData != null) {
                    req.setAttribute(CMnBuildDataForm.BUILD_OBJECT_LABEL, buildData);
                }

                result.setDestination("reports/flextest.jsp");
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
