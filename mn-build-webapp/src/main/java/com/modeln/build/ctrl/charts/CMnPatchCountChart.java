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

import com.modeln.build.common.data.CMnTimeInterval;
import com.modeln.build.common.data.account.CMnAccount;
import com.modeln.build.common.data.account.CMnAccountNameComparator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;



/**
 * The build chart contains methods for rendering charts which present
 * information about the number of patches requested by customers. 
 *
 * @author             Shawn Stafford
 *
 */
public class CMnPatchCountChart {

    /** Construct an instance of the chart formatter */
    private static final CMnChartFormatter chartFormatter = new CMnChartFormatter();


    /**
     * Create a chart representing the number of patches per customer. 
     * The data is passed in as a hashtable where the key is the customer
     * information and the value is the number of patch requests.
     *
     * @param  data   Hashtable containing accounts and the corresponding counts
     * @param  label  Count label
     *
     * @return Bar chart repesenting the customer and count information
     */
    public static final JFreeChart getPatchesByCustomerChart(
        Hashtable<CMnAccount, Integer> data, 
        String label) 
    {
        Hashtable<String, Integer> hash = new Hashtable<String, Integer>();
        Enumeration accountList = data.keys();
        while (accountList.hasMoreElements()) {
            CMnAccount account = (CMnAccount) accountList.nextElement();
            Integer value = data.get(account);
            hash.put(account.getName(), value);
        }

        return getBarChart(hash, label + " by Customer", "Customer", label); 
    }

    /**
     * Create a chart representing the number patches grouped by time interval.
     * The data passed in the hashtable is a list of the time intervals and
     * the value is the number of patch requests for each interval.
     *
     * @param  data   Hashtable cnotaining the time intervals and corresponding counts
     * @param  label  Count label
     *
     * @return Bar chart representing the interval and count information
     */
    public static final JFreeChart getPatchesByIntervalChart(
        Hashtable<CMnTimeInterval, Integer> data, 
        String label) 
    {
        JFreeChart chart = null;

        String title = "Patches per " + label; 
        String nameLabel = label; 
        String valueLabel = "Patches"; 

        // Sort the data by date
        Vector<CMnTimeInterval> intervals = new Vector<CMnTimeInterval>();
        Enumeration intervalList = data.keys();
        while (intervalList.hasMoreElements()) {
            intervals.add((CMnTimeInterval) intervalList.nextElement());
        }
        Collections.sort(intervals);

        // Populate the dataset with data
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Iterator keyIter = intervals.iterator();
        while (keyIter.hasNext()) {
            CMnTimeInterval interval = (CMnTimeInterval) keyIter.next();
            Integer value = data.get(interval);
            dataset.addValue(value, valueLabel, interval.getName());
        }

        // Create the chart
        chart = ChartFactory.createBarChart(
            title,                        /*title*/
            nameLabel,                    /*categoryAxisLabel*/
            valueLabel,                   /*valueAxisLabel*/
            dataset,                      /*dataset*/
            PlotOrientation.VERTICAL,     /*orientation*/
            false,                        /*legend*/
            false,                        /*tooltips*/
            false                         /*urls*/
        );

        // get a reference to the plot for further customization...
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        //chartFormatter.formatMetricChart(plot, "min");

        // Set the chart colors
        plot.setBackgroundPaint(Color.white);
        plot.setDomainGridlinePaint(Color.lightGray);
        plot.setRangeGridlinePaint(Color.lightGray);

        // set the range axis to display integers only...
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        // disable bar outlines...
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(false);
        renderer.setShadowVisible(false);
        final GradientPaint gp = new GradientPaint(
            0.0f, 0.0f, Color.blue,
            0.0f, 0.0f, Color.blue
        );
        renderer.setSeriesPaint(0, gp);


        // Set the label orientation
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);

        return chart;
    }


    /**
     * Create a chart representing an arbitrary collection of name/value pairs. 
     * The data is passed in as a hashtable where the key is the name and the  
     * value is the number items. 
     */
    public static final JFreeChart getBarChart(
        Hashtable<String, Integer> data, 
        String title, 
        String nameLabel, 
        String valueLabel) 
    {
        JFreeChart chart = null;

        // Sort the data by name
        Vector<String> names = new Vector<String>();
        Enumeration nameList = data.keys();
        while (nameList.hasMoreElements()) {
            names.add((String) nameList.nextElement());
        }
        Collections.sort(names);

        // Populate the dataset with data
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Iterator keyIter = names.iterator();
        while (keyIter.hasNext()) {
            String name = (String) keyIter.next();
            Integer value = data.get(name);
            dataset.addValue(value, valueLabel, name);
        }

        // Create the chart
        chart = ChartFactory.createBarChart(
            title,                        /*title*/ 
            nameLabel,                    /*categoryAxisLabel*/ 
            valueLabel,                   /*valueAxisLabel*/ 
            dataset,                      /*dataset*/ 
            PlotOrientation.VERTICAL,     /*orientation*/ 
            false,                        /*legend*/ 
            false,                        /*tooltips*/ 
            false                         /*urls*/
        );

        // get a reference to the plot for further customization...
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        //chartFormatter.formatMetricChart(plot, "min");

        // Set the chart colors
        plot.setBackgroundPaint(Color.white);
        plot.setDomainGridlinePaint(Color.lightGray);
        plot.setRangeGridlinePaint(Color.lightGray);

        // set the range axis to display integers only...
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        // disable bar outlines...
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(false);
        renderer.setShadowVisible(false);
        final GradientPaint gp = new GradientPaint(
            0.0f, 0.0f, Color.blue,
            0.0f, 0.0f, Color.blue
        );
        renderer.setSeriesPaint(0, gp);


        // Set the label orientation
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        
        return chart;
    }


}

