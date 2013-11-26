/*
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.ctrl.command.patch; 

import com.modeln.build.common.data.product.CMnPatch;
import com.modeln.build.common.data.product.CMnPatchFix;
import com.modeln.build.ctrl.CMnControlApp;
import com.modeln.build.ctrl.database.CMnPatchTable;
import com.modeln.build.ctrl.forms.CMnBaseForm;
import com.modeln.build.ctrl.forms.IMnPatchForm;
import com.modeln.testfw.reporting.CMnBuildTable;
import com.modeln.testfw.reporting.CMnDbBuildData;


import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.*;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.Connection;
import java.sql.SQLException;

import com.modeln.build.web.errors.ApplicationError;
import com.modeln.build.web.errors.ApplicationException;
import com.modeln.build.web.application.CommandResult;
import com.modeln.build.web.application.AdminCommand;
import com.modeln.build.web.application.WebApplication;
import com.modeln.build.web.database.RepositoryConnection;
import com.modeln.build.web.errors.ErrorMap;


/**
 * This command allows a user to update a the origin of each fix in the patch request. 
 * 
 * @author             Shawn Stafford
 */
public class CMnOriginUpdate extends AdminCommand {

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
            try {
                rc = app.getRepositoryConnection();
                CMnPatchTable patchTable = CMnPatchTable.getInstance();
                app.debug("CMnPatchUpdate: obtained a connection to the build database");

                // Fall back to the request attributes in case the data was set by another command
                String patchId = (String) req.getParameter(IMnPatchForm.PATCH_ID_LABEL);
                if (patchId == null) {
                    patchId = (String) req.getAttribute(IMnPatchForm.PATCH_ID_LABEL);
                }

                // Get the list of origin selections for each SDR
                Vector<CMnPatchFix> fixes = new Vector<CMnPatchFix>();
                Enumeration names = req.getParameterNames();
                while (names.hasMoreElements()) {
                    String name = (String) names.nextElement();
                    String value = (String) req.getParameter(name);
                    if (name.matches("^" + IMnPatchForm.FIX_ORIGIN_PREFIX + "[0-9]+")) {
                        // Construct an object to hold the patch fix information
                        Integer bugId = Integer.parseInt(name.substring(2));
                        CMnPatchFix fix = new CMnPatchFix();
                        fix.setBugId(bugId.intValue());
                        // Associate the fix with a patch request (the origin)
                        CMnPatch origin = new CMnPatch();
                        origin.setId(new Integer(value));
                        fix.setOrigin(origin);

                        fixes.add(fix);
                    }
                }


                // Obtain the previous patch data and update with user input 
                CMnPatch patch = null;
                if ((patchId != null) && (patchId.length() > 0)) {
                    patch = patchTable.getRequest(rc.getConnection(), patchId, true);
                    if (patch != null) {

                        // Determine if the user has submitted the information
                        boolean updateOk = false;
                        String requestModeValue = (String) req.getParameter(CMnBaseForm.FORM_STATUS_LABEL);
                        if ((requestModeValue != null) && requestModeValue.equalsIgnoreCase(CMnBaseForm.UPDATE_DATA)) {
                            // Get a list of all updated fix origins
                            Vector<CMnPatchFix> updatedFixes = getUpdatedFixes(patch.getFixes(), fixes);
                            if (updatedFixes.size() > 0) {
                                app.debug("CMnOriginUpdate: Attempting to update " + updatedFixes.size() + " origins for Patch ID " + patchId); 
                                Enumeration fixlist = updatedFixes.elements();
                                while (fixlist.hasMoreElements()) {
                                    updateOk = true;
                                    CMnPatchFix fix = (CMnPatchFix) fixlist.nextElement();
                                    boolean success = patchTable.updateOrigin(rc.getConnection(), patch.getId().toString(), fix);
                                    if (!success) {
                                        updateOk = false;
                                    }
                                }
                            } else {
                                app.debug("CMnOriginUpdate: No updated fix origins detected for Patch ID " + patchId);
                                updateOk = true;
                            }
                        } else {
                            if (patch != null) {
                                // Get a list of all previous fixes that might apply
                                String custId = null;
                                if ((patch.getCustomer() != null) && (patch.getCustomer().getId() != null)) {
                                    custId = patch.getCustomer().getId().toString();
                                }

                                String buildId = null;
                                if ((patch.getBuild() != null) && (patch.getBuild().getBuildVersion() != null)) {
                                    buildId = Integer.toString(patch.getBuild().getId());
                                }

                                Vector<CMnPatch> relatedPatches = null;
                                if ((custId != null) && (buildId != null)) {
                                    relatedPatches = patchTable.getRelatedRequests(rc.getConnection(), buildId, custId, true);
                                    if (relatedPatches != null) {
                                        app.debug("CMnOriginUpdate: Found " + relatedPatches.size() + " related patches.");
                                        // Group the list of fixes by bug ID
                                        Hashtable<Integer, Vector<CMnPatch>> relatedFixes = getRelatedFixes(app, patch, relatedPatches);
                                        req.setAttribute(IMnPatchForm.FIX_LIST, relatedFixes);
                                    }
                                }

                                req.setAttribute(IMnPatchForm.PATCH_DATA, patch);
                            } else {
                                throw new ApplicationException(ErrorMap.APPLICATION_DISPLAY_FAILURE,
                                    "Failed to obtain patch data for patch ID " + patchId);
                            }
                        }

                        // Forward the user to the correct page following the update
                        if (updateOk) {
                            result = app.forwardToCommand(req, res, "/patch/CMnPatchRequest");
                        } else {
                            result.setDestination("patch/origin_update.jsp");
                        }
                    } else {
                        app.debug("Unable to locate patch ID " + patchId);
                        throw new ApplicationException(ErrorMap.APPLICATION_DISPLAY_FAILURE,
                            "Failed to locate Patch ID " + patchId);
                    }
                } else {
                    app.debug("No patch ID provided by user.");
                    throw new ApplicationException(ErrorMap.APPLICATION_DISPLAY_FAILURE,
                        "No patch ID provided by user.");
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

                // Throw any exceptions once the database connections have been cleaned up
                if (exApp != null) {
                    throw exApp;
                }
            }
        } else {
            app.debug("CMnPatchDelete: skipping execution due to pre-existing error: " + result.getError());
        }

        return result;
    }


    /**
     * Return a list of possible origins for each bug.
     * The method returns a hashtable where the key is the BugID
     * and the value is a Vector containing a list of related patch IDs.
     * 
     * @param  patch   Patch request containing the list of bugs
     * @param  related List of related patches 
     */
    private Hashtable<Integer, Vector<CMnPatch>> getRelatedFixes(WebApplication app, CMnPatch patch, Vector<CMnPatch> related) {
        Hashtable<Integer, Vector<CMnPatch>> relatedFixes = new Hashtable<Integer, Vector<CMnPatch>>(); 

        // Iterate through the list of bugs in the patch
        if ((patch != null) && (patch.getFixes() != null)) {
            Enumeration fixList = patch.getFixes().elements();
            while (fixList.hasMoreElements()) {
                CMnPatchFix currentFix = (CMnPatchFix) fixList.nextElement();
                Integer bugId = new Integer(currentFix.getBugId());
                Vector<CMnPatch> fixes = getRelatedFixes(app, bugId.intValue(), related);
                if ((fixes != null) && (fixes.size() > 0)) {
                    relatedFixes.put(bugId, fixes);
                }
            }
        }

        return relatedFixes;
    }

    /**
     * Return a list of possible origins for the current bug ID.
     */
    private Vector<CMnPatch> getRelatedFixes(WebApplication app, int bugId, Vector<CMnPatch> related) {
        Vector<CMnPatch> relatedFixes = new Vector<CMnPatch>();

        if (related != null) {
            // Iterate through each patch in the list of related patches
            Enumeration patchList = related.elements();
            while (patchList.hasMoreElements()) {
                CMnPatch currentPatch = (CMnPatch) patchList.nextElement();
                if ((currentPatch != null) && (currentPatch.getFixes() != null)) {
                    // Iterate through each fix in the current patch
                    Enumeration fixList = currentPatch.getFixes().elements();
                    while (fixList.hasMoreElements()) {
                        // Determine if the current fix matches the bug ID
                        CMnPatchFix currentFix = (CMnPatchFix) fixList.nextElement();
                        if ((bugId == currentFix.getBugId()) && (currentFix.getOrigin() != null)) {
                            boolean added = addPatch(relatedFixes, currentFix.getOrigin());
app.debug("Patch ID added to list: " + added);
                        } else if (currentFix.getOrigin() != null) {
app.debug("Bug IDs: " + bugId + " != " + currentFix.getBugId());
                        } else {
app.debug("Bug IDs match but origin is null.");
                        }
                    }
                }
            }
        }
 
        return relatedFixes;
    }


    /**
     * Add the patch to the list of patches if it is not already 
     * part of the list.
     *
     * @param   patches    List of patches
     * @param   patch      Patch to add to the list
     */
    private boolean addPatch(Vector<CMnPatch> patches, CMnPatch patch) {
        boolean added = false;

        // Determine if the patch already exists in the list
        boolean found = false;
        CMnPatch current = null;
        Enumeration patchList = patches.elements();
        while (patchList.hasMoreElements() && !found) {
            current = (CMnPatch) patchList.nextElement(); 
            if (current.getId() == patch.getId()) {
                found = true;
            }
        }

        // Add the patch to the list if necessary
        if (!found) {
            patches.add(patch);
            added = true;
        }

        return added;
    }

    /**
     * Get a list of fixes which have updated origins.
     * 
     * @param  oldfixes   Old list of fix information
     * @param  newfixes   New list of fix information
     * @return List of updated fixes
     */
    private Vector<CMnPatchFix> getUpdatedFixes(Vector<CMnPatchFix> oldfixes, Vector<CMnPatchFix> newfixes) {
        Vector<CMnPatchFix> updates = new Vector<CMnPatchFix>();

        if ((oldfixes != null) && (oldfixes.size() > 0) && (newfixes != null) && (newfixes.size() > 0)) {
            Enumeration oldlist = oldfixes.elements();
            while (oldlist.hasMoreElements()) {
                CMnPatchFix oldfix = (CMnPatchFix) oldlist.nextElement();
                Enumeration newlist = newfixes.elements();
                while (newlist.hasMoreElements()) {
                    CMnPatchFix newfix = (CMnPatchFix) newlist.nextElement();
                    // Compare the origin of the old and new fix
                    if ((oldfix.getOrigin() != null) && (newfix.getOrigin() != null)) {
                        Integer oldOrigin = oldfix.getOrigin().getId();
                        Integer newOrigin = newfix.getOrigin().getId(); 
                        if (!oldOrigin.equals(newOrigin)) {
                            updates.add(newfix);
                        }
                    }
                }
            }
        }

        return updates;
    } 

}
