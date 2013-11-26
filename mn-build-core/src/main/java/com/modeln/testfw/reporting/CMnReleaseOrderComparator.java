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
 * Comparator for comparing releases by order field. 
 *
 * @author  Shawn Stafford
 */
public class CMnReleaseOrderComparator implements Comparator {

    /**
     * Compare the order value of two releases.  
     * Returns 0 if the order value of both releases are equal.
     * Returns a negative integer if the order value of release1 is less than release2 
     * Returns a positive integer if the order value of release1 is greater than release2 
     */
    public int compare(Object release1, Object release2) {
        Integer order1 = new Integer(((CMnDbReleaseSummaryData) release1).getOrder());
        Integer order2 = new Integer(((CMnDbReleaseSummaryData) release2).getOrder());
        return order1.compareTo(order2);
    }

}

