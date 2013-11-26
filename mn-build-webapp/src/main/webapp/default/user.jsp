<%@ include file="common.jsp" %>
<%@ page import="com.modeln.build.ctrl.forms.IMnUserForm"%>
<%@ page import="com.modeln.build.ctrl.forms.CMnUserForm"%>
<%
    selectedTab = ADMIN_TAB;

    // Contruct the form URLs
    URL formSubmitUrl = new URL(appUrl + "/CMnUser");
    URL formImageUrl = new URL(imgUrl);

    CMnUserForm form = new CMnUserForm(formSubmitUrl, formImageUrl);
    form.setAdminMode(admin);
    form.setInputMode(true);
    form.setValues(request);

%>
<html>
<head>
  <title>Edit User</title>
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

<%= form.toString() %>

<%@ include file="footer.jsp" %>



      </td>
    </tr>
  </table>

</body>
</html>
