/*
* Copyright 2000-2003 by Model N, Inc.  All Rights Reserved.
*
* This software is the confidential and proprietary information
* of Model N, Inc ("Confidential Information").  You shall not
* disclose such Confidential Information and shall use it only
* in accordance with the terms of the license agreement you
* entered into with Model N, Inc.
*/
package com.modeln.build.ctrl.forms;

import com.modeln.testfw.reporting.CMnDbBuildData;
import com.modeln.testfw.reporting.CMnDbHostData;
import com.modeln.testfw.reporting.CMnDbTestSuite;

import java.net.URL;
import java.util.GregorianCalendar;
import java.util.Vector;
import javax.servlet.http.HttpServletRequest;



/**
 * The suite form provides an HTML interface to the unittest suite objects.
 * The class manages transfering the data through the HTTP request.
 * Form data can be rendered in either a read-only or input mode.
 *
 * @author  Shawn Stafford
 */
public class CMnUnittestSuiteForm extends CMnBaseSuiteForm {

    /** Default title displayed in the bordered header */
    private static final String DEFAULT_TITLE = "Unit Test Results";



    /**
     * Construct the form from a list of unit test suites.  The form will
     * group the suites by type.
     *
     * @param  form      URL to use when submitting form input
     * @param  images    URL to use when referencing images
     * @param  suites    Entire list of test suites to be displayed
     */
    public CMnUnittestSuiteForm(URL form, URL images, Vector suites) {
        super(form, images, suites);
    }


    /**
     * Create some default suite groups.
     */
    protected void initGroups() {
        int collapse = CMnTestSuiteGroup.COLLAPSE_BY_NONE;
        int sort = CMnTestSuiteGroup.SORT_BY_HOST;

        // Create a default group
        CMnDbTestSuite defaultPrototype = new CMnDbTestSuite(CMnDbTestSuite.SuiteType.JUNIT);
        defaultGroup = new CMnTestSuiteGroup(formUrl, imageUrl, "Other", defaultPrototype);
        defaultGroup.setAdminMode(adminEnabled);
        defaultGroup.setCollapseCriteria(collapse);
        defaultGroup.setSubgroupOrder(sort);

        // Group the populate suites together
        CMnDbTestSuite populatePrototype = new CMnDbTestSuite(CMnDbTestSuite.SuiteType.JUNIT);
        populatePrototype.addOption("populate", "true");
        populatePrototype.addOption("bug", "false");
        CMnTestSuiteGroup populateGroup = new CMnTestSuiteGroup(formUrl, imageUrl, "Populate Suite", populatePrototype);
        populateGroup.setCollapseCriteria(collapse);
        populateGroup.setSubgroupOrder(sort);
        groupList.add(populateGroup);

        // Group the test suites together
        CMnDbTestSuite testPrototype = new CMnDbTestSuite(CMnDbTestSuite.SuiteType.JUNIT);
        testPrototype.addOption("populate", "false");
        testPrototype.addOption("bug", "false");
        CMnTestSuiteGroup testGroup = new CMnTestSuiteGroup(formUrl, imageUrl, "Test Suite", testPrototype);
        testGroup.setCollapseCriteria(collapse);
        testGroup.setSubgroupOrder(sort);
        groupList.add(testGroup);

        // Group the bug suites together
        CMnDbTestSuite bugPrototype = new CMnDbTestSuite(CMnDbTestSuite.SuiteType.JUNIT);
        bugPrototype.addOption("bug", "true");
        CMnTestSuiteGroup bugGroup = new CMnTestSuiteGroup(formUrl, imageUrl, "Bug Suite", bugPrototype);
        bugGroup.setHighlightCriteria(CMnTestSuiteGroup.HIGHLIGHT_PASSING);
        bugGroup.setCollapseCriteria(collapse);
        bugGroup.setSubgroupOrder(sort);
        groupList.add(bugGroup);
    }




    /**
     * Create a table which defines a title and border for the contents.
     *
     * @param   title   Text to be placed in the title bar
     * @param   content Content of the table
     */
    public String getTitledBorder(String content) {
        return getTitledBorder(DEFAULT_TITLE, content);
    }

}

