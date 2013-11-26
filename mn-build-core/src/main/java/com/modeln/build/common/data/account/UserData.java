/*
 * UserData.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.common.data.account;

import java.util.Date;
import java.util.Locale;
import java.util.Vector;

/**
 * The UserData object contains login information for the user,
 * such as username, encrypted password, and identity information.
 *
 * @version            $Revision: 1.1.1.1 $
 * @author             Shawn Stafford
 *
 */
public class UserData {

    // Provide names for each data field which should be publicly available
    // (ie: names which correspond to "get" and "set" methods)
    // This is used by other classes to extract data from a hashtable, etc.
    public static final String USER_ID      = "Uid";
    public static final String USERNAME     = "Username";
    public static final String PASSWORD     = "Password";
    public static final String ENCRYPTION   = "PasswordEncryption";
    public static final String STATUS       = "Status";
    public static final String FIRST_NAME   = "FirstName";
    public static final String MIDDLE_NAME  = "MiddleName";
    public static final String LAST_NAME    = "LastName";
    public static final String EMAIL_ADDR   = "EmailAddress";
    public static final String LOCALE       = "Locale";
    public static final String PRIMARY_GROUP = "PrimaryGroup";
    public static final String GROUPS       = "Groups";
    public static final String LANDING_PAGE = "LandingPage";


    // Password encryption types
    public static final int UNENCRYPTED_PASSWORD = 0;
    public static final int CRYPT_PASSWORD = 1;
    public static final int MD5_PASSWORD = 2;

    // Account status values
    public static final int ACCOUNT_ACTIVE = 0;
    public static final int ACCOUNT_INACTIVE = 1;
    public static final int ACCOUNT_DELETED = 2;
    public static final int ACCOUNT_ABUSE = 3;

    /** Unique user ID value, such as a UID */
    private String uid;

    /** Unique name which identifies the user on the system for login */
    private String username;

    /** Account password */
    private String password;

    /** Primary user group */
    private GroupData primaryGroup;

    /** Groups to which the user belongs */
    private Vector secondaryGroups;

    /** Login status of the account */
    private int accountStatus = ACCOUNT_ACTIVE;

    /** Type of password encryption used. */
    private int passType;

    private String firstName;
    private String lastName;
    private String middleName;

    /** Language and location for this user */
    private Locale preferedLocale;

    /** Off-system user e-mail address */
    private String emailAddress;

    /** Date of the last successful login attempt */
    private Date loginSuccess;

    /** Date of the last failed login attempt */
    private Date loginFailure;

    /** Number of failed login attempts since the last successful login */
    private int loginFailureCount;

    /** Boolean to track whether the data has been fully populated */
    private boolean isComplete = false;

    /**
     * Construct a new data object.
     */
    public UserData() {
    }

    /**
     * Construct a new data object with the given username and password.
     *
     * @param   name    Username which identifies the user
     * @param   pass    Password associated with this user
     * @param   enc     Encryption type used to encrypt the password
     */
    public UserData(String name, String pass, int enc) {
        username = name;
        password = pass;
        passType = enc;
    }

    /**
     * Set the data completion flag when the data object has been fully
     * populated.  This allows the application to determine whether this
     * is a transitory data object used to represent partial data or
     * whether the data represents the complete data found in the 
     * database.
     *
     * @param  complete  TRUE if the data is complete
     */
    public void setDataComplete(boolean complete) {
        isComplete = complete;
    }

    /**
     * Indicates whether the data in the object is complete or partially
     * populated.
     *
     * @return  TRUE if the data is complete
     */
    public boolean isDataComplete() {
        return isComplete;
    }

    /**
     * Compare the encrypted and unencrypted string using the 
     * correct algorithm.  Returns true if the passwords are a 
     * match, false otherwise.
     *
     * @param   unencrypted     Unencrypted password string
     * @return  TRUE if the strings match, FALSE otherwise
     */
    public boolean matchesPassword(String unencrypted) {
        boolean match = false;

        switch (passType) {
            case UNENCRYPTED_PASSWORD:  
                match = password.equals(unencrypted);
                break;
            case CRYPT_PASSWORD:
                match = Password.matchesCrypt(unencrypted, password);
                break;
            case MD5_PASSWORD:
                match = Password.matchesMD5(unencrypted, password);
                break;
        }

        return match;
    }


    /**
     * Sets the username field.
     * 
     * @param   name    value to be set
     */
    public void setUsername(String name) {
        username = name;
    }

    /**
     * Returns the username value.
     *
     * @return  username value
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the encrypted password field.
     * 
     * @param   pass    value to be set
     * @param   enc     Encryption type used to encrypt the password
     */
    public void setPassword(String pass, int enc) {
        password = pass;
        passType = enc;
    }

    /**
     * Returns the encrypted password value.
     *
     * @return  password value
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the user id field.
     * 
     * @param   id    value to be set
     */
    public void setUid(String id) {
        uid = id;
    }

    /**
     * Returns the user id value.
     *
     * @return  user id value
     */
    public String getUid() {
        return uid;
    }

    /**
     * Sets the account status field.
     * 
     * @param   status    value to be set
     */
    public void setStatus(int status) {
        accountStatus = status;
    }

    /**
     * Returns the account status value.
     *
     * @return  account status value
     */
    public int getStatus() {
        return accountStatus;
    }

    /**
     * Returns true if the account is active and in good standing. 
     *
     * @return  TRUE if the account is active, FALSE otherwise
     */
    public boolean isActive() { return (accountStatus == ACCOUNT_ACTIVE); }

    /**
     * Returns the type of encryption used to encrypt the password value.
     *
     * @return  encryption type (MD5, CRYPT)
     */
    public int getPasswordEncryption() {
        return passType;
    }

    /**
     * Sets the firstname field.
     * 
     * @param   name    value to be set
     */
    public void setFirstName(String name) {
        firstName = name;
    }

    /**
     * Returns the first name value.
     *
     * @return  firstname value
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the middlename field.
     * 
     * @param   name    value to be set
     */
    public void setMiddleName(String name) {
        middleName = name;
    }

    /**
     * Returns the middle name value.
     *
     * @return  middlename value
     */
    public String getMiddleName() {
        return middleName;
    }

    /**
     * Sets the lastname field.
     * 
     * @param   name    value to be set
     */
    public void setLastName(String name) {
        lastName = name;
    }

    /**
     * Returns the last name value.
     *
     * @return  lastname value
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Convenience method for returning the full name as a 
     * concatenation of the first, middle, and last name.
     * 
     * @return Full name
     */
    public String getFullName() {
        StringBuffer sb = new StringBuffer();

        if (firstName != null) {
            sb.append(firstName);
        }

        if (middleName != null) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(middleName);
        }


        if (lastName != null) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(lastName);
        }

        return sb.toString();
    }


    /**
     * Determines if the user belongs to the specified group.
     *
     * @param   group   Data about the group in question
     * @return  TRUE if the current group is part of the specified group
     */
    public boolean isPartOf(GroupData group) {
        boolean isPart = false;

        GroupData[] groups = getAllGroups();
        for (int idx = 0; idx < groups.length; idx++) {
            if (group.getGid().equalsIgnoreCase(groups[idx].getGid())) {
                isPart = true;
            }
        }

        return isPart;
    }

    /** 
     * Returns the group information specified by the group name.
     * If the user does not belong to the group, null will be returned.
     *
     * @param   name   Group name
     * @return  Group information or null
     */
    public GroupData getGroupByName(String name) {
        GroupData group = null;

        GroupData[] groups = getAllGroups();
        for (int idx = 0; idx < groups.length; idx++) {
            if (name.equalsIgnoreCase(groups[idx].getName())) {
                group = groups[idx];
            }
        }

        return group;
    }


    /**
     * Convenience method for determining if the user belongs to
     * and admin group. 
     *
     * @return  TRUE if the user belongs to an admin group 
     */
    public boolean isAdmin() {
        boolean isAdmin = false;

        GroupData[] groups = getAllGroups();
        for (int idx = 0; idx < groups.length; idx++) {
            if (groups[idx].isAdmin()) {
                isAdmin = true;
            }
        }

        return isAdmin;
    }


    /**
     * Sets the primary user group.
     *
     * @param   group   Group information
     */
    public void setPrimaryGroup(GroupData group) {
        primaryGroup = group;
    }

    /**
     * Returns the primary user group.
     *
     * @return  group   Group information
     */
    public GroupData getPrimaryGroup() {
        return primaryGroup;
    }

    /**
     * Adds a group to the list of groups to which the user belongs.
     */
    public void addSecondaryGroup(GroupData group) {
        secondaryGroups.add(group);
    }

    /**
     * Returns the list of groups to which the user belongs.
     *
     * @return  Vector of GroupData objects indicating group membership
     */
    public Vector getSecondaryGroups() {
        return secondaryGroups;
    }

    /**
     * Sets the list of secondary groups.
     *
     * @param   groups  List of groups
     */
    public void setSecondaryGroups(Vector groups) {
        secondaryGroups = groups;
    }

    /**
     * Returns an array of all groups.  If no groups exist, a NULL will be
     * returned.
     * 
     * @return  List of groups
     */
    public GroupData[] getAllGroups() {
        int count = 0;
        GroupData[] list = null;

        // Determine the correct size of the array
        if (primaryGroup != null) {
            count = count + 1;
        }
        if (secondaryGroups != null) {
            count = count + secondaryGroups.size();
        }

        // Construct the list of groups
        if (count > 0) {
            list = new GroupData[count];
            int idx = 0;
            // Add the primary group
            if (primaryGroup != null) {
                list[idx] = primaryGroup;
                idx++;
            }
            // Add the secondary groups
            if (secondaryGroups != null) {
                for (int secondIdx = 0; idx < secondaryGroups.size(); secondIdx++) {
                    list[idx] = (GroupData)secondaryGroups.get(secondIdx);
                    idx++;
                }
            }
        }

        return list;
    }

    /**
     * Sets the language and country information for the current user.
     *
     * @param   locale  user language and country info
     */
    public void setLocale(Locale locale) {
        preferedLocale = locale;
    }

    /**
     * Returns the language and country information for the current user.
     *
     * @return  User language and country info
     */
    public Locale getLocale() {
        return preferedLocale;
    }

    /**
     * Sets the outside e-mail address for the user
     *
     * @param   addr    User e-mail address
     */
    public void setEmailAddress(String addr) {
        emailAddress = addr;
    }

    /**
     * Returns the outside e-mail address for the user.
     *
     * @return  User e-mail address
     */
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     * Set the date of the most recent successful login.
     *
     * @param   date    Date of the most recent successful login
     */
    public void setSuccessfulLogin(Date date) {
        loginSuccess = date;
    }

    /**
     * Return the date of the most recent successful login.
     *
     * @return Date of the most recent successful login
     */
    public Date getSuccessfulLogin() {
        return loginSuccess;
    }

    /**
     * Set the date of the most recent failed login.
     *
     * @param   date    Date of the most recent failed login
     */
    public void setFailedLogin(Date date) {
        loginFailure = date;
    }

    /**
     * Return the date of the most recent failed login.
     *
     * @return Date of the most recent failed login
     */
    public Date getFailedLogin() {
        return loginFailure;
    }


    /**
     * Set the number of failed login attempts since the
     * last successful login.
     *
     * @param  count   Number of failed login attempts
     */
    public void setFailureCount(int count) {
        loginFailureCount = count;
    }

    /**
     * Return the number of failed login attempts since 
     * the last successful login.
     *
     * @return  Failed login attempts
     */
    public int getFailureCount() {
        return loginFailureCount;
    }

}
