/*
 * SecureCategoryFactory.java
 *
 * Copyright 2006 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.common.logging;

import org.apache.log4j.*;
import org.apache.log4j.spi.*;
import java.util.*;

/**
 * A SecureCategoryFactory provides methods for creating a secure logger. 
 *
 * @version            $Revision: 1.1.1.1 $
 * @author             Shawn Stafford
 *
 */
public class SecureCategoryFactory implements LoggerFactory {

    /**
     * The constructor should be public as it will be called by
     * configurators in different packages.  
     */
    public SecureCategoryFactory() {
    }

    public Logger makeNewLoggerInstance(String name) {
        return new SecureCategory(name);
    }


}

