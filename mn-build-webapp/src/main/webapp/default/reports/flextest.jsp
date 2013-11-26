<%@ include file="../common.jsp" %>
<%@ page import="java.util.Vector"%>
<%@ page import="com.modeln.testfw.reporting.*"%>
<%@ page import="com.modeln.build.ctrl.forms.CMnBuildDataForm"%>
<%@ page import="com.modeln.build.ctrl.forms.CMnSuiteInfoForm"%>
<%@ page import="com.modeln.build.ctrl.forms.CMnTestInfoForm"%>
<%@ page import="com.modeln.build.ctrl.forms.IMnTestForm"%>
<%@ page import="com.modeln.build.util.StringUtility"%>
<%
    selectedTab = BUILD_TAB;

    // Contruct the form URLs
    URL formSummaryUrl = new URL(appUrl + "/report/CMnBuildSummary");
    URL formSearchUrl = new URL(appUrl + "/report/CMnBuildList");
    URL formBuildUrl   = new URL(appUrl + "/report/CMnBuildData");
    URL formTestUrl = new URL(appUrl + "/report/CMnFlexTestData");
    URL formHistoryUrl = new URL(appUrl + "/report/CMnFlexTestHistory" + "?" + request.getQueryString());
    URL formImageUrl   = new URL(imgUrl);
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
    CMnDbFlexTestData test = (CMnDbFlexTestData) request.getAttribute(IMnTestForm.TEST_OBJECT_LABEL);

    CMnDbFlexTestData lastPass = (CMnDbFlexTestData) request.getAttribute(IMnTestForm.LASTPASS_OBJECT_LABEL);
    CMnDbFlexTestData lastFail = (CMnDbFlexTestData) request.getAttribute(IMnTestForm.LASTFAIL_OBJECT_LABEL);

    // Contruct UitSuiteInfoForm
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


  <!-- ==================================================================== -->
  <!--                          Flex Information                            -->
  <!-- ==================================================================== -->
<%= testInfoForm.getTitledBorderLink(testInfoForm.toString(), formHistoryUrl, "history") %>
  <p>


<%  String message = test.getMessage();
    if ((message != null) && (message.length() > 0)) {
%>
  <b>Test Messages:</b>
  <pre>
<%= StringUtility.stringToHtml(test.getMessage()) %>
  </pre>
<%  } %>


<%@ include file="../footer.jsp" %>

</body>
</html>
