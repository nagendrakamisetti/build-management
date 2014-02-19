/*
* Copyright 2000-2003 by Model N, Inc.  All Rights Reserved.
*
* This software is the confidential and proprietary information
* of Model N, Inc ("Confidential Information").  You shall not
* disclose such Confidential Information and shall use it only
* in accordance with the terms of the license agreement you
* entered into with Model N, Inc.
*/
package com.modeln.build.ctrl.database;

import com.modeln.build.common.data.account.CMnAccount;
import com.modeln.build.common.data.account.CMnEnvironment;
import com.modeln.build.common.data.product.*;
import com.modeln.build.common.database.CMnCustomerTable;
import com.modeln.build.common.enums.CMnServicePatch;
import com.modeln.testfw.reporting.CMnBuildTable;
import com.modeln.testfw.reporting.CMnDbBuildData;
import com.modeln.testfw.reporting.CMnTable;
import com.modeln.testfw.reporting.search.CMnSearchCriteria;
import com.modeln.testfw.reporting.search.CMnSearchGroup;
import com.modeln.build.common.data.account.GroupData;
import com.modeln.build.common.data.account.UserData;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;


/**
 * The service patch table interface defines all of the table and column names
 * used to represent customer service patches and the approval process.
 *
 * @author  Shawn Stafford
 */
public class CMnPatchTable extends CMnTable {


    /** Name of the table used to represent a service patch request */
    public static final String REQUEST_TABLE = "patch_request";

    /** Name of the column that identifies the request by ID */
    public static final String REQUEST_ID = "patch_id";

    /** Name of the column that indicates whether the patch is for internal use only */
    public static final String REQUEST_INTERNAL = "internal_only";

    /** Name of the column that identifies the request by name (i.e. SP1) */
    public static final String PATCH_NAME = "patch_name";

    /** Name of the column that identifies the request date */
    public static final String REQUEST_DATE = "request_date";

    /** Name of the column that identifies the request status */
    public static final String REQUEST_STATUS = "status";

    /** Name of the column that identifies the requesting customer by ID */
    public static final String ACCOUNT_ID = "account_id";

    /** Name of the column that identifies the requesting user by ID */
    public static final String USER_ID = "user_id";

    /** Name of the column that identifies the target environment by ID */
    public static final String ENVIRONMENT_ID = "env_id";

    /** Name of the column that identifies the build used as the basis for the patch */
    public static final String BUILD_ID = "build_id";

    /** Name of the column that identifies the service patch build associated with the patch request */
    public static final String PATCH_BUILD = "patch_build";

    /** Name of the column that identifies the previous service patch */
    public static final String PREVIOUS_PATCH_ID = "previous_patch";

    /** Name of the column that identifies a list of service patch options */
    public static final String PATCH_OPTIONS = "patch_options";

    /** Name of the column that identifies a list of e-mail recipients for patch notification */
    public static final String PATCH_NOTIFICATION = "notification";

    /** Name of the column that identifies justfication comments for the patch  */
    public static final String PATCH_JUSTIFICATION = "justification";


    /** Name of the table used to record patch status changes */
    private static final String PATCH_STATUS_HISTORY_TABLE = "patch_status_history";

    /** Name of the column that identifies the date of a status change */
    private static final String PATCH_STATUS_DATE = "change_date";

    /** Name of the column that identifies the previous status */
    private static final String PATCH_STATUS_OLD = "old_status";

    /** Name of the column that identifies the new status */
    private static final String PATCH_STATUS_NEW = "new_status";



    /** Name of the table used to represent a service patch fix */
    public static final String FIX_TABLE = "patch_fixes";

    /** Name of the column that identifies the original patch containing this fix */
    public static final String FIX_ORIGIN = "origin";

    /** Name of the column that identifies the bug fix by ID */
    public static final String FIX_BUG_ID = "bug_id";

    /** Name of the column that identifies when the fix was added to the request */
    public static final String FIX_REQUEST_DATE = "request_date";

    /** Name of the column that identifies the source control location of the fix */
    public static final String FIX_BRANCH = "version_ctrl_root";

    /** Name of the column that identifies any comments associated with the fix */
    public static final String FIX_NOTES = "notes";

    /** Name of the column that identifies any changelists that should be excluded from the fix */
    public static final String FIX_EXCLUSIONS = "exclusions";

    
    /** Name of the table used to represent a group of fixes */
    public static final String FIX_GROUP_TABLE = "patch_group";

    /** Name of the column that identifies the fix group ID */
    public static final String FIX_GROUP_ID = "group_id";

    /** Name of the column that identifies the group name */
    public static final String FIX_GROUP_NAME = "group_name";

    /** Name of the column that identifies the group description */
    public static final String FIX_GROUP_DESCRIPTION = "group_desc";

    /** Name of the column that identifies the group fix status */
    public static final String FIX_GROUP_STATUS = "status";


    /** Name of the table used to represent a member of a fix group */
    public static final String FIX_GROUP_ITEM_TABLE = "patch_group_fixes";



    /** Name of the table used to represent the patch approval table */
    public static final String APPROVAL_TABLE = "patch_approvals";

    /** Name of the column that identifies the approval request status */
    public static final String APPROVAL_STATUS = "status";

    /** Name of the column that identifies the patch status when approved */
    public static final String APPROVAL_PATCH_STATUS = "patch_status";

    /** Name of the column that identifies the approval comment */
    public static final String APPROVAL_COMMENT = "comment";

    /** Name of the column that identifies the approval user ID */
    public static final String APPROVAL_USER_ID = "user_id";


    /** Name of the table used to represent the patch approvers table */
    public static final String APPROVERS_TABLE = "patch_approvers";

    /** Name of the column that identifies the approver by ID */
    public static final String APPROVER_ID = "approver_id";

    /** Name of the column that identifies the approver's build version */
    public static final String BUILD_VERSION = "build_version";


    /** Name of the column used to represent the patch approval status */
    public static final String STATUS = "status";
 
    /** Name of the column for the comment */
    public static final String COMMENT = "comment";

    /** Name of the column for the group_id */
    public static final String GROUP_ID = "group_id";

    /** Name of the column used to represent the group name */
    public static final String GROUP_NAME = "group_name";

    /** Name of the column for the group description */
    public static final String GROUP_DESC = "group_desc";


    /** Name of the table used to record notification rules */
    public static final String NOTIFICATION_TABLE = "patch_notification";

    /** Name of the column for the auto-generated notification ID */
    public static final String NOTIFICATION_ID = "notification_id";

    /** Name of the column for identifying the user requesting notification */
    public static final String NOTIFICATION_USER_ID = "user_id";



    /** Name of the table used to represent the groups */
    public static final String ASSIGNMENT_TABLE = "patch_assignment";

    /** Name of the column that identifies the user responsible for completing work on the patch */
    public static final String ASSIGNMENT_USER_ID = "user_id";

    /** Name of the column that identifies the requesting user by ID */
    public static final String ASSIGNMENT_PATCH_ID = "patch_id";

    /** Name of the column for the date when the user started working */
    public static final String ASSIGNMENT_START_DATE = "start_date";

    /** Name of the column for the date when the user started working */
    public static final String ASSIGNMENT_END_DATE = "end_date";

    /** Name of the column for the date when the patch must be completed */
    public static final String ASSIGNMENT_DEADLINE = "deadline";

    /** Name of the column for the date when the user started working */
    public static final String ASSIGNMENT_COMMENTS = "comments";

    /** Name of the column that defines the patch priority */
    public static final String ASSIGNMENT_PRIORITY = "priority";


    /** Name of the table used to represent the patch comments */
    public static final String COMMENT_TABLE = "patch_comments";

    /** Name of the column that identifies the user responsible for submitting the comment */
    public static final String COMMENT_USER_ID = "user_id";

    /** Name of the column that identifies the date of the comment */
    public static final String COMMENT_DATE = "save_date";

    /** Name of the column that indicates who the comment should be displayed to */
    public static final String COMMENT_STATUS = "status";

    /** Name of the column that contains the comment text */
    public static final String COMMENT_TEXT = "comment";



    /** Name of the table used to represent patch fix dependencies */
    public static final String DEPENDENCY_TABLE = "patch_fix_dependency";

    /** Name of the column that identifies the bug that a fix depends on */
    public static final String DEPENDENCY_ON = "depends_on";

    /** Name of the column that identifies the type of dependency */
    public static final String DEPENDENCY_TYPE = "dependency_type";






    /** Singleton instance of the table class */
    private static CMnPatchTable instance;

    /**
     * Return the singleton instance of the class.
     */
    public static CMnPatchTable getInstance() {
        if (instance == null) {
            instance = new CMnPatchTable();

            // Enable debbuging to a file
            String logfile = "/var/tmp/CMnPatchTable.txt";
            try {
                instance.setDebugOutput(new PrintStream(logfile));
                instance.debugEnable(true);
            } catch (FileNotFoundException nfex) {
                System.out.println("Unable to enable debugging.  Failed to open log file: " + logfile);
            }

        }
        return instance;
    }



    /**
     * Retrieve the service patch information associated with the given ID.
     *
     * @param   conn      Database connection
     * @param   patchId   Service patch ID
     * @param   deep      Determines whether to perform querries to fill in related data
     *
     * @return  Service patch information 
     */
    public synchronized CMnPatch getRequest(Connection conn, String patchId, boolean deep)
        throws SQLException
    {
        CMnPatch patch = null; 

        StringBuffer sql = new StringBuffer();
        sql.append("SELECT * FROM " + REQUEST_TABLE + ", " +
                                      CMnBuildTable.BUILD_TABLE + ", " +
                                      CMnCustomerTable.ACCOUNT_TABLE + ", " +
                                      CMnCustomerTable.ENV_TABLE +
                           " WHERE " + REQUEST_TABLE + "." + ACCOUNT_ID + " = " + CMnCustomerTable.ACCOUNT_TABLE + "." + CMnCustomerTable.ACCOUNT_ID +
                             " AND " + REQUEST_TABLE + "." + ENVIRONMENT_ID + " = " + CMnCustomerTable.ENV_TABLE + "." + CMnCustomerTable.ENV_ID +
                             " AND " + REQUEST_TABLE + "." + BUILD_ID + " = " + CMnBuildTable.BUILD_TABLE + "." + CMnBuildTable.BUILD_ID + 
                             " AND " + REQUEST_TABLE + "." + REQUEST_ID + " = '" + patchId + "'");

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            getInstance().debugWrite("Attempting to execute: " + sql.toString());
            rs = executeQuery(st, "getRequest", sql.toString());
            if ((rs != null) && rs.first()) {
                patch = parseRequestData(rs);
                if (deep && (patch != null)) {
                    // Load details about the previous patch
                    if (patch.getPreviousPatch() != null) {
                        Integer previousPatchId = patch.getPreviousPatch().getId();
                        if ((previousPatchId != null) && (previousPatchId > 0)) {
                            CMnPatch previousPatch = getRequest(conn, previousPatchId.toString(), /**deep=*/false);
                            patch.setPreviousPatch(previousPatch);
                        }
                    }

                    // Load the e-mail notifications for the patch
                    Vector<CMnPatchNotification> notifications = getNotifications(conn, patch);
                    patch.setNotifications(notifications);

                    // Load the list of fixes for the current patch
                    Vector<CMnPatchFix> fixes = getFixes(conn, patchId, true);
                    patch.setFixes(fixes);

                    // Load the list of comments
                    Vector<CMnPatchComment> comments = getComments(conn, patchId);
                    getInstance().debugWrite("Setting comment list for patch ID " + patchId);
                    patch.setCommentList(comments);
                }
            } else {
                 getInstance().debugWrite("Unable to obtain the service patch data.");
            }
        } catch (SQLException ex) {
            getInstance().debugWrite("Failed to obtain service patch data: " + ex.toString());
            //getInstance().debugWrite(ex);
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return patch;
    }



    /**
     * Retrieve a list of all service patch requests from the database.
     *
     * @param   conn    Database connection
     * @param   deep      Determines whether to perform querries to fill in related data
     *
     * @return  List of patch request objects
     */
    public synchronized Vector<CMnPatch> getAllRequests(Connection conn, boolean deep)
        throws SQLException
    {
        Vector<CMnPatch> list = new Vector<CMnPatch>();

        StringBuffer sql = new StringBuffer();
        sql.append("SELECT * FROM " + REQUEST_TABLE + ", " + 
                                      CMnBuildTable.BUILD_TABLE + ", " + 
                                      CMnCustomerTable.ACCOUNT_TABLE + ", " + 
                                      CMnCustomerTable.ENV_TABLE +
                           " WHERE " + REQUEST_TABLE + "." + ACCOUNT_ID + " = " + CMnCustomerTable.ACCOUNT_TABLE + "." + CMnCustomerTable.ACCOUNT_ID +
                             " AND " + REQUEST_TABLE + "." + ENVIRONMENT_ID + " = " + CMnCustomerTable.ENV_TABLE + "." + CMnCustomerTable.ENV_ID +
                             " AND " + REQUEST_TABLE + "." + BUILD_ID + " = " + CMnBuildTable.BUILD_TABLE + "." + CMnBuildTable.BUILD_ID);

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            getInstance().debugWrite("Attempting to execute: " + sql.toString());
            rs = executeQuery(st, "getAllRequests", sql.toString());
            if (rs != null) {
                CMnPatch patch = null;
                while (rs.next()) {
                    patch = parseRequestData(rs);
                    if (deep && (patch != null)) {
                        // Load the list of fixes for the current patch
                        Vector<CMnPatchFix> fixes = getFixes(conn, patch.getId().toString(), false);
                        patch.setFixes(fixes);
                    }
                    list.add(patch);
                }
            } else {
                 getInstance().debugWrite("Unable to obtain the service patch data.");
            }
        } catch (SQLException ex) {
            getInstance().debugWrite("Failed to obtain service patch data: " + sql.toString());
            getInstance().debugWrite(ex);
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return list;
    }




    /**
     * Retrieve a list of all patches from the database, limiting the result
     * set to a specified number or rows.  If the limit is set to zero, all
     * matches will be returned.
     *
     * @param   conn      Database connection
     * @param   criteria  Set of limiting criteria
     * @param   count     Number of rows to return, or zero for all rows
     * @param   deep      Determines whether to perform querries to fill in related data
     * @return  List of patch information objects
     */
    public synchronized Vector<CMnPatch> getAllRequests(
            Connection conn,
            CMnSearchGroup criteria,
            int count,
            boolean deep)
        throws SQLException
    {
        Vector<CMnPatch> list = new Vector<CMnPatch>();

        StringBuffer sql = new StringBuffer();
        sql.append("SELECT * FROM " + REQUEST_TABLE + ", " +
                                      CMnBuildTable.BUILD_TABLE + ", " +
                                      CMnCustomerTable.ACCOUNT_TABLE + ", " +
                                      CMnCustomerTable.ENV_TABLE +
                           " WHERE " + REQUEST_TABLE + "." + ACCOUNT_ID + " = " + CMnCustomerTable.ACCOUNT_TABLE + "." + CMnCustomerTable.ACCOUNT_ID +
                             " AND " + REQUEST_TABLE + "." + ENVIRONMENT_ID + " = " + CMnCustomerTable.ENV_TABLE + "." + CMnCustomerTable.ENV_ID +
                             " AND " + REQUEST_TABLE + "." + BUILD_ID + " = " + CMnBuildTable.BUILD_TABLE + "." + CMnBuildTable.BUILD_ID);

        // Make sure we actually have some criteria to add
        if (criteria != null) {
            String crStr = criteria.toSql();
            if ((crStr != null) && (crStr.length() > 0)) {
                sql.append(" AND " + criteria.toSql());
            }
        }
        sql.append(" ORDER BY " + REQUEST_TABLE + "." + REQUEST_DATE + " DESC");
        if (count > 0) {
            sql.append(" LIMIT " + count);
        }

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            getInstance().debugWrite("getAllRequests: Attempting to execute: " + sql.toString());
            rs = executeQuery(st, "getAllRequests", sql.toString());
            if (rs != null) {
                CMnPatch patch = null;
                while (rs.next()) {
                    patch = parseRequestData(rs);
                    if (deep && (patch != null)) {
                        // Load the list of fixes for the current patch
                        Vector<CMnPatchFix> fixes = getFixes(conn, patch.getId().toString(), false);
                        patch.setFixes(fixes);
                    }
                    list.add(patch);
                }
            } else {
                 getInstance().debugWrite("Unable to obtain the service patch data.");
            }
        } catch (SQLException ex) {
            getInstance().debugWrite("getAllRequests: Failed to obtain service patch data: " + ex.toString());
            getInstance().debugWrite(ex);
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return list;
    }



    /**
     * Retrieve a list of all patches from the database, limiting the result
     * set to a specified number or rows.  If the limit is set to zero, all
     * matches will be returned.
     *
     * @param   conn      Database connection
     * @param   fixes     List of fixes
     * @param   criteria  Set of limiting criteria
     * @param   count     Number of rows to return, or zero for all rows
     * @param   deep      Determines whether to perform querries to fill in related data
     * @return  List of patch information objects
     */
    public synchronized Vector<CMnPatch> getAllRequestsByFix(
            Connection conn,
            Collection<CMnPatchFix> fixes,
            CMnSearchGroup criteria,
            int count,
            boolean deep)
        throws SQLException
    {
        Vector<CMnPatch> list = new Vector<CMnPatch>();

        // Construct the inner select query from the list of fixes
        StringBuffer inner = new StringBuffer();
        if (fixes != null) {
            inner.append("SELECT DISTINCT " + REQUEST_TABLE + "." + REQUEST_ID);
            inner.append(" FROM " + REQUEST_TABLE + ", " + FIX_TABLE);
            inner.append(" WHERE " + REQUEST_TABLE + "." + REQUEST_ID + " = " + FIX_TABLE + "." + REQUEST_ID);
            inner.append(" AND " + FIX_TABLE + "." + FIX_BUG_ID + " IN ("); 
            Iterator iter = fixes.iterator();
            while (iter.hasNext()) {
                String currentFix = (String) iter.next();
                inner.append("'" + currentFix + "'");
                if (iter.hasNext()) {
                    inner.append(", ");
                }
            }
            inner.append(")");
        }


        // Construct the outer select query
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT * FROM " + REQUEST_TABLE + ", " +
                                      CMnBuildTable.BUILD_TABLE + ", " +
                                      CMnCustomerTable.ACCOUNT_TABLE + ", " +
                                      CMnCustomerTable.ENV_TABLE +
                           " WHERE " + REQUEST_TABLE + "." + ACCOUNT_ID + " = " + CMnCustomerTable.ACCOUNT_TABLE + "." + CMnCustomerTable.ACCOUNT_ID +
                             " AND " + REQUEST_TABLE + "." + ENVIRONMENT_ID + " = " + CMnCustomerTable.ENV_TABLE + "." + CMnCustomerTable.ENV_ID +
                             " AND " + REQUEST_TABLE + "." + BUILD_ID + " = " + CMnBuildTable.BUILD_TABLE + "." + CMnBuildTable.BUILD_ID +
                             " AND " + REQUEST_TABLE + "." + REQUEST_ID + " IN (" + inner + ")");

        // Make sure we actually have some criteria to add
        if (criteria != null) {
            String crStr = criteria.toSql();
            if ((crStr != null) && (crStr.length() > 0)) {
                sql.append(" AND " + criteria.toSql());
            }
        }
        sql.append(" ORDER BY " + REQUEST_TABLE + "." + REQUEST_DATE + " DESC");
        if (count > 0) {
            sql.append(" LIMIT " + count);
        }

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            getInstance().debugWrite("getAllRequests: Attempting to execute: " + sql.toString());
            rs = executeQuery(st, "getAllRequests", sql.toString());
            if (rs != null) {
                CMnPatch patch = null;
                while (rs.next()) {
                    patch = parseRequestData(rs);
                    if (deep && (patch != null)) {
                        // Load the list of fixes for the current patch
                        Vector<CMnPatchFix> fxs = getFixes(conn, patch.getId().toString(), false);
                        patch.setFixes(fxs);
                    }
                    list.add(patch);
                }
            } else {
                 getInstance().debugWrite("Unable to obtain the service patch data.");
            }
        } catch (SQLException ex) {
            getInstance().debugWrite("getAllRequests: Failed to obtain service patch data: " + ex.toString());
            getInstance().debugWrite(ex);
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return list;
    }


    /**
     * Retrieve a list of service patches previously requested for this customer and build.
     *
     * @param   conn    Database connection
     * @param   build   Build ID
     * @param   cust    Customer ID
     * @param   deep      Determines whether to perform querries to fill in related data
     */
    public synchronized Vector<CMnPatch> getRelatedRequests(Connection conn, String build, String cust, boolean deep)
        throws SQLException
    {
        Vector<CMnPatch> list = new Vector<CMnPatch>();

        StringBuffer sql = new StringBuffer();
        sql.append("SELECT * FROM " + REQUEST_TABLE + ", " + 
                                      CMnBuildTable.BUILD_TABLE + ", " +
                                      CMnCustomerTable.ACCOUNT_TABLE + ", " +
                                      CMnCustomerTable.ENV_TABLE +
                           " WHERE " + REQUEST_TABLE + "." + ACCOUNT_ID + " = " + CMnCustomerTable.ACCOUNT_TABLE + "." + CMnCustomerTable.ACCOUNT_ID +
                             " AND " + REQUEST_TABLE + "." + ENVIRONMENT_ID + " = " + CMnCustomerTable.ENV_TABLE + "." + CMnCustomerTable.ENV_ID +
                             " AND " + REQUEST_TABLE + "." + BUILD_ID + " = " + CMnBuildTable.BUILD_TABLE + "." + CMnBuildTable.BUILD_ID +
                             " AND " + REQUEST_TABLE + "." + BUILD_ID + " = " + build + 
                             " AND " + REQUEST_TABLE + "." + ACCOUNT_ID + " = " + cust +
                             " ORDER BY " + REQUEST_TABLE + "." + REQUEST_DATE + " DESC"); 

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            getInstance().debugWrite("Attempting to execute: " + sql.toString());
            rs = executeQuery(st, "getRelatedRequests", sql.toString());
            if (rs != null) {
                getInstance().debugWrite("Iterating through result set to parse for service patch requests.");
                CMnPatch patch = null;
                while (rs.next()) {
                    patch = parseRequestData(rs);
                    if (deep && (patch != null)) {
                        patch.setFixes(getFixes(conn, patch.getId().toString(), deep));
                        list.add(patch);
                    }
                    getInstance().debugWrite("Added patch " + patch.getId() + " to the list of results.");
                }
                getInstance().debugWrite("Finished processing service patch results.");
            } else {
                 getInstance().debugWrite("Unable to obtain the related service patch requests.");
            }
        } catch (SQLException ex) {
            getInstance().debugWrite("Encountered exception while parsing request data: " + ex.toString());
            getInstance().debugWrite(ex);
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return list;
    }


    /**
     * Retrieve a list of fixes associated with the service patch ID. 
     *
     * @param   conn    Database connection
     * @param   pid     Service patch ID 
     * @param   deep    Determines whether to perform querries to fill in related data
     */
    public synchronized Vector<CMnPatchFix> getFixes(Connection conn, String pid, boolean deep)
        throws SQLException
    {
        Vector<CMnPatchFix> list = new Vector<CMnPatchFix>();

        // Keep track of all patches related to these fixes
        // This allows the deep query to be optimized to minimize
        // the number of queries required to fetch origin data
        Hashtable<Integer,CMnPatch> origins = new Hashtable<Integer,CMnPatch>();

        StringBuffer sql = new StringBuffer();
        sql.append("SELECT * FROM " + FIX_TABLE +
                   " WHERE " + REQUEST_ID + " = '" + pid + "'" +
                   " ORDER BY " + FIX_ORIGIN + ", " + FIX_BUG_ID);

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            getInstance().debugWrite("Attempting to execute: " + sql.toString());
            rs = executeQuery(st, "getFixes", sql.toString());
            if (rs != null) {
                getInstance().debugWrite("Iterating through result set to parse for fixes in patch " + pid);
                CMnPatchFix fix = null;
                while (rs.next()) {
                    fix = parseFixData(rs);
                    if (fix != null) {
                        // Get the list of dependencies
                        if (deep) {
                            String bugId = Integer.toString(fix.getBugId());
                            fix.setDependencies(getDependencies(conn, pid, bugId));
                        }

                        // Get the origin information
                        if (fix.getOrigin() != null) {
                            Integer oid = fix.getOrigin().getId();
                            CMnPatch origin = null;
                            if (origins.containsKey(oid)) {
                                origin = (CMnPatch) origins.get(oid);
                            } else {
                                if (deep) {
                                    origin = getRequest(conn, oid.toString(), false);  
                                } 

                                // Make sure that an origin data object is returned
                                if (origin == null) {
                                    origin = new CMnPatch();
                                    origin.setId(oid);
                                }
                                origins.put(oid, origin);
                            }
                            fix.setOrigin(origin);
                        }

                        list.add(fix);
                        getInstance().debugWrite("Added fix " + fix.getBugId() + " to the patch request.");
                    } else {
                        getInstance().debugWrite("Failed to parse fix data for patch " + pid);
                    }
                }
                getInstance().debugWrite("Finished processing fix results.");
            } else {
                 getInstance().debugWrite("Unable to obtain the customer data.");
            }
        } catch (SQLException ex) {
            getInstance().debugWrite("Encountered exception while parsing fix data: " + ex.toString());
            getInstance().debugWrite(ex);
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return list;
    }


    /**
     * Delete the service patch request and all associated fixes.
     *
     * @param   conn    Database connection
     * @param   pid     Service patch ID
     * @return  TRUE if the service patch request was deleted
     */
    public synchronized boolean deleteRequest(Connection conn, String pid)
        throws SQLException
    {
        boolean success = false;

        StringBuffer sql = new StringBuffer();
        sql.append("DELETE FROM " + REQUEST_TABLE +
                   " WHERE " + REQUEST_ID + " = '" + pid + "'");

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            int fixes = deleteFixes(conn, pid);
            getInstance().debugWrite("Attempting to execute: " + sql.toString());
            success = st.execute(sql.toString());
        } catch (SQLException ex) {
            getInstance().debugWrite("Encountered exception while deleting patch data: " + ex.toString());
            getInstance().debugWrite(ex);
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return success;
    }


    /**
     * Delete the list of fixes associated with the service patch ID.
     * If an error occurred, a negative value will be returned.
     *
     * @param   conn    Database connection
     * @param   pid     Service patch ID
     * @return  Number of rows deleted, or -1 if an error occurred
     */
    public synchronized int deleteFixes(Connection conn, String pid)
        throws SQLException
    {
        int count = 0; 

        StringBuffer sql = new StringBuffer();
        sql.append("DELETE FROM " + FIX_TABLE +
                   " WHERE " + REQUEST_ID + " = '" + pid + "'");

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            getInstance().debugWrite("Attempting to execute: " + sql.toString());
            boolean success = st.execute(sql.toString());
            if (success) {
                count = 1;
            }
        } catch (SQLException ex) {
            count = -1;
            getInstance().debugWrite("Encountered exception while deleting fix data: " + ex.toString());
            getInstance().debugWrite(ex);
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return count;
    }



    /**
     * Add a new patch fix to the database.
     *
     * @param  conn     Database connection
     * @param  patchId  Service patch ID
     * @param  fix      Fix information
     * @return TRUE if the fix was added successfully
     */
    public synchronized boolean addFix(Connection conn, String patchId, CMnPatchFix fix)
        throws SQLException
    {
        boolean success = true;

        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO " + FIX_TABLE + " ");

        sql.append("(" + REQUEST_ID);
        sql.append(", " + FIX_REQUEST_DATE);
        if ((fix.getOrigin() != null) && (fix.getOrigin().getId() != null) && (fix.getOrigin().getId() > 0)) { 
            sql.append(", " + FIX_ORIGIN);
        }
        sql.append(", " + FIX_BUG_ID); 
        if (fix.getVersionControlRoot() != null) {
            sql.append(", " + FIX_BRANCH);
        }
        if (fix.getExclusionCount() > 0) {
            sql.append(", " + FIX_EXCLUSIONS);
        }
        if (fix.getNotes() != null) {
            sql.append(", " + FIX_NOTES);
        }

        sql.append(") VALUES ");

        sql.append("(\"" + patchId + "\"");
        sql.append(", NOW()");
        if ((fix.getOrigin() != null) && (fix.getOrigin().getId() != null) && (fix.getOrigin().getId() > 0)) {
            sql.append(", \"" + fix.getOrigin().getId() + "\"");
        }
        sql.append(", \"" + fix.getBugId() + "\"");
        if (fix.getVersionControlRoot() != null) {
            sql.append(", \"" + fix.getVersionControlRoot() + "\"");
        }
        if (fix.getExclusionCount() > 0) {
            sql.append(", \"" + fix.getExclusionsAsString() + "\"");
        }
        if (fix.getNotes() != null) {
            sql.append(", \"" + escapeQueryText(fix.getNotes()) + "\"");
        }
        sql.append(")");

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            getInstance().debugWrite("Attempting to execute: " + sql.toString());
            st.execute(sql.toString());
        } catch (SQLException ex) {
            success = false;
            getInstance().debugWrite("Failed to add patch request: " + ex.toString());
            getInstance().debugWrite(ex);
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return success;
    }



    /**
     * Add a new comment to the database.
     *
     * @param  conn     Database connection
     * @param  patchId  Service patch ID
     * @param  comment  Comment 
     * @return TRUE if the fix was added successfully
     */
    public synchronized boolean addComment(Connection conn, String patchId, CMnPatchComment comment)
        throws SQLException
    {
        boolean success = true;

        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO " + COMMENT_TABLE + " ");

        sql.append("(" + REQUEST_ID);
        if ((comment.getUser() != null) && (comment.getUser().getUid() != null)) {
            sql.append(", " + COMMENT_USER_ID);
        }
        sql.append(", " + COMMENT_STATUS);
        sql.append(", " + COMMENT_DATE);
        sql.append(", " + COMMENT_TEXT);

        sql.append(") VALUES ");

        sql.append("(\"" + patchId + "\"");
        if ((comment.getUser() != null) && (comment.getUser().getUid() != null)) { 
            sql.append(", \"" + comment.getUser().getUid() + "\"");
        }
        sql.append(", \"" + comment.getStatus() + "\"");
        if (comment.getDate() != null) {
            sql.append(", \"" + DATETIME.format(comment.getDate()) + "\"");
        } else {
            sql.append(", \"" + DATETIME.format(new Date()) + "\"");
        }
        sql.append(", \"" + escapeQueryText(comment.getComment()) + "\"");
        sql.append(")");

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            getInstance().debugWrite("Attempting to execute: " + sql.toString());
            st.execute(sql.toString());
        } catch (SQLException ex) {
            success = false;
            getInstance().debugWrite("Failed to add patch comment: " + ex.toString());
            getInstance().debugWrite(ex);
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return success;
    }


    /**
     * Retrieve the service patch comments associated with the given patch ID.
     *
     * @param   conn      Database connection
     * @param   patchId   Service patch ID
     * @param   deep      Determines whether to perform querries to fill in related data
     *
     * @return  Service patch information 
     */
    public synchronized Vector<CMnPatchComment> getComments(Connection conn, String patchId)
        throws SQLException
    {
        Vector<CMnPatchComment> comments = null;

        StringBuffer sql = new StringBuffer();
        sql.append("SELECT * FROM " + COMMENT_TABLE + 
                   " WHERE " + REQUEST_ID + " = '" + patchId + "'");

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            getInstance().debugWrite("Attempting to execute: " + sql.toString());
            rs = executeQuery(st, "getComments", sql.toString());
            if (rs != null) {
                getInstance().debugWrite("getComments cursor is at ROW " + rs.getRow());
                boolean hasMoreRows = rs.first();
                comments = new Vector<CMnPatchComment>();
                while (hasMoreRows) {
                    getInstance().debugWrite("Attempting to parse comment data.");
                    CMnPatchComment data = parseCommentData(rs);
                    getInstance().debugWrite("Adding comment ID " + data.getId() + " to patch " + patchId); 
                    comments.add(data);
                    hasMoreRows = rs.next();
                }
            } else {
                getInstance().debugWrite("Unable to obtain the service patch comments.");
            }
        } catch (SQLException ex) {
            getInstance().debugWrite("Failed to obtain service patch comments: " + sql.toString());
            ex.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return comments;
    }

    /**
     * Retrieve the list of dependencies associated with the given bug. 
     *
     * @param   conn      Database connection
     * @param   patchId   Patch ID
     * @param   bugId     Bug ID
     *
     * @return  List of bugs on which this one depends 
     */
    public synchronized Vector<CMnBaseFixDependency> getDependencies(Connection conn, String patchId, String bugId)
        throws SQLException
    {
        Vector<CMnBaseFixDependency> dependencies = null;

        StringBuffer sql = new StringBuffer();
        sql.append("SELECT * FROM " + DEPENDENCY_TABLE +
                   " WHERE " + REQUEST_ID + " = '" + patchId + "'" +
                   "  AND  " + FIX_BUG_ID + " = '" + bugId + "'");

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            getInstance().debugWrite("Attempting to execute: " + sql.toString());
            rs = executeQuery(st, "getDependencies", sql.toString());
            if (rs != null) {
                getInstance().debugWrite("getDependencies cursor is at ROW " + rs.getRow());
                boolean hasMoreRows = rs.first();
                dependencies = new Vector<CMnBaseFixDependency>();
                while (hasMoreRows) {
                    getInstance().debugWrite("Attempting to parse dependency data.");
                    CMnBaseFixDependency data = parseDependencyData(rs);
                    dependencies.add(data);
                    hasMoreRows = rs.next();
                }
            } else {
                getInstance().debugWrite("Unable to obtain the dependencies for Bug ID " + bugId);
            }
        } catch (SQLException ex) {
            getInstance().debugWrite("Failed to obtain service patch dependencies: " + sql.toString());
            ex.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return dependencies;
    }


    /**
     * Add a new bug dependency to the database.
     *
     * @param   conn     Database connection
     * @param   pid      Patch ID
     * @param   bid      Bug ID
     * @param   dep      Dependency information
     * 
     * @return  TRUE if the dependency was added
     */
    public synchronized boolean addDependency(Connection conn, String pid, String bid, CMnBaseFixDependency dep)
        throws SQLException
    {
        boolean success = false;

        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO " + DEPENDENCY_TABLE + " ");
        sql.append("(" + REQUEST_ID);
        sql.append(", " + FIX_BUG_ID);
        sql.append(", " + DEPENDENCY_ON);
        sql.append(", " + DEPENDENCY_TYPE + ") ");

        sql.append("VALUES ");
        sql.append("(\"" + pid + "\"");
        sql.append(", \"" + bid + "\"");
        sql.append(", \"" + dep.getBugId() + "\"");
        sql.append(", \"" + dep.getType() + "\")");

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            getInstance().debugWrite("Attempting to execute: " + sql.toString());
            st.execute(sql.toString());
            success = true;
        } catch (SQLException ex) {
            getInstance().debugWrite("Encountered exception while inserting dependency data: " + ex.toString());
            getInstance().debugWrite(ex);
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return success;
    }


    /**
     * Delete the dependency from the patch request bug list. 
     *
     * @param   conn    Database connection
     * @param   pid     Service patch ID
     * @param   bid     Bug ID (source)
     * @param   did     Dependent Bug ID (target)
     * @return  Number of rows deleted, or -1 if an error occurred
     */
    public synchronized int deleteDependency(Connection conn, String pid, String bid, String did)
        throws SQLException
    {
        int count = 0;

        StringBuffer sql = new StringBuffer();
        sql.append("DELETE FROM " + DEPENDENCY_TABLE +
                   " WHERE " + REQUEST_ID + " = '" + pid + "'" +
                   "  AND  " + FIX_BUG_ID + " = '" + bid + "'" +
                   "  AND  " + DEPENDENCY_ON + " = '" + did + "'");

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            getInstance().debugWrite("Attempting to execute: " + sql.toString());
            boolean success = st.execute(sql.toString());
            if (success) {
                count = 1;
            }
        } catch (SQLException ex) {
            count = -1;
            getInstance().debugWrite("Encountered exception while deleting approval data: " + ex.toString());
            getInstance().debugWrite(ex);
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return count;
    }


    /**
     * Add a new patch request to the database.
     *
     */
    public synchronized String addRequest(Connection conn, CMnPatch patch)
        throws SQLException
    {
        String patchId = null;

        String email = null;
        if (patch.getCCList() != null) {
            email = InternetAddress.toString(patch.getCCList());
        }

        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO " + REQUEST_TABLE + " ");
        sql.append("(" + PATCH_NAME);
        sql.append(", " + REQUEST_INTERNAL);
        sql.append(", " + REQUEST_DATE);
        sql.append(", " + ACCOUNT_ID);
        sql.append(", " + USER_ID);
        sql.append(", " + ENVIRONMENT_ID);
        sql.append(", " + BUILD_ID);
        if (patch.getPatchBuild() != null) {
            sql.append(", " + PATCH_BUILD);
        }
        if (patch.getPreviousPatch() != null) {
            sql.append(", " + PREVIOUS_PATCH_ID);
        }
        if (email != null) {
            sql.append(", " + PATCH_NOTIFICATION);
        }
        sql.append(", " + PATCH_JUSTIFICATION + ") ");

        sql.append("VALUES ");
        sql.append("(\"" + escapeQueryText(patch.getName()) + "\"");
        sql.append(", \"" + (!patch.getForExternalUse()) + "\"");
        if (patch.getRequestDate() != null) {
            sql.append(", \"" + DATETIME.format(patch.getRequestDate()) + "\"");
        } else {
            sql.append(", \"" + DATETIME.format(new Date()) + "\"");
        }
        sql.append(", \"" + patch.getCustomer().getId() + "\"");
        sql.append(", \"" + patch.getRequestor().getUid() + "\"");
        sql.append(", \"" + patch.getEnvironment().getId() + "\"");
        sql.append(", \"" + patch.getBuild().getId() + "\"");
        if (patch.getPatchBuild() != null) {
            sql.append(", \"" + patch.getPatchBuild().getId() + "\"");
        }
        if (patch.getPreviousPatch() != null) {
            sql.append(", \"" + patch.getPreviousPatch().getId() + "\"");
        }
        if (email != null) {
            sql.append(", \"" + escapeQueryText(email) + "\"");
        }
        sql.append(", \"" + escapeQueryText(patch.getJustification()) + "\")");

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            getInstance().debugWrite("Attempting to execute: " + sql.toString());
            st.execute(sql.toString());
            //rs = st.getGeneratedKeys();
            rs = getGeneratedKeys(st);
            if ((rs != null) && rs.first()) {
                patchId = rs.getString(1);
            } else {
                 getInstance().debugWrite("Unable to obtain generated key.");
            }
        } catch (SQLException ex) {
            getInstance().debugWrite("Failed to add patch request: " + ex.toString());
            getInstance().debugWrite(ex);
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return patchId;
    }


    /**
     * Update an existing patch request in the database.
     *
     * @return TRUE if the update was successful, FALSE otherwise
     */
    public synchronized boolean updateRequest(Connection conn, CMnPatch patch)
        throws SQLException
    {
        boolean success = true;

        String email = null;
        if (patch.getCCList() != null) {
            email = InternetAddress.toString(patch.getCCList());
        }


        StringBuffer sql = new StringBuffer();
        sql.append("UPDATE " + REQUEST_TABLE + " SET ");

        // Update patch name
        sql.append(PATCH_NAME + "='" + escapeQueryText(patch.getName()) + "'");

        // Update the patch external status
        sql.append(", " + REQUEST_INTERNAL + "='" + patch.getForExternalUse() + "'");

        // Update patch date
        if (patch.getRequestDate() != null) {
            sql.append(", " + REQUEST_DATE + "='" + DATETIME.format(patch.getRequestDate()) + "'");
        } else {
            sql.append(", " + REQUEST_DATE + "='" + DATETIME.format(new Date()) + "'");
        }

        // Update patch customer account
        if ((patch.getCustomer() != null) && (patch.getCustomer().getId() != null)) {
            sql.append(", " + ACCOUNT_ID + "='" + patch.getCustomer().getId() + "'");
        }

        // Update patch requestor
        if ((patch.getRequestor() != null) && (patch.getRequestor().getUid() != null)) {
            sql.append(", " + USER_ID + "='" + patch.getRequestor().getUid() + "'");
        }

        // Update patch environment
        if ((patch.getEnvironment() != null) && (patch.getEnvironment().getId() != null)) {
            sql.append(", " + ENVIRONMENT_ID + "='" + patch.getEnvironment().getId() + "'");
        }

        // Update product build
        if (patch.getBuild() != null) {
            sql.append(", " + BUILD_ID + "='" + patch.getBuild().getId() + "'");
        }

        // Update the previous patch
        if (patch.getPreviousPatch() != null) {
            sql.append(", " + PREVIOUS_PATCH_ID + "='" + patch.getPreviousPatch().getId() + "'");
        }

        // Update the build report
        if (patch.getPatchBuild() != null) {
            sql.append(", " + PATCH_BUILD + "='" + patch.getPatchBuild().getId() + "'");
        }

        // Update patch notification
        if (email != null) {
            sql.append(", " + PATCH_NOTIFICATION + "='" + escapeQueryText(email) + "'");
        }

        // Update patch justification
        if (patch.getJustification() != null) {
            sql.append(", " + PATCH_JUSTIFICATION + "='" + escapeQueryText(patch.getJustification()) + "'");
        }

        // Specify the patch being updatged
        sql.append(" WHERE " + REQUEST_ID + "='" + patch.getId() + "'");


        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            getInstance().debugWrite("Attempting to execute: " + sql.toString());
            st.execute(sql.toString());
        } catch (SQLException ex) {
            success = false;
            getInstance().debugWrite("Failed to update patch request: " + ex.toString());
            getInstance().debugWrite(ex);
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return success;
    }


    /**
     * Add a patch status change to the history. 
     *
     * @param  conn  Database connection
     * @param  gid   Fix group ID
     * @param  fix   Bug ID
     */
    public synchronized void addRequestStatusHistory(
            Connection conn, 
            String pid, 
            String uid,
            CMnServicePatch.RequestStatus oldStatus, 
            CMnServicePatch.RequestStatus newStatus)
        throws SQLException
    {
        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO " + PATCH_STATUS_HISTORY_TABLE + " ");
        sql.append("(" + USER_ID);
        sql.append(", " + PATCH_STATUS_DATE);
        sql.append(", " + PATCH_STATUS_OLD);
        sql.append(", " + PATCH_STATUS_NEW);
        sql.append(", " + REQUEST_ID + ") ");

        sql.append("VALUES ");
        sql.append("(\"" + uid + "\"");
        sql.append(", NOW()");
        sql.append(", \"" + oldStatus + "\"");
        sql.append(", \"" + newStatus + "\"");
        sql.append(", \"" + pid + "\")");

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            getInstance().debugWrite("Attempting to execute: " + sql.toString());
            st.execute(sql.toString());
        } catch (SQLException ex) {
            getInstance().debugWrite("Encountered exception while inserting status history: " + ex.toString());
            getInstance().debugWrite(ex);
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }
    }


    /**
     * Retrieve the service patch status. 
     *
     * @param   conn      Database connection
     * @param   patchId   Service patch ID
     *
     * @return  Service patch status 
     */
    public synchronized CMnServicePatch.RequestStatus getRequestStatus(Connection conn, String patchId)
        throws SQLException
    {
        CMnServicePatch.RequestStatus status = null;

        StringBuffer sql = new StringBuffer();
        sql.append("SELECT " + REQUEST_STATUS + " FROM " + REQUEST_TABLE +
                   " WHERE " + REQUEST_ID + " = '" + patchId + "'");

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            getInstance().debugWrite("Attempting to execute: " + sql.toString());
            rs = executeQuery(st, "getRequestStatus", sql.toString());
            if ((rs != null) && rs.first()) {
                String value = rs.getString(REQUEST_STATUS);
                if (value != null) {
                    status = CMnServicePatch.RequestStatus.valueOf(value.toUpperCase());
                }
            } else {
                getInstance().debugWrite("Unable to obtain the service patch owner data.");
            }
        } catch (SQLException ex) {
            getInstance().debugWrite("Failed to obtain service patch owner data: " + ex.toString());
            getInstance().debugWrite(ex);
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return status;
    }


    /**
     * Update an existing patch request in the database with a new status.
     *
     * @param  conn   Database connection
     * @param  pid    Patch ID
     * @param  uid    User performing the update
     * @param  status Patch status
     *
     * @return TRUE if the update was successful, FALSE otherwise
     */
    public synchronized boolean updateRequestStatus(Connection conn, String pid, String uid, CMnServicePatch.RequestStatus status)
        throws SQLException
    {
        boolean success = true;

        StringBuffer sql = new StringBuffer();
        sql.append("UPDATE " + REQUEST_TABLE + " SET ");
        sql.append(REQUEST_STATUS + "='" + status + "'");

        // Specify the patch being updated
        sql.append(" WHERE " + REQUEST_ID + "='" + pid + "'");


        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            CMnServicePatch.RequestStatus oldStatus = getRequestStatus(conn, pid);
            addRequestStatusHistory(conn, pid, uid, oldStatus, status);
            getInstance().debugWrite("Attempting to execute: " + sql.toString());
            st.execute(sql.toString());
        } catch (SQLException ex) {
            success = false;
            getInstance().debugWrite("Failed to update patch request: " + ex.toString());
            getInstance().debugWrite(ex);
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return success;
    }


    /**
     * Update an existing patch fixes in the database with a new origin.
     *
     * @param  conn   Database connection
     * @param  pid    Patch ID
     * @param  fix    Fix information
     *
     * @return TRUE if the update was successful, FALSE otherwise
     */
    public synchronized boolean updateOrigin(Connection conn, String pid, CMnPatchFix fix)
        throws SQLException
    {
        boolean success = true;

        StringBuffer sql = new StringBuffer();
        sql.append("UPDATE " + FIX_TABLE + " SET ");
        sql.append(FIX_ORIGIN + "='" + fix.getOrigin().getId() + "'");

        // Specify the patch being updated
        sql.append(" WHERE " + REQUEST_ID + "='" + pid + "'");
        sql.append(" AND " + FIX_BUG_ID + "='" + fix.getBugId() + "'");


        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            getInstance().debugWrite("Attempting to execute: " + sql.toString());
            st.execute(sql.toString());
        } catch (SQLException ex) {
            success = false;
            getInstance().debugWrite("Failed to update patch request: " + ex.toString());
            getInstance().debugWrite(ex);
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return success;
    }



    /**
     * Update an existing patch request in the database with a new build report.
     *
     * @param  conn   Database connection
     * @param  pid    Patch ID
     * @param  build  Service patch build information 
     *
     * @return TRUE if the update was successful, FALSE otherwise
     */
    public synchronized boolean updateBuildReport(Connection conn, String pid, CMnDbBuildData build)
        throws SQLException
    {
        boolean success = true;

        StringBuffer sql = new StringBuffer();
        sql.append("UPDATE " + REQUEST_TABLE + " SET ");
        sql.append(PATCH_BUILD + "='" + build.getId() + "'");

        // Specify the patch being updated
        sql.append(" WHERE " + REQUEST_ID + "='" + pid + "'");


        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            getInstance().debugWrite("Attempting to execute: " + sql.toString());
            st.execute(sql.toString());
        } catch (SQLException ex) {
            success = false;
            getInstance().debugWrite("Failed to update patch request: " + ex.toString());
            getInstance().debugWrite(ex);
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return success;
    }



    /**
     * Return the list of patches that are still waiting for approval by this user.
     * This only returns patches which are in the 'pending' state waiting for approval
     * AND which match the list of releases that this user has approval rights for. 
     *
     * @param  conn  Database connection
     * @param  uid   User ID
     * @return List of patches waiting for approval
     */
    public synchronized Vector<CMnPatch> getPatchesForApproval(Connection conn, String uid)
        throws SQLException
    {
        Vector<CMnPatch> list = new Vector<CMnPatch>();

        // Obtain a list of product releases that this user has approval rights for
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT * FROM " + APPROVERS_TABLE + 
                   " WHERE " + APPROVER_ID + " = '" + uid + "'");

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            getInstance().debugWrite("Attempting to execute: " + sql.toString());
            rs = executeQuery(st, "getPatchesForApproval", sql.toString());
            if (rs != null) {
                // for each product release, obtain a list of patches in 'pending' state
                getInstance().debugWrite("Iterating through result set to parse for patches of interest to user " + uid);
                CMnPatchApprover approver = null;
                while (rs.next()) {
                    approver = parseApproverData(rs);

                    // Query for patch that match the version string
                    CMnSearchCriteria statusCriteria = new CMnSearchCriteria(
                        REQUEST_TABLE,
                        REQUEST_STATUS,
                        CMnSearchCriteria.EQUAL_TO,
                        approver.getStatus());
                    CMnSearchCriteria verCriteria = new CMnSearchCriteria(
                        CMnBuildTable.BUILD_TABLE, 
                        CMnBuildTable.BUILD_VERSION, 
                        CMnSearchCriteria.LIKE, 
                        approver.getBuildVersion()); 
                    CMnSearchGroup searchGroup = new CMnSearchGroup(CMnSearchGroup.AND);
                    searchGroup.add(statusCriteria); 
                    searchGroup.add(verCriteria);
                    Vector<CMnPatch> patches = getAllRequests(conn, searchGroup, 0, false);

                    // Add each patch to the list
                    int userUid = Integer.parseInt(uid);
                    CMnPatch patch = null;
                    Enumeration patchList = patches.elements();
                    while (patchList.hasMoreElements()) {
                        boolean addPatch = true;
                        patch = (CMnPatch) patchList.nextElement();

                        // Query the patch to determine if it has already been approved
                        Vector<CMnPatchApproval> approvals = getApprovals(conn, patch.getId().toString(), patch.getStatus());
                        if (approvals != null) {
                            CMnPatchApproval approval = null;
                            Enumeration approvalList = approvals.elements();
                            while (approvalList.hasMoreElements()) {
                                approval = (CMnPatchApproval) approvalList.nextElement(); 
                                // Don't include patches that the user has already approved 
                                int approvalUid = Integer.parseInt(approval.getUser().getUid());
                                if (approvalUid == userUid) {
                                    addPatch = false;
                                } 
                            }
                        }

                        if (addPatch) {
                            list.add(patch);
                        }
                    }
                }
                getInstance().debugWrite("Finished processing patch results.");
            } else {
                 getInstance().debugWrite("Unable to obtain the patch data.");
            }
        } catch (SQLException ex) {
            getInstance().debugWrite("Encountered exception while parsing approver data: " + ex.toString());
            getInstance().debugWrite(ex);
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return list;
    }


    /**
     * Return the list of approval entries associated with the patch request.
     *
     * @param  conn     Database connection
     * @param  pid      Patch ID
     * @return List of approvals
     */
    public synchronized Vector<CMnPatchApproval> getApprovals(Connection conn, String pid)
        throws SQLException
    {
        return getApprovals(conn, pid, null);
    }



    /**
     * Return the list of approval entries associated with the patch request.
     * Since approvals are only valid for a specific patch status, the patch
     * status must be provided when fetching the list of approvals.
     *
     * @param  conn     Database connection
     * @param  pid      Patch ID
     * @param  status   Patch status
     * @return List of approvals
     */
    public synchronized Vector<CMnPatchApproval> getApprovals(Connection conn, String pid, CMnServicePatch.RequestStatus status)
        throws SQLException
    {
        Vector<CMnPatchApproval> list = new Vector<CMnPatchApproval>();

        StringBuffer sql = new StringBuffer();
        sql.append("SELECT * FROM " + APPROVAL_TABLE); 
        sql.append(" WHERE " + APPROVAL_TABLE + "." + REQUEST_ID + " = '" + pid + "'");
        if (status != null) {
            sql.append("  AND " + APPROVAL_TABLE + "." + APPROVAL_PATCH_STATUS + "= '" + status + "'");
        }

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            getInstance().debugWrite("Attempting to execute: " + sql.toString());
            rs = executeQuery(st, "getApprovals", sql.toString());
            if (rs != null) {
                getInstance().debugWrite("Iterating through result set to parse for approvals in patch " + pid);
                CMnPatchApproval approval = null;
                while (rs.next()) {
                    approval = parseApprovalData(rs);
                    list.add(approval);
                    if (approval.getUser() != null) {
                        getInstance().debugWrite("Added approval from user [" + approval.getUser().getUid() + "] " + approval.getUser().getUsername() + " to the patch request.");
                    }
                }
                getInstance().debugWrite("Finished processing approval results.");
            } else {
                 getInstance().debugWrite("Unable to obtain the approval data.");
            }
        } catch (SQLException ex) {
            getInstance().debugWrite("Encountered exception while parsing approval data: " + ex.toString());
            getInstance().debugWrite(ex);
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return list;
    }


    /**
     * Delete the list of approvals associated with the service patch ID.
     * If an error occurred, a negative value will be returned.
     *
     * @param   conn    Database connection
     * @param   pid     Service patch ID
     * @return  Number of rows deleted, or -1 if an error occurred
     */
    public synchronized int deleteApprovals(Connection conn, String pid)
        throws SQLException
    {
        int count = 0;

        StringBuffer sql = new StringBuffer();
        sql.append("DELETE FROM " + APPROVAL_TABLE +
                   " WHERE " + REQUEST_ID + " = '" + pid + "'");

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            getInstance().debugWrite("Attempting to execute: " + sql.toString());
            boolean success = st.execute(sql.toString());
            if (success) {
                count = 1;
            }
        } catch (SQLException ex) {
            count = -1;
            getInstance().debugWrite("Encountered exception while deleting approval data: " + ex.toString());
            getInstance().debugWrite(ex);
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return count;
    }



    /**
     * Determine if the patch has received approval from all required groups. 
     *
     * @param  conn         Database connection
     * @param  approvals    List of approvals
     * @param  groups       List of approver groups
     * @param  pid          Patch ID
     * @return TRUE if all approvals have been granted 
     */
    public synchronized boolean isApproved(
            Connection conn, 
            Vector<CMnPatchApproval> approvals, 
            Vector<CMnPatchApproverGroup> groups, 
            String pid)
        throws SQLException
    {
        boolean approved = true;

        if ((approvals != null) && (approvals.size() > 0) && (groups != null) && (groups.size() > 0)) {
            getInstance().debugWrite("isApproved: Found " + approvals.size() + " approvals and " + groups.size() + " groups.");
            // Keep track of which groups have been satisfied
            Hashtable<GroupData,Boolean> results = new Hashtable<GroupData,Boolean>();

            // Determine if each approval group has an approval 
            Enumeration<CMnPatchApproverGroup> groupList = groups.elements();
            while (groupList.hasMoreElements()) {
                CMnPatchApproverGroup approvalGroup = groupList.nextElement(); 
                GroupData currentGroup = approvalGroup.getGroup();
                boolean satisfiesGroup = false;

                // Determine if any approval applies to the current group
                Enumeration<CMnPatchApproval> approvalList = approvals.elements();
                while (approvalList.hasMoreElements()) {
                    CMnPatchApproval currentApproval = approvalList.nextElement();
                    if ((currentApproval != null) && currentApproval.isApproved()) { 
                        UserData currentUser = currentApproval.getUser();
                        if ((currentUser != null) && currentUser.isPartOf(currentGroup)) {
                            satisfiesGroup = true;
                        }
                    }
                }

                // Add the current group status to the list
                getInstance().debugWrite("isApproved: Group " + currentGroup.getName() + " approval: " + satisfiesGroup);
                results.put(currentGroup, new Boolean(satisfiesGroup));
            }

            // Check to see if every group has approval
            Collection<Boolean> values = results.values();
            Iterator<Boolean> valIter = values.iterator();
            while (valIter.hasNext()) {
                if (valIter.next().booleanValue() == false) {
                    getInstance().debugWrite("isApproved: Patch has not been approved for all groups for the current patch: " + pid);
                    approved = false;
                }
            }
        } else if ((groups == null) || (groups.size() == 0)) {
            getInstance().debugWrite("isApproved: Approval granted since the patch has no approval groups: " + pid);
            approved = true; 
        } else {
            getInstance().debugWrite("isApproved: Unable to determine the approval status of the patch: " + pid);
            approved = false;
        }

        return approved;
    }


    /**
     * Return the list of all notification entries.
     *
     * @param  conn     Database connection
     * @return List of notifications
     */
    public synchronized Vector<CMnPatchNotification> getNotifications(Connection conn)
        throws SQLException
    {
        Vector<CMnPatchNotification> list = new Vector<CMnPatchNotification>();

        StringBuffer sql = new StringBuffer();

        sql.append("SELECT * FROM " + NOTIFICATION_TABLE); 
        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            getInstance().debugWrite("Attempting to execute: " + sql.toString());
            rs = executeQuery(st, "getNotifications", sql.toString());
            if (rs != null) {
                getInstance().debugWrite("Iterating through result set to parse for notifications");
                CMnPatchNotification notification = null;
                while (rs.next()) {
                    notification = parseNotificationData(rs);
                    list.add(notification);
                }
                getInstance().debugWrite("Finished processing notification results.");
            } else {
                getInstance().debugWrite("Unable to obtain the notification data.");
            }
        } catch (SQLException ex) {
            getInstance().debugWrite("Encountered exception while parsing notification data: " + ex.toString());
            getInstance().debugWrite(ex);
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return list;
    }


    /**
     * Return the list of notification entries associated with the patch status.
     * Since notifications are only valid for a specific patch status, the patch
     * status must be provided when fetching the list of notifications.
     *
     * @param  conn     Database connection
     * @param  status   Patch status
     * @return List of notifications
     */
    public synchronized Vector<CMnPatchNotification> getNotifications(Connection conn, CMnServicePatch.RequestStatus status)
        throws SQLException
    {
        Vector<CMnPatchNotification> list = new Vector<CMnPatchNotification>();

        StringBuffer sql = new StringBuffer();
        sql.append("SELECT * FROM " + NOTIFICATION_TABLE +
                   " WHERE FIND_IN_SET('" + status + "'," + REQUEST_STATUS + ") > 0");

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            getInstance().debugWrite("Attempting to execute: " + sql.toString());
            rs = executeQuery(st, "getNotifications", sql.toString());
            if (rs != null) {
                getInstance().debugWrite("Iterating through result set to parse for notifications in status " + status);
                CMnPatchNotification notification = null;
                while (rs.next()) {
                    notification = parseNotificationData(rs);
                    list.add(notification);
                }
                getInstance().debugWrite("Finished processing notification results.");
            } else {
                getInstance().debugWrite("Unable to obtain the notification data.");
            }
        } catch (SQLException ex) {
            getInstance().debugWrite("Encountered exception while parsing notification data: " + ex.toString());
            getInstance().debugWrite(ex);
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return list;
    }


    /**
     * Get a list of users who should be notified about the serivce patch.
     *
     * @param   conn     Database connection
     * @param   patch    Service patch
     */
    protected Vector<CMnPatchNotification> getNotifications(Connection conn, CMnPatch patch)
        throws SQLException
    {
        Vector<CMnPatchNotification> results = new Vector<CMnPatchNotification>();

        CMnAccount customer = patch.getCustomer();
        CMnDbBuildData build = patch.getBuild();

        // Get a list of notifications
        Vector<CMnPatchNotification> notifications = getNotifications(conn);

        // Translate the notifications into e-mail addresses
        Enumeration notificationList = notifications.elements();
        while (notificationList.hasMoreElements()) {
            boolean custMatch = false;
            boolean verMatch = false;
            CMnPatchNotification notification = (CMnPatchNotification) notificationList.nextElement();
            // Determine whether the patch matches the notification criteria
            // for customer and build version
            if ((notification.getCustomerId() != null) && (notification.getBuildVersion() != null)) {
               int customerId = customer.getId();
               int notificationId = notification.getCustomerId();
                // Determine if the customer is a match
                if (customerId == notificationId) {
                    custMatch = true;
                }

                // Determine if the build is a match
                if ((build != null) && (build.getBuildVersion() != null)) {
                    String version = build.getBuildVersion();
                    if (version.startsWith(notification.getBuildVersion())) {
                        verMatch = true;
                    } else {
                        verMatch = false;
                    }
                } else {
                    verMatch = false;
                }

                // Both the customer and build need to match
                if (verMatch && custMatch) {
                    getInstance().debugWrite("getNotifications: Customer and version found.  Adding notification " + notification.getId());
                    results.add(notification);
                }

            } else if ((notification.getCustomerId() != null) && (notification.getBuildVersion() == null)) {
                // Only the customer ID needs to match
                if (customer.getId() == notification.getCustomerId()) {
                    getInstance().debugWrite("getNotifications: Customer found.  Adding notification " + notification.getId());
                    results.add(notification);
                }
            } else if ((notification.getCustomerId() == null)  && (notification.getBuildVersion() != null)) {
                // Only the build version string needs to match
                if ((build != null) && (build.getBuildVersion() != null)) {
                    String version = build.getBuildVersion();
                    if (version.startsWith(notification.getBuildVersion())) {
                        getInstance().debugWrite("getNotifications: Build version found.  Adding notification " + notification.getId());
                        results.add(notification);
                    }
                }
            } else {
                getInstance().debugWrite("getNotifications: Null customer and build version.  Adding notification " + notification.getId());
                results.add(notification);
            }
        }

        return results;
    }


    /**
     * Return a list of all fixes that have been grouped together
     * based on the release they belong to. 
     *
     * @param  conn      Database connection
     */
    public Vector<CMnPatchGroup> getFixGroups(Connection conn)
        throws SQLException
    {
        return getFixGroups(conn, null, 0, null);
    }



    /**
     * Return a list of fixes that have been grouped together
     * based on the release they belong to. 
     *
     * @param  conn      Database connection
     * @param  version   Product version
     */
    public Vector<CMnPatchGroup> getFixGroups(Connection conn, String version) 
        throws SQLException
    {
        CMnSearchGroup group = new CMnSearchGroup(CMnSearchGroup.AND);
        CMnSearchCriteria versionCriteria = new CMnSearchCriteria(
            CMnPatchTable.FIX_GROUP_TABLE,
            CMnPatchTable.BUILD_VERSION,
            CMnSearchCriteria.EQUAL_TO,
            version 
        );
        group.add(versionCriteria);

        return getFixGroups(conn, group, 0, null);
    }


    /**
     * Return a list of fixes that have been grouped together
     * based on the release they belong to. 
     *
     * @param  conn      Database connection
     * @param  criteria  Set of limiting criteria
     * @param  count     Number of rows to return, zero for no limit
     * @param  bid       Find only groups that contain this bug ID 
     */
    public Vector<CMnPatchGroup> getFixGroups(Connection conn, CMnSearchGroup criteria, int count, String bid)
        throws SQLException
    {
        Vector<CMnPatchGroup> list = new Vector<CMnPatchGroup>();

        StringBuffer sql = new StringBuffer();
        sql.append("SELECT DISTINCT " + FIX_GROUP_TABLE + "." + FIX_GROUP_ID);
        sql.append(", " + FIX_GROUP_NAME);
        sql.append(", " + FIX_GROUP_DESCRIPTION);
        sql.append(", " + FIX_GROUP_STATUS);
        sql.append(", " + BUILD_VERSION);

        // Join the group and fixes tables if we have to limit by bug ID
        sql.append(" FROM " + FIX_GROUP_TABLE);
        if ((bid != null) && (bid.trim().length() > 0)) {
            sql.append(", " + FIX_GROUP_ITEM_TABLE);
        }

        //
        // Construct the WHERE clause to limit the search results 
        // 
        StringBuffer where = new StringBuffer();
        if ((bid != null) && (bid.trim().length() > 0)) {
            // Join the group and fix table by group ID if searching by bug ID
            where.append(" " + FIX_GROUP_TABLE + "." + FIX_GROUP_ID + " = " + FIX_GROUP_ITEM_TABLE + "." + FIX_GROUP_ID + " ");
            where.append(" AND " + FIX_BUG_ID + " = '" + bid + "'");
        }
        if ((criteria != null) && (criteria.toSql().length() > 0)) {
            if (where.length() > 0) {
                where.append(" AND ");
            }
            where.append(" " + criteria.toSql() + " ");
        }
        if (where.length() > 0) {
            sql.append(" WHERE " + where.toString());
        }

        // Sort the results by version
        sql.append(" ORDER BY " + BUILD_VERSION + " DESC");

        // Limit the size of the result set
        if (count > 0) {
            sql.append(" LIMIT " + count);
        }

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            rs = executeQuery(st, "getFixGroups", sql.toString());
            if (rs != null) {
                CMnPatchGroup group = null;
                while (rs.next()) {
                    group = parseGroupData(rs);
                    if (group != null) {
                        // Load the list of fixes for the current group
                        Vector<CMnBaseFix> fixes = getGroupFixes(conn, Integer.toString(group.getId()));
                        if ((fixes != null) && (fixes.size() > 0)) {
                            group.setFixes(fixes);
                        }
                    }
                    list.add(group);
                }
            } else {
                 getInstance().debugWrite("Unable to obtain the service patch group data.");
            }
        } catch (SQLException ex) {
            getInstance().debugWrite("Failed to obtain service patch group data: " + ex.toString());
            getInstance().debugWrite(ex);
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return list;
    }


    /**
     * Add a new fix group to the database. 
     *
     */
    public synchronized String addFixGroup(Connection conn, CMnPatchGroup group)
        throws SQLException
    {
        String groupId = null;

        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO " + FIX_GROUP_TABLE + " ");
        sql.append("(" + FIX_GROUP_STATUS);
        sql.append(", " + FIX_GROUP_NAME);
        sql.append(", " + FIX_GROUP_DESCRIPTION);
        sql.append(", " + BUILD_VERSION + ") ");

        sql.append("VALUES ");
        sql.append("(\"" + group.getStatus().toString() + "\"");
        sql.append(", \"" + escapeQueryText(group.getName()) + "\"");
        sql.append(", \"" + escapeQueryText(group.getDescription()) + "\"");
        sql.append(", \"" + group.getBuildVersion() + "\")");

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            getInstance().debugWrite("Attempting to execute: " + sql.toString());
            st.execute(sql.toString());
            //rs = st.getGeneratedKeys();
            rs = getGeneratedKeys(st);
            if ((rs != null) && rs.first()) {
                groupId = rs.getString(1);
            } else {
                 getInstance().debugWrite("Unable to obtain generated key.");
            }
        } catch (SQLException ex) {
            getInstance().debugWrite("Encountered exception while inserting group data: " + ex.toString());
            getInstance().debugWrite(ex);
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return groupId;
    }


    /**
     * Update an existing fix group in the database.
     *
     * @return TRUE if the update was successful, FALSE otherwise
     */
    public synchronized boolean updateFixGroup(Connection conn, CMnPatchGroup group)
        throws SQLException
    {
        boolean success = true;

        StringBuffer sql = new StringBuffer();
        sql.append("UPDATE " + FIX_GROUP_TABLE + " SET ");

        // Update group status 
        sql.append(FIX_GROUP_STATUS + "='" + group.getStatus() + "'");

        // Update the group name 
        sql.append(", " + FIX_GROUP_NAME + "='" + escapeQueryText(group.getName()) + "'");

        // Update the group description 
        sql.append(", " + FIX_GROUP_DESCRIPTION + "='" + escapeQueryText(group.getDescription()) + "'");

        // Update the assignment comments
        sql.append(", " + BUILD_VERSION + "='" + group.getBuildVersion() + "'");


        // Specify the patch being updated
        sql.append(" WHERE " + FIX_GROUP_ID + "='" + group.getId() + "'");


        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            getInstance().debugWrite("Attempting to execute: " + sql.toString());
            st.execute(sql.toString());
        } catch (SQLException ex) {
            success = false;
            getInstance().debugWrite("Failed to update fix group" + group.getId());
            getInstance().debugWrite(ex);
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return success;
    }



    /**
     * Add a new fix to a fix group.
     *
     * @param  conn  Database connection
     * @param  gid   Fix group ID
     * @param  fix   Bug ID
     */
    public synchronized void addFixGroupFix(Connection conn, String gid, String bid)
        throws SQLException
    {
        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO " + FIX_GROUP_ITEM_TABLE + " ");
        sql.append("(" + FIX_GROUP_ID);
        sql.append(", " + FIX_BUG_ID + ") ");

        sql.append("VALUES ");
        sql.append("(\"" + gid + "\"");
        sql.append(", \"" + bid + "\")");

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            getInstance().debugWrite("Attempting to execute: " + sql.toString());
            st.execute(sql.toString());
        } catch (SQLException ex) {
            getInstance().debugWrite("Encountered exception while inserting group data: " + ex.toString());
            getInstance().debugWrite(ex);
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }
    }



    /**
     * Return a fix group containing a list of related fixes. 
     *
     * @param  conn      Database connection
     * @param  gid       Fix group ID
     */
    public CMnPatchGroup getFixGroup(Connection conn, String gid)
        throws SQLException
    {
        CMnPatchGroup group = null;

        StringBuffer sql = new StringBuffer();
        sql.append("SELECT * FROM " + FIX_GROUP_TABLE);
        sql.append(" WHERE " + FIX_GROUP_ID + " = '" + gid + "'");

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            rs = executeQuery(st, "getFixGroup", sql.toString());
            if (rs != null) {
                while (rs.next()) {
                    group = parseGroupData(rs);
                    if (group != null) {
                        // Load the list of fixes for the current group
                        Vector<CMnBaseFix> fixes = getGroupFixes(conn, gid);
                        if ((fixes != null) && (fixes.size() > 0)) {
                            group.setFixes(fixes);
                        }
                    }
                }
            } else {
                 getInstance().debugWrite("Unable to obtain the service patch group data.");
            }
        } catch (SQLException ex) {
            getInstance().debugWrite("Failed to obtain service patch group data: " + ex.toString());
            getInstance().debugWrite(ex);
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return group;
    }


    /**
     * Retrieve a list of fixes associated with the fix group. 
     *
     * @param   conn    Database connection
     * @param   gid     Service patch group ID 
     */
    public synchronized Vector<CMnBaseFix> getGroupFixes(Connection conn, String gid)
        throws SQLException
    {
        Vector<CMnBaseFix> list = new Vector<CMnBaseFix>();

        StringBuffer sql = new StringBuffer();
        sql.append("SELECT * FROM " + FIX_GROUP_ITEM_TABLE +
                   " WHERE " + FIX_GROUP_ID + " = '" + gid + "'" +
                   " ORDER BY " + FIX_BUG_ID);

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            getInstance().debugWrite("Attempting to execute: " + sql.toString());
            rs = executeQuery(st, "getGroupFixes", sql.toString());
            if (rs != null) {
                CMnBaseFix fix = null;
                while (rs.next()) {
                    fix = parseGroupFixData(rs);
                    list.add(fix);
                }
            } else {
                getInstance().debugWrite("Unable to obtain the fix data.");
            }
        } catch (SQLException ex) {
            getInstance().debugWrite("Encountered exception while parsing fix data: " + ex.toString());
            getInstance().debugWrite(ex);
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return list;
    }


    /**
     * Delete the fix group and all associated fixes.
     *
     * @param   conn    Database connection
     * @param   gid     Fix Group ID
     * @return  TRUE if the fix group was deleted
     */
    public synchronized boolean deleteFixGroup(Connection conn, String gid)
        throws SQLException
    {
        boolean success = false;

        StringBuffer sql = new StringBuffer();
        sql.append("DELETE FROM " + FIX_GROUP_TABLE +
                   " WHERE " + FIX_GROUP_ID + " = '" + gid + "'");

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            int fixes = deleteFixGroupFixes(conn, gid);
            getInstance().debugWrite("Attempting to execute: " + sql.toString());
            success = st.execute(sql.toString());
        } catch (SQLException ex) {
            getInstance().debugWrite("Encountered exception while deleting fix group data: " + ex.toString());
            getInstance().debugWrite(ex);
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return success;
    }


    /**
     * Delete the list of fixes associated with the fix group ID.
     * If an error occurred, a negative value will be returned.
     *
     * @param   conn    Database connection
     * @param   gid     Fix group ID
     * @return  Number of rows deleted, or -1 if an error occurred
     */
    public synchronized int deleteFixGroupFixes(Connection conn, String gid)
        throws SQLException
    {
        int count = 0; 

        StringBuffer sql = new StringBuffer();
        sql.append("DELETE FROM " + FIX_GROUP_ITEM_TABLE +
                   " WHERE " + FIX_GROUP_ID + " = '" + gid + "'");

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            getInstance().debugWrite("Attempting to execute: " + sql.toString());
            boolean success = st.execute(sql.toString());
            if (success) {
                count = 1;
            }
        } catch (SQLException ex) {
            count = -1;
            getInstance().debugWrite("Encountered exception while deleting fix data: " + ex.toString());
            getInstance().debugWrite(ex);
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return count;
    }



    /**
     * Parse the result set to obtain patch information.
     *
     * @param   rs    Result set containing patch data
     *
     * @return  Patch information
     */
    public static CMnPatch parseSimpleRequestData(ResultSet rs)
        throws SQLException
    {
        CMnPatch data = new CMnPatch();

        int id = rs.getInt(REQUEST_TABLE + "." + REQUEST_ID);
        data.setId(id);

        String name = rs.getString(REQUEST_TABLE + "." + PATCH_NAME);
        data.setName(name);

        boolean internalUse = Boolean.parseBoolean(rs.getString(REQUEST_TABLE + "." + REQUEST_INTERNAL));
        data.setForExternalUse(!internalUse);

        Date date = rs.getTimestamp(REQUEST_TABLE + "." + REQUEST_DATE);
        data.setRequestDate(date);

        int patchBuildId = rs.getInt(REQUEST_TABLE + "." + PATCH_BUILD);
        if (patchBuildId > 0) {
            CMnDbBuildData build = new CMnDbBuildData();
            build.setId(patchBuildId);
            data.setPatchBuild(build);
        }

        int previousPatchId = rs.getInt(REQUEST_TABLE + "." + PREVIOUS_PATCH_ID);
        if (previousPatchId > 0) {
            CMnPatch previousPatch = new CMnPatch();
            previousPatch.setId(previousPatchId);
            data.setPreviousPatch(previousPatch);
        }

        String status = rs.getString(REQUEST_TABLE + "." + REQUEST_STATUS);
        if (status != null) {
            try {
                data.setStatus(CMnServicePatch.RequestStatus.valueOf(status.toUpperCase()));
            } catch (Exception ex) {
            }
        }

        String cc = rs.getString(REQUEST_TABLE + "." + PATCH_NOTIFICATION);
        if ((cc != null) && (cc.trim().length() > 0)) {
            try {
                data.setCCList(InternetAddress.parse(cc));
            } catch (AddressException aex) {
                // Hopefully data is correct in the database
            }
        }

        String just = rs.getString(REQUEST_TABLE + "." + PATCH_JUSTIFICATION);
        data.setJustification(just);

        return data;
    }


    /**
     * Parse the result set to obtain patch information.
     *
     * @param   rs    Result set containing patch data
     *
     * @return  Patch information
     */
    public static CMnPatch parseRequestData(ResultSet rs)
        throws SQLException
    {
        CMnPatch data = parseSimpleRequestData(rs); 

        // Parse the more complex nested objects
        CMnAccount customer = CMnCustomerTable.parseCustomerData(rs);
        data.setCustomer(customer);

        UserData requestor = new UserData();
        int uid = rs.getInt(REQUEST_TABLE + "." + USER_ID); 
        requestor.setUid(Integer.toString(uid)); 
        data.setRequestor(requestor);

        CMnEnvironment env = CMnCustomerTable.parseEnvironmentData(rs); 
        data.setEnvironment(env);

        CMnDbBuildData build = CMnBuildTable.parseBuildData(rs); 
        data.setBuild(build);

        return data;
    }


    /**
     * Parse the result set to obtain fix inforation.
     *
     * @param  rs   Result set containing fix data
     *
     * @return Fix information
     */
    public static CMnPatchFix parseFixData(ResultSet rs)
        throws SQLException
    {
        CMnPatchFix data = new CMnPatchFix();

        Date date = rs.getTimestamp(FIX_TABLE + "." + FIX_REQUEST_DATE);
        data.setDate(date);

        int origin = rs.getInt(FIX_TABLE + "." + FIX_ORIGIN);
        if (origin > 0) {
            CMnPatch patch = new CMnPatch();
            patch.setId(new Integer(origin));
            data.setOrigin(patch);
        }

        int bugId = rs.getInt(FIX_TABLE + "." + FIX_BUG_ID);
        data.setBugId(bugId);

        String notes = rs.getString(FIX_TABLE + "." + FIX_NOTES);
        data.setNotes(notes);

        String exclusions = rs.getString(FIX_TABLE + "." + FIX_EXCLUSIONS);
        if ((exclusions != null) && (exclusions.length() > 0)) {
            data.setExclusions(exclusions);
        }

        String branch = rs.getString(FIX_TABLE + "." + FIX_BRANCH);
        data.setVersionControlRoot(branch);

        return data;
    }


    /**
     * Parse the result set to obtain approval inforation.
     *
     * @param  rs   Result set containing approval data
     *
     * @return Approval information
     */
    public static CMnPatchApproval parseApprovalData(ResultSet rs)
        throws SQLException
    {
        CMnPatchApproval data = new CMnPatchApproval();

        UserData user = new UserData();
        int uid = rs.getInt(APPROVAL_TABLE + "." + USER_ID);
        user.setUid(Integer.toString(uid));
        data.setUser(user);

        String approvalStatus = rs.getString(APPROVAL_TABLE + "." + APPROVAL_STATUS);
        data.setStatus(approvalStatus);

        String patchStatus = rs.getString(APPROVAL_TABLE + "." + APPROVAL_PATCH_STATUS); 
        data.setPatchStatus(patchStatus);

        String comment = rs.getString(APPROVAL_TABLE + "." + APPROVAL_COMMENT);
        data.setComment(comment);

        return data;

    }

    /**
     * Get approvers for this build.  The status field limits the approvers to 
     * the ones who have approval for the specified patch status.
     *
     * @param   conn           Database connection
     * @param   buildString    Build version string (or portion of)
     * @param   status         Patch status 
     *
     * @return approvers approvers for this build
     */
    public Vector<CMnPatchApprover> getApproversForBuild(Connection conn, String buildString) 
        throws SQLException 
    {
        return getApproversForBuild(conn, buildString, null);
    }

    /**
     * Get approvers for this build.  The status field limits the approvers to 
     * the ones who have approval for the specified patch status.
     *
     * @param   conn           Database connection
     * @param   buildString    Build version string (or portion of)
     * @param   status         Patch status 
     *
     * @return approvers approvers for this build
     */
    public Vector<CMnPatchApprover> getApproversForBuild(Connection conn, String buildString, CMnServicePatch.RequestStatus status) throws SQLException {
        Vector<CMnPatchApprover> list = new Vector<CMnPatchApprover>();


        StringBuffer userSql = new StringBuffer();
        userSql.append("SELECT * FROM " + APPROVERS_TABLE);
        if (status != null) {
            userSql.append("  WHERE  " + APPROVERS_TABLE + "." + REQUEST_STATUS + " = '" + status + "'");
        } 

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            getInstance().debugWrite("Attempting to execute: " + userSql.toString());
            rs = executeQuery(st, "getApproversForBuild", userSql.toString());
            if (rs != null) {
                getInstance().debugWrite("Iterating through result set to parse for approvers for patch ");
                UserData user = null;
                CMnPatchApprover approver = null;
                while (rs.next()) {
                    approver = parseApproverData(rs);
                    if(buildString.startsWith(approver.getBuildVersion())){
                        getInstance().debugWrite("Approver version " + approver.getBuildVersion() + " matches build " + buildString);
                        list.add(approver);
                    } else {
                        getInstance().debugWrite("Approver version " + approver.getBuildVersion() + " does not match build " + buildString);
                    }
                }
                getInstance().debugWrite("Finished processing approver results with " + list.size() + " matches.");
            } else {
                getInstance().debugWrite("Unable to obtain the approver data.");
            }
        } catch (SQLException ex) {
            getInstance().debugWrite("Encountered exception while parsing approver data: " + ex.toString());
            getInstance().debugWrite(ex);
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return list;
    }



    /**
     * Parse the result set to obtain approver's build version information.
     *
     * @param  rs   Result set containing approver data
     *
     * @return Approver build version information
     */
    public static CMnPatchApprover parseApproverData(ResultSet rs)
        throws SQLException
    {
        CMnPatchApprover approver = new CMnPatchApprover();

        String uid = rs.getString(APPROVERS_TABLE + "." + APPROVER_ID);
        if (uid != null) {
            UserData user = new UserData();
            user.setUid(uid);
            approver.setUser(user);
        } 

        String buildVer = rs.getString(APPROVERS_TABLE + "." + BUILD_VERSION);
        if(buildVer != null) {
            buildVer = buildVer.replaceAll("%", ".*?");
            approver.setBuildVersion(buildVer);
        }

        String status = rs.getString(APPROVERS_TABLE + "." + REQUEST_STATUS);
        if ((status != null) && (status.length() > 0)) {
            approver.setStatus(status);
        } 

        return approver;
    }


    /**
     * Add a new patch request to the database.
     *
     */
    public synchronized String addApproval(Connection conn, CMnPatch patch, CMnPatchApproval approval)
        throws SQLException
    {
        String approvalId = null;


        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO " + APPROVAL_TABLE + " ");
        sql.append("(" + REQUEST_ID);
        sql.append(", " + USER_ID);
        sql.append(", " + STATUS);
        sql.append(", " + APPROVAL_PATCH_STATUS);
        sql.append(", " + COMMENT + ") ");

        sql.append("VALUES ");
        sql.append("(\"" + patch.getId() + "\"");
        sql.append(", \"" + approval.getUser().getUid() + "\"");
        sql.append(", \"" + approval.getStatus().toString() + "\"");
        sql.append(", \"" + patch.getStatus().toString() + "\"");
        sql.append(", \"" + escapeQueryText(approval.getComment()) + "\")");

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            getInstance().debugWrite("Attempting to execute: " + sql.toString());
            st.execute(sql.toString());
            //rs = st.getGeneratedKeys();
            rs = getGeneratedKeys(st);
            if ((rs != null) && rs.first()) {
                approvalId = rs.getString(1);
            } else {
                 getInstance().debugWrite("Unable to obtain generated key.");
            }
        } catch (SQLException ex) {
            getInstance().debugWrite("Encountered exception while inserting approval data: " + ex.toString());
            getInstance().debugWrite(ex);
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return approvalId;
    }


    /**
     * Retrieve the service patch assignment information associated with the given patch.
     *
     * @param   conn      Database connection
     * @param   patchId   Service patch ID
     *
     * @return  Service patch assignment information 
     */
    public synchronized CMnPatchOwner getPatchOwner(Connection conn, String patchId)
        throws SQLException
    {
        CMnPatchOwner owner = null;

        StringBuffer sql = new StringBuffer();
        sql.append("SELECT * FROM " + ASSIGNMENT_TABLE + 
                   " WHERE " + ASSIGNMENT_TABLE + "." + ASSIGNMENT_PATCH_ID + " = '" + patchId + "'");

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            getInstance().debugWrite("Attempting to execute: " + sql.toString());
            rs = executeQuery(st, "getPatchOwner", sql.toString());
            if ((rs != null) && rs.first()) {
                owner = parseOwnerData(rs);
            } else {
                getInstance().debugWrite("Unable to obtain the service patch owner data.");
            }
        } catch (SQLException ex) {
            getInstance().debugWrite("Failed to obtain service patch owner data: " + ex.toString());
            getInstance().debugWrite(ex);
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return owner;
    }


    /**
     * Assign ownership of a service patch.
     *
     * @param   owner   Patch owner
     */
    public static void addPatchOwner(Connection conn, CMnPatchOwner owner) 
        throws SQLException
    {
        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO " + ASSIGNMENT_TABLE + " ");
        sql.append("(" + ASSIGNMENT_PATCH_ID);
        if (owner.getStartDate() != null) {
            sql.append(", " + ASSIGNMENT_START_DATE);
        }
        if (owner.getEndDate() != null) {
            sql.append(", " + ASSIGNMENT_END_DATE);
        }
        if (owner.getDeadline() != null) {
            sql.append(", " + ASSIGNMENT_DEADLINE);
        }
        if (owner.getComment() != null) {
            sql.append(", " + ASSIGNMENT_COMMENTS);
        }
        if (owner.getPriority() != null) {
            sql.append(", " + ASSIGNMENT_PRIORITY); 
        }
        sql.append(", " + ASSIGNMENT_USER_ID + ") ");

        sql.append("VALUES ");
        sql.append("(\"" + owner.getPatchId() + "\"");
        if (owner.getStartDate() != null) {
            sql.append(", \"" + DATETIME.format(owner.getStartDate()) + "\"");
        }
        if (owner.getEndDate() != null) {
            sql.append(", \"" + DATETIME.format(owner.getEndDate()) + "\"");
        }
        if (owner.getDeadline() != null) {
            sql.append(", \"" + DATETIME.format(owner.getDeadline()) + "\"");
        }
        if (owner.getComment() != null) {
            sql.append(", \"" + escapeQueryText(owner.getComment()) + "\"");
        }
        if (owner.getPriority() != null) {
            sql.append(", \"" + owner.getPriority().toString() + "\"");
        }
        sql.append(", \"" + owner.getUser().getUid() + "\")");

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            getInstance().debugWrite("Attempting to execute: " + sql.toString());
            st.execute(sql.toString());
        } catch (SQLException ex) {
            getInstance().debugWrite("Failed to add owner data: " + ex.toString());
            getInstance().debugWrite(ex);
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

    }


    /**
     * Update an existing patch request in the database.
     *
     * @return TRUE if the update was successful, FALSE otherwise
     */
    public synchronized boolean updatePatchOwner(Connection conn, CMnPatchOwner owner)
        throws SQLException
    {
        boolean success = true;

        StringBuffer sql = new StringBuffer();
        sql.append("UPDATE " + ASSIGNMENT_TABLE + " SET ");

        //    sql.append(", " + ASSIGNMENT_START_DATE);
        //    sql.append(", " + ASSIGNMENT_END_DATE);
        //    sql.append(", " + ASSIGNMENT_DEADLINE);


        // Update the user assignment 
        sql.append(ASSIGNMENT_USER_ID + "='" + owner.getUser().getUid() + "'");

        // Update the patch priority 
        if (owner.getPriority() != null) {
            sql.append(", " + ASSIGNMENT_PRIORITY + "='" + owner.getPriority() + "'");
        } else {
            sql.append(", " + ASSIGNMENT_PRIORITY + "='" + CMnPatchOwner.PatchPriority.LOW + "'");
        }

        // Update the start, end, and deadline dates
        if (owner.getStartDate() != null) {
            sql.append(", " + ASSIGNMENT_START_DATE + "='" + DATETIME.format(owner.getStartDate()) + "'");
        } else {
            sql.append(", " + ASSIGNMENT_START_DATE + "='" + NULL_DATETIME + "'");
        }

        if (owner.getEndDate() != null) {
            sql.append(", " + ASSIGNMENT_END_DATE + "='" + DATETIME.format(owner.getEndDate()) + "'");
        } else {
            sql.append(", " + ASSIGNMENT_END_DATE + "='" + NULL_DATETIME + "'");
        }

        if (owner.getDeadline() != null) {
            sql.append(", " + ASSIGNMENT_DEADLINE + "='" + DATETIME.format(owner.getDeadline()) + "'");
        } else {
            sql.append(", " + ASSIGNMENT_DEADLINE + "='" + NULL_DATETIME + "'");
        }

        // Update the assignment comments
        sql.append(", " + ASSIGNMENT_COMMENTS + "='" + escapeQueryText(owner.getComment()) + "'");


        // Specify the patch being updated
        sql.append(" WHERE " + ASSIGNMENT_PATCH_ID + "='" + owner.getPatchId() + "'");


        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            getInstance().debugWrite("Attempting to execute: " + sql.toString());
            st.execute(sql.toString());
        } catch (SQLException ex) {
            success = false;
            getInstance().debugWrite("Failed to update patch owner: " + ex.toString());
            getInstance().debugWrite(ex);
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return success;
    }



    /**
     * Parse the patch ownership information.
     *
     * @param  rs   Result set containing the owner data
     *
     * @return  Patch owner info
     */
    public static CMnPatchOwner parseOwnerData(ResultSet rs) 
        throws SQLException
    {
        CMnPatchOwner owner = new CMnPatchOwner();

        int userId = rs.getInt(ASSIGNMENT_TABLE + "." + USER_ID);
        UserData user = new UserData();
        user.setUid(Integer.toString(userId)); 
        owner.setUser(user);

        Date startDate = rs.getTimestamp(ASSIGNMENT_TABLE + "." + ASSIGNMENT_START_DATE);
        owner.setStartDate(startDate);

        Date endDate = rs.getTimestamp(ASSIGNMENT_TABLE + "." + ASSIGNMENT_END_DATE);
        owner.setEndDate(endDate);

        Date deadline = rs.getTimestamp(ASSIGNMENT_TABLE + "." + ASSIGNMENT_DEADLINE);
        owner.setDeadline(deadline);

        String comments = rs.getString(ASSIGNMENT_TABLE + "." + ASSIGNMENT_COMMENTS);
        owner.setComment(comments);

        String priority = rs.getString(ASSIGNMENT_TABLE + "." + ASSIGNMENT_PRIORITY);
        owner.setPriority(priority);

        return owner;
    }


    /**
     * Parse the patch comments. 
     *
     * @param  rs   Result set containing the comment data
     *
     * @return  Patch comment
     */
    public static CMnPatchComment parseCommentData(ResultSet rs)
        throws SQLException
    {
        CMnPatchComment comment = new CMnPatchComment();

        int commentId = rs.getInt(USER_ID);
        comment.setId(new Integer(commentId));

        int userId = rs.getInt(USER_ID);
        UserData user = new UserData();
        user.setUid(Integer.toString(userId));
        comment.setUser(user);

        Date date = rs.getTimestamp(COMMENT_DATE);
        comment.setDate(date);

        String text = rs.getString(COMMENT_TEXT);
        comment.setComment(text);

        String status = rs.getString(COMMENT_STATUS);
        comment.setStatus(status);

        return comment;
    }


    /**
     * Parse the patch dependency.
     *
     * @param  rs   Result set containing the dependency data
     *
     * @return  Patch dependency
     */
    public static CMnBaseFixDependency parseDependencyData(ResultSet rs)
        throws SQLException
    {
        CMnBaseFixDependency dependency = new CMnBaseFixDependency();

        int bugId = rs.getInt(DEPENDENCY_ON);
        dependency.setBugId(new Integer(bugId));

        String type = rs.getString(DEPENDENCY_TYPE);
        if (type != null) {
            dependency.setType(CMnBaseFixDependency.DependencyType.valueOf(type.toUpperCase()));
        }

        return dependency;
    }



    /**
     * Parse the result set to obtain notification data. 
     *
     * @param  rs   Result set containing notification data
     *
     * @return Notification information
     */
    public static CMnPatchNotification parseNotificationData(ResultSet rs)
        throws SQLException
    {
        CMnPatchNotification notification = new CMnPatchNotification();

        int notificationId = rs.getInt(NOTIFICATION_ID);
        notification.setId(notificationId);
        
        int userId = rs.getInt(NOTIFICATION_USER_ID);
        notification.setUserId(userId);

        // Notifications are not required to include a customer ID
        // The customer ID is used to determine if the user wishes
        // to be notified only for patches requested for a specific
        // customer
        String custId = rs.getString(ACCOUNT_ID);
        if (custId != null) {
            notification.setCustomerId(new Integer(custId));
        }

        // The build version is not required and if specified will
        // be used to look for a pattern of build versions
        String version = rs.getString(BUILD_VERSION);
        notification.setBuildVersion(version);

        // Status is a comma-delmited list of values in the set
        //pass in a hardocrd string  "Status" as the rs contains two status from login and the patch_notification table
        // earlier when it was set to Request_Status it was getting a list of loing status and not the patch_notification table status
        String status = rs.getString("Status");
        if (status != null) {
            //pass the status csv string and the comma delim
            StringTokenizer st = new StringTokenizer(status,",");
            while (st.hasMoreTokens()) {
                CMnServicePatch.RequestStatus statusValue = null;
                try {
                    statusValue = CMnServicePatch.RequestStatus.valueOf(st.nextToken().toUpperCase());
                } catch (Exception ex) {
                }
                notification.addStatus(statusValue);
            }
        }

        return notification;
    }


    /**
     * Parse the result set to obtain fix group information.
     *
     * @param  rs   Result set containing fix group data
     *
     * @return  Fix group information
     */
    public static CMnPatchGroup parseGroupData(ResultSet rs)
        throws SQLException
    {
        CMnPatchGroup group = new CMnPatchGroup();

        int groupId = rs.getInt(FIX_GROUP_ID);
        group.setId(groupId);

        String name = rs.getString(FIX_GROUP_NAME);
        group.setName(name);

        String desc = rs.getString(FIX_GROUP_DESCRIPTION);
        group.setDescription(desc);

        String status = rs.getString(FIX_GROUP_STATUS);
        group.setStatus(status);

        String version = rs.getString(BUILD_VERSION);
        group.setBuildVersion(version);

        return group;
    }

    /**
     * Parse the result set to obtain a patch fix  
     *
     * @param  rs   Result set containing group fix data
     *
     * @return Fix information
     */
    public static CMnBaseFix parseGroupFixData(ResultSet rs)
        throws SQLException
    {
        CMnBaseFix data = new CMnBaseFix();

        int groupId = rs.getInt(FIX_GROUP_ID);
        data.setGroupId(groupId);

        int bugId = rs.getInt(FIX_BUG_ID);
        data.setBugId(bugId);

        String notes = rs.getString(FIX_NOTES);
        data.setNotes(notes);

        String exclusions = rs.getString(FIX_EXCLUSIONS);
        if ((exclusions != null) && (exclusions.length() > 0)) {
            data.setExclusions(exclusions);
        }

        String branch = rs.getString(FIX_BRANCH);
        data.setVersionControlRoot(branch);

        return data;
    }


}

