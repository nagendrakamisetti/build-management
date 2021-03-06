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

/**
 * The Flex suite command displays information for a single
 * flex test suite. 
 * 
 * @author             Shawn Stafford
 */
public class CMnFlexSuiteData extends TrustedHostCommand {

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

                // Collect the suite data
                String suiteId = (String) req.getParameter(IMnTestForm.SUITE_ID_LABEL);
                CMnDbTestSuite suiteData = flexTable.getSuite(rc.getConnection(), suiteId);
                Vector testList = flexTable.getTestsBySuite(rc.getConnection(), suiteId);
                req.setAttribute(IMnTestForm.SUITE_OBJECT_LABEL, suiteData);
                req.setAttribute("FLEX_TEST_LIST", testList);

                // Collect the build data
                if (suiteData != null) {
                    CMnDbBuildData buildData = CMnBuildTable.getBuild(rc.getConnection(), Integer.toString(suiteData.getParentId()));
                    req.setAttribute(CMnBuildDataForm.BUILD_OBJECT_LABEL, buildData);
                }

                result.setDestination("reports/flextest_suite.jsp");
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
