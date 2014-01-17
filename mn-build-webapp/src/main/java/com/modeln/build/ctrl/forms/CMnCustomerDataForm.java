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
import com.modeln.build.common.data.product.CMnProduct;
import com.modeln.testfw.reporting.CMnDbBuildData;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
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
 * The customer form provides an HTML interface to the customer data object.
 * The class manages transfering the data through the HTTP request.
 * Form data can be rendered in either a read-only or input mode.
 *
 * @author  Shawn Stafford
 */
public class CMnCustomerDataForm extends CMnBaseReleaseForm implements IMnPatchForm {

    /** Save a reference to the customer data so it can be used later */
    private CMnAccount customer = null;

    /** Customer ID */
    private TextTag custIdTag;

    /** Customer name */
    private TextTag nameTag;

    /** Short customer name */
    private TextTag shortNameTag;

    /** List of branch types */
    private SelectTag branchTypeTag;


    /** URL for deleting a table entry */
    private URL deleteUrl = null;

    /** URL for accessing build information */
    private URL buildUrl = null;

    /** URL for accessing environment information */
    private URL envUrl = null;




    /**
     * Construct a form for editing customer data. 
     *
     * @param    form     URL for submitting form data
     * @param    images   URL for displaying HTML images
     */
    public CMnCustomerDataForm(URL form, URL images) {
        super(form, images);

        custIdTag = new TextTag(CUSTOMER_ID_LABEL);
        custIdTag.setWidth(10);

        nameTag = new TextTag(CUSTOMER_NAME_LABEL);
        nameTag.setWidth(30);

        shortNameTag = new TextTag(CUSTOMER_SHORT_NAME_LABEL);
        shortNameTag.setWidth(20);

        branchTypeTag = new SelectTag(CUSTOMER_BRANCH_TYPE_LABEL, CMnAccount.getBranchTypeList());
        branchTypeTag.setDefault(CMnAccount.BranchType.PRODUCT.toString());
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
     * Set the URL used to view/edit environment information
     *
     * @param  url   Link to the command related to environment information
     */
    public void setEnvUrl(URL url) {
        envUrl = url;
    }



    /**
     * Hide the form elements
     *
     * @param  hidden   TRUE if the form elements should be hidden
     */
    public void setHidden(boolean hidden) {
        custIdTag.setHidden(hidden);
        nameTag.setHidden(hidden);
        shortNameTag.setHidden(hidden);
        branchTypeTag.setHidden(hidden);
    }

    /**
     * Extend the base method for enabling the input mode.  All input fields
     * must be enabled or disabled when this method is called.
     *
     * @param enabled  TRUE to enable form input
     */
    public void setInputMode(boolean enabled) {
        super.setInputMode(enabled);

        custIdTag.setDisabled(!enabled);
        nameTag.setDisabled(!enabled);
        shortNameTag.setDisabled(!enabled);
        branchTypeTag.setDisabled(!enabled);
    }


    /**
     * Set the input fields by examining the HTTP request to see if
     * a value was submitted.
     *
     * @param   req     HTTP request
     */
    public void setValues(HttpServletRequest req) {
        // Check the request attribute for a data object with the default/previous values
        customer = (CMnAccount) req.getAttribute(CUSTOMER_DATA);
        if (customer != null) {
            setValues(customer);
        }

        //
        // Override the request attribute data with form input values
        //
        if (custIdTag.isValueAvailable(req)) {
            custIdTag.setValue(req);
        }
        if (nameTag.isValueAvailable(req)) {
            nameTag.setValue(req);
        }
        if (shortNameTag.isValueAvailable(req)) {
            shortNameTag.setValue(req);
        }
        if (branchTypeTag.isValueAvailable(req)) {
            branchTypeTag.setValue(req);
        }

    }


    /**
     * Set the form information using customer data.
     *
     * @param   cust   Customer information
     */
    public void setValues(CMnAccount cust) {
        customer = cust;
        custIdTag.setValue(cust.getId().toString());
        nameTag.setValue(cust.getName());
        shortNameTag.setValue(cust.getShortName());
        if (cust.getBranchType() != null) {
            branchTypeTag.setSelected(cust.getBranchType().toString());
        }
    }


    /**
     * Render the patch request form as HTML.
     */
    public String toString() {
        StringBuffer html = new StringBuffer();

        if (inputEnabled) {
            String method = null;
            if (postMethodEnabled) {
                method = "post";
            } else {
                method = "get";
            }
            html.append("<form method=\"" + method + "\" action=\"" + getFormUrl() + "\">\n");
        }

        // Always render the customer ID as a hidden field since a primary key should never change
        // But don't render the value if it is null
        if (custIdTag.isComplete()) {
            custIdTag.setHidden(true);
            html.append(custIdTag.toString());
        }


        html.append("<table border=\"0\" cellspacing=\"2\" cellpadding=\"2\">\n");
        html.append("  <tr>\n");
        html.append("    <td>Customer Name:</td>\n");
        html.append("    <td>");
        html.append(nameTag.toString());
        html.append("<input type=\"submit\" name=\"" + CUSTOMER_DATA_BUTTON + "\" value=\"Submit\"/>");
        html.append("</td>\n");
        html.append("  </tr>\n");

        html.append("  <tr>\n");
        html.append("    <td>Short Name:</td>\n");
        html.append("    <td>");
        html.append(shortNameTag.toString());
        html.append("</td>\n");
        html.append("  </tr>\n");

        html.append("  <tr>\n");
        html.append("    <td>Branch Type:</td>\n");
        html.append("    <td>");
        html.append(branchTypeTag.toString());
        html.append("</td>\n");
        html.append("  </tr>\n");


        if ((customer != null) && (customer.getEnvironments() != null)) {
            html.append("  <tr>\n");
            html.append("    <td valign=top>Environments:</td>\n");
            html.append("    <td valign=top>\n");

            html.append("      <table class=\"spreadsheet\">\n");
            html.append("        <tr class=\"spreadsheet-header\"><td>Environment</td><td>Name</td><td>Product</td><td>Build</td></tr>\n");
            Set environments = customer.getEnvironments();
            Iterator envIter = environments.iterator();
            while (envIter.hasNext()) {
                html.append("        <tr class=\"spreadsheet-shaded\">\n");

                // Display the environment name
                CMnEnvironment env = (CMnEnvironment) envIter.next();
                String href = envUrl + "?" + CUSTOMER_ID_LABEL + "=" + customer.getId() + 
                                       "&" + ENV_ID_LABEL + "=" + env.getId();
                html.append("        <td><a href=\"" + href + "\">" + env.getName() + "</a></td>\n");

                // Display the environment short name
                html.append("        <td>");
                if (env.getShortName() != null) {
                    html.append(env.getShortName());
                }
                html.append("</td>");

                // Display the product name
                html.append("        <td>");
                CMnProduct product = env.getProduct();
                if (product != null) {
                    html.append(product.getName());
                } else {
                    html.append("&nbsp;");
                }
                html.append("</td>\n");

                // Display the build version
                html.append("        <td>");
                CMnDbBuildData build = env.getBuild();
                if (build != null) {
                    html.append("<a href=\"" + buildUrl + "?" + IMnBuildForm.BUILD_ID_LABEL + "=" + build.getId() + "\">" + build.getBuildVersion() + "</a>");
                } else {
                    html.append("&nbsp;");
                }
                html.append("</td>\n");

                html.append("        </tr>\n");
            }

            html.append("        <tr class=\"spreadsheet-footer\"><td colspan=\"4\"><a href=\"" + envUrl + "?" + CUSTOMER_ID_LABEL + "=" + customer.getId() + "\">Add Environment</a></td></tr>\n");

            html.append("      </table>\n");

            html.append("    </td>\n");
            html.append("  </tr>\n");
            html.append("</table>\n");
        }

        // Complete the input form
        if (inputEnabled) {
            html.append("</form>\n");
        }

        return html.toString();
    }

}

