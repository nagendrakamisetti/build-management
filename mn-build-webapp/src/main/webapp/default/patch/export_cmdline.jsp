<%@ include file="../common.jsp" %>
<%@ page import="com.modeln.build.common.data.product.CMnPatch"%>
<%@ page import="com.modeln.build.common.data.product.CMnPatchFix"%>
<%@ page import="com.modeln.build.common.tool.CMnPatchUtil"%>
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
  <title>Service Patch Tool</title>
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
        <h3>Service Patch Tool</h3>

<!-- =================================================================== -->
<%  if (patch != null) { %>
<p><pre><%= CMnPatchUtil.getPatchBuildCmd(patch, null) %></pre></p>
<%  }  %>
<!-- =================================================================== -->


<%@ include file="../footer.jsp" %>


      </td>
    </tr>
  </table>

</body>
</html>
