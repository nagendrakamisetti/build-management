package com.modeln.build.ctrl.command.chart; 

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Hashtable;
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
import com.modeln.build.ctrl.CMnControlApp;
import com.modeln.build.ctrl.charts.CMnPatchCountChart;
import com.modeln.build.ctrl.database.CMnPatchReportTable;
import com.modeln.build.ctrl.forms.CMnChartForm;
import com.modeln.build.sdtracker.CMnBugTable;



/**
 * The chart command is used to diplay a chart of the number
 * of patch fixes per area. 
 * 
 * @author             Shawn Stafford
 */
public class CMnShowAreaFixChart extends ChartCommand {

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

            RepositoryConnection patchConn = null;
            RepositoryConnection bugConn = null;

            CMnBugTable bugTable = CMnBugTable.getInstance();
            CMnPatchReportTable reportTable = CMnPatchReportTable.getInstance();
            try {
                patchConn = app.getRepositoryConnection();
                bugConn = ((CMnControlApp)app).getBugRepositoryConnection();

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

                // Obtain the list of fixes 
                Hashtable data = new Hashtable();
                app.debug("CMnShowFixCountChart rendering chart from " + start + " to " + end);
                Vector<Integer> fixes = reportTable.getFixes(patchConn.getConnection(), start, end); 

                // Count the number of SDRs in each area
                Enumeration fixList = fixes.elements();
                while (fixList.hasMoreElements()) {
                    Integer bugId = (Integer) fixList.nextElement();
                    String name = bugTable.getApprovalGroup(bugConn.getConnection(), bugId.toString());
                    if (name != null) {
                        if (data.containsKey(name)) {
                            Integer total = (Integer) data.get(name);
                            data.put(name, total + 1);
                        } else {
                            data.put(name, new Integer(1));
                        }
                    } else {
                        app.debug("CMnShowAreaFixChart: getApprovalGroup returned null name for SDR " + bugId);
                    }
                }

                if ((format != null) && format.equalsIgnoreCase(CMnChartForm.FORMAT_CSV)) {
                    app.streamAsSpreadsheet(req, res, data);
                } else {
                    JFreeChart chart = CMnPatchCountChart.getBarChart(data, "SDRs by Area", "Area", "SDRs");

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
                app.releaseRepositoryConnection(patchConn);

                // Throw the exception once the connection has been cleaned up
                if (exApp != null) {
                    throw exApp;
                }

            } // try/catch


        } // if no error


        return result;
    }



}
