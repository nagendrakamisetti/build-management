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
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Vector;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;

import com.modeln.build.web.errors.ApplicationError;
import com.modeln.build.web.errors.ApplicationException;
import com.modeln.build.web.application.ChartCommand;
import com.modeln.build.web.application.CommandResult;
import com.modeln.build.web.application.WebApplication;
import com.modeln.build.web.errors.ErrorMap;

import com.modeln.build.ctrl.charts.CMnBuildChart;
import com.modeln.build.ctrl.forms.CMnBuildDataForm;
import com.modeln.testfw.reporting.CMnBuildTable;
import com.modeln.testfw.reporting.CMnDbBuildData;



/**
 * The chart command is used to diplay charts for a single build.
 * 
 * @version            $Revision: 1.1.1.1 $  
 * @author             Shawn Stafford
 */
public class CMnShowSessionChart extends ChartCommand {

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
            try {
                // Obtain the name of the session object which contains the chart data
                String chartName = req.getParameter("chart");

                // Obtain the height and width for the chart
                int chartHeight = Integer.parseInt(req.getParameter("height"));
                int chartWidth  = Integer.parseInt(req.getParameter("width"));

                // Display the chart to the user
                HttpSession session = req.getSession(true);
                JFreeChart chart = (JFreeChart) session.getAttribute(chartName); 
                ChartRenderingInfo info = (ChartRenderingInfo) session.getAttribute(chartName + "Info");
                if (chart != null) {
                    ChartUtilities.writeChartAsPNG(res.getOutputStream(), chart, chartWidth, chartHeight, info);
                    res.setContentType("image/png");
                } else {
                    exApp = new ApplicationException(
                        ErrorMap.APPLICATION_DISPLAY_FAILURE,
                        "Failed to render chart: " + chartName);
                }

            } catch (Exception ex) {
                exApp = new ApplicationException(
                    ErrorMap.APPLICATION_DISPLAY_FAILURE,
                    "Failed to process command.");
                exApp.setStackTrace(ex);
            } finally {
                // Throw the exception once the connection has been cleaned up
                if (exApp != null) {
                    throw exApp;
                }

            } // try/catch


        } // if no error


        return result;
    }


}
