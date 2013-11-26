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

import com.modeln.testfw.reporting.CMnBuildTable;
import com.modeln.testfw.reporting.CMnDbAcceptanceTestData;
import com.modeln.testfw.reporting.CMnDbBuildData;
import com.modeln.testfw.reporting.CMnDbBuildStatusData;
import com.modeln.testfw.reporting.CMnDbFlexTestData;
import com.modeln.testfw.reporting.CMnDbHostData;
import com.modeln.testfw.reporting.CMnDbMetricData;
import com.modeln.testfw.reporting.CMnDbTestData;
import com.modeln.testfw.reporting.CMnDbTestSummaryData;
import com.modeln.testfw.reporting.CMnDbUit;
import com.modeln.testfw.reporting.CMnDbUnitTestData;
import com.modeln.testfw.reporting.search.CMnSearchCriteria;
import com.modeln.testfw.reporting.search.CMnSearchGroup;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;
import java.util.Hashtable;
import javax.servlet.http.HttpServletRequest;
import com.modeln.build.web.tags.TextTag;
import com.modeln.build.web.tags.DateTag;
import com.modeln.build.web.tags.OptionTag;
import com.modeln.build.web.tags.TagGroup;


/**
 * The build form provides an HTML interface for specifying builds
 * to be compared to determined test result differences.
 * The class manages transfering the data through the HTTP request.
 * Form data can be rendered in either a read-only or input mode.
 * 
 * @author  Shawn Stafford
 */
public class CMnBuildDiffForm extends CMnBaseTableForm implements IMnBuildForm {

    /** Color used in the table headers */
    private static final String headerColor = "#C0C0C0";

    /** Color used in the table footers */
    private static final String footerColor = "#C0C0C0"; 


    /** List of build IDs used to identify the builds to be diffed */
    private OptionTag buildListTag;

    /** List of build data objects. */
    private Vector buildList = null;

    /**  URL for accessing build information */
    private URL buildUrl = null;

    /** URL for accessing test information */
    private URL unittestUrl = null;

    /** URL for accessing the ACT test information */
    private URL actUrl = null;

    /** URL for accessing the Flex test information */
    private URL flexUrl = null;

    /** URL for accessing the UI test information */
    private URL uitUrl = null;



    /**
     * Construct a base form.
     * 
     * @param  url    URL to use when submitting form input
     */
    public CMnBuildDiffForm(URL form, URL images, Vector builds) {
        super(form, images);
        buildList = builds;
	
        // Construct a list of builds
//        String selected = null;
        Hashtable buildItems = new Hashtable();
	if (buildList != null) {
            Enumeration list = buildList.elements();
	    while (list.hasMoreElements()) {
	        CMnDbBuildData build = (CMnDbBuildData) list.nextElement();
                String currentId = Integer.toString(build.getId());
                String displayName = build.getBuildVersion() + " (ID " + currentId + ")";
                buildItems.put(currentId, displayName);

                // Use the first value in the list as the default selection
//                if (selected == null) {
//                    selected = currentId;
//                }
            }
	}

        // Construct the form input elements
        buildListTag = new OptionTag(BUILD_ID_LABEL, buildItems);
        buildListTag.setMultiple(true);
        buildListTag.setAllSelected();
        buildListTag.setDisabled(true);
//        if (selected != null) {
//            buildListTag.setSelected(selected);
//        }

    }

    /**
     * Set the URL used to retrieve build status notes. 
     *
     * @param  url   Link to the command related to build status notes
     */
    public void setBuildUrl(URL url) {
        buildUrl = url;
    }

    /**
     * Set the URL used to retrieve test results. 
     *
     * @param  url   Link to the command related to test results 
     */
    public void setUnitTestUrl(URL url) {
        unittestUrl = url;
    }

    /**
     * Set the URL used to retrieve ACT test results. 
     *
     * @param  url   Link to the command related to test results 
     */
    public void setActTestUrl(URL url) {
        actUrl = url;
    }

    /**
     * Set the URL used to retrieve test results. 
     *
     * @param  url   Link to the command related to test results 
     */
    public void setFlexTestUrl(URL url) {
        flexUrl = url;
    }

    /**
     * Set the URL used to retrieve test results. 
     *
     * @param  url   Link to the command related to test results 
     */
    public void setUitTestUrl(URL url) {
        uitUrl = url;
    }



    /**
     * Extend the base method for enabling the input mode.  All input fields
     * must be enabled or disabled when this method is called. 
     *
     * @param enabled  TRUE to enable form input
     */
    public void setInputMode(boolean enabled) {
        super.setInputMode(enabled);
        buildListTag.setDisabled(!enabled);
        inputEnabled = enabled;
    }


    /**
     * Return the list of selected build IDs.
     *
     */
    public String[] getSelectedIds() {
        return buildListTag.getSelected();
    }

    /**
     * Return the build IDs corresponding to the list of builds. 
     *
     */
    public String[] getBuildIds() {
        String[] ids = null;
        if (buildList != null) {
            ids = new String[buildList.size()];
            for (int idx = 0; idx < ids.length; idx++) {
                CMnDbBuildData currentBuild = (CMnDbBuildData) buildList.get(idx);
                ids[idx] = Integer.toString(currentBuild.getId());
            }
        }
        return ids; 
    }

    /**
     * Set the input fields by examining the HTTP request to see if
     * a value was submitted.
     *
     * @param   req     HTTP request
     */
    public void setValues(HttpServletRequest req) {
        super.setValues(req);
        buildListTag.setValue(req);
    }

    /**
     * Construct a table header containing input fields.
     */
    private String getInputFields() {
        StringBuffer html = new StringBuffer();

        String[] keys = buildListTag.getKeyOrder();
        if ((keys != null) && (keys.length > 0)) {
            html.append("<tr>\n");
            html.append("  <td nowrap>" + buildListTag.toString() + "</td>\n");
            html.append("</tr>\n");
        }

        return html.toString();
    }

    /**
     * Construct a table body containing the diff information.
     */
    public String getBuildList() {
        StringBuffer html = new StringBuffer();

        if ((buildList != null) && (buildList.size() > 0)) {
            html.append("<!-- Build List: " + buildList.size() + " builds -->\n");

            // Define a data structure to map the build ID to a list of metrics for that build
            Hashtable metrics = new Hashtable(buildList.size());

            // Define a data structure to represent a summary of the test results
            Hashtable tests = new Hashtable(buildList.size()); 

            // Iterate through each build to collect data about the builds
            Enumeration list = buildList.elements();
            while (list.hasMoreElements()) {
                CMnDbBuildData build = (CMnDbBuildData) list.nextElement();

                // Collect build metrics about each build
                metrics.put(Integer.toString(build.getId()), build.getMetrics());

                // Collect the test summary for each build
                //testSummary.put(Integer.toString(build.getId()), build.getTestSummary());

                // Collect the test results for each build
                tests.put(Integer.toString(build.getId()), build.getTestData());
            }

            // Display the build metric comparison
            html.append(metricsToString(getBuildIds(), metrics));

            // Display the build test summary comparison

            // Display the build test comparison
            html.append(testsToString(getBuildIds(), tests));

        } else {
            html.append("  <td nowrap>\n");
            html.append("    Input build IDs to compare:\n");
            html.append("    <form method=\"POST\" action=\"" + getFormUrl() + "\">\n");
            html.append("    <table border=\"0\" cellspacing=\"1\" cellpadding=\"0\">\n");
            html.append("      <tr>\n");
            html.append("        <td>Build ID:</td>\n");
            html.append("        <td><input type=\"text\" name=\"" + BUILD_ID_LABEL + "\">\n");
            html.append("      </tr>\n");
            html.append("      <tr>\n");
            html.append("        <td>Build ID:</td>\n");
            html.append("        <td><input type=\"text\" name=\"" + BUILD_ID_LABEL + "\">\n");
            html.append("      </tr>\n");
            html.append("      <tr>\n");
            html.append("        <td>&nbsp;</td>\n");
            html.append("        <td><input type=\"submit\">\n");
            html.append("      </tr>\n");
            html.append("    </table>\n");
            html.append("    </form>\n");
            html.append("  </td>\n");
        }

        return html.toString();
    }


    /**
     * Compare the build metrics for each build.
     *
     * @param metrics  Hashtable containing build IDs and the corresponding build metrics.
     * @return HTML table representing the comparison data
     */
    public String metricsToString(String[] buildIds, Hashtable metrics) {
        StringBuffer html = new StringBuffer();

        // Construct a list of metric names
        Vector metricNames = new Vector();
        Enumeration list = metrics.keys();
        while (list.hasMoreElements()) {
            Object buildId = list.nextElement();

            // Iterate through the metrics to get the name for each one
            Vector metricList = (Vector) metrics.get(buildId);
            html.append("<!-- " + metricList.size() + " metrics found for Build ID " + buildId + " -->\n");
            Enumeration mlist = metricList.elements();
            while (mlist.hasMoreElements()) {
                CMnDbMetricData currentMetric = (CMnDbMetricData) mlist.nextElement(); 
                if (currentMetric != null) {
                    String metricName = CMnDbMetricData.getMetricType(currentMetric.getType()); 
                    if (!metricNames.contains(metricName)) {
                        metricNames.add(metricName);
                    }
                }
            }
        }


        // Display a table row for each metric
        if (metricNames.size() > 0) {
            html.append("<table border=1 width=\"50%\">\n");

            // Display the table header
            html.append("  <tr>\n");
            html.append("  <td bgcolor=\"" + headerColor + "\"><b>Metric</b></td>\n");
            for (int idx = 0; idx < buildIds.length; idx++) {
                html.append("    <td bgcolor=\"" + headerColor + "\" align=\"center\" width=\"100\">");
                String buildId = buildIds[idx];
                if (buildUrl != null) {
                    html.append("<a href=\"" + buildUrl + "?" + BUILD_ID_LABEL + "=" + buildId + "\">" + buildId + "</a>");
                } else {
                    html.append(buildId);
                }
                html.append("</td>\n");
            }
            html.append("  </tr>\n");

            // Display the table body
            Enumeration names = metricNames.elements();
            while (names.hasMoreElements()) {
                String name = (String) names.nextElement();
                html.append("  <tr>\n");
                html.append("    <td>" + name + "</td>\n");

                // Display the metric value for each build
                for (int idx = 0; idx < buildIds.length; idx++) {
                    String buildId = buildIds[idx];
                    Vector metricList = (Vector) metrics.get(buildId);
                    if (metricList != null) {
                        Enumeration mlist = metricList.elements();
                        while (mlist.hasMoreElements()) {
                            CMnDbMetricData currentMetric = (CMnDbMetricData) mlist.nextElement();
                            if (currentMetric != null) {
                                String metricName = CMnDbMetricData.getMetricType(currentMetric.getType());
                                if (metricName.equals(name)) {
                                    // Calculate the duration of the metric
                                    html.append("    <td align=right>" + currentMetric.getElapsedTimeString() + "</td>\n");
                                }
                            }
                        }
                    }
                    
                }

                html.append("  </tr>\n");
            }

            html.append("</table>\n");
        } else {
            html.append("<tr><td>No metrics available.</td></tr>\n");
        }

        return html.toString();
    }

    /**
     * Render the list of tests as a table comparing the results of each test.
     *
     * @param   title      Title to display in the table header
     * @param   buildIds   List of build IDs
     * @param   totals     Total number of tests associated with each build
     * @param   tests      Hashtable containig the test name and a list of results
     * @return  HTML table representing the test result comparison
     */
    private String testTypeToString(String title, String[] buildIds, Integer[] totals, Hashtable tests) {
        StringBuffer html = new StringBuffer();

        html.append("<table border=\"1\" width=\"90%\">\n");

        // Display the table header
        html.append("  <tr>\n");
        html.append("    <td bgcolor=\"" + headerColor + "\"><b>" + title + "</b></td>\n");
        for (int idx = 0; idx < buildIds.length; idx++) {
            html.append("    <td bgcolor=\"" + headerColor + "\" width=\"50\" align=\"center\">");
            html.append("<a href=\"" + buildUrl + "?" + BUILD_ID_LABEL + "=" + buildIds[idx] + "\">" + buildIds[idx] + "</a>");
            html.append("</td>\n");
        }
        html.append("  </tr>\n");

        // Iterate through the list of tests
        Enumeration keys = tests.keys();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            CMnDbTestData[] values = (CMnDbTestData[]) tests.get(key);

            // Determine if the test results differ across builds
            boolean resultsDiffer = false;
            boolean containsFailure = false;
            if (values.length > 0) {
                int expectedResult = CMnDbTestData.UNKNOWN_STATUS; 
                int actualResult = CMnDbTestData.UNKNOWN_STATUS;
                for (int idx = 0; idx < values.length; idx++) {
                    // Set the expected value for the row to the first item in the list
                    if ((idx == 0) && (values[idx] != null)) {
                        expectedResult = values[idx].getStatus();
                    } 

                    // Determine the actual value of the current item
                    if (values[idx] != null) {
                        actualResult = values[idx].getStatus();
                        if ((actualResult == CMnDbTestData.ERROR) || (actualResult == CMnDbTestData.FAIL)) {
                            containsFailure = true;
                        }
                    } else {
                        actualResult = CMnDbTestData.UNKNOWN_STATUS;
                    }

                    // Determine if the remaining values in the list match the first value
                    if (expectedResult != actualResult) {
                        resultsDiffer = true;
                    }
                }
            }

            // Display the results if there are differences for the current test
            if (resultsDiffer && containsFailure) {
                html.append("  <tr>\n");
                html.append("    <td>" + key + "</td>\n");
                for (int idx = 0; idx < buildIds.length; idx++) {
                    CMnDbTestData data = values[idx]; 
                    if (data != null) {
                        // Construct the URL based on the test type
                        String currentUrl = null;
                        if (data instanceof CMnDbUnitTestData) {
                            currentUrl = unittestUrl.toString();
                        } else if (data instanceof CMnDbUit) {
                            currentUrl = uitUrl.toString();
                        } else if (data instanceof CMnDbFlexTestData) {
                            currentUrl = flexUrl.toString();
                        } else if (data instanceof CMnDbAcceptanceTestData) {
                            currentUrl = actUrl.toString();
                        }
                        // Add the build and test information to the URL
                        currentUrl = currentUrl + "?" +
                            BUILD_ID_LABEL + "=" + buildIds[idx] + "&" +
                            IMnTestForm.SUITE_ID_LABEL + "=" + data.getParentId() + "&" +
                            IMnTestForm.TEST_ID_LABEL + "=" + data.getId();

                        // Set the row color based on the test status
                        int status = data.getStatus();
                        String text = null;
                        String color = null;
                        switch (status) {
                            case CMnDbTestData.ERROR:
                                text = "Error"; 
                                color = "#FF3333";
                                break;
                            case CMnDbTestData.PASS:
                                text = "Pass";
                                color = "#00FF00";
                                break;
                            case CMnDbTestData.FAIL:
                                text = "Fail";
                                color = "#FF3333";
                                break;
                            case CMnDbTestData.SKIP:
                                text = "Skip";
                                color = "#808080";
                                break;
                            case CMnDbTestData.KILL:
                                text = "Kill";
                                color = "#800000";
                                break;
                            case CMnDbTestData.RUNNING:
                                text = "Running";
                                color = "#33FF33";
                                break;
                            case CMnDbTestData.PENDING:
                                text = "Pending";
                                color = "#000000";
                                break;
                            case CMnDbTestData.BLACKLIST:
                                text = "?";
                                color = "#C0C0C0";
                                break;
                            case CMnDbTestData.UNKNOWN_STATUS:
                                text = "?";
                                color = "#FF0000";
                                break;
                            default:
                                text = "?";
                                color = "#FF0000";
                                break;
                        }

                        html.append("<td bgcolor=\"" + color + "\" align=\"center\"><a href=\"" + currentUrl + "\">" + text + "</a></td>");
                    } else {
                        html.append("<td>&nbsp;</td>");
                    }

                } // for each build
                html.append("  </tr>\n");

            } // if results differ

        } // while keys has more elements

        // Display the total number of tests associated with each build
        html.append("  <tr><td bgcolor=\"" + footerColor + "\" align=\"right\"><b>Total:</b></td>");
        for (int idx = 0; idx < buildIds.length; idx++) {
            html.append("<td bgcolor=\"" + footerColor + "\" align=\"right\"><b>" + totals[idx] + "</b></td>");
        }
        html.append("</tr>\n");

        html.append("</table>\n");

        return html.toString();
    } 


    /**
     * Compare the build test results for each build.
     *
     * @param tests  Hashtable containing build IDs and the corresponding tests 
     * @return HTML table representing the comparison data
     */
    private String testsToString(String[] buildIds, Hashtable tests) {
        StringBuffer html = new StringBuffer();

        // "tests" is a Hastable of
        // buildId (String) -> tests (Enumeration)

        html.append("<h2>Test Results</h2>");

        // Map the suite ID to the build ID
        Hashtable suiteIds = new Hashtable();

        // Store the test data in a hashtable so it can be looked up by name
        // Each entry in the hashtable will contain a list of tests associated with that name 
        Hashtable unittests = new Hashtable();
        Hashtable flextests = new Hashtable();
        Hashtable uittests  = new Hashtable();
        Hashtable acttests  = new Hashtable(); 

        // Record the total number of tests in each test group
        Integer[] unittestCount = new Integer[buildIds.length];
        Integer[] flextestCount = new Integer[buildIds.length];
        Integer[] uittestCount = new Integer[buildIds.length];
        Integer[] acttestCount = new Integer[buildIds.length];

        // Iterate through the list of builds to group the data
        for (int idx = 0; idx < buildIds.length; idx++) {
            String buildId = buildIds[idx];

            // Initialize the test count
            unittestCount[idx] = new Integer(0);
            flextestCount[idx] = new Integer(0);
            uittestCount[idx]  = new Integer(0);
            acttestCount[idx]  = new Integer(0);

            Enumeration testList = (Enumeration) tests.get(buildId);
            if (testList != null) {

                // Iterate through the list of tests for the current build
                while (testList.hasMoreElements()) {
                    CMnDbTestData data = (CMnDbTestData) testList.nextElement();
                    if (data != null) {
                        String suiteId = Integer.toString(data.getParentId());

                        // Map the current suite ID to a build
                        if (!suiteIds.containsKey(suiteId)) {
                            suiteIds.put(suiteId, buildId);
                        }

                        // Store the tests by type
                        CMnDbTestData[] results = new CMnDbTestData[buildIds.length];
                        String key = null;
                        if (data instanceof CMnDbUnitTestData) {
                            key = ((CMnDbUnitTestData)data).getClassName() + "." + ((CMnDbUnitTestData)data).getMethodName();
                            if (unittests.containsKey(key)) {
                                results = (CMnDbTestData[])unittests.get(key);
                            } else {
                                results = new CMnDbTestData[buildIds.length];
                            }
                            results[idx] = data;
                            unittests.put(key, results);
                            unittestCount[idx]++;
                        } else if (data instanceof CMnDbUit) {
                            key = ((CMnDbUit)data).getTestName();
                            if (uittests.containsKey(key)) {
                                results = (CMnDbTestData[])uittests.get(key);
                            } else {
                                results = new CMnDbTestData[buildIds.length];
                            }
                            results[idx] = data;
                            uittests.put(key, results);
                            uittestCount[idx]++;
                        } else if (data instanceof CMnDbFlexTestData) {
                            key = ((CMnDbFlexTestData)data).getClassName() + "." + ((CMnDbFlexTestData)data).getMethodName();
                            if (flextests.containsKey(key)) {
                                results = (CMnDbTestData[])flextests.get(key);
                            } else {
                                results = new CMnDbTestData[buildIds.length];
                            }
                            results[idx] = data;
                            flextests.put(key, results);
                            flextestCount[idx]++;
                        } else if (data instanceof CMnDbAcceptanceTestData) {
                            key = ((CMnDbAcceptanceTestData)data).getScriptName();
                            if (acttests.containsKey(key)) {
                                results = (CMnDbTestData[])acttests.get(key);
                            } else {
                                results = new CMnDbTestData[buildIds.length];
                            }
                            results[idx] = data;
                            acttests.put(key, results);
                            acttestCount[idx]++;
                        }

                    } // if data != null

                } // while testList has more elements

            } // if testList null

        } // for loop

        // Display the test comparison for each group
        if (unittests.size() > 0) {
            html.append("<p>\n");
            html.append(testTypeToString("Unit Tests", buildIds, unittestCount, unittests));
            html.append("</p>\n");
        }
        if (acttests.size() > 0) {
            html.append("<p>\n");
            html.append(testTypeToString("ACT Tests", buildIds, acttestCount, acttests));
            html.append("</p>\n");
        }
        if (flextests.size() > 0) {
            html.append("<p>\n");
            html.append(testTypeToString("Flex Tests", buildIds, flextestCount, flextests));
            html.append("</p>\n");
        }
        if (uittests.size() > 0) {
            html.append("<p>\n");
            html.append(testTypeToString("QTP Tests", buildIds, uittestCount, uittests));
            html.append("</p>\n");
        }

        return html.toString();
    }



    /**
     * Render the build data form.
     */
    public String toString() {
        StringBuffer html = new StringBuffer();

        try {
            html.append("<form action=" + getFormUrl() + ">\n");
            html.append("<table width=\"100%\" cellspacing=\"0\" cellpadding=\"1\">\n");
            html.append("<p>" + getInputFields() + "</p>\n");
            html.append(getBuildList());
            html.append("</table>\n");
            html.append("</form>\n");
        } catch (Exception ex) {
           html.append(ex.getMessage());
        }

        return html.toString();
    }


}


