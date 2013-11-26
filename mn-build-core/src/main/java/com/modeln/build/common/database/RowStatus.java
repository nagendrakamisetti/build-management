/*
 * RowStatus.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.common.database;


/**
 * The RowStatus object represents data stored in a database.
 * The object maintains syncronization between the object and the
 * database by storing state information about the syncronization
 * status of the data.
 *
 * @version            $Revision: 1.1 $
 * @author             Shawn Stafford
 *
 */
public class RowStatus {

    /** Type of update is UNKNOWN */
    public static final int UNKNOWN_ACTION   = 0;

    /** Row must be added */
    public static final int ADD_ROW = 1;

    /** Row must be deleted */
    public static final int DELETE_ROW = 2;

    /** Row must be updated */
    public static final int UPDATE_ROW = 3;

    /** List of status names */
    public static final String[] ACTION_NAMES = {
        "unknown", "add", "delete", "update"
    };

    /** The row must be added to the database. */
    public static final RowStatus ADD = new RowStatus(ADD_ROW);

    /** The row must be deleted from the database. */
    public static final RowStatus DELETE = new RowStatus(DELETE_ROW);

    /** The row must be updated in the database. */
    public static final RowStatus UPDATE = new RowStatus(UPDATE_ROW);


    /** Row status */
    private int status = UNKNOWN_ACTION;


    /**
     * Construct a row status of the specified action type.  The action
     * type must be one of the values defined by this class.
     */
    public RowStatus(int action) throws IllegalArgumentException {
        setStatus(status);
    }

    /**
     * Construct a row status of the specified type.
     */
    public RowStatus(String action) throws IllegalArgumentException {
        setStatus(status);
    }

    /**
     * Set the row status by specifying the type of action that should
     * be performed on the row..  If the action is not a valid status 
     * value, an exception will be thrown and the status will be set 
     * to UNKNOWN.
     *
     * @param   action      Row action to be performed
     */
    public void setStatus(int action) throws IllegalArgumentException {
        if ((action >= 0) && (action < ACTION_NAMES.length)) {
            status = action;
        } else {
            status = UNKNOWN_ACTION;
            throw new IllegalArgumentException("Invalid row action: " + action);
        }
    }

    /**
     * Set the row status by specifying the type of action that should
     * be performed on the row..  If the action is not a valid status 
     * value, an exception will be thrown and the status will be set 
     * to UNKNOWN.
     *
     * @param   action      Row action to be performed
     */
    public void setStatus(String action) throws IllegalArgumentException {
        status = toInt(action);
    }

    /**
     * Returns the integer value associated with the current status.
     *
     * @return  Action value
     */
    public int toInt() {
        return status;
    }

    /** 
     * Returns the name of the status type.
     *
     * @return  Action name
     */
    public String toString() {
        return ACTION_NAMES[status];
    }

    /**
     * Returns the integer value associated with the action.
     * The value must be one of the action
     * names defined by this method.  If the action name is not 
     * recognized, an exception will be thrown.
     *
     * @param   action   Action name
     * @return  Action type
     * @throws  IllegalArgumentException if the action name is unrecognized
     */
    public static int toInt(String action) throws IllegalArgumentException {
        for (int idx = 0; idx < ACTION_NAMES.length; idx++) {
            if (ACTION_NAMES[idx].equalsIgnoreCase(action)) {
                return idx;
            }
        }

        // A priority name has not been found
        throw new IllegalArgumentException("Invalid row action: " + action);
    }

    /** 
     * Returns the name of the action type.
     *
     * @param   action   Action name
     * @return  Action type
     * @throws  IllegalArgumentException if the action name is unrecognized
     */
    public static String toString(int action) throws IllegalArgumentException {
        if ((action >= 0) && (action < ACTION_NAMES.length)) {
            return ACTION_NAMES[action];
        } else {
            throw new IllegalArgumentException("Invalid row action: " + action);
        }
    }

}
