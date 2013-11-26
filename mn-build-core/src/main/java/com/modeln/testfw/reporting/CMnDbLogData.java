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

import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * Data object used to represent a log file associated with a build. 
 * 
 * @author  Shawn Stafford
 */
public class CMnDbLogData {

    /** Log content formatted as plain text. */
    public static final int TEXT_FORMAT = 0;

    /** Log content formatted as plain text. */
    public static final int HTML_FORMAT = 1;


    /** Unique key value used to identify a log */
    private int logId;

    /** Name string which identifies the log */
    private String logName;

    /** Format of the log content (html, text, etc) */
    private int logType;

    /** Contents of the log file */
    private String logText;


    /**
     * Set the unique key value used to identify a single log.
     * This value is typically an auto-incremented value created by the database when
     * a new entry is inserted into the table.
     *
     * @param    id     Key value
     */
    public void setId(int id) {
        logId = id;
    }

    /**
     * Return the unique key used to identify the log. 
     *
     * @return  Key value
     */
    public int getId() {
        return logId;
    }


    /**
     * Set the name of the log file. 
     * 
     * @param  name  Log file name 
     */
    public void setName(String name) {
        logName = name;
    }

    /**
     * Return the name of the log file. 
     * 
     * @return  Log file name 
     */
    public String getName() {
        return logName;
    }

    /**
     * Set the text of the log file. 
     *
     * @param  text  Contents of the log file 
     */
    public void setText(String text) {
        logText = text;
    }

    /**
     * Return the text of the log file.
     *
     * @return  Log file content 
     */
    public String getText() {
        return logText;
    }

    /**
     * Set the file format type for the log file (html, text, etc).
     *
     * @param  format  File format of the log file 
     */
    public void setFormat(int format) {
        logType = format;
    }

    /**
     * Return the file format of the log file.
     *
     * @return  File format of the log file 
     */
    public int getFormat() {
        return logType;
    }

}
