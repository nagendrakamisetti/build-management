/* 
* Copyright 2000-2003 by Model N, Inc.  All Rights Reserved. 
* 
* This software is the confidential and proprietary information 
* of Model N, Inc ("Confidential Information").  You shall not 
* disclose such Confidential Information and shall use it only 
* in accordance with the terms of the license agreement you 
* entered into with Model N, Inc. 
*/
package com.modeln.testfw.reporting;

import java.io.PrintStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;


/**
 * The table interface defines all of the methods and fields used
 * to interact with the database. 
 * 
 * @author  Shawn Stafford
 */
public class CMnTable {

    /** Define limits on the size of text in a TEXT column */
    public static final int TEXT_SIZE = 50000;

    /** Format used to represent a date */
    protected static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /** Timestamp used to prefix every test message line */
    protected static final SimpleDateFormat DATETIME = new SimpleDateFormat(DATETIME_FORMAT);

    /** Create a null timestamp that can be used when the date is unavailable or cannot be formatted */
    protected static final String NULL_DATETIME = "0000-00-00 00:00:00";

    /** Enables or disables output to the debug print stream */
    private boolean debugEnabled = false;

    /** Output stream used to write debug messages to. */
    private PrintStream debug = null;

    /** Counts the number of times a named query has been executed */
    private Hashtable<String, CMnQueryPerformance> queryPerformance = new Hashtable<String, CMnQueryPerformance>();


    /**
     * Return the number of times the named query has been executed.
     *
     * @param   name   Query name
     * @return  Query execution count
     */
    public int getQueryCount(String name) {
        CMnQueryPerformance perf = (CMnQueryPerformance) queryPerformance.get(name);
        if (perf != null) {
            return perf.getQueryCount();
        } else {
            return 0;
        }
    }

    /**
     * Record the performance of the named query.
     *
     * @param  name   Query name
     * @param  query  Query execution data
     */
    public void trackQueryPerformance(String name, CMnQueryData query) {
        CMnQueryPerformance perf = (CMnQueryPerformance) queryPerformance.get(name);
        if (perf != null) {
            perf.add(query);
        } else {
            perf = new CMnQueryPerformance();
            perf.add(query);
            queryPerformance.put(name, perf);
        }
    }

    /**
     * Reset the query count to zero for all named queries.
     */
    public void resetQueryPerformance() {
        queryPerformance = new Hashtable<String, CMnQueryPerformance>();
    }

    /**
     * Print a summary of the named query execution count.
     *
     * @param    msg    Message to display just prior to printing the query data
     */
    public void debugQueryPerformance(PrintStream stream) {
        Enumeration keys = queryPerformance.keys();
        while (keys.hasMoreElements()) {
            String name = (String) keys.nextElement();
            CMnQueryPerformance perf = (CMnQueryPerformance) queryPerformance.get(name);
            stream.println(name + ": \t" + perf.toString());
            stream.flush();
        }
    }

    /**
     * Convenience wrapper for collecting and debugging query execution time information.
     *
     * @param   st    SQL statement used to execute the query
     * @param   name  Unique name for the query
     * @param   sql   SQL query to execute
     */
    public synchronized void execute(Statement st, String name, String sql) throws SQLException {
        // Execute the query and write debugging messages if debugging is enabled
        try {
            // Execute the query and track the execution time
            Date start = new Date();
            boolean result = st.execute(sql);
            Date end = new Date();
            long elapsed = end.getTime() - start.getTime();

            // Record the query data so we can analyze the results later
            CMnQueryData queryData = new CMnQueryData();
            queryData.setTime(elapsed);
            trackQueryPerformance(name, queryData);

            // Use the query count as the query ID
            int count = getQueryCount(name);
            queryData.setId(Integer.toString(count));

            debugWrite("Executed query " + name + ":" + count + " in " + elapsed + " ms: " + sql);
        } catch (SQLException ex) {
            debugWrite("Failed to execute query " + name + ": " + sql);
            debugWrite("Exception found for query " + name + ": " + ex.toString() );
            System.err.println("Failed to execute query: " + sql);
            ex.printStackTrace();
            throw ex;
        }
    }


    /**
     * Convenience wrapper for collecting and debugging query execution time information.
     * This method executes an insert statement and assumes that a single auto-generated
     * value will be returned.
     *
     * @param   st    SQL statement used to execute the query
     * @param   name  Unique name for the query
     * @param   sql   SQL query to execute
     *
     * @return  Auto-generated value 
     */
    public synchronized String executeInsert(Statement st, String name, String sql) throws SQLException {
        String result = null;

        // Execute the query and write debugging messages if debugging is enabled
        try {
            // Execute the query and track the execution time
            Date start = new Date();
            boolean hasResultSet = st.execute(sql, Statement.RETURN_GENERATED_KEYS);
            ResultSet rs = getGeneratedKeys(st);
            if (rs != null) {
                rs.first();
                result = rs.getString(1);
            } else {
                System.err.println("Unable to obtain generated key.");
            }

            Date end = new Date();
            long elapsed = end.getTime() - start.getTime();

            // Record the query data so we can analyze the results later
            CMnQueryData queryData = new CMnQueryData();
            queryData.setTime(elapsed);
            trackQueryPerformance(name, queryData);

            // Use the query count as the query ID
            int count = getQueryCount(name);
            queryData.setId(Integer.toString(count));

            debugWrite("Executed query " + name + ":" + count + " in " + elapsed + " ms: " + sql);
            if (result != null) {
                debugWrite("Returned generated key: " + hasResultSet + ", " + result);
            }
        } catch (SQLException ex) {
            debugWrite("Failed to execute query: " + sql);
            System.err.println("Failed to execute query: " + sql);
            ex.printStackTrace();
            throw ex;
        }

        return result;
    }


    /**
     * Convenience wrapper for collecting and debugging query execution time information.
     *
     * @param   st    SQL statement used to execute the query
     * @param   name  Unique name for the query
     * @param   sql   SQL query to execute
     *
     * @return  Query results
     */
    public synchronized ResultSet executeQuery(Statement st, String name, String sql) throws SQLException {
        ResultSet rs = null;

        // Execute the query and write debugging messages if debugging is enabled
        try {
            // Execute the query and track the execution time 
            Date start = new Date();
            rs = st.executeQuery(sql);
            Date end = new Date();
            long elapsed = end.getTime() - start.getTime();

            // Record the query data so we can analyze the results later 
            CMnQueryData queryData = new CMnQueryData();
            queryData.setTime(elapsed); 
            trackQueryPerformance(name, queryData);

            // Use the query count as the query ID
            int count = getQueryCount(name);
            queryData.setId(Integer.toString(count));

            debugWrite("Executed query " + name + ":" + count + " in " + elapsed + " ms: " + sql); 
        } catch (SQLException ex) {
            debugWrite("Exception encountered while executing query: " + ex);
            debugWrite("Failed to execute query: " + sql);
            System.err.println("Failed to execute query: " + sql);
            ex.printStackTrace();
            throw ex;
        }

        return rs;
    }


    /**
     * Set the output stream for debugging output.
     *
     * @param  stream   Debugging output stream
     */
    public synchronized void setDebugOutput(PrintStream stream) {
        debug = stream;
    }

    /**
     * Enable SQL debugging by passing in a reference to an output stream.  Any 
     * debugging messages will be written to this stream when debugging is enabled.
     *
     * @param  enable   TRUE to enable debug output 
     */
    public synchronized void debugEnable(boolean enable) {
        debugEnabled = enable;
    }

    /**
     * Determines if debugging output is available.
     *
     * @return Returns TRUE if debugging is enabled, FALSE otherwise.
     */
    public boolean debugActive() {
        return (debugEnabled && (debug != null));
    }
     
    /**
     * Writes a debugging message to the debugging output stream if debugging is enabled.
     *
     * @param  str   Debugging output string
     */
    public synchronized void debugWrite(String str) {
        if (debugActive()) {
            debug.println(str);
            debug.flush();
        } 
    }

    /**
     * Writes a stack trace to the debugging output stream if debugging is enabled.
     *
     * @param  ex   Exception
     */
    public synchronized void debugWrite(Exception ex) {
        if (debugActive()) {
            StackTraceElement[] lines = ex.getStackTrace();
            for (int idx = 0; idx < lines.length; idx++) {
                debug.println(lines[idx]);
                debug.flush();
            }
        }
    }

    /**
     * Return the last primary key that was generated.  The JDBC API provided by
     * JDK 1.3.1 does not provide a mechanism for retrieving the generated key
     * values for fields which autmatically generate values.  This method simulates
     * this functionality by performing a SELECT statement to obtain the last key
     * entry.  This is highly inefficient and could introduce syncronization problems
     * if used incorrectly.  Once this class is no longer restricted to JDK 1.3.1
     * compatibility, it is strongly advised that the Statement.getGeneratedKeys
     * method be used instead of this method.
     *
     * @param   st    Statement used to obtain the most recent generated key
     */
    protected static ResultSet getGeneratedKeys(Statement st) throws SQLException {
        return st.executeQuery("SELECT LAST_INSERT_ID()");
    }


    /**
     * Escape any reserved characters from the text so it can be
     * used in a query string.
     *
     * @param   text    String to be escaped
     * @return  Text containing escaped character sequences
     */
    protected static String escapeQueryText(String text) {
        text = normalizeToAscii(text);
        text = replaceChar(text, '\\', "\\\\");
        text = replaceChar(text, '"', "\\\"");
        text = replaceChar(text, '\'', "\\'");
        return text;
    }

    /**
     * Truncate the message text to fit within the limits of the column 
     * storage limitations.
     *
     * @param   text   String to be truncated
     * @param   size   Number of characters allowed in the string
     * @return  The text truncated to the specified size
     */
    protected static String escapeQueryText(String text, int size) {
        // Size of the unescaped text
        int rawTextSize = text.length();

        // Assume we want to continually reduce the escaped text size by 
        // a certain factor, but not loop more than 10 times 
        int reduceByScale = size / 10;

        // Escaping the text will result in a longer string
        // so we need to continually reduce the escaped string size
        String strEsc = escapeQueryText(text);
        while ((strEsc != null) && (strEsc.length() > size) && (rawTextSize > 0)) {
            // Reduce the text size
            rawTextSize = rawTextSize - reduceByScale;

            // Escape a substring of the text and see if that fits the size requirement
            if (rawTextSize > 0) {
                text = text.substring(0, rawTextSize);
                strEsc = escapeQueryText(text);
            }
        }

        return strEsc;
    }

    /**
     * Replaces a single character with a string.
     *
     * @param   line    Line of text to be updated
     * @param   ch      character to be replaced
     * @param   str     String to replace the character with
     * @return  String containing the substituted values
     */
    protected static String replaceChar(String line, char ch, String str) {
        String newString = "";
        int idxChar;

        while ((line != null) && (line.length() > 0)) {
            idxChar = line.indexOf(ch);
            if (idxChar >= 0) {
                newString = newString + line.substring(0, idxChar) + str;
                // Trim the processed information from the current string
                line = line.substring(idxChar + 1);
            } else {
                // Get the rest of the line when no more characters are found
                newString = newString + line;
                line = null;
            }
        }
        return newString;
    }

    /**
     * Normalize the text to ASCII characters and then strip out any 
     * remaining non-ASCII characters. 
     *
     * @param   line    Line of text to be updated
     * @return  String containing the substituted values
     */
    protected static String normalizeToAscii(String line) {
        String newString = line;
        if (line != null) {
            // Normalize the string to remove non-ASCII character variations
            newString = Normalizer.normalize(newString, Normalizer.Form.NFD);

            // Replace any remaining non-ASCII characters
            newString = newString.replaceAll("\\P{InBasic_Latin}", "");
        }
        return newString;
    }


}

