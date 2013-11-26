/* 
* Copyright 2000-2003 by Model N, Inc.  All Rights Reserved. 
* 
* This software is the confidential and proprietary information 
* of Model N, Inc ("Confidential Information").  You shall not 
* disclose such Confidential Information and shall use it only 
* in accordance with the terms of the license agreement you 
* entered into with Model N, Inc. 
*/
package com.modeln.testfw.reporting;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;


/**
 * Data object used to represent a single ACT test in the database.  This 
 * object provides the object/relational mapping.
 * 
 * @author  Shawn Stafford
 */
public class CMnDbAcceptanceTestData extends CMnDbTestData {


    /** Name of the ACT script containing the test */
    private String scriptName;

    /** Description of the ACT test */
    private String summary;

    /** Name of the script author */
    private String author;

    /** List of stories associated with the test */
    private Vector stories = new Vector();

    /** List of test cases associated with the test */
    private Vector testcases = new Vector();


    /**
     * Returns a string representing the display name for the test.
     *
     * @return String representing the name of the test
     */
    public String getDisplayName() {
        if (scriptName != null) {
            return scriptName;
        } else {
            return Integer.toString(getId());
        }
    }


    /**
     * Set the name of the test script.
     * 
     * @param   name    Name of the script file
     */
    public void setScriptName(String name) {
        scriptName = name;
    }

    /**
     * Return the name of the test script.
     * 
     * @return  Script name
     */
    public String getScriptName() {
        return scriptName;
    }


    /**
     * Set the test summary. 
     *
     * @param   text    Test summary 
     */
    public void setSummary(String text) {
        summary = text;
    }

    /**
     * Return the test summary. 
     *
     * @return  Test summary 
     */
    public String getSummary() {
        return summary;
    }


    /**
     * Set the name of the test author.
     *
     * @param   name    Script author 
     */
    public void setAuthor(String name) {
        author = name;
    }

    /**
     * Return the name of the test author.
     *
     * @return  Script author
     */
    public String getAuthor() {
        return author;
    }


    /**
     * Set the list of agile stories associated with the test. 
     *
     * @param  list  List of story names
     */
    public void setStories(List list) {
        stories = new Vector(list);
    }

    /**
     * Add a story to the list of agile stories associated with this test. 
     *
     * @param  name     Story name
     */
    public void addStory(String name) {
        stories.add(name);
    }

    /**
     * Returns the list of agile stories associated with this test. 
     *
     * @return List of story names
     */
    public Enumeration getStories() {
        return stories.elements();
    }


    /**
     * Set the list of test cases associated with this test. 
     *
     * @param  list  Test cases 
     */
    public void setTestCases(List list) {
        testcases = new Vector(list);
    }

    /**
     * Add a test case to the list of QA test cases associated with this test.
     *
     * @param  name     Test case name
     */
    public void addTestCase(String name) {
        testcases.add(name);
    }

    /**
     * Returns the list of QA test cases associated with this test.
     *
     * @return List of test cases 
     */
    public Enumeration getTestCases() {
        return testcases.elements();
    }


}
