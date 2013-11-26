/*
 * JspUtility.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.web.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/** 
 * This class provides utility methods commonly useful within JSP pages.
 * 
 * @author             Shawn Stafford
 * @version            $Revision: 1.1.1.1 $ 
 */

public class JspUtility {

    /** Format used to display dates to the user */
    private static final SimpleDateFormat standardDateFormat = new SimpleDateFormat("MM/dd/yyyy");

    /**
     * Ensures that a non-null string is always returned to ensure
     * that NULL is not displayed on the JSP output.
     *
     * @param   data    Data object to be displayed
     * @return  Non-null string
     */
    public static String showUser(Object data) {
        if (data != null) {
            return data.toString();
        } else {
            return "";
        }
    }

    /**
     * Returns the date as a standard short format of <code>MM/dd/yyyy</code>
     *
     * @param   date    Date to be formatted
     * @return  Formatted date string
     */
    public static String getSimpleText(Date date) {
        if (date != null) {
            return standardDateFormat.format(date);
        } else {
            return "";
        }
    }
    
}
