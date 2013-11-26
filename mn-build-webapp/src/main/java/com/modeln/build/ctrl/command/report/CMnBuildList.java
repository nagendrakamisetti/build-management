/*
 * Login.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.ctrl.command.report; 

import com.modeln.testfw.reporting.CMnBuildTable;
import com.modeln.build.ctrl.forms.CMnBuildListForm;

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
import com.modeln.testfw.reporting.CMnDbFeatureOwnerData;
import com.modeln.testfw.reporting.CMnDbTestSuite;
import com.modeln.testfw.reporting.CMnFeatureOwnerTable;
import com.modeln.testfw.reporting.CMnUnittestTable;
import com.modeln.build.ctrl.charts.CMnBuildListChart;
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
                CMnBuildListForm form = new CMnBuildListForm(new URL("http://localhost/CMnBuildList"), new URL("http://localhost/images"), new Vector());
                form.setValues(req); 
                app.debug("CMnBuildList: finished parsing form parameters");

                // Perform the query
                rc = app.getRepositoryConnection();
                app.debug("CMnBuildList: obtained a connection to the build database");
                Vector list = CMnBuildTable.getAllBuilds(rc.getConnection(), form.getValues(), 0, form.getMaxRows(), true);

                // Determine if any of the results need to be excluded based on the test results
                Vector newList = null;
                if (form.requireTests()) {
                    newList = new Vector();
                    Enumeration buildList = list.elements();
                    while (buildList.hasMoreElements()) {
                        CMnDbBuildData build = (CMnDbBuildData) buildList.nextElement();
                        if ((build.getTestCount() > 0) || (build.getSummaryTestCount() > 0)) {
                            newList.add(build);
                        }
                    }
                    list = newList; 
                }
 
                app.debug("CMnBuildList: obtained a list of all builds that match the form parameters");
                if ((list != null) && (list.size() == 1)) {
                    CMnDbBuildData buildData = (CMnDbBuildData) list.get(0);
                    app.debug("CMnBuildList: obtained first entry in the list of builds ");
                    req.setAttribute(CMnBuildDataForm.BUILD_ID_LABEL, Integer.toString(buildData.getId()));
                    app.debug("CMnBuildList: added the build data to the request attribute");
                    result = app.forwardToCommand(req, res, "/report/CMnBuildData");
                } else {
                    // Determine whether to render charts for the build list
                    if (form.showCharts()) {
                        createCharts(app, req, res, rc.getConnection(), list);
                    }

                    // Pass the build list to the JSP for display
                    req.setAttribute("BUILD_LIST", list);
                    app.debug("CMnBuildList: added entire list of builds to the request attribute");
                    result.setDestination("reports/build_list.jsp");
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
     * Generate charts and display them to the user. 
     *
     * @param   app     Application which called the command
     * @param   req     HttpServletRequest object
     * @param   res     HttpServletResponse object
     * @param   conn    Database connection
     * @param   list    List of CMnDbBuildData objects
     */
    private void createCharts(WebApplication app, HttpServletRequest req, HttpServletResponse res, Connection conn, Vector list) throws SQLException {
        CMnUnittestTable utTable = CMnUnittestTable.getInstance();

        // Save the charts in the session for later use
        HttpSession session = req.getSession(true);
        session.setAttribute("blm", CMnBuildListChart.getMetricChart(list));
        app.debug("CMnBuildList: obtained the metric chart");
        session.setAttribute("testCount", CMnBuildListChart.getTestCountChart(list));
        app.debug("CMnBuildList: obtained the test count chart");
        session.setAttribute("blmAvg", CMnBuildListChart.getAverageMetricChart(list));
        app.debug("CMnBuildList: obtained the average metric chart");
        session.setAttribute("testCountAvg", CMnBuildListChart.getAverageTestCountChart(list));
        app.debug("CMnBuildList: obtained the average test count chart");


        // Handle area grouping slightly differently
        Vector<CMnDbFeatureOwnerData> areas = CMnFeatureOwnerTable.getAllAreas(conn);
        app.debug("CMnBuildList: obtained a list of product areas");
        Vector<CMnDbTestSuite> allSuites = new Vector<CMnDbTestSuite>();
        if (list != null) {
            Enumeration buildList = list.elements();
            while (buildList.hasMoreElements()) {
                CMnDbBuildData build = (CMnDbBuildData) buildList.nextElement();
                String buildId = Integer.toString(build.getId());
                Vector suites = utTable.getSuitesByBuild(conn, buildId);
                app.debug("CMnBuildList: obtained a list of suites for build " + buildId);
                // Update the suites to use the correct area grouping and add them to the list
                allSuites.addAll(utTable.organizeTestsByGroup(conn, buildId, suites));
            }
            session.setAttribute("testsByArea", CMnBuildListChart.getAreaTestCountChart(list, allSuites, areas));
            app.debug("CMnBuildList: obtained the test count by area chart");
            session.setAttribute("avgTestsByArea", CMnBuildListChart.getAverageAreaTestCountChart(list, allSuites, areas));
            app.debug("CMnBuildList: obtained the average test count by area chart");

            session.setAttribute("timeByArea", CMnBuildListChart.getAreaTestTimeChart(list, allSuites, areas));
            app.debug("CMnBuildList: obtained the test time by area chart");
            session.setAttribute("avgTimeByArea", CMnBuildListChart.getAverageAreaTestTimeChart(list, allSuites, areas));
            app.debug("CMnBuildList: obtained the average test time by area chart");

        }

        // Save some basic chart information in the session to ensure 
        // that consistent rendering information is used for both the 
        // image map and chart (separate request)
        session.setAttribute("blmInfo", CMnBuildListChart.getMetricRenderingInfo());
        session.setAttribute("blmAvgInfo", CMnBuildListChart.getAverageMetricRenderingInfo());
        session.setAttribute("testCountInfo", CMnBuildListChart.getTestCountRenderingInfo());
        session.setAttribute("testCountAvgInfo", CMnBuildListChart.getAverageTestCountRenderingInfo());

    }

}
