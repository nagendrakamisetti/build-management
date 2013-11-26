/*
 * SessionTicket.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.web.data;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/** 
 * This class provides the data methods for managing session timeouts
 * within the web application.  If the user is inactive (no commands
 * executed), then the session timestamp will eventually expire and
 * the user will be forced to re-authenticate.
 * 
 * @author             Shawn Stafford
 * @version            $Revision: 1.1.1.1 $ 
 */

public class SessionTicket {
    
    /** Date format used to parse dates */
    private static final SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");

    /** date and time recorded by the timestamp */
    private Date timestamp;

    /**
     * Construct a new timestamp
     */
    public SessionTicket() {
        timestamp = new Date();
    }

    /**
     * Construct a new timestamp
     * 
     * @param   time    Timestamp value
     */
    public SessionTicket(Date time) {
        timestamp = time;
    }

    /**
     * Parse a string to recreate the timestamp from the value.
     * The string should be in the format of: 
     * <code>
     *    dow mon dd hh:mm:ss zzz yyyy
     * </code>
     * @param   str     Timestamp to be parsed
     * @return  Session timestamp value
     */
    public static SessionTicket parse(String str)
        throws ParseException
    {
        return new SessionTicket(format.parse(str));
    }

    /**
     * Returns the total time (in milliseconds) that has elapsed since
     * the timestamp was created.
     */
    public long getElapsedTime() {
        Date now = new Date();
        return now.getTime() - timestamp.getTime();
    }

    /**
     * Returns the timestamp as a string value
     */
    public String toString() {
        return format.format(timestamp);
    }
}
