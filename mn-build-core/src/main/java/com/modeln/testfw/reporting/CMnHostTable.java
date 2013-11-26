/* 
* Copyright 2000-2003 by Model N, Inc.  All Rights Reserved. 
* 
* This software is the confidential and proprietary information 
* of Model N, Inc ("Confidential Information").  You shall not 
* disclose such Confidential Information and shall use it only 
* in accordance with the terms of the license agreement you 
* entered into with Model N, Inc. 
*/
package com.modeln.testfw.reporting;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;
import java.text.SimpleDateFormat;

/**
 * The host table provides methods for extracting host information from
 * the build database tables. 
 * 
 * @author  Shawn Stafford
 */
public class CMnHostTable extends CMnTable {


    /** Name of the column that identifies a user account on a host machine. */
    public static final String HOST_ACCOUNT = "username";

    /** Name of the column that identifies a host machine. */
    public static final String HOST_NAME = "hostname";

    /** Name of the column that identifies a version of a JDK. */
    public static final String JDK_VERSION = "jdk_version";

    /** Name of the column that identifies a vendor of a JDK. */
    public static final String JDK_VENDOR = "jdk_vendor";

    /** Name of the column that identifies the Operating System name. */
    public static final String OS_NAME = "os_name";

    /** Name of the column that identifies the Operating System chip architecture. */
    public static final String OS_ARCHITECTURE = "os_arch";

    /** Name of the column that identifies the Operating System version. */
    public static final String OS_VERSION = "os_version";

    /** Singleton instance of the table class */
    private static CMnHostTable instance;


    /**
     * Return the singleton instance of the class.
     */
    public static CMnHostTable getInstance() {
        if (instance == null) {
            instance = new CMnHostTable();
        }
        return instance;
    }


    /**
     * Parse the result set to obtain host information.
     * 
     * @param   rs    Result set containing host data
     *
     * @return  Host information
     */
    public static CMnDbHostData parseHostData(ResultSet rs) 
        throws SQLException 
    {
        CMnDbHostData data = new CMnDbHostData();

        String username = rs.getString(HOST_ACCOUNT);
        data.setUsername(username);

        String hostname = rs.getString(HOST_NAME);
        data.setHostname(hostname);

        String jdkVersion = rs.getString(JDK_VERSION);
        data.setJdkVersion(jdkVersion);

        String jdkVendor = rs.getString(JDK_VENDOR);
        data.setJdkVendor(jdkVendor);

        String osName = rs.getString(OS_NAME);
        data.setOSName(osName);

        String osArch = rs.getString(OS_ARCHITECTURE);
        data.setOSArchitecture(osArch);

        String osVersion = rs.getString(OS_VERSION);
        data.setOSVersion(osVersion);

        return data;

    }

}

