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
 * The purchase item creates a relationship mapping between a purchase order
 * and the product components being purchased.
 *
 * @hibernate.class table="purchase_item"
 *
 * @version      1.0
 * @author       Shawn Stafford (sstafford@modeln.com)
 */
public class CMnPurchaseItem {

    /** Auto-generated ID used to identify the customer mapping */
    private Integer id;
    
    /** Item being purchased */
    private CMnProductComponent item;
    
    /** Cost or value of the purchased item */
    private Float price;
    
    /**
     * Set the ID used to look-up the purchase order line item.
     *
     * @param  id   Unique ID of the purchased item
     */
    public void setId(Integer id) {
        this.id = id;
    }
    
    /**
     * Return the ID used to look-up the purchase order line item.
     *
     * @hibernate.id generator-class="native"
     *
     * @return ID for the purchased item
     */
    public Integer getId() {
        return id;
    }

    /**
     * Set the product component being purchased for the current order item. 
     * If a purchase consists of multiple components or a bundle of components, 
     * each product component should have a corresponding purchase item entry in 
     * the purchase order.
     *
     * @param item  Single item of a purchase order
     */
    public void setProductComponent(CMnProductComponent item) {
        this.item = item;
    }
     
    /**
     * Return the product component associated with the current purchase item.  
     * Each purchase item corresponds to a single product component.  
     *
     * @hibernate.many-to-one column="component_id"
     *
     * @return Purchase item
     */
    public CMnProductComponent getProductComponent() {
        return item;
    }
    
    
    
}

