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
import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Obtains file information about a specific file and stores the 
 * information in Ant properties.
 *
 * @author Shawn Stafford
 */
public final class FileInfo extends Task {

    /** File to be examined */
    private File file = null;

    /** Determines whether the file information should be written to a log file. */
    private boolean enableLog = true;

    /** The property prefix that will store the file info properties.  */
    protected String property = "file";

    /** Date format used to display file modificaiton dates information */
    protected String dateFormat = null;


    /**
     * Set the property prefix that will store the file info properties.
     * If no property prefix is set, the default "file" prefix will be used.
     * The property prefix that will store the file info properties.
     * For example, if the property is set to "file1" then the ant task
     * will set the following properties:
     * 
     *    file1.size
     *    file1.lastmodified
     * 
     * @param   property    Property name to be set
     */
     public void setProperty(String property) {
         this.property = property;
     }


    /**
     * Set the file to be examined. 
     *
     * @param   file    File name 
     */
    public void setFile(File file) {
        this.file = file;
    }

    /**
     * This boolean determines whether the file information is 
     * written to stdout.
     *
     * @param   enable  If TRUE, Write file information to the log
     */
    public void setEcho(Boolean enable) {
        enableLog = enable.booleanValue();
    }

    /**
     * Set the date format used to display file modification information.
     * The date format uses SimpleDateFormat patterns.
     */
    public void setDateFormat(String format) {
        dateFormat = format;
    }

    /**
     * Obtain the file information and save it in property values.
     * The file size can be retrieved from the ${file.size} property and
     * the last modified date can be retrieved from the ${file.lastmodified} property.
     */
    public void execute() throws BuildException {
        // Verify that a file has been specified
        if (file == null) {
            throw new BuildException("file attribute is required",
                                     getLocation());
        }

        // Verify that the user has not set the property prefix to a blank value
        if ((property == null) || (property.length() == 0)) {
            throw new BuildException("property attribute cannot be null",
                                     getLocation());
        }


        // Obtain the file information
        long size = file.length();
        long date = file.lastModified();

        // Format the date if necessary
        String formattedDate = null;
        if (dateFormat != null) {
            try {
                SimpleDateFormat formatObj = new SimpleDateFormat(dateFormat);
                formattedDate = formatObj.format(new Date(date));
            } catch (IllegalArgumentException argex) {
                throw new BuildException("invalid date format pattern" + dateFormat, getLocation()); 
            }
        } else {
            formattedDate = Long.toString(date);
        }

        // Set the project properties
        getProject().setProperty(property + ".size",    "" + size);
        getProject().setProperty(property + ".lastmodified", "" + formattedDate);

        // Display the network information in the logs
        if (enableLog) {
            log(property + ".size = " + size);
            log(property + ".lastmodified = " + formattedDate);
        }
    }

}
