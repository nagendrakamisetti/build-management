
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

import java.io.File;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.FileSet;

import org.apache.tools.ant.taskdefs.email.EmailAddress;
import org.apache.tools.ant.taskdefs.email.EmailTask;
import org.apache.tools.ant.taskdefs.email.Mailer;
import org.apache.tools.ant.taskdefs.email.Message;
import org.apache.tools.ant.taskdefs.email.MimeMailer;

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
