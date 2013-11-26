/*
 * Login.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.ctrl.command.database; 

import java.io.*;
import java.net.URL;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.Connection;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.util.Vector;

import com.modeln.build.web.errors.ApplicationError;
import com.modeln.build.web.errors.ApplicationException;
import com.modeln.build.web.application.CommandResult;
import com.modeln.build.web.application.TrustedHostCommand;
import com.modeln.build.web.application.WebApplication;
import com.modeln.build.web.errors.ErrorMap;

import com.modeln.build.common.data.database.CMnCachedResultSet;
import com.modeln.build.common.data.database.CMnQueryData;
import com.modeln.build.ctrl.forms.CMnDatabaseQueryForm;
import com.modeln.build.ctrl.forms.CMnDatabaseQueryHistoryForm;

/**
 * This command allows users to query an Oracle database. 
 * 
 * @author Shawn Stafford
 */
public class CMnDatabaseQuery extends TrustedHostCommand {

    /**
     * This is the primary method which will be used to perform the command
     * actions.  The application will use this method to service incoming
     * requests.  You must pass a reference to the calling application into
     * the service method to allow callback method calls to be performed.
     *
     * @param   app     Application which called the command
     * @param   req     HttpServletRequest object
     * @param   res     HttpServletResponse object
     */
    public CommandResult execute(WebApplication app, HttpServletRequest req, HttpServletResponse res) 
        throws ApplicationException
    {
        // Execute the generic actions for all commands
        CommandResult result = super.execute(app, req, res);

        // Execute the actions for the command
        if (!result.containsError()) {
            ApplicationException exApp = null;
            ApplicationError error = null;
            CMnQueryData query = null;
            Connection conn = null;
            try {
                // Determine if the user is operating from the query history
                String historyAction = req.getParameter(CMnDatabaseQueryHistoryForm.ACTION_LABEL);
                CMnDatabaseQueryHistoryForm historyForm = null;
                if (historyAction != null) {
                    historyForm = new CMnDatabaseQueryHistoryForm(new URL("http://localhost"), new URL("http://localhost"));
                    historyForm.setValues(req);
                    if (historyAction.equals(CMnDatabaseQueryHistoryForm.RUN_ACTION)) {
                        query = historyForm.getSelected();
                    } else {
                        historyForm.updateHistory(req);
                    }

                    // Make sure the query object is never null
                    if (query == null) {
                        query = new CMnQueryData();
                    }
                } else {
                    // If the user is not using the history, collect SQL input
                    CMnDatabaseQueryForm form = new CMnDatabaseQueryForm(new URL("http://localhost"), new URL("http://localhost"));
                    form.setValues(req);
                    query = form.getValues();
                }


                // Perform the database query
                String sql = query.getSQL(); 
                if ((sql != null) && (sql.trim().length() > 0)) {
                    Statement st = null;
                    ResultSet rs = null;
                    try {
                        String url = query.getJdbcUrl();
                        String user = query.getUsername();
                        String pass = query.getPassword();
                        conn = DriverManager.getConnection(url, user, pass);
                        if (conn != null) {
                            st = conn.createStatement();
                            rs = st.executeQuery(sql.toString());
                            //rs.first();
                            query.setResults(new CMnCachedResultSet(rs));
                            query.setMessage("Query execution complete.");
                        } else {
                            query.setMessage("Unable to establish a connection to the database: url=" + url + ", user=" + user + ", pass=" + pass);
                        }

                        // Keep a history of the query
                        if (historyForm == null) {
                            HttpSession session = req.getSession(true);
                            Vector history = (Vector) session.getAttribute(CMnDatabaseQueryHistoryForm.HISTORY_OBJECT_LABEL); 
                            if (history == null) {
                                history = new Vector();
                                session.setAttribute(CMnDatabaseQueryHistoryForm.HISTORY_OBJECT_LABEL, history);
                            }
                            history.add(query);
                        }
                    } catch (SQLException sqlex) {
                        query.setMessage("Unable to perform the query: " + sqlex.getMessage());
                        sqlex.printStackTrace(); 
                    }

                    // Close the database connections
                    try {
                        if (rs != null)   rs.close();
                        if (st != null)   st.close();
                        if (conn != null) conn.close();
                    } catch (SQLException sqlex) {
                        query.setMessage("Unable to close the database connection: " + sqlex.getMessage());
                        sqlex.printStackTrace();
                    }

                }

                // Return the results of the query
                req.setAttribute(CMnDatabaseQueryForm.QUERY_OBJECT_LABEL, query);
                result.setDestination("database/query_editor.jsp");

            } catch (Exception ex) {
                exApp = new ApplicationException(
                    ErrorMap.APPLICATION_DISPLAY_FAILURE,
                    "Failed to process command.");
                exApp.setStackTrace(ex);
            } finally {
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException sqlex) {
                        if (exApp != null) {
                            // There are pre-existing errors, so just silently log this one
                            sqlex.printStackTrace();
                        } else {
                            exApp = new ApplicationException(
                                ErrorMap.APPLICATION_DISPLAY_FAILURE,
                                "Unable to close the database connection: " + sqlex.getMessage());
                        }
                    }
                }

                // Throw any exceptions once the database connections have been cleaned up
                if (exApp != null) {
                    throw exApp;
                }
            }
        }

        return result;
    }



}
