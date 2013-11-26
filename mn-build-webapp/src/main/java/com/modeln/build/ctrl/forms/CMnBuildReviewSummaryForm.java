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

import com.modeln.testfw.reporting.CMnBuildTable;
import com.modeln.testfw.reporting.CMnDbBuildData;
import com.modeln.testfw.reporting.CMnDbBuildStatusData;
import com.modeln.testfw.reporting.CMnDbFeatureOwnerData;
import com.modeln.testfw.reporting.CMnDbHostData;
import com.modeln.testfw.reporting.CMnDbReleaseSummaryData;
import com.modeln.testfw.reporting.CMnDbTestSuite;
import com.modeln.testfw.reporting.search.CMnSearchCriteria;
import com.modeln.testfw.reporting.search.CMnSearchGroup;

import com.modeln.build.common.data.product.CMnBuildReviewData;

import java.net.URL;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.Vector;
import javax.servlet.http.HttpServletRequest;

import com.modeln.build.common.data.account.UserData;
import com.modeln.build.util.StringUtility;
import com.modeln.build.web.tags.DateTag;
import com.modeln.build.web.tags.SelectTag;
import com.modeln.build.web.tags.TagGroup;
import com.modeln.build.web.tags.TextTag;


/**
 * The build summary form is used to display an overview of test results that
 * have been executed against a set of builds.
 *
 * @author  Shawn Stafford
 */
public class CMnBuildReviewSummaryForm extends CMnBaseForm implements IMnBuildForm {

    /** Default text of the titled border */
    public static final String DEFAULT_TITLE = "Build Review Summary";


    /** Form field that indicates the grouping criteria */
    public static final String FORM_AREA_LABEL = "area";

    /** Form field that indicates review status */
    public static final String REVIEW_STATUS_LABEL = "status";

    /** Form field that indicates the review comment */
    public static final String REVIEW_COMMENT_LABEL = "comment";

    /** Form field that indicates the review user */
    public static final String REVIEW_USER_LABEL = "user_id";





    /** Form parameter value used to indicate the patch is approved by the user */
    public static final String AREA_APPROVED_VALUE = "approved";

    /** Form parameter value used to indicate the patch is rejected by the user */
    public static final String AREA_REJECTED_VALUE = "rejected";


    /** Build ID associated with this review */
    private int buildId = 0;

    /** Currently logged in user */
    private UserData user = null; 


    /** List of product areas */
    private Vector areaList = null;

    /** List of review data objects. */
    private Vector reviewList = null;
    

    /**
     * Construct the form from a list of releases. 
     *
     * @param  form      URL to use when submitting form input
     * @param  images    URL to use when referencing images
     * @param  areas     List of product areas
     * @param  reviews   List of area reviews
     */
    public CMnBuildReviewSummaryForm(URL form, URL images, Vector areas, Vector reviews) {
        super(form, images);
        areaList = areas;
        reviewList = reviews;
    }


    /**
     * Set the build ID associated with this review.
     * 
     * @param  id   Build ID
     */
    public void setBuildId(int id) {
        buildId = id;
    }

    /**
     * Set the currently logged in user.
     * 
     * @param  data   Currently logged in user
     */
    public void setUser(UserData data) {
        user = data;
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
     * Render the build data form.
     */
    public String toString() {
        StringBuffer html = new StringBuffer();

        // Display a summary of the areas
        if ((areaList != null) && (areaList.size() > 1)) {
            StringBuffer summary = new StringBuffer();

            // Assume that all elements of the list have the same area ID
            CMnDbFeatureOwnerData area = (CMnDbFeatureOwnerData) areaList.get(0);

            summary.append("<table border=\"0\" cellspacing=\"2\" cellpadding=\"5\" width=\"100%\">\n");
            summary.append("  <tr>\n");
            summary.append("    <td width=\"5%\"></td>\n");
            summary.append("    <td width=\"75%\">Area</td>\n");
            summary.append("    <td width=\"10%\" align=\"center\">Accepted</td>\n");
            summary.append("    <td width=\"10%\" align=\"center\">Rejected</td>\n");
            summary.append("  </tr>\n");

            Iterator areaIter = areaList.iterator();
            while (areaIter.hasNext()) {
                summary.append("  <tr>\n");
                area = (CMnDbFeatureOwnerData) areaIter.next();

                // Create a link for adding a review
                String href = getFormUrl() +
                    "?" + BUILD_ID_LABEL + "=" + buildId +
                    "&" + FORM_AREA_LABEL + "=" + area.getId();
                summary.append("    <td>");
                summary.append("<a href=\"" + href + "\"><img src=\"" + getImageUrl() + "/icons_small/pencil.png\"></a>");
                summary.append("</td>\n");

                summary.append("    <td valign=\"top\">\n");
                summary.append(area.getDisplayName());
                summary.append("    </td>\n");
                summary.append(areaSummaryToString(area));
                summary.append("  </tr>\n");
            }

            summary.append("</table>\n");
            html.append(getTitledBorder(summary.toString()));
        }


        // Show the review detials
        html.append(detailToString());

        return html.toString();
    }


    /**
     * Render the review information as a compact summary indicating
     * whether each area has been approved, rejected, or is pending.
     */
    public String summaryToString() {
        StringBuffer html = new StringBuffer();

        Iterator areaIter = null;
        CMnDbFeatureOwnerData area = null;

        // Display the detailed comments for each area
        if ((areaList != null) && (areaList.size() > 0)) {
            html.append("<ul>\n");
            areaIter = areaList.iterator();
            while (areaIter.hasNext()) {
                area = (CMnDbFeatureOwnerData) areaIter.next();
                html.append("<li>" + area.getDisplayName() + " - <i>" + getReviewStatus(area) + "</i></li>\n");
            }
            html.append("</ul>\n");
        }

        return html.toString();
    }

    /**
     * Render the summary table as a string.
     *
     * @return  Review summary
     */
    public String detailToString() {
        StringBuffer html = new StringBuffer();

        Iterator areaIter = null;
        CMnDbFeatureOwnerData area = null;

        // Display the detailed comments for each area
        if (areaList != null) {
            areaIter = areaList.iterator();
            while (areaIter.hasNext()) {
                area = (CMnDbFeatureOwnerData) areaIter.next();
                html.append(areaToString(area));
            }
        }
        
        return html.toString();
    }

    /**
     * Return the review status for the specified area. 
     *
     * @param  area    Feature area
     * @return Review status 
     */
    public String getReviewStatus(CMnDbFeatureOwnerData area) {
        int approved = 0;
        int rejected = 0;
        if (reviewList != null) {
            Iterator reviewIter = reviewList.iterator();
            while (reviewIter.hasNext()) {
                CMnBuildReviewData review = (CMnBuildReviewData) reviewIter.next();
                if (area.getId() == review.getAreaId()) {
                    if (CMnBuildReviewData.ReviewStatus.APPROVED == review.getStatus()) {
                        approved++;
                    } else {
                        rejected++;
                    }
                }
            }
        }

        if (rejected > 0) {
            return "rejected";
        } else if (approved > 0) {
            return "approved";
        } else {
            return "pending";
        }
    }


    /**
     * Return a summary of results for the specified area.
     *
     * @param  area    Feature area
     * @return Table row summarizing the results
     */
    public String areaSummaryToString(CMnDbFeatureOwnerData area) {
        StringBuffer html = new StringBuffer();

        int approved = 0;
        int rejected = 0;
        if (reviewList != null) {
            Iterator reviewIter = reviewList.iterator();
            while (reviewIter.hasNext()) {
                CMnBuildReviewData review = (CMnBuildReviewData) reviewIter.next();
                if (area.getId() == review.getAreaId()) {
                    if (CMnBuildReviewData.ReviewStatus.APPROVED == review.getStatus()) {
                        approved++;
                    } else {
                        rejected++;
                    }
                }
            }
        }
        html.append("<td align=\"center\">" + approved + "</td>\n");
        html.append("<td align=\"center\">" + rejected + "</td>\n");

        return html.toString();
    }

    /**
     * Return the review detials for the specified area.
     * 
     * @param  area   Feature area
     * @return Review results for the area
     */
    public String areaToString(CMnDbFeatureOwnerData area) {
        StringBuffer html = new StringBuffer();

        // Allow the user to provide a review of the current area
        StringBuffer form = new StringBuffer();
        if ((user != null) && (areaList != null) && (areaList.size() == 1)) {

            SelectTag statusTag = new SelectTag(REVIEW_STATUS_LABEL);
            statusTag.addOption(CMnBuildReviewData.ReviewStatus.REJECTED.name(), AREA_REJECTED_VALUE);
            statusTag.addOption(CMnBuildReviewData.ReviewStatus.APPROVED.name(), AREA_APPROVED_VALUE);
            statusTag.setSorting(true);
            statusTag.setDefault(CMnBuildReviewData.ReviewStatus.APPROVED.name());

            TextTag messageTag = new TextTag(REVIEW_COMMENT_LABEL);
            messageTag.setHeight(3);
            messageTag.setWidth(30);

            String title = area.getDisplayName() + " Review";
            form.append("<form action=\"" + getFormUrl() + "\" method=\"post\">\n");
            form.append("<input type=\"hidden\" name=\"" + BUILD_ID_LABEL + "\" value=\"" + buildId + "\"/>\n");
            form.append("<input type=\"hidden\" name=\"" + FORM_AREA_LABEL + "\" value=\"" + area.getId() + "\"/>\n");
            form.append("<table border=\"0\" cellspacing=\"2\" cellpadding=\"5\">\n");
            form.append("  <tr>\n");
            form.append("    <td>Status:</td>\n");
            form.append("    <td>" + statusTag.toString() + " <input type=\"submit\" value=\"Submit\"/></td>\n");
            form.append("  </tr>\n");
            form.append("  <tr>\n");
            form.append("    <td valign=\"top\">Comments:</td>\n");
            form.append("    <td valign=\"top\">" + messageTag.toString() + "</td>\n");
            form.append("  </tr>\n");
            form.append("</table>\n");
            form.append("</form>\n");
            html.append(getTitledBorder(title , form.toString()));
        }

        // Display reviews that other users have provided
        if ((reviewList != null) && (reviewList.size() > 0)) {
            html.append("<p>\n");
            html.append("<b><i>" + area.getDisplayName() + "</i></b>\n");
            html.append("<table width=\"100%\" border=\"0\" cellspacing=\"2\" cellpadding=\"5\">\n");

            boolean areaHasReview = false;
            Iterator reviewIter = reviewList.iterator();
            while (reviewIter.hasNext()) {
                CMnBuildReviewData review = (CMnBuildReviewData) reviewIter.next();
                if (area.getId() == review.getAreaId()) {
                    html.append("  <tr>\n");
                    html.append("    <td width=\"10%\" valign=\"top\">");
                    UserData reviewer = review.getUser();
                    if (reviewer != null) {
                        // Allow the user to edit their own 
                        if ((reviewer.getUid() != null) && (user.getUid() != null)) {
                            if (reviewer.getUid().equals(user.getUid())) {
                                String href = getFormUrl() +
                                    "?" + BUILD_ID_LABEL + "=" + buildId +
                                    "&" + FORM_AREA_LABEL + "=" + area.getId() +
                                    "&" + REVIEW_USER_LABEL + "=" + reviewer.getUid();
                                html.append("<a href=\"" + href + "\"><img src=\"" + getImageUrl() + "/icons_small/pencil.png\"></a>");
                            } else {
                                html.append("<!-- UID " + reviewer.getUid() + " ne " + user.getUid() + " -->");
                            }
                        }

                        if (reviewer.getFirstName() != null) {
                            html.append(reviewer.getFirstName() + " ");
                        }
                        if (reviewer.getMiddleName() != null) {
                            html.append(reviewer.getMiddleName() + " ");
                        }
                        if (reviewer.getLastName() != null) {
                            html.append(reviewer.getLastName());
                        }
                    }
                    html.append("</td>\n");
                    html.append("    <td width=\"10%\" valign=\"top\">" + review.getStatus() + "</td>\n");
                    html.append("    <td width=\"80%\" valign=\"top\">" + review.getComment() + "</td>\n");
                    html.append("  </tr>\n");
                    areaHasReview = true;
                }
            }

            // Display a message indicating that the area has no reviews
            if (!areaHasReview) {
                html.append("<tr><td colspan=\"3\">No reviews</td></tr>\n");
            }

            html.append("</table>\n");
            html.append("</p>\n");
        }

        return html.toString();
    }

    
}

