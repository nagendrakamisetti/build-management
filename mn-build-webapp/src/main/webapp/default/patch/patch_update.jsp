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
    URL formSubmitUrl = new URL(appUrl + "/patch/CMnPatchUpdate");
    URL formImageUrl = new URL(imgUrl);
    URL formBuildUrl = new URL(appUrl + "/report/CMnBuildData");
    URL formPatchUrl = new URL(appUrl + "/report/CMnPatchRequest");

    Vector customers = (Vector) request.getAttribute(IMnPatchForm.CUSTOMER_LIST_DATA);
    Vector builds = (Vector) request.getAttribute(IMnPatchForm.BUILD_LIST_DATA);
    Vector metrics = (Vector) request.getAttribute(IMnPatchForm.BUILD_METRIC_DATA);
    Vector patches = (Vector) request.getAttribute(IMnPatchForm.PATCH_LIST_DATA);
    CMnPatch patch = (CMnPatch) request.getAttribute(IMnPatchForm.PATCH_DATA);
    

    //
    // Construct the request form and populate it with data
    //
    CMnPatchRequestForm form = new CMnPatchRequestForm(formSubmitUrl, formImageUrl);
    form.setPatchUrl(formPatchUrl);
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

    String pageTitle = "Update Patch Request";
    if (patch != null) {
        if (patch.getCustomer() != null) {
            form.setEnvironments(patch.getCustomer().getEnvironments());
        }
        form.setValues(patch);
        form.setValues(request);
    } else {
        form.setValues(request);
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
<%  // Display the patch information (or collect user input) 
    if (patch != null) { 
        form.setInputMode(true);
        out.println(form.getTitledBorder("Patch Information", form.toString()));
    } else {
        form.setInputMode(true);
        out.println(form.toString());
    }
%>
          </td>
          <td width="50%" valign="top">&nbsp;</td>
        </tr>


      </table>



<%@ include file="../footer.jsp" %>

      </td>
    </tr>
  </table>

</body>
</html>
