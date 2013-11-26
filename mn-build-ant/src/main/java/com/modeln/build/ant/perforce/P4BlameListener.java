/*
 * Copyright 2000-2003 by Model N, Inc.  All Rights Reserved.
 * 
 * This software is the confidential and proprietary information
 * of Model N, Inc ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you
 * entered into with Model N, Inc.
 */
package com.modeln.build.ant.perforce;

import com.modeln.build.ant.EmailMessage;
import com.modeln.build.perforce.Changelist;
import com.modeln.build.perforce.Client;
import com.modeln.build.perforce.Counter;
import com.modeln.build.perforce.User;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.email.EmailAddress;
import org.apache.tools.ant.taskdefs.email.Message;
import org.apache.tools.ant.taskdefs.email.Mailer;
import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.util.DateUtils;
import org.apache.tools.ant.util.StringUtils;
import org.apache.tools.mail.MailMessage;

/**
 *  Buffers log messages from DefaultLogger, and sends an e-mail with the
 *  results.
 *
 * @author Shawn Stafford
 */
public class P4BlameListener extends DataType implements BuildListener {

    /** Background color used for info lines */
    private static final String BGCOLOR_INFO = "#FFFFFF";

    /** Background color used for error lines */
    private static final String BGCOLOR_ERR = "#FFCCCC";

    /** Background color used for warning lines */
    private static final String BGCOLOR_WARN = "#FFFFCC";

    /** List of errors encountered during the build. */
    private Vector events = new Vector();

    /** Logging level being monitored by the listener */
    private int logLevel = Project.MSG_INFO;

    /** File to send instead of the report output */
    private File reportFile = null;

    /** Reference to the Ant task that was responsible for registering the listener */
    private P4BlameReport parent = null;

    /**
     * Construct a new listener.
     */
    public P4BlameListener(P4BlameReport parent) {
        this.parent = parent;
    }

    /**
     * Set the file to be sent as the body of the e-mail message.
     * If the file does not exist the listener will use the dynamic
     * build output as the body of the message.
     *
     * @param   file    File name
     */
    public void setReportFile(File file) {
        reportFile = file;
    }

    /**
     * Return the file to be sent as the body of the e-mail message.
     *
     * @param   file    File name
     */
    public File getReportFile() {
        return reportFile;
    }


    /**
     * Set the log level that the listener should listen at.
     *
     * @param  level   Log level (error, warning, info, verbose, debug)
     */
    public void setLogLevel(int level) {
        logLevel = level;
    }

    /**
     * Return the log level being monitored by the listener.
     *
     * @return   Log level (error, warning, info, verbose, debug)
     */
    public int getLogLevel() {
        return logLevel;
    }

    /**
     *  Sends an e-mail with the log results.
     *
     * @param event the build finished event
     */
    public void buildFinished(BuildEvent event) {
        if (isEnabled()) {
            // Determine whether notification is required due to a failure
            boolean success = (event.getException() == null);

            if (success) {
                // Set the counter to the current changelist number
                resetCounter();
            } else {
                // Determine the list of changes that went into the build
                Changelist[] changes = getChanges();

                // Determine the list of users
                User[] users = getUsers(changes);

                // Attempt to load the body of the message from a file
                StringBuffer msg = null;
                try {
                    msg = loadFile(reportFile);
                } catch (IOException ioex) {
                    System.out.println("Failure reading report file.  Defaulting to generated message body.");
                }

                // Generate the body of the message
                String msgMimeType = null;
                if (P4BlameReport.HTML_FORMAT.equals(parent.getFormat())) {
                    msgMimeType = "text/html";
                    if (msg == null) { 
                        msg = new StringBuffer();
                        msg.append("<html>");
                        msg.append("<body>");
                        msg.append(formatEventsAsHtml());
                        msg.append("<table border=0 cellspacing=1 cellpadding=1 width=\"100%\">\n");
                        msg.append(formatEventAsHtml(event));
                        msg.append("</table>\n");
                        msg.append("<p>\n");
                        msg.append(formatChangesAsHtml(changes, users));
                        msg.append("</body>");
                        msg.append("</html>");
                    }
                } else {
                    msgMimeType = "text/plain";
                    if (msg == null) {
                        msg = new StringBuffer();
                        msg.append("The plain text report format is not yet implemented.");
                    }
                }

                // Determine the unique list of users for the e-mail notification
                User[] uniqueUsers = getUniqueUsers(users);

                // Iterate through the list of messages that must be sent
                Vector msgList = parent.getEmailMessages();
                if (msgList.size() > 0) {
                    Iterator msgIterator = msgList.iterator();
                    while (msgIterator.hasNext()) {
                        EmailMessage current = (EmailMessage) msgIterator.next();
                        if ((uniqueUsers != null) && (uniqueUsers.length > 0)) {
                            for (int idx = 0; idx < uniqueUsers.length; idx++) {
                                current.addTo(new EmailAddress(uniqueUsers[idx].getEmailAddress()));
                            }
                        } else {
                            current.addTo(new EmailAddress(parent.getDefaultTo()));
                        }
                        current.setMessage(msg.toString());
                        current.setMessageMimeType(msgMimeType);
                        try {
                            current.sendMessage();
                        } catch (BuildException ex) {
                            StringBuffer addrStr = new StringBuffer();
                            for (int idx = 0; idx < uniqueUsers.length; idx++) {
                                if (idx > 0) addrStr.append(", ");
                                addrStr.append(uniqueUsers[idx].getEmailAddress());
                            }
                            System.out.println("Unable to notify the following users: " + addrStr);
                            ex.printStackTrace();
                        }
                    }
                } else {
                    System.out.println("No messages found.  Nothing to do.");
                }

            }
        }
    }

    /**
     * Disable the build listener.
     */
    public void disable() {
        parent.setDefaultEnabled(false);
    }

    /**
     * Determine if the listener should be considered enabled or disabled 
     * by examining the default status in conjunction with the toggle
     * property setting.  Returns TRUE if the listener is enabled, FALSE
     * if disabled.
     *
     * @return  TRUE if the listener is enabled, FALSE if disabled
     */
    public boolean isEnabled() {
        boolean enabled = parent.getDefaultEnabled();
        String propName = parent.getToggleProperty();
        if (propName != null) {
            Project proj = parent.getProject();
            if (proj != null) {
                String propValue = proj.getProperty(propName);
                if (propValue != null) {
                    Boolean booleanValue = new Boolean(propValue);
                    enabled = booleanValue.booleanValue();
                }
            }
        }

        return enabled;
    }

    /**
     * Convert a list of email address objects to a string.
     * 
     * @param  list   List of email address objects
     * @return String of addresses
     */
    private String getAddressList(Vector list) {
        StringBuffer addrList = new StringBuffer();
        if (list != null) {
            Iterator addrIterator = list.iterator();
            while (addrIterator.hasNext()) {
                EmailAddress current = (EmailAddress) addrIterator.next();
                addrList.append(current.toString());
            }
        }
        return addrList.toString();
    }


    /**
     * Signals that a build has started. This event
     * is fired before any targets have started.
     *
     * @param event An event with any relevant extra information.
     *              Must not be <code>null</code>.
     */
    public void buildStarted(BuildEvent event) {
    }

    /**
     * Signals that a target is starting.
     *
     * @param event An event with any relevant extra information.
     *              Must not be <code>null</code>.
     *
     * @see BuildEvent#getTarget()
     */
    public void targetStarted(BuildEvent event) {
    }

    /**
     * Signals that a target has finished. This event will
     * still be fired if an error occurred during the build.
     *
     * @param event An event with any relevant extra information.
     *              Must not be <code>null</code>.
     *
     * @see BuildEvent#getException()
     */
    public void targetFinished(BuildEvent event) {
    }

    /**
     * Signals that a task is starting.
     *
     * @param event An event with any relevant extra information.
     *              Must not be <code>null</code>.
     *
     * @see BuildEvent#getTask()
     */
    public void taskStarted(BuildEvent event) {
    }

    /**
     * Signals that a task has finished. This event will still
     * be fired if an error occurred during the build.
     *
     * @param event An event with any relevant extra information.
     *              Must not be <code>null</code>.
     *
     * @see BuildEvent#getException()
     */
    public void taskFinished(BuildEvent event) {
    }

    /**
     * Receives and buffers log messages.
     *
     * @param event A BuildEvent containing message information.
     *              Must not be <code>null</code>.
     */
    public void messageLogged(BuildEvent event) {
        if (isEnabled()) {
            // Only buffer error messages
            if (event.getPriority() <= logLevel) {
                events.add(event);
            }
        }
    }

    /**
     * Reset the changelist counter value to the most recent changelist
     * available on the client.
     */
    private void resetCounter() {
        // Obtain the most recent changelist available on the client
        String depot = parent.getDepot();
        Client client = Client.getClient();
        Changelist toChange = Changelist.getChange(depot, client);

        // Reset the Perforce counter value
        String counterName = parent.getCounter();
        Counter.setCounter(counterName, toChange.getNumber());
    }


    /**
     * Obtains the list of changes between the changelist specified by the
     * ant task the most recent changelist on the client.
     *
     * @return list of changlists
     */
    private Changelist[] getChanges() {
        String depot = parent.getDepot();
        String counterName = parent.getCounter();
        Client client = Client.getClient();

        // Obtain the most recent changelist available on the client
        Changelist toChange = Changelist.getChange(depot, client);

        // Obtain the lower boundary for the changelist results
        Counter counter = Counter.getCounter(counterName);
        int counterVal = 0;
        if (counter != null) {
            counterVal = counter.getValue();
        } else {
            counterVal = toChange.getNumber();
        }

        return Changelist.getChanges(depot, counterVal, toChange.getNumber());
    }


    /**
     * Obtains the list of users that submitted the changelists.
     * The list of users will have a one-to-one correspondence with
     * the number of changelists, even if the list of users contains
     * duplicates.
     *
     * @return  list of users
     */
    private User[] getUsers(Changelist[] changes) {
        User[] userList = new User[changes.length];

        // Collect users in a hashtable to avoid the overhead of
        // fetching duplicates from Perforce
        Hashtable users = new Hashtable();

        // Get the user of each changelist
        String username = null;
        for (int idx = 0; idx < changes.length; idx++) {
            username = changes[idx].getUser();
            if (users.containsKey(username)) {
                userList[idx] = (User) users.get(username);
            } else {
                userList[idx] = User.getUser(username);
                if (userList[idx] != null) {
                    users.put(username, userList[idx]);
                }
            }
        }

        return userList;
    }


    /**
     * Obtains a list of unique users from the list of all users.
     *
     * @return  list of unique users
     */
    private User[] getUniqueUsers(User[] users) {
        User[] uniqueList = null;

        if (users.length > 0) {
            // Collect users in a hashtable to remove duplicate usernames
            Hashtable userHash = new Hashtable();

            // Examine each user in the list
            String username = null;
            for (int idx = 0; idx < users.length; idx++) {
                username = users[idx].getUsername();
                if (!userHash.containsKey(username)) {
                    userHash.put(username, users[idx]);
                }
            }

            // Return an array of unique users
            int idx = 0;
            uniqueList = new User[userHash.size()];
            Collection values = userHash.values();
            Iterator userIterator = values.iterator();
            while (userIterator.hasNext()) {
                uniqueList[idx] = (User) userIterator.next();
                idx++;
            }
        }

        return uniqueList;
    }


    /**
     * Formats an HTML table containing information about each error
     * encountered in the build.
     */
    private String formatEventsAsHtml() {
        StringBuffer html = new StringBuffer();

        html.append("<b><u>Build events:</u></b><br>\n");
        html.append("<table border=0 cellspacing=0 cellpadding=1 width=\"100%\">\n");
        for (int idx = 0; idx < events.size(); idx++) {
            BuildEvent currentEvent = (BuildEvent) events.get(idx);
            html.append(formatEventAsHtml(currentEvent));
        }
        html.append("</table>\n");

        return html.toString();
    }

    /**
     * Formats the event as a row in an HTML table.
     *
     * @param  event   Build event to be formatted
     */
    private String formatEventAsHtml(BuildEvent event) {
        StringBuffer html = new StringBuffer();

        // Determine the priority of the message
        String bgcolor = null;
        switch (event.getPriority()) {
            case Project.MSG_ERR: 
                bgcolor = BGCOLOR_ERR; 
                break;
            case Project.MSG_WARN: 
                bgcolor = BGCOLOR_WARN; 
                break;
            default: 
                bgcolor = BGCOLOR_INFO;
        }

        // Determine if there is any text to display
        StringBuffer msg = new StringBuffer();
        if (event.getMessage() != null) {
            msg.append(formatTextAsHtml(event.getMessage()));
        }
        if (event.getException() != null) {
            bgcolor = BGCOLOR_ERR; 
            html.append(formatTextAsHtml(event.getException().toString()));
        }

        // Format the message
        if (msg.length() > 0) {
            html.append("<tr>\n");
            html.append("  <td bgcolor=\"" + bgcolor + "\"><tt>" + formatTextAsHtml(msg.toString()) + "</tt></td>\n");
            html.append("  </td>\n");
            html.append("</tr>\n");
        }

        return html.toString();
    }


    /**
     * Formats an HTML table containing information about each changelist.
     * The table will contain a row for each changelist.
     *
     * @param  changes   List of changelists that should be displayed
     * @param  users     User information associated with each changelist
     */
    private String formatChangesAsHtml(Changelist[] changes, User[] users) {
        StringBuffer html = new StringBuffer();

        if (changes == null) {
            html.append("<b>Unable to determine the list of changes.</b>");
        } else {
            html.append("<b><u>Perforce check-ins:</u></b><br>\n");
            html.append("<table border=0 cellspacing=1 cellpadding=1 width=\"100%\">\n");
            for (int idx = 0; idx < changes.length; idx++) {
                // Obtain a shortened description
                String currentDesc = changes[idx].getDescription();
                if (currentDesc != null) {
                    currentDesc = currentDesc.trim();
                    if (currentDesc.length() > 80) {
                        currentDesc = currentDesc.substring(0, 80);
                    }
                }

                // Create a row for the changelist information
                String changelistString = Integer.toString(changes[idx].getNumber());
                String clientString = changes[idx].getUser() + "@" + changes[idx].getClient();
                html.append("<tr>\n");
                html.append("  <td>" + formatTextAsHtml(changelistString) + "</td>\n");
                html.append("  <td><a href=\"mailto:" + users[idx].getEmailAddress() + "\">" + formatTextAsHtml(clientString.trim()) + "</a></td>\n");
                html.append("  <td>" + formatTextAsHtml(currentDesc) + "</td>\n");
                html.append("</tr>\n");
            }
            html.append("</table>\n");
        }

        return html.toString();
    }


    /**
     * Formats plain text as HTML by wrapping the text in &lt;tt&gt; tags
     * and converting any whitespace or reserved characters to escape
     * codes.
     *
     * @param   text   Plain text
     * @return  Formatted HTML
     */
    private static final String formatTextAsHtml(String text) {
        StringBuffer html = new StringBuffer();
        html.append("<tt>");
        for (int idx = 0; idx < text.length(); idx++) {
            if (text.charAt(idx) == ' ') {
                html.append("&nbsp;");
            } else if (text.charAt(idx) == '\t') {
                html.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            } else if (text.charAt(idx) == '\n') {
                html.append("<br>");
            } else {
                html.append(text.charAt(idx));
            }
        }
        html.append("</tt>");
        return html.toString();
    }

    /**
     * Load the contents of the report file into a string.
     * 
     * @return Contents of the report file as a string
     */
    private static final StringBuffer loadFile(File file) throws IOException {
        StringBuffer buffer = null;
 
        if ((file != null) && file.exists()) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(file));
                buffer = new StringBuffer();
                String currentLine = null;
                while ((currentLine = reader.readLine()) != null) {
                    buffer.append(currentLine);
                }
            } catch (IOException ioex) {
                System.out.println("Unable to load report file: " + file.getAbsolutePath());
                buffer = null;
            } finally {
                if (reader != null) reader.close(); 
            }
        }
        
        return buffer;
    }

}


