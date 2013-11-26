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

import java.io.IOException;
import org.apache.log4j.Logger;


/**
 * Merge open files with other revisions or files.
 *
 * @author Shawn Stafford
 */
public class Resolve {

	/** Log4j */
	private static Logger logger = Logger.getLogger(Resolve.class.getName());

    /** Automatic resolve, skips merging and accept their version of the file */
    private static final String	FLAG_THEIRS = "-at";

	/** Automatic resolve, skips merging and accept their version of the file */
    private static final String	FLAG_YOURS = "-ay";

	/** Automatic resolve, skips any conflicts, accept merge or the newer version of the files */
    private static final String	FLAG_MERGE = "-am";
    
	/** Automatic "Safe" resolve,  accepting only files that have either your changes or their changes, but not both. */
    private static final String	FLAG_SAFE = "-as";
    
	/** The return string signals a successful execution */
    public static final String	SUCCESS = "success";
    public static final String	FAIL = "fail";
    
    /**
     * Perform an automatic resolve that skips the merging.  
     * Instead it automatically accepts their (-at) version of the file.  
     * This is equivalent to executing the following p4 command:
     * <blockquote>
     *   p4 resolve -at
     * </blockquote>
     *
     * @return  true if either no file(s) to resolve or files are resolved successfully; false if any other resolve error occurs.
     */
    public static boolean resolveToTheirs() {
    	return execute(FLAG_THEIRS, false);
    }
    public static String resolveToTheirs_getStdout() {
    	String outText = execute_getStdout(FLAG_THEIRS, true);
    	logger.debug(outText);
    	return outText;
    }
    
    /**
     * Perform an automatic resolve that skips the conflicts.  
     * Instead it automatically accepts their (-at) version of the file.  
     * This is equivalent to executing the following p4 command:
     * <blockquote>
     *   p4 resolve -at
     * </blockquote>
     *
     * @return 	true if either no file(s) to resolve or files are resolved successfully; false if any other resolve error occurs.
     */
    public static boolean resolveToYours() {
    	return execute(FLAG_YOURS, false);
    }
    
    /**
     * Perform an automatic resolve that skips the conflicts.  
     * If there are no conflicts and yours hasn't changed it accepts theirs.
     * If theirs hasn't changed it accepts yours
     * If both yours and theirs have changed it accepts the merge.  
     * Files that have no base for merging (e.g. binary files) are always skipped.
     * This is equivalent to executing the following p4 command:
     * <blockquote>
     *   p4 resolve -am
     * </blockquote>
     *
     * @return  true if either no file(s) to resolve or files are resolved successfully; false if any other resolve error occurs.
     */
    public static boolean resolveToMerge() {
   		return execute(FLAG_MERGE, false);
    }
    public static String resolveToMerge_getStdout() {
    	String outText = execute_getStdout(FLAG_MERGE, true);
    	logger.debug(outText);
    	return outText;
    }
    
    /**
     * Perform an automatic "safe" resolve.  
     * accepting only files that have either your changes or their changes, 
     * but not both.
     * Files with changes to both yours and theirs are skipped.
     * This is equivalent to executing the following p4 command:
     * <blockquote>
     *   p4 resolve -as
     * </blockquote>
     *
     * @return  true if either no file(s) to resolve or files are resolved successfully; false if any other resolve error occurs.
     */
    public static boolean resolveSafe() {
    	return execute(FLAG_SAFE, false);
    }
    
    /**
     * Perform a resolve with options  
     * This is equivalent to executing the following p4 command:
     * <blockquote>
     *   p4 resolve [flag]
     * </blockquote>
     *
     * @return  stdout (parseIn==true) or RESULT_SUCCESS (parseIn==false) if resolved successfully; else return RESULT_FAIL
     */
    private static String execute_getStdout(String flag, boolean parseIn) {
    	String errText, outText, returnText;
    	boolean success = false;
    	CommonResult result= null;
    	try {
    		String cmd = "p4 resolve -db "+flag;
            result = Common.exec(cmd, parseIn, true);
            errText = result.getStdErrText();
            if ((errText==null)||(errText.compareTo("")==0)) {
            	success = true;
            } else {
            	if (errText.startsWith("No file(s) to resolve.")) {
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
	    /* set returnText */
		if (success) {
        	returnText = SUCCESS;
        	if (parseIn) {
                outText = result.getStdOutText();
                if ( outText!=null ) {
                	returnText = outText.trim();
                }
        	} 
        } else {
        	returnText = FAIL;
        }
	    return returnText;
    }
    
    /**
     * Perform a resolve with options  
     * This is equivalent to executing the following p4 command:
     * <blockquote>
     *   p4 resolve [flag]
     * </blockquote>
     *
     * @return  true if either no file(s) to resolve or files are resolved successfully; false if any other resolve error occurs.
     */
    private static boolean execute(String flag, boolean parseIn) {
    	String result = execute_getStdout(flag, parseIn);
    	if (result.equals(SUCCESS)) {
    		return true;
    	} else return false;
    }
    
}
