/*
 * Emulator.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.protocol;

/**
 * The Emulator interface defines the methods which will be used to
 * handle input and output from a Telnet session.
 *
 * @version            $Revision: 1.1.1.1 $
 * @author             Shawn Stafford
 *
 */
public interface Emulator {

    /**
     * Handles a string of text received from the server.
     * 
     * @param   text    Text received
     */
    public void received(String text);

    /**
     * Handles a string of text sent from the client.
     * 
     * @param   text    Text sent
     */
    public void sent(String text);

    /**
     * Handles locally generated user messages.
     * 
     * @param   text    Text message
     */
    public void showMessage(String text);

    /**
     * Handles debugging messages.
     * 
     * @param   text    Text message
     */
    public void showDebug(String text);

    /**
     * Handles exceptions which occur during execution.
     * This usually includes dumping a stack trace during
     * debugging.
     * 
     * @param   text    Text message
     */
    public void showException(Exception ex);

}
