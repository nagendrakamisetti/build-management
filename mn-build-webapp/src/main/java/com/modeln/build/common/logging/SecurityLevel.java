/*
 * SecurityLevel.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.common.logging;

/**
 * Security levels control which log messages can be written to which
 * log destinations.  
 *
 * @version            $Revision: 1.1.1.1 $
 * @author             Shawn Stafford
 *
 */
public class SecurityLevel {

    /** Security level for this instance. */
    protected int level;

    /** String representation of this security level. */
    protected String levelStr;

    /**
     * Security level 4 (highest security).
     * The appender can only write information to a destination which is
     * local to the current machine (ie no socket IO or disk writes to a 
     * foreign host).
     */
    protected static final int      LOCAL_ONLY_INT  = 4000;
    protected static final String   LOCAL_ONLY_STR  = "LOCAL_ONLY";

    /**
     * Security level 3 (moderate-to-high security).
     * The appender can write to any secure socket.  This level of security
     * is slightly more relaxed that LOCAL_ONLY.
     */
    protected static final int      SSL_INT = 3000;
    protected static final String   SSL_STR = "SSL";

    /**
     * Security level 2 (moderate security).
     * The appender can only write to an authenticated connection.
     */
    protected static final int      AUTHENTICATED_INT = 2000;
    protected static final String   AUTHENTICATED_STR = "AUTHENTICATED";

    /**
     * Security level 1 (minimum security).
     * The appender can write to any location.
     */
    protected static final int      UNSECURED_INT   = 1000;
    protected static final String   UNSECURED_STR   = "UNSECURED";


    /**
     * Information at this level can be written to any location.
     */
    public static final SecurityLevel UNSECURED = new SecurityLevel(UNSECURED_INT, UNSECURED_STR);

    /**
     * It is assumed that information at this level is being written to a
     * destination which requires user authentication before it can be accessed.
     */
    public static final SecurityLevel AUTHENTICATED = new SecurityLevel(AUTHENTICATED_INT, AUTHENTICATED_STR);

    /**
     * It is assumed that information at this level is being written to an
     * SSL or similarly secure destination.
     */
    public static final SecurityLevel SSL = new SecurityLevel(SSL_INT, SSL_STR);

    /**
     * It is assumed that information at this level is being written to a
     * local destination (ie not transmitted across a network connection).
     */
    public static final SecurityLevel LOCAL_ONLY = new SecurityLevel(LOCAL_ONLY_INT, LOCAL_ONLY_STR);

    /**
     * Constructs a security level object of the given type.
     */
    protected SecurityLevel(int level, String levelStr) {
        level = this.level;
        levelStr = this.levelStr;
    }


    /**
     * Returns all possible security levels as an array of SecurityLevel objects,
     * ordered in ascending order (least secure first).
     *
     * @return SecurityLevel[] array of all possible security levels
     */
    public static SecurityLevel[] getAllPossibleLevels() {
        return new SecurityLevel[] {
            SecurityLevel.UNSECURED,
            SecurityLevel.AUTHENTICATED,
            SecurityLevel.SSL,
            SecurityLevel.LOCAL_ONLY
        };
    }


    /**
     * Converts the given integer value to a SecurityLevel or returns null if
     * it doesn't match any of the defined levels.
     *
     * @param   int     val
     * @return  SecurityLevel assigned to that integer
     */
    public static SecurityLevel toSecurityLevel(int val) {
        switch(val) {
            case UNSECURED_INT:     return UNSECURED;
            case AUTHENTICATED_INT: return AUTHENTICATED;
            case SSL_INT:           return SSL;
            case LOCAL_ONLY_INT:    return LOCAL_ONLY;
            default: return null;
        }
    }


    /**
     * Converts the given String value to a SecurityLevel or returns null if
     * it doesn't match any of the defined levels.
     *
     * @param   int     val
     * @return  SecurityLevel assigned to that integer
     */
    public static SecurityLevel toSecurityLevel(String val) {
        if (val.equals(UNSECURED_STR))      return UNSECURED;
        if (val.equals(AUTHENTICATED_STR))  return AUTHENTICATED;
        if (val.equals(SSL_STR))            return SSL;
        if (val.equals(LOCAL_ONLY_STR))     return LOCAL_ONLY;
        return null;
    }

    /**
     * Determines if the given SecurityLevel is greater than or equal to this 
     * security level.
     *
     * @param   level   Security level to compare to
     * @return  boolean FALSE if the given level is less than this, TRUE otherwise
     */
    public boolean isGreaterOrEqual(SecurityLevel level) {
        if (this.level >= level.toInt()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Determines if the given SecurityLevel is greater than this 
     * security level.
     *
     * @param   level   Security level to compare to
     * @return  boolean FALSE if the given level is less than  or equal to this, TRUE otherwise
     */
    public boolean isGreaterThan(SecurityLevel level) {
        if (this.level > level.toInt()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Determines if the given SecurityLevel is equal to this security level.
     *
     * @param   level   Security level to compare to
     * @return  boolean TRUE if the levels are equal
     */
    public boolean equals(SecurityLevel level) {
        if (this.level == level.toInt()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns the integer value assigned to this level.
     *
     * @return integer value for this level
     */
    public int toInt() {
        return this.level;
    }

    /**
     * Returns a string representation of the SecurityLevel.
     *
     * @return String representing the SecurityLevel
     */
    public String toString() {
        return this.levelStr;
    }


}
