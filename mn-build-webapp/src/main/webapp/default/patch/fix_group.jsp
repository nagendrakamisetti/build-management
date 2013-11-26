<%@ include file="../common.jsp" %>
<%@ page import="com.modeln.testfw.reporting.*"%>
<%@ page import="com.modeln.build.common.data.product.*"%>
<%@ page import="com.modeln.build.ctrl.forms.*"%>
<%
    selectedTab = PATCH_TAB;

    // Contruct the form URLs
    URL formSubmitUrl = new URL(appUrl + "/patch/CMnFixGroupData");
    URL formImageUrl = new URL(imgUrl);
    URL formAddFixUrl = new URL(appUrl + "/patch/CMnFixGroupAdd");

    CMnPatchGroup group = (CMnPatchGroup) request.getAttribute(IMnPatchForm.FIX_GROUP_DATA);

    //
    // Construct the request form and populate it with data
    //
    CMnFixGroupForm form = new CMnFixGroupForm(formSubmitUrl, formImageUrl, formAddFixUrl);
    form.setExternalUrls(urls);

    // Set form attributes once the data is available
    form.setPostEnabled(true);
    form.setAdminMode(admin);
    //form.setInputMode(admin);
    form.setValues(group);



%>
<html>
<head>
  <title>Group</title>
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
      <h3>Group</h3><br>

<%= form.toString() %>

<%@ include file="../footer.jsp" %>

      </td>
    </tr>
  </table>

</body>
</html>
