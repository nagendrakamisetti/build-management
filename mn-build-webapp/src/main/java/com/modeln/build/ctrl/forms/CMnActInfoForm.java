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

import com.modeln.testfw.reporting.CMnDbAcceptanceTestData;
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
public class CMnActInfoForm extends CMnTestInfoForm {

    /** Author name */
    private TextTag authorTag = new TextTag("author");

    /** Test cases */
    private OptionTag testcaseTag = new OptionTag("testcase");

    /** Agile stories */
    private OptionTag storyTag = new OptionTag("story");

    /**
     * Construct a build form.
     *
     * @param  form   URL to use when submitting form input
     * @param  images URL to use when referencing images
     */
    public CMnActInfoForm(URL form, URL images) {
        super(form, images);

        authorTag.setWidth(40); 
        testcaseTag.setDisabled(true);
        storyTag.setDisabled(true);
    }


    /**
     * Set the Test that should be rendered in the form.
     *
     * @param  testData  Test data object
     */
    public void setValues(CMnDbTestData testData, CMnDbTestData lastPass, CMnDbTestData lastFail) {
        super.setValues(testData, lastPass, lastFail);
        if ((testData != null) && (testData instanceof CMnDbAcceptanceTestData)) {
            authorTag.setValue(((CMnDbAcceptanceTestData)testData).getAuthor());
            testcaseTag.setOptions(((CMnDbAcceptanceTestData)testData).getTestCases());
            storyTag.setOptions(((CMnDbAcceptanceTestData)testData).getStories());
        }
    }


    /**
     * This is a placeholder for adding additional test-specific content
     * to the test information.
     */
    protected String getExtendedInfo() {
        StringBuffer html = new StringBuffer();

        html.append("  <tr>\n");
        html.append("    <td width=\"30%\"><b>Test Author:</b></td>\n");
        html.append("    <td width=\"70%\">");
        html.append(authorTag.getValue());
        html.append("</td>\n");
        html.append("  </tr>\n");

        html.append("  <tr>\n");
        html.append("    <td width=\"30%\"><b>Test Cases:</b></td>\n");
        html.append("    <td width=\"70%\">");
        html.append(testcaseTag.toString());
        html.append("</td>\n");
        html.append("  </tr>\n");

        html.append("  <tr>\n");
        html.append("    <td width=\"30%\"><b>Agile Stories:</b></td>\n");
        html.append("    <td width=\"70%\">");
        html.append(storyTag.toString());
        html.append("</td>\n");
        html.append("  </tr>\n");

        return html.toString();
    }


}

