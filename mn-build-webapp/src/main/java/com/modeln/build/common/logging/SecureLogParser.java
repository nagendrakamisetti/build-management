/*
 * SecureLogParser.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.common.logging;

import java.io.*;
import java.net.Socket;
import org.apache.log4j.*;

/**
 * This class provides log analysis methods which are used to analyze
 * SecureLog log files.
 *
 * @version            $Revision: 1.1.1.1 $
 * @author             Shawn Stafford
 *
 */
public class SecureLogParser {

    /** Responsible for reading the log entries. */
    private BufferedInputStream log;

    /** Determines whether log analysis is currently running. */
    private boolean running = false;


    /**
     * Constructs a log parser for analyzing log input.
     */
    public SecureLogParser(InputStream stream) {
        log = new BufferedInputStream(stream);
    }
    
    /**
     * Constructs a log parser for analyzing a local log file.
     * This is merely a convenience method.  The same functionality
     * can be achieved by providing an input stream to the appropriate
     * constructor.
     */
    public SecureLogParser(File logfile) throws FileNotFoundException {
        log = new BufferedInputStream(new FileInputStream(logfile));
    }

    /**
     * Constructs a log parser for analyzing log input being received 
     * from a socket.
     * This is merely a convenience method.  The same functionality
     * can be achieved by providing an input stream to the appropriate
     * constructor.
     */
    public SecureLogParser(Socket sock) throws IOException {
        log = new BufferedInputStream(sock.getInputStream());
    }

    /**
     * Begins analyzing the log.  Analysis will continue until the
     * stopAnalysis method is called.
     */
    public void startAnalysis() {
        running = true;
        while (running) {
            // Process the available input

            // Wait for a while to see if any more input comes in
            //sleep(1000);
        }
    }

    /**
     * Halts log analysis.
     */
    public void stopAnalysis() {
        running = false;
    }

    /**
     * Parse the log entry and return the parsed data as an object.
     * A null is returned if the line cannot be parsed.
     */
    public SecureLogEntry parseLine(String line) {
        SecureLogEntry entry = null;
        try {
            entry = new SecureLogEntry(line);
        } catch (Exception ex) {
        }

        return entry;
    }

}

