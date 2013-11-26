<%@ include file="../common.jsp" %>
<%@ page import="com.modeln.testfw.reporting.*"%>
<%@ page import="com.modeln.build.common.data.account.*"%>
<%@ page import="com.modeln.build.common.data.product.*"%>
<%@ page import="com.modeln.build.ctrl.forms.*"%>
<%
    selectedTab = PATCH_TAB;

    // Contruct the form URLs
    URL formImageUrl = new URL(imgUrl);
    URL buildUrl = new URL(appUrl + "/report/CMnBuildData");
    URL patchUrl = new URL(appUrl + "/patch/CMnPatchRequest");

    Vector patches = (Vector) request.getAttribute(IMnPatchForm.PATCH_LIST_DATA);

%>
<html>
<head>
  <title>Pending Patches</title>
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
      <h3>Pending Patches</h3><br>

<%  if ((patches != null) && (patches.size() > 0)) { %>
<table border="0" cellspacing="0" cellpadding="2">
<tr>
  <td bgcolor="#CCCCCC">Customer</td>
  <td bgcolor="#CCCCCC">Environment</td>
  <td bgcolor="#CCCCCC">Product</td>
  <td bgcolor="#CCCCCC">Patch</td>
  <td bgcolor="#CCCCCC">Age</td>
  <td bgcolor="#CCCCCC">Status</td>
</tr>

<%
        Enumeration patchList = patches.elements();
        while (patchList.hasMoreElements()) {
            CMnPatch patch = (CMnPatch) patchList.nextElement();
            String strCust = null;
            if (patch.getCustomer() != null) {
                strCust = patch.getCustomer().getName();
            }
            String strEnv = null;
            if (patch.getEnvironment() != null) {
                strEnv = patch.getEnvironment().getName();
            }
            String strProd = null;
            String urlProd = null;
            if (patch.getBuild() != null) {
                strProd = CMnBaseReleaseForm.getShortVersion(patch.getBuild().getBuildVersion());
                urlProd = buildUrl.toString() + "?" + IMnBuildForm.BUILD_ID_LABEL + "=" + patch.getBuild().getId(); 
            }

            String pidAttr = IMnPatchForm.PATCH_ID_LABEL + "=" + patch.getId();
            String urlPatch = patchUrl.toString() + "?" + pidAttr;

            String strStatus = null;
            if (patch.getStatus() != null) {
                strStatus = patch.getStatus().toString();
            }

%>
  <tr>
    <td><%= strCust %></td>
    <td><%= strEnv %></td>
    <td><tt><!-- a href="<%= urlProd %>" --><%= strProd %><!-- /a --></tt></td>
    <td><tt>(<a href="<%= urlPatch %>"><%= patch.getName() %></a>)</tt></td>
    <td align="right"><%= patch.getElapsedTimeString() %></td>
    <td><%= strStatus %></td>
  </tr>
<%      } // end of "while patchList has more elements" %>

</table>
<%  } else { %>
You have no pending approval requests.
<%  } // end of "if patches != null" %>


<%@ include file="../footer.jsp" %>

      </td>
    </tr>
  </table>

</body>
</html>
