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
import com.modeln.testfw.reporting.CMnDbFeatureOwnerData;
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
public class CMnBaseSuiteForm extends CMnBaseTestForm {


    /** List of unit test suites to be displayed */
    protected Vector<CMnDbTestSuite> suiteList;

    /** List of test suite groups */
    protected Vector<CMnTestSuiteGroup> groupList = new Vector<CMnTestSuiteGroup>();

    /** Default group for any uncategorized test suites */
    protected CMnTestSuiteGroup defaultGroup;



    /**
     * Construct the form from a list of test suites.
     *
     * @param  form      URL to use when submitting form input
     * @param  images    URL to use when referencing images
     * @param  suites    Entire list of test suites to be displayed
     */
    public CMnBaseSuiteForm(URL form, URL images, Vector<CMnDbTestSuite> suites) {
        super(form, images);
        suiteList = suites;

        // Sort by GID or group name only if all suites have valid values
        boolean validGid = true;
        boolean validName = true;
        CMnDbTestSuite currentSuite = null;
        for (int suiteIdx = 0; suiteIdx < suiteList.size(); suiteIdx++) {
            currentSuite = (CMnDbTestSuite) suiteList.get(suiteIdx);
            if (currentSuite.getGroupId() <= 0) {
                validGid = false;
            }
            if (currentSuite.getGroupName() == null) {
                validName = false;
            }
        }

        initGroups();
    }


    /**
     * Create some default suite groups.  Override this method to define 
     * grouping information that is specific to the type of test suites
     * being displayed.
     */
    private void initGroups() {
        int collapse = CMnTestSuiteGroup.COLLAPSE_BY_NONE;
        int sort = CMnTestSuiteGroup.SORT_BY_HOST;

        // Create a default group
        CMnDbTestSuite defaultPrototype = new CMnDbTestSuite(CMnDbTestSuite.SuiteType.JUNIT);
        defaultGroup = new CMnTestSuiteGroup(formUrl, imageUrl, "Feature Suites", defaultPrototype);
        defaultGroup.setCollapseCriteria(collapse);
        defaultGroup.setSubgroupOrder(sort);
    }


    /**
     * Sets the list of product areas that will be used to map features to areas.
     *
     * @param   areas    List of product areas
     */
    public void setProductOwners(Vector<CMnDbFeatureOwnerData> areas) {
        defaultGroup.setProductOwners(areas);

        // Iterate through the groups and set these parameters for the groups
        CMnTestSuiteGroup currentGroup = null;
        for (int idx = 0; idx < groupList.size(); idx++) {
            currentGroup = (CMnTestSuiteGroup) groupList.get(idx);
            currentGroup.setProductOwners(areas);
        }

    }

    /**
     * Sets the URL to be used to delete individual test suites.
     *
     * @param  url   Link to the suite delete command
     */
    public void setDeleteUrl(URL url) {
        super.setDeleteUrl(url);

        defaultGroup.setDeleteUrl(deleteUrl);

        // Iterate through the groups and set these parameters for the groups
        CMnTestSuiteGroup currentGroup = null;
        for (int idx = 0; idx < groupList.size(); idx++) {
            currentGroup = (CMnTestSuiteGroup) groupList.get(idx);
            currentGroup.setDeleteUrl(deleteUrl);
        }

    }


    /**
     * Enables or disables administrative functionality.
     *
     * @param enabled  TRUE to enable administrative functionality
     */
    public void setAdminMode(boolean enabled) {
        super.setAdminMode(enabled);

        defaultGroup.setAdminMode(enabled);

        // Iterate through the groups and set these parameters for the groups
        CMnTestSuiteGroup currentGroup = null;
        for (int idx = 0; idx < groupList.size(); idx++) {
            currentGroup = (CMnTestSuiteGroup) groupList.get(idx);
            currentGroup.setAdminMode(enabled);
        }

    }



    /**
     * Set the filter criteria used to limit the results being displayed.
     *
     * @param regexp  String representing the filter criteria regular expression
     */
    public void setFilterCriteria(String regexp) {
        super.setFilterCriteria(regexp);

        defaultGroup.setFilterCriteria(regexp);

        // Iterate through the groups and set these parameters for the groups
        CMnTestSuiteGroup currentGroup = null;
        for (int idx = 0; idx < groupList.size(); idx++) {
            currentGroup = (CMnTestSuiteGroup) groupList.get(idx);
            currentGroup.setFilterCriteria(regexp);
        }

    }


    /**
     * Set the collapse criteria to use when combining multiple suites together.
     *
     * @param  collapse   Collapse critieria to use when grouping 
     */
    public void setCollapseCriteria(int collapse) {
        super.setCollapseCriteria(collapse);

        // Determine a reasonable sort order for the given criteria
        int sort = CMnTestSuiteGroup.SORT_BY_HOST;
        switch (collapse) {
            case CMnTestSuiteGroup.COLLAPSE_BY_NONE:
                sort = CMnTestSuiteGroup.SORT_BY_HOST;
                break;
            case CMnTestSuiteGroup.COLLAPSE_BY_GID:
                sort = CMnTestSuiteGroup.SORT_BY_GID;
                break;
            case CMnTestSuiteGroup.COLLAPSE_BY_NAME:
                sort = CMnTestSuiteGroup.SORT_BY_NAME;
                break;
            default:
                sort = CMnTestSuiteGroup.SORT_BY_HOST;
                collapse = CMnTestSuiteGroup.COLLAPSE_BY_NONE;
        }

        defaultGroup.setCollapseCriteria(collapse);
        defaultGroup.setSubgroupOrder(sort);

        // Iterate through the groups and set these parameters for the groups
        CMnTestSuiteGroup currentGroup = null;
        for (int idx = 0; idx < groupList.size(); idx++) {
            currentGroup = (CMnTestSuiteGroup) groupList.get(idx);
            currentGroup.setCollapseCriteria(collapse);
            currentGroup.setSubgroupOrder(sort);
        }

    }


    /**
     * Produce a more compact summary of the results.
     * 
     * @param  enable   TRUE if all collumns should be displayed for the test suite
     */
    public void enableAllColumns(boolean enable) {
        defaultGroup.enableAllColumns(enable);

        // Iterate through the groups and set these parameters for the groups
        CMnTestSuiteGroup currentGroup = null;
        for (int idx = 0; idx < groupList.size(); idx++) {
            currentGroup = (CMnTestSuiteGroup) groupList.get(idx);
            currentGroup.enableAllColumns(enable);
        }
    }


    /**
     * Enable or disable the display of the time spent executing the tests.
     *
     * @param  enable   TRUE if the column should be displayed, FALSE otherwise
     */
    public void enableTimeColumn(boolean enable) {
        defaultGroup.enableTimeColumn(enable);

        // Iterate through the groups and set these parameters for the groups
        CMnTestSuiteGroup currentGroup = null;
        for (int idx = 0; idx < groupList.size(); idx++) {
            currentGroup = (CMnTestSuiteGroup) groupList.get(idx);
            currentGroup.enableTimeColumn(enable);
        }
    }


    /**
     * Return the total number tests for the set of groups.
     *
     * @return Total number of tests
     */
    public int getTestCount() {
        int totalTests = 0;

        CMnDbTestSuite currentSuite = null;
        for (int suiteIdx = 0; suiteIdx < suiteList.size(); suiteIdx++) {
            currentSuite = (CMnDbTestSuite) suiteList.get(suiteIdx);
            totalTests = totalTests + currentSuite.getTestCount();
        }

        return totalTests;
    }


    /**
     * Return the total number of passing tests for the set of groups.
     *
     * @return Total number of passing tests
     */
    public int getPassingCount() {
        int totalPassing = 0;

        CMnDbTestSuite currentSuite = null;
        for (int suiteIdx = 0; suiteIdx < suiteList.size(); suiteIdx++) {
            currentSuite = (CMnDbTestSuite) suiteList.get(suiteIdx);
            totalPassing = totalPassing + currentSuite.getPassingCount();
        }

        return totalPassing;
    }


    /**
     * Return the total number of failing tests for the set of groups. 
     *
     * @return Total number of failing tests
     */
    public int getFailingCount() {
        int totalTests = 0;
        int totalPassing = 0;
        int totalFailing = 0;

        CMnDbTestSuite currentSuite = null;
        for (int suiteIdx = 0; suiteIdx < suiteList.size(); suiteIdx++) {
            currentSuite = (CMnDbTestSuite) suiteList.get(suiteIdx);
        /*    totalTests = totalTests + currentSuite.getTestCount();
            totalPassing = totalPassing + currentSuite.getPassingCount();*/
            totalFailing = totalFailing + currentSuite.getFailingCount() + currentSuite.getErrorCount();
        }

        /*if (totalTests > totalPassing) {
            totalFailing = totalTests - totalPassing;
        }*/

        return totalFailing;
    }


    /**
     * Render the build data form.
     */
    public String toString() {
        StringBuffer html = new StringBuffer();

        // Calculate the total number of tests across all groups
        int totalTests = 0;
        int totalPassing = 0;

        // Categorize each test suite
        CMnDbTestSuite currentSuite = null;
        CMnTestSuiteGroup currentGroup = null;
        for (int suiteIdx = 0; suiteIdx < suiteList.size(); suiteIdx++) { 
            currentSuite = (CMnDbTestSuite) suiteList.get(suiteIdx);
            totalTests = totalTests + currentSuite.getTestCount();
            totalPassing = totalPassing + currentSuite.getPassingCount();

            // Iterate through the group prototypes to classify the suite list
            boolean groupFound = false;
            for (int idx = 0; idx < groupList.size(); idx++) {
                currentGroup = (CMnTestSuiteGroup) groupList.get(idx);
                if ((groupFound == false) && (currentGroup.allowsMember(currentSuite))) {
                    currentGroup.addMember(currentSuite);
                    groupFound = true;
                } 
            }

            // If no group was found, add it to the default group
            if (!groupFound) {
                defaultGroup.addMember(currentSuite);
            }

        }

        // Render the list of groups
        for (int idx = 0; idx < groupList.size(); idx++) {
            currentGroup = (CMnTestSuiteGroup) groupList.get(idx);
            html.append(currentGroup.toString());
        }

        // Render the default group
        html.append(defaultGroup.toString());

        return html.toString();
    }



}

