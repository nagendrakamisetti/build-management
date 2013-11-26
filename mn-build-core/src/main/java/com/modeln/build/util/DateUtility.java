/*
 * DateUtility.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.util;

import java.util.Date;
import java.util.GregorianCalendar;

/**
 * This class implements date manipulation methods.
 */
public class DateUtility {

    private static final long millisecondsPerYear   = 1000 * 60 * 60 * 24 * 365;
    private static final long millisecondsPerMonth  = 1000 * 60 * 60 * 24 * 30;
    private static final long millisecondsPerQuarter = 1000 * 60 * 60 * 24 * 7 * 13;
    private static final long millisecondsPerWeek   = 1000 * 60 * 60 * 24 * 7;
    private static final long millisecondsPerDay    = 1000 * 60 * 60 * 24;
    private static final long millisecondsPerHour   = 1000 * 60 * 60;
    private static final long millisecondsPerMinute = 1000 * 60;
    private static final long millisecondsPerSecond = 1000;

    /** Beginning date of the date interval */
    private static final int BEGIN_DATE = 0;

    /** Ending date of the date interval */
    private static final int END_DATE = 1;

    /** Reporting interval of one day */
    public static final int HOURLY = 0;

    /** Reporting interval of one day */
    public static final int DAILY = 1;

    /** Reporting interval of one week */
    public static final int WEEKLY = 2;

    /** Reporting interval of one month */
    public static final int MONTHLY = 3;

    /** Reporting interval of one quarter */
    public static final int QUARTERLY = 4;

    /** Reporting interval of one year */
    public static final int YEARLY = 5;

    /**
     * Return the approximate reporting interval that should be used to
     * represent the range of dates.  If the interval range cannot be
     * determined, a YEARLY interval range is assumed.
     *
     * @param   begin   Start of the date range
     * @param   end     End of the date range
     * @param   count   Number of intervals into which the range will be divided
     */
    public static int getIntervalType(Date begin, Date end, int count) {
        int type = YEARLY;

        long interval = (end.getTime() - begin.getTime()) / count;
        if (interval <= millisecondsPerHour) {
            type = HOURLY;
        } else if (interval <= millisecondsPerDay) {
            type = DAILY;
        } else if (interval <= millisecondsPerWeek) {
            type = WEEKLY;
        } else if (interval <= (millisecondsPerDay * 31)) {
            type = MONTHLY;
        } else if (interval <= (millisecondsPerDay * 31 * 3)) {
            type = QUARTERLY;
        } else if (interval <= millisecondsPerYear) {
            type = YEARLY;
        }

        return type;
    }

    /**
     * Return the total number of intervals within the date range if the
     * specified interval type is used.  This method can be used to
     * approximate the number of slices the range will be divided into.
     * If the interval is unknown, the method will return zero.
     *
     * @param   begin   Start of the date range
     * @param   end     End of the date range
     * @param   type    Interval type
     *
     * @return  Number of intervals of this type
     */
    public static int getIntervalCount(Date begin, Date end, int type) {
        switch (type) {
            case HOURLY:    return getElapsedHours(begin, end) + 1;
            case DAILY:     return getElapsedDays(begin, end) + 1;
            case WEEKLY:    return getElapsedWeeks(begin, end) + 1;
            case MONTHLY:   return getElapsedMonths(begin, end) + 1;
            case QUARTERLY: return getElapsedQuarters(begin, end) + 1;
            case YEARLY:    return getElapsedYears(begin, end) + 1;
            default: return 0;
        }
    }

    /**
     * Return the approximate number of years that have elapsed within the 
     * specified date range.  This is only an approximation based upon the
     * standard assumption that there are 1000 * 60 * 60 * 24 * 365 milliseconds in
     * one year.  This does not account for the possibility of leap years
     * and similarly irregular events.
     *
     * @param   begin   Start of the date range
     * @param   end     End of the date range
     */
    public static int getElapsedYears(Date begin, Date end) {
        return (int)((end.getTime() - begin.getTime()) / millisecondsPerYear);
    }

    /**
     * Return the approximate number of quarters that have elapsed within the 
     * specified date range.  There are 4 quarters per year and 13 weeks per quarter.
     * This is only an approximation based upon the
     * standard assumption that there are 1000 * 60 * 60 * 24 * 7 * 13 milliseconds in
     * one quarter.  This does not account for the possibility of leap seconds
     * and similarly irregular events.
     *
     * @param   begin   Start of the date range
     * @param   end     End of the date range
     */
    public static int getElapsedQuarters(Date begin, Date end) {
        return (int)((end.getTime() - begin.getTime()) / millisecondsPerQuarter);
    }


    /**
     * Return the approximate number of months that have elapsed within the 
     * specified date range.  This is only an approximation based upon the
     * standard assumption that there are 1000 * 60 * 60 * 24 * 30 milliseconds in
     * one month.  This does not account for the possibility of leap seconds
     * and similarly irregular events.
     *
     * @param   begin   Start of the date range
     * @param   end     End of the date range
     */
    public static int getElapsedMonths(Date begin, Date end) {
        return (int)((end.getTime() - begin.getTime()) / millisecondsPerMonth);
    }

    /**
     * Return the approximate number of weeks that have elapsed within the 
     * specified date range.  This is only an approximation based upon the
     * standard assumption that there are 1000 * 60 * 60 * 24 * 7 milliseconds in
     * one week.  This does not account for the possibility of leap seconds
     * and similarly irregular events.
     *
     * @param   begin   Start of the date range
     * @param   end     End of the date range
     */
    public static int getElapsedWeeks(Date begin, Date end) {
        return (int)((end.getTime() - begin.getTime()) / millisecondsPerWeek);
    }

    /**
     * Return the approximate number of days that have elapsed within the 
     * specified date range.  This is only an approximation based upon the
     * standard assumption that there are 1000 * 60 * 60 * 24 milliseconds in
     * one day.  This does not account for the possibility of leap seconds
     * and similarly irregular events.
     *
     * @param   begin   Start of the date range
     * @param   end     End of the date range
     */
    public static int getElapsedDays(Date begin, Date end) {
        return (int)((end.getTime() - begin.getTime()) / millisecondsPerDay);
    }


    /**
     * Return the approximate number of hours that have elapsed within the 
     * specified date range.  This is only an approximation based upon the
     * standard assumption that there are 1000 * 60 * 60 milliseconds in
     * one hour.  This does not account for the possibility of leap seconds
     * and similarly irregular events.
     *
     * @param   begin   Start of the date range
     * @param   end     End of the date range
     */
    public static int getElapsedHours(Date begin, Date end) {
        return (int)((end.getTime() - begin.getTime()) / millisecondsPerHour);
    }

    /**
     * Return the approximate number of minutes that have elapsed within the 
     * specified date range.  This is only an approximation based upon the
     * standard assumption that there are 1000 * 60 milliseconds in
     * one minute.  This does not account for the possibility of leap seconds
     * and similarly irregular events.
     *
     * @param   begin   Start of the date range
     * @param   end     End of the date range
     */
    public static int getElapsedMinutes(Date begin, Date end) {
        return (int)((end.getTime() - begin.getTime()) / millisecondsPerMinute);
    }

    /**
     * Return the approximate number of seconds that have elapsed within the 
     * specified date range.  This is only an approximation based upon the
     * standard assumption that there are 1000 milliseconds in
     * one second.  This does not account for the possibility of leap seconds
     * and similarly irregular events.
     *
     * @param   begin   Start of the date range
     * @param   end     End of the date range
     */
    public static int getElapsedSeconds(Date begin, Date end) {
        return (int)((end.getTime() - begin.getTime()) / millisecondsPerSecond);
    }


    /**
     * Returns the beginning and ending date for a particular interval
     * surrounding the specified date.  For example, if the specified
     * date is July 1, 2002 and the interval is yearly, the method would
     * return an array containing the beginning date of January 1, 2002
     * and the ending date of December 31, 2002.
     *
     * @param   target   Date around which the interval is centered
     * @return  An array containing the beginning and ending date of the interval
     */
    public static Date[] getDateInterval(Date target, int interval) {
        Date[] dates = new Date[2];

        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(target);
        int year  = calendar.get(GregorianCalendar.YEAR);
        int month = calendar.get(GregorianCalendar.MONTH);
        int date  = calendar.get(GregorianCalendar.DATE);

        // Start with a calendar interval of ONE day
        GregorianCalendar begin = new GregorianCalendar(year, month, date,  0,  0,  0);
        GregorianCalendar end   = new GregorianCalendar(year, month, date, 23, 59, 59);

        switch (interval) {
            // Expand the interval to include an entire week
            case WEEKLY:
                // Determine how many days to subtract to reach the beginning of the week
                int subDate = begin.get(GregorianCalendar.DAY_OF_WEEK) - begin.getActualMinimum(GregorianCalendar.DAY_OF_WEEK);
                if (subDate > 0) {
                    begin.add(GregorianCalendar.DATE, -(subDate));
                }

                // Determine how many days to add to reach the end of the week
                int addDate = end.getActualMaximum(GregorianCalendar.DAY_OF_WEEK) - end.get(GregorianCalendar.DAY_OF_WEEK);
                if (addDate > 0) {
                    end.add(GregorianCalendar.DATE, addDate);
                }

                break;

            case MONTHLY:
                // Determine the first and last day of the month
                begin.set(GregorianCalendar.DAY_OF_MONTH, begin.getActualMinimum(GregorianCalendar.DAY_OF_MONTH));
                end.set(GregorianCalendar.DAY_OF_MONTH, end.getActualMaximum(GregorianCalendar.DAY_OF_MONTH));
                break;

            case QUARTERLY:
                // Calculate the month that begins the current quarter
                int beginMonth = (month % 4) * 3;
                begin.set(GregorianCalendar.MONTH, beginMonth);
                begin.set(GregorianCalendar.DAY_OF_MONTH, begin.getActualMinimum(GregorianCalendar.DAY_OF_MONTH));

                // Calculate the month that ends the current quarter
                int endMonth   = beginMonth + 2;
                end.set(GregorianCalendar.MONTH, endMonth);
                end.set(GregorianCalendar.DAY_OF_MONTH, end.getActualMaximum(GregorianCalendar.DAY_OF_MONTH));
                break;

            case YEARLY:
                begin.set(GregorianCalendar.MONTH, begin.getActualMinimum(GregorianCalendar.MONTH));
                begin.set(GregorianCalendar.DAY_OF_MONTH, begin.getActualMinimum(GregorianCalendar.DAY_OF_MONTH));
                
                end.set(GregorianCalendar.MONTH, end.getActualMinimum(GregorianCalendar.MONTH));
                end.set(GregorianCalendar.DAY_OF_MONTH, end.getActualMaximum(GregorianCalendar.DAY_OF_MONTH));
                break;
        }

        // Set the date values in the array
        dates[BEGIN_DATE] = begin.getTime();
        dates[END_DATE] = end.getTime();

        return dates;
    }


}
