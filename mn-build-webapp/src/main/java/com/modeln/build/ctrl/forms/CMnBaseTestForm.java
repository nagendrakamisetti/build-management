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
import java.text.SimpleDateFormat;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.servlet.http.HttpServletRequest;
import com.modeln.build.web.tags.TextTag;
import com.modeln.build.web.tags.SelectTag;

import com.modeln.testfw.reporting.CMnDbBuildStatusData;


/**
 * This provides base functionality for all HTML forms.
 * 
 * @author  Shawn Stafford
 */
public class CMnBaseTestForm extends CMnBaseForm {


    /** Form field that indicates the grouping criteria */
    public static final String FORM_GROUP_LABEL = "grp";

    /** Form field that indicates the filter criteria */
    public static final String FORM_FILTER_LABEL = "filter";


    /** Form field that indicates the status of the page */
    public static final String FORM_STATUS_LABEL = "mode";

    /** Form status value indicating that the form data should be updated */
    public static final String UPDATE_DATA = "update";

    /** Form status value indicating that the form data should be deleted */
    public static final String DELETE_DATA = "delete";

    /** Form status value indicating that the form data should be in view-only mode */
    public static final String VIEW_DATA = "view";



    /** Sort the suites by suite ID */
    public static final int SORT_BY_ID = 0;

    /** Sort suites that ran on the same host */
    public static final int SORT_BY_HOST = 1;

    /** Sort suites that ran on the same operating system. */
    public static final int SORT_BY_OS = 2;

    /** Sort suites that were generated as part of the same test group */
    public static final int SORT_BY_GID = 3;

    /** Sort suites by the group name */
    public static final int SORT_BY_NAME = 4;



    /** Group suites that match the unit test suite type. */
    public static final int GROUP_BY_TYPE = 0;

    /** Group suites that ran on the same host */
    public static final int GROUP_BY_HOST = 1;

    /** Group suites that ran on the same operating system. */
    public static final int GROUP_BY_OS = 2;

    /** Group suites that were generated as part of the same test group */
    public static final int GROUP_BY_GID = 3;

    /** Group suites by the group name */
    public static final int GROUP_BY_NAME = 4;


    /** Do not collapse the suite results */
    public static final int COLLAPSE_BY_NONE = 0;

    /** Collapse the suites that have the same group ID */
    public static final int COLLAPSE_BY_GID = 1;

    /** Collapse the suites that have the same group name */
    public static final int COLLAPSE_BY_NAME = 2;


    /** Do not highlight any tests */
    public static final int HIGHLIGHT_NONE = 0;

    /** Highlight any failing tests */
    public static final int HIGHLIGHT_FAILING = 1;

    /** Highlight any passing tests */
    public static final int HIGHLIGHT_PASSING = 2;

    /** Highlight any skipped tests */
    public static final int HIGHLIGHT_SKIPPED = 3;

    /** Highlight any long tests */
    public static final int HIGHLIGHT_LONG = 4;


    
    /** Criteria used to group the results */
    private int groupByCriteria = GROUP_BY_TYPE;

    /** Type of value used to collapse the results into a single line */
    private int collapseByCriteria = COLLAPSE_BY_NONE;

    /** Regular expression used to filter the results */
    private Pattern filterCriteria = null;
    private String  filterString = null;


    /** Criteria used to determine which results to highlight */
    private int highlightCriteria = HIGHLIGHT_FAILING;

    

    /**
     * Construct a base form.
     * 
     * @param  url    URL to use when submitting form input
     */
    public CMnBaseTestForm(URL form, URL images) {
        super(form, images);
    }

    /**
     * Set the input fields by examining the HTTP request to see if
     * a value was submitted.
     *
     * @param   req     HTTP request
     */
    public void setValues(HttpServletRequest req) {
        super.setValues(req);

        // Obtain the filter criteria from the URL or session
        String regex = req.getParameter(FORM_FILTER_LABEL);
        if (regex == null) {
            // Fall back to the session value if no URL parameter is found
            regex = (String) req.getAttribute(FORM_FILTER_LABEL);
        }
        if (regex != null) {
            try {
                setFilterCriteria(regex);
            } catch (PatternSyntaxException pex) {
                String regexError = "The form value does not conform to the rules for this field: field=" + FORM_FILTER_LABEL + ", value=" + regex;
                throw new IllegalArgumentException(regexError, pex);
            }
        }
    }



    /**
     * Set the criteria that determines which suites to highlight.  Options
     * include HIGHLIGHT_NONE, HIGHLIGHT_FAILING, and HIGHLIGHT_PASSING.
     *
     * @param   criteria   Type of tests to highlight
     */
    public void setHighlightCriteria(int criteria) {
        highlightCriteria = criteria;
    }

    /**
     * Return the criteria that determines which suites to highlight.  Options
     * include HIGHLIGHT_NONE, HIGHLIGHT_FAILING, and HIGHLIGHT_PASSING.
     *
     * @return   Type of tests to highlight
     */
    public int getHighlightCriteria() {
        return highlightCriteria;
    }

    /**
     * Set the criteria that determines which test suites will be classified
     * as part of the group.   Options include GROUP_BY_TYPE, GROUP_BY_HOST, 
     * and GROUP_BY_OS.
     *
     * @param  criteria   Type of group membes to allow
     */
    public void setGroupCriteria(int criteria) {
        groupByCriteria = criteria;
    }

    /**
     * Return the criteria that determines which test suites will be classified
     * as part of the group.   Options include GROUP_BY_TYPE, GROUP_BY_HOST, 
     * and GROUP_BY_OS.
     *
     * @return   Type of group membes to allow
     */
    public int getGroupCriteria() {
        return groupByCriteria;
    }

    /**
     * Set the criteria that determines whether the test suites should be
     * collapsed into a single line.  Options include COLLAPSE_BY_NONE,
     * COLLAPSE_BY_GID, COLLAPSE_BY_HOST, COLLAPSE_BY_NAME. 
     *
     * @param  criteria   Type of suites to collapse 
     */
    public void setCollapseCriteria(int criteria) {
        collapseByCriteria = criteria;
    }
    
    /**
     * Return the criteria that determines whether the test suites should be
     * collapsed into a single line. 
     *
     * @return   Type of suites to collapse 
     */
    public int getCollapseCriteria() { 
        return collapseByCriteria;
    }


    /**
     * Set the regular expression that will be used to filter the suite results.
     * If the criteria is null, all suites will be displayed on the form.
     * Otherwise, the regular expression will be matched against the group
     * criteria field and only matching suites will be displayed on the form.
     *
     * @param regexp  Regular expression used to match against the group criteria
     */
    public void setFilterCriteria(String regexp) throws PatternSyntaxException {
        filterString = regexp;
        filterCriteria = Pattern.compile(regexp);
    }

    /**
     * Return true if a filter criteria has been applied to the form. 
     *
     * @return  TRUE if a regular expression is being used to filter the results 
     */
    public boolean hasFilterCriteria() {
        return (filterCriteria != null);
    }

    /**
     * Return true if the specified string matches the filter criteria.
     *
     * @param  text   String to analyze using the filter criteria
     * @return TRUE if the specified string matches
     */
    public boolean matchesFilterCriteria(String text) {
        Matcher matcher = filterCriteria.matcher(text);
        return matcher.matches();
    }

    /**
     * Return the filter criteria used.
     *
     * @return String representing the filter regex
     */
    public String getFilterCriteria() {
        if (filterCriteria != null) {
            return filterCriteria.pattern();
        } else {
            return null;
        }
    }

    /**
     * Returns the original string used to build the filter regular expression.
     * This value is useful for debugging regular expression compilation errors.
     *
     * @return String representing the filter regex
     */
    public String getFilterString() {
        return filterString;
    }


}

