<%@ include file="common.jsp" %>
<%@ page import="com.modeln.build.ctrl.forms.CMnPasswordResetForm"%> 
<%
    UserData userdata = (UserData) request.getAttribute(CMnPasswordResetForm.USER_DATA); 
%>
<html>
<head>
  <title>Password Reset</title>
  <%@ include file="stylesheet.html" %>
  <%@ include file="pwcheck.html" %>
</head>

<body>

  <table border="0" width="100%">

    <tr>
      <td align="center">
        <form action="<%= appUrl %>/CMnPasswordReset" method="post">
        <table border="0" cellspacing="2" cellpadding="2" align="center" width="400">
          <tr><td align="center" class="pagetitle" bgcolor="#9999FF">Password Reset</td></tr>
          <tr>
            <td>
<%= CMnPasswordResetForm.render(request, response) %>
            </td>
          </tr>
          <tr>
            <td>
              <hr color="#9999FF">
<%  if (userdata != null) { %>
              You password has been reset to a temporary value and the
              new password has been sent to <%= userdata.getEmailAddress() %>.
              Please use the temporary password to complete the following
              form.
<%  } else { %>
              Please provide your system username (not your e-mail address).
              If the account exists, the password will be set to a temporary
              value and the new password will be e-mailed to the address
              associated with the account.
<%  } %>
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
