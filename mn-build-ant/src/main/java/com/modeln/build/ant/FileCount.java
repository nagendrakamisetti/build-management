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

import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.types.FileSet;

import java.util.Vector;

/**
 * Counts the total number of files in a file set.
 *
 * @author Shawn Stafford
 */
public final class FileCount extends Task {

    /** The fileset is used to specify the list of files to count */
    protected Vector filesets = new Vector();

    /** The project property that will store the file count value */
    protected String property;

    /**
     * Set the property that the count will be placed in.
     *
     * @param   property    Property name to be set
     */
     public void setProperty(String property) {
         this.property = property;
     }


    /**
     * Adds a set of files to count.
     */
    public void addFileset(FileSet set) {
        filesets.addElement(set);
    }

    /**
     * Counts the total number of files in the fileset that match the given pattern.
     */
    public void execute() throws BuildException {
        if (property == null) {
            throw new BuildException("property attribute is required", 
                                     getLocation());
        }

        // Count the total number of files in all file sets
        int count = 0;
        for (int idx = 0; idx < filesets.size(); idx++) {
            FileSet current = (FileSet) filesets.get(idx);
            DirectoryScanner scanner = current.getDirectoryScanner(getProject());
            String[] files = scanner.getIncludedFiles();
            count = count + files.length;
        }

        // Set the property with the file count
        String oldvalue = getProject().getProperty(property);
        if (oldvalue != null) {
            throw new BuildException(
                "Property already exists and must not be set again: " + property, 
                getLocation());
        } else {
            getProject().setProperty(property, Integer.toString(count));
        }

    }
}
