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

import com.modeln.build.common.data.product.CMnPatchApproval;
import com.modeln.build.jenkins.Build;
import com.modeln.build.jenkins.Job;
import com.modeln.testfw.reporting.CMnDbBuildData;
import com.modeln.testfw.reporting.CMnDbTestSummaryData;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import javax.servlet.http.HttpServletRequest;

import com.modeln.build.web.tags.TextTag;
import com.modeln.build.web.tags.DateTag;
import com.modeln.build.web.tags.OptionTag;
import com.modeln.build.web.tags.SelectTag;
import com.modeln.build.web.tags.TagGroup;


/**
 * The build form provides an HTML interface to the service patch job. 
 * The class manages transfering the data through the HTTP request.
 * Form data can be rendered in either a read-only or input mode.
 *
 * @author  Shawn Stafford
 */
public class CMnPatchJobForm extends CMnBaseForm {

    /** Display format for the build date */
    public static final SimpleDateFormat fullDateFormat = new SimpleDateFormat("EEE, MMM dd, yyyy 'at' HH:mm:ss");


    /** List of Jenkins jobs and the associated build information */
    Hashtable<Job, Vector<CMnDbBuildData>> jobs = null;
 


    /**
     * Construct the job form using the job information.
     *
     * @param  form   URL to use when submitting form input
     * @param  images URL to use when constructing image links
     */
    public CMnPatchJobForm(URL form, URL images) {
        super(form, images);
    }

    /**
     * Set the job information.
     *
     * @param  jobs   Job information
     */
    public void setJobs(Hashtable<Job, Vector<CMnDbBuildData>> jobs) {
        this.jobs = jobs;
    }


    /**
     * Iterates through the list of builds to map it to the Jenkins
     * build.  If no match is found, the method returns null.
     *
     * @param   build   Jenkins build
     * @param   builds  List of build data objects
     * @return  Build data
     */
    private CMnDbBuildData getBuildData(Build build, Vector<CMnDbBuildData> builds) {
        boolean matchFound = false;

        CMnDbBuildData data = null;
        if ((builds != null) && (build != null)) {
            Enumeration buildList = builds.elements();
            while (!matchFound && buildList.hasMoreElements()) {
                data = (CMnDbBuildData) buildList.nextElement();
                if ((data.getJobUrl() != null) && (build.getURL() != null)) {
                    matchFound = data.getJobUrl().equalsIgnoreCase(build.getURL().toString());
                }
            }
        }
        return data;
    }

    /**
     * Display the table header in HTML format.
     *
     * @param  job   Jenkins job information
     */
    private String getHeader(Job job) {
        StringBuffer html = new StringBuffer();

        Vector<CMnDbBuildData> builds = jobs.get(job);

        // Render the table header
        html.append("<tr>\n");
        html.append("  <td colspan=2 bgcolor=\"#CCCCCC\" colspan=\"7\" align=\"left\">\n");
        html.append("    <b><a href=\"" + job.getURL() + "\">" + job.getName() + "</a></b>\n");
        html.append("  </td>\n");
        if ((builds != null) && (builds.size() > 0)) {
            html.append("  <td bgcolor=\"#CCCCCC\" align=\"right\" width=\"5%\">JUnit</td>\n");
            html.append("  <td bgcolor=\"#CCCCCC\" align=\"right\" width=\"5%\">UIT</td>\n");
            html.append("  <td bgcolor=\"#CCCCCC\" align=\"right\" width=\"5%\">Flex</td>\n");
            html.append("  <td bgcolor=\"#CCCCCC\" align=\"right\" width=\"5%\">ACT</td>\n");
        }
        html.append("</tr>\n");

        return html.toString();
    }


    /**
     * Display the job information in HTML format.
     */
    public String toString() {
        StringBuffer html = new StringBuffer();

        if (jobs != null) {
            html.append("<!-- Found " + jobs.size() + " jobs in the session. -->\n");
            Enumeration jobList = jobs.keys();
            while (jobList.hasMoreElements()) {
                Job job = (Job) jobList.nextElement();
                Vector<CMnDbBuildData> builds = jobs.get(job);
 
                html.append("<table width=\"100%\">\n");

                // Render the table header
                html.append(getHeader(job));

                // Display the builds
                if (builds != null) {
                    Enumeration buildList = builds.elements();
                    while (buildList.hasMoreElements()) {
                        Build build = (Build) buildList.nextElement();
                        if (build != null) {
                            html.append("<tr>\n");
                            html.append(toString(build));
                            html.append(toString(getBuildData(build, builds)));
                            html.append("</tr>\n");
                        }
                    }
                }
                html.append("</table>\n");
            }

        } else {
            html.append("No job data available.");
        }


        return html.toString(); 
    }

    /**
     * Display the build information in HTML format.
     *
     * @param  build  Build data
     * @return HTML
     */
    public String toString(Build build) {
        StringBuffer html = new StringBuffer();

        if (build != null) {
            html.append("<td><a href=\"" + build.getURL() + "\">" + build.getDisplayName() + "</a></td>\n");
            html.append("<td>");
            if (build.getDate() != null) {
                String formattedDate = fullDateFormat.format(build.getDate()); 
                if (formattedDate != null) {
                    html.append(formattedDate);
                }
            }
            html.append("</td>\n");
        } else {
            html.append("<td><!-- name --></td>\n");
            html.append("<td><!-- date --></td>\n");
        }

        return html.toString();
    }

    /**
     * Display the build data in HTML format.
     *
     * @param  build   Build data
     * @return HTML
     */
    private String toString(CMnDbBuildData build) {
        StringBuffer html = new StringBuffer();

        if (build != null) {
            html.append(toString(build.getTestSummary("JUNIT")));
            html.append(toString(build.getTestSummary("UIT")));
            html.append(toString(build.getTestSummary("FLEX")));
            html.append(toString(build.getTestSummary("ACT")));
        } else {
            html.append("<td><!-- UnitTests  --></td>\n");
            html.append("<td><!-- UIT Tests  --></td>\n");
            html.append("<td><!-- Flex Tests --></td>\n");
            html.append("<td><!-- ACT Tests  --></td>\n");
        }

        return html.toString();
    }

    /**
     * Display a summary of the test results in HTML format.
     *
     * @param  data   Test summary data
     * @return HTML
     */
    private String toString(CMnDbTestSummaryData data) {
        StringBuffer html = new StringBuffer();

        html.append("<td align=\"right\">");
        if (data != null) { 
            int pass = data.getPassingCount();
            int total = data.getTotalCount();
            int pct = 0;
            if (total > 0) {
                float ratio = ((float) pass / (float) total) * 100;
                pct = (int) ratio;
            }
            html.append("<!-- " + pass + " / " + total + " -->");
            html.append(Integer.toString(pct));
            html.append("%");
        } else {
            html.append("&nbsp;");
        }
        html.append("</td>");

        return html.toString();
    }

}

