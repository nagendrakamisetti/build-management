package com.modeln.build.web.data;

/**
 * The session activity is used to track users logged in to the application. 
 *
 * @author       Shawn Stafford (sstafford@modeln.com)
 */
public class SessionActivity {

    /** Hostname or IP address where the request originated */
    private String origin;

    /** User ID */
    private String uid;

    /** Username */
    private String username;

    /** Timestamp when the session was created */
    private long initTime;

    /** Timestamp when the session was last accessed */
    private long accessTime; 


    /**
     * Set the hostname or IP address where the request originated.
     *
     * @param  host   Hostname or IP address
     */
    public void setOrigin(String host) {
        origin = host;
    }

    /**
     * Return the hostname or IP address where the request originated.
     *
     * @return  Hostname or IP address
     */
    public String getOrigin() {
        return origin;
    }


    /**
     * Sets the user id field.
     * 
     * @param   id    value to be set
     */
    public void setUid(String id) {
        uid = id;
    }

    /**
     * Returns the user id value.
     *
     * @return  user id value
     */
    public String getUid() {
        return uid;
    }


    /**
     * Sets the username field.
     * 
     * @param   name    value to be set
     */
    public void setUsername(String name) {
        username = name;
    }

    /**
     * Returns the username value.
     *
     * @return  username value
     */
    public String getUsername() {
        return username;
    }

    /**
     * Set the timestamp when the session was created as the number
     * of milliseconds since midnight January 1, 1970 GMT.
     *
     * @param   time    Session initialization date
     */
    public void setCreationTime(long time) {
        initTime = time;
    }

    /**
     * Return the timestamp when the session was created as the number
     * of milliseconds since midnight January 1, 1970 GMT.
     *
     * @return  Session initialization date
     */
    public long getCreationTime() {
        return initTime;
    }

    /**
     * Set the timestamp of the most recent session activity as the number
     * of milliseconds since midnight January 1, 1970 GMT.
     *
     * @param   time    Session activity date
     */
    public void setLastAccessedTime(long time) {
        accessTime = time;
    }

    /**
     * Return the timestamp of the most recent session activity as the number
     * of milliseconds since midnight January 1, 1970 GMT.
     *
     * @return  Session activity date
     */
    public long getLastAccessedTime() {
        return accessTime;
    }

}
