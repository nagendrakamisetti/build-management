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

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import javax.mail.internet.InternetAddress;


/**
 * Information about a notification request triggered by a 
 * change in service patch status. 
 *
 * @version      1.0
 * @author       Shawn Stafford (sstafford@modeln.com)
 */
public class CMnPatchNotification {

    /** Auto-generated ID used to identify the service patch */
    private Integer id;

    /** User ID used to locate user who should be notified */
    private Integer userId;

    /** User e-mail address where they should be notified */
    private InternetAddress address;

    /** Customer ID that the notification is tied to (if any) */
    private Integer custId;

    /** Build version string */
    private String buildVersion;

    /** Patch status */
    private Vector<CMnServicePatch.RequestStatus> status;



    /**
     * Set the ID used to look-up the notification.
     *
     * @param  id   Unique ID of the notification
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Return the ID used to look-up the notification.
     *
     * @return ID for the patch
     */
    public Integer getId() {
        return id;
    }

    /**
     * Set the User ID used to look-up the users for notification.
     *
     * @param  id   User ID 
     */
    public void setUserId(Integer id) {
        userId = id;
    }

    /**
     * Return the User ID used to look-up the user for notification.
     *
     * @return User ID 
     */
    public Integer getUserId() {
        return userId;
    }


    /**
     * Set the User email address where they should recieve notification. 
     *
     * @param  addr   User email address 
     */
    public void setUserEmail(InternetAddress addr) {
        address = addr;
    }

    /**
     * Return the User email address where they should recieve notification. 
     *
     * @return User email
     */
    public InternetAddress getUserEmail() {
        return address;
    }



    /**
     * Set the customer ID that should trigger a notification.
     *
     * @param  id   Customer ID 
     */
    public void setCustomerId(Integer id) {
        custId = id;
    }

    /**
     * Return the customer ID that should trigger a the notification.
     *  
     * @return Customer ID 
     */
    public Integer getCustomerId() {
        return custId;
    }



    /**
     * Set the build version string for matching the notification. 
     * The version string should not be a full version string of
     * a single build, but rather a partial version string that 
     * can be used to match a set of builds.
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


    /**
     * Add a status to the list.
     *
     * @param  value   Status value
     */
    public void addStatus(CMnServicePatch.RequestStatus value) {
        if (status == null) {
            status = new Vector<CMnServicePatch.RequestStatus>();
        }
        status.add(value);
    }

    /**
     * Determine if the specified status is included in the list
     *
     * @param  value   Status value
     * @return TRUE if the status requires notification
     */
    public boolean hasStatus(CMnServicePatch.RequestStatus value) {
        if (status != null) {
            return status.contains(value);
        } else {
            return false;
        }
    }


}


