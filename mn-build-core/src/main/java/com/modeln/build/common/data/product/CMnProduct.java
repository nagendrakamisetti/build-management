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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Product information includes the name, version, and components associated
 * with a product.
 *
 * @hibernate.class table="release_product"
 *
 * @version      1.0
 * @author       Shawn Stafford (sstafford@modeln.com)
 */
public class CMnProduct {

    /** Auto-generated ID used to identify the product */
    private Integer id;
    
    /** Product title */
    private String name;

    /** Brief product description */
    private String description;
    
    /** List of product versions */
    private Set versions;
    
    /**
     * Create a copy of the product object.
     *
     * @return Product information
     */
    public Object clone() {
        CMnProduct clone = new CMnProduct();
        clone.setId(getId());
        clone.setName(getName());
        clone.setDescription(getDescription());

        if (versions != null) {
            Iterator versionList = versions.iterator();
            while (versionList.hasNext()) {
                CMnProductVersion currentVersion = (CMnProductVersion) versionList.next();
                CMnProductVersion versionClone = (CMnProductVersion) currentVersion.clone(); 
                clone.addVersion(versionClone);
            }
        }
        
        return clone;
    }
    
    /**
     * Set the ID used to look-up the product.
     *
     * @param  id   Unique ID of the product
     */
    public void setId(Integer id) {
        this.id = id;
    }
    
    /**
     * Return the ID used to look-up the product.
     *
     * @hibernate.id generator-class="native"
     *
     * @return ID for the product
     */
    public Integer getId() {
        return id;
    }
    
    /**
     * Set the product name.
     *
     * @param  text   Product title
     */
    public void setName(String text) {
        name = text;
    }
    
    /**
     * Return the product name.
     *
     * @hibernate.property
     *
     * @return Product title
     */
    public String getName() {
        return name;
    }

    /**
     * Set the product description.
     *
     * @param  text   Product title
     */
    public void setDescription(String text) {
        description = text;
    }
    
    /**
     * Return the product description.
     *
     * @hibernate.property
     *
     * @return Product description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Set the list of product versions.
     *
     * @param  list  Product version list
     */
    public void setVersions(Set list) {
        versions = list;
    }
     
    /**
     * Return the list of product versions.
     *
     * @hibernate.set table="product_version_map" cascade="all"
     * @hibernate.collection-key column="product_id"
     * @hibernate.collection-one-to-many class="com.modeln.build.common.data.product.CMnProductVersion"
     *
     * @return Product version list
     */
    public Set getVersions() {
        return versions;
    }
    
    /**
     * Add a list of product versions to the existing list.  This method merges
     * the versions by ensuring that all versions and components from the two
     * lists are merged into a single list.  The version ID is used to compare
     * the identity of versions in the two lists.
     *
     * @param  list  List of additional product versions
     */
    public void addVersions(Set list) {
        Iterator versionList = list.iterator();
        while (versionList.hasNext()) {
            CMnProductVersion currentVersion = (CMnProductVersion) versionList.next();
            addVersion(currentVersion);
        }
    }
    
    /**
     * Returns the version with the specified ID value or NULL if the version
     * doesn't exist.
     *
     * @param  id   Version ID
     * @return Version object with the specified ID
     */
    public CMnProductVersion getVersion(Integer id) {
        Iterator versionList = versions.iterator();
        while (versionList.hasNext()) {
            CMnProductVersion currentVersion = (CMnProductVersion) versionList.next();
            if (id.equals(currentVersion.getId())) {
                return currentVersion;
            }
        }
        
        return null;
    }
    
    /**
     * Returns TRUE if the product contains a version object with the specified
     * ID value.
     *
     * @param  id   Version ID
     * @return TRUE if the specified version exists
     */
    public boolean containsVersion(Integer id) {
        return (getVersion(id) != null);
    }
    
    /**
     * Updates the specified version object by locating the matching version ID
     * in the list merging any missing information into the existing version 
     * object.  The merge will copy any missing components into the version. If a 
     * matching version does not already exist, the supplied version object is
     * simply added to the list.
     *
     * @param  version  Version data
     */
    public void addVersion(CMnProductVersion version) {
        boolean versionFound = false;
        
        if (version != null) {
            if (versions != null) {
                Iterator versionList = versions.iterator();
                while (versionList.hasNext()) {
                    CMnProductVersion currentVersion = (CMnProductVersion) versionList.next();
                    
                    // Match by ID 
                    boolean matchFound = false;
                    if ((version.getId() != null) && 
                        (currentVersion.getId() != null) && 
                        version.getId().equals(currentVersion.getId())) 
                    {
                        matchFound = true;
                    }
                    
                    // Copy the components to the current version if a match is found
                    if (matchFound) {
                        versionFound = true;
                        Set components = currentVersion.getComponents();
                        Iterator componentList = components.iterator();
                        while (componentList.hasNext()) {
                            currentVersion.addComponent((CMnProductComponent) componentList.next());
                        }
                        
                        Set releases = currentVersion.getReleases();
                        Iterator releaseList = releases.iterator();
                        while (releaseList.hasNext()) {
                            currentVersion.addRelease((CMnProductRelease) releaseList.next());
                        }
                    }
                }
            } else {
                versions = new HashSet();
            }
            
            // If the version didn't already exist, add it to the list
            if (!versionFound) {
                versions.add(version);
            }
        }
    }

    /**
     * Returns the version containing the specified component.  If no match is
     * found, a NULL will be returned.
     *
     * @param  id   Component ID
     * @return Version containing the component, or null if none found
     */
    public CMnProductVersion getVersionByComponent(Integer id) {
        CMnProductVersion matchingVersion = null;
        
        Iterator versionList = versions.iterator();
        while (versionList.hasNext()) {
            CMnProductVersion currentVersion = (CMnProductVersion) versionList.next();
            if (currentVersion.containsComponent(id)) {
                matchingVersion = currentVersion;
            }
        }
        
        return matchingVersion;
    }
    
    /**
     * Returns TRUE if the product contains a version which contains a component 
     * object with the specified ID value.
     *
     * @param  id   Component ID
     * @return TRUE if the specified component exists
     */
    public boolean containsComponent(Integer id) {
        CMnProductVersion version = getVersionByComponent(id);
        return (version != null);
    }
    
}

