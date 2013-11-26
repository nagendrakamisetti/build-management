<%@ include file="../common.jsp" %>
<%@ page import="com.modeln.testfw.reporting.*"%>
<%@ page import="com.modeln.build.ctrl.forms.*"%>
<%
    selectedTab = PATCH_TAB;

    // Contruct the form URLs
    URL formSubmitUrl = new URL(appUrl + "/patch/CMnPatchList");
    URL formImageUrl = new URL(imgUrl);
    URL buildUrl = new URL(appUrl + "/report/CMnBuildData");
    URL patchUrl = new URL(appUrl + "/patch/CMnPatchRequest");
    URL deleteUrl = new URL(appUrl + "/patch/CMnPatchDelete");

    Vector patches = (Vector) request.getAttribute(IMnPatchForm.PATCH_LIST_DATA);
    Vector customers = (Vector) request.getAttribute(IMnPatchForm.CUSTOMER_LIST_DATA);

    //
    // Construct the request form and populate it with data
    //
    CMnPatchListForm form = new CMnPatchListForm(formSubmitUrl, formImageUrl, patches);
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

<%= form.toString() %>

<%@ include file="../footer.jsp" %>

      </td>
    </tr>
  </table>

</body>
</html>
