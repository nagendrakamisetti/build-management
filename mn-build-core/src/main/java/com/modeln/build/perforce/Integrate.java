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

import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;



/**
 * Perform an integrate operation.
 *
 * @author Shawn Stafford
 */
public class Integrate {

	/** Log4j */
	private static Logger logger = Logger.getLogger(Integrate.class.getName());

	public static final int	INTEGRATE_SUCCEEDED = 1;
	public static final int	INTEGRATE_FAILED = 0;
	public static final int	ALREADY_INTEGRATED = 2;

    /**
     * Construct the integrate command.
     */
    private Integrate() {
    }

    /**
     * Integrate code change from a specified location to a destination location.
     * All changes between the 2 given changelist numbers will be incoorporated.
     * If the target file does not exist, the "-Dt" option branches the source file
     * before apply the indicated change.
     * This is equivalent to executing the following p4 command:
     * <p>
     * <blockquote>
     *   p4 integrate -Dt fromFile[revRange] toFile
     * </blockquote>
     *
     * @param   from    	Depot or path that should be the source
     * @param   to      	Depot or path that should be the destination
     * @param	changelistFrom	changelists range starts from
     * @param	changelistTo	changelists range ends withs
     * @return  			List of files that were integrated
     */
    public static int executeIntegrating(String from, String to, String changelistFrom, String changelistTo) {
    	return execute("-Dt "+from+"@"+changelistFrom+","+changelistTo, to);
    }
    public static int executeIntegrating_fakeIt(String from, String to, String changelistFrom, String changelistTo) {
    	return execute_fakeIt("-Dt "+from+"@"+changelistFrom+","+changelistTo, to);
    }
    
    /**
     * Branch from a specified location to a destination location.
     * All changes before the given changelist number will be incoorporated.
     * This is equivalent to executing the following p4 command:
     * <p>
     * <blockquote>
     *   p4 integrate fromFile[changelist] toFile
     * </blockquote>
     *
     * @param   from    	Depot or path that should be the source
     * @param   to      	Depot or path that should be the destination
     * @param	changelist	changelist to branch from
     * @return  			List of files that were integrated
     */
    public static int executeBranching(String from, String to, String changelist) {
    	String source ="";
    	if (changelist=="") { // branching from the newest revision
    		source = from;
    	} else {
    		source = from+"@"+changelist;
    	}
    	return execute(source, to);
    }

    /**
     * Fake a integrate with options and log the errText using "debug" but not "error"
     * This is equivalent to executing the following p4 command:
     * <blockquote>
     *   p4 integrate -n [from] [to]
     * </blockquote>
     *
     * @return  true if files were either already been integrated or being integrated successfully; false if any other integration error occurs.
     */
    public static int execute_fakeIt(String from, String to) {
    	// Need to lower the errText logging level
    	// return execute("-n "+from, to);
    	int returnValue = INTEGRATE_FAILED;
    	CommonResult result= null;
    	try {
    		String cmd = "p4 integrate -n " + from + " " + to;
            result = Common.exec(cmd, false, true);
            String errText = result.getStdErrText();
            
            if ((errText==null)||(errText.compareTo("")==0)) {
            	returnValue = INTEGRATE_SUCCEEDED;
            } else {
            	if (errText.trim().endsWith("already integrated.")) {
            		logger.debug(errText.trim());
            		returnValue = ALREADY_INTEGRATED;
            	} else {
            		logger.debug(errText.trim());
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
	    return returnValue;
    }
    
    /**
     * Perform a integrate with options  
     * This is equivalent to executing the following p4 command:
     * <blockquote>
     *   p4 integrate [from] [to]
     * </blockquote>
     *
     * @return  true if files were either already been integrated or being integrated successfully; false if any other integration error occurs.
     */
    public static int execute(String from, String to) {
    	int returnValue = INTEGRATE_FAILED;
    	CommonResult result= null;
    	try {
    		String cmd = "p4 integrate " + from + " " + to;
            result = Common.exec(cmd, false, true);
            String errText = result.getStdErrText();
            
            if ((errText==null)||(errText.compareTo("")==0)) {
            	returnValue = INTEGRATE_SUCCEEDED;
            } else {
            	if (errText.trim().endsWith("already integrated.")) {
            		logger.debug(errText.trim());
            		returnValue = ALREADY_INTEGRATED;
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
	    return returnValue;
    }    

}
