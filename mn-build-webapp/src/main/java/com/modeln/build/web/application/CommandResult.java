/*
 * CommandResult.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.web.application;

import java.io.*;
import java.net.URL;
import javax.servlet.*;
import javax.servlet.http.*;

import com.modeln.build.web.errors.*;
import com.modeln.build.web.errors.*;

/**
 * The CommandResult provides information about the results of the
 * command execution.
 * 
 * @version            $Revision: 1.1.1.1 $  
 * @author             Shawn Stafford
 */
public class CommandResult {

    /** If an error was encountered during processing, it will be recorded here. */
    private ApplicationError error;

    /** Destination page where control should ultimately be forwarded */
    private String destination;

    /** Determines whether the HTTP request should be redirected to the destination */
    private boolean redirect = false;

    /**
     * Construct the command result
     */
    public CommandResult() {
    }

    /**
     * Sets the destination page to be a URL and enables HTTP redirection.
     *
     * @param   url   URL to redirect to
     */
    public void setDestination(URL url) {
        if (url != null) {
            destination = url.toString();
            redirect = true;
        } else {
            destination = null;
            redirect = false;
        }
    } 

    /**
     * Sets the destination page to be loaded upon completion.
     */
    public void setDestination(String page) {
        destination = page;
        redirect = false;
    }

    /**
     * Sets the error information if any is available.
     */
    public void setError(ApplicationError error) {
        this.error = error;
    }


    /**
     * Returns the error associated with the result
     */
    public ApplicationError getError() {
        return error;
    }

    /**
     * Determines whether the response contained an error object
     */
    public boolean containsError() {
        return ((error != null) && (error.getErrorCode() != ErrorMap.NO_ERROR));
    }

    /**
     * Returns the destination page.
     */
    public String getDestination() {
        return destination;
    }

    /**
     * Determine if the destination requres a request redirect to the URL.
     *
     * @return TRUE if the destination requires a request redirect
     */
    public boolean useRedirect() {
        return redirect;
    }
}

