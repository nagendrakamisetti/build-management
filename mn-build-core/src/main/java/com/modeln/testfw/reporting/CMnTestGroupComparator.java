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
 * Comparator for comparing two test groups to determine which one is a higher priority.
 * Priority is determined primarily by test status and execution time.  The test
 * status priority order is defined by CMnDbTestData.statusOrder. 
 *
 * @author  Shawn Stafford
 */
public class CMnTestGroupComparator implements Comparator {

    /**
     * Compares this the two test groups to determine which group has a higher priority. 
     * The test groups are compared using the first element in their test list, which
     * is assumed to be a sorted list of test items.
     *
     * Returns negative if this object is less than the specified object.
     * Returns positive if this object is greater than the specified object.
     * Returns zero if the objects are equal.
     * 
     * @return a negative integer, zero, or a positive integer as this 
     *         object is less than, equal to, or greater than the specified object 
     */
    public int compare(Object group1, Object group2) throws ClassCastException {
        int result = 0;

        // Compare the first object of each test group
        CMnDbTestData test1 = ((CMnTestGroup)group1).getFirstTest();
        CMnDbTestData test2 = ((CMnTestGroup)group2).getFirstTest();
        CMnTestPriorityComparator testComparator = new CMnTestPriorityComparator(); 
        if ((test1 != null) && (test2 != null)) {
            result = testComparator.compare(test1,test2);
        } else if ((test1 == null) && (test2 == null)) {
            result = 0;
        } else if (test1 != null) {
            result = 1;
        } else {
            result = -1;
        }

        return result;
    }



}

