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
import com.modeln.build.ctrl.forms.IMnBuildForm;
import com.modeln.build.ctrl.forms.IMnTestForm;
import com.modeln.testfw.reporting.CMnBuildTable;
import com.modeln.testfw.reporting.CMnUnittestTable;
import com.modeln.testfw.reporting.CMnDbBuildData;
import com.modeln.testfw.reporting.CMnDbUnitTestData;
import com.modeln.testfw.reporting.CMnDbTestSuite;

/**
 * The Login command attempts to authenticate the user and 
 * create a login session if successful.
 * 
 * @version            $Revision: 1.1.1.1 $  
 * @author             Shawn Stafford
 */
public class CMnSuiteData extends TrustedHostCommand {

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

            try {
                rc = app.getRepositoryConnection();

                // Define data objects which will hopefully be stored in the session
                CMnDbBuildData buildData = null;
                CMnDbTestSuite suiteData = null;
                Vector testList = null;

                int buildId = 0;

                // Collect the suite data
                String suiteId = (String) req.getParameter(IMnTestForm.SUITE_ID_LABEL);
                String groupId = (String) req.getParameter(IMnTestForm.GROUP_ID_LABEL);
                String groupName = (String) req.getParameter(IMnTestForm.GROUP_NAME_LABEL);
                if ((suiteId != null) && (suiteId.length() > 0)) {
                    suiteData = utTable.getSuite(rc.getConnection(), suiteId);
                    testList = utTable.getTestsBySuite(rc.getConnection(), suiteId);
                    if (suiteData != null) {
                        buildId = suiteData.getParentId();
                        req.setAttribute(IMnTestForm.SUITE_OBJECT_LABEL, suiteData);
                    }
                } else if ((groupId != null) && (groupId.length() > 0)) {
                    testList = utTable.getTestsBySuiteGroup(rc.getConnection(), groupId);

                    // Attempt to retrieve the build ID indirectly by following the test/suite relationship
                    if ((testList != null) && (testList.size() > 0)) {
                        CMnDbUnitTestData test = (CMnDbUnitTestData) testList.get(0);
                        if ((test != null) && (test.getParentId() > 0)) {
                            suiteData = utTable.getSuite(rc.getConnection(), Integer.toString(test.getParentId()));
                            if ((suiteData != null) && (suiteData.getParentId() > 0)) {
                                buildId = suiteData.getParentId();
                            }
                        }
                    }
                } else if ((groupName != null) && (groupName.length() > 0)) {
                    // The group name is not a unique key so we need to combine it with a suite or build ID
                    String buildIdStr = (String) req.getParameter(IMnBuildForm.BUILD_ID_LABEL);
                    if ((buildIdStr != null) && (buildIdStr.length() > 0)) {
                        buildId = Integer.parseInt(buildIdStr);
                        testList = utTable.getTestsByGroupName(rc.getConnection(), buildIdStr, groupName);
                    }
                }

                // Determine if we can obtain the build information from the suite parent ID
                if (buildId > 0) {
                    // Collect the build data from the database
                    buildData = CMnBuildTable.getBuild(rc.getConnection(), Integer.toString(buildId));
                }

                // Store the build information in the session
                if (buildData != null) {
                    req.setAttribute(CMnBuildDataForm.BUILD_OBJECT_LABEL, buildData);
                }

                // Store the list of unit tests in the session
                if (testList != null) {
                    req.setAttribute("UNITTEST_LIST", testList);
                }


                result.setDestination("reports/unittest_suite.jsp");
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
