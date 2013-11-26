/*
 * Login.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.ctrl.command.report; 

import com.modeln.build.common.data.product.CMnBuildReviewData;
import com.modeln.build.ctrl.CMnErrorMap;
import com.modeln.build.ctrl.database.CMnReviewTable;
import com.modeln.build.ctrl.forms.CMnBaseForm;
import com.modeln.build.ctrl.forms.IMnBuildForm;
import com.modeln.build.ctrl.forms.CMnBuildDataForm;
import com.modeln.build.ctrl.forms.CMnBuildReviewSummaryForm;
import com.modeln.build.ctrl.forms.CMnBuildStatusForm;
import com.modeln.build.ctrl.forms.CMnBuildStatusNoteForm;
import com.modeln.build.ctrl.forms.IMnBuildForm;
import com.modeln.testfw.reporting.CMnDbBuildData;
import com.modeln.testfw.reporting.CMnDbBuildStatusData;
import com.modeln.testfw.reporting.CMnDbBuildStatusNote;
import com.modeln.testfw.reporting.CMnDbFeatureOwnerData;
import com.modeln.testfw.reporting.CMnFeatureOwnerTable;
import com.modeln.testfw.reporting.CMnBuildTable;
import com.modeln.testfw.reporting.CMnReleaseTable;

import java.util.Vector;
import java.io.*;
import java.net.URL;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.Connection;
import java.sql.SQLException;

import com.modeln.build.common.data.account.UserData;
import com.modeln.build.common.database.LoginTable;
import com.modeln.build.web.errors.ApplicationError;
import com.modeln.build.web.errors.ApplicationException;
import com.modeln.build.web.application.CommandResult;
import com.modeln.build.web.application.ProtectedCommand;
import com.modeln.build.web.application.WebApplication;
import com.modeln.build.web.database.RepositoryConnection;
import com.modeln.build.web.errors.ErrorMap;
import com.modeln.build.web.util.SessionUtility;

/**
 * This command is used by developers to sign-off on the build results for 
 * a product area.
 * 
 * @version            $Revision: 1.1.1.1 $  
 * @author             Shawn Stafford
 */
public class CMnBuildReview extends ProtectedCommand {

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
                String buildId = (String) req.getParameter(CMnBuildDataForm.BUILD_ID_LABEL);
                // Fall back to the request attributes in case the ID was set by another command
                if (buildId == null) {
                    buildId = (String) req.getAttribute(CMnBuildDataForm.BUILD_ID_LABEL);
                }

                String areaId = (String) req.getParameter(CMnBuildReviewSummaryForm.FORM_AREA_LABEL);
                // Fall back to the request attributes in case the ID was set by another command
                if (areaId == null) {
                    areaId = (String) req.getAttribute(CMnBuildReviewSummaryForm.FORM_AREA_LABEL);
                }

                String status = (String) req.getParameter(CMnBuildReviewSummaryForm.REVIEW_STATUS_LABEL);
                // Fall back to the request attributes in case the status was set by another command
                if (status == null) {
                    status = (String) req.getAttribute(CMnBuildReviewSummaryForm.REVIEW_STATUS_LABEL);
                }

                String comment = (String) req.getParameter(CMnBuildReviewSummaryForm.REVIEW_COMMENT_LABEL);
                // Fall back to the request attributes in case the comment was set by another command
                if (comment == null) {
                    comment = (String) req.getAttribute(CMnBuildReviewSummaryForm.REVIEW_COMMENT_LABEL);
                }


                // Send the user to an error page if the build ID is invalid
                if (buildId == null) {
                    error = app.getError(CMnErrorMap.INVALID_BUILD_ID); 
                    result.setError(error);
                } else if (areaId == null) {
                    rc = app.getRepositoryConnection();

                    // Obtain information about the selected build
                    CMnDbBuildData build = CMnBuildTable.getBuild(rc.getConnection(), buildId);
                    req.setAttribute(IMnBuildForm.BUILD_OBJECT_LABEL, build);

                    // Obtain a list of product areas
                    Vector areas = CMnFeatureOwnerTable.getAllAreas(rc.getConnection());
                    if ((areas != null) && (areas.size() > 0)) {
                        req.setAttribute(IMnBuildForm.PRODUCT_AREA_DATA, areas);
                    }

                    // Obtain a list of area reviews
                    Vector reviews = CMnReviewTable.getReviews(rc.getConnection(), buildId);
                    if ((reviews != null) && (reviews.size() > 0)) {
                        setUserData(app, reviews);
                        req.setAttribute(IMnBuildForm.AREA_REVIEW_DATA, reviews);
                    }

                    req.setAttribute(CMnBuildDataForm.BUILD_OBJECT_LABEL, build);
                    result.setDestination("reports/build_review.jsp");
                } else {
                    rc = app.getRepositoryConnection();

                    // Obtain information about the selected build
                    CMnDbBuildData build = CMnBuildTable.getBuild(rc.getConnection(), buildId);
                    req.setAttribute(IMnBuildForm.BUILD_OBJECT_LABEL, build);

                    // Obtain information about the selected area
                    CMnDbFeatureOwnerData area = CMnFeatureOwnerTable.getArea(rc.getConnection(), areaId);
                    if (area != null) {
                        Vector areas = new Vector<CMnDbFeatureOwnerData>(1);
                        areas.add(area);
                        req.setAttribute(IMnBuildForm.PRODUCT_AREA_DATA, areas);
                    }

                    // Obtain the reviews for the specified area
                    Vector reviews = CMnReviewTable.getReviews(rc.getConnection(), buildId, areaId);
                    if ((reviews != null) && (reviews.size() > 0)) {
                        setUserData(app, reviews);
                    }
                    app.debug("Found " + reviews.size() + " review records for area " + areaId);

                    // Add the new review to the list if the user is submitting one
                    if (status != null) {
                        CMnBuildReviewData review = new CMnBuildReviewData();
                        review.setBuildId(Integer.parseInt(buildId));
                        review.setAreaId(Integer.parseInt(areaId));
                        review.setUser(SessionUtility.getLogin(req.getSession()));
                        review.setStatus(status);
                        review.setComment(comment);

                        // Determine if the user is adding or updating a review
                        boolean success = false;
                        CMnBuildReviewData oldReview = removeReview(reviews, review);
                        if (oldReview != null) {
                            // Update the review
                            app.debug("Updating the existing review to the database.");
                            success = CMnReviewTable.updateReview(rc.getConnection(), review);
                        } else {
                            // Add the new review to the list
                            app.debug("Adding review to the database.");
                            success = CMnReviewTable.addReview(rc.getConnection(), review);
                            if (reviews == null) {
                                reviews = new Vector<CMnBuildReviewData>(1);
                            }
                        }
                        reviews.add(review);
                    } else {
                        app.debug("Review status is null.");
                    }

                    // Store the list of reviews in the session
                    if ((reviews != null) && (reviews.size() > 0)) {
                        req.setAttribute(IMnBuildForm.AREA_REVIEW_DATA, reviews);
                    }


                    req.setAttribute(CMnBuildDataForm.BUILD_OBJECT_LABEL, build);
                    result.setDestination("reports/build_review.jsp");
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


    /**
     * Look up the full user data for each review.
     * 
     * @param   app       Application which called the command
     * @param   reviews   List of reviews
     */
    private void setUserData(WebApplication app, Vector<CMnBuildReviewData> reviews) 
        throws ApplicationException
    {
        RepositoryConnection ac = app.getAccountConnection();
        for (int idx = 0; idx < reviews.size(); idx++) {
            CMnBuildReviewData review = (CMnBuildReviewData) reviews.get(idx);
            UserData user = review.getUser();
            LoginTable lt = LoginTable.getInstance();
            try {
                user = lt.getUserByUid(ac.getConnection(), user.getUid());
                review.setUser(user);
            } catch (SQLException sqlex) {
            }
        }
    }

    /**
     * Use the review build, area, and user information to identify 
     * a review already submitted by the user.  If the review exists,
     * return the review information and remove it from the list.
     * If the review does not exist, return null.
     *
     * @param  reviews   List of reviews
     * @param  review    Review to locate in the list
     */
    private CMnBuildReviewData removeReview(Vector<CMnBuildReviewData> reviews, CMnBuildReviewData review) {
        CMnBuildReviewData data = null;
        if ((reviews != null) && (review != null)) {
            CMnBuildReviewData current = null;
            for (int idx = 0; idx < reviews.size(); idx++) {
                current = reviews.get(idx);
                // Determine if the build IDs match
                boolean buildMatch = (current.getBuildId() == review.getBuildId());

                // Determine if the area IDs match
                boolean areaMatch = (current.getAreaId() == review.getAreaId());

                // Determine if the user IDs match 
                boolean userMatch = false;
                UserData currentUser = current.getUser();
                UserData reviewUser = review.getUser();
                if ((currentUser != null) && 
                    (currentUser.getUid() != null) && 
                    (reviewUser != null) && 
                    (reviewUser.getUid() != null)) 
                {
                    userMatch = currentUser.getUid().equals(reviewUser.getUid());
                }

                // Remove the review if it matches all criteria
                if (buildMatch && areaMatch && userMatch) {
                    data = reviews.remove(idx);
                }
            }
        }

        return data;
    }

}
