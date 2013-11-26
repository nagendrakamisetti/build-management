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

import java.util.Date;

import com.modeln.build.common.data.account.UserData;


/**
 * Comments relating to the service patch assignment. 
 *
 * @version      1.0
 * @author       Shawn Stafford (sstafford@modeln.com)
 */
public class CMnPatchOwner {

    public static enum PatchPriority {
        LOW, MEDIUM, HIGH
    }


    /** Information about the patch owner */
    private UserData user;

    /** Identifies the patch assigned to the user */
    private int patchId;

    /** Record the starting time of the test suite */
    private Date startDate;

    /** Record the completion time of the test suite */
    private Date endDate;

    /** ETA or deadline for the patch */
    private Date deadline;

    /** Relative priority of the patch request */
    private PatchPriority priority = PatchPriority.LOW;



    /** Assignment comments */
    private String comment;



    /**
     * Set the ID used to look-up the patch.
     *
     * @param  id   Unique ID of the patch
     */
    public void setPatchId(int id) {
        patchId = id;
    }

    /**
     * Return the ID used to look-up the patch.
     *
     * @return ID for the patch
     */
    public int getPatchId() {
        return patchId;
    }



    /**
     * Set the user who is assigned to work on the patch. 
     *
     * @param  data  user data
     */
    public void setUser(UserData data) {
        user = data;
    }

    /**
     * Return the user who is assigned to work on the patch. 
     *
     * @return User data
     */
    public UserData getUser() {
        return user;
    }


    /**
     * Set the starting time of the build.
     * 
     * @param   date    Starting time
     */
    public void setStartDate(Date date) {
        startDate = date;
    }

    /**
     * Return the starting time of the build.
     * 
     * @hibernate.property column="start_date" type="timestamp" not-null="true"
     *
     * @return  Starting time
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * Set the ending time of the build.
     * 
     * @param   date    Ending time
     */
    public void setEndDate(Date date) {
        endDate = date;
    }

    /**
     * Return the ending time of the build.
     * 
     * @hibernate.property column="end_date" type="timestamp" not-null="true"
     *
     * @return  Ending time
     */
    public Date getEndDate() {
        return endDate;
    }


    /**
     * Set the ETA or deadline for the completed build. 
     * 
     * @param   date    Deadline for the build 
     */
    public void setDeadline(Date date) {
        deadline = date;
    }

    /**
     * Return the ETA or deadline for the completed build. 
     * 
     * @return  Deadline for a completed build 
     */
    public Date getDeadline() {
        return deadline;
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


    /**
     * Set the patch priority. 
     *
     * @param    priority  Patch priority 
     */
    public void setPriority(String priority) {
        if (priority != null) {
            this.priority = PatchPriority.valueOf(priority.toUpperCase());
        } else {
            this.priority = null;
        }
    }


    /**
     * Return the patch priority 
     *
     * @return  Patch priority 
     */
    public PatchPriority getPriority() {
        return priority;
    }


    /**
     * Return the list of possible priorities as an array.
     *
     * @return List of priorities
     */
    public static String[] getPriorityList() {
        PatchPriority[] list = PatchPriority.values();
        String[] strList = new String[list.length];
        int idx = 0;
        while (idx < list.length) {
            strList[idx] = list[idx].toString(); 
            idx++;
        }
        return strList;
    }


}

