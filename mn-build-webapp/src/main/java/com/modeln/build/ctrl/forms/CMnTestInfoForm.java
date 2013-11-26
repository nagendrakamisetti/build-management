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

import com.modeln.testfw.reporting.CMnDbTestData;
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
 * The build form provides an HTML interface to a generic test object.
 * The class manages transfering the data through the HTTP request.
 * Form data can be rendered in either a read-only or input mode.
 * 
 * @author  Shawn Stafford, Karen An
 */
public class CMnTestInfoForm extends CMnBaseForm implements IMnTestForm {

    /** Default title used when displaying a title and border */
    private static final String DEFAULT_TITLE = "Test Information";

    /** Test  ID */
    private TextTag testIdTag = new TextTag(TEST_ID_LABEL);

    /** Test  name */
    private TextTag testNameTag = new TextTag("testName");

    /** Run date field */
    private DateTag runDateTag = new DateTag("runDate");

    /** Test Status information */
    private TextTag statusTag = new TextTag("status");

    /** Last Passed Test information */
    private DateTag lastPassTag = new DateTag("lastPass");

    /** Last Failed Test information */
    private DateTag lastFailTag = new DateTag("lastFail");

    /** Test data object that has been reconstructed from the form input. */
    private CMnDbTestData testData = null;

    /** Last passed Test data object that has been reconstructed from the form input. */
    private CMnDbTestData lastPass = null;

    /** Last failed Test data object that has been reconstructed from the form input. */
    private CMnDbTestData lastFail = null;

    /** The Test URL */
    private URL testUrl = null;

    /**
     * Construct a build form.
     *
     * @param  form   URL to use when submitting form input
     * @param  images URL to use when referencing images
     */
    public CMnTestInfoForm(URL form, URL images) {
        super(form, images);
        setInputMode(false);
        setUrl(form);

        testNameTag.setWidth(40);
        //testNameTag.setDisabled(true);
        runDateTag.setDisabled(true);
        statusTag.setWidth(40);
        lastPassTag.setDisabled(true);
        lastFailTag.setDisabled(true);

    }

    /**
     * Sets the URL to be used when querying by test id number.  This allows
     * the test name to be hyperlinked.
     *
     * @param  url   Link to the test id command
     */ 
    public void setUrl(URL url) {
        testUrl = url;
    }


    /**
     * Set the Test that should be rendered in the form.
     *
     * @param  testData  Test data object
     */
    public void setValues(CMnDbTestData testData, CMnDbTestData lastPass, CMnDbTestData lastFail) {
    	this.testData = testData;
    	this.lastPass=lastPass;
    	this.lastFail=lastFail;
        if (testData != null) {
            // Pull the field data from the object
            testIdTag.setValue(Integer.toString(testData.getId()));
            testNameTag.setValue(testData.getDisplayName());
            if (testData.getStartTime() != null) {
                GregorianCalendar runDate = new GregorianCalendar();
                runDate.setTime(testData.getStartTime());
                runDateTag.setDate(runDate);
            }
            statusTag.setValue(testData.getDisplayStatus());

            // Set last passing test
            GregorianCalendar lastPassedDate = new GregorianCalendar();
            if ((lastPass != null) && (lastPass.getStartTime() != null)) {
                lastPassedDate.setTime(lastPass.getStartTime());
            } 
            lastPassTag.setDate(lastPassedDate);

            // Set last failing test
            GregorianCalendar lastFaileddDate = new GregorianCalendar();
            if ((lastFail != null) && (lastFail.getStartTime() != null)) {
                lastFaileddDate.setTime(lastFail.getStartTime());
            } 
            lastFailTag.setDate(lastFaileddDate);
        } else {
            // Clear all of the field values
            testIdTag.setValue("");
            testNameTag.setValue("");
            GregorianCalendar runDate = new GregorianCalendar();
            runDateTag.setDate(runDate);
            statusTag.setValue("");
            GregorianCalendar lastPassedDate = new GregorianCalendar();
            GregorianCalendar lastFaileddDate = new GregorianCalendar();
            lastPassTag.setDate(lastPassedDate);
            lastFailTag.setDate(lastFaileddDate);
        }
    }

    /**
     * Set the input fields by examining the HTTP request to see if
     * a value was submitted.
     *
     * @param   req     HTTP request
     */
    public void setValues(HttpServletRequest req) {
    	CMnDbTestData testData = (CMnDbTestData) req.getAttribute(IMnTestForm.TEST_OBJECT_LABEL);
    	CMnDbTestData lastPass = (CMnDbTestData) req.getAttribute(IMnTestForm.LASTPASS_OBJECT_LABEL);
    	CMnDbTestData lastFail = (CMnDbTestData) req.getAttribute(IMnTestForm.LASTFAIL_OBJECT_LABEL);
        setValues(testData, lastPass, lastFail);
    }


    /**
     * Obtain the test from the fields.  The values must first be populated by
     * calling the setValues method.  A null object may be returned if no data was
     * found in the request.
     *
     * @return   Test found in the request
     */
    public CMnDbTestData getValues() {
        return testData;
    }
 

    /**
     * Render the test info form.
     */
    public String toString() {
        StringBuffer html = new StringBuffer();

        // Don't display the ID value to the user
        testIdTag.setHidden(true);
        html.append(testIdTag.toString());

        //Test Name:
        html.append("<table border=\"0\" cellspacing=\"0\" cellpadding=\"2\" width=\"100%\" bgcolor=\"#FFFFFF\">\n");
        html.append("  <tr>\n");
        html.append("    <td width=\"30%\"><b>Test Name:</b></td>\n");
        html.append("    <td width=\"70%\">");
        if (inputEnabled) { 
            html.append(testNameTag.toString());
        } else {
            if ((testUrl != null) && (testData != null)) {
                html.append("<a href=\"" + testUrl.toString() + "?" + TEST_ID_LABEL + "=" + testData.getId() + "\">");
                html.append(testNameTag.getValue());
                html.append("</a>");
            } else {
                html.append(testNameTag.getValue());
            }
        }
        html.append("</td>\n");
        html.append("  </tr>\n");

        // Extended test information
        String extendedInfo = getExtendedInfo();
        if (extendedInfo != null) {
            html.append(getExtendedInfo());
        }

        //Run Date:
        html.append("  <tr>\n");
        html.append("    <td><b>Run Date:</b></td>\n");
        html.append("    <td>");
        if (runDateTag.getDate() != null) {
            if (inputEnabled) {
                html.append(runDateTag.toString());
            } else {
                html.append(fullDateFormat.format(runDateTag.getDate()));
            }
        }
        html.append("</td>\n");
        html.append("  </tr>\n");

        //Status:
        html.append("  <tr>\n");
        html.append("    <td><b>Status:</b></td>\n");
        html.append("    <td>");
        if (inputEnabled) {
            html.append(statusTag.toString());
        } else {
            html.append(statusTag.getValue());
        }
        html.append("</td>\n");
        html.append("  </tr>\n");
        
        //Most Recently Passed:
        html.append("  <tr>\n");
        html.append("    <td><b>Most Recently Passed:</b></td>\n");
        html.append("    <td>");
        if (inputEnabled) {
            html.append(lastPassTag.toString());
        } else {
            if ((testUrl != null) && (lastPass != null) && (lastPass.getStartTime() != null)) {
                html.append("<a href=\"" + testUrl.toString() + "?" + TEST_ID_LABEL + "=" + lastPass.getId() + "\">");
                html.append(fullDateFormat.format(lastPassTag.getDate()));
                html.append("</a>");
            } else {
                html.append("None");
            }
        }
        html.append("</td>\n");
        html.append("  </tr>\n");
        
        
        //Most Recently Failed:
        html.append("  <tr>\n");
        html.append("    <td><b>Most Recently Failed:</b></td>\n");
        html.append("    <td>");
        if (inputEnabled) {
            html.append(lastFailTag.toString());
        } else {
            if ((testUrl != null) && (lastFail != null) && (lastFail.getStartTime() != null)) {
                html.append("<a href=\"" + testUrl.toString() + "?" + TEST_ID_LABEL + "=" + lastFail.getId() + "\">");
                html.append(fullDateFormat.format(lastFailTag.getDate()));
                html.append("</a>");
            } else {
                html.append("None");
            }
        }
        html.append("</td>\n");
        html.append("  </tr>\n");
        
        html.append("</table>\n");

        return html.toString();
    }


    /**
     * This is a placeholder for adding additional test-specific content
     * to the test information.
     */
    protected String getExtendedInfo() {
        return null;
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
     * Create a table which defines a title, a link and borders for the contents.
     *
     * @param   title   Text to be placed in the title bar
     * @param   content Content of the table
     * @param   linkUrl	Link to be placed in the right side of the title bar
     * @param   linkName	Link to be placed in the right side of the title bar
     */
    public String getTitledBorderLink(String content, URL linkUrl, String linkName) {
        return getTitledBorderLink(DEFAULT_TITLE, content, linkUrl, linkName);
    }

}
