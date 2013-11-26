/*
 * DatabaseRepository.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.web.database;

/**
 * The DatabaseRepository class contains information required to connect to a 
 * database.
 * 
 * @version            $Revision: 1.1.1.1 $  
 * @author             Shawn Stafford
 */
public class DatabaseRepository extends DataRepository {

    /** Username used to connect to the repository */
    private String username;

    /** Password used to connect to the repository */
    private String password;

    /** URL used to establish a connection to the repository */
    private String url;

    /** Class containing the methods and code used to connect to the repository */
    private String driver;

    /**
     * Construct the database repository.
     */
    protected DatabaseRepository() {
    }

    /**
     * Construct the repository
     *
     * @param   url     URL used to connect to the repository
     */
    public DatabaseRepository(String url) {
        this.url = url;
        setDescription("Database repository: " + url);
    }

    /**
     * Sets the URL used to establish a connection to the repository
     *
     * @param   url     URL used to connect to the repository
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Sets the username used to connect to the repository.
     *
     * @param   username    Name used when connecting to the repository
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Sets the password used to connect to the repository.
     *
     * @param   password    Password used when connecting to the repository
     */
    public void setPassword(String password) {
        this.password = password;
    }


    /**
     * Returns the URL used to establish a connection to the repository
     *
     * @return  URL used to connect to the repository
     */
    public String getUrl() {
        return url;
    }


    /**
     * Returns the username used to connect to the repository.
     *
     * @return  Name used when connecting to the repository
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the password used to connect to the repository.
     *
     * @return  Password used when connecting to the repository
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the driver class used to connect to the repository.
     *
     * @param   driver  Class used to connect to the repository
     */
    public void setDriver(String driver) {
        this.driver = driver;
    }

    /**
     * Returns the driver used to connect and communicate with the repository.
     *
     * @return  Driver used to connect to the repository
     */
    public String getDriver() {
        return driver;
    }


}
