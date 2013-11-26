/*
 * Login.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.ctrl.command.move; 

import java.io.*;
import java.net.URL;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.util.Enumeration;
import java.util.Vector;

import com.modeln.build.web.errors.ApplicationError;
import com.modeln.build.web.errors.ApplicationException;
import com.modeln.build.web.application.CommandResult;
import com.modeln.build.web.application.TrustedHostCommand;
import com.modeln.build.web.application.WebApplication;
import com.modeln.build.web.database.RepositoryConnection;
import com.modeln.build.web.errors.ErrorMap;

import com.modeln.build.common.data.product.CMnBuildQuery;
import com.modeln.build.ctrl.forms.CMnBaseForm;
import com.modeln.build.ctrl.forms.CMnBuildDataForm;
import com.modeln.build.ctrl.forms.CMnBuildQueryForm;
import com.modeln.testfw.reporting.CMnAcceptanceTestTable;
import com.modeln.testfw.reporting.CMnBuildTable;
import com.modeln.testfw.reporting.CMnFeatureOwnerTable;
import com.modeln.testfw.reporting.CMnLogTable;
import com.modeln.testfw.reporting.CMnReleaseTable;
import com.modeln.testfw.reporting.CMnReportTable;
import com.modeln.testfw.reporting.CMnTestTable;
import com.modeln.testfw.reporting.CMnUnittestTable;
import com.modeln.testfw.reporting.CMnFlexTestTable;
import com.modeln.testfw.reporting.CMnUitTable;
import com.modeln.testfw.reporting.CMnDbAcceptanceTestData;
import com.modeln.testfw.reporting.CMnDbBuildData;
import com.modeln.testfw.reporting.CMnDbBuildStatusData;
import com.modeln.testfw.reporting.CMnDbFlexTestData;
import com.modeln.testfw.reporting.CMnDbTestData;
import com.modeln.testfw.reporting.CMnDbTestSuite;
import com.modeln.testfw.reporting.CMnDbUit;
import com.modeln.testfw.reporting.CMnDbUnitTestData;


/**
 * The Login command attempts to authenticate the user and 
 * create a login session if successful.
 * 
 * @version            $Revision: 1.1.1.1 $  
 * @author             Shawn Stafford
 */
public class CMnBuildData extends TrustedHostCommand {

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
            RepositoryConnection rcLocal = null;
            Connection connRemote = null;
            CMnBuildQuery query = null;

            CMnBuildTable buildTable = CMnBuildTable.getInstance();
            CMnUnittestTable utTable = CMnUnittestTable.getInstance();
            CMnUitTable uitTable = CMnUitTable.getInstance();
            CMnAcceptanceTestTable actTable = CMnAcceptanceTestTable.getInstance();
            CMnFlexTestTable flexTable = CMnFlexTestTable.getInstance();

            try {
                // If the user is not using the history, collect SQL input
                CMnBuildQueryForm form = new CMnBuildQueryForm(new URL("http://localhost"), new URL("http://localhost"));
                form.setValues(req);

                String localBuildId = null;
                if (form.isComplete()) {
                    query = form.getValues();

                    // Obtain a JDBC connection to the remote database
                    String url = query.getJdbcUrl();
                    String user = query.getUsername();
                    String pass = query.getPassword();
                    connRemote = DriverManager.getConnection(url, user, pass);

                    String remoteBuildId = query.getBuildId();
                    if (remoteBuildId != null) {
                        // Obtain information about the current build
                        rcLocal = app.getRepositoryConnection();
                        CMnDbBuildData build = CMnBuildTable.getBuild(connRemote, remoteBuildId);
                        if (build != null) {
                            // Obtain a list of test suites
                            Vector utSuiteList = utTable.getSuitesByBuild(connRemote, remoteBuildId);
                            app.debug("Retrieved " + utSuiteList.size() + " unit test suites.");
                            Vector actSuiteList = actTable.getSuitesByBuild(connRemote, remoteBuildId);
                            app.debug("Retrieved " + actSuiteList.size() + " ACT suites.");
                            Vector uitSuiteList = uitTable.getSuitesByBuild(connRemote, remoteBuildId);
                            app.debug("Retrieved " + uitSuiteList.size() + " UI test suites.");
                            Vector flexSuiteList = flexTable.getSuitesByBuild(connRemote, remoteBuildId);
                            app.debug("Retrieved " + flexSuiteList.size() + " Flex test suites.");

                            // Obtain a list of tests
                            Vector utTestList = utTable.getTestsByBuild(connRemote, remoteBuildId);
                            app.debug("Retrieved " + utTestList.size() + " unit tests.");
                            Vector actTestList = actTable.getTestsByBuild(connRemote, remoteBuildId);
                            app.debug("Retrieved " + actTestList.size() + " ACT tests.");
                            Vector uitTestList = uitTable.getTestsByBuild(connRemote, remoteBuildId);
                            app.debug("Retrieved " + uitTestList.size() + " UI tests.");
                            Vector flexTestList = flexTable.getTestsByBuild(connRemote, remoteBuildId);
                            app.debug("Retrieved " + flexTestList.size() + " Flex tests.");

                            // Add the build data to the local database
                            localBuildId = buildTable.addBuild(rcLocal.getConnection(), build);
                            app.debug("Inserted build into local database: " + remoteBuildId + " to " + localBuildId); 

                            // Add the suite data to the local database
                            CMnDbTestSuite currentSuite = null;
                            int oldSuiteId = 0;
                            int newSuiteId = 0;
                            Enumeration utSuites = utSuiteList.elements();
                            while (utSuites.hasMoreElements()) {
                                currentSuite = (CMnDbTestSuite) utSuites.nextElement();
                                oldSuiteId = currentSuite.getId(); 
                                String suiteId = utTable.addSuite(rcLocal.getConnection(), localBuildId, currentSuite); 
                                newSuiteId = Integer.parseInt(suiteId);
                                app.debug("Inserted unit test suite into local database: " + oldSuiteId + " to " + newSuiteId);
                                updateSuiteId(oldSuiteId, newSuiteId, utTestList);
                            }

                            Enumeration actSuites = actSuiteList.elements();
                            while (actSuites.hasMoreElements()) {
                                currentSuite = (CMnDbTestSuite) actSuites.nextElement();
                                oldSuiteId = currentSuite.getId();
                                String suiteId = actTable.addSuite(rcLocal.getConnection(), localBuildId, currentSuite);
                                newSuiteId = Integer.parseInt(suiteId);
                                app.debug("Inserted ACT suite into local database: " + oldSuiteId + " to " + newSuiteId);
                                updateSuiteId(oldSuiteId, newSuiteId, actTestList);
                            }

                            Enumeration uitSuites = uitSuiteList.elements();
                            while (uitSuites.hasMoreElements()) {
                                currentSuite = (CMnDbTestSuite) uitSuites.nextElement();
                                oldSuiteId = currentSuite.getId();
                                String suiteId = uitTable.addSuite(rcLocal.getConnection(), localBuildId, currentSuite);
                                newSuiteId = Integer.parseInt(suiteId);
                                app.debug("Inserted UI test suite into local database: " + oldSuiteId + " to " + newSuiteId);
                                updateSuiteId(oldSuiteId, newSuiteId, uitTestList);
                            }

                            Enumeration flexSuites = flexSuiteList.elements();
                            while (flexSuites.hasMoreElements()) {
                                currentSuite = (CMnDbTestSuite) flexSuites.nextElement();
                                oldSuiteId = currentSuite.getId();
                                String suiteId = flexTable.addSuite(rcLocal.getConnection(), localBuildId, currentSuite);
                                newSuiteId = Integer.parseInt(suiteId);
                                app.debug("Inserted Flex test suite into local database: " + oldSuiteId + " to " + newSuiteId);
                                updateSuiteId(oldSuiteId, newSuiteId, flexTestList);
                            }

                            // Add test data to the local database
                            Enumeration utTests = utTestList.elements();
                            while (utTests.hasMoreElements()) {
                                CMnDbUnitTestData currentTest = (CMnDbUnitTestData) utTests.nextElement();
                                utTable.addTest(rcLocal.getConnection(), Integer.toString(currentTest.getParentId()), currentTest);
                            }

                            Enumeration actTests = actTestList.elements();
                            while (actTests.hasMoreElements()) {
                                CMnDbAcceptanceTestData currentTest = (CMnDbAcceptanceTestData) actTests.nextElement();
                                actTable.addTest(rcLocal.getConnection(), Integer.toString(currentTest.getParentId()), currentTest);
                            }

                            Enumeration uitTests = uitTestList.elements();
                            while (uitTests.hasMoreElements()) {
                                CMnDbUit currentTest = (CMnDbUit) uitTests.nextElement();
                                uitTable.addTest(rcLocal.getConnection(), "mysql", Integer.toString(currentTest.getParentId()), currentTest);
                            }

                            Enumeration flexTests = flexTestList.elements();
                            while (flexTests.hasMoreElements()) {
                                CMnDbFlexTestData currentTest = (CMnDbFlexTestData) flexTests.nextElement();
                                flexTable.addTest(rcLocal.getConnection(), Integer.toString(currentTest.getParentId()), currentTest);
                            }

                        }
                    }

                }

                // If the build was transferred, display the results
                if (localBuildId != null) {
                    app.debug("Displaying build results for build ID " + localBuildId);
                    req.setAttribute(CMnBuildDataForm.BUILD_ID_LABEL, localBuildId);
                    result = app.forwardToCommand(req, res, "/report/CMnBuildData");
                } else {
                    result.setDestination("move/query.jsp");
                }

            } catch (ApplicationException aex) {
                exApp = aex;
            } catch (Exception ex) {
                exApp = new ApplicationException(
                    ErrorMap.APPLICATION_DISPLAY_FAILURE,
                    "Failed to process command.");
                exApp.setStackTrace(ex);
            } finally {
                app.releaseRepositoryConnection(rcLocal);
                if (connRemote != null) {
                    try {
                        connRemote.close();
                    } catch (SQLException sqlex) {
                        if (exApp != null) {
                            // There are pre-existing errors, so just silently log this one
                            sqlex.printStackTrace();
                        } else {
                            exApp = new ApplicationException(
                                ErrorMap.APPLICATION_DISPLAY_FAILURE,
                                "Unable to close the database connection: " + sqlex.getMessage());
                        }
                    }
                }

                // Throw the exception once the connection has been cleaned up
                if (exApp != null) {
                    throw exApp;
                }

            }
        }

        return result;
    }


    /**
     * Update the list of tests with the new suite ID. 
     *
     * @param   oid   Old suite ID
     * @param   nid   New suite ID
     * @param   list  List of tests
     */
    private void updateSuiteId(int oid, int nid, Vector list) {
        CMnDbTestData test = null;
        Enumeration tests = list.elements();
        while (tests.hasMoreElements()) {
            test = (CMnDbTestData) tests.nextElement();
            if (test.getParentId() == oid) {
                test.setParentId(nid);
            }
        }
    }


}
