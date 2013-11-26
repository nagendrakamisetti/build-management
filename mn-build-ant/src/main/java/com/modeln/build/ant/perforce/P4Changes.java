/*
 * Copyright 2000-2003 by Model N, Inc.  All Rights Reserved.
 * 
 * This software is the confidential and proprietary information
 * of Model N, Inc ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you
 * entered into with Model N, Inc.
 */
package com.modeln.build.ant.perforce;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildListener;

import java.util.Date;
import com.modeln.build.perforce.Changelist;
import com.modeln.build.perforce.Client;
import com.modeln.build.perforce.Common;

/**
 * Obtains information about the most recent changelist.
 *
 * @author Shawn Stafford
 */
public final class P4Changes extends Task {

    /** Depot location being queried */
    private String depotPath;

    /** Determines whether the local clientspec should be used to limit queries */
    private boolean useLocal = false;

    /**
     * Set the UseLocal value to TRUE if the changelist query should only be
     * performed against the local clientsped.  Otherwise, changelist information
     * will be returned with respect to the current status of the server.
     *
     * @param   status    Query against the local clientspec 
     */
    public void setUseLocal(Boolean status) {
        useLocal = status.booleanValue();
    }

    /**
     * Set the path to the files or depot location being queried.
     *
     * @param   path    Depot path
     */
    public void setPath(String path) {
        depotPath = path;
    }

    /**
     * Obtain the most recent changelist information and save the information
     * as property values.
     *
     * @throws  BuildException if the changelist information cannot be obtained
     */
    public void execute() throws BuildException {
        Changelist change = null;
        if (useLocal) {
            Client client = Client.getClient();
            if (client != null) {
                change = Changelist.getChange(depotPath, client);
            } else {
                throw new BuildException("Unable to obtain clientspec information.", getLocation());
            }
        } else {
            change = Changelist.getChange(depotPath);
        }
        if (change != null) {
            int num = change.getNumber();
            getProject().setProperty("p4.changelist",  "" + num);

            Date date = change.getDate();
            if (date != null) {
                getProject().setProperty("p4.date", "" + Common.SHORT_DATE_FORMAT.format(date));
            } else {
                getProject().setProperty("p4.date", "");
            }

            String desc = change.getDescription();
            if (desc != null) {
                getProject().setProperty("p4.description", desc);
            } else {
                getProject().setProperty("p4.description", "");
            }

            String user = change.getUser();
            if (user != null) {
                getProject().setProperty("p4.user", user);
            } else {
                getProject().setProperty("p4.user", "");
            }

            String client = change.getClient();
            if (client != null) {
                getProject().setProperty("p4.client", client);
            } else {
                getProject().setProperty("p4.client", "");
            }

        }
    }

}
