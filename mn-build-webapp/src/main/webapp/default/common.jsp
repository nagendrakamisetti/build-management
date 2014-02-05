<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ page import="java.net.URL"%>
<%@ page import="java.util.Date"%>
<%@ page import="java.util.Enumeration"%>
<%@ page import="java.util.GregorianCalendar"%>
<%@ page import="java.util.Hashtable"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="java.util.Vector"%>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.modeln.build.web.tags.*"%>
<%@ page import="com.modeln.build.web.util.*"%>
<%@ page import="com.modeln.build.web.errors.*"%>
<%@ page import="com.modeln.build.web.errors.*"%>
<%@ page import="com.modeln.build.common.data.account.*"%>
<%
    // Define the navigation tabs
    int HOME_TAB = 0;
    int BUILD_TAB = 1;
    int PATCH_TAB = 2;
    int ENV_TAB = 3;
    int DB_TAB = 4;
    int ADMIN_TAB = 5;
    int selectedTab = HOME_TAB;

    // Get the list of external URLs
    Hashtable<String,String> urls =  HttpUtility.getExternalUrls(request); 

    // Gather user authentication info
    boolean input = false;
    boolean admin = false;
    ApplicationError error = HttpUtility.getError(request);
    UserData user = SessionUtility.getLogin(session);
    if (user != null) {
        GroupData group = user.getGroupByName("admin");
        if ((group != null) && (group.getType() == GroupData.ADMIN_GROUP_TYPE)) {
            admin = true;
        }
    }
    // For now, default to input mode if an admin is logged in
    input = admin;

    // Text formatting objects
    SimpleDateFormat fullDateFormat = new SimpleDateFormat("EEE, MMM dd, yyyy 'at' HH:mm:ss z");
    SimpleDateFormat shortDateFormat = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm z");

    int tabWidth = 150;
    int leftNavWidth = 150;
    String activeTabColor = "#CCCCCC";
    String selectedTabColor = "#DDDDDD";

    String baseUrl = HttpUtility.getApplicationUrl(request);
    String appUrl = baseUrl + "/command"; 
    String imgUrl = baseUrl + "/default/images";
    String d3Url = baseUrl + "/d3js/d3.v3.js";
    String downloadUrl = urls.get("download"); 

    // Determine whether the header and footer should be displayed
    boolean disableHeader = false;
    boolean disableFooter = false;
    String headerParam = (String) request.getParameter("disableHeader");
    if (headerParam == null) {
        headerParam = (String) request.getAttribute("disableHeader");
    }
    if ((headerParam != null) && (!headerParam.equalsIgnoreCase("false"))) {
        disableHeader = true; 
        disableFooter = true;
    } 

    // Determine whether an alert message should be displayed
    String alertMessage = HttpUtility.getAlert(request);
    // Make sure we don't show an empty string alert
    if ((alertMessage != null) && (alertMessage.trim().length() == 0)) {
        alertMessage = null;
    }
%>
