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

import com.modeln.build.common.enums.CMnServicePatch;
import com.modeln.build.sourcecontrol.CMnCheckIn;

import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;


/**
 * Information about a product fix. 
 *
 * @version      1.0
 * @author       Shawn Stafford (sstafford@modeln.com)
 */
public class CMnBaseFix {

    /** Group ID to associate the fix with other fixes */
    private int gid;

    /** Bug identifier */
    private int bugId;

    /** Human readable name (if different from bug ID) */
    private String bugName;

    /** Date when the fix was added to the containing service patch */
    private Date requestDate;

    /** List of changelists */
    private Vector<CMnCheckIn> changelists = new Vector<CMnCheckIn>();

    /** List of changelists to exclude from the list */
    private Vector<CMnCheckIn> exclusions = new Vector<CMnCheckIn>();

    /** List of bugs on which this one depends */
    private Vector<CMnBaseFixDependency> dependencies = new Vector<CMnBaseFixDependency>();

    /** Product release where this fix has been applied */
    private String release;

    /** Status of the fix */
    private String status;

    /** Severity of the fix */
    private CMnServicePatch.FixSeverity severity;

    /** Type of fix (defect, enhancement, etc) */
    private String fixType;

    /** Sub-type of fix (data, performance, etc) */
    private String fixSubType;

    /** Product area of the fix */
    private String productArea;

    /** Type of version control system used */
    private String versionControlType;

    /** Root version control location where the build was obtained from */
    private String versionControlRoot;

    /** Description of the fix, such as the subject of the bug */
    private String description;

    /** Notes about the patch */
    private String notes;


    /**
     * Set the group ID used to group the fix with other fixes. 
     *
     * @param  id   Group ID 
     */
    public void setGroupId(int id) {
        gid = id;
    }

    /**
     * Return the group ID used to look-up the patch.
     *
     * @return Group ID for the patch
     */
    public int getGroupId() {
        return gid;
    }



    /**
     * Set the bug ID for the fix. 
     *
     * @param  id  Bug ID 
     */
    public void setBugId(int id) {
        bugId = id;
    }

    /**
     * Return the Bug ID.
     *
     * @return Bug ID 
     */
    public int getBugId() {
        return bugId;
    }

    /**
     * Set the date when the fix was added to the containing patch request. 
     *
     * @param    date   Request date
     */
    public void setDate(Date date) {
        requestDate = date;
    }

    /**
     * Return the date when the fix was added to the containing patch request. 
     *
     * @return  Request date 
     */
    public Date getDate() {
        return requestDate;
    }


    /**
     * Set the bug name for the fix. 
     *
     * @param  name  Bug name
     */
    public void setBugName(String name) {
        bugName = name;
    }

    /**
     * Return the bug name.
     *
     * @return Bug name
     */
    public String getBugName() {
        return bugName;
    }


    /**
     * Set the list of check-ins.
     *
     * @param   list  Check-ins
     */
    public void setChangelists(Vector<CMnCheckIn> list) {
        changelists = list;
    }

    /**
     * Add a check-in to the list.
     *
     * @param   ci    Check-in information 
     */
    public void addChangelist(CMnCheckIn ci) {
        changelists.add(ci);
    }

    /**
     * Return the list of changlists.
     *
     * @return changelists
     */
    public Vector<CMnCheckIn> getChangelists() {
        return changelists;
    }

    /**
     * Add a changelist to be excluded from the fix. 
     *
     * @param   ci    Check-in information 
     */
    public void addExclusion(CMnCheckIn ci) {
        exclusions.add(ci);
    }


    /**
     * Set the list of exclusions based on the comma-delimited list of values.
     *
     * @param   list  Comma-delimited list of changelists
     */
    public void setExclusions(String list) {
        exclusions = new Vector();
        if ((list != null) && (list.length() > 0)) {
            StringTokenizer st = new StringTokenizer(list, ",");
            while (st.hasMoreTokens()) {
                String cl = (String) st.nextToken();
                if ((cl != null) && (cl.trim().length() > 0)) {
                    CMnCheckIn ci = new CMnCheckIn();
                    ci.setId(cl);
                    exclusions.add(ci);
                }
            }
        }
    }


    /**
     * Return the number of changelists excluded from the fix.
     *
     * @return Number of exclusions
     */
    public int getExclusionCount() {
        return exclusions.size();
    }

    /**
     * Return the list of excluded changlists.
     *
     * @return changelists
     */
    public Vector<CMnCheckIn> getExclusions() {
        return exclusions;
    }

    /**
     * Get the list of exclusions as a comma-delimited list.
     *
     * @return Comma-delimited list of excluded change lists
     */
    public String getExclusionsAsString() {
        CMnCheckIn ci = null;
        StringBuffer list = new StringBuffer();
        Enumeration<CMnCheckIn> e = exclusions.elements();
        while (e.hasMoreElements()) {
            ci = (CMnCheckIn) e.nextElement();
            if (ci.getId() != null) {
                list.append(ci.getId());
                if (e.hasMoreElements()) {
                    list.append(",");
                }
            }
        }
        return list.toString();
    }


    /**
     * Set the list of dependencies. 
     *
     * @param   list  Dependencies 
     */
    public void setDependencies(Vector<CMnBaseFixDependency> list) {
        dependencies = list;
    }

    /**
     * Add a dependency to the list if it does not already exist.
     *
     * @param   dep    Dependency information
     */
    public void addDependency(CMnBaseFixDependency dep) {
        CMnBaseFixDependency existing = getDependency(dep.getBugId());
        if (existing == null) { 
            if (dependencies == null) {
                dependencies = new Vector<CMnBaseFixDependency>();
            }
            dependencies.add(dep);
        }
    }

    /**
     * Return the list of dependencies.
     *
     * @return Dependency list
     */
    public Vector<CMnBaseFixDependency> getDependencies() {
        return dependencies;
    }


    /**
     * Return the matching dependency or null if none found.
     *
     * @param   bid   Bug ID
     * @return Dependency
     */
    public CMnBaseFixDependency getDependency(int id) {
        if (dependencies != null) {
            Enumeration list = dependencies.elements();
            while (list.hasMoreElements()) {
                CMnBaseFixDependency current = (CMnBaseFixDependency) list.nextElement();
                if (current.getBugId() == id) {
                    return current;
                }
            }
        }
        return null;
    }

    /**
     * Set the release where the fix was applied. 
     *
     * @param  name  Release name or number 
     */
    public void setRelease(String name) {
        release = name;
    }

    /**
     * Return the release where the fix was applied. 
     *
     * @return Release name or number
     */
    public String getRelease() {
        return release;
    }

    /**
     * Set the status of the fix 
     *
     * @param  state   Fix status 
     */
    public void setStatus(String state) {
        status = state;
    }

    /**
     * Return fix status 
     *
     * @return Fix status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Set the severity of the fix.
     *
     * @param  severity   Fix severity
     */
    public void setSeverity(CMnServicePatch.FixSeverity severity) {
        this.severity = severity;
    }

    /**
     * Return the severity of the fix.
     *
     * @return Fix severity
     */
    public CMnServicePatch.FixSeverity getSeverity() {
        return severity;
    }

    /**
     * Set the type of fix 
     *
     * @param  type   Fix type 
     */
    public void setType(String type) {
        fixType = type;
    }

    /**
     * Return fix type 
     *
     * @return Fix type
     */
    public String getType() {
        return fixType;
    }


    /**
     * Set the sub-type of fix 
     *
     * @param  subtype   Fix subtype 
     */
    public void setSubType(String subtype) {
        fixSubType = subtype;
    }

    /**
     * Return fix sub-type 
     *
     * @return Fix subtype
     */
    public String getSubType() {
        return fixSubType;
    }

    /**
     * Set the product area
     *
     * @param  area     Product area
     */
    public void setProductArea(String area) {
        productArea = area;
    }

    /**
     * Return the product area
     *
     * @return Product area
     */
    public String getProductArea() {
        return productArea;
    }


    /**
     * Set the version control type that is used to interpret the version
     * control values.
     *
     * @param  type   Version control type
     */
    public void setVersionControlType(String type) {
        versionControlType = type;
    }

    /**
     * Return the version control type.
     *
     * @return Version control type
     */
    public String getVersionControlType() {
        return versionControlType;
    }


    /**
     * Set the version control root that is used to identify the location from
     * which the source code was obtained for the build.  In perforce, the
     * version control root refers to the depot.  In CVS, this might refer to
     * a respository directory.
     *
     * @param  root   Version control root
     */
    public void setVersionControlRoot(String root) {
        versionControlRoot = root;
    }

    /**
     * Return a root location that can be used to locate and obtain the source
     * code from the version control system.
     *
     * @return Version control root
     */
    public String getVersionControlRoot() {
        return versionControlRoot;
    }

    /**
     * Set description of the patch fix.
     *
     * @param  text   Description of the fix
     */
    public void setDescription(String text) {
        description = text;
    }

    /**
     * Return description of the patch fix.
     *
     * @return Description of the fix 
     */
    public String getDescription() {
        return description;
    }


    /**
     * Set notes regarding the patch fix. 
     *
     * @param  text   Notes about the fix 
     */
    public void setNotes(String text) {
        notes = text;
    }

    /**
     * Return notes about the patch fix. 
     *
     * @return Notes 
     */
    public String getNotes() {
        return notes;
    }


}

