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
 * This class encapsulates information specific to a single product version,
 * such as the version name and product components for the version.
 *
 * @hibernate.class table="product_version"
 *
 * @version      1.0
 * @author       Shawn Stafford (sstafford@modeln.com)
 */
public class CMnProductVersion {

    /** Auto-generated ID used to identify the version */
    private Integer id;
    
    /** Unique code name for the version */
    private String name;

    /** Version number */
    private String number;
    
    /** List of product components within this version */
    private Set components;

    /** List of builds released in this version */
    private Set releases;
    
    
    /**
     * Create a copy of the version object.
     *
     * @return Version information
     */
    public Object clone() {
        CMnProductVersion clone = new CMnProductVersion();
        clone.setId(getId());
        clone.setName(getName());
        clone.setNumber(getNumber());

        if (components != null) {
            Iterator componentList = components.iterator();
            while (componentList.hasNext()) {
                CMnProductComponent currentComponent = (CMnProductComponent) componentList.next();
                CMnProductComponent componentClone = (CMnProductComponent) currentComponent.clone(); 
                clone.addComponent(componentClone);
            }
        }

        if (releases != null) {
            Iterator releaseList = releases.iterator();
            while (releaseList.hasNext()) {
                CMnProductRelease currentRelease = (CMnProductRelease) releaseList.next();
                CMnProductRelease releaseClone = (CMnProductRelease) currentRelease.clone(); 
                clone.addRelease(releaseClone);
            }
        }
        
        return clone;
    }
    
    /**
     * Set the ID used to look-up the password type.
     *
     * @param  id   Unique ID of the password format
     */
    public void setId(Integer id) {
        this.id = id;
    }
    
    /**
     * Return the ID used to look-up the password type.
     *
     * @hibernate.id generator-class="native"
     *
     * @return ID for the password type
     */
    public Integer getId() {
        return id;
    }

    /**
     * Set the version code name.
     *
     * @param  text   Version title
     */
    public void setName(String text) {
        name = text;
    }
    
    /**
     * Return the version code name.
     *
     * @hibernate.property
     *
     * @return Version title
     */
    public String getName() {
        return name;
    }

    /**
     * Set the version number.
     *
     * @param  text   Version number
     */
    public void setNumber(String text) {
        number = text;
    }
    
    /**
     * Return the version number.
     *
     * @hibernate.property
     *
     * @return Version number
     */
    public String getNumber() {
        return number;
    }


    /**
     * Set the list of product components within this version.
     *
     * @param  list  Product component list
     */
    public void setComponents(Set list) {
        components = list;
    }
     
    /**
     * Return the list of product components.
     *
     * @hibernate.set table="version_component_map"  cascade="all"
     * @hibernate.collection-key column="version_id"
     * @hibernate.collection-one-to-many class="com.modeln.build.common.data.product.CMnProductComponent"
     *
     * @return Product component list
     */
    public Set getComponents() {
        return components;
    }

    
    /**
     * Add a list of components to the existing list.  This method merges
     * the components by ensuring that all components from the two
     * lists are merged into a single list.  The component ID is used to compare
     * the identity of components in the two lists.
     *
     * @param  list  List of additional product components
     */
    public void addComponents(Set list) {
        Iterator componentList = list.iterator();
        while (componentList.hasNext()) {
            CMnProductComponent currentComponent = (CMnProductComponent) componentList.next();
            addComponent(currentComponent);
        }
    }
    
    /**
     * Returns the component with the specified ID value or NULL if the component
     * doesn't exist.
     *
     * @param  id   Component ID
     * @return Component object with the specified ID
     */
    public CMnProductComponent getComponent(Integer id) {
        Iterator componentList = components.iterator();
        while (componentList.hasNext()) {
            CMnProductComponent currentComponent = (CMnProductComponent) componentList.next();
            if (id.equals(currentComponent.getId())) {
                return currentComponent;
            }
        }
        
        return null;
    }
    
    /**
     * Returns TRUE if the version contains a component object with the specified
     * ID value.
     *
     * @param  id   Component ID
     * @return TRUE if the specified component exists
     */
    public boolean containsComponent(Integer id) {
        return (getComponent(id) != null);
    }

    
    /**
     * Updates the specified compnent object by locating the matching component ID
     * in the list merging any missing information into the existing component 
     * object.  If a matching component does not already exist, the supplied 
     * component object is simply added to the list.
     *
     * @param  component  Component data
     */
    public void addComponent(CMnProductComponent component) {
        boolean componentFound = false;
        
        if (component != null) {
            if (components != null) {
                Iterator componentList = components.iterator();
                while (componentList.hasNext()) {
                    CMnProductComponent currentComponent = (CMnProductComponent) componentList.next();
                    
                    // Match by ID 
                    boolean matchFound = false;
                    if ((component.getId() != null) && 
                        (currentComponent.getId() != null) && 
                        component.getId().equals(currentComponent.getId())) 
                    {
                        matchFound = true;
                    }
                    
                    // Stub out a section for copying data from the current component
                    if (matchFound) {
                        componentFound = true;
                    }
                }
            } else {
                components = new HashSet();
            }
            
            // If the component didn't already exist, add it to the list
            if (!componentFound) {
                components.add(component);
            }
        }
    }


    /**
     * Set the list of product releases within this version.
     *
     * @param  list  Product release list
     */
    public void setReleases(Set list) {
        releases = list;
    }
     
    /**
     * Return the list of product releases.
     *
     * @hibernate.set table="version_release_map" cascade="all"
     * @hibernate.collection-key column="version_id"
     * @hibernate.collection-one-to-many class="com.modeln.build.common.data.product.CMnProductRelease"
     *
     * @return Product release list
     */
    public Set getReleases() {
        return releases;
    }

    
    /**
     * Add a list of releases to the existing list.  This method merges
     * the releases by ensuring that all releases from the two
     * lists are merged into a single list.  The release ID is used to compare
     * the identity of releases in the two lists.
     *
     * @param  list  List of additional product releases
     */
    public void addReleases(Set list) {
        Iterator releaseList = list.iterator();
        while (releaseList.hasNext()) {
            CMnProductRelease currentRelease = (CMnProductRelease) releaseList.next();
            addRelease(currentRelease);
        }
    }
    
    /**
     * Returns the release with the specified ID value or NULL if the release
     * doesn't exist.
     *
     * @param  id   Release ID
     * @return Release object with the specified ID
     */
    public CMnProductRelease getRelease(Integer id) {
        Iterator releaseList = releases.iterator();
        while (releaseList.hasNext()) {
            CMnProductRelease currentRelease = (CMnProductRelease) releaseList.next();
            if (id.equals(currentRelease.getId())) {
                return currentRelease;
            }
        }
        
        return null;
    }
    
    /**
     * Updates the specified release object by locating the matching release ID
     * in the list merging any missing information into the existing release 
     * object.  If a matching release does not already exist, the supplied 
     * release object is simply added to the list.
     *
     * @param  component  Release data
     */
    public void addRelease(CMnProductRelease release) {
        boolean releaseFound = false;
        
        if (release != null) {
            if (releases != null) {
                Iterator releaseList = releases.iterator();
                while (releaseList.hasNext()) {
                    CMnProductRelease currentRelease = (CMnProductRelease) releaseList.next();
                    
                    // Match by ID 
                    boolean matchFound = false;
                    if ((release.getId() != null) && 
                        (currentRelease.getId() != null) && 
                        release.getId().equals(currentRelease.getId())) 
                    {
                        matchFound = true;
                    }
                    
                    // Stub out a section for copying data from the current release
                    if (matchFound) {
                        releaseFound = true;
                    }
                }
            } else {
                releases = new HashSet();
            }
            
            // If the release didn't already exist, add it to the list
            if (!releaseFound) {
                releases.add(release);
            }
        }
    }
    
}
