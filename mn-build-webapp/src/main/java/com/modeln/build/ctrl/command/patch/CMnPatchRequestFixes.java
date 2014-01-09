/*
 * Login.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.ctrl.command.patch;

import com.modeln.build.common.data.product.CMnPatch;
import com.modeln.build.common.data.product.CMnPatchApprover;
import com.modeln.build.common.data.product.CMnPatchFix;
import com.modeln.build.common.data.product.CMnPatchGroup;
import com.modeln.build.common.enums.CMnServicePatch;
import com.modeln.build.common.tool.CMnPatchUtil;
import com.modeln.build.ctrl.database.CMnPatchTable;
import com.modeln.build.ctrl.forms.CMnBaseForm;
import com.modeln.build.ctrl.forms.IMnPatchForm;
import com.modeln.build.sdtracker.CMnBug;
import com.modeln.testfw.reporting.CMnBuildTable;
import com.modeln.testfw.reporting.CMnDbBuildData;
import com.modeln.build.common.data.account.UserData;
import com.modeln.build.common.database.LoginTable;
import com.modeln.build.web.errors.ApplicationError;
import com.modeln.build.web.errors.ApplicationException;
import com.modeln.build.web.application.CommandResult;
import com.modeln.build.web.application.WebApplication;
import com.modeln.build.web.database.RepositoryConnection;
import com.modeln.build.web.errors.ErrorMap;
import com.modeln.build.web.util.SessionUtility;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;


/**
 * This command allows a user to select the list of bugs to apply in the patch.
 *
 * @author             Shawn Stafford
 */
public class CMnPatchRequestFixes extends CMnBasePatchFixes {

    /** Default prefix for jobs */
    private static final String JOB_PREFIX = "SDRLS";

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

        // Execute the actions for the command
        if (!result.containsError()) {
            // Get the login information from the session
            UserData user = SessionUtility.getLogin(req.getSession());

            ApplicationException exApp = null;
            ApplicationError error = null;
            RepositoryConnection rc = null;
            RepositoryConnection ac = null;
            try {
                rc = app.getRepositoryConnection();
                ac = app.getAccountConnection();
                CMnPatchTable patchTable = CMnPatchTable.getInstance();
                app.debug("CMnPatchRequestFixes: obtained a connection to the build database");

                // Keep track of field validation errors
                Hashtable<String,String> inputErrors = new Hashtable<String,String>();

                // Fall back to the request attributes in case the data was set by another command
                String patchId = (String) req.getParameter(IMnPatchForm.PATCH_ID_LABEL);
                if (patchId == null) {
                    patchId = (String) req.getAttribute(IMnPatchForm.PATCH_ID_LABEL);
                }
                String custId = (String) req.getParameter(IMnPatchForm.CUSTOMER_ID_LABEL);
                if (custId == null) {
                    custId = (String) req.getAttribute(IMnPatchForm.CUSTOMER_ID_LABEL);
                }
                String envId = (String) req.getParameter(IMnPatchForm.ENV_ID_LABEL);
                if (envId == null) {
                    envId = (String) req.getAttribute(IMnPatchForm.ENV_ID_LABEL);
                }
                String buildId = (String) req.getParameter(IMnPatchForm.BUILD_ID_LABEL);
                if (buildId == null) {
                    buildId = (String) req.getAttribute(IMnPatchForm.BUILD_ID_LABEL);
                }
                String basePatchId = (String) req.getParameter(IMnPatchForm.BASE_PATCH_LABEL);
                if (basePatchId == null) {
                    basePatchId = (String) req.getAttribute(IMnPatchForm.BASE_PATCH_LABEL);
                }
                String userText = (String) req.getParameter(IMnPatchForm.JUSTIFY_LABEL);
                if (userText == null) {
                    userText = (String) req.getAttribute(IMnPatchForm.JUSTIFY_LABEL);
                }

                // Collect the list of selected SDRs
                Vector<String> fixlist = null; 
                String[] fixvalues = req.getParameterValues(IMnPatchForm.FIX_LIST);
                if (fixvalues == null) {
                    fixlist = (Vector) req.getAttribute(IMnPatchForm.FIX_LIST);
                    if (fixlist != null) {
                        app.debug("CMnPatchRequestFixes: Found " + fixlist.size() + " fixes in the session.");
                    } else {
                        app.debug("CMnPatchRequestFixes: No fixes found in request parameter or session attribute.");
                    }
                } else {
                    // Initialize the vector with the parameter values
                    fixlist = new Vector<String>(fixvalues.length);
                    for (int idx = 0; idx < fixvalues.length; idx++) {
                        fixlist.add(fixvalues[idx]);
                    } 
                    app.debug("CMnPatchRequestFixes:  Found " + fixlist.size() + " fixes in the request.");
                }

                // Determine if the user has submitted the information
                // Don't fall back to the request attributes because we don't want to
                // consider this field if the request was forwarded from another command
                String requestSubmitValue = (String) req.getParameter(IMnPatchForm.PATCH_REQUEST_BUTTON);
                String fixSubmitValue = (String) req.getParameter(IMnPatchForm.PATCH_FIXES_BUTTON);

                // Remove the patch list from the session in case the request was forward from another page
                Vector previousPatches = (Vector) req.getAttribute(IMnPatchForm.PATCH_LIST_DATA);
                if (previousPatches != null) {
                    req.removeAttribute(IMnPatchForm.PATCH_LIST_DATA);
                    if (req.getAttribute(IMnPatchForm.PATCH_LIST_DATA) != null) {
                        app.debug("CMnPatchRequestFixes: failed to remove previous patches from the request attribute");
                    } else {
                        app.debug("CMnPatchRequestFixes: removed " + previousPatches.size() + " previous patches from the request attribute");
                    }
                }

                // Determine if the user has submitted the information
                // Don't fall back to the request attributes because we don't want to 
                // consider this field if the request was forwarded from another command
                // If submitting fixes, the patch will be updated without changing the state
                // If submitting for approval, the patch will move to the next state to wait for approval 
                String submitFixesValue = (String) req.getParameter(IMnPatchForm.PATCH_FIXES_BUTTON);
                String submitPatchValue = (String) req.getParameter(IMnPatchForm.PATCH_APPROVAL_BUTTON);



                // Obtain patch data from the database or session
                CMnPatch patch = null;
                if ((patchId != null) && (patchId.trim().length() > 0)) {
                    patch = patchTable.getRequest(rc.getConnection(), patchId, true);
                    app.debug("CMnPatchRequestFixes: obtained patch object from the database: id = " + patch.getId());
                    req.setAttribute(IMnPatchForm.PATCH_DATA, patch);
                } else {
                    patch = (CMnPatch) req.getAttribute(IMnPatchForm.PATCH_DATA);
                    if (patch != null) {
                        app.debug("CMnPatchRequestFixes: obtained patch object from the request attribute: id = " + patch.getId());
                        patchId = patch.getId().toString();
                    }
                }

                // Get the list of fix groups
                if ((patch != null) && (patch.getBuild() != null)) {
                    // Convert the full build version to a generic version string 
                    // that identifies all builds for that release
                    String version = patch.getBuild().getBuildVersion();
                    version = CMnPatchUtil.getShortVersion(version); 

                    Vector<CMnPatchGroup> fixGroups = patchTable.getFixGroups(rc.getConnection(), version);
                    if ((fixGroups != null) && (fixGroups.size() > 0)) {
                        req.setAttribute(IMnPatchForm.FIX_GROUP_LIST_DATA, fixGroups);
                    }
                }

                // Make sure the user information is fully populated
                if (patch != null) {
                    getPatchUserData(ac.getConnection(), patch);
                }


                // Use the data from the patch object if user input was not provided
                if (patch != null) {
                    if (custId == null)      custId  = patch.getCustomer().getId().toString();
                    if (envId == null)       envId   = patch.getEnvironment().getId().toString();
                    if (buildId == null)     buildId = Integer.toString(patch.getBuild().getId());
                    if (userText == null)    userText = patch.getJustification();
                } else {
                    app.debug("CMnPatchRequestFixes: unable to obtain patch object data");
                }


                // Obtain base patch information from database
                CMnPatch basePatch = null;
                Vector<CMnPatchFix> baseFixes = null;
                if (basePatchId != null) {
                    app.debug("CMnPatchRequestFixes: obtaining base patch information from database");
                    basePatch = patchTable.getRequest(rc.getConnection(), basePatchId, true);
                } else {
                    app.debug("CMnPatchRequestFixes: setting base patch information equal to current patch");
                    basePatch = patch;
                    basePatchId = patchId;
                }
                // Obtain a list of fixes that were applied to the base patch
                baseFixes = patchTable.getFixes(rc.getConnection(), basePatchId, true);
                app.debug("CMnPatchRequestFixes: obtained " + baseFixes.size() + " base fixes");
                // Update the base fixes with bug information
                getSDTrackerFixes(app, baseFixes);
                // Update the fixes with fix origin information
                setOrigin(basePatch, baseFixes, false);


                // Obtain available fix information
                CMnDbBuildData build = null;
                Vector<CMnPatchFix> availableFixes = null;
                if (buildId != null) {
                    // Obtain detailed build information that will be used to get a list of available fixes
                    build = CMnBuildTable.getBuild(rc.getConnection(), buildId);
                    app.debug("CMnPatchRequestFixes: obtained build data");

                    // Create a mapping between the requested fixes and the source control fixes
                    boolean strict = true;
                    if (!patch.getForExternalUse()) {
                        strict = false;
                    }
                    availableFixes = getSourceFixes(app, build, strict);
                    app.debug("CMnPatchRequestFixes: available fixes = " + getFixesAsString(availableFixes));

                    // Update the fixes with fix origin information
                    if ((basePatch != null) && (availableFixes != null)) {
                        setOrigin(basePatch, availableFixes, false);
                    }
                } else {
                    app.debug("CMnPatchRequestFixes: unable to obtain available fixes due to missing build ID.");
                }


                // Store the list of fixes in the session so they can be displayed to the user
                int count = setFixes(req, availableFixes, baseFixes);
                app.debug("CMnPatchRequestFixes: finished setting " + count + " base and available fixes in request");

                // Continue with the request if all parameters have been provided
                if ((inputErrors.size() == 0) &&
                    (patchId != null) && (patchId.length() > 0) &&
                    (custId != null) && (custId.length() > 0) &&
                    (envId != null) && (envId.length() > 0) &&
                    (buildId != null) && (buildId.length() > 0) &&
                    (userText != null) && (userText.length() > 0))
                {
                    // Determine what to display to the user
                    if (submitFixesValue != null) {
                        // Parse the list of fixes submitted by the user (checkboxes)
                        Vector fixes = new Vector();
                        if ((fixlist != null) && (fixlist.size() > 0)) {
                            int idx = 0;
                            StringBuffer msgBuffer = new StringBuffer();
                            Enumeration fenum = fixlist.elements();
                            while (fenum.hasMoreElements()) {
                                String bugIdStr = (String) fenum.nextElement();
                                String exclusions = req.getParameter(IMnPatchForm.FIX_EXCLUDE_PREFIX + bugIdStr);
                                String notes = req.getParameter(IMnPatchForm.FIX_NOTE_PREFIX + bugIdStr);
                                String scmRoot = req.getParameter(IMnPatchForm.FIX_BRANCH_PREFIX + bugIdStr);
                                String origin = req.getParameter(IMnPatchForm.FIX_ORIGIN_PREFIX + bugIdStr);
                                int bugId = Integer.parseInt(bugIdStr);
                                idx++;

                                // Construct a patch object from the user input values for the current fix
                                CMnPatchFix fix = new CMnPatchFix();
                                fix.setBugId(bugId);
                                if ((exclusions != null) && (exclusions.length() > 0) && (!exclusions.equalsIgnoreCase("null"))) {
                                    fix.setExclusions(exclusions);
                                }
                                if ((notes != null) && (notes.length() > 0) && (!notes.equalsIgnoreCase("null"))) {
                                    fix.setNotes(notes);
                                }
                                if ((scmRoot != null) && (scmRoot.length() > 0) && (!scmRoot.equalsIgnoreCase("null"))) {
                                    fix.setVersionControlRoot(scmRoot);
                                }
                                if (origin != null) {
                                    try {
                                        CMnPatch oldPatch = new CMnPatch();
                                        oldPatch.setId(new Integer(origin));
                                        fix.setOrigin(oldPatch);
                                    } catch (NumberFormatException nfex) {
                                        app.debug("CMnPatchRequestFixes: error parsing origin patch ID " + origin + ": " + nfex.getMessage());
                                    }
                                } else {
                                    fix.setOrigin(patch);
                                }

                                // Removed: Validate the fix to ensure it can be added to the patch
                                String fixCountStr = idx + " of " + fixlist.size();
                                if ((scmRoot != null) && (scmRoot.length() > 0)) {
                                    app.debug("CMnPatchRequestFixes: adding fix " + fixCountStr + " to the list: " + bugId + ", " + scmRoot); 
                                    fixes.add(fix);
                                } else if (hasFix(bugId, baseFixes)) {
                                    app.debug("CMnPatchRequestFixes: adding base fix " + fixCountStr + " to the list: " + bugId);
                                    fixes.add(fix);
                                } else if (hasFix(bugId, availableFixes)) {
                                    app.debug("CMnPatchRequestFixes: adding available fix " + fixCountStr + " to the list: " + bugId);
                                    fixes.add(fix);
                                } else {
                                    app.debug("CMnPatchRequestFixes: Fix " + fixCountStr + " not in list of available fixes.  Discarding fix: " + bugId);
                                    if (msgBuffer.length() > 0) {
                                        msgBuffer.append(",");
                                    }
                                    msgBuffer.append(bugIdStr);
                                }

                            }
                            if (msgBuffer.length() > 0) {
                                app.debug("CMnPatchRequestFixes: User has entered invalid fixes: " + msgBuffer.toString());
                                inputErrors.put(IMnPatchForm.FIX_LIST, "Invalid fixes: " + msgBuffer.toString());
                            }
                        }

                        // Determine if the bulk fixes come from a specific branch
                        String bulkBranch = (String) req.getParameter(IMnPatchForm.BULK_FIX_BRANCH);
                        if (bulkBranch == null) {
                            bulkBranch = (String) req.getAttribute(IMnPatchForm.BULK_FIX_BRANCH);
                        }

                        // Parse the list of fixes submitted by the user (comma-delmited list)
                        String bulkFixes = (String) req.getParameter(IMnPatchForm.BULK_FIX_LIST);
                        if (bulkFixes == null) {
                            bulkFixes = (String) req.getAttribute(IMnPatchForm.BULK_FIX_LIST);
                        }
                        if ((bulkFixes != null) && (bulkFixes.trim().length() > 0)) {
                            StringBuffer msgBuffer = new StringBuffer();
                            StringTokenizer st = new StringTokenizer(bulkFixes, ",");
                            while (st.hasMoreTokens()) {
                                boolean bulkIdFound = false;
                                String bulkBugIdStr = st.nextToken();
                                try {
                                    // Validate the list of bulk bugs
                                    int bulkBugId = Integer.parseInt(bulkBugIdStr.trim());
                                    if ((bulkBranch != null) && (bulkBranch.length() > 0)) {
                                        // Don't bother to validate bulk fixes when a branch is provided
                                        CMnPatchFix bulkFix = new CMnPatchFix();
                                        bulkFix.setBugId(bulkBugId);
                                        bulkFix.setVersionControlRoot(bulkBranch);
                                        //when parsing the csv separated fixes from text input,
                                        //we need to set the origin.
                                        bulkFix.setOrigin(patch);
                                        fixes.add(bulkFix);
                                    } else if (hasFix(bulkBugId, baseFixes) || hasFix(bulkBugId, availableFixes)) {
                                        // Iterate through the existing fixes to see if it was already selected
                                        Enumeration f = fixes.elements();
                                        while (f.hasMoreElements()) {
                                            CMnPatchFix currentFix = (CMnPatchFix) f.nextElement();
                                            if (currentFix.getBugId() == bulkBugId) {
                                                bulkIdFound = true;
                                            }
                                        }

                                        // Add the bulk ID to the fix list
                                        if (!bulkIdFound) {
                                            CMnPatchFix bulkFix = new CMnPatchFix();
                                            bulkFix.setBugId(bulkBugId);
                                            bulkFix.setVersionControlRoot(bulkBranch);
                                            //when parsing the csv separated fixes from text input,
                                            //we need to set the origin.
                                            bulkFix.setOrigin(patch);
                                            fixes.add(bulkFix);
                                        }
                                    } else {
                                        app.debug("CMnPatchRequestFixes: Discarded invalid fix from bulk fix list: " + bulkBugIdStr);

                                        // TODO:  Try to get meaningful validation information for the user
                                        CMnBug bug = getSDTrackerBug(app, bulkBugIdStr);
                                        if (bug != null) {
                                            if ((build.getStartTime() != null) && 
                                                (bug.getResolveDate() != null) && 
                                                (build.getStartTime().after(bug.getResolveDate()))) 
                                            {
                                                msgBuffer.append(bulkBugIdStr + " - Date Resolved indicates fix already in product\n");
                                            } else if ((build.getBuildVersion() != null) && (bug.getRelease() != null)) {
                                                String fixVerNum = bug.getRelease();
                                                String buildVerNum = CMnPatchUtil.getVersionNumber(build.getBuildVersion());
                                                String family = getReleaseFamily(CMnPatchUtil.getVersionNumber(buildVerNum));
                                                if (!bug.hasCheckIns()) {
                                                    msgBuffer.append(bulkBugIdStr + " - No check-ins found\n");
                                                } else if (!fixVerNum.startsWith(family)) {
                                                    msgBuffer.append(bulkBugIdStr + " - Fixed in the " + fixVerNum + " release (not in the " + family + " family)\n");
                                                } else if (bug.getResolveDate() == null) {
                                                    msgBuffer.append(bulkBugIdStr + " - SDR Date Resolved is invalid\n");
                                                } else {
                                                    msgBuffer.append(bulkBugIdStr + " - Invalid SDR\n");  
                                                }
                                            } else if (bug.getRelease() == null) {
                                                msgBuffer.append(bulkBugIdStr + " - SDR Target Release is invalid\n");
                                            } else {
                                                msgBuffer.append(bulkBugIdStr + " - Invalid build version\n");
                                            }
                                        } else {
                                            msgBuffer.append(bulkBugIdStr + " - Unable to locate SDR\n");
                                        }

                                    }
                                } catch (NumberFormatException nfex) {
                                    app.debug("CMnPatchRequestFixes: Unable to format bulk ID as a number: " + bulkBugIdStr);
                                    msgBuffer.append(bulkBugIdStr + " - C'mon, that isn't even a number!\n");
                                }
                            }
                            if (msgBuffer.length() > 0) {
                                inputErrors.put(IMnPatchForm.BULK_FIX_LIST, msgBuffer.toString());
                            }
                        }

                        // Perform the command data updates if all data is valid
                        if (inputErrors.size() == 0) {
                            // Send the user to command which deletes the Jenkins job
                            result = app.forwardToCommand(req, res, "/patch/CMnDeleteJob");

                            // Delete any previous approvals since the approval process must start over
                            int approvalCount = patchTable.deleteApprovals(rc.getConnection(), patchId);

                            // Delete the existing fixes and add the updated fixes 
                            boolean addOk = true;
                            int deleteCount = patchTable.deleteFixes(rc.getConnection(), patchId);
                            app.debug("CMnPatchRequestFixes: deleted " + deleteCount + " fixes for Patch ID " + patchId);
                            if (deleteCount >= 0) {
                                patch.setFixes(null);
                                Enumeration e = fixes.elements();
                                while (e.hasMoreElements()) {
                                    CMnPatchFix currentFix = (CMnPatchFix) e.nextElement();

                                    // Update the fix to record the origin of the fix
                                    // The basePatchId will be set to the base patch selection when 
                                    // requesting a patch OR to the current patch ID if the user is 
                                    // editing an existing patch
                                    if ((currentFix.getOrigin() == null) && (basePatch != null)) {
                                        app.debug("CMnPatchRequestFixes: setting fix origin to base patch: bug=" + currentFix.getBugId() + ", base=" + basePatch.getId());
                                        currentFix.setOrigin(basePatch);
                                    }

                                    // Print a debug message
                                    StringBuffer sbDebug = new StringBuffer();
                                    sbDebug.append("CMnPatchRequestFixes: adding fix to database: ");
                                    sbDebug.append("patch=" + patchId);
                                    sbDebug.append(", bug=" + currentFix.getBugId());
                                    if (currentFix.getOrigin() != null) {
                                        sbDebug.append(", origin =" + currentFix.getOrigin().getId());
                                    } else {
                                        sbDebug.append(", no origin");
                                    }
                                    app.debug(sbDebug.toString());
                                    boolean currentAddOk = patchTable.addFix(rc.getConnection(), patchId, currentFix);
                                    if (currentAddOk) {
                                        patch.addFix(currentFix);
                                    } else {
                                        addOk = false;
                                    }
                                }
                            } // deleteCount >= 0

                            // Update the session with the list of fixes
                            setFixes(req, null, patch.getFixes());

                            // Move the request back to the initial request status
                            boolean updateOk = patchTable.updateRequestStatus(rc.getConnection(), patchId, user.getUid(), CMnServicePatch.RequestStatus.SAVED);

                            //call notify users
                            sendEmailViaNotifyUsersAPI(updateOk, app, req, patch, error);

                            // TODO: the app throws errors if forwarded to patch_request.jsp
                            // Something is going wrong with the session data constructed
                            // during the flow from CMnPatchRequest -> CMnPatchRequestFixes
                            result.setDestination("patch/patch.jsp");

                        } // input errors

                    } else if (submitPatchValue != null) {

                        // Update the patch to wait for approval, or move to next state if no approvers
                        patch.setStatus(CMnServicePatch.RequestStatus.APPROVAL);
                        Vector<CMnPatchApprover> approvers = patchTable.getApproversForBuild(rc.getConnection(), patch.getBuild().getBuildVersion(), patch.getStatus());
                        if (approvers.size() > 0) {
                            try {
                                // Populate the approver user information
                                getApproverUserData(ac.getConnection(), approvers);

                                // Send e-mail notification to the approvers
                                CMnEmailNotification.notifyApprovers(app, req, patch, approvers);
                            } catch (SQLException sqlex) {
                                app.log("Failed to retrieve notification data from account database.");
                            } catch (ApplicationException apex) {
                                ApplicationError mailError = app.getError(apex);
                                if (error == null) {
                                    error = mailError;
                                }
                                app.log(mailError);
                            }
                        } else {
                            // Send the user to command which runs the Jenkins job
                            //result = app.forwardToCommand(req, res, "/patch/CMnRunJob");
                            patch.setStatus(CMnServicePatch.RequestStatus.PENDING);
                        }

                        // Notify users of the status change
                        boolean updateOk = patchTable.updateRequestStatus(rc.getConnection(), patchId, user.getUid(), patch.getStatus());

                        //call notify users
                        sendEmailViaNotifyUsersAPI(updateOk, app, req, patch, error);

                        result.setDestination("patch/patch.jsp");
                    } else if ((basePatchId != null) && (basePatchId.length() > 0)) {
                        // User is ready to select fixes for the patch
                        result.setDestination("patch/patch_fixes.jsp");
                    } else {
                        // There are no previous patches to build on
                        result.setDestination("patch/patch_fixes.jsp");
                        app.debug("CMnPatchRequestFixes: finished setting " + count + " base fixes in request");
                    }

                } else {
                    // Log a debug message so we can figure out what parameter was missing
                    if ((patchId == null) || (patchId.length() < 1)) {
                        app.debug("CMnPatchRequestFixes: missing value: patchId = " + patchId);
                    } else if ((custId == null) || (custId.length() < 0)) {
                        app.debug("CMnPatchRequestFixes: missing value: custId = " + custId);
                    } else if ((envId == null) || (envId.length() < 0)) {
                        app.debug("CMnPatchRequestFixes: missing value: envId = " + envId);
                    } else if ((buildId == null) || (buildId.length() < 0)) {
                        app.debug("CMnPatchRequestFixes: missing value: buildId = " + buildId);
                    } else if ((userText == null) || (userText.length() < 0)) {
                        app.debug("CMnPatchRequestFixes: missing value: userText = " + userText);
                    }

                    // Send the user back to the input form to gather additional info
                    result.setDestination("patch/patch_fixes.jsp");
                }

                // We only care about displaying validation errors to the user
                // if they have submitted data using the submit button
                if ((requestSubmitValue != null) && (inputErrors.size() > 0)) {
                    // Add the patch list back to the session so that errors can be corrected 
                    req.setAttribute(IMnPatchForm.PATCH_LIST_DATA, previousPatches);

                    // Add the input errors to the session
                    app.debug("CMnPatchRequestFixes:  Adding " + inputErrors.size() + " validation errors to the session.");
                    req.setAttribute(CMnBaseForm.INPUT_ERROR_DATA, inputErrors);

                    result.setDestination("patch/patch_request.jsp");
                    app.debug("CMnPatchRequestFixes: Input errors detected.  Sending user back to patch request page.");
                } else if ((fixSubmitValue != null) && (inputErrors.size() > 0)) {
                    // Add the selected fixes back to the session
                    if (fixlist != null) {
                        app.debug("CMnPatchRequestFixes:  Adding " + fixlist.size() + " selected fixes to the session.");
                        req.setAttribute(IMnPatchForm.FIX_LIST, fixlist);

                        // If the user has submitted a list of fixes then we want to make sure
                        // we pass that along as base fixes when sending the user back to the
                        // previous page otherwise that input will be lost
                        req.setAttribute(IMnPatchForm.BASE_PATCH_LABEL, basePatchId);

                    } else {
                        app.debug("CMnPatchRequestFixes:  No selected fixes added to the session.");
                    }

                    // Try to debug the existence of the base and available fixes
                    Vector bf = (Vector) req.getAttribute("BASE_FIXES");
                    if (bf != null) {
                        app.debug("CMnPatchRequestFixes: Found " + bf.size() + " base fixes in the request attribute.");
                    } else {
                        app.debug("CMnPatchRequestFixes: No base fixes found in the request attribute.");
                    }
                    Vector af = (Vector) req.getAttribute("AVAILABLE_FIXES");
                    if (af != null) {
                        app.debug("CMnPatchRequestFixes: Found " + af.size() + " available fixes in the request attribute.");
                    } else {
                        app.debug("CMnPatchRequestFixes: No available fixes found in the request attribute.");
                    }


                    // Add the input errors to the session
                    req.setAttribute(CMnBaseForm.INPUT_ERROR_DATA, inputErrors);

                    result.setDestination("patch/patch_fixes.jsp");
                    app.debug("CMnPatchRequestFixes: Input errors detected.  Sending user back to patch fixes page.");
                } else if (inputErrors.size() > 0) {
                    app.debug("CMnPatchRequestFixes: Input errors detected but no submit button was pressed.");
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
                    app.debug("CMnPatchRequestFixes: Preparing to throw application exception: " + exApp.getMessage());
                    throw exApp;
                }
            }
        }

        return result;
    }

    private void sendEmailViaNotifyUsersAPI(boolean updateOk, WebApplication app, HttpServletRequest req, CMnPatch patch, ApplicationError error ) {
        if (updateOk) {
            try {
                CMnEmailNotification.notifyUsers(app, req, patch);
            } catch (ApplicationException apex) {
                ApplicationError mailError = app.getError(apex);
                if (error == null) {
                    error = mailError;
                }
                app.log(mailError);
            }
        }
    }


}
