/*
 * SecureLogEntry.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.common.logging;

import java.io.*;
import java.util.*;
import java.text.ParseException;
import org.apache.log4j.*;

/**
 * A SecureLogEntry is a data object which is populated by parsing a
 * log entry and returning the data object.
 *
 * @version            $Revision: 1.1.1.1 $
 * @author             Shawn Stafford
 *
 */
public class SecureLogEntry {

    /** Define the error levels for the log entries as an array of strings. */
    public static String[] PRIORITY_LEVEL = {
        Level.FATAL.toString(),
        Level.ERROR.toString(),
        Level.WARN.toString(),
        Level.INFO.toString(),
        Level.DEBUG.toString() 
    };

    /** Define the abreviations representing the months of the year */
    public static String[] MONTHS = {
        "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    };

    /** Priority level for this event. */
	private int priority;

    /** Identifies the logging mechanism which generated this entry */
    private String log_id;

    /** Hour during which the event occurred */
    private int hour;

    /** Minute at which the event occurred */
    private int minute;

    /** Second at which the event occurred */
    private int second;

    /** Month during which the event occurred */
    private int month;

    /** Date on which the event occurred */
    private int day;

    /** Year during which the event occurred */
    private int year;

    /** Log message */
    private String message;


    /**
     * Constructs a log entry by parsing the given string.
     *
     * @param   line    Log file entry
     */
    public SecureLogEntry(String line) throws ParseException {
        int line_idx = 0;
        StringTokenizer st = new StringTokenizer(line);

        // Parse the log level for this line
        try {
            // Check the log level identifier
            String level = st.nextToken();
            for (int idx = 0; idx < PRIORITY_LEVEL.length; idx++) {
                if (level.equals(PRIORITY_LEVEL[idx])) {
                    line_idx = line.indexOf(level) + level.length();
                } else {
                    throw new ParseException("Unrecognized logging level: " + level, line.indexOf(level));
                }
            }
        } catch (NullPointerException npe) {
            throw new ParseException("Invalid log entry.", 0);
        }

        // Parse the timestamp
        try {
            String timestamp_str = st.nextToken();
            line_idx = line.indexOf(timestamp_str) + timestamp_str.length();

            StringTokenizer ts = new StringTokenizer(timestamp_str, ":");
            hour   = Integer.parseInt(ts.nextToken());
            minute = Integer.parseInt(ts.nextToken());
            second = Integer.parseInt(ts.nextToken());
        } catch (Exception e) {
            throw new ParseException("Unrecognized timestamp format.", line.length());
        }

        // Parse the month
        try {
            String month_str = st.nextToken();
            line_idx = line.indexOf(month_str) + month_str.length();
            for (int idx = 0; idx < MONTHS.length; idx++) {
                if (month_str.equalsIgnoreCase(MONTHS[idx])) {
                    month = idx + 1;
                }
            }
        } catch (Exception e) {
            throw new ParseException("Unrecognized month.", line_idx);
        }

        // Parse the day and year
        try {
            day  = Integer.parseInt(st.nextToken());
            year = Integer.parseInt(st.nextToken());
            line_idx = line.indexOf(year) + 4;
        } catch (Exception nfe) {
            throw new ParseException("Unable to parse day or year.", line_idx);
        }

        // Parse the log identifier
        try {
            log_id = st.nextToken();
            if ((log_id.startsWith("[") && log_id.endsWith("]"))) {
                line_idx = line.indexOf(log_id) + log_id.length();
                log_id = log_id.substring(1, log_id.length() - 1);
            } else {
                throw new ParseException("Unrecognized logging level: " + log_id, line.indexOf(log_id));
            }
        } catch (NoSuchElementException nse) {
            throw new ParseException("Unable to parse the log ID.", line.length());
        }

        // Finally, get the Message
        try {
            message = line.substring(line_idx);
        } catch (NoSuchElementException nse) {
            message = "";
        }

    }


    /**
     * Returns the priority level for this event.
     */
    public int getPriority() {
        return priority;
    }

    /**
     * Returns the identifier which indicates who made the log entry.
     */
    public String getLogId() {
        return log_id;
    }

    /**
     * Returns the hour at which the log entry was made.
     */
    public int getHour() {
        return hour;
    }

    /**
     * Returns the minute at which the log entry was made.
     */
    public int getMinute() {
        return minute;
    }

    /**
     * Returns the second at which the log entry was made.
     */
    public int getSecond() {
        return second;
    }

    /**
     * Returns the month when the log entry was made.
     */
    public int getMonth() {
        return month;
    }

    /**
     * Returns the day when the log entry was made.
     */
    public int getDay() {
        return day;
    }

    /**
     * Returns the year when the log entry was made.
     */
    public int getYear() {
        return year;
    }

    /**
     * Returns the log message.
     */
    public String getMessage() {
        return message;
    }

}
