<%@ include file="../common.jsp" %>
<%@ page import="java.util.Vector"%>
<%@ page import="com.modeln.testfw.reporting.*"%>
<%@ page import="com.modeln.build.ctrl.forms.CMnBuildDataForm"%>
<%@ page import="com.modeln.build.ctrl.forms.CMnUitSuiteInfoForm"%>
<%@ page import="com.modeln.build.ctrl.forms.CMnTestInfoForm"%>
<%@ page import="com.modeln.build.ctrl.forms.IMnTestForm"%>
<%@ page import="com.modeln.build.ctrl.view.CMnUitHistoryView"%>
<%@ page import="com.modeln.build.ctrl.view.CMnUitStepView"%>
<%@ page import="com.modeln.build.util.StringUtility"%>

<%
    selectedTab = BUILD_TAB;

    // Contruct the form URLs
    URL formSummaryUrl = new URL(appUrl + "/report/CMnBuildSummary");
    URL formSearchUrl = new URL(appUrl + "/report/CMnBuildList");
    URL formBuildUrl = new URL(appUrl + "/report/CMnBuildData");
    URL formImageUrl = new URL(imgUrl);
    URL formDownloadUrl = new URL(downloadUrl);

	// Construct BuildDataForm
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

    // Contruct UitSuiteInfoForm
    URL formUitSuiteUrl = new URL(appUrl + "/report/CMnUitSuiteData");
    CMnUitSuiteInfoForm uitSuiteInfoForm = new CMnUitSuiteInfoForm(formUitSuiteUrl, formImageUrl);
    CMnDbTestSuite suite = (CMnDbTestSuite) request.getAttribute(IMnTestForm.SUITE_OBJECT_LABEL);
    uitSuiteInfoForm.setValues(suite);
    uitSuiteInfoForm.setAdminMode(admin);
    uitSuiteInfoForm.setInputMode(false);
  
    // Construct UitInfoForm
    URL formUitUrl = new URL(appUrl + "/report/CMnUitData");
    CMnTestInfoForm uitInfoForm = new CMnTestInfoForm(formUitUrl, formImageUrl);
    CMnDbUit uit = (CMnDbUit) request.getAttribute(IMnTestForm.TEST_OBJECT_LABEL);
    CMnDbUit lastPass = (CMnDbUit) request.getAttribute(IMnTestForm.LASTPASS_OBJECT_LABEL);
    CMnDbUit lastFail = (CMnDbUit) request.getAttribute(IMnTestForm.LASTFAIL_OBJECT_LABEL);
    uitInfoForm.setValues(uit, lastPass, lastFail);
    uitInfoForm.setAdminMode(admin);
    uitInfoForm.setInputMode(false);
  
    URL viewHistoryUrl = new URL(appUrl + "/report/CMnUitHistory?uitId="+uit.getId());
    CMnUitStepView uitStepView = new CMnUitStepView(uit.getSteps());
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
  <!--                           UIT Suite Information                      -->
  <!-- ==================================================================== -->
<%= uitSuiteInfoForm.getTitledBorder(uitSuiteInfoForm.toString()) %>
  <p>

  
  <!-- ==================================================================== -->
  <!--                           UIT Information                            -->
  <!-- ==================================================================== -->
<%= uitInfoForm.getTitledBorderLink(uitInfoForm.toString(), viewHistoryUrl, "history") %>
  <p>
  
  
  <!-- ==================================================================== -->
  <!--                             UIT Steps                                -->
  <!-- ==================================================================== -->
<%= uitStepView.toString() %>
  <p>



<%@ include file="../footer.jsp" %>

</body>
</html>
