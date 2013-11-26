/*
* Copyright 2000-2003 by Model N, Inc.  All Rights Reserved.
*
* This software is the confidential and proprietary information
* of Model N, Inc ("Confidential Information").  You shall not
* disclose such Confidential Information and shall use it only
* in accordance with the terms of the license agreement you
* entered into with Model N, Inc.
*/
package com.modeln.build.ctrl.charts;

import com.modeln.testfw.reporting.CMnBuildIdComparator;
import com.modeln.testfw.reporting.CMnDbBuildData;
import com.modeln.testfw.reporting.CMnDbMetricData;
import com.modeln.testfw.reporting.CMnDbHostData;
import com.modeln.testfw.reporting.CMnDbBuildStatusData;
import com.modeln.testfw.reporting.CMnDbFeatureOwnerData;
import com.modeln.testfw.reporting.CMnDbMetricData;
import com.modeln.testfw.reporting.CMnDbTestSuite;
import com.modeln.testfw.reporting.CMnDbTestSummaryData;
import com.modeln.testfw.reporting.CMnMetricDateComparator;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.urls.CategoryURLGenerator;
import org.jfree.chart.urls.StandardCategoryURLGenerator;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.general.DefaultPieDataset;



/**
 * The build list chart contains methods for rendering charts which present
 * various aspects of multiple builds. 
 *
 * @author             Shawn Stafford
 *
 */
public class CMnBuildListChart {

    /** Create an output channel for writing debug messages to file */
    private static PrintStream debug = null; 

    /** Construct an instance of the chart formatter */
    private static final CMnChartFormatter chartFormatter = new CMnChartFormatter();


    /** Write debugging messages to a file */
    private static final void debugWrite(String str) {
        if (debug == null) {
            try {
                debug = new PrintStream("/var/tmp/chart.txt");
            } catch (FileNotFoundException nfex) {
            }
        }

        if (debug != null) {
            debug.println(str);
            debug.flush();
        }
    }



    /**
     * Return rendering information for the chart.
     */
    public static final ChartRenderingInfo getMetricRenderingInfo() {
        return new ChartRenderingInfo(new StandardEntityCollection());
    }
 
    /**
     * Generate a stacked bar graph representing build execution times across 
     * all builds in the list. 
     *
     * @param   builds  List of builds 
     * 
     * @return  Stacked graph representing build execution times across all builds 
     */
    public static final JFreeChart getMetricChart(Vector<CMnDbBuildData> builds) {
        JFreeChart chart = null;

        // Get a list of all possible build metrics
        int[] metricTypes = CMnDbMetricData.getAllTypes();

        DefaultCategoryDataset dataset = new DefaultCategoryDataset(); 
        if ((builds != null) && (builds.size() > 0)) {

            // Collect build metrics for each of the builds in the list 
            Collections.sort(builds, new CMnBuildIdComparator());
            Enumeration buildList = builds.elements();
            while (buildList.hasMoreElements()) {

                // Process the build metrics for the current build
                CMnDbBuildData build = (CMnDbBuildData) buildList.nextElement(); 
                Vector metrics = build.getMetrics();
                if ((metrics != null) && (metrics.size() > 0)) {
                    // Sort the list of metrics to ensure they are displayed on the chart in a sensible order
                    Collections.sort(metrics, new CMnMetricDateComparator());

                    // Collect data for each of the build metrics in the current build 
                    Enumeration metricList = metrics.elements();
                    while (metricList.hasMoreElements()) {
                        CMnDbMetricData currentMetric = (CMnDbMetricData) metricList.nextElement();
                        // Get elapsed time in "minutes"
                        Long elapsedTime = new Long(currentMetric.getElapsedTime() / (1000*60));
                        Integer buildId = new Integer(build.getId());
                        String metricName = CMnDbMetricData.getMetricType(currentMetric.getType());
                        dataset.addValue(elapsedTime, metricName, buildId);

                    } // while build has metrics

                } // if has metrics

            } // while list has elements

        } // if list has elements

        // API: ChartFactory.createStackedBarChart(title, domainAxisLabel, rangeAxisLabel, dataset, orientation, legend, tooltips, urls)
        chart = ChartFactory.createStackedBarChart("Build Metrics", "Builds", "Execution Time (min)", dataset, PlotOrientation.VERTICAL, true, true, false);

        // get a reference to the plot for further customization...
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        chartFormatter.formatMetricChart(plot, dataset);

        return chart;
    }



    /**
     * Return rendering information for the chart.
     */
    public static final ChartRenderingInfo getAverageMetricRenderingInfo() {
        return new ChartRenderingInfo(new StandardEntityCollection());
    }

    /**
     * Generate a pie graph representing average build execution times across 
     * all builds in the list. 
     *
     * @param   builds  List of builds 
     * 
     * @return  Pie graph representing build execution times across all builds 
     */
    public static final JFreeChart getAverageMetricChart(Vector<CMnDbBuildData> builds) {
        JFreeChart chart = null;

        // Get a list of all possible build metrics
        int[] metricTypes = CMnDbMetricData.getAllTypes();
        Hashtable metricAvg = new Hashtable(metricTypes.length);

        DefaultPieDataset dataset = new DefaultPieDataset(); 
        if ((builds != null) && (builds.size() > 0)) {

            // Collect build metrics for each of the builds in the list 
            Enumeration buildList = builds.elements();
            while (buildList.hasMoreElements()) {

                // Process the build metrics for the current build
                CMnDbBuildData build = (CMnDbBuildData) buildList.nextElement();
                Vector metrics = build.getMetrics();
                if ((metrics != null) && (metrics.size() > 0)) {
                    // Collect data for each of the build metrics in the current build 
                    Enumeration metricList = metrics.elements();
                    while (metricList.hasMoreElements()) {
                        CMnDbMetricData currentMetric = (CMnDbMetricData) metricList.nextElement();
                        // Get elapsed time in "minutes"
                        Long elapsedTime = new Long(currentMetric.getElapsedTime() / (1000*60));
                        String metricName = CMnDbMetricData.getMetricType(currentMetric.getType());
                        Long avgValue = null;
                        if (metricAvg.containsKey(metricName)) {
                            Long oldAvg = (Long) metricAvg.get(metricName);
                            avgValue = oldAvg + elapsedTime;
                        } else {
                            avgValue = elapsedTime;
                        }
                        metricAvg.put(metricName, avgValue);

                    } // while build has metrics

                } // if has metrics

            } // while list has elements

            // Populate the data set with the average values for each metric
            Enumeration keys = metricAvg.keys();
            while (keys.hasMoreElements()) {
                String key = (String) keys.nextElement();
                Long total = (Long) metricAvg.get(key);
                Long avg = new Long(total.longValue() / (long) builds.size());
                //dataset.setValue(key, (Long) metricAvg.get(key));
                dataset.setValue(key, avg);
            }

        } // if list has elements

        // API: ChartFactory.createPieChart(title, data, legend, tooltips, urls)
        chart = ChartFactory.createPieChart("Average Build Metrics", dataset, true, true, false);

        // get a reference to the plot for further customization...
        PiePlot plot = (PiePlot) chart.getPlot();
        chartFormatter.formatMetricChart(plot, "min");

        return chart;
    }



    /**
     * Return rendering information for the chart.
     */
    public static final ChartRenderingInfo getTestCountRenderingInfo() {
        return new ChartRenderingInfo(new StandardEntityCollection());
    }


    /**
     * Generate a stacked bar graph representing the total number of tests in
     * in each build. 
     *
     * @param   builds  List of builds 
     * 
     * @return  Stacked graph representing automated tests across all builds 
     */
    public static final JFreeChart getTestCountChart(Vector<CMnDbBuildData> builds) {
        JFreeChart chart = null;

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if ((builds != null) && (builds.size() > 0)) {

            // Collect build test numbers for each of the builds in the list 
            Collections.sort(builds, new CMnBuildIdComparator());
            Enumeration buildList = builds.elements();
            while (buildList.hasMoreElements()) {

                // Process the test summary for the current build
                CMnDbBuildData build = (CMnDbBuildData) buildList.nextElement();
                Hashtable testSummary = build.getTestSummary();
                if ((testSummary != null) && (testSummary.size() > 0)) {
                    Enumeration keys = testSummary.keys();
                    while (keys.hasMoreElements()) {
                        String testType = (String) keys.nextElement();
                        Integer buildId = new Integer(build.getId());
                        CMnDbTestSummaryData tests = (CMnDbTestSummaryData) testSummary.get(testType);
                        // Record the total number of tests
                        dataset.addValue(tests.getTotalCount(), testType, buildId);
                    }
                }

            } // while list has elements

        } // if list has elements

        // API: ChartFactory.createStackedBarChart(title, domainAxisLabel, rangeAxisLabel, dataset, orientation, legend, tooltips, urls)
        chart = ChartFactory.createStackedBarChart("Automated Tests by Type", "Builds", "Test Count", dataset, PlotOrientation.VERTICAL, true, true, false);

        // get a reference to the plot for further customization...
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        chartFormatter.formatTypeChart(plot, dataset);

        return chart;
    }



    /**
     * Return rendering information for the chart.
     */
    public static final ChartRenderingInfo getAverageTestCountRenderingInfo() {
        return new ChartRenderingInfo(new StandardEntityCollection());
    }

    /**
     * Generate a pie graph representing average test counts for each type of test for 
     * all builds in the list. 
     *
     * @param   builds  List of builds 
     * 
     * @return  Pie graph representing build execution times across all builds 
     */
    public static final JFreeChart getAverageTestCountChart(Vector<CMnDbBuildData> builds) {
        JFreeChart chart = null;

        // Collect the average of all test types 
        Hashtable countAvg = new Hashtable();

        DefaultPieDataset dataset = new DefaultPieDataset();
        if ((builds != null) && (builds.size() > 0)) {

            // Collect build metrics for each of the builds in the list 
            Enumeration buildList = builds.elements();
            while (buildList.hasMoreElements()) {
                // Process the build metrics for the current build
                CMnDbBuildData build = (CMnDbBuildData) buildList.nextElement();
                Hashtable testSummary = build.getTestSummary();
                if ((testSummary != null) && (testSummary.size() > 0)) {
                    Enumeration keys = testSummary.keys();
                    while (keys.hasMoreElements()) {
                        String testType = (String) keys.nextElement();
                        CMnDbTestSummaryData tests = (CMnDbTestSummaryData) testSummary.get(testType);

                        Integer avgValue = null;
                        if (countAvg.containsKey(testType)) {
                            Integer oldAvg = (Integer) countAvg.get(testType);
                            avgValue = oldAvg + tests.getTotalCount();
                        } else {
                            avgValue = tests.getTotalCount();
                        }
                        countAvg.put(testType, avgValue);

                    }
                }


            } // while list has elements

            // Populate the data set with the average values for each metric
            Enumeration keys = countAvg.keys();
            while (keys.hasMoreElements()) {
                String key = (String) keys.nextElement();
                Integer total = (Integer) countAvg.get(key);
                Integer avg = new Integer(total.intValue() / builds.size());
                dataset.setValue(key, avg);
            }

        } // if list has elements

        // API: ChartFactory.createPieChart(title, data, legend, tooltips, urls)
        chart = ChartFactory.createPieChart("Avg Test Count by Type", dataset, true, true, false);

        // get a reference to the plot for further customization...
        PiePlot plot = (PiePlot) chart.getPlot();
        chartFormatter.formatTypeChart(plot, "tests");

        return chart;
    }


    /**
     * Return rendering information for the chart.
     */
    public static final ChartRenderingInfo getAreaTestCountRenderingInfo() {
        return new ChartRenderingInfo(new StandardEntityCollection());
    }

    /**
     * Generate a stacked bar graph representing test counts for each product area. 
     *
     * @param   builds   List of builds
     * @param   suites   List of test suites
     * @param   areas    List of product areas 
     * 
     * @return  Pie graph representing build execution times across all builds 
     */
    public static final JFreeChart getAreaTestCountChart(
        Vector<CMnDbBuildData> builds,
        Vector<CMnDbTestSuite> suites,
        Vector<CMnDbFeatureOwnerData> areas) 
    {
        JFreeChart chart = null;

        // Collect the total times for each build, organized by area
        // This hashtable maps a build to the area/time information for that build
        Hashtable<Integer, Hashtable> buildTotals = new Hashtable<Integer, Hashtable>();

        // Generate placeholders for each build so the chart maintains a 
        // format consistent with the other charts that display build information
        if (builds != null) {
            Enumeration buildList = builds.elements();
            while (buildList.hasMoreElements()) {
                CMnDbBuildData build = (CMnDbBuildData) buildList.nextElement();
                // Create the empty area list
                buildTotals.put(new Integer(build.getId()), new Hashtable<String, Integer>()); 
            }
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if ((suites != null) && (suites.size() > 0)) {

            // Collect build test numbers for each of the builds in the list 
            Enumeration suiteList = suites.elements();
            while (suiteList.hasMoreElements()) {

                // Process the test summary for the current build
                CMnDbTestSuite suite = (CMnDbTestSuite) suiteList.nextElement();
                Integer buildId = new Integer(suite.getParentId());
                Integer testCount = new Integer(suite.getTestCount());

                // Parse the build information so we can track the time by build
                Hashtable<String, Integer> areaCount = null;
                if (buildTotals.containsKey(buildId)) {
                    areaCount = (Hashtable) buildTotals.get(buildId); 
                } else {
                    areaCount = new Hashtable<String, Integer>();
                    buildTotals.put(buildId, areaCount);
                }

                // Iterate through each product area to determine who owns this suite
                CMnDbFeatureOwnerData area = null;
                Iterator iter = areas.iterator();
                while (iter.hasNext()) {
                    CMnDbFeatureOwnerData currentArea = (CMnDbFeatureOwnerData) iter.next();
                    if (currentArea.hasFeature(suite.getGroupName())) {
                        area = currentArea;
                    }
                }

                // Add the elapsed time for the current suite to the area total
                Integer totalValue = null;
                String areaName = area.getDisplayName();
                if (areaCount.containsKey(areaName)) {
                    Integer oldTotal = (Integer) areaCount.get(areaName);
                    totalValue = oldTotal + testCount;
                } else {
                    totalValue = testCount;
                }
                areaCount.put(areaName, totalValue);

            } // while list has elements

            // Make sure every area is represented in the build totals
            Enumeration bt = buildTotals.keys();
            while (bt.hasMoreElements()) {
                // Get the build ID for the current build
                Integer bid = (Integer) bt.nextElement();

                // Get the list of area totals for the current build
                Hashtable<String, Integer> ac = (Hashtable<String, Integer>) buildTotals.get(bid);
                Iterator a = areas.iterator();
                while (a.hasNext()) {
                    // Add a value of zero if no total was found for the current area
                    CMnDbFeatureOwnerData area = (CMnDbFeatureOwnerData) a.next();
                    if (!ac.containsKey(area.getDisplayName())) {
                        ac.put(area.getDisplayName(), new Integer(0)); 
                    }
                } 
            }

            // Populate the data set with the area times for each build
            Collections.sort(builds, new CMnBuildIdComparator());
            Enumeration bList = builds.elements();
            while (bList.hasMoreElements()) {
                CMnDbBuildData build = (CMnDbBuildData) bList.nextElement();
                Integer buildId = new Integer(build.getId()) ;
                Hashtable areaCount = (Hashtable) buildTotals.get(buildId);

                Enumeration areaKeys = areaCount.keys();
                while (areaKeys.hasMoreElements()) {
                    String area = (String) areaKeys.nextElement();
                    Integer count = (Integer) areaCount.get(area);
                    dataset.addValue(count, area, buildId);
                }
            }

        } // if list has elements


        // API: ChartFactory.createStackedBarChart(title, domainAxisLabel, rangeAxisLabel, dataset, orientation, legend, tooltips, urls)
        chart = ChartFactory.createStackedBarChart("Automated Tests by Area", "Builds", "Test Count", dataset, PlotOrientation.VERTICAL, true, true, false);

        // get a reference to the plot for further customization...
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        chartFormatter.formatAreaChart(plot, dataset);

        return chart;
    }


    /**
     * Return rendering information for the chart.
     */
    public static final ChartRenderingInfo getAverageAreaTestCountRenderingInfo() {
        return new ChartRenderingInfo(new StandardEntityCollection());
    }
    
    /**
     * Generate a pie graph representing test counts for each product area. 
     *
     * @param   suites   List of test suites
     * @param   areas    List of product areas 
     * 
     * @return  Pie graph representing build execution times across all builds 
     */
    public static final JFreeChart getAverageAreaTestCountChart(
        Vector<CMnDbBuildData> builds,
        Vector<CMnDbTestSuite> suites,
        Vector<CMnDbFeatureOwnerData> areas)
    {
        JFreeChart chart = null;

        // Collect the average of all test types 
        Hashtable countAvg = new Hashtable();

        DefaultPieDataset dataset = new DefaultPieDataset();
        if ((suites != null) && (suites.size() > 0)) {

            // Collect test data for each of the suites in the list 
            Enumeration suiteList = suites.elements();
            while (suiteList.hasMoreElements()) {

                // Process the data for the current suite
                CMnDbTestSuite suite = (CMnDbTestSuite) suiteList.nextElement();
                Integer testCount = new Integer(suite.getTestCount());
                CMnDbFeatureOwnerData area = null;

                // Iterate through each product area to determine who owns this suite
                CMnDbFeatureOwnerData currentArea = null;
                Iterator iter = areas.iterator();
                while (iter.hasNext()) {
                    currentArea = (CMnDbFeatureOwnerData) iter.next();
                    if (currentArea.hasFeature(suite.getGroupName())) {
                        Integer avgValue = null;
                        String areaName = currentArea.getDisplayName();
                        if (countAvg.containsKey(areaName)) {
                            Integer oldAvg = (Integer) countAvg.get(areaName);
                            avgValue = oldAvg + testCount;
                        } else {
                            avgValue = testCount;
                        }
                        countAvg.put(areaName, avgValue);

                    }
                }


            } // while list has elements


            // Populate the data set with the average values for each metric
            Enumeration keys = countAvg.keys();
            while (keys.hasMoreElements()) {
                String key = (String) keys.nextElement();
                Integer total = (Integer) countAvg.get(key);
                Integer avg = new Integer(total.intValue() / builds.size());
                dataset.setValue(key, avg);
            }

        } // if list has elements

        // API: ChartFactory.createPieChart(title, data, legend, tooltips, urls)
        chart = ChartFactory.createPieChart("Avg Test Count by Area", dataset, true, true, false);

        // get a reference to the plot for further customization...
        PiePlot plot = (PiePlot) chart.getPlot();
        chartFormatter.formatAreaChart(plot, "tests");

        return chart;
    }


    /**
     * Return rendering information for the chart.
     */
    public static final ChartRenderingInfo getAreaTestTimeRenderingInfo() {
        return new ChartRenderingInfo(new StandardEntityCollection());
    }


    /**
     * Generate a stacked bar graph representing test execution time for each 
     * product area. 
     *
     * @param   builds   List of builds
     * @param   suites   List of test suites
     * @param   areas    List of product areas 
     * 
     * @return  Stacked bar chart representing test execution times across all builds 
     */
    public static final JFreeChart getAreaTestTimeChart(
        Vector<CMnDbBuildData> builds,
        Vector<CMnDbTestSuite> suites,
        Vector<CMnDbFeatureOwnerData> areas)
    {
        JFreeChart chart = null;

        // Collect the total times for each build, organized by area
        // This hashtable maps a build to the area/time information for that build
        Hashtable<Integer, Hashtable> buildTotals = new Hashtable<Integer, Hashtable>();

        // Generate placeholders for each build so the chart maintains a 
        // format consistent with the other charts that display build information
        HashSet areaNames = new HashSet();
        if (builds != null) {
            Enumeration buildList = builds.elements();
            while (buildList.hasMoreElements()) {
                CMnDbBuildData build = (CMnDbBuildData) buildList.nextElement();
                // Create the empty area list
                buildTotals.put(new Integer(build.getId()), new Hashtable<String, Long>());
            }
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if ((suites != null) && (suites.size() > 0)) {

            // Collect build test numbers for each of the builds in the list 
            Enumeration suiteList = suites.elements();
            while (suiteList.hasMoreElements()) {

                // Process the test summary for the current build
                CMnDbTestSuite suite = (CMnDbTestSuite) suiteList.nextElement();
                Integer buildId = new Integer(suite.getParentId());
                Long elapsedTime = new Long(suite.getElapsedTime());

                // Parse the build information so we can track the time by build
                Hashtable<String, Long> areaTime = null;
                if (buildTotals.containsKey(buildId)) {
                    areaTime = (Hashtable) buildTotals.get(buildId); 
                } else {
                    areaTime = new Hashtable<String, Long>();
                    buildTotals.put(buildId, areaTime);
                }

                // Iterate through each product area to determine who owns this suite
                CMnDbFeatureOwnerData area = null;
                Iterator iter = areas.iterator();
                while (iter.hasNext()) {
                    CMnDbFeatureOwnerData currentArea = (CMnDbFeatureOwnerData) iter.next();
                    if (currentArea.hasFeature(suite.getGroupName())) {
                        area = currentArea;
                    }
                }

                // Add the elapsed time for the current suite to the area total
                Long totalValue = null;
                String areaName = area.getDisplayName();
                areaNames.add(areaName);
                if (areaTime.containsKey(areaName)) {
                    Long oldTotal = (Long) areaTime.get(areaName);
                    totalValue = oldTotal + elapsedTime;
                } else {
                    totalValue = elapsedTime;
                }
                areaTime.put(areaName, totalValue);

            } // while list has elements

            // Populate the data set with the area times for each build
            Collections.sort(builds, new CMnBuildIdComparator());
            Iterator buildIter = builds.iterator();
            while (buildIter.hasNext()) {
                CMnDbBuildData build = (CMnDbBuildData) buildIter.next();
                Integer buildId = new Integer(build.getId());
                Hashtable areaTime = (Hashtable) buildTotals.get(buildId);

                Iterator areaKeys = areaNames.iterator();
                while (areaKeys.hasNext()) {
                    String area = (String) areaKeys.next();
                    Long time = (Long) areaTime.get(area);
                    if (time != null) {
                        // Convert the time from milliseconds to minutes
                        time = time / (1000 * 60);
                    } else {
                        time = new Long(0);
                    }
                    dataset.addValue(time, area, buildId);
                }
            }

        } // if list has elements

        // API: ChartFactory.createStackedBarChart(title, domainAxisLabel, rangeAxisLabel, dataset, orientation, legend, tooltips, urls)
        chart = ChartFactory.createStackedBarChart("Automated Tests by Area", "Builds", "Execution Time (min)", dataset, PlotOrientation.VERTICAL, true, true, false);

        // get a reference to the plot for further customization...
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        chartFormatter.formatAreaChart(plot, dataset);

        return chart;
    }


    /**
     * Return rendering information for the chart.
     */
    public static final ChartRenderingInfo getAverageAreaTestTimeRenderingInfo() {
        return new ChartRenderingInfo(new StandardEntityCollection());
    }


    /**
     * Generate a pie graph representing test counts for each product area. 
     *
     * @param   suites   List of test suites
     * @param   areas    List of product areas 
     * 
     * @return  Pie graph representing build execution times across all builds 
     */
    public static final JFreeChart getAverageAreaTestTimeChart(
        Vector<CMnDbBuildData> builds,
        Vector<CMnDbTestSuite> suites,
        Vector<CMnDbFeatureOwnerData> areas)
    {
        JFreeChart chart = null;

        // Collect the average of all test types 
        Hashtable timeAvg = new Hashtable();

        DefaultPieDataset dataset = new DefaultPieDataset();
        if ((suites != null) && (suites.size() > 0)) {

            // Collect test data for each of the suites in the list 
            Enumeration suiteList = suites.elements();
            while (suiteList.hasMoreElements()) {

                // Process the data for the current suite
                CMnDbTestSuite suite = (CMnDbTestSuite) suiteList.nextElement();
                Long elapsedTime = new Long(suite.getElapsedTime() / (1000*60));
                CMnDbFeatureOwnerData area = null;

                // Iterate through each product area to determine who owns this suite
                CMnDbFeatureOwnerData currentArea = null;
                Iterator iter = areas.iterator(); 
                while (iter.hasNext()) {
                    currentArea = (CMnDbFeatureOwnerData) iter.next();
                    if (currentArea.hasFeature(suite.getGroupName())) {
                        Long avgValue = null;
                        String areaName = currentArea.getDisplayName();
                        if (timeAvg.containsKey(areaName)) {
                            Long oldAvg = (Long) timeAvg.get(areaName);
                            avgValue = oldAvg + elapsedTime;
                        } else {
                            avgValue = elapsedTime;
                        }
                        timeAvg.put(areaName, avgValue);

                    }
                }


            } // while list has elements


            // Populate the data set with the average values for each metric
            Enumeration keys = timeAvg.keys();
            while (keys.hasMoreElements()) {
                String key = (String) keys.nextElement();
                Long total = (Long) timeAvg.get(key);
                Long avg = new Long(total.longValue() / builds.size());
                dataset.setValue(key, avg);
            }

        } // if list has elements

        // API: ChartFactory.createPieChart(title, data, legend, tooltips, urls)
        chart = ChartFactory.createPieChart("Avg Test Time by Area", dataset, true, true, false);

        // get a reference to the plot for further customization...
        PiePlot plot = (PiePlot) chart.getPlot();
        chartFormatter.formatAreaChart(plot, "min");

        return chart;
    }



}

