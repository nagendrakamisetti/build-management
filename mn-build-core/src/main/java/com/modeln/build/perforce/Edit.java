package com.modeln.build.perforce;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.ArrayList;

import org.apache.log4j.Logger;


public class Edit {

	/** Log4j */
	private static Logger logger = Logger.getLogger(Edit.class.getName());

	/* the full name of the file to be edited */
	private String file;
	
	/* the pending changelist# that has been previously created with 'p4 change */
	private String changelistNumber;

	/* the file type this file will be opened as.  See below for the list of types */
	private String fileType;
	
	public static final String FILETYPE_TEXT = "text";
	public static final String FILETYPE_BINARY = "binary";
	public static final String FILETYPE_SYMLINK = "symlink";
	public static final String FILETYPE_APPLE = "apple";
	public static final String FILETYPE_RESOURCE = "resource";
	public static final String FILETYPE_UNICODE = "unicode";

	private Edit() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * Open existing file(s) for edit.  The file(s) will be opened as its previous 'filetype' and
	 * will be put into the default changelist.
     * <p>
     * <blockquote>
     *   p4 edit file ...
     * </blockquote>
     *
     * @param   file    the file to be edited
     * @return			return a list of files opened for edit; return null if action failed
	 */
	public static boolean openFile (String file) {
		if ((file==null)||(file=="")) {
			throw new IllegalArgumentException("File name not supplied.");
		}
		String command = "p4 edit "+file;
		return execute(command);
	}
	
	/**
	 * Open existing file(s) for edit.  The file(s) will be opened as its previous 'filetype' and
	 * will be put into a pending changelist.
     * <p>
     * <blockquote>
     *   p4 edit [ -c changelist# ] file ...
     * </blockquote>
     *
     * @param   file    			the file to be edited
     * @param   changelistNumber	the pending changelist that this file will be put into. This changelist must have been previously created by 'p4 change' 
     * @return						return a list of files opened for edit; return null if action failed
	 */
	public static boolean openFile (String file, String changelistNumber) {
		if ((file==null)||(file=="")) {
			throw new IllegalArgumentException("File name not supplied.");
		}
		if ((changelistNumber==null)||(changelistNumber=="")) {
			throw new IllegalArgumentException("Changelist number not supplied.");
		}
		String command = "p4 edit "+file+" -c "+changelistNumber;
		return execute(command);
	}
	
	/**
	 * Open existing file(s) for edit.  The file(s) will be opened as 'filetype' and
	 * will be put into a pending changelist.
     * <p>
     * <blockquote>
     *   p4 edit [ -c changelist# ] [ -t filetype ] file ...
     * </blockquote>
     *
     * @param   file    			the file to be edited
     * @param   changelistNumber	the pending changelist that this file will be put into. This changelist must have been previously created by 'p4 change' 
     * @param	fileType			the filetype that this file will be opened as.
     * @return						return a list of files opened for edit; return null if action failed
	 */
	public static boolean openFile (String file, String changelistNumber, String fileType) {
		if ((file==null)||(file=="")) {
			throw new IllegalArgumentException("File name not supplied.");
		}
		if ((changelistNumber==null)||(changelistNumber=="")) {
			throw new IllegalArgumentException("Changelist number not supplied.");
		}
		if ((fileType==null)||(fileType=="")) {
			throw new IllegalArgumentException("File type not supplied.");
		}
		if ((fileType==FILETYPE_TEXT)||(fileType==FILETYPE_BINARY)||(fileType==FILETYPE_SYMLINK)
				||(fileType==FILETYPE_APPLE)||(fileType==FILETYPE_RESOURCE)||(fileType==FILETYPE_UNICODE)) {
			String command = "p4 edit "+file+" -c "+changelistNumber+" -t "+fileType;
			return execute(command);
		} else {
			throw new IllegalArgumentException("File type has to be one of the followings: "+
					"\n\t"+FILETYPE_TEXT+
					"\n\t"+FILETYPE_BINARY+
					"\n\t"+FILETYPE_SYMLINK+
					"\n\t"+FILETYPE_APPLE+
					"\n\t"+FILETYPE_RESOURCE+
					"\n\t"+FILETYPE_UNICODE + "\n");
		}
	}
	
	private static boolean execute (String command) {
    	boolean success = true;
		ArrayList files = new ArrayList();
        try {
            Process p4 = Common.exec(command);

            // Report any errors to stderr
            BufferedReader p4err = new BufferedReader(new InputStreamReader(p4.getErrorStream()));
            String errorLine = p4err.readLine();
            if (errorLine != null) {
                success = false;
            }
            while (errorLine != null) {
                logger.error(errorLine);
                errorLine = p4err.readLine();
            }

            // Check the command result to determine if the update was performed
            BufferedReader p4in = new BufferedReader(new InputStreamReader(p4.getInputStream()));
            // The format of a success message is as follows:
            //
            String currentLine = p4in.readLine();
            while( currentLine != null){
                // if (currentLine.indexOf("currently opened for edit") < 0)
            	File f = new File( currentLine );
                files.add( f );
                currentLine = p4in.readLine();
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
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
