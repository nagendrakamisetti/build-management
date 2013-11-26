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
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import java.lang.IllegalArgumentException;

import org.apache.log4j.Logger;


/**
 * Obtains client information from the Perforce server.
 *
 * @author Shawn Stafford
 */
public class Client {

	/** Log4j */
	private static Logger logger = Logger.getLogger(Client.class.getName());

    /** Text label which identifies the clientspec name */
    private static final String CLIENT_LABEL = "Client:";

    /** Text label which identifies the date when the clientspec was last modified */
    private static final String LASTUPDATE_LABEL = "Update:";

    /** Text label which identifies the date when the clientspec was last accessed */
    private static final String LASTACCESS_LABEL = "Access:";

    /** Text label which identifies the clientspec owner */
    private static final String OWNER_LABEL = "Owner:";

    /** Text label which identifies the host that can access the clientspec */
    private static final String HOST_LABEL = "Host:";

    /** Text label which identifies the changelist description */
    private static final String DESCRIPTION_LABEL = "Description:";

    /** Text label which identifies the clientspec root directory */
    private static final String ROOTDIR_LABEL = "Root:";

    /** Text label which identifies alternate clientspec root directories */
    private static final String ALTROOT_LABEL = "AltRoots:";

    /** Text label which identifies the clientspec options */
    private static final String OPTIONS_LABEL = "Options:";

    /** Text label which identifies the type of line ends that should be used for text files */
    private static final String SUBMIT_OPTIONS_LABEL = "SubmitOptions:";

    /** Text label which identifies the type of line ends that should be used for text files */
    private static final String LINEEND_LABEL = "LineEnd:";

    /** Text label which identifies the depots in the clientspec */
    private static final String VIEW_LABEL = "View:";



    /** Name of the client specification */
    private String clientspec;

    /** Date when the clientspec was last updated */
    private Date lastUpdate;

    /** Date when the clientspec was last accessed */
    private Date lastAccess;

    /** Name of the client specification owner */
    private String owner;

    /** Name of the host that can access the clientspec */
    private String allowedHost;

    /** Comments at the beginning of the clientspec */
    private String comments = "";
    
    /** Description of the clientspec */
    private String description = "";

    /** Root directory */
    private String rootDir;

    /** Alternate root directories */
    private Vector altRoots = new Vector();
    
    /** Client behavior flags */
    private Vector options = new Vector();
    
    /** Submit behavior flag */
    private String _submitOption = "";
        
    /** Line ending character for client text files */
    private String lineEnd;
     	
    /** List of depots in the client view */
    private Vector view = new Vector();

    /** Output received from the command execution */
    private StringBuffer cmdOutput = new StringBuffer();
    
    /**
     * Construct a new client specification
     */
    private Client() {
    }


    /**
     * Set the clientspec name
     *
     * @param   client     Clientspec name
     */
    public void setName(String client) {
        clientspec = client;
    }

    /**
     * Return the clientspec name
     *
     * @return     Clientspec name
     */
    public String getName() {
        return clientspec;
    }



    /**
     * Set the date of the last clientspec update.
     *
     * @param   date     Last update
     */
    public void setLastUpdate(Date date) {
        lastUpdate = date;
    }

    /**
     * Return the date of the last update.
     *
     * @return     Last update
     */
    public Date getLastUpdate() {
        return lastUpdate;
    }

    /**
     * Set the date when the clientspec was last accessed.
     *
     * @param   date     Last access
     */
    public void setLastAccess(Date date) {
        lastAccess = date;
    }

    /**
     * Return the date when the clientspec was last accessed.
     *
     * @return     Last access
     */
    public Date getLastAccess() {
        return lastAccess;
    }


    /**
     * Set the clientspec owner 
     *
     * @param   username     User who owns the clientspec
     */
    public void setOwner(String username) {
        owner = username;
    }

    /**
     * Return the clientspec owner 
     *
     * @return     Clientspec owner
     */
    public String getOwner() {
        return owner;
    }


    /**
     * Set the host that is allowed to access the clientspec.
     * If no host is specified, access is unrestricted.
     *
     * @param   host     Host that is allowed to access the clientspec
     */
    public void setAllowedHost(String host) {
        allowedHost = host;
    }

    /**
     * Return the host that is allowed to access the clientspec.
     *
     * @return     Clientspec host
     */
    public String getAllowedHost() {
        return allowedHost;
    }

    /**
     * Set the comments at the beginning of the clientspec
     *
     * @param   comments   Clientspec comments
     */
    public void setComments(String text) {
        comments = text;
    }

    /**
     * Return the clientspec comments
     *
     * @return     Clientspec comment block
     */
    public String getComments() {
        return comments;
    }

    /**
     * Set the clientspec description 
     *
     * @param   desc     Clientspec description
     */
    public void setDescription(String desc) {
        description = desc;
    }

    /**
     * Return the clientspec description 
     *
     * @return     Clientspec description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the root directory location for the clientspec.
     *
     * @param   dir     Root directory
     */
    public void setRootDir(String dir) {
        rootDir = dir;
    }

    /**
     * Return the root directory
     *
     * @return     root directory
     */
    public String getRootDir() {
        return rootDir;
    }

    /**
     * Add an alternate root directory location for the clientspec.
     *
     * @param   dir     Root directory
     */
    public void addAltRoot(String dir) {
        altRoots.add(dir);
    }

    /**
     * Return the root directory
     *
     * @return     root directory
     */
    public List getAltRoots() {
        return altRoots;
    }
    
    /**
     * Add an option flag for the clientspec.
     *
     * @param   option   Option name
     */
    public void addOption(String option) {
        options.add(option);
    }

    /**
     * Return the list of client options
     *
     * @return     list of options
     */
    public List getOptions() {
        return options;
    }

    /**
     * Set the Submit Option for the clientspec.
     *
     * @param   submitOption    Submit Option including: 
     * 							submitunchanged/submitunchanged+reopen
     * 							revertunchanged/revertunchanged+reopen
     *							leaveunchanged/leaveunchanged+reopen
     */
    public void setSubmitOptions(String submitOption) {
        _submitOption = submitOption;
    }

    /**
     * Return the Submit Option
     *
     * @return     submitOption
     */
    public String getSubmitOptions() {
        return _submitOption;
    }

    /**
     * Set the type of client lineend
     *
     * @param   type   Line-end type
     */
    public void setLineEnd(String type) {
        lineEnd = type;
    }

    /**
     * Return the line-end type
     *
     * @return     Line-end type
     */
    public String getLineEnd() {
        return lineEnd;
    }
    
    /**
     * Add a depot to the view.
     *
     * @param   depot  View entry
     * @return	true if view is added to instance; false otherwise
     */
    public boolean addView(String depot) {
    	boolean viewAdded = false;
        if ((depot != null) && (depot.length() > 0) && (depot != Common.EOL)) {
        	if ( ! view.contains(depot)) {
        		viewAdded = view.add(depot);
        	} 
        } else throw new IllegalArgumentException();
        
        return viewAdded;
    }

    /**
     * Add a depot to the view.
     *
     * @param   depot  View entry
     * @return	true if view is removed from instance; false otherwise
     */
    public boolean removeView(String depot) {
    	boolean viewRemoved = false;
        if ((depot != null) && (depot.length() > 0) && (depot != Common.EOL)) {
        	if ( view.contains(depot)) {
        		viewRemoved = view.remove(depot);
        	} 
        } else throw new IllegalArgumentException();
        
        return viewRemoved;
    }

    /**
     * Check if view exists
     *
     * @param   depot  View entry
     * @return	true if view is already part of "view" variable; false otherwise
     */
    public boolean existsView (String depot) {
        if ((depot != null) && (depot.length() > 0) && (depot != Common.EOL)) {
        	if ( view.contains(depot)) {
        		return true;
        	} 
        } else throw new IllegalArgumentException();
        
        return false;
    }
    
    /**
     * Return the list of depots in the client view
     *
     * @return     list of deopots
     */
    public List getView() {
        return view;
    }

    
    /**
      * Convert the client information to a string.
      *
      * @return Text representation of the clientspec
      */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        
        if (getComments() != null) {
            buf.append(getComments());
        }
        buf.append(Common.EOL);
        
        buf.append(CLIENT_LABEL + Common.TAB + getName() + Common.EOL);
        buf.append(Common.EOL);
        
        buf.append(LASTUPDATE_LABEL + Common.TAB + Common.LONG_DATE_FORMAT.format(getLastUpdate()) + Common.EOL);
        buf.append(Common.EOL);
        
        buf.append(LASTACCESS_LABEL + Common.TAB + Common.LONG_DATE_FORMAT.format(getLastAccess()) + Common.EOL);
        buf.append(Common.EOL);
        
        buf.append(OWNER_LABEL + Common.TAB + getOwner() + Common.EOL);
        buf.append(Common.EOL);

        buf.append(DESCRIPTION_LABEL + Common.EOL);
        buf.append(getDescription());
        buf.append(Common.EOL);
        
        buf.append(ROOTDIR_LABEL + Common.TAB + getRootDir() + Common.EOL);
        buf.append(Common.EOL);

        List roots = getAltRoots();
        if ((roots != null) && (roots.size() > 0)) {
            buf.append(ALTROOT_LABEL + Common.EOL);
            Iterator rootsIter = roots.iterator();
            while (rootsIter.hasNext()) {
                buf.append(Common.TAB + rootsIter.next() + Common.EOL);
            }
            buf.append(Common.EOL);
        }

        List opts = getOptions();
        if ((opts != null) && (opts.size() > 0)) {
            buf.append(OPTIONS_LABEL + Common.TAB);
            Iterator optsIter = opts.iterator();
            while (optsIter.hasNext()) {
                buf.append(optsIter.next());
                if (optsIter.hasNext()) {
                    buf.append(" ");
                }
            }
            buf.append(Common.EOL);
            buf.append(Common.EOL);
        }
        
        buf.append(LINEEND_LABEL + Common.TAB + getLineEnd() + Common.EOL);
        buf.append(Common.EOL);

        List depots = getView();
        if ((depots != null) && (depots.size() > 0)) {
            buf.append(VIEW_LABEL + Common.EOL);
            Iterator viewIter = depots.iterator();
            while (viewIter.hasNext()) {
                buf.append(Common.TAB + viewIter.next() + Common.EOL);
            }
            buf.append(Common.EOL);
        }
        
        return buf.toString();
    }
    
    /**
     * Parse the perforce output generated by a full client entry.
     * This changelist output can be obtained by querying a specific
     * clientspec:
     * <blockquote>
     *   p4 client -o SOMECLIENTSPEC
     * </blockquote>
     */
    public static Client parse(String text) {
        Client client = new Client();

        StringTokenizer lines = new StringTokenizer(text, Common.EOL);

        String currentLine = null;
        if (lines.hasMoreTokens()) {
            currentLine = lines.nextToken();
            
            // Obtain the comment lines
            while (currentLine.startsWith(Common.COMMENT_STRING)) {
                client.setComments(client.getComments() + currentLine + Common.EOL);
                currentLine = lines.nextToken();
            }

            // Obtain the change number
            if (currentLine.startsWith(CLIENT_LABEL)) {
                currentLine = currentLine.substring(CLIENT_LABEL.length() + 1).trim();
                client.setName(currentLine);
                currentLine = lines.nextToken();  // discard blank line
            } else {
                logger.error("Unexpected client name: " + currentLine);
            }

            // Obtain the full date of last change
            if (currentLine.startsWith(LASTUPDATE_LABEL)) {
                currentLine = currentLine.substring(LASTUPDATE_LABEL.length() + 1).trim();
                try {
                    client.setLastUpdate(Common.LONG_DATE_FORMAT.parse(currentLine));
                } catch (ParseException ex) {
                    logger.error("Unable to parse date: " + currentLine);
                }
                currentLine = lines.nextToken();  // discard blank line
            } else {
                logger.error("Unexpected date: " + currentLine);
            }

            // Obtain the full date of last access
            if (currentLine.startsWith(LASTACCESS_LABEL)) {
                currentLine = currentLine.substring(LASTACCESS_LABEL.length() + 1).trim();
                try {
                    client.setLastAccess(Common.LONG_DATE_FORMAT.parse(currentLine));
                } catch (ParseException ex) {
                    logger.error("Unable to parse date: " + currentLine);
                }
                currentLine = lines.nextToken();  // discard blank line
            } else {
                logger.error("Unexpected date: " + currentLine);
            }

            // Obtain the owner
            if (currentLine.startsWith(OWNER_LABEL)) {
                client.setOwner(currentLine.substring(OWNER_LABEL.length() + 1).trim());
                currentLine = lines.nextToken();  // discard blank line
            } else {
                logger.error("Unexpected user: " + currentLine);
            }

            // Obtain the allowed hosts (optional)
            if (currentLine.startsWith(HOST_LABEL)) {
                client.setAllowedHost(currentLine.substring(HOST_LABEL.length() + 1).trim());
                currentLine = lines.nextToken();  // discard blank line
            } else {
                // The host field is optional, so don't display an error if missing
                //logger.error("Unexpected host: " + currentLine);
            }

            // Obtain the description (optional)
            if (currentLine.startsWith(DESCRIPTION_LABEL)) {
                currentLine = lines.nextToken();  // discard the label line
                while (lines.hasMoreTokens() && (!currentLine.startsWith(ROOTDIR_LABEL))) {
                    client.setDescription(client.getDescription() + currentLine + Common.EOL);
                    currentLine = lines.nextToken();
                }
            } else {
                // The description field is optional, so don't display an error if missing
                //logger.error("Unexpected description: " + currentLine);
            }

            // Obtain the root directory
            if (currentLine.startsWith(ROOTDIR_LABEL)) {
                client.setRootDir(currentLine.substring(ROOTDIR_LABEL.length() + 1).trim());
                currentLine = lines.nextToken();  // discard blank line
            } else {
                logger.error("Unexpected root directory: " + currentLine);
            }

            // Obtain any alternate root directories (optional)
            if (currentLine.startsWith(ALTROOT_LABEL)) {
                currentLine = lines.nextToken();  // discard the label line
                while (lines.hasMoreTokens() && (!currentLine.startsWith(OPTIONS_LABEL))) {
                    client.addAltRoot(currentLine.trim());
                    currentLine = lines.nextToken();
                }
            } else {
                // Alternate root directories are optional, so don't display an error
                //logger.error("Unexpected alt root: " + currentLine);
            }
            
            // Obtain the list of options
            if (currentLine.startsWith(OPTIONS_LABEL)) {
                StringTokenizer st = new StringTokenizer(currentLine.substring(OPTIONS_LABEL.length() + 1).trim());
                while (st.hasMoreTokens()) {
                    client.addOption(st.nextToken());
                }
                currentLine = lines.nextToken();  // discard blank line
            } else {
                logger.error("Unexpected options: " + currentLine);
            }
            
            // Obtain the Submit options
            if (currentLine.startsWith(SUBMIT_OPTIONS_LABEL)) {
                client.setSubmitOptions(currentLine.substring(SUBMIT_OPTIONS_LABEL.length() + 1).trim());
                currentLine = lines.nextToken();  // discard blank line
            } else {
                logger.error("Unexpected Submit options directory: " + currentLine);
            }

            // Obtain the line-end type
            if (currentLine.startsWith(LINEEND_LABEL)) {
                client.setLineEnd(currentLine.substring(LINEEND_LABEL.length() + 1).trim());
                if (lines.hasMoreTokens()) {
                	currentLine = lines.nextToken();  // discard blank line
                }
            } else {
                logger.error("Unexpected Line end: " + currentLine);
            }
            
            // Obtain the view depots (optional)
            if (currentLine.startsWith(VIEW_LABEL)) {
                currentLine = lines.nextToken();  // discard the label line
                while (lines.hasMoreTokens()) {
                    client.addView(currentLine.trim());
                    currentLine = lines.nextToken();
                }
                client.addView(currentLine.trim());
            } else {
                // The view field is optional, so don't display an error if missing
                //logger.error("Unexpected view: " + currentLine);
            }
        }

        return client;
    }


    /**
     * Obtain the client information for the current client.
     * This is equivalent to executing the following p4 command:
     * <blockquote>
     *   p4 client -o 
     * </blockquote>
     *
     * @param    name    Clientspec name
     */
    public static Client getClient() {
        return getClient("");
    }

    /**
     * Obtain the client information for the specified client.
     * This is equivalent to executing the following p4 command:
     * <blockquote>
     *   p4 client -o SOMECLIENTSPEC
     * </blockquote>
     *
     * @param    name    Clientspec name
     * @return   Client information
     */
    public static Client getClient(String name) {
        Client client = null;

        try {
            Process p4 = Common.exec("p4 client -o " + name);
            BufferedReader p4in = new BufferedReader(new InputStreamReader(p4.getInputStream()));
            StringBuffer str = new StringBuffer();
            String currentLine = p4in.readLine();
            while (currentLine != null) {
                str.append(currentLine + Common.EOL);
                currentLine = p4in.readLine();
            }

            client = parse(str.toString());
        } catch (IOException ex) {
            logger.error("Unable to perform the Perforce command.");
            ex.printStackTrace();
	    } catch (InterruptedException ex) {
	        ex.printStackTrace();
	    } catch (RuntimeException ex) {
	        ex.printStackTrace();
       }

        return client;
    }

    /**
     * Update the client information for the specified client.
     * This is equivalent to executing the following p4 command:
     * <blockquote>
     *   p4 client -i SOMECLIENTSPEC < clientspec
     * </blockquote>
     *
     * @param    client    Updated clientspec information
     * @param    TRUE if the clientspec was updated
     */
    public static boolean setClient(Client client) {
        boolean updated = false;

        try {
            // Update the clientspec
            Process p4 = Common.exec("p4 client -i ");
            PrintWriter p4out = new PrintWriter(p4.getOutputStream(), true);
            p4out.print(client.toString());
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
                if (!error && currentLine.trim().equals("Client " + client.getName() + " saved.")) {
                    updated = true;
                }
                currentLine = p4in.readLine();
            }
        } catch (InterruptedException intex) {
            logger.error("The Perforce command terminated before the client could be updated.");
            intex.printStackTrace();
        } catch (IOException ex) {
            logger.error("Unable to perform the Perforce command.");
            ex.printStackTrace();
        }

        return updated;
    }


    /**
     * Print the client information to standard out.
     */
    public static void print(Client client) {
        logger.info("Client ================");
        logger.info("Client = " + client.getName());
        logger.info("Last Update = " + Common.LONG_DATE_FORMAT.format(client.getLastUpdate()));
        logger.info("Last Access = " + Common.LONG_DATE_FORMAT.format(client.getLastAccess()));
        logger.info("Owner = " + client.getOwner());
        logger.info("Description = \n" + client.getDescription());
        logger.info("Root = " + client.getRootDir());
        logger.info("AltRoots = " + client.getAltRoots());
        logger.info("Options = " + client.getOptions());
        logger.info("Line end = " + client.getLineEnd());
        logger.info("View = \n" + client.getView());
    }


    /** 
     * The main method is used to test the class from the command line to
     * ensure that perforce output is being parsed correctly.
     */
    public static void main(String[] args) {
        try {
            Client client = getClient();
            print(client);
        
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }
    }


}
