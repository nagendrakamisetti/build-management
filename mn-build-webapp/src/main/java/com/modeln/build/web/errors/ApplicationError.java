/*
 * ApplicationError.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.web.errors;

import java.util.*;

/**
 * The LoginTable loads user data from the login table
 * by mapping database fields to the correct data members.
 *
 * @version            $Revision: 1.1.1.1 $
 * @author             Shawn Stafford
 *
 */
public class ApplicationError {

	/** 
     * Errors of this level simply inform the user that an action could 
     * not be completed as requested. 
     */
    public static final int INFO_SEVERITY = 0;

    /** 
     * The application can continue but the error should be investigated. 
     */
    public static final int WARN_SEVERITY = 1;

    /** 
     * The error is critical and the application will not perform correctly 
     * until it is repaired. 
     */
    public static final int CRITICAL_SEVERITY = 2;

    /** 
     * The error has caused the application to fail and normal operation cannot
     * resume until the error has been fixed.
     */
    public static final int FATAL_SEVERITY = 3;



    /** 
     * Data errors are caused by data inconsistencies or corruption of data 
     * within the database. 
     */
    public static final int DATA_ERROR = 1;

    /**
     * Application errors are caused by errors within the Member Services 
     * application.
     */
    public static final int APPLICATION_ERROR = 2;

    /**
     * Repository process errors are caused by a failure related to the
     * repository process which services Member Services requests.  This
     * could indicate that the AMS or SORB process is unavailable to 
     * service the current request.
     */
    public static final int PROCESS_ERROR = 3;

    /**
     * A user error indicates that the user has entered information into
     * a form which does not conform to the restrictions required by the
     * Member Services application.
     */
    public static final int USER_ERROR = 4;

    /**
     * Database errors indicate a problem with the database query or
     * stored procedure which handles the current request.
     */
    public static final int DATABASE_ERROR = 5;


    /** Delimits the fields of the error string. */
    public static final String FIELD_DELIMITER = "::";

    /** Delimits the fields of the escalation path. */
    public static final String TIER_DELIMITER = ",";



    /** Maps the error to a numeric code. */
    private int errorCode = 0;

    /** Determines how serious the error is and how the application should handle it. */
	private int severity = 0;

    /** Indicates the type of error and possibly who it can be escalated to. */
    private int errorType = APPLICATION_ERROR;

    /** Name which identifies the error to the user. */
	private String name;

    /** Error string which describes the error to the user. */
	private String errorText;

    /** Exception associated with the error (if any). */
    private Exception exception;

    /** Debugging name used to identify the error by name. */
    private String debugName;

    /** Debugging message used to determine what caused the error. */
    private String debugMsg;

    /** Escalation path which should be followed when diagnosing this error. */
    private int[] escalationPath = {};

    /**
     * Constructs an error object with only an error code value.
     */
    public ApplicationError(int code) {
        errorCode = code;
    }

    /**
     * Constructs an error object with the given error code and text message.
     *
     * @param   code    application error code
     * @param   msg     error message to be displayed to the user
	 */
	protected ApplicationError (int code, String msg) {
        errorCode = code;
        errorText = msg;
	}

    /**
     * Constructs an error object by parsing the given string, which can be
     * loaded from external files such as Properties files.  The entry parameter
     * is parsed and split into its component elements to populate the object
     * with information describing the error.  The toString method can be used
     * to reverse this process and store the string entry.
     * 
     * @param   code    Application error code
     * @param   entry   String entry containing information to describe the error
	 */
    public static ApplicationError parseLine(int code, String entry) 
        throws NoSuchElementException, NumberFormatException 
    {
        ApplicationError error = null;

        StringTokenizer fields = new StringTokenizer(entry, FIELD_DELIMITER);
        String errorName = fields.nextToken();
        String errorText = fields.nextToken();

        error = new ApplicationError(code, errorText);
        error.setErrorName(errorName);

        return error;
    }

    /**
     * Constructs an error object by parsing the given text string, which can be
     * loaded from external files such as Properties files.  The entry parameter
     * is parsed and split into its component elements to populate the object
     * with information describing the error.  The toString method can be used
     * to reverse this process and store the string entry.  The data string
     * contains information about how to diagnose the error.
     * 
     * @param   code    Application error code
     * @param   text    String entry containing information to describe the error
     * @param   data    String entry containing error diagnosis information
	 */
    public static ApplicationError parseLine(int code, String text, String data) 
        throws NoSuchElementException, NumberFormatException 
    {
        ApplicationError error = parseLine(code, text);

        // Parse the data line to populate the error diagnosis info
        StringTokenizer fields = new StringTokenizer(data, FIELD_DELIMITER);
        error.setDebugName(fields.nextToken());
        error.setSeverity(Integer.parseInt(fields.nextToken()));
        error.setErrorType(Integer.parseInt(fields.nextToken()));
        String tierList = null;
        if (fields.hasMoreTokens()) {
            tierList = fields.nextToken();
        }

        // Parse the list of tiers
        if ((tierList != null) && (tierList.length() > 0)) {
            StringTokenizer tiers = new StringTokenizer(tierList, TIER_DELIMITER);
            int[] path = new int[tiers.countTokens()];
            for (int idx = 0; idx < tiers.countTokens(); idx++) {
                path[idx] = Integer.parseInt(tiers.nextToken());
            }
            error.setEscalationPath(path);
        }

        return error;
    }



    /**
     * Determines whether the current error is of the specified error type.
     * If the error matches the given type, TRUE is returned.
     *
     * @param   type    Error type to check
     * @return  boolean TRUE if the error matches the specified type
     */
    public boolean isType(int type) {
        return (errorType == type);
    }

    /**
     * Returns the type of error as a string.
     * 
     * @param   type    type of error
     * @return  String representing the error type.
     */
    public String getTypeAsString() {
        return getTypeAsString(errorType);
    }

    /**
     * Returns the type of error as a string.
     * 
     * @return  String representing the error type.
     */
    public static String getTypeAsString(int type) {
        switch (type) {
            case DATA_ERROR: return "Repository data error";
            case APPLICATION_ERROR: return "Application error";
            case PROCESS_ERROR: return "Process not responding";
            case USER_ERROR: return "User error";
            case DATABASE_ERROR: return "Database error";
            default: return "Unknown error";
        }
    }

    /**
     * Sets the type of error. 
     * 
     * @param   type    indicates the type of error
     */
    public void setErrorType(int type) {
        errorType = type;
    }

    /**
     * Returns the name of the error.
     */
    public String getErrorName() {
        return name;
    }

    /**
     * Returns the application error code associated with the error.
     */
    public int getErrorCode() {
        return errorCode;
    }

    /** 
     * Returns the application error message associated with the error.
     */
    public String getErrorMsg() {
        return errorText;
    }

    /** 
     * Returns the name used to identify the error internally.
     */
    public String getDebugName() {
        return debugName;
    }

    /**
     * Returns the severity of the error.
     */
    public int getSeverity() {
        return severity;
    }

    /**
     * Determines whether the error is considered severe.  Severe errors
     * will have severity values greater than or equal to CRITICAL_SEVERITY.
     */
    public boolean isSevere() {
        return (severity >= CRITICAL_SEVERITY);
    }

    /**
     * Returns the severity of the error as a string.
     * 
     * @return  String representing the error severity.
     */
    public String getSeverityAsString() {
        return getSeverityAsString(severity);
    }

    /**
     * Returns the severity of the error as a string.
     * 
     * @param   severity    Severity of the error to be evaluated
     * @return  String representing the error severity.
     */
    public static String getSeverityAsString(int severity) {
        switch (severity) {
            case INFO_SEVERITY: return "Informational";
            case WARN_SEVERITY: return "Warning";
            case CRITICAL_SEVERITY: return "Critical";
            case FATAL_SEVERITY: return "Fatal";
            default: return "Unknown";
        }
    }

    /**
     * Returns the exception attached to the error.
     */
    public Exception getException() {
        return exception;
    }

    /** 
     * Returns the debugging message associated with the error.
     */
    public String getDebugMsg() {
        return debugMsg;
    }

    /**
     * Sets the name of the error message.  This is used as a means for 
     * tiered support to more easily identify errors (rather than using
     * numeric codes).
     */
    protected void setErrorName(String str) {
        name = str;
    }

    /**
     * Sets the error code.
     */
    protected void setErrorCode(int code) {
        errorCode = code;
    }

    /**
     * Sets the error message.
     */
    protected void setErrorMsg(String msg) {
        errorText = msg;
    }

    /**
     * Sets the severity of the error.
     */
    protected void setSeverity(int level) {
        severity = level;
    }

    /**
     * Sets the debugging name used to identify the error.
     */
    public void setDebugName(String name) {
        debugName = name;
    }

    /**
     * Sets the debugging message.
     */
    public void setDebugMsg(String msg) {
        debugMsg = msg;
    }

    /** 
     * Attaches an exception to the error.  This is useful when support
     * is attempting to debug application errors.
     */
    public void attachException(Exception ex) {
        exception = ex;
    }

    /**
     * Sets the customer support escalation path which should be followed 
     * when diagnosing this error.
     *
     * @param   int[]   list of tiers which can fix the error
     */
    public void setEscalationPath(int[] path) {
        escalationPath = path;
    }

    /**
     * Converts the error to a string.  This is the reverse of the parseLine call.
     */
    public String toString() {
        return name + FIELD_DELIMITER + severity + FIELD_DELIMITER + errorText;
    }

    /**
     * Converts the error to a log friendly string format.
     *
     * @return  Message formatted for log output
     */
    public String toLogString() {
        StringBuffer msg = new StringBuffer();
        msg.append("TYPE=" + getTypeAsString());
        msg.append(", SEV=" + getSeverityAsString());
        msg.append(", MSG=" + errorText);
        msg.append(", DEBUG=" + getDebugMsg());
        if (exception != null) {
            msg.append(", EX: " + exception.toString());
        }
        return msg.toString();
    }

}
