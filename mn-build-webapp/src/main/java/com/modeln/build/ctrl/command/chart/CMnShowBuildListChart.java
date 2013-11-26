/*
 * Login.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.ctrl.command.chart; 

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Vector;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;

import com.modeln.build.web.errors.ApplicationError;
import com.modeln.build.web.errors.ApplicationException;
import com.modeln.build.web.application.ChartCommand;
import com.modeln.build.web.application.CommandResult;
import com.modeln.build.web.application.WebApplication;
import com.modeln.build.web.database.RepositoryConnection;
import com.modeln.build.web.errors.ErrorMap;

import com.modeln.build.ctrl.charts.CMnBuildListChart;
import com.modeln.build.ctrl.forms.CMnBuildDataForm;
import com.modeln.build.ctrl.forms.CMnBuildListForm;
import com.modeln.testfw.reporting.CMnBuildTable;
import com.modeln.testfw.reporting.CMnDbBuildData;



/**
 * The chart command is used to diplay charts for a list of builds.
 * 
 * @version            $Revision: 1.1.1.1 $  
 * @author             Shawn Stafford
 */
public class CMnShowBuildListChart extends ChartCommand {

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
                // Obtain the name of the session object which contains the chart data
                String chartName = req.getParameter("chart");

                // Obtain the height and width for the chart
                int chartHeight = Integer.parseInt(req.getParameter("height"));
                int chartWidth  = Integer.parseInt(req.getParameter("width"));

                // Collect the query results from the build list page
                CMnBuildListForm form = new CMnBuildListForm(new URL("http://localhost/CMnBuildList"), new URL("http://localhost/images"), new Vector());
                form.setValues(req);
                app.debug("CMnBuildList: finished parsing form parameters");

                // Perform the query
                rc = app.getRepositoryConnection();
                Vector list = CMnBuildTable.getAllBuilds(rc.getConnection(), form.getValues(), 0, form.getMaxRows(), true);
                if ((list != null) && (list.size() == 1)) {
                    CMnDbBuildData buildData = (CMnDbBuildData) list.get(0);
                    req.setAttribute(CMnBuildDataForm.BUILD_ID_LABEL, Integer.toString(buildData.getId()));
                    result = app.forwardToCommand(req, res, "/chart/CMnShowBuildChart");
                } else {
                    JFreeChart chart = null;
                    if ((list != null) && (list.size() > 0)) {
                        // Generate the build charts
                        if ((chartName != null) && (chartName.equals("metrics"))) {
                            chart = CMnBuildListChart.getMetricChart(list);
                        }
                    }

                    // Display the chart to the user
                    if (chart != null) {
                        ChartUtilities.writeChartAsPNG(res.getOutputStream(), chart, chartWidth, chartHeight);
                        res.setContentType("image/png");
                    } else {
                        exApp = new ApplicationException(
                            ErrorMap.APPLICATION_DISPLAY_FAILURE,
                            "Failed to render chart: " + chartName);
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

            } // try/catch


        } // if no error


        return result;
    }


}
