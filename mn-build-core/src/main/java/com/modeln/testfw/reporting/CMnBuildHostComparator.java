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
 * Comparator for comparing hostnames. 
 *
 * @author  Shawn Stafford
 */
public class CMnBuildHostComparator implements Comparator {

    /**
     * Compare the hostname of two build machines. 
     */
    public int compare(Object build1, Object build2) {
        CMnDbHostData host1 = ((CMnDbBuildData) build1).getHostData();
        CMnDbHostData host2 = ((CMnDbBuildData) build2).getHostData();

        String hostname1 = host1.getHostname();
        String hostname2 = host2.getHostname();

        // Perform a primary sort by hostname
        if ((hostname1 == null) || (hostname2 == null)) {
            return 0;
        } else if (hostname1.length() == hostname2.length()) {

            // Perform a secondary sort by build start time
            if (hostname1.equalsIgnoreCase(hostname2)) {
                Date start1 = ((CMnDbBuildData) build1).getStartTime();
                Date start2 = ((CMnDbBuildData) build2).getStartTime();
                if ((start1 != null) && (start2 != null)) {
                    return start1.compareTo(start2);
                } else {
                    return 0;
                }
            } else {
                return hostname1.compareToIgnoreCase(hostname2);
            }

        } else {
            // Assume that the length inequality is due to a numerical hostname suffix
            if (hostname1.length() < hostname2.length()) {
                return -1;
            } else {
                return 1;
            }
        }
    }

}

