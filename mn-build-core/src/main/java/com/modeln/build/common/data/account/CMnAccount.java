package com.modeln.build.common.data.account;

import com.modeln.build.common.data.product.CMnPurchaseOrder;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;

/**
 * The account information represents a point of contact and billing for
 * a customer.
 *
 * @hibernate.class table="account"
 *
 * @version      1.0
 * @author       Shawn Stafford (sstafford@modeln.com)
 */
public class CMnAccount {

    /** List of possible branch types */
    public static enum BranchType {
        PRODUCT, CUSTOMER
    }


    /** Auto-generated ID used to identify an account */
    private Integer id;
    
    /** Name of the account */
    private String name;

    /** Short name of the account */
    private String shortName;

    /** Type of branch where the product build is maintained for this customer */
    private BranchType branchType = BranchType.PRODUCT;

    
    /** Default language and country for the account */
    private Locale locale;
    
    /** List of users that belong to the account */
    private Set users = Collections.EMPTY_SET;
    
    /** List of purchases made by the account */
    private Set purchases = Collections.EMPTY_SET;

    /** List of projects associated with this account */
    private Set projects = Collections.EMPTY_SET;

    /** List of environments associated with this account */
    private Set environments = Collections.EMPTY_SET;

    
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
     * @hibernate.id generator-class="native"
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
     * @hibernate.property
     *
     * @return Name of the account
     */
    public String getName() {
        return name;
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


    /**
     * Set the locale (language and country) of the user.
     *
     * @param  locale  Language and country information
     */
    public void setLocale(Locale locale) {
        this.locale = locale;
    }
    
    /**
     * Return the locale (language and country) of the user.
     *
     * @hibernate.property
     *
     * @return Locale
     */
    public Locale getLocale() {
        return locale;
    }
    
    /**
     * Set the list of users that belong to this account.
     *
     * @param  list  User list
     */
    public void setUsers(Set list) {
        users = list;
    }
    
    /**
     * Return the list of users.
     *
     * @hibernate.set table="account_user_map" cascade="all"
     * @hibernate.collection-key column="account_id"
     * @hibernate.collection-one-to-many class="com.modeln.build.common.data.account.CMnUser"
     *
     * @return User list
     */
    public Set getUsers() {
        return users;
    }
    
    /**
     * Add a user to the list of existing users.  This method does not check 
     * for duplicate IDs.
     *
     * @param  user   User to be added to the list
     */
    public void addUser(CMnUser user) {
        users.add(user);
    }
    
    /**
     * Set the list of purchases made by the account.
     *
     * @param  list  Purchase list
     */
    public void setPurchases(Set list) {
        purchases = list;
    }
    
    /**
     * Return the list of purchases made by the account.
     *
     * @hibernate.set table="account_purchase_map" cascade="all"
     * @hibernate.collection-key column="account_id"
     * @hibernate.collection-one-to-many class="com.modeln.build.common.data.product.CMnPurchaseOrder"
     *
     * @return Purchase list
     */
    public Set getPurchases() {
        return purchases;
    }

    /**
     * Add an order to the list of existing purchases.  This method does not check 
     * for duplicate IDs.
     *
     * @param  order   Purchase order to be added to the list
     */
    public void addPurchase(CMnPurchaseOrder order) {
        purchases.add(order);
    }
    
    /**
     * Set the list of projects associated with the account.
     *
     * @param  list  Project list
     */
    public void setProjects(Set list) {
        projects = list;
    }
    
    /**
     * Return the list of projects associated with this account.
     *
     * @hibernate.set table="account_project_map"
     * @hibernate.collection-key column="account_id"
     * @hibernate.collection-one-to-many class="com.modeln.build.common.data.account.CMnProject"
     *
     * @return Project list
     */
    public Set getProjects() {
        return projects;
    }

    /**
     * Add a project to the list of existing projects.  This method does not check 
     * for duplicate IDs.
     *
     * @param  project   Project to be added to the list
     */
    public void addProject(CMnProject project) {
        projects.add(project);
    }

    /**
     * Set the list of environments associated with the account.
     *
     * @param  list  Environment list
     */
    public void setEnvironments(Set list) {
        environments = list;
    }

    /**
     * Return the list of environents associated with this account.
     *
     * @return Project list
     */
    public Set getEnvironments() {
        return environments;
    }

    /**
     * Add an environment to the list of existing environments.  This method does not check
     * for duplicate IDs.
     *
     * @param  environment   Environment to be added to the list 
     */
    public void addEnvironment(CMnEnvironment environment) {
        environments.add(environment);
    }


    /**
     * Set the branch type 
     *
     * @param    type   Branch type 
     */
    public void setBranchType(String type) {
        if (type != null) {
            branchType = BranchType.valueOf(type.toUpperCase());
        } else {
            branchType = null;
        }
    }


    /**
     * Return the branch type 
     *
     * @return  Branch type 
     */
    public BranchType getBranchType() {
        return branchType;
    }


    /**
     * Return the list of possible branch types as an array.
     *
     * @return List of branch types
     */
    public static String[] getBranchTypeList() {
        BranchType[] list = BranchType.values();
        String[] strList = new String[list.length];
        int idx = 0;
        while (idx < list.length) {
            strList[idx] = list[idx].toString();
            idx++;
        }
        return strList;
    }

    
}
