/*
 * NorthAmericanPhoneNumber.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.common.data.phone;


/**
 * The NorthAmericanPhoneNumber class represents a North American
 * telephone number.  Telephone numbers of this class rely on the
 * North American Numbering Plan (NANP) for country code (world
 * zone) 1.  This format relies on a fixed 10-digit number.
 * The numbering format is defined as:
 * <code>
 * (NXX) NXX XXXX
 * </code>
 * 3-digit area code, followed by 7-digit local number.
 * <p>
 * Additional information can be found at the following sources:<br>
 * <a href="http://www.wtng.info">www.wtng.info</a>
 * <p>
 *
 * @version            $Revision: 1.1.1.1 $
 * @author             Shawn Stafford
 *
 */
public class NorthAmericanPhoneNumber implements PhoneNumber {

    /** Matches a phone number of the format: <code>ddd-dddd</code> */
    public static final String LOCAL_PHONE_REGEX = "\\d{3}-\\d{4}";

    /** Matches phone numbers of the format: <code>(ddd) ddd-dddd</code> */
    public static final String FULL_PHONE_REGEX = "\\(\\d{3}\\)\\w?" + LOCAL_PHONE_REGEX;

    /** 
     * Matches any phone number with a generic NANP format of 
     * <code>ddd ddd dddd</code> where delimiters can be any non-digit
     * character.
     */
    public static final String GENERIC_PHONE_REGEX = "\\D?\\d{3}\\D?\\w?\\d{3}\\D?\\d{4}";

    /** Identifies the primary usage function of the number, such as fax or mobile. */
    private int phoneFunction = VOICE_PHONE;

    /** Identifies the country for international calls. */
    private String countryCode;
    
    /** 
     * Identifies the 3-digit area code of the number identified by 'x': 
     * <code>(xxx) 000 0000</code>
     */
    private String areaCode;

    /** 
     * Identifies the initial 3-digit portion of the phone number 
     * identified by 'x': <code>(000) xxx-0000<code>
     */
    private String phonePrefix;

    /** 
     * Identifies the initial 4-digit portion of the phone number 
     * identified by 'x': <code>(000) 000-xxxx<code>
     */
    private String phonePostfix;

    /**
     * Extension used to reach an internal party through the
     * internal phone system.
     */
    private String phoneExtension;

    /**
     * Construct a blank phone number.
     */
    private NorthAmericanPhoneNumber() {
        countryCode = "";
        areaCode = "";
        phonePrefix = "";
        phonePostfix = "";
        phoneExtension = "";
    }

    /**
     * Construct a phone number object by parsing the given number
     * string.  The phone number is assumed to be in the format of
     * the regular expression <code>GENERIC_PHONE_REGEX</code>.
     * 
     * @param   number  phone number string
     * @throws  NumberFormatException if the string is not a recognizable phone number
     */
    public NorthAmericanPhoneNumber(String number) 
        throws NumberFormatException
    {
        countryCode = "";
        phoneExtension = "";
        parse(number);
    }

    /**
     * Sets the phone extension.
     *
     * @param   ext  phone extension to perform internal routing
     */
    public void setExtension(String ext) {
        phoneExtension = ext;
    }

    /**
     * Returns the phone extension to perform internal routing
     * to a particular internal recipient.
     * 
     * @return  unformatted phone extension
     */
    public String getExtension() {
        return phoneExtension;
    }

    /**
     * Sets the primary function of the phone.  Phone functions
     * indicate the type of phone associated with this number,
     * such as mobile phones, fax machines, and computer data lines.
     *
     * @param   function    Function code which identifies the primary phone use
     */
    public void setPhoneFunction(int function) {
        phoneFunction = function;
    }

    /**
     * Returns the primary function of the phone.  Phone functions
     * indicate the type of phone associated with this number,
     * such as mobile phones, fax machines, and computer data lines.
     *
     * @return Function code which identifies the primary phone use
     */
    public int getPhoneFunction() {
        return phoneFunction;
    }

    /**
     * Return a phone number object by parsing the given number
     * string.  The phone number is assumed to be in the format of
     * the regular expression <code>GENERIC_PHONE_REGEX</code>.
     * 
     * @param   number  phone number string
     * @throws  NumberFormatException if the string is not a recognizable phone number
     */
    private void parse(String number) 
        throws NumberFormatException
    {
        // Remove all non-digit characters
        number = trim(number, GENERIC_PHONE_REGEX);

        switch (number.length()) {
            case 10:
                countryCode = "";
                areaCode = number.substring(0,3);
                phonePrefix = number.substring(3,6);
                phonePostfix = number.substring(6);
                break;
            case 7:
                countryCode = "";
                areaCode = "";
                phonePrefix = number.substring(0,3);
                phonePostfix = number.substring(3);
                break;
            default:
                throw new NumberFormatException("Unrecognized phone number format");
        }
    }

    /**
     * Removes any non-digit characters from the string and returns 
     * a string of pure digit characters.
     *
     * @param   number  String to be parsed
     * @param   regex   Regular expression pattern to be applied to the number
     * @throws  NumberFormatException if the string does not match the regex
     */
    private static String trim(String number, String regex) 
        throws NumberFormatException
    {
        number = number.trim();
        if ((number != null) && (number.length() > 0) && number.matches(regex)) {
            number = number.replaceAll("\\D", "");
        } else {
            throw new NumberFormatException("String does not match the regular expression.");
        }

        return number;
    }

    /**
     * Returns the phone number as a string.
     */
    public String toString() {
        return toString(STANDARD_FORMAT);
    }

    /**
     * Returns the phone number as a string.  
     *
     * @param   format  Format to be used when constructing the string
     */
    public String toString(int format) {
        StringBuffer num = new StringBuffer(16);
        switch (format) {
            case STANDARD_FORMAT:
                if ((areaCode != null) && (areaCode.length() > 0)) {
                    num.append(areaCode + "-");
                }
                num.append(phonePrefix + "-" + phonePostfix);
                break;
            case LOCAL_FORMAT:
                if ((areaCode != null) && (areaCode.length() > 0)) {
                    num.append("(" + areaCode + ") ");
                }
                num.append(phonePrefix + "-" + phonePostfix);
                break;
            case WHITESPACE_FORMAT:
                if ((areaCode != null) && (areaCode.length() > 0)) {
                    num.append(areaCode + " ");
                }
                num.append(phonePrefix + " " + phonePostfix);
                break;
            default: // NULL_FORMAT
                num.append(areaCode + phonePrefix + phonePostfix);
                break;
        }

        return num.toString();
    }

    /**
     * Sets the country code.
     *
     * @param   code    country code
     */
    public void setCountryCode(String code) {
        countryCode = code;
    }

    /**
     * Returns the country code.
     * 
     * @return  Country code string
     */
    public String getCountryCode() {
        return countryCode;
    }

    /**
     * Sets the area code.
     *
     * @param   code    area code
     */
    public void setAreaCode(String code) {
        areaCode = code;
    }

    /**
     * Returns the area code.
     * 
     * @return  Area code string
     */
    public String getAreaCode() {
        return areaCode;
    }

    /**
     * Sets the local phone number.
     *
     * @param   number  7-digit phone number
     */
    public void setLocalNumber(String number) {
        if (number.length() == 7) {
            phonePrefix = number.substring(0,3);
            phonePostfix = number.substring(3);
        }
    }

    /**
     * Returns the 7-digit unformatted (digits only) local 
     * phone number.
     * 
     * @return  7-digit phone number
     */
    public String getLocalNumber() {
        return phonePrefix + phonePostfix;
    }

}

