/*
 * DatabaseField.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.common.database;


/**
 * The DatabaseField object represents a field data stored in a database.
 *
 * @version            $Revision: 1.1.1.1 $
 * @author             Shawn Stafford
 *
 */
public class DatabaseField {

    /** TRUE if the data object matches the data in the database, FALSE otherwise */
    protected boolean matchesDatabase = false;

    /** Name of the field in the database. */
    protected String field;


    /**
     * Determines whether the data object is up-to-date with the
     * information stored in the database.
     *
     * @return  TRUE if the data is up-to-date, FALSE otherwise
     */
    public boolean getDatabaseStatus() { return matchesDatabase; }
    public boolean isUpToDate() { return matchesDatabase; }

    /**
     * Sets the synchronization status of the information in the
     * database.
     *
     * @param   matches  TRUE if the data is up-to-date, FALSE otherwise
     */
    public void setDatabaseStatus(boolean matches) {
        matchesDatabase = matches;
    }

}
