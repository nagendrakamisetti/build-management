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

import java.util.HashSet;

/**
 * Data object used to represent ownership over a set of product features. 
 * 
 * @author  Shawn Stafford
 */
public class CMnDbFeatureOwnerData {

    /** Primary key used to identify the area */
    private int areaId = 0;

    /** Display name associated with this group of features */
    private String displayName = null;

    /** E-mail addresses used to notify the owners of this feature set */
    private HashSet<String> addresses = new HashSet<String>();

    /** List of features owned by this area */
    private HashSet<String> features = new HashSet<String>();


    /**
     * Constructor.
     */
    public CMnDbFeatureOwnerData() {
    }


    /**
     * Set the unique key value used to identify a single product area. 
     * This value is typically an auto-incremented value created by the database when
     * a new entry is inserted into the table.
     *
     * @param    id     Key value
     */
    public void setId(int id) {
        areaId = id;
    }

    /**
     * Return the unique key used to identify the area.  A null key value indicates that
     * the test has not been inserted into the database.
     *
     * @return  Key value
     */ 
    public int getId() {
        return areaId;
    }

    /**
     * Set the display name for the feature set.
     *
     * @param  name   display name
     */
    public void setDisplayName(String name) {
        displayName = name;
    }

    /**
     * Return the display name for the feature set.
     *
     * @return   Display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Set the list of features.
     *
     * @param  list   List of features
     */
    public void setFeatures(HashSet<String> list) {
        features = list;
    }

    /**
     * Add a new feature to the list.
     *
     * @param   feature   New feature
     * @return  TRUE if the feature was added to the list
     */
    public boolean addFeature(String feature) {
        boolean success = false;
        if ((feature != null) && (feature.length() > 0)) {
            success = features.add(feature);
        }
        return success;
    }

    /**
     * Determine if the area contains the specified feature.
     *
     * @param   feature   Feature name
     * @return  TRUE if the area contains this feature
     */
    public boolean hasFeature(String feature) {
        if ((features != null) && (features.size() > 0)) {
            return features.contains(feature);
        } else {
            return false;
        } 
    }
 

    /**
     * Set the list of e-mail addresses
     *
     * @param  list   List of e-mail addresses 
     */
    public void setEmailList(HashSet<String> list) {
        addresses = list;
    }

    /**
     * Return the list of e-mail addresses.
     * 
     * @return  List of e-mail addresses
     */
    public HashSet getEmailList() {
        return addresses;
    }

    /**
     * Add a new e-mail address to the list.
     *
     * @param  addr  New email address
     * @return TRUE if the address was added to the list
     */
    public boolean addEmail(String addr) {
        boolean success = false;
        if ((addr != null) && (addr.length() > 0)) {
            success = addresses.add(addr);
        }
        return success;
    }


}

