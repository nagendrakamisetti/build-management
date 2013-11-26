<%@ include file="../common.jsp" %>
<%@ page import="java.util.Collections"%>
<%@ page import="java.util.Enumeration"%>
<%@ page import="java.util.Vector"%>
<%@ page import="com.modeln.testfw.reporting.*"%>
<%@ page import="com.modeln.build.ctrl.forms.CMnBuildDataForm"%>
<%@ page import="com.modeln.build.ctrl.forms.CMnDatabaseQueryForm"%>
<%@ page import="com.modeln.build.ctrl.forms.CMnSuiteInfoForm"%>
<%@ page import="com.modeln.build.ctrl.forms.CMnTestListForm"%>
<%@ page import="com.modeln.build.ctrl.forms.IMnTestForm"%>
<%@ page import="com.modeln.build.common.data.database.CMnQueryData"%>
<%
    selectedTab = BUILD_TAB;

    // Contruct the form URLs
    URL formSummaryUrl = new URL(appUrl + "/report/CMnBuildSummary");
    URL formSearchUrl = new URL(appUrl + "/report/CMnBuildList");
    URL formBuildUrl = new URL(appUrl + "/report/CMnBuildData");
    URL formSuiteUrl = new URL(appUrl + "/report/CMnActSuiteData");
    URL formTestUrl = new URL(appUrl + "/report/CMnActTestData");
    URL formQueryUrl = new URL(appUrl + "/database/CMnDatabaseQuery");
    URL formImageUrl = new URL(imgUrl);
    URL formDownloadUrl = new URL(downloadUrl);

    CMnBuildDataForm buildForm = new CMnBuildDataForm(formBuildUrl, formImageUrl);
    buildForm.setSummaryUrl(formSummaryUrl);
    buildForm.setSearchUrl(formSearchUrl);
    buildForm.setVersionUrl(formBuildUrl);
    buildForm.setDownloadUrl(formDownloadUrl);
    buildForm.setValues(request);
    buildForm.setAdminMode(admin);
    buildForm.setInputMode(false);
    buildForm.setRelatedLinks(!disableHeader);
    CMnDbBuildData build = buildForm.getValues();

    //CMnDbHostData buildHost = build.getHostData();
    CMnDbTestSuite suite = (CMnDbTestSuite) request.getAttribute(IMnTestForm.SUITE_OBJECT_LABEL);
    CMnSuiteInfoForm suiteForm = new CMnSuiteInfoForm(formSuiteUrl, formImageUrl);
    suiteForm.setValues(suite);
    suiteForm.setQueryUrl(formQueryUrl);

    Vector testList = (Vector) request.getAttribute("ACT_TEST_LIST");
    CMnTestListForm testForm = new CMnTestListForm(formTestUrl, formImageUrl, testList);
%>

<html>
<head>
  <title>Build Report</title>
  <%@ include file="../stylesheet.html" %>
</head>

<body>
  <%@ include file="../header.jsp" %>

<%= buildForm.getTitledBorder(buildForm.toString()) %>
<p/>

<%  // Display the test suite information if available 
    if (suite != null) {
        out.println(suiteForm.getTitledBorder(suiteForm.toString()));
    }
%>
<p/>

<%= testForm.toString() %>
<p/>

<%@ include file="../footer.jsp" %>

</body>
</html>
