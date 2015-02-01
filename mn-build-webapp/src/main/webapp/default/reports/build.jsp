<%@ include file="../common.jsp" %>
<%@ page import="java.text.NumberFormat"%>
<%@ page import="com.modeln.testfw.reporting.*"%>
<%@ page import="com.modeln.build.ctrl.forms.CMnBaseSuiteForm"%>
<%@ page import="com.modeln.build.ctrl.forms.CMnBuildDataForm"%>
<%@ page import="com.modeln.build.ctrl.forms.CMnBuildHostForm"%>
<%@ page import="com.modeln.build.ctrl.forms.CMnBuildStatusForm"%>
<%@ page import="com.modeln.build.ctrl.forms.CMnLogDataForm"%>
<%@ page import="com.modeln.build.ctrl.forms.CMnUnittestSuiteForm"%>
<%@ page import="com.modeln.build.ctrl.forms.CMnUitSuiteForm"%>
<%@ page import="com.modeln.build.ctrl.forms.CMnFlexSuiteForm"%>
<%@ page import="com.modeln.build.ctrl.forms.CMnActSuiteForm"%>
<%@ page import="com.modeln.build.ctrl.forms.CMnBuildEventForm"%>
<%@ page import="com.modeln.build.ctrl.forms.CMnTestSuiteGroup"%>
<%
    selectedTab = BUILD_TAB;

    // Contruct the form URLs
    URL formSummaryUrl = new URL(appUrl + "/report/CMnBuildSummary");
    URL formSearchUrl  = new URL(appUrl + "/report/CMnBuildList");
    URL formBuildUrl   = new URL(appUrl + "/report/CMnBuildData");
    URL formNotesUrl   = new URL(appUrl + "/report/CMnBuildStatusNotes");
    URL formEventUrl   = new URL(appUrl + "/report/CMnBuildEvents");
    URL formChartUrl   = new URL(appUrl + "/chart/CMnShowBuildChart");
    URL formImageUrl   = new URL(imgUrl);
    URL formDownloadUrl= new URL(downloadUrl);

    // Construct the build information to be presented
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
    CMnDbHostData host = build.getHostData();

    // Construct a URL for reviewing the build
    URL reviewUrl  = new URL(appUrl + "/report/CMnBuildReview?" + CMnBuildDataForm.BUILD_ID_LABEL + "=" + build.getId());

    // Construct the release information
    CMnBuildStatusForm statusForm = null;
    if ((build.getStatus() != null) || admin || input) {
        statusForm = new CMnBuildStatusForm(formBuildUrl, formImageUrl);
        statusForm.setNotesUrl(formNotesUrl);
        if (build.getStatus() != null) {
            statusForm.setValues(build.getStatus());
        } else {
            statusForm.setValues(new CMnDbBuildStatusData(build.getId()));
        }
        statusForm.setAdminMode(admin);
        statusForm.setInputMode(input);
    }

    // Obtain a list of product areas
    Vector areas = (Vector) request.getAttribute("PRODUCT_AREAS");

    // Construct the unit test information to be presented
    Vector suites = (Vector) request.getAttribute("SUITE_LIST");
    URL formSuiteUrl = new URL(appUrl + "/report/CMnSuiteData");
    URL formDeleteUnittestSuiteUrl = new URL(appUrl + "/report/CMnDeleteSuite");
    CMnUnittestSuiteForm suiteForm = new CMnUnittestSuiteForm(formSuiteUrl, formImageUrl, suites);
    suiteForm.setValues(request);
    suiteForm.setAdminMode(admin);
    suiteForm.setInputMode(false);
    suiteForm.setDeleteUrl(formDeleteUnittestSuiteUrl);
    suiteForm.enableAllColumns(!disableHeader);
    int suiteTotal = suiteForm.getTestCount();
    int suitePassing = suiteForm.getPassingCount();
    int suiteFailures = suiteForm.getFailingCount();

    // Construct the ACT information to be presented
    Vector actSuiteList = (Vector) request.getAttribute("ACT_SUITE_LIST");
    URL formActSuiteUrl = new URL(appUrl + "/report/CMnActSuiteData");
    URL formDeleteActSuiteUrl = new URL(appUrl + "/report/CMnActSuiteDelete");
    CMnActSuiteForm actSuiteForm = new CMnActSuiteForm(formActSuiteUrl, formImageUrl, actSuiteList);
    actSuiteForm.setValues(request);
    actSuiteForm.setAdminMode(admin);
    actSuiteForm.setInputMode(false);
    actSuiteForm.setDeleteUrl(formDeleteActSuiteUrl);
    actSuiteForm.enableAllColumns(!disableHeader);
    int actTotal = actSuiteForm.getTestCount();
    int actPassing = actSuiteForm.getPassingCount();
    int actFailures = actSuiteForm.getFailingCount();

    // Construct the Flex information to be presented
    Vector flexSuiteList = (Vector) request.getAttribute("FLEX_SUITE_LIST");
    URL formFlexSuiteUrl = new URL(appUrl + "/report/CMnFlexSuiteData");
    URL formDeleteFlexSuiteUrl = new URL(appUrl + "/report/CMnFlexSuiteDelete");
    CMnFlexSuiteForm flexSuiteForm = new CMnFlexSuiteForm(formFlexSuiteUrl, formImageUrl, flexSuiteList);
    flexSuiteForm.setValues(request);
    flexSuiteForm.setAdminMode(admin);
    flexSuiteForm.setInputMode(false);
    flexSuiteForm.setDeleteUrl(formDeleteFlexSuiteUrl);
    flexSuiteForm.enableAllColumns(!disableHeader);
    int flexTotal = flexSuiteForm.getTestCount();
    int flexPassing = flexSuiteForm.getPassingCount();
    int flexFailures = flexSuiteForm.getFailingCount();

    // Construct the UIT information to be presented
    Vector uitSuiteList = (Vector) request.getAttribute("UIT_SUITE_LIST");
    URL formUitSuiteUrl = new URL(appUrl + "/report/CMnUitSuiteData");
    URL formDeleteUitSuiteUrl = new URL(appUrl + "/report/CMnUitSuiteDelete");
    CMnUitSuiteForm uitSuiteForm = new CMnUitSuiteForm(formUitSuiteUrl, formImageUrl, uitSuiteList);
    uitSuiteForm.setValues(request);
    uitSuiteForm.setAdminMode(admin);
    uitSuiteForm.setInputMode(false);
    uitSuiteForm.setDeleteUrl(formDeleteUitSuiteUrl);
    uitSuiteForm.enableAllColumns(!disableHeader);
    int uitTotal = uitSuiteForm.getTestCount();
    int uitPassing = uitSuiteForm.getPassingCount();
    int uitFailures = uitSuiteForm.getFailingCount();

    // Construct the build metrics to be presented
    Vector buildList = new Vector();
    buildList.add(build);
    CMnBuildHostForm metricsForm = new CMnBuildHostForm(formBuildUrl, formImageUrl, buildList);
    metricsForm.setInputMode(false);

    int totalTests = suiteTotal + actTotal + flexTotal + uitTotal;
    int totalPass = suitePassing + actPassing + flexPassing + uitPassing;
    int totalFailures = suiteFailures + actFailures + flexFailures + uitFailures;
    String title = null;
    if (totalFailures != 0) {
        float percent = ((float)totalPass / (float)(totalPass + totalFailures)) * 100;
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(1);
        title = buildForm.getVersionNumber() + " " + buildForm.getProductAbreviation() + " " + numberFormat.format(percent) + "% pass : " + build.getReleaseId() + " Build";
    } else {
        title = buildForm.getVersionNumber() + " " + buildForm.getProductAbreviation() + " success : " + build.getReleaseId() + " Build";
    }

    // Construct the build event information to be presented
    CMnBuildEventForm eventForm = new CMnBuildEventForm(formEventUrl, formImageUrl);
    eventForm.setValues(request);
    eventForm.setAdminMode(admin);
    eventForm.setInputMode(false);

    // Determine if it is possible to group by name or area
    boolean hasValidAreas = true;
    Enumeration suiteList = suites.elements();
    while (suiteList.hasMoreElements()) {
        CMnDbTestSuite suite = (CMnDbTestSuite) suiteList.nextElement();
        if (suite.getGroupName() == null) {
            hasValidAreas = false;
        }
    }

    // Use a temporary URL parameter to enable the suite collapse functionality
    String groupBy = (String) request.getParameter(CMnTestSuiteGroup.FORM_GROUP_LABEL);
    CMnBaseSuiteForm allSuitesForm = null; 
    if (groupBy != null) {
        if (groupBy.equals("id")) {
            suiteForm.setCollapseCriteria(CMnTestSuiteGroup.COLLAPSE_BY_GID);
            actSuiteForm.setCollapseCriteria(CMnTestSuiteGroup.COLLAPSE_BY_GID);
        } else if (groupBy.equals("name") && hasValidAreas) {
            suiteForm.setCollapseCriteria(CMnTestSuiteGroup.COLLAPSE_BY_NAME);
            suiteForm.enableTimeColumn(true);
            actSuiteForm.setCollapseCriteria(CMnTestSuiteGroup.COLLAPSE_BY_NAME);
            actSuiteForm.enableTimeColumn(true);
        } else if (groupBy.equals("area") && hasValidAreas) {
            // Construct a form object which contains all of the test suites
            Vector<CMnDbTestSuite> allSuitesList = new Vector();
            allSuitesList.addAll(suites);
            allSuitesList.addAll(actSuiteList);
            allSuitesForm = new CMnUnittestSuiteForm(formBuildUrl, formImageUrl, allSuitesList);
            allSuitesForm.setCollapseCriteria(CMnTestSuiteGroup.COLLAPSE_BY_NAME);
            allSuitesForm.setProductOwners(areas);
            allSuitesForm.enableAllColumns(!disableHeader);
            allSuitesForm.enableTimeColumn(true);
        } else {
            suiteForm.setCollapseCriteria(CMnTestSuiteGroup.COLLAPSE_BY_NONE);
            actSuiteForm.setCollapseCriteria(CMnTestSuiteGroup.COLLAPSE_BY_NONE);
        }
    }

%>
<html>
<head>
  <title><%= title %></title>
  <script language="JavaScript">
    <%@ include file="report.js" %>
  </script>
  <%@ include file="../stylesheet.html" %>
</head>

<body>
  <%@ include file="../header.jsp" %>

  <!-- ==================================================================== -->
  <!--                           Build Summary                              -->
  <!-- ==================================================================== -->
<%  // Display build information 
    // if disableHeader, then display buildForm.getVersionString()
    if (disableHeader) {
        out.println(buildForm.getVersionString());
    } else {
        out.println(buildForm.getTitledBorder(buildForm.toString()));
    }
%>
<p>

<%  // Display build status if available
    if (statusForm != null) {
        out.println(statusForm.getTitledBorder(statusForm.toString()) + "<p>\n");
    }
%>


<%  // Conditionally display the list of build events 
    String eventFormStr = eventForm.getSummary();
    if ((eventFormStr != null) && (eventFormStr.length() > 0)) {
if (disableHeader) {
    out.println(eventFormStr);
} else {
            out.println(eventForm.getTitledBorder(eventFormStr));
        }
    }
%>
<p>


  <!-- ==================================================================== -->
  <!--                            ACT Suites                                -->
  <!-- ==================================================================== -->
<%  
    if (allSuitesForm != null) {
        String allSuitesStr = allSuitesForm.toString();
        if ((allSuitesStr != null) && (allSuitesStr.length() > 0)) {
            out.println(allSuitesForm.getTitledBorderLink("Test Results", allSuitesStr, reviewUrl, "Review"));
        }
    } else {
        String actFormStr = actSuiteForm.toString();
        if ((actFormStr != null) && (actFormStr.length() > 0)) {
            out.println(actSuiteForm.getTitledBorderLink("ACT Test Results", actFormStr, reviewUrl, "Review"));
            out.println("<p>\n");
        }
        String unittestFormStr = suiteForm.toString();
        if ((unittestFormStr != null) && (unittestFormStr.length() > 0)) {
            out.println(suiteForm.getTitledBorderLink("Unit Test Results", unittestFormStr, reviewUrl, "Review")); 
            out.println("<p>\n");
        }
    }
%>


  <!-- ==================================================================== -->
  <!--                           Flex Suites                                -->
  <!-- ==================================================================== -->
<%  // Conditionally display the list of flex test suites
    String flexFormStr = flexSuiteForm.toString();
    if ((flexFormStr != null) && (flexFormStr.length() > 0)) {
%><%= flexSuiteForm.getTitledBorderLink("Flex Test Resuls", flexFormStr, reviewUrl, "Review") %><%
    }
%>
  <p>


  <!-- ==================================================================== -->
  <!--                           UIT Suites                                 -->
  <!-- ==================================================================== -->
<%  // Conditionally display the list of unit test suites
    String uitFormStr = uitSuiteForm.toString();
    if ((uitFormStr != null) && (uitFormStr.length() > 0)) {
%><%= uitSuiteForm.getTitledBorderLink("QTP Test Results", uitFormStr, reviewUrl, "Review") %><%
    }
%>
  <p>


  <!-- ==================================================================== -->
  <!--                           Build Metrics                              -->
  <!-- ==================================================================== -->
<%  // Display build information 
    String metricsFormStr = metricsForm.toString();
    if ((disableHeader == false) && (metricsFormStr != null) && (metricsFormStr.length() > 0)) {
%>
  <table border="0" width="100%">
    <tr>
      <td width="50%">
        <%= metricsForm.getTitledBorder(metricsFormStr) %>
      </td>
      <td width="50%">
        <img src="<%=formChartUrl.toString()%>?<%=CMnBuildDataForm.BUILD_ID_LABEL%>=<%=build.getId()%>&chart=metrics&height=200&width=300"/>
      </td>
    </tr>
  </table>
  <p>
<%  } %>


  <!-- ==================================================================== -->
  <!--                                Logs                                  -->
  <!-- ==================================================================== -->
<%
    Vector logs = build.getLogs();
    if ((logs != null) && (logs.size() > 0)) {
%>
  <table border="0" cellspacing="0" cellpadding="1" width="100%">
    <tr><td colspan="1"><h2>Build Logs</h2></td></tr>
    <tr>
      <td>
<%
        for (int idx = 0; idx < logs.size(); idx++) {
            CMnDbLogData current = (CMnDbLogData) logs.get(idx);
%>
        <a href="<%=appUrl%>/report/CMnLogData?<%=CMnBuildDataForm.BUILD_ID_LABEL%>=<%=build.getId()%>&<%=CMnLogDataForm.LOG_ID_LABEL%>=<%=current.getId()%>"><%=current.getName()%></a><br>
<%      } // end for loop %>
      </td>
    </tr>
  </table>
<%  } // end if %>


<%@ include file="../footer.jsp" %>

</body>
</html>
