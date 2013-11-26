/*
 * Login.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.ctrl.command.patch;

import com.modeln.build.common.data.account.CMnAccount;
import com.modeln.build.common.data.account.CMnEnvironment;
import com.modeln.build.common.data.product.CMnProduct;
import com.modeln.build.common.data.product.CMnPatch;
import com.modeln.build.common.data.product.CMnPatchFix;
import com.modeln.build.common.database.CMnCustomerTable;
import com.modeln.build.common.tool.CMnPatchUtil;
import com.modeln.build.ctrl.CMnControlApp;
import com.modeln.build.ctrl.database.CMnPatchTable;
import com.modeln.build.ctrl.forms.CMnBaseForm;
import com.modeln.build.ctrl.forms.IMnPatchForm;
import com.modeln.build.perforce.Fixes;
import com.modeln.build.perforce.FixRecord;
import com.modeln.build.sdtracker.CMnBug;
import com.modeln.build.sdtracker.CMnBugTable;
import com.modeln.build.sourcecontrol.CMnCheckIn;
import com.modeln.build.sourcecontrol.CMnPerforceCheckIn;
import com.modeln.build.sourcecontrol.CMnUser;
import com.modeln.testfw.reporting.CMnBuildTable;
import com.modeln.testfw.reporting.CMnDbBuildData;
import com.modeln.testfw.reporting.CMnDbMetricData;
import com.modeln.testfw.reporting.CMnMetricTable;
import com.modeln.testfw.reporting.CMnReleaseTable;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.InternetAddress;
import javax.mail.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

import com.modeln.build.web.errors.ApplicationError;
import com.modeln.build.web.errors.ApplicationException;
import com.modeln.build.web.application.CommandResult;
import com.modeln.build.web.application.ProtectedCommand;
import com.modeln.build.web.application.WebApplication;
import com.modeln.build.web.database.RepositoryConnection;
import com.modeln.build.web.errors.ErrorMap;
import com.modeln.build.web.util.SessionUtility;
import com.modeln.build.common.data.account.UserData;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * This command allows a user to select the list of bugs related to a patch. 
 *
 * @author             Shawn Stafford
 */
public class CMnBasePatchFixes extends CMnBasePatchRequest {

    /** Default prefix for jobs */
    protected static final String JOB_PREFIX = "SDRLS";


    /**
     * Return the maintenance release family for the specified release version.
     * For example, if this is a 5.3.3.2 build we should consider all fixes in 
     * 5.3.3.x
     * 
     * @param  release   Product release version (i.e. 5.3.3.2)
     * @return Up to the first 3 digits of the release version (i.e. 5.3.2)
     */
    public String getReleaseFamily(String release) {
        String family = null;
        StringTokenizer st = new StringTokenizer(release, ".");
        if (st.countTokens() > 3) {
            family = st.nextToken() + "." + st.nextToken() + "." + st.nextToken();
        } else {
            family = release;
        }

        return family;
    }



    /**
     * Convert the Perforce fixes to service patch fixes.
     */
    protected Vector<CMnPatchFix> convertFixes(List<FixRecord> fixlist) {
        Vector<CMnPatchFix> fixes = new Vector<CMnPatchFix>();

        // Create a list of bugs (should be fewer than the overall number of fixes/changelists)
        Hashtable<String,CMnPatchFix> bugs = new Hashtable<String,CMnPatchFix>();

        // Process each of the perforce fixes and map them to bugs
        FixRecord p4Fix = null;
        CMnPatchFix patchFix = null;
        Iterator iter = fixlist.iterator();
        while (iter.hasNext()) {
            p4Fix = (FixRecord) iter.next();

            // Create or obtain a data object for the current fix 
            String job = p4Fix.getJob();
            if (job != null) {
                if (bugs.contains(job)) {
                    patchFix = (CMnPatchFix) bugs.get(job);
                } else {
                    // Map the Job name to a bug number
                    patchFix = new CMnPatchFix();
                    if (job.startsWith(JOB_PREFIX)) {
                        Integer bugId = new Integer(job.substring(JOB_PREFIX.length()));
                        patchFix.setBugId(bugId.intValue());
                    } else {
                        patchFix.setBugId(0);
                    }
                }

                // Record the changelists associated with
                CMnPerforceCheckIn cl = new CMnPerforceCheckIn();
                String email = p4Fix.getAuthor();
                Integer changelist = new Integer(p4Fix.getChangelist());
                cl.setId(changelist.toString());
                cl.setAuthor(new CMnUser(email, email));
                patchFix.addChangelist(cl);

                // Update the bug list
                bugs.put(job, patchFix);
            }

        }

        // Process each of the service patch bugs
        Enumeration keys = bugs.keys();
        while (keys.hasMoreElements()) {
            fixes.add(bugs.get(keys.nextElement()));
        }

        return fixes;
    }


    /**
     * Convert the Perforce fixes to service patch fixes.
     */
    protected Vector<CMnPatchFix> convertBugs(List<CMnBug> buglist) {
        Vector<CMnPatchFix> fixes = new Vector<CMnPatchFix>();

        // Process each of the bugs 
        CMnCheckIn checkin = null;
        CMnBug bug = null;
        Iterator iter = buglist.iterator();
        while (iter.hasNext()) {
            bug = (CMnBug) iter.next();
            if ((bug != null) && (bug.getId() != null)) {
                // Convert the bug information to a fix data object
                CMnPatchFix fix = new CMnPatchFix();
                convertBug(bug, fix);
                fixes.add(fix);
            }
        }

        return fixes;
    }


    /**
     * Determine if the fix list contains the fix ID.
     *
     * @param  id     Bug number
     * @param  fixes  List of fixes
     * @return TRUE if a match is found
     */
    protected boolean hasFix(int id, Vector<CMnPatchFix> fixes) {
        boolean fixFound = false;
        if (fixes != null) {
            Enumeration fixlist = fixes.elements();
            while (fixlist.hasMoreElements()) {
                CMnPatchFix fix = (CMnPatchFix) fixlist.nextElement();
                if (fix.getBugId() == id) {
                    return true;
                }
            }
        }
        return fixFound;
    }


    /**
     * Obtain a list of bugs from the source control or bug tracking system.
     *
     * @param   app     Web application reference
     * @param   build   Build data
     * @return  List of bugs
     */
    protected Vector<CMnPatchFix> getSourceFixes(WebApplication app, CMnDbBuildData build)
        throws ApplicationException
    {
        Vector<CMnPatchFix> fixes = null; 

        if (build != null) {
            // Determine which repository to connect to for fix information
            //if (build.getVersionControlType() == CMnDbBuildData.VersionControlType.PERFORCE) {
            //    fixes = getPerforceFixes(app, build);
            //} else {
                fixes = getSDTrackerFixes(app, build);
            //}
        }

        return fixes;
    }


    /**
     * Obtain a list of bugs from Perforce.
     *
     * @param   app     Web application reference
     * @param   build   Build data
     * @return  List of bugs
     */
    protected Vector<CMnPatchFix> getPerforceFixes(WebApplication app, CMnDbBuildData build) 
        throws ApplicationException
    {
        Vector<CMnPatchFix> fixes = null; 

        if (build != null) {
            fixes = new Vector<CMnPatchFix>(0);
            String p4path = build.getVersionControlRoot() + "@" + build.getVersionControlId() + ",#head";
            app.debug("CMnBasePatchFixes: Querying Perforce for job data: " + p4path);
            List fixlist = Fixes.getFixes(p4path);
            if ((fixlist != null) && (fixlist.size() > 0)) {
                app.debug("CMnBasePatchFixes: Converting " + fixlist.size() + " jobs to patch fixes.");
                fixes = convertFixes(fixlist);
                if (fixes != null) {
                    app.debug("CMnBasePatchFixes: Returned " + fixes.size() + " converted jobs.");
                } else {
                    app.debug("CMnBasePatchFixes: Unable to convert jobs to patch fixes.");
                }
            }
        }

        return fixes;
    }


    /**
     * Obtain bug information from the SDTracker database.
     * If the corresponding bug does not exist, null will be returned.
     *
     * @param   app     Web application reference
     * @param   sdr     SDR Number
     * @return  Bug information 
     */
    protected CMnBug getSDTrackerBug(WebApplication app, String sdr)
        throws ApplicationException
    {
        CMnBug bug = null;

        if (sdr != null) {
            ApplicationException exApp = null;
            RepositoryConnection rc = null;

            CMnBugTable bugTable = CMnBugTable.getInstance();
            try {
                // Obtain fix from SDTracker
                rc = ((CMnControlApp)app).getBugRepositoryConnection();
                if (rc != null) {
                    app.debug(rc);
                    app.debug("CMnBasePatchFixes: Querying SDTracker for sdr: " + sdr);
                    bug = bugTable.getBug(rc.getConnection(), sdr);
                } else {
                    app.debug("CMnBasePatchFixes: Failed to obtain connection to SDTracker database.");
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

        return bug;
    }


    /**
     * Obtain bug information from the SDTracker database.
     * If the corresponding bug does not exist, zero results will be returned.
     * If the SDR number is not unique, multiple SDRs will be returned.
     *
     * @param   app     Web application reference
     * @param   sdr     SDR Number
     * @return  List of bugs
     */
    protected Vector<CMnPatchFix> getSDTrackerFixes(WebApplication app, String sdr)
        throws ApplicationException
    {
        Vector<CMnPatchFix> fixes = new Vector<CMnPatchFix>(0);

        if (sdr != null) {
            fixes = new Vector<CMnPatchFix>(0);

            ApplicationException exApp = null;
            RepositoryConnection rc = null;

            CMnBugTable bugTable = CMnBugTable.getInstance();
            try {
                // Obtain a list of fixes from SDTracker
                rc = ((CMnControlApp)app).getBugRepositoryConnection();
                if (rc.getConnection() != null) {
                    app.debug(rc);
                    app.debug("CMnBasePatchFixes: Querying SDTracker for sdr: " + sdr);
                    CMnBug bug = bugTable.getBug(rc.getConnection(), sdr);
                    if (bug != null) {
                        Vector<CMnBug> bugs = new Vector<CMnBug>();
                        bugs.add(bug);
                        app.debug("CMnBasePatchFixes: Converting SDR " + sdr + " to patch fix.");
                        fixes = convertBugs(bugs);
                        if (fixes != null) {
                            app.debug("CMnBasePatchFixes: Returned " + fixes.size() + " converted bugs.");
                        } else {
                            app.debug("CMnBasePatchFixes: Unable to convert bugs to patch fixes.");
                        }
                    } else {
                        app.debug("CMnBasePatchFixes: No results returned from SDTracker.");
                    }

                } else {
                    app.debug("CMnBasePatchFixes: Failed to obtain connection to SDTracker database.");
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

        return fixes;
    }



    /**
     * Obtain a list of bugs from the SDTracker database. 
     *
     * @param   app     Web application reference
     * @param   build   Build data
     * @return  List of bugs
     */
    protected Vector<CMnPatchFix> getSDTrackerFixes(WebApplication app, CMnDbBuildData build)
        throws ApplicationException
    {
        Vector<CMnPatchFix> fixes = new Vector<CMnPatchFix>(0);

        if (build != null) {
            fixes = new Vector<CMnPatchFix>(0);

            ApplicationException exApp = null;
            RepositoryConnection rc = null;

            CMnBugTable bugTable = CMnBugTable.getInstance();
            try {
                // Obtain a list of fixes from SDTracker
                rc = ((CMnControlApp)app).getBugRepositoryConnection();
                if (rc.getConnection() != null) {
                    app.debug(rc);
                    String release = CMnPatchUtil.getVersionNumber(build.getBuildVersion());

                    // When we query for fixes, we should consider all fixes within
                    // a maintenance release family.  For example, if this is a 5.3.3.2
                    // build we should consider all fixes in 5.3.3.x
                    String family = getReleaseFamily(release);
                    app.debug("CMnBasePatchFixes: Querying SDTracker for fixed bugs: release=" + release + ", family=" + family + ", date=" + build.getStartTime());
                    Vector<CMnBug> bugs = bugTable.getFixedBugs(rc.getConnection(), family, build.getStartTime());
                    if ((bugs != null) && (bugs.size() > 0)) {
                        app.debug("CMnBasePatchFixes: Converting " + bugs.size() + " bugs to patch fixes.");
                        fixes = convertBugs(bugs);
                        if (fixes != null) {
                            app.debug("CMnBasePatchFixes: Returned " + fixes.size() + " converted bugs.");
                        } else {
                            app.debug("CMnBasePatchFixes: Unable to convert bugs to patch fixes.");
                        }
                    } else {
                        app.debug("CMnBasePatchFixes: No results returned from SDTracker.");
                    }
                } else {
                    app.debug("CMnBasePatchFixes: Failed to obtain connection to SDTracker database.");
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

        return fixes;
    }


    /**
     * Update the list of fixes to point to the specified service patch
     * as their origin.
     *
     * @param   origin      Service Patch data 
     * @param   fixes       List of fixes
     * @param   overwrite   If TRUE, overwrite existing origin values
     */
    protected void setOrigin(CMnPatch origin, Vector<CMnPatchFix> fixes, boolean overwrite) {
        if (fixes != null) {
            for (int idx = 0; idx < fixes.size(); idx++) {
                CMnPatchFix fix = (CMnPatchFix) fixes.get(idx);
                if (overwrite || (fix.getOrigin() == null)) {
                    fix.setOrigin(origin);
                }
            }
        }
    }

    /**
     * Query the Perforce server for a complete list of fixes that apply to the current build.
     */
    protected int setFixes(HttpServletRequest req, Vector<CMnPatchFix> fixes) {
        int count = 0;
        if ((fixes != null) && (fixes.size() > 0)) {
            req.setAttribute("AVAILABLE_FIXES", fixes);
            count = fixes.size();
        }
        return count;
    }


    /**
     * Query the Perforce server for a complete list of fixes that apply to the current build.
     */
    protected int setFixes(HttpServletRequest req, Vector<CMnPatchFix> fixes, Vector<CMnPatchFix> baseFixes) {
        int count = 0;

        // Remove the base fixes from the fix list
        if ((baseFixes != null) && (fixes != null)) {
            CMnPatchFix currentFix = null;
            Enumeration e = baseFixes.elements();
            while (e.hasMoreElements()) {
                currentFix = (CMnPatchFix) e.nextElement();
                removeFix(fixes, currentFix);
            }
        }

        // Return the list of fixes in the request
        if ((baseFixes != null) && (baseFixes.size() > 0)) {
            req.setAttribute("BASE_FIXES", baseFixes);
            count = count + baseFixes.size();
        }
        if ((fixes != null) && (fixes.size() > 0)) {
            req.setAttribute("AVAILABLE_FIXES", fixes);
            count = count + fixes.size();
        }

        return count;
    }


    /**
     * Remove the specified fix from the list of fixes.
     */
    protected synchronized boolean removeFix(List fixes, CMnPatchFix fix) {
        boolean fixFound = false;

        CMnPatchFix currentFix = null;
        int fixCount = fixes.size();
        for (int idx = 0; idx < fixCount; idx++) {
            currentFix = (CMnPatchFix) fixes.get(idx);
            if (currentFix.getBugId() == fix.getBugId()) {
                // Remove the fix from the list
                fixes.remove(idx);

                // Adjust the count and index 
                fixCount--;
                idx--;

                fixFound = true;
            }
        }

        return fixFound;
    }


    /**
     * Return a list of all fixes. 
     *
     * @param  fixes  List of fixes
     * @return Comma-delimited list of fixes
     */
    protected String getFixesAsString(Vector<CMnPatchFix> fixes) {
        StringBuffer buf = new StringBuffer(); 
        if (fixes != null) {
            Enumeration fixlist = fixes.elements();
            while (fixlist.hasMoreElements()) {
                CMnPatchFix fix = (CMnPatchFix) fixlist.nextElement();
                if (buf.length() > 0) {
                    buf.append(",");
                }
                buf.append(fix.getBugId());
            }
        }
        return buf.toString();
    }



}
