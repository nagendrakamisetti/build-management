/*
* Copyright 2000-2012 by Model N, Inc.  All Rights Reserved.
*
* This software is the confidential and proprietary information
* of Model N, Inc ("Confidential Information").  You shall not
* disclose such Confidential Information and shall use it only
* in accordance with the terms of the license agreement you
* entered into with Model N, Inc.
*/
package com.modeln.testfw.reporting;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Vector;


/**
 * Data object used to group tests together. 
 *
 * @author  Shawn Stafford
 */
public class CMnTestGroup {

    /** Name of the group */
    private String name = null;

    /** List of tests in the group */
    private Vector<CMnDbTestData> tests = new Vector<CMnDbTestData>();

    /** Comparator used to sort the list of tests */
    private CMnTestPriorityComparator testOrder = new CMnTestPriorityComparator();

    /** 
     * Construct the test group.
     *
     * @param  name   Test group name
     */
    public CMnTestGroup(String name) {
        this.name = name;
    }

    /**
     * Return the name of the test group.
     *
     * @return  Test group name
     */
    public String getName() {
        return name;
    }

    /**
     * Add a new test to the group.
     *
     * @param   test    Test data
     */
    public void addTest(CMnDbTestData test) {
        tests.add(test);

        // Keep the list of tests sorted so the most significant
        // test is always first in the list and can be easily retrieved
        if (tests.size() > 1) {
            Collections.sort(tests, testOrder);
        }
    }

    /**
     * Return the number of tests in the group.
     *
     * @return  Number of tests
     */
    public int getTestCount() {
        return tests.size();
    }

    /**
     * Return the first test in the list.  The list of tests is sorted
     * by test status, so the most significant test status will be the
     * first in the list.  A null will be returned if the list is empty.
     * 
     * @return First test item
     */
    public CMnDbTestData getFirstTest() {
        if (tests.size() > 0) {
            return tests.firstElement();
        } else {
            return null;
        }
    }

    /**
     * Return the list of tests.
     *
     * @return  List of tests
     */
    public Enumeration<CMnDbTestData> getTests() {
        return tests.elements();
    }
}
