/*
 * LoginGroupTable.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.common.database;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.sql.*;
import java.util.StringTokenizer;
import java.util.Enumeration;
import java.util.Vector;

import com.modeln.build.common.data.account.*;
import com.modeln.testfw.reporting.CMnTable;

/**
 * The LoginGroupTable loads group data from the login_group table
 * by mapping database fields to the correct data members.
 *
 * @version            $Revision: 1.1.1.1 $
 * @author             Shawn Stafford
 *
 */
public class LoginGroupTable extends CMnTable {
    public static final String TABLE_NAME       = "login_group";

    // Table columns
    public static final String GROUP_ID         = "group_id";
    public static final String PARENT_ID        = "parent_id";
    public static final String GROUP_NAME       = "group_name";
    public static final String GROUP_DESC       = "group_desc";
    public static final String GROUP_TYPE       = "group_type";
    public static final String SELF_PERMISSIONS    = "perm_self";
    public static final String GROUP_PERMISSIONS   = "perm_group";
    public static final String USER_PERMISSIONS    = "perm_user";

    // Possible permission values
    public static final String EDIT    = "edit";
    public static final String ADD     = "add";
    public static final String DELETE  = "delete";

    // Possible group types
    public static final String TYPE_ADMIN = "admin";
    public static final String TYPE_USER = "user";



    /**
     * Construct the table object.
     */
    private LoginGroupTable() {
    }


    /** Singleton instance of the table class */
    private static LoginGroupTable instance;

    /**
     * Return the singleton instance of the class.
     */
    public static LoginGroupTable getInstance() {
        if (instance == null) {
            instance = new LoginGroupTable();

            // Enable debbuging to a file
            String logfile = "/var/tmp/LoginGroupTable.txt";
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
     * Returns the list of groups found in the result set.
     *
     * @param   rs      Result set containing the group data
     * @param   gid     Group ID
     * @throws  SQLException if the result set cannot be parsed correctly
     */
    public static Vector<GroupData> getGroups(ResultSet rs) 
        throws SQLException
    {
        Vector<GroupData> groups = new Vector<GroupData>();
        rs.first();
        do {
            groups.add(getGroup(rs));
        } while (rs.next());
        
        return groups;
    }

    /**
     * Returns the group information found at the current location in 
     * the result set.
     *
     * @param   rs      Result set containing the group data
     * @param   gid     Group ID
     * @throws  SQLException if the result set cannot be parsed correctly
     */
    public static GroupData getGroup(ResultSet rs) 
        throws SQLException
    {
        GroupData group = new GroupData(rs.getString(GROUP_ID), rs.getString(PARENT_ID));
        group.setName(rs.getString(GROUP_NAME));
        group.setDesc(rs.getString(GROUP_DESC));

        // Set group permissions
        setPermission(group, GroupData.SELF_PERMISSIONS, rs.getString(SELF_PERMISSIONS));
        setPermission(group, GroupData.GROUP_PERMISSIONS, rs.getString(GROUP_PERMISSIONS));
        setPermission(group, GroupData.USER_PERMISSIONS, rs.getString(USER_PERMISSIONS));

        // Set the group type
        String type = rs.getString(GROUP_TYPE);
        if ((type != null) && (type.equalsIgnoreCase(TYPE_ADMIN))) {
            group.setType(GroupData.ADMIN_GROUP_TYPE);
        } else {
            group.setType(GroupData.USER_GROUP_TYPE);
        }

        return group;
    }

    /**
     * Get a list of all the groups associated with the specified users.
     */
    public Vector<GroupData> getGroups(Connection conn, Vector<UserData> users) 
        throws SQLException
    {
        Vector<GroupData> list = null; 

        if ((users != null) && (users.size() > 0)) {
            StringBuffer userSql = new StringBuffer();
            userSql.append("SELECT * FROM " + TABLE_NAME +
                    " WHERE " + GROUP_ID + " in ( ");

            UserData currentUser = null;
            Enumeration userList = users.elements();
            while (userList.hasMoreElements()) {
                currentUser = (UserData) userList.nextElement();
                if ((currentUser != null) && (currentUser.getPrimaryGroup() != null)) {
                    userSql.append(currentUser.getPrimaryGroup().getGid());
                    if (userList.hasMoreElements()) {
                        userSql.append(",");
                    }
                }
            }
            userSql.append(" )");

            Statement st = conn.createStatement();
            ResultSet rs = null;
            try {
                getInstance().debugWrite("getGroups: Attempting to execute: " + userSql.toString());
                rs = executeQuery(st, "getGroups", userSql.toString());
                if (rs != null) {
                    list = getGroups(rs);
                } else {
                    System.err.println("Unable to obtain the group data.");
                }
            } catch (SQLException ex) {
                getInstance().debugWrite("Encountered exception while parsing group data: " + ex.toString());
                System.err.println("Failed to obtain group data: " + userSql.toString());
                ex.printStackTrace();
            } finally {
                if (rs != null) rs.close();
                if (st != null) st.close();
            }
        }

        return list;
    }

    /**
     * Parses the permission strings returned by the database and
     * sets the permissions in the group object.
     *
     * @param   group   Group to which the permissions apply
     * @param   field   Permission field to which the values apply
     * @param   perms   Permission values
     */
    private static void setPermission(GroupData group, int field, String perms) {
        if ((group != null) && (perms != null) && (perms.length() > 0)) {
            StringTokenizer st = new StringTokenizer(perms, ",");
            String perm;
            for (int idx = 0; idx < st.countTokens(); idx++) {
                perm = st.nextToken();
                if (EDIT.equalsIgnoreCase(perm)) {
                    group.setPermission(field, GroupData.EDIT, true);
                } else if (ADD.equalsIgnoreCase(perm)) {
                    group.setPermission(field, GroupData.ADD, true);
                } else if (DELETE.equalsIgnoreCase(perm)) {
                    group.setPermission(field, GroupData.DELETE, true);
                }
            }
        }
    }

    /**
     * Return a list of all groups. 
     *
     * @param   conn        Connection to the database
     * @return  List of groups
     *
     * @throws  SQLException if the result set cannot be parsed correctly
     */
    public static Vector getGroups(Connection conn)
        throws SQLException
    {
        Vector groups = null;

        Statement st = conn.createStatement();
        String query = "SELECT * FROM " + TABLE_NAME;

        ResultSet rs = null;
        try {
            // Execute the query
            rs = st.executeQuery(query);
            rs.first();
            groups = getGroups(rs);
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return groups;
    }


    /**
     * Return a list of groups belonging to the specified group ID.
     *
     * @param   conn        Connection to the database
     * @param   gid         Parent group ID
     * @throws  SQLException if the result set cannot be parsed correctly
     */
    public static Vector getChildren(Connection conn, String gid) 
        throws SQLException
    {
        Vector groups = null;

        Statement st = conn.createStatement();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " +
            PARENT_ID + "='" + gid + "'";

        ResultSet rs = null;
        try {
            // Execute the query
            rs = st.executeQuery(query);
            groups = getGroups(rs);
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return groups;
    }

    /**
     * Return the group information for the given group ID.
     *
     * @param   conn        Connection to the database
     * @param   gid         Group ID
     * @throws  SQLException if the result set cannot be parsed correctly
     */
    public static GroupData getGroup(Connection conn, String gid) 
        throws SQLException
    {
        GroupData group = null;

        Statement st = conn.createStatement();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " +
            GROUP_ID + "='" + gid + "'";

        ResultSet rs = null;
        try {
            // Execute the query
            rs = st.executeQuery(query);
            rs.first();
            group = getGroup(rs);
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return group;
    }

    /**
     * Return the parent group of the given group ID.
     *
     * @param   conn        Connection to the database
     * @param   gid         Child group ID
     * @throws  SQLException if the result set cannot be parsed correctly
     */
    public static GroupData getParent(Connection conn, String gid) 
        throws SQLException
    {
        GroupData child = getGroup(conn, gid);

        return getGroup(conn, child.getParent());
    }

    /**
     * Return a hierarchical representation of the group structure
     * using the given group ID as the root of the hierarchy.  To 
     * prevent excessive database querries, only the information for
     * the current GID and its immediate children will be retrieved
     * from the database.  Lower levels must be populated by calling
     * getChildren(conn, gid, tree)
     *
     * @param   conn        Connection to the database
     * @param   gid         Group ID
     * @throws  SQLException if the result set cannot be parsed correctly
     */
    public static GroupTree getGroupTree(Connection conn, String gid) 
        throws SQLException
    {
        GroupTree tree =  null;

        GroupData group = getGroup(conn, gid);
        if (group != null) {
            GroupTreeNode root = new GroupTreeNode(group);
            tree = new GroupTree(root);
        }

        return tree;
    }

    /**
     * Populates the group information for the children of the specified
     * group.  The given group ID must exist in the tree for this to have
     * any effect.
     *
     * @param   conn        Connection to the database
     * @param   gid         Group ID
     * @param   tree        GroupTree containing the group
     * @throws  SQLException if the result set cannot be parsed correctly
     */
    public static void getChildren(Connection conn, String gid, GroupTree tree) 
        throws SQLException
    {
        GroupTreeNode groupNode = tree.getGroupById(gid);
        if (groupNode != null) {
            // Retrieve the children from the database
            Vector children = getChildren(conn, gid);

            // Add the children to the tree
            int childCount = 0;
            if (children != null) childCount = children.size();
            for (int idx = 0; idx < childCount; idx++) {
                groupNode.add(new GroupTreeNode((GroupData)children.get(idx)));
            }
            groupNode.setComplete(true);
        }
    }
}
