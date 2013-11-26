/*
 * SecureAppender.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.common.logging;

import org.apache.log4j.Appender;

/**
 * Extends the functional requirements of Appender to include the use of
 * security levels during log reporting.
 *
 * @version            $Revision: 1.1.1.1 $
 * @author             Shawn Stafford
 */
public interface SecureAppender extends Appender {

    /**
     * Sets the security level for the appender.  When a SecureCategory 
     * class attempts to write to an appender, it should only append 
     * messages to appenders which are at least as secure as the security
     * level assigned to the current message.
     *
     * @param   SecurityLevel     Security level
     */
    public void setSecurityLevel(SecurityLevel level);

    /**
     * Returns the current security level for this appender.
     *
     * @return  SecurityLevel Security level
     */
    public SecurityLevel getSecurityLevel();

}
