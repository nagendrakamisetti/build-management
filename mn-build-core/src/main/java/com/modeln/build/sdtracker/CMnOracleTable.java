package com.modeln.build.sdtracker;


import com.modeln.testfw.reporting.CMnTable;

import java.io.PrintStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;


/**
 * Utility class used to query the database for data.
 *
 * @version      1.0
 * @author       Shawn Stafford (sstafford@modeln.com)
 */
public class CMnOracleTable extends CMnTable {

    /** Formatter used to represent a date in Oracle queries */
    protected static final SimpleDateFormat ORACLE_DATE = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /** String used by the Oracle TO_DATE function when parsing a string */
    protected static final String ORACLE_DATE_FORMAT = "YYYY-MM-DD HH24:MI:SS";


    /**
     * Convenience method for returning a string containing the date
     * as a TO_DATE function.
     *
     * @param   date   Date to use in an Oracle query
     * @return  String representing the date as a TO_DATE function call
     */
    protected String format(Date date) {
        return "TO_DATE('" + ORACLE_DATE.format(date) + "', '" + ORACLE_DATE_FORMAT + "')"; 
    }

}

