/*
 * SessionLog.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.common.logging;

import org.apache.log4j.*;
import java.io.*;
import java.util.Vector;
//import java.net.ServerSocket;
//import java.net.Socket;
//import javax.servlet.*;
//import javax.servlet.http.*;
import com.modeln.build.common.data.account.UserData;
import com.modeln.build.common.data.account.GroupData;

/**
 * Session tracking allows a seperate log to be maintained specifically
 * for tracking a LoginData field matching a specific criteria.  Using
 * this mechanism, programs can log information specific to a particular
 * user, MSO, etc. 
 *
 * @version            $Revision: 1.1.1.1 $
 * @author             Shawn Stafford
 */
public class SessionLog extends SecureLog {

    /** Implements the log for tracking a specific login session */
    private SecureCategory logTracking;

    /**
     * Specifies that a specific user or MSO should be logged to a seperate 
     * log file.  The value of this variable will identify which information
     * to track.  This must be used in conjunction with the 
     * <code>trackField</code> value.
     */
    private String trackSession;

    /**
     * Specifies which field of the login object should be tracked.  For
     * example, if <code>trackField = LoginData.LOGIN_ID</code>, then the
     * login referred to by <code>trackSession</code> will be tracked.
     */
    private String trackField;

    /**
     * Log appender responsible for tracking the session information for 
     * the LoginData being tracked.
     */
    private Appender trackAppender;

    /**
     * Determines whether session tracking is enabled.
     */
    private boolean trackEnabled = false;

    /**
     * Default log file location.
     */
    protected OutputStream defaultLog = System.out; 

    /**
     * Name of the default log.
     */
    protected String defaultLogName = "System.out";

    /**
     * Constructs a log which writes logs entries to a default logging location.
     *
     * @param title     Identifies the log entries
     */
    public SessionLog(String title) {
        super(title);
        trackAppender = new WriterAppender(defaultLayout, defaultLog);
        trackAppender.setName(defaultLogName);

        // Configure the logging options
        // logTracking = new SecureCategory(title);
        logTracking = (SecureCategory)SecureCategory.getLogger(title);

        logTracking.addAppender(trackAppender);
        logTracking.setLevel(Level.DEBUG);
        setLogLevel(DEBUG);
        logEntry(this, DEBUG, 
            "Constructing " + title + " log.  Initial log level is DEBUG.  " +
            "Output will be sent to: " + defaultLogName);
    }


    /**
     * Constructs a log which writes log entries to the given log file.
     *
     * @param title     Identifies the log entries
     * @param logfile   Location of the log file
     */
    public SessionLog(String title, String logfile) throws IOException {
        super(title, logfile);
        trackAppender = new FileAppender(defaultLayout, logfile);
        trackAppender.setName(logfile);

        // Configure the logging options
        // logTracking = new SecureCategory(title);
        logTracking = (SecureCategory)SecureCategory.getLogger(title);
        
        logTracking.addAppender(trackAppender);
        logTracking.setLevel(Level.DEBUG);
        setLogLevel(DEBUG);
        logEntry(this, DEBUG, 
            "Constructing " + title + " log.  Initial log level is DEBUG.  " +
            "Output will be sent to: " + logfile);
    }


    /**
     * When session tracking is enabled, any log entries 
     * made using the <code>logSecureSession</code> method will be evaluated
     * and logged to the session log if they match the tracking criteria.
     * The current implementation currently only allows for tracking by 
     * LOGIN_ID and by MSO_NAME.
     *
     * @param   field   The LoginData field to track, such as LoginData.LOGIN_ID
     * @param   target  The value to target for the given field
     * @param   logfile Log file to write to 
     */
    public void trackSession(String field, String target, String logfile) 
        throws IOException
    {
        try {
            trackAppender = new FileAppender(defaultLayout, logfile);
            trackAppender.setName(logfile);
            logTracking.addAppender(trackAppender);
            trackEnabled = true;
            trackSession = target;
            trackField = field;
        } catch (Exception e) {
            String msg = "Could not enable session tracking: " + 
                "field=" + field + ", target=" + target + ", logfile=" + logfile;
            logRoot.logEntry(this, getPriority(ERROR), msg, e);
            trackEnabled = false;
        }
    }

    /**
     * Prevents further session logging until the tracking is enabled
     * again using the <code>trackSession</code> command.
     */
    public void haltTracking() {
        trackEnabled = false;
        if (trackAppender != null) {
            logTracking.removeAppender(trackAppender);
            trackAppender = null;
        }
    }

    /** 
     * Determines if session tracking is currently active.
     *
     * @return  boolean     True if session tracking has been enabled
     */
    public boolean isTracking() {
        return trackEnabled;
    }

    /** 
     * Determines if the LoginData object meets the current tracking criteria.
     *
     * @param   login       LoginData object for the session
     * @return  boolean     True if the LoginData should be tracked
     */
    private boolean isTracking(UserData login) {
        if (trackEnabled && (login != null)) {
            if (trackField.equals(UserData.USERNAME)) {
                return trackSession.equals(login.getUsername());
            } else if (trackField.equals(GroupData.GROUP_ID)) {
                GroupData[] groups = login.getAllGroups();
                if (groups != null) {
                    for (int idx = 0; idx < groups.length; idx++) {
                        if (trackSession.equals(groups[idx].getGid())) return true;
                    }
                }
            } else if (trackField.equals(GroupData.NAME)) {
                GroupData[] groups = login.getAllGroups();
                if (groups != null) {
                    for (int idx = 0; idx < groups.length; idx++) {
                        if (trackSession.equals(groups[idx].getName())) return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Outputs the message to the session log file as well as any general log
     * files in use.  The session log entry will only be written if the current 
     * log level is equal or greater than the level specified by the given 
     * parameter AND the LoginData matches the criteria currently being tracked.
     * 
     *
     * @param   caller      Identifies the class from which the call was made
     * @param   priority    Level at which the log entry should be generated
     * @param   login       LoginData object which identifies the session
     * @param   message     Log message used to describe the object being logged
     */
    public void logSession(Object caller, int priority, UserData login, String message) {
        if (logLevel >= priority) {
            logSession(caller, priority, login, null, message);
        }
    }

    /**
     * Outputs the message to the session log file as well as any general log
     * files in use.  The session log entry will only be written if the current 
     * log level is equal or greater than the level specified by the given 
     * parameter AND the LoginData matches the criteria currently being tracked.
     * A stack trace of the exception will be written to the logs.
     * 
     *
     * @param   caller      Identifies the class from which the call was made
     * @param   priority    Level at which the log entry should be generated
     * @param   login       LoginData object which identifies the session
     * @param   error       Exception being logged
     * @param   message     Log message used to describe the object being logged
     */
    public void logSession(Object caller, int priority, UserData login, Throwable error, String message) {
        if (logLevel >= priority) {
            Level level = getPriority(priority);

            // Perform the general logging first
            logRoot.logEntry(caller, level, message, error);

            // Perform additional session logging
            if ((login != null) && isTracking(login)) {
                logTracking.logEntry(caller, level, "(Tracking: " + trackField + "=" + trackSession + ") " + message, error);
            }

        }
    }


    /**
     * Returns the name of the LoginData field currently being tracked.  If
     * tracking is disabled, null is returned.
     *
     * @return  String  Name of the LoginData field
     */
    public String whichField() {
        if (trackEnabled) {
            return trackField;
        } else {
            return null;
        }
    }


    /**
     * Returns the session value which is currently being tracked.  For example,
     * if the <code>LoginData.LOGIN_ID</code> field is being tracked, then the
     * value returned would be a specific login name to watch for.
     *
     * @return  String  Value of the field being tracked
     */
    public String whichSession() {
        if (trackEnabled) {
            return trackSession;
        } else {
            return null;
        }
    }

    /**
     * Returns the name of the log file where the tracking information is being
     * written.
     *
     * @return  String path to the session tracking log file
     */
    public String getTrackingLocation() {
        if (trackEnabled) {
            return trackAppender.getName();
        } else {
            return null;
        }
    }

}
