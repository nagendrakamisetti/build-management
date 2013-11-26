/*
 * Copyright 2000-2003 by Model N, Inc.  All Rights Reserved.
 * 
 * This software is the confidential and proprietary information
 * of Model N, Inc ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you
 * entered into with Model N, Inc.
 */
package com.modeln.build.ant.math;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildEvent;

/**
 * Increments the value of a property.  The property must be a valid 
 * integer value.
 *
 * @author Shawn Stafford
 */
public final class Increment extends Task {

    /** The property to be incremented. */
    protected String property;

    /**
     * Set the property to be incremented.
     *
     * @param   property    Property name to be set
     */
     public void setProperty(String property) {
         this.property = property;
     }

    /**
     * Counts the total number of files in the fileset that match the given pattern.
     */
    public void execute() throws BuildException {
        if (property == null) {
            throw new BuildException("property attribute is required", 
                                     getLocation());
        }

        // Increment the property value
        Integer oldvalue = new Integer(getProject().getProperty(property));
        int newvalue = oldvalue.intValue() + 1;
        getProject().setProperty(property, Integer.toString(newvalue));

    }
}
