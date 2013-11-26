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

import java.io.File;

import org.apache.tools.ant.Location;
import org.apache.tools.ant.Target;


/**
 * Contains the information used by DbProgressListener to determine if the 
 * current Ant target matches the desired ant Target. 
 *
 * @author Shawn Stafford
 */
public final class ProgressTarget {

    /** Display name used to identify the Ant target */
    private String displayName;

    /** Name of the Ant target */
    private String targetName;

    /** Ant file containing the target */
    private File targetFile;



    /**
     * Set the display name of the target.
     *
     * @param  name   Target display name 
     */
    public void setDisplayName(String name) {
        displayName = name;
    }

    /**
     * Return the display name of the target.
     *
     * @return Target display name
     */
    public String getDisplayName() {
        return displayName;
    }


    /**
     * Set the name of the ant target.
     *
     * @param  name   Ant target 
     */
    public void setTarget(String name) {
        targetName = name;
    }

    /**
     * Return the name of the ant target. 
     *
     * @return Ant target 
     */
    public String getTarget() {
        return targetName;
    }


    /**
     * Set the name of the file containing the ant target. 
     *
     * @param   file    Ant file 
     */
    public void setFile(File file) {
        targetFile = file;
    }

    /**
     * Determine if the parse target matches the specified ant Target.
     *
     * @param   target    Ant target
     * @return  TRUE if the target matches
     */
    public boolean matches(Target target) {
        boolean match = true;

        if (!targetName.equals(target.getName())) {
            match = false;
        }

        Location location = target.getLocation();
        String targetFileName = targetFile.getAbsolutePath();
        if (!targetFileName.equals(location.getFileName())) {
            match = false;
        }

        return match;
    }

}

