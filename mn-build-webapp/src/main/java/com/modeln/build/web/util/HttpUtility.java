/*
 * HttpUtility.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.web.util;

import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import com.modeln.build.web.errors.*;
import com.modeln.build.web.data.*;
import com.modeln.build.web.application.*;

/** 
 * This class provides utility methods for HTTP.
 * 
 * @author             Shawn Stafford
 * @version            $Revision: 1.1.1.1 $ 
 */

public class HttpUtility {

    public static final String ALERT    = "alert";
    public static final String ERROR    = "error";
    public static final String NEXTPAGE = "nextpage";
    public static final String PREVPAGE = "prevpage";
    public static final String CONTENT  = "jsp_content";
    public static final String RESPONSE = "response";
    public static final String TICKET   = "ticket";

    /** List of external URLs configured for the application */
    public static final String EXTERNAL_URLS = "urls";

    /**
     * Construct the class.
     */
    private HttpUtility() {}

    /**
     * Return the URL to the application instance. 
     *
     * @param   req      HTTP request information
     * @return  URL to access the application
     */
    public static String getApplicationUrl(HttpServletRequest req) {
        StringBuffer url = new StringBuffer();

        // Format: HTTP/1.1
        String protocol = req.getProtocol();
        int pidx = protocol.indexOf('/');
        String pstr = protocol.substring(0, pidx);

        // Determine if the request uses the default port for the current protocol
        boolean defaultPort = false;
        if (pstr.equalsIgnoreCase("HTTP") && (req.getServerPort() == 80)) {
            defaultPort = true;
        } else if (pstr.equalsIgnoreCase("HTTPS") && (req.getServerPort() == 443)) {
            defaultPort = true;
        }

        // HTTPS request protocol shows as "HTTP" so we need to adjust for that
        if (req.getServerPort() == 443) {
            pstr = "https";
            defaultPort = true;
        }

        // Protocol
        url.append(pstr.toLowerCase() + "://");

        // Host and port
        url.append(req.getServerName());
        if (!defaultPort) {
            url.append(":" + req.getServerPort());
        }

        // Application context path (i.e. how to reference the application instance
        // relative to the application server
        url.append(req.getContextPath());

        return url.toString();
    }


    /**
     * Sets the object which will be used to render dynamic JSP content.
     * The object needs to have a standard toString method so the JSP page
     * can render it correctly.
     *
     * @param   req      HTTP request information
     * @param   content  Object containing the dynamic content
     */
    public static void setDynamicContent(HttpServletRequest req, Object content) {
        req.setAttribute(CONTENT, content);
    }


    /**
     * Sets the error object in the request.
     * 
     * @param   error   Application error object
     */
    public static void setError(HttpServletRequest req, ApplicationError error) {
        req.setAttribute(ERROR, error);
    }


    /**
     * Places an ApplicationError object in the HTTP response to notify a JSP
     * page that a user error has been encountered.  This response may be used
     * to allow the user to correct their error.  The response information is
     * intentionally kept seperate from a standard error to indicate that it
     * is a correctible error.
     * 
     * @param   error   Application error object
     */
    public static void setResponse(HttpServletRequest req, CommandResult response) {
        req.setAttribute(RESPONSE, response);
    }

    /**
     * Sets the list of external URLs in the HTTP response so they can be
     * referenced by JSP pages.
     *
     * @param  req   HTTP request
     * @param  urls  External URL list
     */
    public static void setExternalUrls(HttpServletRequest req, Hashtable<String,String> urls) {
        req.setAttribute(EXTERNAL_URLS, urls);
    }

    /**
     * Return the list of external URLs found in the HTTP response.
     *
     * @return   List of external URLs
     */
    public static Hashtable<String,String> getExternalUrls(HttpServletRequest req) {
        return (Hashtable<String,String>) req.getAttribute(EXTERNAL_URLS);
    }


    /** 
     * Returns the parameter indicating the next page to navigate to.
     *
     * @param   req     HTTP request
     *
     * @return  String  next page
     */
    public static String getNextPage(HttpServletRequest req) {
        return (String) req.getParameter(NEXTPAGE);
    }

    /** 
     * Returns the parameter indicating the previous page to navigate to.
     *
     * @param   req     HTTP request
     *
     * @return  String  previous page
     */
    public static String getPreviousPage(HttpServletRequest req) {
        return (String) req.getParameter(PREVPAGE);
    }

    /** 
     * Returns the error object from the correct request parameter.
     *
     * @param   req     HTTP request
     *
     * @return  ApplicationError containing error information
     */
    public static ApplicationError getError(HttpServletRequest req) {
        return (ApplicationError) req.getAttribute(ERROR);
    }

    /**
     * Places a system alert in the response.
     *
     * @param   req     HTTP request
     * @param   res     HTTP response
     * @param   msg     Alert message
     */
    public static void setAlert(HttpServletRequest req, HttpServletResponse res, String msg) {
        req.setAttribute(ALERT, msg);
    }

    /**
     * Return a system alert message from the session if one exists.
     *
     * @return  Alert message
     */
    public static String getAlert(HttpServletRequest req) {
        return (String) req.getAttribute(ALERT);
    }

    /**
     * Places the timestamp object in the response as a cookie.
     *
     * @param   req     HTTP request
     * @param   res     HTTP response
     * @param   time    Timestamp
     */
    public static void setTicket(HttpServletRequest req, HttpServletResponse res, SessionTicket time) {
        Cookie timeCookie = new Cookie(TICKET, time.toString());

        // Set the cookie domain to the root domain so that any host
        // in the domain will be able to access the cookie
        String serverName = req.getServerName();
        if ((serverName != null) && (serverName.contains("."))) {
            String domain = serverName;

            // Locate the top-level domain  (com, org, edu)
            int idxTopLevel = serverName.lastIndexOf('.');

            // Locate the mid-level domain name
            int idxMidLevel = serverName.substring(0, idxTopLevel).lastIndexOf('.');

            if (idxMidLevel > 0) {
                timeCookie.setDomain(serverName.substring(idxMidLevel + 1));
            }
        }

        // Set the cookie path to the root of the application so that
        // all commands will have access to the cookie
        timeCookie.setPath(req.getContextPath());

        addCookie(req, res, timeCookie);
    }

    /**
     * Returns the timestamp object from the cookie jar
     *
     * @param   req     HTTP request
     *
     * @return  SessionTicket
     */
    public static SessionTicket getTicket(HttpServletRequest req) {
        SessionTicket stamp = null;

        String time = getCookieValue(req, TICKET);
        if ((time != null) && (time.length() > 0)) {
            try {
                stamp = SessionTicket.parse(time);
            } catch (Exception ex) {}
        }

        return stamp;
    }

    /** 
     * Returns the command result as an object in the HTTP request.
     *
     * @param   req     HTTP request
     *
     * @return  ApplicationError containing error information
     */
    public static CommandResult getResponse(HttpServletRequest req) {
        return (CommandResult) req.getAttribute(RESPONSE);
    }


    /**
     * Returns the dynamic content from the correct request parameter. 
     *
     * @param   req     HTTP request
     *
     * @return  Object containing the dynamic content
     */
    public static Object getDynamicContent(HttpServletRequest req) {
        return req.getAttribute(CONTENT);
    }


    /**
     * There's no method in HttpServletRequest to return cookie
     * value, given cookie name. This method should be replaced
     * by the corresponding method from HttpServletRequest when          
     * it becomes available.        
     *
     * @param   req     Request to look for cookie in
     * @param   name    cookie's name
     *
     * @return  cookie's value in string format or null
     */
    public static Cookie getCookieObject(HttpServletRequest req, String name) {
        Cookie jar[] = req.getCookies();

        //No cookies in the jar.
        if (jar == null) {
            return null;        
        }

        //Examine the jar to see if the name matches
        for (int i = 0; i < jar.length; i++) {
            if (jar[i].getName().equals(name))  {
                return jar[i];
            }
        }

        //Not so lucky, hava a nice day!!
        return null;        
    }

    /**
     * There's no method in HttpServletRequest to return cookie
     * value, given cookie name. This method should be replaced
     * by the corresponding method from HttpServletRequest when          
     * it becomes available.        
     *
     * @param req Request to look for cookie in
     * @param name cookie's name
     *
     * @return cookie's value in string format or null
     */
    public static String getCookieValue(HttpServletRequest req, String name) {
        String value = null;

        Cookie jar = getCookieObject(req, name);
        if (jar != null) {
System.err.println("HttpUtility.getCookieValue(): name=" + jar.getName() + ", maxAge=" + jar.getMaxAge() + ", value=" + jar.getValue());
            value = jar.getValue();
        }

        return value; 
    }

    /**
     * Returns true if there's a cookie with certain value.
     *
     * @param req Request to look for cookie in
     * @param name cookie's name
     * @param value cookie's value
     *
     * @return  true/false  if cookie exists in the request with given value
     */
    public static boolean hasCookieWithValue(HttpServletRequest req, String name, String value) {
        String valueFromReq = getCookieValue(req, name);
        return (valueFromReq != null && valueFromReq.equals(value));
    }

    /**
     * Expire and null a cookie based on its name
     *
     * @param req Request to look for cookie in
     * @param res response to re-issue the cookie to
     * @param name cookie's name
     */
    public static void expireCookie(HttpServletRequest req, HttpServletResponse res, String name) {
        if ((name != null) && (name.length() > 0)) {
            HttpSession session = req.getSession(false);
            if (session != null) {
                session.removeAttribute(name);
            }

            Cookie jar = getCookieObject(req, name);
            if (jar == null) {
               jar = new Cookie(name, "");
            }
            jar.setMaxAge(0);
            jar.setValue("");
            res.addCookie(jar);
System.err.println("HttpUtility.expireCookie(): Cookie should be deleted following this request.");
        }
    }

    /**
     * Add the cookie to the response and to the session
     *
     * @param req Request to look for cookie in
     * @param res response to re-issue the cookie to
     * @param name Cookie object
     */
    public static void addCookie(HttpServletRequest req, HttpServletResponse res, Cookie cookieObj) {
        if (cookieObj == null) return;
System.err.println("HttpUtility.addCookie(): name=" + cookieObj.getName() + ", maxAge=" + cookieObj.getMaxAge() + ", value=" + cookieObj.getValue());
        res.addCookie(cookieObj);
    }

    /**
     * Add the cookies to the response and to the session
     *
     * @param req       Request to look for cookie in
     * @param res       response to re-issue the cookie to
     * @param cookies   List of cookies object
     */
    public static void addCookies(HttpServletRequest req, HttpServletResponse res, Cookie[] cookies) {
        for (int idx = 0; idx < cookies.length; idx++) {
            if (cookies[idx] != null) {
                res.addCookie(cookies[idx]);
System.err.println("HttpUtility.addCookies(): name=" + cookies[idx].getName() + ", maxAge=" + cookies[idx].getMaxAge() + ", value=" + cookies[idx].getValue());
            }
        }
    }


    /**
     * Handles double-byte request parameters.
     *
     * @param req   HTTP request object
     * @param name  Parameter name within the request object
     */
    public static String getDBParameter(HttpServletRequest req, String name) {
        if ((req == null)||(name == null)) return null;

        String s = (String) req.getParameter(name);
        if (s == null) return null;

        String lang = Locale.getDefault().getLanguage();

        String cs = null;
        try {
            cs = new String(s.getBytes(), "UTF8");
        } catch (Exception e) {
            return s;
        }

        // printByte(cs);
        return cs;
    }

}

