/*
 * TaskPriority.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.scheduler;


/**
 * The TaskPriority class is used in scheduling applications to assign a 
 * relative priority to tasks.  This object is have very little 
 * application logic, but is used to provide type protection.  The 
 * toString and toInt methods can be used to store the priority in
 * a database or file for reuse.
 *
 * @version            $Revision: 1.1 $
 * @author             Shawn Stafford
 *
 */
public class TaskPriority implements Comparable {

    /**
     * Highest possible priority value.  This should generally be reserved 
     * for time-critical execution.
     */
    public static final int HIGHEST_PRIORITY = 0;

    /** 
     * High priority, but not time-critical.  Systems should assume that
     * tasks of this priority will be executed as soon as possible.
     */
    public static final int HIGH_PRIORITY = 1;

    /** 
     * Medium priority tasks will execute once all of the higher priorty
     * tasks have been processed.
     */
    public static final int MEDIUM_PRIORITY = 2;

    /** 
     * Low priority tasks will execute only when all of the higher priority
     * tasks have been processed.
     */
    public static final int LOW_PRIORITY = 3;

    /**
     * Lowest possible priority value.  This should generally be reserved
     * for tasks that are not important because they risk starvation if 
     * the scheduler becomes heavily burdened with tasks.  This priority
     * should generally be reserved for background tasks.
     */
    public static final int LOWEST_PRIORITY = 4;

    /**
     * List of priority names.
     */
    private static final String[] PRIORITY_NAMES = {
        "critical", "high", "medium", "low", "lowest"
    };


    /** 
     * The task has a the highest possible priority and should be performed 
     * immediately. 
     */
    public static final TaskPriority CRITICAL = new TaskPriority(HIGHEST_PRIORITY);


    /** 
     * The task has a high priority and should be performed before lower 
     * priority tasks. 
     */
    public static final TaskPriority HIGH = new TaskPriority(HIGH_PRIORITY);

    /** 
     * The task has a medium priority and should defer to higher priority tasks
     * but prempt lower priority tasks.
     */
    public static final TaskPriority MEDIUM = new TaskPriority(MEDIUM_PRIORITY);

    /** 
     * The task has a low priority and should only be executed when 
     * all higher priority tasks have been processed.
     */
    public static final TaskPriority LOW = new TaskPriority(LOW_PRIORITY);



    /** Priority level of this object */
    private int priority = HIGH_PRIORITY;


    /**
     * Construct a task priority.  The priority level must be between 
     * 0 and 255, where 0 is considered the highest priority.  
     * It is prefered that the value be set using the named values provided
     * by this class to ensure proper use and prioritization.
     *
     * @param   level   Priority level (0-255)
     * @throws  IllegalArgumentException if the priority level is unrecognized
     */
    public TaskPriority(int level) throws IllegalArgumentException {
        setPriority(level);
    }

    /**
     * Construct a task priority.  The priority level must correspond to
     * one of the priority names defined in this class.
     *
     * @param   name   Priority name
     * @throws  IllegalArgumentException if the priority level is unrecognized
     */
    public TaskPriority(String name) throws IllegalArgumentException {
        setPriority(name);
    }


    /**
     * Sets the priority level.  This value must be between 0 and 255.
     * It is prefered that the value be set using the named values provided
     * by this class to ensure proper use and prioritization.
     *
     * @param   level   Priority level
     * @throws  IllegalArgumentException if the priority level is unrecognized
     */
    public void setPriority(int level) throws IllegalArgumentException {
        if ((level >= 0) && (level < PRIORITY_NAMES.length)) {
            priority = level;
        } else {
            throw new IllegalArgumentException("Invalid priority: " + level);
        }
    }


    /** 
     * Sets the priority level.  The value must be one of the priority
     * names defined by this method.  If the priority name is not 
     * recognized, an exception will be thrown.
     *
     * @param   level   Priority level
     * @throws  IllegalArgumentException if the priority level is unrecognized
     */
    public void setPriority(String level) throws IllegalArgumentException {
        priority = toInt(level);
    }

    /**
     * Returns the integer value associated with the priority.
     *
     * @return  Priority level
     */
    public int toInt() {
        return priority;
    }

    /** 
     * Returns the name of the priority level.
     *
     * @return  Priority name
     */
    public String toString() {
        return PRIORITY_NAMES[priority];
    }

    /**
     * Compares this task priority to the specified task priority.  This
     * method is mean to be used by classes that require a Comparable interface.
     * Returns a negative integer, zero, or a positive integer as this object 
     * is less than, equal to, or greater than the specified object.
     *
     * @param   o   Priority against which the current priority will be compared
     * @return  -1 if less than, 0 if equal, 1 if greater than
     * @throws  ClassCastException if the specified object is not a priority
     */
    public int compareTo(Object o) throws ClassCastException {
        if (priority > ((TaskPriority)o).toInt()) {
            return 1;
        } else if (priority < ((TaskPriority)o).toInt()) {
            return -1;
        } else {
            return 0;
        }
    }

    /**
     * Returns true of the current priority level is greater than the
     * specified priority level.
     *
     * @param   level   Priority level to compare to
     * @return  TRUE if the current priority level is greater
     */
    public boolean greaterThan(String level) throws IllegalArgumentException {
        return (priority > toInt(level));
    }

    /**
     * Returns true of the current priority level is less than the
     * specified priority level.
     *
     * @param   level   Priority level to compare to
     * @return  TRUE if the current priority level is less
     */
    public boolean lessThan(String level) throws IllegalArgumentException {
        return (priority < toInt(level));
    }

    /**
     * Returns true of the current priority level is equal to the
     * specified priority level.
     *
     * @param   level   Priority level to compare to
     * @return  TRUE if the current priority level is equal
     */
    public boolean equalTo(String level) throws IllegalArgumentException {
        return (priority == toInt(level));
    }

    /**
     * Returns the integer value associated with the priority.
     * The value must be one of the priority
     * names defined by this method.  If the priority name is not 
     * recognized, an exception will be thrown.
     *
     * @param   level   Priority level
     * @return  Priority level
     * @throws  IllegalArgumentException if the priority level is unrecognized
     */
    public static int toInt(String level) throws IllegalArgumentException {
        for (int idx = 0; idx < PRIORITY_NAMES.length; idx++) {
            if (PRIORITY_NAMES[idx].equalsIgnoreCase(level)) {
                return idx;
            }
        }

        // A priority name has not been found
        throw new IllegalArgumentException("Invalid priority: " + level);
    }

    /** 
     * Returns the name of the priority level.
     *
     * @param   level   Priority level
     * @return  Priority name
     * @throws  IllegalArgumentException if the priority level is unrecognized
     */
    public static String toString(int level) throws IllegalArgumentException {
        if ((level >= 0) && (level < PRIORITY_NAMES.length)) {
            return PRIORITY_NAMES[level];
        } else {
            throw new IllegalArgumentException("Invalid priority: " + level);
        }
    }

}
