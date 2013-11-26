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
import com.modeln.testfw.reporting.CMnDbReleaseSummaryData;
import com.modeln.testfw.reporting.CMnBuildIdComparator;
import com.modeln.testfw.reporting.CMnReleaseOrderComparator;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
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
 * The customer form provides an HTML interface to the customer environment object.
 * The class manages transfering the data through the HTTP request.
 * Form data can be rendered in either a read-only or input mode.
 *
 * @author  Shawn Stafford
 */
public class CMnCustomerEnvForm extends CMnBaseReleaseForm implements IMnPatchForm {

    /** Save a reference to the customer data so it can be used later */
    private CMnEnvironment environment = null;

    /** Customer ID tag */
    private TextTag custIdTag;

    /** Environment name */
    private TextTag envNameTag;

    /** Environment short name */
    private TextTag envShortNameTag;

    /** Environment ID */
    private TextTag envIdTag;

    /** List of products */
    private SelectTag productTag;

    /** List of product releases */
    private SelectTag releaseTag;

    /** List of product builds */
    private SelectTag buildTag;


    /** URL for deleting a table entry */
    private URL deleteUrl = null;

    /** URL for accessing build information */
    private URL buildUrl = null;


    /** Save a reference to the customer data object */
    private CMnAccount customer = null;

    /** Save a reference to the list of products */
    private Vector productData = null;

    /** Save a reference to the list of release data objects */
    private Vector releaseData = null;




    /**
     * Construct a form for editing customer data. 
     *
     * @param    form     URL for submitting form data
     * @param    images   URL for displaying HTML images
     */
    public CMnCustomerEnvForm(URL form, URL images) {
        super(form, images);

        productTag = new SelectTag(PRODUCT_ID_LABEL);
        releaseTag = new SelectTag(RELEASE_ID_LABEL);
        buildTag = new SelectTag(BUILD_ID_LABEL);
        custIdTag = new TextTag(CUSTOMER_ID_LABEL);
        envIdTag = new TextTag(ENV_ID_LABEL);
        envNameTag = new TextTag(ENV_NAME_LABEL);
        envShortNameTag = new TextTag(ENV_SHORT_NAME_LABEL);

        custIdTag.setWidth(10);
        envIdTag.setWidth(10);
        envNameTag.setWidth(30);
        envShortNameTag.setWidth(30);

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
     * Hide the form elements
     *
     * @param  hidden   TRUE if the form elements should be hidden
     */
    public void setHidden(boolean hidden) {
        productTag.setHidden(hidden);
        releaseTag.setHidden(hidden);
        buildTag.setHidden(hidden);
        envIdTag.setHidden(hidden);
        envNameTag.setHidden(hidden);
        envShortNameTag.setHidden(hidden);
    }

    /**
     * Extend the base method for enabling the input mode.  All input fields
     * must be enabled or disabled when this method is called.
     *
     * @param enabled  TRUE to enable form input
     */
    public void setInputMode(boolean enabled) {
        super.setInputMode(enabled);
        productTag.setDisabled(!enabled);
        releaseTag.setDisabled(!enabled);
        buildTag.setDisabled(!enabled);
        envIdTag.setDisabled(!enabled);
        envNameTag.setDisabled(!enabled);
        envShortNameTag.setDisabled(!enabled);
    }

    /**
     * Return the customer name if one is available.
     *
     * @return Customer name
     */
    public String getCustomerName() {
        if (customer != null) {
            return customer.getName();
        } else {
            return null;
        }
    }

    /**
     * Sets the customer information associated with this environment.
     *
     * @param   cust    Customer data
     */
    public void setCustomer(CMnAccount cust) {
        customer = cust;
        custIdTag.setValue(cust.getId().toString());
    }

    /**
     * Set the list of products that the user can choose from
     * when selecting the build deployed to the environment.
     *
     * @param  products  List of products
     */
    public void setProducts(Vector products) {
        Hashtable productNames = new Hashtable();

        // Populate the select list with products
        CMnProduct product = null;
        for (Enumeration e = products.elements(); e.hasMoreElements(); ) {
            product = (CMnProduct) e.nextElement();
            productNames.put(product.getId().toString(), product.getName());
        }

        // Add a prompt to force the user to select an item
        if (productNames.size() > 0) {
            productNames.put("", "... select ...");
            productTag.setOptions(productNames);
            productTag.setSorting(true);
            productTag.sortByValue();
        }

        // Save a reference to the release data so we can use it to
        // populate the release list field
        productData = products;

    }


    /**
     * Set the list of releases that the user can choose from when
     * selecting the build deployed to the environment.
     *
     * @param   releases   List of releases
     */
    public void setReleases(Vector releases) {
        Hashtable releaseNames = new Hashtable();

        // Populate the select list with releases
        CMnDbReleaseSummaryData release = null;
        for (Enumeration e = releases.elements(); e.hasMoreElements(); ) {
            release = (CMnDbReleaseSummaryData) e.nextElement();
            releaseNames.put(release.getId().toString(), release.getText());
        }

        // Add a prompt to force the user to select an item
        if (releaseNames.size() > 0) {
            releaseNames.put("", "... select ...");
            releaseTag.setOptions(releaseNames);
            //releaseTag.setSorting(true);
            //releaseTag.sortByValue();
            releaseTag.setSubmitOnChange(true);

            // Ensure that the release list is sorted by the order attribute
            Comparator releaseComparator = Collections.reverseOrder(new CMnReleaseOrderComparator());
            Vector releaseOrder = new Vector();
            releaseOrder.add("");
            Collections.sort(releases, releaseComparator);
            Iterator releaseIter = releases.iterator();
            while (releaseIter.hasNext()) {
                release = (CMnDbReleaseSummaryData) releaseIter.next();
                releaseOrder.add(release.getId());
            }
            releaseTag.setKeyOrder(releaseOrder);
        }

        // Save a reference to the release data so we can use it to
        // populate the release list field
        releaseData = releases;
    }


    /**
     * Set the list of builds that the user can choose from when
     * selecting the build deployed to the environment.
     *
     * @param   builds   List of builds
     */
    private void setBuilds(Vector builds) {
        Hashtable buildVersions = new Hashtable();

        // Populate the select list with builds
        CMnDbBuildData build = null;
        for (Enumeration e = builds.elements(); e.hasMoreElements(); ) {
            build = (CMnDbBuildData) e.nextElement();
            buildVersions.put(Integer.toString(build.getId()), build.getBuildVersion());
        }

        // Add a prompt to force the user to select an item
        if (buildVersions.size() > 0) {
            buildVersions.put("", "... select ...");
            buildTag.setOptions(buildVersions);
            //buildTag.setSorting(true);
            //buildTag.sortByValue();
            buildTag.setSubmitOnChange(true);

            // Ensure that the build list is sorted correctly 
            Comparator buildComparator = Collections.reverseOrder(new CMnBuildIdComparator());
            Vector buildOrder = new Vector();
            buildOrder.add("");
            Collections.sort(builds, buildComparator);
            Iterator buildIter = builds.iterator();
            while (buildIter.hasNext()) {
                build = (CMnDbBuildData) buildIter.next();
                buildOrder.add(Integer.toString(build.getId()));
            }
            buildTag.setKeyOrder(buildOrder);
        }
    }


    /**
     * Set the list of builds based on the selected release.
     *
     * @param   rid   Release ID
     */
    private void setRelease(String rid) {
        Iterator releaseIter = releaseData.iterator();
        while (releaseIter.hasNext()) {
            CMnDbReleaseSummaryData currentRelease = (CMnDbReleaseSummaryData) releaseIter.next();
            if (currentRelease != null) {
                if (rid.equals(currentRelease.getId())) {
                    // Populate the build input field with values
                    if (currentRelease.getBuilds() != null) {
                        setBuilds(currentRelease.getBuilds());
                    }
                }
            } 
        }
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
            setCustomer(customer);
        }

        environment = (CMnEnvironment) req.getAttribute(ENV_DATA);
        if (environment != null) {
            setValues(environment);
        }

        productData = (Vector) req.getAttribute(PRODUCT_LIST_DATA);
        if (productData != null) {
            setProducts(productData);
        }

        releaseData = (Vector) req.getAttribute(RELEASE_LIST_DATA);
        if (releaseData != null) {
            setReleases(releaseData);
        }

        //
        // Override the request attribute data with form input values
        //
        if (custIdTag.isValueAvailable(req)) {
            custIdTag.setValue(req);
        }
        if (envIdTag.isValueAvailable(req)) {
            envIdTag.setValue(req);
        }
        if (envNameTag.isValueAvailable(req)) {
            envNameTag.setValue(req);
        }
        if (envShortNameTag.isValueAvailable(req)) {
            envShortNameTag.setValue(req);
        }
        if (productTag.isValueAvailable(req)) {
            productTag.setValue(req);
        }
        if (releaseTag.isValueAvailable(req)) {
            releaseTag.setValue(req);
            String[] selectedReleases = releaseTag.getSelected();
            if (selectedReleases.length > 0) {
                setRelease(selectedReleases[0]);
            }
        }
        if (buildTag.isValueAvailable(req)) {
            buildTag.setValue(req);
        }
    }


    /**
     * Set the form information using environment data.
     *
     * @param   env   Environment information
     */
    public void setValues(CMnEnvironment env) {
        environment = env;
        envIdTag.setValue(env.getId().toString());
        envNameTag.setValue(env.getName());
        envShortNameTag.setValue(env.getShortName());

        CMnProduct product = env.getProduct();
        if (product != null) {
            productTag.setSelected(product.getId().toString());
        }

        CMnDbBuildData build = env.getBuild();
        if (build != null) {
            buildTag.setSelected(Integer.toString(build.getId()));
        }
    }


    /**
     * Render the patch request form as HTML.
     */
    public String toString() {
        StringBuffer html = new StringBuffer();
        boolean showSubmit = false;

        if (inputEnabled) {
            String method = null;
            if (postMethodEnabled) {
                method = "post";
            } else {
                method = "get";
            }
            html.append("<form method=\"" + method + "\" action=\"" + getFormUrl() + "\">\n");
        }

        // Always render the customer ID as a hidden field since it should never change 
        custIdTag.setHidden(true);
        html.append(custIdTag.toString());

        // Always render the environment ID as a hidden field since a primary key should never change
        // But don't render the value if it is null
        if (envIdTag.isComplete()) {
            envIdTag.setHidden(true);
            html.append(envIdTag.toString());
        }

        html.append("<table border=\"0\" cellspacing=\"2\" cellpadding=\"2\">\n");
        html.append("  <tr>\n");
        html.append("    <td>Environment Name:</td>\n");
        html.append("    <td>" + envNameTag.toString() + "</td>\n");
        html.append("  </tr>\n");

        html.append("  <tr>\n");
        html.append("    <td>Short Name:</td>\n");
        html.append("    <td>" + envShortNameTag.toString() + "</td>\n");
        html.append("  </tr>\n");

        html.append("  <tr>\n");
        html.append("    <td>Product Name:</td>\n");
        html.append("    <td>" + productTag.toString() + "</td>\n");
        html.append("  </tr>\n");

        html.append("  <tr>\n");
        html.append("    <td>Deployed Product:</td>\n");
        html.append("    <td>\n");
        CMnDbBuildData build = null;
        if (environment != null) {
            build = environment.getBuild();
        }
        if ((customer != null) && (build != null)) {
            html.append("<a href=\"" + buildUrl + "?" + IMnBuildForm.BUILD_ID_LABEL + "=" + build.getId() + "\">" + build.getBuildVersion() + "</a>");
            String href = getFormUrl() + "?" + CUSTOMER_ID_LABEL + "=" + customer.getId() +
                                   "&" + ENV_ID_LABEL + "=" + environment.getId();
            html.append(" (<a href=\"" + href + "\">Update</a>)\n");
        } else {
            if (buildTag.getSelectionCount() == 1) {
                // A build has been selected so only display that selection
                String[] buildVer = buildTag.getSelectedText();
                String[] buildId = buildTag.getSelected();
                html.append(buildVer[0]);
                buildTag.setHidden(true);
                html.append(buildTag.toString());
                showSubmit = true;
            } else if (releaseTag.getSelectionCount() == 1) {
                // A release has been selected, so display the list of possible builds
                String[] text = releaseTag.getSelectedText();
                html.append(text[0]);
                releaseTag.setHidden(true);
                html.append(releaseTag.toString());
                html.append("<br>\n");
                html.append(buildTag.toString());
            } else {
                // Display the list of possible releases
                html.append(releaseTag.toString());
            }
        }
        html.append("    </td>\n");
        html.append("  </tr>\n");

        // Display the submit button
        if (showSubmit) {
            html.append("  <tr><td>&nbsp;</td><td><input type=\"submit\" name=\"" + CUSTOMER_ENV_BUTTON + "\" value=\"Continue\"/></td></tr>\n");
        }

        html.append("</table>\n");

        // Complete the input form
        if (inputEnabled) {
            html.append("</form>\n");
        }

        return html.toString();
    }

}

