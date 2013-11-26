/* 
* Copyright 2000-2003 by Model N, Inc.  All Rights Reserved. 
* 
* This software is the confidential and proprietary information 
* of Model N, Inc ("Confidential Information").  You shall not 
* disclose such Confidential Information and shall use it only 
* in accordance with the terms of the license agreement you 
* entered into with Model N, Inc. 
*/
package com.modeln.build.ctrl.forms;

import com.modeln.build.common.data.product.CMnBuildData;
import com.modeln.testfw.reporting.CMnBuildDateComparator;
import com.modeln.testfw.reporting.CMnBuildHostComparator;
import com.modeln.testfw.reporting.CMnBuildTable;
import com.modeln.testfw.reporting.CMnDbBuildData;
import com.modeln.testfw.reporting.CMnDbBuildStatusData;
import com.modeln.testfw.reporting.CMnDbHostData;
import com.modeln.testfw.reporting.CMnDbMetricData;
import com.modeln.testfw.reporting.CMnMetricTable;
import com.modeln.testfw.reporting.search.CMnSearchCriteria;
import com.modeln.testfw.reporting.search.CMnSearchGroup;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Vector;
import java.util.Hashtable;
import javax.servlet.http.HttpServletRequest;
import com.modeln.build.web.tags.TextTag;
import com.modeln.build.web.tags.DateTag;
import com.modeln.build.web.tags.SelectTag;
import com.modeln.build.web.tags.TagGroup;


/**
 * The build form provides an HTML interface to the build data object.
 * The class manages transfering the data through the HTTP request.
 * Form data can be rendered in either a read-only or input mode.
 * 
 * @author  Shawn Stafford
 */
public class CMnBuildHostForm extends CMnBaseForm implements IMnBuildForm {

    /** Default title displayed in the bordered header */
    private static final String DEFAULT_TITLE = "Build Metrics";

    /** Date format used when constructing SQL queries */
    protected static final SimpleDateFormat DATE = new SimpleDateFormat("yyyy-MM-dd");

    /** Default number of minutes in the display window for the build progress results */
    protected static final String DEFAULT_WINDOW_SIZE = "1440";

    /** Default number of columns displayed in the build progress result window */
    protected static final int DEFAULT_COLUMN_COUNT = 48;


    /** Build window duration field */
    private SelectTag buildWindowTag;


    /** Calculated progress window start date based on the build window tag value */
    private Date windowStart = null;

    /** End date for the progress window */
    private Date windowEnd = new Date();


    /** List of build data objects. */
    private Vector buildList = null;


    /** URL for deleting a table entry */
    private URL deleteUrl = null;

    /**  URL for accessing detailed build information */
    private URL buildUrl = null;

    /**  URL for accessing build status information */
    private URL statusUrl = null;



    /**
     * Construct a build host list.  When input is enabled, the form
     * will be rendered with input fields.
     *
     * @param    builds          List of builds
     */
    public CMnBuildHostForm(URL form, URL images, Vector builds) {
        super(form, images);
        buildList = builds;

        // Construct a list of options for the granularity of the table
        Hashtable windowOps = new Hashtable(4);
        String[] windowKeys = { "60", "240", "720", "1080", "1440" };
        windowOps.put("60", "1hr");
        windowOps.put("240", "4hr");
        windowOps.put("720", "12hr");
        windowOps.put("1080", "18hr");
        windowOps.put("1440", "24hr");
        buildWindowTag = new SelectTag(BUILD_WINDOW_LABEL, windowOps);
        buildWindowTag.setKeyOrder(windowKeys);
        //buildWindowTag.setDefault(DEFAULT_WINDOW_SIZE);
        buildWindowTag.setSelected(DEFAULT_WINDOW_SIZE);
        windowStart = getWindowStart();

        // Sort the list of builds
        //Collections.sort(buildList, new CMnBuildHostComparator());
        Collections.sort(buildList, new CMnBuildDateComparator());
    }

    /**
     * Set the URL used to delete build entries.
     *
     * @param  url   Link to the command related to build deletion
     */
    public void setDeleteUrl(URL url) {
        deleteUrl = url;
    }

    /**
     * Set the URL used to retrieve build status notes. 
     *
     * @param  url   Link to the command related to build status notes
     */
    public void setStatusUrl(URL url) {
        statusUrl = url;
    }

    /**
     * Set the URL used to retrieve detailed build information.
     *
     * @param  url   Link to the command for detailed build info 
     */
    public void setBuildUrl(URL url) {
        buildUrl = url;
    }


    /**
     * Extend the base method for enabling the input mode.  All input fields
     * must be enabled or disabled when this method is called. 
     *
     * @param enabled  TRUE to enable form input
     */
    public void setInputMode(boolean enabled) {
        super.setInputMode(enabled);
        buildWindowTag.setDisabled(!enabled);
        inputEnabled = enabled;
    }

    /**
     * Set the input fields by examining the HTTP request to see if
     * a value was submitted.
     *
     * @param   req     HTTP request
     */
    public void setValues(HttpServletRequest req) {
        buildWindowTag.setValue(req);
        windowStart = getWindowStart();
    }


    /**
     * Determine if the specified build falls within the progress window.
     *
     * @param   build   Build information containing start and end times
     * @return  TRUE if the build falls within the progress window, FALSE otherwise
     */
    private boolean showInWindow(CMnDbBuildData build) {
        boolean inWindow = false;

        Date buildStart = build.getStartTime();
        Date buildEnd = build.getEndTime();
        if ((buildStart != null) && windowStart.before(buildStart)) {
            // The build started within the progress window
            inWindow = true;
        } else if ((buildEnd != null) && windowStart.before(buildEnd)) {
            // The build ended (or continued) within the progress window
            inWindow = true;
        }

        return inWindow;
    }

    /**
     * Return the total number of minutes represented by the progress window.
     *
     * @return  Number of minutes in the progress window
     */
    public int getWindowSize() {
        int windowSize = 0;
        if (buildWindowTag.isComplete()) {
            String[] windowSizeList = buildWindowTag.getSelected();
            windowSize = Integer.valueOf(windowSizeList[0]).intValue();
        } else {
            windowSize = Integer.valueOf(DEFAULT_WINDOW_SIZE).intValue();
        }

        return windowSize;
    }

    /**
     * Return the start time of the build progress window.
     *
     * @return  Start date of the build progress window
     */
    public Date getWindowStart() {
        GregorianCalendar endDate = new GregorianCalendar();
        endDate.setTime(windowEnd);

        // Calculate the start time by subtracting from the window size from the end time
        int windowMinutes = getWindowSize();
        endDate.add(GregorianCalendar.MINUTE, -windowMinutes);
        return endDate.getTime();
    }

    /**
     * Return the end time of the build progress window.
     *
     * @return  End date of the build progress window
     */
    public Date getWindowEnd() {
        return windowEnd; 
    }


    /**
     * Return the number of minutes represented by a single column in the progress area. 
     *
     * @return  Number of minutes per column
     */
    private int getColumnMinutes() {
        return getWindowSize() / DEFAULT_COLUMN_COUNT;
    }


    /**
     * Render the build data form.
     */
    public String toString() {
        StringBuffer html = new StringBuffer();

        if (buildList.size() > 1) {
            html.append("<form action=" + getFormUrl() + ">\n");
            html.append("<table width=\"100%\" cellspacing=\"0\" cellpadding=\"1\" border=\"1\">\n");
            html.append(getHeader());
            html.append(getBuildList());
            html.append(getFooter());
            html.append("</table>\n");
            html.append("</form>\n");
        } else if (buildList.size() == 1) {
            CMnDbBuildData currentBuild = (CMnDbBuildData) buildList.get(0);
            html.append(getBuildMetrics(currentBuild));
        } else {
            html.append("No build metrics available.");
        }

        return html.toString();
    }

    /**
     * Return a string representation of the elapsed time.
     *
     * @param  millis  Elapsed time (in milliseconds)
     * @return String representing the elapsed time
     */
    public String formatElapsedTime(long millis) {
        StringBuffer text = new StringBuffer();

        // Display the total number of minutes for build execution
        if (millis == Long.MAX_VALUE) {
            text.append("Incomplete");
        } else {
            int elapsedTime = (int) ((millis / 1000) / 60);
            if (elapsedTime > 60) {
                int elapsedHours = elapsedTime / 60;
                text.append(elapsedHours + "hr ");
            }
            int elapsedMinutes = elapsedTime % 60;
            text.append(elapsedMinutes + "min");
        }

        return text.toString();
    }


    /**
     * Return a string representation of the metric title.
     *
     * @param  metric  Numeric ID as defined in CMnDbBuildMetric
     */
    public String getMetricTitle(int metric) {
        switch (metric) {
          case CMnDbMetricData.TYPE_BUILD:         return "Compilation and Packaging";
          case CMnDbMetricData.TYPE_UNITTEST:      return "Unit Tests";
          case CMnDbMetricData.TYPE_JAVADOC:       return "JavaDoc";
          case CMnDbMetricData.TYPE_POPULATE:      return "Static Content Population";
          case CMnDbMetricData.TYPE_POPULATESUITE: return "Dynamic Content Population (Unittests)"; 
          case CMnDbMetricData.TYPE_MIGRATE:       return "Database Migration";
          case CMnDbMetricData.TYPE_DEPLOY:        return "Application Deployment";
          default: return "Unknown Metric: " + Integer.toString(metric);
        }
    } 

    /**
     * Return the build metrics as a table containing the name and execution
     * time for each metric.
     */
    public String getBuildMetrics(CMnDbBuildData build) {
        StringBuffer html = new StringBuffer();

        html.append("<table width=\"100%\" border=\"0\" cellspacing=\"1\" cellpadding=\"1\">\n");

        Enumeration metrics = build.getMetrics().elements();
        while (metrics.hasMoreElements()) {
            CMnDbMetricData metric = (CMnDbMetricData) metrics.nextElement();
            Date metricStart = metric.getStartTime();
            Date metricEnd = metric.getEndTime();

            // Account for the case where the metric time is null
            long elapsedTime = 0;
            if (metricStart == null) {
                // When the start is null, assume the task is zero-length
                elapsedTime = 0;
            } else if (metricEnd == null) {
                // When the start is valid but the end is null, assume it is in progress
                elapsedTime = Long.MAX_VALUE;
            } else {
                elapsedTime = getElapsedTime(metricStart, metricEnd);
            }

            html.append("<tr>\n");
            html.append("  <td width=\"50%\">" + getMetricTitle(metric.getType()) + "</td>\n");
            html.append("  <td width=\"50%\">" + formatElapsedTime(elapsedTime) + "</td>\n");
            html.append("</tr>\n");
        }

        html.append("</table>\n");

        return html.toString();
    }

    /**
     * Construct a table header.
     */
    private String getHeader() {
        StringBuffer html = new StringBuffer();
        html.append("<tr>\n");
        //html.append("  <td nowrap width=\"5%\"  bgcolor=\"#CCCCCC\">Host</td>\n");
        html.append("  <td nowrap width=\"10%\"  bgcolor=\"#CCCCCC\">Build</td>\n");
        for (int idx = 0; idx < DEFAULT_COLUMN_COUNT; idx++) {
            html.append("  <td bgcolor=\"#CCCCCC\">&nbsp;</td>\n");
        }
        html.append("  <td colspan=\"2\" nowrap width=\"4%\" bgcolor=\"#CCCCCC\" align=\"center\">Duration</td>\n");
        html.append("</tr>\n");

        return html.toString();
    }


    /**
     * Construct a table footer.
     */
    private String getFooter() {
        StringBuffer html = new StringBuffer();

        html.append("  <tr>\n");
        html.append("    <td colspan=\"1\">&nbsp;</td>\n");
        html.append("    <td colspan=\"" + DEFAULT_COLUMN_COUNT + "\" align=\"center\" bgcolor=\"#CCCCCC\">\n");
        html.append(windowStart + " to " + windowEnd);
        html.append("    </td>\n");
        if (inputEnabled) {
            html.append("    <td colspan=\"2\">" + getInputFields() + "</td>\n");
        }
        html.append("  </tr>\n");

        html.append("  <tr>\n");
        html.append("    <td colspan=\"1\">&nbsp;</td>\n");
        html.append("    <td colspan=\"" + DEFAULT_COLUMN_COUNT + "\">\n");
        html.append(getSymbolKey());
        html.append("    </td>\n");
        html.append("    <td colspan=\"2\">&nbsp;</td>\n");
        html.append("  </tr>\n");

        return html.toString();
    }

    /**
     * Display a key for the various build elements.
     */
    private String getSymbolKey() {
        StringBuffer html = new StringBuffer();

        html.append("<table width=\"100%\" border=\"0\" cellspacing=\"4\" cellpadding=\"1\">\n");

        html.append("<tr>\n");
        html.append("  <td width=\"5%\" class=\"metricNone\" align=\"center\">&nbsp;</td>\n");
        html.append("  <td width=\"95%\">Overall Build Process (" + getColumnMinutes() + " minute increments)</td>\n");
        html.append("</tr>\n");

        html.append("<tr>\n");
        html.append("  <td width=\"5%\" class=\"metricBuild\" align=\"center\">B</td>\n");
        html.append("  <td width=\"95%\">" + getMetricTitle(CMnDbMetricData.TYPE_BUILD) + "</td>\n");
        html.append("</tr>\n");

        html.append("<tr>\n");
        html.append("  <td width=\"5%\" class=\"metricJavadoc\" align=\"center\">J</td>\n");
        html.append("  <td width=\"95%\">" + getMetricTitle(CMnDbMetricData.TYPE_JAVADOC) + "</td>\n");
        html.append("</tr>\n");

        html.append("<tr>\n");
        html.append("  <td width=\"5%\" class=\"metricPopulate\" align=\"center\">P</td>\n");
        html.append("  <td width=\"95%\">" + getMetricTitle(CMnDbMetricData.TYPE_POPULATE) + "</td>\n");
        html.append("</tr>\n");

        html.append("<tr>\n");
        html.append("  <td width=\"5%\" class=\"metricPopulateSuite\" align=\"center\">S</td>\n");
        html.append("  <td width=\"95%\">" + getMetricTitle(CMnDbMetricData.TYPE_POPULATESUITE) + "</td>\n");
        html.append("</tr>\n");

        html.append("<tr>\n");
        html.append("  <td width=\"5%\" class=\"metricDeploy\" align=\"center\">D</td>\n");
        html.append("  <td width=\"95%\">" + getMetricTitle(CMnDbMetricData.TYPE_DEPLOY) + "</td>\n");
        html.append("</tr>\n");

        html.append("<tr>\n");
        html.append("  <td width=\"5%\" class=\"metricUnittest\" align=\"center\">U</td>\n");
        html.append("  <td width=\"95%\">" + getMetricTitle(CMnDbMetricData.TYPE_UNITTEST) + "</td>\n");
        html.append("</tr>\n");

        html.append("<tr>\n");
        html.append("  <td width=\"5%\" class=\"metricMigrate\" align=\"center\">M</td>\n");
        html.append("  <td width=\"95%\">" + getMetricTitle(CMnDbMetricData.TYPE_MIGRATE) + "</td>\n");
        html.append("</tr>\n");

        html.append("</table>\n");

        return html.toString();
    }

    /**
     * Construct a table header containing input fields.
     */
    private String getInputFields() {
        StringBuffer html = new StringBuffer();

        html.append("<table border=\"0\">\n");
        html.append("<tr>\n");
        html.append("  <td nowrap>" + buildWindowTag.toString() + "</td>\n");
        html.append("  <td align=\"center\"><input type=\"submit\" value=\"Go\"></a></td>\n");
        html.append("</tr>\n");
        html.append("</table>\n");

        return html.toString();
    }


    /**
     * Generate the table rows containing the list of builds.
     */
    private String getBuildList() {
        StringBuffer html = new StringBuffer();

        CMnDbBuildData currentBuild = null;
        CMnDbHostData currentHost = null;

        Enumeration list = buildList.elements();
        while (list.hasMoreElements()) {
            currentBuild = (CMnDbBuildData) list.nextElement();
            if (showInWindow(currentBuild)) {
                html.append(getBuildRow(currentBuild));
            }
        }


        return html.toString();
    }


    /**
     * Render the list of builds as a table row.
     *
     * @param   build   Build data 
     */
    private String getBuildRow(CMnDbBuildData build) {
        CMnDbHostData host = build.getHostData();

        StringBuffer row = new StringBuffer();
        row.append("<tr>\n");
        //row.append("  <td NOWRAP>" + host.getHostname() + "</td>\n");

        // Display the build version number
        String versionHref = buildUrl + "?" + BUILD_ID_LABEL + "=" + build.getId();
        row.append("  <td NOWRAP><tt><a href=\"" + versionHref + "\">" + build.getBuildVersion() + "</a></tt></td>\n");

        // Keep track of the date at the current column
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(windowStart);
        int colMinutes = getColumnMinutes();

        // Calculate the visual representation of the build execution 
        int currentCol = 0;
        int preCols = getStartCol(build) - 1;
        int buildCols = getColSpan(build);

        // Correct any invalid values
        if (preCols >= DEFAULT_COLUMN_COUNT) {
            preCols = DEFAULT_COLUMN_COUNT - 1;
        } else if (preCols < 0) {
            preCols = 0;
        }
        if (buildCols >= DEFAULT_COLUMN_COUNT) {
            buildCols = DEFAULT_COLUMN_COUNT;
        }

        // Calculate the remaining columns based on corrected values 
        int postCols = DEFAULT_COLUMN_COUNT - buildCols - preCols;

        // Display a cell representing the build execution
        if (preCols > 0) {
            row.append("  <td colspan=\"" + preCols + "\">&nbsp;</td>\n");
            calendar.add(GregorianCalendar.MINUTE, preCols * colMinutes);
            currentCol = currentCol + preCols;
        }
        for (int col = 0; col < buildCols; col++) {
            Date colStart = calendar.getTime();
            calendar.add(GregorianCalendar.MINUTE, colMinutes);
            Date colEnd = calendar.getTime();
            currentCol++;
            row.append(getBuildCol(build, colStart, colEnd));
        }
        if (postCols > 0) {
            row.append("  <td colspan=\"" + postCols + "\">&nbsp;</td>\n");
            calendar.add(GregorianCalendar.MINUTE, postCols * colMinutes);
        }

        // Display the total number of minutes for build execution
        long elapsedMillis = getElapsedTime(build.getStartTime(), build.getEndTime());
        if (elapsedMillis == Long.MAX_VALUE) {
            row.append("  <td colspan=\"2\" align=\"center\" NOWRAP>Incomplete</td>\n");
        } else {
            int elapsedTime = (int) ((elapsedMillis / 1000) / 60);
            if (elapsedTime > 60) {
                int elapsedHours = elapsedTime / 60;
                row.append("<td align=\"right\" width=\"2%\" NOWRAP>" + elapsedHours + "hr</td>\n");
            } else {
                row.append("<td align=\"right\" width=\"2%\" NOWRAP>&nbsp;</td>");
            }
            int elapsedMinutes = elapsedTime % 60;
            row.append("<td align=\"right\" width=\"2%\" NOWRAP>" + elapsedMinutes + "min</td>\n");
        }

        row.append("</tr>\n");
        return row.toString();
    }


    /**
     * Render the list of builds as a table row.
     *
     * @param   build   Build data
     * @param   start   Start date of the column
     * @param   end     End date of the column
     *
     * @return  String representing the column
     */
    private String getBuildCol(CMnDbBuildData build, Date start, Date end) {
        StringBuffer col = new StringBuffer();

        String colStyle = "metricNone";
        String colStr = "&nbsp;";
        String comment = "";
        Enumeration metrics = build.getMetrics().elements();
        while (metrics.hasMoreElements()) {
            CMnDbMetricData metric = (CMnDbMetricData) metrics.nextElement();
            Date metricStart = metric.getStartTime();
            Date metricEnd = metric.getEndTime();

            // Account for the case where the metric time is null
            if (metricStart == null) {
                // When the start is null, assume the task is zero-length
                metricStart = start;
                metricEnd = start;
            } else if (metricEnd == null) {
                // When the start is valid but the end is null, assume it is in progress
                metricEnd = end;
            }

            // Determine if the start and end date fall within at least 50% of
            // the specified column.
            float pct = getPercentOverlap(metricStart, metricEnd, start, end);
            int percent = (int) (pct * 100.0);
            if (percent > 50) {
                switch (metric.getType()) {
                    case CMnDbMetricData.TYPE_BUILD:          colStr = "B"; colStyle = "metricBuild"; break;
                    case CMnDbMetricData.TYPE_JAVADOC:        colStr = "J"; colStyle = "metricJavadoc"; break;
                    case CMnDbMetricData.TYPE_POPULATE:       colStr = "P"; colStyle = "metricPopulate"; break;
                    case CMnDbMetricData.TYPE_POPULATESUITE:  colStr = "S"; colStyle = "metricPopulateSuite"; break;
                    case CMnDbMetricData.TYPE_UNITTEST:       colStr = "U"; colStyle = "metricUnittest"; break;
                    case CMnDbMetricData.TYPE_MIGRATE:        colStr = "M"; colStyle = "metricMigrate"; break;
                    case CMnDbMetricData.TYPE_DEPLOY:         colStr = "D"; colStyle = "metricDeploy"; break;
                    default: colStr = "?"; break;
                }
            }
            comment = comment + " [" + metric.getType() + "=" + percent + "%]";
        }
        col.append("<td class=\"" + colStyle + "\" bgcolor=\"#9999CC\" align=\"center\" NOWRAP>" + colStr + "</td><!-- " + comment + " -->\n");

        return col.toString();
    }


    /**
     * Determine how far to indent the build from the start of the table. 
     *
     * @param   build    Build data
     *
     * @return  Number of columns before the start of the build 
     */
    private int getStartCol(CMnDbBuildData build) {
        // Determine the time difference between the window start and the build date
        if (build.getStartTime() != null) {
            GregorianCalendar startTime = new GregorianCalendar();
            GregorianCalendar endTime = new GregorianCalendar();
            startTime.setTime(windowStart);
            endTime.setTime(build.getStartTime());
            long elapsedMillis = endTime.getTimeInMillis() - startTime.getTimeInMillis();
            return getColSpan(elapsedMillis);
        } else {
            return 0;
        } 
    }

    /**
     * Determine the number of milliseconds that have elapsed between the 
     * start date and end date of the build.  If the start time is null
     * or after the end date, a value of -1 will be returned.  If the end 
     * time is null, a value * of 0 will be returned. 
     *
     * @param   start    Start time
     * @param   end      End time 
     *
     * @return  Number of milliseconds elapsed between the start and end of the build 
     */
    private long getElapsedTime(Date start, Date end) {
        if ((start != null) && (end != null)) {
            if (start.equals(end)) {
                return 0;
            } else if (start.before(end)) {
                // Construct calendar objects for operating on the start and end date
                GregorianCalendar startDate = new GregorianCalendar();
                GregorianCalendar endDate = new GregorianCalendar();
                startDate.setTime(start);
                endDate.setTime(end);

                // Determine how many minutes elapsed between the start and end date
                long startMillis = startDate.getTimeInMillis();
                long endMillis = endDate.getTimeInMillis();

                return endMillis - startMillis;
            } else {
                return -1;
            }
        } else if ((start == null) && (end == null)) {
            // Start and end time are both null, so they are equal
            return 0;
        } else if (start == null) {
            // Start time was not recorded and is considered infinitely negative (past)
            return -1;
        } else {
            // End time was not recorded and is considered infinitely possitive (future)
            return Long.MAX_VALUE;
        }

    }

    /**
     * Determine how many columns the build spans based on the
     * start and end date of the build.
     *
     * @param   build    Build data
     *
     * @return  Number of columns spanned by the build
     */
    private int getColSpan(CMnDbBuildData build) {
        Date start = build.getStartTime();
        Date end = build.getEndTime();

        // Correct the elapsed time when the build lies outside of the window boundaries
        if ((start == null) || start.before(windowStart)) {
            start = windowStart;
        }
        if ((end == null) || end.after(windowEnd)) {
            end = windowEnd;
        }

        long elapsedMillis = getElapsedTime(start, end);
        if ((elapsedMillis > 0) && (elapsedMillis < Long.MAX_VALUE)) {
            return getColSpan(elapsedMillis);
        } else if (elapsedMillis == Long.MAX_VALUE) {
            return DEFAULT_COLUMN_COUNT - getStartCol(build) + 1;
        } else {
            return 0;
        }
    }


    /**
     * Determine the number of columns spanned by a number of milliseconds.
     *
     * @param   millis    Number of milliseconds
     * @return  Number of columns
     */
    private int getColSpan(long millis) {
        int columnMinutes = getColumnMinutes();
        int elapsedMinutes = (int) ((millis / 1000) / 60);

        // Calculate the number of columns spanned by the elapsed minutes
        int cols = elapsedMinutes / columnMinutes;
        int remainder = elapsedMinutes % columnMinutes;

        // Account for the remainder minutes by allocating a column if
        // the remainder is greater than half a column
        if (remainder > (columnMinutes / 2)) {
            return cols + 1;
        } else {
            return cols;
        }
    }


    /**
     * Determine the percentage the limit range that is covered by the start and
     * end date. 
     *
     * @param   start         Start of the date range which must fall within the limit
     * @param   end           End of the date range which must fall within the limit
     * @param   startLimit    Start of the container boundary
     * @param   endLimit      End of the container boundary
     *
     * @return Percent of the limit range that is covered by the start and end date 
     */
    private float getPercentOverlap(Date start, Date end, Date startLimit, Date endLimit) {
        // Throw exceptions if
        //    any of the dates are null
        //    pct is not between 0 and 100
        //    start > end
        //    startLimit > endLimit

        // Convert the time to milliseconds from the epoch
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(start);
        long s1Millis = calendar.getTimeInMillis();
        calendar.setTime(end);
        long e1Millis = calendar.getTimeInMillis();
        calendar.setTime(startLimit);
        long s2Millis = calendar.getTimeInMillis();
        calendar.setTime(endLimit);
        long e2Millis = calendar.getTimeInMillis();

        long overlapMillis = 0;
        long limitMillis = e2Millis - s2Millis;
        if (start.before(startLimit) && end.before(startLimit)) {
            // Date range is outside of the limit boundaries
            overlapMillis = 0;
        } else if (start.after(endLimit) && end.after(endLimit)) {
            // Date range is outside of the limit boundaries
            overlapMillis = 0;
        } else if (start.before(startLimit) && end.after(endLimit)) {
            // Date range completely fills the limit boundaries
            // so use the limit boundaries to calculate overlap
            overlapMillis = e2Millis - s2Millis;
        } else if (start.after(startLimit) && end.before(endLimit)) {
            // Date range falls completely within the limit boundaries
            overlapMillis = e1Millis - s1Millis;
        } else if (start.after(startLimit) && end.after(endLimit)) {
            // Date range starts within the boundary but extends beyond it
            // so use the boundary upper limit when calculating the overlap
            overlapMillis = e2Millis - s1Millis;
        } else if (start.before(startLimit) && end.before(endLimit)) {
            // Date range starts outside of the boundary but ends within it
            // so use the boundary lower limit when calculating the overlap 
            overlapMillis = e1Millis - s2Millis;
        } else {
            // Both date ranges are equal
            overlapMillis = limitMillis;
        }

        Long overlap = new Long(overlapMillis);
        Long limit = new Long(limitMillis);

        return (overlap.floatValue() / limit.floatValue());
    }


    /**
     * Create a table which defines a title and border for the contents.
     *
     * @param   title   Text to be placed in the title bar
     * @param   content Content of the table
     */
    public String getTitledBorder(String content) {
        return getTitledBorder(DEFAULT_TITLE, content);
    }

}
