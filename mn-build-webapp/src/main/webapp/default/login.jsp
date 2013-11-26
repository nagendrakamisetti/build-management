<%@ include file="common.jsp" %>
<%@ page import="com.modeln.build.ctrl.forms.CMnLoginForm"%> 
<%
    URL formLoginUrl = new URL(appUrl + "/CMnLogin");
    URL formResetUrl = new URL(appUrl + "/CMnPasswordReset");
    CMnLoginForm form = new CMnLoginForm(formLoginUrl);
    form.setPasswordResetUrl(formResetUrl);
%>
<html>
<head>
  <title>Authentication Page</title>
  <%@ include file="stylesheet.html" %>
</head>

<body>

  <table border="0" width="100%">

    <tr>
      <td align="center">
        <table border="0" cellspacing="2" cellpadding="2" align="center" width="400">
          <tr><td align="center" class="pagetitle" bgcolor="#9999FF">Login Required</td></tr>
<%  if (error != null) { %>
          <!-- Error <%= error.getErrorCode() %> -->
          <tr><td><font color="red"><%= error.getErrorMsg() %></font></td></tr>
<%  } %>
          <tr>
            <td>
<%= form.render(request, response) %>
            </td>
          </tr>
          <tr><td><hr color="#9999FF"></td></tr>
        </table>
        <p>

      </td>
    </tr>
  </table>

</body>
</html>
