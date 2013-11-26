package com.modeln.build.servicepatch;

import com.modeln.build.common.data.account.CMnAccount;
import com.modeln.build.common.data.account.CMnEnvironment;
import com.modeln.build.common.data.account.CMnUser;
import com.modeln.build.common.data.database.CMnQueryData;
import com.modeln.build.common.data.product.CMnBuildData;
import com.modeln.build.common.data.product.CMnPatch;
import com.modeln.build.common.database.CMnCustomerTable;
import com.modeln.build.common.file.CMnMapTokenResolver;
import com.modeln.build.common.file.CMnTokenReplacingReader;
import com.modeln.build.common.tool.CMnCmdLineTool;
import com.modeln.build.common.tool.CMnPatchUtil;
import com.modeln.build.sdtracker.CMnBug;
import com.modeln.build.sdtracker.CMnBugTable;
import com.modeln.build.sourcecontrol.CMnCheckIn;
import com.modeln.build.sourcecontrol.CMnFile;
import com.modeln.build.sourcecontrol.CMnGitServer;
import com.modeln.build.sourcecontrol.CMnGitCheckIn;
import com.modeln.build.sourcecontrol.CMnPerforceServer;
import com.modeln.build.sourcecontrol.CMnPerforceCheckIn;
import com.modeln.build.sourcecontrol.IMnServer;
import com.modeln.build.util.StringUtility;
import com.modeln.testfw.reporting.CMnBuildTable;
import com.modeln.testfw.reporting.CMnDbBuildData;
import com.modeln.testfw.reporting.CMnDbHostData;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.StackOverflowError;
import java.lang.String;
import java.lang.StringBuffer;
import java.lang.System;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

/**
 * Command-line tool for creating a service patch.
 */
public class CMnPatchTool extends CMnCmdLineTool {

    public static final String CONFIG_BUILD_DB_URL        = "build.db.url";
    public static final String CONFIG_BUILD_DB_USERNAME   = "build.db.username";
    public static final String CONFIG_BUILD_DB_PASSWORD   = "build.db.password";
    public static final String CONFIG_BUG_DB_URL          = "bug.db.url";
    public static final String CONFIG_BUG_DB_USERNAME     = "bug.db.username";
    public static final String CONFIG_BUG_DB_PASSWORD     = "bug.db.password";
    public static final String CONFIG_GIT_REMOTE_URL      = "git.remote.url";
    public static final String CONFIG_GIT_REMOTE_USERNAME = "git.remote.username";
    public static final String CONFIG_GIT_REMOTE_PASSWORD = "git.remote.password";

    /** Command line option for specifying the bug fix jobs */
    public static final Option ARG_BUGFIXES = new Option("b", "bugs", true, "Comma-delimited list of bugs");

    /** Command line option for specifying the bug fix changelists */
    public static final Option ARG_CHANGELIST = new Option("c", "checkins", true, "Comma-delimited list of check-ins");

    /** Command line option for specifying the destination root location */
    public static final Option ARG_DESTINATION = new Option("d", "destination", true, "Local source code destination directory");

    /** Command line option to execute a native git client instead of using the Java API */
    public static final Option ARG_NATIVE = new Option("e", "exec", false, "Execute a native git client");

    /** Command line option to ignore the isSafe check on commit sequence order */
    public static final Option ARG_FORCE = new Option("f", "force", false, "Force the tool to skip the isSafe check"); 

    /** Command line option to allow tool to interact with the user */
    public static final Option ARG_INTERACTIVE = new Option("i", "interact", false, "Prompt for user input when necessary");

    /** Command line option to specify the customer environment name */
    public static final Option ARG_ENVIRONMENT = new Option("j", "env", true, "Customer environment");

    /** Command line option to use only local git resources to perform the patch */
    public static final Option ARG_LOCALONLY = new Option("l", "local", false, "Use only local resources");

    /** Command line option to produce only the build scripts and configuration files */
    public static final Option ARG_CONFIGONLY = new Option("n", "config", false, "Only generate the build scripts and config files");

    /** Command line option to specify the source control password */
    public static final Option ARG_PASSWORD = new Option("p", "password", false, "Source control password");

    /** Command line option to specify the source control password */
    public static final Option ARG_REMOTE = new Option("r", "remote", false, "Source control server");

    /** Command line option for specifying the property and template directory */
    public static final Option ARG_CONFIGDIR = new Option("t", "confdir", true, "Directory containing the tool properties and templates");

    /** Command line option to specify the source control username */
    public static final Option ARG_USERNAME = new Option("u", "username", false, "Source control username");

    /** Command line option to display the version information */
    public static final Option ARG_VERSION = new Option("v", "version", false, "Display software version");


    /** Define the index order for the customer command-line argument */
    public static final int ARG_CUSTOMER = 0;

    /** Define the index order for the build command-line argument */
    public static final int ARG_BUILD = 1;

    /** Define the index order for the patch command-line argument */
    public static final int ARG_PATCH = 2;

    /** Create a logger for capturing service patch tool output */
    protected static Logger logger = Logger.getLogger(CMnPatchTool.class.getName());

    /** List of required command-line arguments */
    private static String[] REQUIRED_ARGS = {"customer", "build", "patch"};

    /** URI for accessing the git server */
    private static String gitUri = "ssh://pdgit.modeln.com/home/git/repositories/";

    /** Determine whether the tool should prompt the user for input when necessary */
    private static boolean interactive = false;

    /** Determine whether the tool should perform the isSafe check on file merges */
    private static boolean checksafe = true;

    /**
     * Construct the command-line tool
     */
    public CMnPatchTool () {
        StringBuffer sb = new StringBuffer();
        sb.append("java -jar servicepatch.jar");
        for (int idx = 0; idx < REQUIRED_ARGS.length; idx++) {
            sb.append(" <" + REQUIRED_ARGS[idx] + ">");
        }
        setCommandName(sb.toString());
        setOptions();
    }

    /**
     * Load tool configuration properties from a property file.
     *
     * @param   file    Property file
     * @return  Configuration values
     */
    public Properties loadConfig(File file) throws IOException {
        Properties config = new Properties();


        // Default Build Database properties
        config.put(CONFIG_BUILD_DB_URL,      "jdbc:mysql://pdbuilds.modeln.com:3306/mn_build");
        config.put(CONFIG_BUILD_DB_USERNAME, "mndist");
        config.put(CONFIG_BUILD_DB_PASSWORD, "mndist");

        // Default SDTracker properties
        config.put(CONFIG_BUG_DB_URL,        "jdbc:oracle:thin:@pdbugs.modeln.com:1521:XE");
        config.put(CONFIG_BUG_DB_USERNAME,   "sdtrack");
        config.put(CONFIG_BUG_DB_PASSWORD,   "sdtrack");

        // Default git properties
        config.put(CONFIG_GIT_REMOTE_URL,      "ssh://pdgit.modeln.com/home/git/repositories/");
        config.put(CONFIG_GIT_REMOTE_USERNAME, "");
        config.put(CONFIG_GIT_REMOTE_PASSWORD, "");

        // Load properties from file
        FileReader reader = new FileReader(file);
        config.load(reader);

        return config;
    }

    /**
     * Display a separator line in the output.  If a text string is provided
     * it will be displayed in-line with the header characters.  If the text
     * is null, only the header charactes will be displayed.
     *
     * @param  text     Header text
     */
    private static void printHeader(String text) {
        printSeparator("=", text); 
    }


    /**
     * Display a separator line in the output.  If a text string is provided
     * it will be displayed in-line with the header characters.  If the text
     * is null, only the header charactes will be displayed.
     *
     * @param  ch       Separator character
     * @param  text     Header text
     */
    private static void printSeparator(String ch, String text) {
        StringBuffer header = new StringBuffer();

        int width = 60;
        if ((text != null) && (text.length() < width)) {
            int padding = (width - text.length()) / 2;

            // Append the separator characters before the text
            for (int idx = 0; idx < padding - 1; idx++) {
                header.append(ch);
            }

            // Add the header text
            header.append(" " + text + " ");

            // Add an extra space for the case where the text is an odd length
            if (padding + padding + text.length() < width) {
                header.append(" ");
            }

            // Append the separator characters after the text
            for (int idx = 0; idx < padding - 1; idx++) {
                header.append(ch);
            }
        } else if (text != null) {
            header.append(text);
        } else {
            for (int idx = 0; idx < width; idx++) {
                header.append(ch);
            }
        }


        logger.info(header.toString());
    }

    /**
     * Display the list of check-ins.
     *
     * @param  cl   Check-in list
     */
    private static void printCheckIns(List<CMnCheckIn> cl) {
        if (cl != null) {
            Iterator clIter = cl.iterator();
            while (clIter.hasNext()) {
                CMnCheckIn current = (CMnCheckIn) clIter.next();
                logger.info("Check-in: " + current.getId() + " " + current.getDate());
            }
        }
    }

    /**
     * Display the list of related check-ins.
     *
     * @param  cl    List of related checkins
     */
    private static void printRelatedCheckIns(HashMap<CMnCheckIn, Float> cl) {
        if ((cl != null) && (cl.size() > 0)) {
            Set<CMnCheckIn> keys = cl.keySet();
            Iterator keyIter = keys.iterator();
            while (keyIter.hasNext()) {
                CMnCheckIn checkin = (CMnCheckIn) keyIter.next();
                Float value = (Float) cl.get(checkin);
                logger.info("   Related Check-in: " + checkin.getFormattedId() + 
                            "   (Score: " + String.format("%.2f", value) + ")");
            }
        }

    }

    /**
     * Display an analysis of the file similarities between two commits.
     *
     * @param  c1     Check-in
     * @param  c2     Check-in
     */
    private static void printFileDiff(CMnCheckIn c1, CMnCheckIn c2) {
        // Get a list of all files in the two commits
        HashSet<String> files = new HashSet<String>();
        if ((c1 != null) && (c1.getFiles() != null)) {
            List<CMnFile> list = c1.getFiles();
            Iterator iter = list.iterator();
            while (iter.hasNext()) {
                CMnFile file = (CMnFile) iter.next();
                files.add(file.getFilename());
            }
        }
        if ((c2 != null) && (c2.getFiles() != null)) {
            List<CMnFile> list = c2.getFiles();
            Iterator iter = list.iterator();
            while (iter.hasNext()) {
                CMnFile file = (CMnFile) iter.next();
                files.add(file.getFilename());
            }
        } 

        // Display column headers
        if (files.size() > 0) {
            String header = StringUtility.getFixedString(c1.getFormattedId(), 10, ' ') + " " +
                            StringUtility.getFixedString(c2.getFormattedId(), 10, ' ') + " " +
                            "Score";
            logger.info(header);
            logger.info("---------- ---------- -----");
        }

        // Display information about each file
        Iterator fileIter = files.iterator();
        while (fileIter.hasNext()) {
            String filename = (String) fileIter.next();
            CMnFile file1 = c1.getFile(filename);
            CMnFile file2 = c2.getFile(filename);
            boolean equalOp = ((file1.getOp() != null) && 
                               (file2.getOp() != null) && 
                               (file1.getOp() == file2.getOp()));
            float diffEquality = 0.0f;
            if ((file1.getDiff() != null) && (file2.getDiff() != null)) {
                diffEquality = StringUtility.getEquality(file1.getDiff(), file2.getDiff()); 
            } 

            String op1 = "          "; 
            String op2 = "          "; 
            String score = "     ";
            if (!equalOp) {
                op1 = StringUtility.getFixedString(file1.getOp().toString(), 10, ' ');
                op2 = StringUtility.getFixedString(file2.getOp().toString(), 10, ' ');
            }
            if (diffEquality < 1.0f) {
                score = " " + String.format("%.2f", diffEquality);
            }
            logger.info(op1 + " " + op2 + " " + score + " " + filename);
        }

    }


    /**
     * Display the full information about a check-in.
     * 
     * @param  checkin    Check-in information
     */
    private static void printDetail(CMnCheckIn checkin) {
        logger.info("  ID: " + checkin.getId());
        logger.info("Desc: " + checkin.getDescription(50));

        printRelatedCheckIns(checkin.getRelatedCheckins());
    } 

    /**
     * Translate the JDK information from the build data into a vendor name
     * that can be used by the build scripts.
     *
     * @param    build    Build information
     * @return   sun or ibm
     */
    private String getJdkVendor(CMnBuildData build) {
        String shortVendor = null;

        // Select the vendor
        String vendor = build.getJdkVendor();
        if (vendor != null) {
            if (vendor.startsWith("IBM")) {
                shortVendor = "ibm";
            } else {
                shortVendor = "sun";
            }
        } else {
            logger.severe("Unable to determine JDK vendor.");
        }

        return shortVendor;
    }

    /**
     * Translate the JDK information from the build data into a two digit
     * version number that can be used by the build scripts.
     *
     * @param    build    Build information
     * @return   Two digit version number (1.7, 1.6, 1.5, etc)
     */
    private String getJdkVersion(CMnBuildData build) {
        String shortVersion = null;

        // We only care about the first two digits of the JDK
        StringBuffer sb = new StringBuffer();
        String version = build.getJdkVersion();
        if (version != null) {
            StringTokenizer st = new StringTokenizer(version, ".");
            if (st.countTokens() > 1) {
                sb.append(st.nextToken());
                sb.append(".");
                sb.append(st.nextToken());
            } else {
                sb.append(version);
            }
        } else {
            logger.severe("Unable to determine JDK version.");
            sb.append(version);
        }

        return sb.toString();
    }

    /**
     * Translate the JDK information from the build data into a path that is
     * available for use by the current service patch build.
     *
     * @param    build    Build information
     * @return   Path to the JDK
     */
    private String getJdkPath(CMnBuildData build) {
        StringBuffer sb = new StringBuffer();

        sb.append("/opt/java/");
        sb.append(build.getJdkVendor());
        sb.append("-jdk-");
        sb.append(build.getJdkVersion());

        return sb.toString();
    }


    /**
     * Map the build data from the build database to a build data object.
     *
     * @param   data    Build database data
     * @return  Build data object
     */
    private static CMnBuildData convertBuildData(CMnDbBuildData data) {
        CMnBuildData build = new CMnBuildData();
        build.setId(data.getId());
        build.setStartTime(data.getStartTime());
        build.setEndTime(data.getEndTime());
        build.setBuildVersion(data.getBuildVersion());
        build.setDownloadUri(data.getDownloadUri());
        build.setVersionStatus(data.getReleaseId());
        build.setVersionControlType(data.getVersionControlType().toString().toLowerCase());
        build.setVersionControlId(data.getVersionControlId());
        build.setVersionControlRoot(data.getVersionControlRoot());
        if (data.getHostData() != null) {
            build.setJdkVersion(data.getHostData().getJdkVersion());
            build.setJdkVendor(data.getHostData().getJdkVendor());
        }
        return build;
    }

    /**
     * Map the build data object to a database object.
     *
     * @param   data   Build data
     * @return  Build database object
     */
    private static CMnDbBuildData convertBuildData(CMnBuildData data) {
        CMnDbBuildData build = new CMnDbBuildData();
        build.setId(data.getId());
        build.setStartTime(data.getStartTime());
        build.setEndTime(data.getEndTime());
        build.setBuildVersion(data.getBuildVersion());
        build.setDownloadUri(data.getDownloadUri());
        build.setReleaseId(data.getVersionStatus());
        build.setVersionControlType(data.getVersionControlType());
        build.setVersionControlId(data.getVersionControlId());
        build.setVersionControlRoot(data.getVersionControlRoot());

        CMnDbHostData host = new CMnDbHostData();
        host.setJdkVersion(data.getJdkVersion());
        host.setJdkVendor(data.getJdkVendor());
        build.setHostData(host);

        return build;
    }


    /**
     * Load the customer information using command-line arguments.
     *
     * @param  cl   Command-line arguments
     * @param  db   Database connection information
     */
    private CMnAccount getCustomer(CommandLine cl, CMnQueryData db) {
        String custName = ((String)cl.getArgList().get(ARG_CUSTOMER)).toUpperCase();

        String dbDriver = db.getJdbcDriver();
        String dbUrl = db.getJdbcUrl();
        String dbUsername = db.getUsername();
        String dbPassword = db.getPassword();

        // Populate the customer information
        CMnAccount acct = null;
        try {
            // The newInstance() call is a work around for some broken Java implementations
            Class.forName(dbDriver).newInstance();
            Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
            if (conn != null) {
                CMnCustomerTable customerTable = CMnCustomerTable.getInstance();
                acct = customerTable.getCustomerByName(conn, custName);
            }

        } catch (SQLException sqlex) {
            logger.severe("Failed to obtain account information from the build database: " + dbUrl);
            sqlex.printStackTrace();
        } catch (ClassNotFoundException nfex) {
            logger.severe("Failed to load the JDBC drivers for the build database: " + dbDriver);
            nfex.printStackTrace();
        } catch (InstantiationException iex) {
            logger.severe("Failed to instantiate the JDBC driver for the build database: " + dbDriver);
            iex.printStackTrace();
        } catch (IllegalAccessException aex) {
            logger.severe("Unable to access the JDBC driver for the build database: " + dbDriver);
            aex.printStackTrace();
        }

        return acct;
    }

    /**
     * Load the customer environment information using command-line arguments.
     *
     * @param  cl   Command-line arguments
     * @param  db   Database connection information
     */
    private CMnEnvironment getEnvironment(CommandLine cl, CMnQueryData db) {
        String envId = cl.getOptionValue(ARG_ENVIRONMENT.getOpt());

        String dbDriver = db.getJdbcDriver();
        String dbUrl = db.getJdbcUrl();
        String dbUsername = db.getUsername();
        String dbPassword = db.getPassword();

        // Populate the customer environment information
        CMnEnvironment env = null;
        try {
            // The newInstance() call is a work around for some broken Java implementations
            Class.forName(dbDriver).newInstance();
            Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
            if (conn != null) {
                CMnCustomerTable customerTable = CMnCustomerTable.getInstance();
                env = customerTable.getEnvironment(conn, envId);
            }

        } catch (SQLException sqlex) {
            logger.severe("Failed to obtain customer environment information from the build database: " + dbUrl);
            sqlex.printStackTrace();
        } catch (ClassNotFoundException nfex) {
            logger.severe("Failed to load the JDBC drivers for the build database: " + dbDriver);
            nfex.printStackTrace();
        } catch (InstantiationException iex) {
            logger.severe("Failed to instantiate the JDBC driver for the build database: " + dbDriver);
            iex.printStackTrace();
        } catch (IllegalAccessException aex) {
            logger.severe("Unable to access the JDBC driver for the build database: " + dbDriver);
            aex.printStackTrace();
        }

        return env;
    }


    /**
     * Load the build information using command-line arguments.
     *
     * @param  cl   Command-line arguments
     * @param  db   Database connection information
     */
    private CMnBuildData getBuild(CommandLine cl, CMnQueryData db) {
        String buildVer = ((String)cl.getArgList().get(ARG_BUILD)).toUpperCase();

        String dbDriver = db.getJdbcDriver();
        String dbUrl = db.getJdbcUrl();
        String dbUsername = db.getUsername();
        String dbPassword = db.getPassword();

        // Populate the build information
        CMnBuildData build = null;
        try {
            // The newInstance() call is a work around for some broken Java implementations
            Class.forName(dbDriver).newInstance();
            Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
            if (conn != null) {
                CMnBuildTable buildTable = CMnBuildTable.getInstance();
                Vector<CMnDbBuildData> buildList = buildTable.getBuildsByVersion(conn, buildVer);
                if ((buildList != null) && (buildList.size() == 1)) {
                    CMnDbBuildData dbData = (CMnDbBuildData) buildList.firstElement();
                    build = convertBuildData(dbData);
                } else if (buildList.size() > 1) {
                    logger.severe("Multiple builds found matching version string: " + buildVer);
                } else {
                    logger.severe("Unable to locate build data matching version string: " + buildVer);
                }
            }

        } catch (SQLException sqlex) {
            logger.severe("Failed to obtain build information from the build database: " + dbUrl);
            sqlex.printStackTrace();
        } catch (ClassNotFoundException nfex) {
            logger.severe("Failed to load the JDBC drivers for the build database: " + dbDriver);
            nfex.printStackTrace();
        } catch (InstantiationException iex) {
            logger.severe("Failed to instantiate the JDBC driver for the build database: " + dbDriver);
            iex.printStackTrace();
        } catch (IllegalAccessException aex) {
            logger.severe("Unable to access the JDBC driver for the build database: " + dbDriver);
            aex.printStackTrace();
        }

        //CMnBuildData build = new CMnBuildData();
        //build.setBuildVersion(buildVer);

        return build;
    }

    /**
     * Parse the list of bugs specified by the user on the command-line.
     *
     * @param  cl       Command-line information
     * @return List of bug IDs
     */
    private List<Integer> getBugs(CommandLine cl) {
        ArrayList<Integer> ids = new ArrayList<Integer>();

        // Parse the list of bug IDs to ensure they are valid numbers
        String opt = ARG_BUGFIXES.getOpt();
        if (cl.hasOption(opt) && (cl.getOptionValue(opt) != null)) {
            String bugStr = cl.getOptionValue(opt);
            logger.info("Parsing bug list: " + bugStr);
            // Iterate through each bug in the list
            StringTokenizer st = new StringTokenizer( bugStr, "," );
            while (st.hasMoreTokens()) {
                String numberVal = st.nextToken();
                try {
                    // Check if the bug ID is a valid number
                    Integer id = new Integer(numberVal);

                    // Add this bug ID to List
                    if (!ids.contains(id)) {
                        ids.add(id);
                    }
                } catch (NumberFormatException e) {
                    logger.severe("NumberFormatException parsing the bug-fix changelist: " + numberVal);
                    e.printStackTrace();
                } catch (Exception e) {
                    logger.severe("Other Exeptions parsing the bug-fix changelist: " + numberVal);
                    e.printStackTrace();
                }
            }
        } else if (cl.hasOption(opt)) {
            logger.severe("Bug option specified without a list of bugs.");
        } else {
            logger.warning("No bugs specified.");
        }

        return ids;
    }

    /**
     * Query the source control system for the list of bugs and their associated
     * commit information.
     *
     * @param  scm      Source control system
     * @param  cl       Command-line information
     * @param  branch   Source control branch
     * @param  id       Check-in ID
     * @return List of bugs
     */
    private List<CMnBug> getBugs(IMnServer scm, CommandLine cl, String branch, String id) {
        Vector<CMnBug> bugs = null;

        // Parse the command-line arguments
        List<Integer> ids = getBugs(cl);

        try {
            // Query the source control system for the list of bugs
            HashMap<String, List<String>> bugMap = scm.getBugMap(branch, id);

            // Create a list of bugs out of the source control data
            Iterator bugIter = bugMap.keySet().iterator();
            while (bugIter.hasNext()) {
                String bugId = (String) bugIter.next();

                // Create a new bug from the source control results
                CMnBug bug = new CMnBug();
                bug.setId(Integer.valueOf(bugId));

                // Convert the check-in IDs to check-in objects
                List<String> checkinList = (List<String>) bugMap.get(bugId);
                Iterator checkinIter = checkinList.iterator();
                while (checkinIter.hasNext()) {
                    String checkinId = (String) checkinIter.next();
                    CMnCheckIn checkin = new CMnCheckIn();
                    checkin.setId(checkinId);
                    bug.addCheckIn(checkin);
                }
            }
        } catch (Exception ex) {
            logger.severe("Failed to obtain bug list from source control: branch=" + branch + ", id=" + id);
            ex.printStackTrace();
        }

        return bugs;
    }

    /**
     * Parse the list of bugs specified by the user on the command-line.
     *
     * @param  scm    Source control system
     * @param  cl     Command-line information
     * @param  db     Database connection information
     * @return List of bugs
     */
    private List<CMnBug> getBugs(IMnServer scm, CommandLine cl, CMnQueryData db) {
        Vector<CMnBug> bugs = null;

        String dbDriver = db.getJdbcDriver();
        String dbUrl = db.getJdbcUrl();
        String dbUsername = db.getUsername();
        String dbPassword = db.getPassword();

        // Parse the command-line arguments
        List<Integer> ids = getBugs(cl);

        // Create a list of bugs out of the ID list
        String[] bugList = new String[ids.size()];
        for (int idx = 0; idx < ids.size(); idx++) {
            bugList[idx] = ids.get(idx).toString();
        }
        try {
            // The newInstance() call is a work around for some broken Java implementations
            Class.forName(dbDriver).newInstance();
            Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
            if (conn != null) {
                CMnBugTable bugTable = CMnBugTable.getInstance();
                bugs = bugTable.getBugs(conn, bugList);
            }

        } catch (SQLException sqlex) {
            logger.severe("Failed to obtain bug information from the bug database: " + dbUrl);
            sqlex.printStackTrace();
        } catch (ClassNotFoundException nfex) {
            logger.severe("Failed to load the JDBC drivers for the bug database: " + dbDriver);
            nfex.printStackTrace();
        } catch (InstantiationException iex) {
            logger.severe("Failed to instantiate the JDBC driver for the bug database: " + dbDriver);
            iex.printStackTrace();
        } catch (IllegalAccessException aex) {
            logger.severe("Unable to access the JDBC driver for the bug database: " + dbDriver);
            aex.printStackTrace();
        }

        return bugs;
    }

    /**
     * Iterate through the list of check-ins to determine if the 
     * check-in already exists in the list.
     *
     * @param  list   List of check-ins
     * @param  item   Check-in to look for
     * @return TRUE if the check-in is in the list, FALSE otherwise
     */
    private boolean contains(List<CMnCheckIn> list, CMnCheckIn item) {
        boolean result = false;

        if ((list != null) && (item != null) && (item.getId() != null)) {
            CMnCheckIn current = null;
            Iterator iter = list.iterator();
            while (iter.hasNext()) {
                current = (CMnCheckIn) iter.next();
                if ((current.getId() != null) && (current.getId().equalsIgnoreCase(item.getId()))) {
                    return true;
                }
            }
        }

        return result;
    }

    /**
     * Query the source control system for the specified check-in.
     * If the check-in cannot be found or is not a supported type,
     * null will be returned.
     *
     * @param   scm   Source control system
     * @param   checkin    Target check-in
     * @return  Check-in information
     */
    private CMnCheckIn getCheckIn(IMnServer scm, CMnCheckIn checkin) {
        CMnCheckIn result = null;

        if ((checkin != null) && (checkin.getId() != null)) {
            try {
                if ((scm instanceof CMnGitServer) && (checkin instanceof CMnGitCheckIn)) {
                    result = scm.getCheckIn(checkin.getId());
                } else if ((scm instanceof CMnPerforceServer) && (checkin instanceof CMnPerforceCheckIn)) {
                    result = scm.getCheckIn(checkin.getId());
                }
            } catch (Exception ex) {
                logger.severe("Unable to locate check-in: " + checkin.getId());
            }
        }

        return result;
    }

    /**
     * Determine if the commit is the correct type for the source
     * control system.
     *
     * @param  scm    Source control system
     * @param  commit Commit
     * @return TRUE if the commit is the correct type
     */
    private boolean isCorrectType(IMnServer scm, CMnCheckIn commit) {
        if ((scm instanceof CMnGitServer) && (commit instanceof CMnGitCheckIn)) {
            return true;
        } else if ((scm instanceof CMnPerforceServer) && (commit instanceof CMnPerforceCheckIn)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Return a list of check-ins associated with the list of bugs.
     *
     * @param  scm    Source control system
     * @param  bugs   List of bugs
     * @return List of check-ins
     */
    private List<CMnCheckIn> getCheckInList(IMnServer scm, List<CMnBug> bugs) {
        Vector<CMnCheckIn> checkins = new Vector<CMnCheckIn>();

        if ((bugs != null) && (bugs.size() > 0)) {
            CMnBug currentBug = null;
            Iterator bugIter = bugs.iterator();

            // Iterate through each bug specified by the user
            while (bugIter.hasNext()) {
                currentBug = (CMnBug) bugIter.next();
                List<CMnCheckIn> scmCheckIns = currentBug.getCheckIns();
                if (scmCheckIns != null) {
                    CMnCheckIn currentCheckIn = null;
                    CMnCheckIn scmCheckIn = null;

                    // Iterate through the list of check-ins associated with the bug
                    Iterator checkInIter = scmCheckIns.iterator();
                    while (checkInIter.hasNext()) {
                        currentCheckIn = (CMnCheckIn) checkInIter.next();

                        // Discard checkins that are not part of this source control system
                        boolean isCorrectType = isCorrectType(scm, currentCheckIn);

                        // Make sure duplicate check-ins are not added to the list
                        boolean duplicate = contains(checkins, currentCheckIn);
                        if (isCorrectType && !duplicate) {
                            try {
                                // Query the source control system for the check-in information
                                scmCheckIn = getCheckIn(scm, currentCheckIn);
                                if (scmCheckIn == null) {
                                    logger.severe("Failed to find check-in: " + currentCheckIn.getId());
                                    scmCheckIn = currentCheckIn;
                                    scmCheckIn.setCurrentState(CMnCheckIn.State.INVALID);
                                    checkins.add(scmCheckIn);
                                } else {
                                    scmCheckIn.setCurrentState(CMnCheckIn.State.PENDING);
                                    checkins.add(scmCheckIn);
                                }
                            } catch (Exception ex) {
                                logger.severe("Failed to parse check-in: " + currentCheckIn.getId());
                                ex.printStackTrace();
                            }
                        }
                    }
                }
            }
        }

        return checkins;
    }

    /**
     * Set the list of available command line options.
     */
    protected void setOptions() {
        ARG_HELP.setRequired(false);
        ARG_HELP.setArgName("help");
        cmdOptions.addOption(ARG_HELP);

        ARG_CHANGELIST.setRequired(false);
        ARG_CHANGELIST.setArgs(1);
        ARG_CHANGELIST.setArgName("check-ins");
        cmdOptions.addOption(ARG_CHANGELIST);

        ARG_BUGFIXES.setRequired(false);
        ARG_BUGFIXES.setArgs(1);
        ARG_BUGFIXES.setArgName("bugs");
        cmdOptions.addOption(ARG_BUGFIXES);

        ARG_REMOTE.setRequired(false);
        ARG_REMOTE.setArgs(1);
        ARG_REMOTE.setArgName("remote");
        cmdOptions.addOption(ARG_REMOTE);

        ARG_USERNAME.setRequired(false);
        ARG_USERNAME.setArgs(1);
        ARG_USERNAME.setArgName("username");
        cmdOptions.addOption(ARG_USERNAME);

        ARG_PASSWORD.setRequired(false);
        ARG_PASSWORD.setArgs(1);
        ARG_PASSWORD.setArgName("password");
        cmdOptions.addOption(ARG_PASSWORD);

        ARG_DESTINATION.setRequired(false);
        ARG_DESTINATION.setArgs(1);
        ARG_DESTINATION.setArgName("dir");
        cmdOptions.addOption(ARG_DESTINATION);

        ARG_CONFIGDIR.setRequired(false);
        ARG_CONFIGDIR.setArgs(1);
        ARG_CONFIGDIR.setArgName("dir");
        cmdOptions.addOption(ARG_CONFIGDIR);

        ARG_CONFIGONLY.setRequired(false);
        ARG_HELP.setArgName("config");
        cmdOptions.addOption(ARG_CONFIGONLY);

        ARG_INTERACTIVE.setRequired(false);
        ARG_INTERACTIVE.setArgName("interactive");
        cmdOptions.addOption(ARG_INTERACTIVE);

        ARG_NATIVE.setRequired(false);
        ARG_NATIVE.setArgName("exec");
        cmdOptions.addOption(ARG_NATIVE);

        ARG_LOCALONLY.setRequired(false);
        ARG_LOCALONLY.setArgName("local");
        cmdOptions.addOption(ARG_LOCALONLY);

        ARG_VERSION.setRequired(false);
        ARG_VERSION.setArgName("version");
        cmdOptions.addOption(ARG_VERSION);

        ARG_FORCE.setRequired(false);
        ARG_FORCE.setArgName("force");
        cmdOptions.addOption(ARG_FORCE);

    }

    /**
     * Display the software version information to the user.
     */
    public void displayVersion() {
        String manifest = "/META-INF/MANIFEST.MF";
        try {
            InputStream is = getClass().getResourceAsStream(manifest);
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            StringBuffer sb = new StringBuffer();
            System.out.println("");
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
            br.close();
            isr.close();
            is.close();
        } catch (IOException ioex) {
            System.out.println("Failed to load version information from " + manifest);
            ioex.printStackTrace();
        }
    } 

    /**
     * Validate input format.
     * Functions displays help (commandline syntax) and returns false for the following situations:
     * 1. There's a --help flag
     * 2. There's no argument
     * 3. The format of the inputs is not correct
     *
     * @param   cl      the commandline inputs
     * @return          true if the options and arguments are in a correct format, false if otherwise
     */
    private boolean hasValidArgs (CommandLine cl) {
        if (cl == null) {
            return false;
        } else {
            // Verify there's enough arguments
            List extraArgs = cl.getArgList();
            if (extraArgs.size() != REQUIRED_ARGS.length) {
                Iterator iter = extraArgs.iterator();
                int idx = 0;
                logger.info("Command Arguments: ");
                String argName = null;
                String argValue = null;
                while (iter.hasNext()) {
                    argValue = (String) iter.next();
                    if (idx < REQUIRED_ARGS.length) {
                        argName = REQUIRED_ARGS[idx];
                    } else {
                        argName = "?";
                    }
                    logger.info("Argument:  " + argName + " = " + argValue);
                    idx++;
                }
                logger.severe("Command requires " + REQUIRED_ARGS.length + " arguments to build a service patch:");
                return false;
            }
        }
        return true;
    }

    /**
     * Determine if it is safe to merge the specified fix into the service
     * patch branch.  A fix is safe to integrate if any of the following
     * are true:
     * <ul>
     *   <li>It occurs later than any of the previously integrated fixes</li>
     *   <li>It occurs earlier than previously integrated fixes, but 
     *   does not modify any files that have been modified in previous fixes</li>
     * </ul>
     *
     * @param  scm          Source control system
     * @param  checkin      Check-in to be evaluated
     * @param  start        Starting point on the destination branch
     * @param  end          Ending point on the destination branch
     * @return TRUE if the check-in can be safely merged, FALSE otherwise
     */
    public boolean isSafe(IMnServer scm, CMnCheckIn checkin, CMnCheckIn start, CMnCheckIn end) {
        boolean safe = false;

        // Ensure that the check-in provided contains a list of modified files
        if (checkin.getFileCount() == 0) {
            logger.severe("No files found in commit: " + checkin.getId());
            return safe;
        }

        // Determine if the check-in is safe to integrate
        if ((start.getDate() == null) && (end.getDate() == null)) {
            logger.severe("Invalid branch start and end date.");
        } else if (start.getDate() == null) {
            logger.severe("Invalid branch start date.");
        } else if (end.getDate() == null) {
            logger.severe("Invalid branch end date.");
        } else if (start.getDate().after(end.getDate())) {
            logger.severe("Branch start date occurs after branch end date.");
        } else {
            if (checkin.getDate().after(end.getDate())) {
                // Safe if it occurs later than the last branch check-in
                safe = true;
            } else {
                // Get a list of all check-ins that occur on the branch after 
                // the specified check-in
                try {
                    List<CMnCheckIn> list = scm.getCheckInList(null, start.getId(), end.getId());
                    if ((list != null) && (list.size() > 0)) {
                        // Determine if any of the check-ins touch common files
                        Iterator listIter = list.iterator();
                        CMnCheckIn current = null;
                        while (listIter.hasNext()) {
                            current = (CMnCheckIn) listIter.next();
                            if (current != null) {
                                List<String> common = current.getCommonFiles(checkin);
                                if (common != null) {
                                    logger.info("New commit " + checkin.getFormattedId() + " (" + checkin.getDate() + ") and existing commit " + current.getFormattedId() + " (" + current.getDate() + ")"); 
                                    // Check-in touches files that have already been modified
                                    logger.info("Found the following files in common:");
                                    for (String file : common) {
                                        logger.info("   " + file); 
                                    }
                                    return false;
                                }
                            }
                        }

                        // Safe if none of the later check-ins touch these files
                        safe = true;
                    } else {
                        logger.severe("Unable to get a list of files modified by subsequent check-ins.");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    logger.severe("Unable to get the list of branch check-ins.");
                }
            }
        }

        return safe;
    }

    /**
     * Integrate the list of fixes from the specified branch to the destination branch.
     *
     * @param  scm          Source control system
     * @param  checkins     List of check-ins
     * @param  srcBranch    Source of the check-ins
     * @param  destBranch   Destination branch
     * @param  sp           Service patch name
     * @param  start        First commit on the service patch branch
     * @param  end          Last commit in the service patch branch
     */
    public void integrate(IMnServer scm, List<CMnCheckIn> checkins, String srcBranch, String destBranch, CMnCheckIn start, CMnCheckIn end) {
        // Integrate each check-in into the service patch branch
        printHeader("Integrate Fixes");
        int fixCount = 0;
        String fixCountText = null;
        Iterator checkinIter = checkins.iterator();
        while (checkinIter.hasNext()) {
            CMnCheckIn currentCheckIn = (CMnCheckIn) checkinIter.next();
            String id = currentCheckIn.getId();
            fixCount++;
            fixCountText = fixCount + " of " + checkins.size();

            CMnCheckIn.State currentState = currentCheckIn.getCurrentState();

            // Determine how to proceed with unknown commits
            boolean useUnknown = true;
            if (interactive && (currentState == CMnCheckIn.State.UNKNOWN)) {
                // Desplay information to the user about the uncertain check-in
                logger.info("Unable to determine merge status of check-in: ");
                printDetail(currentCheckIn);

                // Display information about related check-ins
                HashMap<CMnCheckIn, Float> related = currentCheckIn.getRelatedCheckins();
                if (related != null) { 
                    Set<CMnCheckIn> keys = related.keySet();
                    Iterator keyIter = keys.iterator();
                    int relatedCount = 0;
                    while (keyIter.hasNext()) {
                        relatedCount++;
                        CMnCheckIn rc = (CMnCheckIn) keyIter.next();
                        logger.info("File comparison for related commit " + 
                            relatedCount + " of " + related.size() + ": " + rc.getId());
                        printFileDiff(currentCheckIn, rc); 
                    }
                }

                // Ask the user if 
                boolean allow = doContinue("Would you like to include the UNKNOWN check-in?");
                if (allow) {
                    currentState = CMnCheckIn.State.PENDING;
                }
            }

            // Only merge the pending fixes
            if (currentState == CMnCheckIn.State.PENDING) {
                // Determine if the current check-in is safe to merge
                boolean safe = true;
                if (checksafe) {
                    safe = isSafe(scm, currentCheckIn, start, end);
                }

                // Perform the isSafe check on the current fix
                if (!safe) {
                    logger.severe("Fix " + fixCountText + " is not safe to merge: " + id);
                    if (interactive) {
                        boolean abort = doContinue("Would you like to abort the service patch process?");
                        if (abort) {
                            logger.info("Aborting service patch process as requested.");
                            System.exit(1);
                        }
                    } else {
                        logger.severe("Terminating serivce patch process due to potentially unsafe merge sequence.");
                        logger.severe("Use the interactive mode of this tool to override this behavior.");
                        System.exit(1);
                    }
                }

                // Merge the current check-in into the destination branch
                try {
                    printSeparator("-", fixCountText);
                    logger.info("Merging check-in " + id + " from " + srcBranch + " to " + destBranch);
                    IMnServer.MergeResult mergeResult = scm.merge(srcBranch, destBranch, id);
                    if (mergeResult == IMnServer.MergeResult.FAILURE) {
                        logger.severe("Failed to merge check-in: " + id);
                        System.exit(1);
                    } else if (mergeResult == IMnServer.MergeResult.CONFLICT) {
                        logger.severe("Merge contains conflicts that require manual resolution: " + id);
                        System.exit(1);
                    } else if (mergeResult == IMnServer.MergeResult.SUCCESS) {
                        //logger.info("Successfully merged check-in: " + id);
                    } else {
                        logger.severe("Unknown merge result: " + mergeResult + ", SHA: " + id);
                        System.exit(1);
                    }
                    printSeparator("-", null);
                } catch (Exception ex) {
                    logger.severe("Failed to integrate check-in: " + id);
                    ex.printStackTrace();
                    System.exit(1);
                }
            } else {
                logger.info("Skipping fix " + fixCount + " of " + checkins.size() + ": " + id + " (" + currentState + ")");
            }
        }

    }



    /**
     * Create the service patch branch.  If the branch does not already exist,
     * a new branch will be created and the fixes will be integrated on the
     * newly created branch.  If one or more branches for this patch
     * already exist, select the most recently created branch and use that
     * as the basis for the current service patch.
     *
     * @param  scm          Source control system
     * @param  bugs         List of bugs to be integrated
     * @param  patch        Service patch information
     */
    public void branchAndIntegrate(IMnServer scm, List<CMnBug> bugs, CMnPatch patch) {
        //String scmType = patch.getBuild().getVersionControlType();
        String scmBranch = patch.getBuild().getVersionControlRoot();
        String scmId = patch.getBuild().getVersionControlId();

        List<CMnCheckIn> checkins = getCheckInList(scm, bugs);

        // Sort the list of check-ins in chronological order
        scm.sort(checkins);
        //printCheckIns(checkins);

        printHeader("Create Service Patch Branch");
        String spBranch = CMnPatchUtil.getBranchName(patch);
        int strIdx = spBranch.indexOf("_" + patch.getName());
        String spBranchPrefix = spBranch.substring(0, strIdx); 

        // Fetch the latest copy of the git notes
        if (scm instanceof CMnGitServer) {
            String notesref = CMnGitServer.getMergeNotesNamespace(spBranch);
            try {
                ((CMnGitServer) scm).getMergeNotes(notesref);
            } catch (Exception ex) {
                logger.severe("Failed to fetch notes: " + notesref);
            }
        }

        // Use an existing branch or create a new one
        if (scm.branchExists(spBranch)) {
            logger.info("Using existing service patch branch: " + spBranch);

            //TODO See if we can determine how to branch from an existing branch
            /*
            try {
                printHeader("EXISTING BRANCH");
                List<CMnCheckIn> existingList = scm.getCheckInList(spBranch, scmId);
                printCheckIns(existingList);
                scm.merge(spBranch, spBranch + "_new", checkins, existingList);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            System.exit(1);
            */

        } else {
            // Parse the SP name to identify a previous branch
            String prevBranch = null;
            try {
                Integer spNumber = CMnPatchUtil.getPatchNumber(patch.getName());
                if (spNumber > 1) {
                    Integer prevNumber = new Integer(spNumber - 1);
                    prevBranch = spBranchPrefix + CMnPatchUtil.getPatchName(prevNumber);
                }
            } catch (Exception nex) {
                logger.severe("Failed to determine the previous service patch number for: " + patch.getName());
            }

            // Create a new service patch branch
            try {
                logger.info("Attempting to create service patch branch " + spBranch + " off of " + scmBranch + " at check-in " + scmId);
                scm.createBranch(scmBranch, spBranch, scmId);
            } catch (Exception ex) {
                logger.severe("Failed to create service patch branch: " + spBranch);
                ex.printStackTrace();
            }
        }



        // Determine which of the check-ins have already been applied on the service patch branch
        //printHeader("Check Pending Fixes");
        int checkinCount = checkins.size();
        for (int idx = 0; idx < checkinCount; idx++) {
            CMnCheckIn currentCheckIn = (CMnCheckIn) checkins.get(idx);
            String id = currentCheckIn.getId();
            float isMerged = IMnServer.RELATIVE_CERTAINTY_NONE;
            if ((currentCheckIn.getCurrentState() == CMnCheckIn.State.PENDING) ||
                (currentCheckIn.getCurrentState() == CMnCheckIn.State.UNKNOWN))
            {
                HashMap<CMnCheckIn, Float> matches = null;
                try {
                    matches = scm.isMerged(scmBranch, spBranch, id);
                    currentCheckIn.setRelatedCheckins(matches);
                } catch (Exception mex) {
                    logger.severe("Failed to detmine the merge status of SHA " + id);
                    mex.printStackTrace();
                }

                // Update the check-in with the merge status
                if (matches.size() == 0) {
                    currentCheckIn.setCurrentState(CMnCheckIn.State.PENDING);
                } else if (currentCheckIn.hasRelatedCheckin(IMnServer.RELATIVE_CERTAINTY_HIGH)) {
                    currentCheckIn.setCurrentState(CMnCheckIn.State.SUBMITTED);
                } else {
                    currentCheckIn.setCurrentState(CMnCheckIn.State.UNKNOWN);
                }
            }

            logger.info("Merge status " + (idx+1) + " of " + checkinCount + ": " + 
                id + " " + currentCheckIn.getCurrentState());
        }

        // Determine the first checkin on the patch branch
        CMnCheckIn firstCommit = null;
        try {
            firstCommit = scm.getCheckIn(scmId);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.severe("Unable to locate the start of the branch: " + scmId);
            System.exit(1);
        }

        // Determine the last checkin on the patch branch
        CMnCheckIn lastCommit = null;
        try {
            lastCommit = scm.getHead(spBranch);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.severe("Unable to locate HEAD of branch: " + spBranch);
            System.exit(1);
        }

        // Merge all of the pending check-ins into the service patch branch
        // Any check-ins that do not have a pending status will be skipped
        if ((firstCommit != null) && (lastCommit != null)) {
            logger.info("Merging check-ins from " + scmBranch + " to " + spBranch);
            logger.info("Identified the service patch as commits " + firstCommit.getId() + " to " + lastCommit.getId());
            integrate(scm, checkins, scmBranch, spBranch, firstCommit, lastCommit);
        } else {
            logger.severe("Unable to deterine the service patch branch boundaries.");
            System.exit(1);
        }
    }


    /**
     * Replace the list of variables within the specified file.
     *
     * @param sourceFile  File to copy from
     * @param destFile    File to copy to
     * @param variables   Name/value pairs to be replaced in the file
     */
    private void replaceVariables(File sourceFile, File destFile, Map<String, String> variables) throws FileNotFoundException, IOException {
        CMnMapTokenResolver resolver = new CMnMapTokenResolver(variables);
        FileReader fileReader = new FileReader(sourceFile);
        FileWriter fileWriter = new FileWriter(destFile);
        Reader tokenReader = new CMnTokenReplacingReader(fileReader, resolver);

        // Copy the data to the new file
        try {
            int data = tokenReader.read();
            while(data != -1) {
                fileWriter.write((char) data);
                data = tokenReader.read();
            }
        } finally {
            if (fileReader != null) {
                fileReader.close();
            }
            if (fileWriter != null) {
                fileWriter.close();
            }
        }
    }

    /**
     * Copy the build template files into a service patch location and update
     * the properties with values from the current service patch.
     *
     * @param  confdir      Directory containing the config files
     * @param  destdir      Destination directory
     * @param  variables    Variables to be replaced in the files
     * @param  build        Base product build information
     */
    private void writeBuildFiles(File confdir, File destdir, Map variables, CMnBuildData build) {
        // Use a stack to process the version number,
        // popping the end digit if no matching directory is found
        StringTokenizer vernumTokenizer = new StringTokenizer(CMnPatchUtil.getVersionNumber(build.getBuildVersion()), ".");
        Stack<String> vernumStack = new Stack<String>();
        while(vernumTokenizer.hasMoreTokens()) {
            vernumStack.push((String) vernumTokenizer.nextToken());
        }

        // Select the most appropriate template directory
        File templatedir = null;
        while(!vernumStack.empty() && (templatedir == null)) {
            StringBuffer dirname = new StringBuffer();
            Iterator stackIter = vernumStack.iterator();
            while(stackIter.hasNext()) {
                if (dirname.length() > 0) {
                    dirname.append(".");
                }
                dirname.append((String) stackIter.next());
            }

            // Check to see if the most specific directory exists
            File currentdir = new File(confdir.getAbsolutePath() + File.separator + dirname);
            if (currentdir.isDirectory()) {
                templatedir = currentdir;
            } else {
                // Remove the least significant digit and make another pass
                vernumStack.pop();
            }
        }

        // Make sure the destination directory exists
        boolean destfound = false;
        if (!destdir.exists()) {
            destfound = destdir.mkdirs();
        } else {
            destfound = true;
        }
        // Copy the template files to the output directory
        if ((templatedir != null) && destfound) {
            destdir.mkdirs();
            File[] files = templatedir.listFiles();
            for (int idx = 0; idx < files.length; idx++) {
                String filename = files[idx].getName();
                File srcfile = files[idx];
                File destfile = new File(destdir.getAbsolutePath() + File.separator + filename);
                try {
                    // Replace any variables in the file with build values
                    logger.info("Copying template file from " + srcfile + " to " + destfile);
                    replaceVariables(srcfile, destfile, variables);
                } catch (FileNotFoundException nfex) {
                    logger.severe("Unable to locate file: " + srcfile.getName());
                } catch (IOException ioex) {
                    logger.severe("Unable to copy file: " + destfile.getName());
                }

                // Make sure any script files are executable
                if (filename.endsWith(".sh")) {
                    try {
                        destfile.setExecutable(true);
                    } catch (java.lang.SecurityException sex) {
                        logger.severe("Unable to make build script executable: " + destfile.getAbsolutePath());
                    }
                }
            }
        } else {
            logger.severe("Unable to copy template files: src=" + templatedir + ", dest=" + destdir);
        }
    }

    /**
     * Copy a file from one location to another.
     *
     * @param sourceFile  File to copy from
     * @param destFile    File to copy to
     * @throws IOException if the file cannot be copied
     */
    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.exists()) {
            destFile.createNewFile();
        }
        FileInputStream fIn = null;
        FileOutputStream fOut = null;
        FileChannel source = null;
        FileChannel destination = null;
        try {
            fIn = new FileInputStream(sourceFile);
            source = fIn.getChannel();
            fOut = new FileOutputStream(destFile);
            destination = fOut.getChannel();
            long transfered = 0;
            long bytes = source.size();
            while (transfered < bytes) {
                transfered += destination.transferFrom(source, 0, source.size());
                destination.position(transfered);
            }
        } finally {
            if (source != null) {
                source.close();
            } else if (fIn != null) {
                fIn.close();
            }
            if (destination != null) {
                destination.close();
            } else if (fOut != null) {
                fOut.close();
            }
        }
    }


    /**
     * Process the command line arguments and execute the program
     * accordingly.
     */
    public static void main (String[] args) {
        CMnPatchTool cmd = new CMnPatchTool();

        // Parse the command-line arguments
        CommandLine cl = parseArgs(args);

        // Process the help flag
        if ((cl != null) && cl.hasOption(ARG_HELP.getOpt())) {
            displayHelp();
            System.exit(0);
        } else if ((cl != null) && cl.hasOption(ARG_VERSION.getOpt())) {
            cmd.displayVersion();
            System.exit(0);
        } else if ((cl == null) || !cmd.hasValidArgs(cl)) {
            displayHelp();
            System.exit(1);
        }

        // Determine whether the tool should prompt the user for input
        if ((cl != null) && cl.hasOption(ARG_INTERACTIVE.getOpt())) {
            interactive = true;
        }

        // Determine whether the tool should perform the isSafe check
        if ((cl != null) && cl.hasOption(ARG_FORCE.getOpt())) {
            checksafe = false;
        }

        // Determine whether to only generate the build config files
        boolean configOnly = false;
        if ((cl != null) && cl.hasOption(ARG_CONFIGONLY.getOpt())) {
            configOnly = true;
        }

        // Determine whether to use only local resources
        boolean localOnly = false;
        if ((cl != null) && cl.hasOption(ARG_LOCALONLY.getOpt())) {
            localOnly = true;
        }

        // Select the configuration directory where the tool will obtain properties and templates
        String configDir = null;
        if (cl.hasOption(ARG_CONFIGDIR.getOpt())) {
            configDir = cl.getOptionValue(ARG_CONFIGDIR.getOpt());
        } else {
            configDir = "/etc/servicepatch";
        }

        // Load configuration properties from a property file
        Properties config = null;
        File propFile = new File(configDir + File.separator + "props" + File.separator + "sp.properties");
        if (propFile.canRead()) {
            try {
                config = cmd.loadConfig(propFile);
            } catch (IOException ioex) {
                logger.severe("Failed to load configuration file: " + propFile.getAbsolutePath());
                ioex.printStackTrace();
                System.exit(1);
            }
        } else {
            logger.severe("Unable to load configuration values from file: " + propFile.getAbsolutePath());
            System.exit(1);
        }


        logger.info("Starting service patch tool...");

        // Get the source control user information
        String remote = null;
        if (cl.hasOption(ARG_REMOTE.getOpt())) {
            remote = cl.getOptionValue(ARG_REMOTE.getOpt());
        } else if (config.contains(CONFIG_GIT_REMOTE_URL)) {
            remote = (String) config.get(CONFIG_GIT_REMOTE_URL);
        } else {
            remote = gitUri;
        }

        String username = null;
        if (cl.hasOption(ARG_USERNAME.getOpt())) {
            username = cl.getOptionValue(ARG_USERNAME.getOpt());
        } else if (config.contains(CONFIG_GIT_REMOTE_USERNAME)) {
            username = (String) config.get(CONFIG_GIT_REMOTE_USERNAME);
        } else {
            username = System.getProperty("user.name");
        }

        String password = null;
        if (cl.hasOption(ARG_USERNAME.getOpt())) {
            password = cl.getOptionValue(ARG_PASSWORD.getOpt());
        } else if (config.contains(CONFIG_GIT_REMOTE_PASSWORD)) {
            password = (String) config.get(CONFIG_GIT_REMOTE_PASSWORD);
        }

        // Establish the source code location
        String destDir = null;
        if (cl.hasOption(ARG_DESTINATION.getOpt())) {
            destDir = cl.getOptionValue(ARG_DESTINATION.getOpt());
        } else {
            destDir = System.getProperty("user.home") + File.separator + "servicepatch";
        }

        // Get the product version
        String version = ((String)cl.getArgList().get(ARG_BUILD)).toUpperCase();

        // Get the service patch number specified by the user
        String spName = ((String)cl.getArgList().get(ARG_PATCH)).toLowerCase();

        // JDBC URL for the Build Database
        String buildDbUrl = (String) config.get(CONFIG_BUILD_DB_URL);
        String buildDbUsername = (String) config.get(CONFIG_BUILD_DB_USERNAME);
        String buildDbPassword = (String) config.get(CONFIG_BUILD_DB_PASSWORD);
        CMnQueryData buildDb = new CMnQueryData(buildDbUrl);
        logger.info("Build DB URL: " + buildDb.getName());
        buildDb.setUsername(buildDbUsername);
        buildDb.setPassword(buildDbPassword);

        // JDBC URL for the Bug Database
        String bugDbUrl = (String) config.get(CONFIG_BUG_DB_URL);
        String bugDbUsername = (String) config.get(CONFIG_BUG_DB_USERNAME);
        String bugDbPassword = (String) config.get(CONFIG_BUG_DB_PASSWORD);
        CMnQueryData bugDb = new CMnQueryData(bugDbUrl);
        logger.info("Bug DB URL: " + bugDb.getName());
        bugDb.setUsername(bugDbUsername);
        bugDb.setPassword(bugDbPassword);

        // Get information about the customer environment
        CMnEnvironment env = null;
        if (cl.hasOption(ARG_ENVIRONMENT.getOpt())) {
            env = cmd.getEnvironment(cl, buildDb);
            if (env == null) {
                logger.severe("Unable to locate customer environment by ID in the build database.");
                System.exit(1);
            }
        }

        // Get information about the customer
        CMnAccount customer = cmd.getCustomer(cl, buildDb);
        if (customer == null) {
            logger.severe("Unable to locate customer by name in the build database.");
            System.exit(1);
        }

        // Create a service patch name that identifies the customer, 
        // product version, and service patch number
        String patchName = customer.getShortName() + "_" + CMnPatchUtil.getVersionNumber(version) + "_" + spName; 

        // Obtain build information from the database 
        CMnBuildData build = cmd.getBuild(cl, buildDb);

        // Construct a service patch data object 
        CMnPatch patch = new CMnPatch();
        patch.setName(spName);
        patch.setCustomer(customer);
        patch.setEnvironment(env);

        // Get source control information about the build
        IMnServer scm = null;
        String scmType = null;
        String scmRoot = null;
        String scmId = null;
        String scmBranch = null;
        if ((build != null) && (build.getVersionControlType() != null) && (build.getVersionControlRoot() != null)) {
            scmType = build.getVersionControlType();
            scmRoot = build.getVersionControlRoot();
            scmId = build.getVersionControlId();

            // Convert the build data to an object that can be stored as patch info
            CMnDbBuildData prodBuild = convertBuildData(build);
            patch.setBuild(prodBuild);

            logger.info("Creating a " + scmType + " source control repository for " + scmRoot); 
            if (scmType.equalsIgnoreCase("git")) {
                String[] scmInfo = CMnPatchUtil.getRepositoryInfo(patch);
                scmRoot = scmInfo[1];
                scmBranch = scmInfo[2];
                if (scmBranch == null) {
                    logger.severe("Invalid git information.  Field does not contain branch information.");
                    System.exit(1);
                }

                // Construct the git repository
                String origin = remote + scmRoot;
                scm = new CMnGitServer(username, password, origin);

                // Use the repository name as the directory name unless one has been specified by the user
                if (!cl.hasOption(ARG_DESTINATION.getOpt())) {
                    destDir = destDir + File.separator + patchName;
                }

                // Once a repository has been created, the build no longer needs to reference it
                // Set the branch information in the build object
                build.setVersionControlRoot(scmBranch);
                prodBuild.setVersionControlRoot(scmBranch);
            } else if (scmType.equalsIgnoreCase("perforce")) {
                scm = new CMnPerforceServer();
            } else {
                logger.severe("Unknown source control type: " + build.getVersionControlType());
                System.exit(1);
            }
        } else if (build != null) {
            logger.severe("Build does not contain source control information.");
            System.exit(1);
        } else {
            logger.severe("Unable to retrieve information from build version.");
            System.exit(1);
        }

        // Determine whether the tool will interact with the source control system using
        // a Java API or the native command-line client
        boolean execNative = cl.hasOption(ARG_NATIVE.getOpt());
        scm.setNativeMode(execNative);
        logger.info("Execute source control commands using native executables: " + execNative); 

        // Determine if the source control commands will prompt for input
        scm.setInteractiveMode(interactive);

        // Determine what the branch name should be
        String spBranchName = CMnPatchUtil.getBranchName(patch); 

        // Generate the build script and build property file
        // Define the list of variables to be replaced within the template files
        HashMap<String, String> variables = new HashMap<String, String>();
        variables.put("sp.build.build_version", version);
        variables.put("sp.build.vernum", CMnPatchUtil.getVersionNumber(version));
        variables.put("sp.build.product", CMnPatchUtil.getProductName(version.toLowerCase()));
        variables.put("sp.build.download_uri", build.getDownloadUri());
        variables.put("sp.build.java_home", cmd.getJdkPath(build));
        variables.put("sp.build.java_version", cmd.getJdkVersion(build));
        variables.put("sp.build.java_vendor", cmd.getJdkVendor(build));
        variables.put("sp.build.version_ctrl_type", build.getVersionControlType());
        variables.put("sp.build.version_ctrl_root", spBranchName); 
        variables.put("sp.build.projdir", destDir);
        variables.put("sp.patch.scriptdir", destDir);
        variables.put("sp.patch.name", spName);
        variables.put("sp.patch.customer", customer.getShortName().toLowerCase());

        // Skip all of the source control actions in config-only mode
        if (configOnly) {
            logger.info("Running in config-only mode.  No source control actions will be performed.");
        } else {
            // Get access to the source code repository
            printHeader("Initialize");
            File root = new File(destDir);
            try {
                scm.init(root);
                scm.getSource(scmBranch);
            } catch (java.lang.Exception ex) {
                logger.severe("Failed to initialize the repository.");
                ex.printStackTrace();
                System.exit(1);
            }

            // Get a list of check-ins associated with the bugs
            printHeader("Get Bugs");
            List<CMnBug> bugs = null;
            if (localOnly) {
                bugs = cmd.getBugs(scm, cl, scmRoot, scmId);
            } else {
                bugs = cmd.getBugs(scm, cl, bugDb);
            }
            logger.info("Processing " + bugs.size() + " bugs...");

            // Create a service patch branch
            cmd.branchAndIntegrate(scm, bugs, patch);
        }

        // Generate a build script for the current build
        File templateDir = new File(configDir + File.separator + "templates");
        if (templateDir.isDirectory()) {
            cmd.writeBuildFiles(templateDir, new File(destDir), variables, build);
        } else {
            logger.severe("Build template directory does not exist: " + templateDir.getAbsolutePath());
        }


        logger.info("Service patch complete.");
    }

}
