/*
 * SecureSocketAppender.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.common.logging;

import java.net.Socket;
import java.io.*;

import org.apache.log4j.*;
import org.apache.log4j.spi.*;

/**
 * This class contains utility methods which are used to create
 * application logs.
 *
 * @version            $Revision: 1.1.1.1 $
 * @author             Shawn Stafford
 *
 */
public class SecureSocketAppender extends AppenderSkeleton implements SecureAppender {

    /**
     * The character encoding determines what encoding scheme is used
     * when writing characters to the socket.
     */
    private String charEncoding = "US-ASCII";

    /**
     * The socket to be written to.
     */
    private Socket appSocket;

    /**
     * The output stream which does the actual writing.
     */
    private PrintWriter writer;

    /** Security level for this event. */
	private SecurityLevel securityLevel = SecurityLevel.SSL;

    /**
     * Immediate flush means that the undelying writer or stream will be
     * flushed at the end of each append operation. Immediate flush is
     * slower but ensures that each append request is actually
     * written. If <code>immediateFlush</code> is set to
     * <code>false</code>, then there is a good chance that the last few
     * logs events are not actually written to persistent media when the
     * application crashes.
     * <p>
     * The <code>immediateFlush</code> variable is set to
     * <code>true</code> by default.
     */
    private boolean immediateFlush = true;

    /**
     * Constructs an appender to write to the given socket.
     *
     * @param layout Text formatting used when writing to the socket
     * @param socket Socket to which the output will be written
     */
    public SecureSocketAppender(Layout layout, Socket socket) throws IOException {
        OutputStreamWriter stream;
        try {
            stream = new OutputStreamWriter(socket.getOutputStream(), charEncoding);
        } catch (UnsupportedEncodingException e) {
            stream = new OutputStreamWriter(socket.getOutputStream());
        }
        writer = new PrintWriter(stream);

        this.layout = layout;
        this.appSocket = socket;
    }

    /**
     * Writes the event to the socket.
     *
     * @param event     Event information to be sent to the socket
     */
    public void append(LoggingEvent event) {
System.err.println("Attempting to append to the socket...");
        // Write the event to the stream
        writer.write(this.layout.format(event));

        // Determine if exceptions should be handled by the layout
        if (layout.ignoresThrowable()) {
            ThrowableInformation info = event.getThrowableInformation();
            if (info != null) {
                Throwable error = info.getThrowable();
                if (error != null) {
                    writer.write(error.toString());
                }
            }
        }
     
        // Flush output to the socket
        if(immediateFlush) {
System.err.println("Flushing socket buffer.");
            writer.flush();
        } 
    }
          

    /**
     * Release any resources allocated within the appender such as file
     * handles, network connections, etc.
     * <p>
     * It is a programming error to append to a closed appender.
     */
    public void close() {
        writer.close();
    }
  

    /**
     * Configurators call this method to determine if the appender
     * requires a layout. If this method returns <code>true</code>,
     * meaning that layout is required, then the configurator will
     * configure a layout using the configuration information at its
     * disposal.  If this method returns <code>false</code>, meaning that
     * a layout is not required, then layout configuration will be
     * skipped even if there is available layout configuration
     * information at the disposal of the configurator.
     * <p>
     * In the rather exceptional case, where the appender
     * implementation admits a layout but can also work without it, then
     * the appender should return <code>true</code>.
     */
    public boolean requiresLayout() {
        return true;
    }

    /**
     * Sets the security level for the appender.  When a SecureCategory 
     * class attempts to write to an appender, it should only append 
     * messages to appenders which are at least as secure as the security
     * level assigned to the current message.
     *
     * @param   SecurityLevel     Security level
     *
     * @see     SecurityLevel
     */
    public void setSecurityLevel(SecurityLevel level) {
        securityLevel = level;
    }

    /**
     * Returns the current security level for this appender.
     *
     * @return  SecurityLevel Security level
     *
     * @see     SecurityLevel
     */
    public SecurityLevel getSecurityLevel() {
        return securityLevel;
    }

}
