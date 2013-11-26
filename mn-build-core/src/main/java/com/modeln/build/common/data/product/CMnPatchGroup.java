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

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;



/**
 * Define a group of service patch fixes.  This is used to group
 * related fixes such as a list of fixes required for an RMI release
 * or a Regulatory fix. 
 *
 * @version      1.0
 * @author       Shawn Stafford (sstafford@modeln.com)
 */
public class CMnPatchGroup implements Comparable {

    /** Group ID */
    private int id;

    /** Group name */
    private String name;

    /** Group description */
    private String description;

    /** Build version string */
    private String buildVersion;

    /** Information about whether the group of fixes needs to be included in a patch */
    private CMnServicePatch.FixRequirement status;

    /** List of fixes associated with the group */
    private Vector<CMnBaseFix> fixes;



    /**
     * Set the ID used to look-up the patch.
     *
     * @param  id   Unique ID of the patch
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Return the ID used to look-up the patch.
     *
     * @return ID for the patch
     */
    public int getId() {
        return id;
    }


    /**
     * Set the group name.
     *
     * @param  text   group title
     */
    public void setName(String text) {
        name = text;
    }

    /**
     * Return the group name.
     *
     * @return group title
     */
    public String getName() {
        return name;
    }

    /**
     * Set the group description. 
     *
     * @param  text   group description 
     */
    public void setDescription(String text) {
        description = text;
    }

    /**
     * Return the group description. 
     *
     * @return group description 
     */
    public String getDescription() {
        return description;
    }



    /**
     * Set the build version string for matching the approval. 
     *
     * @param  version   Build version string 
     */
    public void setBuildVersion(String version) {
        buildVersion = version;
    }

    /**
     * Return the build version. 
     *
     * @return build version 
     */
    public String getBuildVersion() {
        return buildVersion;
    }


    /**
     * Set information about whether the group of fixes is required.
     *
     * @param    status  Group status 
     */
    public void setStatus(String status) {
        if (status != null) {
            this.status = CMnServicePatch.FixRequirement.valueOf(status.toUpperCase());
        } else {
            this.status = null;
        }
    }


    /**
     * Set information about whether the group of fixes is required. 
     *
     * @param  status   Group status 
     */
    public void setStatus(CMnServicePatch.FixRequirement status) {
        this.status = status;
    }


    /**
     * Return information about whether the group of fixes is required. 
     *
     * @return Group status 
     */
    public CMnServicePatch.FixRequirement getStatus() {
        return status;
    }


    /**
     * Set the list of fixes associated with the group.
     *
     * @param  fixes   List of fixes
     */
    public void setFixes(Vector<CMnBaseFix> fixes) {
        this.fixes = fixes;
    }

    /**
     * Return the list of fixes associated with the group.
     *
     * @return  List of fixes
     */
    public Vector<CMnBaseFix> getFixes() {
        return fixes;
    }

    /**
     * Add a new fix to the list associated with the group.
     *
     * @param  fix   New fix
     */
    public void addFix(CMnBaseFix fix) {
        if (fixes == null) {
            fixes = new Vector<CMnBaseFix>();
        }
        fixes.add(fix);
    }

    /**
     * Returns the fix with the matching bug ID, or null if
     * no match is found. 
     *
     * @param   bugId   Bug ID
     * @return  Fix information or null if none found 
     */
    public CMnBaseFix getFix(int bugId) {
        CMnBaseFix fix = null;
        if (fixes != null) {
            Enumeration fixlist = fixes.elements();
            while (fixlist.hasMoreElements()) {
                fix = (CMnBaseFix) fixlist.nextElement();
                if (fix.getBugId() == bugId) {
                    return fix;
                }
            }
        }
        return null;
    }


    /**
     * Returns true if the patch contains a fix with the 
     * same bug ID.
     *
     * @param   bugId   Bug ID
     * @return  TRUE if the list of fixes contains a match
     */
    public boolean hasFix(int bugId) {
        return (getFix(bugId) != null);
    }


    /**
     * Implement the compareTo method of the Comparable interface 
     * to allow object sorting.  Returns a negative integer, zero, 
     * or a positive integer as this object is less than, equal to, 
     * or greater than the specified object.
     * Group equality is determined by evaluating the group status.
     * The least significant (optional) groups are considered
     * "less than" the most significant (required) groups.
     * The following order is used:  OPTIONAL, RECOMMENDED, REQUIRED.
     *
     * @param  group    Patch group
     * @return Negative, zero, or positive integer
     */
    public int compareTo(Object group) {
        CMnPatchGroup patchGroup = (CMnPatchGroup) group;
        if (status == patchGroup.getStatus()) {
            return 0;
        } else if (patchGroup.getStatus() == CMnServicePatch.FixRequirement.REQUIRED) {
            return -1;
        } else if (patchGroup.getStatus() == CMnServicePatch.FixRequirement.OPTIONAL) {
            return 1;
        } else if (patchGroup.getStatus() == CMnServicePatch.FixRequirement.RECOMMENDED) {
            // Determine if this object is greater than or less than RECOMMENDED
            if (status == CMnServicePatch.FixRequirement.OPTIONAL) {
                return -1;
            } else if (status == CMnServicePatch.FixRequirement.REQUIRED) {
                return 1;
            }
        }

        // If none of the criteria match, there is an error
        return -2;
    } 
}

