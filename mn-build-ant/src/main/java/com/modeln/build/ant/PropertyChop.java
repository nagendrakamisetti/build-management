/*
 * Copyright 2000-2003 by Model N, Inc.  All Rights Reserved.
 * 
 * This software is the confidential and proprietary information
 * of Model N, Inc ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you
 * entered into with Model N, Inc.
 */
package com.modeln.build.ant;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildEvent;



/**
 * Removes the specified string from the property value. 
 *
 * @author Shawn Stafford
 */
public final class PropertyChop extends Task {


    /** Name of the system property being returned. */
    private String property = null;

    /** Substring to be removed from the property */
    private String substring = null;

    /**
     * Set the name of the Ant property containing the string value. 
     *
     * @param   name    Name of the ant property to return 
     */
    public void setProperty(String name) {
        property = name;
    }

    /**
     * Set the substring value to be removed from the property string.
     *
     * @param   value    String value to be removed  
     */
    public void setSubstring(String value) {
        substring = value;
    }



    /**
     * Remove the specified portion of the property if it exists. 
     */
    public void execute() throws BuildException {
        String value = getProject().getProperty(property);
        if ((value != null) && (substring != null) && (substring.length() > 0)) {
            int startIdx = value.indexOf(substring);
            if (startIdx != -1) {
                int endIdx = startIdx + substring.length();
                String pre = value.substring(0, startIdx);
                String post = value.substring(endIdx);
                value = pre + post;
            }
        }
        getProject().setProperty(property, value);
    }



}
