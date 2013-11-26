/*
 * PhoneNumber.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.common.data.phone;


/**
 * The PhoneNumber interface provides a generalized representation
 * of a global phone number.  Attributes common to all phone numbers
 * include a country code (used for international dialing), an area
 * code (used to identify local regions), and a local phone number.
 * <p>
 * Additional information can be found at the following sources:<br>
 * <a href="http://www.wtng.info">www.wtng.info</a>
 * <p>
 *
 * @version            $Revision: 1.1.1.1 $
 * @author             Shawn Stafford
 *
 */
public interface PhoneNumber {

    /** 
     * Performs no formatting on the phone number, containing no
     * delimiters or non-digit characters:
     * <code>9999999999</code> 
     */
    public static final int NULL_FORMAT = 0;

    /** 
     * Formats the phone number with standard dash delimiters: 
     * <code>999-999-9999</code> 
     */
    public static final int STANDARD_FORMAT = 1;

    /** 
     * Formats the phone number in a format commonly used locally 
     * within the region.  For example, the United States commonly
     * displays parenthetical area code delimiters: 
     * <code>(999) 999-9999</code>
     */
    public static final int LOCAL_FORMAT = 2;

    /** 
     * Formats the phone number with whitespace delimiters: 
     * <code>ddd ddd dddd</code>
     */
    public static final int WHITESPACE_FORMAT = 3;


    /** Serves as a standard land-based voice phone */
    public static final int VOICE_PHONE = 0;

    /** Serves as a mobile/cellular phone */
    public static final int MOBILE_PHONE = 1;

    /** Serves as a fax machine */
    public static final int FAX_PHONE = 2;

    /** Accepts computer dial-up connections */
    public static final int DATA_PHONE = 3;

    /** Serves as a pager */
    public static final int PAGER_PHONE = 4;

    /** Unspecified phone function */
    public static final int OTHER_PHONE = 5;

    /**
     * Sets the primary function of the phone.  Phone functions
     * indicate the type of phone associated with this number,
     * such as mobile phones, fax machines, and computer data lines.
     *
     * @param   function    Function code which identifies the primary phone use
     */
    public void setPhoneFunction(int function);

    /**
     * Returns the primary function of the phone.  Phone functions
     * indicate the type of phone associated with this number,
     * such as mobile phones, fax machines, and computer data lines.
     *
     * @return Function code which identifies the primary phone use
     */
    public int getPhoneFunction();

    /**
     * Sets the country code.
     *
     * @param   code    country code
     */
    public void setCountryCode(String code);

    /**
     * Returns the country code.
     * 
     * @return  Country code string
     */
    public String getCountryCode();

    /**
     * Sets the area code.
     *
     * @param   code    area code
     */
    public void setAreaCode(String code);

    /**
     * Returns the area code.
     * 
     * @return  Area code string
     */
    public String getAreaCode();

    /**
     * Sets the local phone number.
     *
     * @param   number  unformatted (digits only) local phone number
     */
    public void setLocalNumber(String number);

    /**
     * Returns the unformatted (digits only) local 
     * phone number.
     * 
     * @return  unformatted local phone number
     */
    public String getLocalNumber();

    /**
     * Sets the phone extension.
     *
     * @param   ext  phone extension to perform internal routing
     */
    public void setExtension(String ext);

    /**
     * Returns the phone extension to perform internal routing
     * to a particular internal recipient.
     * 
     * @return  unformatted phone extension
     */
    public String getExtension();


    /**
     * Returns the phone number as a string.
     */
    public String toString();

    /**
     * Returns the phone number as a string.  
     *
     * @param   format  Format to be used when constructing the string
     */
    public String toString(int format);

}
