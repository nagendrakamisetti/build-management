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
import org.apache.tools.ant.BuildException;

import java.io.File;
import java.util.Vector;

/**
 * Parses the output of an Ant task and generates a report of any errors
 * or warnings.
 *
 * @author Shawn Stafford
 */
public class Report extends Task {


    /** Display the report in HTML format */
    public static final String HTML_FORMAT = "html";

    /** Display the report in text format */
    public static final String TEXT_FORMAT = "text";

    /** Display the report in XML format */
    public static final String XML_FORMAT = "xml";

    /** Create a list of all possible formats  */
    private static final String[] formats = { HTML_FORMAT, TEXT_FORMAT, XML_FORMAT };

    /** Output file that the report will be written to */
    private File outputFile = new File("Report.out");

    /** Report format to generate */
    private String reportFormat = TEXT_FORMAT;

    /** List of parse targets that must be processed when processing a build event */
    protected Vector<ReportParseTarget> parseTargets = new Vector<ReportParseTarget>();

    /** List of notifications that must be sent as a result of the build process */
    protected Vector<ReportNotification> notificationList = new Vector<ReportNotification>();


    /**
     * Set the format that will be used to display the report results.
     * Valid formats include HTML, Text, and XML.
     *
     * @param   format  Display format
     */
    public void setFormat(String format) throws BuildException {
        if (isFormatValid(format)) {
            format = format.toLowerCase();
            reportFormat = format;
        } else {
            throw new BuildException("Invalid report format: " + format, getLocation());
        }
    }

    /**
     * Returns the file format that the report should be presented in.
     *
     * @return  Type of format (text, html, xml)
     */
    public String getFormat() {
        return reportFormat;
    }

    /**
     * Set the output file for the report results.
     *
     * @param   file    Report file to be generated
     */
    public void setFile(File file) {
        outputFile = file;
    }

    /**
     * Return the output file for the report results.
     *
     * @return    Report file to be generated
     */
    public File getFile() {
        return outputFile;
    }

    /**
     * Determine if the specified report format is valid.
     *
     * @param   format  Display format
     */
    protected static boolean isFormatValid(String format) {
        if ((format != null) && (format.length() > 0)) {
            // Cycle through the list of valid formats
            for (int idx = 0; idx < formats.length; idx++) {
                if (format.equalsIgnoreCase(formats[idx])) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Process the nested parse elements to determine how the task
     * output parsing should be performed.
     */
    public void addConfiguredParse(ReportParseTarget parser) {
        System.out.println("Adding parse target: " + parser.getTargetName());
        parseTargets.add(parser);
    }

    /**
     * Return a list of log criteria to be parsed.
     */
    public Vector<ReportParseTarget> getParseTargets() {
        return parseTargets;
    }

    /**
     * Process the nested notification elements to determine how to send 
     * notifications to the interested parties or processes.
     */
    public void addConfiguredReportSummary(ReportNotification notification) {
        System.out.println("Adding notification target: " + notification.getType());
        notificationList.add(notification);
    }

    /**
     * Return a list of notification tasks.
     */
    public Vector<ReportNotification> getNotifications() {
        return notificationList;
    }

    /**
     * Perform the task parsing and generate the report.
     */
    public void execute() throws BuildException {
    }

}
