/*
* Copyright 2000-2003 by Model N, Inc.  All Rights Reserved.
*
* This software is the confidential and proprietary information
* of Model N, Inc ("Confidential Information").  You shall not
* disclose such Confidential Information and shall use it only
* in accordance with the terms of the license agreement you
* entered into with Model N, Inc.
*/
package com.modeln.build.common.data.product;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * The database query data used to search for a single build 
 * on a remote database instance. 
 *
 * @author  Shawn Stafford
 */
public class CMnBuildQuery {


    /** Hostname of the database server */
    private String dbHostname;

    /** Port number on which the database accepts connections */
    private String dbPort;

    /** Database username */
    private String dbUsername;

    /** Database password */
    private String dbPassword;

    /** Database name */
    private String dbName;

    /** Database type (oracle, mysql, etc) */
    private String dbType;

    /** Build ID */
    private String buildId;


    /** Oracle database type */
    public final static String ORACLE_TYPE = "oracle";

    /** MySQL database type */
    public final static String MYSQL_TYPE = "mysql";


    /** List of allowable database types */
    public final static String[] VALID_TYPES = {ORACLE_TYPE, MYSQL_TYPE};



    /**
     * Construct an empty query data object.
     */
    public CMnBuildQuery() {
    }

    /**
     * Set the database information by parsing the JDBC url.
     *
     * @param  url  JDBC URL
     */
    public CMnBuildQuery(String url) {
        if (url != null) {
            // URL Format:  jdbc:oracle:thin:username/password@hostname:port:sid
            // URL Format:  jdbc:mysql://hostname/database?username=value&password=value

            try {
                StringTokenizer st = new StringTokenizer(url, ":");
                if (st.countTokens() >= 3) {
                    st.nextToken();  // jdbc
                    dbType = st.nextToken();
                    if ((dbType != null) && (dbType.equals(ORACLE_TYPE))) {
                        st.nextToken();  // thin
                        String auth = st.nextToken();
                        dbPort = st.nextToken();
                        dbName = st.nextToken();

                        // Parse the authentication info
                        int pwIdx = auth.indexOf("/");
                        int hostIdx = auth.indexOf("@");
                        dbUsername = auth.substring(0, pwIdx);
                        dbPassword = auth.substring(pwIdx + 1, hostIdx);
                        dbHostname = auth.substring(hostIdx + 1);

                    } else if ((dbType != null) && (dbType.equals(MYSQL_TYPE))) {
                        String auth = st.nextToken();
                    }
                }
            } catch (NoSuchElementException ex) {
                ex.printStackTrace();
            }
        }
    }


    /**
     * Set the database hostname to connect to when executing the query. 
     *
     * @param  host  Hostname of the database server 
     */
    public void setHostname(String host) {
        dbHostname = host;
    }

    /**
     * Return the hostname of the database server. 
     *
     * @return Database hostname 
     */
    public String getHostname() {
        return dbHostname;
    }


    /**
     * Set the database port number to connect to when executing the query.
     *
     * @param  port Server port to connect 
     */
    public void setPort(String port) {
        dbPort = port;
    }

    /**
     * Return the port number of the database server.
     *
     * @return Database port number
     */
    public String getPort() {
        return dbPort;
    }


    /**
     * Set the username for the database. 
     *
     * @param  user  Database username 
     */
    public void setUsername(String user) {
        dbUsername = user;
    }

    /**
     * Return the username for the database. 
     *
     * @return Database username 
     */
    public String getUsername() {
        return dbUsername;
    }

    /**
     * Set the password for the database. 
     *
     * @param  pass   Database password 
     */
    public void setPassword(String pass) {
        dbPassword = pass;
    }

    /**
     * Return the password for the database. 
     *
     * @return Database password 
     */
    public String getPassword() {
        return dbPassword;
    }

    /**
     * Set the database name.  For Oracle databases, this corresponds to the
     * database SID.  For MySQL this corresponds to the database schema. 
     *
     * @param  name   Database name 
     */
    public void setDbName(String name) {
        dbName = name;
    }

    /**
     * Return the name of the database. 
     *
     * @return Database name 
     */
    public String getDbName() {
        return dbName;
    }

    /**
     * Set the database type.  Valid types include: oracle, mysql 
     *
     * @param  type   Database type
     */
    public void setType(String type) {
        dbType = type;
    }

    /**
     * Return the database type. 
     *
     * @return Database type
     */
    public String getType() {
        return dbType;
    }


    /**
     * Return a JDBC URL for the current database type.
     *
     * @return JDBC URL
     */
    public String getJdbcUrl() {
        String url = null;

        if (dbType != null) {
            if (dbType.equals(ORACLE_TYPE)) {
                return "jdbc:oracle:thin:" + dbUsername + "/" + dbPassword + "@" + dbHostname + ":" + dbPort + ":" + dbName;
            } else if (dbType.equals(MYSQL_TYPE)) {
                return "jdbc:mysql://" + dbHostname + "/" + dbName + "?" + "user=" + dbUsername + "&" + "password=" + dbPassword;
            }
        }

        return url;
    }


    /**
     * Set the build ID. 
     *
     * @param  id   Build ID 
     */
    public void setBuildId(String id) {
        buildId = id;
    }

    /**
     * Return the build ID. 
     *
     * @return Build ID 
     */
    public String getBuildId() {
        return buildId;
    }


}


