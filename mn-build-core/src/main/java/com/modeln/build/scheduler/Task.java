/*
 * Task.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.scheduler;


/**
 * A Task is an event that is scheduled to occur at some time in the future.
 * Tasks can be one-time events or occur at regular intervals.  A task has
 * a priority
 * 
 * @version            $Revision: 1.1 $  
 * @author             Shawn Stafford
 */
public class Task extends Thread {

    /** 
     * Schedule the task before all other non-critical tasks.   A critical
     * task should be expected to begin executing at the specified time, 
     * preempting other tasks of lower priority if necessary.
     */
    public static final int CRITICAL_PRIORITY = 0;

    /** 
     * Schedule the task with high priority unless a critical task is scheduled. 
     * This task may preempt other tasks only if they are a low priority and 
     * will not finish before the current task would be scheduled to finish.
     */
    public static final int HIGH_PRIORITY = 25;

    /** 
     * Schedule the task as normal.  This task should be expected to finish
     * in the same order it was scheduled, provided there are not high
     * priority tasks that preempt it.
     */
    public static final int NORMAL_PRIORITY = 50;

    /**
     * Schedule the task to execute in a reasonable amount of time.  Although
     * this task is scheduled to start at a given time, it is not guaranteed
     * to start at that time.  Once started, the task will execute to completion
     * unless preempted by a critical task.
     */
    public static final int LOW_PRIORITY = 75;

    /** 
     * Schedule the task to execute only when no other tasks are scheduled.
     * Tasks at this priority will not begin executing until all other tasks
     * are complete or to avoid starvation.
     */
    public static final int BACKGROUND_PRIORITY = 100;


    /** Priority of the task in relation to other tasks */
    private int taskPriority = NORMAL_PRIORITY;



    /**
     * Construct the task 
     *
     * @param   name    Name of the task 
     */
    public Task(String name) {
        super(name);
    }

    /**
     * Construct the task as part of the specified thread group.
     *
     * @param   name    Name of the task 
     * @param   group   Thread group to which the task belongs
     */
    public Task(String name, ThreadGroup group) {
        super(group, name);
    }


    /**
     * Set the priority of the task.  This value can be any value greater than
     * or equal to zero.  Priorities closer to zero are considered the highest
     * priority while priorities near 100 are considered background tasks that
     * will only execute as resources become available.
     *
     * @param   pri     Task priority
     */
    public void setTaskPriority(int pri) {
        taskPriority = pri;
    }

    /**
     * Returns the priority of the task.  Zero represents the highest priority,
     * meaning that tasks closest to zero will execute with a higher priority
     * than tasks with greater priority values.  Priority values greater than
     * 100 are considered background tasks and will only execute when resources
     * are available.
     * 
     * @return  Priority value
     */
    public int getTaskPriority() {
        return taskPriority;
    }



}
