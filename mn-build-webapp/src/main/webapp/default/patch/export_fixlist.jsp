<%@ include file="../common.jsp" %>
<%@ page import="com.modeln.build.common.data.product.CMnPatch"%>
<%@ page import="com.modeln.build.common.data.product.CMnPatchFix"%>
<%@ page import="com.modeln.build.ctrl.forms.*"%>
<%
    selectedTab = PATCH_TAB;

    // Contruct the form URLs
    URL formSubmitUrl = new URL(appUrl + "/patch/CMnExportFixes");
    URL formImageUrl = new URL(imgUrl);

    CMnPatch patch = (CMnPatch) request.getAttribute(IMnPatchForm.PATCH_DATA);
%>
<html>
<head>
  <title>Export Service Patch Fixes</title>
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
        <h3>Export Service Patch Fixes</h3>

<!-- =================================================================== -->
<%  if (patch != null) { %>
<p>
<table border="0" cellspacing="0" cellpadding="1" width="100%">
  <tr>
    <td width="20%" align="right">Export Format: </td>
    <td>
      <form action="<%= formSubmitUrl.toString() %>" method="GET">
        <input type="hidden" name="<%= IMnPatchForm.PATCH_ID_LABEL %>" value="<%= patch.getId() %>"/>
        <select name="<%= IMnPatchForm.EXPORT_FORMAT_LABEL %>" autofocus="true" size="1">
          <option value="<%= IMnPatchForm.EXPORT_CSV %>">Spreadsheet</option>
          <option value="<%= IMnPatchForm.EXPORT_EXE %>">Command-line Tool</option>
        </select>
        <input type="submit" value="Export"/>
      </form>
    </td>
  </tr>

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

  <tr>
    <td width="20%" align="right" valign="top">Fix List: </td>
    <td width="80%" align="left"  valign="top">

<% // Loop through each fix and display it to the user
       if (patch.getFixes() != null) {
           int counter = 0;
           boolean needsComma = false;
           CMnPatchFix currentFix = null;
           Iterator fixIter = patch.getFixes().iterator();
           while (fixIter.hasNext()) {
               currentFix = (CMnPatchFix) fixIter.next();
               if (needsComma) {
                   out.print(",");
               } else {
                   needsComma = true;
               }

               // Wrap to a new line after each 10 bugs
               if ((counter > 0) && ((counter % 10) == 0)) {
                   out.print("<br/>\n");
               }

               // Print the current bug
               out.print(currentFix.getBugId());
               counter++;
           }
       }
%>
    </td>
  </tr>

</table>
</p>
<%  }  %>
<!-- =================================================================== -->


<%@ include file="../footer.jsp" %>


      </td>
    </tr>
  </table>

</body>
</html>
