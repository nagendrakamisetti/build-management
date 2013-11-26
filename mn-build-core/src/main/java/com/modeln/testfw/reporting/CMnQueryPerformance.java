/*
* Copyright 2000-2003 by Model N, Inc.  All Rights Reserved.
*
* This software is the confidential and proprietary information
* of Model N, Inc ("Confidential Information").  You shall not
* disclose such Confidential Information and shall use it only
* in accordance with the terms of the license agreement you
* entered into with Model N, Inc.
*/
package com.modeln.testfw.reporting;


/**
 * This class represents a performance metric used to measure the
 * performance characteristics of a particular query. 
 *
 * @author  Shawn Stafford
 */
public class CMnQueryPerformance {

    /** Record the total time spent executing the named queries */
    private long totalTime = 0;

    /** Record the query with the shortest execution time */
    private CMnQueryData minTime = null;

    /** Record the query with the longest execution time */
    private CMnQueryData maxTime = null;

    /** Record the average query exection time (in milliseconds) */
    private long avgTime = 0;

    /** Number of times the query has been executed */
    private int queryCount = 0;

    /**
     * Set the execution time of the latest query.  This will increment
     * the query count and update the min, max, and average time values.
     *
     * @param   time    Query execution time
     */
    public void add(CMnQueryData query) {
        if (query != null) {

            // Check to see if the current query exceeds the previous maximum
            if ((maxTime == null) || (query.getTime() > maxTime.getTime())) {
                maxTime = query;
            }

            // Check to see if the current query is less than the previous minimum
            if ((minTime == null) || (query.getTime() < minTime.getTime())) {
                minTime = query;
            }

            // Calculate the total time spent executing queries
            totalTime = totalTime + query.getTime();

            // Calculate the average query execution time
            avgTime = (avgTime + query.getTime()) / 2; 

            // Increase the query count
            queryCount++;

        }
    }

    /**
     * Return the minimum query time.
     */
    public CMnQueryData getMinTime() {
        return minTime;
    }

    /**
     * Return the maximum query time.
     */
    public CMnQueryData getMaxTime() {
        return maxTime;
    }

    /**
     * Return the total time spent executing queries.
     */
    public long getTotalTime() {
        return totalTime;
    }

    /**
     * Return the average query time.
     */
    public long getAvgTime() {
        return avgTime;
    }

    /**
     * Return the number of times the query has executed.
     */
    public int getQueryCount() {
        return queryCount;
    }

    /**
     * Return a string representing the performance data.
     *
     * @return   Performance information
     */
    public String toString() {
        return "min=" + minTime.getTime() + "ms (ID#" + minTime.getId() + "), " +
               "\tmax=" + maxTime.getTime() + "ms (ID#" + maxTime.getId() + "), " +
               "\tavg=" + avgTime + " ms, " + 
               "\ttotal=" + totalTime + " ms, " +
               "\tcount=" + queryCount;
    }


}

