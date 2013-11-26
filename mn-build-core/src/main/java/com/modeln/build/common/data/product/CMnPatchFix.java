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

import com.modeln.build.sourcecontrol.CMnCheckIn;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;


/**
 * Service patch request information such as customer, fixes, and other
 * information required to produce a service patch.
 *
 * @version      1.0
 * @author       Shawn Stafford (sstafford@modeln.com)
 */
public class CMnPatchFix extends CMnBaseFix {

    /** Service patch where the fix first appeared. */
    private CMnPatch origin;


    /**
     * Set the patch where the fix first appeared. 
     *
     * @param  patch   Service patch data 
     */
    public void setOrigin(CMnPatch patch) {
        origin = patch;
    }

    /**
     * Return the patch where the fix first appeared. 
     * If the fix is new, the origin will be null.  If the fix was carried
     * forward from a previous service patch then this patch can be ussed to
     * locate information about the patch.
     *
     * @return Service patch data 
     */
    public CMnPatch getOrigin() {
        return origin;
    }

}
