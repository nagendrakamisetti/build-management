/*
 * GroupData.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.common.data.account;

/**
 * The GroupData object contains information about a single login group,
 * such as its ID, name, description, and type.
 *
 * @version            $Revision: 1.1.1.1 $
 * @author             Shawn Stafford
 *
 */
public class GroupData {

    // Provide names for each data field which should be publicly available
    // (ie: names which correspond to "get" and "set" methods)
    // This is used by other classes to extract data from a hashtable, etc.
    public static final String GROUP_ID     = "Gid";
    public static final String NAME         = "Name";
    public static final String TYPE         = "Type";
    public static final String DESCRIPTION  = "Desc";


    // Group types
    public static final int ADMIN_GROUP_TYPE = 0;
    public static final int USER_GROUP_TYPE = 1;

    public static final int SELF_PERMISSIONS    = 0;
    public static final int GROUP_PERMISSIONS   = 1;
    public static final int USER_PERMISSIONS    = 2;
    public static final int DEVICE_PERMISSIONS  = 3;

    public static final int EDIT    = 0;
    public static final int ADD     = 1;
    public static final int DELETE  = 2;

    private String  gid;
    private String  parent;
    private String  name;
    private String  desc;
    private int     type;

    // Admin permissions
    private boolean[][] permissions = {
        {false, false, false},      // Self
        {false, false, false},      // Group
        {false, false, false},      // User
        {false, false, false}       // Device
    };


    /**
     * Construct a group object.
     */
    private GroupData() {
    }

    /**
     * Construct a group with the given group ID.
     */
    public GroupData(String id) {
        this.gid = id;
    }

    /**
     * Construct a group with the given group ID and parent ID.
     */
    public GroupData(String id, String parent) {
        this.gid = id;
        this.parent = parent;
    }

    /**
     * Sets the permission value for the given field and function.
     * For example, to allow the users to edit their own information,
     * the permissions should be set using:
     * <code>
     *   setPermission(SELF_PERMISSIONS, EDIT, true);
     * </code>
     *
     * @param   field   The permissions field to be modified
     * @param   perm    The permission being added or removed
     * @param   value   The permission value
     */
    public void setPermission(int field, int perm, boolean value) {
        permissions[field][perm] = value;
    }

    /**
     * Returns the permission value for the given field and function.
     * For example, to determine if the users are allowed to edit 
     * their own information, the permission can be determined by:
     * <code>
     *   getPermission(SELF_PERMISSIONS, EDIT);
     * </code>
     *
     * @param   field   The permissions field to be modified
     * @param   perm    The permission being added or removed
     * @return  The boolean permission setting for the given field
     */
    public boolean getPermission(int field, int perm) {
        return permissions[field][perm];
    }

    
    /**
     * Sets the group id field.
     * 
     * @param   id    value to be set
     */
    public void setGid(String id) {
        gid = id;
    }

    /**
     * Returns the group id value.
     *
     * @return  group id value
     */
    public String getGid() {
        return gid;
    }

    /**
     * Sets the parent id field, which refers to the group
     * which owns this group.
     * 
     * @param   id    value to be set
     */
    public void setParent(String id) {
        parent = id;
    }

    /**
     * Returns the parent id field, which refers to the group
     * which owns this group.
     *
     * @return  parent id value
     */
    public String getParent() {
        return parent;
    }

    /**
     * Sets the group name field.
     * 
     * @param   name    group name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the group name.
     *
     * @return  group name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the group description field.
     * 
     * @param   desc    group description
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * Returns the group description value.
     *
     * @return  group description
     */
    public String getDesc() {
        return desc;
    }

    /**
     * Sets the group type field.
     * 
     * @param   type    group type
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * Returns the group type value.
     *
     * @return  group type value
     */
    public int getType() {
        return type;
    }

    /**
     * Convenience method to determine if the group is an 
     * admin group.
     *
     * @return TRUE if the group is of type 'admin'
     */
    public boolean isAdmin() {
        return (type == ADMIN_GROUP_TYPE);
    }


}
