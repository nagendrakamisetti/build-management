/*
 * Login.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.ctrl.command.patch;

import com.modeln.build.common.data.product.CMnPatch;
import com.modeln.build.common.tool.CMnPatchUtil;
import com.modeln.build.ctrl.database.CMnPatchTable;
import com.modeln.build.ctrl.forms.IMnPatchForm;
import com.modeln.build.jenkins.XmlApi;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.servlet.*;
import javax.servlet.http.*;

import com.modeln.build.web.errors.ApplicationError;
import com.modeln.build.web.errors.ApplicationException;
import com.modeln.build.web.application.CommandResult;
import com.modeln.build.web.application.ProtectedCommand;
import com.modeln.build.web.application.WebApplication;
import com.modeln.build.web.database.RepositoryConnection;
import com.modeln.build.web.errors.ErrorMap;
import com.modeln.build.web.util.SessionUtility;

import org.w3c.dom.Document;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



/**
 * This command is used to test the integration with Jenkins. 
 *
 * @author             Shawn Stafford
 */
public class CMnCreateJob extends ProtectedCommand {

 
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
            CMnPatch patch = null;

            RepositoryConnection rc = null;
            try {
                rc = app.getRepositoryConnection();
                CMnPatchTable patchTable = CMnPatchTable.getInstance();
                app.debug("CMnCreateJob: obtained a connection to the build database");

                // Fall back to the request attributes in case the data was set by another command
                String patchId = (String) req.getParameter(IMnPatchForm.PATCH_ID_LABEL);
                if (patchId == null) {
                    patchId = (String) req.getAttribute(IMnPatchForm.PATCH_ID_LABEL);
                }
                if ((patchId != null) && (patchId.length() > 0)) {
                    patch = patchTable.getRequest(rc.getConnection(), patchId, true);
                    app.debug("CMnCreateJob: obtained data for patch ID " + patchId);
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


            Hashtable<String, Document> jobs = new Hashtable<String, Document>();
            try {
                Document shortBuild = getBuildJob(app, req, res, patch, true);
                if (shortBuild != null) {
                    String shortName = CMnPatchUtil.getJobName(patch, true);
                    jobs.put(shortName, shortBuild); 
                }

                Document longBuild  = getBuildJob(app, req, res, patch, false);
                if (longBuild != null) {
                    String longName = CMnPatchUtil.getJobName(patch, false);
                    jobs.put(longName, longBuild);
                }
            } catch (IOException ioex) {
                String msg = "Failed to load the Jenkins job template: " + ioex.getMessage();
                app.debug(msg);
                exApp = new ApplicationException(ErrorMap.APPLICATION_DISPLAY_FAILURE, msg);
                exApp.setStackTrace(ioex);
            } catch (ServletException sex) {
                String msg = "Failed to load the Jenkins job template: " + sex.getMessage();
                app.debug(msg);
                exApp = new ApplicationException(ErrorMap.APPLICATION_DISPLAY_FAILURE, msg);
                exApp.setStackTrace(sex);
            }


            // Send the job request to Jenkins
            String url = app.getConfigValue("patch.jenkins.url");
            app.debug("Loaded Jenkins URL from config: " + url);
            if ((jobs.size() > 0) && (url != null)) {
                try {
                    // Construct a Jenkins API instance
                    URL jenkinsUrl = new URL(url);
                    XmlApi jenkins = new XmlApi(jenkinsUrl);
                    Enumeration keys = jobs.keys();

                    // Create each Jenkins job on the Jenkins server
                    while (keys.hasMoreElements()) {
                        String key = (String) keys.nextElement();
                        Document value = (Document) jobs.get(key); 
                        app.debug("CMnCreateJob: preparing to create Jenkins job: " + key);
                        int jobStatus = jenkins.createJob(key, value);
                        app.debug("CMnCreateJob: job creation status: " + key + " = " + jobStatus);
                    }
                } catch (MalformedURLException mfex) {
                    app.debug("CMnCreateJob: failed to parse Jenkins URL: " + mfex.getMessage());
                    exApp = new ApplicationException(
                        ErrorMap.APPLICATION_DISPLAY_FAILURE,
                        "Unable to parse the Jenkins URL: " + mfex.getMessage());
                    exApp.setStackTrace(mfex);
                } catch (IOException ioex) {
                    app.debug("CMnCreateJob: failed to connect to Jenkins instance: " + ioex.getMessage());
                    exApp = new ApplicationException(
                        ErrorMap.APPLICATION_DISPLAY_FAILURE,
                        "Unable to connect to the Jenkins instance: " + ioex.getMessage());
                    exApp.setStackTrace(ioex);
                }
            } else if (url == null) {
                app.debug("CMnCreateJob: skipping Jenkins call due to null Jenkins URL"); 
            } else {
                app.debug("CMnCreateJob: zero Jenkins jobs to create");
            }

            // Throw any exceptions once the database connections have been cleaned up
            if (exApp != null) {
                throw exApp;
            }

        }

        // Send the user to the patch information page
        if (result.getDestination() == null) {
            result = app.forwardToCommand(req, res, "/patch/CMnPatchRequest"); 
        }

        return result;
    }


    /**
     * Return the jenkins job for creating the service patch branch. 
     */
    private Document getPatchJob(WebApplication app, HttpServletRequest req, HttpServletResponse res, CMnPatch patch)
        throws IOException, ServletException
    {
        Document job = null;

        // Location where the service patch should create the build.sh script
        String jobname = CMnPatchUtil.getJobName(patch);
        String scriptdir = "$HOME/servicepatch/" + jobname;

        String patchcmd = CMnPatchUtil.getPatchBuildCmd(patch, scriptdir);

        // Update the job with the patch details
        if (patch.getFixes() != null) {
            app.debug("CMnCreateJob: Found " + patch.getFixes().size() + " fixes for this patch."); 
        } else {
            app.debug("CMnCreateJob: patch contains no fixes.");
        }
        if (patchcmd != null) { 
            app.debug("CMnCreateJob: patch command: " + patchcmd);
        }

        return null;
    }


    /**
     * Return the jenkins job for running the build.
     */
    private Document getBuildJob(WebApplication app, HttpServletRequest req, HttpServletResponse res, CMnPatch patch, boolean quick) 
        throws IOException, ServletException
    {
        // Load the Jenkins job template
        Document job = app.loadXmlConfig(req, res, "jenkins_job.xml");

        // Location where the service patch should create the build.sh script
        String jobname = CMnPatchUtil.getJobName(patch);
        String scriptdir = "$HOME/servicepatch/" + jobname;

        // Collect information about the patch
        String flag = "";
        if (quick) {
            flag = "-short";
        }
        String buildcmd = scriptdir + "/build.sh " + flag;
        String vernum = CMnPatchUtil.getVersionNumber(patch.getBuild().getBuildVersion());

        String email = app.getConfigValue("mail.from");
        //String email = CMnPatchUtil.getNotificationList(patch);
        if ((email == null) || (email.length() == 0)) {
            email = "mn-pd-bre-alerts@modeln.com";
        }

       // Update the Jenkins job to call the build script 
       app.debug("CMnCreateJob: build command: " + buildcmd);
       addCommand(job, buildcmd);

       setGitInfo(job, patch); 

       replaceNodeValue(job, "recipients", email);
       replaceNodeValue(job, "assignedNode", vernum);

       return job;
    }


    /**
     * Update the build with source control information.
     *
     * @param   doc    XML Document
     * @param   patch  Service patch information
     */
    private boolean setGitInfo(Document doc, CMnPatch patch) {
        boolean success = false;

        // Get the repository name from the product build
        String[] scmInfo = CMnPatchUtil.getRepositoryInfo(patch);
        String repository = scmInfo[1];

        // Construct the service patch branch name from the patch request
        String branch = CMnPatchUtil.getBranchName(patch);

        try {
            // Update the repository URI
            NodeList repoNodes = doc.getElementsByTagName("scm");
            for (int idx = 0; idx < repoNodes.getLength(); idx++) {
                Node node = repoNodes.item(idx);
                int repoCount = replaceVariable(node, "@@repository@@", repository);
                int branchCount = replaceVariable(node, "@@branch@@", repository);
                if ((repoCount > 0) || (branchCount > 0)) {
                    success = true;
                }
            }
        } catch (DOMException dex) {
            success = false;
        }

        return success;
    }
 

    /**
     * Recursively check each node and replace the variable with the value.
     *
     * @param   node   XML Node
     * @param   var    Variable name
     * @param   val    Replacement value
     */
    private int replaceVariable(Node node, String var, String val) throws DOMException {
        int count = 0;

        // Replace the value in the current node
        if ((node.getNodeValue() != null) && (node.getNodeValue().contains(var))) {
            String value = node.getNodeValue().replace(var, val);
            node.setNodeValue(value);
            count++;
        }

        // Recursively process the child nodes
        NodeList children = node.getChildNodes();
        for (int idx = 0; idx < children.getLength(); idx++) {
            count = count + replaceVariable(children.item(idx), var, val);
        }

        return count;
    }

    /**
     * Replace the content of the named XML node with the new value.
     *
     * @param   doc    XML Document
     * @param   name   Node name
     * @param   value  New node value
     */
    private boolean replaceNodeValue(Document doc, String name, String value) throws DOMException {
        boolean success = false;

        try {
            NodeList nodes = doc.getElementsByTagName(name);
            for (int idx = 0; idx < nodes.getLength(); idx++) {
                Node node = nodes.item(idx);
                node.setTextContent(value);
                success = true;
            }
        } catch (DOMException dex) {
            success = false;
        }

        return success;
    } 

    /**
     * Add a script command to the Jenkins job.
     *
     * @param  doc    Jenkins job
     * @param  cmd    Script command
     */
    private boolean addCommand(Document doc, String cmd) throws DOMException {
        boolean success = false;

        try {
            NodeList builderNodes = doc.getElementsByTagName("builders");
            for (int idx = 0; idx < builderNodes.getLength(); idx++) {
                Node builderNode = builderNodes.item(idx);
                Element shellNode = doc.createElement("hudson.tasks.Shell");
                Element commandNode = doc.createElement("command");
                commandNode.setTextContent(cmd);
                shellNode.appendChild(commandNode);
                builderNode.appendChild(shellNode);
                success = true;
            }
        } catch (DOMException dex) {
            success = false;
        }

        return success;
    }

}

