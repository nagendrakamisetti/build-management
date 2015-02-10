/*
 * Copyright 2000-2003 by Model N, Inc.  All Rights Reserved.
 *
 * This software is the confidential and proprietary information
 * of Model N, Inc ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you
 * entered into with Model N, Inc.
 */
package com.modeln.testfw.reporting;

import java.util.StringTokenizer;
import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Echo;

/**
 * The build event utility class provides helpful methods for managing and 
 * manipulating Ant BuildEvent objects. 
 *
 * @author Shawn Stafford
 */
public class BuildEventUtil {
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
    public static final String PROJECT_TYPE = "P";

    /** Indicates a target entry within the log execution stack */
    public static final String TARGET_TYPE = "T";

    /** Indicates a task entry within the log execution stack */
    public static final String TASK_TYPE = "C";


    /** Character used to delimit the execution stack entries in the log */
    public static final String EVENT_DELIMITER = "|";



    /**
     * Return the list of events as a string.  Each event in the list is
     * delimited by the EVENT_DELIMITER.  Event entries are indicated by
     * a name/value pair, where the format is "event type=event name".
     * The event type indicates the type of Ant container that generated
     * the build event (project, target, or task).
     *
     * @param  path   List of events in the path 
     * @return String representation of the path 
     */
    public static String getPathAsString(BuildEvent[] path) {
        StringBuffer pathStr = new StringBuffer();
        for (int idx = 0; idx < path.length; idx++) {
            if (path[idx].getTask() != null) {
                pathStr.append(TASK_TYPE + "=" + path[idx].getTask().getTaskName());
            } else if (path[idx].getTarget() != null) {
                pathStr.append(TARGET_TYPE + "=" + path[idx].getTarget().getName());
            } else if (path[idx].getProject() != null) {
                pathStr.append(PROJECT_TYPE + "=" + path[idx].getProject().getName());
            }

            // Append the delimiter if more elements remain
            if (idx < (path.length - 1)) {
                pathStr.append(EVENT_DELIMITER);
            }
        }
        return pathStr.toString();
    }

    /**
     * Parse the string and return a list of events.  The events are empty data
     * objects that are created from the information in the path.  They should 
     * not be treated as actual objects or used for any information other than
     * what is encoded in the path string.
     *
     * @param  path   String representation of the event path
     * @return List of events represented by the event path string
     */
    public static BuildEvent[] getPath(String path) {
        BuildEvent[] list = null;
        if ((path != null) && (path.length() > 0)) {
            int idx = 0;
            StringTokenizer st = new StringTokenizer(path, EVENT_DELIMITER); 
            list = new BuildEvent[st.countTokens()];
            while (st.hasMoreTokens()) {
                list[idx] = parseEvent(st.nextToken());
                idx++;
            }
        }

        return list;
    }


    /**
     * Parse the event string and return an object representation.
     *
     * @param  info   String representing a build event
     * @return Object representation of the build event
     */
    public static BuildEvent parseEvent(String info) {
        BuildEvent event = null;

        int eqIdx = info.indexOf("=");
        String eventType = null;
        String eventName = null;

        // Parse the type and name from the string 
        if (eqIdx > 0) {
            eventType = info.substring(0, eqIdx);
            eventName = info.substring(eqIdx + 1);
        } else if (eqIdx == 0) {
            // The type is unknown
            return null;
        } else {
            // There is no '=' delimiter so assume we're parsing the type
            if (PROJECT_TYPE.equals(info) || TARGET_TYPE.equals(info) || TASK_TYPE.equals(info)) {
                eventType = info;
                eventName = "Unknown";
            } else {
                // If we can't determine the task type then just give up
                return null;
            }
        }

        if (PROJECT_TYPE.equals(eventType)) {
            // Fudge and construct a target instead of a project (see reason below) 
            Target tgt = new Target();
            tgt.setName(eventName);
            event = new BuildEvent(tgt);

            // Constructing an Ant Project doesn't work because there is a lot of
            // excess baggage that goes along with it.  Specifically, once the
            // project is constructed it will fire event listeners when attempting
            // to set the project name.  This typically causes exceptions since the
            // project isn't truly complete and is unable to service the events.
            /**
            Project proj = new Project(); 
            Vector listeners = proj.getBuildListeners();
            for (int idx = 0; idx < 0; idx++) {
                proj.removeBuildListener((BuildListener) listeners.get(idx));
            }
            proj.setName(eventName);
            event = new BuildEvent(proj);
            */
        } else if (TARGET_TYPE.equals(eventType)) {
            Target tgt = new Target();
            tgt.setName(eventName);
            event = new BuildEvent(tgt);
        } else if (TASK_TYPE.equals(eventType)) {
            Task tsk = new Echo();
            tsk.setTaskName(eventName);
            event = new BuildEvent(tsk);
        }
        return event;
    }

    /**
     * Convert the event priority to a string.  If no match is found,
     * the MSG_NONE value is returned.
     *
     * @param   event  Build event 
     * @return  String representing the priority
     */
    public static String getPriorityAsString(BuildEvent event) {
        if (event != null) {
            int pri = event.getPriority();
            switch (pri) {
                case Project.MSG_DEBUG: return MSG_DEBUG;
                case Project.MSG_ERR:   return MSG_ERR;
                case Project.MSG_INFO:  return MSG_INFO;
                case Project.MSG_VERBOSE: return MSG_VERBOSE;
                case Project.MSG_WARN:  return MSG_WARN;
                default: return MSG_NONE;
            }
        } else {
            return MSG_NONE;
        }
    }

}

