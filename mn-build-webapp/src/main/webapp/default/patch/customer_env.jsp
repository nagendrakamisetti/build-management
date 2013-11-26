<%@ include file="../common.jsp" %>
<%@ page import="com.modeln.testfw.reporting.*"%>
<%@ page import="com.modeln.build.common.data.account.*"%>
<%@ page import="com.modeln.build.ctrl.forms.*"%>
<%
    selectedTab = PATCH_TAB;

    // Contruct the form URLs
    URL formSubmitUrl = new URL(appUrl + "/patch/CMnCustomerEnv");
    URL formDeleteUrl = new URL(appUrl + "/patch/CMnCustomerEnvDelete");
    URL formCustUrl = new URL(appUrl + "/patch/CMnCustomerData");
    URL formBuildUrl = new URL(appUrl + "/report/CMnBuildData");
    URL formImageUrl = new URL(imgUrl);


    //
    // Construct the request form and populate it with data
    //
    CMnCustomerEnvForm form = new CMnCustomerEnvForm(formSubmitUrl, formImageUrl);
    form.setDeleteUrl(formDeleteUrl);
    form.setBuildUrl(formBuildUrl);
    form.setInputMode(true);
    form.setValues(request);

    String custName = "Customer";
    if (form.getCustomerName() != null) {
        custName = form.getCustomerName();
    }
%>
<html>
<head>
  <title><%= custName %> Environment</title>
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
      <td>
        <h3><%= custName %> Environment</h3>
<%= form.toString() %>
      </td>
    </tr>
  </table>

</body>
</html>

