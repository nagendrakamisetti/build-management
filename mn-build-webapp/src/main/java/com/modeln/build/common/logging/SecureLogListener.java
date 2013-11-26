/*
 * SecureLogListener.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.common.logging;

/**
 * This interface defines the methods which should be made available by any
 * class which will be registered as a log listener.
 *
 * @version            $Revision: 1.1.1.1 $
 * @author             Shawn Stafford
 *
 */
public interface SecureLogListener {

    /**
     * Updates the instance of the log with a new copy.
     *
     * @param   newLog      New copy of the log object
     */
    public void updateLog(SecureLog newLog);
}
