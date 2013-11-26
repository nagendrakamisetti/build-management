/*
 * Copyright 2000-2003 by Model N, Inc.  All Rights Reserved.
 * 
 * This software is the confidential and proprietary information
 * of Model N, Inc ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you
 * entered into with Model N, Inc.
 */
package com.modeln.build.ant.perforce;

import com.modeln.build.ant.EmailMessage;

import java.io.File;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.email.EmailAddress;
import org.apache.tools.ant.types.Reference;

/**
 * This task registers an event listener that processes log
 * errors and sends an e-mail message with a summary of the 
 * results if the build fails.  The event listener uses Perforce
 * to obtain a list of check-ins that may have caused the build
 * to fail.  The list of check-ins is determined using the changelist
 * value stored in a Perforce counter as the lower bound and the
 * most recent changelist of the client as the upper bound.  If the 
 * build succeeds, the value of the counter is updated with the most
 * recent successful changelist number.
 *
 * @author Shawn Stafford
 */
public class P4BlameReport extends Task {

    /** Identifies a DEBUG log message */
    public static final String MSG_DEBUG = "debug";

    /** Identifies a ERROR log message */
    public static final String MSG_ERR = "error";

    /** Identifies a INFO log message */
    public static final String MSG_INFO = "info";

    /** Identifies a VERBOSE log message */
    public static final String MSG_VERBOSE = "verbose";

    /** Identifies a WARN log message */
    public static final String MSG_WARN = "warning";

    /** List of allowed logging levels */
    private static final String[] msgTypes = { MSG_ERR, MSG_WARN, MSG_INFO, MSG_VERBOSE, MSG_DEBUG };

    
    /** Format the blame report as HTML */
    public static final String HTML_FORMAT = "html";

    /** List of allowed report types */
    private static final String[] reportTypes = { HTML_FORMAT };


    /** Log level that the listener should listen for log messages */
    private String msgLevel = MSG_INFO;

    /** Indicate the format of the message generated by the event listener. */
    private String reportFormat = HTML_FORMAT;

    /** File to send instead of the report output */
    private File reportFile = null;

    /** Name of the Perforce counter used to store the last successful changelist number. */
    private String counterName;

    /** Name of the perforce depot that the counter applies to */
    private String depotName;

    /** List of e-mail messages to be sent. */
    private Vector messageList = new Vector();

    /** Default e-mail recipient if no matching users are found. */
    private String defaultRecipient;

    /** Default status of the listener. */
    private boolean defaultEnabled = true;

    /** Name of the property that can be set to toggle the status of the listener */
    private String toggleProperty;

    /** Reference ID of the listener */
    private String refid;


    /**
     * Set the reference ID for the listener object.
     * The reference ID allows the listener to be located later and deleted
     * or updated.
     *
     * @param id   Reference ID string
     */
    public void setId(String id) {
        refid = id;
    }

    /**
     * Return the reference ID for the listener object.
     * 
     * @return Reference ID string
     */
    public String getId() {
        return refid;
    }

    /**
     * Set the file to be sent as the body of the e-mail message.
     * If the file does not exist the listener will use the dynamic
     * build output as the body of the message.
     *
     * @param   file    File name
     */
    public void setReportFile(File file) {
        reportFile = file;
    }

    /**
     * Return the file to be sent as the body of the e-mail message. 
     *
     * @param   file    File name
     */
    public File getReportFile() {
        return reportFile; 
    }


    /**
     * Set the default status of the listener.  If set to TRUE, the listener
     * will be enabled by default and can only be disabled if the toggle 
     * property value is set by the client.
     *
     * @param  status   TRUE if the listener should be enabled, FALSE to disable
     */
    public void setDefaultEnabled(boolean status) {
        defaultEnabled = status;
    }

    /**
     * Return the default status of the listener.  A result of TRUE indicates 
     * that the listener should be enabled by default.
     *
     * @return   Default status of the listener
     */
    public boolean getDefaultEnabled() {
        return defaultEnabled;
    }


    /**
     * Set the name of a property that can be set to toggle the status of
     * the listener.  If the property is set to TRUE, the listener will
     * be enabled.  If the property is set to FALSE, the listener will
     * be disabled.
     *
     * @param  name   Property name
     */
    public void setToggleProperty(String name) {
        toggleProperty = name;
    }

    /**
     * Return the name of the property that determines whether the 
     * listener is enabled or disabled.  The value of the property
     * must be obtained by looking up the property value in the Project.
     * If no value is defined, the defaultEnabled status will be used.
     *
     * @return   Property name
     */
    public String getToggleProperty() {
        return toggleProperty;
    }


    /**
     * Set the default e-mail address that messages will be sent to if no 
     * recipients can be determined from the Perforce changelist information.
     *
     * @param  addr  E-mail address
     */
    public void setDefaultTo(String addr) {
        defaultRecipient = addr;
    }

    /**
     * Return the default e-mail address to send the notification to if none 
     * can be determined from the Perforce changelist information.
     *
     * @return   E-mail address
     */
    public String getDefaultTo() {
        return defaultRecipient;
    }

    /**
     * Set the name of the Perforce counter used to store the last
     * successful changelist number.
     *
     * @param  name   Perforce counter
     */
    public void setCounter(String name) {
        counterName = name;
    }

    /**
     * Return the name of the Perforce counter used to store the last
     * successful changelist number.
     *
     * @return   Perforce counter name
     */
    public String getCounter() {
        return counterName;
    }



    /**
     * Set the name of the Perforce depot to which the counter value
     * applies.
     *
     * @param  name   Perforce depot
     */
    public void setDepot(String name) {
        depotName = name;
    }

    /**
     * Return the name of the Perforce depot that will be inspected to 
     * determine the list of applicable check-ins.
     *
     * @return   Perforce depot name
     */
    public String getDepot() {
        return depotName;
    }

    /**
     * Set the message format generated by the event listener.
     * The default format is HTML.
     *
     * @param  format   Message format (html, text, etc)
     */
    public void setFormat(String format) {
        reportFormat = null;

        // Make sure a valid format is specified by the user
        for (int idx = 0; idx < reportTypes.length; idx++) {
            if (reportTypes[idx].equalsIgnoreCase(format)) {
                reportFormat = format.toLowerCase();
            }
        }

        // Alert the user if they fail to set a valid format
        if (reportFormat == null) {
            throw new BuildException("Invalid message format: " + format);
        }
    }

    /**
     * Return the message format generated by the event listener.
     * The default format is HTML.
     *
     * @return   Message format (html, text, etc)
     */
    public String getFormat() {
        return reportFormat;
    }

    /**
     * Set the log level that the listener should listen at.
     *
     * @param  level   Log level (error, warning, info, verbose, debug)
     */
    public void setLogLevel(String level) {
        msgLevel = null;

        // Make sure a valid format is specified by the user
        for (int idx = 0; idx < msgTypes.length; idx++) {
            if (msgTypes[idx].equalsIgnoreCase(level)) {
                msgLevel = level.toLowerCase();
            }
        }

        // Alert the user if they fail to set a valid format
        if (msgLevel == null) {
            throw new BuildException("Invalid log level: " + level);
        }
    }

    /**
     * Return the log level being monitored by the listener.
     *
     * @return   Log level (error, warning, info, verbose, debug)
     */
    public String getLogLevel() {
        return msgLevel;
    }


    /**
     * Set the mail message to be sent.
     *
     * @param  message  Email message to send as notification
     */
    public void addConfiguredEmailMessage(EmailMessage message) {
        messageList.add(message);
    }

    /**
     * Return the list of email messages that must be sent as notification.
     *
     * @return List of email messages
     */
    public Vector getEmailMessages() {
        return messageList;
    }

    /**
     * Register an event listener to process build events and generate
     * a summary report.
     */
    public void execute() throws BuildException {
        if (counterName == null) {
            throw new BuildException("A Perforce counter name must be specified.");
        }
        if (depotName == null) {
            throw new BuildException("A Perforce depot must be specified.");
        }
        if (messageList.size() < 1) {
            throw new BuildException("At least one notification message must be defined.");
        }

        // Create a new build listener for parsing events
        P4BlameListener listener = new P4BlameListener(this);

        // Set the report file for the listener
        listener.setReportFile(getReportFile());

        // Set the log level of the listener
        listener.setLogLevel(getLogLevelAsInt());

        // Set the reference ID for the listener
        if (refid != null) {
            listener.setRefid(new Reference(project, refid));
        }

        // Add the event listeners to the project
        project.addBuildListener(listener);
    }


    /**
     * Converts the log level to an integer value that corresponds to the
     * log levels defined for Ant projects.
     *
     * @return  Log level 
     */
    private int getLogLevelAsInt() {
        if (msgLevel.equalsIgnoreCase(MSG_ERR)) {
            return Project.MSG_ERR;
        } else if (msgLevel.equalsIgnoreCase(MSG_WARN)) {
            return Project.MSG_WARN;
        } else if (msgLevel.equalsIgnoreCase(MSG_WARN)) {
            return Project.MSG_WARN;
        } else if (msgLevel.equalsIgnoreCase(MSG_INFO)) {
            return Project.MSG_INFO;
        } else if (msgLevel.equalsIgnoreCase(MSG_VERBOSE)) {
            return Project.MSG_VERBOSE;
        } else if (msgLevel.equalsIgnoreCase(MSG_DEBUG)) {
            return Project.MSG_DEBUG;
        } else {
            return -1;
        }
    }

}
