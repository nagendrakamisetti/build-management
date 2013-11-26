/*
* Copyright 2000-2003 by Model N, Inc.  All Rights Reserved.
*
* This software is the confidential and proprietary information
* of Model N, Inc ("Confidential Information").  You shall not
* disclose such Confidential Information and shall use it only
* in accordance with the terms of the license agreement you
* entered into with Model N, Inc.
*/
package com.modeln.testfw.reporting;

import java.util.Comparator;
import java.util.Date;


/**
 * Comparator for comparing two build metrics to determine which one occurs first
 * based on the execution time. 
 *
 * @author  Shawn Stafford
 */
public class CMnMetricDateComparator implements Comparator {


    /**
     * Compares this the two metrics to determine which metric is expected to occur first.
     * Objects are compared first by their start and end date.
     * Returns negative if this object is less than the specified object.
     * Returns positive if this object is greater than the specified object.
     * Returns zero if the objects are equal.
     * 
     * @return a negative integer, zero, or a positive integer as this 
     *         object is less than, equal to, or greater than the specified object 
     */
    public int compare(Object metric1, Object metric2) throws ClassCastException {
        Date startTime1 = ((CMnDbMetricData) metric1).getStartTime();
        Date endTime1   = ((CMnDbMetricData) metric1).getEndTime();
        Date startTime2 = ((CMnDbMetricData) metric2).getStartTime();
        Date endTime2   = ((CMnDbMetricData) metric2).getEndTime();

        if (startTime1.equals(startTime2)) {
            // Start times are equal so use end time
            return endTime1.compareTo(endTime2);
        } else {
            // Compare the start times
            return startTime1.compareTo(startTime2);
        }

    }



}

