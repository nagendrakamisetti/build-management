<%@ include file="../common.jsp" %>
<%@ page import="java.util.Vector"%>
<%@ page import="com.modeln.testfw.reporting.*"%>
<%@ page import="com.modeln.build.ctrl.forms.CMnBuildDataForm"%>
<%@ page import="com.modeln.build.ctrl.forms.CMnUitSuiteInfoForm"%>
<%@ page import="com.modeln.build.ctrl.forms.IMnTestForm"%>
<%@ page import="com.modeln.build.ctrl.view.CMnUitStatusView"%>


<%
    selectedTab = BUILD_TAB;

    // Contruct the form URLs
    URL formSummaryUrl = new URL(appUrl + "/report/CMnBuildSummary");
    URL formSearchUrl = new URL(appUrl + "/report/CMnBuildList");
    URL formBuildUrl = new URL(appUrl + "/report/CMnBuildData");
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

    URL formUitSuiteUrl = new URL(appUrl + "/report/CMnUitSuiteData");
    CMnUitSuiteInfoForm uitSuiteInfoForm = new CMnUitSuiteInfoForm(formUitSuiteUrl, formImageUrl);
    CMnDbTestSuite suite = (CMnDbTestSuite) request.getAttribute(IMnTestForm.SUITE_OBJECT_LABEL);
    uitSuiteInfoForm.setValues(suite);
    //uitSuiteInfoForm.setValues(request);
    uitSuiteInfoForm.setAdminMode(admin);
    uitSuiteInfoForm.setInputMode(false);
  
    //UIT Tests
    Vector testList = (Vector) request.getAttribute("UIT_LIST");
    URL uitUrl = new URL(appUrl + "/report/CMnUitData");
    CMnUitStatusView uitStatusView = new CMnUitStatusView(uitUrl, testList);
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
  <!--                   	        UIT Status                              -->
  <!-- ==================================================================== -->
<%= uitStatusView.toString() %> 
  <p>




<%@ include file="../footer.jsp" %>

</body>
</html>
