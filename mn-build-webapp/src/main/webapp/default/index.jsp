<%@ include file="common.jsp" %>
<%
    selectedTab = HOME_TAB;
%>
<html>
<head>
  <title>Home Page</title>
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
      Welcome to the Model N Build and Release management application.  From here 
      you can view build history, access environment tools, and formulate plans for
      world domination. 
      <p>

<%@ include file="footer.jsp" %>

      </td>
    </tr>
  </table>

</body>
</html>
