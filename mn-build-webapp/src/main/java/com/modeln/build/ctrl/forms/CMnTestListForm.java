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
import com.modeln.testfw.reporting.CMnTestGroup;
import com.modeln.testfw.reporting.CMnTestGroupComparator;
import com.modeln.testfw.reporting.CMnTestPriorityComparator;

import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;



/**
 * The suite form provides an HTML interface to a list of test objects. 
 * The class manages the display of individual tests in a list format. 
 * Form data can be rendered in either a read-only or input mode.
 *
 * @author  Shawn Stafford
 */
public class CMnTestListForm extends CMnBaseForm {

    /** Default title displayed in the bordered header */
    private static final String DEFAULT_TITLE = "Unit Test Results";

    /** List of tests to be rendered */
    private Hashtable<String,CMnTestGroup> testhash = new Hashtable<String,CMnTestGroup>();

    /**
     * Construct form for rendering the list of tests. 
     * 
     * @param  form    URL to use when submitting form input
     * @param  images  URL to use when displaying images
     * @param  tests   List of tests to be displayed
     */
    public CMnTestListForm(URL form, URL images, Vector<CMnDbTestData> tests) {
        super(form, images);

        // Construct a hashtable of the tests, using the display name as the hash key
        Enumeration list = tests.elements();
        while (list.hasMoreElements()) {
            CMnDbTestData data = (CMnDbTestData) list.nextElement();
            String key = data.getDisplayName();
            if (testhash.containsKey(key)) {
                CMnTestGroup group = (CMnTestGroup) testhash.get(key);
                group.addTest(data);
            } else {
                CMnTestGroup group = new CMnTestGroup(data.getDisplayName());
                group.addTest(data);
                testhash.put(key, group);
            }
        }
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
            html.append("<table width=\"100%\" cellspacing=\"0\" cellpadding=\"3\">\n");
            html.append(getHeader());
            html.append(getTestList());
            html.append("</table>\n");
            html.append("</form>\n");
        } catch (Exception ex) {
           html.append(ex.getMessage());

           html.append("JSP Exception: " + ex);
           html.append("<pre>\n");
           StackTraceElement[] lines = ex.getStackTrace();
           for (int idx = 0; idx < lines.length; idx++) {
               html.append(lines[idx] + "\n");
           }
           html.append("</pre>\n");

        }

        return html.toString();
    }


    /**
     * Construct a table header.
     */
    private String getHeader() {
        StringBuffer html = new StringBuffer();
        html.append("<tr>\n");
        html.append("  <td nowrap width=\"2%\">&nbsp;</td>\n");
        html.append("  <td nowrap width=\"80%\"><u>Test</u></td>\n");
        html.append("  <td nowrap width=\"10%\" align=\"right\"><u>Time</u></td>\n");
        html.append("  <td nowrap width=\"10%\" align=\"center\"><u>Status</u></td>\n");
        html.append("</tr>\n");
        return html.toString();
    }

    /**
     * Generate the table rows containing the list of tests.
     */
    private String getTestList() {
        StringBuffer html = new StringBuffer();

        // Construct a mutable list of test groups and sort the list
        Vector<CMnTestGroup> values = new Vector<CMnTestGroup>();
        Collection hashValues = testhash.values(); 
        if (hashValues != null) {
            Iterator groupIter = hashValues.iterator();
            while (groupIter.hasNext()) {
                values.add((CMnTestGroup) groupIter.next()); 
            }
            Collections.sort(values, new CMnTestGroupComparator());
        }

        // Display the test information
        if (values.size() > 0) {
            //
            // Iterate through each test group
            //
            int rowCount = 0;
            int testCount = 0;

            String rowColor = "";
            String statusColor = "";
            String timeColor = "";

            String rowClass = "";
            String statusClass = "";
            String timeClass = "";

            Enumeration groupList = values.elements();
            while (groupList.hasMoreElements()) {
                CMnTestGroup group = (CMnTestGroup) groupList.nextElement();
                rowCount++;

                // Make every other row visually distinct
                if ((rowCount % 2) == 0) {
                    rowColor = "#F8F8F8";
                    rowClass = "shadeEvenRow";
                } else {
                    rowColor = "#FFFFFF";
                    rowClass = "shadeOddRow";
                }

                //
                // Iterate through each test in the current group
                //
                int rowspan = group.getTestCount();
                Enumeration<CMnDbTestData> tests = group.getTests();
                CMnDbTestData currentTest = null;
                boolean firstTest = true;
                while (tests.hasMoreElements()) {
                    currentTest = (CMnDbTestData) tests.nextElement();
                    testCount++;

                    // Determine what colors to use to highlight the current row
                    if (currentTest.getStatus() == CMnDbTestData.ERROR) {
                        statusColor = "#FFCCCC";
                        statusClass = "errorColumn";
                    } else if (currentTest.getStatus() == CMnDbTestData.FAIL) {
                        statusColor = "#FFCCCC";
                        statusClass = "errorColumn";
                    } else if (currentTest.getStatus() == CMnDbTestData.KILL) {
                        statusColor = "#FFCCCC";
                        statusClass = "errorColumn";
                    } else if (currentTest.getStatus() == CMnDbTestData.SKIP) {
                        statusColor = "#FFFF33";
                        statusClass = "warningColumn";
                    } else {
                        statusColor = rowColor;
                        statusClass = rowClass; 
                    }

                    // Perform special highlighting for long running tests
                    if (currentTest.getElapsedTime() > 300000) {
                        timeColor = "#CCAAFF";
                        timeClass = "timingColumn";
                    } else {
                        timeColor = rowColor;
                        timeClass = rowClass;
                    }

                    // Generate a URL for navigating to the test details
                    String testUrl = getFormUrl() + "?"; 
                    //if (build != null) {
                    //    testUrl = testUrl + CMnBuildDataForm.BUILD_ID_LABEL + "=" + build.getId() + "&";
                    //}
                    if (currentTest.getParentId() > 0) {
                        testUrl = testUrl + IMnTestForm.SUITE_ID_LABEL + "=" + currentTest.getParentId() + "&";
                    }
                    testUrl = testUrl + IMnTestForm.TEST_ID_LABEL + "=" + currentTest.getId();

                    html.append("<tr>\n");
                    if (firstTest) {
                        html.append("  <td bgcolor=\"" + rowColor + "\" align=\"right\" rowspan=\"" + rowspan + "\">" + rowCount + "</td>\n");
                        html.append("  <td bgcolor=\"" + rowColor + "\" rowspan=\"" + rowspan + "\">" + group.getName());
                        if (rowspan > 1) {
                            html.append("&nbsp;&nbsp;(" + rowspan + " tests)");
                        }
                        html.append("</td>\n");
                    }
                    html.append("  <td bgcolor=\"" + timeColor + "\" align=\"right\">" + formatTime(currentTest.getElapsedTime()) + "</td>\n");
                    html.append("  <td bgcolor=\"" + statusColor + "\" align=\"center\"><a href=\"" + testUrl + "\">" + currentTest.getDisplayStatus() + "</a></td>\n");
                    html.append("</tr>\n");

                    firstTest = false;

                } // while has more tests

            } // while has more groups

        } else {
            html.append("<tr><td colspan=\"3\" align=\"center\">No Results Found</td></tr>\n");
        }

        return html.toString();
    }


}

