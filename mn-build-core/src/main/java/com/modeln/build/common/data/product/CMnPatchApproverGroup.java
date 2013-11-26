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

import com.modeln.build.common.enums.CMnServicePatch;
import com.modeln.build.common.data.account.GroupData;


/**
 * Information about a group's rights to approve service patches which match 
 * the build version string. 
 *
 * @version      1.0
 * @author       Shawn Stafford (sstafford@modeln.com)
 */
public class CMnPatchApproverGroup {

    /** Information about the group */
    private GroupData group;

    /** Service patch request status */
    private CMnServicePatch.RequestStatus status;

    /** Build version string */
    private String buildVersion;


    /**
     * Set the user group responsible for the approval entry. 
     *
     * @param  data  group data
     */
    public void setGroup(GroupData data) {
        group = data;
    }

    /**
     * Return the group responsible for the approval entry. 
     *
     * @return Group data
     */
    public GroupData getGroup() {
        return group;
    }


    /**
     * Set the service patch request status.
     *
     * @param  st   Request status
     */
    public void setStatus(String st) {
        status = CMnServicePatch.getRequestStatus(st);
    }


    /**
     * Set the service patch request status. 
     *
     * @param  st   Request status 
     */
    public void setStatus(CMnServicePatch.RequestStatus st) {
        status = st;
    }

    /**
     * Return the service patch request status. 
     *
     * @return Approval status 
     */
    public CMnServicePatch.RequestStatus getStatus() {
        return status;
    }

    /**
     * Determine if the request matches the specified status. 
     *
     * @return TRUE if the request status matches the specified status 
     */
    public boolean equals(CMnServicePatch.RequestStatus st) {
        return (status == st);
    }


    /**
     * Set the build version string for matching the approval. 
     *
     * @param  version   Build version string 
     */
    public void setBuildVersion(String version) {
        buildVersion = version;
    }

    /**
     * Return the build version. 
     *
     * @return build version 
     */
    public String getBuildVersion() {
        return buildVersion;
    }


}

