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

import org.apache.tools.ant.Task;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.email.EmailAddress;
import org.apache.tools.ant.taskdefs.email.EmailTask;
import org.apache.tools.mail.MailMessage;

import com.modeln.build.ant.Notification;

/**
 * Contains notification information for reports.  At the moment, this
 * notification only supports e-mail.  However, future implementations
 * may wish to use other protocols.  The interface has been designed
 * with this in mind, but may need some additional thought and redesign
 * to accomodate this.
 *
 * @author Shawn Stafford
 */
public final class ReportNotification extends Notification {


    /** Send the notification if the build fails */
    public static final String BUILD_FAILURE = "fail";

    /** Send the notification if the build succeeds */
    public static final String BUILD_SUCCESS = "success";

    /** Send the notification if the build completes, regardless of it status */
    public static final String BUILD_COMPLETION = "complete";

    /** Event that will trigger the notification */
    private static final String[] NOTIFICATION_EVENTS = { BUILD_FAILURE, BUILD_SUCCESS, BUILD_COMPLETION };



    /** Type of build event that will trigger a notification */
    private int notificationEvent = 0;


    /**
     * Set the type of event which will trigger a notification.
     *
     * @param   type    Type of build event
     */
    public void setIf(String event) throws BuildException {
        boolean validEvent = false;
        for (int idx = 0; idx < NOTIFICATION_EVENTS.length; idx++) {
            if (event.equalsIgnoreCase(NOTIFICATION_EVENTS[idx])) {
                notificationEvent = idx;
                validEvent = true;
            }
        }

        // Make sure the user has specified an appropriate event type
        if (!validEvent) {
            throw new BuildException("Invalid notification event: " + event);
        }
    }

    /**
     * Return the notification event that will be used.
     */
    public String getIf() {
        return NOTIFICATION_EVENTS[notificationEvent];
    }

    /**
     * Determine if the notification is of the specified type.
     *
     * @param   event   Notification event type
     * @return  True if the notification matches the specified type.
     */
    public boolean equalsIf(String event) {
        return (event.equalsIgnoreCase(NOTIFICATION_EVENTS[notificationEvent]));
    }


    /**
     * Determine if the notification should be sent if an error was encountered.
     *
     * @param   event   Build event which triggered the notification
     * @return  True if the notification should be sent
     * @throws  BuildException if the notification condition cannot be resolved.
     */
    private boolean shouldSendNotification(BuildEvent event) throws BuildException {
        boolean failure = (event.getException() != null);

        // Always send on completion
        if (equalsIf(BUILD_COMPLETION)) {
            return true;
        } else if (equalsIf(BUILD_FAILURE)) {
            return(failure);
        } else if (equalsIf(BUILD_SUCCESS)) {
            return(!failure);
        } else {
            throw new BuildException("Unable to resolve notification condition.");
        }
    }


    /**
     * Compose the notification message and send using the appropriate
     * protocol.
     *
     * @param   event   Build event which triggered the notification
     */
    public void sendNotification(BuildEvent event) throws BuildException {

        // Determine if the notification should be sent at all
        if (shouldSendNotification(event)) {
            try {
                super.sendNotification(event);            	
            } catch (BuildException ex) {
                ex.printStackTrace();
                throw ex;
            }
        }
    }


}
