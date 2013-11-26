<%@ include file="../common.jsp" %>
<%@ page import="java.util.Vector"%>
<%@ page import="com.modeln.testfw.reporting.*"%>
<%@ page import="com.modeln.build.ctrl.forms.*"%>
<%@ page import="java.net.URL"%>
<%
    selectedTab = ENV_TAB;
    
    Vector list = (Vector) request.getAttribute("ACTION_LIST");

%>
<html>
<head>
  <title>Dual Boot Host</title>
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
      <h3>Pending Actions</h3><br>
      <table border="1" cellspacing="2" cellpadding="2">
        <tr>
          <td>Host Name</td>
          <td>Host Type</td>
          <td>Target Date</td>
          <td>Order</td>
          <td>Action</td>
          <td>Arguments</td>
        </tr>
        
<%  
    // Iterate through list of hosts for display
    for (int idx = 0; idx < list.size(); idx++) {
        CMnDbHostActionData action = (CMnDbHostActionData) list.get(idx);
%>
        <tr>
          <!-- Action ID: <%= action.getId() %> -->
          <td><%= action.getHostname() %></td>
          <td><%= action.getType() %></td>
          <td><%= action.getTargetDate() %></td>
          <td><%= action.getOrder() %></td>
          <td><%= action.getName() %></td>
          <td><%= action.getArguments() %></td>
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
