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

import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.Project;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

/**
 * The event records the build event and its corresponding parse
 * criteria.
 *
 * @author Shawn Stafford
 */
public class ReportParseEvent {

    /** Format of the date string prepended to each log line */
    protected static String DATE_FORMAT = "MM/dd/yy HH:mm:ss";


    /** Date formatter used to print and parse the log timestamps */
    private static DateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT);


    /** Unique key value used to identify an event */
    private int eventId;

    /** Set the build event which is associated with the parse event */
    private BuildEvent buildEvent;

    /** List of parse criteria that match the current event */
    private Vector matches;

    /** References the parse target that generated the event */
    private ReportParseTarget owner;

    /** Elements of the execution path (Projects, Targets, and Tasks) */
    private BuildEvent[] executionPath;

    /** Record the approximate date when the event was encountered */
    private Date parseDate = new Date();

    /**
     * Construct an empty parse event.
     */
    public ReportParseEvent() {
        matches = new Vector();
    }

    /**
     * Construct a parse event from an existing event.
     */
    public ReportParseEvent(BuildEvent event) {
        buildEvent = event;
        matches = new Vector();
    }

    /**
     * Construct a parse event from an existing event and set the matching
     * search criteria.
     */
    public ReportParseEvent(BuildEvent event, Vector criteria) {
        buildEvent = event;
        matches = criteria;
    }

    /**
     * Set the unique key value used to identify a single event.
     * This value is typically an auto-incremented value created by the database when
     * a new entry is inserted into the table.
     *
     * @param    id     Key value
     */
    public void setId(int id) {
        eventId = id;
    }

    /**
     * Return the unique key used to identify the event. 
     *
     * @return  Key value
     */
    public int getId() {
        return eventId;
    }


    /**
     * Set the date when the event was encountered.  The date is set to the 
     * date that the event was constructed by default.
     *
     * @param  date   Date when the event was parsed
     */
    public void setParseDate(Date date) {
        parseDate = date;
    }

    /**
     * Return the date when the event was encountered.  By default, the date 
     * is set to the date when the event object was constructed.
     *
     * @return  Date when the event was parsed
     */
    public Date getParseDate() {
        return parseDate;
    }

    /**
     * Return the date when the event was encountered.  By default, the date
     * is set to the date when the event object was constructed.
     *
     * @return  Parse date as a formatted string
     */
    public String getParseDateAsString() {
        return dateFormatter.format(parseDate);
    }

    /**
     * Add the matching parse criteria to the current event.
     *
     * @param   criteria    Matching parse criteria
     */
    public void addCriteria(ReportParseCriteria criteria) {
        matches.add(criteria);
    }

    /**
     * Replace an existing criteria object with a new criteria object.
     *
     * @param  oldCriteria  Existing criteria object
     * @param  newCriteria  Replacement criteria object
     * @return TRUE if the object was found and replaced, false otherwise
     */
    public boolean replaceCriteria(ReportParseCriteria oldCriteria, ReportParseCriteria newCriteria) {
        boolean success = false; 
        int idx = matches.indexOf(oldCriteria);
        if (idx >= 0) {
            matches.setElementAt(newCriteria, idx);
            success = true;
        }
        return success;
    }

    /**
     * Set the build event that contained matching criteria.
     *
     * @param event Build event
     */
    public void setBuildEvent(BuildEvent event) {
        buildEvent = event;
    }

    /**
     * Return the build event that contained matching criteria.
     *
     * @return  Build event matching the criteria
     */
    public BuildEvent getBuildEvent() {
        return buildEvent;
    }

    /**
     * Return the list of matching parse criteria.
     *
     * @return  Vector containing the matching parse criteria
     */
    public Vector getCriteria() {
        return matches;
    }

    /**
     * Return a count of the total number of criteria matches found so far.
     *
     * @return Number of criteria matches
     */
    public int getCriteriaCount() {
        int count = 0;
        if (matches != null) {
            count = matches.size();
        }
        return count;
    }

    /**
     * Select the criteria with the highest priority level.
     *
     * @return  Criteria with the highest priority
     */
    public ReportParseCriteria getHighestCriteria() {
        ReportParseCriteria highest = null;
        ReportParseCriteria current = null;
        for (int idx = 0; idx < matches.size(); idx++) {
            current = (ReportParseCriteria) matches.get(idx);

            // Make sure we have an initial high value
            if (highest == null) {
                highest = current;
            }

            // Select the current criteria if higher than existing
            if (current.compare(current, highest) == 1) {
                highest = current;
            }
        }

        return highest;
    }

    /**
     * Set the parse target that generated the event.
     *
     * @param   target      Target which generated the event.
     */
    public void setOwner(ReportParseTarget target) {
        owner = target;
    }

    /**
     * Return the parse target that generated the event.
     *
     * @return  Target which generated the event.
     */
    public ReportParseTarget getOwner() {
        return owner;
    }

    /**
     * Set the execution path that generated the event.
     *
     * @param   path      Sequence of events that triggered the event
     */
    public void setExecutionPath(BuildEvent[] path) {
        executionPath = path;
    }

    /**
     * Return the execution path that generated the event.
     *
     * @return  Sequence of events that triggered the event
     */
    public BuildEvent[] getExecutionPath() {
        return executionPath;
    }

    /**
     * Return the name of each element in the execution path.
     */
    public String[] getExecutionPathNames() {
        String[] names = new String[executionPath.length];
        for (int idx = 0; idx < executionPath.length; idx++) {
            if (executionPath[idx].getTask() != null) {
                names[idx] = executionPath[idx].getTask().getTaskName();
            } else if (executionPath[idx].getTarget() != null) {
                names[idx] = executionPath[idx].getTarget().getName();
            } else if (executionPath[idx].getProject() != null) {
                names[idx] = executionPath[idx].getProject().getName();
            }
        }

        return names;
    }

}
