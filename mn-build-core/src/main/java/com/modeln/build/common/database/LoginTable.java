/*
 * LoginTable.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.common.database;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.sql.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import com.modeln.build.common.data.account.*;
import com.modeln.testfw.reporting.CMnTable;


/**
 * The LoginTable loads user data from the login table
 * by mapping database fields to the correct data members.
 *
 * @version            $Revision: 1.1.1.1 $
 * @author             Shawn Stafford
 *
 */
public class LoginTable extends CMnTable {
    public static final String TABLE_NAME       = "login";

    public static final String USER_ID          = "user_id";
    public static final String USERNAME         = "username";
    public static final String PASSWORD         = "password";
    public static final String PASSWORD_TYPE    = "pass_type";
    public static final String STATUS           = "status";
    public static final String PRIMARY_GROUP    = "pri_group";
    public static final String EMAIL            = "email";
    public static final String FIRSTNAME        = "firstname";
    public static final String LASTNAME         = "lastname";
    public static final String MIDDLENAME       = "middlename";
    public static final String TITLE            = "title";
    public static final String LANGUAGE         = "language";
    public static final String COUNTRY          = "country";

    public static final String STATUS_ACTIVE    = "active";
    public static final String STATUS_INACTIVE  = "inactive";
    public static final String STATUS_DELETED   = "deleted";
    public static final String STATUS_ABUSE     = "abuse";

    public static final String PASSWORD_TYPE_CRYPT = "crypt";
    public static final String PASSWORD_TYPE_MD5   = "md5";

    public static final String TITLE_MR  = "Mr";
    public static final String TITLE_MS  = "Ms";
    public static final String TITLE_MRS = "Mrs"; 

    public static final String[] PASSWORD_TYPE_VALUES = { PASSWORD_TYPE_CRYPT, PASSWORD_TYPE_MD5 };
    public static final String[] STATUS_VALUES  = { STATUS_ACTIVE, STATUS_INACTIVE, STATUS_DELETED, STATUS_ABUSE }; 
    public static final String[] TITLE_VALUES   = { TITLE_MR, TITLE_MS, TITLE_MRS };

    public static final String HISTORY_TABLE    = "login_history";

    public static final String SUCCESS_DATE     = "date";
    public static final String FAILURE_DATE     = "fail_date";
    public static final String FAILURE_COUNT    = "fail_count";


    /**
     * Construct the table object.
     */
    private LoginTable() {
    }


    /** Singleton instance of the table class */
    private static LoginTable instance;

    /**
     * Return the singleton instance of the class.
     */
    public static LoginTable getInstance() {
        if (instance == null) {
            instance = new LoginTable();

            // Enable debbuging to a file
            String logfile = "/var/tmp/LoginTable.txt";
            try {
                instance.setDebugOutput(new PrintStream(logfile));
                instance.debugEnable(true);
            } catch (FileNotFoundException nfex) {
                System.out.println("Unable to enable debugging.  Failed to open log file: " + logfile);
            }

        }
        return instance;
    }



    /**
     * Create a new UserData object by parsing the result set and 
     * filling in any values found in the Login table.
     *
     * @param   rs          Login table result set containing the user data
     * @param   tablename   TRUE if the column names should have the full
     *                      table name prefix such as login.username
     * @throws  SQLException if the result set cannot be parsed correctly
     */
    public static UserData getUserData(ResultSet rs, boolean tablename) 
        throws SQLException
    {
        UserData user = null;
        if (rs != null) {
            user = new UserData();
            getUserData(rs, user, tablename);
        }

        return user;
    }

    /**
     * Query the user table and populate the data objects.
     *
     * @return Number of users updated
     */
    public int getUserData(Connection conn, Hashtable<String,UserData> users) 
        throws SQLException
    {
        int count = 0;

        Statement st = conn.createStatement();

        Enumeration userList = null;
        UserData current = null;
        UserData replacement = null;

        // Get the list of user IDs
        StringBuffer sb = new StringBuffer();
        userList = users.elements();
        while (userList.hasMoreElements()) {
            current = (UserData) userList.nextElement();
            sb.append(current.getUid());
            if (userList.hasMoreElements()) {
                sb.append(",");
            }
        }

        // Execute the query
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + USER_ID + " IN (" + sb.toString() + ")";

        ResultSet rs = null;
        try {
            rs = executeQuery(st, "getUserData", sql);

            // Replace the existing user data with data from the database
            Vector<UserData> newUsers = getUsers(rs);
            userList = newUsers.elements();
            while (userList.hasMoreElements()) {
                current = (UserData) userList.nextElement();
                users.put(current.getUid(), current);
                count++;
            }
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return count;
    }


    /**
     * Populate the user data object with history information. 
     *
     * @param   conn        Connection to the database
     * @param   user        User data to populate 
     * @throws  SQLException if the result set cannot be parsed correctly
     */
    public boolean getHistory(Connection conn, UserData user)
        throws SQLException
    {
        boolean hasHistory = false;
        Statement st = conn.createStatement();

        // Execute the query
        String sql = "SELECT * FROM " + HISTORY_TABLE + " WHERE " + USER_ID + " = '" + user.getUid() + "'";

        ResultSet rs = null;
        try {
            rs = executeQuery(st, "getHistory", sql);
            if (rs.first()) {
                getHistoryData(rs, user, false);
                hasHistory = true;
            }
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return hasHistory;
    }


    /**
     * Create a new UserData object by connecting to the database and
     * retrieving the user with the given UID.
     *
     * @param   conn        Connection to the database
     * @param   uid         User to retrieve
     * @throws  SQLException if the result set cannot be parsed correctly
     */
    public UserData getUserByUid(Connection conn, String uid)
        throws SQLException
    {
        UserData data = null;
        Statement st = conn.createStatement();

        // Execute the query
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + USER_ID + "='" + uid + "'";

        ResultSet rs = null;
        try {
            rs = executeQuery(st, "getLogin", sql);
            if (rs.first()) {
                data = getUserData(rs, true);
                getHistory(conn, data);
            } else {
                return null;
            }

            // Retrieve complete group information for the primary group
            GroupData primary = data.getPrimaryGroup();
            if (primary != null) {
                data.setPrimaryGroup(LoginGroupTable.getGroup(conn, primary.getGid()));
            }
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return data;
    }


    /**
     * Create a new UserData object by connecting to the database and
     * retrieving the user with the given username.
     *
     * @param   conn        Connection to the database
     * @param   username    User to retrieve
     * @throws  SQLException if the result set cannot be parsed correctly
     */
    public UserData getUserByName(Connection conn, String username) 
        throws SQLException
    {
        UserData data = null;
        Statement st = conn.createStatement();

        // Execute the query
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + USERNAME + "='" + username + "'";

        ResultSet rs = null;
        try {
            rs = executeQuery(st, "getUserByName", sql);
            if (rs.first()) {
                data = getUserData(rs, true);
                getHistory(conn, data);
            } else {
                return null;
            }

            // Retrieve complete group information for the primary group
            GroupData primary = data.getPrimaryGroup();
            if (primary != null) {
                data.setPrimaryGroup(LoginGroupTable.getGroup(conn, primary.getGid()));
            }
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return data;
    }


    /**
     * Update an existing UserData object with values from the result
     * set.
     *
     * @param   rs          Login table result set containing the user data
     * @param   user        Existing user object
     * @param   tablename   TRUE if the column names should have the full
     *                      table name prefix such as login.username
     *
     * @throws  SQLException if the result set cannot be parsed correctly
     */
    public static void getUserData(ResultSet rs, UserData user, boolean tablename)
        throws SQLException
    {
        String prefix = null;
        if (tablename) {
            prefix = TABLE_NAME + "."; 
        } else {
            prefix = "";
        }

        user.setUid(rs.getString(prefix + USER_ID));
        user.setUsername(rs.getString(prefix + USERNAME));
        user.setEmailAddress(rs.getString(prefix + EMAIL));
        user.setFirstName(rs.getString(prefix + FIRSTNAME));
        user.setLastName(rs.getString(prefix + LASTNAME));
        user.setMiddleName(rs.getString(prefix + MIDDLENAME));

        // Set the user password
        String password = rs.getString(prefix + PASSWORD);
        String passType = rs.getString(prefix + PASSWORD_TYPE);
        if (passType.equalsIgnoreCase(PASSWORD_TYPE_CRYPT)) {
            user.setPassword(password, UserData.CRYPT_PASSWORD);
        } else if (passType.equalsIgnoreCase(PASSWORD_TYPE_MD5)) {
            user.setPassword(password, UserData.MD5_PASSWORD);
        } else {
            user.setPassword(password, UserData.UNENCRYPTED_PASSWORD);
        }

        // Set the account status
        String acctStatus = rs.getString(prefix + STATUS);
        if (acctStatus.equalsIgnoreCase(STATUS_ACTIVE)) {
            user.setStatus(UserData.ACCOUNT_ACTIVE);
        } else if (acctStatus.equalsIgnoreCase(STATUS_INACTIVE)) {
            user.setStatus(UserData.ACCOUNT_INACTIVE);
        } else if (acctStatus.equalsIgnoreCase(STATUS_DELETED)) {
            user.setStatus(UserData.ACCOUNT_DELETED);
        } else if (acctStatus.equalsIgnoreCase(STATUS_ABUSE)) {
            user.setStatus(UserData.ACCOUNT_ABUSE);
        }

        // Set the primary group membership
        String group = rs.getString(prefix + PRIMARY_GROUP);
        if (group != null) {
            user.setPrimaryGroup(new GroupData(group));
        }

        user.setDataComplete(true);
    }


    /**
     * Update an existing UserData object with values from the result
     * set.
     *
     * @param   rs          History table result set containing the user data
     * @param   user        Existing user object
     * @param   tablename   TRUE if the column names should have the full
     *                      table name prefix such as login.username
     *
     * @throws  SQLException if the result set cannot be parsed correctly
     */
    public static void getHistoryData(ResultSet rs, UserData user, boolean tablename)
        throws SQLException
    {
        String prefix = null;
        if (tablename) {
            prefix = HISTORY_TABLE + ".";
        } else {
            prefix = "";
        }

        user.setFailureCount(rs.getInt(prefix + FAILURE_COUNT));
        user.setFailedLogin(rs.getDate(prefix + FAILURE_DATE));
        user.setSuccessfulLogin(rs.getDate(prefix + SUCCESS_DATE));

    }



    /**
     * Return the SQL syntax for setting user fields. 
     *
     * @param   user    User data to be stored
     * @return  SQL syntax for adding or updating user data 
     */
    public static String getUserSql(UserData user) {
        StringBuffer sql = new StringBuffer();

        if (user.getUsername() != null)     sql.append(USERNAME + "='" + user.getUsername() + "'");
        if (user.getPrimaryGroup() != null) sql.append(", " + PRIMARY_GROUP + "='" + user.getPrimaryGroup().getGid() + "'");
        if (user.getEmailAddress() != null) sql.append(", " + EMAIL + "='" + user.getEmailAddress() + "'");
        if (user.getFirstName() != null)    sql.append(", " + FIRSTNAME + "='" + user.getFirstName() + "'");
        if (user.getMiddleName() != null)   sql.append(", " + MIDDLENAME + "='" + user.getMiddleName() + "'");
        if (user.getLastName() != null)     sql.append(", " + LASTNAME + "='" + user.getLastName() + "'");

        if (user.getLocale() != null) {
            sql.append(", " + LANGUAGE + "='" + user.getLocale().getLanguage() + "'");
            sql.append(", " + COUNTRY + "='" + user.getLocale().getCountry() + "'");
        }

        if (user.getPassword() != null) {
            switch(user.getPasswordEncryption()) {
                case UserData.CRYPT_PASSWORD:
                    sql.append(", " + PASSWORD + "='" + user.getPassword() + "'");
                    sql.append(", " + PASSWORD_TYPE + "='" + getEncryptionType(user) + "'");
                    break;
                case UserData.MD5_PASSWORD:
                    sql.append(", " + PASSWORD + "='" + user.getPassword() + "'");
                    sql.append(", " + PASSWORD_TYPE + "='" + getEncryptionType(user) + "'");
                    break;
                default:
                    sql.append(", " + PASSWORD + "=ENCRYPT('" + user.getPassword() + "')");
                    sql.append(", " + PASSWORD_TYPE + "='" + PASSWORD_TYPE_CRYPT + "'");
            }
        }

        String status = getUserStatus(user);
        if (status != null) sql.append(", " + STATUS + "='" + status + "'");

        return sql.toString();
    }


    /**
     * Return the SQL syntax for setting login history fields. 
     *
     * @param   user    User data to be stored
     * @return  SQL syntax for adding or updating login history
     */
    public static String getHistorySql(UserData user) {
        StringBuffer sql = new StringBuffer();

        sql.append(FAILURE_COUNT + " = '" + user.getFailureCount() + "'");
        if (user.getSuccessfulLogin() != null) {
            sql.append(", " + SUCCESS_DATE + " = '" + DATETIME.format(user.getSuccessfulLogin()) + "'");
        }
        if (user.getFailedLogin() != null) {
            sql.append(", " + FAILURE_DATE + " = '" + DATETIME.format(user.getFailedLogin()) + "'");
        }

        return sql.toString();
    }


    /**
     * Store the user data in the database.
     *
     * @param   conn    Connection to the database
     * @param   user    User data to be stored
     * @return  TRUE if the data was inserted, false otherwise
     * @throws  SQLException if the data cannot be stored correctly
     */
    public boolean addUser(Connection conn, UserData user)
        throws SQLException
    {
        boolean result = false;

        StringBuffer sql = new StringBuffer();
        sql.append("INSERT " + TABLE_NAME + " SET ");
        sql.append(getUserSql(user));

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            execute(st, "addUser", sql.toString());
            result = true;
        } catch (SQLException ex) {
            System.err.println("Failed to insert new user: " + sql.toString());
            ex.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return result;
    }


    /**
     * Delete the user data from the database.
     *
     * @param   conn    Connection to the database
     * @param   uid     User ID 
     * @return  TRUE if the data was deleted, false otherwise
     * @throws  SQLException if the data cannot be stored correctly
     */
    public boolean deleteUser(Connection conn, String uid)
        throws SQLException
    {
        boolean result = false;

        StringBuffer sql = new StringBuffer();
        sql.append("DELETE FROM " + TABLE_NAME);
        sql.append(" WHERE " + USER_ID + "='" + uid + "'");

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            execute(st, "deleteUser", sql.toString());
            result = true;
        } catch (SQLException ex) {
            System.err.println("Failed to insert new user: " + sql.toString());
            ex.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return result;
    }

    /**
     * Update user status (such as when disabling an account).
     *
     * @param   conn    Connection to the database
     * @param   uid     User ID
     * @param   status  New status
     * @throws  SQLException if the data cannot be updated
     */
    public boolean updateStatus(Connection conn, String uid, String status) 
        throws SQLException
    {
        boolean result = false;

        StringBuffer sql = new StringBuffer();
        sql.append("UPDATE " + TABLE_NAME + " SET ");
        sql.append(STATUS + " = '" + status + "'");
        sql.append(" WHERE " + USER_ID + "=" + uid);

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            execute(st, "updateStatus", sql.toString());
            result = true;
        } catch (SQLException ex) {
            System.err.println("Failed to update user status: " + sql.toString());
            ex.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return result;
    } 


    /**
     * Store the user data in the database.
     *
     * @param   conn    Connection to the database
     * @param   user    User data to be stored
     * @return  TRUE if the data was updated, false otherwise
     * @throws  SQLException if the data cannot be stored correctly
     */
    public boolean updateUser(Connection conn, UserData user) 
        throws SQLException
    {
        boolean result = false;

        StringBuffer sql = new StringBuffer();
        sql.append("UPDATE " + TABLE_NAME + " SET ");
        sql.append(getUserSql(user));
        sql.append(" WHERE " + USER_ID + "=" + user.getUid());

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            execute(st, "updateUser", sql.toString());
            result = true;
        } catch (SQLException ex) {
            System.err.println("Failed to update user: " + sql.toString());
            ex.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return result;
    }


    /**
     * Create a new login history entry in the database. 
     *
     * @param   conn    Connection to the database
     * @param   user    User data to be stored
     * @return  TRUE if the data was updated, false otherwise
     * @throws  SQLException if the data cannot be stored correctly
     */
    public boolean addHistory(Connection conn, UserData user)
        throws SQLException
    {
        boolean result = false;

        StringBuffer sql = new StringBuffer();
        sql.append("INSERT " + HISTORY_TABLE + " SET ");
        sql.append(getHistorySql(user));
        sql.append(", " + USER_ID + " = " + user.getUid());

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            execute(st, "addHistory", sql.toString());
            result = true;
        } catch (SQLException ex) {
            System.err.println("Failed to add login history: " + sql.toString());
            ex.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return result;
    }



    /**
     * Store the user login history in the database.
     *
     * @param   conn    Connection to the database
     * @param   user    User data to be stored
     * @return  TRUE if the data was updated, false otherwise
     * @throws  SQLException if the data cannot be stored correctly
     */
    public boolean updateHistory(Connection conn, UserData user)
        throws SQLException
    {
        boolean result = false;

        StringBuffer sql = new StringBuffer();
        sql.append("UPDATE " + HISTORY_TABLE + " SET ");
        sql.append(getHistorySql(user));
        sql.append(" WHERE " + USER_ID + " = " + user.getUid());

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            execute(st, "updateHistory", sql.toString());
            result = true;
        } catch (SQLException ex) {
            System.err.println("Failed to update login history: " + sql.toString());
            ex.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return result;
    }


    /**
     * Retrieve a list of users from the database. 
     *
     * @param   conn        Connection to the database
     * @return  List of user data
     *
     * @throws  SQLException if the result set cannot be parsed correctly
     */
    public Vector<UserData> getUsers(Connection conn)
        throws SQLException
    {
        Vector<UserData> users = null;
        Statement st = conn.createStatement();

        // Execute the query
        String sql = "SELECT * FROM " + TABLE_NAME;

        ResultSet rs = null;
        try {
            // Obtain a list of groups
            Vector<GroupData> groups = LoginGroupTable.getGroups(conn);

            // Obtain the list of users
            rs = executeQuery(st, "getUsers", sql);
            users = new Vector<UserData>();
            rs.first();
            do {
                UserData user = getUserData(rs, true);

                // Retrieve complete group information for the primary group
                GroupData primary = user.getPrimaryGroup();
                GroupData current = null;
                if ((primary != null) && (groups != null)) {
                    Enumeration groupList = groups.elements();
                    while (groupList.hasMoreElements()) {
                        current = (GroupData) groupList.nextElement();
                        if ((primary.getGid() != null) && (current.getGid() != null)) {
                            int pid = Integer.parseInt(primary.getGid());
                            int cid = Integer.parseInt(current.getGid());
                            if (pid == cid) {
                                user.setPrimaryGroup(current);
                            }
                        }
                    }
                }

                users.add(user);
            } while (rs.next());
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return users;
    }

    /**
     * Returns the list of users found in the result set.
     *
     * @param   rs      Result set containing the group data
     * @return  List of users
     * @throws  SQLException if the result set cannot be parsed correctly
     */
    public static Vector<UserData> getUsers(ResultSet rs)
        throws SQLException
    {
        Vector<UserData> users = new Vector<UserData>();
        if (rs.first()) {
            do {
                users.add(getUserData(rs, false));
            } while (rs.next());
        }

        return users;
    }


    /**
     * Return the user account status as a database value.
     *
     * @param  user   User data
     * @return Status as a string
     */
    public static final String getUserStatus(UserData user) {
        switch (user.getStatus()) {
            case UserData.ACCOUNT_ACTIVE:
                return STATUS_ACTIVE;
            case UserData.ACCOUNT_INACTIVE:
                return STATUS_INACTIVE;
            case UserData.ACCOUNT_DELETED:
                return STATUS_DELETED;
            case UserData.ACCOUNT_ABUSE:
                return STATUS_ABUSE;
            default:
                return null;
        }

    }

    /**
     * Set the account status of the user.
     *
     * @param  status  Account status database value
     */
    public static final void setUserStatus(UserData user, String status) {
        if (status.equalsIgnoreCase(STATUS_ACTIVE)) {
            user.setStatus(UserData.ACCOUNT_ACTIVE);
        } else if (status.equalsIgnoreCase(STATUS_INACTIVE)) {
            user.setStatus(UserData.ACCOUNT_INACTIVE);
        } else if (status.equalsIgnoreCase(STATUS_DELETED)) {
            user.setStatus(UserData.ACCOUNT_DELETED);
        } else if (status.equalsIgnoreCase(STATUS_ABUSE)) {
            user.setStatus(UserData.ACCOUNT_ABUSE);
        }
    }


    /**
     * Return the password encryption type as a database value.
     * If no encryption type is specified, null will be returned.
     *
     * @param  user   User data
     * @return Encryption type string
     */
    public static final String getEncryptionType(UserData user) {
        switch (user.getPasswordEncryption()) {
            case UserData.CRYPT_PASSWORD:
                return PASSWORD_TYPE_CRYPT;
            case UserData.MD5_PASSWORD:
                return PASSWORD_TYPE_MD5;
            default:
                return null;
        }

    }

    /**
     * Return the password encryption type as a UserData value.
     *
     * @param  type    Encryption type
     *  
     * @return Encryption type value used in the database 
     */
    public static final int getEncryptionType(String type) {
        int typeAsInt = -1;
        if (type != null) {
            if (type.equalsIgnoreCase(PASSWORD_TYPE_CRYPT)) {
                typeAsInt = UserData.CRYPT_PASSWORD;
            } else if (type.equalsIgnoreCase(PASSWORD_TYPE_MD5)) {
                typeAsInt = UserData.MD5_PASSWORD;
            }
        }
        return typeAsInt;
    }



}
