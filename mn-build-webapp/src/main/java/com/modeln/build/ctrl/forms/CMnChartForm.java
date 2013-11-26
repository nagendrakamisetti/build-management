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

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.servlet.http.HttpServletRequest;

import com.modeln.build.web.tags.DateTag;
import com.modeln.build.web.tags.OptionTag;
import com.modeln.build.web.tags.SelectTag;


/**
 * The patch chart form provides an easy way for the user to
 * input chart information such as chart size, date range, etc. 
 * 
 * @author  Shawn Stafford
 */
public class CMnChartForm extends CMnBaseForm {

    /** Default title used when displaying a title and border */
    private static final String DEFAULT_TITLE = "Chart Parameters";

    /** Label to identify the data format to output (png, csv) */
    public static final String FORMAT_LABEL = "fmt";

    /** Label to identify the chart height value in the http request */
    public static final String HEIGHT_LABEL = "height";

    /** Label to identify the chart width value in the http request */
    public static final String WIDTH_LABEL = "width";

    /** Label to identify the start date value in the http request */
    public static final String START_DATE_LABEL = "startDate";

    /** Label to identify the end date value in the http request */
    public static final String END_DATE_LABEL = "endDate";

    /** Label to identify the chart to be displayed */
    public static final String CHART_LABEL = "chart";


    /** Display the chart as a PNG image file */
    public static final String FORMAT_PNG = "png";

    /** Display the data as a CSV spreadsheet */
    public static final String FORMAT_CSV = "csv";


    /** Date format used to specify chart ranges */
    public static final SimpleDateFormat DATE = new SimpleDateFormat("yyyy-MM-dd");


    /** Patch data start date */
    private DateTag startDateTag = null;

    /** Patch data end date */
    private DateTag endDateTag = null;


    /** Chart format */
    private SelectTag formatTag = null;

    /** Chart width */
    private SelectTag widthTag = null;

    /** Chart height */
    private SelectTag heightTag = null;


    /** Chart commands */
    private SelectTag chartTag = null;


    /**
     * Construct a form for getting chart parameters 
     *
     * @param    form     URL for submitting form data
     * @param    images   URL for displaying HTML images
     */
    public CMnChartForm(URL form, URL images) {
        super(form, images);

        GregorianCalendar now = new GregorianCalendar();
        GregorianCalendar startRange = new GregorianCalendar();
        GregorianCalendar endRange = new GregorianCalendar();
        int year = now.get(GregorianCalendar.YEAR);
        startRange.set(GregorianCalendar.YEAR, year - 1);
        endRange.set(GregorianCalendar.YEAR, year + 1);

        startDateTag = new DateTag(START_DATE_LABEL, startRange.getTime(), endRange.getTime(), false);
        endDateTag   = new DateTag(END_DATE_LABEL,   startRange.getTime(), endRange.getTime(), false);

        formatTag = new SelectTag(FORMAT_LABEL);
        formatTag.addOption(FORMAT_PNG, "Chart");
        formatTag.addOption(FORMAT_CSV, "Data");

        String[] sizes = { "100", "200", "300", "400", "500", "600", "700", "800", "900" };
        widthTag = new SelectTag(WIDTH_LABEL);
        widthTag.setOptions(sizes);
        widthTag.setDefault("600");

        heightTag = new SelectTag(HEIGHT_LABEL);
        heightTag.setOptions(sizes);
        heightTag.setDefault("400");

        chartTag = new SelectTag(CHART_LABEL);
    }


    /**
     * Extend the base method for enabling the input mode.  All input fields
     * must be enabled or disabled when this method is called.
     *
     * @param enabled  TRUE to enable form input
     */
    public void setInputMode(boolean enabled) {
        super.setInputMode(enabled);
        startDateTag.setDisabled(!enabled);
        endDateTag.setDisabled(!enabled);
        formatTag.setDisabled(!enabled);
        heightTag.setDisabled(!enabled);
        widthTag.setDisabled(!enabled);
        chartTag.setDisabled(!enabled);
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

    /**
     * Determine if the start and end date have been set.
     * 
     * @return TRUE if the dates are set
     */
    public boolean hasDateRange() {
        return (startDateTag.isComplete() && endDateTag.isComplete());
    }

    /**
     * Return the chart type selected by the user.
     *
     * @return  Chart url
     */
    public void addChartType(URL url, String name) {
        chartTag.addOption(name, url.toString());
    }

    /**
     * Return the chart format.
     *
     * @return  Chart format
     */
    public String getChartFormat() {
        String fmt = null;

        String[] selected = formatTag.getSelected();
        if ((selected != null) && (selected.length > 0)) {
            fmt = selected[0];
        }
 
        return fmt; 
    }

    /**
     * Return the full URL to access the chart.
     *
     * @return URL to access the chart
     */
    public URL getChartUrl() {
        URL url = null;
        String[] charts = chartTag.getSelected();
        if ((charts != null) && (charts.length > 0)) {
            StringBuffer query = new StringBuffer();

            if (startDateTag.isComplete()) {
                if (query.length() > 0) query.append("&");
                query.append(START_DATE_LABEL + "=" + DATE.format(startDateTag.getDate()));
            }

            if (endDateTag.isComplete()) {
                if (query.length() > 0) query.append("&");
                query.append(END_DATE_LABEL + "=" + DATE.format(endDateTag.getDate()));
            }

            String[] height = heightTag.getSelected();
            if ((height != null) && (height.length > 0)) {
                if (query.length() > 0) query.append("&");
                query.append(HEIGHT_LABEL + "=" + height[0]);
            }

            String[] width = widthTag.getSelected();
            if ((width != null) && (width.length > 0)) {
                if (query.length() > 0) query.append("&");
                query.append(WIDTH_LABEL + "=" + width[0]);
            }

            try {
                String base = charts[0];
                String delimiter = "?";
                if ((base != null) && (base.contains("?"))) {
                    delimiter = "&";
                }
                url = new URL(charts[0] + delimiter + query.toString());
            } catch (MalformedURLException mfex) {
            }
        }

        return url; 
    }

    /**
     * Set the input fields by examining the HTTP request to see if
     * a value was submitted.
     *
     * @param   req     HTTP request
     */
    public void setValues(HttpServletRequest req) {
        super.setValues(req);
        chartTag.setValue(req);
        formatTag.setValue(req);
        heightTag.setValue(req);
        widthTag.setValue(req);
        startDateTag.setValue(req);
        endDateTag.setValue(req);
    }

    /**
     * Set the start and end dates.
     *
     * @param  start   Starting date
     * @param  end     Ending date
     */
    public void setDateRange(Date start, Date end) {
        GregorianCalendar calStart = new GregorianCalendar();
        calStart.setTime(start);
        startDateTag.setDate(calStart);

        GregorianCalendar calEnd = new GregorianCalendar();
        calEnd.setTime(end);
        endDateTag.setDate(calEnd);
    }


    /**
     * Render the build data form.
     */
    public String toString() {
        StringBuffer html = new StringBuffer();

        boolean allowInput = getInputMode();

        html.append("<table width=\"100%\" cellspacing=\"0\" cellpadding=\"2\">\n");

        html.append("  <tr>\n");
        html.append("    <td width=\"20%\" align=\"right\">Chart Content:</td>\n");
        html.append("    <td width=\"80%\">");
        if (allowInput) {
            html.append(chartTag.toString());
        } else {
            if (chartTag.getSelected() != null) {
                html.append(chartTag.getSelected());
            }
        }
        html.append("</td>\n");
        html.append("  </tr>\n");


        html.append("  <tr>\n");
        html.append("    <td width=\"20%\" align=\"right\">Chart Size:</td>\n");
        html.append("    <td width=\"80%\">");
        if (allowInput) {
            html.append(heightTag.toString());
            html.append("H");
            html.append(" x ");
            html.append(widthTag.toString());
            html.append("W");
        } else {
            if (heightTag.getSelected() != null) {
                html.append(heightTag.getSelected());
                html.append("H");
            }
            if ((heightTag.getSelected() != null) && (widthTag.getSelected() != null)) {
                html.append(" x ");
            }
            if (widthTag.getSelected() != null) {
                html.append(widthTag.getSelected());
                html.append("W");
            }
        }
        html.append("</td>\n");
        html.append("  </tr>\n");

        /*
        html.append("  <tr>\n");
        html.append("    <td width=\"20%\" align=\"right\">Chart Format:</td>\n");
        html.append("    <td width=\"80%\">");
        if (allowInput) {
            html.append(formatTag.toString());
        } else {
            if (formatTag.getSelected() != null) {
                html.append(formatTag.getSelected());
            }
        }
        html.append("</td>\n");
        html.append("  </tr>\n");
        */


        html.append("  <tr>\n");
        html.append("    <td width=\"20%\" align=\"right\">Start Date:</td>\n");
        html.append("    <td width=\"80%\">"); 
        if (allowInput) { 
            html.append(startDateTag.toString());
        } else {
            if (startDateTag.isComplete() && (startDateTag.getDate() != null)) {
                Date startDate = startDateTag.getDate();
                html.append(startDate.toString());
            }
        } 
        html.append("</td>\n");
        html.append("  </tr>\n");

        html.append("  <tr>\n");
        html.append("    <td width=\"20%\" align=\"right\">End Date:</td>\n");
        html.append("    <td width=\"80%\">"); 
        if (allowInput) { 
            html.append(endDateTag.toString());
        } else {
            if (endDateTag.isComplete() && (endDateTag.getDate() != null)) {
                Date endDate = endDateTag.getDate();
                html.append(endDate.toString());
            }
        } 
        html.append("</td>\n");
        html.append("  </tr>\n");

        // Display a link to download the data as CSV
        URL chartUrl = getChartUrl();
        if (chartUrl != null) {
            String csvLink = chartUrl.toString() + "&" + FORMAT_LABEL + "=" + FORMAT_CSV;
            html.append("  <tr>\n");
            html.append("    <td width=\"20%\" align=\"right\">Data Formats:</td>\n");
            html.append("    <td width=\"80%\">");
            html.append("<a href=\"" + csvLink + "\">CSV</a>");
            html.append("</td>\n");
            html.append("  </tr>\n");
        }


        html.append("</table>\n");

        return html.toString();
    }

}

