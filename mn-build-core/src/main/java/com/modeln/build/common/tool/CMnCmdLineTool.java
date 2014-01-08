/*
 * Copyright 2000-2002 by Model N, Inc.  All Rights Reserved.
 * 
 * This software is the confidential and proprietary information
 * of Model N, Inc ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you
 * entered into with Model N, Inc.
 */
package com.modeln.build.common.tool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import org.apache.commons.cli.AlreadySelectedException;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.UnrecognizedOptionException;

/**
 * The command line tool provides common methods which can be reused
 * by all command line tool implementations.  The common tool 
 * functionality includes displaying the command line arguments.
 */
public class CMnCmdLineTool {

    /** Command line option for displaying help and usage information */
    public static final Option ARG_HELP = new Option("h", "help", false, "Command-line help");

    /** Define the command-line parser used to parse arguments */
    protected static BasicParser argParser = new BasicParser();

    /** List of all possible command line arguments */
    protected static Options cmdOptions = new Options();

    /** Name of the command */
    protected static String cmdName = "cmd";

    /** Number of seconds to wait before prompting the user again */
    protected static int promptInterval = 600;

    /**
     * Calculate the elapsed time and return a string representing
     * the elapsed time in a human-readable format.
     *
     * @param   start     Starting date
     * @param   end       Ending date
     * @return  Elapsed time text
     */
    public static String getElapsedTime(Date start, Date end) {
        long ms = end.getTime() - start.getTime();

        long elapsedMillis   =  ms %  1000;
        long elapsedSeconds  = (ms /  1000) % 60;
        long elapsedMinutes  = (ms / (1000*60) ) % 60;
        long elapsedHours    = (ms / (1000*60*60) ) % 24;
        long elapsedDays     =  ms / (1000*60*60*24);

        StringBuffer elapsedStr = new StringBuffer();
        if (elapsedDays > 0) {
            elapsedStr.append(elapsedDays + "days ");
        }

        if (elapsedHours > 0) {
            elapsedStr.append(elapsedHours + "hr ");
        }

        if (elapsedMinutes > 0) {
            elapsedStr.append(elapsedMinutes + "min ");
        }

        if (elapsedSeconds > 0) {
            elapsedStr.append(elapsedSeconds + "sec ");
        }

        if (elapsedMillis > 0) {
            elapsedStr.append(elapsedMillis + "ms ");
        }

        return elapsedStr.toString();
    }

    /**
     * Terminate the program with an error message.
     */
    public static void fail(String msg) {
        display(msg);
        System.exit(1);
    }

    /**
     * Display a message to the user.
     *
     * @param   msg       Text to be displayed
     */
    public static void display(String msg) {
        System.out.println(msg);
    }

    /**
     * Display an error to the user.
     *
     * @param   ex        Exception to display
     */
    public static void displayException(Exception ex) {
        ex.printStackTrace();
    }

    /**
     * Write the elapsed time to a logger.
     *
     * @param   start     Starting date
     * @param   end       Ending date
     * @param   msg       Message to display
     */
    public static void logElapsedTime(Logger logger, Date start, Date end, String msg) {
        String elapsedTime = getElapsedTime(start, end);
        logger.fine(msg + elapsedTime);
    }


    /**
     * Display the elapsed time spent performing an action.
     *
     * @param   start     Starting date
     * @param   end       Ending date
     * @param   msg       Message to display
     */
    public static void displayElapsedTime(Date start, Date end, String msg) {
        String elapsedTime = getElapsedTime(start, end);
        display(msg + elapsedTime);
    }



    /**
     * Set the command name.
     */
    protected static void setCommandName(String name) {
        cmdName = name;
    }

    /**
     * Display the command line help.
     */
    protected static void displayHelp() {
        HelpFormatter help = new HelpFormatter();
        help.printHelp(cmdName, cmdOptions, true);
    }

    /**
     * Wait for the executing process to complete.
     *
     * @param  process  Process to monitor
     * @param  profile  Enable or disable execution profiling
     */
    public static int wait(Process process, boolean profile) throws InterruptedException {
        Date startDate = null;
        if (profile) {
            startDate = new Date();
        }

        // Use a separate thread to relay the process output to the user
        CMnProcessOutput stdout = new CMnProcessOutput(process, process.getInputStream());
        CMnProcessOutput stderr = new CMnProcessOutput(process, process.getErrorStream());
        Thread out = new Thread(stdout);
        Thread err = new Thread(stderr);
        out.start();
        err.start();

        // Wait for the process to complete
        int result = process.waitFor();

        // Display the elapsed time for this operation
        if (profile) {
            displayElapsedTime(startDate, new Date(), "Execution complete: ");
        }

        return result;
    }

    /**
     * Prompt for user input "yes/no" to continue with the program or not.
     * The user will continue to be prompted for unput until either yes 
     * or no is provided.
     *
     * @param   question   The question to present to the user
     * @return true if user answers yes, false otherwise
     */
    public static boolean doContinue(String question) {
        String yes = "yes";
        String no = "no";

        String prompt = question + " (" + yes + "/" + no + "): ";
        String answer = null;
        boolean validResponse = false;
        boolean saidYes = false;

        try {
            // Prompt the user for input
            CMnContinuousPrompt cp = new CMnContinuousPrompt(promptInterval, prompt);
            Thread t = new Thread(cp);
            t.start();
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            while (!validResponse) {
                cp.showPrompt();
                answer = br.readLine().trim();
                validResponse = ( answer.equalsIgnoreCase(yes) || answer.equalsIgnoreCase(no) );
            }
            cp.setEnabled(false);

            // Determine whether the user responded "yes"
            saidYes = answer.equalsIgnoreCase(yes);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return saidYes;
    }


    /**
     * Parse the command line arguments.
     */
    protected static CommandLine parseArgs(String[] args) {
        CommandLine cl = null;
        try {
            cl = argParser.parse(cmdOptions, args);
        } catch (AlreadySelectedException dupex) {
            displayHelp();
            display("\nDuplicate option: " + dupex.getMessage());
        } catch (MissingOptionException opex) {
            displayHelp();
            display("\nMissing command line option: " + opex.getMessage());
        } catch (UnrecognizedOptionException uex) {
            displayHelp();
            display(uex.getMessage());
        } catch (ParseException pe) {
            display("Unable to parse the command line arguments: " + pe);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return cl;
    }

}
