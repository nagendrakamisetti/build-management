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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.StringTokenizer;

import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.Target;


/**
 */
public class LineLogger extends DefaultLogger {

    /** Identifies a DEBUG log message */
    public static final String MSG_DEBUG = "DEBUG";

    /** Identifies a ERROR log message */
    public static final String MSG_ERR = "ERROR";

    /** Identifies a INFO log message */
    public static final String MSG_INFO = "INFO";

    /** Identifies a VERBOSE log message */
    public static final String MSG_VERBOSE = "VERBOSE";

    /** Identifies a WARN log message */
    public static final String MSG_WARN = "WARN";

    /** Identifies a NONE log message */
    public static final String MSG_NONE = "NONE";


    /** Indicates a project entry within the log execution stack */
    public static final String PROJECT = "P";

    /** Indicates a target entry within the log execution stack */
    public static final String TARGET = "T";

    /** Indicates a task entry within the log execution stack */
    public static final String TASK = "C";


    /** Character used to delimit the execution stack entries in the log */
    public static final String STACK_DELIMITER = "|";


    /** Format of the date string prepended to each log line */
    protected static String DATE_FORMAT = "MM/dd/yy HH:mm:ss";


    /** Record the execution stack for the build */
    private LinkedList<BuildEvent> executionStack = new LinkedList<BuildEvent>();

    /** Display the execution stack as part of each message */
    private boolean showStack = false;


    /**
     * Sole constructor.
     */
    public LineLogger() {
    }

    /**
     * Sets this logger to display the execution stack as part of the line.
     * The execution stack is formatted as a comma delimited list of 
     * task, target, and project names that indicate the execution path
     * that led to the current log statement.
     *
     * @param status    <code>true</code> if the execution stack should be displayed
     */
    public void setStackDisplay(boolean status) {
        this.showStack = status;
    }


    /**
     * Logs a message, if the priority is suitable.
     * In non-emacs mode, task level messages are prefixed by the
     * task name which is right-justified.
     * 
     * @param event A BuildEvent containing message information.
     *              Must not be <code>null</code>.
     */
    public synchronized void messageLogged(BuildEvent event) {
        int priority = event.getPriority();
        // Filter out messages based on priority
        if (priority <= msgOutputLevel) {

            StringBuffer message = new StringBuffer();
            if (event.getTask() != null && !emacsMode) {
                SimpleDateFormat datefmt = new SimpleDateFormat(DATE_FORMAT);
                message.append(datefmt.format(new Date()) + " ");
                message.append(getPriorityAsString(priority) + " ");
                if (showStack) {
                    message.append("[" + formatEventStack() + "] ");
                }
            }
            message.append(event.getMessage());

            String msg = message.toString();
            if (priority != Project.MSG_ERR) {
                printMessage(msg, out, priority);
            } else {
                printMessage(msg, err, priority);
            }
            log(msg);
        }
    }

    /**
     * Convert the event priority to a string.  If no match is found,
     * the MSG_NONE value is returned.
     *
     * @param   pri     Event priority (see Project)
     * @return  String representing the priority
     */
    public static String getPriorityAsString(int pri) {
        switch (pri) {
            case Project.MSG_DEBUG: return MSG_DEBUG;
            case Project.MSG_ERR:   return MSG_ERR;
            case Project.MSG_INFO:  return MSG_INFO;
            case Project.MSG_VERBOSE: return MSG_VERBOSE;
            case Project.MSG_WARN:  return MSG_WARN;
            default: return MSG_NONE;
        }
    }


    /**
     * Convert the event priority to an integer.  If no match is found,
     * a -1 is returned.
     *
     * @param   pri     Event priority 
     * @return  Integer representing the priority  (see Project)
     */
    public static int getPriorityAsInt(String pri) {
        if (pri.equals(MSG_DEBUG)) {
            return Project.MSG_DEBUG;
        } else if (pri.equals(MSG_ERR)) {
            return Project.MSG_ERR;
        } else if (pri.equals(MSG_INFO)) {
            return Project.MSG_INFO;
        } else if (pri.equals(MSG_VERBOSE)) {
            return Project.MSG_VERBOSE;
        } else if (pri.equals(MSG_WARN)) {
            return Project.MSG_WARN;
        } else {
            return -1;
        }
    }


    /**
     * Signals that a build has started. This event
     * is fired before any targets have started.
     * 
     * @param event An event with any relevant extra information.
     *              Must not be <code>null</code>.
     */
    public void buildStarted(BuildEvent event) {
        super.buildStarted(event);
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
        super.buildFinished(event);
        pop();
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
        super.targetStarted(event);
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
        super.targetFinished(event);
        pop();
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
        super.taskStarted(event);
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
        super.taskFinished(event);
        pop();
    }


    /**
     * Push a build event onto the execution stack.
     */
    private synchronized void push(BuildEvent event) {
        executionStack.add(event);
    }

    /**
     * Pop a build event event off the execution stack and return the event.
     * If the result is null, then an invalid execution stack was encountered.
     */
    private synchronized BuildEvent pop() {
        if (executionStack.size() > 0) {
            return (BuildEvent) executionStack.removeLast();
        } else {
            String msg = "The event stack is empty.  Unable to return the current event.";
            printMessage(msg, err, Project.MSG_ERR);
            return null;
        }
    }

    /**
     * Format the execution stack as a text string.
     */
    private String formatEventStack() {
        StringBuffer stackTrace = new StringBuffer();
        BuildEvent current = null;

        ListIterator<BuildEvent> iterator = executionStack.listIterator(0);
        while (iterator.hasNext()) {
            current = (BuildEvent) iterator.next();
            if (current.getTask() != null) {
                stackTrace.append(TASK + "=" + current.getTask().getTaskName());
            } else if (current.getTarget() != null) {
                stackTrace.append(TARGET + "=" + current.getTarget().getName());
            } else if (current.getProject() != null) {
                stackTrace.append(PROJECT + "=" + current.getProject().getName());
            }

            // Use a delimiter between events
            if ((stackTrace.length() > 0) && (iterator.hasNext())) {
                stackTrace.append(STACK_DELIMITER);
            }
        }


        return stackTrace.toString();
    }


    /**
     * Parse a line from a log file.  If the line is invalid, a null will be 
     * returned.
     *
     * @param   line    Text to be parsed
     * @param   hasStack  True if the line contains execution stack information
     * @return  Information about the log entry line
     */
    public static LogEntry parseLine(String line, boolean hasStack) {
        LogEntry entry = new LogEntry();
        StringBuffer text = new StringBuffer(line);

        try {
            // Obtain the most recent date
            String timestamp = text.substring(0, DATE_FORMAT.length() + 1);
            SimpleDateFormat datefmt = new SimpleDateFormat(DATE_FORMAT);
            entry.setDate(datefmt.parse(timestamp.trim()));
            text = text.delete(0, DATE_FORMAT.length() + 1);

            // Obtain the message priority
            int spcIdx = text.toString().indexOf(" ");
            String priority = text.substring(0, spcIdx).trim();
            entry.setPriority(getPriorityAsInt(priority));
            text = text.delete(0, spcIdx);

            // Obtain the execution stack 
            if (hasStack) {
                int stackBegin = text.toString().indexOf("[");
                int stackEnd   = text.toString().indexOf("]");
                String stack = text.substring(stackBegin + 1, stackEnd).trim();
                String stackEntry = null;
                StringTokenizer stackTokenizer = new StringTokenizer(stack, STACK_DELIMITER);
                while (stackTokenizer.hasMoreTokens()) {
                    stackEntry = stackTokenizer.nextToken();
                    int eqIdx = stackEntry.indexOf("=");
                    String name = stackEntry.substring(eqIdx + 1);
                    if (stackEntry.startsWith(PROJECT)) {
                        Project prj = new Project();
                        prj.setName(name);
                        entry.add(prj);
                    } else if (stackEntry.startsWith(TARGET)) {
                        Target tgt = new Target();
                        tgt.setName(name);
                        entry.add(tgt);
                    } else if (stackEntry.startsWith(TASK)) {
                        Task tsk = new DummyTask();
                        tsk.setTaskName(name);
                        entry.add(tsk);
                    }
                }
                text = text.delete(0, stackEnd + 1);
            }

            // Assume that the remaining text is the message
            entry.setMessage(text.toString().trim());

        } catch (Exception ex) {
//System.out.println("Error parsing line: " + ex);
            entry = null;
        }

        return entry;
    }


}
