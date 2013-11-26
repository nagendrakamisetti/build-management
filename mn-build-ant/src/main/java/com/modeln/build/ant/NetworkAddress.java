/*
 * Copyright 2000-2003 by Model N, Inc.  All Rights Reserved.
 * 
 * This software is the confidential and proprietary information
 * of Model N, Inc ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you
 * entered into with Model N, Inc.
 */
package com.modeln.build.ant;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildListener;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Obtains network information about a specific host and stores the 
 * information in Ant properties.
 *
 * @author Shawn Stafford
 */
public final class NetworkAddress extends Task {

    /** Name of the host in question. */
    private String hostname = null;

    /** Determines whether the network information should be written to a log file. */
    private boolean enableLog = true;

    /**
     * Set the hostname used when obtaining network information.
     * If no hostname is specified, the localhost information will be obtained.
     *
     * @param   name    Hostname of the computer
     */
    public void setHost(String name) {
        hostname = name;
    }

    /**
     * This boolean determines whether the network information is 
     * written to stdout.
     *
     * @param   enable  If TRUE, Write network information to the log
     */
    public void setEcho(Boolean enable) {
        enableLog = enable.booleanValue();
    }

    /**
     * Obtain the network information and save it in property values.
     * The host name can be retrieved from the ${host.name} property and
     * the host address can be retrieved from the ${host.address} property.
     *
     * @throws  BuildException if the network information cannot be obtained
     */
    public void execute() throws BuildException {
        try {
            InetAddress host = null;
            if (hostname != null) {
                host = InetAddress.getByName(hostname);
            } else {
                host = InetAddress.getLocalHost();
            }

            // Set the project properties.
            String name = host.getHostName();
            String addr = host.getHostAddress();
            getProject().setProperty("host.name",    "" + name);
            getProject().setProperty("host.address", "" + addr);

            // Display the network information in the logs
            if (enableLog) {
                log("host.name = " + name);
                log("host.address = " + addr);
            }
        } catch (UnknownHostException ex) {
            throw new BuildException("Unable to obtain host information for host: " + hostname, ex, getLocation());
        }
    }

}
