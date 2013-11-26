/*
 * ScheduledTaskManager.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.scheduler;

import java.util.Date;


/**
 * The ScheduledTaskManager is responsible for maintaining the list of 
 * scheduled on-time tasks.  
 * 
 * @version            $Revision: 1.1 $  
 * @author             Shawn Stafford
 */
public class ScheduledTaskManager extends TaskManager {


    /**
     * Construct the task manager
     *
     * @param   name    Name of the task manager
     */
    public ScheduledTaskManager(String name) {
        super(name);
    }

    /** 
     * Add the task to the list of scheduled tasks.
     *
     * @param   task    Task to be added
     *
     * @throws  IllegalStateException if a task with the same name already exists
     */
    public void add(Task task) throws IllegalStateException {
    }

    /**
     * Get the next task that is scheduled to execute, but do not remove it or
     * mark it as having already been executed.  If no tasks are available to 
     * be scheduled, a null will be returned.  This method operates on
     * recurring tasks by creating a ScheduledTask for the next occurrence
     * of a recurring task.
     *
     * @return  Next scheduled task
     */
    public ScheduledTask peekNext() {
        return null;
    }

    /**
     * Get the next task that is scheduled to execute and remove it from
     * the list of scheduled tasks.  If no tasks are available to be scheduled,
     * a null will be returned.  
     *
     * @return  Next scheduled task
     */
    public ScheduledTask removeNext() {
        return null;
    }

    /** 
     * Determine whether the manager has any tasks that need to be evaluated
     * for scheduling.
     *
     * @return  TRUE if tasks are available for scheduling
     */
    public boolean hasTasks() {
        return false;
    }

    /**
     * Return the amount of time in milliseconds until the next task is
     * scheduled for execution.  This method may return a negative value
     * if the task has already passed the scheduled execution date.  If 
     * no tasks are available for scheduling, a zero will be returned.
     *
     * @return  Number of milliseconds until the next task
     */
    public long getTimeToNext() {
        return 0;
    }

}
