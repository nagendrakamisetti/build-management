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
import com.modeln.build.common.data.product.CMnPatchApproval;
import com.modeln.build.common.data.product.CMnPatchApprover;
import com.modeln.build.common.data.product.CMnPatchApproverGroup;
import com.modeln.build.common.data.product.CMnProduct;
import com.modeln.build.common.database.CMnCustomerTable;
import com.modeln.build.common.enums.CMnServicePatch;
import com.modeln.build.common.tool.CMnPatchUtil;
import com.modeln.build.ctrl.CMnControlApp;
import com.modeln.build.ctrl.database.CMnPatchTable;
import com.modeln.build.ctrl.forms.CMnBaseForm;
import com.modeln.build.ctrl.forms.IMnPatchForm;
import com.modeln.build.jenkins.Build;
import com.modeln.build.jenkins.Job;
import com.modeln.build.jenkins.XmlApi;
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
import com.modeln.build.web.errors.ApplicationError;
import com.modeln.build.web.errors.ApplicationException;
import com.modeln.build.web.application.CommandResult;
import com.modeln.build.web.application.ProtectedCommand;
import com.modeln.build.web.application.WebApplication;
import com.modeln.build.web.database.RepositoryConnection;
import com.modeln.build.web.errors.ErrorMap;
import com.modeln.build.web.util.SessionUtility;


/**
 * This utility class provides common functionality for managing 
 * approval data. 
 *
 * @author             Shawn Stafford
 */
public class CMnApprovals extends CMnBasePatchRequest {

    /**
     * This is the primary method which will be used to perform the command
     * actions.  The application will use this method to service incoming
     * requests.  You must pass a reference to the calling application into
     * the service method to allow callback method calls to be performed.
     *
     * @param   app     Application which called the command
     * @param   req     HttpServletRequest object
     * @param   res     HttpServletResponse object
     */
    public CommandResult execute(WebApplication app, HttpServletRequest req, HttpServletResponse res)
        throws ApplicationException
    {
        // Execute the generic actions for all commands
        CommandResult result = super.execute(app, req, res);

        return result;
    }


    /**
     * Query the database for approval information based on the current
     * patch status.  This should be executed just prior to page display
     * to ensure that the patch status is not updated by an intermediate
     * action or command.
     *
     */
    protected static void setApprovalData(WebApplication app, HttpServletRequest req, HttpServletResponse res, String patchId)
        throws ApplicationException
    {
        CMnPatchTable patchTable = CMnPatchTable.getInstance();
        ApplicationException exApp = null;
        ApplicationError error = null;
        RepositoryConnection rc = null;
        RepositoryConnection ac = null;
        try {
            // Obtain a connection to the regular build database
            rc = app.getRepositoryConnection();
            ac = app.getAccountConnection();

            // Query the patch data rather than passing it in just in case the object data is outdated
            CMnPatch patch = patchTable.getRequest(rc.getConnection(), patchId, false);

            // Obtain a list of approvals
            Vector<CMnPatchApproval> approvals = patchTable.getApprovals(rc.getConnection(), patchId);
            if ((approvals != null) && (approvals.size() > 0)) {
                getApprovalUserData(ac.getConnection(), approvals);
                app.debug("CMnApprovals: adding " + approvals.size() + " patch approval entries to the session.");
                req.setAttribute(IMnPatchForm.APPROVAL_LIST_DATA, approvals);
            } else {
                app.debug("CMnApprovals: no approvals found for patch ID# " + patchId);
            }

            // Obtain the list of required approval groups
            Vector<CMnPatchApproverGroup> approverGroups = getApproverGroupsForBuild(app, patch);
            if ((approverGroups != null) && (approverGroups.size() > 0)) {
                app.debug("CMnPatchRequest: adding " + approverGroups.size() + " approval groups to the session.");
                req.setAttribute(IMnPatchForm.APPROVER_GROUP_DATA, approverGroups);
            } else {
                app.debug("CMnPatchRequest: no approval groups found for patch ID# " + patchId);
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

    }



}
