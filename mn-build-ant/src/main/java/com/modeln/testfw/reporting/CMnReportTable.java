/*
 * Copyright 2000-2003 by Model N, Inc.  All Rights Reserved.
 * 
 * This software is the confidential and proprietary information
 * of Model N, Inc ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you
 * entered into with Model N, Inc.
 */
package com.modeln.testfw.reporting;

import com.modeln.build.ant.report.BuildEventUtil;
import com.modeln.build.ant.report.ProgressTarget;
import com.modeln.build.ant.report.ReportParseCriteria;
import com.modeln.build.ant.report.ReportParseEvent;
import com.modeln.build.ant.report.ReportParseTarget;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.Vector;
import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.Project;


/**
 * The table interface defines all of the methods and fields used
 * to interact with the report database. 
 * 
 * @author  Shawn Stafford
 */
public class CMnReportTable {

    /** Timestamp used to prefix every test message line */
    protected static final SimpleDateFormat DATETIME = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    /** Name of the log parsing criteria table. */
    private String criteriaTable = "event_criteria";


    /** Name of the column that specifies the build version number */
    public static final String BUILD_VERSION = "build_version";

    /** Name of the column that identifies the criteria entry used to categroize log messages */
    public static final String CRITERIA_ID = "criteria_id";

    /** Name of the column that categorizes the severity of an event matching this criteria */
    public static final String EVENT_SEVERITY = "event_severity";

    /** Name of the column that specifies the Ant target to which the criteria applies */
    public static final String ANT_TARGET = "ant_target";

    /**  Name of the column that contains the text or expression used when searching for matching log entries */
    public static final String CRITERIA_TEXT = "criteria_text";

    /** Name of the group used to collect similar search criteria under the same heading */
    public static final String CRITERIA_GROUP = "criteria_group";



    /** Name of the table containing build log events. */
    private String eventTable = "build_event";


    /** Name of the column that identifies a single event log entry */
    public static final String EVENT_ID = "event_id";

    /** Name of the column that identifies the log level at which the event was logged. */
    public static final String EVENT_LEVEL = "event_level";

    /** Name of the column that specifies the date and time when the event was logged */
    public static final String EVENT_DATE = "event_date";

    /** Name of the column that contains the event log message */
    public static final String EVENT_MESSAGE = "event_message";

    /** Name of the column that specifies the execution stack of the event */
    public static final String EVENT_STACK = "event_stack"; 

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


    /**
     * Construct the table access object with the default table names.
     */
    public CMnReportTable() {
    }


    /**
     * Construct the table access object.
     *
     * @param  criteria    Name of the event criteria table
     * @param  event       Name of the event table
     */
    public CMnReportTable(String criteria, String event) {
        criteriaTable = criteria;
        eventTable = event;
    }

    /**
     * Set the name of the event table.
     *
     * @param  name   Table name
     */
    public void setEventTable(String name) {
        eventTable = name;
    }

    /**
     * Return the name of the event table.
     *
     * @return   Table name
     */
    public String getEventTable() {
        return eventTable;
    }

    /**
     * Set the name of the event criteria table.
     *
     * @param  name   Table name
     */
    public void setCriteriaTable(String name) {
        criteriaTable = name;
    }

    /**
     * Return the name of the event criteria table.
     *
     * @return   Table name
     */
    public String getCriteriaTable() {
        return criteriaTable;
    }



    /**
     * Store the report parsing criteria in the database.
     * 
     * @param   conn    Database connection
     * @param   version Foreign key that links the criteria to a build
     * @param   target  Report parsing information
     */
    public synchronized void addCriteria(
            Connection conn, 
            String version, 
            ReportParseTarget target) 
        throws SQLException
    {
        String targetName = target.getTargetName();
        Vector criteriaList = target.getAllCriteria();
        ReportParseCriteria currentCriteria = null;
        for (int idx = 0; idx < criteriaList.size(); idx++) {
            currentCriteria = (ReportParseCriteria) criteriaList.get(idx);

            StringBuffer sql = new StringBuffer();
            sql.append("INSERT INTO " + criteriaTable + " ");
            sql.append("(" + BUILD_VERSION);
            sql.append(", " + EVENT_SEVERITY);
            sql.append(", " + CRITERIA_GROUP);
            sql.append(", " + ANT_TARGET);
            sql.append(", " + CRITERIA_TEXT);
            sql.append(") VALUES ");

            sql.append("(\"" + version + "\""); 
            sql.append(", \"" + currentCriteria.getType() + "\"");
            sql.append(", \"" + target.getGroup() + "\"");
            sql.append(", \"" + targetName + "\"");
            sql.append(", \"" + escapeQueryText(currentCriteria.getText()) + "\")");

            Statement st = conn.createStatement();
            ResultSet rs = null;
            try {
                st.execute(sql.toString(), Statement.RETURN_GENERATED_KEYS);
                rs = st.getGeneratedKeys();
                if (rs != null) {
                    rs.first();
                    currentCriteria.setId(rs.getInt(1));
                } else {
                     System.err.println("Unable to obtain generated key.");
                }
            } catch (SQLException ex) {
                System.err.println("Failed to add criteria: " + sql.toString());
                ex.printStackTrace();
            }

        }


    }


    /**
     * Store parsed log events in the database.
     * 
     * @param   conn    Database connection
     * @param   target  Report parsing information
     */
    public synchronized void addEvent(
            Connection conn, 
            String version,
            ReportParseEvent event) 
        throws SQLException
    {
        ReportParseCriteria criteria = event.getHighestCriteria();
        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO " + eventTable + " ");
        sql.append("(" + EVENT_DATE);
        if (criteria != null) {
            sql.append(", " + CRITERIA_ID);
        }
        if (version != null) {
            sql.append(", " + BUILD_VERSION);
        }
        if (event.getExecutionPath() != null) {
            sql.append(", " + EVENT_STACK);
        }
        sql.append(", " + EVENT_LEVEL);
        sql.append(", " + EVENT_MESSAGE);
        sql.append(") VALUES ");

        sql.append("(\"" + DATETIME.format(event.getParseDate()) + "\"");
        if (criteria != null) {
            sql.append(", \"" + criteria.getId() + "\"");
        }
        if (version != null) {
            sql.append(", \"" + version + "\"");
        }
        if (event.getExecutionPath() != null) {
            sql.append(", \"" + BuildEventUtil.getPathAsString(event.getExecutionPath()) + "\"");
        }
        sql.append(", \"" + priorityToString(event.getBuildEvent().getPriority()) + "\"");
        sql.append(", \"" + escapeQueryText(event.getBuildEvent().getMessage()) + "\")");

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            st.execute(sql.toString());
        } catch (SQLException ex) {
            System.err.println("Failed to add event: " + sql.toString());
            ex.printStackTrace();
        }


    }

    /**
     * Look up any build events which match the build version number. 
     * The build version number may be a full version number or a partial string.
     * The database query will use the LIKE operator to locate any entries that
     * match the string.
     *
     * @param   conn    Database connection
     * @param   build   Build information  
     *
     * @return  List of build event objects
     */
    public synchronized Vector getEventList(
            Connection conn, 
            String version)
        throws SQLException
    {
        Vector list = null;

        StringBuffer sql = new StringBuffer();
        sql.append(
            "SELECT * FROM " + eventTable + 
            " WHERE " + BUILD_VERSION + " LIKE '%" + version + "%'" +
            " ORDER BY " + EVENT_ID + " ASC"
        );

        Statement st = conn.createStatement();
        try {
            ResultSet rs = st.executeQuery(sql.toString());
            if (rs != null) {
                list = parseEventList(conn, rs);
            } else {
                 System.err.println("Unable to obtain a list of build events.");
            }
        } catch (SQLException ex) {
            System.err.println("Failed to obtain build event.");
            ex.printStackTrace();
        }

        return list;
    }


    /**
     * Return the build version associated with a specific event.
     *
     * @param   conn    Database connection
     * @param   eventId Primary key used to identify a single build event
     *
     * @return  Build version assoicated with the specified event ID
     */
    public synchronized String getBuildVersion(
            Connection conn, 
            int eventId)
        throws SQLException
    {
        String version = null;

        StringBuffer sql = new StringBuffer();
        sql.append(
            "SELECT * FROM " + eventTable + 
            " WHERE " + EVENT_ID + "=" + eventId
        );

        Statement st = conn.createStatement();
        try {
            ResultSet rs = st.executeQuery(sql.toString());
            if (rs != null) {
                rs.first();
                version = rs.getString(BUILD_VERSION);
            } else {
                 System.err.println("Unable to obtain a list of build events.");
            }
        } catch (SQLException ex) {
            System.err.println("Failed to obtain build event.");
            ex.printStackTrace();
        }

        return version;
    }
    

    /**
     * Look up any build events which match the build version number and which 
     * occurred just before or just after the specified event ID. 
     * The build version number may be a full version number or a partial string.
     * The database query will use the LIKE operator to locate any entries that
     * match the string.  The event ID must be an exact match.
     *
     * @param   conn    Database connection
     * @param   eventId Primary key used to identify a single build event
     * @param   size    Maximum number of build events that constitute a context
     *
     * @return  List of build event objects
     */
    public synchronized Vector getEventContext(
            Connection conn, 
            int eventId,
            int size)
        throws SQLException
    {
        Vector list = null;
        String version = getBuildVersion(conn, eventId);

        int idMin = eventId - (size / 2);
        int idMax = eventId + (size / 2);

        StringBuffer sql = new StringBuffer();
        sql.append(
            "SELECT * FROM " + eventTable + 
            " WHERE " + BUILD_VERSION + " LIKE '%" + version + "%'" +
            " AND " + EVENT_ID + ">" + idMin +
            " AND " + EVENT_ID + "<" + idMax +
            " ORDER BY " + EVENT_ID + " ASC"
        );

        Statement st = conn.createStatement();
        try {
            ResultSet rs = st.executeQuery(sql.toString());
            if (rs != null) {
                list = parseEventList(conn, rs);
            } else {
                 System.err.println("Unable to obtain a list of build events.");
            }
        } catch (SQLException ex) {
            System.err.println("Failed to obtain build event.");
            ex.printStackTrace();
        }

        return list;
    }


    /**
     * Look up only those build events which match the build version number
     * and have an associated criteria.  The build version number may be a 
     * full version number or a partial string.  The database query will 
     * use the LIKE operator to locate any entries that match the string.
     * The list of events will contain references to the build targets
     * that were used to categorize the events.
     *
     * @param   conn    Database connection
     * @param   build   Build information
     *
     * @return  List of build event objects
     */
    public synchronized Vector getEventSummary(
            Connection conn,
            String version)
        throws SQLException
    {
        Vector list = null;

        StringBuffer sql = new StringBuffer();
        sql.append(
            "SELECT * FROM " + eventTable +
            " WHERE " + BUILD_VERSION + " LIKE '%" + version + "%'" +
            " AND " + CRITERIA_ID + " > 0" +
            " ORDER BY " + EVENT_ID + " ASC LIMIT 5000"
        );

        Statement st = conn.createStatement();
        try {
            ResultSet rs = st.executeQuery(sql.toString());
            if (rs != null) {
                list = parseEventList(conn, rs);
            } else {
                 System.err.println("Unable to obtain a list of build events.");
            }
        } catch (SQLException ex) {
            System.err.println("Failed to obtain build event.");
            ex.printStackTrace();
        }

        return list;
    }



    /**
     * Retrieve event criteria information from the database.
     * 
     * @param   conn    Database connection
     * @param   id      Primary key used to locate the criteria info
     *
     * @return  Criteria information
     */
    public synchronized ReportParseTarget getCriteria(
            Connection conn, 
            int id) 
        throws SQLException
    {
        ReportParseTarget target = null;

        StringBuffer sql = new StringBuffer();
        sql.append(
            "SELECT * FROM " + criteriaTable + 
            " WHERE " + CRITERIA_ID + "=" + id +
            " ORDER BY " + CRITERIA_ID + " DESC" 
        );

        Statement st = conn.createStatement();
        try {
            ResultSet rs = st.executeQuery(sql.toString());
            if (rs != null) {
                rs.first();
                target = parseTargetData(rs);
            } else {
                 System.err.println("Unable to obtain the build data.");
            }
        } catch (SQLException ex) {
            System.err.println("Failed to obtain build data: " + sql.toString());
            ex.printStackTrace();
        }

        return target;
    }


    /**
     * Return the logging event priority as a string.
     */
    private static String priorityToString(int priority) {
        switch (priority) {
            case Project.MSG_DEBUG:     return DEBUG_LEVEL;
            case Project.MSG_VERBOSE:   return VERBOSE_LEVEL;
            case Project.MSG_INFO:      return INFO_LEVEL;
            case Project.MSG_WARN:      return WARN_LEVEL;
            case Project.MSG_ERR:       return ERROR_LEVEL;
            default: return null;
        }
    }

    /**
     * Return the logging event priority as an integer.  If the string
     * does not match one of the defined priorities, a -1 will be returned.
     */
    private static int priorityToInt(String priority) {
        if (priority.equalsIgnoreCase(DEBUG_LEVEL)) {
            return Project.MSG_DEBUG;
        } else if (priority.equalsIgnoreCase(VERBOSE_LEVEL)) {
            return Project.MSG_VERBOSE;
        } else if (priority.equalsIgnoreCase(INFO_LEVEL)) {
            return Project.MSG_INFO;
        } else if (priority.equalsIgnoreCase(WARN_LEVEL)) {
            return Project.MSG_WARN;
        } else if (priority.equalsIgnoreCase(ERROR_LEVEL)) {
            return Project.MSG_ERR;
        } else {
            return -1;
        }
    }


    /**
     * Parse the result set to obtain a list of events. 
     *
     * @param   conn  Connection to the database (in case subsequent queries must be run)
     * @param   rs    Result set containing event data
     *
     * @return  List of events 
     */
    public Vector parseEventList(Connection conn, ResultSet rs)
        throws SQLException
    {
        Vector list = new Vector();

        ReportParseEvent event = null;
        ReportParseCriteria criteria = null;
        while (rs.next()) {
            event = parseEventData(rs);
            Vector criteriaList = event.getCriteria();
            for (int idx = 0; idx < criteriaList.size(); idx++) {
                criteria = (ReportParseCriteria) criteriaList.get(idx);
                if (criteria != null) {
                    // The assumption here is that the getCriteria call will always return
                    // a valid target containing only a single criteria object
                    ReportParseTarget newTarget = getCriteria(conn, criteria.getId());
                    Vector allCriteria = newTarget.getAllCriteria();
                    if ((allCriteria != null) && (allCriteria.size() > 0)) {
                        ReportParseCriteria newCriteria = (ReportParseCriteria) allCriteria.get(0);
                        newCriteria.setParent(newTarget);
                        boolean status = event.replaceCriteria(criteria, newCriteria);
                    }
                }
            }
            list.add(event);
        }

        return list;
    }


    /**
     * Parse the result set to obtain event information.
     * 
     * @param   rs    Result set containing event data
     *
     * @return  Event information
     */
    public ReportParseEvent parseEventData(ResultSet rs) 
        throws SQLException 
    {
        ReportParseEvent data = new ReportParseEvent();

        int id = rs.getInt(eventTable + "." + EVENT_ID);
        data.setId(id);

        // Add the criteria info if available
        int criteriaId = rs.getInt(eventTable + "." + CRITERIA_ID);
        if (criteriaId > 0) {
            ReportParseCriteria criteria = new ReportParseCriteria();
            criteria.setId(criteriaId); 
            data.addCriteria(criteria);
        }

        Date eventDate = rs.getTimestamp(eventTable + "." + EVENT_DATE);
        data.setParseDate(eventDate);

        String stack = rs.getString(eventTable + "." + EVENT_STACK);
        BuildEvent[] eventStack = BuildEventUtil.getPath(stack);
        data.setExecutionPath(eventStack);

        // Set the event information for the current event
        BuildEvent buildEvent = null;
        if (eventStack != null) {
            buildEvent = eventStack[eventStack.length - 1];
            if (buildEvent != null) {
                String levelStr = rs.getString(eventTable + "." + EVENT_LEVEL);
                int level = priorityToInt(levelStr); 

                String text = rs.getString(eventTable + "." + EVENT_MESSAGE);
                buildEvent.setMessage(text, level);

                data.setBuildEvent(buildEvent);
            }
        }

        return data;
    }


    /**
     * Parse the result set to obtain event criteria information.
     *
     * @param   rs    Result set containing criteria data
     *
     * @return  Event criteria information
     */
    public ReportParseCriteria parseCriteriaData(ResultSet rs)
        throws SQLException
    {
        ReportParseCriteria data = new ReportParseCriteria();

        int criteriaId = rs.getInt(criteriaTable + "." + CRITERIA_ID);
        data.setId(criteriaId);

        String version = rs.getString(criteriaTable + "." + BUILD_VERSION);
        //data.setBuildVersion(version);

        String severity = rs.getString(criteriaTable + "." + EVENT_SEVERITY);
        data.setType(severity);

        String text = rs.getString(criteriaTable + "." + CRITERIA_TEXT);
        data.setText(text);

        return data;
    }

    /**
     * Parse the result set to obtain group information.
     *
     * @param   rs    Result set containing group data
     * @return  Information obtained from the result set 
     */
    public ReportParseTarget parseTargetData(ResultSet rs) 
        throws SQLException
    {
        ReportParseTarget data = new ReportParseTarget();

        String group = rs.getString(criteriaTable + "." + CRITERIA_GROUP);
        data.setGroup(group);

        String target = rs.getString(criteriaTable + "." + ANT_TARGET);
        data.setTarget(target);

        data.addConfiguredFind(parseCriteriaData(rs));

        return data;
    }

    /**
     * Escape any reserved characters from the text so it can be
     * used in a query string.
     *
     * @param   text    String to be escaped
     * @return  Text containing escaped character sequences
     */
    protected static String escapeQueryText(String text) {
        text = replaceChar(text, '\\', "\\\\");
        text = replaceChar(text, '"', "\\\"");
        return text;
    }

    /**
     * Replaces a single character with a string.
     *
     * @param   line    Line of text to be updated
     * @param   ch      character to be replaced
     * @param   str     String to replace the character with
     * @return  String containing the substituted values
     */
    protected static String replaceChar(String line, char ch, String str) {
        String newString = "";
        int idxChar;

        while ((line != null) && (line.length() > 0)) {
            idxChar = line.indexOf(ch);
            if (idxChar >= 0) {
                newString = newString + line.substring(0, idxChar) + str;
                // Trim the processed information from the current string
                line = line.substring(idxChar + 1);
            } else {
                // Get the rest of the line when no more characters are found
                newString = newString + line;
                line = null;
            }
        }
        return newString;
    }



}
