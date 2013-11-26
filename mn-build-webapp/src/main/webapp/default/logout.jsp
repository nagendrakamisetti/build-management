<%@ include file="common.jsp" %>
<%@ page import="com.susansilver.web.forms.LoginForm"%>
<html>
<head>
  <title>Log Out Page</title>
  <%@ include file="stylesheet.html" %>
</head>

<body>

  <table border="0" width="100%">

    <tr>
      <td align="center">
        Thank you for using the application.  Your session has been closed.  
        If you wish to resume your session, please provide your username and
        password below.
        <p>

        <form action="<%= appUrl %>/Login" method="post">
        <table border="0" cellspacing="2" cellpadding="2" align="center">
          <tr><td align="center" class="pagetitle" bgcolor="#BCC1D0">Re-authenticate</td></tr>
          <tr>
            <td>
<%= LoginForm.render(request, response) %>
            </td>
          </tr>
        </table>
        </form>
        <p>

<%@ include file="footer.jsp" %>

      </td>
    </tr>
  </table>

</body>
</html>
