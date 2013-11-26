package com.modeln.build.ctrl.forms;

import com.modeln.build.common.data.account.CMnAccount;
import com.modeln.build.common.data.account.CMnEnvironment;
import com.modeln.build.common.data.product.CMnBuildReviewData;
import com.modeln.build.common.data.product.CMnPatch;
import com.modeln.build.common.enums.CMnServicePatch;
import com.modeln.build.sourcecontrol.CMnCheckIn;
import com.modeln.build.sourcecontrol.CMnGitCheckIn;
import com.modeln.build.sourcecontrol.CMnPerforceCheckIn;
import com.modeln.testfw.reporting.CMnDbBuildData;
import com.modeln.testfw.reporting.CMnDbFeatureOwnerData;
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
import com.modeln.build.web.util.HttpUtility;


/**
 * The build form provides an HTML interface to the service patch review.
 * The class manages transfering the data through the HTTP request.
 * Form data can be rendered in either a read-only or input mode.
 *
 * @author  Shawn Stafford
 */
public class CMnPatchReviewForm extends CMnBaseReleaseForm implements IMnPatchForm {

    /** Keep a reference to the service patch data object */
    private CMnPatch patch = null;

    /** List of product areas */
    private Vector<CMnDbFeatureOwnerData> productAreas = null;

    /** List of build reviews */
    private Vector<CMnBuildReviewData> buildReviews = null;


    /**
     * Construct a form for requesting a new service patch. 
     *
     * @param    form     URL for submitting form data
     * @param    images   URL for displaying HTML images 
     */
    public CMnPatchReviewForm(URL form, URL images) {
        super(form, images);
    }

    /**
     * Set the list of product areas associated with the patch build. 
     * 
     * @param  areas  List of product areas 
     */
    public void setProductAreas(Vector<CMnDbFeatureOwnerData> areas) {
        productAreas = areas;
    }

    /**
     * Set the list of product area reviews associated with the patch build.
     *
     * @param  reivews   List of build reviews
     */
    public void setBuildReviews(Vector<CMnBuildReviewData> reviews) {
        buildReviews = reviews;
    }


    /**
     * Set the form information using patch data.
     *
     * @param   patch   Patch information
     */
    public void setValues(CMnPatch patch) {
        // Keep a reference to this patch data for later use
        this.patch = patch;
    }


    /**
     * Render the patch request form as HTML. 
     */
    public String toString() {
        StringBuffer html = new StringBuffer();

        if (patch != null) {
            String hrefReview = getFormUrl() +
                "?" + IMnPatchForm.PATCH_ID_LABEL + "=" + patch.getId();

            html.append("<ul>\n");

            // Provide user feedback on missing data
            if (patch.getPreviousPatch() == null) {
                html.append("<li><i>No previous patch information.</i></li>\n");
            } else if (patch.getPreviousPatch().getPatchBuild() == null) {
                html.append("<li><i>No previous build information.</i></li>\n");
            }
            if (productAreas == null) {
                html.append("<li><i>No product areas for the patch build.</i></li>\n");
            }

            // Display build diff link
            if ((patch.getPatchBuild() != null) && 
                (patch.getPreviousPatch() != null) && 
                (patch.getPreviousPatch().getPatchBuild() != null)) 
            {
               int oldId = patch.getPreviousPatch().getPatchBuild().getId();
               int newId = patch.getPatchBuild().getId();
               String buildId[] = { Integer.toString(oldId), Integer.toString(newId) };

               // Use a method in the base class to reference an external URL
               String hrefDiff = getPatchBuildDiffUrl(buildId); 

               if (hrefDiff != null) {
                   html.append("<li><a href=\"" + hrefDiff + "\">Compare with previous build</a></li>\n");
               }
            }


            // Display admin functions
            if (getAdminMode()) {
                if (patch.getPatchBuild() == null) {
                    html.append("<li><i>No build information.</i></li>\n");
                } else if (patch.getStatus() == CMnServicePatch.RequestStatus.BUILT) {
                    // Display link for requesting developer review
                    html.append("<li><a href=\"" + hrefReview + "\">Request developer review</a></li>\n");
                } else if (patch.getStatus() == CMnServicePatch.RequestStatus.COMPLETE) {
                    // Display a link for releasing the build
                    html.append("<li><a href=\"" + hrefReview + "\">Notify requester of release</a></li>\n");
                } else if (patch.getStatus() == CMnServicePatch.RequestStatus.RELEASE) {
                    html.append("<li>Build has been released</li>\n");
                } else {
                    html.append("<li><i>Build cannot be reviewed in " + patch.getStatus() + " status</i></li>\n");
                }

            }


            // Display review information
            if (patch.getPatchBuild() != null) {
                String reviewHref = getPatchBuildReviewUrl(Integer.toString(patch.getPatchBuild().getId()));
                html.append("<li><a href=\"" + reviewHref + "\"/>Review Patch Build</a></li>\n");

                // Display a summary of the product areas that have reviewed the build
                if (productAreas != null) {
                    CMnBuildReviewSummaryForm reviewForm = new CMnBuildReviewSummaryForm(null, null, productAreas, buildReviews);
                    if (patch.getPatchBuild() != null) {
                        reviewForm.setBuildId(patch.getPatchBuild().getId());
                    }
                    html.append(reviewForm.summaryToString());
                }
            }

            html.append("</ul>\n");
        } else {
            html.append("No patch information.");
        }

        return html.toString();
    }

}

