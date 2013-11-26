<%@ include file="../common.jsp" %>
<%@ page import="java.util.Collections"%>
<%@ page import="java.util.Vector"%>
<%@ page import="com.modeln.testfw.reporting.*"%>
<%@ page import="com.modeln.build.ctrl.forms.CMnBuildDataForm"%>
<%@ page import="com.modeln.build.ctrl.forms.CMnDatabaseQueryForm"%>
<%@ page import="com.modeln.build.ctrl.forms.CMnTestListForm"%>
<%@ page import="com.modeln.build.ctrl.forms.CMnSuiteInfoForm"%>
<%@ page import="com.modeln.build.ctrl.forms.CMnTestInfoForm"%>
<%@ page import="com.modeln.build.ctrl.forms.IMnTestForm"%>
<%@ page import="com.modeln.build.common.data.database.CMnQueryData"%>
<%
    selectedTab = BUILD_TAB;

    // Contruct the form URLs
    URL formSummaryUrl = new URL(appUrl + "/report/CMnBuildSummary");
    URL formSearchUrl = new URL(appUrl + "/report/CMnBuildList");
    URL formBuildUrl = new URL(appUrl + "/report/CMnBuildData");
    URL formTestUrl = new URL(appUrl + "/report/CMnFlexTestData");
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

    CMnDbHostData buildHost = build.getHostData();
    CMnDbTestSuite suite = (CMnDbTestSuite) request.getAttribute(IMnTestForm.SUITE_OBJECT_LABEL);
    CMnDbHostData suiteHost = suite.getHostData();
    Vector testList = (Vector) request.getAttribute("FLEX_TEST_LIST");
    CMnTestListForm testForm = new CMnTestListForm(formTestUrl, formImageUrl, testList);

    // Create an object containing the JDBC connection information
    String jdbcUrl = suite.getJdbcUrl();
    CMnQueryData jdbcObj = new CMnQueryData(jdbcUrl);

    CMnDbFlexTestData lastPass = (CMnDbFlexTestData) request.getAttribute(IMnTestForm.LASTPASS_OBJECT_LABEL);
    CMnDbFlexTestData lastFail = (CMnDbFlexTestData) request.getAttribute(IMnTestForm.LASTFAIL_OBJECT_LABEL);

    // Contruct UitSuiteInfoForm
    URL formSuiteUrl = new URL(appUrl + "/report/CMnSuiteData");
    CMnSuiteInfoForm suiteInfoForm = new CMnSuiteInfoForm(formSuiteUrl, formImageUrl);
    suiteInfoForm.setValues(suite);
    suiteInfoForm.setAdminMode(admin);
    suiteInfoForm.setInputMode(false);

%>

<html>
<head>
  <title>Build Report</title>
  <%@ include file="../stylesheet.html" %>
</head>

<body>
  <%@ include file="../header.jsp" %>

  <!-- ==================================================================== -->
  <!--                           Build Summary                              -->
  <!-- ==================================================================== -->
  <%= buildForm.getTitledBorder(buildForm.toString()) %>
  <p>


  <!-- ==================================================================== -->
  <!--                          Flex Suite Information                      -->
  <!-- ==================================================================== -->
<%= suiteInfoForm.getTitledBorder(suiteInfoForm.toString()) %>
  <p>


<%= testForm.toString() %>


<%@ include file="../footer.jsp" %>

</body>
</html>
