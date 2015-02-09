/*
 * RepositoryConnection.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.web.db;

import java.sql.Connection;

/**
 * Wrapper for a database connection.  This provides extra methods for
 * tracking the connection, such as which process created the connection.
 * These methods are intended to make it easier to track and prevent
 * connection leaks. 
 * 
 * @version            $Revision: 1.1.1.1 $  
 * @author             Shawn Stafford
 */
public class RepositoryConnection {

    /** Database connection */
    private Connection conn = null;

    /** Identifier used to identify a connection */
    private long connId = 0;

    /** Thread ID that requested the connection */
    private long threadId = 0;

    /** Thread name that requested the connection */
    private String threadName;

    /**
     * Construct the repository connection.
     */
    public RepositoryConnection(Connection conn) {
        this.conn = conn;
    }

    /**
     * Return the database connection.
     *
     * @return  Database connection
     */
    public Connection getConnection() {
        return conn;
    }


    /**
     * Set the connection ID.  The connection ID should be a unique number
     * that can be used to track an individual connection.
     *
     * @param   id    Connection ID
     */
    public void setConnectionId(long id) {
        connId = id;
    }

    /**
     * Return the connection ID.
     *
     * @return Connection ID
     */
    public long getConnectionId() {
        return connId;
    }

    /**
     * Set the connection thread ID.  The thread ID represents the thread
     * ID of the thread in which the connection was created.
     *
     * @param   id    Connection thread ID
     */
    public void setThreadId(long id) {
        threadId = id;
    }

    /**
     * Return the connection thread ID.
     *
     * @return Connection thread ID
     */
    public long getThreadId() {
        return threadId;
    }

    /**
     * Set the connection thread name.  The thread name represents the 
     * name of the thread in which the connection was created.
     *
     * @param   name    Connection thread name
     */
    public void setThreadName(String name) {
        threadName = name;
    }

    /**
     * Return the connection thread name.
     *
     * @return Connection thread name
     */
    public String getThreadName() {
        return threadName;
    }

    /**
     * Render the connection as a string.
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();

        // Print the connection ID
        sb.append("id=" + connId + ", ");

        // Print the thread information
        if (threadName != null) {
            sb.append("thread=" + threadName + " (" + threadId + ")");
        } else {
            sb.append("thread=" + threadId);
        }

        return sb.toString();
    }

}

