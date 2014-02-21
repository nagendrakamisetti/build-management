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
import com.modeln.build.sourcecontrol.CMnCheckIn;
import com.modeln.build.sourcecontrol.CMnGitCheckIn;
import com.modeln.build.sourcecontrol.CMnPerforceCheckIn;
import com.modeln.testfw.reporting.CMnDbBuildData;
import com.modeln.testfw.reporting.CMnDbMetricData;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import java.util.Hashtable;
import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpServletRequest;

import com.modeln.build.common.data.account.UserData;
import com.modeln.build.web.tags.TextTag;
import com.modeln.build.web.tags.DateTag;
import com.modeln.build.web.tags.ListTag;
import com.modeln.build.web.tags.OptionTag;
import com.modeln.build.web.tags.SelectTag;
import com.modeln.build.web.tags.TagGroup;


/**
 * The build form provides an HTML interface to the build data object.
 * The class manages transfering the data through the HTTP request.
 * Form data can be rendered in either a read-only or input mode.
 *
 * @author  Shawn Stafford
 */
public class CMnPatchRequestForm extends CMnBaseReleaseForm implements IMnPatchForm {


    /** List of user information */
    private Vector userData;

    /** List of customer information */
    private Vector customerData;

    /** List of previous patches */
    private Vector previousPatches;


    /** Base Patch ID field */
    private TextTag baseIdTag;

    /** Patch request ID field */
    private TextTag patchIdTag;

    /** User who requested the patch */
    private SelectTag userTag;

    /** Build report field */
    private TextTag patchBuildTag;

    /** Customer name field */
    private SelectTag customerTag;

    /** Customer deployment environment field */
    private SelectTag envTag;

    /** Product release field */
    private SelectTag releaseTag;

    /** Build version field */
    private SelectTag buildTag;

    /** Indicates whether the patch should be available for customer use */
    private OptionTag patchUseTag;

    /** Patch name (i.e. SP1) */
    private TextTag nameTag;

    /** List of e-mail addresses to notify of patch updates */
    private TextTag notifyTag;

    /** Service patch business justification field */
    private TextTag justifyTag;


    /** Group of checkboxes for selecting which parts of a build to perform */
    private OptionTag buildOptionsTag;

    /** Source control type used for patches */
    private SourceControl scm = SourceControl.GIT;


    /** Determines whether the form input fields should submit data on change events */
    private boolean submitOnChange = false;

    /** Keep a reference to the service patch data object */
    private CMnPatch patch = null;

    /** The URL used when submitting form input. */
    protected URL buildUrl = null;

    /** The URL used to view patch requests */
    protected URL patchUrl = null;



    /**
     * Construct a form for requesting a new service patch. 
     *
     * @param    form     URL for submitting form data
     * @param    images   URL for displaying HTML images 
     */
    public CMnPatchRequestForm(URL form, URL images) {
        super(form, images);

        userTag = new SelectTag(PATCH_USER_LABEL);
        customerTag = new SelectTag(CUSTOMER_ID_LABEL);
        envTag = new SelectTag(ENV_ID_LABEL);
        releaseTag = new SelectTag(RELEASE_ID_LABEL);
        buildTag = new SelectTag(BUILD_ID_LABEL);

        buildOptionsTag = new OptionTag(BUILD_OPTIONS_LABEL);
        buildOptionsTag.setMultiple(true);

        baseIdTag = new TextTag(BASE_PATCH_LABEL);
        baseIdTag.setWidth(10);

        patchBuildTag = new TextTag(PATCH_BUILD_LABEL);
        patchBuildTag.setWidth(10);

        patchIdTag = new TextTag(PATCH_ID_LABEL);
        patchIdTag.setWidth(10);

        Hashtable useOptions = new Hashtable();
        useOptions.put(Boolean.TRUE.toString(), "Available for customer use");
        useOptions.put(Boolean.FALSE.toString(), "Internal use only");
        patchUseTag = new OptionTag(PATCH_USE_LABEL, useOptions);
        patchUseTag.setDefault(Boolean.TRUE.toString());
        patchUseTag.setSelected(Boolean.TRUE.toString());
        patchUseTag.setDisabled(false);

        nameTag = new TextTag(PATCH_NAME_LABEL);
        nameTag.setWidth(10);

        notifyTag = new TextTag(NOTIFY_LABEL);
        notifyTag.setWidth(40);

        justifyTag = new TextTag(JUSTIFY_LABEL);
        justifyTag.setHeight(5);
        justifyTag.setWidth(40);

    }

    /**
     * Set the source control type for this form.
     *
     * @param  type   Source control type
     */
    public void setSourceControlType(SourceControl type) {
        scm = type;
    }

    /**
     * Return the source control type of this form.
     *
     * @return Source control type
     */
    public SourceControl getSourceControlType() {
        return scm;
    }


    /**
     * Sets the URL to be used to link to build reports. 
     *
     * @param  url   Link to the build report 
     */
    public void setBuildUrl(URL url) {
        buildUrl = url;
    }


    /**
     * Sets the URL to be used to link to service patch request. 
     *
     * @param  url   Link to the patch request 
     */
    public void setPatchUrl(URL url) {
        patchUrl = url;
    }




    /**
     * Hide the form elements
     *
     * @param  hidden   TRUE if the form elements should be hidden
     */
    public void setHidden(boolean hidden) {
        baseIdTag.setHidden(hidden);
        patchIdTag.setHidden(hidden);
        patchBuildTag.setHidden(hidden);
        userTag.setHidden(hidden);
        customerTag.setHidden(hidden);
        envTag.setHidden(hidden);
        releaseTag.setHidden(hidden);
        buildTag.setHidden(hidden);
        buildOptionsTag.setHidden(hidden);
        patchUseTag.setHidden(hidden);
        nameTag.setHidden(hidden);
        notifyTag.setHidden(hidden);
        justifyTag.setHidden(hidden);
    }


    /**
     * Enable the submitOnChange behavior for the form elements.
     *
     * @param  enable    TRUE if the form elements should submitOnChange
     */
    public void setSubmitOnChange(boolean enable) {
        baseIdTag.setSubmitOnChange(enable);
        patchIdTag.setSubmitOnChange(enable);
        patchBuildTag.setSubmitOnChange(enable);
        userTag.setSubmitOnChange(enable);
        customerTag.setSubmitOnChange(enable);
        envTag.setSubmitOnChange(enable);
        releaseTag.setSubmitOnChange(enable);
        buildTag.setSubmitOnChange(enable);
        buildOptionsTag.setSubmitOnChange(enable);
        patchUseTag.setSubmitOnChange(enable);
        nameTag.setSubmitOnChange(enable);
        notifyTag.setSubmitOnChange(enable);
        justifyTag.setSubmitOnChange(enable);
    }


    /**
     * Set the list of users in the user dropdown menu.
     * 
     * @param  users  List of users
     */
    public void setUsers(Vector users) {
        Hashtable userNames = new Hashtable();

        // Populate the select list with users
        UserData user = null;
        for (Enumeration e = users.elements(); e.hasMoreElements(); ) {
            userNames.put("", "... select ...");
            userTag.setOptions(userNames);
            //userTag.setSorting(true);
            userTag.sortByValue();
            userTag.setSubmitOnChange(true);
        }

        // Save a reference to the user data
        userData = users;
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
            customerNames.put("", "... select ...");
            customerTag.setOptions(customerNames);
            //customerTag.setSorting(true);
            customerTag.sortByValue();
            customerTag.setSubmitOnChange(true);
        }

        // Save a reference to the customer data so we can use it to 
        // populate the customer environment field
        customerData = customers;
    }


    /**
     * Set the list of customer environments in the environment dropdown menu.
     *
     * @param  environments    List of environemnts for a single customer
     */
    public void setEnvironments(Set environments) {
        Hashtable envNames = new Hashtable();
        CMnEnvironment env = null;
        for (Iterator iter = environments.iterator(); iter.hasNext(); ) {
            env = (CMnEnvironment) iter.next();
            envNames.put(env.getId().toString(), env.getName());
        }

        // Add a prompt to force the user to select an item
        if (envNames.size() > 0) {
            envNames.put("", "... select ...");
            envTag.setOptions(envNames);
            //envTag.setSorting(true);
            envTag.sortByValue();
            envTag.setSubmitOnChange(true);
        }
    }


    /**
     * Set the list of releases in the release dropdown menu.
     *
     * @param  releases    List of releases
     */
    public void setReleasesFromBuildList(Vector builds) {
        Hashtable releaseNames = new Hashtable();
        CMnDbBuildData build = null;
        for (Enumeration e = builds.elements(); e.hasMoreElements(); ) {
            build = (CMnDbBuildData) e.nextElement();

            // Add the version if it does not already exist in the list
            String verNum = getVersionNumber(build.getBuildVersion());
            if (verNum != null) {
                if (!releaseNames.containsKey(verNum)) {
                    releaseNames.put(verNum, verNum + " " + build.getReleaseId());
                }
            }
        }

        // Add a prompt to force the user to select an item
        if (releaseNames.size() > 0) {
            releaseNames.put("", "... select ...");
            releaseTag.setOptions(releaseNames);
            // There seems to be some sort of array bounding problem with sorting this
            releaseTag.setSorting(true);
            //releaseTag.sortByValue();
            releaseTag.setSubmitOnChange(true);
        }
    }

    /**
     * Internal helper method for returning a shortened version of the
     * JDK vendor.  This is displayed along with the build string to
     * help users distinguish easily between builds from different vendors.
     *
     * @param  build   Build data
     */
    private String getVendor(CMnDbBuildData build) {
        String vendor = null;

        if ((build != null) && (build.getHostData() != null)) {
            vendor = build.getHostData().getJdkVendor();
            if (vendor != null) {
                // Shorten the vendor to just the first word
                int idx = vendor.indexOf(' ');
                if (idx > 0) {
                    vendor = vendor.substring(0, idx);
                }
            }
        }

        return vendor;
    }



    /**
     * Set the list of builds in the build dropdown menu.
     *
     * @param  builds   List of builds
     */
    public void setBuilds(Vector builds) {
        Hashtable buildNames = new Hashtable();
        CMnDbBuildData build = null;
        for (Enumeration e = builds.elements(); e.hasMoreElements(); ) {
            build = (CMnDbBuildData) e.nextElement();
            String buildId = Integer.toString(build.getId());
            String buildVer = build.getBuildVersion();
            String jdk = getVendor(build);
            if (jdk != null) {
                buildVer = buildVer + " (" + jdk.trim() + ")";
            }
            buildNames.put(buildId, buildVer);
        }

        if (buildNames.size() > 0) {
            buildNames.put("", "... select ...");
            buildTag.setOptions(buildNames);
            // There seems to be some sort of array bounding problem with sorting this
            buildTag.setSorting(true);
            //buildTag.sortByValue();
            buildTag.setSubmitOnChange(true);
        } else if (buildNames.size() == 1) {
            buildTag.setOptions(buildNames);
        }
    }

    /**
     * Set the list of build options for the selected build.  The key 
     * represents the name of the option and the value represents the 
     * number of minutes required to perform the option.
     *
     * @param   metrics   List of build options
     */
    public void setBuildMetrics(Vector metrics) {
        Hashtable options = new Hashtable();

        // Iterate through the list of options and construct the checkbox items
        for (Enumeration e = metrics.elements(); e.hasMoreElements(); ) {
            CMnDbMetricData metric = (CMnDbMetricData) e.nextElement();
            if (metric != null) {
                int type = metric.getType();
                String strType = CMnDbMetricData.getMetricType(type);
                String desc = metric.getDescription() + " (ETA: " + metric.getElapsedTimeString() + ")";
                if ((strType != null) && (strType.length() > 0)) {
                    options.put(strType, desc);
                }
            }
        }

        buildOptionsTag.setOptions(options);
    }


    /**
     * Set the list of previous patches for the selected build.
     *
     * @param  patches   List if previous patches
     */
    public void setPreviousPatches(Vector patches) {
        previousPatches = patches;
    }

    /**
     * Extend the base method for enabling the input mode.  All input fields
     * must be enabled or disabled when this method is called.
     *
     * @param enabled  TRUE to enable form input
     */
    public void setInputMode(boolean enabled) {
        super.setInputMode(enabled);

        baseIdTag.setDisabled(!enabled);
        patchIdTag.setDisabled(!enabled);
        userTag.setDisabled(!enabled);
        customerTag.setDisabled(!enabled);
        envTag.setDisabled(!enabled);
        releaseTag.setDisabled(!enabled);
        buildTag.setDisabled(!enabled);
        buildOptionsTag.setDisabled(!enabled);
        patchUseTag.setDisabled(!enabled);
        nameTag.setDisabled(!enabled);
        notifyTag.setDisabled(!enabled);
        justifyTag.setDisabled(!enabled);
 
        inputEnabled = enabled;
    }


    /**
     * Set the form information using patch data.
     *
     * @param   patch   Patch information
     */
    public void setValues(CMnPatch patch) {
        // Keep a reference to this patch data for later use
        this.patch = patch;

        if (patch.getRequestor() != null) {
            String userId = patch.getRequestor().getUid();
            String userName = patch.getRequestor().getFullName();
            if ((!userTag.hasOption(userId)) || (userTag.getOptionName(userId) == null)) {
                if (userName != null) {
                    userTag.addOption(userName, userId);
                } else {
                    userTag.addOption(userId, userId);
                }
            }
            userTag.setDefault(userId);
            userTag.setSelected(userId);
        }

        if (patch.getCustomer() != null) {
            String customerId = patch.getCustomer().getId().toString();
            String customerName = patch.getCustomer().getName();
            if ((!customerTag.hasOption(customerId)) || (customerTag.getOptionName(customerId) == null)) {
                if (customerName != null) {
                    customerTag.addOption(customerName, customerId);
                } else {
                    customerTag.addOption(customerId, customerId);
                }
            }
            customerTag.setDefault(customerId);
            customerTag.setSelected(customerId);
        }

        if (patch.getEnvironment() != null) {
            String envId = patch.getEnvironment().getId().toString();
            String envName = patch.getEnvironment().getName();
            if ((!envTag.hasOption(envId)) || (envTag.getOptionName(envId) == null)) {
                if (envName != null) {
                    envTag.addOption(envName, envId);
                } else {
                    envTag.addOption(envId, envId);
                }
            }
            envTag.setDefault(envId);
            envTag.setSelected(envId);
        }

        String verNum = getVersionNumber(patch.getBuild().getBuildVersion());
        if ((!releaseTag.hasOption(verNum)) || (releaseTag.getOptionName(verNum) == null)) {
            releaseTag.addOption(verNum, verNum);
        }
        releaseTag.setDefault(verNum);
        releaseTag.setSelected(verNum);

        String buildId = Integer.toString(patch.getBuild().getId());
        String buildVer = patch.getBuild().getBuildVersion();
        if ((!buildTag.hasOption(buildId)) || (buildTag.getOptionName(buildId) == null)) {
            if (buildVer != null) {
                buildTag.addOption(buildVer, buildId);
            } else {
                buildTag.addOption(buildId, buildId);
            }
        }
        buildTag.setDefault(buildId);
        buildTag.setSelected(buildId);

        //buildOptionsTag
        
        patchIdTag.setValue(patch.getId().toString());

        // Set the Patch Build ID field
        if (patch.getPatchBuild() != null) {
            int patchBuildId = patch.getPatchBuild().getId();
            if (patchBuildId > 0) {
                patchBuildTag.setValue(Integer.toString(patchBuildId));
            }
        }

        // Set the Previous Patch ID field
        if (patch.getPreviousPatch() != null) {
            int previousPatchId = patch.getPreviousPatch().getId();
            if (previousPatchId > 0) {
                baseIdTag.setValue(Integer.toString(previousPatchId));
            }
        }

        Boolean externalUse = new Boolean(patch.getForExternalUse());
        patchUseTag.setSelected(externalUse.toString());
        // Force the default to be the selected value
        // to avoid potential bugs where the tag has contradictory values
        patchUseTag.setDefault(externalUse.toString());

        nameTag.setValue(patch.getName());
        notifyTag.setValue(InternetAddress.toString(patch.getCCList()));
        justifyTag.setValue(patch.getJustification());
    }


    /**
     * Set the input fields by examining the HTTP request to see if
     * a value was submitted.
     *
     * @param   req     HTTP request
     */
    public void setValues(HttpServletRequest req) {
        formErrors = (Hashtable) req.getAttribute(INPUT_ERROR_DATA);

        // Check the request attribute for a data object with the default/previous values
        CMnPatch data = (CMnPatch) req.getAttribute(PATCH_DATA);
        if (data != null) {
            setValues(data);
        }

        //
        // Override the request attribute data with form input values
        //

        // Environments are customer-specific so this field only has meaning
        // if a customer has been selected
        if (customerTag.isValueAvailable(req)) {
            customerTag.setValue(req);
            CMnAccount customer = null;
            if (customerData != null) {
                for (Enumeration e = customerData.elements(); e.hasMoreElements(); ) {
                    customer = (CMnAccount) e.nextElement();
                    String custId = customer.getId().toString();
                    String[] selected = customerTag.getSelected();
    
                    // Set the environment values for the selected customer
                    if (custId.equals(selected[0])) {
                        setEnvironments(customer.getEnvironments());
                    }
                }
                envTag.setValue(req);
                // Make sure the options are populated with at least some values
                String[] selectedEnv = envTag.getSelected();
                if ((selectedEnv != null) && (envTag.getOptionCount() == 0)) {
                    envTag.setOptions(selectedEnv);
                }
            } else {
                String envId = req.getParameter(ENV_ID_LABEL);
                if (envId != null) {
                    Vector envOptions = new Vector(1);
                    envOptions.add(envId);
                    envTag.setOptions(envOptions);
                    envTag.setSelected(envId);
                }
            }
        } else {
            String custId = req.getParameter(CUSTOMER_ID_LABEL);
            if (custId != null) {
                Vector custOptions = new Vector(1);
                custOptions.add(custId);
                customerTag.setOptions(custOptions);
                //customerTag.setSorting(true);
                customerTag.sortByValue();
                customerTag.setSelected(custId);
            }
        }

        // Set the user selection
        String userId = req.getParameter(PATCH_USER_LABEL);
        if (userId != null) {
            Vector userOptions = new Vector(1);
            userOptions.add(userId);
            userTag.setOptions(userOptions);
            // userTag.setSorting(true);
            userTag.sortByValue();
            userTag.setSelected(userId);
        }

        if (releaseTag.isValueAvailable(req)) {
            releaseTag.setValue(req);
        }
        if (buildTag.isValueAvailable(req)) {
            buildTag.setValue(req);
        }
        if (buildOptionsTag.isValueAvailable(req)) {
            buildOptionsTag.setValue(req);
        }
        if (baseIdTag.isValueAvailable(req)) {
            baseIdTag.setValue(req);
        }
        if (patchIdTag.isValueAvailable(req)) {
            patchIdTag.setValue(req);
        }
        if (patchBuildTag.isValueAvailable(req)) {
            patchBuildTag.setValue(req);
        }
        if (patchUseTag.isValueAvailable(req)) {
            patchUseTag.setValue(req);
        }
        if (nameTag.isValueAvailable(req)) {
            nameTag.setValue(req);
        }
        if (notifyTag.isValueAvailable(req)) {
            notifyTag.setValue(req);
        }
        if (justifyTag.isValueAvailable(req)) {
            justifyTag.setValue(req);
        }

    }


    /**
     * Return form input fields as hidden tags.
     */
    public String toHiddenString() {
        StringBuffer html = new StringBuffer();

        boolean userHide = userTag.isHidden();
        userTag.setHidden(true);
        html.append(userTag.toString());
        userTag.setHidden(userHide);

        boolean custHide = customerTag.isHidden();
        customerTag.setHidden(true);
        html.append(customerTag.toString());
        customerTag.setHidden(custHide);

        boolean envHide = envTag.isHidden();
        envTag.setHidden(true);
        html.append(envTag.toString());
        envTag.setHidden(envHide);

        boolean releaseHide = releaseTag.isHidden();
        releaseTag.setHidden(true);
        html.append(releaseTag.toString());
        releaseTag.setHidden(releaseHide);

        boolean buildHide = buildTag.isHidden();
        buildTag.setHidden(true);
        html.append(buildTag.toString());
        buildTag.setHidden(buildHide);

        boolean buildOptsHide = buildOptionsTag.isHidden();
        buildOptionsTag.setHidden(true);
        html.append(buildOptionsTag.toString());
        buildOptionsTag.setHidden(buildOptsHide);

        boolean baseIdHide = baseIdTag.isHidden();
        baseIdTag.setHidden(true);
        html.append(baseIdTag.toString());
        baseIdTag.setHidden(baseIdHide);

        boolean patchIdHide = patchIdTag.isHidden();
        patchIdTag.setHidden(true);
        html.append(patchIdTag.toString());
        patchIdTag.setHidden(patchIdHide);

        boolean buildReportHide = patchBuildTag.isHidden();
        patchBuildTag.setHidden(true);
        html.append(patchBuildTag.toString());
        patchBuildTag.setHidden(buildReportHide);

        boolean useHide = patchUseTag.isHidden();
        patchUseTag.setHidden(true);
        html.append(patchUseTag.toString());
        patchUseTag.setHidden(useHide);

        boolean nameHide = nameTag.isHidden();
        nameTag.setHidden(true);
        html.append(nameTag.toString());
        nameTag.setHidden(nameHide);

        boolean notifyHide = notifyTag.isHidden();
        notifyTag.setHidden(true);
        html.append(notifyTag.toString());
        notifyTag.setHidden(notifyHide);

        boolean justifyHide = justifyTag.isHidden(); 
        justifyTag.setHidden(true);
        html.append(justifyTag.toString());
        justifyTag.setHidden(justifyHide);

        return html.toString();
    }

    /**
     * Render the list of previous patches as HTML.
     *
     * @return HTML text
     */
    public String getPatchTable() {
        StringBuffer html = new StringBuffer();

        // Display a list of previous patches
        html.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"1\">\n");

        // Display the table header
        html.append("  <tr>\n");
        html.append("    <td bgcolor=\"#CCCCCC\">&nbsp;</td>\n");
        html.append("    <td bgcolor=\"#CCCCCC\">Patch Name</td>\n");
        html.append("    <td bgcolor=\"#CCCCCC\">Internal</td>\n");
        html.append("    <td bgcolor=\"#CCCCCC\">Request Date</td>\n");
        html.append("    <td bgcolor=\"#CCCCCC\">Requested By</td>\n");
        html.append("    <td bgcolor=\"#CCCCCC\">Bug Count</td>\n");
        html.append("  </tr>\n");

        CMnPatch currentPatch = null;
        Enumeration e = previousPatches.elements();
        while (e.hasMoreElements()) {
            currentPatch = (CMnPatch) e.nextElement();
            html.append("  <tr>\n");
            html.append("    <td><input type=\"radio\" name=\"" + BASE_PATCH_LABEL + "\" value=\"" + currentPatch.getId() + "\"/></td>\n");
            html.append("    <td>" + currentPatch.getName() + "</td>\n");
            html.append("    <td>" + (!currentPatch.getForExternalUse()) + "</td>\n");
            html.append("    <td>" + currentPatch.getRequestDate() + "</td>\n");
            html.append("    <td>" + currentPatch.getRequestor().getFullName() + "</td>\n");
            int fixCount = 0;
            Vector fixes = currentPatch.getFixes();
            if (fixes != null) {
                fixCount = fixes.size();
            }
            html.append("    <td align=\"right\">" + fixCount + "</td>\n");
            html.append("  </tr>\n");
        }

        html.append("  <tr>\n");
        html.append("    <td><input type=\"radio\" name=\"" + BASE_PATCH_LABEL + "\" value=\"\"/></td>\n");
        html.append("    <td colspan=\"6\">Start from scratch</td>\n");
        html.append("  </tr>\n");

        html.append("</table>\n");

        return html.toString();
    }


    /**
     * Render an HTML table containing summary information about the patch.
     */
    public String getPatchSummaryTable() {
        StringBuffer html = new StringBuffer();

        html.append("<table width=\"100%\" cellspacing=\"0\" cellpadding=\"1\">\n");

        html.append("  <tr>\n");
        html.append("    <td nowrap width=\"20%\" align=\"right\" NOWRAP>Requested By:</td>\n");
        html.append("    <td nowrap width=\"80%\" align=\"left\" NOWRAP>");
        String[] userNames = userTag.getSelectedText();
        if ((userNames != null) && (userNames.length == 1)) {
            html.append(userNames[0]);
        }
        html.append("</td>\n");
        html.append("  </tr>\n");


        html.append("  <tr>\n");
        html.append("    <td nowrap width=\"20%\" align=\"right\" NOWRAP>Customer:</td>\n");
        html.append("    <td nowrap width=\"80%\" align=\"left\" NOWRAP>");
        String[] custIds = customerTag.getSelected();
        String[] custNames = customerTag.getSelectedText();
        if ((custIds != null) && (custIds.length == 1)) {
            String id = custIds[0];
            String name = custNames[0];
            if (searchUrl != null) {
                String href = getSearchUrl() + "?" + CUSTOMER_ID_LABEL + "=" + id;
                html.append("<a href=\"" + href + "\">" + name + "</a>"); 
            } else {
                html.append(name);
            }
        }
        html.append("</td>\n");
        html.append("  </tr>\n");

        html.append("  <tr>\n");
        html.append("    <td nowrap width=\"20%\" align=\"right\" NOWRAP>Product Build:</td>\n");
        html.append("    <td nowrap width=\"80%\" align=\"left\" NOWRAP>");
        String[] buildNames = buildTag.getSelectedText();
        if ((buildNames != null) && (buildNames.length == 1)) { 
            html.append(buildNames[0]);
        } 
        if (nameTag.getValue() != null) {
            html.append(" (" + nameTag.getValue() + ")");
        }
        html.append("</td>\n");
        html.append("  </tr>\n");

        html.append("  <tr>\n");
        html.append("    <td nowrap width=\"20%\" align=\"right\" valign=\"top\" NOWRAP>E-mail List:</td>\n");
        html.append("    <td nowrap width=\"80%\" align=\"left\"  valign=\"top\">");
        if (notifyTag.getValue() != null) {
            //html.append(notifyTag.getValue());
            html.append(notifyTag.getValue().replaceAll(",\\s+", ",<br>"));
        }
        html.append("</td>\n");
        html.append("  </tr>\n");

        html.append("</table>\n");

        return html.toString();
    }
 
    /**
     * Render the patch request form as HTML. 
     */
    public String toString() {
        StringBuffer html = new StringBuffer();

        boolean hasCustEnvReleaseBuild = (
            (customerTag.getSelectionCount() == 1) &&
            (envTag.getSelectionCount() == 1) &&
            (releaseTag.getSelectionCount() == 1) &&
            (buildTag.getSelectionCount() == 1)
        );


        String errorMsg = null;

        if (inputEnabled) {
            String method = null;
            if (postMethodEnabled) {
                method = "post";
            } else {
                method = "get";
            }
            html.append("<form method=\"" + method + "\" action=\"" + getFormUrl() + "\">\n");
        }


        // Always render the patch ID as a hidden field since a primary key should never change
        // But don't render the value if it is null
        if (patchIdTag.isComplete()) {
            patchIdTag.setHidden(true);
            html.append(patchIdTag.toString());
        }

        html.append("<table width=\"100%\" cellspacing=\"0\" cellpadding=\"1\">\n");

        html.append("  <tr>\n");
        html.append("    <td nowrap width=\"20%\" align=\"right\" NOWRAP>Customer:</td>\n");
        html.append("    <td nowrap width=\"80%\" align=\"left\" NOWRAP>");
        if (customerTag.getSelectionCount() == 1) {
            String[] custIds = customerTag.getSelected();
            String[] custNames = customerTag.getSelectedText();
            if ((custIds != null) && (custIds.length == 1)) {
                String id = custIds[0];
                String name = custNames[0];
                if (searchUrl != null) {
                    String href = getSearchUrl() + "?" + CUSTOMER_ID_LABEL + "=" + id;
                    html.append("<a href=\"" + href + "\">" + name + "</a>");
                } else {
                    html.append(name);
                }
                customerTag.setHidden(true);
                html.append(customerTag.toString());
            }

        } else {
            html.append(customerTag.toString());
            errorMsg = getFormError(customerTag.getName());
            if (errorMsg != null) {
                // The UI doesn't require an error message for this field
                //html.append(" <font color=red>" + errorMsg + "</font>");
            }
        }
        html.append("</td>\n");
        html.append("  </tr>\n");


        html.append("  <tr>\n");
        html.append("    <td align=\"right\" NOWRAP>Environment:</td>\n");
        html.append("    <td align=\"left\" NOWRAP>");
        if (customerTag.getSelectionCount() == 1) {
            if (envTag.getSelectionCount() == 1) {
                String[] text = envTag.getSelectedText();
                html.append(text[0]);
                envTag.setHidden(true);
                html.append(envTag.toString());
            } else {
                html.append(envTag.toString());
                errorMsg = getFormError(envTag.getName());
                if (errorMsg != null) {
                    // The UI doesn't require an error message for this field
                    //html.append(" <font color=red>" + errorMsg + "</font>");
                }
            }
        }
        html.append("</td>\n");
        html.append("  </tr>\n");

        html.append("  <tr>\n");
        html.append("    <td align=\"right\" NOWRAP>Release:</td>\n");
        html.append("    <td align=\"left\" NOWRAP>");
        if ((customerTag.getSelectionCount() == 1) && (envTag.getSelectionCount() == 1)) {
            if (releaseTag.getSelectionCount() == 1) {
                String[] text = releaseTag.getSelectedText();
                html.append(text[0]);
                releaseTag.setHidden(true);
                html.append(releaseTag.toString());
            } else {
                html.append(releaseTag.toString());
                errorMsg = getFormError(releaseTag.getName());
                if (errorMsg != null) {
                    // The UI doesn't require an error message for this field
                    //html.append(" <font color=red>" + errorMsg + "</font>");
                }
            }
        }
        html.append("</td>\n");
        html.append("  </tr>\n");

        html.append("  <tr>\n");
        html.append("    <td align=\"right\" NOWRAP>Product Build:</td>\n");
        html.append("    <td align=\"left\" NOWRAP>");
        if ((customerTag.getSelectionCount() == 1) && (envTag.getSelectionCount() == 1) && (releaseTag.getSelectionCount() == 1)) {
            if (buildTag.getSelectionCount() == 1) {
                buildTag.setHidden(true);
                html.append(buildTag.toString());

                // Display a link to the product build
                String[] key = buildTag.getSelected();
                String[] text = buildTag.getSelectedText();

                // Create the link to the build report
                if (buildUrl != null) {
                    String href = buildUrl + "?" + BUILD_ID_LABEL + "=" + key[0];
                    html.append("<a href=\"" + href + "\">" + text[0] + "</a>");
                } else {
                    html.append(text[0]);
                }

            } else {
                html.append(buildTag.toString());
                errorMsg = getFormError(buildTag.getName());
                if (errorMsg != null) {
                    // The UI doesn't require an error message for this field
                    //html.append(" <font color=red>" + errorMsg + "</font>");
                }
            }
        }
        html.append("</td>\n");
        html.append("  </tr>\n");

        // Only display the Build Report field if the patch request 
        // exists
        if (patchIdTag.isComplete() && (inputEnabled || (patchBuildTag.getValue() != null))) {
            // Display the build report URL 
            html.append("  <tr>\n");
            html.append("    <td align=\"right\" NOWRAP>Service Patch Build:</td>\n");
            html.append("    <td align=\"left\" NOWRAP>");
            if (inputEnabled) {
                html.append(patchBuildTag.toString());
            } else {
                patchBuildTag.setHidden(true);
                html.append(patchBuildTag.toString());
                if (patchBuildTag.getValue() != null) {
                    String patchBuildId = null;
                    String patchBuildDisplay = null;
                    if (patch.getPatchBuild() != null) {
                        // Try to pull additional information out of the patch data object
                        patchBuildId = Integer.toString(patch.getPatchBuild().getId());
                        if (patchBuildId.equals(patchBuildTag.getValue())) {
                            patchBuildDisplay = patch.getPatchBuild().getBuildVersion();
                            String jdk = getVendor(patch.getPatchBuild());
                            if (jdk != null) {
                                patchBuildDisplay = patchBuildDisplay + " (" + jdk.trim() + ")";
                            }
                        } else {
                            // Fall back to user input values if patch data is invalid
                            patchBuildId = patchBuildTag.getValue();
                            patchBuildDisplay = patchBuildTag.getValue();
                        }
                    } else {
                        patchBuildId = patchBuildTag.getValue();
                        patchBuildDisplay = patchBuildTag.getValue();
                    }

                    // Create the link to the build report
                    if (patchBuildId != null) {
                        html.append("<a href=\"" + getPatchBuildUrl(patchBuildId) + "\">" + patchBuildDisplay + "</a>");
                    } else {
                        html.append(patchBuildDisplay);
                    }
                }
            }
            html.append("</td>\n");
            html.append("  </tr>\n");
        }

        // Only display the Previous Patch field if the patch request exists
        if (patchIdTag.isComplete() && (inputEnabled || (baseIdTag.getValue() != null))) {
            // Display the build report URL 
            html.append("  <tr>\n");
            html.append("    <td align=\"right\" NOWRAP>Previous Service Patch:</td>\n");
            html.append("    <td align=\"left\" NOWRAP>");
            if (inputEnabled) {
                html.append(baseIdTag.toString());
            } else {
                baseIdTag.setHidden(true);
                html.append(baseIdTag.toString());
                if (baseIdTag.getValue() != null) {
                    if (patch.getPreviousPatch() != null) {
                        String href = null;
                        Integer id = patch.getPreviousPatch().getId();
                        String name = patch.getPreviousPatch().getName();
                        if ((patchUrl != null) && (id != null) && (id > 0)) {
                            href = patchUrl + "?" + PATCH_ID_LABEL + "=" + id; 
                            html.append("<a href=\"" + href + "\">");
                        }
                        if (name != null) {
                            html.append(name);
                        } else {
                            html.append(id);
                        }
                        if (href != null) {
                            html.append("</a>");
                        }
                    }
                }
            }
        }

        // Only display this portion of the page once the necessary build information has been provided
        if (hasCustEnvReleaseBuild) {
            // Display the input field for the service patch name (i.e. SP1)
            html.append("  <tr>\n");
            html.append("    <td align=\"right\" valign=\"top\" NOWRAP>Patch Name:</td>\n");
            html.append("    <td align=\"left\" valign=\"top\" NOWRAP>");
            // Only edit the patch name if the patch is new
            boolean editName = ! patchIdTag.isComplete();
            if (inputEnabled || editName) {
                nameTag.setDisabled(false);
                html.append(nameTag.toString());
            } else {
                nameTag.setDisabled(true);
                if (nameTag.getValue() != null) {
                    html.append(nameTag.getValue());
                }
            }
            if (editName) {
                errorMsg = getFormError(nameTag.getName());
                if (errorMsg != null) {
                    html.append(" <font color=red>" + errorMsg + "</font><br>");
                }
            }
            html.append("</td>\n");
            html.append("  </tr>\n");


            // Display the list of build options
            /* We are not currently using this field, so hide it
            html.append("  <tr>\n");
            html.append("    <td align=\"right\" valign=\"top\" NOWRAP>Build Options:</td>\n");
            html.append("    <td align=\"left\" valign=\"top\" NOWRAP>");
            // Only edit options if this is a new patch
            boolean editOptions = ! patchIdTag.isComplete();
            if (editOptions) {
                errorMsg = getFormError(buildOptionsTag.getName());
                if (errorMsg != null) {
                    html.append("<br><font color=red>" + errorMsg + "</font>");
                }
            }
            if (inputEnabled || editOptions) {
                buildOptionsTag.setDisabled(false);
            } else {
                buildOptionsTag.setDisabled(true);
            }
            html.append(buildOptionsTag.toString());
            html.append("</td>\n");
            html.append("  </tr>\n");
            */


            // Get input for whether the patch will be externally available 
            html.append("  <tr>\n");
            html.append("    <td align=\"right\" valign=\"top\" NOWRAP>Distribution Status:</td>\n");
            html.append("    <td align=\"left\" valign=\"top\" NOWRAP>");
            // Only edit the patch status if the patch is new
            boolean editUse = ! patchIdTag.isComplete();
            if (inputEnabled || editUse) {
                patchUseTag.setDisabled(false);
            } else {
                patchUseTag.setDisabled(true);
            }
            html.append(patchUseTag.toString());
            if (editUse) {
                errorMsg = getFormError(patchUseTag.getName());
                if (errorMsg != null) {
                    html.append(" <font color=red>" + errorMsg + "</font><br>");
                }
            }
            html.append("</td>\n");
            html.append("  </tr>\n");


            // Display the name of the user who submitted the request
            if (patchIdTag.isComplete()) {
                html.append("  <tr>\n");
                html.append("    <td nowrap width=\"20%\" align=\"right\" NOWRAP>Requested By:</td>\n");
                html.append("    <td nowrap width=\"80%\" align=\"left\" NOWRAP>");
                if (userTag.getSelectionCount() == 1) {
                    String[] text = userTag.getSelectedText();
                    html.append(text[0]);
                    userTag.setHidden(true);
                    html.append(userTag.toString());
                } else {
                    html.append(userTag.toString());
                    errorMsg = getFormError(userTag.getName());
                    if (errorMsg != null) {
                        // The UI doesn't require an error message for this field
                        //html.append(" <font color=red>" + errorMsg + "</font>");
                    }
                }
                html.append("</td>\n");
                html.append("  </tr>\n");
            }


            // Display the input field for the e-mail notification list 
            if (inputEnabled || (notifyTag.getValue() != null)) {
                html.append("  <tr>\n");
                html.append("    <td align=\"right\" valign=\"top\" NOWRAP>E-mail List:</td>\n");
                html.append("    <td align=\"left\" valign=\"top\">");
                // Only edit the notification if the patch is new
                boolean editNotify = ! patchIdTag.isComplete();
                if (inputEnabled || editNotify) {
                    notifyTag.setDisabled(false);
                    html.append(notifyTag.toString());
                } else {
                    notifyTag.setDisabled(true);
                    if (notifyTag.getValue() != null) {
                        html.append(notifyTag.getValue());
                    }
                }
                if (editNotify) {
                    errorMsg = getFormError(notifyTag.getName());
                    if (errorMsg != null) {
                        html.append(" <font color=red>" + errorMsg + "</font><br>");
                    }
                }
                html.append("</td>\n");
                html.append("  </tr>\n");
            }


            // Display the text area for describing the reason for the request
            html.append("  <tr>\n");
            html.append("    <td align=\"right\" valign=\"top\" NOWRAP>Business Justification:</td>\n");
            html.append("    <td align=\"left\" valign=\"top\">");
            // Only edit the business justification if the patch is new
            boolean editJustify = ! patchIdTag.isComplete();
            if (editJustify) {
                errorMsg = getFormError(justifyTag.getName());
                if (errorMsg != null) {
                    html.append("<font color=red>" + errorMsg + "</font><br>");
                }
            }
            if (inputEnabled || editJustify) {
                justifyTag.setDisabled(false);
                html.append(justifyTag.toString());
            } else {
                justifyTag.setDisabled(true);
                if (justifyTag.getValue() != null) {
                    html.append(justifyTag.getValue());
                }
            }
            html.append("</td>\n");
            html.append("  </tr>\n");

            // Display the list of previous service patches
            // But only if this is not an exist patch
            if ((previousPatches != null) && (!patchIdTag.isComplete())) {
                html.append("  <tr>\n");
                html.append("    <td align=\"right\" valign=\"top\" NOWRAP>Use Previous Patch:</td>\n");
                html.append("    <td align=\"left\" valign=\"top\">");
                errorMsg = getFormError(BASE_PATCH_LABEL);
                if (errorMsg != null) {
                    html.append("<font color=red>" + errorMsg + "</font><br>");
                }
                html.append(getPatchTable());
                html.append("    </td>\n");
                html.append("  </tr>\n");
            }

            // Display the submit button if this is a new patch
            if (! patchIdTag.isComplete()) {
                html.append("  <tr><td>&nbsp;</td><td><input type=\"submit\" name=\"" + PATCH_REQUEST_BUTTON + "\" value=\"" + ACTION_CONTINUE + "\"/></td></tr>\n");
            }
        }

        html.append("</table>\n");


        // Complete the input form
        if (inputEnabled) {
            html.append("</form>\n");
        }

        return html.toString();
    }

}

