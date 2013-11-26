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
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Vector;
import javax.servlet.http.HttpServletRequest;



/**
 * The suite form provides an HTML interface to the UIT suite objects.
 * The class manages transfering the data through the HTTP request.
 * Form data can be rendered in either a read-only or input mode.
 *
 * @author  Shawn Stafford, Karen An
 */
public class CMnUitSuiteForm extends CMnBaseSuiteForm {

    /** Default title displayed in the bordered header */
    private static final String DEFAULT_TITLE = "User Interface Test Results";


    /**
     * Construct the form from a list of UIT suites.  The form will
     * group the suites by type.
     *
     * @param  form      URL to use when submitting form input
     * @param  images    URL to use when referencing images
     * @param  suites    Entire list of test suites to be displayed
     */
    public CMnUitSuiteForm(URL form, URL images, Vector suites) {
        super(form, images, suites);
    }


    /**
     * Create some default suite groups.
     */
    protected void initGroups() {
        // Create a default group
        CMnDbTestSuite defaultPrototype = new CMnDbTestSuite(CMnDbTestSuite.SuiteType.UIT);
        defaultGroup = new CMnTestSuiteGroup(formUrl, imageUrl, "Test Suite", defaultPrototype);

        // Group the vATS suites together
        CMnDbTestSuite vATSPrototype = new CMnDbTestSuite(CMnDbTestSuite.SuiteType.UIT);
        vATSPrototype.addOption("vATS", "true");
        CMnTestSuiteGroup vATSGroup = new CMnTestSuiteGroup(formUrl, imageUrl, "vATS Suite", vATSPrototype);
        groupList.add(vATSGroup);

        // Group the rATS suites together
        CMnDbTestSuite rATSPrototype = new CMnDbTestSuite(CMnDbTestSuite.SuiteType.UIT);
        rATSPrototype.addOption("rATS", "true");
        CMnTestSuiteGroup rATSGroup = new CMnTestSuiteGroup(formUrl, imageUrl, "rATS Suite", rATSPrototype);
        groupList.add(rATSGroup);

        // Group the populate suites together
        CMnDbTestSuite populatePrototype = new CMnDbTestSuite(CMnDbTestSuite.SuiteType.UIT);
        populatePrototype.addOption("populate", "true");
        populatePrototype.addOption("bug", "false");
        CMnTestSuiteGroup populateGroup = new CMnTestSuiteGroup(formUrl, imageUrl, "Populate Suite", populatePrototype);
        groupList.add(populateGroup);
        
        // Group the populate suites together
        CMnDbTestSuite regressionPrototype = new CMnDbTestSuite(CMnDbTestSuite.SuiteType.UIT);
        regressionPrototype.addOption("populate", "false");
        regressionPrototype.addOption("bug", "false");
        CMnTestSuiteGroup regressionGroup = new CMnTestSuiteGroup(formUrl, imageUrl, "Regression Suite", regressionPrototype);
        groupList.add(regressionGroup);

        // Group the test suites together
        CMnDbTestSuite populateBugPrototype = new CMnDbTestSuite(CMnDbTestSuite.SuiteType.UIT);
        populateBugPrototype.addOption("populate", "true");
        populateBugPrototype.addOption("bug", "true");
        CMnTestSuiteGroup populateBugGroup = new CMnTestSuiteGroup(formUrl, imageUrl, "Populate Bug Suite", populateBugPrototype);
        populateBugGroup.setHighlightCriteria(CMnTestSuiteGroup.HIGHLIGHT_PASSING);
        groupList.add(populateBugGroup);

        // Group the bug suites together
        CMnDbTestSuite regressionBugPrototype = new CMnDbTestSuite(CMnDbTestSuite.SuiteType.UIT);
        regressionBugPrototype.addOption("populate", "false");
        regressionBugPrototype.addOption("bug", "true");
        CMnTestSuiteGroup regressionBugGroup = new CMnTestSuiteGroup(formUrl, imageUrl, "Regression Bug Suite", regressionBugPrototype);
        regressionBugGroup.setHighlightCriteria(CMnTestSuiteGroup.HIGHLIGHT_PASSING);
        groupList.add(regressionBugGroup);

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

