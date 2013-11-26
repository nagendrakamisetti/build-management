<%@ include file="../common.jsp" %>
<%@ page import="com.modeln.testfw.reporting.*"%>
<%@ page import="com.modeln.build.ctrl.forms.CMnBuildDataForm"%>
<%@ page import="com.modeln.build.ctrl.forms.CMnBuildReviewSummaryForm"%>
<%
    selectedTab = BUILD_TAB;

    URL formReviewUrl  = new URL(appUrl + "/report/CMnBuildReview");
    URL formSearchUrl  = new URL(appUrl + "/report/CMnBuildList");
    URL formBuildUrl   = new URL(appUrl + "/report/CMnBuildData");
    URL formImageUrl   = new URL(imgUrl);
    URL formDownloadUrl= new URL(downloadUrl);

    // Construct the build information to be presented
    CMnBuildDataForm buildForm = new CMnBuildDataForm(formBuildUrl, formImageUrl);
    buildForm.setSearchUrl(formSearchUrl);
    buildForm.setVersionUrl(formBuildUrl);
    buildForm.setDownloadUrl(formDownloadUrl);
    buildForm.setValues(request);
    buildForm.setAdminMode(false);
    buildForm.setInputMode(false);
    //buildForm.setRelatedLinks(!disableHeader);
    CMnDbBuildData build = buildForm.getValues();
    CMnDbHostData host = build.getHostData();

    // Obtain a list of product areas
    Vector areas = (Vector) request.getAttribute("PRODUCT_AREAS");
    Vector reviews = (Vector) request.getAttribute("AREA_REVIEWS");

    CMnBuildReviewSummaryForm reviewSummaryForm = new CMnBuildReviewSummaryForm(formReviewUrl, formImageUrl, areas, reviews); 
    reviewSummaryForm.setBuildId(build.getId());
    if (user != null) {
        reviewSummaryForm.setUser(user);
    }
%>
<html>
<head>
  <title>Area Review</title>
  <script language="JavaScript">
    <%@ include file="report.js" %>
  </script>
  <%@ include file="../stylesheet.html" %>
</head>

<body>
  <%@ include file="../header.jsp" %>

  <!-- ==================================================================== -->
  <!--                           Build Summary                              -->
  <!-- ==================================================================== -->
<%= buildForm.getTitledBorder(buildForm.toString()) %>
<p>

  <!-- ==================================================================== -->
  <!--                            Area Summary                              -->
  <!-- ==================================================================== -->
<%
        try {
            out.println(reviewSummaryForm.toString());
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


<%@ include file="../footer.jsp" %>

</body>
</html>
