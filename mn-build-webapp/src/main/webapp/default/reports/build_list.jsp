<%@ include file="../common.jsp" %>
<%@ page import="java.util.Collections"%>
<%@ page import="java.util.Comparator"%>
<%@ page import="java.util.Vector"%>
<%@ page import="com.modeln.testfw.reporting.*"%>
<%@ page import="com.modeln.build.ctrl.forms.*"%>
<%@ page import="org.jfree.chart.*"%>
<%@ page import="org.jfree.chart.imagemap.*"%>
<%@ page import="java.net.URL"%>
<%
    selectedTab = BUILD_TAB;

    // Contruct the form URLs
    URL formSubmitUrl = new URL(appUrl + "/report/CMnBuildList");
    URL formImageUrl = new URL(imgUrl); 
    URL formDeleteUrl = new URL(appUrl + "/report/CMnDeleteBuild");
    URL formVersionUrl = new URL(appUrl + "/report/CMnBuildData");
    URL formStatusUrl = new URL(appUrl + "/report/CMnBuildStatusNotes");
    URL formChartUrl = new URL(appUrl + "/chart/CMnShowSessionChart");

    Vector list = (Vector) request.getAttribute("BUILD_LIST");
    // Display the list in reverse order by build ID
    if (list != null) {
        Comparator reverseBidComparator = Collections.reverseOrder(new CMnBuildIdComparator());
        Collections.sort(list, reverseBidComparator);
    }
    CMnBuildListForm form = new CMnBuildListForm(formSubmitUrl, formImageUrl, list);
    form.setDeleteUrl(formDeleteUrl);
    form.setStatusUrl(formStatusUrl);
    form.setAdminMode(admin);
    form.setInputMode(true);
    form.setValues(request);
    boolean showCharts = form.showCharts();
%>

<html>
<head>
  <title>Build Report</title>
  <%@ include file="../stylesheet.html" %>
</head>

<body>
<%@ include file="../header.jsp" %>


  <table border="0" width="100%">

    <tr>
      <td>
        <h2>Build Results</h2>

<%= form.toString() %>

<!-- ====================================================================== -->
<!--                           Build Metrics                                -->
<!-- ====================================================================== -->
<% // Render an image map if the data is available
    ChartRenderingInfo blmInfo = (ChartRenderingInfo) session.getAttribute("blmInfo");
    if (blmInfo != null) {
        out.print(ImageMapUtilities.getImageMap("blmMap", blmInfo));
    }
    ChartRenderingInfo blmAvgInfo = (ChartRenderingInfo) session.getAttribute("blmAvgInfo");
    if (blmAvgInfo != null) {
        out.print(ImageMapUtilities.getImageMap("blmAvgMap", blmAvgInfo));
    }

    JFreeChart blmChart = (JFreeChart) session.getAttribute("blm");
    JFreeChart blmAvgChart = (JFreeChart) session.getAttribute("blmAvg");
    if (showCharts && (blmChart != null) && (blmAvgChart != null)) {
%>
<p>
<table border="0">
  <tr>
    <td align="center" valign="top"><img src="<%=formChartUrl.toString()%>?chart=blm&height=400&width=900" usemap="#blmMap"/></td>
    <td align="center" valign="top"><img src="<%=formChartUrl.toString()%>?chart=blmAvg&height=350&width=350" usemap="#blmAvgMap"/></td>
  </tr>
</table>
</p>
<% } %>

<!-- ====================================================================== -->
<!--                              Test Counts                               -->
<!-- ====================================================================== -->
<% // Render an image map if the data is available
    ChartRenderingInfo testCountInfo = (ChartRenderingInfo) session.getAttribute("testCountInfo");
    if (testCountInfo != null) {
        out.print(ImageMapUtilities.getImageMap("testCountMap", testCountInfo));
    }
    ChartRenderingInfo testCountAvgInfo = (ChartRenderingInfo) session.getAttribute("testCountAvgInfo");
    if (testCountInfo != null) {
        out.print(ImageMapUtilities.getImageMap("testCountAvgMap", testCountInfo));
    }

    JFreeChart testCountChart = (JFreeChart) session.getAttribute("testCount");
    JFreeChart testCountAvgChart = (JFreeChart) session.getAttribute("testCountAvg");
    if (showCharts && (testCountChart != null) && (testCountAvgChart != null)) {
%>
<p>
<table border="0">
  <tr>
    <td align="center" valign="top"><img src="<%=formChartUrl.toString()%>?chart=testCount&height=400&width=900" usemap="#testCountMap"/></td>
    <td align="center" valign="top"><img src="<%=formChartUrl.toString()%>?chart=testCountAvg&height=350&width=350" usemap="#testCountAvgMap"/></td>
  </tr>
</table>
</p>
<%  } %>


<!-- ====================================================================== -->
<!--                           Test Count by Area                           -->
<!-- ====================================================================== -->
<% // Render an image map if the data is available
    ChartRenderingInfo testsByAreaInfo = (ChartRenderingInfo) session.getAttribute("testsByAreaInfo");
    if (testsByAreaInfo != null) {
        out.print(ImageMapUtilities.getImageMap("testsByAreaMap", testsByAreaInfo));
    }
    ChartRenderingInfo avgTestsByAreaInfo = (ChartRenderingInfo) session.getAttribute("avgTestsByAreaInfo");
    if (avgTestsByAreaInfo != null) {
        out.print(ImageMapUtilities.getImageMap("avgTestsByAreaMap", avgTestsByAreaInfo));
    }


    JFreeChart testsByAreaChart = (JFreeChart) session.getAttribute("testsByArea");
    JFreeChart avgTestsByAreaChart = (JFreeChart) session.getAttribute("avgTestsByArea");
    if (showCharts && (testsByAreaChart != null) && (avgTestsByAreaChart != null)) {
%>
<p>
<table border="0">
  <tr>
    <td align="center" valign="top"><img src="<%=formChartUrl.toString()%>?chart=testsByArea&height=400&width=900" usemap="#testsByAreaMap"/></td>
    <td align="center" valign="top"><img src="<%=formChartUrl.toString()%>?chart=avgTestsByArea&height=350&width=350" usemap="#avgTestsByAreaMap"/></td>
  </tr>
</table>
</p>
<% } %>


<!-- ====================================================================== -->
<!--                           Test Time by Area                            -->
<!-- ====================================================================== -->
<% // Render an image map if the data is available
    ChartRenderingInfo timeByAreaInfo = (ChartRenderingInfo) session.getAttribute("testTimeInfo");
    if (timeByAreaInfo != null) {
        out.print(ImageMapUtilities.getImageMap("timeByAreaMap", timeByAreaInfo));
    }
    ChartRenderingInfo avgTimeByAreaInfo = (ChartRenderingInfo) session.getAttribute("testTimeAvgInfo");
    if (avgTimeByAreaInfo != null) {
        out.print(ImageMapUtilities.getImageMap("avgTimeByAreaMap", avgTimeByAreaInfo));
    }


    JFreeChart timeByAreaChart = (JFreeChart) session.getAttribute("timeByArea");
    JFreeChart avgTimeByAreaChart = (JFreeChart) session.getAttribute("avgTimeByArea");
    if (showCharts && (timeByAreaChart != null) && (avgTimeByAreaChart != null)) {
%>
<p>
<table border="0">
  <tr>
    <td align="center" valign="top"><img src="<%=formChartUrl.toString()%>?chart=timeByArea&height=400&width=900" usemap="#timeByAreaMap"/></td>
    <td align="center" valign="top"><img src="<%=formChartUrl.toString()%>?chart=avgTimeByArea&height=350&width=350" usemap="#avgTimeByAreaMap"/></td>
  </tr>
</table>
</p> 
<% } %>



<%@ include file="../footer.jsp" %>

      </td>
    </tr>
  </table>

</body>
</html>
