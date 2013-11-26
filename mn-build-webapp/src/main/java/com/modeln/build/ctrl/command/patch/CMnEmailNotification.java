/*
 * Login.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.ctrl.command.patch;

import com.modeln.build.common.data.product.CMnPatch;
import com.modeln.build.common.data.product.CMnPatchApprover;
import com.modeln.build.common.data.product.CMnPatchFix;
import com.modeln.build.common.data.product.CMnPatchNotification;
import com.modeln.build.common.data.product.CMnPatchOwner;
import com.modeln.build.common.tool.CMnPatchUtil;
import com.modeln.build.ctrl.forms.CMnBaseForm;
import com.modeln.build.ctrl.forms.IMnPatchForm;
import com.modeln.build.sourcecontrol.CMnCheckIn;
import com.modeln.build.sourcecontrol.CMnGitCheckIn;
import com.modeln.build.sourcecontrol.CMnPerforceCheckIn;
import com.modeln.build.common.data.account.UserData;
import com.modeln.build.web.errors.ApplicationError;
import com.modeln.build.web.errors.ApplicationException;
import com.modeln.build.web.application.WebApplication;
import com.modeln.build.web.errors.ErrorMap;
import com.modeln.build.web.util.HttpUtility;
import com.modeln.build.web.util.SessionUtility;

import java.io.UnsupportedEncodingException;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.Vector;


/**
 * This utility class provides common functionality for sending 
 * e-mail notification regarding service patch information. 
 *
 * @author             Shawn Stafford
 */
public class CMnEmailNotification {

    /**
     * Send e-mail notification to the approvers once the patch
     * request has been finalized.
     *
     * @param  app         Web application reference
     * @param  req         HTTP request information
     * @param  patch       Service patch information
     * @param  approvers   List of service patch approvers
     */
    protected static void notifyApprovers(
            WebApplication app, 
            HttpServletRequest req, 
            CMnPatch patch, 
            Vector<CMnPatchApprover> approvers) 
        throws ApplicationException
    {
        // Get the login information from the session
        UserData user = SessionUtility.getLogin(req.getSession());

        ApplicationException exApp = null;
        ApplicationError error = null;
        Transport smtp = null;
        try {
            app.debug("CMnEmailNotification.notifyApprovers(): obtained a connection to the build database");

            // Add the list of approver e-mail addresses as recipients
            Vector<InternetAddress> to = new Vector<InternetAddress>();
            Enumeration approverList = approvers.elements();
            while (approverList.hasMoreElements()) {
                CMnPatchApprover approver = (CMnPatchApprover) approverList.nextElement();
                if ((approver != null) && (approver.getUser() != null)) {
                    InternetAddress addr = getEmailAddress(approver.getUser());
                    if (addr != null) {
                        to.add(addr);
                    }
                }
            }
            // Convert the list of addresses to an array
            InternetAddress[] recipients = new InternetAddress[to.size()];
            to.toArray(recipients);

            // Format the mime message
            String subject = "Service Patch " + patch.getStatus() +
                ": " + patch.getCustomer().getName() + " " + patch.getBuild().getReleaseId() + " " + patch.getName();

            StringBuffer msgBuffer = new StringBuffer();
            msgBuffer.append("The following service patch is now in " + patch.getStatus() + " status:\n\n");
            msgBuffer.append("Customer: " + patch.getCustomer().getName() + "\n");
            msgBuffer.append("Environment: " + patch.getEnvironment().getName() + "\n");
            msgBuffer.append("Build Version: " + patch.getBuild().getBuildVersion() + "\n");
            msgBuffer.append("Patch Name: " + patch.getName() + "\n");

            msgBuffer.append("Fixes: \n");
            Vector<CMnPatchFix> fixes = patch.getFixes();
            if ((fixes != null) && (fixes.size() > 0)) {
                Enumeration<CMnPatchFix> fixlist = fixes.elements();
                CMnPatchFix fix = null;
                while (fixlist.hasMoreElements()) {
                    fix = fixlist.nextElement();
                    msgBuffer.append(fix.getBugId());
                    if ((fix.getOrigin() != null) && (fix.getOrigin().getId() != null) && (patch.getId() != null) && (fix.getOrigin().getId().equals(patch.getId()))) {
                        msgBuffer.append(" (new)");
                    }
                    msgBuffer.append("\n");
                }
            }

            // Display the URL for viewing the patch details
            String approveUrl = HttpUtility.getApplicationUrl(req) +  
                "/command/patch/CMnPatchRequest" +
                "?" + IMnPatchForm.PATCH_ID_LABEL + "=" + patch.getId();
            msgBuffer.append("\nUse the following URL to view this service patch request: \n" + approveUrl);

            // Display the URL for viewing the user approval queue
            String queueUrl = HttpUtility.getApplicationUrl(req) +  
                "/command/patch/CMnPatchQueue";
            msgBuffer.append("\nUse the following URL to view all patches that require approval: \n" + queueUrl); 


            // Send the message and close transport session
            app.sendPlainMailMessage(recipients, null, subject, msgBuffer.toString());

        } catch (ApplicationException aex) {
            exApp = aex;
        } catch (Exception ex) {
            exApp = new ApplicationException(
                    ErrorMap.MAIL_SEND_FAILURE,
                    "Failed to send e-mail.");
            exApp.setStackTrace(ex);
        } finally {
            // Throw any exceptions once the database connections have been cleaned up
            if (exApp != null) {
                throw exApp;
            }
        }

    }


    /**
     * Send e-mail notification to the users once the patch
     * request has been submitted.
     */
    protected static void notifyUsers(WebApplication app, HttpServletRequest req, CMnPatch patch) 
        throws ApplicationException
    {
        // Get the login information from the session
        UserData user = SessionUtility.getLogin(req.getSession());

        ApplicationException exApp = null;
        ApplicationError error = null;
        Transport smtp = null;
        try {
            app.debug("CMnEmailNotification.notifyUsers(): obtained a connection to the build database");

            // Send e-mail to the requestor
            InternetAddress[] to = InternetAddress.parse(user.getEmailAddress());
            InternetAddress[] cc = patch.getCCList();
            InternetAddress[] watchers = getEmailAddresses(patch.getStatusNotifications());

            int ccLength = 0;
            int watcherLength = 0;

            if(cc != null){
               ccLength = cc.length;
            }
            if(watchers != null){
                watcherLength = watchers.length;
            }


            // Append the list of CC and watchers to the e-mail CC list
            StringBuffer cBuff = new StringBuffer();
            StringBuffer wBuff = new StringBuffer();
            int idxNew = 0;
            InternetAddress[] ccNew = new InternetAddress[ccLength + watcherLength];
            for (int ccIdx = 0; ccIdx < ccLength; ccIdx++) {
                ccNew[idxNew] = cc[ccIdx];
                if (cc[ccIdx] != null) {
                    cBuff.append(cc[ccIdx].toString() + " ");
                }
                idxNew++;
            }
            for (int wIdx = 0; wIdx < watcherLength; wIdx++) {
                ccNew[idxNew] = watchers[wIdx];
                if (watchers[wIdx] != null) {
                    wBuff.append(watchers[wIdx].toString() + " ");
                }
                idxNew++;
            }
            app.debug("CMnEmailNotification.notifyUsers(): " + ccLength + " cc users = " + cBuff.toString());
            app.debug("CMnEmailNotification.notifyUsers(): " + watcherLength + " watchers = " + wBuff.toString());

            // Format the mime message
            String subject = "Service Patch " + patch.getStatus() +
                ": " + patch.getCustomer().getName() + " " + patch.getBuild().getReleaseId() + " " + patch.getName();

            StringBuffer msgBuffer = new StringBuffer();
            msgBuffer.append("The following service patch is now in " + patch.getStatus() + " status:\n\n");
            msgBuffer.append("Customer: " + patch.getCustomer().getName() + "\n");
            msgBuffer.append("Environment: " + patch.getEnvironment().getName() + "\n");
            msgBuffer.append("Build Version: " + patch.getBuild().getBuildVersion() + "\n");
            msgBuffer.append("Patch Name: " + patch.getName() + "\n");

            // Display the URL for viewing the patch details
            String approveUrl = HttpUtility.getApplicationUrl(req) + 
                "/command/patch/CMnPatchRequest" +
                "?" + IMnPatchForm.PATCH_ID_LABEL + "=" + patch.getId();
            msgBuffer.append("\nUse the following URL to view this service patch request: \n" + approveUrl);

            // Send the message and close transport session
            app.sendPlainMailMessage(to, ccNew, subject, msgBuffer.toString());

        } catch (ApplicationException aex) {
            exApp = aex;
        } catch (Exception ex) {
            exApp = new ApplicationException(
                    ErrorMap.MAIL_SEND_FAILURE,
                    "Failed to send e-mail.");
            exApp.setStackTrace(ex);
        } finally {
            // Throw any exceptions once the database connections have been cleaned up
            if (exApp != null) {
                throw exApp;
            }
        }

    }


    /**
     * Send e-mail notification to the assigned user
     * to notify them of a change in ownership. 
     */
    protected static void notifyOwner(
            WebApplication app, 
            HttpServletRequest req, 
            CMnPatch patch, 
            CMnPatchOwner oldOwner, 
            CMnPatchOwner newOwner)
        throws ApplicationException
    {
        // Get the login information from the session
        UserData user = SessionUtility.getLogin(req.getSession());

        ApplicationException exApp = null;
        ApplicationError error = null;
        Transport smtp = null;
        try {
            app.debug("CMnEmailNotification.notifyOwner(): obtained a connection to the build database");

            // Add the list of owner e-mail addresses as recipients
            Vector<InternetAddress> to = new Vector<InternetAddress>();
            if ((oldOwner != null) && (oldOwner.getUser() != null)) {
                InternetAddress oldEmail = getEmailAddress(oldOwner.getUser());
                if (oldEmail != null) {
                    to.add(oldEmail);
                } else {
                    app.debug("CMnEmailNotification.notifyOwner(): no e-mail address for old user");
                }
            }
            if ((newOwner != null) && (newOwner.getUser() != null)) {
                InternetAddress newEmail = getEmailAddress(newOwner.getUser());
                if (newEmail != null) {
                    to.add(newEmail);
                } else {
                    app.debug("CMnEmailNotification.notifyOwner(): no e-mail address for new user");
                }
            }

            // Don't send any mail if there are no recipients
            if (to.size() > 0) {
                // Convert the list of addresses to an array
                InternetAddress[] recipients = new InternetAddress[to.size()];
                to.toArray(recipients);

                // Format the mime message
                String subject = "Service Patch Assignment: " + patch.getCustomer().getName() + " " + patch.getBuild().getReleaseId() + " " + patch.getName();

                StringBuffer msgBuffer = new StringBuffer();
                if (newOwner != null) {
                    msgBuffer.append("The following service patch has been assigned to " + newOwner.getUser().getFullName() + "\n");
                }
                if (oldOwner != null) {
                    msgBuffer.append("Previous Owner: " + oldOwner.getUser().getFullName() + "\n");
                }
                msgBuffer.append("\n");
                msgBuffer.append("Priority:   " + newOwner.getPriority()  + "\n");
                msgBuffer.append("Deadline:   " + newOwner.getDeadline()  + "\n");
                msgBuffer.append("Start Date: " + newOwner.getStartDate() + "\n");
                msgBuffer.append("End Date:   " + newOwner.getEndDate()   + "\n");
                msgBuffer.append("\n");

                msgBuffer.append("The service patch is now in " + patch.getStatus() + " status:\n\n");
                msgBuffer.append("Customer: " + patch.getCustomer().getName() + " " + patch.getEnvironment().getName() + "\n");
                msgBuffer.append("Build Version: " + patch.getBuild().getBuildVersion() + " (" + patch.getName() + ")\n");

                // Display the URL for viewing the patch assignment details
                String assignUrl = HttpUtility.getApplicationUrl(req) + 
                    "/command/patch/CMnPatchAssignment" +
                    "?" + IMnPatchForm.PATCH_ID_LABEL + "=" + patch.getId();
                msgBuffer.append("\nUse the following URL to view the patch assignment: \n" + assignUrl + "\n");

                // Send the message and close transport session
                app.sendPlainMailMessage(recipients, null, subject, msgBuffer.toString());

            } else {
                app.debug("CMnEmailNotification.notifyOwner(): skipping e-mail notification.");
            }

        } catch (ApplicationException aex) {
            exApp = aex;
        } catch (Exception ex) {
            app.debug("Failed to send e-mail: " + ex.toString());
            exApp = new ApplicationException(
                    ErrorMap.MAIL_SEND_FAILURE,
                    "Failed to send e-mail.");
            exApp.setStackTrace(ex);
        } finally {
            // Throw any exceptions once the database connections have been cleaned up
            if (exApp != null) {
                throw exApp;
            }
        }

    }


    /**
     * Send e-mail notification to the patch requester to inform
     * them that the patch has been reviewed and released.
     *
     * @param  app         Web application reference
     * @param  req         HTTP request information
     * @param  patch       Service patch information
     */
    protected static void notifyOfRelease(
            WebApplication app,
            HttpServletRequest req,
            CMnPatch patch)
        throws ApplicationException
    {
        // Get the login information from the session
        UserData user = SessionUtility.getLogin(req.getSession());

        ApplicationException exApp = null;
        ApplicationError error = null;
        Transport smtp = null;
        try {
            app.debug("CMnEmailNotification.notifyOfRelease(): obtained a connection to the build database");

            // Convert the list of addresses to an array
            InternetAddress[] recipients = new InternetAddress[1];
            recipients[0] = getEmailAddress(patch.getRequestor()); 

            InternetAddress[] cclist = patch.getCCList();

            String custName = patch.getCustomer().getName();
            String shortVer = CMnPatchUtil.getVersionNumber(patch.getBuild().getBuildVersion());

            // Format the mime message
            StringBuffer subject = new StringBuffer();
            subject.append(custName + " " + shortVer + " " + patch.getName() + " complete");

            StringBuffer msgBuffer = new StringBuffer();
            msgBuffer.append("<p>\n");
            msgBuffer.append("The " + custName + " " + shortVer + " " + patch.getName() + " service patch ");
            msgBuffer.append("has been released.  Details can be found using the following links: ");
            msgBuffer.append("</p>\n");

            msgBuffer.append("<ul>\n");


            // Get the URLs for various external systems
            CMnBaseForm form = new CMnBaseForm(null, null);
            form.setExternalUrls(app.getExternalUrls());

            // Link to the patch request
            String hrefPatch = HttpUtility.getApplicationUrl(req) +
                "/command/patch/CMnPatchRequest" +
                "?" + IMnPatchForm.PATCH_ID_LABEL + "=" + patch.getId();
            msgBuffer.append("  <li><a href=\"" + hrefPatch + "\">Patch Details</a></li>\n");

            // Link to the build download
            String hrefDownload = form.getDownloadUrl(patch.getPatchBuild().getDownloadUri());
            msgBuffer.append("  <li><a href=\"" + hrefDownload + "\">Download Location</a></li>\n");

            msgBuffer.append("</ul>\n");

            // Send the message and close transport session
            app.sendHtmlMailMessage(recipients, cclist, subject.toString(), msgBuffer.toString());

        } catch (ApplicationException aex) {
            exApp = aex;
        } catch (Exception ex) {
            app.debug("Failed to send e-mail: " + ex.toString());
            exApp = new ApplicationException(
                    ErrorMap.MAIL_SEND_FAILURE,
                    "Failed to send e-mail.");
            exApp.setStackTrace(ex);
        } finally {
            // Throw any exceptions once the database connections have been cleaned up
            if (exApp != null) {
                throw exApp;
            }
        }


    }



    /**
     * Send e-mail notification to the patch reviewers so that 
     * they can review the service patch results and determine
     * whether it can be released to the customer. 
     *
     * @param  app         Web application reference
     * @param  req         HTTP request information
     * @param  patch       Service patch information
     * @param  reviewers   List of service patch reviewers
     */
    protected static void notifyReviewers(
            WebApplication app, 
            HttpServletRequest req, 
            CMnPatch patch, 
            Vector<InternetAddress> reviewers)
        throws ApplicationException
    {
        // Get the login information from the session
        UserData user = SessionUtility.getLogin(req.getSession());

        ApplicationException exApp = null;
        ApplicationError error = null;
        Transport smtp = null;
        try {
            app.debug("CMnEmailNotification.notifyReviewers(): obtained a connection to the build database");

            // Don't send any mail if there are no recipients
            if ((reviewers != null) && (reviewers.size() > 0)) {
                // Add the patch requester to the recipient list
                if (patch.getRequestor() != null) {  
                    InternetAddress addr = getEmailAddress(patch.getRequestor());
                    if (addr != null) {
                        reviewers.add(addr);
                    }
                }

                // Convert the list of addresses to an array
                InternetAddress[] recipients = new InternetAddress[reviewers.size()];
                reviewers.toArray(recipients);

                InternetAddress[] cclist = patch.getCCList();

                String custName = patch.getCustomer().getName(); 
                String shortVer = CMnPatchUtil.getVersionNumber(patch.getBuild().getBuildVersion());

                // Format the mime message
                StringBuffer subject = new StringBuffer();
                subject.append(custName + " " + shortVer + " " + patch.getName());
                subject.append(" | Review UT/ACT Results");
                subject.append(" | Patch ID #" + patch.getId());
                subject.append(" | Build ID #" + patch.getPatchBuild().getId());

                StringBuffer msgBuffer = new StringBuffer();
                msgBuffer.append("<p>\n");
                msgBuffer.append("Please review the service patch test results for the ");
                msgBuffer.append(custName + " " + shortVer + " " + patch.getName() + ". ");
                msgBuffer.append("Details can be found using the following links: ");
                msgBuffer.append("</p>\n");

                String srcRoot = patch.getPatchBuild().getVersionControlRoot();
                String srcId = patch.getPatchBuild().getVersionControlId();
                CMnCheckIn checkin = null;
                if ((patch.getPatchBuild().getVersionControlType() != null) &&
                    (srcRoot != null) && (srcRoot.trim().length() > 0) &&
                    (srcId != null) && (srcId.trim().length() > 0)) 
                {
                    switch (patch.getPatchBuild().getVersionControlType()) {
                        case GIT:
                            checkin = new CMnGitCheckIn();
                            ((CMnGitCheckIn)checkin).setRepository(srcRoot);
                            checkin.setId(srcId);
                            break;
                        case PERFORCE:
                            checkin = new CMnPerforceCheckIn();
                            checkin.setId(srcId);
                            break;
                    }
                }

                msgBuffer.append("<ul>\n");


                // Get the URLs for various external systems
                CMnBaseForm form = new CMnBaseForm(null, null);
                form.setExternalUrls(app.getExternalUrls());

                // Link to the patch request
                String hrefPatch = HttpUtility.getApplicationUrl(req) +
                    "/command/patch/CMnPatchRequest" +
                    "?" + IMnPatchForm.PATCH_ID_LABEL + "=" + patch.getId();
                msgBuffer.append("  <li><a href=\"" + hrefPatch + "\">Patch Details</a></li>\n");

                // Link to source control
                if (checkin != null) {
                    String hrefSource = form.getPatchChangelistUrl(checkin);
                    msgBuffer.append("  <li><a href=\"" + hrefSource + "\">Source Code</a></li>\n");
                }

                // Link to the build download
                String hrefDownload = form.getDownloadUrl(patch.getPatchBuild().getDownloadUri());
                msgBuffer.append("  <li><a href=\"" + hrefDownload + "\">Download Location</a></li>\n");

                msgBuffer.append("</ul>\n");

                msgBuffer.append("<p>\n");
                msgBuffer.append("Technical owners must sign off on their area of ");
                msgBuffer.append("ownership before the patch can be released to the customer. ");
                msgBuffer.append("</p>\n");

                // Send the message and close transport session
                app.sendHtmlMailMessage(recipients, cclist, subject.toString(), msgBuffer.toString());

            } else {
                app.debug("CMnEmailNotification.notifyReviewers(): skipping e-mail notification.");
            }

        } catch (ApplicationException aex) {
            exApp = aex;
        } catch (Exception ex) {
            app.debug("Failed to send e-mail: " + ex.toString());
            exApp = new ApplicationException(
                    ErrorMap.MAIL_SEND_FAILURE,
                    "Failed to send e-mail.");
            exApp.setStackTrace(ex);
        } finally {
            // Throw any exceptions once the database connections have been cleaned up
            if (exApp != null) {
                throw exApp;
            }
        }


    }



    /**
     * Convert the list of notifications into a list of e-mail addresses.
     * 
     * @param  notifications   Notifications
     * @return List of e-mail addresses
     */
    private static InternetAddress[] getEmailAddresses(Vector<CMnPatchNotification> notifications) {
        if(notifications == null){
            return null;
        }
        Vector<InternetAddress> results = null;
        if ((notifications != null) && (notifications.size() > 0)) {
            results = new Vector<InternetAddress>(notifications.size());
            for (int idx = 0; idx < notifications.size(); idx++) {
                CMnPatchNotification notif = notifications.get(idx);
                if (notif.getUserEmail() != null) {
                    results.add(notif.getUserEmail());
                }
            }
        }

        int resultsLength = 0;
        if(results != null){
            resultsLength = results.size();
        }
        // Convert the vector to an array
        InternetAddress[] list = new InternetAddress[resultsLength];
        for (int idx = 0; idx < resultsLength; idx++) {
            list[idx] = results.get(idx);
        } 
        return list;
    }


    /**
     * Convert the user information to an e-mail address that can be used
     * to send mail.
     *
     * @param   user    User data
     * @return  Internet address
     */
    private static InternetAddress getEmailAddress(UserData user) 
        throws AddressException, UnsupportedEncodingException
    {
        InternetAddress email = null;

        if ((user != null) && (user.getEmailAddress() != null)) {
            InternetAddress[] addr = InternetAddress.parse(user.getEmailAddress());
            if ((user.getFirstName() != null) && (user.getLastName() != null)) {
                String fullname = user.getFirstName() + " " + user.getLastName();
                addr[0].setPersonal(fullname);
            }
            email = addr[0];
        }

        return email;
    }

}
