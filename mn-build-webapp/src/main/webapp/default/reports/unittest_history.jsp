<%@ include file="../common.jsp" %>
<%@ page import="java.util.Vector"%>
<%@ page import="java.util.Enumeration"%>
<%@ page import="com.modeln.testfw.reporting.*"%>
<%@ page import="com.modeln.build.ctrl.forms.CMnBuildDataForm"%>
<%@ page import="com.modeln.build.ctrl.forms.CMnSuiteInfoForm"%>
<%@ page import="com.modeln.build.ctrl.forms.CMnTestHistoryForm"%>
<%@ page import="com.modeln.build.ctrl.forms.CMnTestInfoForm"%>
<%@ page import="com.modeln.build.ctrl.forms.IMnTestForm"%>
<%
    selectedTab = BUILD_TAB;

    // Contruct the form URLs
    URL formSummaryUrl = new URL(appUrl + "/report/CMnBuildSummary");
    URL formSearchUrl = new URL(appUrl + "/report/CMnBuildList");
    URL formBuildUrl = new URL(appUrl + "/report/CMnBuildData");
    URL formTestUrl  = new URL(appUrl + "/report/CMnTestData");
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
    CMnDbUnitTestData test = (CMnDbUnitTestData) request.getAttribute(IMnTestForm.TEST_OBJECT_LABEL);

    CMnDbUnitTestData lastPass = (CMnDbUnitTestData) request.getAttribute(IMnTestForm.LASTPASS_OBJECT_LABEL);
    CMnDbUnitTestData lastFail = (CMnDbUnitTestData) request.getAttribute(IMnTestForm.LASTFAIL_OBJECT_LABEL);

    Vector history = (Vector) request.getAttribute(IMnTestForm.HISTORY_OBJECT_LABEL);

    // Contruct SuiteInfoForm
    URL formSuiteUrl = new URL(appUrl + "/report/CMnSuiteData");
    CMnSuiteInfoForm suiteInfoForm = new CMnSuiteInfoForm(formSuiteUrl, formImageUrl);
    suiteInfoForm.setValues(suite);
    suiteInfoForm.setAdminMode(admin);
    suiteInfoForm.setInputMode(false);

    // Construct test info form
    CMnTestInfoForm testInfoForm = new CMnTestInfoForm(formTestUrl, formImageUrl);
    testInfoForm.setValues(test, lastPass, lastFail);
    testInfoForm.setAdminMode(admin);
    testInfoForm.setInputMode(false);

    // Construct the test history form
    CMnTestHistoryForm testHistoryForm = new CMnTestHistoryForm(formTestUrl, formImageUrl, history);
    
%>

<html>
<head>
  <title>Unittest History</title>
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
  <!--                        Unittest Suite Information                    -->
  <!-- ==================================================================== -->
<%= suiteInfoForm.getTitledBorder(suiteInfoForm.toString()) %>
  <p>


  <!-- ==================================================================== -->
  <!--                        Unittest Information                          -->
  <!-- ==================================================================== -->
<%= testInfoForm.getTitledBorder(testInfoForm.toString()) %>
  <p>

  <!-- ==================================================================== -->
  <!--                         History Information                          -->
  <!-- ==================================================================== -->
<%= testHistoryForm.toString() %>
  <p>

<%@ include file="../footer.jsp" %>

</body>
</html>
