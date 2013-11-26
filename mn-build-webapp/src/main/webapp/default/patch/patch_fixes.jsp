<%@ include file="../common.jsp" %>
<%@ page import="com.modeln.build.common.data.product.CMnPatch"%>
<%@ page import="com.modeln.testfw.reporting.*"%>
<%@ page import="com.modeln.build.ctrl.forms.*"%>
<%
    selectedTab = PATCH_TAB;

    // Contruct the form URLs
    URL formSubmitUrl = new URL(appUrl + "/patch/CMnPatchRequestFixes");
    URL formImageUrl = new URL(imgUrl);
    URL formFixGroupUrl = new URL(appUrl + "/patch/CMnPatchFixGroup");

    CMnPatch patch = (CMnPatch) request.getAttribute(IMnPatchForm.PATCH_DATA);

    int availableFixCount = 0;
    int baseFixCount = 0;
    Vector baseFixes = (Vector) request.getAttribute("BASE_FIXES");
    if (baseFixes != null) {
        baseFixCount = baseFixes.size();
    }
    Vector availableFixes = (Vector) request.getAttribute("AVAILABLE_FIXES");
    if (availableFixes != null) {
        availableFixCount = availableFixes.size();
    }
    Vector fixGroups = (Vector) request.getAttribute(IMnPatchForm.FIX_GROUP_DATA);

    // Construct a form to display the fix groups
    CMnFixGroupListForm groupForm = new CMnFixGroupListForm(formFixGroupUrl, formImageUrl, fixGroups);

    //
    // Construct the request form and populate it with data
    //
    CMnPatchFixForm form = new CMnPatchFixForm(formSubmitUrl, formImageUrl);
    form.setExternalUrls(urls);
    boolean forExternalUse = true;
    if (patch != null) { 
        forExternalUse = patch.getForExternalUse();
    }
    form.showAdvancedFeatures(true);
    if (fixGroups != null) {
        form.setFixGroups(fixGroups);
    }
    if (baseFixes != null) {
        form.setBaseFixes(baseFixes);
        form.setSelectedFixes(baseFixes);
    }
    if (availableFixes != null) {
        form.setAvailableFixes(availableFixes);
    }
    // Set form attributes once the data is available
    form.setPostEnabled(true);
    form.setAdminMode(admin);
    form.setInputMode(true);
    // Set the selected values from the request parameters
    form.setValues(request);

%>
<html>
<head>
  <title>Patch Fixes</title>
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
      <h3>Patch Fixes</h3><br>

<!-- =================================================================== -->
<!-- Patch information                                                   -->
<!-- =================================================================== -->
<%  if (patch != null) { %>
<p>
<table border="0" cellspacing="0" cellpadding="1" width="100%">

<%      if (patch.getCustomer() != null) { %> 
  <tr>
    <td width="20%" align="right">Customer: </td>
    <td width="80%" align="left"><%= patch.getCustomer().getShortName() %></td>
  </tr>
<%      } %>

<%      if (patch.getEnvironment() != null) { %>
  <tr>
    <td width="20%" align="right">Environment: </td>
    <td width="80%" align="left"><%= patch.getEnvironment().getName() %></td>
  </tr>
<%      } %>

<%      if (patch.getBuild() != null) { %>
  <tr>
    <td width="20%" align="right">Build Version: </td>
    <td width="80%" align="left"><%= patch.getBuild().getBuildVersion() %></td>
  </tr>
<%      } %>

  <tr>
    <td width="20%" align="right">Patch Name: </td>
    <td width="80%" align="left"><%= patch.getName() %></td>
  </tr>

  <tr>
    <td width="20%" align="right">Available to Customer: </td>
    <td width="80%" align="left"><%= forExternalUse %></td>
  </tr>

</table>
</p>
<%  }  %>


<!-- =================================================================== -->
<!-- Fixes                                                               -->
<!-- =================================================================== -->

<% if ((fixGroups != null) && (fixGroups.size() > 0)) { %>
<b>This release has <%= fixGroups.size() %> SDR recommendations:</b>
<%= groupForm.toString() %>
<% } %>

<b>Found <%= baseFixCount %> base fixes and <%= availableFixCount %> available fixes.</b>

<%  // Display the check-in information
    try {
        out.println(form.toString());
    } catch (Exception formex) {
        out.println("JSP Exception: " + formex);
        out.println("<pre>\n");
        StackTraceElement[] lines = formex.getStackTrace();
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
