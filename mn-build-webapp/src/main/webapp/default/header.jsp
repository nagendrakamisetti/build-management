<%  if (disableHeader == false) { %>
        <table border="0" cellspacing="0" cellpadding="0" width="100%">
          <tr><td colspan="2" align="center"><h3>Build Management Application</h3></td></tr>
<% if ((alertMessage != null) && (alertMessage.length() > 0)) { %>
          <tr>
            <td colspan="2" align="center">
              <p>
              <table border="1" cellspacing="0" cellpadding="1" bgcolor="red" width="80%">
                <tr>
                  <td align="center">
                    <b><i><font color="white"><%= alertMessage %></font></i></b>
                  </td>
                </tr>
              </table>
              </p>
            </td>
          </tr>
<% } %>
          <tr>
            <td align="left" valign="bottom">
              <!-- navigation tabs -->
              <table border="0" cellspacing="1" cellpadding="2" bgcolor="<%= selectedTabColor %>">
                <tr>


<% if (selectedTab == HOME_TAB) { %>
                  <td class="tabselected" width="<%= tabWidth %>" bgcolor="<%= selectedTabColor %>" NOWRAP><a href="<%= appUrl %>/CMnLogin">Home</a></td>
<% } else { %>
                  <td class="tabactive" width="<%= tabWidth %>" bgcolor="<%= activeTabColor %>" NOWRAP><a href="<%= appUrl %>/CMnLogin">Home</a></td>
<% } %>
<% if (selectedTab == BUILD_TAB) { %>
                  <td class="tabselected" width="<%= tabWidth %>" bgcolor="<%= selectedTabColor %>" NOWRAP><a href="<%= appUrl %>/report/CMnReleaseList">Build Reports</a></td>
<% } else { %>
                  <td class="tabactive" width="<%= tabWidth %>" bgcolor="<%= activeTabColor %>" NOWRAP><a href="<%= appUrl %>/report/CMnReleaseList">Build Reports</a></td>
<% } %>
<% if (selectedTab == PATCH_TAB) { %>
                  <td class="tabselected" width="<%= tabWidth %>" bgcolor="<%= selectedTabColor %>" NOWRAP><a href="<%= appUrl %>/patch/CMnPatch">Patches</a></td>
<% } else { %>
                  <td class="tabactive" width="<%= tabWidth %>" bgcolor="<%= activeTabColor %>" NOWRAP><a href="<%= appUrl %>/patch/CMnPatch">Patches</a></td>
<% } %>
<% if (selectedTab == ENV_TAB) { %>
                  <td class="tabselected" width="<%= tabWidth %>" bgcolor="<%= selectedTabColor %>" NOWRAP><a href="<%= appUrl %>/environment/CMnEnvList">Environments</a></td>
<% } else { %>
                  <td class="tabactive" width="<%= tabWidth %>" bgcolor="<%= activeTabColor %>" NOWRAP><a href="<%= appUrl %>/environment/CMnEnvList">Environments</a></td>
<% } %>
<% if (selectedTab == DB_TAB) { %>
                  <td class="tabselected" width="<%= tabWidth %>" bgcolor="<%= selectedTabColor %>" NOWRAP><a href="<%= appUrl %>/database/CMnDatabaseQuery">Databases</a></td>
<% } else { %>
                  <td class="tabactive" width="<%= tabWidth %>" bgcolor="<%= activeTabColor %>" NOWRAP><a href="<%= appUrl %>/database/CMnDatabaseQuery">Databases</a></td>
<% } %>
<% if ((selectedTab == ADMIN_TAB) && admin) { %>
                  <td class="tabselected" width="<%= tabWidth %>" bgcolor="<%= selectedTabColor %>" NOWRAP><a href="<%= appUrl %>/CMnAdmin">Admin</a></td>
<% } else if (admin) { %>
                  <td class="tabactive" width="<%= tabWidth %>" bgcolor="<%= activeTabColor %>" NOWRAP><a href="<%= appUrl %>/CMnAdmin">Admin</a></td>
<% } %>


                </tr>
              </table>
            </td>

            <td align="right" valign="bottom">
<%
    if (user != null) {
        %><%= user.getFirstName() %> <%= user.getLastName() %> | <a href="<%= appUrl %>/CMnLogout">Log out</a><%
    } else {
        %><a href="<%= appUrl %>/CMnLogin">Log in</a><%
    }
%>
            </td>

          </tr>
          <tr>
            <td colspan="2" bgcolor="<%= selectedTabColor %>"></td>
          </tr>

        </table>
        <p>
<%  } %>

