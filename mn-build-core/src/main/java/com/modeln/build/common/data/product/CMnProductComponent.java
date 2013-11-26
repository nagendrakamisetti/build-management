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

/**
 * Component information includes the name and description of the component.
 *
 * @hibernate.class table="product_component"
 *
 * @version      1.0
 * @author       Shawn Stafford (sstafford@modeln.com)
 */
public class CMnProductComponent {

    /** Auto-generated ID used to identify the component */
    private Integer id;
    
    /** Component title */
    private String name;

    /** Brief component description */
    private String description;
    

    /**
     * Create a copy of the component object.
     *
     * @return Component information
     */
    public Object clone() {
        CMnProductComponent clone = new CMnProductComponent();
        clone.setId(getId());
        clone.setName(getName());
        clone.setDescription(getDescription());
        
        return clone;
    }
    
    
    /**
     * Set the ID used to look-up the component.
     *
     * @param  id   Unique ID of the component
     */
    public void setId(Integer id) {
        this.id = id;
    }
    
    /**
     * Return the ID used to look-up the component.
     *
     * @hibernate.id generator-class="native"
     *
     * @return ID for the component
     */
    public Integer getId() {
        return id;
    }

    /**
     * Set the component name.
     *
     * @param  text   Component title
     */
    public void setName(String text) {
        name = text;
    }
    
    /**
     * Return the component name.
     *
     * @hibernate.property
     *
     * @return Component title
     */
    public String getName() {
        return name;
    }

    /**
     * Set the component description.
     *
     * @param  text   Component title
     */
    public void setDescription(String text) {
        description = text;
    }
    
    /**
     * Return the component description.
     *
     * @hibernate.property
     *
     * @return Component description
     */
    public String getDescription() {
        return description;
    }

    
}

