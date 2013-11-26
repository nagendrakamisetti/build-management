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
import java.util.Iterator;
import java.util.Set;

/**
 * This class encapsulates information specific to a single product release,
 * such as a patch release of a product version.
 *
 * @hibernate.class table="product_release"
 *
 * @version      1.0
 * @author       Shawn Stafford (sstafford@modeln.com)
 */
public class CMnProductRelease {

    /** Auto-generated ID used to identify the release */
    private Integer id;
    
    /** Name of the product release */
    private String name;

    /** Date when the product was released to the customer */
    private Date releaseDate;

    /** Type of version control system used */
    private String versionControlType;

    /** Root version control location where the build was obtained from */
    private String versionControlRoot;

    /** Provides a version control ID which can be used to obtain the build source from version control */
    private String versionControlId;


    /**
     * Create a copy of the release object.
     *
     * @return Version information
     */
    public Object clone() {
        CMnProductRelease clone = new CMnProductRelease();
        clone.setId(getId());
        clone.setName(getName());
        clone.setReleaseDate(getReleaseDate());
        
        return clone;
    }
    
    /**
     * Set the ID used to look-up the release.
     *
     * @param  id   Unique ID of the product release
     */
    public void setId(Integer id) {
        this.id = id;
    }
    
    /**
     * Return the ID used to look-up the release.
     *
     * @hibernate.id generator-class="native"
     *
     * @return ID for the release
     */
    public Integer getId() {
        return id;
    }

    /**
     * Set the release name.
     *
     * @param  text   Release name
     */
    public void setName(String text) {
        name = text;
    }
    
    /**
     * Return the release name.
     *
     * @hibernate.property
     *
     * @return Release name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the release date.
     *
     * @param  date   Release date
     */
    public void setReleaseDate(Date date) {
        releaseDate = date;
    }
    
    /**
     * Return the release date.
     *
     * @hibernate.property
     *
     * @return Release date
     */
    public Date getReleaseDate() {
        return releaseDate;
    }

    /**
     * Set the version control type that is used to interpret the version
     * control values.
     *
     * @param  type   Version control type
     */
    public void setVersionControlType(String type) {
        versionControlType = type;
    }

    /**
     * Return the version control type.
     *
     * @return Version control type
     */
    public String getVersionControlType() {
        return versionControlType;
    }

    /**
     * Set the version control root that is used to identify the location from
     * which the source code was obtained for the build.  In perforce, the
     * version control root refers to the depot.  In CVS, this might refer to
     * a respository directory.
     *
     * @param  root   Version control root
     */
    public void setVersionControlRoot(String root) {
        versionControlRoot = root;
    }

    /**
     * Return a root location that can be used to locate and obtain the source
     * code from the version control system.
     *
     * @return Version control root
     */
    public String getVersionControlRoot() {
        return versionControlRoot;
    }

    /**
     * Set the version control identifier that is used to identify the point in
     * time at which the source code was obtained for the build.  In perforce,
     * the version control ID refers to the Changelist number.  In CVS, this might
     * refer to a tag or similar marker.
     *
     * @param  id     Version control identifier
     */
    public void setVersionControlId(String id) {
        versionControlId = id;
    }

    /**
     * Return an identifier that can be used to sync the source code and recreate
     * the build.
     *
     * @return  Version control identifier
     */
    public String getVersionControlId() {
        return versionControlId;
    }

    
}
