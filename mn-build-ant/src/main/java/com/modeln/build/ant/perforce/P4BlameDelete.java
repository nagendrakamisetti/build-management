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

import org.apache.tools.ant.Task;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.email.EmailAddress;
import org.apache.tools.ant.types.Reference;

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
    private void disableAll() {
        int count = 0;

        // Obtain a list of listeners in the project
        Vector listeners = project.getBuildListeners();
        Object current = null;
        for (Enumeration e = listeners.elements(); e.hasMoreElements(); ) {
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

    /**
     * Disable the listener which matches the specified refid.
     */
    private void disableByRefId() {
        // Obtain a list of listeners in the project
        Vector listeners = project.getBuildListeners();
        Object current = null;
        for (Enumeration e = listeners.elements(); e.hasMoreElements(); ) {
            current = e.nextElement();
            if (current instanceof P4BlameListener) {
                P4BlameListener blame = (P4BlameListener) current;
                project.removeBuildListener(blame);
                //The getRefid() method only appears to be available in Ant 1.7+
                //Reference ref = blame.getRefid();
                Reference ref = null;
                if ((ref != null) && (refid.equals(ref.getRefId()))) {
                    log("Disabling P4BlameListener by refid: " + refid);
                    blame.disable();
                } else if (ref == null) {
                    log("Unable to determine refid of P4BlameListener.  Listener will remain active.");
                }
            }
        }
    }

}
