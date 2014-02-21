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

<%
    if (customers != null) {
%>
      <table class="spreadsheet">
        <tr class="spreadsheet-header">
          <td>Full Name</td>
          <td>Short Name</td>
          <td>Branch Type</td>
          <td>Environments</td>
        </tr>
<%
        Enumeration custList = customers.elements();
        while (custList.hasMoreElements()) {
            CMnAccount cust = (CMnAccount) custList.nextElement();
            out.println("<tr class=\"spreadsheet-shaded\">");
            out.println("  <td><a href=\"" + formCustomerUrl + "?" + IMnPatchForm.CUSTOMER_ID_LABEL + "=" + cust.getId() + "\">" + cust.getName() + "</a></td>");
            out.println("  <td>" + cust.getShortName() + "</td>");
            out.println("  <td class=\"" + cust.getBranchType() + "\">" + cust.getBranchType() + "</td>");
            if (cust.getEnvironments() != null) {
                out.println("  <td>");
                Iterator envList = cust.getEnvironments().iterator();
                while (envList.hasNext()) {
                    CMnEnvironment env = (CMnEnvironment) envList.next();
                    out.print(env.getName());
                    if (envList.hasNext()) {
                        out.print(",\n");
                    } else {
                        out.print("\n");
                    }
                }
                out.println("  </td>");
            } else {
                out.println("  <td>&nbsp;</td>");
            }
            out.println("</tr>");
        }
%>
        <tr class="spreadsheet-footer">
          <td colspan=4"><a href="<%= formCustomerUrl %>">Add Customer</a></td>
        </tr>
      </table>
<%
    } else {
        out.println("<a href=\"" + formCustomerUrl + "\">Add Customer</a>");
    }
%>

<%@ include file="../footer.jsp" %>

      </td>
    </tr>
  </table>

</body>
</html>
