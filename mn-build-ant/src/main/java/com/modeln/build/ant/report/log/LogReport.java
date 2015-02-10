/*
 * Copyright 2000-2003 by Model N, Inc.  All Rights Reserved.
 * 
 * This software is the confidential and proprietary information
 * of Model N, Inc ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you
 * entered into with Model N, Inc.
 */
package com.modeln.build.ant.report.log;

import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildException;

import com.modeln.build.ant.report.Report;
import com.modeln.build.ant.report.ReportListener;
import com.modeln.build.ant.report.ReportParseTarget;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Parses an Ant log file 
 *
 * @author Shawn Stafford
 */
public final class LogReport extends Report {

    /** The report listener will be used to process the log events */
    private ReportListener listener;

    /** List of log files to be parsed and the parse targets that will be used */
    private Hashtable<String, Vector<ReportParseTarget>> logfiles = new Hashtable<String, Vector<ReportParseTarget>>();

    /**
     * Perform the task parsing and generate the report.
     */
    public void execute() throws BuildException {
        // Create a new build listener for parsing events
        listener = new ReportListener(this, parseTargets);

        // Iterate through each parse target to obtain a list of all possible files
        ReportParseTarget currentTarget = null;
        Enumeration<ReportParseTarget> targets = parseTargets.elements();
        while (targets.hasMoreElements()) {
            currentTarget = (ReportParseTarget) targets.nextElement();
            String[] filelist = currentTarget.getTargetFiles();
            for (int idx = 0; idx < filelist.length; idx++) {
                addLogFile(filelist[idx], currentTarget);
            }
        }

        // Parse the log files
        Enumeration<String> files = logfiles.keys();
        String currentFile = null;
        while (files.hasMoreElements()) {
            currentFile = (String) files.nextElement();
            parseFile(currentFile);
        }

        // Write the log file and perform notifications
        listener.buildFinished(new BuildEvent(this));
    }


    /**
     * Add the log file and its associated parse target to the list of
     * log files.  This should help optimize log parsing by ensuring that
     * each log file is only processed once.
     *
     * @param   file        Name of the log file
     * @param   target      Target that should be used to parse the file
     */
    private void addLogFile(String file, ReportParseTarget target) {
        Vector<ReportParseTarget> targetList = null;

        // Determine if the file already exists in the list
        if (logfiles.contains(file)) {
            // Add the target to an existing file entry
            targetList = (Vector<ReportParseTarget>) logfiles.get(file);
            targetList.add(target);
        } else {
            // Create a new file and an associated target list
        	targetList = new Vector<ReportParseTarget>();
            targetList.add(target);
            logfiles.put(file, targetList);
        }

    }


    /**
     * Process the log file by comparing each line of text with the parse
     * targets that are associated with the file.
     *
     * @param   filename    Log file to parse
     */
    public void parseFile(String filename) {
        // List of parse targets associated with the file
        String currentLine = null;

        // Open the file
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            LogEntry currentEntry = null;
            BuildEvent currentEvent = null;

            log("Opening file for report parsing: " + filename);
            while ((currentLine = reader.readLine()) != null) {
                // Simulate a logging event by parsing the log line and constructing one
                currentEntry = LineLogger.parseLine(currentLine, false);
                if (currentEntry != null) {
                    currentEvent = currentEntry.createBuildEvent();
                }
                if (currentEvent != null) {
                    listener.messageLogged(currentEvent);
                }

            }
        } catch (Exception ex) {
            log("Unable to parse file: " + ex);
        }
    }


}
