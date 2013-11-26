/*
 * SecureLogServer.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.common.logging;

import java.net.*;
import javax.net.ServerSocketFactory;
import javax.net.ssl.*;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.InterruptedIOException;

/**
 * This class accepts creates a server which accepts socket connections
 * from log clients.  Once a socket connection is established, the server
 * adds the socket connection to the list maintained by the Log Manager.
 *
 * @version            $Revision: 1.1.1.1 $
 * @author             Shawn Stafford
 *
 */
public class SecureLogServer implements Runnable {

    /**
     * The parent SecureLog who must be notified when a connection is accepted.
     */
    private SecureLog log;

    /**
     * The server socket allows a user to connect to that port and view the
     * application logs as they are generated.
     */
    private ServerSocket logServer;

    /**
     * Determines if the server is currently accepting connections
     */
    private boolean serverActive = true;

    /**
     * This is the interval of time (in milliseconds) that the server performs
     * its "am I still active" check.
     */
    private static final int serverTimeout = 60000;

    /**
     * Constructs the server, which listens on the specified port and returns
     * any incoming connections to its parent (SecureLog).
     *
     * @param   parent  Log Manager responsible for maintaining the list of logs
     * @param   port    Port on which the server will listen
     */
    public SecureLogServer(SecureLog parent, int port) throws IOException {
        // Record the parent so callbacks can be made in the run method
        log = parent;

        ServerSocketFactory socketFactory = SSLServerSocketFactory.getDefault();
        logServer = socketFactory.createServerSocket(port);

        // Tell the server how long to wait for incoming connections
        logServer.setSoTimeout(serverTimeout);
    }

    /**
     * Initializes the server socket to listen for connections on the
     * given port.  If a socket connection is established, the application
     * log output will be written to the socket.
     */
    public void run() {
        log.logEntry(this, SecureLog.INFO, 
            "Log server is accepting connections on port " + logServer.getLocalPort(), "");

        while (serverActive) {
            try {
                Socket socket = logServer.accept();
                log.logEntry(this, SecureLog.INFO, "Accepting LogServer client connection.", "");
                PrintWriter out = new PrintWriter(socket.getOutputStream());
                out.println("Welcome to the Member Services log server.");
                log.addOutput(socket);
            } catch (InterruptedIOException ie) {
                // Raised when the setSoTimeout value expires.  
                // The server socket quits accepting connections to check for deactivation.
            } catch (IOException ioe) {
                // An error has occurred while waiting for a connection
                log.logException(this, SecureLog.ERROR, ioe, 
                    "A log server socket error has occurred while waiting for a connection.");
                disable();
            }
        }
    }

    /**
     * Disables the server socket so that connections are no longer
     * accepted.
     */
    public void disable() {
        serverActive = false;
        log.logEntry(this, SecureLog.INFO, "Log server has been disabled.", "");
    }


    /**
     * Enables the server socket so that connections are accepted.
     */
    public void enable() {
        serverActive = true;
        log.logEntry(this, SecureLog.INFO, "Log server has been enabled.", "");
    }


    /**
     * Returns the current status of the server.  If the server is currently
     * accepting socket connections, the method will return <code>true</code>,
     * otherwise the method will return <code>false</code>.
     */
    public boolean isActive() {
        return serverActive;
    }

    /**
     * Returns the port on which the server is listening.
     */
    public int getPort() {
        return logServer.getLocalPort();
    }

}
