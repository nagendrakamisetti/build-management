package com.modeln.build.perforce;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;


public class Filelog {

	/** Log4j */
	private static Logger logger = Logger.getLogger(Filelog.class.getName());

	/* To show file revisions across branches */
    private static final String	FLAG_CROSS_BRANCH = "-i";

    /**
     * Print detailed information about files' revisions following file history 
     * across branches. If a file was created by integration via p4 integrate, 
     * Perforce describes the file's revisions and displays the revisions of the 
     * file from which it was branched (back to the branch point of the original
     *  file).
     * This is equivalent to executing the following p4 command:
     * <blockquote>
     *   p4 fileLog -i
     * </blockquote>
     *
     * @return  List of files that were resolved
     */
    public static List listAllBranches(String file) {
    	return execute_getList(FLAG_CROSS_BRANCH, file);
    }
    
    public static List list(String file) {
    	return execute_getList("", file);
    }

    /**
     * Print detailed information about files' revisions 
     * This is equivalent to executing the following p4 command:
     * <blockquote>
     *   p4 fileLog [flag]
     * </blockquote>
     *
     * @return  true if able to print the fileLog; false otherwise
     */
    private static List execute_getList(String flag, String file) {
    	List outList = new ArrayList();
    	String outText = execute(FLAG_CROSS_BRANCH, file);
    	if (outText=="") {
    		logger.error("p4 filelog stdout is an empty string!");
    		return null;
    	} else if (outText==null) {
    		return null;
        }       
    	String[] lines = outText.split("\n");
    	for (int i=0; i<lines.length; i++) {
    		outList.add(lines[i]);
    	}
	    return outList;
    }
    
    /**
     * Print detailed information about files' revisions 
     * This is equivalent to executing the following p4 command:
     * <blockquote>
     *   p4 fileLog [flag]
     * </blockquote>
     *
     * @return  true if able to print the fileLog; false otherwise
     */
    private static String execute(String flag, String file) {
    	String cmd, errText, outText;
    	boolean success = false;
    	CommonResult result= null;
    	try {
    		if (flag == "") {
    			cmd = "p4 filelog "+file;	
    		} else {
    			cmd = "p4 filelog "+flag+" "+file;
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
