/*
 * BufferedDataInterface.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.common.data;


/**
 * The BufferedDataInterface is an object abstraction which allows classes
 * to identify objects which have been modified or which need to be
 * protected from modification.
 *
 * @version            $Revision: 1.1.1.1 $
 * @author             Shawn Stafford
 *
 */
public interface BufferedDataInterface {

    /**
     * Determines if the object has been modified since created
     * or flushed.
     *
     * @return  TRUE if the object has been modified, FALSE otherwise
     */
    public boolean isDirty();

    /**
     * Marks the object as clean provided that no lock is in place.
     */
    public void flush();

    /**
     * Prevents the data object from being modified.
     */
    public void lock();

    /**
     * Unlocks the data object so it can be modified or flushed.
     */
    public void unlock();
}
