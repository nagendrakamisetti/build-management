/*
* Copyright 2000-2003 by Model N, Inc.  All Rights Reserved.
*
* This software is the confidential and proprietary information
* of Model N, Inc ("Confidential Information").  You shall not
* disclose such Confidential Information and shall use it only
* in accordance with the terms of the license agreement you
* entered into with Model N, Inc.
*/
package com.modeln.build.ctrl.forms;

import com.modeln.build.ant.report.BuildEventUtil;
import com.modeln.build.ant.report.ReportParseCriteria;
import com.modeln.build.ant.report.ReportParseEvent;
import com.modeln.build.ant.report.ReportParseTarget;
import com.modeln.testfw.reporting.CMnDbBuildData;
import com.modeln.testfw.reporting.CMnDbHostData;

import java.net.URL;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import javax.servlet.http.HttpServletRequest;

import com.modeln.build.web.tags.TextTag;
import com.modeln.build.web.tags.DateTag;
import com.modeln.build.web.tags.SelectTag;
import com.modeln.build.web.tags.TagGroup;



/**
 * The build event form provides an HTML interface to the build event entries. 
 * Form data can be rendered in either a read-only or input mode.
 *
 * @author  Shawn Stafford
 */
public class CMnBuildEventForm extends CMnBaseForm implements IMnBuildForm {


    /** Name of the request attribute containing the list of build events */
    public static final String EVENT_LIST_LABEL = "eventList";

    /** Name of the request attribute containing a specific event ID */
    public static final String EVENT_ID_LABEL = "eventId";

    /** Default title used for the titled border */
    private static final String DEFAULT_TITLE = "Build Events";

    /** Flag to determine if this page is being viewed interactively */
    boolean disableHeader = false;

    /** List of build events */
    private Vector buildEvents = new Vector();


    /**
     * Construct a build form.
     *
     * @param  form   URL to use when submitting form input
     * @param  images URL to use when referencing images
     */
    public CMnBuildEventForm(URL form, URL images) {
        super(form, images);
        setInputMode(false);
    }

    /**
     * Add an event to the form. 
     */
    public void addEvent(ReportParseEvent event) {
        buildEvents.add(event);
    }

    /**
     * Set the input fields by examining the HTTP request to see if
     * a value was submitted.
     *
     * @param   req     HTTP request
     */
    public void setValues(HttpServletRequest req) {
        // Determine whether the header and footer should be displayed
        String headerParam = (String) req.getParameter("disableHeader");
        if (headerParam == null) {
            headerParam = (String) req.getAttribute("disableHeader");
        }
        if ((headerParam != null) && (!headerParam.equalsIgnoreCase("false"))) {
            disableHeader = true;
        }

        // Get the list of events from the session
        Vector list = (Vector) req.getAttribute(EVENT_LIST_LABEL);
        if (list != null) {
            for (int idx = 0; idx < list.size(); idx++) {
                addEvent((ReportParseEvent) list.get(idx));
            }
        }
    }


    /**
     * Render the build data as a hierarchical summary that is grouped by
     * parse target groups.
     */
    public String getSummary() {
        StringBuffer html = new StringBuffer();

        // Construct a list of groups
        HashMap groups = new HashMap();
        for (int idx = 0; idx < buildEvents.size(); idx++) {
            ReportParseEvent parseEvent = (ReportParseEvent) buildEvents.get(idx);
            ReportParseCriteria criteria = parseEvent.getHighestCriteria();
            ReportParseTarget target = criteria.getParent();
            // The event can only be grouped if it belongs to a target 
            if (target != null) {
                String groupName = target.getGroup();
                Vector eventList = null;
                if (groups.containsKey(groupName)) {
                    // Add the event to an existing list of events
                    eventList = (Vector) groups.get(groupName);
                    eventList.add(parseEvent);
                } else {
                    // Create a new list of events under the new group name
                    eventList = new Vector();
                    eventList.add(parseEvent);
                }
                groups.put(groupName, eventList);
            }
        }

        // Create an HTML table to display the group results
        if (groups.size() > 0) {
            html.append("<table border=0 cellspacing=0 cellpadding=1>\n");
        }
        Set keys = groups.keySet();
        Iterator keyIterator = keys.iterator();
        int groupIdx = 0;
        while (keyIterator.hasNext()) {
            String groupName = (String) keyIterator.next();
            Vector eventList = (Vector) groups.get(groupName);
            ReportParseEvent currentEvent = null;
            ReportParseCriteria criteria = null;

            // Display information about the group
            // The assumption is that every event in the group will have the same group
            // information, so we can just look at the first event for the group info
            currentEvent = (ReportParseEvent) eventList.get(0);
            criteria = currentEvent.getHighestCriteria();
            ReportParseTarget parent = criteria.getParent();
            if (parent != null) {
                html.append("  <tr>\n");
                if (!disableHeader) {
                    html.append("    <td width=20 align=right><input type=checkbox onclick=\"toggleElement('hidden" + groupIdx + "')\"></td>\n");
                }
                html.append("    <td>" + parent.getGroup() + ": " + getEventStats(eventList) + "</td>\n");
                html.append("  </tr>\n");
            }

            // Display each of the events in the group
            if (!disableHeader) {
                html.append("  <tr>\n");
                html.append("    <td></td>\n");
                html.append("    <td>\n");
                html.append("      <div style=\"display: none\" id=\"hidden" + groupIdx + "\">\n");
                html.append("      <table border=0 cellspacing=0 cellpadding=1>\n");
                for (int idx = 0; idx < eventList.size(); idx++) {
                    currentEvent = (ReportParseEvent) eventList.get(idx);
                    criteria = currentEvent.getHighestCriteria();
                    html.append("        <tr>\n");
                    html.append("          <td width=100 align=right valign=top>[" + criteria.getType() + "]</td>\n");
                    html.append("          <td valign=top>");
                    html.append("<a href=\"" + getFormUrl() + "?" + EVENT_ID_LABEL + "=" + currentEvent.getId() + "\">" + 
                                currentEvent.getBuildEvent().getMessage() + "</a></td>\n");
                    html.append("        </tr>\n");
                }
                html.append("      </table>\n");
                html.append("      </div>\n");
                html.append("    </td>\n");
                html.append("  </tr>\n");
            }

            // Move to the next group
            groupIdx++;
        }
        if (groupIdx > 0) {
            html.append("</table>\n");
        }

        return html.toString();
    }

    /**
     * Examines each event in the list and returns a summary of the total number of 
     * occurances of each event type.  For example, a list of 10 events might return
     * a result such as "4 errors, 6 warnings".  Only errors and warnings will be
     * counted
     *
     * @param  events   List of events
     * @return Event stats
     */
    private String getEventStats(Vector events) {
        StringBuffer sb = new StringBuffer();

        // This is a less-than-optimal way to obtain the summary results.
        // The more robust method is commented out below.  The only reason
        // for not implmenting a more robust method is that it would require
        // the result tally to be sorted by severity, and it's not clear 
        // whether the lower severity errors are significant enough to tally
        int errors = 0;
        int warnings = 0;
        for (int idx = 0; idx < events.size(); idx++) {
            ReportParseEvent parseEvent = (ReportParseEvent) events.get(idx);
            ReportParseCriteria criteria = parseEvent.getHighestCriteria();
            if (criteria.isError()) {
                errors++;
            } else if (criteria.isWarning()) {
                warnings++;
            }
        }
        if (errors > 0) {
            sb.append(errors + " errors");
        }
        if (warnings > 0) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(warnings + " warnings");
        }


/*
        // Tally up the events by type
        HashMap map = new HashMap();
        for (int idx = 0; idx < events.size(); idx++) {
            ReportParseEvent parseEvent = (ReportParseEvent) events.get(idx);
            ReportParseCriteria criteria = parseEvent.getHighestCriteria();
            String eventType = criteria.getType();
            if (map.containsKey(eventType)) {
                Integer count = (Integer) map.get(eventType);
                map.put(eventType, count + 1);
            } else {
                map.put(eventType, new Integer(1));
            }
        }

        // Convert the event tally as a string
        Set keys = map.keySet();
        Iterator keyIterator = keys.iterator();
        int groupIdx = 0;
        while (keyIterator.hasNext()) {
            String key = (String) keyIterator.next();
            Integer value = (Integer) map.get(key);
        }
*/
        return sb.toString();
    }


    /**
     * Render the build data form.
     */
    public String toString() {
        StringBuffer html = new StringBuffer();
        html.append("<table border=0 cellspacing=0 cellpadding=1 width=\"100%\" bgcolor=\"" + DEFAULT_BGLIGHT + "\">\n");
        ReportParseEvent parseEvent = null;
        for (int idx = 0; idx < buildEvents.size(); idx++) {
            parseEvent = (ReportParseEvent) buildEvents.get(idx);
            if (parseEvent.getBuildEvent() != null) {
                html.append("<tr>");
                ReportParseCriteria criteria = parseEvent.getHighestCriteria();
                String bgRow = null;
                if ((criteria != null) && criteria.isError()) {
                    html.append("<td nowrap align=center valign=top bgcolor=\"" + ERROR_BGDARK + "\"><b>" + criteria.getType() + "</b></td>");
                    bgRow = ERROR_BGLIGHT;
                } else if ((criteria != null) && criteria.isWarning()) {
                    html.append("<td nowrap align=center valign=top bgcolor=\"" + WARNING_BGDARK + "\"><b>" + criteria.getType() + "</b></td>");
                    bgRow = WARNING_BGLIGHT;
                } else {
                    html.append("<td>&nbsp;</td>");
                    bgRow = DEFAULT_BGLIGHT;
                }
                html.append("<td nowrap valign=top bgcolor=\"" + bgRow + "\">");
                html.append("<!-- ");
                html.append(parseEvent.getParseDateAsString() + " ");
                html.append(BuildEventUtil.getPriorityAsString(parseEvent.getBuildEvent()) + " ");
                html.append("[" + BuildEventUtil.getPathAsString(parseEvent.getExecutionPath()) + "] ");
                html.append("-->");
                String msg = parseEvent.getBuildEvent().getMessage();
                msg = msg.replaceAll("\t", "    ");
                String indent = "";
                if ((msg != null) && msg.startsWith(" ")) {
                    indent = getIndentStyle(msg);
                }
                html.append("<tt" + indent + ">" + parseEvent.getBuildEvent().getMessage() + "</tt>");
                html.append("</td></tr>\n");
            }
        } 
        html.append("</table>\n");
        return html.toString();
    }


    /**
     * Determine the number of spaces that the text should be indented 
     * and return a style tag that peforms the indentation.
     *
     * @param  text   Text containing the leading spaces
     * @return Style HTML tag attribute indicating the amount to indent
     */
    private String getIndentStyle(String text) {
        String trimmedText = text.trim();
        int idxTrimmedText = text.indexOf(trimmedText);
        return " style=\"text-indent: " + (idxTrimmedText / 2) + "em\"";
    }

    /**
     * Create a table which defines a title and border for the contents.
     *
     * @param   title   Text to be placed in the title bar
     * @param   content Content of the table
     */
    public String getTitledBorder(String content) {
        return getTitledBorder(DEFAULT_TITLE, content);
    }


}


