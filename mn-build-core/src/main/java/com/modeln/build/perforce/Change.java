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
import java.io.PrintWriter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.NoSuchElementException;
import java.lang.NullPointerException;
import java.lang.Integer;

import org.apache.log4j.Logger;




/**
 * Create or edit a changelist description to the Perforce server.
 *
 * @author Shawn Stafford, Karen An
 */
public class Change {

	/** Log4j */
	private static Logger logger = Logger.getLogger(Change.class.getName());

    /** Text label which identifies the changelist number */
    private static final String CHANGELIST_LABEL = "Change:";

    /** Text label which identifies the changelist submission date */
    private static final String DATE_LABEL = "Date:";

    /** Text label which identifies the clientspec */
    private static final String CLIENTSPEC_LABEL = "Client:";

    /** Text label which identifies the Perforce user */
    private static final String USER_LABEL = "User:";

    /** Text label which identifies the changelist status */
    private static final String STATUS_LABEL = "Status:";

    /** Text label which identifies the changelist description */
    private static final String DESCRIPTION_LABEL = "Description:";

    /** Text label which identifies the changelist description */
    private static final String FILES_LABEL = "Files:";

    /** Change status value of "new" */
    private static final String STATUS_NEW = "new";
    
    /** Change status value of "pending" */
    private static final String STATUS_PENDING = "pending";

    /** Change status value of "submitted" */
    private static final String STATUS_SUBMITTED = "submitted";

    
    /** Change comment header */
    private String comments;
    
    /** Change number */
    private int number;

    /** Date the changelist was submitted */
    private Date date;

    /** Description of the changelist */
    private String description = "";

    /** Person who submitted the changelist */
    private String user;

    /** Perforce client spec used when submitting the changelist */
    private String clientspec;

    /** Status of the changlist (submitted, pending, etc) */
    private String status;

    /** files in the changelist */ 
    private StringBuffer _filesBuffer = new StringBuffer();

    // obsolete!
    /** List of files in the changelist */
    private Vector files = new Vector();

    /**
     * Construct a new changelist
     */
    private Change() {
    }
    
    /**
     * Set the text in the comment header.
     *
     * @param  Comment header text
     */
    public void setComments(String text) {
        comments = text;
    }
    
    /**
     * Return the text in the comment header.
     *
     * @return Change comment header
     */
    public String getComments() {
        return comments;
    }

    /**
     * Set the changelist number.  A changelist number of Zero (0) indicates
     * that this is a new changelist and has not yet been assigned a number.
     *
     * @param   num     Change number
     */
    public void setNumber(int num) {
        number = num;
    }

    /**
     * Return the changelist number.  A changelist number of Zero (0) indicates
     * that this is a new changelist and has not yet been assigned a number.
     *
     * @return     Change number
     */
    public int getNumber() {
        return number;
    }

    /**
     * Set the changelist date 
     *
     * @param   date     Change date
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Return the changelist date 
     *
     * @return     Change date
     */
    public Date getDate() {
        return date;
    }

    /**
     * Set the changelist description 
     *
     * @param   desc     Change description
     */
    public void setDescription(String desc) {
        description = desc;
    }

    /**
     * Set the changelist description 
     *
     * @param   desc     Change description
     */
    public void addDescription(String desc) {
        description = description.concat(desc);
    }

    /**
     * Return the changelist description 
     *
     * @return     Change description
     */
    public String getDescription() {
        return description;
    }


    /**
     * Set the changelist user 
     *
     * @param   username     User who submitted the changelist
     */
    public void setUser(String username) {
        user = username;
    }

    /**
     * Return the changelist user 
     *
     * @return     Change username
     */
    public String getUser() {
        return user;
    }


    /**
     * Set the changelist clientspec 
     *
     * @param   client     Change clientspec
     */
    public void setClient(String client) {
        clientspec = client;
    }

    /**
     * Return the changelist clientspec 
     *
     * @return     Change clientspec
     */
    public String getClient() {
        return clientspec;
    }


    /**
     * Set the changelist status 
     *
     * @param   status     Change status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Return the changelist status 
     *
     * @return     Change status
     */
    public String getStatus() {
        return status;
    }
    
  
    /**
     * Add a file to the changelist.
     *
     * @param file  File information
     */
    //TODO: keep this function?
    public void addFile(File file) {
        files.add(file);
    }
    
    /**
     * Return a list of files in the changelist.
     *
     * @return List of files
     */
    public List getFiles() {
        return files;
    }

    /**
     * Return the StringBuffer of files in the changelist.
     *
     * @return StringBuffer of files
     */
    public StringBuffer getFilesBuffer() {
        return _filesBuffer;
    }

    /**
     * Return a list of files in the changelist.
     *
     * @return List of files
     */
    public void setFilesBuffer(StringBuffer filesBuffer) {
    	_filesBuffer = filesBuffer; 
    }
    
    /**
     * Render the changelist in its long format.
     *
     * @return  String representation of the changelist
     */
    public String toString() {
        StringBuffer buf = toStringBuffer();
        return buf.toString();
    }
    
    public StringBuffer toStringBuffer() {
        StringBuffer buf = new StringBuffer();
        
        // Header comments
        if (getComments() != null) {
            buf.append(getComments());
        }
        buf.append(Common.EOL);
        
        // Change number
        buf.append(CHANGELIST_LABEL + Common.TAB);
        if (getNumber() > 0) {
            buf.append(getNumber());
        } else {
            buf.append("new");
        }
        buf.append(Common.EOL);
        buf.append(Common.EOL);
        
        // Modification date
        if (getDate() != null) {
            buf.append(DATE_LABEL + Common.TAB + Common.LONG_DATE_FORMAT.format(getDate()) + Common.EOL);
            buf.append(Common.EOL);
        } 

        buf.append(CLIENTSPEC_LABEL + Common.TAB + getClient() + Common.EOL);
        buf.append(Common.EOL);

        buf.append(USER_LABEL + Common.TAB + getUser() + Common.EOL);
        buf.append(Common.EOL);

        buf.append(STATUS_LABEL + Common.TAB + getStatus() + Common.EOL);
        buf.append(Common.EOL);
        
        buf.append(DESCRIPTION_LABEL + Common.EOL);
        buf.append(Common.TAB + getDescription() + Common.EOL);
        buf.append(Common.EOL);

        if ((_filesBuffer != null) && (_filesBuffer.length() > 0)) {
            buf.append(FILES_LABEL + Common.EOL);
            buf.append(_filesBuffer);
        }
        return buf;
    }
    
    /**
     * This method uses the Perforce command-line client to obtain a new 
     * changelist containing any open files found in the default changelist.
     * This is equivalent to running the following command:
     * <blockquote>
     *   p4 change -o
     * </blockquote>
     *
     * @return 	Change instance containing information from the default changelist.
     */
    public static Change getChange() {
    	Change change = new Change();
        String currentLine = "";
        String label = "";
        String value = "";
    	BufferedReader p4in = null;
    	StringBuffer filesBuffer = null;

        try {
	        /* Common.exec("p4 change -o", true, false) */
	        String cmd = "p4 change -o";
	        logger.debug(cmd);
	        Process p4 = Runtime.getRuntime().exec(cmd);
	        p4in = new BufferedReader(new InputStreamReader(p4.getInputStream()));
	        filesBuffer = new StringBuffer();
	        while ((currentLine = p4in.readLine()) != null) {
	        	if (label.equals(FILES_LABEL)) {        	
	        		filesBuffer.append( currentLine+"\n" ); 
	        		continue;
	        	} 
	            if ( currentLine.startsWith(Common.COMMENT_STRING)) {
	            	continue; //ignore comments
	            }
	        	/* label each line (except Files) and get its value */
	            if ( currentLine.startsWith(Common.TAB)||currentLine.startsWith(Common.EOL)) {
	            	value = currentLine;
	            } else {
	                // start a new information label if currentline doesn't start with '\t' '\n'
		            if (currentLine.startsWith(CHANGELIST_LABEL) ||
		            	currentLine.startsWith(DATE_LABEL) ||
		            	currentLine.startsWith(CLIENTSPEC_LABEL) ||
		            	currentLine.startsWith(USER_LABEL) ||
		            	currentLine.startsWith(STATUS_LABEL) ||
		            	currentLine.startsWith(DESCRIPTION_LABEL) ||
		            	currentLine.startsWith(FILES_LABEL)) {
		            	label = currentLine.substring(0, currentLine.indexOf(':')+1);
		            	if ( label.length() < currentLine.length() ) {
		            		value = currentLine.substring(label.length() + 1).trim();
		            	} else {
		            		value = "";
		            		continue;
		            	}
		            }
		            if (label == null) {
		            	throw new NullPointerException("Can't recognize this part of the changelist: "+currentLine);
		            }
	            }
	            /* set memeber variabels for this Change object */
	            if (label != "") {
	            	change.setVariable( label, value );
	            }
	        }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
        	e.printStackTrace();
        } finally {
        	try {
        		if (p4in != null) {
        			p4in.close();
        		}
        	} catch (IOException e) {
        		e.printStackTrace();
        	}
        }
        change.setFilesBuffer(filesBuffer);
    	return change;
    }
    
    private void setVariable(String label, String value) {
        if (label.equals(CHANGELIST_LABEL)) {
            try {
            	setNumber(Integer.parseInt(value));
            } catch (NumberFormatException ex) {
                // There may be cases where a new changelist does not have a valid number
            }
        } else if (label.equals(DATE_LABEL)) {
            try {
                setDate(Common.LONG_DATE_FORMAT.parse(value));
            } catch (ParseException ex) {
                logger.error("Unable to parse date: " + value);
            }
        } else if (label.equals(CLIENTSPEC_LABEL)) {
            setClient(value);
        } else if (label.equals(USER_LABEL)) {
            setUser(value);
        } else if (label.equals(STATUS_LABEL)) {
            setStatus(value);
        } else if (label.equals(DESCRIPTION_LABEL)) {
        	addDescription(value + "\n");
        } else if (label.equals(FILES_LABEL)) {
        	throw new RuntimeException("unexpected label: "+label);
        } else {
        	throw new RuntimeException("unexpected label: "+label);
        }
    }
    
    /**
     * Create a new changelist from the Change object 
     * This is equivalent to executing the following p4 command:
     * <blockquote>
     *   p4 change -i
     * </blockquote>
     *
     * @param   changelist  The default changelist retrieved by Change.getChange()
     * @param   text    	Change description
     * @return	int			return changelist number > 0 if created successfully, return 0 otherwise
     */
    public static int create(Change changelist, String text) {
    	int changelistNumber= 0;
        changelist.setDescription(text);

        // Create the changelist on the server
        changelistNumber = create(changelist);
        return changelistNumber;
    }
    

    /**
     * Create a new changelist from the Change object 
     * This is equivalent to executing the following p4 command:
     * <blockquote>
     *   p4 change -i
     * </blockquote>
     *
     * @param   text    Change description
     * @return	int		return changelist number > 0 if created successfully, return 0 otherwise
     */
    private static int create(Change changelist) {
        int changelistNumber = 0;
        try {
            Process p4 = Common.exec("p4 change -i");
            PrintWriter p4out = new PrintWriter(p4.getOutputStream(), true);
            p4out.print(changelist.toStringBuffer());
            p4out.flush();
            p4out.close();
            
            // Check for errors that might have occurred
            boolean error = p4out.checkError();
            int resultt = p4.waitFor();
            if (resultt > 0) {
                error = true;
            }

            // Report any errors to stderr
            BufferedReader p4err = new BufferedReader(new InputStreamReader(p4.getErrorStream()));
            String errorLine = p4err.readLine();
            while (errorLine != null) {
                logger.error(errorLine);
                errorLine = p4err.readLine();
            }
            
            // Check the command result to determine if the update was performed
            BufferedReader p4in = new BufferedReader(new InputStreamReader(p4.getInputStream()));
            String currentLine = p4in.readLine();
            while (currentLine != null) {
                // The format of a success message is as follows:
                // Change 175204 created with 1 open file(s).
                if (!error) {
                    String msgPrefix = "Change ";
                    int numBeginIdx = msgPrefix.length();
                    int numEndIdx = currentLine.indexOf("created");
                    if (numEndIdx > 0) {
                        String clNumber = currentLine.substring(numBeginIdx, numEndIdx);
                        clNumber = clNumber.trim();
                        try {
                        	changelistNumber = Integer.parseInt(clNumber);
                        } catch (NumberFormatException e) {
                        	e.printStackTrace();
                        }
                        logger.debug(currentLine);
                    }
                }
                currentLine = p4in.readLine();
            }
            
        } catch (InterruptedException intex) {
            logger.error("The Perforce command terminated before the changelist could be updated.");
            intex.printStackTrace();
        } catch (IOException ex) {
            logger.error("Unable to perform the Perforce command.");
            ex.printStackTrace();
        }

        return changelistNumber;
    }

    /**
     * Submit the default changelist.
     * This is equivalent to executing the following p4 command:
     * <blockquote>
     *   p4 submit -c changelistNumber
     * </blockquote>
     *
     * @param   changelistNumber    the number indicates a pending changelist previously created with 'p4 change' or a failed 'p4 submit'
     * @return 	true if the changelist has been submitted successfully; false otherwise
     */
    public static boolean submit(int changelistNumber ) {
    	boolean submitted = false;
    	if (changelistNumber < 1) {
    		throw new IllegalArgumentException("changelistNumber has to be bigger than 0");
    	}

    	CommonResult result= null;
        try {
    		String cmd = "p4 submit -c "+Integer.toString(changelistNumber);
            //result = Common.exec(cmd, false, true, true);
    		result = Common.exec(cmd, false, true);
            String errText = result.getStdErrText();
            
            if ((errText==null)||(errText.compareTo("")==0)) {
            	submitted = true;
            } else {
            	if (errText.toLowerCase().matches(".*submitted.*")) {
            		logger.info(errText.trim());
            		submitted = true;
            	} else {
            		logger.error(errText.trim());
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
        return submitted;
    }    
 
    /**
     * Print the changelist information to standard out.
     */
    public static void print(Change currentChange) {
        logger.info(
            "num=" + currentChange.getNumber() + ", \t" + 
//            "date=" + Common.LONG_DATE_FORMAT.format(currentChange.getDate()) + ", \t" +
            "user=" + currentChange.getUser() + ", \t" +
            "client=" + currentChange.getClient() + ", \t" +
            "fileCount=" + currentChange.getFilesBuffer().toString() + ", \t" +
            "desc=" + currentChange.getDescription() + ", \t" 
        );
    }
 
}
