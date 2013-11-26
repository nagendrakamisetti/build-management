/*
 * SecureLoggingEvent.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.common.logging;

import org.apache.log4j.*;
import org.apache.log4j.spi.*;

/**
 * A SecureLoggingEvent contains information about the security level of an
 * event, which is used to determine the security level of the event.
 *
 * @version            $Revision: 1.1.1.1 $
 * @author             Shawn Stafford
 *
 */
public class SecureLoggingEvent extends LoggingEvent {

    /** Security level for this event. */
	public SecurityLevel securityLevel;

    /**
     * Constructs a secure logging event which contains the security level of
     * the event.
     *
     * @param   fqnOfCategoryClass  Fully qualified Category class name
     * @param   category  Category managing the log access for the event
     * @param   priority    Event priority level
     * @param   security    Event security level
     * @param   message     Event message to write to the log
     * @param   throwable   Exception to write to the log
     */
    public SecureLoggingEvent(
        String          fqnOfCategoryClass, 
        Category        category, 
        Priority        priority, 
        SecurityLevel   security, 
        String          message, 
        Throwable       throwable)
    {
        super(fqnOfCategoryClass, category, priority, message, throwable);
        securityLevel = security;
    }

}
