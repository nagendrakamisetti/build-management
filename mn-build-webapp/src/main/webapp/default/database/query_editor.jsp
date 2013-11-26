<%@ include file="../common.jsp" %>
<%@ page import="com.modeln.build.ctrl.forms.CMnDatabaseQueryForm"%>
<%@ page import="com.modeln.build.ctrl.forms.CMnDatabaseQueryHistoryForm"%>
<%@ page import="com.modeln.build.common.data.database.CMnQueryData"%>
<%
    selectedTab = DB_TAB;

    URL formQueryUrl  = new URL(appUrl + "/database/CMnDatabaseQuery");
    URL formImageUrl  = new URL(imgUrl);

    // Construct the build information to be presented
    CMnDatabaseQueryForm dbForm = new CMnDatabaseQueryForm(formQueryUrl, formImageUrl);
    CMnDatabaseQueryHistoryForm historyForm = new CMnDatabaseQueryHistoryForm(formQueryUrl, formImageUrl);

    dbForm.setValues(request);
    dbForm.setAdminMode(admin);
    dbForm.setInputMode(true);

    historyForm.setValues(request);
    historyForm.setAdminMode(admin);
    historyForm.setInputMode(true);

    // Obtain the form data
    CMnQueryData data = dbForm.getValues();

%>
<html>
<head>
  <title>Database Query Editor</title>
  <%@ include file="../stylesheet.html" %>
</head>

<body>

  <table border="0" width="100%">
    <tr>
      <td>
<%@ include file="../header.jsp" %>
      </td>
    </tr>

    <tr>
      <td align="left">
      <h3>Database Query Editor</h3><br>
      <table border="0" cellspacing="2" cellpadding="2" width="100%">
        <tr>
          <td valign="top">
<%= dbForm.getTitledBorder(dbForm.toString()) %>
          </td>
          <td valign="top">
<%= historyForm.getTitledBorder(historyForm.toString()) %>
          </td>
        </tr>
      </table>
      <p>   

      <!-- ================================================================ -->
      <!--                       Query Messages                             --> 
      <!-- ================================================================ -->
<%
    if (data.getMessage() != null) {
%><center><%= data.getMessage() %></center><p><%
    }
%>


      <!-- ================================================================ -->
      <!--                       Query Results                              -->
      <!-- ================================================================ -->
<center>
<%= dbForm.getResults() %>
</center>
<p>


<%@ include file="../footer.jsp" %>

      </td>
    </tr>
  </table>

</body>
</html>
