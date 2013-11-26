<%@ include file="../common.jsp" %>
<%@ page import="com.modeln.testfw.reporting.*"%>
<%@ page import="com.modeln.build.ctrl.forms.*"%>
<%
    selectedTab = PATCH_TAB;

    // Contruct the form URLs
    URL formSubmitUrl = new URL(appUrl + "/patch/CMnFixGroupList");
    URL formImageUrl = new URL(imgUrl);
    URL groupUrl = new URL(appUrl + "/patch/CMnFixGroupData");
    URL deleteUrl = new URL(appUrl + "/patch/CMnFixGroupDelete");

    Vector groups = (Vector) request.getAttribute(IMnPatchForm.FIX_GROUP_LIST_DATA);

    //
    // Construct the request form and populate it with data
    //
    CMnFixGroupListForm form = new CMnFixGroupListForm(formSubmitUrl, formImageUrl, groups);
    form.setGroupUrl(groupUrl);
    form.setDeleteUrl(deleteUrl);
    form.setExternalUrls(urls);

    // Set form attributes once the data is available
    form.setPostEnabled(true);
    form.setAdminMode(admin);
    form.setInputMode(true);
    // Set the selected values from the request parameters
    form.setValues(request);



%>
<html>
<head>
  <title>Group List</title>
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
      <h3>Group List</h3><br>

<%
        try {
            out.println(form.toString());
        } catch (Exception ex) {
            out.println("JSP Exception: " + ex);
            out.println("<pre>\n");
            StackTraceElement[] lines = ex.getStackTrace();
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
