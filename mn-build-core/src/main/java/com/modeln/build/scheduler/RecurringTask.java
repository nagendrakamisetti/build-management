/*
 * RecurringTask.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.scheduler;

import java.util.Date;
import java.util.GregorianCalendar;

/**
 * A RecurringTask is an event that is scheduled to occur at regular intervals.
 * 
 * @version            $Revision: 1.1 $  
 * @author             Shawn Stafford
 */
public class RecurringTask extends Task {

    /** Minutes at which the task should execute */
    private int[] minutes;

    /** Hours at which the task should execute */
    private int[] hours;

    /** Days of the week on which the task should execute */
    private int[] daysPerWeek;

    /** Days of the month on which the task should execute */
    private int[] daysPerMonth;

    /** Months of the year on which the task should execute */
    private int[] months;


    /** Calendar used to schedule recurring tasks */
    private static final GregorianCalendar calendar = new GregorianCalendar();

    /** Date when the task last executed */
    private Date lastOccurance;


    /**
     * Construct a recurring task.
     * 
     * @param   name    Identifies the task by name
     */
    public RecurringTask(String name) {
        super(name);
    }

    /** 
     * Construct a recurring task that will execute as part of a specified
     * thread group.
     *
     * @param   name    Identifies the task by name
     * @param   group   Thread group to which the task belongs
     */
    public RecurringTask(String name, ThreadGroup group) {
        super(name, group);
    }

    /**
     * Perform the task.  This method simply sets the date of the last
     * occurrence to the current date.  Classes which extend this class
     * should override this method to do something useful, make sure to
     * call super so that the last occurrence is still recorded.
     *
     */
    public void run() {
        super.run();
        lastOccurance = new Date();
    }

    /**
     * Determine the date and time at which the task should be executed
     * relative to the date and time provided.
     *
     * @param   from    Date and time from which the next occurance is determined
     *
     * @return  A specific instance of a task
     */
    public ScheduledTask getNextOccurrence(Date from) {
        ScheduledTask task = null;

        calendar.setTime(from);

        // Calculate the next minute and roll forward if it has already passed
        int fromMinute = calendar.get(GregorianCalendar.MINUTE);
        int nextMinute = getNextMinute(fromMinute);
        if (fromMinute < nextMinute) {
            calendar.set(GregorianCalendar.MINUTE, nextMinute);
        } else {
            calendar.set(GregorianCalendar.MINUTE, fromMinute);
            calendar.add(GregorianCalendar.HOUR, 1);
        }
        
        // Calculate the next hour and roll forward if it has already passed
        int fromHour = calendar.get(GregorianCalendar.HOUR);
        int nextHour = getNextHour(fromHour);
        if (fromHour < nextHour) {
            calendar.set(GregorianCalendar.HOUR, nextHour);
        } else {
            calendar.set(GregorianCalendar.HOUR, fromHour);
            calendar.add(GregorianCalendar.DATE, 1);
        }

        // Calculate the next date and roll forward if it has already passed
        int fromDay = calendar.get(GregorianCalendar.DATE);
        int nextDay = getNextDate(fromDay);
        if (fromDay < nextDay) {
            calendar.set(GregorianCalendar.DATE, nextDay);
        } else {
            calendar.set(GregorianCalendar.DATE, fromDay);
            calendar.add(GregorianCalendar.MONTH, 1);
        }
        
        // Calculate the next month and roll forward if it has already passed
        int fromMonth = calendar.get(GregorianCalendar.MONTH);
        int nextMonth = getNextMonth(fromMonth);
        if (fromMonth < nextMonth) {
            calendar.set(GregorianCalendar.MONTH, nextMonth);
        } else {
            calendar.set(GregorianCalendar.MONTH, fromMonth);
            calendar.add(GregorianCalendar.YEAR, 1);
        }

        // The day of the week is sort of quirky and must be treated as
        // a special case.  The current implementation isn't correct because
        // it performs a logical OR between DayOfWeek, Month, and Date.
        // The correct implementation should use a logical AND.
        int fromDayOfWeek = calendar.get(GregorianCalendar.DAY_OF_WEEK);
        int nextDayOfWeek = getNextWeekDay(fromDayOfWeek);
        if (fromDayOfWeek < nextDayOfWeek) {
            // Roll forward to make up the difference in days
            // This isn't really the correct way of doing this....
            calendar.add(GregorianCalendar.DATE, nextDayOfWeek - fromDayOfWeek);
        } else if (fromDayOfWeek > nextDayOfWeek) {
            // Roll forward one week
            // This isn't really the correct way of doing this....
            calendar.add(GregorianCalendar.WEEK_OF_YEAR, 1);
        }


        Date next = calendar.getTime();
        if (next.after(from)) {
            task = new ScheduledTask(getName(), getThreadGroup());
            task.setSchedule(next);
        } else {
            // Task occurs in the past
            task = new ScheduledTask(getName(), getThreadGroup());
            task.setSchedule(from);
        }

        return task;
    }

    /**
     * Return the minute at which the next task occurrance should be scheduled.
     * If the task recurrance is not dependent upon this field, the value
     * provided will be returned.
     *
     * @param   from    Current minute
     */
    private int getNextMinute(int from) {
        if ((minutes != null) && (minutes.length > 0)) {
            for (int idx = 0; idx < minutes.length; idx++) {
                if (minutes[idx] > from) {
                    return minutes[idx];
                }
            }

            // Wrap around to the first entry if we went past the end
            return minutes[0];
        }

        return from;
    }

    /**
     * Return the hour at which the next task occurrance should be scheduled.
     * If the task recurrance is not dependent upon this field, the value
     * provided will be returned.
     *
     * @param   from    Current hour
     */
    private int getNextHour(int from) {
        if ((hours != null) && (hours.length > 0)) {
            for (int idx = 0; idx < hours.length; idx++) {
                if (hours[idx] > from) {
                    return hours[idx];
                }
            }

            // Wrap around to the first entry if we went past the end
            return hours[0];
        }

        return from;
    }

    /**
     * Return the date at which the next task occurrance should be scheduled.
     * If the task recurrance is not dependent upon this field, the value
     * provided will be returned.
     *
     * @param   from    Current day of the month
     */
    private int getNextDate(int from) {
        if ((daysPerMonth != null) && (daysPerMonth.length > 0)) {
            for (int idx = 0; idx < daysPerMonth.length; idx++) {
                if (daysPerMonth[idx] > from) {
                    return daysPerMonth[idx];
                }
            }

            // Wrap around to the first entry if we went past the end
            return daysPerMonth[0];
        }

        return from;
    }

    /**
     * Return the day of the week at which the next task occurrance should be 
     * scheduled.  If the task recurrance is not dependent upon this field, 
     * the value provided will be returned.
     *
     * @param   from    Current day of the week
     */
    private int getNextWeekDay(int from) {
        if ((daysPerWeek != null) && (daysPerWeek.length > 0)) {
            for (int idx = 0; idx < daysPerWeek.length; idx++) {
                if (daysPerWeek[idx] > from) {
                    return daysPerWeek[idx];
                }
            }

            // Wrap around to the first entry if we went past the end
            return daysPerWeek[0];
        }

        return from;
    }

    /**
     * Return the month at which the next task occurrance should be scheduled.
     * If the task recurrance is not dependent upon this field, the value
     * provided will be returned.
     *
     * @param   from    Current month
     */
    private int getNextMonth(int from) {
        if ((months != null) && (months.length > 0)) {
            for (int idx = 0; idx < months.length; idx++) {
                if (months[idx] > from) {
                    return months[idx];
                }
            }

            // Wrap around to the first entry if we went past the end
            return months[0];
        }

        return from;
    }

}
