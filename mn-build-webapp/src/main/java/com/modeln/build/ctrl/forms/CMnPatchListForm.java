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
import java.util.Hashtable;
import javax.servlet.http.HttpServletRequest;

import com.modeln.build.common.data.account.UserData;
import com.modeln.build.web.tags.TextTag;
import com.modeln.build.web.tags.DateTag;
import com.modeln.build.web.tags.SelectTag;
import com.modeln.build.web.tags.TagGroup;


/**
 * The patch list form provides an HTML interface to the patch requset data object.
 * The class manages transfering the data through the HTTP request.
 * Form data can be rendered in either a read-only or input mode.
 * 
 * @author  Shawn Stafford
 */
public class CMnPatchListForm extends CMnBaseTableForm implements IMnPatchForm {

    /** Patch request status field */
    private SelectTag statusTag = new SelectTag(PATCH_STATUS_LABEL);

    /** Customer name field */
    private SelectTag customerTag = new SelectTag(CUSTOMER_ID_LABEL);


    /** Build version field */
    private TextTag buildVersionTag = new TextTag(BUILD_VERSION_LABEL);

    /** Patch request date field */
    private DateTag requestDateTag = new DateTag("requestDate");

    /** Request date query operator field */
    private SelectTag requestDateOp;

    /** Tag group to contain the date fields. */
    private TagGroup requestDateGroup = new TagGroup("requestDateGroup");



    /** List of patch request data objects. */
    private Vector requestList = null;


    /** URL for deleting a table entry */
    private URL deleteUrl = null;

    /** URL for accessing build information */
    private URL buildUrl = null;

    /** URL for editing patch information */
    private URL patchUrl = null;


    /**
     * Construct a patch request data list.  When input is enabled, the form
     * will be rendered with input fields.
     *
     * @param    patches          List of patches
     */
    public CMnPatchListForm(URL form, URL images, Vector patches) {
        super(form, images);
        requestList = patches;
        buildVersionTag.setWidth(30);

        // Construct a list of date comparison operators
        Hashtable dateOps = new Hashtable(5);
        dateOps.put(CMnSearchCriteria.GREATER_THAN, ">");
        dateOps.put(CMnSearchCriteria.LESS_THAN, "<");
        dateOps.put(CMnSearchCriteria.LIKE, "="); 
        requestDateOp = new SelectTag("patch_date_op", dateOps);

        // Group the date tags
        requestDateGroup.add(requestDateOp);
        requestDateGroup.add(requestDateTag);
        requestDateGroup.setUserDisable(true);
        requestDateGroup.setSubmitOnChange(true);
        requestDateGroup.enableGroup(false);

        // Construct the status list
        Hashtable statusList = new Hashtable();
        statusList.put("", "...All...");
        for (CMnServicePatch.RequestStatus status : CMnServicePatch.RequestStatus.values()) {
            statusList.put(status.name(), status.name());
        } 
        statusTag.setOptions(statusList);
        statusTag.setDefault("");
        //statusTag.setSorting(true);
        statusTag.sortByValue();

    }

    /**
     * Set the URL used to delete build entries.
     *
     * @param  url   Link to the command related to build deletion
     */
    public void setDeleteUrl(URL url) {
        deleteUrl = url;
    }

    /**
     * Set the URL used to retrieve build information 
     *
     * @param  url   Link to the command related to build information 
     */
    public void setBuildUrl(URL url) {
        buildUrl = url;
    }

    /**
     * Set the URL used to view/edit patch information
     *
     * @param  url   Link to the command related to patch information
     */
    public void setPatchUrl(URL url) {
        patchUrl = url;
    }



    /**
     * Extend the base method for enabling the input mode.  All input fields
     * must be enabled or disabled when this method is called. 
     *
     * @param enabled  TRUE to enable form input
     */
    public void setInputMode(boolean enabled) {
        super.setInputMode(enabled);
        buildVersionTag.setDisabled(!enabled);
        requestDateGroup.setDisabled(!enabled);
        statusTag.setDisabled(!enabled);
        customerTag.setDisabled(!enabled);
        inputEnabled = enabled;
    }

    /**
     * Set the input fields by examining the HTTP request to see if
     * a value was submitted.
     *
     * @param   req     HTTP request
     */
    public void setValues(HttpServletRequest req) {
        super.setValues(req);
        buildVersionTag.setValue(req);
        requestDateGroup.setValue(req);
        statusTag.setValue(req);
        customerTag.setValue(req);
    }


    /**
     * Return a search group that describes all of the selected form input.
     *
     * @return Form values as a database search group
     */
    public CMnSearchGroup getValues() {
        CMnSearchGroup group = new CMnSearchGroup(CMnSearchGroup.AND);

        // Construct the search criteria for the customer
        if (customerTag.isComplete()) {
            String[] selectedCustomers = customerTag.getSelected();
            if ((selectedCustomers[0] != null) && (selectedCustomers[0].length() > 0)) {
                CMnSearchCriteria customerCriteria = new CMnSearchCriteria(
                    CMnPatchTable.REQUEST_TABLE,
                    CMnPatchTable.ACCOUNT_ID,
                    CMnSearchCriteria.EQUAL_TO, 
                    selectedCustomers[0]
                );
                group.add(customerCriteria);
            }
        }

        // Construct the search criteria for the status
        if (statusTag.isComplete()) {
            String[] selectedStatus = statusTag.getSelected();
            if ((selectedStatus[0] != null) && (selectedStatus[0].length() > 0)) {
                CMnSearchCriteria statusCriteria = new CMnSearchCriteria(
                    CMnPatchTable.REQUEST_TABLE,
                    CMnPatchTable.REQUEST_STATUS,
                    CMnSearchCriteria.EQUAL_TO,
                    selectedStatus[0] 
                );
                group.add(statusCriteria);
            }
        }

        // Construct the search criteria for the build version
        if (buildVersionTag.isComplete()) {
            CMnSearchCriteria versionCriteria = new CMnSearchCriteria(
                CMnBuildTable.BUILD_TABLE,
                CMnBuildTable.BUILD_VERSION,
                CMnSearchCriteria.LIKE,
                buildVersionTag.getValue()
            );
            group.add(versionCriteria);
        }

        // Construct the search criteria for the build date
        if (requestDateGroup.isComplete()) {
            String[] dateOp = requestDateOp.getSelected();
            CMnSearchCriteria dateCriteria = new CMnSearchCriteria(
                CMnBuildTable.BUILD_TABLE,
                CMnBuildTable.BUILD_START_DATE,
                dateOp[0],
                sqlDateFormat.format(requestDateTag.getDate())
            );
            group.add(dateCriteria);
        }

        return group;
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

        html.append("<form action=" + getFormUrl() + ">\n");
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
        html.append("  <td nowrap width=\"2%\">&nbsp;</td>\n");
        html.append("  <td nowrap width=\"3%\">&nbsp;</td>\n");
        html.append("  <td nowrap width=\"10%\">Customer</td>\n");
        html.append("  <td nowrap width=\"15%\">Environment</td>\n");
        html.append("  <td nowrap width=\"5%\">Patch</td>\n");
        html.append("  <td nowrap width=\"5%\">Fixes</td>\n");
        html.append("  <td nowrap width=\"5%\">Build</td>\n");
        html.append("  <td nowrap width=\"15%\">Request Date</td>\n");
        html.append("  <td nowrap width=\"30%\">Product Version</td>\n");
        html.append("  <td nowrap width=\"10%\">Request Status</td>\n");
        html.append("</tr>\n");

        return html.toString();
    }


    /**
     * Construct a table footer.
     */
    private String getFooter() {
        StringBuffer html = new StringBuffer();
        html.append("<tr class=\"spreadsheet-footer\">\n");
        html.append("  <td nowrap colspan=\"10\">");
        html.append("<a href=\"" + patchUrl + "\">New Patch Request</a>");
        html.append("</td>\n");
        html.append("</tr>\n");
        return html.toString();
    }


    /**
     * Construct a table header containing input fields.
     */
    private String getInputFields() {
        StringBuffer html = new StringBuffer();

        html.append("<tr class=\"spreadsheet-subheader\">\n");
        html.append("  <td align=\"center\"><input type=\"submit\" value=\"Go\"></a></td>\n");
        html.append("  <td nowrap>" + resultSizeTag.toString() + "</td>\n");
        html.append("  <td nowrap>" + customerTag.toString() + "</td>\n");
        html.append("  <td nowrap><!-- Environment Name --></td>\n");
        html.append("  <td nowrap><!-- Patch Name --></td>\n");
        html.append("  <td nowrap><!-- Fixes --></td>\n");
        html.append("  <td nowrap><!-- Build --></td>\n");
        html.append("  <td nowrap><!-- requestDateGroup.toString() --></td>\n");
        html.append("  <td nowrap>" + buildVersionTag.toString() + "</td>\n");
        html.append("  <td nowrap>" + statusTag.toString() + "</td>\n");
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

            // Display the edit/delete controls for administrators
            html.append("  <td align=\"center\">\n");
            if (adminEnabled) {
                String deleteHref = deleteUrl + "?" + PATCH_ID_LABEL + "=" + currentPatch.getId();
                html.append("    <a href=\"" + deleteHref + "\"><img border=\"0\" src=\"" + getImageUrl() + "/icons_small/trashcan_red.png\" alt=\"Delete\"></a>\n");
            } else { 
                html.append("    &nbsp;\n");
            } 
            html.append("  </td>\n");
            html.append("  <td align=\"right\">" + (idx + 1) + "</td>\n");

            // Display the customer
            CMnAccount customer = currentPatch.getCustomer();
            html.append("  <td NOWRAP>" + customer.getName() + "</td>\n");

            // Display the environment
            CMnEnvironment env = currentPatch.getEnvironment();
            html.append("  <td NOWRAP>");
            if (env != null) {
                html.append(env.getName());
            } else {
                html.append("&nbsp;");
            } 
            html.append("</td>\n");

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

            // Display the service patch build
            html.append("  <td align=\"right\">");
            if (currentPatch.getPatchBuild() != null) {
                int patchBuildId = currentPatch.getPatchBuild().getId();
                String hrefBuild = getPatchBuildUrl(Integer.toString(patchBuildId));
                html.append("<a href=\"" + hrefBuild + "\">" + patchBuildId + "</a>");
            }
            html.append("</td>\n");

            // Display the patch request date
            html.append("  <td>");
            if (currentPatch.getRequestDate() != null) {
                html.append("<tt>" + shortDateFormat.format(currentPatch.getRequestDate()) + "</tt>");
            } else {
                html.append("&nbsp;");
            }
            html.append("</td>\n");

            // Display the build version number 
            CMnDbBuildData currentBuild = currentPatch.getBuild();
            String buildHref = buildUrl + "?" + IMnBuildForm.BUILD_ID_LABEL + "=" + currentBuild.getId();
            html.append("  <td NOWRAP><tt><a href=\"" + buildHref + "\">" + currentBuild.getBuildVersion() + "</a></tt></td>\n");

            // Display the patch status
            String statusName = "";
            if (currentPatch.getStatus() != null) {
                statusName = currentPatch.getStatus().name();
            }
            html.append("  <td>" + statusName + "</td>\n");

            html.append("</tr>\n");
        }

        return html.toString();
    }



}
