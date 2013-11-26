/*
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.web.components;

import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * The calendar form displays an HTML representation of a monthly calendar. 
 *
 * @version            $Revision: 1.1.1.1 $
 * @author             Shawn Stafford
 *
 */
public class Calendar {

    private static final String borderColor = "#000000";
    private static final String cellColor   = "#FFFFFF";
    private static final String highlightColor = "#FFCCCC";

    /** Title of the calendar */
    private String title;

    /** Starting date for the calendar */
    private GregorianCalendar startDate;

    /** Ending date for the calendar */
    private GregorianCalendar endDate;

    /** List of calendar entries */
    private TreeSet entries = new TreeSet(); 

    /** Determines whether the calendar entries should be displayed on the calendar */
    private boolean showEntries = true;


    /**
     * Construct the calendar with a start and end date.
     */
    public Calendar(GregorianCalendar start, GregorianCalendar end) {
        startDate = start;
        endDate = end;
    }

    /**
     * Enables or disables the display of entries on the calendar.
     *
     * @param  enable  TRUE if entries should be displayed, FALSE otherwise
     */
    public void setEntryDisplay(boolean enable) {
        showEntries = enable;
    }

    /**
     * Add an event entry to the calendar.  An exception will be thrown if the
     * starting date of the calendar entry falls outside the effective range
     * of the calendar.
     *
     * @param  entry   Calendar event
     * @throws IllegalArgumentException 
     *         if the entry start date occurs outside the range of the calendar
     *         start or end date
     */
    public void addEntry(CalendarEntry entry) throws IllegalArgumentException {
        if (startDate.after(entry.getStartDate())) {
            throw new IllegalArgumentException(
                "Entry date does not fall within the effective range of the calendar: " +
                "entry date = " + entry.toString() + ", " +
                " calendar range = " + startDate.toString() + " to " + endDate.toString());
        } else {
            entries.add(entry);
        }
    }

    /**
     * Determines if the specified date occurs on or before an upper bound.
     *
     * @param  date   Date in question
     * @param  bound  Upper boundary date
     */
    private boolean isOnOrBefore(GregorianCalendar date, GregorianCalendar bound) {
        boolean onOrBefore = (date.before(bound));

        // Check to see if they occur on the same day
        if ((date.get(GregorianCalendar.YEAR) == bound.get(GregorianCalendar.YEAR)) &&
            (date.get(GregorianCalendar.MONTH) == bound.get(GregorianCalendar.MONTH)) &&
            (date.get(GregorianCalendar.DATE) == bound.get(GregorianCalendar.DATE)) )
        {
            onOrBefore = true;
        }

        return onOrBefore;
    }

    /**
     * Set the title for the calendar.
     *
     * @param title  Calendar title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Generate the empty cells up to the given date.
     *
     * @param  start   Starting date
     */
    public String getStartingPadding(GregorianCalendar start) {
        // Determine the number of days in the week prior to the start
        int days = start.get(GregorianCalendar.DAY_OF_WEEK) - start.getActualMinimum(GregorianCalendar.DAY_OF_WEEK);

        StringBuffer html = new StringBuffer();
        for (int idx = 0; idx < days; idx++) {
            html.append("<td class=\"calendar.blank\"></td>\n");
        }
        return html.toString();
    }

    /**
     * Generate the empty cells after the given date. 
     *
     * @param  end   Ending date
     */
    public String getEndingPadding(GregorianCalendar end) {
        // Determine the number of days in the week after the end
        int days = end.getActualMaximum(GregorianCalendar.DAY_OF_WEEK) - end.get(GregorianCalendar.DAY_OF_WEEK);

        StringBuffer html = new StringBuffer();
        for (int idx = 0; idx < days; idx++) {
            html.append("<td class=\"calendar.blank\"></td>\n");
        }
        return html.toString();
    }


    /**
     * Renders a single calendar day.
     *
     * @param  day   Current calendar day
     */
    public String getDay(GregorianCalendar day) {
        StringBuffer html = new StringBuffer();

        // Obtain a subset of calendar entries for the current day
        GregorianCalendar nextDay = (GregorianCalendar) day.clone();
        nextDay.add(GregorianCalendar.DATE, 1);
        CalendarEntry from = new CalendarEntry(day.getTime(), day.getTime());
        CalendarEntry to = new CalendarEntry(nextDay.getTime(), nextDay.getTime()); 
        SortedSet currentEntries = entries.subSet(from, to);

        html.append("<td bgcolor=\"" + cellColor + "\">");
        html.append("  <table border=0 cellspacing=0 cellpadding=0 width=\"100%\">\n");
        html.append("    <tr>\n");
        html.append("      <td align=left></td>\n");
        html.append("      <td align=right>\n");
        html.append(day.get(GregorianCalendar.DATE)); 
        html.append("      </td>\n");
        html.append("    </tr>\n");

        if ((currentEntries != null) && (currentEntries.size() > 0)) {
            Iterator entryList = currentEntries.iterator();
            CalendarEntry currentEntry = null;
            while (entryList.hasNext()) {
                html.append("    <tr>\n");
                html.append("      <td align=center colspan=2");
                if (currentEntry.showHighlight()) {
                    html.append(" class=\"calendar.entry.highlight\" bgcolor=\"" + highlightColor + "\"");
                }
                html.append(">\n");

                currentEntry = (CalendarEntry) entryList.next();
                boolean showUrl = (currentEntry.getUrl() != null);
                if (showUrl) {
                    html.append("<a href=\"" + currentEntry.getUrl().toString() + "\">");
                    html.append(currentEntry.getTitle());
                    html.append("</a>");
                } else {
                    html.append(currentEntry.getTitle());
                }

                html.append("      </td>\n");
                html.append("    </tr>\n");
            }
        } else {
            // Display an empty cell
            html.append("    <tr>\n");
            html.append("      <td align=center colspan=2 height=60>\n");
            html.append("      </td>\n");
            html.append("    </tr>\n");
        }

        html.append("  </table>\n");
        html.append("</td>\n");

        return html.toString();
    }


    /**
     * Generate the monthly calendar.
     *
     * @return  HTML string
     */
    public String getBody() {
        StringBuffer html = new StringBuffer();

        // Determine the starting date for the starting month
        GregorianCalendar calendarStart = new GregorianCalendar(
            startDate.get(GregorianCalendar.YEAR),
            startDate.get(GregorianCalendar.MONTH),
            startDate.getActualMinimum(GregorianCalendar.DATE));

        // Determine the ending date for the ending month
        GregorianCalendar calendarEnd = new GregorianCalendar(
            endDate.get(GregorianCalendar.YEAR),
            endDate.get(GregorianCalendar.MONTH),
            endDate.getActualMaximum(GregorianCalendar.DATE));

        // Pad the beginning of the calendar with empty cells
        String startPad = getStartingPadding(calendarStart);
        if ((startPad != null) && (startPad.length() > 0)) {
            html.append("<tr>\n");
            html.append(startPad);
        }

        // Iterate through each calendar day until the end date is reached
        GregorianCalendar currentDate = (GregorianCalendar) calendarStart.clone();
        while (isOnOrBefore(currentDate, calendarEnd)) {
            // Determine if this is the start of a new week
            if (currentDate.get(GregorianCalendar.DAY_OF_WEEK) == currentDate.getActualMinimum(GregorianCalendar.DAY_OF_WEEK)) {
                html.append("<tr>\n");
            }

            // Render the current calendar day
            html.append(getDay(currentDate));

            // Determine if this is the end of a week
            if (currentDate.get(GregorianCalendar.DAY_OF_WEEK) == currentDate.getActualMaximum(GregorianCalendar.DAY_OF_WEEK)) {
                html.append("</tr>\n");
            }

            currentDate.add(GregorianCalendar.DATE, 1);
        } 

        // Pad the end of the calendar with empty cells
        String endPad = getEndingPadding(calendarEnd); 
        if ((endPad != null) && (endPad.length() > 0)) {
            html.append(endPad);
            html.append("</tr>\n");
        }

        return html.toString();
    }


    /**
     * Return the calendar as an HTML string.
     *
     * @return  HTML string
     */
    public String toString() {
        StringBuffer html = new StringBuffer();

        html.append("<table border=0 cellspacing=1 cellpadding=0 width=\"100%\" bgcolor=\"" + borderColor + "\">\n");

        // Create the title
        html.append("  <tr>\n");
        html.append("    <td>\n");
        html.append("      <table border=0 cellspacing=0 cellpadding=2 width=\"100%\">\n");
        html.append("        <tr>\n");
        html.append("          <td width=\"15%\" bgcolor=\"" + cellColor + "\" align=left><!-- BACK --></td>\n");
        html.append("          <td width=\"70%\" bgcolor=\"" + cellColor + "\" align=center><font class=\"calendar.title\">" + title + "</font></td>\n");
        html.append("          <td width=\"15%\" bgcolor=\"" + cellColor + "\" align=right><!-- FWD --></td>\n");
        html.append("        </tr>\n");
        html.append("      </table>\n");
        html.append("    </td>\n");
        html.append("  </tr>\n");

        html.append("  <tr>\n");
        html.append("    <td>\n");
        html.append("      <table border=0 cellspacing=1 cellpadding=2 width=\"100%\">\n");

        // Create the headers for the days of the week
        html.append("        <tr>\n");
        html.append("          <td width=\"15%\" bgcolor=\"" + cellColor + "\" class=\"calendar.day-of-week\">Sun</td>\n");
        html.append("          <td width=\"14%\" bgcolor=\"" + cellColor + "\" class=\"calendar.day-of-week\">Mon</td>\n");
        html.append("          <td width=\"14%\" bgcolor=\"" + cellColor + "\" class=\"calendar.day-of-week\">Tue</td>\n");
        html.append("          <td width=\"14%\" bgcolor=\"" + cellColor + "\" class=\"calendar.day-of-week\">Wed</td>\n");
        html.append("          <td width=\"14%\" bgcolor=\"" + cellColor + "\" class=\"calendar.day-of-week\">Thu</td>\n");
        html.append("          <td width=\"14%\" bgcolor=\"" + cellColor + "\" class=\"calendar.day-of-week\">Fri</td>\n");
        html.append("          <td width=\"15%\" bgcolor=\"" + cellColor + "\" class=\"calendar.day-of-week\">Sat</td>\n");
        html.append("        </tr>\n");

        // Create the calendar body
        html.append(getBody());

        html.append("      </table>\n");
        html.append("    </td>\n");
        html.append("  </tr>\n");


        html.append("</table>\n");
        
        return html.toString();
    }

}

