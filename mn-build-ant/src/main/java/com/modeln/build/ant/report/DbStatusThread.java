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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Continually poll the database to determine if the connection
 * is still open.
 *
 * @author Shawn Stafford
 */
public final class DbStatusThread extends Thread {


    /** Connection to the build database */
    private Connection dbConnection;

    /** Boolean indicating whether the database polling is active */
    private boolean active = true;
    
    /** Number of milliseconds between database checks.  Defaults to 10 minutes. */
    private int interval = 10 * 60 * 1000;
    
    /**
     * Construct a thread to poll the database periodically.
     *
     * @param   conn    Connection to the database
     */
    public DbStatusThread(Connection conn) {
        dbConnection = conn;
    }

    /**
     * Set the number of milliseconds between database status queries.
     * 
     * @param  interval   Number of milliseconds
     */
    public void setInterval(int milliseconds) {
        interval = milliseconds;
    }

    /**
     * Return the number of milliseconds between database status queries.
     *
     * @return Number of milliseconds
     */
    public int getInterval() {
        return interval;
    }
    
    /**
     * Continuously poll the database to check for connectivity.
     */
    public void run() {
        try {
            while(active) {
                poll();
                sleep(interval);
            }
        } catch (InterruptedException iex) {
            active = false;
        }
    }

    /**
     * Connect to the database and determine if the connection is still valid.
     */
    public void poll() {
        String sql = "SELECT 1";
        try {
            Statement st = dbConnection.createStatement();
            ResultSet rs = st.executeQuery(sql);
            if (rs == null) {
                 System.err.println("Polling thread returned a null result.");
            }
        } catch (SQLException ex) {
            System.err.println("Polling thread failed to connect to the database.");
            ex.printStackTrace();
        }

    }    
}

