<%@ include file="../common.jsp" %>
<%@ page import="java.util.Vector"%>
<%@ page import="com.modeln.testfw.reporting.*"%>
<%@ page import="com.modeln.build.ctrl.forms.*"%>
<%@ page import="java.net.URL"%>
<%
    selectedTab = BUILD_TAB;

    // Contruct the form URLs
    URL formSubmitUrl = new URL(appUrl + "/environment/CMnBuildList");
    URL formImageUrl = new URL(imgUrl); 
    URL formDeleteUrl = new URL(appUrl + "/report/CMnDeleteBuild");
    URL formVersionUrl = new URL(appUrl + "/report/CMnBuildData");
    URL formStatusUrl = new URL(appUrl + "/report/CMnBuildStatusNotes");

    Vector list = (Vector) request.getAttribute("BUILD_LIST");
    CMnBuildHostForm form = new CMnBuildHostForm(formSubmitUrl, formImageUrl, list);
    form.setDeleteUrl(formDeleteUrl);
    form.setStatusUrl(formStatusUrl);
    form.setBuildUrl(formVersionUrl);
    form.setAdminMode(admin);
    form.setInputMode(true);
    form.setValues(request);
%>

<html>
<head>
  <title>Build Report</title>
  <%@ include file="../stylesheet.html" %>
</head>

<body>
<%@ include file="../header.jsp" %>


  <table border="0" width="100%">

    <tr>
      <td>
        <h2>Build Results</h2>

<%= form.toString() %>

<%@ include file="../footer.jsp" %>

      </td>
    </tr>
  </table>

</body>
</html>
