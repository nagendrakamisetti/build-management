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

import java.io.File;
import java.io.FileOutputStream;
import java.util.Stack;
import java.util.Vector;

import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;


/**
 * Parses the output of Ant events and generates a report of any errors
 * or warnings.
 *
 * @author Shawn Stafford
 */
public class ReportListener implements BuildListener {

    /** List of report entries recieved so far */
    private Vector<ReportParseTarget> targets;

    /** List of parse events that should be considered in the report */
    private Vector<ReportParseEvent> matches;

    /** Maintain a reference to the report which constructed this listner */
    protected Report parent;

    /** Generates the report */
    protected ReportComposer composer;

    /** Target execution stack */
    private Stack<BuildEvent> executionStack = new Stack<BuildEvent>();

    /**
     * Construct the listener and watch for the specified warning and error
     * strings.
     *
     * @param   list    List of build targets to scan for text
     */
    public ReportListener(Report report, Vector<ReportParseTarget> list) {
        parent = report;
        targets = list;
        matches = new Vector<ReportParseEvent>();
        composer = new ReportComposer(targets, matches, null);
    }

    /**
     * Signals that a build has started. This event
     * is fired before any targets have started.
     * 
     * @param event An event with any relevant extra information.
     *              Must not be <code>null</code>.
     */
    public void buildStarted(BuildEvent event) {
        push(event);
    }

    /**
     * Signals that the last target has finished. This event
     * will still be fired if an error occurred during the build.
     * 
     * @param event An event with any relevant extra information.
     *              Must not be <code>null</code>.
     *
     * @see BuildEvent#getException()
     */
    public void buildFinished(BuildEvent event) {
        pop();

        showMatches(event);

        // Determine if a build notification should be sent
        Vector<ReportNotification> notifications = parent.getNotifications();
        if (notifications != null) {
            ReportNotification current = null;
            event.getProject().log("Build complete.  Processing report notifications...");
            for (int idx = 0; idx < notifications.size(); idx++) {
                current = (ReportNotification) notifications.get(idx);
                current.sendNotification(event);
            }
        } else {
            event.getProject().log("No notifications found.", Project.MSG_WARN);
        }
    }

    /**
     * Signals that a target is starting.
     * 
     * @param event An event with any relevant extra information.
     *              Must not be <code>null</code>.
     *
     * @see BuildEvent#getTarget()
     */
    public void targetStarted(BuildEvent event) {
        push(event);


        // Cycle through the list of targets
        ReportParseTarget currentTarget = null;
        String targetName = event.getTarget().getName();
        for (int idx = 0; idx < targets.size(); idx++) {
            currentTarget = (ReportParseTarget) targets.get(idx);
            if (targetName.equalsIgnoreCase(currentTarget.getTargetName())) {
                currentTarget.start();
            }
        }

        showMatches(event);
    }

    /**
     * Signals that a target has finished. This event will
     * still be fired if an error occurred during the build.
     * 
     * @param event An event with any relevant extra information.
     *              Must not be <code>null</code>.
     *
     * @see BuildEvent#getException()
     */
    public void targetFinished(BuildEvent event) {
        pop();

        Target target = event.getTarget();
        String targetName = target.getName();

        // Cycle through the list of targets 
        ReportParseTarget currentTarget = null;
        for (int idx = 0; idx < targets.size(); idx++) {
            currentTarget = (ReportParseTarget) targets.get(idx);
            if (targetName.equalsIgnoreCase(currentTarget.getTargetName())) {
                currentTarget.stop();
            }
        }

        showMatches(event);
    }

    /**
     * Signals that a task is starting.
     * 
     * @param event An event with any relevant extra information.
     *              Must not be <code>null</code>.
     *
     * @see BuildEvent#getTask()
     */
    public void taskStarted(BuildEvent event) {
        push(event);
    }

    /**
     * Signals that a task has finished. This event will still
     * be fired if an error occurred during the build.
     *
     * @param event An event with any relevant extra information.
     *              Must not be <code>null</code>.
     *
     * @see BuildEvent#getException()
     */
    public void taskFinished(BuildEvent event) {
        pop();
    }

    /**
     * Signals a message logging event.
     * 
     * @param event An event with any relevant extra information.
     *              Must not be <code>null</code>.
     *
     * @see BuildEvent#getMessage()
     * @see BuildEvent#getPriority()
     */
    public void messageLogged(BuildEvent event) {
        ReportParseTarget currentTarget = null;
        Vector<ReportParseCriteria> localMatches = null;

        // Cycle through the list of targets to search for matching criteria
        for (int idx = 0; idx < targets.size(); idx++) {
            currentTarget = (ReportParseTarget) targets.get(idx);
            localMatches = currentTarget.getMatchingCriteria(event);

            // If matching criteria were found, create a report entry
            if (localMatches.size() > 0) {
                ReportParseEvent parseEvent = new ReportParseEvent(event, localMatches);
                parseEvent.setOwner(currentTarget);
                parseEvent.setExecutionPath(getEventStack());
                matches.add(parseEvent);
            }

        }

    }

    /**
     * Return the execution stack as an array of BuildEvent objects.
     *
     * @return List of build events
     */
    private BuildEvent[] getEventStack() {
        // Convert the generic array to a more specific one
        Object[] objStack = executionStack.toArray();
        BuildEvent[] eventStack = new BuildEvent[objStack.length];
        for (int idx = 0; idx < objStack.length; idx++) {
            eventStack[idx] = (BuildEvent) objStack[idx];
        }
        return eventStack;
    }


    /**
     * Creates a new (empty) report file and returns a reference to the file.
     * If the file cannot be created, a null will be returned.
     */
    private File createReportFile() {
        try {
            // Create the directory and file if it does not exist
            File reportFile = parent.getFile();
            File parentDir = reportFile.getParentFile();
            if (parentDir != null) {
                parentDir.mkdirs();
            }
            reportFile.createNewFile();
            return reportFile;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Display the list of matches.
     *
     * @param event An event with any relevant extra information.
     *              Must not be <code>null</code>.
     */
    public void showMatches(BuildEvent event) {
        Project proj = event.getProject();

        File reportFile = createReportFile();
        try {
            composer.setOutputStream(new FileOutputStream(reportFile));
            composer.setTitle(proj.getName());
            composer.setDescription(proj.getDescription());
            composer.write(parent.getFormat());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
    }


    /**
     * Push a build event onto the execution stack.
     */
    private void push(BuildEvent event) {
        executionStack.push(event);
    }

    /**
     * Pop a build event event off the execution stack and return the event.
     * If the result is null, then an invalid execution stack was encountered.
     */
    private BuildEvent pop() {
        // It's possible that this class was created within a deeply nested target
        // Make sure we don't try to pop the stack beyond where we started recording
        if (executionStack.size() > 0) {
            return (BuildEvent) executionStack.pop();
        } else {
            return null;
        }
    }


}
