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

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * The purchase order creates a relationship mapping between a customer and
 * the products purchased during a single transaction.
 *
 * @hibernate.class table="purchase_order"
 *
 * @version      1.0
 * @author       Shawn Stafford (sstafford@modeln.com)
 */
public class CMnPurchaseOrder {

    /** Auto-generated ID used to identify the customer mapping */
    private Integer id;
    
    /** List of items purchased by the customer */
    private Set purchaseItems;
    
    /** Starting date of the license purchase */
    private Date licenseStartDate;
    
    /** End date for the license purchase */
    private Date licenseEndDate;
    
    
    /**
     * Set the ID used to look-up the customer mapping.
     *
     * @param  id   Unique ID of the customer
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
     * Set the effective start date of the license. 
     *
     * @param date  Start date of the license
     */
    public void setLicenseStart(Date date) {
        licenseStartDate = date;
    }
     
    /**
     * Return the effective start date of the license.
     *
     * @hibernate.property unique="false" not-null="false"
     *
     * @return Start date of the license
     */
    public Date getLicenseStart() {
        return licenseStartDate;
    }

    /**
     * Set the effective end date of the license. 
     *
     * @param date  End date of the license
     */
    public void setLicenseEnd(Date date) {
        licenseEndDate = date;
    }
     
    /**
     * Return the effective end date of the license.
     *
     * @hibernate.property unique="false" not-null="false"
     *
     * @return End date of the license
     */
    public Date getLicenseEnd() {
        return licenseEndDate;
    }
    
    
    /**
     * Set the list of product components purchased.
     *
     * @param  list  Purchase item list
     */
    public void setPurchaseItems(Set list) {
        purchaseItems = list;
    }
    
    /**
     * Return the list of purchased products.
     *
     * @hibernate.set table="order_item_map" cascade="all"
     * @hibernate.collection-key column="order_id"
     * @hibernate.collection-one-to-many class="com.modeln.build.common.data.product.CMnPurchaseItem"
     *
     * @return Purchase item list
     */
    public Set getPurchaseItems() {
        return purchaseItems;
    }
    
    /**
     * Add an item to the list of purchases.
     *
     * @param Item to add to the list
     */
    public void addPurchaseItem(CMnPurchaseItem item) {
        purchaseItems.add(item);
    }
    
    /**
     * Merge all of the products, components, and versions covered by the list
     * of orders into a single list of products.
     *
     * @param   List of purchase orders
     * @param   List of all available products
     * @return  List of products covered by all purchase orders
     */
     public static Set getProducts(Set orders, Set products) {
         HashMap productList = new HashMap();
         
         // Merge the products from each order
         Iterator orderList = orders.iterator();
         while (orderList.hasNext()) {
             CMnPurchaseOrder currentOrder = (CMnPurchaseOrder) orderList.next();
             Set currentList = currentOrder.getProducts(products);
             
             // Iterate through the products in the current order and add them
             Iterator currentProductList = currentList.iterator();
             while (currentProductList.hasNext()) {
                 CMnProduct currentProduct = (CMnProduct) currentProductList.next();
                 if (productList.containsKey(currentProduct.getId())) {
                     CMnProduct existingProduct = (CMnProduct) productList.get(currentProduct.getId());
                     existingProduct.addVersions(currentProduct.getVersions());
                 } else {
                     productList.put(currentProduct.getId(), currentProduct);
                 }
             }
         }
         
        // Convert the hashmap to a set before returning
         return new HashSet(productList.values());
     }

    /**
     * Merge all of the products, components, and versions covered the current
     * order into a single list of products.  The method returns a hashtable
     * containing the list of products, with the product ID used as keys of the
     * hashtable.
     *
     * @param   List of all available products
     * @return  List of products covered by the order
     */
    public Set getProducts(Set products) {
        // The list of products must contain object clones to ensure that 
        // the product version relationships are not affected by reordering
        HashMap productList = new HashMap();

        // Iterate through the list of items to determine the product list
        Iterator itemList = purchaseItems.iterator();
        while (itemList.hasNext()) {
            CMnPurchaseItem item = (CMnPurchaseItem) itemList.next();
            CMnProductComponent currentComponent = item.getProductComponent();
            
            // Iterate through the list of products to locate matching components
            Iterator allProducts = products.iterator();
            CMnProduct currentProduct = null;
            CMnProduct matchingProduct = null;
            while (allProducts.hasNext()) {
                currentProduct = (CMnProduct) allProducts.next();
                if (currentProduct.containsComponent(currentComponent.getId())) {
                    // Determine if the product already exists in the list and add if necessary
                    if (productList.containsKey(currentProduct.getId())) {
                        matchingProduct = (CMnProduct) productList.get(currentProduct.getId());
                    } else {
                        // Clone the product to ensure that the caller can't modify the order info
                        matchingProduct = (CMnProduct) currentProduct.clone();
                        matchingProduct.setVersions(null);
                        productList.put(matchingProduct.getId(), matchingProduct);
                    }
                    
                    // Determine if the version already exists in the list and add if necessary
                    CMnProductVersion matchingVersion = matchingProduct.getVersionByComponent(currentComponent.getId());
                    if (matchingVersion == null) {
                        // Clone the version to ensure that the caller can't modify the order info
                        CMnProductVersion currentVersion = currentProduct.getVersionByComponent(currentComponent.getId());
                        matchingVersion = (CMnProductVersion) currentVersion.clone();
                        matchingVersion.setComponents(null);
                        matchingVersion.setReleases(null);
                        matchingProduct.addVersion(matchingVersion);
                    }
        
                    // Determine if the component already exists in the list and add if necessary
                    if (!matchingVersion.containsComponent(currentComponent.getId())) {
                        // Clone the component to ensure that the caller can't modify the order info
                        CMnProductComponent matchingComponent = (CMnProductComponent) currentComponent.clone();
                        matchingVersion.addComponent(matchingComponent);
                    }
                }
            }
            
            
        }
        
        // Convert the hashmap to a set before returning
        return new HashSet(productList.values());
    }

}

