/*
 * SessionUtility.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.web.util;

import java.util.*;
import javax.servlet.http.*;

import com.modeln.build.common.data.account.*;

/**
 * The session manager keeps track of all the session values
 * stored in the Servlet session object.  These values are used
 * to track users as they move through the Member Services area.
 * 
 * @author	Shawn Stafford
 * @version	$Revision: 1.1.1.1 $
 *
 */

public class SessionUtility {

    private static final String LOGIN            = "LOGIN_DATA";
    private static final String COOKIES_PENDING  = "COOKIES_PENDING";
    private static final String COOKIES_SENT     = "COOKIES_SENT";

    /**
     * Class constructor
     */
    private SessionUtility() {
    }


    /**
     * Adds the object to the session.  This method is provided as
     * a convenience to provide abstraction from recently deprecated
     * get and set methods in the Session class.
     * 
     * @param   session HTTP Session to add the object to
     * @param   key     identifies the session object
     * @param   obj     session object
     */
    public static void setObject(HttpSession session, String key, Object obj) {
        session.setAttribute(key, obj);
    }


    /**
     * Returns the object from the session.  This method is provided as
     * a convenience to provide abstraction from recently deprecated
     * get and set methods in the Session class.
     *
     * @param   session HTTP Session to add the object to
     * @param   key     identifies the session object
     *
     * @return  Object found in the session (null if none found)
     */
    public static Object getObject(HttpSession session, String key) {
        return session.getAttribute(key);
    }

    /**
     * Returns a hashtable containing all of the session objects.
     *
     * @param   session HTTP Session to add the object to
     *
     * @return  Hashtable containing the names and values of all objects in the session.
     */
    public static Hashtable getObjects(HttpSession session) {
        Hashtable objects = new Hashtable();

        String attribute = null;
        for (Enumeration e = session.getAttributeNames(); e.hasMoreElements(); ) {
            attribute = (String)e.nextElement();
            objects.put(attribute, getObject(session, attribute));
        }

        return objects;
    }

    /**
     * Removes the object from the session.
     * 
     * @param   session HTTP Session
     * @param   key     identifies the object to be removed
     */
    public static void removeObject(HttpSession session, String key) {
        session.removeAttribute(key);
    }


    /**
     * Disposes of the session object and logs the user out of the system.
     *
     * @param   session HTTP Session
     */
    public static void logout(HttpSession session) {
        session.invalidate();
    }


    /**
     * Returns the login object from the session
     *
     * @param   session HTTP Session
     * @return login data
     */
    public static UserData getLogin(HttpSession session) {
        return (UserData) getObject(session, LOGIN);
    }

    /**
     * Stores the login object in the session
     *
     * @param   session HTTP Session
     * @param login data to be stored
     */
    public static void setLogin(HttpSession session, UserData login) {
        setObject(session, LOGIN, (Object)login);
    }


    /**
     * Returns the list of cookies which must be committed and moves pending
     * cookies to the list of sent cookies.
     *
     * @param   session HTTP Session
     * @return  Vector of cookies to be sent to the client
     */
    public static Vector commitCookies(HttpSession session) {
        Vector cookiesPending = (Vector) getObject(session, COOKIES_PENDING);
        Vector cookiesSent    = (Vector) getObject(session, COOKIES_SENT);

        // List of pending cookies
        Vector response = new Vector();

        // Make sure there are cookies pending
        if (cookiesPending != null) {
            // Make sure the list of sent cookies is created if necessary
            if (cookiesSent == null) {
                cookiesSent = new Vector();
            }

            // Process all pending cookies
            for (int idx = 0; idx < cookiesPending.size(); idx++) {
                boolean sent = false;
                Cookie pending = (Cookie) cookiesPending.remove(idx);
                response.add(pending);

                // Add the cookie to the list of sent cookies
                for (int s = 0; s < cookiesSent.size(); s++) {
                    Cookie cookie = (Cookie) cookiesSent.get(s);
                    if (cookie.getName().equals(pending.getName())) {
                        cookiesSent.set(s, pending);
                        sent = true;
                    }
                }

                // If cookie did not already exist, add it now
                if (sent == false) { 
                    cookiesSent.add(pending); 
                    sent = true;
                }

                // Make sure the sent vector exists in the session
                setObject(session, COOKIES_SENT, cookiesSent);
            }
        }

        return response;
    }

    /**
     * Sets the cookie pending update.  If the cookie already exists
     * and is pending deletion, it will not be updated.  If the cookie
     * is able to be updated, the method will return TRUE.  If the cookie
     * is scheduled for deletion and cannot be updated, the method will
     * return FALSE.
     *
     * @param   session HTTP Session
     * @param   cookie  Cookie to be updated
     * @return  boolean TRUE if the cookie was set, FALSE otherwise
     */
    public static boolean setCookie(HttpSession session, Cookie cookie) {
        boolean status = false;

        // Make sure the list of sent cookies is created if necessary
        Vector cookiesPending = (Vector) getObject(session, COOKIES_PENDING);
        if (cookiesPending == null) {
            cookiesPending = new Vector();
            cookiesPending.add(cookie);

            // Make sure the sent vector exists in the session
            setObject(session, COOKIES_PENDING, cookiesPending);
            return true;
        } else {
            // If the cookie is already pending, replace it
            for (int idx = 0; idx < cookiesPending.size(); idx++) {
                Cookie current = (Cookie) cookiesPending.get(idx);
                // Cookies flagged for deletion should remain deleted
                if ((current.getMaxAge() != 0) && (current.getName().equals(cookie.getName()))) {
                    cookiesPending.set(idx, cookie);
                    return true;
                }
            }

            // Else cookies does not exist and should be added
            cookiesPending.add(cookie);
            return true;
        }

    }

    /**
     * Returns a specific cookie from the list of pending and sent cookies.
     * If the cookie cannot be found, NULL will be returned.
     *
     * @param   session HTTP Session
     * @param   name    Identifies the cookie
     * @return  cookie  Cookie which matches the specified name
     */
    public static Cookie getCookie(HttpSession session, String name) {
        Vector cookiesPending = (Vector) getObject(session, COOKIES_PENDING);
        Vector cookiesSent    = (Vector) getObject(session, COOKIES_SENT);
        Cookie current = null;

        // Search for the cookie in the list of pending updates
        for (int p = 0; p < cookiesPending.size(); p++) {
            current = (Cookie) cookiesPending.get(p);
            if (current.getName().equals(name)) {
                return current;
            }
        }

        // Get a combined list of all pending and sent cookies
        for (int s = 0; s < cookiesSent.size(); s++) {
            current = (Cookie) cookiesSent.get(s);
            if (current.getName().equals(name)) {
                return current;
            }
        }

        return null;
    }

    /**
     * Returns an array containing all of the cookies which
     * are expected to be managed by the session.
     * 
     * @param   session HTTP Session
     */
    public static synchronized Cookie[] getCookies(HttpSession session) {
        Vector cookiesPending = (Vector) getObject(session, COOKIES_PENDING);
        Vector cookiesSent    = (Vector) getObject(session, COOKIES_SENT);

        // Determine the total number of cookies
        int cookieCount = 0;
        if (cookiesPending != null) {
            cookieCount = cookieCount + cookiesPending.size();
        }
        if (cookiesSent != null) {
            cookieCount = cookieCount + cookiesSent.size();
        }

        // Construct the list of cookies
        Cookie[] cookiesAll = null;
        if (cookieCount > 0) {
            cookiesAll = new Cookie[cookieCount];
            int cookieIdx = 0;

            // Get a combined list of all pending and sent cookies
            Cookie current = null;
            if (cookiesSent != null) {
                for (int s = 0; s < cookiesSent.size(); s++) {
                    cookiesAll[cookieIdx] = (Cookie) cookiesSent.get(s);
                    cookieIdx++;
                }
            }

            // Pending cookies will overwrite any existing cookies
            if (cookiesPending != null) {
                for (int p = 0; p < cookiesPending.size(); p++) {
                    cookiesAll[cookieIdx] = (Cookie) cookiesPending.get(p);
                    cookieIdx++;
                }
            }

        }


        return cookiesAll;
    }
}
