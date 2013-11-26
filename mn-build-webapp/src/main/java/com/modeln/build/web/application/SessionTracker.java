package com.modeln.build.web.application;

import com.modeln.build.web.data.SessionActivity;

import java.util.Hashtable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionListener;
import javax.servlet.http.HttpSessionEvent;

import com.modeln.build.common.data.account.UserData;
import com.modeln.build.web.util.SessionUtility;

/**
 * Implement a listener to keep track of the active sessions on the servlet.
 * Keep in mind that some containers such as Tomcat may restore sessions
 * after a server restart without triggering a sessionCreated event.  For
 * this reason, the code must protect against the case that it may not
 * be tracking all active sessions.
 */
public class SessionTracker implements HttpSessionListener {

    /** List of active sessions on the server */
    private static Hashtable<String, SessionActivity> activeSessions = new Hashtable<String, SessionActivity>();

    /**
     * Listener callback for adding session tracking.
     *
     * @param   se    Session event
     */
    public void sessionCreated(HttpSessionEvent se) {
        updateActivity(se.getSession());
    }

    /**
     * Listener callback for removing session tracking.
     *
     * @param   se    Session event
     */
    public void sessionDestroyed(HttpSessionEvent se) {
        removeActivity(se.getSession());
    }

    /**
     * Return a list of sessions being tracked by the application.
     *
     * @return  List of sessions
     */
    public static Hashtable<String, SessionActivity> getActiveSessions() {
        return activeSessions;
    }

    /**
     * Create or update the session activity.  If the session
     * activity does not already exist, it will be created and
     * tracked.  If activity already exists for the session,
     * the tracking information will be updated.  Session
     * activity will be returned following the update.
     *
     * @param  session   Session information
     */
    private static SessionActivity updateActivity(HttpSession session) {
        SessionActivity activity = null;
        if (session != null) {
            // Look up the activity by session ID
            String id = session.getId();
            if (activeSessions.containsKey(id)) {
                activity = (SessionActivity) activeSessions.get(id);
                activity.setCreationTime(session.getCreationTime());
            } else {
                activity = new SessionActivity();
            }

            // Get user information from the session
            UserData user = SessionUtility.getLogin(session);
            if (user != null) {
                activity.setUid(user.getUid());
                activity.setUsername(user.getUsername());
            }

            activity.setLastAccessedTime(session.getLastAccessedTime());

            // Update the session tracking information
            activeSessions.put(id, activity);
        }
        return activity;
    }

    /**
     * Remove the session information.  If the session activity is
     * found, it will be returned.  If no session activity exists,
     * null will be returned.
     *
     * @param  session   Session information
     */
    private static SessionActivity removeActivity(HttpSession session) {
        SessionActivity activity = null;
        if (session != null) {
            String id = session.getId();
            activity = (SessionActivity) activeSessions.remove(id);
        }
        return activity;
    }


    /**
     * Create or update the session activity.  If the session
     * activity does not already exist, it will be created and
     * tracked.  If activity already exists for the session,
     * the tracking information will be updated.  Session
     * activity will be returned following the update.
     *
     * @param  request   Request information
     */
    public static SessionActivity updateActivity(HttpServletRequest request) {
        SessionActivity activity = updateActivity(request.getSession());
        if (activity != null) {

            // Identify the host where the user is connected from
            String origin = null;
            String host = request.getRemoteHost();
            String addr = request.getRemoteAddr();
            if ((host != null) && (addr != null) && (host != addr)) {
                origin = host + " (" + addr + ")";
            } else if (host != null) {
                origin = host;
            } else if (addr != null) {
                origin = addr;
            }

            activity.setOrigin(origin);
        }
        return activity;
    }

    /**
     * Remove the session information.  If the session activity is
     * found, it will be returned.  If no session activity exists,
     * null will be returned.
     *
     * @param  request    Request information
     */
    public static SessionActivity removeActivity(HttpServletRequest request) {
        SessionActivity activity = removeActivity(request.getSession());

        return activity;
    }


}
