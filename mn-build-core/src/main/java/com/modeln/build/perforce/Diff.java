package com.modeln.build.perforce;

import java.lang.Exception;
import java.lang.IllegalArgumentException;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;


public class Diff {

	/** Log4j */
	private static Logger logger = Logger.getLogger(Diff.class.getName());

    /**
     * Run Perforce "diff" of a client workspace file against its the depot file.
     * The file is only compared if the file is opened for edit.  
     * Whitespace changes are ignored.
     * This is equivalent to executing the following p4 command:
     * <blockquote>
     *   p4 diff -db 
     * </blockquote>
     *
     * @param	Path of the file to diff
     * @return  boolean indicating if the workspace and the depot files are the same, ignoring white spaces 
     */
    public static boolean hasDifferences (String file) throws IllegalArgumentException, FileNotFoundException {
        if ((file==null)||(file=="")) {
                throw new IllegalArgumentException("Missing filename");
        } 
	String result = execute("-db", file);
        if (result==null) {
                throw new FileNotFoundException("Unable to diff file "+file+".\n See /var/tmp/servicepatch.detail.log for details.");
        }
        /* remove metadata and test if having any differences */
        String diffResult = result.replaceFirst("====.*====", "");
        if (diffResult.length() > 0) {
            	logger.debug("Found differences between the client and the depot file: "+result);
                return true;
        } else {
            	logger.debug("Client file ");
            	logger.debug("No differences between the client and the depot file: "+file);
                return false;
        }
    }
    
    /**
     * Display diff of client file with depot file 
     * This is equivalent to executing the following p4 command:
     * <blockquote>
     *   p4 diff [flag] [file]
     * </blockquote>
     *
     * @return  the diff content 
     */
    private static String execute(String flag, String file) {
    	String cmd, errText, outText;
    	boolean success = false;
    	CommonResult result= null;
    	try {
    		if (flag == "") {
    			cmd = "p4 diff "+file;	
    		} else {
    			cmd = "p4 diff "+flag+" "+file;
    		}    		
            result = Common.exec(cmd, true, true);
            errText = result.getStdErrText();
            if ((errText==null)||(errText.compareTo("")==0)) {
            	success = true;
            } else {
            	logger.debug(errText.trim());
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
                outText = result.getStdOutText();
                if ( outText!=null ) {
                     return outText.trim();
                }
        } 
	return null;
    }

    
}
