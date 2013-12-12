package com.modeln.build.sdtracker;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;


/* Table containing the bug information
SQL> desc sdr
 Name					   Null?    Type
 ----------------------------------------- -------- ----------------------------
 OID					   NOT NULL NUMBER
 CREATED_DATE				   NOT NULL DATE
 CREATED_BY				   NOT NULL VARCHAR2(80 CHAR)
 MODIFIED_DATE					    DATE
 GUI_MODIFIED_DATE				    DATE
 MODIFIED_BY					    VARCHAR2(80 CHAR)
 OBSOLETE_FLAG				   NOT NULL NUMBER
 STATUS 					    VARCHAR2(20 CHAR)
 SDR_CLASS_OID					    NUMBER
 RELEASE					    VARCHAR2(20 CHAR)
 SEVERITY					    VARCHAR2(10 CHAR)
 PRIORITY					    VARCHAR2(10 CHAR)
 CATEGORY_OID					    NUMBER
 REPORTED					    VARCHAR2(40 CHAR)
 ASSIGNED					    VARCHAR2(40 CHAR)
 DESCRIPTION					    VARCHAR2(4000 CHAR)
 TEST_PLAN					    VARCHAR2(4000 CHAR)
 SEE_ALSO					    VARCHAR2(1024 CHAR)
 NUM_DAYS_TO_DO 				    NUMBER
 RELEASE_FOUND					    VARCHAR2(20 CHAR)
 RESOLVED_DATE					    DATE
 FOUND_CLIENT_OID				    NUMBER
 CONFIRMER					    VARCHAR2(80 CHAR)
 DATE_FOUND					    DATE
 RESOLUTION					    VARCHAR2(20 CHAR)
 CONFIRMED_DATE 				    DATE
 TARG_CLIENT_OID				    NUMBER
 CLIENT_DESCRIPTION				    VARCHAR2(4000 CHAR)
 PROB_RESOLUTION				    VARCHAR2(4000 CHAR)
 IS_PUBLIC					    NUMBER
 STEPS_TO_REPRODUCE				    VARCHAR2(4000 CHAR)
 SCRIPT_NEEDED					    NUMBER
 SCRIPT_NAME					    VARCHAR2(80 CHAR)
 CONFIDENCE					    VARCHAR2(20 CHAR)
 CODE_REVIEWER					    VARCHAR2(40 CHAR)
 REL_PROMISED					    VARCHAR2(80 CHAR)
 SPEC_PARAGRAPH 				    VARCHAR2(80 CHAR)
 SPEC_URL					    VARCHAR2(256 CHAR)
 WORK_AROUND					    VARCHAR2(1024 CHAR)
 TITLE						    VARCHAR2(256 CHAR)
 DEV_HOURS					    NUMBER(38,6)
 QA_HOURS					    NUMBER(38,6)
 MISC_HOURS					    NUMBER(38,6)
 DEVELOPER					    VARCHAR2(40 CHAR)
 HAS_TEMPLATE					    NUMBER
 REVIEWED_DATE				   NOT NULL DATE
 QA_TOTAL_HOURS 				    NUMBER(38,6)
 VERTICAL					    VARCHAR2(80 CHAR)
 UNIT_TESTS					    VARCHAR2(1024 CHAR)
 QA_TEST_CASE					    VARCHAR2(1024 CHAR)
 SDR_NUM					    NUMBER
 APPROVAL_STATUS				    VARCHAR2(80 CHAR)
 APPROVAL_EXPIRY_DATE				    DATE
 PF_JOB_NUM					    VARCHAR2(80 CHAR)
 DUP_TO_SDR_OID 				    NUMBER
 REOPEN_COUNT					    NUMBER
 SDR_SUB_CLASS_OID			   NOT NULL NUMBER
 FOUND_PHASE					    VARCHAR2(20 CHAR)
 DOC_REQUIRED				   NOT NULL NUMBER
 TARGET_FIX_DATE				    DATE
 ESCALATION_FLAG				    NUMBER
 ESCALATION_STATUS				    VARCHAR2(10 CHAR)
 ESCALATION_RESOLUTION				    VARCHAR2(20 CHAR)
 ESCALATION_OPEN_DATE				    DATE
 ESCALATION_CLOSE_DATE				    DATE
 ESCALATION_RESOLUTION_TIME		   NOT NULL NUMBER
 QA_TEST_PHASE					    VARCHAR2(80 CHAR)
 ESCALATION_HOURS				    NUMBER(38,6)
 STORY_POINTS					    NUMBER
 RALLY_USER_STORY_ID				    VARCHAR2(80 CHAR)
 RALLY_DEFECT_ID				    VARCHAR2(80 CHAR)
 RALLY_USER_STORY_NAME				    VARCHAR2(256 CHAR)

SQL> desc sdr_class;
 Name					   Null?    Type
 ----------------------------------------- -------- ----------------------------
 OID					   NOT NULL NUMBER
 CREATED_DATE				   NOT NULL DATE
 CREATED_BY				   NOT NULL VARCHAR2(80 CHAR)
 MODIFIED_DATE					    DATE
 GUI_MODIFIED_DATE				    DATE
 MODIFIED_BY					    VARCHAR2(80 CHAR)
 OBSOLETE_FLAG				   NOT NULL NUMBER
 NAME					   NOT NULL VARCHAR2(15 CHAR)
 DISPLAY				   NOT NULL VARCHAR2(15 CHAR)
 DEFINITION					    VARCHAR2(80 CHAR)
 ORDER_BY					    NUMBER
 NEEDS_CLIENT					    NUMBER


SQL> desc sdr_sub_class;
 Name					   Null?    Type
 ----------------------------------------- -------- ----------------------------
 OID					   NOT NULL NUMBER
 CREATED_DATE				   NOT NULL DATE
 CREATED_BY				   NOT NULL VARCHAR2(80 CHAR)
 MODIFIED_DATE					    DATE
 GUI_MODIFIED_DATE				    DATE
 MODIFIED_BY					    VARCHAR2(80 CHAR)
 OBSOLETE_FLAG				   NOT NULL NUMBER
 NAME					   NOT NULL VARCHAR2(15 CHAR)

SQL> desc category;
 Name					   Null?    Type
 ----------------------------------------- -------- ----------------------------
 OID					   NOT NULL NUMBER
 CREATED_DATE				   NOT NULL DATE
 CREATED_BY				   NOT NULL VARCHAR2(80 CHAR)
 MODIFIED_DATE					    DATE
 GUI_MODIFIED_DATE				    DATE
 MODIFIED_BY					    VARCHAR2(80 CHAR)
 OBSOLETE_FLAG				   NOT NULL NUMBER
 DISPLAY				   NOT NULL VARCHAR2(30 CHAR)
 DEFINITION					    VARCHAR2(80 CHAR)
 SDR_USER_OID					    NUMBER
 CONFIRMER_OID					    NUMBER
 NEEDS_CLIENT					    NUMBER
 IS_PUBLIC					    NUMBER
 VERTICAL					    VARCHAR2(80 CHAR)
 IS_CALL					    NUMBER
 IS_SDR 					    NUMBER
 PRODUCT_AREA_OID				    NUMBER
 IS_CLOUD					    NUMBER

SQL> desc approval_group;
 Name					   Null?    Type
 ----------------------------------------- -------- ----------------------------
 OID					   NOT NULL NUMBER
 CREATED_DATE				   NOT NULL DATE
 CREATED_BY				   NOT NULL VARCHAR2(80 CHAR)
 MODIFIED_DATE					    DATE
 GUI_MODIFIED_DATE				    DATE
 MODIFIED_BY					    VARCHAR2(80 CHAR)
 OBSOLETE_FLAG				   NOT NULL NUMBER
 NAME						    VARCHAR2(80 CHAR)
 DESCRIPTION					    VARCHAR2(255 CHAR)
 VERTICAL					    VARCHAR2(80 CHAR)
 IS_ACTIVE					    NUMBER
 ESCALATION_EMAIL				    VARCHAR2(80 CHAR)


*/



/**
 * Utility class used to query the database for Bug data. 
 *
 * @version      1.0
 * @author       Shawn Stafford (sstafford@modeln.com)
 */
public class CMnBugTable extends CMnOracleTable {

    /** Name of the generic column representing an autogenerated primary key */
    protected static final String OID = "oid";


    /** Name of the database table containing the bug information */
    protected static final String TABLE_SDR = "sdr";

    /** Name of the column representing the SDR ID */
    protected static final String COLUMN_BUG_ID = "oid";

    /** Name of the column representing the SDR category ID */
    protected static final String COLUMN_BUG_CATEGORY = "category_oid";


    /** Name of the column representing the SDR status */
    protected static final String COLUMN_STATUS = "status";

    /** Name of the column representing the vertical used to categorize the bug */
    protected static final String COLUMN_BUG_VERTICAL = "vertical";

    /** Name of the column representing the SDR number (not guaranteed to be unique) */
    protected static final String COLUMN_BUG_NUMBER = "sdr_num";

    /** Name of the column representing the bug title */
    protected static final String COLUMN_TITLE = "title";

    /** Name of the column representing the bug description */
    protected static final String COLUMN_DESCRIPTION = "description";

    /** Name of the column representing the date when the bug was resolved */
    protected static final String COLUMN_RESOLVED_DATE = "resolved_date";

    /** Name of the column representing the product release version number */
    protected static final String COLUMN_RELEASE = "release";

    /** Name of the column representing the bug type */
    protected static final String COLUMN_CLASS_ID = "sdr_class_oid";

    /** Name of the column representing the bug sub-type */
    protected static final String COLUMN_SUBCLASS_ID = "sdr_sub_class_oid";




    /** Name of the database table containing the SDR class information */
    protected static final String TABLE_SDR_CLASS = "sdr_class";

    /** Name of the column representing the bug type */
    protected static final String COLUMN_CLASS = "display";



    /** Name of the database table containing the SDR class information */
    protected static final String TABLE_SDR_SUBCLASS = "sdr_sub_class";

    /** Name of the column representing the bug sub-type */
    protected static final String COLUMN_SUBCLASS = "name";



    /** Name of the database table containing the SDR category (area) information */
    protected static final String TABLE_CATEGORY = "category";

    /** Name of the column representing the category name */
    protected static final String COLUMN_CATEGORY_NAME = "name";

    /** Name of the column representing the category product area foreign key */
    protected static final String COLUMN_CATEGORY_PRODUCT_AREA = "product_area_oid";




    /** Name of the database table containing the SDR approval group information */
    protected static final String TABLE_APPROVAL_GROUP = "approval_group";

    /** Name of the column representing the category name */
    protected static final String COLUMN_APPROVAL_GROUP_NAME = "name";





    /** Name of an alias that will be used to identify the SDR OID */
    private static final String ALIAS_BUG_ID = "bugid";



    /** Singleton instance of the table class */
    private static CMnBugTable instance;

    /**
     * Return the singleton instance of the class.
     */
    public static CMnBugTable getInstance() {
        if (instance == null) {
            instance = new CMnBugTable();

            // Enable debbuging to a file
            String logfile = "/var/tmp/SDTrackerBugTable.txt";
            try {
                instance.setDebugOutput(new PrintStream(logfile));
                instance.debugEnable(true);
            } catch (FileNotFoundException nfex) {
                System.out.println("Unable to enable debugging.  Failed to open log file: " + logfile);
            }

        }
        return instance;
    }

    /**
     * Return a list of columns that should be included in the 
     * SQL query result set.  The named list of columns is necessary
     * because Oracle does not provide the means to select columns
     * from the result set when multiple tables are joined with 
     * non-unique column names.  For example, if SDR.OID and SDR_CLASS.OID
     * are returned in the same result set using "SELECT * FROM ..."
     * there is no way to get either value from the result set.
     * For this reason, an alias must be used for each non-unique 
     * column so that it can be obtained from the result set.
     *
     * @return List of columns to include in the result set
     */
    private static String getColumnNames() {
        StringBuffer names = new StringBuffer();

        names.append(TABLE_SDR + "." + COLUMN_BUG_ID + " AS " + ALIAS_BUG_ID);
        names.append(", " + TABLE_SDR + "." + COLUMN_BUG_NUMBER);
        names.append(", " + TABLE_SDR + "." + COLUMN_BUG_VERTICAL);
        names.append(", " + TABLE_SDR + "." + COLUMN_STATUS);
        names.append(", " + TABLE_SDR + "." + COLUMN_TITLE);
        names.append(", " + TABLE_SDR + "." + COLUMN_DESCRIPTION);
        names.append(", " + TABLE_SDR + "." + COLUMN_RELEASE);
        names.append(", " + TABLE_SDR + "." + COLUMN_RESOLVED_DATE);

        names.append(", " + TABLE_SDR_CLASS + "." + COLUMN_CLASS);

        names.append(", " + TABLE_SDR_SUBCLASS + "." + COLUMN_SUBCLASS);

        return names.toString();
    }


    /**
     * Retrieve information about the bug. 
     *
     * @param   conn      Database connection
     * @param   bugId     SDTracker bug ID
     *
     * @return  Bug information
     */
    public synchronized CMnBug getBug(Connection conn, String bugId)
        throws SQLException
    {
        CMnBug bug = null; 

        StringBuffer sql = new StringBuffer();
        sql.append("SELECT " + getColumnNames() + 
                   " FROM " + TABLE_SDR + ", " + TABLE_SDR_CLASS + ", " + TABLE_SDR_SUBCLASS +
                   " WHERE " + TABLE_SDR + "." + COLUMN_CLASS_ID + " = " + TABLE_SDR_CLASS + "." + OID + 
                   " AND " + TABLE_SDR + "." + COLUMN_SUBCLASS_ID + " = " + TABLE_SDR_SUBCLASS + "." + OID +
                   " AND " + TABLE_SDR + "." + COLUMN_BUG_ID + " = '" + bugId + "'");

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            // Get a reference to the fix table
            CMnFixTable fixTable = CMnFixTable.getInstance();
            getInstance().debugWrite("Attempting to execute: " + sql.toString());
            rs = executeQuery(st, "getBug", sql.toString());
            while ((rs != null) && rs.next()) {
                bug = parseBugData(rs);
                if (bug.getId() != null) {
                    bug.setCheckIns(fixTable.getCheckIns(conn, bug.getId().toString()));
                } 
            }
        } catch (SQLException ex) {
            System.err.println("Failed to obtain the bug data: " + sql.toString());
            ex.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return bug;
    }


    /**
     * Retrieve information about each of the bugs in the list.
     *
     * @param   conn      Database connection
     * @param   buglist   List of bugs
     *
     * @return  List of bugs
     */
    public synchronized Vector<CMnBug> getBugs(Connection conn, String[] buglist)
        throws SQLException
    {
        Vector<CMnBug> bugs = new Vector<CMnBug>();

        if (buglist.length > 0) {
            StringBuffer list = new StringBuffer();
            for (int idx = 0; idx < buglist.length; idx++) {
                if (idx > 0) list.append(",");
                list.append(buglist[idx]);
            }

            // Select bug details from the rows returned by the inner select
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT " + getColumnNames() + 
                       " FROM " + TABLE_SDR + ", " + TABLE_SDR_CLASS + ", " + TABLE_SDR_SUBCLASS +
                       " WHERE " + TABLE_SDR + "." + COLUMN_CLASS_ID + " = " + TABLE_SDR_CLASS + "." + OID +
                       " AND " + TABLE_SDR + "." + COLUMN_SUBCLASS_ID + " = " + TABLE_SDR_SUBCLASS + "." + OID +
                       " AND " + TABLE_SDR + "." + COLUMN_BUG_ID + " IN (" + list + ")");
            Statement st = conn.createStatement();
            ResultSet rs = null;
            try {
                // Get a reference to the fix table
                CMnFixTable fixTable = CMnFixTable.getInstance();
                getInstance().debugWrite("Attempting to execute: " + sql.toString());
                rs = executeQuery(st, "getBugs", sql.toString());
                while ((rs != null) && rs.next()) {
                    CMnBug bug = parseBugData(rs);
                    if (bug.getId() != null) {
                        bug.setCheckIns(fixTable.getCheckIns(conn, bug.getId().toString()));
                    }
                    bugs.add(bug);
                }
            } catch (SQLException ex) {
                System.err.println("Failed to obtain the list of fixed bugs: " + sql.toString());
                ex.printStackTrace();
            } finally {
                if (rs != null) rs.close();
                if (st != null) st.close();
            }
        }

        return bugs;
    }



    /**
     * Retrieve a list of all bugs fixed for the specified release
     * after the specified date.
     *
     * @param   conn      Database connection
     * @param   release   Release number (i.e. 5.6.1)
     * @param   start     Starting date
     *
     * @return  Bug information
     */
    public synchronized Vector<CMnBug> getFixedBugs(Connection conn, String release, Date start)
        throws SQLException
    {
        Vector<CMnBug> bugs = new Vector<CMnBug>();

        StringBuffer sql = new StringBuffer();

        // Define the select query for finding bugs fixed on this release
        String inner = "SELECT DISTINCT " + TABLE_SDR + "." + COLUMN_BUG_ID +
                       " FROM " + TABLE_SDR + ", " + CMnFixTable.TABLE_NAME + 
                       " WHERE " + TABLE_SDR + "." + COLUMN_BUG_ID + " = " + CMnFixTable.TABLE_NAME + "." + CMnFixTable.COLUMN_BUG_ID +
                       "  AND " + TABLE_SDR + "." + COLUMN_STATUS + " IN ('closed', 'verified')" +
                       "  AND " + TABLE_SDR + "." + COLUMN_RESOLVED_DATE + " > " + format(start) +
                       "  AND " + TABLE_SDR + "." + COLUMN_RELEASE + " LIKE '" + release + "%'"; 

        // Select bug details from the rows returned by the inner select
        sql.append("SELECT " + getColumnNames() + 
                   " FROM " + TABLE_SDR + ", " + TABLE_SDR_CLASS + ", " + TABLE_SDR_SUBCLASS +
                   " WHERE " + TABLE_SDR + "." + COLUMN_CLASS_ID + " = " + TABLE_SDR_CLASS + "." + OID +
                   " AND " + TABLE_SDR + "." + COLUMN_SUBCLASS_ID + " = " + TABLE_SDR_SUBCLASS + "." + OID +
                   " AND " + TABLE_SDR + "." + COLUMN_BUG_ID + " IN (" + inner + ")");

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            // Get a reference to the fix table
            CMnFixTable fixTable = CMnFixTable.getInstance();
            getInstance().debugWrite("Attempting to execute: " + sql.toString());
            rs = executeQuery(st, "getFixedBugs", sql.toString());
            while ((rs != null) && rs.next()) {
                CMnBug bug = parseBugData(rs);
                if (bug.getId() != null) {
                    bug.setCheckIns(fixTable.getCheckIns(conn, bug.getId().toString()));
                }
                bugs.add(bug);
            }
        } catch (SQLException ex) {
            System.err.println("Failed to obtain the list of fixed bugs: " + sql.toString());
            ex.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return bugs;
    }


    /**
     * Retrieve the approval group associated with the SDR. 
     *
     * @param   conn      Database connection
     * @param   bugId     SDTracker bug OID
     *
     * @return  Approval group name 
     */
    public synchronized String getApprovalGroup(Connection conn, String bugId)
        throws SQLException
    {
        String name = null; 

        StringBuffer sql = new StringBuffer();
        sql.append("SELECT " + TABLE_APPROVAL_GROUP + "." + COLUMN_APPROVAL_GROUP_NAME);
        sql.append(" FROM " + TABLE_SDR + ", " + TABLE_CATEGORY + ", " + TABLE_APPROVAL_GROUP);
        sql.append(" WHERE " + TABLE_SDR + "." + COLUMN_BUG_ID + " = " + bugId);
        sql.append(" AND " + TABLE_CATEGORY + "." + OID + " = " + TABLE_SDR + "." + COLUMN_BUG_CATEGORY);
        sql.append(" AND " + TABLE_APPROVAL_GROUP + "." + OID + " = " + TABLE_CATEGORY + "." + COLUMN_CATEGORY_PRODUCT_AREA); 

        Statement st = conn.createStatement();
        ResultSet rs = null;
        try {
            rs = executeQuery(st, "getApprovalGroup", sql.toString());
            while ((rs != null) && rs.next()) {
                getInstance().debugWrite("getApprovalGroup returned " + name + " for SDR " + bugId); 
                name = rs.getString(COLUMN_APPROVAL_GROUP_NAME);
            }
        } catch (SQLException ex) {
            getInstance().debugWrite("Failed to obtain the approval group: " + sql.toString());
            getInstance().debugWrite("SQLException: " + ex.toString());
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return name;
    }


    /**
     * Display information about the result set meta data.
     *
     * @param  rs  Result Set
     */
    private static void debugResultSet(ResultSet rs)
        throws SQLException
    {
        ResultSetMetaData metadata = rs.getMetaData();
        int colCount = metadata.getColumnCount();
        for (int idx = 1; idx <= colCount; idx++) {
            getInstance().debugWrite("debugResultSet: (" + idx + ") column name = " + metadata.getColumnName(idx));
        }
    }



    /**
     * Parse the result set to obtain bug information.
     *
     * @param  rs   Result set containing bug data
     *
     * @return Bug information
     */
    public static CMnBug parseBugData(ResultSet rs)
        throws SQLException
    {
        CMnBug bug = new CMnBug();

        int id = rs.getInt(ALIAS_BUG_ID);
        bug.setId(id);

        int num = rs.getInt(COLUMN_BUG_NUMBER);
        bug.setNumber(num);

        String vertical = rs.getString(COLUMN_BUG_VERTICAL);
        bug.setVertical(vertical);

        String status = rs.getString(COLUMN_STATUS);
        bug.setStatus(status);

        String title = rs.getString(COLUMN_TITLE);
        bug.setTitle(title);

        String desc = rs.getString(COLUMN_DESCRIPTION);
        bug.setDescription(desc);

        String release = rs.getString(COLUMN_RELEASE);
        bug.setRelease(release);

        String type = rs.getString(COLUMN_CLASS);
        bug.setType(type);

        String subtype = rs.getString(COLUMN_SUBCLASS);
        bug.setSubType(subtype);

        Date resolveDate = rs.getTimestamp(COLUMN_RESOLVED_DATE);
        bug.setResolveDate(resolveDate);

        return bug;
    }

}

