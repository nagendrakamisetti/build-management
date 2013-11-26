package com.modeln.build.common.data.account;

import com.modeln.build.common.data.product.CMnProduct;
import com.modeln.testfw.reporting.CMnDbBuildData;

import java.util.Collections;
import java.util.Set;


/**
 * The environment information describes a customer deployment environment. 
 *
 * @version      1.0
 * @author       Shawn Stafford (sstafford@modeln.com)
 */
public class CMnEnvironment {

    /** Auto-generated ID used to identify a customer deployment environment */
    private Integer id;
    
    /** Name of the environment */
    private String name;

    /** Short name of the account */
    private String shortName;


    /** List of software installed in the environment */
    private Set software = Collections.EMPTY_SET;

    /** Product available to be deployed to that environment */
    private CMnProduct product;

    /** Product build currently deployed to that environment */
    private CMnDbBuildData build; 


    /**
     * Set the ID used to look-up the account information.
     *
     * @param   id   Unique account ID
     */
    public void setId(Integer id) {
        this.id = id;
    }
    
    /**
     * Return the account ID used to look-up the account information.
     *
     * @return ID for the account
     */
    public Integer getId() {
        return id;
    }
    
    /**
     * Set the name of the account.
     *
     * @param  name    Account name
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Return the name of the account.
     *
     * @return Name of the account
     */
    public String getName() {
        return name;
    }

    /**
     * Set the list of software installed in the environment.
     *
     * @param  list  Software list
     */
    public void setSoftware(Set list) {
        software = list;
    }

    /**
     * Return the list of software.
     *
     * @return User list
     */
    public Set getSoftware() {
        return software;
    }

    /**
     * Set the product. 
     *
     * @param  prod    Product 
     */
    public void setProduct(CMnProduct prod) {
        product = prod;
    }

    /**
     * Return the product information. 
     *
     * @return product 
     */
    public CMnProduct getProduct() {
        return product;
    }

    /**
     * Set the build.
     *
     * @param  build    Build information
     */
    public void setBuild(CMnDbBuildData build) {
        this.build = build;
    }

    /**
     * Return information about the build currently deployed. 
     *
     * @return build information
     */
    public CMnDbBuildData getBuild() {
        return build;
    }

    /**
     * Set the short name of the account.
     *
     * @param  name    Short account name
     */
    public void setShortName(String name) {
        shortName = name;
    }

    /**
     * Return the short name of the account.
     *
     * @hibernate.property
     *
     * @return Short name of the account
     */
    public String getShortName() {
        return shortName;
    }



}
