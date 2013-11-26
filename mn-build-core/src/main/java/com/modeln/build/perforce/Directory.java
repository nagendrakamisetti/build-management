/*
 * Directory.java		1/16/2006
 * 
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
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.lang.IllegalArgumentException;

import org.apache.log4j.Logger;


/**
 * Information about a Perforce directory.
 * 
 * @author Karen An
 */
public class Directory {

	/** Log4j */
	private static Logger logger = Logger.getLogger(Directory.class.getName());

    /* The depot directory */
    private String directory;
    
    /* The imediate subdirectories of the specified depot directory */
    private List subdirectories;    
    
    /**
     * Set the depot directory.
     *
     * @param dir  The depot directory
     */
    public void setDirectory(String dir) {
    	directory = dir;
    }
    
    /**
     * Return the depot directory.
     *
     * @return directory The depot directory
     */
    public String getDirectory() {
        return directory;
    }
    
    /**
     * Set the subdirectories of the specified depot directory.
     *
     * @param subDir  The imediate subdirectories of the specified depot directory
     */
    public void setSubdirectories(List subDir) {
    	subdirectories = subDir;
    }
    
    /**
     * Return the revision number of the file.
     *
     * @return The imediate subdirectories of the specified depot directory
     */
    public List getSubdirectories() {
        return subdirectories;
    }
    
   
    /**
     * Construct an empty Directory object.
     */
    private Directory() {
    }
    
    /**
     * Construct a  new directory to query
     */
    public Directory(String dir) {
    	directory = dir;
    }
    
    /**
     * Obtain a list of all opened files.
     * This is equivalent to executing the following p4 command: 
     * <blockquote>
     *   p4 dirs depot_direcotry
     * </blockquote>
     * Some examples are as follows:
     * <blockquote>
     *   p4 dirs //modeln/*
     *   p4 dirs //modeln/build/ant/lib/*
     * </blockquote>
     */
    public static List findSubdirectories(String dir) throws IllegalArgumentException {
    	List subdirs = null;
        try {
            Process p4 = Common.exec("p4 dirs "+ dir );
            BufferedReader p4in = new BufferedReader(new InputStreamReader(p4.getInputStream()));
            String currentLine = p4in.readLine();
            if (currentLine == null) {
            	return null;
            } else {
            	while (currentLine != null) {
                    subdirs.add( currentLine );
                    currentLine = p4in.readLine();
                }
            }
        } catch (IOException e) {
            logger.error("Unable to perform the Perforce command: p4 dirs "+dir);
            e.printStackTrace();
	    } catch (InterruptedException ex) {
	        ex.printStackTrace();
	    } catch (RuntimeException ex) {
	        ex.printStackTrace();
        }
        return subdirs;
    }
    
    /**
     * Validate the directory before executing the p4 command.  The directory need to match the following format:
     * <blockquote>
     *  //depot/*
     * <blockquote>
     * Some examples are as follows:
     * <blockquote>
     *   //modeln/*
     *   //modeln/build/ant/lib/*
     * </blockquote>
     */
    public static boolean existsDirectoryPattern(String dirPattern) {
    	try {
            Process p4 = Common.exec("p4 dirs "+ dirPattern );
            BufferedReader p4in = new BufferedReader(new InputStreamReader(p4.getInputStream()));
            String currentLine = p4in.readLine();
            if (currentLine == null) {
           		return false;
            } else {
            	return true;
            }
        } catch (IOException e) {
            logger.error("Unable to perform the Perforce command: p4 dirs "+dirPattern);
            e.printStackTrace();
	    } catch (InterruptedException ex) {
	        ex.printStackTrace();
	    } catch (RuntimeException ex) {
	        ex.printStackTrace();
        }
        return true;
    }    

}
