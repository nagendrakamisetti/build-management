package com.modeln.build.common.data.account;

import java.util.Locale;

/**
 * The user data represents a system user.  User information includes the 
 * real name of the user, any language preferences, and additional 
 * contact information.
 *
 * @hibernate.class table="user"
 *
 * @version      1.0
 * @author       Shawn Stafford (sstafford@modeln.com)
 */
public class CMnUser {

    /** Auto-generated user ID used as a foreign key for other tables */
    private Integer id;
    
    /** 
     * Name shared in common with members of a family, such as a 
     * family name or last name.
     */
    private String surname;
    
    /** First name or given name of the user. */
    private String givenName;
    
    /** Language and country of the user */
    private Locale locale;

    /** Email address for the account */
    private String email;
    
    
    /** User authentication information */
    private CMnLogin login;
    
    /**
     * Set the ID used to look-up the account information.
     *
     * @param   id   Unique user ID
     */
    public void setId(Integer id) {
        this.id = id;
    }
    
    /**
     * Return the user ID used to look-up the account information.
     *
     * @hibernate.id generator-class="native"
     *
     * @return User ID for the account
     */
    public Integer getId() {
        return id;
    }
    
    /**
     * Set the family or last name of the user.  The surname is a name
     * shared in common with members of a family.
     *
     * @param  name    Surname
     */
    public void setSurname(String name) {
        surname = name;
    }
    
    /**
     * Return the family or last name of the user.
     *
     * @hibernate.property
     *
     * @return Surname of the user
     */
    public String getSurname() {
        return surname;
    }

    /**
     * Set the given name of the user.  The given name refers to the name
     * given to the user at birth, such as a first name.
     *
     * @param  name    Firstname
     */
    public void setGivenName(String name) {
        givenName = name;
    }
    
    /**
     * Return the first or given name of the user.
     *
     * @hibernate.property
     *
     * @return First or given name of the user
     */
    public String getGivenName() {
        return givenName;
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
     * Set the user authentication information.
     *
     * @param  login   Authentication information
     */
    public void setLogin(CMnLogin login) {
        this.login = login;
    }
    
    /**
     * Return the user authentication information.
     *
     * @hibernate.many-to-one column="login_id" cascade="all"
     *
     * @return User authentication information
     */
    public CMnLogin getLogin() {
        return login;
    }
    
    /**
     * Set the e-mail address associated with this login.
     *
     * @param  value   E-mail address
     */
    public void setEmail(String value) {
        email = value;
    }
    
    /**
     * Return the e-mail address associated with this login.
     *
     * @hibernate.property
     *
     * @return E-mail address for the account
     */
    public String getEmail() {
        return email;
    }
    
}
