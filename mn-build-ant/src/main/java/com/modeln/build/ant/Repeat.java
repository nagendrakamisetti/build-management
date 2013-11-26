/*
 * Copyright 2000-2003 by Model N, Inc.  All Rights Reserved.
 * 
 * This software is the confidential and proprietary information
 * of Model N, Inc ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you
 * entered into with Model N, Inc.
 */
package com.modeln.build.ant;

import java.util.Hashtable;
import java.util.Vector;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.TaskContainer;
import org.apache.tools.ant.taskdefs.ConditionTask;
import org.apache.tools.ant.taskdefs.condition.Condition;
import org.apache.tools.ant.taskdefs.condition.ConditionBase;
import org.apache.tools.ant.types.EnumeratedAttribute;


/**
 * Executes a set of tasks in a loop until a condition is met.
 */
public class Repeat extends Task implements TaskContainer {

    // Condition which will terminate the loop early
    private RepeatCondition condition = null;

    // storage for nested tasks
    private Vector tasks = new Vector();

    /** default max wait time */
    private long maxWaitMillis = 1000L * 60L * 3L;
    private long maxWaitMultiplier = 1L;
    private long checkEveryMillis = 500L;
    private long checkEveryMultiplier = 1L;
    private String timeoutProperty;

    // should the build fail if any subtasks fail? Default is true.
    private boolean failOnError = true;

    /**
     * Determines whether the build should fail if any of the tasks fail. 
     *
     * @param fail  if true, fail the build if any tasks fail 
     */
    public void setFailonerror(boolean fail) {
        failOnError = fail;
    }

    /**
     * Add a test condition.
     */
    public void addCondition(RepeatCondition c) {
        if (condition != null) {
            throw new BuildException("Only one condition allowed in the <repeat> task.");
        } else {
            condition = c;
        }
    }

    /**
     * Add a task to repeat.
     *
     * @param task                A task to execute
     * @exception BuildException  won't happen
     */
    public void addTask(Task task) throws BuildException {
        tasks.addElement(task);
    }


    /**
     * Set the maximum length of time to wait
     */
    public void setMaxWait(long time) {
        maxWaitMillis = time;
    }

    /**
     * Set the max wait time unit
     */
    public void setMaxWaitUnit(Unit unit) {
        maxWaitMultiplier = unit.getMultiplier();
    }

    /**
     * Set the time between each check
     */
    public void setCheckEvery(long time) {
        checkEveryMillis = time;
    }

    /**
     * Set the check every time unit
     */
    public void setCheckEveryUnit(Unit unit) {
        checkEveryMultiplier = unit.getMultiplier();
    }

    /**
     * Name the property to set after a timeout.
     */
    public void setTimeoutProperty(String p) {
        timeoutProperty = p;
    }

    /**
     * Check repeatedly for the specified conditions until they become
     * true or the timeout expires.
     */
    public void execute() throws BuildException {
        long savedMaxWaitMillis = maxWaitMillis;
        long savedCheckEveryMillis = checkEveryMillis;
        try {
            maxWaitMillis *= maxWaitMultiplier;
            checkEveryMillis *= checkEveryMultiplier;
            long start = System.currentTimeMillis();
            long end = start + maxWaitMillis;

            while (System.currentTimeMillis() < end) {
                if (condition != null) {
                    boolean result = condition.eval();
                    if (result) {
                        return;
                    }
                }
                performTasks();
                try {
                    Thread.sleep(checkEveryMillis);
                } catch (InterruptedException e) {
                    // ignore
                }
            }

            if (timeoutProperty != null) {
                getProject().setNewProperty(timeoutProperty, "true");
            }
        } finally {
            maxWaitMillis = savedMaxWaitMillis;
            checkEveryMillis = savedCheckEveryMillis;
        }
    }

    /**
     * The enumeration of units:
     * millisecond, second, minute, hour, day, week
     * @todo we use timestamps in many places, why not factor this out
     */
    public static class Unit extends EnumeratedAttribute {

        private static final String MILLISECOND = "millisecond";
        private static final String SECOND = "second";
        private static final String MINUTE = "minute";
        private static final String HOUR = "hour";
        private static final String DAY = "day";
        private static final String WEEK = "week";

        private static final String[] units = {
            MILLISECOND, SECOND, MINUTE, HOUR, DAY, WEEK
        };

        private Hashtable timeTable = new Hashtable();

        public Unit() {
            timeTable.put(MILLISECOND, new Long(1L));
            timeTable.put(SECOND,      new Long(1000L));
            timeTable.put(MINUTE,      new Long(1000L * 60L));
            timeTable.put(HOUR,        new Long(1000L * 60L * 60L));
            timeTable.put(DAY,         new Long(1000L * 60L * 60L * 24L));
            timeTable.put(WEEK,        new Long(1000L * 60L * 60L * 24L * 7L));
        }

        public long getMultiplier() {
            String key = getValue().toLowerCase();
            Long l = (Long) timeTable.get(key);
            return l.longValue();
        }

        public String[] getValues() {
            return units;
        }
    }


    /**
     * Iterate through the list of tasks to execute each task. 
     */
    private void performTasks() throws BuildException {
        try {
            // executing nested tasks
            for (int i = 0; i < tasks.size(); i++) {
                Task currentTask = (Task) tasks.get(i);
                try {
                    currentTask.perform();
                } catch (Exception ex) {
                    if (failOnError) {
                        throw ex;
                    }
                }
            }
        } catch (Exception e) {
            if (failOnError) {
                throw new BuildException(e.getMessage());
            } else {
                log(e.getMessage());
            }
        }
    }
}


