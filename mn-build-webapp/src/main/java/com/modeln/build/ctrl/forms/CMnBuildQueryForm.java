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

import java.net.URL;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import javax.servlet.http.HttpServletRequest;

import com.modeln.build.web.tags.TextTag;
import com.modeln.build.web.tags.DateTag;
import com.modeln.build.web.tags.OptionTag;
import com.modeln.build.web.tags.SelectTag;
import com.modeln.build.web.tags.TagGroup;

import com.modeln.build.common.data.product.CMnBuildQuery;


/**
 * The build query form provides an HTML interface for the user to 
 * look up build data.   
 * 
 * @author  Shawn Stafford
 */
public class CMnBuildQueryForm {

    /** Default title used when displaying a title and border */
    private static final String DEFAULT_TITLE = "Build Query Input";

    /** Label used to identify the query data object in the session */
    public static final String QUERY_OBJECT_LABEL = "QUERY_OBJECT";

    /** Label used to identify the database host string */
    private static final String HOSTNAME_LABEL = "host";

    /** Label used to identify the database port string */
    private static final String PORT_LABEL = "port";

    /** Label used to identify the database name string */
    private static final String DB_LABEL = "db";

    /** Label used to identify the database username string */
    private static final String USERNAME_LABEL = "user";

    /** Label used to identify the database password string */
    private static final String PASSWORD_LABEL = "pass";

    /** Label used to identify the type of database */
    private static final String TYPE_LABEL = "type";

    /** Label used to identify the build ID string */
    private static final String BUILD_ID_LABEL = "rbid";


    /** Database hostname field */
    private TextTag hostnameTag = new TextTag(HOSTNAME_LABEL);

    /** Database port field */
    private TextTag portTag = new TextTag(PORT_LABEL);

    /** Database name field */
    private TextTag dbTag = new TextTag(DB_LABEL);

    /** Database username field */
    private TextTag usernameTag = new TextTag(USERNAME_LABEL);

    /** Database password field */
    private TextTag passwordTag = new TextTag(PASSWORD_LABEL);

    /** Database type field */
    private SelectTag typeTag = new SelectTag(TYPE_LABEL, CMnBuildQuery.VALID_TYPES);

    /** Build ID field */
    private TextTag buildIdTag = new TextTag(BUILD_ID_LABEL);


    /** The URL used when submitting form input. */
    protected URL formUrl = null;

    /** The URL used when accessing images */
    protected URL imageUrl = null;

    /** Text displayed on the submit button */
    private String buttonText = "Get Build";


    /** Build query data */
    private CMnBuildQuery buildData = null;



    /**
     * Construct a build form.
     *
     * @param  form   URL to use when submitting form input
     * @param  images URL to use when referencing images
     */
    public CMnBuildQueryForm(URL form, URL images) {
        formUrl = form;
        imageUrl = images;

        hostnameTag.setWidth(20);
        portTag.setWidth(5);
        dbTag.setWidth(20);
        usernameTag.setWidth(10);
        passwordTag.setWidth(10);
        buildIdTag.setWidth(10);
    }


    /**
     * Set the database query data that should be rendered in the form.
     *
     * @param  data   Database query data
     */
    public void setValues(CMnBuildQuery data) {
        buildData = data;
        if (data != null) {
            // Pull the field data from the object
            hostnameTag.setValue(data.getHostname());
            portTag.setValue(data.getPort());
            dbTag.setValue(data.getDbName());
            usernameTag.setValue(data.getUsername());
            passwordTag.setValue(data.getPassword());
            typeTag.setSelected(data.getType());
            buildIdTag.setValue(data.getBuildId());
        } else {
            // Clear all of the field values
            hostnameTag.setValue("");
            portTag.setValue("");
            dbTag.setValue("");
            usernameTag.setValue("");
            passwordTag.setValue("");
            typeTag.setSelected("mysql");
            buildIdTag.setValue("");
        }
    }

    /**
     * Set the input fields by examining the HTTP request to see if
     * a value was submitted.
     *
     * @param   req     HTTP request
     */
    public void setValues(HttpServletRequest req) {
        CMnBuildQuery data = (CMnBuildQuery) req.getAttribute(QUERY_OBJECT_LABEL);

        // Attempt to obtain the data from request parameters 
        if (data == null) {
            data = new CMnBuildQuery();
            data.setHostname(req.getParameter(HOSTNAME_LABEL));
            data.setPort(req.getParameter(PORT_LABEL));
            data.setUsername(req.getParameter(USERNAME_LABEL));
            data.setPassword(req.getParameter(PASSWORD_LABEL));
            data.setDbName(req.getParameter(DB_LABEL));
            data.setType(req.getParameter(TYPE_LABEL));
            data.setBuildId(req.getParameter(BUILD_ID_LABEL));
        }

        setValues(data);
    }

    /**
     * Determine if the form input is complete.
     *
     * @return TRUE if all input fields have been filled in
     */
    public boolean isComplete() {
        if ((hostnameTag.getValue() == null) || (hostnameTag.getValue().length() == 0)) { 
            return false;
        }

        if ((portTag.getValue() == null) || (portTag.getValue().length() == 0)) {
            return false;
        }

        if ((dbTag.getValue() == null) || (dbTag.getValue().length() == 0)) {
            return false;
        }

        if ((usernameTag.getValue() == null) || (usernameTag.getValue().length() == 0)) {
            return false;
        }

        if ((passwordTag.getValue() == null) || (passwordTag.getValue().length() == 0)) {
            return false;
        }

        if ((typeTag.getSelected() == null) || (typeTag.getSelected().length == 0)) {
            return false;
        }

        if ((buildIdTag.getValue() == null) || (buildIdTag.getValue().length() == 0)) {
            return false;
        }

        return true;
    }

    /**
     * Obtain the database query data from the fields.  The values must first be populated by
     * calling the setValues method.  A null object may be returned if no data was
     * found in the request.
     *
     * @return   Database query data found in the request
     */
    public CMnBuildQuery getValues() {
        return buildData;
    }

    /**
     * Construct a URL from the form values.
     * 
     * @return  URL composed of the form values
     */
    public static String getUrlParams(CMnBuildQuery data) {
        StringBuffer url = new StringBuffer();

        if (data.getHostname() != null) {
            url.append(HOSTNAME_LABEL + "=" + data.getHostname());
        }
        if (data.getPort() != null) {
            if (url.length() > 0) url.append("&");
            url.append(PORT_LABEL + "=" + data.getPort());
        }
        if (data.getUsername() != null) {
            if (url.length() > 0) url.append("&");
            url.append(USERNAME_LABEL + "=" + data.getUsername());
        }
        if (data.getPassword() != null) {
            if (url.length() > 0) url.append("&");
            url.append(PASSWORD_LABEL + "=" + data.getPassword());
        }
        if (data.getDbName() != null) {
            if (url.length() > 0) url.append("&");
            url.append(DB_LABEL + "=" + data.getDbName());
        }
        if (data.getType() != null) {
            if (url.length() > 0) url.append("&");
            url.append(TYPE_LABEL + "=" + data.getType());
        }
        if (data.getBuildId() != null) {
            if (url.length() > 0) url.append("&");
            url.append(BUILD_ID_LABEL + "=" + data.getBuildId());
        }

        return url.toString();
    } 

    /**
     * Render the build data form.
     */
    public String toString() {
        StringBuffer html = new StringBuffer();

        html.append("<form method=\"post\" action=\"" + formUrl + "\">\n");

        //html.append("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\" bgcolor=\"#FFFFFF\">\n");
        html.append("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#FFFFFF\">\n");

        // Database server information 
        html.append("  <tr>\n");
        html.append("    <td valign=\"top\">\n");
        html.append("      <table border=\"0\" cellspacing=\"0\" cellpadding=\"2\" bgcolor=\"#FFFFFF\">\n");

        html.append("        <tr>\n");
        html.append("          <td>\n");
        html.append("            <table border=\"0\" cellspacing=\"0\" cellpadding=\"2\" bgcolor=\"#FFFFFF\">\n");
        html.append("              <tr>\n");
        html.append("                <td>Hostname:<br>" + hostnameTag.toString() + "</td>\n");
        html.append("                <td>Port:<br>" + portTag.toString() + "</td>\n");
        html.append("              </tr>\n");
        html.append("            </table>\n");
        html.append("          </td>\n");
        html.append("        </tr>\n");

        html.append("        <tr>\n");
        html.append("          <td>\n");
        html.append("            <table border=\"0\" cellspacing=\"0\" cellpadding=\"2\" bgcolor=\"#FFFFFF\">\n");
        html.append("              <tr>\n");
        html.append("                <td>Database Name:<br>" + dbTag.toString() + "</td>\n");
        html.append("                <td>Type:<br>" + typeTag.toString() + "</td>\n");
        html.append("              </tr>\n");
        html.append("            </table>\n");
        html.append("          </td>\n");
        html.append("        </tr>\n");

        html.append("        <tr>\n");
        html.append("          <td>\n");
        html.append("            <table border=\"0\" cellspacing=\"0\" cellpadding=\"2\" bgcolor=\"#FFFFFF\">\n");
        html.append("              <tr>\n");
        html.append("                <td>Username:<br>" + usernameTag.toString() + "</td>\n");
        html.append("                <td>Password:<br>" + passwordTag.toString() + "</td>\n");
        html.append("              </tr>\n");
        html.append("            </table>\n");
        html.append("          </td>\n");
        html.append("        </tr>\n");

        html.append("        <tr>\n");
        html.append("          <td>\n");
        html.append("            <table border=\"0\" cellspacing=\"0\" cellpadding=\"2\" bgcolor=\"#FFFFFF\">\n");
        html.append("              <tr>\n");
        html.append("                <td>Build ID:<br>" + buildIdTag.toString() + "</td>\n");
        html.append("              </tr>\n");
        html.append("            </table>\n");
        html.append("          </td>\n");
        html.append("        </tr>\n");

        html.append("      </table>\n");

        html.append("    </td>\n");

        html.append("  </tr>\n");
        html.append("  <tr><td align=\"center\"><input type=\"submit\" value=\"" + buttonText + "\"></a></td></tr>\n");
        html.append("</table>\n");

        html.append("</form>\n");

        return html.toString();
    }



}
