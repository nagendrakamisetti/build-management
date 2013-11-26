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

import com.modeln.testfw.reporting.CMnDbBuildData;
import com.modeln.testfw.reporting.CMnDbHostData;
import com.modeln.testfw.reporting.CMnDbBuildStatusData;
import com.modeln.testfw.reporting.CMnDbMetricData;

import java.util.Enumeration;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;



/**
 * The build chart contains methods for rendering charts which present
 * various aspects of a single build.
 *
 * @author             Shawn Stafford
 *
 */
public class CMnBuildChart {

    /** Construct an instance of the chart formatter */
    private static final CMnChartFormatter chartFormatter = new CMnChartFormatter();


    /**
     * Validate the data submitted by the user and return any error codes.
     *
     * @param   req     HTTP request
     * @param   res     HTTP response
     * 
     * @return  Error code if any errors were found.
     */
    public static final JFreeChart getMetricChart(CMnDbBuildData build) {
        JFreeChart chart = null;

        DefaultPieDataset pieData = new DefaultPieDataset();
        if ((build.getMetrics() != null) && (build.getMetrics().size() > 0)) {
            Enumeration metrics = build.getMetrics().elements();
            while (metrics.hasMoreElements()) {
                CMnDbMetricData currentMetric = (CMnDbMetricData) metrics.nextElement();
                String name = currentMetric.getMetricType(currentMetric.getType());

                // Get elapsed time in "minutes"
                Long value = new Long(currentMetric.getElapsedTime() / (1000*60));

                pieData.setValue(name, value);
            }
        }

        // API: ChartFactory.createPieChart(title, data, legend, tooltips, urls)
        chart = ChartFactory.createPieChart("Build Metrics", pieData, true, true, false);

        // get a reference to the plot for further customization...
        PiePlot plot = (PiePlot) chart.getPlot();
        chartFormatter.formatMetricChart(plot, "min");

        return chart;
    }

}

