/*
 * Login.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.ctrl.command.patch; 

import com.modeln.build.common.data.account.CMnAccount;
import com.modeln.build.common.data.account.CMnEnvironment;
import com.modeln.build.common.data.product.CMnPatch;
import com.modeln.build.common.data.product.CMnPatchComment;
import com.modeln.build.common.data.product.CMnPatchFix;
import com.modeln.build.common.data.product.CMnPatchApproval;
import com.modeln.build.common.data.product.CMnPatchApprover;
import com.modeln.build.common.data.product.CMnPatchApproverGroup;
import com.modeln.build.common.data.product.CMnPatchNotification;
import com.modeln.build.common.data.product.CMnProduct;
import com.modeln.build.common.database.CMnCustomerTable;
import com.modeln.build.common.tool.CMnPatchUtil;
import com.modeln.build.ctrl.CMnControlApp;
import com.modeln.build.ctrl.database.CMnPatchTable;
import com.modeln.build.ctrl.forms.CMnBaseForm;
import com.modeln.build.ctrl.forms.IMnPatchForm;
import com.modeln.build.jenkins.Build;
import com.modeln.build.jenkins.Job;
import com.modeln.build.jenkins.XmlApi;
import com.modeln.build.sdtracker.CMnBug;
import com.modeln.build.sdtracker.CMnBugTable;
import com.modeln.build.sourcecontrol.CMnCheckIn;
import com.modeln.testfw.reporting.CMnBuildTable;
import com.modeln.testfw.reporting.CMnDbBuildData;
import com.modeln.testfw.reporting.CMnDbMetricData;
import com.modeln.testfw.reporting.CMnMetricTable;
import com.modeln.testfw.reporting.CMnReleaseTable;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.servlet.*;
import javax.servlet.http.*;

import com.modeln.build.common.data.account.GroupData;
import com.modeln.build.common.data.account.UserData;
import com.modeln.build.common.database.LoginGroupTable;
import com.modeln.build.common.database.LoginTable;
import com.modeln.build.web.errors.ApplicationError;
import com.modeln.build.web.errors.ApplicationException;
import com.modeln.build.web.application.CommandResult;
import com.modeln.build.web.application.ProtectedCommand;
import com.modeln.build.web.application.WebApplication;
import com.modeln.build.web.database.RepositoryConnection;
import com.modeln.build.web.errors.ErrorMap;
import com.modeln.build.web.util.SessionUtility;


/**
 * This command allows a user to submit a service patch request. 
 * 
 * @author             Shawn Stafford
 */
public class CMnBasePatchRequest extends ProtectedCommand {



    /**
     * Augment the fix data by querying SDTracker for additional data.
     *
     * @param   app     Web application reference
     * @param   fixes   List of patch fixes
     */
    protected void getSDTrackerFixes(WebApplication app, Vector<CMnPatchFix> fixes)
        throws ApplicationException
    {
        if (fixes != null) {
            ApplicationException exApp = null;
            RepositoryConnection rc = null;

            CMnBugTable bugTable = CMnBugTable.getInstance();
            try {
                rc = ((CMnControlApp)app).getBugRepositoryConnection();
                if (rc.getConnection() != null) {
                    app.debug(rc);

                    for (int idx = 0; idx < fixes.size(); idx++) {
                        CMnPatchFix fix = (CMnPatchFix) fixes.get(idx);
                        CMnBug bug = bugTable.getBug(rc.getConnection(), Integer.toString(fix.getBugId()));
                        if ((bug != null) && (fix != null)) {
                            convertBug(bug, fix);
                        }
                    }

                } else {
                    app.debug("CMnBasePatchRequest: Failed to obtain connection to SDTracker database.");
                }
            } catch (Exception ex) {
                exApp = new ApplicationException(
                        ErrorMap.APPLICATION_DISPLAY_FAILURE,
                        "Failed to query SDTracker.");
                exApp.setStackTrace(ex);
            } finally {
                app.releaseRepositoryConnection(rc);

                // Throw any exceptions once the database connections have been cleaned up
                if (exApp != null) {
                    throw exApp;
                }
            }

        }
    }


    /**
     * Convenience method to determine if the request contains the
     * named parameter or attribute.
     *
     * @param   req    Servlet request
     * @param   name   Parameter or attribute name
     * @return  TRUE if the parameter or attribute exists
     */
    public boolean hasParameter(HttpServletRequest req, String name) {
        if (req.getParameter(name) != null) {
            return true;
        } else if (req.getAttribute(name) != null) {
            return true;
        } else {
            return false;
        } 
    }

    /**
     * Connect to the authentication database to populate the user data.
     * 
     * @param  conn       Database connection
     * @param  patch      Patch request
     * @return Number of user records updated
     */
    protected static synchronized int getPatchUserData(Connection conn, CMnPatch patch)
        throws SQLException
    {
        int count = 0;

        LoginTable lt = LoginTable.getInstance();

        if (patch != null) {
            // Query the account database for requestor detail
            if (patch.getRequestor() != null) {
                UserData requestor = lt.getUserByUid(conn, patch.getRequestor().getUid());
                if (requestor != null) {
                    patch.setRequestor(requestor);
                    count++;
                }
            }

            // Query the account database for owner detail
            if (patch.getOwner() != null) {
                UserData owner = lt.getUserByUid(conn, patch.getOwner().getUid());
                if (owner != null) {
                    patch.setOwner(owner);
                    count++;
                }
            }

            // Query the account database for notification detail
            if (patch.getNotifications() != null) {
                for (int idx = 0; idx < patch.getNotifications().size(); idx++) {
                    CMnPatchNotification notify = (CMnPatchNotification) patch.getNotifications().get(idx);
                    if (notify.getUserEmail() == null) {
                        UserData user = lt.getUserByUid(conn, notify.getUserId().toString()); 
                        String addr = user.getEmailAddress();
                        try {
                            notify.setUserEmail(new InternetAddress(addr));
                        } catch (AddressException addrex) {
                        }
                    }
                }
            }                    

        }

        return count;
    }


    /**
     * Connect to the authentication database to populate the user data.
     * 
     * @param  conn       Database connection
     * @param  patches    List of patch requests
     * @return Number of user records updated
     */
    protected static synchronized int getPatchUserData(Connection conn, Vector<CMnPatch> patches)
        throws SQLException
    {
        int count = 0;

        CMnPatch current = null;
        UserData owner = null;
        UserData requestor = null;

        if ((patches != null) && (patches.size() > 0)) {

            // Construct a list of user data objects
            Hashtable<String,UserData> users = new Hashtable<String,UserData>();
            for (int idx = 0; idx < patches.size(); idx++) {
                current = (CMnPatch) patches.get(idx);
                owner = current.getOwner();
                if ((owner != null) && !users.containsKey(owner.getUid())) {
                    users.put(owner.getUid(), owner);
                }
                requestor = current.getRequestor();
                if ((requestor != null) && !users.containsKey(requestor.getUid())) {
                    users.put(requestor.getUid(), requestor);
                }
            }

            // Populate the data objects with data from the database
            LoginTable lt = LoginTable.getInstance();
            count = lt.getUserData(conn, users);

            // Translate the populated data back into the containing objects
            for (int idx = 0; idx < patches.size(); idx++) {
                current = (CMnPatch) patches.get(idx);
                owner = current.getOwner();
                if (owner != null) {
                    current.setOwner(users.get(owner.getUid()));
                }
                requestor = current.getRequestor();
                if (requestor != null) {
                    current.setRequestor(users.get(requestor.getUid()));
                }
            }

        }

        return count;
    }

    /**
     * Connect to the authentication database to populate the user data.
     * 
     * @param  conn       Database connection
     * @param  approvers  List of approvers
     * @return Number of user records updated
     */
    protected static synchronized int getApproverUserData(Connection conn, Vector<CMnPatchApprover> approvers)
        throws SQLException
    {
        int count = 0;

        CMnPatchApprover current = null;
        UserData user = null;

        if ((approvers != null) && (approvers.size() > 0)) {
            // Construct a list of user data objects
            Hashtable<String,UserData> users = new Hashtable<String,UserData>();
            for (int idx = 0; idx < approvers.size(); idx++) {
                current = (CMnPatchApprover) approvers.get(idx);
                user = current.getUser();
                if (user != null) {
                    users.put(user.getUid(), user);
                }
            }

            // Populate the data objects with data from the database
            LoginTable lt = LoginTable.getInstance();
            count = lt.getUserData(conn, users);

            // Translate the populated data back into the containing objects
            for (int idx = 0; idx < approvers.size(); idx++) {
                current = approvers.get(idx);
                user = current.getUser();
                if (user != null) {
                    current.setUser(users.get(user.getUid()));
                }
            }
        }

        return count;
    }

    /**
     * Connect to the authentication database to populate the user data.
     * 
     * @param  conn       Database connection
     * @param  approvals  List of approvals
     * @return Number of user records updated
     */
    protected static synchronized int getApprovalUserData(Connection conn, Vector<CMnPatchApproval> approvals)
        throws SQLException
    {
        int count = 0;

        CMnPatchApproval current = null;
        UserData user = null;

        if ((approvals != null) && (approvals.size() > 0)) {

            // Construct a list of user data objects
            Hashtable<String,UserData> users = new Hashtable<String,UserData>();
            for (int idx = 0; idx < approvals.size(); idx++) {
                current = (CMnPatchApproval) approvals.get(idx);
                user = current.getUser();
                if (user != null) {
                    users.put(user.getUid(), user);
                }
            }

            // Populate the data objects with data from the database
            LoginTable lt = LoginTable.getInstance();
            count = lt.getUserData(conn, users);

            // Translate the populated data back into the containing objects
            for (int idx = 0; idx < approvals.size(); idx++) {
                current = approvals.get(idx);
                user = current.getUser();
                if (user != null) {
                    current.setUser(users.get(user.getUid()));
                }
            }

        }

        return count;
    }


    /**
     * Connect to the authentication database to populate the user data.
     * 
     * @param  conn       Database connection
     * @param  comments   List of comments
     * @return Number of user records updated
     */
    protected static synchronized int getCommentUserData(Connection conn, Vector<CMnPatchComment> comments)
        throws SQLException
    {
        int count = 0;

        CMnPatchComment current = null;
        UserData user = null;

        if ((comments != null) && (comments.size() > 0)) {

            // Construct a list of user data objects
            Hashtable<String,UserData> users = new Hashtable<String,UserData>();
            for (int idx = 0; idx < comments.size(); idx++) {
                current = (CMnPatchComment) comments.get(idx);
                user = current.getUser();
                if (user != null) {
                    users.put(user.getUid(), user);
                }
            }

            // Populate the data objects with data from the database
            LoginTable lt = LoginTable.getInstance();
            count = lt.getUserData(conn, users);

            // Translate the populated data back into the containing objects
            for (int idx = 0; idx < comments.size(); idx++) {
                current = comments.get(idx);
                user = current.getUser();
                if (user != null) {
                    current.setUser(users.get(user.getUid()));
                }
            }

        }

        return count;
    }



    /**
     * Get a list of approver groups associated with the patch.
     *
     * @param  app     Web application reference for retrieving database connections
     * @param  patch   Service patch information
     * @return List of approver groups
     */
    protected static Vector<CMnPatchApproverGroup> getApproverGroupsForBuild(
            WebApplication app, 
            CMnPatch patch) 
        throws ApplicationException
    {
        Vector<CMnPatchApproverGroup> approverGroups = null;

        CMnPatchTable patchTable = CMnPatchTable.getInstance(); 
        ApplicationException exApp = null;
        RepositoryConnection rc = null;
        RepositoryConnection ac = null;
        try {
            // Obtain a connection to the regular build database
            rc = app.getRepositoryConnection();
            ac = app.getAccountConnection();

            // Obtain the list of required approval groups
            if ((patch.getBuild() != null) && (patch.getBuild().getBuildVersion() != null)) {

                String buildString = patch.getBuild().getBuildVersion();

                // Create a list of the groups who can approve this patch request
                approverGroups = new Vector<CMnPatchApproverGroup>();

                // Get a list of approvers
                Vector<CMnPatchApprover> approvers = patchTable.getApproversForBuild(rc.getConnection(), buildString, patch.getStatus());
                if ((approvers != null) && (approvers.size() > 0)) {
                    app.debug("getApproverGroupsForBuild: Found " + approvers.size() + " approvers.");
                    int approverCount = getApproverUserData(ac.getConnection(), approvers);
                    app.debug("getApproverGroupsForBuild: Updated " + approverCount + " approver user records.");

                    // Translate the list of approvers to a list of corresponding groups
                    CMnPatchApprover currentApprover = null;
                    Vector<UserData> users = new Vector<UserData>();
                    Enumeration approverList = approvers.elements();
                    while (approverList.hasMoreElements()) {
                        currentApprover = (CMnPatchApprover) approverList.nextElement();
                        if (currentApprover.getUser() != null) {
                            users.add(currentApprover.getUser());
                        } else {
                            app.debug("getApproverGroupsForBuild: Current approver has a null user object.");
                        }
                    }
                    Vector<GroupData> groups = LoginGroupTable.getInstance().getGroups(ac.getConnection(), users);
                    app.debug("getApproverGroupsForBuild: Found " + groups.size() + " groups associated with the user list.");
                    GroupData currentGroup = null;
                    Enumeration groupList = groups.elements();
                    while (groupList.hasMoreElements()) {
                        currentGroup = (GroupData) groupList.nextElement();
                        // Construct a new approval group using the group ID
                        CMnPatchApproverGroup approverGroup = new CMnPatchApproverGroup();
                        approverGroup.setStatus(patch.getStatus());
                        approverGroup.setBuildVersion(buildString);
                        approverGroup.setGroup(currentGroup);
                        approverGroups.add(approverGroup);
                    }
                }
            } else {
                app.debug("Patch build information unavailable.  Unable to retrieve approver groups.");
            }
        } catch (ApplicationException aex) {
            exApp = aex;
        } catch (Exception ex) {
            exApp = new ApplicationException(
                ErrorMap.APPLICATION_DISPLAY_FAILURE,
                "Failed to process command.");
            exApp.setStackTrace(ex);
        } finally {
            app.releaseRepositoryConnection(rc);
            app.releaseRepositoryConnection(ac);

            // Throw any exceptions once the database connections have been cleaned up
            if (exApp != null) {
                throw exApp;
            }
        }


        return approverGroups; 
    }

    /**
     * Convert the SDTracker bug data into patch fix data.
     * Data is transferred from the bug data object to the
     * fix data object without overwriting any extraneous info.
     *
     * @param   bug   Source of the data
     * @param   fix   Destination for the data
     */
    protected void convertBug(CMnBug bug, CMnPatchFix fix) {
        fix.setBugId(bug.getId());

        // Only set the bug name if it differs from the bug number
        if ((bug.getNumber() != null) && 
            (bug.getVertical() != null) &&
            (!bug.getId().equals(bug.getNumber())))
        {
            fix.setBugName(bug.getVertical().toUpperCase() + "-" + bug.getNumber().toString());
        }

        // fix.setTitle(bug.getTitle());
        fix.setStatus(bug.getStatus());
        fix.setRelease(bug.getRelease());
        fix.setDescription(bug.getTitle());
        fix.setType(bug.getType());
        fix.setSubType(bug.getSubType());
        if (bug.getCheckIns() != null) {
            Enumeration<CMnCheckIn> clist = bug.getCheckIns().elements();
            while (clist.hasMoreElements()) {
                fix.addChangelist((CMnCheckIn) clist.nextElement());
            }
        }
    }
}
