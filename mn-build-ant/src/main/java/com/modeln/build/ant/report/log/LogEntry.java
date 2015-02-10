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

import java.util.Date;
import java.util.LinkedList;

import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;

/**
 * Contains information obtained from parsing a log file entry. 
 *
 * @author Shawn Stafford
 */
public class LogEntry {

    /** Date when the log event was generated */
    private Date timestamp;

    /** Record the execution stack for the build */
    private LinkedList<Object> executionStack = new LinkedList<Object>();

    /** Message associated with the log entry */
    private String message;

    /** Priority of the log event (See Project) */
    private int priority;

    /**
     * Construct a log entry from the given text.
     */
    public LogEntry() {
    }


    /**
     * Set the event date and time.
     *
     * @param   date    Timestamp
     */
    public void setDate(Date date) {
        timestamp = date;
    }

    /**
     * Construct a build event by attempting to reconstruct the event 
     * information from the execution stack.  The most specific event
     * will be constructed by traversing the stack in reverse order.
     * If a build event cannot be constructed, a null will be returned.
     *
     * @return  Build event containing information gleaned from the log
     */
    public BuildEvent createBuildEvent() {
        BuildEvent event = null;

        if (executionStack.size() > 0) {
            Task task = getTask();
            Target target = getTarget();
            Project project = getProject();

            if (task != null) {
                task.setOwningTarget(target);
                event = new BuildEvent(task);
            } 
            
            if (target != null) {
                target.setProject(project);
                if (event == null) {
                    event = new BuildEvent(target);
                }
            }

            if (project != null) {
                if (event == null) {
                    event = new BuildEvent(project);
                }
            }

            // Set the event information
            if (event != null) {
                event.setMessage(message, priority);
            }

        }

        return event;
    }

    /**
     * Return the date and time when the event was logged.
     *
     * @return  Date when the event was logged
     */
    public Date getDate() {
        return timestamp;
    }

    /** 
     * Add a target to the sequence of events that led up to the log message.
     */
    public void add(Target target) {
        executionStack.add(target);
    }

    /** 
     * Add a task to the sequence of events that led up to the log message.
     */
    public void add(Task task) {
        executionStack.add(task);
    }

    /** 
     * Add a project to the sequence of events that led up to the log message.
     */
    public void add(Project project) {
        executionStack.add(project);
    }

    /** 
     * Return the most recent project from which the message originated.  This
     * traverses the exection stack from the most recent entry and returns the
     * first project that is found on the stack.
     */
    public Project getProject() {
        Object current = null;

        // Traverse the stack in reverse order
        for (int idx = executionStack.size() - 1; idx >= 0; idx--) {
            current = executionStack.get(idx);
            if (current instanceof Project) {
                return (Project) current;
            }
        }

        return null;
    }

    /** 
     * Return the most recent target from which the message originated.  This
     * traverses the exection stack from the most recent entry and returns the
     * first target that is found on the stack.
     */
    public Target getTarget() {
        Object current = null;

        // Traverse the stack in reverse order
        for (int idx = executionStack.size() - 1; idx >= 0; idx--) {
            current = executionStack.get(idx);
            if (current instanceof Target) {
                return (Target) current;
            }
        }

        return null;
    }

    /** 
     * Return the most recent task from which the message originated.  This
     * traverses the exection stack from the most recent entry and returns the
     * first task that is found on the stack.
     */
    public Task getTask() {
        Object current = null;

        // Traverse the stack in reverse order
        for (int idx = executionStack.size() - 1; idx >= 0; idx--) {
            current = executionStack.get(idx);
            if (current instanceof Task) {
                return (Task) current;
            }
        }

        return null;
    }


    /**
     * Set the message for the current log event.
     *
     * @param   msg     Log message
     */
    public void setMessage(String msg) {
        message = msg;
    }

    /**
     * Return the message for the current log event.
     *
     * @return     Log message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Set the priority for the current log event.
     *
     * @param   pri     Log message priority
     */
    public void setPriority(int pri) {
        priority = pri;
    }

    /**
     * Return the priority for the current log event.
     *
     * @return     Priority
     */
    public int getPriority() {
        return priority;
    }
    
    /**
     * Determine whether the log entry was a child of the specified target
     * by examining the execution stack to find out if it contains a match.
     *
     * @param   targetName      Target to be matched
     * @return  TRUE if the target was found in the stack, FALSE otherwise
     */
    public boolean loggedBy(String targetName) {
        // Cycle through the execution stack to examine all targets
        Object current = null;
        for (int idx = 0; idx < executionStack.size(); idx++) {
            current = executionStack.get(idx);
            if (current instanceof Target) {
                // Compare the names and return if they match
                String currentName = ((Target)current).getName();
                if (targetName.equalsIgnoreCase(currentName)) {
                    return true;
                }
            }
        }

        return false;
    }


}
