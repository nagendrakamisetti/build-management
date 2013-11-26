package com.modeln.build.ctrl.command.chart; 

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
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

import com.modeln.build.common.data.CMnTimeInterval;
import com.modeln.build.common.data.account.CMnAccount;
import com.modeln.build.ctrl.charts.CMnPatchCountChart;
import com.modeln.build.ctrl.database.CMnPatchReportTable;
import com.modeln.build.ctrl.forms.CMnChartForm;



/**
 * The chart command is used to diplay a chart of the number
 * of patch requests per time period (weeks, months, quarters). 
 * 
 * @author             Shawn Stafford
 */
public class CMnShowPatchTrendChart extends ChartCommand {

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

                // Obtain the list of customers 
                rc = app.getRepositoryConnection();
                CMnPatchReportTable reportTable = CMnPatchReportTable.getInstance();
                app.debug("CMnShowPatchTrendChart rendering chart from " + start + " to " + end);

                // Get a list of request dates that occurred witin the time frame
                Vector<Date> requests = reportTable.getRequestDate(rc.getConnection(), start, end); 

                // Categorize the request dates by time interval
                String interval = req.getParameter("interval");
                String label = "Month";
                int size = Calendar.MONTH;
                if (interval != null) {
                    if (interval.equalsIgnoreCase("week")) {
                        size = Calendar.WEEK_OF_MONTH;
                        label = "Week"; 
                    } else if (interval.equalsIgnoreCase("day")) {
                        size = Calendar.DAY_OF_MONTH;
                        label = "Day";
                    } else if (interval.equalsIgnoreCase("year")) {
                        size = Calendar.YEAR;
                        label = "Year";
                    }
                }
                Hashtable<CMnTimeInterval, Integer> intervals = groupBy(requests, size); 

                if ((format != null) && format.equalsIgnoreCase(CMnChartForm.FORMAT_CSV)) {
                    streamTimeSpreadsheet(app, req, res, intervals);
                } else {
                    JFreeChart chart = CMnPatchCountChart.getPatchesByIntervalChart(intervals, label);

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

    /**
     * Count the number of requests that occurred in each interval.
     * The interval size is specified using the Calendar fields of 
     * DAY_OF_MONTH, WEEK_OF_MONTH, MONTH, or YEAR.
     *
     * @param  dates  List of patch request dates
     * @param  size   Interval size 
     * @return Summary count of requests per interval of time
     */
    public Hashtable<CMnTimeInterval, Integer> groupBy(Vector<Date> dates, int size) {
        Hashtable<CMnTimeInterval, Integer> intervals = new Hashtable<CMnTimeInterval, Integer>();

        // Initialize the list of intervals
        Enumeration dateList = dates.elements();
        while (dateList.hasMoreElements()) {
            Date currentDate = (Date) dateList.nextElement();
            CMnTimeInterval interval = getInterval(intervals.keys(), currentDate);
            Integer count = null;
            if (interval != null) {
                count = intervals.get(interval) + 1;
            } else {
                interval = new CMnTimeInterval(currentDate, size);
                count = new Integer(0);
            }

            // Add or update the interval with the latest count
            intervals.put(interval, count);
        }

        return intervals;
    } 

    /**
     * Iterate through the list of intervals to find the one
     * that corresponds to the specified date.
     *
     * @param  date   Target date
     * @return Interval containing the specified date, or null if none found
     */
    private CMnTimeInterval getInterval(Enumeration<CMnTimeInterval> intervals, Date date) {
        CMnTimeInterval match = null;

        while (intervals.hasMoreElements()) {
            CMnTimeInterval current = (CMnTimeInterval) intervals.nextElement();
            if (current.contains(date)) {
                return current;
            }
        }

        return match;
    }

    /**
     * Export the results as a spreadsheet. 
     * 
     * @param   
     */
    protected static void streamTimeSpreadsheet(
            WebApplication app, 
            HttpServletRequest req, 
            HttpServletResponse res, 
            Hashtable<CMnTimeInterval, Integer> intervals)
        throws ApplicationException
    {
        // spreadsheet content
        List<List<String>> content = new ArrayList<List<String>>(intervals.size());

        // Represent each time interval as a row in the spreadsheet
        // interval name, start date, end date, patch count 
        Enumeration intervalList = intervals.keys();
        while (intervalList.hasMoreElements()) {
            CMnTimeInterval interval = (CMnTimeInterval) intervalList.nextElement();
            if (interval != null) {
                ArrayList<String> row = new ArrayList(4);

                // Column value: interval name
                row.add(interval.getName());

                // Column value: interval start
                row.add(interval.getStart().toString());

                // Column value: interval end
                row.add(interval.getEnd().toString());

                // Column value: service patch count
                Integer count = (Integer) intervals.get(interval);
                if (count != null) {
                    row.add(count.toString());
                }

                content.add(row);
            }
        }

        // Stream the spreadsheet content to the user
        try {
            app.streamAsSpreadsheet(req, res, content);
        } catch (Exception ex) {
            throw new ApplicationException(
                ErrorMap.APPLICATION_DISPLAY_FAILURE,
                "Failed to create spreadsheet content.");
        }
    }

}
