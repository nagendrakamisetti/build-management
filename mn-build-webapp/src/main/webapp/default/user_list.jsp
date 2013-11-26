<%@ include file="common.jsp" %>
<%@ page import="com.modeln.build.ctrl.forms.IMnUserForm"%>
<%@ page import="com.modeln.build.ctrl.forms.CMnUserListForm"%>
<%
    selectedTab = ADMIN_TAB;

    // Contruct the form URLs
    URL formSubmitUrl = new URL(appUrl + "/CMnUserList");
    URL formImageUrl = new URL(imgUrl);
    URL formUserUrl = new URL(appUrl + "/CMnUser");
    URL formDeleteUrl = new URL(appUrl + "/CMnUserDelete");

    Vector list = (Vector) request.getAttribute(IMnUserForm.USER_LIST_DATA);
    CMnUserListForm form = new CMnUserListForm(formSubmitUrl, formImageUrl, list);
    form.setUserUrl(formUserUrl);
    form.setDeleteUrl(formDeleteUrl);
    form.setAdminMode(admin);
    //form.setInputMode(true);
    //form.setValues(request);

%>
<html>
<head>
  <title>User List</title>
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
