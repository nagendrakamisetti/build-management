/*
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.web.components;

import java.net.URL;
import java.util.Date;

/**
 * A calendar entry provides an interface for a single entry in a calendar. 
 *
 * @version            $Revision: 1.0 $
 * @author             Shawn Stafford
 *
 */
public class CalendarEntry implements Comparable {

    /** Title displayed on the calendar */
    private String title;

    /** Description displayed on the calendar */
    private String description;

    /** Starting date of the entry */
    private Date startDate;

    /** Ending date of the entry */
    private Date endDate;

    /** URL associated with the entry */
    private URL url;

    /** Indicates whether the entry requires attention */
    private boolean isHighlighted = false;



    /**
     * Construct a calendar entry with a starting and ending date.
     *
     * @param  start  Starting date 
     * @param  end    Ending date
     */
    public CalendarEntry(Date start, Date end) {
        startDate = start;
        endDate = end;
    }

    /**
     * Associate a URL with the entry.
     *
     * @param  url  Resource associated with the event
     */
    public void setUrl(URL url) {
        this.url = url;
    }

    /**
     * Return the URL associated with the current entry.  A null will be returned
     * if no URL has been defined.
     *
     * @return Entry URL
     */
    public URL getUrl() {
        return url;
    }

    /** 
     * Set a short title that will be displayed on the calendar.
     *
     * @param  title  Entry title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Return a short title that can be used when displaying the entry on a calendar.
     *
     * @return Short title for the entry
     */
    public String getTitle() {
        return title;
    }


    /**
     * Set a verbose description of the calendar entry that will be displayed
     * to provide additional information about the entry.
     *
     * @param  desc  Description of the entry
     */
    public void setDescription(String desc) {
        description = desc;
    }
 
    /**
     * Verbose description of the entry.  This is used to display additional
     * information or comments associated with an entry.
     *
     * @return Verbose description of the entry
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the start date of the calendar entry.
     *
     * @return  Starting date and time 
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * Returns the ending date of the calendar entry.
     *
     * @return  Ending date and time
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * Set the highlight attribute to indicate whether the entry needs to be
     * highlighted for review.  The entry should appear visually different
     * when rendered on a calendar.
     *
     * @param  enable  TRUE if the entry requires attention
     */
    public void setHighlight(boolean enable) {
        isHighlighted = true;
    }

    /**
     * Determine if the entry should be highlighted to the user.
     *
     * @param  TRUE if the entry requires attention from the user, FALSE otherwise
     */
    public boolean showHighlight() {
        return isHighlighted;
    }

    /**
     * Compares the start dates of each object to determine which entry
     * should be placed first in the ordering.
     *
     * @return Integer inicating the relative ordering of the entries
     */
    public int compareTo(Object o) {
        CalendarEntry comparable = (CalendarEntry) o;
        return startDate.compareTo(comparable.getStartDate());
    }

}

