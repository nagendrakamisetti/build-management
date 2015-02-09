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

import com.modeln.testfw.reporting.CMnReportTable;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Parses the Ant logging events and generates a report to summarize the
 * event contents.  A LiveReport registers an event listener within Ant
 * and begins parsing the incoming logging events.  The event listener
 * is responsible for categorizing and summarizing the events as they
 * are parsed.
 *
 * @author Shawn Stafford
 */
public final class DbReport extends Report {

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


    /** JDBC Driver class name. */
    private String jdbcDriver;

    /** JDBC URL for connecting to the database. */
    private String jdbcUrl;

    /** Username required to connect to the database */
    private String jdbcUsername;

    /** Password required to connect to the database */
    private String jdbcPassword;

    /** Default target priority */
    private static final int defaultPriority = Project.MSG_INFO;

    /** Level of events that must be inspected for reporting */
    private int targetPriority = defaultPriority;

    /** Parsing criteria level at which the build will fail if a match is found */
    private String failureCriteriaLevel = null;

    /** Determines whether non-criteria events will be logged */
    private boolean logCriteriaOnly = false;

    /** Determines whether a polling thread should be used to manage db connectivity */
    private boolean pollingEnabled = false;

    /** Number of seconds between database polls */
    private int pollingInterval = 600;

    /** Thread which polls the database to determine connection status */
    private DbStatusThread polling = null;

    /** Database access object */
    private CMnReportTable dbTable = new CMnReportTable();


    /**
     * Set the JDBC driver class to use when establishing a connection
     * to the reporting database.
     *
     * @param   driver     Name of the JDBC driver class
     */
    public void setDriver(String driver) throws BuildException {
        jdbcDriver = driver;
    }

    /**
     * Returns the name of the JDBC driver used to connect to the database.
     *
     * @return  JDBC driver class name
     */
    public String getDriver() {
        return jdbcDriver;
    }

    /**
     * Set the JDBC URL to use when establishing a connection
     * to the reporting database.
     *
     * @param   url     JDBC URL
     */
    public void setUrl(String url) throws BuildException {
        jdbcUrl = url;
    }

    /**
     * Returns the JDBC URL used to connect to the database.
     *
     * @return  JDBC driver class name
     */
    public String getUrl() {
        return jdbcUrl;
    }

    /**
     * Set the JDBC username to use when establishing a connection
     * to the reporting database.
     *
     * @param   username    Database username
     */
    public void setUsername(String username) throws BuildException {
        jdbcUsername = username;
    }

    /**
     * Returns the JDBC username used to connect to the database.
     *
     * @return  Database username
     */
    public String getUsername() {
        return jdbcUsername;
    }


    /**
     * Set the JDBC password to use when establishing a connection
     * to the reporting database.
     *
     * @param   password    Database password
     */
    public void setPassword(String password) throws BuildException {
        jdbcPassword = password;
    }

    /**
     * Returns the JDBC password used to connect to the database.
     *
     * @return  Database password
     */
    public String getPassword() {
        return jdbcPassword;
    }

    /**
     * Set the name of the event database table.
     *
     * @param   name    Name of the event table 
     */
    public void setEventTable(String name) throws BuildException {
        dbTable.setEventTable(name);
    }

    /**
     * Return the name of the event database table.
     *
     * @return  Name of the event table
     */
    public String getEventTable() {
        return dbTable.getEventTable();
    }

    /**
     * Set the name of the event criteria database table.
     *
     * @param   name    Name of the event criteria table
     */
    public void setCriteriaTable(String name) throws BuildException {
        dbTable.setCriteriaTable(name);
    }

    /**
     * Return the name of the event criteria database table.
     *
     * @return  Name of the event criteria table
     */
    public String getCriteriaTable() {
        return dbTable.getCriteriaTable();
    }


    /**
     * Set the priority level at which the Ant events are being inspected.
     * Only events with a priority equal or greater than this level will be
     * parsed for reporting.  If no level is specified, the default level
     * will be INFO.
     *
     * @param   level   Event inspection level
     * @throws  BuildException if an invalid level is specified
     */
    public void setEventLevel(String level) throws BuildException {
        if ((level != null) && (level.length() > 0)) {
            if (level.equalsIgnoreCase(DEBUG_LEVEL)) {
                targetPriority = Project.MSG_DEBUG;
            } else if (level.equalsIgnoreCase(VERBOSE_LEVEL)) {
                targetPriority = Project.MSG_VERBOSE;
            } else if (level.equalsIgnoreCase(INFO_LEVEL)) {
                targetPriority = Project.MSG_INFO;
            } else if (level.equalsIgnoreCase(WARN_LEVEL)) {
                targetPriority = Project.MSG_WARN;
            } else if (level.equalsIgnoreCase(ERROR_LEVEL)) {
                targetPriority = Project.MSG_ERR;
            } else {
                throw new BuildException("Unrecognized parsing level: " + level, getLocation());
            }
        } else {
            // Set a default level
            targetPriority = defaultPriority;
        }
    }

    /**
     * Return the priority level at which the Ant events are being inspected.
     *
     * @return  Priority of event inspection
     */
    public int getEventLevel() {
        return targetPriority;
    }


    /**
     * Return the word representation of the priority level, such as error
     * or warning.  If no corresponding level exists, a null is returned.
     *
     * @return  String representation of the priority level
     */
    public String getEventLevelAsString() {
        switch (targetPriority) {
            case Project.MSG_DEBUG:     return DEBUG_LEVEL;
            case Project.MSG_VERBOSE:   return VERBOSE_LEVEL;
            case Project.MSG_INFO:      return INFO_LEVEL;
            case Project.MSG_WARN:      return WARN_LEVEL;
            case Project.MSG_ERR:       return ERROR_LEVEL;
            default: return null;
        }
    }

    /**
     * Set the criteria level at which a build failure will be triggered
     * if a match of equal or greater severity is found.
     */
    public void setFailureCriteriaLevel(String level) {
        failureCriteriaLevel = level;
    }

    /**
     * Enable logging only for events which match the parsing criteria.
     *
     * @param  enabled   Enable
     */
    public void setCriteriaOnly(boolean enabled) {
        logCriteriaOnly = enabled;
    }

    /**
     * Enable a polling thread which checks the database for connectivity.
     *
     * @param  enabled   Enable polling thread
     */
    public void setPollingEnabled(boolean enabled) throws BuildException {
        pollingEnabled = enabled;
    }

    /**
     * Set the number of seconds between database polling queries.
     *
     * @param seconds   Number of seconds between polls
     */
    public void setPollingInterval(int seconds) {
        pollingInterval = seconds;
    }


    /**
     * Perform the task parsing and generate the report.  This method 
     * performs the event listener registration.
     */
    @SuppressWarnings("deprecation")
	public void execute() throws BuildException {
        // Obtain the build version
        String version = getProject().getProperty("com.modeln.App.version");

        // Create a new build listener for parsing events
        Connection conn = getConnection();
        DbReportListener listener = new DbReportListener(conn, version, getParseTargets(), dbTable);
        listener.setMessageOutputLevel(targetPriority);
        listener.setCriteriaOnly(logCriteriaOnly);
        if (failureCriteriaLevel != null) {
            listener.enableFailOnCriteriaMatch(failureCriteriaLevel);
        }

        // Add the event listeners to the project
        project.addBuildListener(listener);

        // Add a thread to poll the database
        if (pollingEnabled) {
            polling = new DbStatusThread(conn);
            polling.setInterval(pollingInterval * 1000);
            polling.start();
        }
    }


    /** 
     * Establish a connection to the database.
     *
     * @return  Database connection
     */
    private Connection getConnection() {
        Connection conn = null;

        // Force the class loader to load the JDBC driver
        try { 
            // The newInstance() call is a work around for some 
            // broken Java implementations
            Class.forName(jdbcDriver).newInstance(); 
        } catch (Exception ex) { 
            System.err.println("Failed to load the JDBC driver: " + jdbcDriver);
        }

        // Include the username and password on the URL
        StringBuffer url = new StringBuffer();
        url.append(jdbcUrl);
        if (jdbcUsername != null) {
            url.append("?user=" + jdbcUsername);
            if (jdbcPassword != null) {
                url.append("&password=" + jdbcPassword);
            }
        }

        // Establish a connection to the database
        try {
            conn = DriverManager.getConnection(url.toString());
        } catch (SQLException ex) {
            System.err.println("Failed to establish a database connection: url=" + url.toString());
            System.err.println("SQLException: " + ex.getMessage()); 
            System.err.println("SQLState: " + ex.getSQLState()); 
            System.err.println("VendorError: " + ex.getErrorCode()); 
            ex.printStackTrace();
        }

        return conn;
    }


}
