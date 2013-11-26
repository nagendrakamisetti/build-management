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

import java.awt.Color;
import java.util.Enumeration;
import java.util.Hashtable;

import org.jfree.data.category.CategoryDataset;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.renderer.category.BarRenderer;


/**
 * This is a utility class for producing consistent charts. 
 *
 * @author             Shawn Stafford
 *
 */
public class CMnChartFormatter {

    private static final Color DEFAULT_COLOR = Color.GRAY;

    private static final Color BUILD_COLOR = Color.BLUE;
    private static final Color DEPLOY_COLOR = Color.YELLOW;
    private static final Color POPULATE_COLOR = Color.GREEN;
    private static final Color TEST_COLOR = Color.RED;

    private static final Color JUNIT_COLOR = Color.MAGENTA;
    private static final Color ACT_COLOR = Color.ORANGE;
    private static final Color FLEX_COLOR = Color.PINK;
    private static final Color UIT_COLOR = Color.CYAN;

    private static final Color PC4_COLOR = Color.BLUE;
    private static final Color INCENTIVES_COLOR = Color.YELLOW;
    private static final Color REGULATORY_COLOR = Color.GREEN;
    private static final Color INFRASTRUCTURE_COLOR = Color.RED;

    /** Map the list of metric names to colors */
    private Hashtable metricColors = new Hashtable();

    /** Map the list of test types to colors */
    private Hashtable typeColors = new Hashtable();

    /** Map the list of product areas to colors */
    private Hashtable areaColors = new Hashtable();

    /**
     * Construct the formatter.
     */
    public CMnChartFormatter() {
        metricColors.put("deploy", DEPLOY_COLOR);
        metricColors.put("build", BUILD_COLOR);
        metricColors.put("unittest", TEST_COLOR);
        metricColors.put("populate", POPULATE_COLOR);

        typeColors.put("JUNIT", JUNIT_COLOR);
        typeColors.put("FLEX", FLEX_COLOR);
        typeColors.put("UIT", UIT_COLOR);
        typeColors.put("ACT", ACT_COLOR); 

        areaColors.put("PC4", PC4_COLOR);
        areaColors.put("Incentives", INCENTIVES_COLOR);
        areaColors.put("Regulatory", REGULATORY_COLOR);
        areaColors.put("Infrastructure", INFRASTRUCTURE_COLOR);
    }

    /**
     * Set the colors for the metric chart and perform visual formatting.
     */
    public void formatMetricChart(PiePlot plot, String label) {
        formatChart(plot, metricColors, label);
    }

    /**
     * Set the colors for the test type chart and perform visual formatting.
     */
    public void formatTypeChart(PiePlot plot, String label) {
        formatChart(plot, typeColors, label);
    }

    /**
     * Set the colors for the area type chart and perform visual formatting.
     */
    public void formatAreaChart(PiePlot plot, String label) {
        formatChart(plot, areaColors, label);
    }



    private void formatChart(PiePlot plot, Hashtable colormap, String label) {
        // Set the chart colors
        Enumeration keys = colormap.keys();
        while (keys.hasMoreElements()) {
            String name = (String) keys.nextElement();
            Color color = (Color) colormap.get(name);
            if (color == null) {
                color = DEFAULT_COLOR;
            }
            plot.setSectionPaint(name, color);
        }

        // Set the chart label format
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{1} " + label + " ({2})"));
    }


    /**
     * Set the colors for the metric chart and perform visual formatting.
     */
    public void formatMetricChart(CategoryPlot plot, CategoryDataset dataset) {
        formatChart(plot, dataset, metricColors);
    }

    public void formatTypeChart(CategoryPlot plot, CategoryDataset dataset) {
        formatChart(plot, dataset, typeColors);
    }

    public void formatAreaChart(CategoryPlot plot, CategoryDataset dataset) {
        formatChart(plot, dataset, areaColors);
    }


    private void formatChart(CategoryPlot plot, CategoryDataset dataset, Hashtable colormap) {
        BarRenderer renderer = (BarRenderer) plot.getRenderer();

        Enumeration keys = colormap.keys();
        while (keys.hasMoreElements()) {
            // Map the metric name to a series number
            String name = (String) keys.nextElement();
            int series = dataset.getRowIndex(name);

            // Select the color that corresponds to the current name
            Color color = (Color) colormap.get(name);
            if (color == null) {
                color = DEFAULT_COLOR;
            }

            // Set the color for the current series as long as it is a valid series
            if (series >= 0) {
                renderer.setSeriesPaint(series, color);
            }
        }
    }


}

