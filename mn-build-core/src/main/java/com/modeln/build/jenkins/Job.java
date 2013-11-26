package com.modeln.build.jenkins;

import java.net.URL;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

/**
 * This class represents information about a Jenkins job. 
 *
 * @author       Shawn Stafford (sstafford@modeln.com)
 */
public class Job {

    /** Job name */
    private String name = null;;

    /** Job description */
    private String description = null;;

    /** Job display name */
    private String displayName = null;;

    /** Job URL */
    private URL url = null;;

    /** Buildable status of the job */
    private boolean buildable = true;

    /** List of builds */
    private Vector<Build> builds = null;

    /** Most recent build */
    private Build last = null;

    /** Most recent successful build */
    private Build lastSuccess = null;

    /** Most recent completed build */
    private Build lastComplete = null;

    /** Most recent failed build */
    private Build lastFailure = null;

    /** Most recent stable build */
    private Build lastStable = null;


    /**
     * Set the name of the job.
     *
     * @param   name    Job name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Return the name of the job.
     *
     * @return Job name
     */
    public String getName() {
        return name;
    }

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
     * Set the job description. 
     *
     * @param   desc    Job description
     */
    public void setDescription(String desc) {
        description = desc;
    }

    /**
     * Return the job description. 
     *
     * @return Job description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the job URL. 
     *
     * @param   url    Job url
     */
    public void setURL(URL url) {
        this.url = url;
    }

    /**
     * Return the job URL. 
     *
     * @return Job url
     */
    public URL getURL() {
        return url;
    }

    /**
     * Set the buildable status of the job.
     *
     * @param   status    Buildable status 
     */
    public void setBuildable(boolean status) {
        buildable = status;
    }

    /**
     * Return the buildable status of the job. 
     *
     * @return Buildable status
     */
    public boolean getBuildable() {
        return buildable;
    }


    /**
     * Add a build to the list.
     *
     * @param   build   New build to add to the list
     */
    public void addBuild(Build build) {
        if (build != null) {
            if (builds == null) {
                builds = new Vector<Build>();
            }
            builds.add(build);
        }
    }

    /**
     * Set the list of builds.
     *
     * @param   list   List of builds
     */
    public void setBuilds(Enumeration<Build> list) {
        while ((list != null) && list.hasMoreElements()) {
            addBuild(list.nextElement());
        }
    }

    /**
     * Returns the list of builds for this job.
     *
     * @return List of builds
     */
    public Enumeration<Build> getBuilds() {
        if (builds != null) {
            return builds.elements();
        } else {
            return null;
        }
    }
}

