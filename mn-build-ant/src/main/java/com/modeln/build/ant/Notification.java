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
import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.email.EmailAddress;
import org.apache.tools.ant.taskdefs.email.EmailTask;
import org.apache.tools.mail.MailMessage;



/**
 * Notifications are often sent by build processes to alert other 
 * processes or to e-mail users about the status of the build.  This
 * notification class is used to send build reports, failure alerts,
 * and other event notifications.  At the moment, the notification 
 * class only supports e-mail.  However, future implementations
 * may wish to use other protocols.  The interface has been designed
 * with this in mind, but may need some additional thought and redesign
 * to accomodate this.
 *
 * @author Shawn Stafford
 */
public class Notification extends EmailTask {

    /** Send the notification by e-mail */
    public static final String EMAIL_NOTIFICATION = "email";

    /** Types of notification that can be generated */
    private static final String[] NOTIFICATION_TYPES = { EMAIL_NOTIFICATION };


    /** Type of notification that will be sent */
    private int notificationType = 0;


    /**
     * Set the type of notification to be sent.
     *
     * @param   type    Type of notification to send
     */
    public void setType(String type) throws BuildException {
        boolean validType = false;
        for (int idx = 0; idx < NOTIFICATION_TYPES.length; idx++) {
            if (type.equalsIgnoreCase(NOTIFICATION_TYPES[idx])) {
                notificationType = idx;
                validType = true;
            }
        }

        // Make sure the user has specified an appropriate type
        if (!validType) {
            throw new BuildException("Invalid notification type: " + type);
        }
    }

    /**
     * Return the notification type that will be used.
     */
    public String getType() {
        return NOTIFICATION_TYPES[notificationType];
    }

    /**
     * Determine if the notification is of the specified type.
     */
    private boolean equalsType(String type) {
        return (type.equalsIgnoreCase(NOTIFICATION_TYPES[notificationType]));
    }



    /**
     * Compose the notification message and send using the appropriate
     * protocol.
     *
     * @param   event   Build event which triggered the notification
     */
    public void sendNotification(BuildEvent event) throws BuildException {
        if (equalsType(EMAIL_NOTIFICATION)) {
            log("Sending E-mail notification.");
            super.execute();
        } else {
            throw new BuildException("Unable to send notification.  Unrecognized notification type: " + getType(), getLocation());
        }
    }


    /** 
     * Override the default execute method so that the definition of the 
     * mail task does not trigger the notification.  The notification should
     * be initiated by a build event once the build finishes.  A build 
     * listener is responsible for initiating the notification.
     */
    public void execute() {
        // Do nothing (this is called when the XML is parsed)
        log("Notification.execute has been called.  This method does nothing.", Project.MSG_DEBUG);
    }

}
