<%@ include file="../common.jsp" %>
<%@ page import="java.util.Vector"%>
<%@ page import="com.modeln.testfw.reporting.*"%>
<%@ page import="com.modeln.build.ctrl.forms.CMnBuildDataForm"%>
<%@ page import="com.modeln.build.ctrl.forms.CMnLogDataForm"%>
<%@ page import="com.modeln.build.ctrl.forms.CMnBuildEventForm"%>
<%
    selectedTab = BUILD_TAB;

    // Contruct the form URLs
    URL formSummaryUrl = new URL(appUrl + "/report/CMnBuildSummary");
    URL formSearchUrl = new URL(appUrl + "/report/CMnBuildList");
    URL formBuildUrl = new URL(appUrl + "/report/CMnBuildData");
    URL formEventUrl = new URL(appUrl + "/report/CMnBuildEvents");
    URL formImageUrl = new URL(imgUrl);
    URL formDownloadUrl = new URL(downloadUrl);

    // Construct the build information to be presented
    CMnBuildDataForm buildForm = new CMnBuildDataForm(formBuildUrl, formImageUrl);
    buildForm.setSummaryUrl(formSummaryUrl);
    buildForm.setSearchUrl(formSearchUrl);
    buildForm.setDownloadUrl(formDownloadUrl);
    buildForm.setValues(request);
    buildForm.setAdminMode(admin);
    buildForm.setInputMode(false);
    buildForm.setRelatedLinks(!disableHeader);
    CMnDbBuildData build = buildForm.getValues();
    CMnDbHostData host = build.getHostData();

    // Construct the build event information to be presented
    CMnBuildEventForm eventForm = new CMnBuildEventForm(formEventUrl, formImageUrl);
    eventForm.setValues(request);
    eventForm.setAdminMode(admin);
    eventForm.setInputMode(input);

%>
<html>
<head>
  <title>Build Report</title>
  <script language="JavaScript">
    <%@ include file="report.js" %>
  </script>
  <%@ include file="../stylesheet.html" %>
</head>

<body>
  <%@ include file="../header.jsp" %>

  <!-- ==================================================================== -->
  <!--                                Build                                 -->
  <!-- ==================================================================== -->
  <table border="0" cellspacing="0" cellpadding="1" width="100%">
    <tr>
      <td bgcolor="#000000">
        <table border="0" cellspacing="0" cellpadding="2" width="100%" bgcolor="#CCCCCC">
          <tr>
            <td><b>Build Information</b></td>
          </tr>
        </table>
      </td>
    </tr>
    <tr>
      <td bgcolor="#000000">
<%= buildForm.toString() %>
      </td>
    </tr>
  </table>
  <p>


<%  // Conditionally display the list of build events 
    String eventFormStr = eventForm.toString();
    if ((eventFormStr != null) && (eventFormStr.length() > 0)) {
%>
  <!-- ==================================================================== -->
  <!--                           Event Summary                              -->
  <!-- ==================================================================== -->
  <table border="0" cellspacing="0" cellpadding="1" width="100%">
    <tr>
      <td bgcolor="#000000">
        <table border="0" cellspacing="0" cellpadding="2" width="100%" bgcolor="#CCCCCC">
          <tr>
            <td><b>Partial Event Log</b> (<a href="<%= formEventUrl.toString() %>?<%= CMnBuildEventForm.BUILD_ID_LABEL %>=<%= build.getId() %>">full log</a>)</td>
          </tr>
        </table>
      </td>
    </tr>
    <tr>
      <td bgcolor="#FFFFFF">
        <%= eventFormStr %>
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
