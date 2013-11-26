package com.modeln.build.jenkins;

import java.net.URL;
import java.util.Date;
import java.util.Enumeration;


/**
 * This class represents information about a Jenkins build.
 *
 * @author       Shawn Stafford (sstafford@modeln.com)
 */
public class Build {

    /** Job display name */
    private String displayName = null;

    /** Job URL */
    private URL url = null;

    /** Running status of the build */
    private boolean building = true;

    /** Build date */
    private Date date = null;

    /** Log retention indicator */
    private boolean keepLog = false;



    /**
     * Set the display name of the job.
     *
     * @param   name    Display name
     */
    public void setDisplayName(String name) {
        displayName = name;
    }

    /**
     * Return the display name of the job.
     *
     * @return Job name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Set the build URL.
     *
     * @param   url    Build url
     */
    public void setURL(URL url) {
        this.url = url;
    }

    /**
     * Return the build URL.
     *
     * @return Build url
     */
    public URL getURL() {
        return url;
    }

    /**
     * Set the running status of the build.
     *
     * @param   status    Running build status
     */
    public void setBuilding(boolean status) {
        building = status;
    }

    /**
     * Return the running status of the build.
     *
     * @return Running build status
     */
    public boolean getBuilding() {
        return building;
    }

    /**
     * Set the date of the build.
     *
     * @param   date   Build date
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Return the date of the build.
     *
     * @return  Build date
     */
    public Date getDate() {
        return date;
    }

    /**
     * Set the log retention status of the build.
     *
     * @param   status    Log retention status
     */
    public void setKeepLog(boolean status) {
        keepLog = status;
    }

    /**
     * Return the log retention status of the build.
     *
     * @return Log retention status
     */
    public boolean getKeepLog() {
        return keepLog;
    }


}

