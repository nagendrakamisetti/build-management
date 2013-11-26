<%@ include file="../common.jsp" %>
<%@ page import="com.modeln.testfw.reporting.*"%>
<%@ page import="com.modeln.build.common.data.account.*"%>
<%@ page import="com.modeln.build.ctrl.forms.*"%>
<%
    selectedTab = PATCH_TAB;

    // Contruct the form URLs
    URL formSubmitUrl = new URL(appUrl + "/patch/CMnCustomerData");
    URL formDeleteUrl = new URL(appUrl + "/patch/CMnCustomerDelete");
    URL formEnvUrl = new URL(appUrl + "/patch/CMnCustomerEnv");
    URL formBuildUrl = new URL(appUrl + "/report/CMnBuildData");
    URL formImageUrl = new URL(imgUrl);

    CMnAccount customer = (CMnAccount) request.getAttribute(IMnPatchForm.CUSTOMER_DATA);

    //
    // Construct the request form and populate it with data
    //
    CMnCustomerDataForm form = new CMnCustomerDataForm(formSubmitUrl, formImageUrl);
    form.setEnvUrl(formEnvUrl);
    form.setDeleteUrl(formDeleteUrl);
    form.setBuildUrl(formBuildUrl);
    form.setInputMode(true);
    if (customer != null) {
        form.setValues(customer);
    }
%>
<html>
<head>
  <title>Customer Data</title>
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
<%= form.toString() %>
      </td>
    </tr>
  </table>

</body>
</html>

