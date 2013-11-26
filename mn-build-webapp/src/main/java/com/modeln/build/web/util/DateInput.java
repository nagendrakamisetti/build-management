/**
 * DateInput.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.web.util;
 
import java.util.*;
import javax.servlet.http.*;
import com.modeln.build.web.tags.SelectTag;
import com.modeln.build.util.DateUtility;
import com.modeln.build.util.StringUtility;

/**
 * The DateInput object constructs a grouping of HTML input elements
 * which allow the user to specify a particular date.
 *
 * @version            $Revision: 1.1.1.1 $
 * @author             Shawn Stafford
 */
public class DateInput {

    /** Name used to identify the month tag */
    private static final String monthTagName = "Month";

    /** Name used to identify the day tag */
    private static final String dayTagName = "Day";

    /** Name used to identify the year tag */
    private static final String yearTagName = "Year";

    /** List of the months */
    private static final String months[] = {
        "January", "February", "March", "April", "May", "June", 
        "July", "August", "September", "October", "November", "December"
    };

    /** Input tag names will be prepended with this name prefix */
    private String tagNamePrefix;

    /** Earliest date the user will be allowed to specify */
    private GregorianCalendar beginDate = new GregorianCalendar();

    /** Latest date the user will be allowed to specify */
    private GregorianCalendar endDate = new GregorianCalendar();


    /**
     * Construct the date input object.
     *
     * @param   name    Name used to identify the form elements
     * @param   begin   Earliest date the user will be allowed to enter
     * @param   end     Latest date the user will be allowed to enter
     */
    public DateInput(String name) {
        tagNamePrefix = name;

        int year = endDate.get(GregorianCalendar.YEAR);
        beginDate.set(GregorianCalendar.YEAR, year - 100);
    }

    /**
     * Construct the date input object.
     *
     * @param   name    Name used to identify the form elements
     * @param   begin   Earliest date the user will be allowed to enter
     * @param   end     Latest date the user will be allowed to enter
     */
    public DateInput(String name, Date begin, Date end) {
        tagNamePrefix = name;
        beginDate.setTime(begin);
        endDate.setTime(end);
    }

    /**
     * Parse the HTTP request attributes to determine the date indicated
     * by the values submitted.
     *
     * @param   req     HTTP request
     * @return  Date found in the request
     */
    public Date parse(HttpServletRequest req) {
        String month = (String) req.getParameter(tagNamePrefix + monthTagName);
        String day   = (String) req.getParameter(tagNamePrefix + dayTagName);
        String year  = (String) req.getParameter(tagNamePrefix + yearTagName);
        try {
            GregorianCalendar calendar = new GregorianCalendar(
                Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));
            return calendar.getTime();
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Construct the HTML elements.
     */
    public String toString() {
        StringBuffer html = new StringBuffer();

        // Construct a table containing a month, day, and year element
        html.append("\n<table border=\"0\" cellspacing=\"2\" cellpadding=\"2\">\n");
        html.append("  <tr>\n");

        html.append("    <td>\n");
        html.append(getMonthTag() + "\n");
        html.append("    </td>\n");

        html.append("    <td>\n");
        html.append(getDayTag() + "\n");
        html.append("    </td>\n");

        html.append("    <td>\n");
        html.append(getYearTag() + "\n");
        html.append("    </td>\n");

        html.append("  </tr>\n");
        html.append("</table>\n");

        return html.toString();
    }

    /**
     * Return the day tag element.  If the beginning and ending date
     * occur in the same day, an input tag is not neccasary so a hidden
     * input tag will be returned containing the value.
     */
    private String getDayTag() {
        StringBuffer html = new StringBuffer();
        String tagName = tagNamePrefix + dayTagName;

        if (isSameDay()) {
            html.append("<input type=\"hidden\" name=\"" + tagName + "\" value=\"" + endDate.get(GregorianCalendar.DATE) + "\">");
        } else {
            String[] dateArray = new String[31];
            for (int idx=0; idx < dateArray.length; idx++) {
                dateArray[idx] = Integer.toString(idx + 1);
            }
            SelectTag tag = new SelectTag(tagName, dateArray);
            tag.setKeyOrder(dateArray);
            html.append(tag.toString());
        }

        return html.toString();
    }

    /**
     * Return the Month tag element.  If the beginning and ending date
     * occur in the same month, an input tag is not neccasary so a hidden
     * input tag will be returned containing the value.
     */
    private String getMonthTag() {
        StringBuffer html = new StringBuffer();
        String tagName = tagNamePrefix + monthTagName;

        if (isSameMonth()) {
            html.append("<input type=\"hidden\" name=\"" + tagName + "\" value=\"" + endDate.get(GregorianCalendar.MONTH) + "\">");
        } else {
            Hashtable monthList = new Hashtable();
            String[][] monthArray = new String[months.length][2];
            Vector keyOrder = new Vector();
            for (int idx=0; idx < months.length; idx++) {
                monthList.put(Integer.toString(idx), months[idx]);
                keyOrder.add(Integer.toString(idx));
            }
            SelectTag tag = new SelectTag(tagName, monthList);
            tag.setKeyOrder(keyOrder);
            html.append(tag.toString());
        }

        return html.toString();
    }


    /**
     * Return the Year tag element.  If the beginning and ending date
     * occur in the same year, an input tag is not neccasary so a hidden
     * input tag will be returned containing the value.
     */
    private String getYearTag() {
        StringBuffer html = new StringBuffer();
        String tagName = tagNamePrefix + yearTagName;

        if (isSameYear()) {
            html.append("<input type=\"hidden\" name=\"" + tagName + "\" value=\"" + endDate.get(GregorianCalendar.YEAR) + "\">");
        } else {
            Vector years = new Vector();
            int beginYear = beginDate.get(GregorianCalendar.YEAR);
            int endYear = endDate.get(GregorianCalendar.YEAR);
            for (int idx=beginYear; idx <= endYear; idx++) {
                years.add(Integer.toString(idx));
            }
            SelectTag tag = new SelectTag(tagName, years);
            tag.setSorting(true);
            html.append(tag.toString());
        }

        return html.toString();
    }

    /**
     * Determines whether the beginning and ending date occur within the
     * same year.
     */
    private boolean isSameYear() {
        if ((beginDate != null) && (endDate != null)) {
            return (beginDate.get(GregorianCalendar.YEAR) == endDate.get(GregorianCalendar.YEAR));
        } else {
            return false;
        }
    }

    /**
     * Determines whether the beginning and ending date occur within the
     * same month of the same year.
     */
    private boolean isSameMonth() {
        if (isSameYear()) {
            return (beginDate.get(GregorianCalendar.MONTH) == endDate.get(GregorianCalendar.MONTH));
        } else {
            return false;
        }
    }

    /**
     * Determines whether the beginning and ending date occur within the
     * same day of the month in the same year.
     */
    private boolean isSameDay() {
        if (isSameYear() && isSameMonth()) {
            return (beginDate.get(GregorianCalendar.DATE) == endDate.get(GregorianCalendar.DATE));
        } else {
            return false;
        }
    }

}

