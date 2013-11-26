<%@ include file="common.jsp" %>
<%
    selectedTab = ADMIN_TAB;

    // Contruct the form URLs
    URL opsUrl = new URL(appUrl + "/CMnOps");
    URL custListUrl = new URL(appUrl + "/patch/CMnCustomerList");
    URL userListUrl = new URL(appUrl + "/CMnUserList");
    URL moveBuildUrl = new URL(appUrl + "/move/CMnBuildData");

%>
<html>
<head>
  <title>Admin Page</title>
  <%@ include file="stylesheet.html" %>
</head>

<body>

  <table border="0" width="100%">
    <tr>
      <td>
<%@ include file="header.jsp" %>
      </td>
    </tr>

    <tr>
      <td align="left">
        <h3>Administration Tasks</h3>
        <ul>
          <li><a href="<%= opsUrl %>">View System Information</a></li>
          <li><a href="<%= userListUrl %>">Manage Users</a></li>
          <li><a href="<%= custListUrl %>">Manage Customers</a></li>
          <li><a href="<%= moveBuildUrl %>">Transfer Build Data</a></li>
        </ul>

        <h3>Environment Tools</h3>
        <ul>
          <li><a href="http://hdqpdmonitor1.modeln.com/zabbix/">Zabbix SNMP Monitor</a></li>
          <li><a href="http://hdqpdbldmgt1.modeln.com/phpMyAdmin/">MySQL Administration</a></li>
        </ul>

<%@ include file="footer.jsp" %>

      </td>
    </tr>
  </table>

</body>
</html>
