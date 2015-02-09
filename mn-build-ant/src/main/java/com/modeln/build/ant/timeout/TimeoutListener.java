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

import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildListener;


/**
 * Monitors the Ant project to determine if the build enters an idle state
 * for too long.  Once this timeout expires, the build should be aborted and
 * an exception raised to signal the error.
 *
 * @author Shawn Stafford
 */
public class TimeoutListener implements BuildListener {


    /** Maintain a reference to the timer which constructed this listner */
    private TimeoutTask parent;

    /**
     * Construct the listener and watch for build activity.
     */
    public TimeoutListener(TimeoutTask timer) {
        parent = timer;
    }

    /**
     * Signals that a build has started. This event
     * is fired before any targets have started.
     * 
     * @param event An event with any relevant extra information.
     *              Must not be <code>null</code>.
     */
    public void buildStarted(BuildEvent event) {
        parent.resetTimer();
    }

    /**
     * Signals that the last target has finished. This event
     * will still be fired if an error occurred during the build.
     * 
     * @param event An event with any relevant extra information.
     *              Must not be <code>null</code>.
     *
     * @see BuildEvent#getException()
     */
    public void buildFinished(BuildEvent event) {
        parent.resetTimer();
    }

    /**
     * Signals that a target is starting.
     * 
     * @param event An event with any relevant extra information.
     *              Must not be <code>null</code>.
     *
     * @see BuildEvent#getTarget()
     */
    public void targetStarted(BuildEvent event) {
        parent.resetTimer();
    }

    /**
     * Signals that a target has finished. This event will
     * still be fired if an error occurred during the build.
     * 
     * @param event An event with any relevant extra information.
     *              Must not be <code>null</code>.
     *
     * @see BuildEvent#getException()
     */
    public void targetFinished(BuildEvent event) {
        parent.resetTimer();
    }

    /**
     * Signals that a task is starting.
     * 
     * @param event An event with any relevant extra information.
     *              Must not be <code>null</code>.
     *
     * @see BuildEvent#getTask()
     */
    public void taskStarted(BuildEvent event) {
        parent.resetTimeoutAttempts();
        parent.resetTimer();
    }

    /**
     * Signals that a task has finished. This event will still
     * be fired if an error occurred during the build.
     *
     * @param event An event with any relevant extra information.
     *              Must not be <code>null</code>.
     *
     * @see BuildEvent#getException()
     */
    public void taskFinished(BuildEvent event) {
        parent.resetTimer();
    }

    /**
     * Signals a message logging event.
     * 
     * @param event An event with any relevant extra information.
     *              Must not be <code>null</code>.
     *
     * @see BuildEvent#getMessage()
     * @see BuildEvent#getPriority()
     */
    public void messageLogged(BuildEvent event) {
        parent.resetTimer();
    }




}
