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

import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;

/**
 * Converts the string case to all lower case characters. 
 *
 * @author Shawn Stafford
 */
public final class ToLower extends Task {


    /** Name of the system property being returned. */
    private String property = null;


    /**
     * Set the name of the Ant property containing the string value. 
     *
     * @param   name    Name of the ant property to return 
     */
    public void setProperty(String name) {
        property = name;
    }


    /**
     * Convert the string to lower case. 
     */
    public void execute() throws BuildException {
        String value = getProject().getProperty(property);
        if (value != null) {
            value = value.toLowerCase();
            getProject().setProperty(property, value);
        }
    }



}
