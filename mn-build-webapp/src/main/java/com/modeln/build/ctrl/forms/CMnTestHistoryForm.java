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

import com.modeln.testfw.reporting.CMnDbTestData;
import com.modeln.testfw.reporting.CMnTestPriorityComparator;

import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Vector;



/**
 * The suite form provides an HTML interface to a list of test objects. 
 * The class manages the display of individual tests in a list format. 
 * Form data can be rendered in either a read-only or input mode.
 *
 * @author  Shawn Stafford
 */
public class CMnTestHistoryForm extends CMnBaseForm {

    /** Default title displayed in the bordered header */
    private static final String DEFAULT_TITLE = "Test History";

    /** List of tests to be rendered */
    private Vector<CMnDbTestData> tests = null;

    /**
     * Construct form for rendering the list of tests. 
     * 
     * @param  form    URL to use when submitting form input
     * @param  images  URL to use when displaying images
     * @param  tests   List of tests to be displayed
     */
    public CMnTestHistoryForm(URL form, URL images, Vector<CMnDbTestData> tests) {
        super(form, images);
        this.tests = tests;
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
     * Render the build data form.
     */
    public String toString() {
        StringBuffer html = new StringBuffer();
        try {
            html.append("<form action=" + getFormUrl() + ">\n");
            html.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"3\">\n");
            html.append(getHeader());
            html.append(getTestList());
            html.append("</table>\n");
            html.append("</form>\n");
        } catch (Exception ex) {
           html.append(ex.getMessage());
        }

        return html.toString();
    }


    /**
     * Construct a table header.
     */
    private String getHeader() {
        StringBuffer html = new StringBuffer();
        html.append("<tr>\n");
        html.append("  <td nowrap class=\"tableheader\" width=\"5%\"  align=\"right\">Test ID</td>\n");
        html.append("  <td nowrap class=\"tableheader\" width=\"35%\" align=\"center\">Start Time</td>\n");
        html.append("  <td nowrap class=\"tableheader\" width=\"35%\" align=\"center\">End Time</td>\n");
        html.append("  <td nowrap class=\"tableheader\" width=\"10%\" align=\"right\">Elapsed Time</td>\n");
        html.append("  <td nowrap class=\"tableheader\" width=\"15%\" align=\"center\">Status</td>\n");
        html.append("</tr>\n");
        return html.toString();
    }

    /**
     * Generate the table rows containing the list of tests.
     */
    private String getTestList() {
        StringBuffer html = new StringBuffer();

        if (tests != null) {
            // Make sure the test list is sorted for display
            //Collections.sort(tests, new CMnTestPriorityComparator());

            int count = 0;
            Enumeration testList = tests.elements();
            while (testList.hasMoreElements()) {
                CMnDbTestData data = (CMnDbTestData) testList.nextElement();
                count++;

                // Determine what colors to use to highlight the current row
                String rowBg = null;
                String statusBg = null; 
                String timeBg = null;
                if (data.getStatus() == CMnDbTestData.ERROR) {
                    rowBg    = "errorRow";
                    statusBg = "errorColumn";
                } else if (data.getStatus() == CMnDbTestData.FAIL) {
                    rowBg    = "errorRow";
                    statusBg = "errorColumn";
                } else if (data.getStatus() == CMnDbTestData.KILL) {
                    rowBg    = "errorRow";
                    statusBg = "errorColumn";
                } else if (data.getStatus() == CMnDbTestData.SKIP) {
                    rowBg    = "warningRow";
                    statusBg = "warningColumn";
                }

                // Perform special highlighting for long running tests
                if (data.getElapsedTime() > 300000) {
                    timeBg = "timingColumn";
                    if (rowBg == null) {
                        rowBg = "timingRow";
                    }
                    if (statusBg == null) {
                        statusBg = "timingRow";
                    }
                } else {
                    timeBg = rowBg;
                }

                // Make sure we initialize the class values
                if (rowBg == null)    rowBg = "";
                if (statusBg == null) statusBg = "";
                if (timeBg == null)   timeBg = "";

                // Generate a URL for navigating to the test details
                String testUrl = getFormUrl() + "?"; 
                //if (build != null) {
                //    testUrl = testUrl + CMnBuildDataForm.BUILD_ID_LABEL + "=" + build.getId() + "&";
                //}
                if (data.getParentId() > 0) {
                    testUrl = testUrl + IMnTestForm.SUITE_ID_LABEL + "=" + data.getParentId() + "&";
                }
                testUrl = testUrl + IMnTestForm.TEST_ID_LABEL + "=" + data.getId();


                String startTime = null;
                if (data.getStartTime() != null) {
                    startTime = data.getStartTime().toString();
                } else {
                    startTime = "&nbsp;";
                }

                String endTime = null;
                if (data.getEndTime() != null) {
                    endTime = data.getEndTime().toString();
                } else {
                    endTime = "&nbsp;";
                }

                String elapsedTime = null;
                if ((data.getStartTime() != null) && (data.getEndTime() != null)) {
                    elapsedTime = formatTime(data.getElapsedTime());
                } else {
                    elapsedTime = "&nbsp;";
                }

                html.append("<tr>\n");
                html.append("  <td class=\"" + rowBg + "\" align=\"right\"><a href=\"" + testUrl + "\">" + data.getId() + "</a></td>\n");
                html.append("  <td class=\"" + rowBg + "\" align=\"center\">" + startTime + "</td>\n");
                html.append("  <td class=\"" + rowBg + "\" align=\"center\">" + endTime + "</td>\n");
                html.append("  <td class=\"" + timeBg + "\" align=\"right\">" + elapsedTime + "</td>\n");
                html.append("  <td class=\"" + statusBg + "\" align=\"center\">" + data.getDisplayStatus() + "</td>\n");
                html.append("</tr>\n");
            }
        } else {
            html.append("<tr><td colspan=\"3\" align=\"center\">No Results Found</td></tr>\n");
        }

        return html.toString();
    }


}

