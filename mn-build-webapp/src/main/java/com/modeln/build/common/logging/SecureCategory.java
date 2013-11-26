/*
 * SecureCategory.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.common.logging;

import org.apache.log4j.*;
import org.apache.log4j.spi.*;
import java.util.*;

/**
 * A SecureCategory provides methods for writing to secure appenders.
 *
 * @version            $Revision: 1.1.1.1 $
 * @author             Shawn Stafford
 *
 */
public class SecureCategory extends Logger {

    // It's usually a good idea to add a dot suffix to the fully
    // qualified class name. This makes caller localization to work
    // properly even from classes that have almost the same fully
    // qualified class name as MyLogger, e.g. MyLoggerTest.
    static String FQCN = SecureCategory.class.getName() + ".";

    // It's enough to instantiate a factory once and for all.
    private static SecureCategoryFactory loggerFactory = new SecureCategoryFactory();


    /**
     * Constructs a secure category of the given name.
     *
     * @param name Category name used to identify the category instance
     */
    public SecureCategory(String name) {
        super(name);
    }

    /**
     * This method overrides {@link Logger#getLogger} by supplying
     * its own factory type as a parameter.
     */
    public static Logger getLogger(String name) {
        return Logger.getLogger(name, loggerFactory); 
    }


    /**
     * Renders the <code>message</code> and writes it 
     * to the log if the specified priority is sufficient.
     *
     * @param caller    Identifies the class from which the call was made
     * @param fqn       Fully qualified class name
     * @param priority  Log message priority
     * @param message   Message to be written
     */
    public void logEntry(Object caller, Level priority, Object message) {
        logEntry(caller, priority, message, null);
    }


    /**
     * Renders the <code>message</code> and <code>error</code> and writes them 
     * to the log if the specified priority is sufficient.
     *
     * @param caller    Identifies the class from which the call was made
     * @param priority  Log message priority
     * @param message   Message to be written
     * @param error     Exception to be written if applicable
     */
    public void logEntry(Object caller, Level priority, Object message, Throwable error) {
        if (isEnabledFor(priority)) {
            // Compose the log message
            String msg = message.toString();

            // Compose the class identification message
            String callerName;
            if (this.getLevel().toInt() == Level.DEBUG.toInt()) {
                Class callerClass = null;
                if (caller instanceof Class) {
                    callerClass = (Class) caller;
                } else {
                    callerClass = caller.getClass();
                }
                callerName = callerClass.getName();
            } else {
                callerName = name;
            }

            // Construct a normal logging event
            LoggingEvent evt = new LoggingEvent(callerName, this, priority, msg, error);

            // Use the callAppenders method to perform the logging
            // The event will eventually get passed back around to the
            // appender's doAppend method which should determine if the
            // event should be appended to the logs.
            callAppenders(evt);

        }

    }


    /**
     * Renders the <code>message</code> and writes it 
     * to the log if the specified priority and security level are sufficient.
     *
     * @param caller    Identifies the class from which the call was made
     * @param priority  Log message priority
     * @param security  Minimum log security level at which the message can be written
     * @param message   Message to be written
     */
    public void secureEntry(Object caller, Level priority, SecurityLevel security, Object message) {
        secureEntry(caller, priority, security, message, null);
    }


    /**
     * Renders the <code>message</code> and <code>error</code> and writes them 
     * to the log if the specified priority and security level are sufficient.
     *
     * @param caller    Identifies the class from which the call was made
     * @param priority  Log message priority
     * @param security  Minimum log security level at which the message can be written
     * @param message   Message to be written
     * @param error     Exception to be written if applicable
     */
    public void secureEntry(Object caller, Level priority, SecurityLevel security, Object message, Throwable error) {
        // Perform the logging if category is enabled and priority level is high enough
        if (isEnabledFor(priority)) {

            // Only perform secure logging if necessary
            if (security.equals(SecurityLevel.UNSECURED)) {
                logEntry(caller, priority, message, error);
            } else {

                // Compose the log message
                String msg = message.toString();

                // Compose the class identification message
                String callerName;
                if (this.getLevel().toInt() == Level.DEBUG.toInt()) {
                    Class callerClass = null;
                    if (caller instanceof Class) {
                        callerClass = (Class) caller;
                    } else {
                        callerClass = caller.getClass();
                    }
                    callerName = callerClass.getName();
                } else {
                    callerName = name;
                }

                // Construct a secure logging event to pass to the appenders
                SecureLoggingEvent evt = new SecureLoggingEvent(
                    callerName, this, priority, security, msg, error);

                // Use the callAppenders method to perform the logging
                // The event will eventually get passed back around to the
                // appender's doAppend method which should determine if the
                // event should be appended to the logs.
                callAppenders(evt);

            }
        }

    }
 
    /**
     * Call the appenders in the hierarchy, starting with <code>this</code>.  Any
     * secure appenders will be evaluated and messages will be logged appropriately.
     * Regular appenders will only receive <code>UNSECURED</code> messages.
     */
    public void callAppenders(LoggingEvent event) {
        if (event instanceof SecureLoggingEvent) {
            Enumeration appenderList = getSecureAppenders();
            while ((appenderList != null) && (appenderList.hasMoreElements())) {
                SecureAppender current = (SecureAppender)appenderList.nextElement();
                SecurityLevel eventSecurity = ((SecureLoggingEvent)event).securityLevel;
                if (current.getSecurityLevel().isGreaterOrEqual(eventSecurity)) {
                    current.doAppend(event);
                }
            }
        } else {
            super.callAppenders(event);
        }
    }

    /**
     * Returns a list of all secure appenders.
     */
    protected Enumeration getSecureAppenders() {
        Enumeration appenderList = getAllAppenders();
        Vector secureList = new Vector();

        // Search the list of appenders for any instances of a SecureAppender
        while ((appenderList != null) && (appenderList.hasMoreElements())) {
            Appender current = (Appender)appenderList.nextElement();
            if (current instanceof SecureAppender) {
                secureList.add(current);
            }
        }
        return secureList.elements();
    }

}

