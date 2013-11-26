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
 * This class represents information about a single query
 * execution. 
 *
 * @author  Shawn Stafford
 */
public class CMnQueryData {

    /** Unique identifier for the query */
    private String queryId;

    /** Amount of time spent executing the query (in milliseconds) */
    private long elapsedTime = 0;


    /**
     * Set the query identifier which can be used to identify
     * an individual query later.  This can be as simple as a 
     * counter value to identify the specific execution of a 
     * known query or it can be something used to uniquely
     * identify the query across all executed queries.
     *
     * @param   id    Query identifier
     */
    public void setId(String id) {
        queryId = id;
    }

    /**
     * Return the query identifier.
     *
     * @return  Query identifier
     */
    public String getId() {
        return queryId;
    }

    /**
     * Set the query time (in milliseconds).
     */
    public void setTime(long time) {
        elapsedTime = time;
    }

    /**
     * Return the query time (in milliseconds).
     */
    public long getTime() {
        return elapsedTime;
    }

}

