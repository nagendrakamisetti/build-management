/*
* Copyright 2000-2003 by Model N, Inc.  All Rights Reserved.
*
* This software is the confidential and proprietary information
* of Model N, Inc ("Confidential Information").  You shall not
* disclose such Confidential Information and shall use it only
* in accordance with the terms of the license agreement you
* entered into with Model N, Inc.
*/
package com.modeln.build.common.data.product;

import java.util.Comparator;
import java.util.Date;


/**
 * Comparator for comparing patches by start and end date. 
 *
 * @author  Shawn Stafford
 */
public class CMnPatchDateComparator implements Comparator {

    /**
     * Compare the start and end dates of two patches.  
     * Returns 0 if the request date of both patches are equal.
     * Returns a negative integer if the first patch was requested before the second patch. 
     * Returns a positive integer if the first patch was requested after the second patch. 
     */
    public int compare(Object patch1, Object patch2) {
        Date time1 = ((CMnPatch) patch1).getRequestDate();
        Date time2   = ((CMnPatch) patch2).getRequestDate();
        if ((time1 != null) && (time2 != null)) {
            return time1.compareTo(time2);
        } else if (time1 != null) {
            return -1;
        } else if (time2 != null) {
            return 1;
        } else {
            return 0;
        }
    }

}


