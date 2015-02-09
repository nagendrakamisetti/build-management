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

import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildEvent;

import java.util.Vector;

import com.modeln.build.ant.Notification;

/**
 * Construct a timeout listener that will terminate the build if there
 * is no build activity within a specified amount of time.
 *
 * @author Shawn Stafford
 */
public final class TimeoutTask extends Task {

    /** List of notifications that must be sent as a result of a timeout */
    private Vector<Notification> notificationList = new Vector<Notification>();

    /** Length of time that the build can remain idle */
    private TimeoutThread timer;

    /** 
     * Record the number of attempts that have been made to stop the thread. 
     * The first attempt will be a gentle interrupt that causes execution of
     * non-blocking process to continue.  The second attempt will perform a
     * full stop of all threads.
     */
    private int timeoutAttempts = 0;

    /**
     * Determine whether the timeout thread should attempt a graceful timeout
     * or whether it should simply force a system exit (not graceful).  The 
     * default is to perform a graceful exit.
     */
    private boolean gracefulExit = true;

    /**
     * Determines whether the build should exit following a timeout or whether
     * a notification is sufficient.  The default is to exit following a timeout.
     */
    private boolean exitOnTimeout = true;

    /**
     * Set the length of time that the build process can remain idle before
     * timing out and exiting with an error.
     *
     * @param   amount  Number of seconds to wait until timing out
     */
    public void setWait(Long amount) throws BuildException {
        timer = new TimeoutThread(this, amount.longValue());
        timer.start();
    }

    /**
     * If a graceful exit is required, the system will attempt to identify the
     * blocked threads and wake them up.  Otherwise the system will simply exit.
     *
     * @param   graceful    True if a graceful exit should be attempted
     */
    public void setGracefulExit(Boolean graceful) {
        gracefulExit = graceful.booleanValue();
    }

    /**
     * Forces the build to exit once the timeout expires.  If the value is false,
     * the build will not attempt to exit but will send a notification to alert
     * listeners that the build may have timed out.
     *
     * @param   exit    True if the build should exit following a timeout
     */
    public void setExitOnTimeout(Boolean exit) {
        exitOnTimeout = exit.booleanValue();
    }

    /**
     * Process the nested notification elements to determine how to send 
     * notifications to the interested parties or processes.
     */
    public void addConfiguredNotification(Notification notification) {
        System.out.println("Adding notification target: " + notification.getType());
        notificationList.add(notification);
    }

    /**
     * Return a list of notification tasks.
     */
    public Vector<Notification> getNotifications() {
        return notificationList;
    }

    /**
     * Reset the timer task.
     */
    public void resetTimer() {
        timer.reset();
    }

    /**
     * Reset the number of attempts that have been made to kill the task.
     */
    public void resetTimeoutAttempts() {
        timeoutAttempts = 0;
    }

    /**
     * Perform the task parsing and generate the report.
     */
    @SuppressWarnings("deprecation")
	public void execute() throws BuildException {
        // Create a new build listener for monitoring the build
        TimeoutListener listener = new TimeoutListener(this);
        project.addBuildListener(listener);
    }

    /**
     * Iterate through the list of notifications that must be sent.
     * Once the notifications have been sent, attempt to interrupt or
     * even stop the threads. 
     *
     * @param   timeoutEvent    Event that generated the notification
     */
    @SuppressWarnings("deprecation")
	public void sendNotifications(BuildEvent timeoutEvent) throws BuildException {

        // Send the list of notifications
        for (int idx = 0; idx < notificationList.size(); idx++) {
            ((Notification)notificationList.get(idx)).sendNotification(timeoutEvent);
        }

        // Determine whether a graceful attempt should be made
        if (!exitOnTimeout) {
            // Do not exit
            log("An exit is not required for this timeout.  The build will continue.");
        } else if (gracefulExit) {
            StringBuffer info = new StringBuffer();

            // Construct a list of threads that need to be terminated
            Vector<Thread> staleThreads = new Vector<Thread>();

            // Obtain some information about the threads
            Thread current = Thread.currentThread();
            ThreadGroup group = current.getThreadGroup();
            Task currentTask = null;

            info.append("There have been " + timeoutAttempts + " timeout attempts since the last reset.\n");
            info.append("The following threads are currently running in the thread group: \n");
            Thread ta[] = new Thread[group.activeCount()];
            int n = group.enumerate(ta);
            for (int i = 0; i < n; i++) {
                info.append("Thread " + i + ": name=" + ta[i].getName());
                currentTask = project.getThreadTask(ta[i]);
                // Only select threads which have an associated Ant task
                if (currentTask != null) {
                    info.append(", task=" + currentTask.getTaskName() + "\n");
                    staleThreads.add(ta[i]);
                }
            }
            log("\n" + info.toString() + "\n");

            // Attempt to stop the threads
            performGracefulStop(staleThreads);
        } else {
            log("Task timeout detected.  Exiting the system.");
            project.fireBuildFinished(
                new BuildException("The timeout task is preparing to force the build to exit."));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
            }
            System.exit(0);
        }

    }


    /**
     * Send a graceful timeout to the list of naughty threads in an attempt
     * to unblock them.  The type of signal sent to the threads will be 
     * determined by the number of previous attempts that have been made.
     * The first attempt will trigger an interrupt of the threads, a 
     * second attempt will attempt an exit, and all other attempts will
     * simply force the JVM to exit.
     *
     * @param   threads     List of threads that should be killed
     */
    @SuppressWarnings("deprecation")
	private void performGracefulStop(Vector<Thread> threads) {
        // Determine how forcefully we should attempt to kill the threads
        switch (timeoutAttempts) {
            case 0:   // Gently interrupt
                interrupt(threads);
                break;

            case 1:   // Forcefully kill  
                stop(threads);
                break;

            default:  // Do the best that you can
                log("Unable to stop the stale threads.  Disabling the timeout thread and exiting.");
                timer.setRunning(false);
                project.fireBuildFinished(
                    new BuildException("The timeout task is preparing to force the build to exit."));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                }
                System.exit(0);
        }

        // Explicitly reset the timer just in case none of the log messages 
        // triggered a reset
        resetTimer();

        // The number of attempts should be reset whenever a new task begins
        timeoutAttempts++;
    }

    /**
     * Attempt to interrupt the specified threads.
     *
     * @param   threads     List of threads that should be killed
     */
    @SuppressWarnings("deprecation")
	private void interrupt(Vector<Thread> threads) {
        Thread staleThread = null;
        Task staleTask = null;

        // Interrupt each of the threads
        for (int idx = 0; idx < threads.size(); idx++) {
            staleThread = (Thread)threads.get(idx);
            staleTask = project.getThreadTask(staleThread);
            log("Attempting to interrupt the stale thread: " +
                "name=" + staleThread.getName() + ", " +
                "task=" + staleTask.getTaskName());
            staleThread.interrupt();
        }

    }

    /**
     * Attempt to stop the specified threads.
     *
     * @param   threads     List of threads that should be killed
     */
    @SuppressWarnings("deprecation")
	private void stop(Vector<Thread> threads) {
        Thread staleThread = null;
        Task staleTask = null;

        // Interrupt each of the threads
        for (int idx = 0; idx < threads.size(); idx++) {
            staleThread = (Thread)threads.get(idx);
            staleTask = project.getThreadTask(staleThread);
            log("Attempting to stop the stale thread: " +
                "name=" + staleThread.getName() + ", " +
                "task=" + staleTask.getTaskName());
            staleThread.stop();
        }

    }


}
