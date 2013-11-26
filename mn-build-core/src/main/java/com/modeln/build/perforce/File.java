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
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.log4j.Logger;


/**
 * Information about a Perforce file.
 *
 * @author Shawn Stafford
 */
public class File {

	/** Log4j */
	private static Logger logger = Logger.getLogger(File.class.getName());

    /** Indicates that the file status is ADD */
    public static final String STATUS_ADD = "add";
    
    /** Indicates that the file status is EDIT */
    public static final String STATUS_EDIT = "edit";

    /** Indicates that the file status is DELETE */
    public static final String STATUS_DELETE = "delete";

    /** Path to the file */
    private String _location;
    
    /** Revision number of the file */
    private int _revision;

    /** Status of the file */
    private String _status;
    
    /**
     * Set the path to the file.
     *
     * @param path  File _location
     */
    public void setLocation(String path) {
        _location = path;
    }
    
    /**
     * Return the path to the file.
     *
     * @return File _location
     */
    public String getLocation() {
        return _location;
    }
    
    /**
     * Set the revision number of the file.
     *
     * @param rev  revision number
     */
    public void setRevision(int rev) {
        _revision = rev;
    }
    
    /**
     * Return the revision number of the file.
     *
     * @return File revision number
     */
    public int getRevision() {
        return _revision;
    }
    
    /**
     * Set the file status.
     *
     * @param state  File status
     */
    public void setState(String state) {
        _status = state;
    }
    
    /**
     * Return the file status.
     *
     * @return File status
     */
    public String getState() {
        return _status;
    }
    
    /**
     * Render the file information as a string.
     *
     * @return String representation of the file information
     */
    public String toString() {
        StringBuffer str = new StringBuffer();
        str.append(_location);
        str.append("#" + _revision);
        
        return str.toString();
    }
    
    /**
     * Construct an empty file object.
     */
    private File() {
    }
    
    /**
     * Construct a file object by parsing the file information.  The 
     * format of the text is as follows:
     * <blockquote>
     *   [depot]#[rev] - [_status] [changelist] [format]
     * </blockquote>
     * Some examples of this text are as follows:
     * <blockquote>
     *   //modeln_build/ant/lib/hibernate3.jar#1 - add change 174401 (binary)  <br/>
     *   //modeln_build/src/com/modeln/build/perforce/File.java#1 - add default change (text)
     * </blockquote>
     *
     * @return Information about the current file integration
     */
    public File(String fileinfo) {
        StringTokenizer st = new StringTokenizer(fileinfo);
        if (st.hasMoreTokens()) {
            String srcFile = st.nextToken();
            int revMark = srcFile.indexOf("#");

            if ((revMark > 0) && (srcFile.length() > revMark + 1)) {
                setLocation(srcFile.substring(0, revMark));
                String revText = srcFile.substring(revMark + 1);
                try {
                    setRevision(Integer.parseInt(revText));
                } catch (NumberFormatException ex) {
                    logger.error("Unable to parse file revision: " + revText);
                }
            } else {
                logger.error("Unable to parse file information: " + fileinfo);
            }
        }
    }
    
    /**
     * Obtain a list of all opened files.
     * This is equivalent to executing the following p4 command: 
     * <blockquote>
     *   p4 opened
     * </blockquote>
     */
    public static List getOpen() {
        Vector files = new Vector();

        try {
            Process p4 = Common.exec("p4 opened");
            BufferedReader p4in = new BufferedReader(new InputStreamReader(p4.getInputStream()));
            String currentLine = p4in.readLine();
            if ((currentLine != null) && (currentLine.indexOf("already integrated.") < 0)) {
                while (currentLine != null) {
                    File currentFile = new File(currentLine);
                    if (currentFile != null) {
                        files.add(currentFile);
                    } else {
                        logger.error("Unable to parse open file: " + currentLine);
                    }
                    currentLine = p4in.readLine();
                }
            }
        } catch (IOException ex) {
            logger.error("Unable to perform the Perforce command.");
            ex.printStackTrace();
	    } catch (InterruptedException ex) {
	        ex.printStackTrace();
	    } catch (RuntimeException ex) {
	        ex.printStackTrace();
        }
        
        return files;
    }
    
}
