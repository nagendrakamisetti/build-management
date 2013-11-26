/*
 * LoginIdentityTable.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.common.database;

import java.sql.*;
import com.modeln.build.common.data.account.UserData;


/**
 * The LoginIdentityTable loads user data from the login_identity table
 * by mapping database fields to the correct data members.
 *
 * @version            $Revision: 1.1.1.1 $
 * @author             Shawn Stafford
 *
 */
public class LoginIdentityTable {
    public static final String TABLE_NAME       = "login_identity";

    public static final String USER_ID          = "user_id";
    public static final String FIRST_NAME       = "firstname";
    public static final String LAST_NAME        = "lastname";
    public static final String MIDDLE_NAME      = "middlename";
    public static final String TITLE            = "title";
    public static final String LANGUAGE         = "language";
    public static final String COUNTRY          = "country";


    /**
     * Construct the parser object.
     */
    private LoginIdentityTable() {
    }


    /**
     * Create a new UserData object by parsing the result set and 
     * filling in any values found in the Login table.
     *
     * @param   rs  Login table result set containing the user data
     * @throws  SQLException if the result set cannot be parsed correctly
     */
    public static UserData getUserData(ResultSet rs) 
        throws SQLException
    {
        UserData user = null;
        if (rs != null) {
            user = new UserData();
            getUserData(rs, user);
        }

        return user;
    }

    /**
     * Create a new UserData object by connecting to the database and
     * retrieving the given user.  The UserData object passed to the 
     * method is populated with data from the database.
     *
     * @param   conn        Connection to the database
     * @param   user        Data object containing the user information
     * @throws  SQLException if the result set cannot be parsed correctly
     */
    public static void getUserData(Connection conn, UserData user) 
        throws SQLException
    {
        Statement st = conn.createStatement();
        String query = null;

        // The most efficient method to obtain this information is to
        // use the USER_ID if available, otherwise a table joing must
        // be performed on the username
        String uid = user.getUid();
        String username = user.getUsername();
        if ((uid != null) && (uid.length() > 0)) {
            query = "SELECT * FROM " + TABLE_NAME + " WHERE " +
                USER_ID + "='" + uid + "'";
        } else if ((username != null) && (username.length() > 0)) {
            query = "SELECT * FROM " + TABLE_NAME + " WHERE " + 
                LoginTable.TABLE_NAME + "." + LoginTable.USERNAME + "='" + username + "' AND " +
                TABLE_NAME + "." + USER_ID + "=" + LoginTable.TABLE_NAME + "." + LoginTable.USER_ID;
        }

        ResultSet rs = null;
        try {
            // Execute the query
            rs = st.executeQuery(query);
            rs.first();
            getUserData(rs, user);
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

    }

    /**
     * Update an existing UserData object with values from the result
     * set.
     *
     * @param   rs      Login table result set containing the user data
     * @param   user    Existing user object
     * @throws  SQLException if the result set cannot be parsed correctly
     */
    public static void getUserData(ResultSet rs, UserData user) 
        throws SQLException
    {
        // Only set the UID if it doesn't already exist
        String uid = user.getUid();
        if ((uid != null) && (uid.length() > 0)) {
            if (!uid.equals(rs.getString(USER_ID))) {
                throw new SQLException("Existing UID does not match current UID: " + uid + " != " + rs.getString(USER_ID));
            }
        } else {
            user.setUid(rs.getString(USER_ID));
        }
        user.setFirstName(rs.getString(FIRST_NAME));
        user.setLastName(rs.getString(LAST_NAME));
        user.setMiddleName(rs.getString(MIDDLE_NAME));

    }


    /**
     * Store the user data in the database.
     *
     * @param   conn    Connection to the database
     * @param   user    User data to be stored
     * @throws  SQLException if the data cannot be stored correctly
     */
    public static void setUserData(Connection conn, UserData user) 
        throws SQLException
    {
        Statement st = conn.createStatement();

        // Execute the query
        String updateLogin = "UPDATE " + TABLE_NAME + " SET " +
            "username='" + user.getUsername() + "'";
        ResultSet rs = st.executeQuery(updateLogin);
    }
}
