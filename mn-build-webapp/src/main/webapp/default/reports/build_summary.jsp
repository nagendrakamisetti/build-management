<%@ include file="../common.jsp" %>
<%@ page import="java.util.Hashtable"%>
<%@ page import="java.util.Vector"%>
<%@ page import="com.modeln.testfw.reporting.*"%>
<%@ page import="com.modeln.build.ctrl.forms.*"%>
<%@ page import="java.net.URL"%>
<%
    selectedTab = BUILD_TAB;

    // Contruct the form URLs
    URL formSubmitUrl = new URL(appUrl + "/report/CMnBuildSummary");
    URL formImageUrl = new URL(imgUrl); 
    URL formDeleteUrl = new URL(appUrl + "/report/CMnDeleteBuild");
    URL formVersionUrl = new URL(appUrl + "/report/CMnBuildData");
    URL formStatusUrl = new URL(appUrl + "/report/CMnBuildStatusNotes");
    URL formUnittestSuiteUrl = new URL(appUrl + "/report/CMnSuiteData");
    URL formUitSuiteUrl = new URL(appUrl + "/report/CMnUitSuiteData");

    Vector builds = (Vector) request.getAttribute("BUILD_LIST");
    Hashtable tests = (Hashtable) request.getAttribute("SUITE_LIST");
    CMnBuildSummaryForm form = new CMnBuildSummaryForm(formSubmitUrl, formImageUrl, builds, tests);
    form.setVersionUrl(formVersionUrl);
    form.setUnittestSuiteUrl(formUnittestSuiteUrl);
    form.setUitSuiteUrl(formUitSuiteUrl);
    form.setInputMode(false);
    form.setValues(request);
%>

<html>
<head>
  <title>Build Summary</title>
  <%@ include file="../stylesheet.html" %>
</head>

<body>
<%@ include file="../header.jsp" %>


  <table border="0" width="100%">

    <tr>
      <td>
        <h2>Build Summary</h2>

<%= form.toString() %>

<%@ include file="../footer.jsp" %>

      </td>
    </tr>
  </table>

</body>
</html>

