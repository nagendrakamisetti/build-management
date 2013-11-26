/*
 * Copyright 2000-2003 by Model N, Inc.  All Rights Reserved.
 * 
 * This software is the confidential and proprietary information
 * of Model N, Inc ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you
 * entered into with Model N, Inc.
 */
package com.modeln.build.perforce;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.log4j.Logger;


/**
 * Obtains user information from the Perforce server.
 *
 * @author Shawn Stafford
 */
public class User {

	/** Log4j */
	private static Logger logger = Logger.getLogger(User.class.getName());

    /** Text label which identifies the username */
    private static final String USER_LABEL = "User:";

    /** Text label which identifies the email address */
    private static final String EMAIL_LABEL = "Email:";

    /** 
     * Text label which identifies the date when the 
     * user info was last updated. 
     */
    private static final String UPDATE_LABEL = "Update:";

    /** 
     * Text label which identifies the date when the 
     * user last issued a client command. 
     */
    private static final String ACCESS_LABEL = "Access:";

    /** Text label which identifies the full name of the user */
    private static final String FULLNAME_LABEL = "FullName:";

    /** 
     * Text label which identifies the list of jobs to present to the 
     * user during changelist submission. 
     */
    private static final String JOBVIEW_LABEL = "JobView:";

    /** 
     * Text label which identifies the list of depots that the 
     * user wishes to monitor. 
     */
    private static final String REVIEWS_LABEL = "Reviews:";

    /** Text label which identifies the password for the user */
    private static final String PASSWORD_LABEL = "Password:";


    /** Username */
    private String username;

    /** Password */
    private String password;

    /** E-mail address */
    private String email;

    /** Date when the user information was last modified */
    private Date modificationDate;

    /** Date when the user last issued a client command */
    private Date accessDate;

    /** Full name of the user */
    private String fullname;

    /** Expression used to select which jobs are submitted by default */
    private String jobview;

    /** List of depots that the user would like to monitor */
    private Vector reviews = new Vector();

    /**
     * Construct a new user
     */
    private User() {
    }

    /**
     * Set the username
     *
     * @param   name   Username of the user
     */
    public void setUsername(String name) {
        username = name;
    }

    /**
     * Return the username
     *
     * @return     Username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Set the password
     *
     * @param   name   Password of the user
     */
    public void setPassword(String pwd) {
        password = pwd;
    }

    /**
     * Return the password for the user
     *
     * @return     Password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set the e-mail address of the user
     *
     * @param   addr   E-mail address
     */
    public void setEmailAddress(String addr) {
        email = addr;
    }

    /**
     * Return the e-mail address of the user
     *
     * @return     E-mail address
     */
    public String getEmailAddress() {
        return email;
    }

    /**
     * Set the full name of the user.
     *
     * @param   name   Full name of the user
     */
    public void setFullname(String name) {
        fullname = name;
    }

    /**
     * Return the full name of the user
     *
     * @return     Full name
     */
    public String getFullname() {
        return fullname;
    }

    /**
     * Set the date when the user information was last modified.
     *
     * @param   date   Modification date
     */
    public void setModificationDate(Date date) {
        modificationDate = date;
    }

    /**
     * Return the date when the user information was last modified.
     *
     * @return     Last modification date
     */
    public Date getModificationDate() {
        return modificationDate;
    }

    /**
     * Set the date when the user last issued a client command.
     *
     * @param   date   Access date
     */
    public void setAccessDate(Date date) {
        accessDate = date;
    }

    /**
     * Return the date when the user last issued a client command.
     *
     * @return     Last access date
     */
    public Date getAccessDate() {
        return accessDate;
    }


    /**
     * Parse the perforce output generated by a full user entry.
     * This user output can be obtained by querying a specific
     * username:
     * <blockquote>
     *   p4 user -o username
     * </blockquote>
     */
    public static User parseLongFormat(String text) {
        User user = new User();

        StringTokenizer lines = new StringTokenizer(text, "\n");

        String currentLine = null;
        if (lines.hasMoreTokens()) {
            currentLine = lines.nextToken();
            // Discard comment lines
            while (currentLine.startsWith(Common.COMMENT_STRING)) {
                currentLine = lines.nextToken();
            }

            // Obtain the username
            if (currentLine.startsWith(USER_LABEL)) {
                user.setUsername(currentLine.substring(USER_LABEL.length() + 1).trim());
                if (lines.hasMoreTokens()) {
                    currentLine = lines.nextToken();  // discard blank line
                }
            } else {
                logger.error("Unexpected username: " + currentLine);
            }

            // Obtain the email address
            if (currentLine.startsWith(EMAIL_LABEL)) {
                user.setEmailAddress(currentLine.substring(EMAIL_LABEL.length() + 1).trim());
                if (lines.hasMoreTokens()) {
                    currentLine = lines.nextToken();  // discard blank line
                }
            } else {
                logger.error("Unexpected e-mail address: " + currentLine);
            }

            // Obtain the date of last update
            if (currentLine.startsWith(UPDATE_LABEL)) {
                currentLine = currentLine.substring(UPDATE_LABEL.length() + 1).trim();
                try {
                    user.setModificationDate(Common.LONG_DATE_FORMAT.parse(currentLine));
                } catch (ParseException ex) {
                    logger.error("Unable to parse date: " + currentLine);
                }
                if (lines.hasMoreTokens()) {
                    currentLine = lines.nextToken();  // discard blank line
                }
            } else {
                logger.error("Unexpected modification date: " + currentLine);
            }

            // Obtain the last access date
            if (currentLine.startsWith(ACCESS_LABEL)) {
                currentLine = currentLine.substring(ACCESS_LABEL.length() + 1).trim();
                try {
                    user.setAccessDate(Common.LONG_DATE_FORMAT.parse(currentLine));
                } catch (ParseException ex) {
                    logger.error("Unable to parse date: " + currentLine);
                }
                if (lines.hasMoreTokens()) {
                    currentLine = lines.nextToken();  // discard blank line
                }
            } else {
                logger.error("Unexpected access date: " + currentLine);
            }

            // Obtain the full user name
            if (currentLine.startsWith(FULLNAME_LABEL)) {
                user.setFullname(currentLine.substring(FULLNAME_LABEL.length() + 1).trim());
                if (lines.hasMoreTokens()) {
                    currentLine = lines.nextToken();  // discard blank line
                }
            } else {
                logger.error("Unexpected full name: " + currentLine);
            }
        
        }

        return user;
    }


    /**
     * This method uses the Perforce command-line client to obtain information
     * about the current user.  This information is obtained by using the
     * following Perforce command:
     * <blockquote>
     *   p4 user -o
     * </blockquote>
     *
     * @return  Perforce user information
     */
    public static User getUser() {
        User user = null;

        try {
            Process p4 = Common.exec("p4 user -o");
            String text = Common.readInput(p4.getInputStream());
            if ((text != null) && (text.length() > 0)) {
                user = parseLongFormat(text);
            }
        } catch (IOException ex) {
            logger.error("Unable to perform the Perforce command.");
            ex.printStackTrace();
	    } catch (InterruptedException ex) {
	        ex.printStackTrace();
	    } catch (RuntimeException ex) {
	        ex.printStackTrace();
        }

        return user;
    }

    /**
     * This method uses the Perforce command-line client to obtain information
     * about the specified user.  This information is obtained by using the
     * following Perforce command:
     * <blockquote>
     *   p4 user -o username
     * </blockquote>
     *
     * @param   username      Perforce user
     * @return  Perforce user information
     */
    public static User getUser(String username) {
        User user = null;

        try {
            Process p4 = Common.exec("p4 user -o " + username);
            String text = Common.readInput(p4.getInputStream());
            if ((text != null) && (text.length() > 0)) {
                user = parseLongFormat(text);
            }
        } catch (IOException ex) {
            logger.error("Unable to perform the Perforce command.");
            ex.printStackTrace();
	    } catch (InterruptedException ex) {
	        ex.printStackTrace();
	    } catch (RuntimeException ex) {
	        ex.printStackTrace();
        }

        return user;
    }

}
