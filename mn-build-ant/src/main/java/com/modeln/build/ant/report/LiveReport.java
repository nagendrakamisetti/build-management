/*
 * Copyright 2000-2003 by Model N, Inc.  All Rights Reserved.
 * 
 * This software is the confidential and proprietary information
 * of Model N, Inc ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you
 * entered into with Model N, Inc.
 */
package com.modeln.build.ant.report;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildException;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;

/**
 * Parses the Ant logging events and generates a report to summarize the
 * event contents.  A LiveReport registers an event listener within Ant
 * and begins parsing the incoming logging events.  The event listener
 * is responsible for categorizing and summarizing the events as they
 * are parsed.
 *
 * @author Shawn Stafford
 */
public final class LiveReport extends Report {

    /** Format used for the build timestamp */
    public static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");


    /** Date of the last successful build */
    private Date lastBuildDate;

    /**
     * Perform the task parsing and generate the report.  This method 
     * performs the event listener registration.
     */
    public void execute() throws BuildException {
        // Create a new build listener for parsing events
        ReportListener listener = new LiveReportListener(this, getParseTargets());

        // Add the event listeners to the project
        project.addBuildListener(listener);

    }


    /**
     * Set the timestamp of the last successful build.  The format of the
     * timestamp is specified by the TIMESTAMP_FORMAT.  If the timestamp is
     * invalid, the date will default to the current date.
     *
     * @param   timestamp   Date and time 
     */
    public void setLastBuild(String timestamp) throws BuildException {
        try {
            lastBuildDate = TIMESTAMP_FORMAT.parse(timestamp.trim());
        } catch (Exception ex) {
            System.out.println("Invalid timestamp: " + timestamp);
            System.out.println("Setting last build to the current date.");
            lastBuildDate = new Date();
        }
    }

    /**
     * Returns the timestamp of the last successful build.
     *
     * @return  Date of the last successful build
     */
    public Date getLastBuild() {
        return lastBuildDate;
    }


}
