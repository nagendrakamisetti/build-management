/*
* Copyright 2000-2003 by Model N, Inc.  All Rights Reserved.
*
* This software is the confidential and proprietary information
* of Model N, Inc ("Confidential Information").  You shall not
* disclose such Confidential Information and shall use it only
* in accordance with the terms of the license agreement you
* entered into with Model N, Inc.
*/
package com.modeln.build.common.data.product;

import com.modeln.build.common.data.account.CMnAccount;
import com.modeln.build.common.data.account.CMnEnvironment;
import com.modeln.build.common.enums.CMnServicePatch;
import com.modeln.testfw.reporting.CMnDbBuildData;

import com.modeln.build.common.data.account.UserData;

import java.net.URL;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.mail.internet.InternetAddress;


/**
 * Service patch request information such as customer, fixes, and other
 * information required to produce a service patch. 
 *
 * @version      1.0
 * @author       Shawn Stafford (sstafford@modeln.com)
 */
public class CMnPatch {



    /** Auto-generated ID used to identify the service patch */
    private Integer id;

    /** Service patch title (i.e. SP1) */
    private String name;

    /** Whether the patch is available to customer */
    private boolean externalUse = true;

    /** Customer who requested the service patch */
    private CMnAccount customer;

    /** Date when the request was submitted */
    private Date requestDate;

    /** User who requested the service patch */
    private UserData requestor;

    /** User who is currently responsible for working on the patch */
    private UserData owner;

    /** Customer environment where the patch will be deployed */
    private CMnEnvironment env;

    /** Information about the build to be patched */
    private CMnDbBuildData build;

    /** Information about the service patch build */
    private CMnDbBuildData patchBuild; 

    /** List of users that should be notified on patch updates */
    private Vector<CMnPatchNotification> notifications;

    /** List of e-mail addresses that should be notified on patch updates */
    private InternetAddress[] ccList;

    /** Text describing the justification for the patch */
    private String justification;

    /** Comments about the service patch results */
    private String comments;

    /** List of approvals for this patch request */
    private Vector<CMnPatchApproval> approvals;

    /** List of fixes associated with the patch */
    private Vector<CMnPatchFix> fixes;

    /** List of comments associated with the patch */
    private Vector<CMnPatchComment> commentList;

    /** Patch status */
    private CMnServicePatch.RequestStatus status = null;

    /** Previous service patch */
    private CMnPatch previousPatch = null;




    /**
     * Set the ID used to look-up the patch.
     *
     * @param  id   Unique ID of the patch
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Return the ID used to look-up the patch.
     *
     * @hibernate.id generator-class="native"
     *
     * @return ID for the patch
     */
    public Integer getId() {
        return id;
    }

    /**
     * Set the external availability of the patch.
     *
     * @param   external   TRUE if the patch is available for customer use
     */
    public void setForExternalUse(boolean external) {
        externalUse = external;
    }

    /**
     * Return the external availability of the patch.
     *
     * @return TRUE if the patch is available for customer use
     */
    public boolean getForExternalUse() {
        return externalUse;
    }

    /**
     * Set the patch name.
     *
     * @param  text   patch title
     */
    public void setName(String text) {
        name = text;
    }

    /**
     * Return the patch name.
     *
     * @hibernate.property
     *
     * @return patch title
     */
    public String getName() {
        return name;
    }


    /**
     * Set the patch request date.
     *
     * @param  date   patch request date
     */
    public void setRequestDate(Date date) {
        requestDate = date;
    }

    /**
     * Return the patch request date.
     *
     * @return patch request date
     */
    public Date getRequestDate() {
        return requestDate;
    }

    /**
     * Return the amount of time between the request date and now. 
     *
     * @return  Elapsed time
     */
    public long getElapsedTime() {
        Date currentDate = new Date();
        if (requestDate != null) {
            return currentDate.getTime() - requestDate.getTime();
        } else {
            return 0;
        }
    }

    /**
     * Return the amount of time between the request date and now.
     *
     * @return  Elapsed time
     */
    public String getElapsedTimeString() {
        long elapsedMillis = getElapsedTime();
        long elapsedMinutes = elapsedMillis / (1000*60);
        long elapsedHours = elapsedMillis / (1000*60*60);
        long elapsedDays = elapsedMillis / (1000*60*60*24);

        String elapsedStr = elapsedMillis + "ms";
        if (elapsedDays > 1) {
            elapsedStr = elapsedDays + "days"; 
        } else if (elapsedHours > 1) {
            elapsedStr = elapsedHours + "hr";
        } else if (elapsedMinutes > 1) {
            elapsedStr = elapsedMinutes + "min";
        } else {
            elapsedStr = "new"; 
        }

        return elapsedStr;
    }


    /**
     * Set the patch customer.
     *
     * @param  acct   patch customer
     */
    public void setCustomer(CMnAccount acct) {
        customer = acct;
    }

    /**
     * Return the patch customer.
     *
     * @return patch customer
     */
    public CMnAccount getCustomer() {
        return customer;
    }

    /**
     * Set the patch requestor.
     *
     * @param  user   patch requestor
     */
    public void setRequestor(UserData user) {
        requestor = user;
    }

    /**
     * Return the patch requestor.
     *
     * @return patch requestor
     */
    public UserData getRequestor() {
        return requestor;
    }

    /**
     * Set the patch owner.
     * The patch owner is the person responsible for working on
     * the patch.  
     *
     * @param  user   patch owner
     */
    public void setOwner(UserData user) {
        owner = user;
    }

    /**
     * Return the patch owner.
     *
     * @return patch owner
     */
    public UserData getOwner() {
        return owner;
    }


    /**
     * Set the customer environment.
     *
     * @param  text   patch environment
     */
    public void setEnvironment(CMnEnvironment env) {
        this.env = env;
    }

    /**
     * Return the patch environment.
     *
     * @return patch environment
     */
    public CMnEnvironment getEnvironment() {
        return env;
    }



    /**
     * Set the product build.
     *
     * @param  build  product build 
     */
    public void setBuild(CMnDbBuildData build) {
        this.build = build;
    }

    /**
     * Return the product build. 
     *
     * @return product build information 
     */
    public CMnDbBuildData getBuild() {
        return build;
    }


    /**
     * Set the service patch build.
     *
     * @param  build   service patch build 
     */
    public void setPatchBuild(CMnDbBuildData build) {
        patchBuild = build;
    }

    /**
     * Return the service patch build. 
     *
     * @return service patch build information 
     */
    public CMnDbBuildData getPatchBuild() {
        return patchBuild;
    }



    /**
     * Set the list of e-mail notification recipients.
     *
     * @param  list   patch notification recipients
     */
    public void setCCList(InternetAddress[] list) {
        ccList = list;
    }

    /**
     * Return the list of e-mail notification recipients. 
     *
     * @return  notification list 
     */
    public InternetAddress[] getCCList() {
        return ccList;
    }


    /**
     * Set the list of e-mail notification recipients.
     *
     * @param  list   patch notification recipients
     */
    public void setNotifications(Vector<CMnPatchNotification> list) {
        notifications = list;
    }

    /**
     * Return the list of e-mail notification recipients. 
     *
     * @return  notification list 
     */
    public Vector<CMnPatchNotification> getNotifications() {
        return notifications;
    }

    /**
     * Return the list of e-mail addresses for users who should be
     * notified of the current patch status.
     *
     * @return  List of notifications   
     */
    public Vector<CMnPatchNotification> getStatusNotifications() {
        Vector<CMnPatchNotification> results = null;
        if ((notifications != null) && (notifications.size() > 0)) {
            results = new Vector<CMnPatchNotification>();
            Enumeration list = notifications.elements();
            while (list.hasMoreElements()) {
                CMnPatchNotification notif = (CMnPatchNotification) list.nextElement();
                if (notif.hasStatus(status)) {
                    results.add(notif);
                }
            }
        } 
        return results;
    }


    /**
     * Set the patch justification.
     *
     * @param  text   patch justification
     */
    public void setJustification(String text) {
        justification = text;
    }

    /**
     * Return the patch justification.
     *
     * @hibernate.property
     *
     * @return patch justification
     */
    public String getJustification() {
        return justification;
    }


    /**
     * Set the patch comments.
     *
     * @param  text   patch comments
     */
    public void setComments(String text) {
        comments = text;
    }

    /**
     * Return the patch comments.
     *
     * @return patch comments
     */
    public String getComments() {
        return comments;
    }


    /**
     * Set the list of fixes associated with the patch.
     *
     * @param  fixes   List of fixes
     */
    public void setFixes(Vector<CMnPatchFix> fixes) {
        this.fixes = fixes;
    }

    /**
     * Return the list of fixes associated with the patch.
     *
     * @return  List of fixes
     */
    public Vector<CMnPatchFix> getFixes() {
        return fixes;
    }

    /**
     * Add a new fix to the list associated with the patch.
     *
     * @param  fix   New fix
     */
    public void addFix(CMnPatchFix fix) {
        if (fixes == null) {
            fixes = new Vector<CMnPatchFix>();
        }
        fixes.add(fix);
    }

    /**
     * Returns the fix with the matching bug ID, or null if
     * no match is found. 
     *
     * @param   bugId   Bug ID
     * @return  Fix information or null if none found 
     */
    public CMnPatchFix getFix(int bugId) {
        CMnPatchFix fix = null;
        if (fixes != null) {
            Enumeration fixlist = fixes.elements();
            while (fixlist.hasMoreElements()) {
                fix = (CMnPatchFix) fixlist.nextElement();
                if (fix.getBugId() == bugId) {
                    return fix;
                }
            }
        }
        return null;
    }


    /**
     * Returns true if the patch contains a fix with the 
     * same bug ID.
     *
     * @param   bugId   Bug ID
     * @return  TRUE if the list of fixes contains a match
     */
    public boolean hasFix(int bugId) {
        return (getFix(bugId) != null);
    }

    /**
     * Return the total number of fixes with the specified
     * status value. 
     *
     * @return Number of fixes that match the given status 
     */
    public int getFixStatusCount(String status) {
        int count = 0;

        if ((fixes != null) && (status != null)) {
            Enumeration<CMnPatchFix> fixList = fixes.elements();
            while (fixList.hasMoreElements()) {
                CMnPatchFix currentFix = (CMnPatchFix) fixList.nextElement();
                if (currentFix.getStatus() != null) {
                    String currentStatus = currentFix.getStatus();
                    if (status.equalsIgnoreCase(currentStatus)) {
                        count++;
                    }
                }
            }
        }

        return count;
    }


    /**
     * Set the list of approvals associated with the patch.
     *
     * @param  list   List of approvals
     */
    public void setApprovals(Vector<CMnPatchApproval> list) {
        approvals = list;
    }

    /**
     * Return the list of approvals associated with the patch.
     *
     * @return  List of approvals
     */
    public Vector<CMnPatchApproval> getApprovals() {
        return approvals;
    }

    /**
     * Add a new approval to the list associated with the patch.
     *
     * @param  approval   New approval
     */
    public void addApproval(CMnPatchApproval approval) {
        if (approvals == null) {
            approvals = new Vector<CMnPatchApproval>();
        }
        approvals.add(approval);
    }

    /**
     * Set the list of comments associated with the patch.
     *
     * @param  list   List of comments
     */
    public void setCommentList(Vector<CMnPatchComment> list) {
        commentList = list;
    }

    /**
     * Return the list of comments associated with the patch.
     *
     * @return  List of comments
     */
    public Vector<CMnPatchComment> getCommentList() {
        return commentList;
    }

    /**
     * Add a new comment to the list associated with the patch.
     *
     * @param  comment   New comment
     */
    public void addComment(CMnPatchComment comment) {
        if (commentList == null) {
            commentList = new Vector<CMnPatchComment>();
        }
        commentList.add(comment);
    }



    /**
     * Set the patch status. 
     *
     * @param  status   Patch status 
     */
    public void setStatus(CMnServicePatch.RequestStatus status) {
        this.status = status;
    }


    /**
     * Return the patch status 
     *
     * @return Patch status 
     */
    public CMnServicePatch.RequestStatus getStatus() {
        return status;
    }


    /**
     * Define a relationship with a previous service patch. 
     * This allows BRE compare the patch request or associated
     * build report with with a previous patch request.
     *
     * @param  patch  Previous service patch
     */
    public void setPreviousPatch(CMnPatch patch) {
        previousPatch = patch;
    }

    /**
     * Return the previous service patch that relates to this one. 
     *
     * @return Previous service patch
     */
    public CMnPatch getPreviousPatch() {
        return previousPatch;
    }

}

