/*
 * SecureLogInterface.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.common.logging;

/**
 * This interface defines the methods which should be made available by any
 * class providing logging implementation.
 *
 * @version            $Revision: 1.1.1.1 $
 * @author             Shawn Stafford
 *
 */
public interface SecureLogInterface {

    /**
     * Fatal errors refer to instances where the application cannot function
     * normally under the current conditions and which cannot be corrected
     * while the application is running.
     */
    public static final int FATAL = 0;

    /**
     * Errors refer to any event which indicates abnormal or unexpected behavior
     * which must be corrected before the application can function normally.
     */
    public static final int ERROR = 1;

    /**
     * Warnings indicate events which may require administrative attention but
     * which will not prevent the application from continuing to function normally.
     */
    public static final int WARN = 2;

    /**
     * Informational events provide information about the status of the program,
     * and do not indicate errors or unexpected conditions.
     */
    public static final int INFO = 3;

    /**
     * Debugging events are used during development to trace the execution of 
     * an application.
     */
    public static final int DEBUG = 4;


    /**
     * Secure log events will not be written to any logs which are not considered
     * secure from network hacking.  For example, an unencrypted socket connection
     * would be considered insecure because the contents could be inspected as they
     * are transmitted across the network.
     */
    public static final int SECURE = 0;

    /**
     * Unprotected log events contain non-sensitive data and do not require a high
     * level of protection.
     */
    public static final int UNPROTECTED = 1;


    /**
     * Setting the log level will prevent lower priority messages from being
     * written to the logs.  Valid levels include: FATAL, ERROR, WARN, INFO, DEBUG
     *
     * @param level Log level (FATAL, ERROR, WARN, INFO, DEBUG)
     */
    public void setLogLevel(int level);

    /**
     * Setting the log level will prevent lower priority messages from being
     * written to the logs.  Valid levels include: FATAL, ERROR, WARN, INFO, DEBUG
     *
     * @param level Log level (FATAL, ERROR, WARN, INFO, DEBUG)
     */
    public void setLogLevel(String level);

    /**
     * Returns the current log level priority.
     *
     * @return int  Log level (FATAL, ERROR, WARN, INFO, DEBUG)
     */
    public int  getLogLevel();

    /**
     * Determines if the current instance of the log is logging at a level
     * greater than or equal to the given log level.  For example, 
     * isLogging(INFO) would return true if the log is creating log
     * entries for INFO level logging statements.
     */
    public boolean isWriting(int level);

    /**
     * Returns the port number on which the server is listening for 
     * connections.  If the server is not running the port number is
     * -1.
     *
     * @return int log level
     */
    public int getPort();

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
     * @param caller    Identifies the class from which the call was made
     * @param priority  Entry priority level (FATAL, ERROR, WARN, INFO, DEBUG)
     * @param message   Additional message to be written to the logs
     */
    public void logEntry(Object caller, int priority, String message);

    /**
     * Outputs the message and entry data to a log file or other form of output.
     * The entry will only be written if the current log level is equal or greater
     * than the level specified by the given parameter.  The object will be
     * converted to a string and written to the error log.  The message string
     * allows additional description of the entry to be provided.  
     *
     * @param caller    Identifies the class from which the call was made
     * @param priority  Entry priority level (FATAL, ERROR, WARN, INFO, DEBUG)
     * @param entry     Object to be written to the logs
     * @param message   Additional message to be written to the logs
     */
    public void logEntry(Object caller, int priority, Object entry, String message);

    /**
     * Outputs the message and exception data to a log file or other form of output.
     * The exception will only be written if the current log level is equal or greater
     * than the level specified by the given parameter.  The object will be
     * converted to a string and written to the error log.  The message string
     * allows additional description of the entry to be provided.  
     *
     * @param caller    Identifies the class from which the call was made
     * @param priority  Entry priority level (FATAL, ERROR, WARN, INFO, DEBUG)
     * @param error     Exception to be written to the logs
     * @param message   Additional message to be written to the logs
     */
    public void logException(Object caller, int priority, Throwable error, String message);

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
    public void logSecureEntry(Object caller, int priority, int security, String message);

    /**
     * Writes the object and message to any logs which provide an adequate level 
     * of security.  The message will only be written if the logging level for this
     * message is greater or equal to the current logging level.
     *
     * @param caller    Identifies the class from which the call was made
     * @param priority  Entry priority level (FATAL, ERROR, WARN, INFO, DEBUG)
     * @param security  Minimum security required (SECURE, UNPROTECTED)
     * @param entry     Object to be written to the logs
     * @param message   Additional message to be written to the logs
     */
    public void logSecureEntry(Object caller, int priority, int security, Object entry, String message);

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
    public void logSecureException(Object caller, int priority, int security, Throwable error, String message);


}
