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


/**
 * Information about the SDR that a fix depends on. 
 *
 * @version      1.0
 * @author       Shawn Stafford (sstafford@modeln.com)
 */
public class CMnBaseFixDependency {

    /** List of dependency types */
    public static enum DependencyType {
        MERGE, COMPILE, FUNCTIONAL, TEST
    }


    /** Bug identifier */
    private int bugId;

    /** Type of dependency relationship to this SDR */
    private DependencyType type;




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
     * Set the dependency type. 
     *
     * @param  type   Dependency type
     */
    public void setType(DependencyType type) {
        this.type = type;
    }

    /**
     * Return the dependency type.
     *
     * @return Dependency type
     */
    public DependencyType getType() {
        return type;
    }


    /**
     * Return the list of possible dependency types as an array.
     *
     * @return List of dependency types
     */
    public static String[] getTypeList() {
        DependencyType[] list = DependencyType.values();
        String[] strList = new String[list.length];
        int idx = 0;
        while (idx < list.length) {
            strList[idx] = list[idx].toString();
            idx++;
        }
        return strList;
    }


}

