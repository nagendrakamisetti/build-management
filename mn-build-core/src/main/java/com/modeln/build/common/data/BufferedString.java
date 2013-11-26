/*
 * BufferedString.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.common.data;


/**
 * The BufferedString is an object abstraction which allows classes
 * to identify strings which have been modified or which need to be
 * protected from modification.
 *
 * @version            $Revision: 1.1.1.1 $
 * @author             Shawn Stafford
 *
 */
public class BufferedString implements BufferedDataInterface {

    /** Data to be buffered and managed by the object. */
    private String data;
    
    /** If locked, the data cannot be updated until the lock is removed. */
    private boolean locked = false;

    /** A dirty object has been updated since the last object flush. */
    private boolean dirty = false;

    /**
     * Construct a buffered object from the given data.  The object
     * is cloned during construction to ensure that it cannot be
     * modified outside of the BufferedObject class.
     *
     * @param   str     Data to be stored
     */
    public BufferedString(String str) {
        data = new String(str);
    }

    /**
     * Updates the data object with a new value.
     * If the object is not locked and has been updated
     * sucessfully, then the method returns true.
     *
     * @param   str     New data
     * @param   TRUE if the data is updated, FALSE otherwise
     */
    public boolean setValue(String str) {
        if (!locked) {
            data = new String(str);
            dirty = true;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns a copy of the data object.
     */
    public String getValue() {
        return new String(data);
    }

    /**
     * Determines if the object has been modified since created
     * or flushed.
     *
     * @return  TRUE if the object has been modified, FALSE otherwise
     */
    public boolean isDirty() {
        return dirty;
    }

    /**
     * Marks the object as clean provided that no lock is in place.
     */
    public void flush() {
        if (!locked) dirty = false;
    }

    /**
     * Prevents the data object from being modified.
     */
    public void lock() {
        locked = true;
    }

    /**
     * Unlocks the data object so it can be modified or flushed.
     */
    public void unlock() {
        locked = false;
    }
}
