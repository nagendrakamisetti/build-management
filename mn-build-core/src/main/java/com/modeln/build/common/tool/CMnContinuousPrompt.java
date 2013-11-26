/*
 * Copyright 2000-2002 by Model N, Inc.  All Rights Reserved.
 * 
 * This software is the confidential and proprietary information
 * of Model N, Inc ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you
 * entered into with Model N, Inc.
 */
package com.modeln.build.common.tool;

import java.util.Date;

/**
 * The continuous prompt runs as a separate thread that displays
 * the message at regular intervals.  This is useful to prevent
 * terminal sessions from timing out due to inactivity or remind
 * the user that action is required. 
 */
public class CMnContinuousPrompt implements Runnable {

    /** Timestamp of the last prompt */
    private long lastprompt = 0;

    /** Interval (in seconds) to display the prompt */
    private int interval = 0;

    /** Text to display to the user */
    private String prompt = null;

    /** Status of the prompt thread */
    private boolean enabled = false;


    /**
     * Construct the thread.
     *
     * @param  sec  Number of seconds between prompt messages
     * @param  msg  Message to display
     */
    public CMnContinuousPrompt(int sec, String msg) {
        interval = sec;
        prompt = msg;
    }


    /**
     * Set the interval at which the prompt message will be repeated.
     *
     * @param  sec   Number of seconds between prompt messages
     */
    public void setInterval(int sec) {
        interval = sec;
    }

    /**
     * Set the message to display.
     *
     * @param  msg   Message to display
     */
    public void setPrompt(String text) {
        prompt = text;
    }

    /**
     * Enable or disable the thread execution.
     *
     * @param  enable   Enable or disable the prompt
     */
    public void setEnabled(boolean enable) {
        enabled = enable;
    }


    /**
     * Display the prompt to the user.
     */
    public void showPrompt() {
        System.out.print("\n" + prompt);
        Date now = new Date();
        lastprompt = now.getTime(); 
    }

    /**
     * Continuously display the prompt message.
     */
    public void run() {
        enabled = true;
        boolean keepgoing = true; 

        while (enabled && keepgoing) {
            // Compare the current time to the last display time
            long elapsed = 0;
            if (lastprompt > 0) {
                Date now = new Date();
                long thisprompt = now.getTime();
                elapsed = thisprompt - lastprompt;
            }

            // Determine if the prompt is due to be displayed
            long desired = interval * 1000;
            if (elapsed >= desired) {
                showPrompt(); 
            }

            // Pause thread execution for a short duration
            // before checking the display interval again 
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                keepgoing = false;
            }


        }
    }

}

