<%@ include file="../common.jsp" %>
<%@ page import="java.util.Vector"%>
<%@ page import="com.modeln.testfw.reporting.*"%>
<%@ page import="com.modeln.build.ctrl.forms.*"%>
<%@ page import="java.net.URL"%>
<%
    selectedTab = ENV_TAB;
    
    Vector list = (Vector) request.getAttribute("HOST_LIST");

%>
<html>
<head>
  <title>Dual Boot Hosts</title>
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
      <h3>Dual Boot Hosts</h3><br>
      <table border="1" cellspacing="2" cellpadding="2">
        <tr>
          <td>Host Name</td>
          <td>Host Type</td>
        </tr>
        
<%  
    // Iterate through list of hosts for display
    for (int idx = 0; idx < list.size(); idx++) {
        CMnDbHostActionData host = (CMnDbHostActionData) list.get(idx);
%>
        <tr>
          <td><a href="<%= appUrl %>/environment/CMnDualBootHost?hostname=<%= host.getHostname() %>"><%= host.getHostname() %></a></td>
          <td><%= host.getType() %></td>
        </tr>
<%
    }
%>
      </table>
      <p>

<%@ include file="../footer.jsp" %>

      </td>
    </tr>
  </table>

</body>
</html>
