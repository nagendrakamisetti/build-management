/*
 * TaskManager.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.scheduler;

import java.util.Date;


/**
 * The TaskManager is responsible for maintaining the list of scheduled
 * tasks.  
 * 
 * @version            $Revision: 1.1 $  
 * @author             Shawn Stafford
 */
public abstract class TaskManager extends Thread {

    /** 
     * Controls the runnable state of the task manager.
     * When the task manager is running, it continually examines the list of
     * tasks and executes the next available task.
     */
    private boolean runnable = true;

    /** List of threads managed by this class */
    private ThreadGroup threads;

    /** Date and time when the task list was last evaluated */
    private Date lastEval;

    /** 
     * Amount of time that the thread sleeps before evaluating the list of
     * scheduled tasks again.
     */
    private static final long evalInterval = 1000;

    /**
     * Maximum number of threads that are allowed to run simultaneously.
     */
    private int maxThreads = 10;


    /**
     * Construct the task manager
     *
     * @param   name    Name of the task manager
     */
    public TaskManager(String name) {
        threads = new ThreadGroup(name);
    }

    /**
     * Select the highest priority task from the task list and run it.
     */
    public void run() {
        // Length of time the current thread sleeps betwee runs
        long sleepInterval;

        // Continually loop until task is stopped
        while(runnable) {
            sleepInterval = evalInterval;

            // Execute the next available task
            if (hasTasks() && (threads.activeCount() <= maxThreads)) {
                lastEval = new Date();
                ScheduledTask current = removeNext();
                if (current != null) {
                    current.run();
                }

                // sleep until the next scheduled event
                long snooze = getTimeToNext();
                if (snooze > 0) {
                    sleepInterval = snooze;
                }
            }

            // Put the thread to sleep until another task is expected
            try {
                sleep(sleepInterval);
            } catch (InterruptedException ex) {
                // Not really sure what the implications of this are
            }

        } // end loop 
    }

    /** 
     * Add the task to the list of scheduled tasks.
     *
     * @param   task    Task to be added
     *
     * @throws  IllegalStateException if a task with the same name already exists
     */
    public abstract void add(Task task) throws IllegalStateException;

    /**
     * Get the next task that is scheduled to execute, but do not remove it or
     * mark it as having already been executed.  If no tasks are available to 
     * be scheduled, a null will be returned.
     *
     * @return  Next scheduled task
     */
    public abstract ScheduledTask peekNext();

    /**
     * Get the next task that is scheduled to execute and remove it from
     * the list of scheduled tasks.  If no tasks are available to be scheduled,
     * a null will be returned.
     *
     * @return  Next scheduled task
     */
    public abstract ScheduledTask removeNext();

    /** 
     * Determine whether the manager has any tasks that need to be evaluated
     * for scheduling.
     *
     * @return  TRUE if tasks are available for scheduling
     */
    public abstract boolean hasTasks();

    /**
     * Return the amount of time in milliseconds until the next task is
     * scheduled for execution.  This method may return a negative value
     * if the task has already passed the scheduled execution date.  If 
     * no tasks are available for scheduling, a zero will be returned.
     *
     * @return  Number of milliseconds until the next task
     */
    public abstract long getTimeToNext();

    /**
     * Return the weighted priority by examining the priority of the task and
     * the estimated length of time required to complete the task.  Weighted
     * priority values range from 0.0 to 1.0.  Weighted priorities close to
     * 1.0 are considered the highest priority tasks and will be executed 
     * before tasks with weighted values closer to 0.0.
     * <p>
     * The weighted priority is calculated by examining all tasks that are
     * actively being managed and ranking them relative to each other.
     *
     * @return  Weighted priority value
     */
    public float getWeightedPriority(ScheduledTask task) {
        return (float)0.0;
    }

}
