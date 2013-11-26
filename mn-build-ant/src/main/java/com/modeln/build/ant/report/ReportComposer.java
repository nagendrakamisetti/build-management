/*
 * Copyright 2000-2003 by Model N, Inc.  All Rights Reserved.
 * 
 * This software is the confidential and proprietary information
 * of Model N, Inc ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you
 * entered into with Model N, Inc.
 */
package com.modeln.build.ant.report;

import java.io.File;
import java.io.BufferedInputStream;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.Location;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildEvent;

import com.modeln.build.perforce.Changelist;

/**
 * Generates a report from the list of parse events.
 *
 * @author Shawn Stafford
 */
public class ReportComposer {

    /** Date format used to represent a date (2003/10/01) */
    private static final SimpleDateFormat SHORT_DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy");


    /** Individual entries on the report (correspond to parse targets) */
    private Vector reportEntries = new Vector();

    /** Report output stream */
    private BufferedOutputStream out;

    /** Title of the report */
    private String title = "Build Report";

    /** Description of the report */
    private String description = "";

    /** List of source control changes that apply to the current report */
    private Changelist[] changelist;


    /** Root node of the execution tree.  The tree contains all processed events */
    private ReportTreeNode root;

    /**
     * Construct the report composer.  A list of parse targets and events will
     * be used to construct the report
     *
     * @param   root        Root of the report event tree
     * @param   stream      Output stream to which the report should be written
     */
    public ReportComposer(ReportTreeNode root, OutputStream stream) {
        out = new BufferedOutputStream(stream);
        this.root = root;
    }


    /**
     * Construct the report composer.  A list of parse targets and events will
     * be used to construct the report
     *
     * @param   targets     List of ReportParseTargets 
     * @param   events      List of ReportParseEvents
     * @param   stream      Output stream to which the report should be written
     */
    public ReportComposer(Vector targets, Vector events, OutputStream stream) {
        out = new BufferedOutputStream(stream);

        ReportParseTarget currentTarget = null;
        for (int idx = 0; idx < targets.size(); idx++) {
            currentTarget = (ReportParseTarget)targets.get(idx);
            reportEntries.add(new ReportParseTargetSummary(currentTarget, events));
        }
    }


    /**
     * Set the output stream to which the report will be written.
     */
    public void setOutputStream(OutputStream stream) {
        if (stream instanceof BufferedOutputStream) {
            out = (BufferedOutputStream) stream;
        } else {
            out = new BufferedOutputStream(stream);
        }
    }

    /**
     * Generate a report from the list of parse events.
     */
    public void write(String format) throws IOException {
        String report = "";
        if (format.equalsIgnoreCase(Report.HTML_FORMAT)) {
            report = getHtml();
        } else {
            report = getText();
        }
        out.write(report.getBytes());
        out.flush();
        out.close();
    }


    /**
     * Set the report title.
     *
     * @param   text    Title used for the report
     */
    public void setTitle(String text) {
        title = text;
    }

    /**
     * Set the report description. 
     *
     * @param   text    Description of the report.
     */
    public void setDescription(String text) {
        description = text;
    }

    /**
     * Set the list of source control changes that apply to the current 
     * build report.
     *
     * @param   changes     List of changes
     */
    public void setChanges(Changelist[] changes) {
        changelist = changes;
    }

    /**
     * Generate a report from the list of parse events.
     *
     * @return  Body of the report
     */
    public String getText() {
        StringBuffer rpt = new StringBuffer();

        ReportParseTargetSummary currentEntry = null;
        for (int idx = 0; idx < reportEntries.size(); idx++) {
            // Display the title of the current section
            int num = idx + 1;
            currentEntry = (ReportParseTargetSummary) reportEntries.get(idx);
            rpt.append(num + ". Parsing Summary: " + currentEntry.getTarget().getTargetName() + "\n");

            // Display the summary counts
            Hashtable errorSummary = currentEntry.getTypeSummary();
            String currentKey = null;
            String separator = "   ";
            for (Enumeration keys = errorSummary.keys() ; keys.hasMoreElements(); ) {
                currentKey = (String) keys.nextElement();
                rpt.append(separator + errorSummary.get(currentKey) + " " + currentKey);
                separator = ", ";
            }
            rpt.append("\n\n");

            // Display the errors
            Vector events = currentEntry.getEvents();
            ReportParseEvent currentEvent = null;
            ReportParseCriteria currentCriteria = null;
            for (int eventIdx = 0; eventIdx < events.size(); eventIdx++) {
                currentEvent = (ReportParseEvent) events.get(eventIdx);
                currentCriteria = currentEvent.getHighestCriteria();
                rpt.append("   !!! " + currentCriteria.getType() + " !!!\n");
                rpt.append("   Criteria: " + currentCriteria.getText() + "\n");
                rpt.append("   Found in: " + currentEvent.getBuildEvent().getPriority() + "\n");
                rpt.append("   Message:  " + currentEvent.getBuildEvent().getMessage() + "\n\n");
            }
        }

        return rpt.toString();
    }

    /**
     * Generate an HTML report from the list of parse events.
     *
     * @return  Body of the report
     */
    public String getHtml() {
        StringBuffer rpt = new StringBuffer();

        rpt.append("<html>\n");
        rpt.append("<head>\n");
        rpt.append("  <title>" + title + "</title>\n");

        // Insert the contents of an external javascript file
        try {
            String stylesheet = getFileContent("com/modeln/build/ant/report/report.css");
            rpt.append("  <style>" + stylesheet + "</style>\n");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // Insert the contents of an external CSS file
        try {
            String javascript = getFileContent("com/modeln/build/ant/report/report.js");
            rpt.append("  <script language=\"JavaScript\">" + javascript + "</script>\n");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        rpt.append("</head>\n\n");
        rpt.append("<body>\n");

        // Display the project description
        rpt.append("<font class=\"pageMainTitle\">" + title + "</font><p>");
        rpt.append("<font class=\"pageContent\">" + description + "</font><p>");

        // Use an outer table to get a border effect
        rpt.append("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" class=\"bordered\"><tr class=\"bordered\"><td class=\"bordered\">\n");

        rpt.append("  <table width=\"100%\" border=\"0\" cellspacing=\"1\" cellpadding=\"2\">\n");
        rpt.append("    <tr>\n");
        rpt.append("      <th align=\"left\" class=\"standard\">Target</th>\n");
        rpt.append("      <th align=\"left\" class=\"standard\">Status</th>\n");
        rpt.append("      <th align=\"left\" class=\"standard\">Errors</th>\n");
        rpt.append("    </tr>\n");

        ReportParseTargetSummary currentEntry = null;
        for (int idx = 0; idx < reportEntries.size(); idx++) {
            int num = idx + 1;
            currentEntry = (ReportParseTargetSummary) reportEntries.get(idx);
            Vector events = currentEntry.getEvents();

            // If there are errors to report, an extra table cell will be needed
            String rowspan = "1";
            if (events.size() > 0) {
                rowspan = "2";
            }

            rpt.append("    <tr>\n");

            // Display the title of the current section
            rpt.append("<td width=\"15%\" valign=\"top\" class=\"standard\" rowspan=" + rowspan + ">");
            rpt.append("<b>" + num + ": ");
            rpt.append(currentEntry.getTarget().getTargetName() + "</b>");
            rpt.append("</td>");

            // Display the current status
            String targetStatus = currentEntry.getTarget().getStatus();
            long time = currentEntry.getTarget().getLength();
            rpt.append("<td width=\"10%\" valign=\"top\" class=\"" + targetStatus + "\" rowspan=" + rowspan + ">" + targetStatus + "<br>(" + formatTime(time) + ")</td>");
            
            // Display the summary counts
            rpt.append("<td width=\"75%\" valign=\"top\" class=\"standard\">");
            Hashtable errorSummary = currentEntry.getTypeSummary();
            String currentKey = null;
            String separator = "   ";
            for (Enumeration keys = errorSummary.keys(); keys.hasMoreElements(); ) {
                currentKey = (String) keys.nextElement();
                rpt.append(separator + errorSummary.get(currentKey) + " " + currentKey);
                separator = ", ";
            }
            // Determine if any errors, warnings, etc have been reported
            if (events.size() == 0) {
                rpt.append(" No Errors<br>\n\n");
            } else {
                rpt.append(" <input type=checkbox onclick=\"toggleElement('hidden" + num + "')\"> Show<br>\n\n");
            }
            rpt.append("</td>");


            // Don't provide details if there are no events to report
            if (events.size() > 0) {
                rpt.append("<tr><td bgcolor=\"#000000\">\n");
                rpt.append("<div style=\"display: none\" id=\"hidden" + num + "\">\n");
                rpt.append("<table border=0 cellspacing=1 cellpadding=2 width=\"100%\" class=\"standard\">\n");
                rpt.append("  <tr>\n");
                rpt.append("    <td width=\"15%\" class=\"tableContentHeader\">Type</td>\n");
                rpt.append("    <td width=\"70%\" class=\"tableContentHeader\">Description</td>\n");
                rpt.append("    <td width=\"15%\" class=\"tableContentHeader\">Called From</td>\n");
                rpt.append("  </tr>\n");

                // Display the errors
                ReportParseEvent currentEvent = null;
                ReportParseCriteria currentCriteria = null;
                for (int eventIdx = 0; eventIdx < events.size(); eventIdx++) {
                    currentEvent = (ReportParseEvent) events.get(eventIdx);
                    currentCriteria = currentEvent.getHighestCriteria();
                    rpt.append("<tr>\n");
                    rpt.append("  <td valign=\"top\" class=\"" + currentCriteria.getType() + "\">" + currentCriteria.getType() + "</td>\n");
                    rpt.append("  <td valign=\"top\" class=\"" + currentCriteria.getType() + "\">" + currentEvent.getBuildEvent().getMessage() + "</td>\n");
                    Task antTask = currentEvent.getBuildEvent().getTask();
                    rpt.append("  <td valign=\"top\" class=\"" + currentCriteria.getType() + "\">");
                    if (antTask != null) {
                        // Trim off the trailing colon and the full path
                        String location = antTask.getLocation().toString();
                        int slashPos = location.lastIndexOf("/");
                        if ((slashPos >= 0) && (slashPos < location.length())) {
                            location = location.substring(slashPos + 1, location.length() - 2);
                        }
                        rpt.append(location);
                    }
                    rpt.append("  </td>\n");
                    rpt.append("</tr>\n");
                }
                rpt.append("</table>\n");
                rpt.append("</div>\n");
                rpt.append("</td></tr>\n");
            }
            rpt.append("  </tr>\n");
        }
        rpt.append("  </table>\n");
        rpt.append("</td></tr></table>\n");  // Border effect table
        rpt.append("<p>\n");


        // Display each change
        if (changelist != null) {
            rpt.append("<font class=\"pageHeader\">Perforce Changelists</font><br>\n");
            rpt.append("<table border=0 cellspacing=1 cellpadding=2 width=\"100%\" class=\"standard\">\n");
            for (int idx = 0; idx < changelist.length; idx++) {
                rpt.append("<tr>\n");
                rpt.append("  <td valign=\"top\" class=\"p4change\">" + SHORT_DATE_FORMAT.format(changelist[idx].getDate()) + "</td>\n");
                rpt.append("  <td valign=\"top\" class=\"p4change\">" + changelist[idx].getNumber() + "</td>\n");
                rpt.append("  <td valign=\"top\" class=\"p4change\">" + changelist[idx].getUser() + "@" + changelist[idx].getClient() + "</td>\n");
                rpt.append("  <td valign=\"top\" class=\"p4change\">" + changelist[idx].getDescription() + "</td>\n");
                rpt.append("</tr>\n");
            }
            rpt.append("</table>\n");
        }

        rpt.append("</body>\n");
        rpt.append("</html>\n");

        return rpt.toString();
    }



    /**
     * Generate an HTML report from the list of parse events.
     *
     * @return  Body of the report
     */
    public String getNewHtml() {
        StringBuffer rpt = new StringBuffer();

        rpt.append("<html>\n");
        rpt.append("<head>\n");
        rpt.append("  <title>" + title + "</title>\n");

        // Insert the contents of an external javascript file
        try {
            String stylesheet = getFileContent("com/modeln/build/ant/report/report.css");
            rpt.append("  <style>" + stylesheet + "</style>\n");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // Insert the contents of an external CSS file
        try {
            String javascript = getFileContent("com/modeln/build/ant/report/report.js");
            rpt.append("  <script language=\"JavaScript\">" + javascript + "</script>\n");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        rpt.append("</head>\n\n");
        rpt.append("<body>\n");

        // Display the project description
        rpt.append("<font class=\"pageMainTitle\">" + title + "</font><p>");
        rpt.append("<font class=\"pageContent\">" + description + "</font><p>");

        // Recursively render the tree of events 
        rpt.append(getHtmlNode(root));
        
        
        rpt.append("</body>\n");
        rpt.append("</html>\n");

        return rpt.toString();
    }


    /**
     * Render a node and all children.
     */
    public String getHtmlNode(ReportTreeNode node) {
        StringBuffer html = new StringBuffer();

        html.append("<blockquote>\n");
        html.append("  <b>" + node.getName() + ":</b><br>\n");

        // Display each event
        Vector events = node.getEvents();
        ReportParseEvent event = null;
        BuildEvent buildEvent = null;
        for (int idx = 0; idx < events.size(); idx++) {
            event = (ReportParseEvent)events.get(idx);
            buildEvent = event.getBuildEvent();
            html.append(buildEvent.getMessage() + "<br>\n");
        }

        // Display each child node
        Vector children = node.getChildren();
        ReportTreeNode child = null;
        for (int idx = 0; idx < children.size(); idx++) {
            html.append(getHtmlNode((ReportTreeNode)children.get(idx)));
        }

        html.append("</blockquote>\n");

        return html.toString();
    }


    /**
     * Load the contents of a file.
     *
     * @param   file    Name of the file
     */
    private String getFileContent(String filename) throws IOException {
        // Locate the file 
        ClassLoader loader = this.getClass().getClassLoader();
        BufferedInputStream stream = new BufferedInputStream(loader.getResourceAsStream(filename));

        // Load the contents of the file
        StringBuffer content = new StringBuffer();
        char current;
        while (stream.available() > 0) {
            current = (char)stream.read();
            if ((stream.available() > 0) && (current != '\r')) {
                content.append(current);
            }
        }
        stream.close();

        return content.toString();
    }


    /**
     * Format the time value into a readable text format.
     *
     * @param   time    Length of time in milliseconds
     * @return  Formatted time
     */
    private String formatTime(long time) {
        StringBuffer str = new StringBuffer();

        long seconds = (time / 1000);
        long minutes = (time / (1000 * 60));
        long hours   = (time / (1000 * 60 * 60));

        if (hours > 0) {
            str.append(hours + "h ");
            minutes = minutes - (hours * 60);
        }

        if (minutes > 0) {
            str.append(minutes + "m ");
            seconds = seconds - (minutes * 60);
        }

        if (seconds > 0) {
            str.append(seconds + "s");
        } else {
            str.append("0s");
        }

        return str.toString();
    }

}
