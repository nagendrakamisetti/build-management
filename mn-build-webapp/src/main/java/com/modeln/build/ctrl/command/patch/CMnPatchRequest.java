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
import com.modeln.build.common.data.product.CMnPatchOwner;
import com.modeln.build.common.data.product.CMnProduct;
import com.modeln.build.common.database.CMnCustomerTable;
import com.modeln.build.common.enums.CMnServicePatch;
import com.modeln.build.common.tool.CMnPatchUtil;
import com.modeln.build.ctrl.CMnControlApp;
import com.modeln.build.ctrl.database.CMnPatchTable;
import com.modeln.build.ctrl.database.CMnReviewTable;
import com.modeln.build.ctrl.forms.CMnBaseForm;
import com.modeln.build.ctrl.forms.CMnPatchOwnerForm;
import com.modeln.build.ctrl.forms.IMnBuildForm;
import com.modeln.build.ctrl.forms.IMnPatchForm;
import com.modeln.build.jenkins.Job;
import com.modeln.build.jenkins.XmlApi;
import com.modeln.testfw.reporting.CMnBuildTable;
import com.modeln.testfw.reporting.CMnFeatureOwnerTable;
import com.modeln.testfw.reporting.CMnDbBuildData;
import com.modeln.testfw.reporting.CMnMetricTable;
import com.modeln.testfw.reporting.CMnReleaseTable;
import com.modeln.build.common.data.account.UserData;
import com.modeln.build.common.data.account.GroupData;
import com.modeln.build.common.database.LoginTable;
import com.modeln.build.web.errors.ApplicationError;
import com.modeln.build.web.errors.ApplicationException;
import com.modeln.build.web.application.CommandResult;
import com.modeln.build.web.application.WebApplication;
import com.modeln.build.web.database.RepositoryConnection;
import com.modeln.build.web.errors.ErrorMap;
import com.modeln.build.web.util.SessionUtility;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;


/**
 * This command allows a user to submit a service patch request. 
 * 
 * @author             Shawn Stafford
 */
public class CMnPatchRequest extends CMnBasePatchRequest {

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
            ApplicationException exApp = null;
            ApplicationError error = null;
            RepositoryConnection rc = null;
            RepositoryConnection ac = null;
            RepositoryConnection pc = null;
            try {
                rc = app.getRepositoryConnection();
                ac = app.getAccountConnection();
                pc = ((CMnControlApp) app).getPatchRepositoryConnection();
                LoginTable loginTable = LoginTable.getInstance();
                CMnBuildTable buildTable = CMnBuildTable.getInstance();
                CMnPatchTable patchTable = CMnPatchTable.getInstance();
                CMnCustomerTable custTable = CMnCustomerTable.getInstance();
                CMnReviewTable reviewTable = CMnReviewTable.getInstance();
                app.debug("CMnPatchRequest: obtained a connection to the build database");

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
                String custUse = (String) req.getParameter(IMnPatchForm.PATCH_USE_LABEL);
                if (custUse == null) {
                    custUse = (String) req.getAttribute(IMnPatchForm.PATCH_USE_LABEL);
                }
                String patchName = (String) req.getParameter(IMnPatchForm.PATCH_NAME_LABEL);
                if (patchName == null) {
                    patchName = (String) req.getAttribute(IMnPatchForm.PATCH_NAME_LABEL);
                }
                String notification = (String) req.getParameter(IMnPatchForm.NOTIFY_LABEL);
                if (notification == null) {
                    notification = (String) req.getAttribute(IMnPatchForm.NOTIFY_LABEL);
                }
                String justification = (String) req.getParameter(IMnPatchForm.JUSTIFY_LABEL);
                if (justification == null) {
                    justification = (String) req.getAttribute(IMnPatchForm.JUSTIFY_LABEL);
                }
                String basePatchId = (String) req.getParameter(IMnPatchForm.BASE_PATCH_LABEL);
                if (basePatchId == null) {
                    basePatchId = (String) req.getAttribute(IMnPatchForm.BASE_PATCH_LABEL);
                }


                // Obtain the patch owner information from the database
                Vector<UserData> users = loginTable.getUsers(ac.getConnection());
                Hashtable<String, String> userhash = null;
                if (users != null) {
                    app.debug("CMnPatchRequest: Returned " + users.size() + " users from the account database.");
                    userhash = new Hashtable<String, String>(users.size());
                    Enumeration userlist = users.elements();
                    while (userlist.hasMoreElements()) {
                        UserData user = (UserData) userlist.nextElement();
                        if (user.isAdmin()) {
                            String uid = user.getUid();
                            StringBuffer name = new StringBuffer();
                            if (user.getFirstName() != null) {
                                name.append(user.getFirstName());
                            }
                            if (user.getMiddleName() != null) {
                                name.append(" " + user.getMiddleName());
                            }
                            if (user.getLastName() != null) {
                                name.append(" " + user.getLastName());
                            }
                            userhash.put(uid, name.toString());
                        }
                    }
                    req.setAttribute(CMnPatchOwnerForm.USER_LIST, userhash);
                } else {
                    app.debug("CMnPatchRequest: No users found in the account database.");
                }


                // Determine if the user has submitted the information
                // Don't fall back to the request attributes because we don't want to
                // consider this field if the request was forwarded from another command
                String submitValue = (String) req.getParameter(IMnPatchForm.PATCH_REQUEST_BUTTON);

                // Validate the user input
                if ((patchName != null) && (!patchName.matches("SP[0-9]+"))) {
                    inputErrors.put(IMnPatchForm.PATCH_NAME_LABEL, "Invalid patch name.  Format must match 'SP#'");
                }
                if (notification != null) {
                    try {
                        InternetAddress[] list = InternetAddress.parse(notification);
                        // further validate that the addresses contain a domain
                        if (list != null) {
                            StringBuffer badstr = new StringBuffer();
                            for (int idx = 0; idx < list.length; idx++) {
                                // Perform validation of the address in addition to 
                                // RF822 syntax (which allows addresses without domains)
                                boolean bad = false;
                                InternetAddress current = list[idx];
                                String addr = current.getAddress();
                                if (addr == null) {
                                    bad = true;
                                } else if (addr.length() < 2) {
                                    bad = true;
                                } else if (addr.indexOf('@') < 1) {
                                    bad = true;
                                }

                                // Report any bad addresses to the user
                                if (bad) {
                                    if (badstr.length() > 0) {
                                        badstr.append(", ");
                                    }
                                    badstr.append(addr);
                                }
                            }
                            if (badstr.length() > 0) {
                                inputErrors.put(IMnPatchForm.NOTIFY_LABEL, "Invalid e-mail addresses: " + badstr);
                            }
                        }
                    } catch (AddressException aex) {
                        inputErrors.put(IMnPatchForm.NOTIFY_LABEL, "Invalid e-mail address: " + aex.getMessage());
                    }
                }


                // Validate the base patch information 
                CMnPatch basePatch = null;
                if (basePatchId != null) {
                    app.debug("CMnPatchRequest: obtaining base patch information from database");
                    basePatch = patchTable.getRequest(rc.getConnection(), basePatchId, true);

                    // Prevent the user from creating an external patch from a
                    // previous internal-only patch
                    if ((custUse != null) && Boolean.parseBoolean(custUse) && (basePatch != null) && (basePatch.getFixes() != null)) {
                        // Determine if the base patch uses alternate branches
                        boolean baseUsesAltBranch = false;
                        Enumeration<CMnPatchFix> baseFixList = basePatch.getFixes().elements();
                        while (baseFixList.hasMoreElements()) {
                            CMnPatchFix fix = (CMnPatchFix) baseFixList.nextElement();
                            if (fix.getVersionControlRoot() != null) {
                                baseUsesAltBranch = true;
                            }
                        }
                        if (baseUsesAltBranch && (!basePatch.getForExternalUse())) {
                            app.debug("CMnPatchRequest: Invalid user input.  External patch cannot be built off of internal patch.");
                            inputErrors.put(IMnPatchForm.PATCH_USE_LABEL, "External patches cannot be built off of internal patches unless all fixes originate from the GA branch.");
                        } else {
                            app.debug("CMnPatchRequest: base has alt branches = " + baseUsesAltBranch + ", base is external = " + basePatch.getForExternalUse());
                        }
                    } else if ((basePatch != null) && (basePatch.getFixes() != null)) {
                        app.debug("CMnPatchRequest: custUse = " + custUse + ", base fix count = " + basePatch.getFixes().size());
                    } else {
                        app.debug("CMnPatchRequest: custUse = " + custUse);
                    }
                }



                // If patchId is specified it means we are editing an existing patch
                CMnPatch patch = null;
                if ((patchId != null) && (patchId.length() > 0)) {
                    patch = patchTable.getRequest(rc.getConnection(), patchId, true);
                    if (patch != null) {
                        app.debug("CMnPatchRequest: obtained data for patch ID " + patchId);

                        // Populate the patch with user information
                        if (patch.getRequestor() != null) {
                            UserData requestor = loginTable.getUserByUid(ac.getConnection(), patch.getRequestor().getUid());
                            if (requestor != null) {
                                patch.setRequestor(requestor);
                            }
                        }

                        // Populate the comments with user data from the authenication repository
                        Vector<CMnPatchComment> comments = patch.getCommentList();
                        if ((comments != null) && (comments.size() > 0)) {
                            app.debug("CMnPatchRequest: returned " + comments.size() + " comments for this patch.");
                            getCommentUserData(ac.getConnection(), comments);
                        }

                        // Populate the service patch build information from the patch repository
                        if (patch.getPatchBuild() != null) {
                            // Obtain patch build information
                            String patchBuildId = Integer.toString(patch.getPatchBuild().getId());
                            app.debug("CMnPatchRequest: Querying the patch repository for Build ID " + patchBuildId);
                            CMnDbBuildData patchBuild = buildTable.getBuild(pc.getConnection(), patchBuildId);
                            patch.setPatchBuild(patchBuild);

                            // Obtain a list of product areas
                            Vector areas = CMnFeatureOwnerTable.getAllAreas(pc.getConnection());
                            if ((areas != null) && (areas.size() > 0)) {
                                req.setAttribute(IMnBuildForm.PRODUCT_AREA_DATA, areas);
                            }

                            // Obtain a list of area reviews
                            Vector reviews = CMnReviewTable.getReviews(pc.getConnection(), patchBuildId);
                            if ((reviews != null) && (reviews.size() > 0)) {
                                //setUserData(app, reviews);
                                req.setAttribute(IMnBuildForm.AREA_REVIEW_DATA, reviews);
                            }

                        } else {
                            app.debug("CMnPatchRequest: No patch build associated with the patch request.");
                        }
                    } else {
                        throw new ApplicationException(ErrorMap.APPLICATION_DISPLAY_FAILURE, "Failed to obtain patch data for patch ID " + patchId);
                    }

                    // Obtain the patch owner information from the database
                    CMnPatchOwner owner = patchTable.getPatchOwner(rc.getConnection(), patchId);
                    if ((owner != null) && (owner.getUser() != null)) {
                        UserData ownerData = loginTable.getUserByUid(ac.getConnection(), owner.getUser().getUid());
                        if (ownerData != null) {
                            owner.setUser(ownerData);
                        }
                        req.setAttribute(CMnPatchOwnerForm.OWNER_DATA, owner);
                    }

                    // Use the data from the database if form values are empty
                    // Otherwise use the form values where they differ from the original
                    if (custId == null) {
                        if ((patch.getCustomer() != null) && (patch.getCustomer().getId() != null)) {
                            custId = patch.getCustomer().getId().toString();
                        } else {
                            if (patch.getCustomer() != null) {
                                app.debug("Patch " + patchId + " has an invalid customer ID: " + patch.getCustomer().getId());
                            } else {
                                app.debug("Patch " + patchId + " is not associated with a customer.");
                            }
                        }
                    } else {
                        boolean useFormValue = true;
                        CMnAccount customer = patch.getCustomer();
                        Integer formCustId = new Integer(custId);
                        if ((customer != null) && (customer.getId().intValue() != formCustId.intValue())) {
                            customer = new CMnAccount();
                            customer.setId(new Integer(custId));
                            patch.setCustomer(customer);
                        }
                    }
                    if (envId == null) {
                        envId = patch.getEnvironment().getId().toString();
                    } else {
                        boolean useFormValue = true;
                        CMnEnvironment env = patch.getEnvironment();
                        Integer formEnvId = new Integer(envId);
                        if ((env != null) && (env.getId().intValue() != formEnvId.intValue())) {
                            env = new CMnEnvironment();
                            env.setId(new Integer(envId));
                            patch.setEnvironment(env);
                        }
                    }
                    if (patchName == null) {
                        patchName = patch.getName();
                    } else {
                        patch.setName(patchName);
                    }
                    if ((notification == null) && (patch.getNotifications() != null)) {
                        notification = InternetAddress.toString(patch.getCCList());
                    }
                    if (justification == null) {
                        justification = patch.getJustification();
                    } else {
                        patch.setJustification(justification);
                    }

                    CMnApprovals.setApprovalData(app, req, res, patchId);

                    // Obtain a list of existing fixes
                    Vector fixes = patchTable.getFixes(rc.getConnection(), patchId, true);
                    if ((fixes != null) && (fixes.size() > 0)) {
                        getSDTrackerFixes(app, fixes);
                        req.setAttribute(IMnPatchForm.FIX_LIST_DATA, fixes);
                    }

                }


                // Load the backing data for these input parameters
                CMnAccount cust = null;
                if ((custId != null) && (custId.length() > 0)) {
                    cust = custTable.getCustomer(rc.getConnection(), custId);
                    app.debug("CMnPatchRequest: obtained data for customer ID " + custId);
                }
                CMnEnvironment env = null;
                if ((cust != null) && (envId != null) && (envId.length() > 0)) {
                    Set envList = cust.getEnvironments();
                    Iterator iter = envList.iterator();
                    while (iter.hasNext()) {
                        CMnEnvironment current = (CMnEnvironment) iter.next();
                        if (current.getId().intValue() == Integer.parseInt(envId)) {
                            env = current;
                        }
                    }
                    if (env != null) {
                        app.debug("Found customer environment matching environment ID " + envId);
                    } else {
                        app.debug("Failed to locate environment ID " + envId + " in any of the " + envList.size() + " records for this customer.");
                    }
                }
                CMnDbBuildData build = null;
                if ((buildId != null) && (buildId.length() > 0)) {
                    build = CMnBuildTable.getBuild(rc.getConnection(), buildId);
                }

                // Populate the customer data from the database
                Vector builds = null;
                Vector customers = null;
                Vector<CMnPatch> previousPatches = null;
                if (cust != null) {
                    customers = new Vector();
                    customers.add(cust);

                    // If an environment is selected, use that to filter the builds
                    if (env != null) { 
                        // Limit the build results by product 
                        CMnProduct product = env.getProduct();
                        if ((product != null) && (product.getName() != null) && (product.getName().length() > 0)) {

                            String release = (String) req.getParameter(IMnPatchForm.RELEASE_ID_LABEL);
                            if (release == null) {
                                release = (String) req.getAttribute(IMnPatchForm.RELEASE_ID_LABEL);
                            }

                            // Further limit the build results by version
                            String releaseStr = null;
                            if ((release != null) && (release.length() > 0)) {
                                releaseStr = "MN-" + product.getName().toUpperCase() + "-" + release + "-";
                                app.debug("CMnPatchRequest: Querying the release table for builds matching " + releaseStr);
                                builds = CMnReleaseTable.getReleaseList(rc.getConnection(), releaseStr);
                            } else {
                                releaseStr = "MN-" + product.getName().toUpperCase() + "-";
                                builds = CMnReleaseTable.getReleaseList(rc.getConnection(), releaseStr);
                            }

                            // Print some debugging information to the logs
                            int buildCount = 0;
                            if (builds != null) {
                                buildCount = builds.size();
                            } 
                            app.debug("CMnPatchRequest: Found " + buildCount + " builds matching " + releaseStr);
                        } else {
                            app.debug("CMnPatchRequest: User provided no product information. Querying the release table for all released builds.");
                            builds = CMnReleaseTable.getReleaseList(rc.getConnection());
                        }
                    } else {
                        app.debug("CMnPatchRequest: User provided no environment information. Querying the release table for all released builds.");
                        builds = CMnReleaseTable.getReleaseList(rc.getConnection());
                    }

                    app.debug("CMnPatchRequest: obtained a list of builds");
                    req.setAttribute(IMnPatchForm.BUILD_LIST_DATA, builds);
                    app.debug("CMnPatchRequest: added entire list of builds to the request attribute");

                    // Provide build-specific form elements
                    if ((buildId != null) && (buildId.length() > 0)) {
                        // Obtain the build metrics for the selected build
                        for (Enumeration e = builds.elements(); e.hasMoreElements(); ) {
                            CMnDbBuildData currentBuild = (CMnDbBuildData) e.nextElement();
                            if (buildId.equals(Integer.toString(currentBuild.getId()))) {
                                CMnMetricTable metricTable = CMnMetricTable.getInstance();
                                Vector metrics = metricTable.getMetrics(rc.getConnection(), currentBuild.getBuildVersion());
                                req.setAttribute(IMnPatchForm.BUILD_METRIC_DATA, metrics);
                            }
                        }

                        // Determine if there are previuos patches that the user can build on
                        previousPatches = patchTable.getRelatedRequests(rc.getConnection(), buildId, custId, true);
                        if ((previousPatches != null) && (previousPatches.size() > 0)) {
                            getPatchUserData(ac.getConnection(), previousPatches);
                            app.debug("CMnPatchRequest: added " + previousPatches.size() + " previous patches to the request attribute");
                            req.setAttribute(IMnPatchForm.PATCH_LIST_DATA, previousPatches);
                            // Make sure the user specifies one of these values
                            if (!hasParameter(req, IMnPatchForm.BASE_PATCH_LABEL)) {
                                inputErrors.put(IMnPatchForm.BASE_PATCH_LABEL, "Select a previous patch.");
                            }
                        } else {
                            app.debug("CMnPatchRequest: No previous patches found: build = " + buildId + ", cust = " + custId);
                        }
                    }

                } else {
                    customers = custTable.getAllCustomers(rc.getConnection());
                    app.debug("CMnPatchRequest: obtained a list of all customers");
                }
                req.setAttribute(IMnPatchForm.CUSTOMER_LIST_DATA, customers);
                app.debug("CMnPatchRequest: added entire list of customers to the request attribute");

                // Continue with the request if all parameters have been provided
                if ((patch != null) && (inputErrors.size() == 0)) {
                    // Obtain job information and store in the session 
                    Hashtable<Job, Vector<CMnDbBuildData>> jobs = getJobData(app, req, res, patch);
                    if ((jobs != null) && (jobs.size() > 0)) {
                        req.setAttribute(IMnPatchForm.JOB_DATA, jobs);
                    }

                    // If we have a patch data object then this is an edit 
                    req.setAttribute(IMnPatchForm.PATCH_DATA, patch);
                    if (submitValue != null) {
                        app.debug("CMnPatchRequest: updating the patch information: name=" + patch.getName());
                        CMnServicePatch.RequestStatus oldStatus = patch.getStatus();
                        boolean updateOk = patchTable.updateRequest(rc.getConnection(), patch);
                        CMnServicePatch.RequestStatus newStatus = patch.getStatus();

                        if (updateOk) {
                            //only if there is a status change as a result of updateRequest call, call notify users
                            if(oldStatus != newStatus){
                             sendEmailViaNotifyUsersAPI(updateOk,app,req,patch,error);
                            }
                            app.debug("CMnPatchRequest: update the patch information in the database using the patch data object");

                            // Forward the user to the next step in the request process
                            result = app.forwardToCommand(req, res, "/patch/CMnPatchRequestFixes");
                        } else {
                            // Send the user back to the input form to correct the error
                            result.setDestination("patch/patch_request.jsp");
                        }
                    } else {
                        // Send the user back to the input form to gather additional info
                        result.setDestination("patch/patch_request.jsp");
                    }
                } else if ((inputErrors.size() == 0) &&
                           (cust != null) && 
                           (env != null) && 
                           (build != null) &&
                           (custUse != null) &&
                           (patchName != null) && (patchName.length() > 0) &&
                           (justification != null) && (justification.length() > 0) &&
                           (submitValue != null)) {
                    // Construct a patch object from the form data
                    patch = new CMnPatch();
                    patch.setName(patchName);
                    patch.setBuild(build);
                    patch.setEnvironment(env);
                    patch.setCustomer(cust);
                    patch.setForExternalUse(Boolean.parseBoolean(custUse));
                    patch.setRequestor(SessionUtility.getLogin(req.getSession()));
                    patch.setRequestDate(new Date());
                    patch.setJustification(justification);
                    if (notification != null) {
                        try {
                            patch.setCCList(InternetAddress.parse(notification));
                        } catch (AddressException aex) {
                            // If there's an error, it's coming from the database anyway
                            // so don't worry about updating the value
                        }
                    }
                    if ((basePatchId != null) && (basePatchId.trim().length() > 0)) {
                        CMnPatch previousPatch = new CMnPatch();
                        previousPatch.setId(new Integer(basePatchId));
                        patch.setPreviousPatch(previousPatch);
                    }

                    patchId = patchTable.addRequest(rc.getConnection(), patch);
                    patch.setId(new Integer(patchId));
                    app.debug("CMnPatchRequest: added new patch request to the database: id = " + patchId);
                    req.setAttribute(IMnPatchForm.PATCH_DATA, patch);

                    // Forward the user to the next step in the request process
                    result = app.forwardToCommand(req, res, "/patch/CMnPatchRequestFixes"); 
                } else {
                    // Return input validation errors to the user
                    if ((custId == null) || (custId.length() == 0)) {
                        inputErrors.put(IMnPatchForm.CUSTOMER_ID_LABEL, "Customer ID must be specified.");
                    } else if (cust == null) {
                        inputErrors.put(IMnPatchForm.CUSTOMER_ID_LABEL, "Unable to locate Customer ID: " + custId);
                    }
                    if ((envId == null) || (envId.length() == 0)) {
                        inputErrors.put(IMnPatchForm.ENV_ID_LABEL, "Environment ID must be specified.");
                    } else if (env == null) {
                        inputErrors.put(IMnPatchForm.ENV_ID_LABEL, "Unable to locate Environment ID: " + envId);
                    } 
                    if ((buildId == null) || (buildId.length() == 0)) {
                        inputErrors.put(IMnPatchForm.BUILD_ID_LABEL, "Build ID must be specified.");
                    } else if (build == null) { 
                        inputErrors.put(IMnPatchForm.BUILD_ID_LABEL, "Unable to locate Build ID: " + buildId);
                    }
                    if ((custUse == null) || (custUse.length() == 0)) {
                        inputErrors.put(IMnPatchForm.PATCH_USE_LABEL, "Distribution status not set.");
                    }
                    if ((patchName == null) || (patchName.length() == 0)) {
                        inputErrors.put(IMnPatchForm.PATCH_NAME_LABEL, "Patch name required.  Format must be 'SP#'");
                    }
                    if ((justification == null) || (justification.length() == 0)) {
                        inputErrors.put(IMnPatchForm.JUSTIFY_LABEL, "Business justification required.");
                    }

                    // We only care about displaying validation errors to the user
                    // if they have submitted data using the submit button
                    if (submitValue != null) {
                        req.setAttribute(CMnBaseForm.INPUT_ERROR_DATA, inputErrors);
                    }

                    // Send the user back to the input form to gather additional info
                    result.setDestination("patch/patch_request.jsp");
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

    /**
     * Query the Jenkins instance for Job and build information. 
     *
     * @param   app     Application which called the command
     * @param   req     HttpServletRequest object
     * @param   res     HttpServletResponse object
     * @param   patch   Service patch information
     *
     * @return  Hashtable containing a list of jobs and the associated builds.
     */
    public Hashtable<Job, Vector<CMnDbBuildData>> getJobData(
        WebApplication app, 
        HttpServletRequest req, 
        HttpServletResponse res, 
        CMnPatch patch
    )
        throws ApplicationException
    {
        Hashtable<Job, Vector<CMnDbBuildData>> jobs = new Hashtable<Job, Vector<CMnDbBuildData>>();

        ApplicationException exApp = null;
        ApplicationError error = null;
        RepositoryConnection rc = null;
        RepositoryConnection prc = null;
        try {
            // Obtain a connection to the regular build database
            rc = app.getRepositoryConnection();

            // Obtain a connection to the service patch build database
            prc = ((CMnControlApp) app).getPatchRepositoryConnection();

            // Send the job request to Jenkins
            String url = app.getConfigValue("patch.jenkins.url");
            app.debug("Loaded Jenkins URL from config: " + url);
            if ((patch != null) && (url != null)) {
                // Collect information about the Jenkins
                String shortname = CMnPatchUtil.getJobName(patch, true);
                String longname  = CMnPatchUtil.getJobName(patch, false);
                String[] names = { 
                    shortname,
                    longname
                };

                try {
                    URL jenkinsUrl = new URL(url);
                    XmlApi jenkins = new XmlApi(jenkinsUrl);
                    for (int idx = 0; idx < names.length; idx++) {
                        app.debug("CMnPatchRequest: preparing to query Jenkins job information: " + jenkins.getJobQueryUrl(names[idx]));
                        Job job = jenkins.getJob(names[idx]);
                        if (job != null) {
                            Vector<CMnDbBuildData> builds = null;

                            // Get the builds associated with this job
                            if (job.getURL() != null) {
                                builds = CMnBuildTable.getBuildsByJob(prc.getConnection(), job.getURL().toString());
                                if ((builds != null) && (builds.size() > 0)) {
                                    app.debug("CMnPatchRequest: Found " + builds.size() + " builds matching this job URL: " + job.getURL().toString());
                                } else {
                                    app.debug("CMnPatchRequest: Found no builds matching job URL: " + job.getURL().toString());
                                }
                            } else {
                                app.debug("CMnPatchRequest: Job URL parsed from XML is null.");
                            }

                            // Add the current job and list of builds
                            jobs.put(job, builds);
                        } else {
                            app.debug("CMnPatchRequest: Job data is null.");
                        }

                    }
                } catch (IOException ioex) {
                    app.debug("CMnPatchRequest: failed to connect to Jenkins instance: " + ioex.getMessage());
                    exApp = new ApplicationException(
                        ErrorMap.APPLICATION_DISPLAY_FAILURE,
                        "Unable to connect to the Jenkins instance: " + ioex.getMessage());
                    exApp.setStackTrace(ioex);
                }
            } else if ((url == null) && (patch != null)) {
                app.debug("CMnPatchRequest: skipping Jenkins call due to null Jenkins URL.");
            } else {
                app.debug("CMnPatchRequest: skipping Jenkins call due to null patch data.");
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
            app.releaseRepositoryConnection(prc);

            // Throw any exceptions once the database connections have been cleaned up
            if (exApp != null) {
                throw exApp;
            }
        }


        return jobs;
    }


}
