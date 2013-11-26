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

import com.modeln.testfw.reporting.CMnBuildTable;
import com.modeln.testfw.reporting.CMnDbBuildData;
import com.modeln.testfw.reporting.CMnDbBuildStatusData;
import com.modeln.testfw.reporting.CMnDbHostData;
import com.modeln.testfw.reporting.CMnDbReleaseSummaryData;
import com.modeln.testfw.reporting.CMnDbTestSuite;
import com.modeln.testfw.reporting.search.CMnSearchCriteria;
import com.modeln.testfw.reporting.search.CMnSearchGroup;

import java.net.URL;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.Vector;
import javax.servlet.http.HttpServletRequest;

import com.modeln.build.util.StringUtility;
import com.modeln.build.web.tags.DateTag;
import com.modeln.build.web.tags.SelectTag;
import com.modeln.build.web.tags.TagGroup;
import com.modeln.build.web.tags.TextTag;


/**
 * The build summary form is used to display an overview of test results that
 * have been executed against a set of builds.
 *
 * @author  Shawn Stafford
 */
public class CMnBuildSummaryForm extends CMnBaseTableForm implements IMnBuildForm {

    /** Date format used when constructing SQL queries */
    protected static final SimpleDateFormat DATE = new SimpleDateFormat("yyyy-MM-dd");

    /** Default text of the titled border */
    public static final String DEFAULT_TITLE = "Build Summary";

    /** Build version field */
    private TextTag buildVersionTag = new TextTag("buildVersion");

    /** Build date field */
    private DateTag buildDateTag = new DateTag("buildDate");

    /** Build date query operator field */
    private SelectTag buildDateOp;

    /** Tag group to contain the date fields. */
    private TagGroup buildDateGroup = new TagGroup("buildDateGroup");

    /** Build changelist field. */
    private TextTag buildChangelistTag = new TextTag(BUILD_CHANGELIST_LABEL);

    /** Build changelist query operator field */
    private SelectTag buildChangelistOp;


    /** List of build data objects. */
    private Vector buildList = null;
    
    /** List of test suites and the corresponding test results */
    private Hashtable testSuites = null;

    /** URL for accessing build information */
    private URL buildUrl = null;

    /**  URL for accessing build status information */
    private URL statusUrl = null;

    /**  URL for accessing build information */
    private URL versionUrl = null;
    
    /**  URL for accessing test suite information */
    private URL unittestSuiteUrl = null;

    /**  URL for accessing flex suite information */
    private URL flexSuiteUrl = null;

    /**  URL for accessing test suite information */
    private URL uitSuiteUrl = null;

    /**  URL for accessing test suite information */
    private URL actSuiteUrl = null;

    /**
     * Construct the form from a list of releases. 
     *
     * @param  form      URL to use when submitting form input
     * @param  images    URL to use when referencing images
     * @param  builds    List of builds
     * @param  tests     Hashtable containing the test suites and test results
     */
    public CMnBuildSummaryForm(URL form, URL images, Vector builds, Hashtable tests) {
        super(form, images);
        buildList = builds;
        testSuites = tests;
        setGroupCriteria(GROUP_BY_HOST);
        buildVersionTag.setWidth(30);
        buildChangelistTag.setWidth(8);

        // Construct a list of date comparison operators
        Hashtable dateOps = new Hashtable(5);
        dateOps.put(CMnSearchCriteria.GREATER_THAN, ">");
        dateOps.put(CMnSearchCriteria.LESS_THAN, "<");
        dateOps.put(CMnSearchCriteria.LIKE, "="); 
        buildDateOp = new SelectTag("build_date_op", dateOps);

        // Construct a list of changelist comparison oparators
        Hashtable changeOps = new Hashtable(5);
        changeOps.put(CMnSearchCriteria.GREATER_THAN, ">");
        changeOps.put(CMnSearchCriteria.LESS_THAN, "<");
        changeOps.put(CMnSearchCriteria.EQUAL_TO, "=");
        changeOps.put(CMnSearchCriteria.LIKE, "like");
        buildChangelistOp = new SelectTag(BUILD_CHANGELIST_OP_LABEL, changeOps);

        // Group the date tags
        buildDateGroup.add(buildDateOp);
        buildDateGroup.add(buildDateTag);
        buildDateGroup.setUserDisable(true);
        buildDateGroup.setSubmitOnChange(true);
        buildDateGroup.enableGroup(false);
    }

    /**
     * Extend the base method for enabling the input mode.  All input fields
     * must be enabled or disabled when this method is called. 
     *
     * @param enabled  TRUE to enable form input
     */
    public void setInputMode(boolean enabled) {
        super.setInputMode(enabled);
        buildVersionTag.setDisabled(!enabled);
        buildDateGroup.setDisabled(!enabled);
        buildChangelistTag.setDisabled(!enabled);
        buildChangelistOp.setDisabled(!enabled);
        inputEnabled = enabled;
    }

    /**
     * Set the input fields by examining the HTTP request to see if
     * a value was submitted.
     *
     * @param   req     HTTP request
     */
    public void setValues(HttpServletRequest req) {
        super.setValues(req);
        buildVersionTag.setValue(req);
        buildDateGroup.setValue(req);
        buildChangelistTag.setValue(req);
        buildChangelistOp.setValue(req);
    }

    /**
     * Return a search group that describes all of the selected form input.
     *
     * @return Form values as a database search group
     */
    public CMnSearchGroup getValues() {
        CMnSearchGroup group = new CMnSearchGroup(CMnSearchGroup.AND);

        // Construct the search criteria for the build version
        if (buildVersionTag.isComplete()) {
            CMnSearchCriteria versionCriteria = new CMnSearchCriteria(
                CMnBuildTable.BUILD_TABLE,
                CMnBuildTable.BUILD_VERSION,
                CMnSearchCriteria.LIKE,
                buildVersionTag.getValue()
            );
            group.add(versionCriteria);
        }

        // Construct the search criteria for the build date
        if (buildDateGroup.isComplete()) {
            String[] dateOp = buildDateOp.getSelected();
            CMnSearchCriteria dateCriteria = new CMnSearchCriteria(
                CMnBuildTable.BUILD_TABLE,
                CMnBuildTable.BUILD_START_DATE,
                dateOp[0],
                DATE.format(buildDateTag.getDate())
            );
            group.add(dateCriteria);
        }

        // Construct the search criteria for the build changelist
        if (buildChangelistTag.isComplete()) {
            String[] changelistOp = buildChangelistOp.getSelected();
            CMnSearchCriteria changelistCriteria = new CMnSearchCriteria(
                CMnBuildTable.BUILD_TABLE,
                CMnBuildTable.BUILD_SOURCE_ID,
                changelistOp[0],
                buildChangelistTag.getValue()
            );
            group.add(changelistCriteria);
        }

        return group;
    }

    /**
     * Set the URL used to retrieve build status notes.
     *
     * @param  url   Link to the command related to build status notes
     */
    public void setStatusUrl(URL url) {
        statusUrl = url;
    }

    /**
     * Set the URL used to retrieve build information.
     *
     * @param  url   Link to the command related to build information
     */
    public void setVersionUrl(URL url) {
        versionUrl = url;
    }

    /**
     * Set the URL used to retrieve test suite information.
     *
     * @param  url   Link to the command related to suite information
     */
    public void setUnittestSuiteUrl(URL url) {
        unittestSuiteUrl = url;
    }
    
    /**
     * Set the URL used to retrieve test suite information.
     *
     * @param  url   Link to the command related to suite information
     */
    public void setUitSuiteUrl(URL url) {
        uitSuiteUrl = url;
    }

    /**
     * Set the URL used to retrieve flex suite information.
     *
     * @param  url   Link to the command related to suite information
     */
    public void setFlexSuiteUrl(URL url) {
        flexSuiteUrl = url;
    }

    /**
     * Set the URL used to retrieve ACT suite information.
     *
     * @param  url   Link to the command related to suite information
     */
    public void setActSuiteUrl(URL url) {
        actSuiteUrl = url;
    }

    /**
     * Return the URL for a specific build.
     *
     * @param   build    Build to be queried on the URL
     *
     * @return  Link to the build 
     */
    private String getVersionUrl(CMnDbBuildData build) {
        StringBuffer url = new StringBuffer();
        url.append(versionUrl + "?");
        url.append(IMnBuildForm.BUILD_ID_LABEL + "=" + build.getId());
        return url.toString();
    }

    
    /**
     * Return the status notes URL for a specific build.
     *
     * @param   build    Build to be queried on the URL
     * @param   status   Status to query
     *
     * @return  Link to the build status notes
     */
    private String getStatusUrl(CMnDbBuildData build, int status) {
        StringBuffer url = new StringBuffer();
        url.append(statusUrl + "?");
        url.append(IMnBuildForm.BUILD_ID_LABEL + "=" + build.getId());
        url.append("&");
        url.append(CMnBuildStatusForm.STATUS_LABEL + "=" + CMnBuildStatusForm.statusKeys[status]);
        return url.toString();
    }
    
    
    /**
     * Return the test suite URL for a specific test suite.
     *
     * @param  suite     Test suite information
     * @return Link to the test suite
     */
    private String getSuiteUrl(CMnDbTestSuite suite) {
        StringBuffer url = new StringBuffer();
        switch(suite.getSuiteType()) {
            case JUNIT:
                url.append(unittestSuiteUrl);
                break;
            case UIT:
                url.append(uitSuiteUrl);
                break;
            case FLEX:
                url.append(flexSuiteUrl);
            case ACT:
                url.append(actSuiteUrl);
            default:
        }

        // Make sure we only append the suite ID if we were able to identify the type
        if (url.length() > 0) {
            url.append("?" + IMnTestForm.SUITE_ID_LABEL + "=" + suite.getId());
        }

        return url.toString();
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

    /**
     * Return the string which identifies the group for the specified build.
     * Null is returned if the group name cannot be determined.
     * 
     * @param  build   Information about the build
     * @return Name of the group to which the build belongs
     */
    private String getGroupName(CMnDbBuildData build) {
        return getGroupName(build.getHostData());
    }

    /**
     * Return the string which identifies the group for the specified suite.
     * Null is returned if the group name cannot be determined.
     * 
     * @param  suite    Information about the test suite
     * @return Name of the group to which the suite belongs
     */
    private String getGroupName(CMnDbTestSuite suite) {
        return getGroupName(suite.getHostData());
    }

    /**
     * Iterate through the list of builds and determine which build 
     * corresponds to the specified test suite.
     * 
     * @param suite    Information about the test suite
     * @return Build information associated with the suite
     */
    private CMnDbBuildData getBuild(CMnDbTestSuite suite) {
        CMnDbBuildData currentBuild = null;
        CMnDbBuildData matchingBuild = null;
        for (int idx = 0; idx < buildList.size(); idx++) {
            currentBuild = (CMnDbBuildData) buildList.get(idx);
            if (currentBuild.getId() == suite.getParentId()) {
                matchingBuild = currentBuild;
            }
        }

        return matchingBuild;
    }

    /**
     * Return the string which identifies the group for the specified suite.
     * Null is returned if the group name cannot be determined.
     * 
     * @param  host    Information about the host
     * @return Name of the group to which the host belongs
     */
    private String getGroupName(CMnDbHostData host) {
        String name = null;
        
        if (getGroupCriteria() == GROUP_BY_HOST) {
            name = host.getHostname();
        } else if (getGroupCriteria() == GROUP_BY_OS) {
            name = host.getOSName();
        }
        
        return name;
    }

    /**
     * Return a hashtable containing lists of test suites for each group which
     * applies to the current grouping criteria.
     * 
     * @return List of test suites in each group
     */
    private Hashtable getSuitesByGroup() {
    	Hashtable groups = new Hashtable();
    	
    	// Iterate through the list of suites
        CMnDbTestSuite currentSuite = null;
        String groupId = null;
        Enumeration keys = testSuites.keys();
        while (keys.hasMoreElements()) {
        	currentSuite = (CMnDbTestSuite) keys.nextElement();
        	groupId = getGroupName(currentSuite);
        	
        	// If a valid group name cannot be found, use the build version
        	if (groupId == null) {
        	    groupId = currentSuite.getSuiteName();
        	}
        	
        	// Add the current build to a group
        	Vector list = null;
        	if (groups.containsKey(groupId)) {
        		list = (Vector) groups.get(groupId);
        		list.add(currentSuite);
        	} else {
        		// Create a new group containing the current build
        		list = new Vector();
        		list.add(currentSuite);
        	    groups.put(groupId, list);
        	}
        }
        
        return groups;
    }
    
    /**
     * Render the build data form.
     */
    public String toString() {
        StringBuffer html = new StringBuffer();

        // Display a list of builds
        html.append(getTitledBorder("Builds", buildsToString()));
        html.append("<p/>\n");
        
        // Display a summary of the test suites grouped by type
        String summary = suitesToString();
        if (summary.length() > 0) {
            html.append(getTitledBorder("Failure Summary", summary));
            html.append("<p/>\n");
        }

        // Display a summary of the failing suites grouped by type
        String failures = failuresToString();
        if (failures.length() > 0) {
            html.append(getTitledBorder("Failing Test Suites", failures));
            html.append("<p/>\n");
        }
        
        return html.toString();
    }
    
    /**
     * Render the list of builds as a string.
     */
    private String buildsToString() {
    	StringBuffer html = new StringBuffer();

    	// Group the builds by JDK vendor
        TreeMap jdkBuilds = new TreeMap();
    	CMnDbBuildData currentBuild = null;
    	CMnDbHostData currentBuildHost = null;
        for (int idx = 0; idx < buildList.size(); idx++) {
        	currentBuild = (CMnDbBuildData) buildList.get(idx);
        	currentBuildHost = currentBuild.getHostData();
        	String buildJdkVendor = currentBuildHost.getJdkVendor();
        	if (jdkBuilds.containsKey(buildJdkVendor)) {
        		Vector builds = (Vector) jdkBuilds.get(buildJdkVendor);
        		builds.add(currentBuild);
        	} else {
        		Vector builds = new Vector();
        		builds.add(currentBuild);
        		jdkBuilds.put(buildJdkVendor, builds);
        	}
        }

        
        // Display the builds in sorted order, grouped by JDK vendor        
        html.append("<table border=\"0\" cellspacing=\"2\" cellpadding=\"5\">\n");
        html.append("  <tr>\n");
        Iterator jdkIter = jdkBuilds.keySet().iterator();
        while (jdkIter.hasNext()) {
        	html.append("    <td valign=\"top\">\n");
            String currentJdkVendor = (String) jdkIter.next();
            Vector builds = (Vector) jdkBuilds.get(currentJdkVendor);
        	html.append("      <b><u>" + currentJdkVendor + "</u></b><br>\n");
            for (int idx = 0; idx < builds.size(); idx++) {
            	currentBuild = (CMnDbBuildData) builds.get(idx);
            	currentBuildHost = currentBuild.getHostData();
                String versionHref = versionUrl + "?" + BUILD_ID_LABEL + "=" + currentBuild.getId();
                html.append("      <a href=\"" + versionHref + "\">" + currentBuild.getBuildVersion() + "</a><br>");
            }
        	html.append("    </td>\n");
        }
        html.append("  </tr>\n");
        html.append("</table>\n");
        
        return html.toString();
    }
    
    /**
     * Render the list of test suites grouped by type.
     */
    private String suitesToString() {
    	StringBuffer html = new StringBuffer();
    	
    	if (testSuites.size() > 0) {
            html.append("<table border=\"0\" cellspacing=\"2\" cellpadding=\"2\" width=\"100%\">\n");
            html.append("  <tr>\n");
            html.append("    <td width=\"40%\"></td>\n");
            html.append("    <td colspan=\"2\" width=\"30%\" align=\"center\"><b><u>Unit Test Failures</u></b></td>\n");
            html.append("    <td colspan=\"2\" width=\"30%\" align=\"center\"><b><u>User Interface Test Failures</u></b></td>\n");
            html.append("  </tr>\n");
        
            // Iterate through each group of test suites
            Hashtable groups = getSuitesByGroup();
            String currentKey = null;
            Vector currentValue = null;
            Enumeration keys = groups.keys();
            while (keys.hasMoreElements()) {
                currentKey = (String) keys.nextElement();
                currentValue = (Vector) groups.get(currentKey);
                html.append(groupToString(currentKey, currentValue));
            }
        
            html.append("</table>\n");
    	}
    	
        return html.toString();
    }
    
    /**
     * Render the current list of test suites as a string.
     */
    private String groupToString(String name, Vector suites) {
    	StringBuffer html = new StringBuffer();

    	int passUnittest = 0;
    	int failUnittest = 0;
    	int totalUnittest = 0;
        int passFlex = 0;
        int failFlex = 0;
        int totalFlex = 0;
    	int passUit = 0;
    	int failUit = 0;
    	int totalUit = 0;

    	// Calculate the test results for the entire group
    	CMnDbTestSuite currentSuite = null;
        for (int idx = 0; idx < suites.size(); idx++) {
            currentSuite = (CMnDbTestSuite) suites.get(idx);

            switch(currentSuite.getSuiteType()) {
                case JUNIT:
                    passUnittest = passUnittest + currentSuite.getPassingCount();
                    totalUnittest = totalUnittest + currentSuite.getTestCount();
                    failUnittest = totalUnittest - passUnittest;
                    break;
                case FLEX:
                    passFlex = passFlex + currentSuite.getPassingCount();
                    totalFlex = totalFlex + currentSuite.getTestCount();
                    failFlex = totalFlex - passFlex;
                    break;
                case UIT:
                    passUit = passUit + currentSuite.getPassingCount();
                    totalUit = totalUit + currentSuite.getTestCount();
                    failUit = totalUit - passUit;
                    break;
                default:
                    html.append("<tr>\n");
                    html.append("  <td colspan=2>" + currentSuite.getSuiteName() + "</td>\n");
                    html.append("  <td colspan=2>" + currentSuite.getTestCount() + "</td>\n");
                    html.append("</tr>\n");
            }
        }
        
    	html.append("  <tr>\n");
    	html.append("    <td>" + name + "</td>\n");
    	
    	NumberFormat ratioFormat = NumberFormat.getPercentInstance();
    	ratioFormat.setMinimumFractionDigits(2);
    	ratioFormat.setMaximumFractionDigits(2);
    	
        // Summarize the unit test results
    	double unittestRatio = (double) 0.0;
        if (totalUnittest > 0) {
            unittestRatio = (float) failUnittest / (float) totalUnittest;
        }
        html.append("    <td align=\"right\" width=\"15%\">" + failUnittest + " / " + totalUnittest + "</td>\n");
        html.append("    <td align=\"left\" width=\"15%\">( " + ratioFormat.format(unittestRatio) + " )</td>\n");

        // Summarize the Flex test results
        double flexRatio = (double) 0.0;
        if (totalFlex > 0) {
            flexRatio = (float) failFlex / (float) totalFlex;
        }
        html.append("    <td align=\"right\" width=\"15%\">" + failFlex + " / " + totalFlex + "</td>\n");
        html.append("    <td align=\"left\" width=\"15%\">(" + ratioFormat.format(flexRatio) + ")</td>\n");
        
        // Summarize the UIT test results
        double uitRatio = (double) 0.0;
        if (totalUit > 0) {
            uitRatio = (float) failUit / (float) totalUit;
        }
        html.append("    <td align=\"right\" width=\"15%\">" + failUit + " / " + totalUit + "</td>\n");
        html.append("    <td align=\"left\" width=\"15%\">(" + ratioFormat.format(uitRatio) + ")</td>\n");
        
    	html.append("  </tr>\n");
        
        return html.toString();
    }
    
    /**
     * Render all of the failing suites as a string.
     */
    private String failuresToString() {
    	StringBuffer html = new StringBuffer();

        CMnDbTestSuite currentSuite = null;
        Vector currentValue = null;

        // Iterate through each suite to group the UIT and unit test
    	Enumeration keys = testSuites.keys();
        TreeMap unittests = new TreeMap();
        TreeMap acttests  = new TreeMap();
        TreeMap flextests = new TreeMap();
        TreeMap uits      = new TreeMap();
        int unittestSuiteFailureCount = 0;
        int actSuiteFailureCount = 0;
        int flexSuiteFailureCount = 0;
        int uitSuiteFailureCount = 0;
        while (keys.hasMoreElements()) {
            currentSuite = (CMnDbTestSuite) keys.nextElement();
            String suiteName = currentSuite.getSuiteName();
            
            // Ignore any suite that doesn't contain at least one failure
            if (currentSuite.getPassingCount() != currentSuite.getTestCount()) {
                switch(currentSuite.getSuiteType()) {
                    case JUNIT:
                        unittestSuiteFailureCount++;
                	if (unittests.containsKey(suiteName)) {
                		Vector suiteList = (Vector) unittests.get(suiteName);
                		suiteList.add(currentSuite);
                	} else {
                	    Vector suiteList = new Vector();
                	    suiteList.add(currentSuite);
                	    unittests.put(suiteName, suiteList);
                	}
                        break;
                    case FLEX:
                        flexSuiteFailureCount++;
                        if (flextests.containsKey(suiteName)) {
                                Vector suiteList = (Vector) flextests.get(suiteName);
                                suiteList.add(currentSuite);
                        } else {
                            Vector suiteList = new Vector();
                            suiteList.add(currentSuite);
                            flextests.put(suiteName, suiteList);
                        }
                        break;
                    case UIT:
            		uitSuiteFailureCount++;
                	if (uits.containsKey(suiteName)) {
                		Vector suiteList = (Vector) uits.get(suiteName);
                		suiteList.add(currentSuite);
                	} else {
                	    Vector suiteList = new Vector();
                	    suiteList.add(currentSuite);
                	    uits.put(suiteName, suiteList);
                	}
                        break;
                    case ACT:
                        actSuiteFailureCount++;
                        if (acttests.containsKey(suiteName)) {
                            Vector suiteList = (Vector) acttests.get(suiteName);
                            suiteList.add(currentSuite);
                        } else {
                            Vector suiteList = new Vector();
                            suiteList.add(currentSuite);
                            acttests.put(suiteName, suiteList);
                        }
                        break;
                }
            }
        }
        
        // Only display the failure summary table if there are failures
        if ((unittestSuiteFailureCount > 0) || (uitSuiteFailureCount > 0) || (flexSuiteFailureCount > 0) || (actSuiteFailureCount > 0)) {
            String currentSuiteName = null;
            Vector currentSuiteList = null;
            html.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"2\" width=\"100%\">\n");

            html.append("  <tr>\n");
            html.append("    <td></td>\n");
            html.append("    <td><b><u>Test Suite</u></b></td>\n");
            html.append("    <td align=\"right\"><b><u>Failures</u></b></td>\n");
            html.append("    <td><b><u>Build</u></b></td>\n");
            html.append("    <td><b><u>Server</u></b></td>\n");
            html.append("    <td><b><u>Operating System</u></b></td>\n");
            html.append("  </tr>\n");
            
            // Render the failing unit test suites
            boolean showUnittestHeader = true;
            Iterator unittestIter = unittests.keySet().iterator();
            while (unittestIter.hasNext()) {
                currentSuiteName = (String) unittestIter.next();
                currentSuiteList = (Vector) unittests.get(currentSuiteName);
                if (showUnittestHeader) {
                    html.append("  <tr>\n");
                    html.append("    <td width=\"15%\" rowspan=\"" + unittestSuiteFailureCount + "\" valign=\"top\" NOWRAP><b>Unit Tests</b></td>\n");
               }
               html.append(suiteFailuresToString(currentSuiteList, showUnittestHeader));
       	       showUnittestHeader = false;
            }

            // Render the failing Flex test suites
            boolean showFlexHeader = true;
            Iterator flexIter = flextests.keySet().iterator();
            while (flexIter.hasNext()) {
                currentSuiteName = (String) flexIter.next();
                currentSuiteList = (Vector) flextests.get(currentSuiteName);
                if (showFlexHeader) {
                    html.append("  <tr>\n");
                    html.append("    <td width=\"15%\" rowspan=\"" + flexSuiteFailureCount + "\" valign=\"top\" NOWRAP><b>Flex Tests</b></td>\n");
                }
                html.append(suiteFailuresToString(currentSuiteList, showFlexHeader));
                showFlexHeader = false;
            }
        
            // Render the failing UIT test suites
            boolean showUitHeader = true;
            Iterator uitIter = uits.keySet().iterator();
            while (uitIter.hasNext()) {
                currentSuiteName = (String) uitIter.next();
       	        currentSuiteList = (Vector) uits.get(currentSuiteName);
                if (showUitHeader) {
                    html.append("  <tr>\n");
                    html.append("    <td width=\"15%\" rowspan=\"" + uitSuiteFailureCount + "\" valign=\"top\" NOWRAP><b>User Interface Tests</b></td>\n");
                }
                html.append(suiteFailuresToString(currentSuiteList, showUitHeader));
       	        showUitHeader = false;
            }

            // Render the failing ACT test suites
            boolean showActHeader = true;
            Iterator actIter = acttests.keySet().iterator();
            while (actIter.hasNext()) {
                currentSuiteName = (String) actIter.next();
                currentSuiteList = (Vector) acttests.get(currentSuiteName);
                if (showActHeader) {
                    html.append("  <tr>\n");
                    html.append("    <td width=\"15%\" rowspan=\"" + actSuiteFailureCount + "\" valign=\"top\" NOWRAP><b>Acceptance Tests</b></td>\n");
                }
                html.append(suiteFailuresToString(currentSuiteList, showActHeader));
                showActHeader = false;
            }
        
            html.append("</table>\n");
        }
        
        return html.toString();
    }
    
    /**
     * Render failure information for a list of test suites.
     */
    private String suiteFailuresToString(Vector failures, boolean skipIntro) {
    	StringBuffer html = new StringBuffer();
    	
    	CMnDbTestSuite currentSuite = null;
        for (int idx = 0; idx < failures.size(); idx++) {
            currentSuite = (CMnDbTestSuite) failures.get(idx);
            int failureCount = currentSuite.getTestCount() - currentSuite.getPassingCount();

            // Figure out if the first row needs a row intro
        	if (skipIntro && (idx == 0)) {
        		// Do nothing
        	} else {
        	    html.append("<tr>\n");
        	}

            if (idx == 0) {
                html.append("  <td width=\"20%\" rowspan=\"" + failures.size() + "\" NOWRAP");
                if (failures.size() > 1) {
                	html.append(" valign=\"top\"");
                }
                html.append(">" + currentSuite.getSuiteName() + "</td>\n");
            }

            // Construct a link directly to the test suite results
            html.append("  <td width=\"5%\" align=\"right\" NOWRAP><a href=\"" + getSuiteUrl(currentSuite) + "\">" + failureCount + "</a></td>\n");

            // Construct a link to the general build
            CMnDbBuildData build = getBuild(currentSuite);
            html.append("  <td width=\"20%\" NOWRAP><a href=\"" + getVersionUrl(build) + "\">" + build.getBuildVersion() + "</a></td>\n");

            // Display general test suite information
            html.append("  <td width=\"20%\" NOWRAP>" + currentSuite.getHostData().getHostname() + "</td>\n");
            html.append("  <td width=\"20%\" NOWRAP>" + currentSuite.getHostData().getOSName() + "</td>\n");
            html.append("</tr>\n");
        }

        return html.toString();
    }
    
}

