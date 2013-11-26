/*
 * SecureLog.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.common.logging;

import org.apache.log4j.Appender;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * This class contains utility methods which are used to create
 * application logs.
 *
 * @version            $Revision: 1.2 $
 * @author             Shawn Stafford
 *
 */
public class SecureLog implements SecureLogInterface {

    /**
     * Daily log rotation.  The day of the week will be appended
     * to the end of the file name.
     */
    public static final String DAILY = "'.'yyyy-MM-dd";
    //public static final String DAILY = "'.'E";

    /** 
     * Monthly log rotation.  The year and numeric month (1-12)
     * will be appended to the end of the file name.
     */
    public static final String MONTHLY = "'.'yyyy-MM";
    
    /** 
     * Weekly log rotation. The year and the numeric week (1-52)
     * will be appended to the end of the file name.
     */
    public static final String WEEKLY = "'.'yyyy-ww";

    /** 
     * Rotate the logs twice a day.  The day of the week and am/pm
     * will be appended to the end of the file name.
     */
    public static final String TWICE_DAILY = "'.'yyyy-MM-dd-a";
    //public static final String TWICE_DAILY = "'.'E-a";

    /** Hourly log rotation. */
    public static final String HOURLY = "'.'yyyy-MM-dd-HH";

    /** Rotate the logs in one minute intervals. */
    public static final String EVERY_MINUTE = "'.'yyyy-MM-dd-HH-mm";

    /** Implements the basic logging functionality. */
    protected SecureCategory logRoot;

    /**
     * This is the current log level for the application.  Log entries will
     * be generated for every log statement equal to or greater than the
     * current log level.
     */
    protected int logLevel;

    /**
     * Format of the log file entries when they are written to the log file
     */
    protected Layout defaultLayout = new PatternLayout("%-5p %d{HH:mm:ss MMM dd yyyy} [%c] %m%n");

    /**
     * List of log files.
     */
    protected Vector<String> logFiles = new Vector<String>();

    /**
     * Default log appender.
     */
    protected Appender logAppender;

    /**
     * Default log file location.
     */
    protected OutputStream defaultLog = System.out; 

    /**
     * Name of the default log.
     */
    protected String defaultLogName = "System.out";

    /**
     * Log rotation interval for log files.
     */
    protected String rotationInterval = DAILY;

    /**
     * The text-only log server accepting incoming socket connections.
     */
    protected SecureLogServer logServer;
    protected Thread serverThread;

    /**
     * Constructs a log which writes logs entries to System.out.
     *
     * @param title     Identifies the log entries
     */
    public SecureLog(String title) {
        logAppender = new WriterAppender(defaultLayout, defaultLog);
        logAppender.setName(defaultLogName);

        // Configure the logging options
        //logRoot = new SecureCategory(title);
        logRoot = (SecureCategory)SecureCategory.getLogger(title);
        
        logRoot.addAppender(logAppender);
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
    public SecureLog(String title, String logfile) throws IOException {
        defaultLog = new FileOutputStream(logfile);
        logAppender = new DailyRollingFileAppender(defaultLayout, logfile, rotationInterval);
        logAppender.setName(defaultLogName);

        // Configure the logging options
        //logRoot = new SecureCategory(title);
        logRoot = (SecureCategory)SecureCategory.getLogger(title);

        logRoot.addAppender(logAppender);
        setLogLevel(DEBUG);
        logEntry(this, DEBUG, 
            "Constructing " + title + " log.  Initial log level is DEBUG.  " +
            "Output will be sent to: " + logfile);
    }


    /**
     * Returns the name of this log.
     */
    public String getName() {
        return logRoot.getName();
    }

    /**
     * Determines if the current instance of the log is logging at a level
     * greater than or equal to the given log level.  For example, 
     * isLogging(INFO) would return true if the log is creating log
     * entries for INFO level logging statements.
     *
     * @param   priority    Logging level in question
     */
    public boolean isWriting(int priority) {
        return (logLevel >= priority);
    }

    /**
     * Outputs the message to a log file or other form of output.
     * The entry will only be written if the current log level is equal or greater
     * than the level specified by the given parameter.  
     * <p>
     * For the sake of
     * execution speed, this method should only be called to display
     * a lightweight string.  If an object is being written to the log
     * using a toString method, the object should be logged using the
     * alternate logEntry(level, entry, message) method.  This ensures
     * that the toString conversion will only take place when the log
     * entry is of a high enough priority to actually be written to the
     * logs.
     *
     * @param   caller      Identifies the class from which the call was made
     * @param   priority    Level at which the log entry should be generated
     * @param   message     Log message used to describe the object being logged
     */
    public void logEntry(Object caller, int priority, String message) {
        if (logLevel >= priority) {
            logRoot.logEntry(caller, getPriority(priority), message);
        }
    }

    /**
     * Outputs the message and entry data to a log file or other form of output.
     * The entry will only be written if the current log level is equal or greater
     * than the level specified by the given parameter.  The object will be
     * converted to a string and written to the error log.  The message string
     * allows additional description of the entry to be provided.  
     *
     * @param   caller      Identifies the class from which the call was made
     * @param   priority    Level at which the log entry should be generated
     * @param   entry       Object to be written to the log
     * @param   message     Log message used to describe the object being logged
     */
    public void logEntry(Object caller, int priority, Object entry, String message) {
        if (logLevel >= priority) {
            Level level = getPriority(priority);

            // Combine the object and the message
            logRoot.logEntry(caller, level, renderObject(entry, message));
        }
    }

    /**
     * Writes the message to any logs which provide an adequate level 
     * of security.  The message will only be written if the logging level for this
     * message is greater or equal to the current logging level.
     *
     * @param caller    Identifies the class from which the call was made
     * @param priority  Entry priority level (FATAL, ERROR, WARN, INFO, DEBUG)
     * @param security  Minimum security required (SECURE, UNPROTECTED)
     * @param message   Additional message to be written to the logs
     */
    public void logSecureEntry(Object caller, int priority, int security, String message) {
        if (logLevel >= priority) {
            logRoot.secureEntry(caller, getPriority(priority), getSecurity(security), message);
        }
    }

    /**
     * Outputs the message and entry data to a secure log file or other form of 
     * output.  The entry will only be written if the current log level is equal 
     * or greater than the level specified by the given parameter AND if the
     * security level of the appender is sufficient to guarantee the required
     * security of the message.  The object will be converted to a string and 
     * written to the log.  The message string allows additional description of 
     * the entry to be provided.  
     *
     * @param caller    Identifies the class from which the call was made
     * @param priority  Log level of this message
     * @param security  Security level required for this message
     * @param entry     Object to be written to the log
     * @param message   Log message used to describe the object being logged
     */
    public void logSecureEntry(Object caller, int priority, int security, Object entry, String message) {
        if (logLevel >= priority) {
            Level level = getPriority(priority);
            SecurityLevel sl = getSecurity(security);

            // Combine the object and the message
            logRoot.secureEntry(caller, level, sl, renderObject(entry, message));
        }

    }

    /**
     * Converts the object and message to a string.
     *
     * @param   entry   The object to be converted to a string
     * @param   message The message describing the object
     * @return  String  Rendered object-message combination
     */
    public static String renderObject(Object entry, String message) {
        String msg = message;
        if (entry != null) {
            if ((message != null) && (message.length() > 0)) {
                msg = msg + "\n";
            }
            msg = msg + entry.toString();
        }

        return msg;
    }

    /**
     * Returns a string representation of the logging level.
     *
     * @param   level   Level to be rendered
     * @return  String  Rendered level
     */
    public static String renderLevel(int level) {
        switch (level) {
        case DEBUG: return "DEBUG";
        case INFO:  return "INFO";
        case WARN:  return "WARN";
        case ERROR: return "ERROR";
        case FATAL: return "FATAL";
        }

        return "UNKNOWN";
    }

    /**
     * Determine the Level associated with the given integer value.
     */
    protected Level getPriority(int value) {
        // Determine the priority
        switch (value) {
            case FATAL: return Level.FATAL;
            case ERROR: return Level.ERROR;
            case WARN : return Level.WARN;
            case INFO : return Level.INFO;
            case DEBUG: return Level.DEBUG;
        }        

        return Level.DEBUG;
    }

    /**
     * Determine the SecurityLevel associated with the given integer value.
     */
    protected SecurityLevel getSecurity(int value) {
        // Determine the priority
        switch (value) {
            case SECURE:        return SecurityLevel.SSL;
            case UNPROTECTED:   return SecurityLevel.UNSECURED;
        }        

        return null;
    }


    /**
     * Outputs the message and exception trace to a log file or other form of output.
     * The entry will only be written if the current log level is equal or greater
     * than the level specified by the given parameter.  The exception will be
     * converted to a string and written to the error log.  The message string
     * allows additional description of the exception to be provided.  
     *
     * @param   caller      Identifies the class from which the call was made
     * @param   priority    Level at which the log entry should be generated
     * @param   error       Exception to be written to the log
     * @param   message     Log message used to describe the object being logged
     */
    public void logException(Object caller, int priority, Throwable error, String message) {
        if (logLevel >= priority) {
            Level level = getPriority(priority);

            logRoot.logEntry(caller, level, message, error);
        }
    }

    /**
     * Writes the error and message to any logs which provide an adequate level 
     * of security.  The message will only be written if the logging level for this
     * message is greater or equal to the current logging level.
     *
     * @param caller    Identifies the class from which the call was made
     * @param priority  Entry priority level (FATAL, ERROR, WARN, INFO, DEBUG)
     * @param security  Minimum security required (SECURE, UNPROTECTED)
     * @param error     Exception to be written to the logs
     * @param message   Additional message to be written to the logs
     */
    public void logSecureException(Object caller, int priority, int security, Throwable error, String message) {
        if (logLevel >= priority) {
            Level level = getPriority(priority);
            SecurityLevel sl = getSecurity(security);

            logRoot.secureEntry(caller, level, sl, message, error);
        }
    }


    /**
     * Sets the current logging level.  The logging levels are defined in
     * net.excitehome.ws.util.
     *
     * @param   level   New logging level
     */
    public void setLogLevel(int level) {
        // Update the logging level
        logLevel = level;
        switch (level) {
            case FATAL:
                logRoot.setLevel(Level.FATAL);
                break;
            case ERROR:
                logRoot.setLevel(Level.ERROR);
                break;
            case WARN:
                logRoot.setLevel(Level.WARN);
                break;
            case INFO:
                logRoot.setLevel(Level.INFO);
                break;
            case DEBUG:
                logRoot.setLevel(Level.DEBUG);
                break;
        }        
    }

    /**
     * Setting the log level will prevent lower priority messages from being
     * written to the logs.  Valid levels include: FATAL, ERROR, WARN, INFO, DEBUG
     *
     * @param level Log level (FATAL, ERROR, WARN, INFO, DEBUG)
     */
    public void setLogLevel(String level) {
        if (level.equalsIgnoreCase("FATAL")) {
            setLogLevel(FATAL);
        } else if (level.equalsIgnoreCase("ERROR")) {
            setLogLevel(ERROR);
        } else if (level.equalsIgnoreCase("WARN")) {
            setLogLevel(WARN);
        } else if (level.equalsIgnoreCase("INFO")) {
            setLogLevel(INFO);
        } else if (level.equalsIgnoreCase("DEBUG")) {
            setLogLevel(DEBUG);
        }
    }


    /**
     * Adds a new log socket for the manager to write data to.
     *
     * @param   client  Socket to which the client has connected
     */
    public void addOutput(Socket client) throws IOException {
        SecureSocketAppender appender = new SecureSocketAppender(defaultLayout, client);
        appender.setName("Socket: port=" + client.getLocalPort());
        logEntry(this, DEBUG, "Attempting to add socket appender to the list of appenders.");
        logRoot.addAppender(appender);
    }

    /**
     * Returns the current logging level.
     *
     * @return int log level
     */
    public int getLogLevel() {
        return logLevel;
    }

    /**
     * Returns the name of the current logging level.
     *
     * @return  Name of the log level
     */
    public String getLogLevelByName() {
        switch (logLevel) {
            case FATAL: return "FATAL";
            case ERROR: return "ERROR";
            case WARN:  return "WARN";
            case INFO:  return "INFO";
            case DEBUG: return "DEBUG";
            default: return "DEBUG";
        }
    }


    /**
     * Begin listening on the specified port for client connections.  Log
     * entries will be written to the socket.
     */
    public void startServer(int port) throws IOException {
        logEntry(this, DEBUG, "Attempting to start the log server on port " + port + ".", "");
        if ((logServer == null) || (port != logServer.getPort())) {
            logServer = new SecureLogServer(this, port);
        }
        logServer.enable();
        serverThread = new Thread(logServer);
        serverThread.start();
    }

    /**
     * Stops accepting client connections.
     */
    public void stopServer() {
        logServer.disable();
    }

    /**
     * Returns the port number on which the server is listening for 
     * connections.  If the server is not running the port number is
     * -1.
     *
     * @return int log level
     */
    public int getPort() {
        if ((logServer != null) && logServer.isActive()) {
            return logServer.getPort();
        } else {
            return -1;
        }
    }


    /**
     * Start logging to an additional log file location.
     *
     * @param   logfile     New logfile location
     */
    public void addLogfile(String logfile) throws IOException {
        Appender existing = logRoot.getAppender(logfile);
        if (existing == null) {
            logEntry(this, INFO, "Adding new log file: " + logfile);
            logAppender = new DailyRollingFileAppender(defaultLayout, logfile, rotationInterval);
            logAppender.setName(logfile);
            logRoot.addAppender(logAppender);
            if (!logFiles.contains(logfile)) {
                logFiles.add(logfile);
            }
        } else {
            logEntry(this, DEBUG, "Log file already active: " + logfile);
        }
    }

    /**
     * Return a list of all the log files associated with this logger.
     * 
     * @return  List of log files
     */
    public Vector<String> getLogFiles() {
        return logFiles;
    }


    /**
     * Start logging to a new log file location.
     *
     * @param   logfile     New logfile location
     */
    public void setLogfile(String logfile) throws IOException {
        logEntry(this, WARN, "Removing existing log file appenders.");
        logRoot.removeAppender(logAppender);
        addLogfile(logfile);
    }

    
    /**
     * Sets the rotation interval of the log files.  The interval must
     * be one of the defined constants: WEEKLY, MONTHLY, YEARLY
     */
    public void setRotationInterval(String interval) {
        rotationInterval = interval;

        // Locate all appenders which can be rotated
        Enumeration appenderList = logRoot.getAllAppenders();
        while ((appenderList != null) && (appenderList.hasMoreElements())) {
            Appender current = (Appender)appenderList.nextElement();
            if (current instanceof DailyRollingFileAppender) {
                ((DailyRollingFileAppender)current).setDatePattern(interval);
            }
        }
    }


    /**
     * Returns the current log rotation interval.
     */
    public String getRotationInterval() {
        return rotationInterval;
    }


    /**
     * Start logging to a new log file location.
     *
     * @param   logfile     New logfile location
     */
    public void resetLogfile() {
        logEntry(this, WARN, "Switching to default log file.");
        logRoot.removeAppender(logAppender);
        logAppender = new WriterAppender(defaultLayout, defaultLog);
        logAppender.setName(defaultLogName);
        logRoot.addAppender(logAppender);
    }

    /**
     * Returns the names or locations of the appenders.
     */
    public Vector getAppenderLocations() {
        Enumeration appenderList = logRoot.getAllAppenders();
        Vector locationList = new Vector();

        // Collect the list of filenames or similar from the appenders
        while ((appenderList != null) && (appenderList.hasMoreElements())) {
            Appender current = (Appender)appenderList.nextElement();
            locationList.add(current.getName());
        }
        return locationList;
    }

}
