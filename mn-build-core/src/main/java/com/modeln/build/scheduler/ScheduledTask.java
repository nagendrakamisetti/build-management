/*
 * ScheduledTask.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.scheduler;

import java.util.Date;

/**
 * A ScheduledTask is a one-time event that is scheduled to occur at a specific
 * time in the future.  Scheduled tasks have a date and time of execution 
 * at which they should be executed.
 * 
 * @version            $Revision: 1.1 $  
 * @author             Shawn Stafford
 */
public class ScheduledTask extends Task {

    /** Date and time when the task should execute */
    private Date startAt;

    /**
     * Construct a task that should begin executing as soon as possible.
     * 
     * @param   name    Identifies the task by name
     */
    public ScheduledTask(String name) {
        super(name);
        startAt = new Date();
    }

    /**
     * Construct a task that should begin executing as part of the specified
     * thread group as soon as possible.
     *
     * @param   name    Identifies the task by name
     * @param   group   Thread group to which the task belongs
     */
    public ScheduledTask(String name, ThreadGroup group) {
        super(name, group);
        startAt = new Date();
    }

    /**
     * Sets the date when the task is scheduled to execute.
     *
     * @param   date    Task execution date and time
     */
    public void setSchedule(Date date) {
        startAt = date;
    }

    /** 
     * Return the date when the task is scheduled to execute.
     *
     * @return  Scheduled execution date
     */
    public Date getSchedule() {
        return startAt;
    }

}
