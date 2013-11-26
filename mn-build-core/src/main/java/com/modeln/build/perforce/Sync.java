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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;



/**
 * Obtains revision information from the Perforce server.  The Sync command
 * can be used to update the host with the latest files from the server.
 *
 * @author Shawn Stafford
 */
public class Sync {

	/** Log4j */
	private static Logger logger = Logger.getLogger(Sync.class.getName());

    /**
     * Construct a new changelist
     */
    private Sync() {
    }

    /**
     * Obtain a count of the number of files that will be changed if a 
     * sync is performed.  This is equivalent to executing the following 
     * p4 command:
     * <blockquote>
     *   p4 sync -n //depot/...
     * </blockquote>
     * This method should be used if you wish to determine whether a 
     * sync should be performed to obtain the most current files.  If the
     * value returned is zero, then the host is up-to-date with the 
     * Perforce server.  
     *
     * @param   path    Depot or path to which the search should be restricted
     * @return  Number of files that will be updated if a sync is performed
     */
    public static int getChangeCount(String path) {
        int count = 0;

        try {
            Process p4 = Common.exec("p4 sync -n " + path);
            BufferedReader p4in = new BufferedReader(new InputStreamReader(p4.getInputStream()));
            String currentLine = p4in.readLine();
            // Process the list of files that would be affected
            if ((currentLine != null) && (currentLine.indexOf("file(s) up-to-date") < 0)) {
                while (currentLine != null) {
                    count++;
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

        return count;
    }

    /**
     * Synchronize the client with its view of the depot.
     */
    public static boolean syncAllToLatest() {
    	return execute("");
    }
    
    /**
     * Synchronize the client with a particular depot
     */
    public static boolean syncDepotToLatest(String depot) {
		return execute(depot);
    }
    
    /**
     * Synchronize the client with a particular depot of a changelist
     */
    public static boolean syncDepotToChangelist(String depot, String changelist) {
    	return execute(depot+"@"+changelist);
    }
    
    /**
     * Synchronize the client with its view of the depot.
     * This is equivalent to executing the following p4 command:
     * <blockquote>
     *   p4 sync
     * </blockquote>
     *
     * @param   flag	
     * @return  true if syncing was successful; false otherwise.
     */
    private static boolean execute(String flag) {
    	boolean success = false;
    	CommonResult result= null;
    	try {
    		String cmd = "p4 sync "+flag;
            result = Common.exec(cmd, false, true);
            String errText = result.getStdErrText();
            if ((errText==null)||(errText.compareTo("")==0)) {
            	success = true;
            } else {
            	if (errText.trim().endsWith("up-to-date.")) {
            		logger.debug(errText.trim());
                	success = true;
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
	    return success;
    }
    
    /** 
     * The main method is used to test the class from the command line to
     * ensure that perforce output is being parsed correctly.
     */
    public static void main(String[] args) {
        try {
//            logger.info"Testing the results of 'p4 sync -n //modeln/...'");
//            logger.info"Number of changes: " + getChangeCount("//modeln/..."));
//            Sync.syncDepotToChangelist("//modeln_build/...", "201051");
            Sync.syncDepotToLatest("//modeln_build/lib/...");
            Sync.syncDepotToLatest("//modeln_build/lib/...");
            logger.info("done.");
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }
    }


}
