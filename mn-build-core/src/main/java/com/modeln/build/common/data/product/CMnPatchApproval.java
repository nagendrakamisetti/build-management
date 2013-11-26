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
import com.modeln.build.common.data.account.UserData;


/**
 * Service patch approval information that represents the approval
 * status provided by an individual within the approval chain. 
 *
 * @version      1.0
 * @author       Shawn Stafford (sstafford@modeln.com)
 */
public class CMnPatchApproval {

    /** Information about the approver */
    private UserData user;

    /** Approval status */
    private CMnServicePatch.ApprovalStatus approvalStatus;

    /** Service patch status for which the approval applies */
    private CMnServicePatch.RequestStatus patchStatus;

    /** Approval comments */
    private String comment;


    /**
     * Set the user who submitted the approval entry. 
     *
     * @param  data  user data
     */
    public void setUser(UserData data) {
        user = data;
    }

    /**
     * Return the user who submitted the approval entry. 
     *
     * @return User data
     */
    public UserData getUser() {
        return user;
    }


    /**
     * Set the approval status.
     *
     * @param  st   Approval status
     */
    public void setStatus(String st) {
        approvalStatus = CMnServicePatch.getApprovalStatus(st);
    }


    /**
     * Set the approval status. 
     *
     * @param  st   Approval status 
     */
    public void setStatus(CMnServicePatch.ApprovalStatus st) {
        approvalStatus = st;
    }

    /**
     * Return the approval status. 
     *
     * @return Approval status 
     */
    public CMnServicePatch.ApprovalStatus getStatus() {
        return approvalStatus;
    }

    /**
     * Set the patch request status.
     *
     * @param  st   Request status
     */
    public void setPatchStatus(String st) {
        patchStatus = CMnServicePatch.getRequestStatus(st);
    }


    /**
     * Set the request status. 
     *
     * @param  st   Request status 
     */
    public void setPatchStatus(CMnServicePatch.RequestStatus st) {
        patchStatus = st;
    }

    /**
     * Return the request status. 
     *
     * @return Request status 
     */
    public CMnServicePatch.RequestStatus getPatchStatus() {
        return patchStatus;
    }


    /**
     * Determine if approval has been granted.
     *
     * @return TRUE if the appoval status allows the patch to be performed
     */
    public boolean isApproved() {
        return (approvalStatus == CMnServicePatch.ApprovalStatus.APPROVED);
    }


    /**
     * Set comments regarding the patch approval.
     *
     * @param  text   Comments about the approval
     */
    public void setComment(String text) {
        comment = text;
    }

    /**
     * Return comment about the patch approval.
     *
     * @return comments about the approval
     */
    public String getComment() {
        return comment;
    }


}

