/*
 * Login.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.ctrl.command.patch; 

import com.modeln.build.common.data.account.CMnAccount;
import com.modeln.build.common.database.CMnCustomerTable;
import com.modeln.build.ctrl.forms.IMnPatchForm;

import java.io.*;
import java.net.URL;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Vector;

import com.modeln.build.web.errors.ApplicationError;
import com.modeln.build.web.errors.ApplicationException;
import com.modeln.build.web.application.CommandResult;
import com.modeln.build.web.application.ProtectedCommand;
import com.modeln.build.web.application.WebApplication;
import com.modeln.build.web.database.RepositoryConnection;
import com.modeln.build.web.errors.ErrorMap;


/**
 * This command displays the data for a single customer. 
 * 
 * @author             Shawn Stafford
 */
public class CMnCustomerData extends ProtectedCommand {

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
            RepositoryConnection rc = null;
            try {
                rc = app.getRepositoryConnection();
                CMnCustomerTable custTable = CMnCustomerTable.getInstance();

                String custId = (String) req.getParameter(IMnPatchForm.CUSTOMER_ID_LABEL);
                if (custId == null) {
                    custId = (String) req.getAttribute(IMnPatchForm.CUSTOMER_ID_LABEL);
                }

                String custName = (String) req.getParameter(IMnPatchForm.CUSTOMER_NAME_LABEL);
                if (custName == null) {
                    custName = (String) req.getAttribute(IMnPatchForm.CUSTOMER_NAME_LABEL);
                }

                String custShortName = (String) req.getParameter(IMnPatchForm.CUSTOMER_SHORT_NAME_LABEL);
                if (custShortName == null) {
                    custShortName = (String) req.getAttribute(IMnPatchForm.CUSTOMER_SHORT_NAME_LABEL);
                }

                // If custId is specified it means we are editing an existing customer
                CMnAccount cust = null;
                if ((custId != null) && (custId.length() > 0)) {
                    // Update an existing customer record
                    cust = custTable.getCustomer(rc.getConnection(), custId);
                    if ((custName != null) && (custName.length() > 0)) {
                        cust.setName(custName);
                    }
                    if ((custShortName != null) && (custShortName.length() > 0)) {
                        cust.setShortName(custShortName);
                    }
                    custTable.updateCustomer(rc.getConnection(), cust);
                } else if ((custName != null) && (custName.length() > 0)) {
                    // Create a new customer record
                    cust = new CMnAccount();
                    cust.setName(custName);
                    cust.setShortName(custShortName);
                    String id = custTable.addCustomer(rc.getConnection(), cust);
                    if ((id != null) && (id.length() > 0)) {
                        cust.setId(new Integer(id)); 
                    }
                }

                req.setAttribute(IMnPatchForm.CUSTOMER_DATA, cust);

                result.setDestination("patch/customer.jsp");
            } catch (ApplicationException aex) {
                exApp = aex;
            } catch (Exception ex) {
                exApp = new ApplicationException(
                    ErrorMap.APPLICATION_DISPLAY_FAILURE,
                    "Failed to process command.");
                exApp.setStackTrace(ex);
            } finally {
                app.releaseRepositoryConnection(rc);

                // Throw any exceptions once the database connections have been cleaned up
                if (exApp != null) {
                    throw exApp;
                }
            }
        }

        return result;
    }


}
