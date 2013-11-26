/*
 * JoinTables.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.common.database;

import java.sql.*;
import java.util.Vector;
import com.modeln.build.common.data.account.*;


/**
 * JoinTables accesses database information from several tables
 * by performing join operations to link tables together.  This 
 * is more efficient than running multiple SELECT operations to 
 * retrieve information in multiple tables.
 *
 * @version            $Revision: 1.1.1.1 $
 * @author             Shawn Stafford
 *
 */
public class JoinTables {

    /**
     * Construct the parser object.
     */
    private JoinTables() {
    }

    /**
     * Create a new UserData object by connecting to the database and
     * retrieving the given user.  Information is retrieved by joining records
     * from multiple tables using the user ID field.
     *
     * @param   conn        Connection to the database
     * @param   uid         User ID field which is used to join the login tables
     * @throws  SQLException if the result set cannot be parsed correctly
     */
    public static UserData getUserData(Connection conn, String uid) 
        throws SQLException
    {
        Statement st = conn.createStatement();
        String query = null;

        // Join the various login tables using the user_id field
        String from = "FROM " + LoginTable.TABLE_NAME + "," + LoginIdentityTable.TABLE_NAME;
        if ((uid != null) && (uid.length() > 0)) {
            query = "SELECT * " + from + " WHERE " + 
                LoginTable.TABLE_NAME + "." + LoginTable.USER_ID + "='" + uid + "' AND " +
                LoginIdentityTable.TABLE_NAME + "." + LoginIdentityTable.USER_ID + "='" + uid + "'";
        } else {
            throw new SQLException("Unable to join tables. Invalid UserId value: " + uid);
        }

        ResultSet rs = null;
        UserData user = null;
        try {
            // Execute the query
            rs = st.executeQuery(query);
            rs.first();
            user = LoginTable.getUserData(rs, true);
            LoginIdentityTable.getUserData(rs, user);
            user.setSecondaryGroups(getGroups(conn, uid));

        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return user;
    }

    /**
     * Retrieve the list of groups associated with a particular user ID.
     * This only retrieves the list of SECONDARY groups, which are contained
     * in the group participation table.  The primary group must be obtained
     * from the user login table.
     *
     * @param   conn        Connection to the database
     * @param   uid         User ID field which is used to join the login tables
     * @throws  SQLException if the result set cannot be parsed correctly
     */
    public static Vector getGroups(Connection conn, String uid) 
        throws SQLException
    {
        Statement st = conn.createStatement();
        String query = null;

        // Join the various login tables using the user_id field
        String from = "FROM " + GroupParticipationTable.TABLE_NAME + "," + LoginGroupTable.TABLE_NAME;
        if ((uid != null) && (uid.length() > 0)) {
            query = "SELECT * " + from + " WHERE " + 
                GroupParticipationTable.TABLE_NAME + "." + GroupParticipationTable.USER_ID + "='" + uid + "' AND " +
                GroupParticipationTable.TABLE_NAME + "." + GroupParticipationTable.GROUP_ID + "=" + LoginGroupTable.TABLE_NAME + "." + LoginGroupTable.GROUP_ID;
        } else {
            throw new SQLException("Unable to join tables. Invalid UserId value: " + uid);
        }

        ResultSet rs = null;
        Vector groups = null;
        try {
            // Execute the query
            rs = st.executeQuery(query);
            groups = LoginGroupTable.getGroups(rs);
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return groups;
    }

}
