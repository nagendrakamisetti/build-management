/**
 * DateTag.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.web.tags;
 
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.servlet.http.*;
import com.modeln.build.web.tags.InputTag;
import com.modeln.build.web.tags.SelectTag;
import com.modeln.build.util.DateUtility;
import com.modeln.build.util.StringUtility;

/**
 * The DateTag object constructs a grouping of HTML input elements
 * which allow the user to specify a particular date.
 *
 * @version            $Revision: 1.2 $
 * @author             Shawn Stafford
 */
public class DateTag extends InputTag {

    /** Name used to identify the month tag */
    private static final String monthTagName = "Month";

    /** Name used to identify the day tag */
    private static final String dayTagName = "Day";

    /** Name used to identify the year tag */
    private static final String yearTagName = "Year";

    /** Name used to identify the hour tag */
    private static final String hourTagName = "Hour";

    /** Name used to identify the minute tag */
    private static final String minuteTagName = "Minute";

    /** Name used to identify the second tag */
    private static final String secondTagName = "Second";


    /** List of the months */
    private static final String months[] = {
        "January", "February", "March", "April", "May", "June", 
        "July", "August", "September", "October", "November", "December"
    };

    /** Earliest date the user will be allowed to specify */
    private GregorianCalendar beginDate = new GregorianCalendar();

    /** Latest date the user will be allowed to specify */
    private GregorianCalendar endDate = new GregorianCalendar();


    /** Tag representing the month */
    private SelectTag monthTag;

    /** Tag representing the day of the month */
    private SelectTag dayTag;

    /** Tag representing the year */
    private SelectTag yearTag;

    /** Tag representing the hour */
    private SelectTag hourTag;

    /** Tag representing the minutes */
    private SelectTag minuteTag;

    /** Tag representing the seconds */
    private SelectTag secondTag;

    /** Determines whether the time fields should be visible */
    private boolean hideTime = true;

    /** Determines whether the user must specify a value */
    private boolean required = false;

    /**
     * Construct the date input object.
     *
     * @param   name    Name used to identify the form elements
     * @param   begin   Earliest date the user will be allowed to enter
     * @param   end     Latest date the user will be allowed to enter
     */
    public DateTag(String name) {
        super(name);

        int year = endDate.get(GregorianCalendar.YEAR);
        beginDate.set(GregorianCalendar.YEAR, year - 100);
        initTag(true);
    }

    /**
     * Construct the date input object.
     *
     * @param   name    Name used to identify the form elements
     * @param   begin   Earliest date the user will be allowed to enter
     * @param   end     Latest date the user will be allowed to enter
     */
    public DateTag(String name, Date begin, Date end) {
        super(name);
        beginDate.setTime(begin);
        endDate.setTime(end);
        initTag(true);
    }

    /**
     * Construct the date input object.
     *
     * @param   name       Name used to identify the form elements
     * @param   begin      Earliest date the user will be allowed to enter
     * @param   end        Latest date the user will be allowed to enter
     * @param   required   Determines whether the values can be empty
     */
    public DateTag(String name, Date begin, Date end, boolean required) {
        super(name);
        beginDate.setTime(begin);
        endDate.setTime(end);
        initTag(required);
    }


    /**
     * Initialize the tag objects.
     *
     * @param   required   Determines whether the values can be empty
     */
    private void initTag(boolean required) {
        this.required = required;

        // Construct the day of the month
        int dateIdx = 0;
        int date = 1;
        String[] dateArray = null;
        if (required) {
            dateArray = new String[31];
        } else {
            dateArray = new String[32];
            dateArray[0] = "";
            dateIdx = 1;
        }
        while (dateIdx < dateArray.length) {
            dateArray[dateIdx] = Integer.toString(date);
            date++;
            dateIdx++;
        }
        dayTag = new SelectTag(getName() + dayTagName, dateArray);
        dayTag.setKeyOrder(dateArray);
    
        // Construct the month tag
        Hashtable monthList = new Hashtable();
        String[][] monthArray = new String[months.length][2];
        Vector monthOrder = new Vector();
        if (!required) {
            monthList.put("", "");
            monthOrder.add("");
        }
        for (int idx=0; idx < months.length; idx++) {
            monthList.put(Integer.toString(idx), months[idx]);
            monthOrder.add(Integer.toString(idx));
        }
        monthTag = new SelectTag(getName() + monthTagName, monthList);
        monthTag.setKeyOrder(monthOrder);

        // Construct the year tag
        Vector years = new Vector();
        int beginYear = beginDate.get(GregorianCalendar.YEAR);
        int endYear = endDate.get(GregorianCalendar.YEAR);
        if (!required) {
            years.add("");
        }
        for (int idx=beginYear; idx <= endYear; idx++) {
            years.add(Integer.toString(idx));
        }
        yearTag = new SelectTag(getName() + yearTagName, years);
        yearTag.setSorting(true);

        // Construct the hour tag
        int hourIdx = 0;
        int hour = 0;
        String[] hourArray = null;
        if (required) {
            hourArray = new String[24];
        } else {
            hourArray = new String[25];
            hourArray[0] = "";
            hourIdx = 1;
        }
        while (hourIdx < hourArray.length) {
            hourArray[hourIdx] = Integer.toString(hour);
            hour++;
            hourIdx++;
        }
        hourTag = new SelectTag(getName() + hourTagName, hourArray);
        hourTag.setKeyOrder(hourArray);

        // Construct the minute tag
        int minuteIdx = 0;
        int minute = 0;
        String[] minuteArray = null;
        if (required) {
            minuteArray = new String[60];
        } else {
            minuteArray = new String[61];
            minuteArray[0] = "";
            minuteIdx = 1;
        }
        while (minuteIdx < minuteArray.length) {
            minuteArray[minuteIdx] = Integer.toString(minute);
            minute++;
            minuteIdx++;
        }
        minuteTag = new SelectTag(getName() + minuteTagName, minuteArray);
        minuteTag.setKeyOrder(minuteArray);

        // Construct the second tag
        int secondIdx = 0;
        int second = 0;
        String[] secondArray = null;
        if (required) {
            secondArray = new String[60];
        } else {
            secondArray = new String[61];
            secondArray[0] = "";
            secondIdx = 1;
        }
        while (secondIdx < secondArray.length) {
            secondArray[secondIdx] = Integer.toString(second);
            second++;
            secondIdx++;
        }
        secondTag = new SelectTag(getName() + secondTagName, secondArray);
        secondTag.setKeyOrder(secondArray);


        // Set the default date to the end, since that is the common case
        if (required) {
            setDefault(endDate);
        } else {
            setDefault();
        }

        // The default is to hide the time fields
        setTimeHidden(true);
    }


    /**
     * Checks to see whether the user must specify a date value.
     *
     * @return  TRUE if the form should force the user to select a value 
     */
    public boolean isRequired() {
        return required;
    }


    /**
     * Checks to see of the overall form has been completed successfully.
     *
     * @return  TRUE if the form is complete.
     */
    public boolean isComplete() {
        boolean complete = (monthTag.isComplete() && dayTag.isComplete() && yearTag.isComplete());
        return complete;
    }

    /**
     * Override the inherited default selection option
     */
    private void setDefault(String key) throws ParseException {
        DateFormat fmt = new SimpleDateFormat();
        if (key != null) {
            Date date = fmt.parse(key);
            GregorianCalendar cal = new GregorianCalendar();
            cal.setTime(date);
            setDefault(cal);
        } else {
            setDefault();
        }
    }

    /**
     * Set the default to null.
     */
    public void setDefault() {
        monthTag.setSelected("");
        dayTag.setSelected("");
        yearTag.setSelected("");
        hourTag.setSelected("");
        minuteTag.setSelected("");
        secondTag.setSelected("");
    }

    /**
     * Sets the default selection.
     *
     * @param   date    Selected date
     */
    public void setDefault(GregorianCalendar date) {
        if (date != null) {
            monthTag.setDefault(Integer.toString(date.get(GregorianCalendar.MONTH)));
            dayTag.setDefault(Integer.toString(date.get(GregorianCalendar.DATE)));
            yearTag.setDefault(Integer.toString(date.get(GregorianCalendar.YEAR)));
            hourTag.setDefault(Integer.toString(date.get(GregorianCalendar.HOUR_OF_DAY)));
            minuteTag.setDefault(Integer.toString(date.get(GregorianCalendar.MINUTE)));
            secondTag.setDefault(Integer.toString(date.get(GregorianCalendar.SECOND)));
        } else {
            setDefault(); 
        }
    }

    /**
     * Sets the currently selected date to the given value.
     *
     * @param   date    Selected date
     */
    public void setDate(GregorianCalendar date) {
        if (date != null) {
            monthTag.setSelected(Integer.toString(date.get(GregorianCalendar.MONTH)));
            dayTag.setSelected(Integer.toString(date.get(GregorianCalendar.DATE)));
            yearTag.setSelected(Integer.toString(date.get(GregorianCalendar.YEAR)));
            hourTag.setSelected(Integer.toString(date.get(GregorianCalendar.HOUR_OF_DAY)));
            minuteTag.setSelected(Integer.toString(date.get(GregorianCalendar.MINUTE)));
            secondTag.setSelected(Integer.toString(date.get(GregorianCalendar.SECOND)));
        } else {
            setDefault(); 
        }
    }

    /**
     * Looks at the request attributes without setting the field to figure
     * out if a value is available.
     *
     * @param  req      HTTP request
     */
    public boolean isValueAvailable(HttpServletRequest req) {
        return (monthTag.isValueAvailable(req) && 
                dayTag.isValueAvailable(req) && 
                yearTag.isValueAvailable(req) && 
                hourTag.isValueAvailable(req) && 
                minuteTag.isValueAvailable(req) &&
                secondTag.isValueAvailable(req));
    }

    /**
     * Set the selected date by examining the HTTP request to see if
     * a value was submitted.
     *
     * @param   req     HTTP request
     */
    public void setValue(HttpServletRequest req) {
        monthTag.setValue(req);
        dayTag.setValue(req);
        yearTag.setValue(req);
        hourTag.setValue(req);
        minuteTag.setValue(req);
        secondTag.setValue(req);
    }

    /**
     * Returns the date specified by the tag.
     *
     * @return  Date
     */
    public Date getDate() {
        String[] selectedYear   = yearTag.getSelected();
        String[] selectedMonth  = monthTag.getSelected();
        String[] selectedDay    = dayTag.getSelected();
        String[] selectedHour   = hourTag.getSelected();
        String[] selectedMinute = minuteTag.getSelected();
        String[] selectedSecond = secondTag.getSelected();

        String year = null;
        if ((selectedYear != null) && (selectedYear.length > 0)) {
            year = selectedYear[0];
        }

        String month = null;
        if ((selectedMonth != null) && (selectedMonth.length > 0)) {
            month = selectedMonth[0];
        }

        String day = null;
        if ((selectedDay != null) && (selectedDay.length > 0)) {
            day = selectedDay[0];
        }

        String hour = null;
        if ((selectedHour != null) && (selectedHour.length > 0)) {
            hour = selectedHour[0];
        }

        String minute = null;
        if ((selectedMinute != null) && (selectedMinute.length > 0)) {
            minute = selectedMinute[0];
        }

        String second = null;
        if ((selectedSecond != null) && (selectedSecond.length > 0)) {
            second = selectedSecond[0];
        }

        return parse(month, day, year, hour, minute, second);
    }


    /**
     * Returns the default date.
     *
     * @return  Date
     */
    public Date getDefault() {
        String year = yearTag.getDefault();
        String month = monthTag.getDefault();
        String day = dayTag.getDefault();
        String hour = hourTag.getDefault();
        String minute = minuteTag.getDefault();
        String second = secondTag.getDefault();

        return parse(year, month, day, hour, minute, second);
    }

    /**
     * Parse the HTTP request attributes to determine the date indicated
     * by the values submitted.
     *
     * @param   req     HTTP request
     * @return  Date found in the request
     */
    public Date parse(HttpServletRequest req) {
        String month  = (String) req.getParameter(monthTag.getName());
        String day    = (String) req.getParameter(dayTag.getName());
        String year   = (String) req.getParameter(yearTag.getName());
        String hour   = (String) req.getParameter(hourTag.getName());
        String minute = (String) req.getParameter(minuteTag.getName());
        String second = (String) req.getParameter(secondTag.getName());

        return parse(month, day, year, hour, minute, second);
    }

    /**
     * Parse the date fields.
     */
    private Date parse(String month, String day, String year, String hour, String minute, String second) {
        try {
            int valMonth = 0;
            int valDay = 0;
            int valYear = 0;
            int valHour = 0;
            int valMinute = 0;
            int valSecond = 0;

            boolean monthComplete = false;
            boolean dayComplete = false;
            boolean yearComplete = false;
            boolean hourComplete = false;
            boolean minuteComplete = false;
            boolean secondComplete = false;

            if ((month != null) && (month.trim().length() > 0)) {
                valMonth = Integer.parseInt(month);
                monthComplete = true;
            }

            if ((day != null) && (day.trim().length() > 0)) {
                valDay = Integer.parseInt(day);
                dayComplete = true;
            }

            if ((year != null) && (year.trim().length() > 0)) {
                valYear = Integer.parseInt(year);
                yearComplete = true;
            }

            if ((hour != null) && (hour.trim().length() > 0)) {
                valHour = Integer.parseInt(hour);
                hourComplete = true;
            }

            if ((minute != null) && (minute.trim().length() > 0)) {
                valMinute = Integer.parseInt(minute);
                minuteComplete = true;
            }

            if ((second != null) && (second.trim().length() > 0)) {
                valSecond = Integer.parseInt(second);
                secondComplete = true;
            }

            // Only parse the date if the month/day/year is valid
            if (monthComplete && dayComplete && yearComplete) {
                GregorianCalendar calendar = new GregorianCalendar(
                    valYear, valMonth, valDay, valHour, valMinute, valSecond 
                );
                return calendar.getTime();
            } else {
                return null;
            }
        } catch (Exception ex) {
            return null;
        }
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


    /**
     * Returns the HTML representation of the hidden tag object.
     *
     * @return  HTML tag representation
     */
    public String getHiddenTag() {
        StringBuffer html = new StringBuffer();
        
        html.append(monthTag.toString() + "\n");
        html.append(dayTag.toString() + "\n");
        html.append(yearTag.toString() + "\n");
        html.append(hourTag.toString() + "\n");
        html.append(minuteTag.toString() + "\n");
        html.append(secondTag.toString() + "\n");

        return html.toString();
    }

    /**
     * Returns the HTML representation of the visible tag object.
     *
     * @return  HTML tag representation
     */
    public String getVisibleTag() {
        StringBuffer html = new StringBuffer();

        // Construct a table containing a month, day, and year element
        html.append("\n<table border=\"0\" cellspacing=\"2\" cellpadding=\"2\">\n");
        html.append("  <tr>\n");

        html.append("    <td>\n");
        html.append(monthTag.toString() + "\n");
        html.append("    </td>\n");

        html.append("    <td>\n");
        html.append(dayTag.toString() + "\n");
        html.append("    </td>\n");

        html.append("    <td>\n");
        html.append(yearTag.toString() + "\n");
        html.append("    </td>\n");

        // Generate input fields in the table if time is visible
        if (!hideTime) {
            html.append("    <td>\n");
            html.append(hourTag.toString() + "\n");
            html.append("    </td>\n");

            html.append("    <td>\n");
            html.append(minuteTag.toString() + "\n");
            html.append("    </td>\n");

            html.append("    <td>\n");
            html.append(secondTag.toString() + "\n");
            html.append("    </td>\n");
        }

        html.append("  </tr>\n");
        html.append("</table>\n");

        // Generate hidden fields outside of the table if time is not visible
        if (hideTime) {
            html.append(hourTag.toString() + "\n");
            html.append(minuteTag.toString() + "\n");
            html.append(secondTag.toString() + "\n");
        }

        return html.toString();
    }


    /**
     * Enables and disables the input list.
     *
     * @param   status  TRUE if the element should appear disabled
     */
    public void setDisabled(boolean status) {
        super.setDisabled(status);
        monthTag.setDisabled(status);
        dayTag.setDisabled(status);
        yearTag.setDisabled(status);
        hourTag.setDisabled(status);
        minuteTag.setDisabled(status);
        secondTag.setDisabled(status);
    }


    /**
     * Hides the date input tag.  A separate call must be made to 
     * show or hide the time input tags.
     *
     * @param   hide    Hide the input tag if TRUE
     */
    public void setHidden(boolean hide) {
        super.setHidden(hide);
        monthTag.setHidden(hide);
        dayTag.setHidden(hide);
        yearTag.setHidden(hide);
    }

    /**
     * Hides only the time portion of the input tag.
     *
     * @param   hide    Hide the time input tag if TRUE
     */
    public void setTimeHidden(boolean hide) {
        hideTime = hide;
        hourTag.setHidden(hide);
        minuteTag.setHidden(hide);
        secondTag.setHidden(hide);
    }


    /**
     * Sets the tool tip associated with the tag.
     *
     * @param   tip     Text to use as a tip
     */
    public void setToolTip(String tip) {
        super.setToolTip(tip);
        monthTag.setToolTip(tip);
        dayTag.setToolTip(tip);
        yearTag.setToolTip(tip);
        hourTag.setToolTip(tip);
        minuteTag.setToolTip(tip);
        secondTag.setToolTip(tip);
    }

    /**
     * Set the inline stylesheet for the tag.
     *
     * @param   css     Inline stylesheet entry
     */
    public void setStyle(String css) {
        super.setStyle(css);
        monthTag.setStyle(css);
        dayTag.setStyle(css);
        yearTag.setStyle(css);
        hourTag.setStyle(css);
        minuteTag.setStyle(css);
        secondTag.setStyle(css);
    }

    /**
     * Sets the class identifier associated with the tag.
     * This is often used to attach stylesheet formatting.
     *
     * @param   id      Stylesheet class identifier
     */
    public void setStyleClass(String id) {
        super.setStyleClass(id);
        monthTag.setStyleClass(id);
        dayTag.setStyleClass(id);
        yearTag.setStyleClass(id);
        hourTag.setStyleClass(id);
        minuteTag.setStyleClass(id);
        secondTag.setStyleClass(id);
    }

}

