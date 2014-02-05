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

import com.modeln.build.common.data.product.CMnBaseFixDependency;
import com.modeln.build.common.data.product.CMnPatch;
import com.modeln.build.common.data.product.CMnPatchFix;

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
 * The build form provides an HTML interface to the service patch fix
 * dependency list.  The class manages transfering the data through 
 * the HTTP request.  Form data can be rendered in either a read-only 
 * or input mode.
 *
 * @author  Shawn Stafford
 */
public class CMnFixDependencyForm extends CMnBaseForm implements IMnPatchForm {


    /** List of dependencies */
    private CMnPatch patch;

    /** Input field for adding a source bug ID */
    private TextTag sourceBugTag;

    /** Input field for adding a target bug ID */
    private TextTag targetBugTag;

    /** Input field for the dependency type */
    private SelectTag depTypeTag;

    /**
     * Construct a form for selecting fix origins for the service patch 
     *
     * @param    form     URL for submitting form data
     * @param    images   URL for displaying HTML images
     */
    public CMnFixDependencyForm(URL form, URL images) {
        super(form, images);

        sourceBugTag = new TextTag(FIX_BUG_LABEL);
        sourceBugTag.setWidth(10);

        targetBugTag = new TextTag(DEPENDENCY_BUG_LABEL);
        targetBugTag.setWidth(10);

        depTypeTag = new SelectTag(DEPENDENCY_TYPE_LABEL, CMnBaseFixDependency.getTypeList());
        depTypeTag.setDefault(CMnBaseFixDependency.DependencyType.MERGE.toString());

    }


    /**
     * Set the input fields by examining the HTTP request to see if
     * a value was submitted.
     *
     * @param   req     HTTP request
     */
    public void setValues(HttpServletRequest req) {
        super.setValues(req);

        // Populate the list of dependencies
        patch = (CMnPatch) req.getAttribute(PATCH_DATA);

        // Save user input values
        sourceBugTag.setValue(req);
        targetBugTag.setValue(req);
        depTypeTag.setValue(req);
    }


    /**
     * Render the patch request form as HTML.
     */
    public String toString() {
        StringBuffer html = new StringBuffer();

        boolean allowInput = adminEnabled;

        if ((patch != null) && (patch.getFixes() != null)) {
            if (allowInput) {
                html.append("<form method=\"POST\" action=\"" + getFormUrl() + "\">\n");
            }

            html.append("<input type=\"hidden\" name=\"" + PATCH_ID_LABEL + "\" value=\"" + patch.getId() + "\"/>\n");
            html.append("<table width=\"100%\" class=\"spreadsheet\">\n");
            html.append("  <tr class=\"spreadsheet-header\">\n");
            html.append("    <td>Fix</td>\n");
            html.append("    <td>Depends On</td>\n");
            html.append("    <td>Dependency Type</td>\n");
            if (allowInput) {
                html.append("    <td width=\"50\" align=\"center\">&nbsp;</td>\n");
            }
            html.append("  </tr>\n");

            // Iterate through each fix in the patch
            Enumeration fixlist = patch.getFixes().elements();
            while (fixlist.hasMoreElements()) {
                CMnPatchFix fix = (CMnPatchFix) fixlist.nextElement();
                Vector<CMnBaseFixDependency> dependencies = fix.getDependencies();
                // Display the list of existing dependencies
                if (dependencies != null) {
                    html.append("  <!-- List of " + dependencies.size() + " dependencies -->\n");
                    Enumeration list = dependencies.elements();
                    while (list.hasMoreElements()) {
                        CMnBaseFixDependency dep = (CMnBaseFixDependency) list.nextElement();
                        html.append("  <tr class=\"spreadsheet-shaded\">\n");
                        html.append("    <td>" + fix.getBugId() + "</td>\n");
                        html.append("    <td>" + dep.getBugId() + "</td>\n");
                        html.append("    <td>" + dep.getType() + "</td>\n");
                        if (allowInput) {
                            html.append("    <td width=\"50\" align=\"center\">\n");
                            String href = getDeleteUrl() + "?" +
                                PATCH_ID_LABEL + "=" + patch.getId() + "&" +
                                FIX_BUG_LABEL  + "=" + fix.getBugId() + "&" +
                                DEPENDENCY_BUG_LABEL + "=" + dep.getBugId(); 
                            html.append("<a href=\"" + href + "\">");
                            html.append("<img src=\"" + getImageUrl().toString() + "/icons_small/trashcan_red.png\"/>");
                            html.append("</a>\n");
                            html.append("    </td>\n");
                        }
                        html.append("  </tr>\n");
                    }
                }
            }

            // Allow the user to add a new dependency
            if (allowInput) {
                html.append("  <!-- New dependency -->\n");
                html.append("  <tr class=\"spreadsheet-shaded\">\n");

                // Source bug number
                html.append("    <td>");
                html.append("      <table>\n");
                html.append("        <tr>\n");
                html.append("          <td valign=\"middle\">" + sourceBugTag.toString() + "</td>\n");
                if ((formErrors != null) && formErrors.containsKey(FIX_BUG_LABEL)) {
                    html.append("          <td valign=\"middle\">\n");
                    html.append("            <img src=\"" + getImageUrl().toString() + "/icons_small/important.png\" title=\"" + formErrors.get(FIX_BUG_LABEL) + "\"/>");
                    html.append("          </td>\n");
                }
                html.append("        </tr>\n");
                html.append("      </table>\n");
                html.append("    </td>\n");

                // Target bug number
                html.append("    <td>");
                html.append("      <table>\n");
                html.append("        <tr>\n");
                html.append("          <td valign=\"middle\">" + targetBugTag.toString() + "</td>\n");
                if ((formErrors != null) && formErrors.containsKey(DEPENDENCY_BUG_LABEL)) {
                    html.append("          <td valign=\"middle\">\n");
                    html.append("<img src=\"" + getImageUrl().toString() + "/icons_small/important.png\" title=\"" + formErrors.get(DEPENDENCY_BUG_LABEL) + "\"/>");
                    html.append("          </td>\n");
                }
                html.append("        </tr>\n");
                html.append("      </table>\n");
                html.append("    </td>\n");

                // Dependency type
                html.append("    <td>");
                html.append("      <table>\n");
                html.append("        <tr>\n");
                html.append("          <td valign=\"middle\">" + depTypeTag.toString() + "</td>\n");
                if ((formErrors != null) && formErrors.containsKey(DEPENDENCY_TYPE_LABEL)) {
                    html.append("          <td valign=\"middle\">\n");
                    html.append("<img src=\"" + getImageUrl().toString() + "/icons_small/important.png\" title=\"" + formErrors.get(DEPENDENCY_TYPE_LABEL) + "\"/>");
                    html.append("          </td>\n");
                }
                html.append("        </tr>\n");
                html.append("      </table>\n");
                html.append("    </td>\n");

                // Add button
                html.append("    <td width=\"50\" align=\"center\"><input type=\"submit\" name=\"" + DEPENDENCY_BUTTON + "\" value=\"Add\"/></td>\n");

                html.append("  </tr>\n");

            }

            html.append("</table>\n");

            if (allowInput) {
                html.append("</form>\n");
            }

        } else {
            html.append("No patch data.");
        }

        return html.toString();

    }




}
