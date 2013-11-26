<%@ include file="../common.jsp" %>
<%@ page import="java.util.Vector"%>
<%@ page import="com.modeln.testfw.reporting.*"%>
<%@ page import="com.modeln.build.ctrl.forms.*"%>
<%
    selectedTab = BUILD_TAB;

    URL formUrl = new URL(appUrl + "/report/CMnBuildList");
    URL imageUrl = new URL(imgUrl); 
    URL formStatusUrl = new URL(appUrl + "/report/CMnBuildStatusNotes");
    URL formSummaryUrl = new URL(appUrl + "/report/CMnBuildSummary");

    Vector releaseList = (Vector) request.getAttribute("RELEASE_LIST");
    CMnReleaseListForm releaseForm = new CMnReleaseListForm(formUrl, imageUrl, releaseList); 
    releaseForm.setStatusUrl(formStatusUrl);
    releaseForm.setSummaryUrl(formSummaryUrl);
    releaseForm.setAdminMode(admin);
    releaseForm.setInputMode(input);
%>

<html>
<head>
  <title>Build Report</title>
  <%@ include file="../stylesheet.html" %>
  <%@ include file="../javascript.html" %>
</head>

<body>
<%@ include file="../header.jsp" %>


  <table border="0" width="100%">

    <tr>
      <td>
        <h2>Release List</h2>
<%= releaseForm.toString() %>

<%@ include file="../footer.jsp" %>

      </td>
    </tr>
  </table>

</body>
</html>
