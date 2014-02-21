<%@ include file="../common.jsp" %>
<%@ page import="com.modeln.testfw.reporting.*"%>
<%@ page import="com.modeln.build.ctrl.forms.*"%>
<%
    selectedTab = PATCH_TAB;

    // Contruct the form URLs
    URL formSubmitUrl = new URL(appUrl + "/patch/CMnPatchBugs");
    URL formImageUrl = new URL(imgUrl);
    URL buildUrl = new URL(appUrl + "/report/CMnBuildData");
    URL patchUrl = new URL(appUrl + "/patch/CMnPatchRequest");
    URL deleteUrl = new URL(appUrl + "/patch/CMnPatchDelete");

    Vector patches = (Vector) request.getAttribute(IMnPatchForm.PATCH_LIST_DATA);
    Vector customers = (Vector) request.getAttribute(IMnPatchForm.CUSTOMER_LIST_DATA);

    //
    // Construct the request form and populate it with data
    //
    CMnPatchBugForm form = new CMnPatchBugForm(formSubmitUrl, formImageUrl, patches);
    form.setExternalUrls(urls);
    form.setBuildUrl(buildUrl);
    form.setPatchUrl(patchUrl);
    form.setDeleteUrl(deleteUrl);
    form.setCustomers(customers);

    // Set form attributes once the data is available
    form.setPostEnabled(true);
    form.setAdminMode(admin);
    form.setInputMode(true);
    // Set the selected values from the request parameters
    form.setValues(request);



%>
<html>
<head>
  <title>Patch List</title>
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
      <h3>Patch List</h3><br>

<%
    try {
%>
            <p><%= form.toString() %></p>
<%
    } catch (Exception fixex) {
        out.println("JSP Exception: " + fixex);
        out.println("<pre>\n");
        StackTraceElement[] lines = fixex.getStackTrace();
        for (int idx = 0; idx < lines.length; idx++) {
            out.println(lines[idx] + "\n");
        }
        out.println("</pre>\n");
    }
%>


<%@ include file="../footer.jsp" %>

      </td>
    </tr>
  </table>

</body>
</html>
