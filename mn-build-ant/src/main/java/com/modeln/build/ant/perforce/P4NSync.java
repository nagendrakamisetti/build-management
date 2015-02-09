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
import org.apache.tools.ant.BuildException;

import com.modeln.build.perforce.Sync;

/**
 * Obtains a list of outdated files that need to be retrieved
 * from Perforce.
 *
 * @author Shawn Stafford
 */
public final class P4NSync extends Task {

    /** Depot location being queried */
    private String depotPath = "";


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
        int num = Sync.getChangeCount(depotPath);
        getProject().setProperty("p4.filecount",  "" + num);
    }

}
