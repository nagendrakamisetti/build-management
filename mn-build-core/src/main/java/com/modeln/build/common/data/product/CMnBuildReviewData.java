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

import java.util.Date;
import java.util.HashSet;

import com.modeln.build.common.data.account.UserData;

/**
 * Data object used to represent a review of the product build
 * by a technical owner. 
 * 
 * @author  Shawn Stafford
 */
public class CMnBuildReviewData {

    public static enum ReviewStatus {
        APPROVED, REJECTED
    }


    /** Foreign key used to identify the build */
    private int buildId = 0;

    /** Foreign key used to identify the product area */
    private int areaId = 0;

    /** Date when the review was last updated */
    private Date reviewDate = null;

    /** User who entered the review */
    private UserData user = null;

    /** Review status */
    private ReviewStatus status = ReviewStatus.APPROVED;

    /** Comments about the review */
    private String comment = null;


    /**
     * Constructor.
     */
    public CMnBuildReviewData() {
    }


    /**
     * Set the foreign key value used to identify the build associated with this review. 
     *
     * @param    id     Build ID
     */
    public void setBuildId(int id) {
        buildId = id;
    }

    /**
     * Return the build ID associated with this review. 
     *
     * @return  Build ID 
     */
    public int getBuildId() {
        return buildId;
    }

    /**
     * Set the date when the review was last updated. 
     *
     * @param    date   Review date
     */
    public void setDate(Date date) {
        reviewDate = date;
    }

    /**
     * Return the date when the review was last updated.
     *
     * @return  Review date 
     */
    public Date getDate() {
        return reviewDate;
    }


    /**
     * Set the user associated with this review. 
     *
     * @param   data    User information
     */
    public void setUser(UserData data) {
        user = data;
    }

    /**
     * Return the user associated with this review. 
     *
     * @return  User information
     */
    public UserData getUser() {
        return user;
    }


    /**
     * Set the foreign key value used to identify the area associated with this review. 
     *
     * @param    id     Area ID
     */
    public void setAreaId(int id) {
        areaId = id;
    }

    /**
     * Return the area ID associated with this review. 
     *
     * @return  Area ID 
     */
    public int getAreaId() {
        return areaId;
    }


    /**
     * Set the status of the item being reviewed. 
     *
     * @param    status  Review status 
     */
    public void setStatus(ReviewStatus status) {
        this.status = status;
    }

    /**
     * Set the status of the item being reviewed. 
     *
     * @param    status  Reivew status 
     */
    public void setStatus(String status) {
        if (status != null) {
            this.status = ReviewStatus.valueOf(status.toUpperCase());
        } else {
            this.status = null;
        }
    }


    /**
     * Return the status of the item being reviewed 
     *
     * @return  Review status 
     */
    public ReviewStatus getStatus() {
        return status;
    }


    /**
     * Set the review comments. 
     *
     * @param    text   Review comments 
     */
    public void setComment(String text) {
        comment = text;
    }

    /**
     * Return the review comments. 
     *
     * @return  Review comments 
     */
    public String getComment() {
        return comment;
    }


}

