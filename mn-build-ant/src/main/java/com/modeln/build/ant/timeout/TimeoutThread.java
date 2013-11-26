/*
 * Copyright 2000-2003 by Model N, Inc.  All Rights Reserved.
 * 
 * This software is the confidential and proprietary information
 * of Model N, Inc ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you
 * entered into with Model N, Inc.
 */
package com.modeln.build.ant.timeout;

import java.util.Date;

import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.Project;


/**
 * The timeout listener is designed to monitor an Ant process and
 * prevent hung processes from blocking the entire build.  This is done
 * by monitoring the Ant logging events and forcing a build failure if
 * no activity is detected within a given amount of time.
 *
 * @author Shawn Stafford
 */
public class TimeoutThread extends Thread {

    /** Number of milliseconds that the timeout thread sleeps between timeout checks */
    private static final long POLLING_INTERVAL = 1000;

    /** Maintain a reference to the task which constructed this timer */
    private TimeoutTask parent;

    /** Timestamp when the thread began counting down */
    private Date startDate;

    /** Number of seconds after the starting date when the timeout should occur */
    private long delayLength;

    /** Determines whether the thread is currently running */
    private boolean running = true;

    /** Determine whether the timeout should execute only once or monitor continuously */
    private boolean continuous = false;


    /**
     * Construct the timout thread to expire after the specified amount of time.
     */
    public TimeoutThread(TimeoutTask task, long delay) {
        super("Build Timeout Thread");
        parent = task;
        startDate = new Date();
        delayLength = delay;
    }

    /**
     * Reset the timer.
     */
    public void reset() {
        startDate = new Date();
    }

    /**
     * Set the timout listener to continue indefinitely or execute only once 
     * and then terminate.
     *
     * @param   enable      TRUE if the timeout loop should continually execute
     */
    public void setContinuous(boolean enable) {
        continuous = enable;
    }

    /**
     * Stop the timeout thread from running by setting a conditional flag.
     * This will cause the timeout polling loop to exit.
     *
     * @param   enable      TRUE if the timeout thread should run
     */
    public void setRunning(boolean enable) {
        running = enable;
    }


    /**
     * Enter a polling loop that wakes up periodically to determine if the 
     * timeout time has elapsed.
     */
    public void run() {
        // DO NOT insert print statements in this block of code
        // Ant will trap them and generate build events, which the timeout
        // listener will interpret as activity.

        while (running) {
            // Determine if the timeout has occurred
            long target = startDate.getTime() + (delayLength * 1000);
            Date current = new Date();
            if (current.getTime() > target) {
                // Construct a build event to go with the notification
                BuildEvent timeoutEvent = new BuildEvent(parent);
                parent.sendNotifications(timeoutEvent);
            } else {

                // Attempt to sleep for a number of seconds
                try {
                    sleep(POLLING_INTERVAL);
                } catch (InterruptedException ex) {
                    // Allow the timeout thread to be interrupted, but don't stop running
                }

            } // if expired

        } // while running

    }

}
