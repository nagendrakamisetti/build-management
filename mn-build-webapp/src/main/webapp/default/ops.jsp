<%@ include file="common.jsp" %>
<%@ page import="com.modeln.build.ctrl.CMnControlApp" %>
<%@ page import="com.modeln.build.ctrl.forms.CMnBaseForm" %>
<%@ page import="com.modeln.build.ctrl.forms.IMnUserForm" %>
<%@ page import="java.lang.management.*" %>
<%@ page import="com.modeln.build.web.application.SessionTracker" %>
<%@ page import="com.modeln.build.web.data.SessionActivity" %>
<%@ page import="com.modeln.build.web.database.*" %>
<%
    selectedTab = ADMIN_TAB;

    URL formOpsUrl = new URL(appUrl + "/CMnOps");

    // Get the JVM information
    MemoryMXBean memBean = ManagementFactory.getMemoryMXBean();
    MemoryUsage heap = memBean.getHeapMemoryUsage();
    MemoryUsage noheap = memBean.getNonHeapMemoryUsage();

    // Get the application information
    CMnControlApp app = (CMnControlApp) request.getAttribute("APP");
    Hashtable<DataRepository, Integer> repoSize = null;
    Vector<String> logFiles = null;
    Hashtable<String, DataRepository> repoList = null;
    Vector<RepositoryConnection> invalidConnections = null;
    if (app != null) {
        invalidConnections = app.getInvalidConnections();
        logFiles = app.getLogFiles();
        repoList = app.getRepositoryList();
        repoSize = app.getRepositorySize();
    }

    Hashtable<String, SessionActivity> activeSessions = SessionTracker.getActiveSessions();
%>
<html>
<head>
  <title>Ops Page</title>
  <%@ include file="stylesheet.html" %>
</head>

<body>

  <table border="0" width="100%">
    <tr>
      <td>
<%@ include file="header.jsp" %>
      </td>
    </tr>

    <tr>
      <td align="left">
        <h3>Request Information</h3>
<pre>
Remote Addr:  <%= request.getRemoteAddr() %>
Remote Host:  <%= request.getRemoteHost() %>
Server Name:  <%= request.getServerName() %>
Local  Addr:  <%= request.getLocalAddr() %>
</pre>
        <h3>Runtime Information</h3>
<pre>
  Heap  : <%= heap.toString() %>
Non-Heap: <%= noheap.toString() %>
</pre>

        <h3>Repository Information</h3>
<pre>
<%  
    if ((invalidConnections != null) && (invalidConnections.size() > 0)) {
        Enumeration list = invalidConnections.elements();
        while (list.hasMoreElements()) {
            RepositoryConnection rc = (RepositoryConnection) list.nextElement();
            out.println("Invalid: " + rc.toString()); 
        }
    } else {
        out.println("No invalid connections detected.");
    }

    if ((repoList != null) && (repoList.size() > 0)) {
        Enumeration keys = repoList.keys();
        out.println("");
        while (keys.hasMoreElements()) {
            String name = (String) keys.nextElement();
            DataRepository repo = (DataRepository) repoList.get(name);
            Integer size = (Integer) repoSize.get(repo);
            StringBuffer sb = new StringBuffer();
            sb.append("Repository: " + name + ", ");
            if (repo instanceof DatabaseRepository) {
                sb.append(((DatabaseRepository)repo).getUrl());
            } else if (repo instanceof JndiRepository) {
                sb.append(((JndiRepository)repo).getName());
            } else {
                sb.append(repo.getDescription());
            }
            if ((size != null) && (size > 0)) {
                sb.append(", " + size + " connections");
            }
            out.println(sb.toString());
        }
    }
%>
</pre>

        <h3>Log Information</h3>
<pre>
<%
    if (logFiles != null) {
        Enumeration list = logFiles.elements();
        while (list.hasMoreElements()) {
            out.println((String) list.nextElement());
        }
    }
%>
</pre>

        <h3>Session Information</h3>
<%
    if ((activeSessions != null) && (activeSessions.size() > 0)) {
        out.println("Session Count: " + activeSessions.size());
%>
<table border="1" cellspacing="0" cellpadding="2">
  <tr>
    <td>Remote Host</td>
    <td>User</td>
    <td>Session Start</td>
    <td>Idle Time</td>
<%
        Enumeration list = activeSessions.elements();
        while (list.hasMoreElements()) {
            SessionActivity activity = (SessionActivity) list.nextElement();
            Date startDate = new Date(activity.getCreationTime());
            Date now = new Date();
            long idleTime = now.getTime() - activity.getLastAccessedTime();
%>
  <tr>
    <td><%= activity.getOrigin() %></td>
<%          if (activity.getUid() != null) { %>
    <td><a href="<%= appUrl %>/CMnUser?<%= IMnUserForm.USER_ID_LABEL %>=<%= activity.getUid() %>"><%= activity.getUsername() %></a></td> 
<%          } else { %>
    <td>&nbsp;</td>
<%          } %>
    <td><%= startDate %></td>
    <td align="right"><%= CMnBaseForm.formatTime(idleTime) %></td> 
  </tr>
<%
        }
        out.println("</table>");
    }
%>


        <h3>Global Alert</h3>
<form action="<%= formOpsUrl %>" method="post">
<table border="0" width="60%">
  <tr>
    <td>
      Specify an alert message that will displayed on 
      the header of each page.  Used to alert users of
      system outages or other important events.  The
      message remains in-memory and will disappear once
      the app is restarted.
    </td>
  </tr>
  <tr>
    <td valign="top">
      <textarea rows="4" cols="60" name="alertmsg"><% if (alertMessage != null) { %><%= alertMessage %><% } %></textarea>
    </td>
  </tr>
  <tr>
    <td valign="top">
      <input type="submit" name="alertsave"  value="Set Alert"/>
      <input type="submit" name="alertclear" value="Clear Alert"/>
    </td>
  </tr>
</table>
</form>


<%@ include file="footer.jsp" %>

      </td>
    </tr>
  </table>

</body>
</html>
