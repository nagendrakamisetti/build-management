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

import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import com.modeln.testfw.reporting.CMnDbFeatureOwnerData;
import com.modeln.testfw.reporting.CMnDbHostData;
import com.modeln.testfw.reporting.CMnDbTestSuite;


/**
 * The test suite group contains configuration properties that 
 * determine whether a test suite data object will be classified
 * as part of the group based upon the group parameters. 
 *
 * @author  Shawn Stafford
 */
public class CMnTestSuiteGroup extends CMnBaseTestForm {

    /** 
     * Number of columns in the display table.  This number is used to 
     * calculate colspan values for rows with empty cells.  When columns 
     * are disabled, the total number of columns will decrease by the 
     * number of disabled columns.
     */ 
    private static final int COLUMN_COUNT = 10;


    /** Display name for the group */
    private String groupName;

    /** Defines a representative member of the group. */
    private CMnDbTestSuite groupPrototype;

    /** Criteria used to sort the results */
    private int sortByCriteria = SORT_BY_HOST;

    /** List of test suites that belong to the group */
    private Hashtable groups = new Hashtable();

    /** List of product areas used to map features to areas */
    protected Vector<CMnDbFeatureOwnerData> ownerList;


    /** Number of test suites excluded by the filter criteria */
    private int excludeCount = 0;

    /** Disables the display of the environment column in the suite results */
    private boolean showEnvColumn = true;

    /** Disables the display of the time column in the suite results */
    private boolean showTimeColumn = true;

    /** Disables the display of the skip column in the suite results */
    private boolean showSkipColumn = true;

    /** Disables the display of the blacklist column in the suite results */
    private boolean showBlacklistColumn = true;

    /** Disables the display of the long running test column in the suite results */
    private boolean showLongColumn = true;

    /** Disables the display of the killed test column in the suite results */
    private boolean showKilledColumn = true;

    /** Disables the display of the total test column in the suite results */
    private boolean showTotalColumn = true;




    /**
     * Construct a group based on the specified type parameters.  The group
     * prototype is a test suite object that is representative of every
     * member of the group.  This prototype object will be compared to each
     * potential group member to determine if the potential member should 
     * belong to the group.  Only the potential members whose grouping criteria
     * fields match the prototype fields will be included in the group. 
     *
     * @param  form       URL to use when submitting form input
     * @param  images     URL to use when referencing images
     * @param  name       Display name for the group
     * @param  prototype  A representative member of the suite
     */
    public CMnTestSuiteGroup(URL form, URL images, String name, CMnDbTestSuite prototype) {
        super(form, images);
        groupName = name;
        groupPrototype = prototype;
        setGroupCriteria(GROUP_BY_TYPE);
    }

    /**
     * Set the criteria that determines how the members of the group will be
     * sorted when displayed to the user.  Options include SORT_BY_HOST or
     * SORT_BY_OS.
     * 
     * @param  order  Type of sort order
     */
    public void setSubgroupOrder(int order) {
        sortByCriteria = order;
        sort();
    }

    /**
     * Return the sorting criteria used when displaying group members to the
     * user.  Options include GROUP_BY_HOST or GROUP_BY_OS.
     *
     * @return Group sort order
     */
    public int getSubgroupOrder() {
        return sortByCriteria;
    }

    /** 
     * Return the name of the group.
     * 
     * @return  Group name
     */
    public String getName() {
        return groupName;
    }

    /**
     * Return the total number of subgroups.
     *
     * @return Number of subgroups
     */
    public int getGroupCount() {
        return groups.size();
    }

    /**
     * Return the number of suites excluded from groups due to the filter
     * criteria.
     *
     * @return Number of suites excluded from display
     */
    public int getExcludeCount() {
        return excludeCount;
    }

    /**
     * Return the total number of suites across all subgroups.
     *
     * @return Total number of suites
     */
    public int getSuiteCount() {
        int total = 0;

        // Iterate through each subgroup
        Collection groupList = groups.values();
        Iterator iter = groupList.iterator();
        while (iter.hasNext()) {
            Vector suiteList = (Vector) iter.next();
            total = total + suiteList.size();
        }

        return total;
    }

    /**
     * Determine if the suites contain valid grouping information.
     *
     * @return  TRUE if the suites contain group names
     */
    public boolean canGroupByName() {
        boolean result = true;

        // Iterate through each subgroup
        Collection groupList = groups.values();
        Iterator iter = groupList.iterator();
        while (iter.hasNext()) {
            Vector suites = (Vector) iter.next();
            Enumeration suiteList = suites.elements(); 
            while (suiteList.hasMoreElements()) {
                CMnDbTestSuite suite = (CMnDbTestSuite) suiteList.nextElement();
                if (suite.getGroupName() == null) {
                    result = false;
                }
            }
        }

        return result;
    }


    /**
     * Return the total number of tests for the current group.
     *
     * @return Total number of tests
     */
    public int getTestCount() {
        int total = 0;

        // Iterate through each subgroup
        Collection groupList = groups.values();
        Iterator iter = groupList.iterator();
        while (iter.hasNext()) { 
            Vector suiteList = (Vector) iter.next();

            // Iterate through each suite in the subgroup
            CMnDbTestSuite currentSuite = null;
            for (int idx = 0; idx < suiteList.size(); idx++) {
                currentSuite = (CMnDbTestSuite) suiteList.get(idx);
                total = total + currentSuite.getTestCount();
            }

        }

        return total;
    }


    /**
     * Return the total time for the current group.
     *
     * @return Total number of passing tests
     */
    public long getElapsedTime() {
        long total = 0;

        // Iterate through each subgroup
        Collection groupList = groups.values();
        Iterator iter = groupList.iterator();
        while (iter.hasNext()) {
            Vector suiteList = (Vector) iter.next();

            // Iterate through each suite in the subgroup
            CMnDbTestSuite currentSuite = null;
            for (int idx = 0; idx < suiteList.size(); idx++) {
                currentSuite = (CMnDbTestSuite) suiteList.get(idx);
                total = total + currentSuite.getElapsedTime();
            }

        }

        return total;
    }


    /**
     * Return the total number of passing tests for the current group.
     *
     * @return Total number of passing tests
     */
    public int getPassingCount() {
        int total = 0;

        // Iterate through each subgroup
        Collection groupList = groups.values();
        Iterator iter = groupList.iterator();
        while (iter.hasNext()) {
            Vector suiteList = (Vector) iter.next();

            // Iterate through each suite in the subgroup
            CMnDbTestSuite currentSuite = null;
            for (int idx = 0; idx < suiteList.size(); idx++) {
                currentSuite = (CMnDbTestSuite) suiteList.get(idx);
                total = total + currentSuite.getPassingCount();
            }

        }

        return total;
    }

    /**
     * Return the total number of executed tests for the current group.
     *
     * @return Total number of executed tests
     */
    public int getExecutedCount() {
        int total = 0;

        // Iterate through each subgroup
        Collection groupList = groups.values();
        Iterator iter = groupList.iterator();
        while (iter.hasNext()) {
            Vector suiteList = (Vector) iter.next();

            // Iterate through each suite in the subgroup
            CMnDbTestSuite currentSuite = null;
            for (int idx = 0; idx < suiteList.size(); idx++) {
                currentSuite = (CMnDbTestSuite) suiteList.get(idx);
                total = total + currentSuite.getExecutedCount();
            }

        }

        return total;
    }


    /**
     * Return the total number of failing tests for the current group.
     *
     * @return Total number of failing tests
     */
    public int getFailingCount() {
        int total = 0;

        // Iterate through each subgroup
        Collection groupList = groups.values();
        Iterator iter = groupList.iterator();
        while (iter.hasNext()) {
            Vector suiteList = (Vector) iter.next();

            // Iterate through each suite in the subgroup
            CMnDbTestSuite currentSuite = null;
            for (int idx = 0; idx < suiteList.size(); idx++) {
                currentSuite = (CMnDbTestSuite) suiteList.get(idx);
                int failures = currentSuite.getFailingCount() + currentSuite.getErrorCount();
                if (failures > 0) {
                    total = total + failures; 
                }
            }

        }

        return total;
    }


    /**
     * Add a test suite to the group.
     * 
     * @param  suite   Test suite to add to the group
     */
    public void addMember(CMnDbTestSuite suite) {
        int order = sortByCriteria;

        // Determine the correct key to use based on the sort type
        Vector list = null;
        Object key = null;
        switch (order) {
            case SORT_BY_HOST:
                CMnDbHostData host = suite.getHostData();
                key = host.getHostname();
                break;
            case SORT_BY_OS:
                CMnDbHostData osHost = suite.getHostData();
                key = osHost.getOSName();
                break;
            case SORT_BY_GID:
                key = new Long(suite.getGroupId());
                break;
            case SORT_BY_NAME:
                key = suite.getGroupName();
                // Attempt to fall back to the suite name if no group is defined
                if (key == null) {
                    key = suite.getSuiteName();
                }
                break;
            default:
                key = new Integer(suite.getId());
                break;
        }

        // Determine if the member matches a filter criteria
        boolean allowMember = true;
        if (hasFilterCriteria()) {
            allowMember = matchesFilterCriteria((String) key); 
        }

        // Add the current suite to the selected subgroup
        if (allowMember) {
            if (groups.containsKey(key)) {
                list = (Vector) groups.get(key);
                list.add(suite);
            } else {
                list = new Vector();
                list.add(suite);
                groups.put(key, list);
            }
        } else {
            excludeCount++;
        }

    }

    /** 
     * Return the list of test suites that belong to the group.
     *
     * @return  Suite list
     */
    public Vector getMembers() {
        Vector members = new Vector();

        // Iterate through each subgroup
        Collection groupList = groups.values();
        Iterator iter = groupList.iterator();
        while (iter.hasNext()) {
            Vector suiteList = (Vector) iter.next();
            for (int idx = 0; idx < suiteList.size(); idx++) {
                members.add(suiteList.get(idx));
            }
        }

        return members;
    }
 

    /**
     * Determine if the suite should be part of the group by comparing it
     * to the prototype.
     *
     * @param  candidate   Potential group member
     * @return TRUE if the candidate should be included in the group
     */
    public boolean allowsMember(CMnDbTestSuite candidate) {
        boolean isMember = false;

        switch (getGroupCriteria()) {
            case GROUP_BY_TYPE:
                isMember = groupPrototype.hasAllOptions(candidate.getOptions());
                break;
            case GROUP_BY_HOST:
                String prototypeHost = groupPrototype.getHostData().getHostname();
                String candidateHost = candidate.getHostData().getHostname();
                isMember = prototypeHost.equals(candidateHost); 
                break;
            case GROUP_BY_OS: 
                String prototypeOS = groupPrototype.getHostData().getOSName();
                String candidateOS = candidate.getHostData().getOSName();
                isMember = prototypeOS.equals(candidateOS);
                break;
            case GROUP_BY_GID:
                isMember = (groupPrototype.getGroupId() == candidate.getGroupId());
                break;
            case GROUP_BY_NAME:
                String prototypeName = groupPrototype.getGroupName();
                String candidateName = candidate.getGroupName();
                if ((prototypeName == null) && (candidateName == null)) {
                    isMember = true;
                } else if ((prototypeName != null) && (candidateName != null)) {
                    isMember = prototypeName.equals(candidateName);
                } else {
                    isMember = false;
                }
                break;
        }

        return isMember;
    }

    /**
     * Determine if the suite should be part of the group by comparing it
     * to the prototype.
     *
     * @param  candidate   Potential group member
     * @return Formatted text that shows the comparison 
     */
    public String compareMember(CMnDbTestSuite candidate) {
        StringBuffer diff = new StringBuffer();

        switch (getGroupCriteria()) {
            case GROUP_BY_HOST:
                String prototypeHost = groupPrototype.getHostData().getHostname();
                String candidateHost = candidate.getHostData().getHostname();
                diff.append("Host: prototype=" + prototypeHost + ", candidate=" + prototypeHost);
                break;
            case GROUP_BY_OS:
                String prototypeOS = groupPrototype.getHostData().getOSName();
                String candidateOS = candidate.getHostData().getOSName();
                diff.append("OS: prototype=" + prototypeOS + ", candidate=" + prototypeOS);
                break;
            case GROUP_BY_GID:
                long prototypeGid = groupPrototype.getGroupId();
                long candidateGid = candidate.getGroupId();
                diff.append("GID: prototype=" + prototypeGid + ", candidate=" + candidateGid);
                break;
            case GROUP_BY_NAME:
                String prototypeName = groupPrototype.getGroupName();
                String candidateName = candidate.getGroupName();
                diff.append("Group: prototype=" + prototypeName + ", candidate=" + candidateName);
                break;
        }

        return diff.toString();
    }

    /**
     * Sort the list of suites by a grouping criteria such as GROUP_BY_HOST 
     * or GROUP_BY_OS.
     */
    private void sort() {
        // Obtain a list of the current members
        Vector members = getMembers();

        // Rebuild the group list
        groups = new Hashtable();
        for (int idx = 0; idx < members.size(); idx++) {
            addMember((CMnDbTestSuite) members.get(idx));
        }
    }


    /**
     * Enable or disable the display of all columns.
     *
     * @param  enable  TRUE if the columns should be displayed, FALSE otherwise
     */
    public void enableAllColumns(boolean enable) {
        enableEnvColumn(enable);
        enableTimeColumn(enable);
        enableTotalColumn(enable);
        enableLongColumn(enable);
        enableSkipColumn(enable);
        enableBlacklistColumn(enable);
        enableKilledColumn(enable);
    }

    /**
     * Enable or disable the display of the environment where the tests were executed. 
     *
     * @param  enable   TRUE if the column should be displayed, FALSE otherwise
     */
    public void enableEnvColumn(boolean enable) {
        showEnvColumn = enable;
    }

    /**
     * Enable or disable the display of the time spent executing the tests.
     *
     * @param  enable   TRUE if the column should be displayed, FALSE otherwise
     */
    public void enableTimeColumn(boolean enable) {
        showTimeColumn = enable;
    }

    /**
     * Enable or disable the display of the total number of tests in the suite. 
     *
     * @param  enable   TRUE if the column should be displayed, FALSE otherwise
     */
    public void enableTotalColumn(boolean enable) {
        showTotalColumn = enable;
    }

    /**
     * Enable or disable the display of the long running tests. 
     *
     * @param  enable   TRUE if the column should be displayed, FALSE otherwise
     */
    public void enableLongColumn(boolean enable) {
        showLongColumn = enable;
    }

    /**
     * Enable or disable the display of the killed tests.
     *
     * @param  enable   TRUE if the column should be displayed, FALSE otherwise
     */
    public void enableKilledColumn(boolean enable) {
        showKilledColumn = enable;
    }

    /**
     * Enable or disable the display of the skipped tests.
     *
     * @param  enable   TRUE if the column should be displayed, FALSE otherwise
     */
    public void enableSkipColumn(boolean enable) {
        showSkipColumn = enable;
    }

    /**
     * Enable or disable the display of the blacklisted tests.
     *
     * @param  enable   TRUE if the column should be displayed, FALSE otherwise
     */
    public void enableBlacklistColumn(boolean enable) {
        showBlacklistColumn = enable;
    }


    /**
     * Sets the list of product areas that will be used to map features to areas.
     *
     * @param   areas    List of product areas
     */
    public void setProductOwners(Vector<CMnDbFeatureOwnerData> areas) {
        ownerList = areas;
    }

    /**
     * Return the product area associated with the test suite.
     *
     * @param   suite    Test suite
     * @return  Product area
     */
    public CMnDbFeatureOwnerData getProductOwner(CMnDbTestSuite suite) {
        // Iterate through each product area to determine who owns this suite
        CMnDbFeatureOwnerData currentArea = null;
        Iterator iter = ownerList.iterator();
        while (iter.hasNext()) {
            currentArea = (CMnDbFeatureOwnerData) iter.next();
            if (currentArea.hasFeature(suite.getGroupName())) {
                return currentArea;
            }
        }

        return null;
    }



    /**
     * Render a single test suite group.
     */
    public String toString() {
        StringBuffer html = new StringBuffer();

        // Don't do anything if the group has no members
        if (getSuiteCount() > 0) {

            // Calcualate the totale number of columns based on how many are disabled
            int colCount = COLUMN_COUNT;
            if (!showTotalColumn) colCount--;
            if (!showLongColumn)  colCount--;
            if (!showEnvColumn)   colCount--;
            if (!showTimeColumn)  colCount--;
            if (!showSkipColumn)  colCount--;
            if (!showKilledColumn)  colCount--;
            if (!showBlacklistColumn) colCount--;

            // Calculate the number of columns spanned by the group name  
            // When in admin mode AND the suites are not collapsed, 
            // an extra column appears containg a trash can for deleting suites
            int grpSpan = 1;
            if (getAdminMode() && (getDeleteUrl() != null)) {
                colCount = colCount + 1;
                grpSpan = 2;
            }

            // Determine how to render the suites
            if ((ownerList != null) && canGroupByName()) {
                html.append(getAreaGroups(colCount, grpSpan));
            } else {
                html.append(getSuiteGroups(colCount, grpSpan));
            }

        } // suiteCount > 0

        return html.toString();
    }


    /**
     * Render the table header.
     *
     * @param   name    Name of the suite group
     * @param   colCount    Number of columns in the table
     * @param   grpSpan     Number of columns spanned by the group header
     * @return  HTML representing the row header
     */
    public String getRowHeader(String name, int colCount, int grpSpan) {
        StringBuffer html = new StringBuffer();

        html.append("  <tr>\n");
        html.append("    <td width=\"40%\" colspan=" + grpSpan + "><b>" + name + "</b></td>\n");
        if (getCollapseCriteria() == COLLAPSE_BY_NONE) {
            html.append("    <td width=\"5%\"><b><u>Env</u></b></td>\n");
            html.append("    <td width=\"10%\"><b><u>Host</u></b></td>\n");
            html.append("    <td width=\"10%\"><b><u>OS</u></b></td>\n");
        }
        if (showTimeColumn) {
            html.append("    <td width=\"5%\" align=\"right\"><b><u>Time</u></b></td>\n");
        }
        html.append("    <td width=\"5%\" align=\"right\"><b><u>Pass</u></b></td>\n");
        html.append("    <td width=\"5%\" align=\"right\"><b><u>Fail</u></b></td>\n");
        if (showSkipColumn) {
            html.append("    <td width=\"5%\" align=\"right\"><b><u>Skip</u>&nbsp;&nbsp;</b></td>\n");
        }
        if (showBlacklistColumn) {
            html.append("    <td width=\"5%\" align=\"right\"><b><u>Black</u>&nbsp;&nbsp;</b></td>\n");
        }
        if (showLongColumn) {
            html.append("    <td width=\"5%\" align=\"right\"><b><u>Long</u>&nbsp;&nbsp;</b></td>\n");
        }
        if (showKilledColumn) {
            html.append("    <td width=\"5%\" align=\"right\"><b><u>Kill</u>&nbsp;&nbsp;</b></td>\n");
        }
        if (showTotalColumn) {
            html.append("    <td width=\"5%\" align=\"right\"><b><u>Total</u></b></td>\n");
        }
        html.append("  </tr>\n");

        return html.toString();
    }

    /**
     * Render a group of suites as a single line of data in a table.
     *
     * @param   name    Name of the row in the table
     * @param   url     Link for accessing the details for the current row
     * @param   suites  List of test suites associated with the row
     * @return  HTML representing the row in a table
     */
    public String getSuiteRow(String name, String url, Vector<CMnDbTestSuite> suites) {
        StringBuffer html = new StringBuffer();

        // Keep track of the subtotals for the current group
        long subtotalTime = 0;
        int subtotalPass  = 0;
        int subtotalSkip  = 0;
        int subtotalBlack = 0;
        int subtotalLong  = 0;
        int subtotalFail  = 0;
        int subtotalKill  = 0;
        int subtotalExec  = 0;
        int subtotal = 0;

        // Calculate the failures for each suite in the current area
        if ((suites != null) && (suites.size() > 0)) {
            Enumeration suiteList = suites.elements();
            while (suiteList.hasMoreElements()) {
                CMnDbTestSuite currentSuite = (CMnDbTestSuite) suiteList.nextElement();

                long execTime = currentSuite.getElapsedTime();
                int passCount = currentSuite.getPassingCount();
                int skipCount = currentSuite.getSkipCount();
                int blCount   = currentSuite.getBlacklistCount();
                int testCount = currentSuite.getTestCount();
                int killCount = currentSuite.getKilledCount();
                int execCount = currentSuite.getExecutedCount();
                int longCount = currentSuite.getLongCount();
                int failCount = currentSuite.getFailingCount() + currentSuite.getErrorCount();

                // Skip suites that have no tests
                if ((testCount > 0) || (execCount > 0)) {
                    // Keep a running count for the total for the current group 
                    subtotalTime  = subtotalTime + execTime;
                    subtotalPass  = subtotalPass + passCount;
                    subtotalSkip  = subtotalSkip + skipCount;
                    subtotalBlack = subtotalBlack + blCount;
                    subtotalFail  = subtotalFail + failCount;
                    subtotalLong  = subtotalLong + longCount;
                    subtotalKill  = subtotalKill + killCount;
                    subtotalExec  = subtotalExec + execCount;
                    subtotal = subtotal + testCount;
                }

            }

            // Only display the row if tests exist in this group
            if ((subtotal > 0) || (subtotalExec > 0)) {
                int numcomplete = subtotalPass + subtotalFail + subtotalSkip + subtotalBlack + subtotalKill;
                boolean incomplete = (subtotal > numcomplete);

                // Select the row background color based on the content of the cell
                String rowBg = DEFAULT_BGLIGHT;
                String timeBg = DEFAULT_BGLIGHT;
                String passBg = DEFAULT_BGLIGHT;
                String longBg = DEFAULT_BGLIGHT;
                String skipBg = DEFAULT_BGLIGHT;
                String blBg   = DEFAULT_BGLIGHT;
                String failBg = DEFAULT_BGLIGHT;
                String testBg = DEFAULT_BGLIGHT;
                if (incomplete) {
                    rowBg = WARNING_BGLIGHT;
                    timeBg = rowBg;
                    passBg = rowBg;
                    skipBg = rowBg;
                    blBg   = rowBg;
                    longBg = rowBg;
                    failBg = rowBg;
                    testBg = WARNING_BGDARK;
                } else if ((subtotalLong > 0) && (subtotalFail > 0) && (getHighlightCriteria() == HIGHLIGHT_FAILING)) {
                    rowBg = ERROR_BGLIGHT;
                    timeBg = rowBg;
                    passBg = rowBg; 
                    skipBg = rowBg;
                    blBg   = rowBg;
                    longBg = ERRORLONG_BGDARK;
                    failBg = ERROR_BGDARK;
                    testBg = rowBg;
                } else if ((subtotalFail > 0) && (getHighlightCriteria() == HIGHLIGHT_FAILING)) {
                    rowBg = ERROR_BGLIGHT;
                    timeBg = rowBg;
                    passBg = rowBg;
                    failBg = ERROR_BGDARK;
                    skipBg = rowBg;
                    blBg   = rowBg;
                    longBg = ERROR_BGLIGHT;
                    testBg = rowBg;
                } else if ((subtotalPass > 0) && (getHighlightCriteria() == HIGHLIGHT_PASSING)) {
                    rowBg = WARNING_BGLIGHT;
                    timeBg = rowBg;
                    passBg = WARNING_BGDARK;
                    skipBg = rowBg;
                    blBg   = rowBg;
                    longBg = rowBg;
                    failBg = rowBg;
                    testBg = rowBg; 
                } else if ((subtotalLong > 0) && (getHighlightCriteria() == HIGHLIGHT_FAILING)) {
                    rowBg = ERRORLONG_BGLIGHT;
                    failBg = ERRORLONG_BGLIGHT;
                    timeBg = rowBg;
                    passBg = rowBg;
                    skipBg = rowBg;
                    blBg   = rowBg;
                    longBg = ERRORLONG_BGDARK;
                    testBg = rowBg; 
                } else if ((subtotalSkip > 0) && (getHighlightCriteria() == HIGHLIGHT_SKIPPED)) {
                    rowBg = ERRORSKIP_BGLIGHT;
                    failBg = rowBg;
                    timeBg = rowBg;
                    passBg = rowBg;
                    skipBg = ERRORSKIP_BGDARK;
                    blBg   = rowBg;
                    longBg = rowBg;
                    testBg = rowBg;
                }

                //
                // Render the HTML table cells
                //
                html.append("  <tr>\n");

                // Render the row name with a link if possible
                html.append("    <td bgcolor=\"" + rowBg + "\">");
                if ((url != null) && (url.length() > 0)) {
                    html.append("<a href=\"" + url + "\">" + name + "</a>");
                } else {
                    html.append(name);
                }
                // Indicate that the tests are incomplete
                if (incomplete) {
                    html.append(" (incomplete)");
                }
                html.append("</td>\n");
                if (showTimeColumn) {
                    html.append("    <td align=\"right\" bgcolor=\"" + timeBg + "\" NOWRAP><b>" + formatTime(subtotalTime) + "</b></td>\n");
                }
                html.append("    <td align=\"right\" bgcolor=\"" + passBg + "\" NOWRAP><b>" + subtotalPass + "</b></td>\n");
                html.append("    <td align=\"right\" bgcolor=\"" + failBg + "\" NOWRAP><b>" + subtotalFail + "</b></td>\n");
                if (showSkipColumn) {
                    html.append("    <td align=\"right\" bgcolor=\"" + skipBg + "\" NOWRAP><b>" + subtotalSkip + "&nbsp;&nbsp;</b></td>\n");
                }
                if (showBlacklistColumn) {
                    html.append("    <td align=\"right\" bgcolor=\"" + blBg + "\" NOWRAP><b>" + subtotalBlack + "&nbsp;&nbsp;</b></td>\n");
                }
                if (showLongColumn) {
                    html.append("    <td align=\"right\" bgcolor=\"" + longBg + "\" NOWRAP><b>" + subtotalLong + "&nbsp;&nbsp;</b></td>\n");
                }
                if (showKilledColumn) {
                    html.append("    <td align=\"right\" bgcolor=\"" + longBg + "\" NOWRAP><b>" + subtotalKill + "&nbsp;&nbsp;</b></td>\n");
                }
                if (showTotalColumn) {
                    html.append("    <td align=\"right\" bgcolor=\"" + testBg + "\" NOWRAP><b>");
                    if (incomplete) {
                        html.append(numcomplete + " of " + subtotal);
                    } else {
                        html.append(subtotal);
                    }
                    html.append("</b></td>\n");
                }

                html.append("  </tr>\n");

            } // subtotal > 0

        }

        return html.toString();
    }


    /**
     * Render a single test suite group.
     *
     * @param   colCount    Number of columns in the table
     * @param   grpSpan     Number of columns spanned by the group header
     */
    public String getAreaGroups(int colCount, int grpSpan) {
        StringBuffer html = new StringBuffer();

        html.append("<table border=\"0\" cellspacing=\"0\" cellpadding=\"1\" width=\"100%\" bgcolor=\"#FFFFFF\">\n");

        // Define a list of the areas that contain suites
        Hashtable<String, Vector> areas = new Hashtable<String, Vector>();
        String currentArea = null;
        Vector<CMnDbTestSuite> currentSuites = null;

        //
        // Group the suites by product area
        // 
        Collection groupList = groups.values();
        Iterator iter = groupList.iterator();
        while (iter.hasNext()) {

            // Iterate through each suite
            Vector suiteList = (Vector) iter.next();
            CMnDbTestSuite currentSuite = null;
            for (int idx = 0; idx < suiteList.size(); idx++) {
                currentSuite = (CMnDbTestSuite) suiteList.get(idx);
                CMnDbFeatureOwnerData area = getProductOwner(currentSuite);

                // Create a default bucket if the suites do not belong to a defined area
                if (area != null) {
                    currentArea = area.getDisplayName();
                } else {
                    currentArea = "Other";
                }

                // If the area already contains suites, add the current suite
                currentSuites = areas.get(currentArea);
                if (currentSuites != null) {
                    currentSuites.add(currentSuite); 
                } else {
                    currentSuites = new Vector<CMnDbTestSuite>();
                    currentSuites.add(currentSuite);
                    areas.put(currentArea, currentSuites);
                }
            }

        }


        // Render the table header
        String header = getRowHeader("Product Areas", colCount, grpSpan);
        html.append(header);

        //
        // Render the product areas
        //
        int buildId = 0;
        Enumeration keys = areas.keys();
        while (keys.hasMoreElements()) {
            currentArea = (String) keys.nextElement();

            // Define the URL used to access the report details
            String rowUrl = null;
            Vector<CMnDbTestSuite> suites = areas.get(currentArea); 
            if ((suites != null) && (suites.size() > 0)) {
                // Construct a unique set of feature names
                HashSet<String> names = new HashSet<String>();
                Enumeration list = suites.elements(); 
                while (list.hasMoreElements()) {
                    CMnDbTestSuite suite = (CMnDbTestSuite) list.nextElement();
                    names.add(suite.getGroupName());

                    // Get the build ID
                    if ((buildId == 0) && (suite.getParentId() > 0)) {
                        buildId = suite.getParentId();
                    }

                }

                StringBuffer namestr = new StringBuffer();
                Iterator nameIter = names.iterator();
                while (nameIter.hasNext()) {
                    namestr.append((String) nameIter.next());
                    if (nameIter.hasNext()) {
                        namestr.append("|");
                    }
                }

                // Set the URL for this row
                rowUrl = getFormUrl().toString() + "?" + 
                    IMnBuildForm.BUILD_ID_LABEL + "=" + buildId + "&" + 
                    FORM_GROUP_LABEL + "=name" + "&" + 
                    FORM_FILTER_LABEL + "=" + namestr;
            }

            // Create the HTML string for this row
            String row = getSuiteRow(currentArea, rowUrl, suites);
            html.append(row);
        }

        // Render and indicator to show if tests have been filtered out of the results
        if (excludeCount > 0) {
            html.append("  <tr>\n");
            html.append("    <td colspan=" + colCount + ">" + excludeCount + " suites excluded by filter: <i>" + getFilterCriteria() + "</i></td>\n");
            html.append("  </tr>\n");
        }

        html.append("</table>\n");

        return html.toString();
    }


    /**
     * Render a single test suite group.
     *
     * @param   colCount    Number of columns in the table
     * @param   grpSpan     Number of columns spanned by the group header
     */
    public String getSuiteGroups(int colCount, int grpSpan) {
        StringBuffer html = new StringBuffer();
        StringBuffer grpBody = new StringBuffer();

        long totalTime = 0;
        int totalPass  = 0;
        int totalSkip  = 0;
        int totalBlack = 0;
        int totalLong  = 0;
        int totalFail  = 0;
        int totalExec  = 0;
        int total = 0;

        // Don't do anything if the group has no members
        if (getSuiteCount() > 0) {

            // Select the build ID for the set of suites
            int buildId = 0;

            // Iterate through each subgroup
            Collection groupList = groups.values();
            Iterator iter = groupList.iterator();
            while (iter.hasNext()) {

                // Keep track of the subtotals for the current group
                long subtotalTime = 0;
                int subtotalPass  = 0;
                int subtotalSkip  = 0;
                int subtotalBlack = 0;
                int subtotalLong  = 0;
                int subtotalFail  = 0;
                int subtotalKill  = 0;
                int subtotalExec  = 0;
                int subtotal = 0;

                Vector suiteList = (Vector) iter.next();
                Collections.sort(suiteList);
                CMnDbTestSuite currentSuite = null;
                CMnDbHostData suiteHost = null;
                String suiteUrl = null;
                String groupName = null;
                long groupId = 0;
                for (int idx = 0; idx < suiteList.size(); idx++) {
                    currentSuite = (CMnDbTestSuite) suiteList.get(idx);
                    suiteHost = currentSuite.getHostData();
                    long execTime = currentSuite.getElapsedTime();
                    int passCount = currentSuite.getPassingCount();
                    int skipCount = currentSuite.getSkipCount();
                    int blCount   = currentSuite.getBlacklistCount();
                    int testCount = currentSuite.getTestCount();
                    int execCount = currentSuite.getExecutedCount();
                    int killCount = currentSuite.getKilledCount();
                    int longCount = currentSuite.getLongCount();
                    int failCount = currentSuite.getFailingCount() + currentSuite.getErrorCount();

                    // Skip suites that have no tests
                    if ((testCount > 0) || (execCount > 0)) {
                        // Keep a running count for the total for the current group 
                        subtotalTime  = subtotalTime  + execTime;
                        subtotalPass  = subtotalPass  + passCount;
                        subtotalSkip  = subtotalSkip  + skipCount;
                        subtotalBlack = subtotalBlack + blCount;
                        subtotalFail  = subtotalFail  + failCount;
                        subtotalLong  = subtotalLong  + longCount;
                        subtotalKill  = subtotalKill  + killCount;
                        subtotalExec  = subtotalExec  + execCount;
                        subtotal = subtotal + testCount;

                        // Keep a running count for all groups
                        totalTime  = totalTime  + execTime;
                        totalPass  = totalPass  + passCount;
                        totalSkip  = totalSkip  + skipCount;
                        totalBlack = totalBlack + blCount;
                        totalLong  = totalLong  + longCount;
                        totalFail  = totalFail  + failCount;
                        totalExec  = totalExec  + execCount;
                        total = total + testCount;

                        String rowBg = DEFAULT_BGLIGHT;
                        String timeBg = DEFAULT_BGLIGHT;
                        String passBg = DEFAULT_BGLIGHT;
                        String skipBg = DEFAULT_BGLIGHT;
                        String blBg   = DEFAULT_BGLIGHT;
                        String longBg = DEFAULT_BGLIGHT;
                        String failBg = DEFAULT_BGLIGHT;
                        String testBg = DEFAULT_BGLIGHT; 
                        if (!currentSuite.isComplete()) {
                            rowBg = WARNING_BGLIGHT;
                            timeBg = rowBg;
                            passBg = rowBg;
                            skipBg = rowBg;
                            blBg   = rowBg;
                            longBg = rowBg;
                            failBg = rowBg;
                            testBg = WARNING_BGDARK;
                        } else if ((longCount > 0) && (failCount > 0) && (getHighlightCriteria() == HIGHLIGHT_FAILING)) {
                            rowBg = ERROR_BGLIGHT;
                            timeBg = rowBg;
                            passBg = rowBg;
                            skipBg = rowBg;
                            blBg   = rowBg;
                            longBg = ERRORLONG_BGDARK;
                            failBg = ERROR_BGDARK;
                            testBg = rowBg;
                        } else if ((failCount > 0) && (getHighlightCriteria() == HIGHLIGHT_FAILING)) {
                            rowBg = ERROR_BGLIGHT;
                            timeBg = rowBg;
                            passBg = rowBg;
                            failBg = ERROR_BGDARK;
                            skipBg = rowBg;
                            blBg   = rowBg;
                            longBg = ERROR_BGLIGHT;
                            testBg = rowBg;
                        } else if ((passCount > 0) && (getHighlightCriteria() == HIGHLIGHT_PASSING)) {
                            rowBg = WARNING_BGLIGHT;
                            timeBg = rowBg;
                            passBg = WARNING_BGDARK;
                            skipBg = rowBg;
                            blBg   = rowBg;
                            longBg = rowBg;
                            failBg = rowBg;
                            testBg = rowBg; 
                        } else if ((longCount > 0) && (getHighlightCriteria() == HIGHLIGHT_FAILING)) {
                            rowBg = ERRORLONG_BGLIGHT;
                            failBg = ERRORLONG_BGLIGHT;
                            timeBg = rowBg;
                            passBg = rowBg;
                            skipBg = rowBg;
                            blBg   = rowBg;
                            longBg = ERRORLONG_BGDARK;
                            testBg = rowBg;
                        } else if ((skipCount > 0) && (getHighlightCriteria() == HIGHLIGHT_SKIPPED)) {
                            rowBg = ERRORSKIP_BGLIGHT;
                            failBg = rowBg;
                            timeBg = rowBg;
                            passBg = rowBg;
                            skipBg = ERRORSKIP_BGDARK;
                            blBg   = rowBg;
                            longBg = rowBg;
                            testBg = rowBg;
                        } 

                        // Get the build ID
                        if ((buildId == 0) && (currentSuite.getParentId() > 0)) {
                            buildId = currentSuite.getParentId();
                        }

                        // Display the suite information and pass/fail summary 
                        if (getCollapseCriteria() == COLLAPSE_BY_NONE) {
                            suiteUrl = getFormUrl().toString() + "?" +
                                IMnTestForm.SUITE_ID_LABEL + "=" + currentSuite.getId();
                            grpBody.append("  <tr>\n");

                            // Display a trashcan icon for deleting suites when in admin mode
                            if (getAdminMode() && (getDeleteUrl() != null)) {
                                grpBody.append("    <td width=\"2%\" bgcolor=\"" + rowBg + "\">");
                                String deleteImg = null;
                                if (getImageUrl() != null) {
                                    deleteImg = "<img border=\"0\" src=\"" + getImageUrl().toString() + "/icons_small/trashcan_red.png\" alt=\"Delete\"/>";
                                } else {
                                    deleteImg = "[del]";
                                }
                                String deleteUrl = getDeleteUrl().toString() + "?" +
                                    IMnTestForm.SUITE_ID_LABEL + "=" + currentSuite.getId();
                                grpBody.append("<a href=\"" + deleteUrl + "\">" + deleteImg + "</a>");
                                grpBody.append("</td>\n");
                            }

                            grpBody.append("    <td width=\"43%\" bgcolor=\"" + rowBg + "\">\n");
                            grpBody.append("      <a href=\"" + suiteUrl + "\">" + currentSuite.getSuiteName() + "</a>\n");
                            if (!currentSuite.isComplete()) {
                                grpBody.append(" (incomplete)");
                            }
                            grpBody.append("    </td>\n");

                            if (showEnvColumn) {
                                grpBody.append("    <td width=\"5%\" bgcolor=\"" + rowBg + "\">");
                                if (currentSuite.getEnvironmentName() != null) {
                                    grpBody.append(currentSuite.getEnvironmentName());
                                }
                                grpBody.append("</td>\n");
                            }

                            grpBody.append("    <td width=\"15%\" bgcolor=\"" + rowBg + "\">" + suiteHost.getHostname() + "</td>\n");
                            grpBody.append("    <td width=\"15%\" bgcolor=\"" + rowBg + "\">" + suiteHost.getOSName() + "</td>\n");

                            if (showTimeColumn) {
                                String timeStr = null;
                                if (currentSuite.isComplete()) {
                                    timeStr = formatTime(execTime);
                                } else {
                                    timeStr = "(" + formatTime(execTime) + ")";
                                }
                                grpBody.append("    <td width=\"5%\" bgcolor=\"" + timeBg + "\" align=\"right\" NOWRAP>" + timeStr + "</td>\n");
                            }

                            grpBody.append("    <td width=\"5%\" bgcolor=\"" + passBg + "\" align=\"right\" NOWRAP>" + passCount + "</td>\n");
                            grpBody.append("    <td width=\"5%\" bgcolor=\"" + failBg + "\" align=\"right\" NOWRAP>" + failCount + "</td>\n");

                            if (showSkipColumn) {
                                grpBody.append("    <td width=\"5%\" bgcolor=\"" + skipBg + "\" align=\"right\" NOWRAP>" + skipCount + "</td>\n");
                            }
                            if (showBlacklistColumn) {
                                grpBody.append("    <td width=\"5%\" bgcolor=\"" + blBg + "\" align=\"right\" NOWRAP>" + blCount + "</td>\n");
                            }
                            if (showLongColumn) {
                                grpBody.append("    <td width=\"5%\" bgcolor=\"" + longBg + "\" align=\"right\" NOWRAP>" + longCount + "</td>\n");
                            }
                            if (showKilledColumn) {
                                grpBody.append("    <td width=\"5%\" bgcolor=\"" + longBg + "\" align=\"right\" NOWRAP>" + killCount + "</td>\n");
                            }


                            if (showTotalColumn) {
                                String totalStr = null;
                                if (currentSuite.isComplete()) {
                                    totalStr = Integer.toString(testCount);
                                } else {
                                    totalStr = "&nbsp;" + execCount + " of " + testCount;
                                }
                                grpBody.append("    <td width=\"5%\" bgcolor=\"" + testBg + "\" align=\"right\" NOWRAP>" + totalStr + "</td>\n");
                            }

                            grpBody.append("  </tr>\n");
                        } else {
                            if ((groupName == null) && (currentSuite.getGroupName() != null)) {
                                groupName = currentSuite.getGroupName();
                            } else if ((groupName == null) && (currentSuite.getGroupName() == null)) {
                                // Fall back to display the suite name if the group name is null
                                groupName = currentSuite.getSuiteName();
                            }
                            if ((groupId == 0) && (currentSuite.getGroupId() > 0)) {
                                groupId = currentSuite.getGroupId();
                            }
                        } // if group by GID

                    } // Suite contains zero tests

                } // Iterate through each suite 


                // Display the subtotal for the group
                if (subtotal > 0) {
                    if (getCollapseCriteria() == COLLAPSE_BY_NONE) {
                        String rowColor = "#FFFFFF";
                        if (excludeCount > 0) {
                            rowColor = "#FFFFAA";
                        }
                        grpBody.append("  <tr>\n");

                        // Calculate the number of columns to span for the subtotal label
                        // By default, span the following columns: Name, Host, OS
                        int subtotalSpan = 3;
                        if (showEnvColumn) {
                            subtotalSpan++;
                        }
                        if (getAdminMode() && (getDeleteUrl() != null)) {
                            subtotalSpan++;
                        }

                        grpBody.append("    <td align=\"right\" colspan=" + subtotalSpan + " bgcolor=\"" + rowColor + "\">\n");
                        if (excludeCount > 0) {
                            // Display a summary of the number of suites excluded by the filter
                            grpBody.append("<table border=\"0\" cellspacing=\"0\" cellpadding=\"1\" width=\"100%\">\n");
                            grpBody.append("  <tr>\n");
                            grpBody.append("    <td align=\"left\" bgcolor=\"#FFFFAA\">\n");
                            grpBody.append("      " + excludeCount + " suites excluded by filter: <i>" + getFilterCriteria() + "</i>");
                            grpBody.append("    </td>\n");
                            grpBody.append("    <td align=\"right\"><b>Subtotal: </b></td>\n");
                            grpBody.append("  </tr>\n");
                            grpBody.append("</table>\n");
                        } else {
                            grpBody.append("      <b>Subtotal: </b>\n");
                            if (hasFilterCriteria()) {
                                grpBody.append("      <!-- Filter criteria applied: " + getFilterCriteria() + " -->\n");
                            } else {
                                grpBody.append("      <!-- Filter criteria applied: none -->\n");
                            }
                        }
                        grpBody.append("    </td>\n");
                        if (showTimeColumn) {
                            grpBody.append("    <td align=\"right\" bgcolor=\"" + rowColor + "\" NOWRAP><b>" + formatTime(subtotalTime) + "</b></td>\n");
                        }
                        grpBody.append("    <td align=\"right\" bgcolor=\"" + rowColor + "\" NOWRAP><b>" + subtotalPass + "</b></td>\n");
                        grpBody.append("    <td align=\"right\" bgcolor=\"" + rowColor + "\" NOWRAP><b>" + subtotalFail + "</b></td>\n");
                        if (showSkipColumn) {
                            grpBody.append("    <td align=\"right\" bgcolor=\"" + rowColor + "\" NOWRAP><b>" + subtotalSkip + "</b></td>\n");
                        }
                        if (showBlacklistColumn) {
                            grpBody.append("    <td align=\"right\" bgcolor=\"" + rowColor + "\" NOWRAP><b>" + subtotalBlack + "</b></td>\n");
                        }
                        if (showLongColumn) {
                            grpBody.append("    <td align=\"right\" bgcolor=\"" + rowColor + "\" NOWRAP><b>" + subtotalLong + "</b></td>\n");
                        }
                        if (showKilledColumn) {
                            grpBody.append("    <td align=\"right\" bgcolor=\"" + rowColor + "\" NOWRAP><b>" + subtotalKill + "</b></td>\n");
                        }
                        if (showTotalColumn) {
                            grpBody.append("    <td align=\"right\" bgcolor=\"" + rowColor + "\" NOWRAP><b>" + subtotal + "</b></td>\n");
                        }
                        grpBody.append("  </tr>\n");
                        grpBody.append("  <tr><td colspan=\"" + colCount + "\" valign=\"bottom\">&nbsp;</td>\n");
                    } else {
                        int numcomplete = subtotalPass + subtotalFail + subtotalSkip + subtotalBlack + subtotalKill;
                        boolean incomplete = (subtotal > numcomplete);

                        String rowBg = DEFAULT_BGLIGHT;
                        String timeBg = DEFAULT_BGLIGHT;
                        String passBg = DEFAULT_BGLIGHT;
                        String skipBg = DEFAULT_BGLIGHT;
                        String blBg   = DEFAULT_BGLIGHT;
                        String longBg = DEFAULT_BGLIGHT;
                        String failBg = DEFAULT_BGLIGHT;
                        String testBg = DEFAULT_BGLIGHT;
                        if (incomplete) {
                            rowBg = WARNING_BGLIGHT;
                            timeBg = rowBg;
                            passBg = rowBg;
                            skipBg = rowBg;
                            blBg   = rowBg;
                            longBg = rowBg;
                            failBg = rowBg;
                            testBg = WARNING_BGDARK;
                        } else if ((subtotalLong > 0) && (subtotalFail > 0) && (getHighlightCriteria() == HIGHLIGHT_FAILING)) {
                            rowBg = ERROR_BGLIGHT;
                            timeBg = rowBg;
                            passBg = rowBg;
                            skipBg = rowBg;
                            blBg   = rowBg;
                            longBg = ERRORLONG_BGDARK;
                            failBg = ERROR_BGDARK;
                            testBg = rowBg;
                        } else if ((subtotalFail > 0) && (getHighlightCriteria() == HIGHLIGHT_FAILING)) {
                            rowBg = ERROR_BGLIGHT;
                            timeBg = rowBg;
                            passBg = rowBg;
                            failBg = ERROR_BGDARK;
                            skipBg = rowBg;
                            blBg   = rowBg;
                            longBg = ERROR_BGLIGHT;
                            testBg = rowBg;
                         } else if ((subtotalPass > 0) && (getHighlightCriteria() == HIGHLIGHT_PASSING)) {
                            rowBg = WARNING_BGLIGHT;
                            timeBg = rowBg;
                            passBg = WARNING_BGDARK;
                            skipBg = rowBg;
                            blBg   = rowBg;
                            longBg = rowBg;
                            failBg = rowBg;
                            testBg = rowBg; 
                        } else if ((subtotalLong > 0) && (getHighlightCriteria() == HIGHLIGHT_FAILING)) {
                            rowBg = ERRORLONG_BGLIGHT;
                            failBg = ERRORLONG_BGLIGHT;
                            timeBg = rowBg;
                            passBg = rowBg;
                            skipBg = rowBg;
                            blBg   = rowBg;
                            longBg = ERRORLONG_BGDARK;
                            testBg = rowBg;
                        } else if ((subtotalLong > 0) && (getHighlightCriteria() == HIGHLIGHT_FAILING)) {
                            rowBg = ERRORSKIP_BGLIGHT;
                            failBg = rowBg;
                            timeBg = rowBg;
                            passBg = rowBg;
                            skipBg = ERRORSKIP_BGDARK;
                            blBg   = rowBg;
                            longBg = rowBg;
                            testBg = rowBg;
                        }

                        grpBody.append("  <tr>\n");
                        String groupUrl = null;
                        if (getCollapseCriteria() == COLLAPSE_BY_GID) {
                            groupUrl = getFormUrl().toString() + "?" + 
                                IMnBuildForm.BUILD_ID_LABEL + "=" + buildId + "&" +
                                IMnTestForm.GROUP_ID_LABEL + "=" + groupId;
                            grpBody.append("    <td bgcolor=\"" + rowBg + "\">");
                            grpBody.append("<a href=\"" + groupUrl + "\">" + groupName + "</a>");
                            if (incomplete) {
                                grpBody.append(" (incomplete)");
                            }
                            grpBody.append("</td>\n");
                        } else {
                            groupUrl = getFormUrl().toString() + "?" + 
                                IMnBuildForm.BUILD_ID_LABEL + "=" + buildId + "&" +
                                IMnTestForm.GROUP_NAME_LABEL + "=" + groupName;
                            grpBody.append("    <td bgcolor=\"" + rowBg + "\">");
                            grpBody.append("<a href=\"" + groupUrl + "\">" + groupName + "</a>");
                            if (incomplete) {
                                grpBody.append(" (incomplete)");
                            }
                            grpBody.append("</td>\n");
                        }

                        if (showTimeColumn) {
                            grpBody.append("    <td align=\"right\" bgcolor=\"" + timeBg + "\" NOWRAP><b>" + formatTime(subtotalTime) + "</b></td>\n");
                        }
                        grpBody.append("    <td align=\"right\" bgcolor=\"" + passBg + "\" NOWRAP><b>" + subtotalPass + "</b></td>\n");
                        grpBody.append("    <td align=\"right\" bgcolor=\"" + failBg + "\" NOWRAP><b>" + subtotalFail + "</b></td>\n");
                        if (showSkipColumn) {
                            grpBody.append("    <td align=\"right\" bgcolor=\"" + skipBg + "\" NOWRAP><b>" + subtotalSkip + "</b></td>\n");
                        }
                        if (showBlacklistColumn) {
                            grpBody.append("    <td align=\"right\" bgcolor=\"" + blBg + "\" NOWRAP><b>" + subtotalBlack + "</b></td>\n");
                        }
                        if (showLongColumn) {
                            grpBody.append("    <td align=\"right\" bgcolor=\"" + longBg + "\" NOWRAP><b>" + subtotalLong + "</b></td>\n");
                        }
                        if (showKilledColumn) {
                            grpBody.append("    <td align=\"right\" bgcolor=\"" + longBg + "\" NOWRAP><b>" + subtotalKill + "</b></td>\n");
                        }
                        if (showTotalColumn) {
                            grpBody.append("    <td align=\"right\" bgcolor=\"" + testBg + "\" NOWRAP><b>");
                            if (incomplete) {
                                grpBody.append(numcomplete + " of " + subtotal);
                            } else {
                                grpBody.append(subtotal);
                            }
                            grpBody.append("</b></td>\n");
                        }

                        grpBody.append("  </tr>\n");
                    } // if group criteria is GID or NAME

                } // subtotal > 0
      
            } // Iterating through each subgroup

            // Display the group title and headers
            String hostHeader = null;
            String osHeader = null;
            html.append("<table border=\"0\" cellspacing=\"0\" cellpadding=\"1\" width=\"100%\" bgcolor=\"#FFFFFF\">\n");
            html.append("  <tr>\n");
            html.append("    <td width=\"55%\" colspan=" + grpSpan + "><b>" + groupName + "</b></td>\n");
            if (getCollapseCriteria() == COLLAPSE_BY_NONE) {
                html.append("    <td width=\"5%\"><b><u>Env</u></b></td>\n");
                html.append("    <td width=\"10%\"><b><u>Host</u></b></td>\n");
                html.append("    <td width=\"10%\"><b><u>OS</u></b></td>\n");
            }
            if (showTimeColumn) {
                html.append("    <td width=\"5%\" align=\"right\"><b><u>Time</u></b></td>\n");
            }
            html.append("    <td width=\"5%\" align=\"right\"><b><u>Pass</u></b></td>\n");
            html.append("    <td width=\"5%\" align=\"right\"><b><u>Fail</u></b></td>\n");
            if (showSkipColumn) {
                html.append("    <td width=\"5%\" align=\"right\"><b><u>Skip</u></b></td>\n");
            }
            if (showBlacklistColumn) {
                html.append("    <td width=\"5%\" align=\"right\"><b><u>Black</u></b></td>\n");
            }
            if (showLongColumn) {
                html.append("    <td width=\"5%\" align=\"right\"><b><u>Long</u></b></td>\n");
            }
            if (showKilledColumn) {
                html.append("    <td width=\"5%\" align=\"right\"><b><u>Kill</u></b></td>\n");
            }
            if (showTotalColumn) {
                html.append("    <td width=\"5%\" align=\"right\"><b><u>Total</u></b></td>\n");
            }
            html.append("  </tr>\n");
            html.append(grpBody.toString());

            // Render and indicator to show if tests have been filtered out of the results
            if (excludeCount > 0) {
                html.append("  <tr>\n");
                html.append("    <td colspan=" + colCount + ">" + excludeCount + " suites excluded by filter: <i>" + getFilterCriteria() + "</i></td>\n");
                html.append("  </tr>\n");
            }

            html.append("</table>\n");

            // Embed the summary information in the HTML comments so they can be parsed by other scripts
            if (totalFail > 0) {
                html.append("\n<!-- TOTAL FAIL: " + totalFail + " -->\n");
            }


        } else {
            if (excludeCount > 0) {
                html.append("<table border=\"0\" cellspacing=\"0\" cellpadding=\"1\" width=\"100%\" bgcolor=\"#FFFFFF\">\n");
                html.append("  <tr><td colspan=" + grpSpan + "><b>" + groupName + "</b></td></tr>\n");
                html.append("  <tr>\n");
                html.append("    <td width=\"2%\">&nbsp;</td>\n");
                html.append("    <td>" + excludeCount + " suites excluded by filter: <i>" + getFilterCriteria() + "</i></td>\n");
                html.append("  </tr>\n");
                html.append("</table>\n");
            }
        }

        return html.toString();
    }

}

