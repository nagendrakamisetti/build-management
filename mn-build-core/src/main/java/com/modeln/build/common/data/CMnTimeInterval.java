package com.modeln.build.common.data;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Date;

/**
 * The time interval defines a start date, end date, and name of the 
 * interval.  This can be a useful data object for defining a list of
 * time intervals, such as fiscal quarters or calendar weeks. 
 *
 * @author       Shawn Stafford (sstafford@modeln.com)
 */
public class CMnTimeInterval implements Comparable {


    /** Name of the time interval */
    private String name;

    /** Starting date of the time interval */
    private Date start;

    /** Ending date of the time interval */
    private Date end;


    /**
     * Construct a new time interval of the specified size
     * which contains the given date.  The interval size
     * is specified using the Calendar fields of 
     * DAY_OF_MONTH, WEEK_OF_MONTH, MONTH, or YEAR.
     *
     * @param   date    Target date contained by the interval
     * @param   size    Interval size (see Calendar fields)
     */
    public CMnTimeInterval(Date date, int size) {
        GregorianCalendar calStart = new GregorianCalendar();
        GregorianCalendar calEnd = new GregorianCalendar();

        calStart.setTime(date);
        calEnd.setTime(date);

        SimpleDateFormat fmt = null;
        boolean zeroMonth = false;
        boolean zeroDate = false;
        boolean zeroTime = false;
        switch (size) {
            case Calendar.YEAR:
                fmt = new SimpleDateFormat("yyyy");
                name = fmt.format(date); 
                zeroMonth = true;
                zeroDate = true;
                zeroTime = true;
                break;
            case Calendar.MONTH:
                fmt = new SimpleDateFormat("MMM yyyy");
                name = fmt.format(date);  
                zeroDate = true;
                zeroTime = true;
                break;
            case Calendar.DAY_OF_MONTH:
                fmt = new SimpleDateFormat("MM/dd");
                name = fmt.format(date); 
                zeroTime = true;
                break;
            case Calendar.WEEK_OF_MONTH:
                fmt = new SimpleDateFormat("MMM dd");
                calStart.set(Calendar.DAY_OF_WEEK, calStart.getActualMinimum(Calendar.DAY_OF_WEEK));
                calEnd.set(Calendar.DAY_OF_WEEK, calEnd.getActualMaximum(Calendar.DAY_OF_WEEK));
                name = fmt.format(calStart.getTime()) + " to " + fmt.format(calEnd.getTime());
                zeroTime = true;
                break;
        }

        // Adjust the start and end date to min and max month in the calendar
        if (zeroMonth) {
            calStart.set(Calendar.MONTH, Calendar.JANUARY);
            calEnd.set(Calendar.MONTH, Calendar.DECEMBER);
        }

        // Adjust the start and end date to min and max day of the month 
        if (zeroDate) {
            calStart.set(Calendar.DAY_OF_MONTH, calStart.getActualMinimum(Calendar.DAY_OF_MONTH));
            calEnd.set(Calendar.DAY_OF_MONTH, calEnd.getActualMaximum(Calendar.DAY_OF_MONTH));
        }

        // Adjust the start and end time to the min and max time 
        if (zeroTime) {
            calStart.set(Calendar.HOUR_OF_DAY, calStart.getActualMinimum(Calendar.HOUR_OF_DAY));
            calStart.set(Calendar.MINUTE, calStart.getActualMinimum(Calendar.MINUTE));
            calStart.set(Calendar.SECOND, calStart.getActualMinimum(Calendar.SECOND));
            calEnd.set(Calendar.HOUR_OF_DAY, calEnd.getActualMaximum(Calendar.HOUR_OF_DAY));
            calEnd.set(Calendar.MINUTE, calEnd.getActualMaximum(Calendar.MINUTE));
            calEnd.set(Calendar.SECOND, calEnd.getActualMaximum(Calendar.SECOND));
        }

        // Initialize the start and end range using the calendar adjustments
        start = calStart.getTime();
        end = calEnd.getTime();
    }
    
    /**
     * Set the starting date for the interval. 
     *
     * @param  start    Starting date 
     */
    public void setStart(Date start) {
        this.start = start;
    }
    
    /**
     * Return the starting date for the interval. 
     *
     * @return Starting date 
     */
    public Date getStart() {
        return start;
    }

    /**
     * Set the ending date for the interval. 
     *
     * @param  end    Ending date 
     */
    public void setEnd(Date end) {
        this.end = end;
    }

    /**
     * Return the ending date for the interval. 
     *
     * @return Ending date 
     */
    public Date getEnd() {
        return end;
    }

    
    /**
     * Set the name of the interval.
     *
     * @param  name   Interval name 
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Return the name of the interval. 
     *
     * @return Interval name 
     */
    public String getName() {
        return name;
    }

    /**
     * Determine if the start and end dates are equal.
     *
     * @param   interval   Interval to compare to
     * @return  TRUE if the target interval is equal
     */
    public boolean equals(CMnTimeInterval interval) {
        boolean sameStart = (start.getTime() == interval.getStart().getTime());
        boolean sameEnd = (end.getTime() == interval.getEnd().getTime());

        return (sameStart && sameEnd);
    }

    /**
     * Compares the two time intervals for equality by comparing the start dates. 
     * If the start dates are equal, the end date is used for comparison.
     * Returns a negative integer, zero, or a positive integer as the first 
     * argument is less than, equal to, or greater than the second.
     *
     * @param  obj   Time interval to compare to 
     * @return a negative integer, zero, or a positive integer 
     */
    public int compareTo(Object obj) {
        CMnTimeInterval interval1 = this;
        CMnTimeInterval interval2 = (CMnTimeInterval) obj;

        if (interval1.equals(interval2)) {
            // Both the start and end date are equal
            return 0;
        } else if (interval1.getStart().equals(interval2.getStart())) {
            // The start date is equal so compare the end date
            return interval1.getEnd().compareTo(interval2.getEnd());
        } else {
            return interval1.getStart().compareTo(interval2.getStart());
        }
    }

    /**
     * Determine if the specified date falls within this
     * time interval.
     *
     * @param  date   Date in question
     */
    public boolean contains(Date date) {
        return ((date.getTime() >= start.getTime()) && (date.getTime() <= end.getTime()));
    }    
}

