/*
 * Copyright 2000-2003 by Model N, Inc.  All Rights Reserved.
 * 
 * This software is the confidential and proprietary information
 * of Model N, Inc ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you
 * entered into with Model N, Inc.
 */
package com.modeln.build.ant.report.db;

import com.modeln.build.ant.report.ReportParseCriteria;
import com.modeln.build.ant.report.ReportParseEvent;
import com.modeln.build.ant.report.ReportParseTarget;
import com.modeln.testfw.reporting.CMnReportTable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Vector;

import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.Project;

/**
 * Parses the output of Ant events, categorizes the events based upon a 
 * defined parse criteria, and stores the results in a database.
 *
 * @author Shawn Stafford
 */
public class DbReportListener implements BuildListener {

    /** List of report entries received so far */
    private Vector<ReportParseTarget> targets;

    /** List of parse events that have not yet been committed to the database */
    private Vector<ReportParseEvent> pendingEvents = new Vector<ReportParseEvent>();

    /** The pending events will be flushed to the database when the maximum limit is reached */
    private int maxEventCount = 20;


    /** The root node of the task execution tree */
    private BuildEventNode root = null;

    /** The current node being executed in the task execution tree */
    private BuildEventNode currentNode = null;

    /** Connection to the build database */
    private Connection dbConnection;

    /** Lowest level of message to write out */
    protected int msgOutputLevel = Project.MSG_ERR;

    /** Version of the build being recorded */
    private String buildVersion;

    /** Log only the messages which match a parsing criteria */
    private boolean logCriteriaOnly = false;

    /** Cause the build to fail if a message of a specified level is found */
    private boolean failOnCriteriaMatch = false;

    /** Lowest criteria level at which the build will fail if a match is found */
    private String criteriaFailureLevel = ReportParseCriteria.ERROR_LEVEL;

    /** Database query object */
    private CMnReportTable dbTable;


    /**
     * Construct a log listener that stores log events in the database.
     *
     * @param   conn    Connection to the build database
     * @param   version Build version string
     * @param   list    List of build targets to scan for text
     * @param   table   Database query object
     */
    public DbReportListener(Connection conn, String version, Vector<ReportParseTarget> list, CMnReportTable table) {
        dbConnection = conn;
        dbTable = table;
        targets = list;
        buildVersion = version;

        // Add each event criteria entry to the database
        ReportParseTarget currentTarget = null;
        for (int idx = 0; idx < targets.size(); idx++) {
            currentTarget = (ReportParseTarget) targets.get(idx);
            try {
                dbTable.addCriteria(dbConnection, buildVersion, currentTarget);
            } catch (SQLException sqe) {
                System.err.println("Unable to add parse target to the database: " + currentTarget.getTargetName());
            }
        }
    }

    /**
     * Disables the fail-on-match criteria checking used to halt a build when 
     * a match is found in the log output.
     */
    public void disableFailOnCriteriaMatch() {
        failOnCriteriaMatch = false;
    }

    /**
     * Enables the fail-on-match criteria checking used to hald a build when
     * matching criteria text is found in the log output.
     *
     * @param   
     */
    public void enableFailOnCriteriaMatch(String level) {
        if (ReportParseCriteria.isValidType(level)) {
            failOnCriteriaMatch = true;
            criteriaFailureLevel = level;
        } else {
            throw new BuildException("Invalid criteria type set when enabling failOnCriteriaMatch: " + level);
        }
    }

    /**
     * Sets the highest level of message this logger should respond to.
     *
     * Only messages with a message level lower than or equal to the
     * given level should be written to the database.
     * <P>
     * Constants for the message levels are in the
     * {@link Project Project} class. The order of the levels, from least
     * to most verbose, is <code>MSG_ERR</code>, <code>MSG_WARN</code>,
     * <code>MSG_INFO</code>, <code>MSG_VERBOSE</code>,
     * <code>MSG_DEBUG</code>.
     * <P>
     * The default message level is Project.MSG_ERR.
     *
     * @param level the logging level for the logger.
     */
    public void setMessageOutputLevel(int level) {
        this.msgOutputLevel = level;
    }

    /**
     * Enable logging only for events which match the parsing criteria.
     *
     * @param  enabled   Enable logging only for criteria events
     */
    public void setCriteriaOnly(boolean enabled) {
        logCriteriaOnly = enabled;
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
        pop(event);
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
        pop(event);
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
        pop(event);
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
        int priority = event.getPriority();
        // Filter out messages based on priority
        if (priority <= msgOutputLevel) {
            ReportParseEvent parseEvent = new ReportParseEvent(event, new Vector<ReportParseCriteria>());
            if (currentNode != null) {
                parseEvent.setExecutionPath(getPathToRoot(currentNode));
            }
            // parseEvent.setOwner(currentTarget);

            // Set execution path for this message (event)
            BuildEvent[] execPath = parseEvent.getExecutionPath();

            // Cycle through the list of targets to search for matching criteria
            Vector<ReportParseCriteria> localMatches = null;
            ReportParseTarget currentTarget = null;
            for (int targetIdx = 0; targetIdx < targets.size(); targetIdx++) {
                currentTarget = (ReportParseTarget) targets.get(targetIdx);

                // Search event execution path, only proceed when the path matches a user-defined Target
                if ( ! currentTarget.matchesTarget( execPath )) continue;

                // If matching criteria were found, add the matching criteria to the current event 
                localMatches = currentTarget.getMatchingCriteria(event);
                ReportParseCriteria currentCriteria = null;
                for (int idx = 0; idx < localMatches.size(); idx++) {
                    currentCriteria = (ReportParseCriteria) localMatches.get(idx); 
                    parseEvent.addCriteria(currentCriteria);
                    if ((failOnCriteriaMatch) && (currentCriteria.isAsSevereAs(criteriaFailureLevel))) {
                        throw new BuildException("Forcing failure on criteria match: " + currentCriteria.getText());
                    }
                }
            }

            // Queue the event for database submission 
            if (logCriteriaOnly) {
                // Determine if the event has a criteria which qualifies for logging
                if (parseEvent.getCriteriaCount() > 0) {
                    pendingEvents.add(parseEvent);
                }
            } else {
                // Log every event regardless of whether it matches the criteria
                pendingEvents.add(parseEvent);
            }
        }
        // Flush pending events if the event queue gets too large
        // This is common for forked processes that have a lot of output
        // Otherwise the events will only be flushed at the beginning and
        // end of tasks and targets.
        if (pendingEvents.size() > maxEventCount) {
            commitEvents();
        }

    }

    /**
     * Return the list of build events from the given node to its root.
     *
     * @return List of build events
     */
    private BuildEvent[] getPathToRoot(BuildEventNode node) {
        BuildEventNode[] nodePath = node.getPathToRoot();
        BuildEvent[] eventPath = null;
        if (nodePath != null) {
            eventPath = new BuildEvent[nodePath.length];
            for (int idx = 0; idx < nodePath.length; idx++) {
                eventPath[idx] = nodePath[idx].getEvent();
            }
        }
        return eventPath;
    }


    /**
     * Set the given event as a new node in the tree.
     */
    private void push(BuildEvent event) {
        commitEvents();

        // Make sure the code is thread safe by synchronizing on the currentNode
        // The entire method is not synchronized because the commitEvents call
        // contains some database connection calls which do not require synchronization
        // and would impact performance if synchronized.

        // Make sure the root node is initialized
        if (root == null) {
            // Fill in the project node to construct the root
            // To determine if we need to fill in the stack, we must look 
            // ahead in the project/target/task relationship
            if ((event.getProject() != null) && (event.getTarget() != null)) {
                BuildEvent projectEvent = new BuildEvent(event.getProject());
                projectEvent.setMessage(
                    "Constructing a dummy project event to fill in the missing execution stack.",
                    Project.MSG_DEBUG);
                root = new BuildEventNode(projectEvent, null);
                currentNode = root;

                // Fill in the target node if one has been executed
                synchronized (currentNode) {
                    if ((event.getTarget() != null) && (event.getTask() != null)) {
                        BuildEvent targetEvent = new BuildEvent(event.getTarget());
                        targetEvent.setMessage(
                            "Constructing a dummy target event to fill in the missing execution stack.",
                            Project.MSG_DEBUG);
                        BuildEventNode targetNode = new BuildEventNode(targetEvent, currentNode);
                        root.addChild(targetNode);
                        currentNode = targetNode;
                    }
                }
            }
        }

        // Make sure the current node is not null before synchronizing on the object
        if (currentNode == null) {
            currentNode = root;
        }

        // Construct a new event node and add it to the tree
        synchronized (currentNode) {
            BuildEventNode newNode = new BuildEventNode(event, currentNode);
            currentNode.addChild(newNode);
            currentNode = newNode;

        }

    }

    /**
     * Pop a build event event off the execution stack and return the event.
     * If the result is null, then an invalid execution stack was encountered.
     *
     * @return  The event node that was previously marked as current
     */
    private BuildEventNode pop(BuildEvent event) {
        BuildEventNode oldNode = null;

        // Make sure the code is thread safe by synchronizing on the currentNode
        // The entire method is not synchronized because the commitEvents call
        // contains some database connection calls which do not require synchronization
        // and would impact performance if synchronized.
        if (currentNode != null) {
            // It's possible that this class was created within a deeply nested target
            // Make sure we don't try to pop the stack beyond where we started recording
            synchronized (currentNode) {
                oldNode = currentNode;
                currentNode = currentNode.getParent();
            }
        }

        commitEvents();
        return oldNode;
    }

    /**
     * Commit any pending events to the database.  Events should only be committed
     * during the push/pop operations in order to reduce the frequency of database
     * transactions.
     */
    private void commitEvents() {
        // Obtain a list of events to be committed to the database
        // Syncronize on the pendingEvent list to ensure that a parallel thread
        // does not consume the same event or clear out the list before the current
        // item can be obtained.
        Enumeration<ReportParseEvent> events = null;
        synchronized (pendingEvents) {
            @SuppressWarnings("unchecked")
			Vector<ReportParseEvent> eventList = (Vector<ReportParseEvent>) pendingEvents.clone();
            events = eventList.elements();
            pendingEvents.clear();
        }

        // We don't care too much about new events coming in that we miss, but
        // we do want to ensure that we don't loop over more elements than the
        // list actually contains
        ReportParseEvent currentEvent = null;
        while ((events != null) && (events.hasMoreElements())) {
            currentEvent = (ReportParseEvent) events.nextElement();
            if ((dbConnection != null) && (currentEvent != null)) {
                try {
                    // Commit the current event to the database
                    dbTable.addEvent(dbConnection, buildVersion, currentEvent);
                } catch (SQLException sqe) {
                    System.err.println("Unable to add build event to the database.");
                    sqe.printStackTrace();
                }
            }

        }

    }

}
