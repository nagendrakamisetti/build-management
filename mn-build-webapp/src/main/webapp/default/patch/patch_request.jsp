<%@ include file="../common.jsp" %>
<%@ page import="com.modeln.build.common.data.product.CMnPatch"%>
<%@ page import="com.modeln.build.common.data.product.CMnPatchFix"%>
<%@ page import="com.modeln.build.common.data.product.CMnPatchOwner"%>
<%@ page import="com.modeln.build.jenkins.Job"%>
<%@ page import="com.modeln.testfw.reporting.*"%>
<%@ page import="com.modeln.build.ctrl.forms.*"%>
<%
    selectedTab = PATCH_TAB;

    // Contruct the form URLs
    URL formSubmitUrl = new URL(appUrl + "/patch/CMnPatchRequest");
    URL formSearchUrl = new URL(appUrl + "/patch/CMnPatchList");
    URL formUpdateUrl = new URL(appUrl + "/patch/CMnPatchUpdate");
    URL formEditUrl = new URL(appUrl + "/patch/CMnPatchRequestFixes");
    URL formAssignUrl = new URL(appUrl + "/patch/CMnPatchAssignment");
    URL formCommentUrl = new URL(appUrl + "/patch/CMnViewComments");
    URL formStatusUrl = new URL(appUrl + "/patch/CMnUpdateStatus");
    URL formImageUrl = new URL(imgUrl);
    URL formApprovalUrl = new URL(appUrl + "/patch/CMnSubmitApproval");
    URL formBuildUrl = new URL(appUrl + "/report/CMnBuildData");
    URL createJobUrl = new URL(appUrl + "/patch/CMnCreateJob");
    URL formReviewUrl = new URL(appUrl + "/patch/CMnUpdateAndNotify");

    Vector customers = (Vector) request.getAttribute(IMnPatchForm.CUSTOMER_LIST_DATA);
    Vector builds = (Vector) request.getAttribute(IMnPatchForm.BUILD_LIST_DATA);
    Vector metrics = (Vector) request.getAttribute(IMnPatchForm.BUILD_METRIC_DATA);
    Vector patches = (Vector) request.getAttribute(IMnPatchForm.PATCH_LIST_DATA);
    CMnPatch patch = (CMnPatch) request.getAttribute(IMnPatchForm.PATCH_DATA);
    Vector<CMnPatchFix> fixes = (Vector<CMnPatchFix>) request.getAttribute(IMnPatchForm.FIX_LIST_DATA);
    Vector approvals = (Vector) request.getAttribute(IMnPatchForm.APPROVAL_LIST_DATA);
    Vector approvers = (Vector) request.getAttribute(IMnPatchForm.APPROVER_GROUP_DATA);
    Hashtable<Job, Vector<CMnDbBuildData>> jobs = (Hashtable<Job, Vector<CMnDbBuildData>>) request.getAttribute(IMnPatchForm.JOB_DATA);
    CMnPatchOwner owner = (CMnPatchOwner) request.getAttribute(CMnPatchOwnerForm.OWNER_DATA);
    Hashtable users = (Hashtable) request.getAttribute(CMnPatchOwnerForm.USER_LIST);
    Vector reviews = (Vector) request.getAttribute(IMnBuildForm.AREA_REVIEW_DATA);
    Vector areas = (Vector) request.getAttribute(IMnBuildForm.PRODUCT_AREA_DATA);

    String statusTitle = "Patch Status";
    String statusValue = "";
    String statusFormTitle = statusTitle;
    if ((patch != null) && (patch.getStatus() != null)) {
        statusValue = patch.getStatus().toString();
        statusFormTitle = statusTitle + ": " + statusValue;
    }
    

    //
    // Construct the status display form
    //
    CMnPatchStatusForm statusForm = new CMnPatchStatusForm(formEditUrl, formImageUrl);
    statusForm.setValues(patch);

    //
    // Construct the patch ownership form
    //
    CMnPatchOwnerForm ownerForm = new CMnPatchOwnerForm(formAssignUrl, formImageUrl, patch, users);
    ownerForm.setValues(owner);

    //
    // Construct the request form and populate it with data
    //
    CMnPatchRequestForm form = new CMnPatchRequestForm(formSubmitUrl, formImageUrl);
    form.setPatchUrl(formSubmitUrl);
    form.setSearchUrl(formSearchUrl);
    form.setBuildUrl(formBuildUrl);
    form.setExternalUrls(urls);
    if (builds != null) {
        form.setReleasesFromBuildList(builds);
        form.setBuilds(builds);
    }
    if (customers != null) {
        form.setCustomers(customers);
    }
    if (metrics != null) {
        form.setBuildMetrics(metrics);
    }
    if (patches != null) {
        form.setPreviousPatches(patches);
    }

    // Set form attributes once the data is available
    form.setPostEnabled(true);
    form.setAdminMode(admin);

    // Set the selected values from the request parameters
    String pageTitle = null;
    URL patchAssignmentUrl = null;
    URL statusUpdateUrl = null; 
    URL patchCommentUrl = null;
    URL patchUpdateUrl = null;
    if (patch != null) {
        patchAssignmentUrl = new URL(formAssignUrl.toString()  + "?" + IMnPatchForm.PATCH_ID_LABEL + "=" + patch.getId());
        statusUpdateUrl    = new URL(formStatusUrl.toString()  + "?" + IMnPatchForm.PATCH_ID_LABEL + "=" + patch.getId());
        patchCommentUrl    = new URL(formCommentUrl.toString() + "?" + IMnPatchForm.PATCH_ID_LABEL + "=" + patch.getId());
        patchUpdateUrl     = new URL(formUpdateUrl.toString()  + "?" + IMnPatchForm.PATCH_ID_LABEL + "=" + patch.getId());
        form.setEnvironments(patch.getCustomer().getEnvironments());
        form.setReleasesFromBuildList(builds);
        form.setValues(patch);
        form.setValues(request);
        pageTitle = "View Patch Request";
    } else {
        patchAssignmentUrl = formAssignUrl;
        statusUpdateUrl = formStatusUrl;
        patchCommentUrl = formCommentUrl;
        patchUpdateUrl = formUpdateUrl;
        form.setValues(request);
        pageTitle = "New Patch Request";
    }

    // Construct the patch review form
    CMnPatchReviewForm reviewForm = new CMnPatchReviewForm(formReviewUrl, formImageUrl);
    reviewForm.setAdminMode(admin);
    reviewForm.setProductAreas(areas);
    reviewForm.setBuildReviews(reviews);
    reviewForm.setValues(patch);
    reviewForm.setExternalUrls(urls);

    // Construct the approval list
    CMnPatchApprovalForm approvalform = new CMnPatchApprovalForm(formApprovalUrl, formImageUrl);
    approvalform.setUser(user);
    approvalform.setPatch(patch);
    if (approvals != null) {
        approvalform.setApprovals(approvals);
    }
    if ((patch != null) && (approvers != null)) {
        approvalform.setApprovers(approvers);
    }

    // Construct the comment list
    CMnPatchCommentForm commentform = new CMnPatchCommentForm(formCommentUrl, formImageUrl, patch);
    commentform.setAdminMode(admin);

    // Construct a read-only version of the fix list
    CMnPatchFixForm fixform = new CMnPatchFixForm(formSubmitUrl, formImageUrl);
    fixform.setExternalUrls(urls);
    if (fixes != null) {
        patch.setFixes(fixes);
        fixform.setBaseFixes(fixes);
        fixform.setSelectedFixes(fixes);
    }
    fixform.setPostEnabled(false);
    fixform.setAdminMode(admin);
    fixform.setInputMode(false);
    fixform.showAvailableFixes(false);
    fixform.showAdvancedFeatures(true);
    // Set the visible form elements if a patch is being requested/edited
    if (patch != null) {
        fixform.setVerifyUrl(new URL(appUrl + "/patch/CMnValidateFixes?" + IMnPatchForm.PATCH_ID_LABEL + "=" + patch.getId()));
        fixform.setExportUrl(new URL(appUrl + "/patch/CMnExportFixes?" + IMnPatchForm.PATCH_ID_LABEL + "=" + patch.getId()));
        fixform.setOriginUrl(new URL(appUrl + "/patch/CMnOriginUpdate?" + IMnPatchForm.PATCH_ID_LABEL + "=" + patch.getId()));
    }
%>
<html>
<head>
  <title><%= pageTitle %></title>
  <%@ include file="../stylesheet.html" %>
  <%@ include file="../javascript.html" %>
</head>

<body>
  <table border="0" width="100%">
    <tr>
      <td>
<%@ include file="../header.jsp" %>
      </td>
    </tr>

    <tr>
      <td align="left">
      <h3><%= pageTitle %></h3><br>

      <!-- =============================================================== -->
      <!-- Patch information and status                                    -->
      <!-- =============================================================== -->
      <table border="0" cellspacing="0" cellpadding="5" width="90%">
        <tr>
          <td width="50%" valign="top">
            <p>
<%  // Display the patch information (or collect user input) 
    if (patch != null) { 
        form.setInputMode(false);
        if (admin) {
            out.println(ownerForm.getTitledBorderLink("Patch Information", form.toString(), patchUpdateUrl, "Update"));
        } else {
            out.println(form.getTitledBorder("Patch Information", form.toString()));
        }
    } else {
        form.setInputMode(true);
        out.println(form.toString());
    }
%>
            </p>

<%  // Display the patch status
    if (patch != null) {
        out.println("<p>");
        if (admin) {
            out.println(statusForm.getTitledBorderLink(statusFormTitle, statusForm.toString(), statusUpdateUrl, "Update"));
        } else {
            out.println(statusForm.getTitledBorder(statusFormTitle, statusForm.toString(), true));
        }
        out.println("</p>");
    }
%>

          </td>
          <td width="50%" valign="top">
<%  // Display the review information
    if (patch != null) {
        out.println("<p>"); 
        try {
            out.println(reviewForm.getTitledBorder("Patch Review", reviewForm.toString(), false));
        } catch (Exception approvalex) {
            out.println("JSP Exception: " + approvalex);
            out.println("<pre>\n");
            StackTraceElement[] lines = approvalex.getStackTrace();
            for (int idx = 0; idx < lines.length; idx++) {
                out.println(lines[idx] + "\n");
            }
            out.println("</pre>\n");
        }
        out.println("</p>");
    }
%>

      <!-- =============================================================== -->
      <!-- Patch owner                                                     -->
      <!-- =============================================================== -->
<%  // Display the owner information
    if (patch != null) {
        out.println("<p>");
        String ownerFormTitle = "Patch Owner";
        String ownerFormHtml = null;
        String ownerFormButton = null;
        if (owner != null) {
            ownerFormHtml = ownerForm.toString();
            ownerFormButton = "Update";
        } else {
            ownerFormHtml = "<center><i>No Assigned Owner</i></center>";
            ownerFormButton = "Assign";
        }

        if (admin) {
            out.println(ownerForm.getTitledBorderLink(ownerFormTitle, ownerFormHtml, patchAssignmentUrl, ownerFormButton));
        } else {
            out.println(ownerForm.getTitledBorder(ownerFormTitle, ownerFormHtml, true));
        }

        out.println("</p>");
    }
%>
          </td>
        </tr>


      <!-- =============================================================== -->
      <!-- Patch Approval                                                  -->
      <!-- =============================================================== -->
<%  
    try {
        String approvalformcontent = approvalform.toString();

        // Handle the case where we don't want to display an update
        // button if the user is offered something to approve 
        if ((approvalformcontent != null) && (approvalformcontent.indexOf("<input ") >= 0)) {
            approvalform.setInputMode(true);
        } else {
            approvalform.setInputMode(false);
        }

        // Only display a titled border if there is data to display
        if ((approvalformcontent != null) && (approvalformcontent.indexOf("<table ") >= 0)) {
%>
          <tr>
            <td colspan="2" align="left">
              <%= approvalform.getTitledBorder(approvalform.toString()) %>
            </td>
          </tr>
<%
        }
    } catch (Exception approvalex) {
        out.println("JSP Exception: " + approvalex);
        out.println("<pre>\n");
        StackTraceElement[] lines = approvalex.getStackTrace();
        for (int idx = 0; idx < lines.length; idx++) {
            out.println(lines[idx] + "\n");
        }
        out.println("</pre>\n");
    }
%>


      <!-- =============================================================== -->
      <!-- Patch Comments                                                  -->
      <!-- =============================================================== -->

        <tr>
          <td colspan="2" align="left">
<% 
    if (patch != null) {
%>
        <tr>
          <td colspan="2" align="left">
            <%= statusForm.getTitledBorderLink("Patch Comment", commentform.getCommentTable(), patchCommentUrl, "Add Comment") %>
          </td>
        </tr>
<%
    }
%>



      <!-- =============================================================== -->
      <!-- Job List                                                        -->
      <!-- =============================================================== -->
<%  
    if (patch != null) {
        URL jobUrl = new URL(createJobUrl + "?" + IMnPatchForm.PATCH_ID_LABEL + "=" + patch.getId());
        CMnPatchJobForm jobForm = new CMnPatchJobForm(jobUrl, formImageUrl);
        jobForm.setJobs(jobs);
%>
        <tr>
          <td colspan="2" align="left">
            <%= jobForm.getTitledBorderLink("Build History", jobForm.toString(), jobUrl, "Create Job") %>
          </td>
        </tr>
<%
    }
%>

      </table>


      <!-- =============================================================== -->
      <!-- SDR List                                                        -->
      <!-- =============================================================== -->
<%
    try {
        String fixcontent = fixform.toString();

        // Only display a titled border if there is data to display
        if ((fixcontent != null) && (fixcontent.indexOf("<table ") >= 0)) {
%>
            <p><%= fixform.toString() %></p>
<%
        }
    } catch (Exception fixex) {
        out.println("JSP Exception: " + fixex);
        out.println("<pre>\n");
        StackTraceElement[] lines = fixex.getStackTrace();
        for (int idx = 0; idx < lines.length; idx++) {
            out.println(lines[idx] + "\n");
        }
        out.println("</pre>\n");
    }
%>



<%@ include file="../footer.jsp" %>

      </td>
    </tr>
  </table>

</body>
</html>
