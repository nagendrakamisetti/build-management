/*
 * RepositoryManager.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.web.database;

import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
// Eventually, repository connections should be pooled
import org.apache.commons.pool.*;
import org.apache.commons.dbcp.*;
*/

import com.modeln.build.web.errors.*;
import com.modeln.build.web.errors.*;

/**
 * The RepositoryManager manages connections to data repositories
 * such as databases, server sockets, and local files.
 * 
 * @version            $Revision: 1.1.1.1 $  
 * @author             Shawn Stafford
 */
public class RepositoryManager {

    /** List of repositories being managed */
    private Hashtable repositoryList = new Hashtable();

    /** List of pooled repository connections */
    private Hashtable repositoryPool = new Hashtable();

    /** Set the logging output used to log database messages */
    private PrintWriter repositoryLog = null;

    /** List of repository connections being managed */
    private Hashtable<RepositoryConnection, DataRepository> connectionList = new Hashtable<RepositoryConnection, DataRepository>();


    /**
     * Construct the repository manager.
     */
    public RepositoryManager() {
    }


    /**
     * Set the logging output for all repositories.
     *
     * @param  out    Log output
     */
    public void setLogWriter(PrintWriter out) {
        repositoryLog = out;
    }

    /**
     * Add a new repository to the list of repositories being managed.
     *
     * @param   repositoryName  Identifies the repository within the manager
     * @param   repository      Connection information for the repository
     */
    public void addRepository(String repositoryName, DataRepository repository) {

        // Create a pool of connections to the repository if possible
        try {
            if (repository instanceof DatabaseRepository) {
                Class driver = Class.forName(((DatabaseRepository)repository).getDriver());
                DriverManager.registerDriver((Driver) driver.newInstance());
                repositoryList.put(repositoryName, repository);
                log("Adding repository to the list: " + repository.getDescription());
            } else if (repository instanceof JndiRepository) {
                //Class.forName(((JndiRepository)repository).getDriver());
                repositoryList.put(repositoryName, repository);
                log("Adding repository to the list: " + repository.getDescription());
                log(repository);
            } else {
                log("Unable to add repository of unknown type.");
            }
        } catch (Exception ex) {
            log("Unable to add repository: " + repositoryName);
            ex.printStackTrace();
        }

    }

    /**
     * Return the list of repositories.
     *
     * @return List of repositories
     */
    public Hashtable<String, DataRepository> getRepositoryList() {
        return repositoryList;
    }

    /**
     * Return a connection to the specified database if it exists
     * 
     * @param  repositoryName   Named key used to lookup the repository info
     * @return Connection to the database
     */
    public RepositoryConnection getDbConnection(String repositoryName) throws SQLException, NamingException {
        RepositoryConnection conn = null;

        // Obtain the repository information from the list
        Object info = repositoryList.get(repositoryName);
        if (info != null) {
            conn = getDbConnection((DataRepository)info);
        } else {
            log("Unable to locate repository: " + repositoryName + " (out of " + repositoryList.size() + " available repositories)");
        }

        return conn;
    }

    /**
     * Return a connection to the database.
     *
     * @param  repository   Repository information
     * @return Connection to the database
     */
    public RepositoryConnection getDbConnection(DataRepository repository) throws SQLException, NamingException {
        // Create a database connection
        Connection conn = null;
        if (repository instanceof DatabaseRepository) {
            String url = ((DatabaseRepository)repository).getUrl();
            String user = ((DatabaseRepository)repository).getUsername();
            String pass = ((DatabaseRepository)repository).getPassword();
            DriverManager.setLogWriter(repositoryLog);
            conn = DriverManager.getConnection(url, user, pass);
            if (conn == null) {
                log("Unable to establish a connection to the database: url=" + url + ", user=" + user + ", pass=" + pass);
            }
        } else if (repository instanceof JndiRepository) {
            DataSource ds = getDataSource((JndiRepository)repository);
            ds.setLogWriter(repositoryLog);
            conn = ds.getConnection();
        } else {
            log("Unable to obtain a connection to the data repository: " + repository.getDescription());
        }

        // Construct tracking information to manage the connection
        RepositoryConnection rc = new RepositoryConnection(conn);
        rc.setConnectionId(System.currentTimeMillis());
        Thread thread = Thread.currentThread();
        rc.setThreadId(thread.getId());
        rc.setThreadName(thread.getName());

        // Keep track of the connection so we can later determine if
        // connections are being released properly
        connectionList.put(rc, repository);

        return rc;
    }

    /**
     * Close the database connection and log the information.
     * 
     * @param   conn   Database connection to be closed
     */
    public void release(RepositoryConnection rc) {
        boolean closed = false;

        if (rc != null) {
            // Close the database connection
            Connection conn = rc.getConnection();
            try {
                if (conn != null) {
                    conn.close();
                    closed = true;
                }
            } catch (SQLException sqlex) {
                log("Unable to close database connection: " + sqlex.toString());
            } finally {
                log(rc);

                // Remove the connection from the list if it is closed
                if (closed) {
                    DataRepository value = (DataRepository) connectionList.remove(rc);
                    if (value == null) {
                        log("Unable to locate the repository connection for release.");
                    }
                }
            }

        } else {
            log("Unable to release null repository connection.");
        }
    }

    /**
     * Convenience method for getting the JNDI data source.
     *
     * @param   repo   JNDI repository information
     * @return  Data source for the repository
     */
    private DataSource getDataSource(JndiRepository repo) throws NamingException {
        Context initCtx = new InitialContext();
        Context envCtx = (Context) initCtx.lookup("java:comp/env");
        DataSource ds = (DataSource) envCtx.lookup(repo.getName());

        return ds;
    }

    /**
     * Attempt to clean up any invalid connections.
     */
    public void logInvalidConnections() {
        Vector<RepositoryConnection> leaks = getInvalidConnections();
        Enumeration list = leaks.elements();
        while (list.hasMoreElements()) {
            RepositoryConnection rc = (RepositoryConnection) list.nextElement();
            log("Invalid connection detected: " + rc.toString()); 
        }
    }

    /**
     * Summarize the number of connections by repository. 
     *
     * @return  Number of connections
     */
    public Hashtable<DataRepository, Integer> getConnectionCount() {
        Hashtable<DataRepository, Integer> repolist = new Hashtable<DataRepository, Integer>();
        Enumeration list = connectionList.keys();
        while (list.hasMoreElements()) {
            DataRepository repo = (DataRepository) connectionList.get(list.nextElement()); 
            Integer count = new Integer(0);
            if (repolist.containsKey(repo)) {
                if (repolist.get(repo) != null) {
                    count = repolist.get(repo);
                }
            }
            repolist.put(repo, count++);
        }
        return repolist;
    }

    /**
     * Attempt to determine if a connection leak has occurred.
     * This method examines the threads running on the system with
     * the connections created by those threads.  If a connection
     * does not have a corresponding thread, this method assumes
     * that a leak has occured and returns the list of connections
     * that are believed to no longer be valid.  The assumption
     * is that if the thread which created the connection no longer
     * exists, no one will close the connection. 
     *
     * If no leaks are detected, the list will be empty.
     *
     * @return  List of invalid connections
     */
    public Vector<RepositoryConnection> getInvalidConnections() {
        Vector<RepositoryConnection> leaks = new Vector<RepositoryConnection>();

        // Examine each repository connection
        Enumeration list = connectionList.keys();
        while (list.hasMoreElements()) {
            RepositoryConnection rc = (RepositoryConnection) list.nextElement();
            if (!isValid(rc)) {
                leaks.add(rc);
            }
        }

        return leaks;
    }

    /**
     * Examine the connection to determine if it should be 
     * considered as a connection leak.  A connection would
     * be considered as a "leak" if the thread that created
     * it no longer exists in the system.
     *
     * @param  rc   Repository connection being examined
     * @return TRUE if the connection is valid
     */
    private boolean isValid(RepositoryConnection rc) {
        ThreadMXBean threads = ManagementFactory.getThreadMXBean(); 
        long[] ids = threads.getAllThreadIds();
        for (int idx = 0; idx < ids.length; idx++) {
            if (ids[idx] == rc.getThreadId()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Log information about each of the repositories.
     */
    public void logDbInfo() {
        Enumeration list = repositoryList.elements();
        while (list.hasMoreElements()) {
            log((DataRepository) list.nextElement());
        }
    }

    /**
     * Log information about each of the repositories.
     */
    public void logConnectionInfo() {
        Enumeration list = connectionList.keys();
        while (list.hasMoreElements()) {
            log((RepositoryConnection) list.nextElement());
        }
    }


    /**
     * Write log messages to the log or to System.err if a log is not available.
     * 
     * @param  message   Text to write to the log
     */
    private void log(String message) {
        if (repositoryLog != null) {
            repositoryLog.println(message);
        } else {
            System.err.println(message);
        }
    }

    /**
     * Log information about the repository connection.
     *
     * @param   rc   Repository connection
     */
    private void log(RepositoryConnection rc) {
        StringBuffer sb = new StringBuffer();

        if (rc != null) {
            log("Repository connection: " + rc.toString());
        } else {
            log("Repository connection is null.");
        }
    }


 

    /**
     * Log important information about the database.
     *
     * @param  repository   Database information 
     */
    private void log(DataRepository repository) {
        log("Repository information: " + repository.getDescription());
        try {
            RepositoryConnection rc = getDbConnection(repository);
            Connection conn = rc.getConnection();
            if (conn != null) {
                // Make sure the connection is open
                if (conn.isClosed()) {
                    log("Unable to obtain metadata for a closed connection: " + repository.getDescription());
                } else {
                    DatabaseMetaData metadata = conn.getMetaData();
                    log("Database Server: " + metadata.getDatabaseProductName() + " " + metadata.getDatabaseProductVersion());
                    log("Database Driver: " + metadata.getDriverName() + " " + metadata.getDriverVersion());
                    log("Database URL:    " + metadata.getURL());
                    log("Max Connections: " + metadata.getMaxConnections());
                }
            }
        } catch (NamingException nex) {
            log("Failed to obtain a connection to the repository: " + repository.getDescription());
        } catch (SQLException sqlex) {
            log("Failed to log repository metadata for " + repository.getDescription());
        }
    }

}
