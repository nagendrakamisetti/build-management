<%@ include file="../common.jsp" %>
<%@ page import="java.util.Vector"%>
<%@ page import="com.modeln.testfw.reporting.*"%>
<%@ page import="com.modeln.build.ctrl.forms.*"%>
<%
    selectedTab = BUILD_TAB;

    // Contruct the form URLs
    URL formSummaryUrl = new URL(appUrl + "/report/CMnBuildSummary");
    URL formSearchUrl = new URL(appUrl + "/report/CMnBuildList");
    URL formBuildUrl = new URL(appUrl + "/report/CMnBuildData");
    URL formEventUrl = new URL(appUrl + "/report/CMnBuildEvents");
    URL formNotesUrl = new URL(appUrl + "/report/CMnBuildStatusNotes");
    URL formImageUrl = new URL(imgUrl);
    URL formDownloadUrl = new URL(downloadUrl);

    // Construct the build information to be presented
    CMnBuildDataForm buildForm = new CMnBuildDataForm(formBuildUrl, formImageUrl);
    buildForm.setSummaryUrl(formSummaryUrl);
    buildForm.setSearchUrl(formSearchUrl);
    buildForm.setVersionUrl(formBuildUrl);
    buildForm.setDownloadUrl(formDownloadUrl);
    buildForm.setAdminMode(admin);
    buildForm.setInputMode(false);
    buildForm.setRelatedLinks(!disableHeader);

    CMnDbBuildData build = (CMnDbBuildData) request.getAttribute(CMnBuildDataForm.BUILD_OBJECT_LABEL);
    buildForm.setValues(build);

    // Determine the status type
    String statusType = (String) request.getParameter(CMnBuildStatusForm.STATUS_LABEL);
    if (statusType == null) {
        statusType = (String) request.getAttribute(CMnBuildStatusForm.STATUS_LABEL);
    }
    int statusTypeIdx = -1;
    for (int idx = 0; idx < CMnBuildStatusForm.statusKeys.length; idx ++) {
        if (CMnBuildStatusForm.statusKeys[idx].equalsIgnoreCase(statusType)) {
            statusTypeIdx = idx;
        }
    }

    // Obtain the list of notes for the current status
    Vector notes = null;
    CMnDbBuildStatusData status = build.getStatus();
    if ((status != null) && (statusTypeIdx >= 0)) {
        notes = status.getStatusNotes(statusTypeIdx);
    } 
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
<%  
    if (build != null) { 
        String buildId = Integer.toString(build.getId());
        out.println(buildForm.getTitledBorder(buildForm.toString())); 
        out.println("<p>\n");

        if ((notes != null) && (notes.size() > 0)) {
            if (notes.size() > 1) {
                out.println("<h2>Status Notes</h2>\n");
                out.println("<ul>\n");
                for (int idx = 0; idx < notes.size(); idx++) {
                    int count = idx + 1;
                    String title = "Status Note " + count;
                    out.println("<li><a href=\"#" + count + "\">" + title + "</a></li>");
                }
                if (input) {
                    out.println("<li><a href=\"#new\">New Note</a>...</li>");
                }
                out.println("</ul>\n");
            }

            // Render the existing notes
            for (int idx = 0; idx < notes.size(); idx++) {
                String title = "Status Note " + (idx + 1);
                int count = idx + 1;
                CMnBuildStatusNoteForm noteForm = new CMnBuildStatusNoteForm(formNotesUrl, formImageUrl); 
                noteForm.setValues(buildId, statusType, (CMnDbBuildStatusNote) notes.get(idx));
                noteForm.setAdminMode(admin);
                noteForm.setInputMode(input);
                out.println("<a name=\"" + count + "\">\n"); 
                out.println(noteForm.getTitledBorder(title, noteForm.toString()));
                out.println("</a>\n");
                out.println("<p>\n");
            }
        } else {
            out.println("<center><b>No notes available.</b></center><p>");
        }

        // Render an input form for creating new notes
        if (input) {
            CMnBuildStatusNoteForm noteForm = new CMnBuildStatusNoteForm(formNotesUrl, formImageUrl);
            int defaultStatus = noteForm.parseNoteStatus(statusType);
            noteForm.setAdminMode(admin);
            noteForm.setInputMode(input);
            CMnDbBuildStatusNote newNote = new CMnDbBuildStatusNote(0);
            newNote.setBuildStatus(defaultStatus);
            noteForm.setValues(buildId, statusType, newNote);
            out.println("<a name=\"new\">\n");
            out.println(noteForm.getTitledBorder("New Note", noteForm.toString()));
            out.println("</a>\n");
            out.println("<p>\n");
        }

    } else {
        out.println("<center><b>No build selected.</b></center>");
    } 
%>


<%@ include file="../footer.jsp" %>

      </td>
    </tr>
  </table>

</body>
</html>
