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


/**
 * Comparator for comparing two tests to determine which one is a higher priority.
 * Priority is determined primarily by test status and execution time.  The test
 * status priority order is defined by CMnDbTestData.statusOrder. 
 *
 * @author  Shawn Stafford
 */
public class CMnTestPriorityComparator implements Comparator {

    /** Define the sort order for the test status field */
    private static final int[] statusOrder = { 
        CMnDbTestData.RUNNING,
        CMnDbTestData.ERROR, 
        CMnDbTestData.FAIL, 
        CMnDbTestData.KILL, 
        CMnDbTestData.SKIP, 
        CMnDbTestData.BLACKLIST,
        CMnDbTestData.PENDING,
        CMnDbTestData.UNKNOWN_STATUS,
        CMnDbTestData.PASS
    };

    /**
     * Return the order of the status relative to the other statuses.
     * This is a helper method used to compare tests to determine which one
     * has the more important status.
     *
     * @return The relative order of the specified status
     */
    private static int getStatusOrder(int status) {
        for (int idx = 0; idx < statusOrder.length; idx++) {
            if (statusOrder[idx] == status) {
                return idx;
            }
        }
        return -1;
    }


    /**
     * Compares this the two tests to determine which test has a higher priority. 
     * Objects are compared using their ID values.
     * Returns negative if this object is less than the specified object.
     * Returns positive if this object is greater than the specified object.
     * Returns zero if the objects are equal.
     * 
     * @return a negative integer, zero, or a positive integer as this 
     *         object is less than, equal to, or greater than the specified object 
     */
    public int compare(Object test1, Object test2) throws ClassCastException {
        // Sort by status if they are not the same 
        int order1 = getStatusOrder(((CMnDbTestData)test1).getStatus());
        int order2 = getStatusOrder(((CMnDbTestData)test2).getStatus());
        if ((order1 >= 0) && (order2 >= 0)) {
            if (order1 < order2) {
                return -1;
            } else if (order1 > order2) {
                return 1;
            }
        }

        // Sort by execution time if they are not the same 
        long elapsed1 = ((CMnDbTestData)test1).getElapsedTime();
        long elapsed2 = ((CMnDbTestData)test2).getElapsedTime();
        if (elapsed1 > elapsed2) {
            return -1;
        } else if (elapsed1 < elapsed2) {
            return 1;
        }

        // Sort by test ID 
        int id1 = ((CMnDbTestData) test1).getId();
        int id2 = ((CMnDbTestData) test2).getId();
        if (id1 > id2) {
            return 1;
        } else if (id1 < id2) {
            return -1;
        } else {
            return 0;
        }
    }



}

