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

import com.modeln.build.sourcecontrol.CMnCheckIn;
import com.modeln.build.sourcecontrol.CMnGitCheckIn;
import com.modeln.build.sourcecontrol.CMnPerforceCheckIn;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Hashtable;
import javax.servlet.http.HttpServletRequest;
import com.modeln.build.web.tags.TextTag;
import com.modeln.build.web.tags.SelectTag;


/**
 * This provides base functionality for all HTML forms.
 * 
 * @author  Shawn Stafford
 */
public class CMnBaseForm {

    public enum SourceControl {
        PERFORCE,
        GIT
    }


    /** Placeholder for a build ID number in the build URL */
    public static final String BUILD_ID_TOKEN = "@@BUILD@@";

    /** Placeholder for a bug number in the bug URL */
    public static final String BUG_TOKEN = "@@BUG@@";

    /** Placeholder for a changelist number in the changelist URL */
    public static final String CHANGELIST_TOKEN = "@@CL@@";

    /** Placeholder for a git repository in the web URL */
    public static final String GIT_PROJECT_TOKEN = "@@PROJECT@@";

    /** Placeholder for a git SHA HASH in the URL */
    public static final String GIT_SHA_TOKEN = "@@HASH@@";



    /** Color used when highlighting errors */
    public static final String ERROR_BGLIGHT = "#FFCCCC";

    /** Color used when highlighting emphasized errors */
    public static final String ERROR_BGDARK = "#FF3333";

    /** Color used when highlighting warnings */
    public static final String WARNING_BGLIGHT = "#FFFFCC";

    /** Color used when highlighting emphasized warnings */
    public static final String WARNING_BGDARK = "#FFFF33";

    /** Color used when highlighting errors */
    public static final String ERRORLONG_BGLIGHT = "#CC99FF";

    /** Color used when highlighting emphasized errors */
    public static final String ERRORLONG_BGDARK = "#CC66FF";

    /** Color used when highlighting skipped tests */
    public static final String ERRORSKIP_BGLIGHT = "#CC9999";

    /** Color used when highlighting emphasized skipped tests */
    public static final String ERRORSKIP_BGDARK = "#CC6666";


    /** Default background color */
    public static final String DEFAULT_BGLIGHT = "#FFFFFF";

    /** Default header color */
    public static final String DEFAULT_BGDARK = "#CCCCCC";

    /** Border color */
    public static final String DEFAULT_BGBORDER = "#000000";


    /** Default text displayed on the submit button when input is enabled */
    public static final String DEFAULT_BUTTON_TEXT = "Update";



    /** List of input fields which contain errors */
    public static final String INPUT_ERROR_DATA = "errors";



    /** Form field that indicates the status of the page */
    public static final String FORM_STATUS_LABEL = "mode";

    /** Form status value indicating that the form data should be updated */
    public static final String UPDATE_DATA = "update";

    /** Form status value indicating that the form data should be deleted */
    public static final String DELETE_DATA = "delete";

    /** Form status value indicating that the form data should be in view-only mode */
    public static final String VIEW_DATA = "view";


    
    /** Short form for displaying dates */
    public static final SimpleDateFormat shortDateFormat = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm");

    /** Long form for displaying dates */
    public static final SimpleDateFormat fullDateFormat = new SimpleDateFormat("EEE, MMM dd, yyyy 'at' HH:mm:ss");

    /** Date format used when constructing SQL queries */
    protected static final SimpleDateFormat sqlDateFormat = new SimpleDateFormat("yyyy-MM-dd");



    /** Determines whether the form uses the get or post method to submit data */
    protected boolean postMethodEnabled = true;

    /** Determines whether the form will be rendered with input fields. */
    protected boolean inputEnabled = false;

    /** Determines whether the form will be rendered with admin functionality. */
    protected boolean adminEnabled = false;


    /** List of external URLs */
    protected Hashtable<String,String> externalUrls = null;



    /** The URL used when submitting form input. */
    protected URL formUrl = null;

    /** The URL used when accessing images */
    protected URL imageUrl = null;

    /** The URL used when performing search operations. */
    protected URL searchUrl = null;

    /** The URL used to delete a unittest suite */
    protected URL deleteUrl = null;

    /** The URL used for admin actions */
    protected URL adminUrl = null;



    /** 
     * Mapping of input field names to error messages.  If an input
     * field has a validation error, the field name and error message
     * should be placed in this data structure so the error can be
     * displayed to the user and the problem can be corrected.
     */
    protected Hashtable<String,String> formErrors = null;



    /** Text displayed on the submit button */
    private String buttonText = DEFAULT_BUTTON_TEXT;
    

    /**
     * Construct an HTML form.
     * 
     * @param  form   URL to use when submitting form input
     * @param  images URL to use when constructing image links
     */
    public CMnBaseForm(URL form, URL images) {
        formUrl = form;
        imageUrl = images;
    }

    /**
     * Set the input fields by examining the HTTP request to see if
     * a value was submitted.
     *
     * @param   req     HTTP request
     */
    public void setValues(HttpServletRequest req) {
        formErrors = (Hashtable) req.getAttribute(INPUT_ERROR_DATA);
    }

    /**
     * Set the list of form errors associated with the input names.
     *
     * @param   errors   List of input elements and the associated errors
     */
    public void setFormErrors(Hashtable<String,String> errors) {
        formErrors = errors;
    }

    /**
     * Return true if there are errors associated with the form input.
     *
     * @return TRUE if errors are found
     */
    public boolean hasFormErrors() {
        return ((formErrors != null) && (formErrors.size() > 0));
    }

    /**
     * Return the list of errors associated with the form input.
     *
     * @return  form errors
     */
    public Hashtable<String,String> getFormErrors() {
        return formErrors;
    }

    /**
     * Return the form error associated with the form element.
     * If the form input is enbled and the specified element has an
     * error message, then the error message will be returned.  
     * If a null is returned, it means that there was no error 
     * for that input element or input is disabled.
     *
     * @param   name   Form element name
     * @return  Error message if the form element contains an error
     */
    public String getFormError(String name) {
        String msg = null;
        if ((formErrors != null) && (name != null)) {
            msg = (String) formErrors.get(name);
        }
        return msg;
    }


    /**
     * Set the text on the submit button to something other than the default value.
     *
     * @param text  Button text
     */
    public void setButtonText(String text) {
        buttonText = text;
    }


    /**
     * Sets the URL to be used to delete individual test suites.
     *
     * @param  url   Link to the suite delete command
     */
    public void setDeleteUrl(URL url) {
        deleteUrl = url;
    }

    /**
     * Sets the URL to be used to perform admin operations. 
     *
     * @param  url   Link to the admin command
     */
    public void setAdminUrl(URL url) {
        adminUrl = url;
    }


    /**
     * Return the URL for admin operations. 
     *
     * @return  Admin URL
     */
    public String getAdminUrl() {
        if (adminUrl != null) {
            return adminUrl.toString();
        } else {
            return null;
        }
    }


    /** 
     * Return the URL for form submissions.
     *
     * @return  Form submission URL
     */
    public String getFormUrl() {
        if (formUrl != null) {
            return formUrl.toString();
        } else {
            return null;
        }
    }

    /** 
     * Return the base URL for retrieving images.
     *
     * @return  Base image URL
     */
    public String getImageUrl() {
        if (imageUrl != null) {
            return imageUrl.toString();
        } else {
            return null;
        }
    }

    /**
     * Enables or disables administrative functionality. 
     *
     * @param enabled  TRUE to enable administrative functionality
     */
    public void setAdminMode(boolean enabled) {
        adminEnabled = enabled;
    }

    /**
     * Determines if the administrative functionality is enabled.
     * 
     * @return TRUE if administrative functionality is enabled.
     */
    public boolean getAdminMode() {
        return adminEnabled;
    }

    /**
     * Enables or disables form input. 
     *
     * @param enabled  TRUE to enable form input
     */
    public void setInputMode(boolean enabled) {
        inputEnabled = enabled;
    }


    /**
     * Determines if form input is allowed.
     * 
     * @return TRUE if form input is enabled.
     */
    public boolean getInputMode() {
        return inputEnabled;
    }

    /**
     * Enables or disables the POST method for submitting form data. 
     *
     * @param enabled  TRUE to use the POST method, FALSE to use the GET method 
     */
    public void setPostEnabled(boolean enabled) {
        postMethodEnabled = enabled;
    }


    /**
     * Determines if form data is submitted using the POST method. 
     *
     * @return TRUE if form data is submitted using the POST method 
     */
    public boolean getPostEnabled() {
        return postMethodEnabled;
    }


    /**
     * Sets the URL to be used to perform search operations. 
     *
     * @param  url   Link to the search command
     */
    public void setSearchUrl(URL url) {
        searchUrl = url;
    }

    /**
     * Return the URL for search queries. 
     *
     * @return  Search submission URL
     */
    public String getSearchUrl() {
        if (searchUrl != null) {
            return searchUrl.toString();
        } else {
            return null;
        }
    }


    /**
     * Set the list of external URLs used for linking patch data. 
     *
     * @param  urls   List of external URLs 
     */
    public void setExternalUrls(Hashtable<String,String> urls) {
        externalUrls = urls;
    }

    /**
     * Return the external URL corresponding to the specified 
     * URL property name.  These properties can be found in the
     * application config file in the following format:
     * <pre>
     * urls=name1,name2
     * url.name1=http://hostname1/@@PARAM@@
     * url.name2=http://hostname2/@@PARAM@@
     * </pre>
     *
     * @param  name   URL property name
     * @return URL property value
     */
    public String getExternalUrl(String name) {
        String value = null;
        if (externalUrls != null) {
            value = externalUrls.get(name);
        }
        return value;
    }


    /**
     * Return the URL for downloading builds.
     *
     * @param   path    Path to the build
     * @return  Download URL
     */
    public String getDownloadUrl(String path) {
        String url = getExternalUrl("download");

        // Append the path to the URL if possible, else return null
        if (url != null) {
            url = url + path; 
        }

        return url;
    }


    /**
     * Return the URL for viewing bug information. 
     *
     * @param   bugId   Bug number to use in URL
     * @return  Bug URL 
     */
    public String getBugUrl(String bugId) {
        String url = getExternalUrl("sdtracker");

        // Replace the URL tokens
        if (url != null) {
            url = url.replaceAll(BUG_TOKEN, bugId);
        }

        return url;
    }

    /**
     * Return the URL for viewing the patch build information.
     *
     * @param  buildId   Build ID to use in the URL
     * @return Service patch build URL
     */
    public String getPatchBuildUrl(String buildId) {
        String url = getExternalUrl("patchbuild");

        // Replace the URL tokens
        if (url != null) {
            url = url.replaceAll(BUILD_ID_TOKEN, buildId);
        }

        return url;
    }

    /**
     * Return the URL for viewing the patch build diff information.
     *
     * @param  buildId   Build IDs to use in the URL
     * @return Service patch build diff URL
     */
    public String getPatchBuildDiffUrl(String[] buildId) {
        String url = getExternalUrl("patchdiff");

        // Replace the URL tokens
        if (url != null) {
            for (int idx = 0; idx < buildId.length; idx++) {
                url = url.replaceFirst(BUILD_ID_TOKEN, buildId[idx]);
            }
        }

        return url;
    }


    /**
     * Return the URL for tech owners to provide feedback on the patch build. 
     *
     * @param  buildId   Build ID to use in the URL
     * @return Service patch build review URL
     */
    public String getPatchBuildReviewUrl(String buildId) {
        String url = getExternalUrl("patchreview");

        // Replace the URL tokens
        if (url != null) {
            url = url.replaceAll(BUILD_ID_TOKEN, buildId);
        }

        return url;
    }


    /**
     * Return the URL for viewing changelist information.
     *
     * @return  Changelist URL
     */
    public String getChangelistUrl(CMnCheckIn cl) {
        return getChangelistUrl("", cl);
    }

    /**
     * Return the URL for viewing changelist information.
     *
     * @return  Changelist URL
     */
    public String getPatchChangelistUrl(CMnCheckIn cl) {
        return getChangelistUrl("patch", cl);
    }

    /**
     * Return the URL for viewing changelist information.
     *
     * @param   prefix    Prefix appended to external URL property name 
     * @param   cl        Check-in information 
     * @return  Changelist URL
     */
    private String getChangelistUrl(String prefix, CMnCheckIn cl) {
        String sysurl = null;
        String url = null;
        if ((cl != null) && (externalUrls != null)) {
            if (cl instanceof CMnGitCheckIn) {
                sysurl = externalUrls.get(prefix + "git");
                if (sysurl != null) {
                    url = sysurl.replaceAll(GIT_SHA_TOKEN, cl.getId());
                    url = url.replaceAll(GIT_PROJECT_TOKEN, ((CMnGitCheckIn)cl).getRepository());
                }
            } else if (cl instanceof CMnPerforceCheckIn) {
                sysurl = externalUrls.get(prefix + "perforce");
                if (sysurl != null) {
                    url = sysurl.replaceAll(CHANGELIST_TOKEN, cl.getId());
                }
            }
        }
        return url;
    }



    /**
     * Return the URL for deleting individual test suites.
     *
     * @return  Search submission URL
     */
    public String getDeleteUrl() {
        if (deleteUrl != null) {
            return deleteUrl.toString();
        } else {
            return null;
        }
    }


    /**
     * Formatting method for displaying time (milliseconds) in "hr m s" format.
     *
     * @param  time    Time value to be formatted, measured in milliseconds
     * @return Formatted string, returned in "#hr #m #s" format
     */
    public static String formatTime(long time) {
         String timestamp="";
         long seconds = (time / 1000) % 60;
         long minutes = (time / (60 * 1000)) % 60;
         long hours = time / (3600 * 1000);

         // Hours
         if (hours > 0) {
           timestamp = hours + "hr";
         }

         // Minutes
         if ((hours > 0) || (minutes > 0)) {
             if ((hours > 0) && (minutes < 10)) {
                 timestamp = timestamp + " 0" + minutes + "m";
             } else {
                 timestamp = timestamp + " " + minutes + "m";
             }
         }

         // Seconds
         if (seconds < 10) {
             timestamp = timestamp + " 0" + seconds + "s";
         } else {
             timestamp = timestamp + " " + seconds + "s";
         }

         return timestamp;
    }


    /**
     * Create a table which defines a title and border for the contents.
     *
     * @param   title   Text to be placed in the title bar
     * @param   content Content of the table
     */
    public String getTitledBorder(String title, String content) {
        return getTitledBorder(title, content, false);
    }


    /**
     * Create a table which defines a title and border for the contents.
     *
     * @param   title   Text to be placed in the title bar
     * @param   content Content of the table
     * @param   center  True if the content should be centered in the border
     */
    public String getTitledBorder(String title, String content, boolean center) {
        StringBuffer html = new StringBuffer();

        if (inputEnabled) {
            String method = null;
            if (postMethodEnabled) {
                method = "post";
            } else {
                method = "get";
            }
            html.append("<form method=\"" + method + "\" action=\"" + formUrl + "\">\n"); 
        }

        html.append("<!-- ==================================================================== -->\n");
        html.append("<table border=\"0\" cellspacing=\"0\" cellpadding=\"1\" width=\"100%\">\n");
        html.append("  <tr>\n");
        html.append("    <td bgcolor=\"" + DEFAULT_BGBORDER + "\">\n");
        html.append("      <table border=\"0\" cellspacing=\"0\" cellpadding=\"2\" width=\"100%\" bgcolor=\"" + DEFAULT_BGDARK + "\">\n");
        html.append("        <tr>\n");
        html.append("          <td><b>" + title + "</b></td>\n");
        if (inputEnabled) {
            html.append("          <td align=\"right\">");
            html.append("<input type=\"hidden\" name=\"" + FORM_STATUS_LABEL + "\" value=\"" + UPDATE_DATA + "\">");
            html.append("<input type=\"submit\" value=\"" + buttonText + "\" tabindex=\"999\">");
            html.append("</td>\n");
        }
        html.append("        </tr>\n");
        html.append("      </table>\n");
        html.append("    </td>\n");
        html.append("  </tr>\n");
        html.append("  <tr>\n");
        html.append("    <td bgcolor=\"" + DEFAULT_BGBORDER + "\">\n");
        html.append("      <table border=\"0\" cellspacing=\"0\" cellpadding=\"2\" width=\"100%\" bgcolor=\"" + DEFAULT_BGLIGHT + "\">\n");
        html.append("        <tr>\n");
        String align = "left";
        if (center) {
            align = "center";
        }
        html.append("          <td align=\"" + align + "\">\n");
        html.append(content);
        html.append("          </td>\n");
        html.append("        </tr>\n");
        html.append("      </table>\n");
        html.append("    </td>\n");
        html.append("  </tr>\n");
        html.append("</table>\n");

        if (inputEnabled) {
            html.append("</form>\n");
        }


        return html.toString();
    }

    /**
     * Create a table which defines a title and border for the contents.
     *
     * @param   title   Text to be placed in the title bar
     * @param   content Content of the table
     * @param   linkUrl	Link to be placed in the right side of the title bar
     * @param   linkName	Link to be placed in the right side of the title bar
     */
    public String getTitledBorderLink(String title, String content, URL linkUrl, String linkName) {
        StringBuffer html = new StringBuffer();

        if (inputEnabled) {
            html.append("<form method=\"post\" action=\"" + formUrl + "\">\n"); 
        }

        html.append("<!-- ==================================================================== -->\n");
        html.append("<table border=\"0\" cellspacing=\"0\" cellpadding=\"1\" width=\"100%\">\n");
        html.append("  <tr>\n");
        html.append("    <td bgcolor=\"" + DEFAULT_BGBORDER + "\">\n");
        html.append("      <table border=\"0\" cellspacing=\"0\" cellpadding=\"2\" width=\"100%\" bgcolor=\"" + DEFAULT_BGDARK + "\">\n");
        html.append("        <tr>\n");
        html.append("          <td><b>" + title + "</b></td>\n");
        html.append("          <td align=\"right\">");
        if (linkUrl != null) {
            html.append("<a href=" + linkUrl.toString() + ">" + linkName + "</a>");
        } else {
            html.append(linkName);
        }
        html.append("</td>\n");
        if (inputEnabled) {
            html.append("          <td align=\"right\">");
            html.append("<input type=\"hidden\" name=\"" + FORM_STATUS_LABEL + "\" value=\"" + UPDATE_DATA + "\">");
            html.append("<input type=\"submit\" value=\"" + buttonText + "\" tabindex=\"999\">");
            html.append("</td>\n");
        }
        html.append("        </tr>\n");
        html.append("      </table>\n");
        html.append("    </td>\n");
        html.append("  </tr>\n");
        html.append("  <tr>\n");
        html.append("    <td bgcolor=\"" + DEFAULT_BGBORDER + "\">\n");
        html.append("      <table border=\"0\" cellspacing=\"0\" cellpadding=\"2\" width=\"100%\" bgcolor=\"" + DEFAULT_BGLIGHT + "\">\n");
        html.append("        <tr>\n");
        html.append("          <td>\n");
        html.append(content);
        html.append("          </td>\n");
        html.append("        </tr>\n");
        html.append("      </table>\n");
        html.append("    </td>\n");
        html.append("  </tr>\n");
        html.append("</table>\n");

        if (inputEnabled) {
            html.append("</form>\n");
        }


        return html.toString();
    }    
}
