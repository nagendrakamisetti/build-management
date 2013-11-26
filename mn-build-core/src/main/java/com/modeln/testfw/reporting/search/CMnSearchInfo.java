/*
* Copyright 2000-2003 by Model N, Inc.  All Rights Reserved.
*
* This software is the confidential and proprietary information
* of Model N, Inc ("Confidential Information").  You shall not
* disclose such Confidential Information and shall use it only
* in accordance with the terms of the license agreement you
* entered into with Model N, Inc.
*/
package com.modeln.testfw.reporting.search;


import java.util.Vector;

/**
 * The search information associated with a user search.
 *
 * @version            $Revision: 1.1 $
 * @author             Shawn Stafford
 *
 */
public class CMnSearchInfo {


    /** Unique identifier for the current search */
    private String search_id;



    /** Title of the search information */
    private String search_title;

    /** Description of the search */
    private String description;

    /** Groups of search criteria that comprise the search */
    private CMnSearchGroup criteriaRoot;


    /** 
     * Construct the search information.
     *
     * @param   title   Name of the search information
     */
    public CMnSearchInfo(String title, String desc, CMnSearchGroup root) {
        search_title = title;
        description = desc;
        criteriaRoot = root;
    }

    /**
     * Sets a unique identifier that can be used to identify the group.
     */
    public void setSystemId(String id) {
        search_id = id;
    }

    /**
     * Returns the unique identifier that can be used to identify the group.
     */
    public String getSystemId() {
        return search_id;
    }

    /**
     * Returns the root search group.
     *
     * @return  Search Group
     */
    public CMnSearchGroup getRoot() {
        return criteriaRoot;
    }

    /**
     * Returns the title of the search.
     *
     * @return  Title
     */
    public String getTitle() {
        return search_title;
    }

    /**
     * Returns the description of the search.
     *
     * @return  Description
     */
    public String getDescription() {
        return description;
    }

}
