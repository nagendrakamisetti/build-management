package com.modeln.build.common.data.account;

import com.modeln.build.common.data.product.CMnPurchaseOrder;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;

/**
 * Information about a development project.
 *
 * @hibernate.class table="project"
 *
 * @version      1.0
 * @author       Shawn Stafford (sstafford@modeln.com)
 */
public class CMnProject {

    /** Auto-generated ID used to identify a project */
    private Integer id;
    
    /** Name of the project */
    private String name;

    /** Brief product description */
    private String description;
    
    /** List of product versions */
    private Set versions;

    /**
     * Set the ID used to look-up the project information.
     *
     * @param   id   Unique project ID
     */
    public void setId(Integer id) {
        this.id = id;
    }
    
    /**
     * Return the account ID used to look-up the project information.
     *
     * @hibernate.id generator-class="native"
     *
     * @return ID for the project
     */
    public Integer getId() {
        return id;
    }
    
    /**
     * Set the name of the project.
     *
     * @param  name    Project name
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Return the name of the project.
     *
     * @hibernate.property
     *
     * @return Name of the project
     */
    public String getName() {
        return name;
    }

    /**
     * Set the project description.
     *
     * @param  text   Project description
     */
    public void setDescription(String text) {
        description = text;
    }
    
    /**
     * Return the project description.
     *
     * @hibernate.property
     *
     * @return Project description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Set the list of project versions.
     *
     * @param  list  Project version list
     */
    public void setVersions(Set list) {
        versions = list;
    }
     
    /**
     * Return the list of project versions.
     *
     * @hibernate.set table="project_version_map"
     * @hibernate.collection-key column="project_id"
     * @hibernate.collection-one-to-many class="com.modeln.build.common.data.account.CMnProjectVersion"
     *
     * @return Project version list
     */
    public Set getVersions() {
        return versions;
    }

    /**
     * Add a version to the list of existing versions.  This method does not check 
     * for duplicate IDs.
     *
     * @param  version   Project version to be added to the list
     */
    public void addVersion(CMnProjectVersion version) {
        versions.add(version);
    }
    
}

