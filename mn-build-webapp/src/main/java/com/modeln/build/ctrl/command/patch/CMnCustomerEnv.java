/*
 * Login.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.ctrl.command.patch; 

import com.modeln.build.common.data.account.CMnAccount;
import com.modeln.build.common.data.account.CMnEnvironment;
import com.modeln.build.common.data.product.CMnProduct;
import com.modeln.build.common.database.CMnCustomerTable;
import com.modeln.build.ctrl.forms.IMnPatchForm;
import com.modeln.testfw.reporting.CMnBuildTable;
import com.modeln.testfw.reporting.CMnDbBuildData;
import com.modeln.testfw.reporting.CMnReleaseSummaryTable;

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
 * This command displays the data for a single customer environment. 
 * 
 * @author             Shawn Stafford
 */
public class CMnCustomerEnv extends ProtectedCommand {

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
                CMnReleaseSummaryTable releaseTable = CMnReleaseSummaryTable.getInstance();
                CMnCustomerTable custTable = CMnCustomerTable.getInstance();
                CMnBuildTable buildTable = CMnBuildTable.getInstance();

                //
                // Retrieve the form parameters from the request
                // 
                String custId = (String) req.getParameter(IMnPatchForm.CUSTOMER_ID_LABEL);
                if (custId == null) {
                    custId = (String) req.getAttribute(IMnPatchForm.CUSTOMER_ID_LABEL);
                }
                String envId = (String) req.getParameter(IMnPatchForm.ENV_ID_LABEL);
                if (envId == null) {
                    envId = (String) req.getAttribute(IMnPatchForm.ENV_ID_LABEL);
                }
                String envName = (String) req.getParameter(IMnPatchForm.ENV_NAME_LABEL);
                if (envName == null) {
                    envName = (String) req.getAttribute(IMnPatchForm.ENV_NAME_LABEL);
                }
                String productId = (String) req.getParameter(IMnPatchForm.PRODUCT_ID_LABEL);
                if (productId == null) {
                    productId = (String) req.getParameter(IMnPatchForm.PRODUCT_ID_LABEL);
                }
                String releaseId = (String) req.getParameter(IMnPatchForm.RELEASE_ID_LABEL);
                if (releaseId == null) {
                    releaseId = (String) req.getAttribute(IMnPatchForm.RELEASE_ID_LABEL);
                }
                String buildId = (String) req.getParameter(IMnPatchForm.BUILD_ID_LABEL);
                if (buildId == null) {
                    buildId = (String) req.getAttribute(IMnPatchForm.BUILD_ID_LABEL);
                }

                // Obtain customer information to maintain database relationship between env and cust
                CMnAccount cust = null;
                if ((custId != null) && (custId.length() > 0)) {
                    cust = custTable.getCustomer(rc.getConnection(), custId);
                }

                // If envId is specified it means we are editing an existing environment
                CMnEnvironment env = null;
                if ((envId != null) && (envId.length() > 0)) {
                    env = custTable.getEnvironment(rc.getConnection(), envId);
                }


                // Determine if the user is viewing the environment data or submitting an update
                if ((envName != null) && (buildId != null)) {
                    // Update the environment with the new information
                    if (env == null) {
                        env = new CMnEnvironment();
                    }
                    env.setName(envName);

                    // Update the product information if necessary
                    CMnProduct prod = null;
                    if ((productId != null) && (productId.length() > 0)) {
                        prod = custTable.getProduct(rc.getConnection(), productId);
                        env.setProduct(prod);
                    }

                    // Update the build information if necessary
                    if (buildId.length() > 0) {
                        CMnDbBuildData build = null;
                        build = buildTable.getBuild(rc.getConnection(), buildId);
                        env.setBuild(build);
                    }
                }



                // Determine if the user has submitted updated environment information
                if ((env != null) && (env.getBuild() != null)) {
                    // Determine if we are performing an add or an update
                    if ((env.getId() != null) && (env.getId() > 0)) {
                        custTable.updateEnvironment(rc.getConnection(), env);
                    } else {
                        custTable.addEnvironment(rc.getConnection(), custId, env);
                        cust.addEnvironment(env);
                    }
                    req.setAttribute(IMnPatchForm.CUSTOMER_DATA, cust);
                    result = app.forwardToCommand(req, res, "/patch/CMnCustomerData");
                } else {
                    req.setAttribute(IMnPatchForm.CUSTOMER_DATA, cust);
                    if (env != null) {
                        req.setAttribute(IMnPatchForm.ENV_DATA, env);
                    }

                    // Retrieve the list of products that the user can select from
                    req.setAttribute(IMnPatchForm.PRODUCT_LIST_DATA, custTable.getAllProducts(rc.getConnection())); 

                    // Retrieve the list of released products that the user can select from
                    req.setAttribute(IMnPatchForm.RELEASE_LIST_DATA, releaseTable.getSummaryList(rc.getConnection()));

                    result.setDestination("patch/customer_env.jsp");

                }
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
