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

import java.util.Enumeration;
import java.util.Vector;

import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;

/**
 * This task removes all P4BlameListeners from the build. 
 *
 * @author Shawn Stafford
 */
public class P4BlameDelete extends Task {

    /** Reference ID of the listener */
    private String refid;


    /**
     * Set the reference ID for the listener object.
     * The reference ID allows the listener to be located later and deleted
     * or updated.
     *
     * @param id   Reference ID string
     */
    public void setRefId(String id) {
        refid = id;
    }

    /**
     * Return the reference ID for the listener object.
     *
     * @return Reference ID string
     */
    public String getRefId() {
        return refid;
    }


    /**
     * Delete all P4BlameListener objects from the project. 
     */
    public void execute() throws BuildException {
        if (refid != null) {
            // RefId support will only work once the code is compiled against Ant 1.7+ APIs
            //disableByRefId();
            throw new BuildException("Code currently does not support RefId attribute usage.");
        } else {
            disableAll();
        }
    }

    /**
     * Disable all blame listeners in the current project.
     */
	@SuppressWarnings("deprecation")
	private void disableAll() {
        int count = 0;

        // Obtain a list of listeners in the project
        @SuppressWarnings("unchecked")
		Vector<BuildListener> listeners = project.getBuildListeners();
        Object current = null;
        for (Enumeration<BuildListener> e = listeners.elements(); e.hasMoreElements(); ) {
            current = e.nextElement();
            if (current instanceof P4BlameListener) {
                P4BlameListener blame = (P4BlameListener) current;
                project.removeBuildListener(blame);
                blame.disable();
                count++;
            }
        }

        log("Disabled " + count + " P4BlameListeners in the current project.");
	}

}
