<%@ include file="../common.jsp" %>
<%@ page import="com.modeln.build.common.data.product.CMnPatch"%>
<%@ page import="com.modeln.build.common.enums.CMnServicePatch"%>
<%@ page import="com.modeln.build.jenkins.Job"%>
<%@ page import="com.modeln.testfw.reporting.*"%>
<%@ page import="com.modeln.build.ctrl.forms.*"%>
<%
    selectedTab = PATCH_TAB;

    // Contruct the form URLs
    URL formPatchUrl = new URL(appUrl + "/patch/CMnPatchRequest");
    URL formSubmitUrl = new URL(appUrl + "/patch/CMnUpdateStatus");
    URL formImageUrl = new URL(imgUrl);

    CMnPatch patch = (CMnPatch) request.getAttribute(IMnPatchForm.PATCH_DATA);
    String patchId = null;
    if (patch != null) {
        patchId = Integer.toString(patch.getId());
    }

    String pageTitle = "Patch Status";

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

    //
    // Construct the status display form
    //
    CMnPatchStatusForm statusForm = new CMnPatchStatusForm(formPatchUrl, formImageUrl);
    statusForm.setValues(patch);


    // Create a list of radio buttons for each status
    Hashtable buttons = new Hashtable();
    for (CMnServicePatch.RequestStatus status : CMnServicePatch.RequestStatus.values()) {
        String radio =
            "<input type=\"radio\"" +
            " name=\"" + IMnPatchForm.PATCH_STATUS_LABEL + "\"" +
            " value=\"" + status + "\">";
        buttons.put(status, radio);
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
<%= statusForm.getTitledBorder("Patch Status", statusForm.toString(), true) %>
          </td>
        </tr>
        <tr>
          <td>
            <form method="POST" action="<%= formSubmitUrl.toString() %>">
            <input type="hidden" name="<%= IMnPatchForm.PATCH_ID_LABEL %>" value="<%= patchId %>"/>
            <table border="0" cellpadding="1" cellspacing="1">
              <tr>
                <td align="right" width="3%"><%= buttons.get(CMnServicePatch.RequestStatus.SAVED) %></td>
                <td align="left"  width="22%"><%= CMnServicePatch.RequestStatus.SAVED %></td>

                <td align="right" width="3%"><%= buttons.get(CMnServicePatch.RequestStatus.APPROVAL) %></td>
                <td align="left"  width="22%"><%= CMnServicePatch.RequestStatus.APPROVAL %></td>

                <td align="right" width="3%"><%= buttons.get(CMnServicePatch.RequestStatus.PENDING) %></td>
                <td align="left"  width="22%"><%= CMnServicePatch.RequestStatus.PENDING %></td>

                <td align="right" width="3%"><%= buttons.get(CMnServicePatch.RequestStatus.RELEASE) %></td>
                <td align="left"  width="22%"><%= CMnServicePatch.RequestStatus.RELEASE %></td>
              </tr>

              <tr>
                <td></td>
                <td></td>

                <td align="right" width="3%"><%= buttons.get(CMnServicePatch.RequestStatus.REJECTED) %></td>
                <td align="left"  width="22%"><%= CMnServicePatch.RequestStatus.REJECTED %></td>

                <td align="right" width="3%"><%= buttons.get(CMnServicePatch.RequestStatus.CANCELED) %></td>
                <td align="left"  width="22%"><%= CMnServicePatch.RequestStatus.CANCELED %></td>

                <td></td>
                <td></td>
              </tr>

              <tr>
                <td></td>
                <td></td>

                <td></td>
                <td></td>

                <td align="right" width="3%"><%= buttons.get(CMnServicePatch.RequestStatus.RUNNING) %></td>
                <td align="left"  width="22%"><%= CMnServicePatch.RequestStatus.RUNNING %></td>

                <td></td>
                <td></td>
              </tr>

              <tr>
                <td></td>
                <td></td>

                <td></td>
                <td></td>

                <td align="right" width="3%"><%= buttons.get(CMnServicePatch.RequestStatus.BRANCHING) %></td>
                <td align="left"  width="22%"><%= CMnServicePatch.RequestStatus.BRANCHING %></td>

                <td></td>
                <td></td>
              </tr>

              <tr>
                <td></td>
                <td></td>

                <td></td>
                <td></td>

                <td align="right" width="3%"><%= buttons.get(CMnServicePatch.RequestStatus.BRANCHED) %></td>
                <td align="left"  width="22%"><%= CMnServicePatch.RequestStatus.BRANCHED %></td>

                <td></td>
                <td></td>
              </tr>

              <tr>
                <td></td>
                <td></td>

                <td></td>
                <td></td>

                <td align="right" width="3%"><%= buttons.get(CMnServicePatch.RequestStatus.BUILDING) %></td>
                <td align="left"  width="22%"><%= CMnServicePatch.RequestStatus.BUILDING %></td>

                <td></td>
                <td></td>
              </tr>

              <tr>
                <td></td>
                <td></td>

                <td></td>
                <td></td>

                <td align="right" width="3%"><%= buttons.get(CMnServicePatch.RequestStatus.BUILT) %></td>
                <td align="left"  width="22%"><%= CMnServicePatch.RequestStatus.BUILT %></td>

                <td></td>
                <td></td>
              </tr>

              <tr>
                <td></td>
                <td></td>

                <td></td>
                <td></td>

                <td align="right" width="3%"><%= buttons.get(CMnServicePatch.RequestStatus.FAILED) %></td>
                <td align="left"  width="22%"><%= CMnServicePatch.RequestStatus.FAILED %></td>

                <td></td>
                <td></td>
              </tr>

              <tr>
                <td></td>
                <td></td>

                <td></td>
                <td></td>

                <td align="right" width="3%"><%= buttons.get(CMnServicePatch.RequestStatus.COMPLETE) %></td>
                <td align="left"  width="22%"><%= CMnServicePatch.RequestStatus.COMPLETE %></td>

                <td></td>
                <td></td>
              </tr>

            </table>
            <hr/>
            <center><input type="submit" name="submit" value="Update Status"/></center>
            </form>
          </td>
        </tr>
      </table>


<%@ include file="../footer.jsp" %>

      </td>
    </tr>
  </table>

</body>
</html>
