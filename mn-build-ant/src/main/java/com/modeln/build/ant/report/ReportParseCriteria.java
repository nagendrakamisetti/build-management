/*
 * Copyright 2000-2003 by Model N, Inc.  All Rights Reserved.
 * 
 * This software is the confidential and proprietary information
 * of Model N, Inc ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you
 * entered into with Model N, Inc.
 */
package com.modeln.build.ant.report;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildException;

import java.util.Comparator;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Contains information about a single parse string to listen for when
 * parsing the Ant build events.
 *
 * @author Shawn Stafford
 */
public final class ReportParseCriteria extends Task implements Comparator<ReportParseCriteria> {

    /** Parse events at the debug level */
    public static final String DEBUG_LEVEL = "debug";

    /** Parse events at the error level */
    public static final String ERROR_LEVEL = "error";

    /** Parse events at the info level */
    public static final String INFO_LEVEL = "information";

    /** Parse events at the verbose level */
    public static final String VERBOSE_LEVEL = "verbose";

    /** Parse events at the warn level */
    public static final String WARN_LEVEL = "warning";

    /** Ordering of the levels */
    private static final String[] LEVELS = { ERROR_LEVEL, WARN_LEVEL, INFO_LEVEL, VERBOSE_LEVEL, DEBUG_LEVEL };


    /** Regular expression text used when searching for a matching report line */
    private String parseTarget;

    /** Type of message that the target text identifies (error, warning, etc) */
    private int targetType = 2;

    /** Database primary key used to identify a single criteria entry */
    private int criteriaId = -1;

    /** Parent group to which the criteria belongs */
    private ReportParseTarget parent;


    /**
     * Set a unique identifier for the parse criteria.
     *
     * @param  id   Unique identifier
     */
    public void setId(int id) {
        criteriaId = id;
    }

    /**
     * Return the unique identifier for the parse criteria.  A negative value
     * indicates that a unique ID has not been set.
     *
     * @return  Unique identifier
     */
    public int getId() {
        return criteriaId;
    }

    /**
     * Set the parent parse target that contains this parse criteria.  The parent
     * is generally used for grouping purposes.
     *
     * @param  parent  Group containing the criteria
     */
    public void setParent(ReportParseTarget parent) {
        this.parent = parent;
    }

    /**
     * Return the parent parse target that contains this parse criteria.  The parent
     * is generally used for grouping purposes, and is not guaranteed to be set
     * in all cases.  If no parent is set, an null will be returned.
     *
     * @return Parent group
     */
    public ReportParseTarget getParent() {
        return parent;
    }

    /**
     * Determines if the criteria indicates an error.
     *
     * @return TRUE if the criteria indicates an error, false otherwise
     */
    public boolean isError() {
        return (targetType == 0);
    }

    /**
     * Determines if the criteria indicates a warning.
     *
     * @return TRUE if the criteria indicates a warning, false otherwise
     */
    public boolean isWarning() {
        return (targetType == 1);
    }

    /**
     * Determines if the current criteria is more severe or at least as severe 
     * as the indicated level.
     * 
     * @param   type   Criteria type to be evaluated against the current criteria
     */
    public boolean isAsSevereAs(String type) throws BuildException {
        return (typeToInt(type) >= targetType);
    }

    /**
     * Set the type of message associated with the target text.  This is
     * used to determine what type of condition the text should indicate,
     * such as error or warning.
     *
     * @param   type    Classification of the target string
     */
    public void setType(String type) throws BuildException {
        targetType = typeToInt(type);
    }

    /**
     * Return the message type associated with the target text.  The
     * type will reflect the identification of the message, categorizing
     * it as an error, warning, or other criteria type.
     */
    public String getType() {
        return typeToString(targetType);
    }

    /**
     * Obtain the type as an ordered value for use in comparison.  The
     * lowest value will indicate the most severe type.  For example,
     * error is equal to Zero and is the most severe type.
     *
     * @return  Severity value
     */
    protected int getTypeValue() {
        return targetType;
    }


    /**
     * Set the search string to be used.
     *
     * @param   value    Text to search for when parsing events
     */
    public void setText(String value) {
        parseTarget = value;
    }

    /**
     * Set the search string by parsing the contents of the tag.
     */
    public void addText(String msg) {
        parseTarget += getProject().replaceProperties(msg);
    }

    /**
     * Return the text being used as the parse criteria.
     *
     * @return  Text used to parse the event
     */
    public String getText() {
        return parseTarget;
    }


    /**
     * Determine if the event matches the parse criteria.  A regular expression
     * comparison is used to determine if the event message matches the regular
     * expression parse criteria.
     * 
     * @return  True if the event matches the criteria
     */
    public boolean matches(BuildEvent event) {
        boolean matchFound = false;

        if (event.getMessage() != null) {
            try {
                Pattern regexPattern = Pattern.compile(parseTarget);
                Matcher regex = regexPattern.matcher(event.getMessage());
                matchFound = regex.find();
            } catch (Exception ex) {
            }
        }

        return matchFound;
    }

    
    /**
     * Compare the target priority of each criteria to determine which criteria
     * is a higher priority.
     */
    public int compare(ReportParseCriteria o1, ReportParseCriteria o2) throws ClassCastException {

        // The comparison operators are actually counter-intuitive in this case
        // because a low number (i.e. error = 0) is considered a higher severity
        if (o1.getTypeValue() > o2.getTypeValue()) {
            return -1;
        } else if (o1.getTypeValue() < o2.getTypeValue()) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * Compare the target priority of each criteria to determine if they are
     * equal.
     */
    public boolean equals(ReportParseCriteria obj) {
        int cmp = compare(this, obj);
        return (cmp == 0);
    }


    /**
     * Convert the criteria type or level to its integer value within the
     * ordered array of LEVELS.  The lower the value, the higher the
     * severity.
     *
     * @param  type   Name of the criteria level
     * @return Index value of the criteria level
     * @throws BuildException if no matching type is defined
     */
    private int typeToInt(String type) throws BuildException { 
        for (int idx = 0; idx < LEVELS.length; idx++) {
            if (type.equalsIgnoreCase(LEVELS[idx])) {
                return idx;
            }
        }

        // Make sure the user has specified an appropriate type
        throw new BuildException("Invalid type: " + type);
    }

    /**
     * Convert the criteria index to a string representation.
     *
     * @param  type  Index of the criteria level
     * @return Name of the criteria level
     * @throws BuildException if an invalid index is requested
     */
    private String typeToString(int type) throws BuildException {
        if ((type >= 0) && (type < LEVELS.length)) {
            return LEVELS[type];
        } else {
            throw new BuildException("Invalid type: " + type);
        }
    }

    /**
     * Determines if the specified criteria type is valid.
     *
     * @param   type   Criteria level to examine
     * @return  TRUE if the criteria type is valid
     */
    public static boolean isValidType(String type) {
        boolean found = false;
        for (int idx = 0; idx < LEVELS.length; idx++) {
            if (type.equalsIgnoreCase(LEVELS[idx])) {
                found = true;
            }
        }
        return found;
    }

}
