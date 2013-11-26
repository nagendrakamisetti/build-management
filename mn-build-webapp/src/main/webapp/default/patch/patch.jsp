<%@ include file="../common.jsp" %>
<%
    selectedTab = PATCH_TAB;
%>
<html>
<head>
  <title>Service Patch Options</title>
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
        <h3>Service Patch Options</h3>
        <ul>
          <li><a href="<%= appUrl %>/patch/CMnPatchRequest">Request a Service Patch</a></li>
          <li><a href="<%= appUrl %>/patch/CMnPatchQueue">View All Pending Patches</a></li>
          <li><a href="<%= appUrl %>/patch/CMnPatchList">Search by Patch Requests</a></li>
          <li><a href="<%= appUrl %>/patch/CMnRequestQueue">View My Requests</a></li>
          <li><a href="<%= appUrl %>/patch/CMnApprovalQueue">View My Approval Queue</a></li>
          <li><a href="<%= appUrl %>/patch/CMnPatchChart">View Charts</a></li>
        </ul>
<%  if (admin) { %>
        <h3>Administration Options</h3>
        <ul>
          <li><a href="<%= appUrl %>/patch/CMnFixGroupList">Manage Fix Groups</a></li>
          <li><a href="<%= appUrl %>/patch/CMnCustomerList">Manage Customers</a></li>
        </ul>
<%  } %>

<%@ include file="../footer.jsp" %>

      </td>
    </tr>
  </table>

</body>
</html>
