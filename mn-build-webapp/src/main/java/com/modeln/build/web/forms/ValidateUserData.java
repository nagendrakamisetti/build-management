/*
 * ValidateUserData.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.web.forms;

import com.modeln.build.common.data.account.UserData;
import com.modeln.build.web.errors.*;

/**
 * The ValidateUserData class contains field validation methods for
 * the field values used to create a UserData object.  If a value
 * is not valid for its associated field, the appropriate ErrorMap
 * code will be returned indicating the cause of the error.
 *
 * @version            $Revision: 1.1.1.1 $
 * @author             Shawn Stafford
 *
 */
public class ValidateUserData {

    public static final int MIN_USERNAME_LENGTH =  3;
    public static final int MAX_USERNAME_LENGTH = 32;
    public static final int MIN_PASSWORD_LENGTH =  6;
    public static final int MAX_PASSWORD_LENGTH = 32;

    public static final String usernameCharSet = 
        "abcdefghijklmnopqrstuvwxyz" + 
        "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
        "1234567890-.";

    public static final String passwordCharSet = 
        "abcdefghijklmnopqrstuvwxyz" + 
        "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
        "1234567890-_.~@!#$%^&*?";


    /**
     * Validate the username field.
     *
     * @param   username    Value to be validated
     */
    public static int validateUsername(String username) {
        if ((username != null) && (username.length() > 0)) {
            if (username.length() < MIN_USERNAME_LENGTH) {
                return ErrorMap.LOGIN_TOO_SHORT;
            }

            if (username.length() > MAX_USERNAME_LENGTH) {
                return ErrorMap.LOGIN_TOO_LONG;
            }

            if (!isValidString(username, usernameCharSet)) {
                return ErrorMap.INVALID_LOGIN_CHARACTER;
            }
        } else {
            return ErrorMap.LOGIN_TOO_SHORT;
        }

        return ErrorMap.NO_ERROR;
    }

    /**
     * Validate the password field.
     *
     * @param   password    Value to be validated
     */
    public static int validatePassword(String password) {
        if ((password != null) && (password.length() > 0)) {
            if (password.length() < MIN_PASSWORD_LENGTH) {
                return ErrorMap.PASSWORD_TOO_SHORT;
            }

            if (password.length() > MAX_PASSWORD_LENGTH) {
                return ErrorMap.PASSWORD_TOO_LONG;
            }

            if (!isValidString(password, passwordCharSet)) {
                return ErrorMap.INVALID_PASSWORD_CHARACTER;
            }

        } else {
            return ErrorMap.PASSWORD_TOO_SHORT;
        }

        return ErrorMap.NO_ERROR;
    }


    /**
     * Determine whether the string contains valid characters.
     *
     * @param   str     String to be validated
     * @param   set     Set of valid characters
     * @return  TRUE if the string contains only valid characters
     */
    public static boolean isValidString(String str, String set) {
        boolean valid = true;

        // Check every letter in the string for validity
        for (int idx = 0; idx < str.length(); idx++) {
            char ch = str.charAt(idx);
            if (set.indexOf(ch) < 0) {
                return false;
            }
        }

        return valid;
    }

    /**
     * Determine whether one string is a circular combination of the other.
     * This is most often used to ensure that the password does not contain
     * some combination of another element such as the username.
     *
     * @param   str     String to be validated
     * @param   ref     Reference string
     * @return  TRUE if the string is a circular variation of the reference string
     */
    public static boolean isCircular(String str, String ref) {
        String reverseStr = "";
        int len = str.length();

        //Password = reverse(userid) ?
        for (int o = len - 1; o >= 0; reverseStr += str.charAt(o--));
        if (ref.equals(reverseStr)) { return true; }
    
        //Password = userid or duseri or seridu etc. ?
        for (int i = 0; i < len; i++) {
            if (ref.equals(str.substring(i, len) + str.substring(0, i))) { 
                return true; 
            }
        }
    
        return false;
    }

}
