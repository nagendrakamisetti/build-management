<%@ include file="../common.jsp" %>
<%@ page import="com.modeln.build.ctrl.forms.CMnBuildQueryForm"%>
<%
    selectedTab = ADMIN_TAB;

    URL formQueryUrl  = new URL(appUrl + "/move/CMnBuildData");
    URL formImageUrl  = new URL(imgUrl);

    // Construct the build information to be presented
    CMnBuildQueryForm dbForm = new CMnBuildQueryForm(formQueryUrl, formImageUrl);

    dbForm.setValues(request);

%>
<html>
<head>
  <title>Database Query Editor</title>
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
      <h3>Remote Build</h3><br>
      <table border="0" cellspacing="2" cellpadding="2" width="100%">
        <tr>
          <td valign="top">
<%= dbForm.toString() %>
          </td>
        </tr>
      </table>
      <p>   


<%@ include file="../footer.jsp" %>

      </td>
    </tr>
  </table>

</body>
</html>
