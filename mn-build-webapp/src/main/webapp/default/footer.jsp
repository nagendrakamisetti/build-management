<%  if (disableFooter == false) { %>
        <hr color="#9999FF">

<%      if (error != null) { %>
        <!-- 
          - An error occurred within the application:
          - Error Code:    <%= error.getErrorCode() %>
          - Error Message: <%= error.getErrorMsg() %>
          - Debug Name:    <%= error.getDebugName() %>
          - Debug Message: <%= error.getDebugMsg() %>
          -->
<%      } %>

<%  } else { %>
<div class="pagefooter"><center>Request generated from <%= request.getRemoteHost() %> on <%= shortDateFormat.format(new Date()) %></center></div>
<%  } %>

