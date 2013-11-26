<%@ include file="../common.jsp" %>
<%
    selectedTab = ENV_TAB;
%>
<html>
<head>
  <title>Environment List</title>
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
      <h3>Environment List</h3><br>
      <ul>
        <li><a href="<%= appUrl %>/environment/CMnDualBootList">Dual Boot Servers</a></li>
        <li><a href="<%= appUrl %>/environment/CMnBuildList">Build Servers</a></li>
        <li><a href="<%= appUrl %>/environment/CMnDeploymentList">Build Deployments</a></li>
      </ul>
      <p>

<%@ include file="../footer.jsp" %>

      </td>
    </tr>
  </table>

</body>
</html>
