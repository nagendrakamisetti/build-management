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

import com.modeln.build.common.data.database.CMnCachedMetaData;
import com.modeln.build.common.data.database.CMnCachedResultSet;
import com.modeln.build.common.data.database.CMnCachedRowSet;
import com.modeln.build.common.data.database.CMnQueryData;


/**
 * The database query form provides an HTML interface for the user to 
 * enter database SQL statements.   
 * 
 * @author  Shawn Stafford
 */
public class CMnDatabaseQueryForm extends CMnBaseForm {

    /** Default title used when displaying a title and border */
    private static final String DEFAULT_TITLE = "Database Query Input";

    /** Label used to identify the name of the query */
    private static final String NAME_LABEL = "name";

    /** Label used to identify the SQL query string */
    private static final String SQL_LABEL = "sql";

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


    /** Label used to identify the SQL data object in the session */
    public static final String QUERY_OBJECT_LABEL = "QUERY_OBJECT";

    /** Query name input field */
    private TextTag nameTag = new TextTag(NAME_LABEL);

    /** SQL input field */
    private TextTag sqlTag = new TextTag(SQL_LABEL);

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
    private SelectTag typeTag = new SelectTag(TYPE_LABEL, CMnQueryData.VALID_TYPES);

    /** SQL Query Data **/
    private CMnQueryData sqlData = null;



    /**
     * Construct a build form.
     *
     * @param  form   URL to use when submitting form input
     * @param  images URL to use when referencing images
     */
    public CMnDatabaseQueryForm(URL form, URL images) {
        super(form, images);
        setInputMode(true);

        hostnameTag.setWidth(20);
        portTag.setWidth(5);
        dbTag.setWidth(20);
        usernameTag.setWidth(10);
        passwordTag.setWidth(10);

        nameTag.setWidth(20);
        sqlTag.setWidth(60);
        sqlTag.setHeight(6);

        setButtonText("Run SQL");
    }


    /**
     * Set the database query data that should be rendered in the form.
     *
     * @param  data   Database query data
     */
    public void setValues(CMnQueryData data) {
        sqlData = data;
        if (data != null) {
            // Pull the field data from the object
            hostnameTag.setValue(data.getHostname());
            portTag.setValue(data.getPort());
            dbTag.setValue(data.getDbName());
            usernameTag.setValue(data.getUsername());
            passwordTag.setValue(data.getPassword());
            nameTag.setValue(data.getName());
            sqlTag.setValue(data.getSQL());
            typeTag.setSelected(data.getType());
        } else {
            // Clear all of the field values
            hostnameTag.setValue("");
            portTag.setValue("");
            dbTag.setValue("");
            usernameTag.setValue("");
            passwordTag.setValue("");
            nameTag.setValue("");
            sqlTag.setValue("");
            typeTag.setSelected("oracle");
        }
    }

    /**
     * Set the input fields by examining the HTTP request to see if
     * a value was submitted.
     *
     * @param   req     HTTP request
     */
    public void setValues(HttpServletRequest req) {
        CMnQueryData data = (CMnQueryData) req.getAttribute(QUERY_OBJECT_LABEL);

        // Attempt to obtain the data from request parameters 
        if (data == null) {
            data = new CMnQueryData();
            data.setHostname(req.getParameter(HOSTNAME_LABEL));
            data.setPort(req.getParameter(PORT_LABEL));
            data.setUsername(req.getParameter(USERNAME_LABEL));
            data.setPassword(req.getParameter(PASSWORD_LABEL));
            data.setDbName(req.getParameter(DB_LABEL));
            data.setType(req.getParameter(TYPE_LABEL));
            data.setName(req.getParameter(NAME_LABEL));
            data.setSQL(req.getParameter(SQL_LABEL));
        }

        setValues(data);
    }

    /**
     * Obtain the database query data from the fields.  The values must first be populated by
     * calling the setValues method.  A null object may be returned if no data was
     * found in the request.
     *
     * @return   Database query data found in the request
     */
    public CMnQueryData getValues() {
        return sqlData;
    }

    /**
     * Construct a URL from the form values.
     * 
     * @return  URL composed of the form values
     */
    public static String getUrlParams(CMnQueryData data) {
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

        return url.toString();
    } 

    /**
     * Render the build data form.
     */
    public String toString() {
        StringBuffer html = new StringBuffer();

        html.append("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\" bgcolor=\"#FFFFFF\">\n");

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

        html.append("      </table>\n");

        html.append("    </td>\n");

        // SQL Text
        html.append("    <td valign=\"top\">\n");
        html.append("Query Name: (optional):<br>" + nameTag.toString() + "<br>\n");
        html.append("SQL Query:<br>" + sqlTag.toString());
        html.append("    </td>\n");

        html.append("  </tr>\n");
        html.append("</table>\n");

        return html.toString();
    }


    /**
     * Create a table which defines a title and border for the contents.
     *
     * @param   title   Text to be placed in the title bar
     * @param   content Content of the table
     */
    public String getTitledBorder(String content) {
        return getTitledBorder(DEFAULT_TITLE, content);
    }

    /**
     * Display the query results as html.
     */
    public String getResults() {
        StringBuffer html = new StringBuffer();

        if (sqlData.getResults() != null) {
            CMnCachedResultSet results = sqlData.getResults();
            html.append("<div class=\"scroll\">\n");
            html.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"1\">\n");

            // Display a table header
            CMnCachedMetaData metadata = results.getMetaData();
            if (metadata != null) {
                html.append(getResultsHeader(metadata));
            }

            // Display the results
            html.append(getResultsBody(results));

            html.append("</table>\n");
            html.append("</div>\n");
        } else {
            html.append("<center>Zero results found.</center><p>\n");
        }

        return html.toString();
    }


    /**
     * Display the result set header as html.
     *
     * @param  metadata   ResultSet metadata
     */
    private String getResultsHeader(CMnCachedMetaData metadata) {
        StringBuffer html = new StringBuffer();

        int columns = 1;
        try {
            columns = metadata.getColumnCount();
            String label = null;
            html.append("<tr>\n");
            for (int idx = 0; idx < columns; idx++) {
                label = metadata.getColumnName(idx);
                html.append("<th class=\"tableheader\">" + label  + "</th>\n");
            }
            html.append("</tr>\n");
        } catch (Exception ex) {
            html = new StringBuffer();
            html.append("<tr>\n");
            html.append("  <td colspan=\"" + columns + "\">\n");
            html.append(ex.getMessage());
            html.append("  </td>\n");
            html.append("</tr>\n");
            ex.printStackTrace();
        }

        return html.toString();
    }


    /**
     * Display the result set rows as html.
     *
     * @param  rs   ResultSet
     */
    private String getResultsBody(CMnCachedResultSet rs) {
        StringBuffer html = new StringBuffer();

        int columns = 1; 
        try {
            columns = rs.getMetaData().getColumnCount();
            CMnCachedRowSet row = null;
            String value = null;
            while (rs.hasNext()) {
                html.append("<tr>\n");
                row = rs.next();
                for (int idx = 0; idx < columns; idx++) {
                    value = (String) row.get(idx);
                    html.append("<td  class=\"tablecontent\" valign=\"top\">" + value  + "</td>\n");
                }

                html.append("</tr>\n");
            }
        } catch (Exception ex) {
            html = new StringBuffer();
            html.append("<tr>\n");
            html.append("  <td colspan=\"" + columns + "\">\n");
            html.append(ex.getMessage());
            html.append("  </td>\n");
            html.append("</tr>\n");
            ex.printStackTrace();
        }

        return html.toString();
    }


}
