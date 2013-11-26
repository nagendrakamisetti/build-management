package com.modeln.build.common.data.account;

import com.modeln.build.common.data.CMnEncodingFormat;
import java.util.Set;

/**
 * The login data represents a username and password used to authenticate
 * against a secure system.
 *
 * @hibernate.class table="password"
 *
 * @version      1.0
 * @author       Shawn Stafford (sstafford@modeln.com)
 */
public class CMnLogin {

    
    /** Account to which the login belongs */
    private CMnAccount account;

    /** User to which the login belongs */
    private CMnUser user;
    
    /** Auto-generated ID used as a foreign key for other tables */
    private Integer id;
    
    /** Login name for the account */
    private String username;
    
    /** Password for the account */
    private String password;

    /** Role for the account */
    private String role;

    /** Password encryption format */
    private CMnEncodingFormat passwordFormat;
    
    /**
     * Set the ID used to look-up the account information.
     *
     * @param   id   Unique ID
     */
    public void setId(Integer id) {
        this.id = id;
    }
    
    /**
     * Return the ID used to look-up the account information.
     *
     * @hibernate.id generator-class="native"
     *
     * @return ID for the account
     */
    public Integer getId() {
        return id;
    }

    /**
     * Set the account that the login belongs to.
     *
     * @param  parent   Parent account
     */
    public void setAccount(CMnAccount parent) {
        account = parent;
    }
    
    /**
     * Return the account to which the login belongs
     *
     * @hibernate.many-to-one column="account_id"
     *
     * @return Parent account
     */
    public CMnAccount getAccount() {
        return account;
    }

    /**
     * Set the user that the login belongs to.
     *
     * @param  parent   Parent user
     */
    public void setUser(CMnUser parent) {
        user = parent;
    }
    
    /**
     * Return the user to which the login belongs
     *
     * @hibernate.many-to-one column="user_id"
     *
     * @return Parent account
     */
    public CMnUser getUser() {
        return user;
    }
    
    /**
     * Set the login name used to look-up the account information.
     *
     * @param  value   Username
     */
    public void setUsername(String value) {
        username = value;
    }
    
    /**
     * Return the login name used to look-up the account information.
     *
     * @hibernate.property unique="true" not-null="true"
     *
     * @return Username for the account
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * Set the password used to authenticate the account.
     *
     * @param  value   Password
     */
    public void setPassword(String value) {
        password = value;
    }
    
    /**
     * Return the password used to authenticate the account.
     *
     * @hibernate.property
     *
     * @return Password for the account
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set the encoding format used to encrypt the password.
     *
     * @param  format   Password encryption format
     */
    public void setPasswordFormat(CMnEncodingFormat format) {
        passwordFormat = format;
    }
    
    /**
     * Return the encoding format used to encrypt the password.
     *
     * @hibernate.many-to-one not-null="true"
     *
     * @return Password encryption format
     */
    public CMnEncodingFormat getPasswordFormat() {
        return passwordFormat;
    }

    /**
     * Set the user role associated with this login.
     *
     * @param  value   User role
     */
    public void setRole(String value) {
        role = value;
    }
    
    /**
     * Return the user role associated with this login.
     *
     * @hibernate.property
     *
     * @return Role for the account
     */
    public String getRole() {
        return role;
    }
    
    
}

