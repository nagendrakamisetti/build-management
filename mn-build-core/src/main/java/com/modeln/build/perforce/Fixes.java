package com.modeln.build.perforce;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;


public class Fixes {

    /** Log4j */
    private static Logger logger = Logger.getLogger(Fixes.class.getName());

    public static List<FixRecord> getFixes(String filePattern) {
        return execute(filePattern);
    }

    public static List<FixRecord> getFixesForJob( String job, String filePattern ) {
        return execute("-j "+job+" "+filePattern);
    }
	
    public static List<FixRecord> getFixesForChanglist(String changelist, String filePattern ) {
        return execute("-c "+changelist+" "+filePattern);
    }
	 
    /**
     * List what changelists fix what jobs 
     * This is equivalent to executing the following p4 command:
     * <blockquote>
     *   p4 fixes [flag]
     * </blockquote>
     *
     * @return  A list of strings associating changelists to jobs (bugfixes) in the following pattern:
     * 			[job] fixed by change [changelist] on [date] by [userEmail] 
     */
    private static List<FixRecord> execute(String args) {
    	List fixes = null;
    	CommonResult result= null;
    	try {
    		String cmd = "p4 fixes "+args;
            result = Common.exec(cmd, true, true);
            String resultText = result.getStdOutText();
            String errText = result.getStdErrText();
            if ((errText==null)||(errText.compareTo("")==0)) {
            	fixes = parseResults( resultText );
            } else {
            	logger.error(errText.trim());
            }
        } catch (IOException ex) {
            logger.error("Unable to perform the Perforce command.");
            ex.printStackTrace();
	    } catch (InterruptedException ex) {
	        ex.printStackTrace();
	    } catch (RuntimeException ex) {
	        ex.printStackTrace();
	    }
	    return fixes;
    }
    
    /**
     * Parse the result output into a list of fixes
     *  
     * @param 	resultText
     * @return	List of FixRecords
     */
    private static List parseResults(String resultText ) {
    	List fixes = new ArrayList();
    	FixRecord fix;
    	String fixString;
    	String empty = "";
    	
    	String[] results = resultText.split("\n");
    	for (int i=0; i<results.length; i++) {
    		if ( ! results[i].trim().equals(empty)) {
    			fixString = results[i];
    			fix = new FixRecord( fixString );
        		fixes.add(fix);
    		}
    	}
    	return fixes;
    }

}
