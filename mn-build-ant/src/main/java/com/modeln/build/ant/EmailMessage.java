
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

import org.apache.tools.ant.taskdefs.email.EmailTask;

/**
 * This task serves as a data object for managing a mail message.
 * Since the primary purpose of the task is to provide a data
 * container for mail information, the execute method can be
 * disabled using the execute attribute of the object.
 *
 * @author Shawn Stafford
 */
public class EmailMessage extends EmailTask {

    /** Sends an email  */
    public void execute() {
    }

    /** Sends an email  */
    public void sendMessage() {
        super.execute();
    }


}
