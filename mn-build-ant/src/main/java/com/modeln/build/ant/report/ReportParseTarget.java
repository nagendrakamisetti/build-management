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
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.types.FileSet;

import java.util.Date;
import java.util.Vector;

/**
 * Contains the information required to parse the output from Ant build
 * events.
 *
 * @author Shawn Stafford
 */
public final class ReportParseTarget extends MatchingTask {

    /** Target has not yet been executed */
    private static final String PENDING = "Pending";

    /** Target is currently running */
    private static final String RUNNING = "Running";

    /** Target execution is complete */
    private static final String COMPLETE = "Complete";


    /** Name of the ant task to be parsed */
    private String target;

    /** Name of the ant task to be excluded from the one set in "target" */
    private String excludeTarget;

    /** List of search criteria that will be used for the current target */
    private Vector criteria;

    /** Execution status of the target */
    private String status = PENDING;

    /** Date and time when the target began running */
    private Date startDate;

    /** Date and time when the target completed */
    private Date endDate;

    /** Name of the parent target */
    private String parent;

    /** File to be processed */
    private String filename;

    /** Name of the group that the target belongs to */
    private String groupName;

    /**
     * Set the name of the group that this parse target should be assigned to.
     * Grouping is used to collect like targets together when generating summary
     * information.
     *
     * @param  name   Group name
     */
    public void setGroup(String name) {
        groupName = name;
    }

    /** 
     * Return the name of the group that this parse target belongs to.
     * The group name can be used to categorize targets.
     *
     * @return Group name
     */
    public String getGroup() {
        return groupName;
    }

    /**
     * Set the target that must be parsed.
     *
     * @param   task    Name of the task to parse
     */
    public void setTarget(String task) {
        target = task;
        criteria = new Vector();
    }

    /**
     * Return the name of the target that must be parsed.
     *
     * @return  Name of the target
     */
    public String getTargetName() {
        return target;
    }

    /**
     * Set the excludeTarget that must be parsed.
     *
     * @param   task    Name of the task to parse
     */
    public void setExcludeTarget(String task) {
        excludeTarget = task;
    }

    /**
     * Return the name of the excludeTarget that must be parsed.
     *
     * @return  Name of the excludeTarget
     */
    public String getExcludeTarget() {
        return excludeTarget;
    }

    /**
     * Process the nested parse strings to determine what to look for when
     * parsing a build event.
     *
     * @param   entry   Information about the text being searched for
     */
    public void addConfiguredFind(ReportParseCriteria entry) {
        criteria.add(entry);
    }


    /**
     * Return a list of all parse criteria associated with the current target.
     *
     * @return  List of all ReportParseCriteria entries
     */
    public Vector getAllCriteria() {
        return criteria;
    }

    /**
     * Cycle through the event executionPath to see if any of the 
     * targets matches the current Target.
     * The excludeTarget is excluded "under" the current target.
     * Example: if target="migrate" excludeTarget="dbimport"
     * the dbimport messages are only ignored if it is nested under
     * the migrate target.
     *
     * @return  true if found any matching target 
     */
    public boolean matchesTarget(BuildEvent[] execPath) {
        boolean underTarget=false;
        boolean underExcludeTarget=false;

        for (int idx=0; idx<execPath.length; idx++) {
            if (execPath[idx].getTarget() != null) {
                if ( execPath[idx].getTarget().getName().equals( target )) {
                    if ((excludeTarget == null)||(excludeTarget.length()==0)) {
                        // Match is found when a task falls under a "target" without a "excludeTarget"
                        return true;
                    } else {
                        underTarget=true; 
                    }
                }
                if (underTarget) {
                    if ( execPath[idx].getTarget().getName().equals( excludeTarget )) { underExcludeTarget=true;}
                }
            }
        }

        // Match is found when a task falls under the user-defined "target" but not under the "excludeTarget"
        if ( underTarget && ( ! underExcludeTarget)) 
            return true;
        return false;
    }

    /**
     * Cycle through the list of search criteria to determine if the
     * event matches any of the criteria.  Matching criteria will be
     * returned as a list.  If no matches were found, the list will
     * be empty but non-null.
     *
     * @return  List of matching criteria
     */
    public Vector getMatchingCriteria(BuildEvent event) {
        Vector matchingCriteria = new Vector();

        ReportParseCriteria currentCriteria = null;
        for (int idx = 0; idx < criteria.size(); idx++) {
            currentCriteria = (ReportParseCriteria) criteria.get(idx);
            // If the message or exception match the parse criteria
            if (currentCriteria.matches(event)) {
                matchingCriteria.add(currentCriteria);
            }
        }

        return matchingCriteria;
    }

    /**
     * Return only the matching criteria which has the highest priority.
     * If no matches were found, null will be returned.
     *
     * @return  List of matching criteria
     */
    public ReportParseCriteria getHighestMatchingCriteria(BuildEvent event) {
        ReportParseCriteria highest = null;
        ReportParseCriteria current = null;

        Vector matchingCriteria = getMatchingCriteria(event);
        for (int idx = 0; idx < matchingCriteria.size(); idx++) {
            current = (ReportParseCriteria) matchingCriteria.get(idx);
            if ((highest != null) && (current != null)) {
                // Swap if the current one is greater
                if (highest.compare(highest, current) == 1) {
                    highest = current;
                }
            } else if (highest == null) {
                highest = current;
            }
        }

        return highest;
    }


    /**
     * Indicate that the target has started running.  This will set the 
     * target status to RUNNING and set the start date to the current time.
     * Start is only valid when the status of the target is PENDING.
     */
    public void start() {
        if (status.equals(PENDING)) {
            status = RUNNING;
            startDate = new Date();
        }
    }

    /**
     * Indicate that the target has completed running.  This will set the 
     * target status to COMPLETE and set the end date to the current time.
     * Stop is only valid when the status of the target is RUNNING.
     */
    public void stop() {
        if (status.equals(RUNNING)) {
            status = COMPLETE;
            endDate = new Date();
        }
    }

    /** 
     * Return the current execution status of the target.  The status should
     * be one of the defined status values (PENDING, RUNNING, or COMPLETED).
     *
     * @return  Status of the target
     */
    public String getStatus() {
        return status;
    }

    /**
     * Return the length of time that the target has been executing.  If the
     * target has a status of RUNNING, the time will be calculated to the
     * current time.
     *
     * @return  Number of milliseconds that the target took to execute
     */
    public long getLength() {
        long time = 0;

        if (status.equals(RUNNING)) {
            Date now = new Date();
            time = now.getTime() - startDate.getTime();
        } else if (status.equals(COMPLETE)) {
            time = endDate.getTime() - startDate.getTime();
        }

        return time;
    }

    /**
     * Set the file to be parsed.
     *
     * @param   file    Log file to be parsed
     */
    public void setLogfile(String file) {
        filename = file;
    }

    /**
     * Return a list of files that should be parsed.
     *
     * @return  List of files that correspond to matching files
     */
    public String[] getTargetFiles() {
        Vector files = new Vector();
        
        // Using a single filename because I couldn't get the fileset to work
        files.add(filename);

/**  Was unable to get the FileSet to work... Use this code when you figure it out

        // Iterate through the file sets to locate any target files
        FileSet currentSet = null;
        DirectoryScanner scanner = fileset.getDirectoryScanner(getProject());
        String[] list = scanner.getIncludedFiles();
        for (int idx = 0; idx < list.length; idx++) {
            files.add(list[idx]);
        }
*/

        // Create an array of files
        String[] filelist = new String[files.size()];
        files.copyInto(filelist);

        return filelist;
    }
}
