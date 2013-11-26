<%@ include file="../common.jsp" %>
<%@ page import="java.util.Vector"%>
<%@ page import="com.modeln.testfw.reporting.*"%>
<%@ page import="com.modeln.build.ctrl.forms.*"%>
<%@ page import="java.net.URL"%>
<%
    selectedTab = BUILD_TAB;

    // Contruct the form URLs
    URL formSubmitUrl = new URL(appUrl + "/report/CMnBuildDiff");
    URL formImageUrl = new URL(imgUrl); 
    URL formVersionUrl = new URL(appUrl + "/report/CMnBuildData");

    URL formUnittestUrl = new URL(appUrl + "/report/CMnTestData");
    URL formActUrl      = new URL(appUrl + "/report/CMnActTestData");
    URL formFlexUrl     = new URL(appUrl + "/report/CMnFlexTestData");
    URL formUitUrl      = new URL(appUrl + "/report/CMnUitData");


    Vector list = (Vector) request.getAttribute("BUILD_LIST");
    CMnBuildDiffForm form = new CMnBuildDiffForm(formSubmitUrl, formImageUrl, list);
    form.setAdminMode(admin);
    form.setInputMode(true);
    form.setBuildUrl(formVersionUrl);

    form.setUnitTestUrl(formUnittestUrl);
    form.setActTestUrl(formActUrl);
    form.setFlexTestUrl(formFlexUrl);
    form.setUitTestUrl(formUitUrl);

    //form.setValues(request);
%>

<html>
<head>
  <title>Build Diff</title>
  <%@ include file="../stylesheet.html" %>
</head>

<body>
<%@ include file="../header.jsp" %>


  <table border="0" width="100%">

    <tr>
      <td>
        <h2>Build Diff</h2>

<%= form.toString() %>

<%@ include file="../footer.jsp" %>

      </td>
    </tr>
  </table>

</body>
</html>
