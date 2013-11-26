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

import java.util.Date;

/**
 * Data object used to represent a host environment that is used to execute  
 * remote actions.
 * 
 * @author  Shawn Stafford
 */
public class CMnDbHostActionData {

    /** Unique identifier for the action */
    private int actionId;

    /** Name of the host computer */
    private String hostname;

    /** Type of host system */
    private String actionType;

    /** Date when the action should be performed */
    private Date targetDate;

    /** Order in which actions should be executed */
    private int actionOrder = 0;
    
    /** Action to be performed */
    private String actionName;
    
    /** Action arguments */
    private String actionArgs;
    

    /**
     * Set the unique identifier for the action.  The ID is typically used
     * as a primary key in the database.
     *
     * @param  id   Unique identifier
     */
    public void setId(int id) {
        actionId = id;
    }

    /**
     * Return the unique identifier for the action. 
     *
     * @return  Type of host
     */
    public int getId() {
        return actionId;
    }

    /**
     * Set the host on which the action was performed.
     *
     * @param  host   Name of the host computer
     */
    public void setHostname(String host) {
        hostname = host;
    }

    /**
     * Return the name of the host on which the action was performed.
     *
     * @return  Hostname
     */
    public String getHostname() {
        return hostname;
    }


    /**
     * Set the type of action to be performed.
     *
     * @param  type   Type of action 
     */
    public void setType(String type) {
        actionType = type;
    }

    /**
     * Return the type of action to be performed. 
     *
     * @return  Type of action
     */
    public String getType() {
        return actionType;
    }
    
    /**
     * Set the date when the action should be performed.
     *
     * @param  date   Execution date 
     */
    public void setTargetDate(Date date) {
        targetDate = date;
    }

    /**
     * Return the date when the action should be performed. 
     *
     * @return  Date of execution
     */
    public Date getTargetDate() {
        return targetDate;
    }

    /**
     * Set the priority in which the action is to be performed if there are
     * multiple actions with the same execution date.  Actions with a value
     * closer to 0 will be executed before actions with a higher order value.
     * 
     * @param  id   Unique identifier
     */
    public void setOrder(int order) {
        actionOrder = order;
    }

    /**
     * Return the order in which the action should be performed if there are
     * multiple actions with the same execution date. 
     *
     * @return  Type of host
     */
    public int getOrder() {
        return actionOrder;
    }

    /**
     * Set the name of the action to be performed.
     *
     * @param  name   Action to be performed
     */
    public void setName(String name) {
        actionName = name;
    }

    /**
     * Return the name of the action to be performed.
     *
     * @return  Action name
     */
    public String getName() {
        return actionName;
    }

    /**
     * Set additional arguments to be passed in when executing the action.
     *
     * @param  args  Action arguments
     */
    public void setArguments(String args) {
        actionArgs = args;
    }

    /**
     * Return the list of additional arguments to pass to the action.
     *
     * @return  Action arguments
     */
    public String getArguments() {
        return actionArgs;
    }

}
