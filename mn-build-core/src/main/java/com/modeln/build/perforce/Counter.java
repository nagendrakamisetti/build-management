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

import org.apache.log4j.Logger;



/**
 * Obtains counter information from the Perforce server.
 *
 * @author Shawn Stafford
 */
public class Counter {

	/** Log4j */
	private static Logger logger = Logger.getLogger(Counter.class.getName());

    /** Name used to identify the counter */
    private String counterName;

    /** Counter value */
    private int counterValue = 0;

    /**
     * Construct a new counter.
     *
     * @param   name   Counter name
     */
    private Counter(String name) {
        counterName = name;
    }

    /**
     * Set the counter name. 
     *
     * @param   name  Counter name
     */
    public void setName(String name) {
        counterName = name;
    }

    /**
     * Return the counter name. 
     *
     * @return   Counter name
     */
    public String getName() {
        return counterName;
    }

    /**
     * Set the counter value.
     *
     * @param   value     Counter value
     */
    public void setValue(int value) {
        counterValue = value;
    }

    /**
     * Return the counter value.
     *
     * @return     Counter value
     */
    public int getValue() {
        return counterValue;
    }


    /**
     * Deletes a counter.  The method will return TRUE if the 
     * counter was deleted successfully, or FALSE if an error was encountered.
     *
     * This is equivalent to executing the following p4 command:
     * <blockquote>
     *   p4 counter -d &lt;name&gt;
     * </blockquote>
     *
     * @param   name   Name of the counter
     */
    public static boolean deleteCounter(String name) {
        boolean success = false;

        try {
            Process p4 = Common.exec("p4 counter -d " + name);
            BufferedReader p4in = new BufferedReader(new InputStreamReader(p4.getInputStream()));
            String msg = Common.readLine(p4in);
            logger.info(msg);

            // Determine if the counter was successfully set
            String successMsg = "Counter " + name + " deleted.";
            if (successMsg.equals(msg.trim())) {
                success = true;
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
     * Set the value of a counter.  The method will return TRUE if the 
     * counter was set correctly, or FALSE if an error was encountered.
     *
     * This is equivalent to executing the following p4 command:
     * <blockquote>
     *   p4 counter &lt;name&gt; &lt;value&gt;
     * </blockquote>
     *
     * @param   name   Name of the counter
     * @param   value  Value of the counter
     */
    public static boolean setCounter(String name, int value) {
        boolean success = false;

        try {
            Process p4 = Common.exec("p4 counter " + name + " " + value);
            BufferedReader p4in = new BufferedReader(new InputStreamReader(p4.getInputStream()));
            String msg = Common.readLine(p4in);

            // Determine if the counter was successfully set
            String successMsg = "Counter " + name + " set.";
            if (successMsg.equals(msg.trim())) {
                logger.info("Counter set: " + name + "=" + value);
                success = true;
            } else {
                logger.info(msg);
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
     * Obtain the value of an existing counter.
     * This is equivalent to executing the following p4 command:
     * <blockquote>
     *   p4 counter &lt;name&gt;
     * </blockquote>
     *
     * @param   name   Name of the counter
     */
    public static Counter getCounter(String name) {
        Counter counter = null;

        try {
            Process p4 = Common.exec("p4 counter " + name);
            BufferedReader p4in = new BufferedReader(new InputStreamReader(p4.getInputStream()));
            String value = Common.readLine(p4in);
            counter = new Counter(name);
            counter.setValue(Integer.parseInt(value));
        } catch (NumberFormatException nfe) {
            logger.error("Unable to parse the counter value.");
            nfe.printStackTrace();
        } catch (IOException ex) {
            logger.error("Unable to perform the Perforce command.");
            ex.printStackTrace();
	    } catch (InterruptedException ex) {
	        ex.printStackTrace();
	    } catch (RuntimeException ex) {
	        ex.printStackTrace();
        }

        return counter;
    }


}
