<%@ include file="../common.jsp" %>
<%@ page import="com.modeln.testfw.reporting.*"%>
<%@ page import="com.modeln.build.common.data.account.*"%>
<%@ page import="com.modeln.build.ctrl.forms.*"%>
<%
    selectedTab = PATCH_TAB;

    // Contruct the form URLs
    URL formSubmitUrl = new URL(appUrl + "/patch/CMnCustomerList");
    URL formCustomerUrl = new URL(appUrl + "/patch/CMnCustomerData");
    URL formImageUrl = new URL(imgUrl);

    Vector<CMnAccount> customers = (Vector) request.getAttribute(IMnPatchForm.CUSTOMER_LIST_DATA);

%>
<html>
<head>
  <title>Customer List</title>
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
      <h3>Customer List</h3>

      <ul>
<%
    Enumeration custList = customers.elements();
    while (custList.hasMoreElements()) {
        CMnAccount cust = (CMnAccount) custList.nextElement();
        out.println("<li><a href=\"" + formCustomerUrl + "?" + IMnPatchForm.CUSTOMER_ID_LABEL + "=" + cust.getId() + "\">" + cust.getName() + "</a></li>");
    }
%>
      </ul>

<a href="<%= formCustomerUrl %>">Add Customer</a>

<%@ include file="../footer.jsp" %>

      </td>
    </tr>
  </table>

</body>
</html>
