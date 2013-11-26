<%@ include file="../common.jsp" %>
<%@ page import="java.util.Vector"%>
<%@ page import="com.modeln.testfw.reporting.*"%>
<%@ page import="com.modeln.build.ctrl.forms.CMnBuildDataForm"%>
<%@ page import="com.modeln.build.ctrl.forms.CMnLogDataForm"%>
<%
    selectedTab = BUILD_TAB;

    // Contruct the form URLs
    URL formSummaryUrl = new URL(appUrl + "/report/CMnBuildSummary");
    URL formSearchUrl = new URL(appUrl + "/report/CMnBuildList");
    URL formBuildUrl = new URL(appUrl + "/report/CMnBuildData");
    URL formImageUrl = new URL(imgUrl);
    URL formDownloadUrl= new URL(downloadUrl);

    CMnBuildDataForm buildForm = new CMnBuildDataForm(formBuildUrl, formImageUrl);
    buildForm.setSummaryUrl(formSummaryUrl);
    buildForm.setSearchUrl(formSearchUrl);
    buildForm.setVersionUrl(formBuildUrl);
    buildForm.setDownloadUrl(formDownloadUrl);
    buildForm.setValues(request);
    buildForm.setAdminMode(admin);
    buildForm.setInputMode(false);
    buildForm.setRelatedLinks(!disableHeader);

    CMnDbLogData log = (CMnDbLogData) request.getAttribute(CMnLogDataForm.LOG_OBJECT_LABEL);
    String body = null;
    if (log != null) {
        body = log.getText();
        if (body == null) {
            body = "Empty log file.";
        }

        // Format the log content appropriately
        if (log.getFormat() == CMnDbLogData.HTML_FORMAT) {
            // Strip out the head and body tags so we can add a header and footer
        } else {
            body = "<pre>\n" + body + "\n</pre>\n";
        }
    } else {
        body = "No log information available.";
    }

%>

<html>
<head>
  <title>Build Report</title>
  <%@ include file="../stylesheet.html" %>
</head>

<body>
  <%@ include file="../header.jsp" %>

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

  <%= body %>
  <%@ include file="../footer.jsp" %>
</body>
</html>
