/*
* Copyright 2000-2003 by Model N, Inc.  All Rights Reserved.
*
* This software is the confidential and proprietary information
* of Model N, Inc ("Confidential Information").  You shall not
* disclose such Confidential Information and shall use it only
* in accordance with the terms of the license agreement you
* entered into with Model N, Inc.
*/
package com.modeln.testfw.reporting;

import java.util.Comparator;
import java.util.Date;


/**
 * Comparator for comparing builds by ID. 
 *
 * @author  Shawn Stafford
 */
public class CMnBuildIdComparator implements Comparator {

    /**
     * Compare the build ID of two builds.  
     * Returns 0 if the build ID of both builds are equal.
     * Returns a negative integer if the build ID of build1 is less than build2 
     * Returns a positive integer if the build ID of build1 is greater than build2 
     */
    public int compare(Object build1, Object build2) {
        Integer id1 = new Integer(((CMnDbBuildData) build1).getId());
        Integer id2 = new Integer(((CMnDbBuildData) build2).getId());
        return id1.compareTo(id2);
    }

}

