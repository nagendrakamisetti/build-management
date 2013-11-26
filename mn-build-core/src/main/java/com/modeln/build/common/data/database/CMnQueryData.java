/*
* Copyright 2000-2003 by Model N, Inc.  All Rights Reserved.
*
* This software is the confidential and proprietary information
* of Model N, Inc ("Confidential Information").  You shall not
* disclose such Confidential Information and shall use it only
* in accordance with the terms of the license agreement you
* entered into with Model N, Inc.
*/
package com.modeln.build.common.data.database;

import java.lang.String;
import java.lang.StringBuffer;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * The database query data represents a single database query (ie SQL). 
 *
 * @author  Shawn Stafford
 */
public class CMnQueryData {


    /** Name used to identify the query */
    private String sqlName;

    /** SQL text of the query */
    private String sqlText;

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

    /** General messages associated with the query, such as status or errors */
    private String message;

    /** Query results returned by the database query */
    private CMnCachedResultSet resultset;


    /** Oracle database type */
    public final static String ORACLE_TYPE = "oracle";

    /** MySQL database type */
    public final static String MYSQL_TYPE = "mysql";


    /** List of allowable database types */
    public final static String[] VALID_TYPES = {ORACLE_TYPE, MYSQL_TYPE};



    /**
     * Construct an empty query data object.
     */
    public CMnQueryData() {
    }

    /**
     * Set the database information by parsing the JDBC url.
     *
     * @param  url  JDBC URL
     */
    public CMnQueryData(String url) {
        StringBuffer msg = new StringBuffer();

        if (url != null) {
            // URL Format:  jdbc:oracle:thin:username/password@hostname:port:sid
            // URL Format:  jdbc:mysql://hostname/database?username=value&password=value

            try {
                StringTokenizer st = new StringTokenizer(url, ":");
                msg.append("URL tokens: " + st.countTokens());
                if (st.countTokens() >= 3) {
                    st.nextToken();  // jdbc
                    dbType = st.nextToken();
                    if ((dbType != null) && (dbType.equals(ORACLE_TYPE))) {
                        msg.append(" (oracle)");
                        st.nextToken();  // thin
                        String auth = st.nextToken();
                        dbPort = st.nextToken();
                        dbName = st.nextToken();
                        msg.append(" sid = " + dbName);

                        // Parse the authentication info
                        int pwIdx = auth.indexOf("/");
                        int hostIdx = auth.indexOf("@");
                        if (pwIdx > 0) {
                            dbUsername = auth.substring(0, pwIdx);
                        } else {
                            msg.append(", no user (/ in " + auth + ")");
                        }
                        if (hostIdx >= 0) {
                            dbHostname = auth.substring(hostIdx + 1);
                        } else {
                            msg.append(", no host (@ in " + auth + ")");
                        }
                        if ((pwIdx > 0) && (hostIdx > 0)) {
                            dbPassword = auth.substring(pwIdx + 1, hostIdx);
                        } else {
                            msg.append(", no pass (/ to @ in " + auth + ")");
                        }

                    } else if ((dbType != null) && (dbType.equals(MYSQL_TYPE))) {
                        // Give up on the string tokenizer since the port number causes issues
                        String info = "";
                        while (st.hasMoreTokens()) {
                            if (info.length() > 0) {
                                info = info + ":" + st.nextToken();
                            } else {
                                info = st.nextToken();
                            }
                        }

                        // Parse the remaining URL content
                        if ((info != null) && (info.startsWith("//"))) {
                            // Parse the hostname
                            int dbIdx = info.indexOf("/", 2);
                            int authIdx = info.indexOf("?");
                            if (dbIdx > 0) {
                                dbHostname = info.substring(2, dbIdx);

                                // Parse the database name
                                if (authIdx > 0) {
                                    dbName = info.substring(dbIdx + 1, authIdx);
                                } else {
                                    dbName = info.substring(dbIdx + 1);
                                }
                            } else {
                                msg.append(", no db name (/ in " + info + ")");
                            }

                            // Parse the username and password
                            if (authIdx > 0) {
                                String auth = info.substring(authIdx);

                                StringTokenizer stAttr = new StringTokenizer("&");
                                if (stAttr.countTokens() == 2) {
                                    String user = stAttr.nextToken();
                                    int uDelimiter = user.indexOf("=");
                                    if (uDelimiter > 0) {
                                        dbUsername = user.substring(uDelimiter + 1);
                                    }
                                    String pass = stAttr.nextToken();
                                    int pDelimiter = pass.indexOf("=");
                                    if (pDelimiter > 0) {
                                        dbPassword = pass.substring(pDelimiter + 1);
                                    }
                                } else {
                                    msg.append(", missing username or password (" + stAttr.countTokens() + ")");
                                }
                            } else {
                                msg.append(", no auth delimiter (? in " + info + ")");
                            }
                        }
                        msg.append(" (mysql)");
                    } else {
                        msg.append(" (unknown)");
                    }
                }
            } catch (NoSuchElementException ex) {
                ex.printStackTrace();
            }
        } else {
            msg.append("URL is null");
        }

        sqlName = msg.toString();
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
     * Return a JDBC driver for the current database type.
     *
     * @return JDBC driver
     */
    public String getJdbcDriver() {
        String driver = null;

        if (dbType != null) {
            if (dbType.equals(ORACLE_TYPE)) {
                return "oracle.jdbc.driver.OracleDriver"; 
            } else if (dbType.equals(MYSQL_TYPE)) {
                return "com.mysql.jdbc.Driver"; 
            }
        }

        return driver;
    }



    /**
     * Return a JDBC URL for the current database type.
     *
     * @return JDBC URL
     */
    public String getJdbcUrl() {
        StringBuffer url = new StringBuffer();

        if (dbType != null) {
            if (dbType.equals(ORACLE_TYPE)) {
                url.append("jdbc:oracle:thin:");
                if (dbUsername != null) {
                    url.append(dbUsername);
                    if (dbPassword != null) {
                        url.append("/" + dbPassword + "@");
                    }
                }
                url.append(dbHostname);
                url.append(":" + dbPort);
                url.append(":" + dbName);
            } else if (dbType.equals(MYSQL_TYPE)) {
                url.append("jdbc:mysql://");
                url.append(dbHostname + "/" + dbName);
                if (dbUsername != null) {
                    url.append("?" + "user=" + dbUsername);
                    if (dbPassword != null) {
                        url.append("&" + "password=" + dbPassword);
                    }
                }
            }
        }

        return url.toString();
    }

    /**
     * Set the text of the query.
     *
     * @param  text   SQL text
     */
    public void setSQL(String sql) {
        sqlText = sql;
    }

    /**
     * Return the text of the query.
     *
     * @return SQL text
     */
    public String getSQL() {
        return sqlText;
    }

    /**
     * Set a unique name for the query.  The query name is used to identify
     * the query in the query history cache.
     *
     * @param  name   Query name
     */
    public void setName(String name) {
        sqlName = name;
    }

    /**
     * Return the name of the query. 
     *
     * @return Query name 
     */
    public String getName() {
        return sqlName;
    }

    /**
     * Set a message to be associated with the query results. 
     *
     * @param  text   Message text
     */
    public void setMessage(String text) {
        message = text;
    }

    /**
     * Return a message associated with the query. 
     *
     * @return Message text
     */
    public String getMessage() {
        return message;
    }

    /**
     * Set the result set returned by the database query. 
     *
     * @param  results  Result set 
     */
    public void setResults(CMnCachedResultSet results) {
        resultset = results;
    }

    /**
     * Return the result set from the database query. 
     *
     * @return Result set 
     */
    public CMnCachedResultSet getResults() {
        return resultset;
    }

}


