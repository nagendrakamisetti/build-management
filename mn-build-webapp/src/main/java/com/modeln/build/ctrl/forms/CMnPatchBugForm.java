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
import com.modeln.build.common.data.product.CMnPatch;
import com.modeln.build.common.enums.CMnServicePatch;
import com.modeln.build.ctrl.database.CMnPatchTable;
import com.modeln.testfw.reporting.CMnBuildTable;
import com.modeln.testfw.reporting.CMnDbBuildData;
import com.modeln.testfw.reporting.search.CMnSearchCriteria;
import com.modeln.testfw.reporting.search.CMnSearchGroup;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import javax.servlet.http.HttpServletRequest;

import com.modeln.build.common.data.account.UserData;
import com.modeln.build.web.tags.TextTag;
import com.modeln.build.web.tags.DateTag;
import com.modeln.build.web.tags.SelectTag;
import com.modeln.build.web.tags.TagGroup;


/**
 * The patch bug form displays a list of patches and whether a list
 * of specified SDRs is fixed in each patch. 
 * The class manages transfering the data through the HTTP request.
 * Form data can be rendered in either a read-only or input mode.
 * 
 * @author  Shawn Stafford
 */
public class CMnPatchBugForm extends CMnPatchListForm implements IMnPatchForm {

    /** Build version field */
    private TextTag fixListTag = new TextTag(FIX_LIST);

    /** List of fixes provided in the fix list text field */
    private HashSet fixset = null;


    /**
     * Construct a patch request data list.  When input is enabled, the form
     * will be rendered with input fields.
     *
     * @param    patches          List of patches
     */
    public CMnPatchBugForm(URL form, URL images, Vector patches) {
        super(form, images, patches);
        fixListTag.setWidth(60);
    }



    /**
     * Extend the base method for enabling the input mode.  All input fields
     * must be enabled or disabled when this method is called. 
     *
     * @param enabled  TRUE to enable form input
     */
    public void setInputMode(boolean enabled) {
        super.setInputMode(enabled);
        fixListTag.setDisabled(!enabled);
    }

    /**
     * Set the input fields by examining the HTTP request to see if
     * a value was submitted.
     *
     * @param   req     HTTP request
     */
    public void setValues(HttpServletRequest req) {
        super.setValues(req);
        fixListTag.setValue(req);

        // Convert the text field input to a list of strings
        if (fixListTag.isComplete()) {
            StringTokenizer st = new StringTokenizer(fixListTag.getValue(), ",");
            fixset = new HashSet(st.countTokens());
            while (st.hasMoreTokens()) {
                String value = st.nextToken();
                if ((value != null) && (value.trim().length() > 0)) {
                    fixset.add(value.trim());
                }
            }
        }
    }


    /**
     * Return the list of fixes parsed from the text field.
     * 
     * @return  List of fixes
     */
    public HashSet getFixes() {
        return fixset;
    }

    /**
     * Set the list of customers in the customer dropdown menu.
     *
     * @param  customers    List of customers
     */
    public void setCustomers(Vector customers) {
        Hashtable customerNames = new Hashtable();

        // Populate the select list with customers
        CMnAccount customer = null;
        for (Enumeration e = customers.elements(); e.hasMoreElements(); ) {
            customer = (CMnAccount) e.nextElement();
            customerNames.put(customer.getId().toString(), customer.getName());
        }

        // Add a prompt to force the user to select an item
        if (customerNames.size() > 0) {
            customerNames.put("", "...All...");
            customerTag.setOptions(customerNames);
            //customerTag.setSorting(true);
            customerTag.sortByValue();
            customerTag.setDefault("");
        }
    }


    /**
     * Render the build data form.
     */
    public String toString() {
        StringBuffer html = new StringBuffer();

        html.append("<form method=\"POST\" action=" + getFormUrl() + ">\n");
        html.append("Fix List: " + fixListTag.toString());
        html.append("<input type=\"submit\" value=\"Search\">\n");
        html.append("<table width=\"100%\" class=\"spreadsheet\">\n");
        html.append(getHeader());
        if (inputEnabled) {
            html.append(getInputFields());
        }
        html.append(getBuildList());
        html.append(getFooter());
        html.append("</table>\n");
        html.append("</form>\n");

        return html.toString();
    }


    /**
     * Construct a table header.
     */
    private String getHeader() {
        StringBuffer html = new StringBuffer();
        html.append("<tr class=\"spreadsheet-header\">\n");
        html.append("  <td nowrap width=\"3%\">&nbsp;</td>\n");
        html.append("  <td nowrap width=\"10%\">Customer</td>\n");
        html.append("  <td nowrap width=\"5%\">Patch</td>\n");
        html.append("  <td nowrap width=\"5%\">Fixes</td>\n");
        html.append("  <td nowrap width=\"20%\">Product Version</td>\n");
        if (fixset != null) {
            html.append("  <td nowrap colspan=\"" + fixset.size() + "\">Fixes</td>\n");
        }
        html.append("</tr>\n");

        return html.toString();
    }


    /**
     * Construct a table footer.
     */
    private String getFooter() {
        StringBuffer html = new StringBuffer();
        int colspan = 5;
        if (fixset != null) {
            colspan = colspan + fixset.size();
        }
        html.append("<tr class=\"spreadsheet-footer\">\n");
        html.append("  <td nowrap colspan=\"" + colspan + "\">&nbsp;</td>\n");
        html.append("</tr>\n");
        return html.toString();
    }


    /**
     * Construct a table header containing input fields.
     */
    private String getInputFields() {
        StringBuffer html = new StringBuffer();

        html.append("<tr class=\"spreadsheet-subheader\">\n");
        html.append("  <td nowrap>" + resultSizeTag.toString() + "</td>\n");
        html.append("  <td nowrap>" + customerTag.toString() + "</td>\n");
        html.append("  <td nowrap><!-- Patch Name --></td>\n");
        html.append("  <td nowrap><!-- Fixes --></td>\n");
        html.append("  <td nowrap>" + buildVersionTag.toString() + "</td>\n");
        if (fixset != null) {
            Iterator iter = fixset.iterator();
            while (iter.hasNext()) {
                html.append("  <td nowrap width=\"50\">" + iter.next() + "</td>\n");
            }
        }
        html.append("</tr>\n");

        return html.toString();
    }


    /**
     * Generate the table rows containing the list of patches.
     */
    private String getBuildList() {
        StringBuffer html = new StringBuffer();

        CMnPatch currentPatch = null;
        for (int idx = 0; idx < requestList.size(); idx++) {
            currentPatch = (CMnPatch) requestList.get(idx);
            html.append("<tr class=\"spreadsheet-shaded\">\n");

            html.append("  <td align=\"right\">" + (idx + 1) + "</td>\n");

            // Display the customer
            CMnAccount customer = currentPatch.getCustomer();
            html.append("  <td NOWRAP>" + customer.getName() + "</td>\n");

            // Display the patch name
            String patchHref = patchUrl + "?" + IMnPatchForm.PATCH_ID_LABEL + "=" + currentPatch.getId();
            html.append("  <td><a href=\"" + patchHref + "\">" + currentPatch.getName() + "</a></td>\n");

            // Display the number of fixes in the patch
            Vector fixes = currentPatch.getFixes();
            html.append("  <td align=\"right\">");
            if (fixes != null) {
                html.append(fixes.size());
            } else {
                html.append("0");
            }
            html.append("</td>\n");

            // Display the build version number 
            CMnDbBuildData currentBuild = currentPatch.getBuild();
            String buildHref = buildUrl + "?" + IMnBuildForm.BUILD_ID_LABEL + "=" + currentBuild.getId();
            html.append("  <td NOWRAP><tt><a href=\"" + buildHref + "\">" + currentBuild.getBuildVersion() + "</a></tt></td>\n");

            // Display the status of the fix for the current patch
            if (fixset != null) {
                Iterator iter = fixset.iterator();
                while (iter.hasNext()) { 
                    html.append("  <td>");
                    int targetFix = Integer.parseInt((String) iter.next());
                    // Determine if the patch contains the fix
                    if (currentPatch.hasFix(targetFix)) {
                        html.append("X");
                    } 
                    html.append("  </td>\n");
                }
            }

            html.append("</tr>\n");
        }

        return html.toString();
    }


    /**
     * Construct a table header.
     */
    private String getFixTable() {
        StringBuffer html = new StringBuffer();
        html.append("<div id=\"tableWrapper\" style=\"overflow-x: auto; width: 500px;float:left;padding-bottom: 10px;\">\n");
        html.append("<table>\n"); 
        html.append("<tr class=\"spreadsheet-header\">\n");
        if (fixset != null) {
            html.append("  <td nowrap colspan=\"" + fixset.size() + "\">Fixes</td>\n");
        }
        html.append("</tr>\n");
        html.append("</table>\n");

        return html.toString();
    }


}
