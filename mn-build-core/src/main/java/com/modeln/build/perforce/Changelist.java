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

import org.apache.log4j.Logger;



/**
 * Obtains changelist information from the Perforce server.
 *
 * @author Shawn Stafford
 */
public class Changelist {

	/** Log4j */
	private static Logger logger = Logger.getLogger(Changelist.class.getName());

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

    
    /** Changelist status value of "new" */
    private static final String STATUS_NEW = "new";
    
    /** Changelist status value of "pending" */
    private static final String STATUS_PENDING = "pending";

    /** Changelist status value of "submitted" */
    private static final String STATUS_SUBMITTED = "submitted";

    
    /** Changelist comment header */
    private String comments;
    
    /** Changelist number */
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

    /** List of files in the changelist */
    private Vector files = new Vector();

    /**
     * Construct a new changelist
     */
    private Changelist() {
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
     * @return Changelist comment header
     */
    public String getComments() {
        return comments;
    }

    /**
     * Set the changelist number.  A changelist number of Zero (0) indicates
     * that this is a new changelist and has not yet been assigned a number.
     *
     * @param   num     Changelist number
     */
    public void setNumber(int num) {
        number = num;
    }

    /**
     * Return the changelist number.  A changelist number of Zero (0) indicates
     * that this is a new changelist and has not yet been assigned a number.
     *
     * @return     Changelist number
     */
    public int getNumber() {
        return number;
    }

    /**
     * Set the changelist date 
     *
     * @param   date     Changelist date
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Return the changelist date 
     *
     * @return     Changelist date
     */
    public Date getDate() {
        return date;
    }

    /**
     * Set the changelist description 
     *
     * @param   desc     Changelist description
     */
    public void setDescription(String desc) {
        description = desc;
    }

    /**
     * Return the changelist description 
     *
     * @return     Changelist description
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
     * @return     Changelist username
     */
    public String getUser() {
        return user;
    }


    /**
     * Set the changelist clientspec 
     *
     * @param   client     Changelist clientspec
     */
    public void setClient(String client) {
        clientspec = client;
    }

    /**
     * Return the changelist clientspec 
     *
     * @return     Changelist clientspec
     */
    public String getClient() {
        return clientspec;
    }


    /**
     * Set the changelist status 
     *
     * @param   status     Changelist status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Return the changelist status 
     *
     * @return     Changelist status
     */
    public String getStatus() {
        return status;
    }
    
    /**
     * Add a file to the changelist.
     *
     * @param file  File information
     */
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
     * Render the changelist in its long format.
     *
     * @return  String representation of the changelist
     */
    public String getLongFormat() {
        StringBuffer buf = new StringBuffer();
        
        // Header comments
        if (getComments() != null) {
            buf.append(getComments());
        }
        buf.append(Common.EOL);
        
        // Changelist number
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
        buf.append(getDescription());
        buf.append(Common.EOL);

/*        
        List files = getFiles();
        if ((files != null) && (files.size() > 0)) {
            buf.append(FILES_LABEL + EOL);
            Iterator fileIter = files.iterator();
            while (fileIter.hasNext()) {
                buf.append(TAB + fileIter.next() + EOL);
            }
            buf.append(EOL);
        }
*/        
        return buf.toString();
    }
    
    /**
     * Parse the perforce output generated by a full changelist entry.
     * This changelist output can be obtained by querying a specific
     * changelist number:
     * <blockquote>
     *   p4 change -o 98692
     * </blockquote>
     */
    public static Changelist parseLongFormat(String text) {
        Changelist change = new Changelist();

        StringTokenizer lines = new StringTokenizer(text, "\n");

        String currentLine = null;
        if (lines.hasMoreTokens()) {
            currentLine = lines.nextToken();
            // Discard comment lines
            while (currentLine.startsWith(Common.COMMENT_STRING)) {
                currentLine = lines.nextToken();
            }

            // Obtain the change number
            if (currentLine.startsWith(CHANGELIST_LABEL)) {
                currentLine = currentLine.substring(CHANGELIST_LABEL.length() + 1).trim();
                int clNumber = 0;
                try {
                    clNumber = Integer.parseInt(currentLine);
                } catch (NumberFormatException ex) {
                    // There may be cases where a new changelist does not have a valid number
                }
                change.setNumber(clNumber);
                currentLine = lines.nextToken();  // discard blank line
            } else {
                logger.error("Unexpected change number: " + currentLine);
            }

            // Obtain the full date
            if (currentLine.startsWith(DATE_LABEL)) {
                currentLine = currentLine.substring(DATE_LABEL.length() + 1).trim();
                try {
                    change.setDate(Common.LONG_DATE_FORMAT.parse(currentLine));
                } catch (ParseException ex) {
                    logger.error("Unable to parse date: " + currentLine);
                }
                currentLine = lines.nextToken();  // discard blank line
            } else {
                logger.error("Unexpected date: " + currentLine);
            }

            // Obtain the clientspec
            if (currentLine.startsWith(CLIENTSPEC_LABEL)) {
                change.setClient(currentLine.substring(CLIENTSPEC_LABEL.length() + 1).trim());
                currentLine = lines.nextToken();  // discard blank line
            } else {
                logger.error("Unexpected clientspec: " + currentLine);
            }

            // Obtain the user
            if (currentLine.startsWith(USER_LABEL)) {
                change.setUser(currentLine.substring(USER_LABEL.length() + 1).trim());
                currentLine = lines.nextToken();  // discard blank line
            } else {
                logger.error("Unexpected user: " + currentLine);
            }

            // Obtain the status
            if (currentLine.startsWith(STATUS_LABEL)) {
                change.setStatus(currentLine.substring(STATUS_LABEL.length() + 1).trim());
                currentLine = lines.nextToken();  // discard blank line
            } else {
                logger.error("Unexpected status: " + currentLine);
            }

            // Obtain the description
            if (currentLine.startsWith(DESCRIPTION_LABEL)) {
                while (lines.hasMoreTokens()) {
                    change.setDescription(change.getDescription() + lines.nextToken() + "\n");
                }
            } else {
                logger.error("Unexpected description: " + currentLine);
            }

        }

        return change;
    }

    /**
     * Parse the perforce output generated by a single-line changelist entry.
     * This changelist output can be obtained by querying a range of 
     * changelists:
     * <blockquote>
     *   p4 changes //depot/...@date1,@date2
     *   p4 changes //depot/...@date1,@now
     * </blockquote>
     */
    public static Changelist parseShortFormat(String text) {
        Changelist change = new Changelist();

        String numStr = null;
        try {
            change.setNumber(Integer.parseInt(text.substring(7, 13)));
        } catch (Exception ex) {
            logger.error("Unable to parse changelist number: " + text);
        }

        String datestr = null;
        try {
            datestr = text.substring(16, 26);
            change.setDate(Common.SHORT_DATE_FORMAT.parse(datestr));
        } catch (Exception ex) {
            logger.error("Unable to parse changelist date: " + text);
        }

        int atIdx = text.indexOf("@");
        change.setUser(text.substring(30, atIdx));

        int quoteIdx = text.indexOf("'");
        change.setClient(text.substring(atIdx + 1, quoteIdx - 1));

        change.setDescription(text.substring(quoteIdx + 1, text.indexOf("'", quoteIdx + 1)));

        return change;
    }


    /**
     * This method uses the Perforce command-line client to obtain a list of 
     * changes submitted to the specified path from the specified date until 
     * now.  This produces a list of changes just as if the following command 
     * had been executed from the command line.
     * <blockquote>
     *   p4 changes //depot/...@date1,@now
     * </blockquote>
     *
     * @param   path    Depot or path to which the search should be restricted
     * @param   from    Starting date
     */
    public static Changelist[] getChanges(String path, Date from) {
        ArrayList list = new ArrayList();
        try {
            // Execute the Perforce command
            String cmd = "p4 changes " + path + 
                "@" + Common.SHORT_DATE_FORMAT.format(from) + ",@now";
            Process p4 = Common.exec(cmd);

            BufferedReader p4in = new BufferedReader(new InputStreamReader(p4.getInputStream()));
            String currentLine = Common.readLine(p4in);
            while (currentLine != null) {
                list.add(parseShortFormat(currentLine));
                currentLine = Common.readLine(p4in);
            }
        } catch (IOException ex) {
            logger.error("Unable to perform the Perforce command.");
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } catch (RuntimeException ex) {
            ex.printStackTrace();
        }

        // Return the changes as an array
        Changelist[] changes = new Changelist[list.size()];
        for (int idx = 0; idx < changes.length; idx++) {
            changes[idx] = (Changelist) list.get(idx);
        }

        return changes;
    }


    /**
     * This method uses the Perforce command-line client to obtain a list of 
     * changes submitted to the specified path between the specified dates.
     * This produces a list of changes just as if the following command had
     * been executed from the command line.
     * <blockquote>
     *   p4 changes //depot/...@date1,@date2
     * </blockquote>
     *
     * @param   path    Depot or path to which the search should be restricted
     * @param   from    Starting date
     * @param   to      Ending date
     */
    public static Changelist[] getChanges(String path, Date from, Date to) {
        Changelist[] changes = null;

        String cmd = "p4 changes " + path + "@" + 
            Common.SHORT_DATE_FORMAT.format(from) + ",@" +
            Common.SHORT_DATE_FORMAT.format(to);

        // Execute the Perforce command
        try {
            Process p4 = Common.exec(cmd);
            changes = getChanges(p4.getInputStream());
        } catch (IOException ex) {
            logger.error("Unable to perform the Perforce command.");
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } catch (RuntimeException ex) {
            ex.printStackTrace();
        }

        return changes;
    }


    /**
     * This method uses the Perforce command-line client to obtain a list of 
     * changes submitted to the specified path between the changelist numbers.
     * This produces a list of changes just as if the following command had
     * been executed from the command line.
     * <blockquote>
     *   p4 changes //depot/...@12300,12399
     * </blockquote>
     *
     * @param   path    Depot or path to which the search should be restricted
     * @param   from    Starting changelist number
     * @param   to      Ending changelist number
     */
    public static Changelist[] getChanges(String path, int from, int to) {
        Changelist[] changes = null;

        String cmd = "p4 changes " + path + "@" + from + "," + to;

        // Execute the Perforce command
        try {
            Process p4 = Common.exec(cmd);
            changes = getChanges(p4.getInputStream());
        } catch (IOException ex) {
            logger.error("Unable to perform the Perforce command.");
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } catch (RuntimeException ex) {
            ex.printStackTrace();
        }

        return changes;
    }

    /**
     * This method uses the Perforce command-line client to obtain a list of 
     * changes submitted to the specified path between the specified dates.
     * This produces a list of changes just as if the following command had
     * been executed from the command line.
     * <blockquote>
     *   p4 changes //depot/...@date1,@date2
     * </blockquote>
     *
     * @param   number      Changelist number
     */
    public static Changelist getChange(int number) {
        Changelist change = null;

        try {
            Process p4 = Common.exec("p4 change -o " + number);
            String text = Common.readInput(p4.getInputStream());
            if ((text != null) && (text.length() > 0)) {
                change = parseLongFormat(text);
            }
        } catch (IOException ex) {
            logger.error("Unable to perform the Perforce command.");
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } catch (RuntimeException ex) {
            ex.printStackTrace();
        }

        return change;
    }

    /**
     * This method uses the Perforce command-line client to obtain a new 
     * changelist containing any open files found in the default changelist.
     * This is equivalent to running the following command:
     * <blockquote>
     *   p4 change -o
     * </blockquote>
     *
     * @param   number      Changelist number
     */
    public static Changelist getChange() {
        Changelist change = null;

        try {
            Process p4 = Common.exec("p4 change -o");
            String text = Common.readInput(p4.getInputStream());
            if ((text != null) && (text.length() > 0)) {
                change = parseLongFormat(text);
            }
        } catch (IOException ex) {
            logger.error("Unable to perform the Perforce command.");
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } catch (RuntimeException ex) {
            ex.printStackTrace();
        }

        return change;
    }


    /**
     * Obtain the most recent changelist sync'd to the specified client.
     * This is equivalent to executing the following p4 command:
     * <blockquote>
     *   p4 changes -m 1 //depot/...@CLIENTSPEC
     * </blockquote>
     *
     * @param   path    Depot or path to which the search should be restricted
     * @param   client  Clientspec to query against
     */
    public static Changelist getChange(String path, Client client) {
        Changelist change = null;

        try {
            Process p4 = Common.exec("p4 changes -m 1 " + path + "@" + client.getName());
            BufferedReader p4in = new BufferedReader(new InputStreamReader(p4.getInputStream()));
            change = parseShortFormat(Common.readLine(p4in));
        } catch (IOException ex) {
            logger.error("Unable to perform the Perforce command.");
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } catch (RuntimeException ex) {
            ex.printStackTrace();
        }

        return change;
    }


    /**
     * Obtain the most recent changelist submitted to the specified depot.
     * This is equivalent to executing the following p4 command:
     * <blockquote>
     *   p4 changes -m 1 //depot/...
     * </blockquote>
     *
     * @param   path    Depot or path to which the search should be restricted
     */
    public static Changelist getChange(String path) {
        Changelist change = null;

        try {
            Process p4 = Common.exec("p4 changes -m 1 " + path);
            BufferedReader p4in = new BufferedReader(new InputStreamReader(p4.getInputStream()));
            change = parseShortFormat(Common.readLine(p4in));
        } catch (IOException ex) {
            logger.error("Unable to perform the Perforce command.");
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } catch (RuntimeException ex) {
            ex.printStackTrace();
        }

        return change;
    }

    
    /**
     * Submit the default changelist.
     * This is equivalent to executing the following p4 command:
     * <blockquote>
     *   p4 submit -o
     * </blockquote>
     *
     * @param   text    Changelist description
     */
    public static Changelist submit(String text) {
        Changelist change = new Changelist();

        try {
            // Figure out what files are in the default changelist
            List openFiles = File.getOpen();
            Iterator fileIter = openFiles.iterator();
            while (fileIter.hasNext()) {
                File currentFile = (File) fileIter.next();
                logger.debug("TODO: Submit open file: " + currentFile);
            }
            logger.debug("TODO: Send the changelist output to the process.");
            Process p4 = Common.exec("p4 submit -o");
            // Send the changelist output to the process
        } catch (IOException ex) {
            logger.error("Unable to perform the Perforce command.");
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } catch (RuntimeException ex) {
            ex.printStackTrace();
        }

        return change;
    }

    /**
     * Create a new empty changelist.
     * This is equivalent to executing the following p4 command:
     * <blockquote>
     *   p4 change -i
     * </blockquote>
     *
     * @param   text    Changelist description
     * @param   files   List of files
     */
    public static Changelist create(String text) {
        // Obtain the required user and client information
        User user = User.getUser();
        Client client = Client.getClient();
        
        // Create a new changelist object
        Changelist changelist = new Changelist();
        changelist.setUser(user.getUsername());
        changelist.setClient(client.getName());
        changelist.setStatus(STATUS_NEW);
        changelist.setDescription(text);

        // Create the changelist on the server
        boolean created = create(changelist);
        if (!created) {
            changelist = null;
        }
        
        return changelist;
    }
    
    /**
     * Create a new empty changelist.
     * This is equivalent to executing the following p4 command:
     * <blockquote>
     *   p4 change -i
     * </blockquote>
     *
     * @param   text    Changelist description
     * @param   files   List of files
     */
    public static Changelist create(String text, List files) {
        // Obtain the required user and client information
        User user = User.getUser();
        Client client = Client.getClient();
        
        // Create a new changelist object
        Changelist changelist = new Changelist();
        changelist.setUser(user.getUsername());
        changelist.setClient(client.getName());
        changelist.setStatus(STATUS_NEW);
        changelist.setDescription(text);
        Iterator fileIter = files.iterator();
        File currentFile = null;
        while (fileIter.hasNext()) {
            changelist.addFile((File) fileIter.next());
        }
        
        // Create the changelist on the server
        boolean created = create(changelist);
        if (!created) {
            changelist = null;
        }
        
        return changelist;
    }

    /**
     * Create a new empty changelist.
     * This is equivalent to executing the following p4 command:
     * <blockquote>
     *   p4 change -i
     * </blockquote>
     *
     * @param   text    Changelist description
     * @param   files   List of files
     */
    private static boolean create(Changelist changelist) {
        boolean updated = false;

        logger.debug("CREATING CHANGELIST:");
        logger.debug(changelist.toString());
        
        try {
            Process p4 = Common.exec("p4 change -i");
            PrintWriter p4out = new PrintWriter(p4.getOutputStream(), true);
            p4out.print(changelist.toString());
            p4out.flush();
            p4out.close();
            
            // Check for errors that might have occurred
            boolean error = p4out.checkError();
            int result = p4.waitFor();
            if (result > 0) {
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
                        logger.debug("TODO: Parse changelist number " + clNumber);
                    }
                    updated = true;
                }
                currentLine = p4in.readLine();
            }
            
        } catch (InterruptedException intex) {
            logger.error("The Perforce command terminated before the changelist could be updated.");
            intex.printStackTrace();
        } catch (IOException ex) {
            logger.error("Unable to perform the Perforce command.");
            ex.printStackTrace();
        } catch (RuntimeException ex) {
            ex.printStackTrace();
        }
        logger.debug("CHANGELIST CREATED.");

        return updated;
    }

    /**
     * Print the changelist information to standard out.
     */
    private static void print(Changelist currentChange) {
        logger.info(
            "num=" + currentChange.getNumber() + ", \t" + 
            "date=" + Common.LONG_DATE_FORMAT.format(currentChange.getDate()) + ", \t" +
            "user=" + currentChange.getUser() + ", \t" +
            "client=" + currentChange.getClient() + ", \t" +
            "desc=" + currentChange.getDescription() + ", \t" 
        );
    }


    /**
     * Parses the input stream and returns the list of changes that were 
     * available.  This assumes that a "p4 changes" command has been executed
     * and the results of this are contained in the input stream.
     *
     * @param   stream     Input stream from the Perforce command
     */
    private static Changelist[] getChanges(InputStream stream) {
        ArrayList list = new ArrayList();
        try {
            BufferedReader p4in = new BufferedReader(new InputStreamReader(stream));
            String currentLine = Common.readLine(p4in);
            while (currentLine != null) {
                list.add(parseShortFormat(currentLine));
                currentLine = Common.readLine(p4in);
            }
        } catch (IOException ex) {
            logger.error("Unable to perform the Perforce command.");
            ex.printStackTrace();
        }

        // Return the changes as an array
        Changelist[] changes = new Changelist[list.size()];
        for (int idx = 0; idx < changes.length; idx++) {
            changes[idx] = (Changelist) list.get(idx);
        }

        return changes;
    }



    /** 
     * The main method is used to test the class from the command line to
     * ensure that perforce output is being parsed correctly.
     */
    public static void main(String[] args) {
        try {
            logger.info("Testing the short format parsing of 'p4 changes //modeln/...@2003/09/29,@2003/10/01'");
            GregorianCalendar from = new GregorianCalendar(2003, 8, 29);
            GregorianCalendar to = new GregorianCalendar(2003, 9, 1);
            Changelist[] changes = getChanges("//modeln/...", from.getTime(), to.getTime());

            // Test the long format parser
            logger.info("Testing the long format parsing of 'p4 change -o 98692'");
            Changelist change = getChange(98692);
            print(change);
        
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }
    }


}
