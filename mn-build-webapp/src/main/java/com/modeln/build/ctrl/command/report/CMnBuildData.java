/*
 * Login.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.ctrl.command.report; 

import java.io.*;
import java.net.URL;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Vector;

import com.modeln.build.web.errors.ApplicationError;
import com.modeln.build.web.errors.ApplicationException;
import com.modeln.build.web.application.CommandResult;
import com.modeln.build.web.application.TrustedHostCommand;
import com.modeln.build.web.application.WebApplication;
import com.modeln.build.web.database.RepositoryConnection;
import com.modeln.build.web.errors.ErrorMap;

import com.modeln.build.ctrl.forms.CMnBaseForm;
import com.modeln.build.ctrl.forms.CMnBuildDataForm;
import com.modeln.build.ctrl.forms.CMnBuildEventForm;
import com.modeln.build.ctrl.forms.CMnBuildStatusForm;
import com.modeln.build.ctrl.forms.CMnTestSuiteGroup;
import com.modeln.build.ctrl.forms.IMnBuildForm;
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
import com.modeln.testfw.reporting.CMnDbBuildData;
import com.modeln.testfw.reporting.CMnDbBuildStatusData;
import com.modeln.testfw.reporting.CMnDbTestSuite;
import com.modeln.testfw.reporting.search.CMnSearchCriteria;
import com.modeln.testfw.reporting.search.CMnSearchGroup;


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
            RepositoryConnection rc = null;

            CMnUnittestTable utTable = CMnUnittestTable.getInstance();
            CMnUitTable uitTable = CMnUitTable.getInstance();
            CMnAcceptanceTestTable actTable = CMnAcceptanceTestTable.getInstance();
            CMnFlexTestTable flexTable = CMnFlexTestTable.getInstance();

            // Construct a set of search criteria for limiting the test suite results
            CMnSearchGroup utGroup = new CMnSearchGroup(CMnSearchGroup.AND);
            CMnSearchGroup uitGroup = new CMnSearchGroup(CMnSearchGroup.AND);
            CMnSearchGroup actGroup = new CMnSearchGroup(CMnSearchGroup.AND);
            CMnSearchGroup flexGroup = new CMnSearchGroup(CMnSearchGroup.AND);

            try {
                String buildId = (String) req.getParameter(CMnBuildDataForm.BUILD_ID_LABEL);
                // Fall back to the request attributes in case the ID was set by another command
                if (buildId == null) {
                    buildId = (String) req.getAttribute(CMnBuildDataForm.BUILD_ID_LABEL);
                }

                String filterCriteria = (String) req.getParameter(CMnBuildDataForm.FORM_FILTER_LABEL);
                if (filterCriteria == null) {
                    filterCriteria = (String) req.getAttribute(CMnBuildDataForm.FORM_FILTER_LABEL);
                }

                String envCriteria = (String) req.getParameter(CMnBuildDataForm.BUILD_ENV_LABEL);
                if (envCriteria == null) {
                    envCriteria = (String) req.getAttribute(CMnBuildDataForm.BUILD_ENV_LABEL);
                }
                if ((envCriteria != null) && (envCriteria.length() > 0)) {
                    CMnSearchCriteria utEnvCriteria = new CMnSearchCriteria(
                        utTable.getSuiteTable(),
                        CMnTestTable.SUITE_ENVIRONMENT,
                        CMnSearchCriteria.EQUAL_TO,
                        envCriteria 
                    );
                    utGroup.add(utEnvCriteria);

                    CMnSearchCriteria uitEnvCriteria = new CMnSearchCriteria(
                        uitTable.getSuiteTable(),
                        CMnTestTable.SUITE_ENVIRONMENT,
                        CMnSearchCriteria.EQUAL_TO,
                        envCriteria
                    );
                    uitGroup.add(uitEnvCriteria);

                    CMnSearchCriteria actEnvCriteria = new CMnSearchCriteria(
                        actTable.getSuiteTable(),
                        CMnTestTable.SUITE_ENVIRONMENT,
                        CMnSearchCriteria.EQUAL_TO,
                        envCriteria
                    );
                    actGroup.add(actEnvCriteria);

                    CMnSearchCriteria flexEnvCriteria = new CMnSearchCriteria(
                        flexTable.getSuiteTable(),
                        CMnTestTable.SUITE_ENVIRONMENT,
                        CMnSearchCriteria.EQUAL_TO,
                        envCriteria
                    );
                    flexGroup.add(flexEnvCriteria);
                }


                // Obtain information about the current build
                rc = app.getRepositoryConnection();
                app.debug("Attempting to retrieve build data for build ID " + buildId);
                CMnDbBuildData build = CMnBuildTable.getBuild(rc.getConnection(), buildId);
                if (build != null) {
                    build.setLogs(CMnLogTable.getLogs(rc.getConnection(), buildId));
                }
                CMnReportTable reportTable = new CMnReportTable(); 
                Vector list = reportTable.getEventSummary(rc.getConnection(), build.getBuildVersion());
                req.setAttribute(CMnBuildEventForm.EVENT_LIST_LABEL, list);

                // Obtain a list of product areas
                Vector areas = CMnFeatureOwnerTable.getAllAreas(rc.getConnection());
                if ((areas != null) && (areas.size() > 0)) {
                    req.setAttribute(IMnBuildForm.PRODUCT_AREA_DATA, areas);
                }

                // Obtain a list of test suites
                Vector suiteList = utTable.getSuitesByBuild(rc.getConnection(), buildId, utGroup);
                Vector actSuiteList = actTable.getSuitesByBuild(rc.getConnection(), buildId, actGroup);
                Vector uitSuiteList = uitTable.getSuitesByBuild(rc.getConnection(), buildId, uitGroup);
                Vector flexSuiteList = flexTable.getSuitesByBuild(rc.getConnection(), buildId, flexGroup);
                req.setAttribute(CMnBuildDataForm.BUILD_OBJECT_LABEL, build);
                if (filterCriteria != null) {
                    req.setAttribute(CMnBuildDataForm.FORM_FILTER_LABEL, filterCriteria);
                }

                // If the command is grouping by name, then we'll need to compensate
                // for the fact that tests may reside in a different suite than the
                // group they're associated with
                String groupBy = (String) req.getParameter(CMnTestSuiteGroup.FORM_GROUP_LABEL);
                if ((groupBy != null) && (groupBy.equals("area") || groupBy.equals("name")) && hasValidGroupNames(suiteList)) {
                    suiteList = utTable.organizeTestsByGroup(rc.getConnection(), buildId, suiteList);
                }

                req.setAttribute("SUITE_LIST", suiteList);
                req.setAttribute("ACT_SUITE_LIST", actSuiteList);
                req.setAttribute("UIT_SUITE_LIST", uitSuiteList);
                req.setAttribute("FLEX_SUITE_LIST", flexSuiteList);
                result.setDestination("reports/build.jsp");

                // Determine the input mode of the form
                String inputMode = (String) req.getParameter(CMnBaseForm.FORM_STATUS_LABEL);
                if (inputMode == null) {
                    inputMode = (String) req.getAttribute(CMnBaseForm.FORM_STATUS_LABEL);
                }
                if ((inputMode != null) && (inputMode.equalsIgnoreCase(CMnBaseForm.UPDATE_DATA)) && app.hasPermission(req)) {
                    CMnBuildStatusForm statusForm = new CMnBuildStatusForm(new URL("http://localhost"), new URL("http://localhost"));
                    statusForm.setValues(req);
                    CMnDbBuildStatusData statusData = statusForm.getValues();
                    if ((build != null) && (statusData != null)) {
                        CMnReleaseTable.updateStatus(rc.getConnection(), build, statusData);
                        build.setStatus(statusData);
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

                // Throw the exception once the connection has been cleaned up
                if (exApp != null) {
                    throw exApp;
                }

/*
                // Summarize the command execution
                String logfile = "/var/tmp/summary.txt";
                try {
                    PrintStream stream = new PrintStream(logfile);
                    stream.println("CMnUnittestTable performance:");
                    CMnUnittestTable.getInstance().debugQueryPerformance(stream);
                    stream.println("CMnAcceptanceTestTable performance:");
                    CMnAcceptanceTestTable.getInstance().debugQueryPerformance(stream);
                } catch (FileNotFoundException nfex) {
                    System.out.println("Unable to enable debugging.  Failed to open log file: " + logfile);
                }
*/

            }
        }

        return result;
    }


    /**
     * Return true if all of the suites in the list have a valid (non-null) 
     * value.
     *
     * @param  suites   List of test suites
     * @return TRUE if the suites contain valid group names 
     */
    private static boolean hasValidGroupNames(Vector suites) {
        boolean result = true;
        Enumeration list = suites.elements();
        while (list.hasMoreElements()) {
            CMnDbTestSuite suite = (CMnDbTestSuite) list.nextElement();
            if (suite.getGroupName() == null) {
                result = false;
            }
        }
        return result;
    }

}
