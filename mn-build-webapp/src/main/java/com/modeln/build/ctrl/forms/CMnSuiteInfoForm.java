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

import com.modeln.build.common.data.database.CMnQueryData;
import com.modeln.testfw.reporting.CMnDbTestSuite;
import com.modeln.testfw.reporting.CMnDbHostData;

import java.net.URL;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import javax.servlet.http.HttpServletRequest;

import com.modeln.build.web.tags.TextTag;
import com.modeln.build.web.tags.DateTag;
import com.modeln.build.web.tags.OptionTag;
import com.modeln.build.web.tags.SelectTag;
import com.modeln.build.web.tags.TagGroup;



/**
 * The build form provides an HTML interface to the suite object.
 * The class manages transfering the data through the HTTP request.
 * Form data can be rendered in either a read-only or input mode.
 * 
 * @author  Shawn Stafford, Karen An
 */
public class CMnSuiteInfoForm extends CMnBaseForm implements IMnTestForm {

    /** Default title used when displaying a title and border */
    private static final String DEFAULT_TITLE = "Suite Information";

    /** suite ID */
    private TextTag suiteIdTag = new TextTag(SUITE_ID_LABEL);

    /** suite name */
    private TextTag suiteNameTag = new TextTag("suiteName");

    /** Suite group */
    private TextTag suiteGroupTag = new TextTag("suiteGroup");

    /** Suite environment name */
    private TextTag suiteEnvTag = new TextTag("suiteEnv");

    /** Run date field */
    private DateTag runDateTag = new DateTag("runDate");

    /** Run host information */
    private TextTag runHostTag = new TextTag("runHost");

    /** Run JVM vendor information */
    private TextTag runJvmTag = new TextTag("runJvm");

    /** Run host information */
    private TextTag runOSTag = new TextTag("runOS");

    /** Suite data object that has been reconstructed from the form input. */
    private CMnDbTestSuite suite = null;

    /** Suite host data object that has been reconstructed from the form input. */
    private CMnDbHostData suiteHost = null;

    /** Suite JDBC data object */
    private CMnQueryData suiteQuery = null;
    
    /** The URL used when querying the database used by this test suite */
    private URL queryUrl = null;


    /**
     * Construct a build form.
     *
     * @param  form   URL to use when submitting form input
     * @param  images URL to use when referencing images
     */
    public CMnSuiteInfoForm(URL form, URL images) {
        super(form, images);
        setInputMode(false);

        suiteNameTag.setWidth(40);
        //suiteNameTag.setDisabled(true);

        suiteGroupTag.setWidth(40);
        //suiteGroupTag.setDisabled(true);

        suiteEnvTag.setWidth(40);
        //suiteEnvTag.setDisabled(true);

        runDateTag.setDisabled(true);

        runHostTag.setWidth(40);
        //runHostTag.setDisabled(true);

        runJvmTag.setWidth(40);
        //runJvmTag.setDisabled(true);

        runOSTag.setWidth(40);
        //runOSTag.setDisabled(true);

    }

    /**
     * Sets the URL to be used when querying the test database.  This allows
     * the JDBC URL to be hyperlinked.
     *
     * @param  url   Link to the test database query command 
     */ 
    public void setQueryUrl(URL url) {
        queryUrl = url;
    }


    /**
     * Set the suite that should be rendered in the form.
     *
     * @param  suite   Suite information
     */
    public void setValues(CMnDbTestSuite suite) {
    	this.suite = suite;
        if (suite != null) {
            // Pull the field data from the object
            suiteIdTag.setValue(Integer.toString(suite.getId()));
            suiteNameTag.setValue(suite.getSuiteName());
            suiteGroupTag.setValue(suite.getGroupName());
            suiteEnvTag.setValue(suite.getEnvironmentName());
            GregorianCalendar buildDate = new GregorianCalendar();
            if (suite.getStartTime() != null) {
                buildDate.setTime(suite.getStartTime());
            }
            runDateTag.setDate(buildDate);

            CMnDbHostData host = suite.getHostData();
            if (host != null) {
                runHostTag.setValue(host.getUsername() + "@" + host.getHostname());
                runJvmTag.setValue(host.getJdkVendor() + " " + host.getJdkVersion());
                runOSTag.setValue(host.getOSName() + " " + host.getOSVersion() + " (" + host.getOSArchitecture() + ")");
            }
        } else {
            // Clear all of the field values
            suiteIdTag.setValue("");
            suiteNameTag.setValue("");
            suiteGroupTag.setValue("");
            suiteEnvTag.setValue("");
            GregorianCalendar buildDate = new GregorianCalendar();
            runDateTag.setDate(buildDate);
            runHostTag.setValue("");
            runJvmTag.setValue("");
            runOSTag.setValue("");
        }
    }

    /**
     * Set the input fields by examining the HTTP request to see if
     * a value was submitted.
     *
     * @param   req     HTTP request
     */
    public void setValues(HttpServletRequest req) {
    	CMnDbTestSuite suite = (CMnDbTestSuite) req.getAttribute(SUITE_OBJECT_LABEL);
        setValues(suite);
    }


    /**
     * Obtain the uit suite from the fields.  The values must first be populated by
     * calling the setValues method.  A null object may be returned if no data was
     * found in the request.
     *
     * @return   suite found in the request
     */
    public CMnDbTestSuite getValues() {
        return suite;
    }
 

    /**
     * Render the suite info form.
     */
    public String toString() {
        StringBuffer html = new StringBuffer();

        // Don't display the ID value to the user
        suiteIdTag.setHidden(true);
        html.append(suiteIdTag.toString());

        html.append("<table border=\"0\" cellspacing=\"0\" cellpadding=\"2\" width=\"100%\" bgcolor=\"#FFFFFF\">\n");
        html.append("  <tr>\n");
        html.append("    <td width=\"30%\"><b>Suite Name:</b></td>\n");
        html.append("    <td width=\"70%\">");
        if (inputEnabled) { 
        	html.append(" INPUT ENABLED ");
            html.append(suiteNameTag.toString());
        } else {
            if ((formUrl != null) && (suite != null)) {
                html.append("<a href=\"" + formUrl.toString() + "?" + SUITE_ID_LABEL + "=" + suite.getId() + "\">");
                html.append(suiteNameTag.getValue());
                html.append("</a>");
            } else {
                html.append(suiteNameTag.getValue());
            }
        }
        html.append("</td>\n");
        html.append("  </tr>\n");


        if ((suiteGroupTag.getValue() != null) && (suiteGroupTag.getValue().trim().length() > 0)) {
            html.append("  <tr>\n");
            html.append("    <td><b>Suite Group:</b></td>\n");
            html.append("    <td>");
            if (inputEnabled) {
                html.append(suiteGroupTag.toString());
            } else {
                html.append(suiteGroupTag.getValue());
            }
            html.append("</td>\n");
            html.append("  </tr>\n");
        }

        if ((suiteEnvTag.getValue() != null) && (suiteEnvTag.getValue().trim().length() > 0)) {
            html.append("  <tr>\n");
            html.append("    <td><b>Environment Name:</b></td>\n");
            html.append("    <td>");
            if (inputEnabled) {
                html.append(suiteEnvTag.toString());
            } else {
                html.append(suiteEnvTag.getValue());
            }
            if ((suite != null) && (suite.getMaxThreadCount() > 0)) {
                html.append(" (" + suite.getMaxThreadCount() + " thread");
                if (suite.getMaxThreadCount() > 1) {
                    html.append("s");
                }
                html.append(")");
            }
            html.append("</td>\n");
            html.append("  </tr>\n");
        }
        
        html.append("  <tr>\n");
        html.append("    <td><b>Run Date:</b></td>\n");
        html.append("    <td>");
        if (inputEnabled) {
            html.append(runDateTag.toString());
        } else {
            html.append(fullDateFormat.format(runDateTag.getDate()));
        }
        html.append("</td>\n");
        html.append("  </tr>\n");

        // Host on which the build was performed
        html.append("  <tr>\n");
        html.append("    <td><b>Host:</b></td>\n");
        html.append("    <td>");
        if (inputEnabled) {
            html.append(runHostTag.toString());
        } else {
            html.append(runHostTag.getValue());
        }
        html.append("</td>\n");
        html.append("  </tr>\n");

        // JDK used to perform the build
        html.append("  <tr>\n");
        html.append("    <td><b>JDK:</b></td>\n");
        html.append("    <td>");
        if (inputEnabled) {
            html.append(runJvmTag.toString());
        } else {
            html.append(runJvmTag.getValue());
        }
        html.append("</td>\n");
        html.append("  </tr>\n");

        // Operating system on which the build was performed
        html.append("  <tr>\n");
        html.append("    <td><b>OS:</b></td>\n");
        html.append("    <td>");
        if (inputEnabled) {
            html.append(runOSTag.toString());
        } else {
            html.append(runOSTag.getValue());
        }
        html.append("</td>\n");
        html.append("  </tr>\n");

        // Database against which the test suite ran its tests
        html.append("  <tr>\n");
        html.append("    <td><b>Database:</b></td>\n");
        html.append("    <td>");
        if ((queryUrl != null) && (suiteQuery != null)) {
            html.append("<a href=\"" + queryUrl.toString() + "?" + CMnDatabaseQueryForm.getUrlParams(suiteQuery) + "\">");
            html.append(suite.getJdbcUrl());
            html.append("</a>");
        } else {
            html.append(suite.getJdbcUrl());
        }
        html.append("</td>\n");
        html.append("  </tr>\n");

        html.append("</table>\n");

        return html.toString();
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
