/*
 * PerformanceTime.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.testing;

import java.util.Date;


/**
 * PerformanceTime maintain performance statistics for a single
 * performance metric being monitored.
 *
 * @version            $Revision: 1.1.1.1 $
 * @author             Shawn Stafford
 *
 */
public class PerformanceTime {

    /** Name of the stat being monitored */
    private String name;

    /** Total number of time entries */
    private int count = 0;

    /** Total time recorded */
    private int time = 0;

    /** Date when the stat was last reset */
    private Date resetDate;

    /**
     * Construct the performance stats with the list of names given.
     *
     * @param   categories  List of categories to monitor
     */
    public PerformanceTime(String name) {
        this.name = name;
        resetDate = new Date();
    }

    /**
     * Resets the query statistics.
     */
    public void reset() {
        count = 0;
        time = 0;
        resetDate = new Date();
    }

    /**
     * Adds the difference between the beginning and ending date 
     * to the existing value.
     *
     * @param   begin   Beginning date
     * @param   end     Ending date
     */
    public void add(Date begin, Date end) {
        int diff = (int)(end.getTime() - begin.getTime());
        time = time + diff;
        count++;
    }

    /**
     * Returns the total number of stat entries.
     *
     * @return  Number of stat entries
     */
    public int getCount() {
        return count;
    }

    /**
     * Returns the total time recorded so far.
     *
     * @return  Total time
     */
    public int getTime() {
        return time;
    }

    /**
     * Returns the average execution time.
     *
     * @return  List of stat values
     */
    public float getAverage() {
        if (count > 0) {
            return (float)time / (float)count;
        } else {
            return 0;
        }
    }

    /**
     * Returns a text summary of the stats.
     *
     * @return  Summary of stat values
     */
    public String getSummary() {
        return "[" + name + "] \tTotal: " + count + " \tElapsed time: " + time + " \tAvg. time: " + getAverage();
    }

    /**
     * Returns the date of the last reset operation.
     *
     * @return  Date when the stats begin
     */
    public Date getResetDate() {
        return resetDate;
    }

}
