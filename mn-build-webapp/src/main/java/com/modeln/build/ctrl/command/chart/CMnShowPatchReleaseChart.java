package com.modeln.build.ctrl.command.chart; 

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.List;
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

import com.modeln.build.common.data.account.CMnAccount;
import com.modeln.build.ctrl.charts.CMnPatchCountChart;
import com.modeln.build.ctrl.database.CMnPatchReportTable;
import com.modeln.build.ctrl.forms.CMnChartForm;




/**
 * The chart command is used to display a chart of the number
 * of patch requests per release. 
 * 
 * @author             Shawn Stafford
 */
public class CMnShowPatchReleaseChart extends ChartCommand {

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
                // Get the format to present the data in
                String format = req.getParameter(CMnChartForm.FORMAT_LABEL);

                // Obtain the height and width for the chart
                int chartHeight = Integer.parseInt(req.getParameter(CMnChartForm.HEIGHT_LABEL));
                int chartWidth  = Integer.parseInt(req.getParameter(CMnChartForm.WIDTH_LABEL));

                // Obtain the date range for the chart
                String startDate = req.getParameter(CMnChartForm.START_DATE_LABEL);
                Date start = CMnChartForm.DATE.parse(startDate);
                String endDate = req.getParameter(CMnChartForm.END_DATE_LABEL);
                Date end = CMnChartForm.DATE.parse(endDate);

                // Define the list of releases to collect data about
                Vector<String> releases = new Vector<String>();
                releases.add("MN-PHARMA-5.%");
                releases.add("MN-PHARMA-6.%");
                releases.add("MN-HT-8.%");

                // Obtain the number of patches per release 
                rc = app.getRepositoryConnection();
                CMnPatchReportTable reportTable = CMnPatchReportTable.getInstance();
                app.debug("CMnShowPatchReleaseChart rendering chart from " + start + " to " + end);

                Hashtable<String, Integer> patchCount = new Hashtable<String, Integer>();
                Enumeration releaseList = releases.elements();
                while (releaseList.hasMoreElements()) {
                    String release = (String) releaseList.nextElement();
                    Hashtable<String, Integer> currentCount = reportTable.getReleasePatchCount(rc.getConnection(), release, start, end); 
                    Enumeration keys = currentCount.keys();
                    while (keys.hasMoreElements()) {
                        String key = (String) keys.nextElement();
                        Integer value = (Integer) currentCount.get(key);
                        if (patchCount.containsKey(key)) {
                            // Add the current count to the existing count
                            Integer oldValue = (Integer) patchCount.get(key);
                            value = oldValue + value; 
                        }

                        // Do some cleanup on the release name so it looks better on the chart
                        if (key.startsWith("MN-PHARMA-")) {
                            key = key.replace("MN-PHARMA-", "LS ");
                        } else if (key.startsWith("MN-HT-")) {
                            key = key.replace("MN-HT-", "HT ");
                        }

                        // Add the release to the chart data
                        patchCount.put(key, value);
                    }
                }

                if ((format != null) && format.equalsIgnoreCase(CMnChartForm.FORMAT_CSV)) {
                    app.streamAsSpreadsheet(req, res, patchCount);
                } else {
                    JFreeChart chart = CMnPatchCountChart.getBarChart(patchCount, "Patch Requests by Release", "Release", "Patches");

                    // Display the chart to the user
                    if (chart != null) {
                        ChartUtilities.writeChartAsPNG(res.getOutputStream(), chart, chartWidth, chartHeight);
                        res.setContentType("image/png");
                    } else {
                        exApp = new ApplicationException(
                            ErrorMap.APPLICATION_DISPLAY_FAILURE,
                            "Failed to render chart");
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
