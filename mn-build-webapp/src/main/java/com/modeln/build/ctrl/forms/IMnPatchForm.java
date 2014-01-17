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
 * Specify the form interface for all service patch input fields. 
 * 
 * @author  Shawn Stafford
 */
public interface IMnPatchForm {

    // ========================================================================
    // The following section contains request attribute names
    // ========================================================================

    /** Service patch data object */
    public static final String PATCH_DATA = "PATCH";

    /** Jenkins job data */
    public static final String JOB_DATA = "JOB";

    /** List of service patch data objects */
    public static final String PATCH_LIST_DATA = "PATCH_LIST";

    /** Customer data object */
    public static final String CUSTOMER_DATA = "CUSTOMER_DATA";

    /** Customer environment data object */
    public static final String ENV_DATA = "ENV_DATA";

    /** Build list data */
    public static final String BUILD_LIST_DATA = "BUILD_LIST";

    /** Build metric data */
    public static final String BUILD_METRIC_DATA = "BUILD_METRICS";

    /** List of products */
    public static final String PRODUCT_LIST_DATA = "PRODUCT_LIST";

    /** List of releases */
    public static final String RELEASE_LIST_DATA = "RELEASE_LIST";

    /** List of customers */
    public static final String CUSTOMER_LIST_DATA = "CUSTOMER_LIST";

    /** List of bug fixes */
    public static final String FIX_LIST_DATA = "FIX_LIST";

    /** List of patch fix groups */
    public static final String FIX_GROUP_DATA = "FIX_GROUP";

    /** List of patch fix groups */
    public static final String FIX_GROUP_LIST_DATA = "FIX_GROUP_LIST";


    /** List of patch approval entries */
    public static final String APPROVAL_LIST_DATA = "APPROVAL_LIST";

    /** List of users that can provide approval for this patch */
    public static final String APPROVER_LIST_DATA = "APPROVER_LIST";

    /** List of groups that are required for patch approval */
    public static final String APPROVER_GROUP_DATA = "APPROVER_GROUPS";


    // ========================================================================
    // The following section contains URL parameter names
    // ========================================================================

    /** Service patch ID number */
    public static final String PATCH_ID_LABEL = "pid";

    /** Identifies the unique ID for a patch fix group */
    public static final String GROUP_ID_LABEL = "gid";

    /** Boolean to indicate if the patch is available for customer use */
    public static final String PATCH_USE_LABEL = "pub";

    /** Customer primary key */
    public static final String CUSTOMER_ID_LABEL = "cust";

    /** Customer name */
    public static final String CUSTOMER_NAME_LABEL = "cname";

    /** Short customer name */
    public static final String CUSTOMER_SHORT_NAME_LABEL = "cshname";

    /** Customer branch type */
    public static final String CUSTOMER_BRANCH_TYPE_LABEL = "cbtype";

    /** Customer environment id */
    public static final String ENV_ID_LABEL = "env";

    /** Environment name */
    public static final String ENV_NAME_LABEL = "ename";

    /** Environment short name */
    public static final String ENV_SHORT_NAME_LABEL = "eshort";

    /** Product identifier */
    public static final String PRODUCT_ID_LABEL = "prodid";

    /** Release identifier */
    public static final String RELEASE_ID_LABEL = "rid";

    /** User ID of the patch requestor */
    public static final String PATCH_USER_LABEL = "puid";

    /** Build identifier */
    public static final String BUILD_ID_LABEL = "bid";

    /** Build report identifier */
    public static final String PATCH_BUILD_LABEL = "pbid";

    /** Build version string */
    public static final String BUILD_VERSION_LABEL = "ver";

    /** Service patch name (i.e. SP1) */
    public static final String PATCH_NAME_LABEL = "pname";

    /** Service patch status (used when updating the status) */
    public static final String PATCH_STATUS_LABEL = "pstatus";

    /** Service patch job type (i.e. short build) */
    public static final String JOB_TYPE_LABEL = "jtype";


    /** List of e-mail addresses to include in notifications */
    public static final String NOTIFY_LABEL = "cc";

    /** Business justification identifier */
    public static final String JUSTIFY_LABEL = "why";

    /** Optional parts of the build to perform */
    public static final String BUILD_OPTIONS_LABEL = "options";

    /** Patch ID of the service patch that the current request will build upon */
    public static final String BASE_PATCH_LABEL = "basepid";

    /** List of bug numbers included in the patch */
    public static final String FIX_LIST = "fixes";

    /** Comma-delimited list of bug numbers to be included in the patch */
    public static final String BULK_FIX_LIST = "bulkfixes";

    /** Source control location of the bulk fixes */
    public static final String BULK_FIX_BRANCH = "bulksrc";

    /** Patch approval status selection */
    public static final String APPROVAL_STATUS = "apprstatus";

    /** Patch approval comment */
    public static final String APPROVAL_COMMENT = "apprnote";

    /** Patch export format */
    public static final String EXPORT_FORMAT_LABEL = "fmt";

    /** Fix group status (optional, recommended, required) */
    public static final String FIX_GROUP_STATUS_LABEL = "grpstatus";

    /** Fix group name */
    public static final String FIX_GROUP_NAME_LABEL = "grpname";

    /** Fix group description */
    public static final String FIX_GROUP_DESC_LABEL = "grpdesc";

    /** Fix group bug list */
    public static final String FIX_GROUP_BUGS_LABEL = "grpbugs";

    /** Individual bug ID */
    public static final String FIX_BUG_LABEL = "bug";



    // ========================================================================
    // The following section contains URL parameter name prefixes
    // These prefixes are used for dynamically generated form input fields
    // ========================================================================

    /** List of changelists to exclude from the fix */
    public static final String FIX_EXCLUDE_PREFIX = "ex";

    /** Branch associated with the fix */
    public static final String FIX_BRANCH_PREFIX = "br";

    /** Note associated with the fix */
    public static final String FIX_NOTE_PREFIX = "note";

    /** Origin associated with the fix */
    public static final String FIX_ORIGIN_PREFIX = "or";


    // ========================================================================
    // The following section contains button names
    // ========================================================================

    /** Submit new patch request button */
    public static final String PATCH_REQUEST_BUTTON = "prequest"; 

    /** Submit patch fixes */
    public static final String PATCH_FIXES_BUTTON = "pfixes";

    /** Kick off the job execution */
    public static final String PATCH_JOB_BUTTON = "pjob";

    /** Submit customer data information */
    public static final String CUSTOMER_DATA_BUTTON = "custinfo";

    /** Submit customer environment information */
    public static final String CUSTOMER_ENV_BUTTON = "custenv";

    /** Submit patch approval information */
    public static final String PATCH_APPROVAL_BUTTON = "papprove";

    /** Submit patch comment information */
    public static final String PATCH_COMMENT_BUTTON = "pcomment";


    // ========================================================================
    // The following section contains button action values
    // ========================================================================
    
    /** User has requested that the current data be added to the database */
    public static final String ACTION_ADD = "Add";

    /** User has requested that the action be cancelled */
    public static final String ACTION_CANCEL = "Cancel";

    /** User has requested to proceed to the next action */
    public static final String ACTION_CONTINUE = "Continue";

    /** User has requested that the current data be deleted */
    public static final String ACTION_DELETE = "Delete";

    /** User has requested that the current data be edited */
    public static final String ACTION_EDIT = "Edit";

    // ========================================================================
    // The following section contains data export formats 
    // ========================================================================

    /** Export data in a spreadsheet format such as comma or tab delimited */
    public static final String EXPORT_CSV = "csv";

    /** Export data in an executable format such as an shell script */
    public static final String EXPORT_EXE = "exe";


}

