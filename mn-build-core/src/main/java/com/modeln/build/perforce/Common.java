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
import java.lang.StringBuffer;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;



/**
 * Perforce constants used by the Perforce classes.
 *
 * @author Shawn Stafford
 */
public class Common {

	/** Log4j */
	private static Logger logger = Logger.getLogger(Common.class.getName());

   /** Comment string that denotes a comment line that will be ignored */
    public static final String COMMENT_STRING = "#";

    /** LineEnd character on the current system */
    public static final String EOL = "\n";
    
    /** Tab character on the current system */
    public static final String TAB = "\t";
    

    /** Date format used to represent a full date and time (2003/10/01 01:06:59) */
    public static final DateFormat LONG_DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd kk:mm:ss");

    /** Date format used to represent a date (2003/10/01) */
    public static final DateFormat SHORT_DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd");

    /**
     * Execute the Perforce command and return the resulting process.
     *
     * @return  Perforce process
     */
    protected static Process exec(String cmd) throws IOException, InterruptedException {
    	logger.debug(cmd);
        Process process = Runtime.getRuntime().exec(cmd);
        return process;
    }

    /**
     * Execute the Perforce command and return the results.
     *
     * @param	parseIn			flag to store the command result 
     * @param	parseErr		flag to store the errors
     *
     * @return  CommonResult	results from executing the Perforce command
     */
    protected static CommonResult exec(String cmd, boolean parseIn, boolean parseErr ) throws IOException, InterruptedException {
        StringBuffer err = new StringBuffer();
        StringBuffer out = new StringBuffer();
        CommonResult result = new CommonResult();
        logger.debug(cmd);

        try {
	        Process p4 = Runtime.getRuntime().exec(cmd);        
	        StreamReaderThread outThread = new StreamReaderThread(p4.getInputStream(), out);
	        StreamReaderThread errThread = new StreamReaderThread(p4.getErrorStream(), err);
	
	        outThread.start();
	        errThread.start();
	        int int_result=p4.waitFor();
	        
	        outThread.join();
	        errThread.join();
	                
	        if (parseIn) { 
	        	result.setStdOutText( out.toString() );
	        }
	        if (parseErr) { 
	        	result.setStdErrText( err.toString() );
	        }	        
	        if (int_result != 0) {
	        	logger.debug("Process "+cmd+ " returned non-zero value:"+int_result);
	        	logger.debug("----> "+ err.toString() );
	        } 
        } catch (Exception e) {
        	logger.error("Error running "+cmd);
        	e.printStackTrace();
        }

        /*
        CommonResult result = new CommonResult();
        result.setStdOutText( inBuffer.toString() );
        result.setStdErrText( errBuffer.toString() );
*/

        return result;
    }

    /**
     * 
     * @param   input    Input stream from a Perforce command
     * @return  Full text obtained from the input stream
     */
    protected static String readInput(InputStream input) throws IOException {
        StringBuffer str = new StringBuffer();
        BufferedReader p4in = new BufferedReader(new InputStreamReader(input));

        // Read each full line of input
        String currentLine = readLine(p4in);
        while (currentLine != null) {
            str.append(currentLine + "\n");
            currentLine = readLine(p4in);
        }
        p4in.close();

        return str.toString();
    }

    /**
     * 
     * @param   input    Input stream from a Perforce command
     * @return  Full text obtained from the input stream
     */
    protected static StringBuffer readInputToBuffer(InputStream input) throws IOException {
        StringBuffer str = new StringBuffer();
        BufferedReader p4in = new BufferedReader(new InputStreamReader(input));

        // Read each full line of input
        String currentLine = readLine(p4in);
        while (currentLine != null) {
            str.append(currentLine + "\n");
            currentLine = readLine(p4in);
        }
        p4in.close();

        return str;
    }
    
    /**
     * 
     * @param   input    Input stream from a Perforce command
     * @return  Single line of text obtained from the input stream
     */
    protected static String readLine(BufferedReader input) throws IOException {
        String line = input.readLine();
        //logger.debug("READING: " + line);
/**
        // Read any characters that were not terminated by a newline
        int nextChar = p4in.read();
        boolean charsFound = (nextChar != -1);
        while (nextChar != -1) {
            str.append(nextChar);
            nextChar = p4in.read();
        }
        if (charsFound) {
            str.append("\n");
        }
*/
        return line;
    }

}
