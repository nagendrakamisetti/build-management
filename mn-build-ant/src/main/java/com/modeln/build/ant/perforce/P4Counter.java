/*
 * Copyright 2000-2003 by Model N, Inc.  All Rights Reserved.
 * 
 * This software is the confidential and proprietary information
 * of Model N, Inc ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you
 * entered into with Model N, Inc.
 */
package com.modeln.build.ant.perforce;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildListener;

import com.modeln.build.perforce.Common;
import com.modeln.build.perforce.Counter;

/**
 * Operates on a Perforce counter.
 *
 * @author Shawn Stafford
 */
public final class P4Counter extends Task {

    /**
     * name of the counter
     */
    public String counter = null;

    /**
     * name of an optional property
     */
    public String property = null;

    /**
     * flag telling whether the value of the counter should be set
     */
    public boolean shouldSetValue = false;

    /**
     * flag telling whether a property should be set
     */
    public boolean shouldSetProperty = false;

    /**
     * new value for the counter
     */
    public int value = 0;

    /**
     * The name of the counter; required
     * @param counter name of the counter
     */
    public void setName(String counter) {
        this.counter = counter;
    }

    /**
     * The new value for the counter; optional.
     * @param value new value for the counter
     */
    public void setValue(int value) {
        this.value = value;
        shouldSetValue = true;
    }

    /**
     * A property to be set with the value of the counter
     * @param property the name of a property to set with the value
     * of the counter
     */
    public void setProperty(String property) {
        this.property = property;
        shouldSetProperty = true;
    }

    /**
     * again, properties are mutable in this tsk
     * @throws BuildException if the required parameters are not supplied.
     */
    public void execute() throws BuildException {
        // Make sure a counter name was provided
        if ((counter == null) || counter.length() == 0) {
            throw new BuildException("No counter specified to retrieve.");
        }

        // Make sure the user is performing an appropriate action
        if (shouldSetValue && shouldSetProperty) {
            throw new BuildException(
                "Cannot both set the value of the property and retrieve the " + 
                "value of the property.");
        }

        // Determine what action should be performed
        if (shouldSetProperty) {
            Counter result = Counter.getCounter(counter);
            if (result != null) {
                int num = result.getValue();
                getProject().setProperty(property,  "" + num);
            }
        } else if (shouldSetValue) {
            boolean success = Counter.setCounter(counter, value);
        } else {
            Counter result = Counter.getCounter(counter);
            if (result != null) {
                System.out.println(result.getValue());
            }
        }
    }

}
