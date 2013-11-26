/*
* Copyright 2000-2003 by Model N, Inc.  All Rights Reserved.
*
* This software is the confidential and proprietary information
* of Model N, Inc ("Confidential Information").  You shall not
* disclose such Confidential Information and shall use it only
* in accordance with the terms of the license agreement you
* entered into with Model N, Inc.
*/
package com.modeln.build.common.enums;

/**
 * This class contains enums related to service patches. 
 *
 * @author  Shawn Stafford
 */
public class CMnServicePatch {

    /** List of possible service patch status */
    public static enum RequestStatus {
        SAVED, APPROVAL, REJECTED, PENDING, CANCELED, RUNNING, BRANCHING, BRANCHED, BUILDING, BUILT, FAILED, COMPLETE, RELEASE
    }

    /** List of possible approval status */
    public static enum ApprovalStatus {
        APPROVED, REJECTED
    }

    /** List of severity requirements associated with a fix or group of fixes */
    public static enum FixRequirement {
        OPTIONAL, RECOMMENDED, REQUIRED
    }

    /** List of comment visibility status */
    public static enum CommentStatus {
        SHOW, HIDE, ADMIN
    }


    /** 
     * Return request status by parsing the string.
     * 
     * @param  str   Request Status string
     * @return Request Status enum value
     */
    public static RequestStatus getRequestStatus(String str) {
        if (str != null) {
            return RequestStatus.valueOf(str.toUpperCase());
        } else {
            return null;
        }
    }

    /**
     * Return an approval status by parsing the string.
     *
     * @param  str   Approval Status string
     * @return Approval Status enum value
     */
    public static ApprovalStatus getApprovalStatus(String str) {
        if (str != null) {
            return ApprovalStatus.valueOf(str.toUpperCase());
        } else {
            return null;
        }
    }

    /**
     * Return a comment status by parsing the string.
     *
     * @param  str   Comment Status string
     * @return Comment Status enum value
     */
    public static CommentStatus getCommentStatus(String str) {
        if (str != null) {
            return CommentStatus.valueOf(str.toUpperCase());
        } else {
            return null;
        }
    }



    /**
     * Return the list of possible comment status values as an array.
     *
     * @return List of status values
     */
    public static String[] getCommentStatusList() {
        CommentStatus[] list = CommentStatus.values();
        String[] strList = new String[list.length];
        int idx = 0;
        while (idx < list.length) {
            strList[idx] = list[idx].toString();
            idx++;
        }
        return strList;
    }

}


