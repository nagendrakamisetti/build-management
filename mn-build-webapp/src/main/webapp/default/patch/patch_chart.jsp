<%@ include file="../common.jsp" %>
<%@ page import="com.modeln.build.ctrl.forms.*"%>
<%
    selectedTab = PATCH_TAB;

    // Contruct the form URLs
    URL formSubmitUrl = new URL(appUrl + "/patch/CMnPatchChart");
    URL formImageUrl  = new URL(imgUrl);

    // Construct a list of chart URLs
    URL patchByCustChartUrl = new URL(appUrl + "/chart/CMnShowPatchCountChart");
    URL patchByRelChartUrl = new URL(appUrl + "/chart/CMnShowPatchReleaseChart");
    URL patchByMonthChartUrl = new URL(appUrl + "/chart/CMnShowPatchTrendChart?interval=month");
    URL patchByWeekChartUrl = new URL(appUrl + "/chart/CMnShowPatchTrendChart?interval=week");
    URL fixByCustChartUrl = new URL(appUrl + "/chart/CMnShowPatchFixChart");
    URL fixByAreaChartUrl = new URL(appUrl + "/chart/CMnShowAreaFixChart");

    String pageTitle = "Patch Request Data";
    
    //
    // Construct the patch chart form
    //
    CMnChartForm chartForm = new CMnChartForm(formSubmitUrl, formImageUrl);
    chartForm.setInputMode(true);
    chartForm.addChartType(patchByCustChartUrl, "Requests by Customer");
    chartForm.addChartType(patchByRelChartUrl, "Requests by Release");
    chartForm.addChartType(fixByCustChartUrl, "SDRs by Customer");
    chartForm.addChartType(fixByAreaChartUrl, "SDRs by Area");
    chartForm.addChartType(patchByWeekChartUrl, "Weekly Patch Volume");
    chartForm.addChartType(patchByMonthChartUrl, "Monthly Patch Volume");

    // Use previous form input from the user to set the form values
    chartForm.setValues(request);

    // Define a sensible default start and end date
    if (!chartForm.hasDateRange()) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.add(GregorianCalendar.MONTH, -6);
        Date start = calendar.getTime();
        Date end = new Date();
        chartForm.setDateRange(start, end);
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
<%= chartForm.getTitledBorder("Chart Options", chartForm.toString()) %>
          </td>
        </tr>


<%  
        try {
            if (chartForm.getChartUrl() != null) {
            %><tr><td><img src="<%= chartForm.getChartUrl() %>"/></td></tr><%
            }
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

      </table>


<%@ include file="../footer.jsp" %>

      </td>
    </tr>
  </table>

</body>
</html>
