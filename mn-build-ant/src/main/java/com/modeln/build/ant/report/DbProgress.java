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

import com.modeln.testfw.reporting.CMnProgressTable;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Vector;

/**
 * Parses the Ant logging events and inserts database rows as output lines
 * are found to match progress targets.  This listener is used to track
 * build progress by locating known markers within the build output. 
 *
 * @author Shawn Stafford
 */
public final class DbProgress extends Task {

    /** Version number or identifier used to uniquely identify the item being monitored */
    private String buildId;

    /** JDBC Driver class name. */
    private String jdbcDriver;

    /** JDBC URL for connecting to the database. */
    private String jdbcUrl;

    /** Username required to connect to the database */
    private String jdbcUsername;

    /** Password required to connect to the database */
    private String jdbcPassword;


    /** Determines whether a polling thread should be used to manage db connectivity */
    private boolean pollingEnabled = false;

    /** Number of seconds between database polls */
    private int pollingInterval = 600;

    /** Thread which polls the database to determine connection status */
    private DbStatusThread polling = null;

    /** Database access object */
    private CMnProgressTable dbTable = new CMnProgressTable();

    /** List of ant targets should be used as progress indicators */
    protected Vector<ProgressTarget> progressTargets = new Vector<ProgressTarget>();


    /**
     * Set the unique identifier, such as a build version number, used to 
     * identify the item being monitored.  This links the progress entry 
     * to an entry in the build table.
     *
     * @param   id     Progress identifier
     */
    public void setBuildId(String id) {
        buildId = id;
    }

    /**
     * Return the unique identifier, such as a build version number, used to
     * identify the item being monitored.
     *
     * @return    Progress identifier
     */
    public String getBuildId() {
        return buildId;
    }


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
     * Set the name of the build progress group database table.
     *
     * @param   name    Name of the progress group table
     */
    public void setGroupTable(String name) throws BuildException {
        dbTable.setGroupTable(name);
    }

    /**
     * Return the name of the build progress group database table.
     *
     * @return  Name of the progress group table
     */
    public String getGroupTable() {
        return dbTable.getGroupTable();
    }

    /**
     * Set the name of the build progress database table.
     *
     * @param   name    Name of the progress table 
     */
    public void setProgressTable(String name) throws BuildException {
        dbTable.setProgressTable(name);
    }

    /**
     * Return the name of the build progress database table.
     *
     * @return  Name of the progress table
     */
    public String getProgressTable() {
        return dbTable.getProgressTable();
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
     * Process the nested target elements to determine which targets should be
     * used as progress indicators. 
     */
    public void addConfiguredTarget(ProgressTarget target) {
        System.out.println("Adding progress target: " + target.getDisplayName());
        progressTargets.add(target);
    }

    /**
     * Return a list of ant targets to be used as progress indicators. 
     */
    public Vector<ProgressTarget> getProgressTargets() {
        return progressTargets;
    }


    /**
     * Perform the task parsing and generate the report.  This method 
     * performs the event listener registration.
     */
    @SuppressWarnings("deprecation")
	public void execute() throws BuildException {
        // Create a new build listener for parsing events
        Connection conn = getConnection();
        DbProgressListener listener = new DbProgressListener(conn, buildId, getProgressTargets(), dbTable);

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
