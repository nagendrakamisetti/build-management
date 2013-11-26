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

import com.modeln.build.common.enums.CMnServicePatch;
import com.modeln.build.common.data.account.UserData;


/**
 * Service patch comments that allow users to enter additional
 * information about the request or provide status updates. 
 *
 * @version      1.0
 * @author       Shawn Stafford (sstafford@modeln.com)
 */
public class CMnPatchComment {

    /** Auto-generated ID used to identify the comment */
    private Integer id;

    /** Information about the commenter */
    private UserData user;

    /** Comment date */
    private Date date;

    /** Comment status */
    private CMnServicePatch.CommentStatus commentStatus;

    /** Comment text */
    private String comment;


    /**
     * Set the ID used to look-up the comment.
     *
     * @param  id   Unique ID of the comment
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Return the ID used to look-up the comment.
     *
     * @return ID for the comment
     */
    public Integer getId() {
        return id;
    }



    /**
     * Set the user who submitted the comment. 
     *
     * @param  data  user data
     */
    public void setUser(UserData data) {
        user = data;
    }

    /**
     * Return the user who submitted the comment. 
     *
     * @return User data
     */
    public UserData getUser() {
        return user;
    }


    /**
     * Set the comment date.
     *
     * @param  date   comment date
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Return the comment date.
     *
     * @return comment date
     */
    public Date getDate() {
        return date;
    }


    /**
     * Set the comment status.
     *
     * @param  st   comment status
     */
    public void setStatus(String st) {
        commentStatus = CMnServicePatch.getCommentStatus(st);
    }


    /**
     * Set the comment status. 
     *
     * @param  st   comment status 
     */
    public void setStatus(CMnServicePatch.CommentStatus st) {
        commentStatus = st;
    }

    /**
     * Return the comment status. 
     *
     * @return comment status 
     */
    public CMnServicePatch.CommentStatus getStatus() {
        return commentStatus;
    }


    /**
     * Set comments regarding the patch.
     *
     * @param  text   Comments about the patch
     */
    public void setComment(String text) {
        comment = text;
    }

    /**
     * Return comment about the patch.
     *
     * @return comments about the patch
     */
    public String getComment() {
        return comment;
    }


}

