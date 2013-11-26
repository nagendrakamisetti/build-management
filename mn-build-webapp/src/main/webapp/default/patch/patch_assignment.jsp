<%@ include file="../common.jsp" %>
<%@ page import="com.modeln.build.common.data.product.CMnPatch"%>
<%@ page import="com.modeln.build.common.data.product.CMnPatchOwner"%>
<%@ page import="com.modeln.build.jenkins.Job"%>
<%@ page import="com.modeln.testfw.reporting.*"%>
<%@ page import="com.modeln.build.ctrl.forms.*"%>
<%
    selectedTab = PATCH_TAB;

    // Contruct the form URLs
    URL formPatchUrl = new URL(appUrl + "/patch/CMnPatchRequest");
    URL formSubmitUrl = new URL(appUrl + "/patch/CMnPatchAssign");
    URL formImageUrl = new URL(imgUrl);

    CMnPatch patch = (CMnPatch) request.getAttribute(IMnPatchForm.PATCH_DATA);
    CMnPatchOwner owner = (CMnPatchOwner) request.getAttribute(CMnPatchOwnerForm.OWNER_DATA);
    Hashtable users = (Hashtable) request.getAttribute(CMnPatchOwnerForm.USER_LIST);

    String pageTitle = "Patch Assignment";
    

    //
    // Construct the patch ownership form
    //
    CMnPatchOwnerForm ownerForm = new CMnPatchOwnerForm(formSubmitUrl, formImageUrl, patch, users);
    ownerForm.setInputMode(true);
    ownerForm.setValues(owner);

    //
    // Construct the request form and populate it with data
    //
    CMnPatchRequestForm patchForm = new CMnPatchRequestForm(formPatchUrl, formImageUrl);
    patchForm.setPostEnabled(false);
    patchForm.setAdminMode(admin);
    patchForm.setInputMode(false);
    if (patch != null) {
        patchForm.setEnvironments(patch.getCustomer().getEnvironments());
        patchForm.setValues(patch);
    }
%>
<html>
<head>
  <title><%= pageTitle %></title>
  <%@ include file="../stylesheet.html" %>
  <%@ include file="../javascript.html" %>
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
      <h3><%= pageTitle %></h3><br>

      <!-- =============================================================== -->
      <!-- Patch information and status                                    -->
      <!-- =============================================================== -->
      <table border="0" cellspacing="0" cellpadding="5" width="45%">
        <tr>
          <td>
<%= patchForm.getTitledBorder("Patch Information", patchForm.getPatchSummaryTable()) %>
          </td>
        </tr>
        <tr>
          <td>
<%  // Display the owner information
    try {
        out.println(ownerForm.getTitledBorder("Patch Owner", ownerForm.toString()));
    } catch (Exception ownerex) {
        out.println("JSP Exception: " + ownerex);
        out.println("<pre>\n");
        StackTraceElement[] lines = ownerex.getStackTrace();
        for (int idx = 0; idx < lines.length; idx++) {
            out.println(lines[idx] + "\n");
        }
        out.println("</pre>\n");
    }
%>
          </td>
        </tr>
      </table>


<%@ include file="../footer.jsp" %>

      </td>
    </tr>
  </table>

</body>
</html>
