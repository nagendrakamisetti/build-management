/*
 * GroupParticipationTable.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.common.database;

import java.sql.*;

/**
 * The LoginGroupTable loads group data from the login_group table
 * by mapping database fields to the correct data members.
 *
 * @version            $Revision: 1.1.1.1 $
 * @author             Shawn Stafford
 *
 */
public class GroupParticipationTable {
    public static final String TABLE_NAME       = "group_participation";

    // Table columns
    public static final String GROUP_ID         = "group_id";
    public static final String USER_ID          = "user_id";

    /**
     * Construct the table object.
     */
    private GroupParticipationTable() {
    }

}
