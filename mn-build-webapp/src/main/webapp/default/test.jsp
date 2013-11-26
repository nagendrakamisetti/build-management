<%@ include file="common.jsp" %>
<%
    selectedTab = ADMIN_TAB;
%>
<html>
<head>
  <title>Test Page</title>
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

        <b>HTTP Request</b>
        <table border="0" cellspacing="0" cellpadding="1">

          <tr>
            <td>Local Address:</td>
            <td><%= request.getLocalAddr() %> (Port <%= request.getLocalPort() %>)</td>
          </tr>

          <tr>
            <td>Local Name:</td>
            <td><%= request.getLocalName() %></td>
          </tr>

          <tr>
            <td>Remote Address:</td>
            <td><%= request.getRemoteAddr() %> (Port <%= request.getRemotePort() %>)</td>
          </tr>

          <tr>
            <td>Remote Name:</td>
            <td><%= request.getRemoteHost() %></td>
          </tr>

          <tr>
            <td>Method:</td>
            <td><%= request.getMethod() %></td>
          </tr>

          <tr>
            <td>Protocol:</td>
            <td><%= request.getProtocol() %></td>
          </tr>

          <tr>
            <td>Content Type:</td>
            <td><%= request.getContentType() %></td>
          </tr>

        </table>

<%@ include file="footer.jsp" %>

      </td>
    </tr>
  </table>

</body>
</html>
