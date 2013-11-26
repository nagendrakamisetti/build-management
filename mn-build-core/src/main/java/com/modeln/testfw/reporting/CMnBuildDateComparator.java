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
 * Comparator for comparing builds by start and end date. 
 *
 * @author  Shawn Stafford
 */
public class CMnBuildDateComparator implements Comparator {

    /**
     * Compare the start and end dates of two builds.  
     * Returns 0 if the start and end dates of both builds are equal.
     * Returns a negative integer if the start dates are equal but the first build completed before the second build.
     * Returns a negative integer if the first build started before the second build.
     * Returns a positive integer if the start dates are equal but the first build completed after the second build.
     * Returns a positive integer if the first build started after the second build.
     */
    public int compare(Object build1, Object build2) {
        Date startTime1 = ((CMnDbBuildData) build1).getStartTime();
        Date endTime1   = ((CMnDbBuildData) build1).getEndTime();
        Date startTime2 = ((CMnDbBuildData) build2).getStartTime();
        Date endTime2   = ((CMnDbBuildData) build2).getEndTime();

        if (startTime1.equals(startTime2)) {
            // Start times are equal so use end time
            return endTime1.compareTo(endTime2);
        } else {
            // Compare the start times
            return startTime1.compareTo(startTime2); 
        }
    }

}

