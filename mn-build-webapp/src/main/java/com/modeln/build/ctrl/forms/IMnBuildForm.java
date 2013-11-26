/* 
* Copyright 2000-2003 by Model N, Inc.  All Rights Reserved. 
* 
* This software is the confidential and proprietary information 
* of Model N, Inc ("Confidential Information").  You shall not 
* disclose such Confidential Information and shall use it only 
* in accordance with the terms of the license agreement you 
* entered into with Model N, Inc. 
*/
package com.modeln.build.ctrl.forms;


/**
 * Specify the form interface for all build input fields. 
 * 
 * @author  Shawn Stafford
 */
public interface IMnBuildForm {

    // ========================================================================
    // The following section contains URL parameter names
    // ========================================================================

    /** Build primary key */
    public static final String BUILD_ID_LABEL = "bid";

    /** Build version string */
    public static final String BUILD_VERSION_LABEL = "ver";

    /** Build changelist parameter label */
    public static final String BUILD_CHANGELIST_LABEL = "cl";

    /** Build environment parameter label */
    public static final String BUILD_ENV_LABEL = "env";


    /** Build changelist search criteria parameter label */
    public static final String BUILD_CHANGELIST_OP_LABEL = "clop";

    /** Build display window parameter label */
    public static final String BUILD_WINDOW_LABEL = "win";



    // ========================================================================
    // The following section contains session attribute names
    // ========================================================================

    /** Build data object */
    public static final String BUILD_OBJECT_LABEL = "buildObj";

    /** Data object containing a list of product areas */
    public static final String PRODUCT_AREA_DATA = "PRODUCT_AREAS";

    /** Data object containing a list of build reviews */
    public static final String AREA_REVIEW_DATA = "AREA_REVIEWS";


}

