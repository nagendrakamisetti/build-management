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

import com.modeln.build.common.data.product.CMnPatch;
import com.modeln.build.common.data.product.CMnPatchDateComparator;
import com.modeln.build.common.data.product.CMnPatchFix;
import com.modeln.testfw.reporting.CMnDbBuildData;

import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import java.util.Hashtable;
import javax.servlet.http.HttpServletRequest;

import com.modeln.build.web.tags.TextTag;
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
public class CMnFixOriginForm extends CMnBaseForm implements IMnPatchForm {

    /** Service patch information */
    private CMnPatch patch = null;

    /** List of columns */
    private Vector<CMnPatch> columns = null;

    /** List of patch fixes associated with each bug */
    private Hashtable<Integer, Vector<CMnPatch>> fixes = null;


    /**
     * Construct a form for selecting fix origins for the service patch 
     *
     * @param    form     URL for submitting form data
     * @param    images   URL for displaying HTML images
     */
    public CMnFixOriginForm(URL form, URL images) {
        super(form, images);
    }


    /**
     * Set the input fields by examining the HTTP request to see if
     * a value was submitted.
     *
     * @param   req     HTTP request
     */
    public void setValues(HttpServletRequest req) {
        super.setValues(req);

        patch = (CMnPatch) req.getAttribute(IMnPatchForm.PATCH_DATA);
        fixes = (Hashtable<Integer, Vector<CMnPatch>>) req.getAttribute(IMnPatchForm.FIX_LIST);
    }

    /**
     * Set the service patch information.
     * 
     * @param  patch  Service patch information
     */
    public void setPatch(CMnPatch patch) {
        this.patch = patch;
    }


    /**
     * Set the list of fixes associated with the patch.
     * The fix list should be composed of a hashtable where the key 
     * is the BugID and the value is a Vector containing a list of 
     * related patch IDs.
     * 
     * @param   fixes   List of fixes
     */
    public void setFixes(Hashtable<Integer, Vector<CMnPatch>> fixes) {
        this.fixes = fixes;

        // Create a list of columns based on the fix list
        if (fixes != null) {
            Iterator values = fixes.values().iterator();
            while (values.hasNext()) {
                Vector<CMnPatch> value = (Vector<CMnPatch>) values.next();
                Enumeration fixList = value.elements();
                while (fixList.hasMoreElements()) {
                    addColumn((CMnPatch) fixList.nextElement());
                }
            }
        }

        if (columns != null) {
            Collections.sort(columns, new CMnPatchDateComparator());
        }
    }


    /**
     * Add the patch to the list of patches if it is not already 
     * part of the list.  The list of patches is assumed to be
     * sorted by patch ID, so new items will be inserted into the
     * list based on that value.
     *
     * @param   patch      Patch to add to the list
     */
    private boolean addColumn(CMnPatch patch) {
        boolean added = false;

        // Make sure the list of columns is initialized
        if (columns == null) {
            columns = new Vector<CMnPatch>();
        }

        // Determine if the patch already exists in the list
        boolean found = false;
        int idx = 0;
        CMnPatch current = null;
        Enumeration patchList = columns.elements();
        while (patchList.hasMoreElements() && !found) {
            idx++;
            current = (CMnPatch) patchList.nextElement();
            if (current.getId().equals(patch.getId())) {
                found = true;
            }
        }

        // Add the patch to the list if necessary
        if (!found) {
            columns.add(patch);
            added = true;
        }

        return added;
    }


    /**
     * Determine if the current patch contains the specified bug ID
     * AND the bug originated from the specified patch ID.
     * 
     * @return TRUE if the bug origin matches the specified patch ID
     */
    private boolean hasOrigin(int bugId, int patchId) {
        boolean result = false;

        if ((patch != null) && (patch.getFixes() != null)) {
            Enumeration fixList = patch.getFixes().elements();
            while (fixList.hasMoreElements()) {
                CMnPatchFix currentFix = (CMnPatchFix) fixList.nextElement();
                Integer originId = null;
                if (currentFix.getOrigin() != null) {
                    int currentBugId = currentFix.getBugId();
                    int currentOrigin = currentFix.getOrigin().getId().intValue();
                    if ((bugId == currentBugId) && (patchId == currentOrigin)) {
                        return true;
                    } 
                }
            } 
        }

        return result;
    }


    /**
     * Return the list of columns formatted as HTML columns.
     */
    private String getHeader() {
        StringBuffer html = new StringBuffer();

        if ((columns != null) && (columns.size() > 0)) {
            html.append("<tr>\n");
            html.append("  <td bgcolor=\"#AAAAAA\">Bug ID</td>\n");
            Enumeration columnList = columns.elements();
            while (columnList.hasMoreElements()) {
                CMnPatch column = (CMnPatch) columnList.nextElement();
                html.append("<td bgcolor=\"#AAAAAA\" align=\"center\">" + column.getName() + "</td>\n");
            }
            html.append("</tr>\n");
        }

        return html.toString();
    }



    /**
     * Render the patch request form as HTML.
     */
    public String toString() {
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

        if (fixes != null) {
            html.append("<!-- DEBUG fixes hashtable -->\n");
            Collection values = fixes.values();
            Iterator iter = values.iterator();
            while (iter.hasNext()) {
                Vector<CMnPatch> origins = (Vector<CMnPatch>) iter.next();
                html.append("<!-- " + origins.size() + " -->\n");
            }
        }

        // Display a table containing the list of bugs and patch origins
        if ((patch != null) && (patch.getFixes() != null) && (columns != null) && (columns.size() > 0)) {
            html.append("<input type=\"hidden\" name=\"" + IMnPatchForm.PATCH_ID_LABEL + "\" value=\"" + patch.getId() + "\"/>");
            html.append("<table border=\"0\" cellspacing=\"1\" cellpadding=\"1\">\n");
            String header = getHeader();
            if (header.length() > 0) {
                html.append(header);
            }

            // Render a row for each bug
            Enumeration bugs = patch.getFixes().elements(); 
            while (bugs.hasMoreElements()) {
                CMnPatchFix fix = (CMnPatchFix) bugs.nextElement();
                html.append("<tr>\n");
                html.append("  <td align=\"right\">&nbsp;<b>" + fix.getBugId() + "</b>&nbsp;</td>\n");

                // Render each column in the defined list of columns
                Enumeration columnList = columns.elements();
                while (columnList.hasMoreElements()) {
                    CMnPatch currentColumn = (CMnPatch) columnList.nextElement();

                    StringBuffer content = new StringBuffer();
                    String color = "";

                    // Determine if each origin belongs to the current column
                    boolean belongs = false;
                    Vector<CMnPatch> origins = fixes.get(fix.getBugId());
                    if ((origins != null) && (origins.size() > 0)) {
                        Enumeration originList = origins.elements();
                        while (originList.hasMoreElements()) {
                            CMnPatch current = (CMnPatch) originList.nextElement();
                            if (current.getId().equals(currentColumn.getId())) {
                                belongs = true; 
                            }
                        }

                        // Render the column input field
                        if (belongs) {
                            String checked = "";
                            if (hasOrigin(fix.getBugId(), currentColumn.getId().intValue())) {
                                checked = "checked";
                                color = "bgcolor=\"#CCCCCC\"";
                            }
                            content.append("<input type=\"radio\" name=\"" + IMnPatchForm.FIX_ORIGIN_PREFIX + fix.getBugId() + "\" value=\"" + currentColumn.getId() + "\" " + checked + "/>\n");
                        }

                    } else {
                        content.append("&nbsp;");
                    }

                    // Render the table cell
                    html.append("  <td align=\"center\" " + color + ">");
                    html.append(content.toString());
                    html.append("</td>\n");
                }
                html.append("</tr>\n");
            }

            html.append("</table>\n");

        } else if ((columns == null) || (columns.size() == 0)) {
            html.append("No columns defined.");
        }


        if (inputEnabled) {
            html.append("</form>\n");
        }

        return html.toString();

    }




}
