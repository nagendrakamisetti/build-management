<%@ include file="../common.jsp" %>
<%@ page import="com.modeln.build.common.data.product.CMnPatch"%>
<%@ page import="com.modeln.build.ctrl.forms.*"%>
<%
    selectedTab = PATCH_TAB;

    // Contruct the form URLs
    URL formSubmitUrl = new URL(appUrl + "/patch/CMnValidateFixes");
    URL formImageUrl = new URL(imgUrl);

    CMnPatch patch = (CMnPatch) request.getAttribute(IMnPatchForm.PATCH_DATA);
    Vector validFixes = (Vector) request.getAttribute("VALID_FIXES");
    Vector invalidFixes = (Vector) request.getAttribute("INVALID_FIXES");

    CMnPatchFixForm fixForm = new CMnPatchFixForm(formSubmitUrl, formImageUrl);
    fixForm.setExternalUrls(urls);
    if (patch != null) {
        fixForm.setBaseFixes(patch.getFixes());
        fixForm.setAvailableFixes(invalidFixes);

        // Ensure that all user-entered fixes are selected
        fixForm.addSelectedFixes(validFixes);
        fixForm.addSelectedFixes(invalidFixes);

        fixForm.setCustomer(patch.getCustomer());
    }

    // Modify the default table titles
    fixForm.setPreviousFixesTitle("Valid Fixes");
    fixForm.setAdditionalFixesTitle("Fixes to Validate");
    fixForm.setAvailableFixesTitle("Invalid Fixes");

    // Hide some columns in the table
    fixForm.showColumn(CMnPatchFixForm.COLUMN_IDX_SELECT,  true);
    fixForm.showColumn(CMnPatchFixForm.COLUMN_IDX_BUGNUM,  true);
    fixForm.showColumn(CMnPatchFixForm.COLUMN_IDX_CHANGE,  true);
    fixForm.showColumn(CMnPatchFixForm.COLUMN_IDX_EXCLUDE, false);
    fixForm.showColumn(CMnPatchFixForm.COLUMN_IDX_REQUEST, false);
    fixForm.showColumn(CMnPatchFixForm.COLUMN_IDX_BRANCH,  false);
    fixForm.showColumn(CMnPatchFixForm.COLUMN_IDX_ORIGIN,  false);
    fixForm.showColumn(CMnPatchFixForm.COLUMN_IDX_NOTES,   true);
    fixForm.showColumn(CMnPatchFixForm.COLUMN_IDX_ICONS,   false);

    // Set form attributes once the data is available
    fixForm.setPostEnabled(true);
    fixForm.setAdminMode(admin);
    fixForm.setInputMode(true);
    // Set the selected values from the request parameters
    fixForm.setValues(request);

%>
<html>
<head>
  <title>Validate Service Patch Fixes</title>
  <%@ include file="../stylesheet.html" %>
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
        <h3>Validate Service Patch Fixes</h3>

<!-- =================================================================== -->
<%  if (patch != null) { %>
<p>
<table border="0" cellspacing="0" cellpadding="1" width="100%">

<%      if (patch.getCustomer() != null) { %>
  <tr>
    <td width="20%" align="right">Customer: </td>
    <td width="80%" align="left"><%= patch.getCustomer().getName() %></td>
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

</table>
</p>
<%  }  %>
<!-- =================================================================== -->

<%= fixForm.toString() %>

<%@ include file="../footer.jsp" %>

      </td>
    </tr>
  </table>

</body>
</html>
