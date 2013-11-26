/*
 * ErrorMap.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.web.errors;

/**
 * The ErrorMap class defines a generic list of error codes which will
 * be common to all applications.  Application-specific error codes should
 * be defined in seperate classes which extend this base class.
 * <p>
 *
 * <table>
 *   <tr><td>General application error message range:</td><td>100 - 199</td></tr>
 *   <tr><td>General database error message range:</td><td>200 - 299</td></tr>
 *   <tr><td>Account related error message range:</td><td>300 - 399</td></tr>
 *   <tr><td>Service related error messages range:</td><td>400 - 499</td></tr>
 * </table>
 *
 * @version            $Revision: 1.1.1.1 $
 * @author             Shawn Stafford
 *
 */
public class ErrorMap {

    /** No error has occurred */
    public static final int NO_ERROR = 0;


    // ------------------------------------------------------------------------
    // General application error message range: 100 - 199
    //
    // Application errors denote problems which occur within the core 
    // application framework.  This may be things such as null pointers,
    // internal program errors, etc.
    //
    // Please follow this guideline when creating new errors

    /** An unexpected error has occurred and cannot be diagnosed further. */
    public static final int UNKNOWN_ERROR = 101;

    /** An internal application error includes exceptions and application faults */
    public static final int INTERNAL_ERROR = 102;

    /** The requested application does not exist */
    public static final int INVALID_APPLICATION = 103;

    /** The requested command does not exist */
    public static final int INVALID_COMMAND = 104;

    /** The application has become temporarily unavailable to users */
    public static final int APPLICATION_UNAVAILABLE = 105;

    /** The command has become temporarily unavailable to users */
    public static final int COMMAND_UNAVAILABLE = 106;

    /** The application has allowed the user to enter an invalid operation. */
    public static final int INVALID_APPLICATION_OPERATION = 107;

    /** Server failed to redirect the user to the correct web page. */
    public static final int SERVER_REDIRECTION_FAILED = 108;

    /** An application configuration file is missing a required configuration setting. */
    public static final int MISSING_CONFIGURATION_ENTRY = 109;
    
    /** An application configuration setting is invalid. */
    public static final int INVALID_CONFIGURATION_ENTRY = 110;

    /** An application configuration file is missing a required error string. */
    public static final int MISSING_ERROR_STRING = 111;
    
    /** An application error string is invalid. */
    public static final int INVALID_ERROR_STRING = 112;

    /** An application string file is missing a required text string. */
    public static final int MISSING_TEXT_STRING = 113;

    /** An application string file contains an invalid text string. */
    public static final int INVALID_TEXT_STRING = 114;

    /** The application is unable to display a server response to the user. */
    public static final int APPLICATION_DISPLAY_FAILURE = 115;


    // ------------------------------------------------------------------------
    // General database error message range: 200 - 299
    //
    // Repository errors are generic to all repositories, and include cases
    // where connection problems occur.
    //
    // Please follow this guideline when creating new errors

    /** A database connection cannot be established */
    public static final int DATABASE_UNAVAILABLE = 201;

    /** Indicates that no data was returned from the database */
    public static final int NO_DATABASE_DATA_RETURNED = 202;

    /** Inidicates that the database returned an incorrectly formatted response */
    public static final int INVALID_DATABASE_RESPONSE = 203;

    /** Database contains invalid or incorrect data */
    public static final int INVALID_DATABASE_DATA = 204;

    /** The request submitted to the database contained invalid data */
    public static final int INVALID_DATABASE_REQUEST = 205;

    /** The request submitted to the database contained an unknown error */
    public static final int UNKNOWN_DATABASE_ERROR = 206;

    /** The application data is not consistent with the database data. */
    public static final int DATA_INCONSISTENCY = 207;

    /** The database failed to complete the transaction. */
    public static final int DATABASE_TRANSACTION_FAILURE = 208;

    /** 
     * The database failed to complete the transaction, but may have 
     * performed some actions which cannot be rolled back. 
     */
    public static final int PARTIAL_TRANSACTION_FAILURE = 209;

    /** Database off-line to prevent customer access to data. */
    public static final int DATABASE_OFFLINE = 221;
     

    // ------------------------------------------------------------------------
    // Account related error message range: 300 - 399
    //
    // Account related errors include login errors, username and password
    // errors, and common account-level errors.
    //
    // Please follow this guideline when creating new errors

    /** The user does not have an account */
    public static final int UNKNOWN_USER = 301;

    /** The password does not match the one associated with the account */
    public static final int INCORRECT_PASSWORD = 302;

    /** The user must be logged on to access this resource. */
    public static final int NOT_LOGGED_ON = 303;

    /** The resource can only be accessed by a group administrator account. */
    public static final int NOT_GROUP_ADMIN = 304;
    
    /** This account has been disabled. */
    public static final int DISABLED_ACCOUNT = 305;

    /** The timestamp within the session ticket has expired.  User must re-authenticate. */
    public static final int EXPIRED_TICKET = 306;

    /** The session ticket is invalid and cannot be used to authenticate the user. */
    public static final int INVALID_TICKET = 307;

    /** The account does not have the appropriate group hierarchy. */
    public static final int INVALID_ACCOUNT_HIERARCY = 308;

    /** The account has been locked due to excessive failed login attempts */
    public static final int MAX_LOGIN_TRIES = 309;


    // ------------------------------------------------------------------------
    // Service related error messages range: 400 - 499
    //
    // Service related errors describe errors related to the services provided
    // to applications.  Such errors include services which are not offered
    // to that user, quota exceeded for a service, etc.
    //
    // Please follow this guideline when creating new errors

    /** Indicates that the service is unavailable to this user */
    public static final int SERVICE_NOT_OFFERED = 401;

    /** The service has become temporarily unavailable to users.  */
    public static final int SERVICE_UNAVAILABLE = 402;

    /** The service permissions do not allow the user to access this service. */
    public static final int SERVICE_PERMISSION_DENIED = 403;

    /** The user has exceeded their quota for this service */
    public static final int SERVICE_QUOTA_EXCEEDED = 404;

    /** The service is already enabled. */
    public static final int SERVICE_ALREADY_ENABLED = 405;

    /** The service is already disabled. */
    public static final int SERVICE_ALREADY_DISABLED = 406;


    // ------------------------------------------------------------------------
    // E-mail notification error message range: 500 - 599
    private static final int BEGIN_MAIL_ERRORS = 500;
    private static final int END_MAIL_ERRORS    = 599;

    /** The message could not be sent due to invalid e-mail addresses */
    public static final int MAIL_SEND_FAILURE = 501; 


    // ------------------------------------------------------------------------
    // USERNAME/LOGIN related error message range: 1100 - 1199
    private static final int BEGIN_LOGIN_ERRORS = 1100;
    private static final int END_LOGIN_ERRORS   = 1199;

    /** Login already exists */
    public static final int LOGIN_EXISTS = 1101;

    /** The user has attempted to create a login which they already have */
    public static final int LOGIN_EXISTS_FOR_SAME_USER = 1102;

    /** The user has attempted to create a login someone else has */
    public static final int LOGIN_EXISTS_FOR_ANOTHER_USER = 1103;

    /** The username does not conform to the rules defined for usernames */
    public static final int LOGIN_INVALID = 1104;

    /** The username is too short */
    public static final int LOGIN_TOO_SHORT = 1105;

    /** The username is too long */
    public static final int LOGIN_TOO_LONG = 1106;

    /** The username contains invalid characters. */
    public static final int INVALID_LOGIN_CHARACTER = 1107;

    /** The username contains a reserved substring. */
    public static final int RESERVED_LOGIN_SUBSTRING = 1108;


    // ------------------------------------------------------------------------
    // PASSWORD related error message range: 1200 - 1299
    private static final int BEGIN_PASSWORD_ERRORS = 1200;
    private static final int END_PASSWORD_ERRORS   = 1299;

    /** The password and confirmation password do not match */
    public static final int PASSWORDS_DONT_MATCH = 1201;

    /** The password is too short */
    public static final int PASSWORD_TOO_SHORT = 1202;

    /** The password is too long */
    public static final int PASSWORD_TOO_LONG = 1203;

    /** The password does not conform to the rules for that locale */
    public static final int PASSWORD_INVALID = 1204;

    /** The password contains the first or last name */
    public static final int PASSWORD_CONTAINS_NAME = 1205;

    /** The password contains a circular combination of the username */
    public static final int PASSWORD_USERNAME_CIRCULAR = 1206;

    /** The password contains invalid characters. */
    public static final int INVALID_PASSWORD_CHARACTER = 1207;

    /** The new password matches the old password, so no update can be made. */
    public static final int PASSWORD_ALREADY_EXISTS = 1208;

    
    // ------------------------------------------------------------------------
    // FIRSTNAME/LASTNAME related error message range: 1300 - 1399
    private static final int BEGIN_FIRSTNAME_ERRORS = 1300;
    private static final int END_FIRSTNAME_ERRORS   = 1349;
    private static final int BEGIN_LASTNAME_ERRORS  = 1350;
    private static final int END_LASTNAME_ERRORS    = 1399;

    /** The firstname does not conform to the rules defined for first names */
    public static final int INVALID_FIRSTNAME = 1301;

    /** The firstname contains non-ascii characters */
    public static final int NONASCII_FIRSTNAME = 1302;

    /** The firstname is too short. */
    public static final int FIRSTNAME_TOO_SHORT = 1303;

    /** The firstname is too long. */ 
    public static final int FIRSTNAME_TOO_LONG = 1304;

    /** The firstname contains invalid characters. */ 
    public static final int INVALID_FIRSTNAME_CHARACTER = 1305;


    /** The lastname does not conform to the rules defined for last names */
    public static final int INVALID_LASTNAME = 1351;

    /** The lastname contains non-ascii characters */
    public static final int NONASCII_LASTNAME = 1352;

    /** The lastname is too short. */
    public static final int LASTNAME_TOO_SHORT = 1353;

    /** The lastname is too long. */ 
    public static final int LASTNAME_TOO_LONG = 1354;

    /** The lastname contains invalid characters. */ 
    public static final int INVALID_LASTNAME_CHARACTER = 1355;




    /**
     * Determines if the error is related to the username.
     * 
     * @param   code    Error code in question
     * @return  boolean TRUE if the error is related to the username
     */
    public static boolean isUsernameError(int code) {
        return ((code >= BEGIN_LOGIN_ERRORS) && (code <= END_LOGIN_ERRORS));
    }

    /**
     * Determines if the error is related to the password.
     * 
     * @param   code    Error code in question
     * @return  boolean TRUE if the error is related to the password
     */
    public static boolean isPasswordError(int code) {
        return ((code >= BEGIN_PASSWORD_ERRORS) && (code <= END_PASSWORD_ERRORS));
    }


}
