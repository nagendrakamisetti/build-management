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
 * Converts the string case to all upper case characters. 
 *
 * @author Shawn Stafford
 */
public final class ToUpper extends Task {


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
     * Convert the string to upper case. 
     */
    public void execute() throws BuildException {
        String value = getProject().getProperty(property);
        if (value != null) {
            value = value.toUpperCase();
            getProject().setProperty(property, value);
        }
    }



}
