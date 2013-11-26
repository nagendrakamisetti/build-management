package com.modeln.build.common.data.account;

import com.modeln.build.common.data.product.CMnProductRelease;

import java.util.Set;

/**
 * This class encapsulates information specific to a customer project version,
 * such as the version name and number.
 *
 * @hibernate.class table="project_version"
 *
 * @version      1.0
 * @author       Shawn Stafford (sstafford@modeln.com)
 */
public class CMnProjectVersion {

    /** Auto-generated ID used to identify the version */
    private Integer id;
    
    /** Unique code name for the version */
    private String name;

    /** Version number */
    private String number;

    /** Product release that is used by this customer project */
    private CMnProductRelease release;
    
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
     * Set the product release used by this customer project. 
     *
     * @param  release    Product release information 
     */
    public void setRelease(CMnProductRelease release) {
        this.release = release;
    }

    /**
     * Return the product release used by this customer project. 
     *
     * @hibernate.many-to-one column="release_id"
     *
     * @return Product release information 
     */
    public CMnProductRelease getRelease() {
        return release;
    }

}
