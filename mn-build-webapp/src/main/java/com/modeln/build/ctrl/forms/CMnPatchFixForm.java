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

import com.modeln.build.common.data.account.CMnAccount;
import com.modeln.build.common.data.account.CMnEnvironment;
import com.modeln.build.common.data.product.CMnPatchFix;
import com.modeln.build.common.data.product.CMnPatchGroup;
import com.modeln.build.common.data.product.CMnProduct;
import com.modeln.build.common.enums.CMnServicePatch;
import com.modeln.build.sourcecontrol.CMnCheckIn;
import com.modeln.build.sourcecontrol.CMnGitCheckIn;
import com.modeln.build.sourcecontrol.CMnPerforceCheckIn;
import com.modeln.build.sourcecontrol.CMnUser;
import com.modeln.testfw.reporting.CMnDbBuildData;
import com.modeln.testfw.reporting.CMnDbMetricData;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import java.util.Hashtable;
import javax.servlet.http.HttpServletRequest;

import com.modeln.build.web.tags.TextTag;
import com.modeln.build.web.tags.DateTag;
import com.modeln.build.web.tags.OptionTag;
import com.modeln.build.web.tags.SelectTag;
import com.modeln.build.web.tags.TagGroup;


/**
 * The build form provides an HTML interface to the service patch fix list. 
 * The class manages transfering the data through the HTTP request.
 * Form data can be rendered in either a read-only or input mode.
 *
 * @author  Shawn Stafford
 */
public class CMnPatchFixForm extends CMnPatchRequestForm implements IMnPatchForm {

    /** Maximum number of changelists to display on a line */
    private static final int MAX_CHANGELISTS_PER_LINE = 3;

    /** The URL used to display service patch information */
    protected URL patchUrl = null;

    /** The URL used to display the patch validation page */
    protected URL verifyUrl = null;

    /** The URL used to display the patch data export page */
    protected URL exportUrl = null;

    /** The URL used to update the origin information */
    protected URL originUrl = null;


    /** Title displayed at the top of the available fixes table */
    private String availableFixesTitle = "Available Fixes";

    /** Title displayed at the top of the additional fixes table */
    private String additionalFixesTitle = "Additional Fixes";

    /** Title displayed at the top of the previous fixes table */
    private String previousFixesTitle = "Previous Fixes";

    /** Enables the display of available fixes */
    private boolean showAvailableFixes = true;

    /** Enables the display of advanced feature fields */
    private boolean showAdvancedFeatures = false;

    /** Enables the edit mode for specifying alternate branches for each fix */
    private boolean allowBranchInput = false;

    /** List of fix groups */
    private Vector<CMnPatchGroup> fixGroups;

    /** List of fixes available for the service patch */
    private Vector<CMnPatchFix> fixes;

    /** List of fixes to apply as the basis for the patch */
    private Vector<CMnPatchFix> baseFixes;

    /** List of selected fixes */
    private Vector<Integer> selectedFixes;

    /** Width of the text fields used for branch information */
    private int branchWidth = 30;

    /** Width of the text fields used for notes */
    private int notesWidth = 60;

    /** Branch where the list of bulk bugs originate from */
    private String bulkBranch = null;

    /** Comma-delimited list of bugs the user wishes to add */
    private String bulkFixes = null;

    /** Define the text that will be used as the header of each table column */
    private String[] COLUMN_NAMES = { "", "Bug", "Changes", "Excludes", "Status", "Release", "Class", "Sub-class", "Requested", "Branch", "Env", "Origin", "Bug Description" , "" };

    // Specify which columns should be visible to the user
    // Columns:                         check    bug    CL#   excl   stat   rel   class  subcl    req  brnch    env   orig   note   icon
    private boolean[] visiblePrev   = {  true,  true,  true, false,  true,  true,  true,  true, false, false,  true,  true,  true , true  };
    private boolean[] populatePrev  = {  true,  true,  true, false,  true,  true,  true,  true, false, false,  true,  true,  true , true  };
    private boolean[] visibleAvail  = {  true,  true,  true, false,  true,  true,  true,  true, false, false, false, false,  true , true  };
    private boolean[] populateAvail = {  true,  true,  true, false,  true,  true,  true,  true, false, false, false, false,  true , true  };

    public static final int COLUMN_IDX_SELECT    = 0;
    public static final int COLUMN_IDX_BUGNUM    = 1;
    public static final int COLUMN_IDX_CHANGE    = 2;
    public static final int COLUMN_IDX_EXCLUDE   = 3;
    public static final int COLUMN_IDX_STATUS    = 4;
    public static final int COLUMN_IDX_RELEASE   = 5;
    public static final int COLUMN_IDX_CLASS     = 6;
    public static final int COLUMN_IDX_SUBCLASS  = 7;
    public static final int COLUMN_IDX_REQUEST   = 8;
    public static final int COLUMN_IDX_BRANCH    = 9;
    public static final int COLUMN_IDX_ENV       = 10;
    public static final int COLUMN_IDX_ORIGIN    = 11;
    public static final int COLUMN_IDX_NOTES     = 12; 
    public static final int COLUMN_IDX_ICONS     = 13;


    /**
     * Construct a form for selecting fixes for the service patch 
     *
     * @param    form     URL for submitting form data
     * @param    images   URL for displaying HTML images
     */
    public CMnPatchFixForm(URL form, URL images) {
        super(form, images);
    }


    /**
     * Set the URL used for viewing service patch information.
     *
     * @param  url   Service Patch URL
     */
    public void setPatchUrl(URL url) {
        patchUrl = url;
    }


    /**
     * Return the URL used for viewing service patch information.
     *
     * @return Service patch URL
     */
    public URL getPatchUrl() {
        return patchUrl;
    }

    /**
     * Set the URL used for validating service patch information.
     *
     * @param  url   Patch validation URL
     */
    public void setVerifyUrl(URL url) {
        verifyUrl = url;
    }


    /**
     * Return the URL used for verifying service patch information.
     *
     * @return Patch validation URL
     */
    public URL getVerifyUrl() {
        return verifyUrl;
    }


    /**
     * Set the URL used for exporting service patch information.
     *
     * @param  url   Patch export URL
     */
    public void setExportUrl(URL url) {
        exportUrl = url;
    }


    /**
     * Return the URL used for exporting service patch information.
     *
     * @return Patch export URL
     */
    public URL getExportUrl() {
        return exportUrl;
    }


    /**
     * Set the URL used for updating the patch origins.
     *
     * @param  url   Patch origin URL
     */
    public void setOriginUrl(URL url) {
        originUrl = url;
    }


    /**
     * Return the URL used for updating service patch origins.
     *
     * @return Patch origin URL
     */
    public URL getOriginUrl() {
        return originUrl;
    }




    /**
     * Set the title for the available fixes table.
     *
     * @param   title   Title text to display
     */
    public void setAvailableFixesTitle(String title) {
        availableFixesTitle = title;
    }

    /**
     * Set the title for the previous fixes table.
     *
     * @param   title   Title text to display
     */
    public void setPreviousFixesTitle(String title) {
        previousFixesTitle = title;
    }

    /**
     * Set the title for the additional fixes table.
     *
     * @param   title   Title text to display
     */
    public void setAdditionalFixesTitle(String title) {
        additionalFixesTitle = title;
    }


    /**
     * Enable or disable the display of available fixes.
     *
     * @param  enabled    TRUE if the available fixes should be displayed
     */
    public void showAvailableFixes(boolean enabled) {
        showAvailableFixes = enabled;
    }

    /**
     * Set the list of available fixes.
     *
     * @param  fixes  Available fixes
     */
    public void setAvailableFixes(Vector fixes) {
        this.fixes = fixes;
    }

    /**
     * Enable the advanced form features such as specifying the origin branch. 
     *
     * @param   adv   TRUE if the advanced fields should be displayed 
     */
    public void showAdvancedFeatures(boolean adv) {
        showAdvancedFeatures = adv;

        //visiblePrev[COLUMN_IDX_EXCLUDE] = adv;
        //visiblePrev[COLUMN_IDX_BRANCH] = adv;

        //populatePrev[COLUMN_IDX_EXCLUDE] = adv;
        //populatePrev[COLUMN_IDX_BRANCH] = adv;

    }

    /**
     * Enables or disables the specified column in all tables.
     *
     * @param  column  Index of the column to enable or disable
     * @param  enable  TRUE if the column should be displayed
     */
    public void showColumn(int column, boolean enable) {
        if ((column >= 0) && (column < visiblePrev.length)) {
            visiblePrev[column] = enable;
        }
        if ((column >= 0) && (column < visibleAvail.length)) {
            visibleAvail[column] = enable;
        }
    }


    /**
     * Determine if the specified check-in is supported by this form.
     * This is used to determine if the check-in information should
     * be displayed to the user.
     *
     * @param  cl    Checkin Information
     * @return TRUE if the checkin is supported
     */
    private boolean isSupported(CMnCheckIn cl) {
        boolean supported = false;

        if (cl != null) {
            SourceControl scm = getSourceControlType();
            if ((scm == SourceControl.GIT) && (cl instanceof CMnGitCheckIn)) {
                supported = true;
            } else if ((scm == SourceControl.PERFORCE) && (cl instanceof CMnPerforceCheckIn)) {
                supported = true;
            }
        }

        return supported;
    }


    /**
     * Set the list of fix groups that are used to determine whether each SDR
     * is recommended/required for the patch.
     *
     * @param  groups   Fix groups
     */
    public void setFixGroups(Vector<CMnPatchGroup> groups) {
        fixGroups = groups;
    }


    /**
     * Set the list of base fixes to build the patch on top of.  This list is
     * typically carried forward from a previous service patch or from the saved
     * progress of the current patch.
     *
     * @param  fixes   Base fixes
     */
    public void setBaseFixes(Vector<CMnPatchFix> fixes) {
        this.baseFixes = fixes;
    }

    /**
     * Return a comma-delimited list of selected fixes.
     *
     * @return Comma-delimited list of selected fixes.
     */
    public String getSelectedFixString() {
        StringBuffer sb = new StringBuffer();
        if ((selectedFixes != null) && (selectedFixes.size() > 0)) {
            Enumeration list = selectedFixes.elements();
            while (list.hasMoreElements()) {
                CMnPatchFix fix = (CMnPatchFix) list.nextElement();
                if (sb.length() > 0) {
                    sb.append(",");
                }
                sb.append(Integer.toString(fix.getBugId()));
            }
        }
        return sb.toString();
    }


    /**
     * Maintain a list of selected fixes.
     *
     * @param  fixes   Selected fixes
     */
    public void setSelectedFixes(Vector<CMnPatchFix> fixes) {
        if ((fixes != null) && (fixes.size() > 0)) {
            selectedFixes = new Vector<Integer>(fixes.size());
            addSelectedFixes(fixes);
        }
    }

    /**
     * Add the specified list to the selected fixes.
     *
     * @param  fixes   Selected fixes
     */
    public void addSelectedFixes(Vector<CMnPatchFix> fixes) {
        if ((fixes != null) && (fixes.size() > 0)) {
            // Ensure that the list of fixes is initialized
            if (selectedFixes == null) {
                selectedFixes = new Vector<Integer>(fixes.size());
            }

            // Add each fix to the list
            Enumeration list = fixes.elements();
            while (list.hasMoreElements()) {
                CMnPatchFix fix = (CMnPatchFix) list.nextElement();
                Integer bugId = new Integer(fix.getBugId());
                if (selectedFixes.indexOf(bugId) < 0) {
                    selectedFixes.add(bugId);
                }
            }
        }
    }

    /**
     * Determine if the fix is currently selected.
     *
     * @param  fix   Fix in question
     * @return TRUE if the fix is selected
     */
    public boolean isSelectedFix(CMnPatchFix fix) {
        boolean found = false;
        if ((selectedFixes != null) && (fix != null)) {
            Integer bugId = new Integer(fix.getBugId()); 
            if (selectedFixes.indexOf(bugId) >= 0) {
                return true;
            }
        }
        return found;
    }


    /**
     * Extend the base method for enabling the input mode.  All input fields
     * must be enabled or disabled when this method is called.
     *
     * @param enabled  TRUE to enable form input
     */
    public void setInputMode(boolean enabled) {
        super.setInputMode(enabled);
    }


    /**
     * Set the input fields by examining the HTTP request to see if
     * a value was submitted.
     *
     * @param   req     HTTP request
     */
    public void setValues(HttpServletRequest req) {
        super.setValues(req);

        bulkBranch = (String) req.getParameter(BULK_FIX_BRANCH);
        if (bulkBranch == null) {
            bulkBranch = (String) req.getAttribute(BULK_FIX_BRANCH);
        }

        bulkFixes = (String) req.getParameter(BULK_FIX_LIST);
        if (bulkFixes == null) {
            bulkFixes = (String) req.getAttribute(BULK_FIX_LIST);
        }

    }


    /**
     * Render the table header as HTML.
     */
    public String getHeader(boolean[] visible) {
        StringBuffer html = new StringBuffer();

        html.append("  <tr class=\"spreadsheet-subheader\">\n");
        if (visible[COLUMN_IDX_SELECT]) {
            html.append("    <th width=\"30\"><b>" + COLUMN_NAMES[COLUMN_IDX_SELECT] + "</b></th>\n");
        }
        if (visible[COLUMN_IDX_BUGNUM]) {
            html.append("    <th width=\"60\"><b>" + COLUMN_NAMES[COLUMN_IDX_BUGNUM] + "</b></th>\n");
        }
        if (visible[COLUMN_IDX_CHANGE]) {
            html.append("    <th width=\"150\"><b>" + COLUMN_NAMES[COLUMN_IDX_CHANGE] + "</b></th>\n");
        }
        if (visible[COLUMN_IDX_EXCLUDE]) {
            html.append("    <th width=\"100\"><b>" + COLUMN_NAMES[COLUMN_IDX_EXCLUDE] + "</b></th>\n");
        }
        if (visible[COLUMN_IDX_STATUS]) {
            html.append("    <th width=\"50\"><b>" + COLUMN_NAMES[COLUMN_IDX_STATUS] + "</b></th>\n");
        }
        if (visible[COLUMN_IDX_RELEASE]) {
            html.append("    <th width=\"50\"><b>" + COLUMN_NAMES[COLUMN_IDX_RELEASE] + "</b></th>\n");
        }
        if (visible[COLUMN_IDX_CLASS]) {
            html.append("    <th width=\"50\"><b>" + COLUMN_NAMES[COLUMN_IDX_CLASS] + "</b></th>\n");
        }
        if (visible[COLUMN_IDX_SUBCLASS]) {
            html.append("    <th width=\"50\"><b>" + COLUMN_NAMES[COLUMN_IDX_SUBCLASS] + "</b></th>\n");
        }
        if (visible[COLUMN_IDX_REQUEST]) {
            html.append("    <th width=\"100\"><b>" + COLUMN_NAMES[COLUMN_IDX_REQUEST] + "</b></th>\n");
        }
        if (visible[COLUMN_IDX_BRANCH]) {
            html.append("    <th><b>" + COLUMN_NAMES[COLUMN_IDX_BRANCH] + "</b></th>\n");
        }
        if (visible[COLUMN_IDX_ENV]) {
            html.append("    <th width=\"60\"><b>" + COLUMN_NAMES[COLUMN_IDX_ENV] + "</b></th>\n");
        }
        if (visible[COLUMN_IDX_ORIGIN]) {
            html.append("    <th width=\"60\"><b>" + COLUMN_NAMES[COLUMN_IDX_ORIGIN] + "</b></th>\n");
        }
        if (visible[COLUMN_IDX_NOTES]) {
            html.append("    <th><b>" + COLUMN_NAMES[COLUMN_IDX_NOTES] + "</b></th>\n");
        }
        if (visible[COLUMN_IDX_ICONS]) {
            html.append("    <th width=\"30\"><b>" + COLUMN_NAMES[COLUMN_IDX_ICONS] + "</b></th>\n");
        }
        html.append("  </tr>\n");

        return html.toString();
    }

    /**
     * Return a comma-delimited list of bugs. 
     *
     * @return  comma-delimited list of bugs 
     */
    public String getFixList() {
        StringBuffer bugs = new StringBuffer();

        if ((baseFixes != null) && (baseFixes.size() > 0)) {
            CMnPatchFix currentFix = null;
            Iterator i = baseFixes.iterator();
            while (i.hasNext()) {
                currentFix = (CMnPatchFix) i.next();
                if (bugs.length() > 0) {
                    bugs.append(",");
                }
                bugs.append(Integer.toString(currentFix.getBugId()));
            }
        }

        return bugs.toString();
    }


    /**
     * Render the previous fixes as an HTML table.
     */
    private String getPreviousFixes() {
        StringBuffer html = new StringBuffer();

        // Display the list of base fixes
        if ((baseFixes != null) && (baseFixes.size() > 0)) {
            html.append("<p>\n");
            html.append("<table width=\"90%\" class=\"spreadsheet\">\n");
            html.append("  <tr class=\"spreadsheet-header\">");
            html.append("    <th colspan=\"" + getVisibleCount(visiblePrev) + "\" align=\"center\">\n");
            html.append("      <b>" + previousFixesTitle + "</b>\n");
            html.append("    </th>\n");
            html.append("  </tr>\n");
            html.append(getHeader(visiblePrev));
            CMnPatchFix currentFix = null;
            Enumeration e = baseFixes.elements();
            int rowcount = 0;
            while (e.hasMoreElements()) {
                rowcount++;
                currentFix = (CMnPatchFix) e.nextElement();
                boolean selected = isSelectedFix(currentFix);
                html.append(toString(currentFix, selected, rowcount, visiblePrev, populatePrev));
            }
            if (inputEnabled) {
                // Display the submit button
                html.append("<tr><td colspan=\"" + getVisibleCount(visiblePrev) + "\" align=\"left\">");
                html.append("<input type=\"submit\" name=\"" + PATCH_FIXES_BUTTON + "\" value=\"Continue\"/>\n");
                html.append("</td></tr>\n");
            } else { 
                html.append("<tr><td colspan=\"" + getVisibleCount(visiblePrev) + "\" align=\"left\">\n");

                // Display additional action buttons
                if (verifyUrl != null) {
                    html.append("<input type=\"submit\" name=\"Verify\" value=\"Verify\" onClick=\"location.href='" + verifyUrl + "'\"/>\n");
                }
                if (exportUrl != null) {
                    html.append("<input type=\"submit\" name=\"Export\" value=\"Export\" onClick=\"location.href='" + exportUrl + "'\"/>\n");
                }
                if (getAdminMode() && (originUrl != null)) {
                    html.append("<input type=\"submit\" name=\"Origin\" value=\"Fix Origins\" onClick=\"location.href='" + originUrl + "'\"/>\n");
                }

                html.append("</td></tr>\n");
            }

            html.append("</table>\n");
            html.append("</p>\n");
        }

        return html.toString();
    }


    /**
     * Render the patch request form as HTML.
     */
    public String toString() {
        int previousColumnCount = getVisibleCount(visiblePrev);
        int availableColumnCount = getVisibleCount(visibleAvail);

        StringBuffer html = new StringBuffer();

        // Disable input if there are form errors
        if (hasFormErrors()) {
            inputEnabled = false;
        }

        if (inputEnabled) {
            String method = null;
            if (postMethodEnabled) {
                method = "post";
            } else {
                method = "get";
            }
            html.append("<form method=\"" + method + "\" action=\"" + getFormUrl() + "\">\n");
        }

        html.append(super.toHiddenString());

        if ((baseFixes != null) || (fixes != null)) {
            String errorMsg = getFormError(IMnPatchForm.FIX_LIST);
            if (errorMsg != null) {
                html.append("<p><font color=red>" + errorMsg + "</font></p>");
            }

            if (showAvailableFixes) {
                html.append(getAdditionalFixes());
            }
            html.append(getPreviousFixes());
            if (showAvailableFixes) {
                html.append(getAvailableFixes());
            }
        } else {
            html.append("No fixes available.");
        }

        if (inputEnabled) {
            html.append("</form>\n");
        }

        return html.toString();

    }





    /**
     * Render the additional fix input fields as an HTML table.
     */
    private String getAdditionalFixes() {
        StringBuffer html = new StringBuffer();

        html.append("<p>\n");
        html.append("<table width=\"90%\" border=\"0\" cellspacing=\"1\" cellpadding=\"1\">\n");
        html.append("<tr class=\"spreadsheet-header\"><th align=\"center\"><b>" + additionalFixesTitle + "</b></th></tr>\n");
        html.append("<tr>\n");
        html.append("  <td>\n");

        html.append("    <table class=\"spreadsheet\">\n");
        // Provide an input box for specifying the source branch of the fixes
        if (allowBranchInput && showAdvancedFeatures) {
            TextTag bulkBranchTag = new TextTag(IMnPatchForm.BULK_FIX_BRANCH);
            bulkBranchTag.setWidth(notesWidth);
            bulkBranchTag.setValue(bulkBranch);
            html.append("<tr>\n");

            html.append("  <td width=\"20%\" align=\"right\" valign=\"top\" NOWRAP>\n");
            html.append("  Source branch:");
            html.append("  </td>\n");

            html.append("  <td width=\"75%\" align=\"left\" valign=\"top\">\n");
            html.append(bulkBranchTag.toString() + "\n");
            String branchErrorMsg = getFormError(bulkBranchTag.getName());
            if (branchErrorMsg != null) {
                html.append("<font color=red><pre>" + branchErrorMsg + "</pre></font><br>\n");
            }
            html.append("  </td>\n");

            html.append("  <td width=\"5%\" align=\"right\" valign=\"top\" NOWRAP>&nbsp;</td>\n");

            html.append("</tr>\n");
        }

        // Provide an input box for specifying a comma-delimited list of fixes
        TextTag bulkTag = new TextTag(IMnPatchForm.BULK_FIX_LIST);
        bulkTag.setHeight(3);
        bulkTag.setWidth(notesWidth);
        bulkTag.setValue(bulkFixes);
        html.append("<tr>\n");
        html.append("  <td width=\"20%\" align=\"right\" valign=\"top\">\n");
        html.append("  Comma-delimited list of fixes:");
        html.append("  </td>\n");
        html.append("  <td width=\"75%\" align=\"left\" valign=\"top\">");
        html.append(bulkTag.toString());
        String errorMsg = getFormError(bulkTag.getName());
        if (errorMsg != null) {
            html.append("<br/><font color=red>\n");
            html.append("Use the BACK button on your browser to return to the previous page and correct the following errors: \n");
            html.append("<pre>" + errorMsg + "</pre>\n");
            html.append("</font><br>");
        }
        html.append("</td>\n");

        html.append("  <td width=\"5%\" align=\"right\" valign=\"top\">\n");
        if (inputEnabled) {
            html.append("<input type=\"submit\" name=\"" + PATCH_FIXES_BUTTON + "\" value=\"Continue\"/>\n");
        }
        html.append("  </td>\n");
        html.append("</tr>\n");
        html.append("    </table>\n");

        html.append("    </td>\n");
        html.append("  </tr>\n");
        html.append("</table>\n");
        html.append("</p>\n");

        return html.toString();
    }


    /**
     * Render the available fixes as an HTML table.
     */
    private String getAvailableFixes() {
        StringBuffer html = new StringBuffer();

        if ((fixes != null) && (fixes.size() > 0)) {
            html.append("<p>\n");
            html.append("<table width=\"90%\" class=\"spreadsheet\">\n");
            html.append("<tr><td colspan=\"" + getVisibleCount(visibleAvail) + "\" align=\"center\">&nbsp;</td></tr>\n");
            html.append("<tr class=\"spreadsheet-header\"><th colspan=\"" + getVisibleCount(visibleAvail) + "\" align=\"center\"><b>" + availableFixesTitle + "</b></th></tr>\n");
            html.append(getHeader(visibleAvail));
            int rowcount = 0;
            CMnPatchFix currentFix = null;
            Iterator i = fixes.iterator();
            while (i.hasNext()) {
                rowcount++;
                currentFix = (CMnPatchFix) i.next();
                boolean selected = isSelectedFix(currentFix);
                html.append(toString(currentFix, selected, rowcount, visibleAvail, populateAvail));
            }

            if (inputEnabled) {
                // Display the submit button
                html.append("<tr><td colspan=\"" + getVisibleCount(visibleAvail) + "\" align=\"left\">");
                html.append("<input type=\"submit\" name=\"" + PATCH_FIXES_BUTTON + "\" value=\"Continue\"/>\n");
                html.append("</td></tr>\n");
            }

            html.append("</table>\n");
            html.append("</p>\n");
        }

        return html.toString();
    }



    /**
     * Determine how many columns to display based on the boolean 
     * values in the specified array.
     *
     * @param  visible   Enables the display of each column
     * @return Number of visible columns
     */
    private int getVisibleCount(boolean[] visible) {
        int count = 0;
        for (int idx = 0; idx < visible.length; idx++) {
            if (visible[idx]) {
                count++;
            }
        }
        return count;
    }

    /**
     * Return a list of any groups that contain the fix.
     *
     * @param  fix    Patch fix
     * @return List of groups containing the fix
     */
    private Vector<CMnPatchGroup> getGroupStatus(CMnPatchFix fix) {
        Vector<CMnPatchGroup> matches = new Vector<CMnPatchGroup>();

        if (fixGroups != null) {
            Enumeration groupList = fixGroups.elements();
            while (groupList.hasMoreElements()) {
                CMnPatchGroup currentGroup = (CMnPatchGroup) groupList.nextElement();
                if (currentGroup.hasFix(fix.getBugId())) {
                    matches.add(currentGroup);
                }
            }
        }

        return matches;
    }

    /**
     * Render the patch fix to a row in the table of fixes.
     *
     * @param  fix       Patch fix data
     * @param  selected  TRUE if the row is selected
     * @param  row       Row number (used to determine odd or even rows)
     * @param  visible   Enables the display of each column 
     * @param  populate  Enables the population of content within the column 
     *
     * @return  HTML representation of the patch fix data
     */
    public String toString(CMnPatchFix fix, boolean selected, int row, boolean[] visible, boolean[] populate) {
        StringBuffer html = new StringBuffer();

        // Get any groups that this fix belongs to
        boolean required = false;
        String rowClass = null;
        CMnPatchGroup highGroup = null;
        Vector<CMnPatchGroup> groups = getGroupStatus(fix);
        if ((groups != null) && (groups.size() > 0)) {
            Collections.sort(groups);
            highGroup = (CMnPatchGroup) groups.lastElement();
            required = (highGroup.getStatus() == CMnServicePatch.FixRequirement.REQUIRED);
            rowClass = highGroup.getStatus().toString();
        } else {
            rowClass = "spreadsheet-shaded";
        }

        // Render the CSS shading style for the class
        html.append("<tr class=\"" + rowClass + "\">\n");

        // Render the selection checkbox
        if (visible[COLUMN_IDX_SELECT]) {
            html.append("  <td NOWRAP valign=\"top\" align=\"right\">");
            if (populate[COLUMN_IDX_SELECT] && getInputMode()) {
                String name = IMnPatchForm.FIX_LIST;
                String value = Integer.toString(fix.getBugId());

                html.append("<input type=\"checkbox\"");
                //html.append(" onClick=\"toggleElement('div" + IMnPatchForm.FIX_NOTE_PREFIX + fix.getBugId() + "')\"");
                html.append(" name=\"" + name + "\" value=\"" + value + "\"");
                if (highGroup != null) {
                    String title = highGroup.getName() + " (" + highGroup.getStatus() + ")";
                    html.append(" title=\"" + title + "\"");
                }
                if (selected || required) {
                    html.append(" CHECKED");
                }
                if (required) {
                    html.append(" DISABLED");
                }
                html.append("/>");

                // Since the checkbox input field will be disabled
                // we need to ensure that the value is still transmitted
                if (required) {
                    html.append("<input type=\"hidden\" name=\"" + name + "\" value=\"" + value + "\"/>");
                }
            }
            html.append("</td>\n");
        }


        // Render the Bug number
        if (visible[COLUMN_IDX_BUGNUM]) {
            html.append("  <td NOWRAP valign=\"top\">");
            if (populate[COLUMN_IDX_BUGNUM]) {
                html.append("<a target=\"_blank\" href=\"" + getBugUrl(Integer.toString(fix.getBugId())) + "\">" + fix.getBugId() + "</a>");
                if (fix.getBugName() != null) {
                    html.append(" (" + fix.getBugName() + ")");
                }
            }
            html.append("</td>\n");
        }


        // Render the list of changes
        if (visible[COLUMN_IDX_CHANGE]) {
            html.append("  <td NOWRAP valign=\"top\">");
            if (populate[COLUMN_IDX_CHANGE]) {
                int clcount = 0;
                Vector<CMnCheckIn> changelists = fix.getChangelists();
                CMnCheckIn cl = null;
                Enumeration clist = changelists.elements();
                while (clist.hasMoreElements()) {
                    cl = (CMnCheckIn) clist.nextElement();
                    if ((cl != null) && isSupported(cl)) {
                        clcount++;
                        html.append("<a target=\"_blank\" href=\"" + getChangelistUrl(cl) + "\">" + cl.getFormattedId() + "</a>");

                        // We probably don't care about author information here
                        //CMnUser author = cl.getAuthor();
                        //if ((author != null) && (author.getEmail() != null)) {
                        //    html.append(" " + author.getEmail());
                        //}

                        if (clist.hasMoreElements()) {
                            html.append(", ");
                            if ((clcount % MAX_CHANGELISTS_PER_LINE) == 0) {
                                html.append("<br/>");
                            }
                        }
                    }
                }
            }
            html.append("</td>\n");
        }



        // Render the changelist exclusions
        if (visible[COLUMN_IDX_EXCLUDE]) {
            html.append("  <td NOWRAP valign=\"top\">");
            if (populate[COLUMN_IDX_EXCLUDE]) {
                if (getInputMode()) {
                    html.append("<input type=\"text\" size=\"10\" maxlength=\"255\" name=\"" + IMnPatchForm.FIX_EXCLUDE_PREFIX + fix.getBugId() + "\" value=\"" + fix.getExclusionsAsString() + "\"/>");
                } else {
                    html.append(fix.getExclusionsAsString());
                }
            }
            html.append("</td>\n");
        }


        // Render the status 
        if (visible[COLUMN_IDX_STATUS]) {
            html.append("  <td NOWRAP valign=\"top\">" + fix.getStatus() + "</td>\n");
        }


        // Render the release 
        if (visible[COLUMN_IDX_RELEASE]) {
            html.append("  <td NOWRAP valign=\"top\">" + fix.getRelease() + "</td>\n");
        }

        // Render the class 
        if (visible[COLUMN_IDX_CLASS]) {
            html.append("  <td NOWRAP valign=\"top\">" + fix.getType() + "</td>\n");
        }

        // Render the subclass 
        if (visible[COLUMN_IDX_SUBCLASS]) {
            html.append("  <td NOWRAP valign=\"top\">" + fix.getSubType() + "</td>\n");
        }

        // Render the requested by information
        if (visible[COLUMN_IDX_REQUEST]) {
            html.append("  <td NOWRAP valign=\"top\"></td>\n");
        }



        // Render the branch information
        if (visible[COLUMN_IDX_BRANCH]) {
            html.append("  <td NOWRAP valign=\"top\">");
            if (populate[COLUMN_IDX_BRANCH]) {
                String branchValue = fix.getVersionControlRoot();
                if ((branchValue == null) || branchValue.equalsIgnoreCase("null")) {
                    branchValue = "";
                }
                if (allowBranchInput && getInputMode()) {
                    html.append("<input type=\"text\" size=\"" + branchWidth + "\" maxlength=\"255\" name=\"" + IMnPatchForm.FIX_BRANCH_PREFIX + fix.getBugId() + "\" value=\"" + branchValue + "\"/>");
                } else {
                    html.append(branchValue);
                }
            }
            html.append("</td>\n");
        }


        // Render the environment of the fix
        if (visible[COLUMN_IDX_ENV]) {
            html.append("  <td NOWRAP valign=\"top\">");
            if (populate[COLUMN_IDX_ENV]) {
                if ((fix.getOrigin() != null) && 
                    (fix.getOrigin().getEnvironment() != null) &&
                    (fix.getOrigin().getEnvironment().getName() != null)) 
                {
                    html.append(fix.getOrigin().getEnvironment().getName());
                }
            }
            html.append("</td>\n");
        }


        // Render the origin of the fix
        if (visible[COLUMN_IDX_ORIGIN]) {
            html.append("  <td NOWRAP valign=\"top\">");
            if (populate[COLUMN_IDX_ORIGIN]) {
                if ((fix.getOrigin() != null) && (fix.getOrigin().getId() > 0)) {
                    if (patchUrl != null) {
                        String url = patchUrl.toString() + "?" + PATCH_ID_LABEL + "=" + fix.getOrigin().getId();
                        html.append("<a href=\"" + url + "\">" + fix.getOrigin().getName() + "</a>");
                    } else {
                        html.append(fix.getOrigin().getName());
                    }
                    html.append("<input type=\"hidden\" name=\"" + FIX_ORIGIN_PREFIX + fix.getBugId() + "\" value=\"" + fix.getOrigin().getId() + "\"/>");
                } else {
                    html.append("NEW");
                }
            }
            html.append("</td>\n");
        }


        // Render the notes textarea
        String notesValue = fix.getNotes();
        if ((notesValue == null) || notesValue.equalsIgnoreCase("null")) {
            notesValue = "";
        }
        if (visible[COLUMN_IDX_NOTES]) {
            html.append("  <td valign=\"top\"");
            if (getInputMode() || (notesValue.length() > 0)) {
                html.append(" onClick=\"show('div" + IMnPatchForm.FIX_NOTE_PREFIX + fix.getBugId() + "')\"");
            }
            html.append(">");
            if (populate[COLUMN_IDX_NOTES]) {
                String descValue = fix.getDescription();
                if (descValue != null) {
                    html.append(descValue);
                    if (descValue != null) {
                        html.append("<br/>\n");
                    }
                }

                html.append("<div id=\"div" + IMnPatchForm.FIX_NOTE_PREFIX + fix.getBugId() + "\" style=\"display:none;\">\n");
                html.append("<p><u><i>Service Patch Notes:</i></u><br/>\n");
                if (getInputMode()) {
                    html.append("<textarea cols=\"" + notesWidth + "\" rows=\"3\" name=\"" + IMnPatchForm.FIX_NOTE_PREFIX + fix.getBugId() + "\">" + notesValue + "</textarea>\n");
                } else {
                    html.append(notesValue);
                }
                html.append("</p>\n");
                html.append("</div>");
            }
            html.append("</td>\n");

        }

        // Display a notes icon if the row contains note data
        if (visible[COLUMN_IDX_ICONS]) {
            html.append("  <td valign=\"top\""); 
            if (getInputMode() || (notesValue.length() > 0)) {
                html.append(" onClick=\"toggleElement('div" + IMnPatchForm.FIX_NOTE_PREFIX + fix.getBugId() + "')\"");
            }
            html.append(">");
            if (notesValue.length() > 0) {
                html.append("<img src=\"" + imageUrl + "/icons_small/notice.png\">\n");
            } else {
                html.append("&nbsp;");
            }
            html.append("</td>\n");
        }


        html.append("</tr>\n");

        return html.toString();
    }

}


