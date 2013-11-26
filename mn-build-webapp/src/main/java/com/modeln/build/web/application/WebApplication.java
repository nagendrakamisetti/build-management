/*
 * WebApplication.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.web.application;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import java.security.GeneralSecurityException;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.NamingException;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.modeln.build.common.branding.*;
import com.modeln.build.common.data.account.*;
import com.modeln.build.common.database.*;
import com.modeln.build.common.logging.*;
import com.modeln.build.web.errors.*;
import com.modeln.build.web.data.*;
import com.modeln.build.web.database.*;
import com.modeln.build.web.errors.*;
import com.modeln.build.web.util.*;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;



/**
 * The WebApplication extends the capabilities of Java's 
 * HttpServlet class to fit the specific needs of a web-based
 * user application.  The application manages access to data
 * sources, logging, and session management.
 * 
 * @version            $Revision: 1.2 $  
 * @author             Shawn Stafford
 */
public class WebApplication extends HttpServlet {

    /** Regular expression for matching IPv4 strings */
    private static final String REGEX_IPv4 = "[0-9]{1,3}+\\.[0-9]{1,3}+\\.[0-9]{1,3}+\\.[0-9]{1,3}+";

    /** Regular expression for matching IPv6 strings */
    private static final String REGEX_IPv6 = "[0-9a-fA-F]{4}+\\.[0-9a-fA-F]{4}+\\.[0-9a-fA-F]{4}+\\.[0-9a-fA-F]{4}+\\.[0-9a-fA-F]{4}+\\.[0-9a-fA-F]{4}+\\.[0-9a-fA-F]{4}+\\.[0-9a-fA-F]{4}+";


    /** The error prefix is prepended to error string entries in the brand file. */
    private static final String ERROR_PREFIX = "ERROR_";

    /** Default error page. */
    protected String errorPage = "error.jsp";

    /** Default configuration file suffix */
    public static final String CONFIG_FILE_SUFFIX = ".cfg";

    /** Default text file suffix */
    public static final String TEXT_FILE_SUFFIX = ".txt";

    /** Default text file suffix */
    public static final String LOG_FILE_SUFFIX = ".log";

    /** Default error strings text file */
    public static final String DEFAULT_ERROR_FILE = "error" + TEXT_FILE_SUFFIX;

    /** Default error strings text file */
    public static final String DEFAULT_CONFIG_FILE = "common" + CONFIG_FILE_SUFFIX;

    /** Default log file */
    public static final String DEFAULT_LOG_FILE = "common" + LOG_FILE_SUFFIX;

    /** Database log file */
    public static final String DATABASE_LOG_FILE = "db" + LOG_FILE_SUFFIX;


    /** Name of the root directory */
    public static final String ROOT_DIR = ".";

    /** Name of the WEB-INF directory which protects non-web documents */
    public static final String WEB_INF = File.separator + "WEB-INF";

    /** Name of the directory where the JSP files and web documents are located */
    public static final String WEB_DIR = ROOT_DIR;

    /** Name of the directory where the configuration files are located */
    public static final String CONFIG_DIR = WEB_INF + File.separator + "config";

    /** Name of the directory where the string files are located */
    public static final String TEXT_DIR = WEB_INF + File.separator + "text";

    /** Name of the directory where the log files are located */
    public static final String LOG_DIR = WEB_INF + File.separator + "logs";

    /** Application settings loaded from properties files */
    protected Properties appSettings;

    /** Error settings loaded from properties files */
    protected Properties errorSettings;

    /** List of external URLs that can be looked up by name */
    protected Hashtable<String,String> externalUrls = new Hashtable<String,String>();

    /** Obtain a reference to the Java runtime */
    private static final Runtime runtime = Runtime.getRuntime();


    /** 
     * Location of the root directory where files are located. 
     * This should be specified as a relative path so that resources can
     * be located independently of where the application is running from.
     */
    protected File rootDir;

    /** Location of the directory where the JSP files are located. */
    protected File jspDir;

    /** Location of the directory where the config files are located. */
    protected File configDir;

    /** Location of the directory where the string files are located. */
    protected File textDir;

    /** Location of the directory where the log files are located. */
    protected File logDir;

    /** TRUE if the application has been successfully initialized */
    protected boolean initialized = false;

    /** Handles common logging. */
    protected static SessionLog commonLog;

    /** List of log files used by the application */
    protected Vector<String> logFiles = new Vector<String>();

    /** Commands which are loaded upon request and cached for later use. */
    protected CommandTree appCommands;

    /** Length of time in milliseconds that a user session can be idle */
    protected long sessionLength = 60 * 60 * 1000;

    /** Maximum number of invalid authentication attempts before an account is disabled */
    protected int maxAuthTries = 10;
    
    /** Default language and country for the application */
    protected Locale defaultLocale;

    /** Default user interface */
    protected String defaultGui;

    /** Default repository name */
    protected String defaultRepository;

    /** Account repository name */
    protected String accountRepository;

    /** System alert message that can be displayed to the user */
    protected String alertMessage = null;

    /** Number of commands currently being serviced */
    protected int commandCount = 0;

    /** List of trusted hosts configured for this instance */
    protected String[] trustedHosts = null;

    /** Repository manager which controls access to the database connections */
    protected RepositoryManager repositoryMgr = new RepositoryManager();

    /**
     * Initialization routine called by the host Servlet engine.
     * 
     *
     * @param config the object containing the configuration settings
     */
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        // Set the headless environment variable 
        // This is required by JFreeCharts when running on Linux
        System.setProperty("java.awt.headless", "true");

        // Initialize the directories
        ServletContext context = getServletContext();

        // Initialize the log files
        commonLog = new SessionLog(getAppName(this.getClass()));
        commonLog.logEntry(this, SecureLog.DEBUG, "Beginning initialization of application: " + this.getClass().getName());

        try {
            loadSettings();

            // Set up the account database manager
            accountRepository = appSettings.getProperty("account.repository");
            commonLog.logEntry(this, SecureLog.INFO, "Adding account repository: " + accountRepository);
            addRepository(accountRepository);

            // Set up the database manager
            defaultRepository = appSettings.getProperty("default.repository");
            commonLog.logEntry(this, SecureLog.INFO, "Adding default repository: " + defaultRepository);
            addRepository(defaultRepository);

            // Set the locale attributes
            String language = appSettings.getProperty("language");
            String country  = appSettings.getProperty("country");
            if ((language != null) && (country != null)) {
                defaultLocale = new Locale(language, country);
            } else {
                defaultLocale = new Locale("en", "US");
            }

            // Set the default user interface
            defaultGui = appSettings.getProperty("gui");
            if (defaultGui == null) {
                defaultGui = "default";
            }

            // Set the log attributes
            setLogLevel(appSettings.getProperty("log-level"));
            setLogDir(appSettings.getProperty("log-directory"));
            repositoryMgr.logDbInfo();

            // Create the command caching tree
            String cmdPackage = appSettings.getProperty("command-package");
            if (cmdPackage == null) {
                cmdPackage = this.getClass().getPackage().getName();
            }
            appCommands = new CommandTree(cmdPackage);

            // Set the session timeout length
            String timeout = appSettings.getProperty("session-timeout");
            if ((timeout != null) && (timeout.length() > 0)) {
                try {
                    sessionLength = Long.parseLong(timeout) * 60 * 1000;
                    commonLog.logEntry(this, SecureLog.INFO, 
                        "Setting session timeout length: " + timeout + " minutes");
                } catch (NumberFormatException nfe) {
                    commonLog.logEntry(this, SecureLog.ERROR, 
                        "Invalid session timeout length: " + timeout);
                }
            }

            // Load the list of trusted hosts
            String hosts = appSettings.getProperty("trusted-hosts");
            if ((hosts != null) && (hosts.length() > 0)) {
                StringTokenizer st = new StringTokenizer(hosts);
                trustedHosts = new String[st.countTokens()];
                int idx = 0;
                while (st.hasMoreTokens()) {
                    String host = st.nextToken();
                    if ((host != null) && (host.trim().length() > 0)) {
                        commonLog.logEntry(this, SecureLog.INFO, "Adding trusted host: " + host);
                        trustedHosts[idx] = host.trim();
                        idx++;
                    }
                }
            }

            // Load the list of external URLs
            String urls = appSettings.getProperty("urls");
            if ((urls != null) && (urls.length() > 0)) {
                StringTokenizer st = new StringTokenizer(urls, ",");
                while (st.hasMoreTokens()) {
                    String urlname = st.nextToken();
                    if ((urlname != null) && (urlname.trim().length() > 0)) {
                        String url = appSettings.getProperty("url." + urlname);
                        if ((url != null) && (url.length() > 0)) {
                            externalUrls.put(urlname, url);
                        }
                    }
                }
            }

            initialized = true;
            commonLog.logEntry(this, SecureLog.DEBUG, "Initialization complete.");
        } catch (Exception ex) {
            commonLog.logException(this, SecureLog.ERROR, ex, "Unable to initialize the application.");
        }

    }

    /**
     * Create a database repository using the configuration settings and add
     * the repository to the repository manager.
     *
     * @param   name    Repository name
     */
    public void addRepository(String name) {
        String repositoryType = appSettings.getProperty(name + ".type");
        DataRepository repository = null;
        if (repositoryType != null) {
            commonLog.logEntry(this, SecureLog.INFO, "Adding database repository: " + name);
            if (repositoryType.equals("jdbc")) {
                // Load the configuration values
                String dbUrl = appSettings.getProperty(name + ".url");
                String dbUsername = appSettings.getProperty(name + ".username");
                String dbPassword = appSettings.getProperty(name + ".password");
                String dbDriver = appSettings.getProperty(name + ".driver");

                // Log the configuration values for debugging
                commonLog.logEntry(this, SecureLog.INFO, "Database URL: " + dbUrl);
                commonLog.logEntry(this, SecureLog.INFO, "Database Driver: " + dbDriver);
                commonLog.logEntry(this, SecureLog.INFO, "Database Username: " + dbUsername);
                commonLog.logEntry(this, SecureLog.INFO, "Database Password: " + dbPassword);

                // Construct the database repository
                DatabaseRepository db = new DatabaseRepository(dbUrl);
                db.setUsername(dbUsername);
                db.setPassword(dbPassword);
                db.setDriver(dbDriver);
                repositoryMgr.addRepository(name, db);
            } else if (repositoryType.equals("jndi")) {
                String dbJndi = appSettings.getProperty(name + ".name");
                commonLog.logEntry(this, SecureLog.INFO, "Database JNDI name: " + dbJndi);
                JndiRepository jndi = new JndiRepository(dbJndi);
                repositoryMgr.addRepository(name, jndi);
            } else {
                commonLog.logEntry(this, SecureLog.ERROR,
                    "Unknown repository type: name = " + name + ", type = " + repositoryType);
            }
        }
    }

    /**
     * Sets the logging level for the application.
     * The logging levels are defined in 
     * com.modeln.build.common.logging.SecureLog.
     *
     * @param   level   Logging level
     */
    public void setLogLevel(String level) {
        if (level != null) {
            commonLog.setLogLevel(level);
            commonLog.logEntry(this, SecureLog.INFO,
                "Setting log level to " + level);
        } else {
            // Better turn debuggin on, since things are messed up
            commonLog.setLogLevel(SecureLog.DEBUG);
            commonLog.logEntry(this, SecureLog.ERROR, 
                "Invalid log level specified.  Defaulting to DEBUG level.");
        }
    }

    /**
     * Add a log file to the list of files.
     *
     * @param  filename  Log file
     */
    private void addLogFile(String filename) {
        if (!logFiles.contains(filename)) {
            logFiles.add(filename);
        }
    }

    /**
     * Return a list of log files used by the application.
     *
     * @return  List of log files
     */
    public Vector<String> getLogFiles() {
        return logFiles;
    }

    /**
     * Return the common log that is used to write entries to the application log.
     *
     * @return  Common log
     */
    public SessionLog getLogInstance() {
        return commonLog;
    }

    /**
     * Returns the current logging level for the common log.
     *
     * @return  Current logging level
     */
    public String getLogLevel() {
        return commonLog.getLogLevelByName();
    }

    /**
     * Return the URL corresponding to the name.
     *
     * @param   name   System name
     * @return  URL string
     */
    public String getExternalUrl(String name) {
        return externalUrls.get(name);
    }

    /**
     * Return the list of external URLs.
     *
     * @return list of external URLs
     */
    public Hashtable<String,String> getExternalUrls() {
        return externalUrls;
    }

    /**
     * Sets the application logging location.
     *
     * @param   dir     Log directory
     */
    public void setLogDir(String dir) {
        if (dir == null) {
            dir = LOG_DIR;
        }

        logDir = new File(dir);
        
        // Should the directory be created if it does not exist?

        String applog = logDir.getAbsolutePath() + File.separator + DEFAULT_LOG_FILE;
        try {
            // Remove the old log but keep the default STDOUT log
            commonLog.resetLogfile();
            commonLog.addLogfile(applog);
            commonLog.logEntry(this, SecureLog.INFO, "Log file set: " + applog);
            addLogFile(applog);
        } catch (IOException ex) {
            commonLog.logException(this, SecureLog.ERROR, ex, "Unable to create common log: " + applog);
        }

        String dblog = logDir.getAbsolutePath() + File.separator + DATABASE_LOG_FILE;
        try {
            // Set the database logging to this location as well
            if (repositoryMgr != null) {
                commonLog.logEntry(this, SecureLog.INFO, "Repository log file set: " + dblog);
                PrintWriter dbout = new PrintWriter(dblog);
                repositoryMgr.setLogWriter(dbout);
                addLogFile(dblog);
            }
        } catch (IOException ex) {
            commonLog.logException(this, SecureLog.ERROR, ex, "Unable to create database log: " + dblog);
        }


    }


    /** 
     * The entry point for the application.
     *
     * @param req the HttpServletRequest object (represents the http request)
     * @param res the HttpServletResponse object (represents the response)
     *
     * @exception ServletException if a servlet exception has occurred
     * @exception IOException if an I/O exception has occured
     */
    public void service(HttpServletRequest req, HttpServletResponse res) 
        throws ServletException, IOException 
    {
        if (initialized) {
            String pathInfo = req.getPathInfo();
            while (pathInfo.startsWith("/")) {
                pathInfo = pathInfo.substring(1); 
            }

            // limit 'command' to 1st part of pathinfo, allowing later pathinfo
            // to be used to pass information, ie:
            // /app/command/Action/param -> command = "Action", pathinfo = "/param"
            int newPathPos;
            newPathPos = pathInfo.indexOf('/');
            if (newPathPos != -1) {
                String newPathInfo = pathInfo.substring(newPathPos);
                pathInfo = pathInfo.substring(0, newPathPos);
            }

            // Pass along some application data in the request
            HttpUtility.setExternalUrls(req, getExternalUrls());

            // Service the command
            Command command = selectCommand(req);
            if (command != null) {
                try {
                    debug(req);
                    long startTime = System.currentTimeMillis(); 
                    long startHeap = getUsedMemory();
                    int startCount = commandCount;
                    commandCount++;
                    CommandResult result = command.execute(this, req, res);
                    command.finalize(this, req, res, result);
                    commandCount--;
                    int endCount = commandCount;
                    long endTime = System.currentTimeMillis(); 
                    long endHeap = getUsedMemory();
                    long elapsedTime = endTime - startTime;
                    long elapsedHeap = endHeap - startHeap;
                    // command.getCommandName()
                    String queryString = "";
                    if (req.getQueryString() != null) {
                        queryString = "?" + req.getQueryString();
                    }

                    // Calculate how many other commands were executing during this request
                    String countString = "";
                    if (startCount != endCount) {
                        countString = "concurrent commands = " + startCount + " to " + endCount + ", ";
                    } else {
                        countString = "concurrent commands = " + startCount + ", ";
                    }

                    // Print a performance debug statement to the logs
                    commonLog.logEntry(this, SecureLog.INFO, "Command complete: " +
                        "elapsed time = " + elapsedTime + " ms, " +
                        "heap used = " + formatMemory(elapsedHeap) + " [" + formatMemory(endHeap) + "/" + formatMemory(runtime.totalMemory()) + "], " + 
                        countString +
                        req.getRequestURL() + queryString); 

                } catch (ApplicationException ex) {
                    commonLog.logException(this, SecureLog.ERROR, ex, "Unable to service command: " + command);
                    commonLog.logEntry(this, SecureLog.ERROR, "Original stack trace: " + ex.getOriginalStackTrace());
                    displayFatalError(req, res);
                }
            } else {
                commonLog.logEntry(this, SecureLog.ERROR, "Unable to select a command.");
                displayFatalError(req, res);
            }
        } else {
            commonLog.logEntry(this, SecureLog.ERROR, "Unable to service the request.  The application has not been successfully initialized.");
            displayFatalError(req, res);
        }

    }

    /**
     * Determine if the request originated from a trusted host.
     *
     * @param  req    HttpServletRequest
     * @return TRUE if the request is from a trusted source
     */
    public boolean isTrusted(HttpServletRequest req) {
        boolean trusted = false;

        // If we trust the end user, we're ok
        if (isTrusted(req.getRemoteAddr()) || isTrusted(req.getRemoteHost())) {
            debug("Host has a trusted remote address or remote hostname.");
            trusted = true;
        } else if (isTrusted(req.getLocalAddr()) || isTrusted(req.getLocalName())) {
            debug("Host has a trusted local address or hostname.");
            trusted = true;
        } else {
            debug("Request does not match a trusted host.");
            trusted = false;
        }

        return trusted;
    }


    /**
     * Determine if the two hostnames are equal.
     *
     * @param   host1   Hostname or IP address
     * @param   host2   Hostname or IP address
     * @return TRUE if the hostnames are equal
     */
    private boolean areHostnamesEqual(String host1, String host2) {
        boolean equal = false;

        boolean fqn_1  = false; // fully qualified hostname
        boolean IPv4_1 = false; // IPv4 IP address
        boolean IPv6_1 = false; // IPv6 IP address

        boolean fqn_2  = false; // fully qualified hostname
        boolean IPv4_2 = false; // IPv4 IP address
        boolean IPv6_2 = false; // IPv6 IP address

        if ((host1 != null) && (host2 != null)) {
            String name1 = host1.toLowerCase().trim();
            String name2 = host2.toLowerCase().trim();

            int fqn_idx1 = name1.indexOf('.');   // Location of the first '.' in the hostname
            int fqn_idx2 = name2.indexOf('.');   // Location of the first '.' in the hostname

            // Determine if the host is an IPv4 address
            IPv4_1 = name1.matches(REGEX_IPv4);
            IPv4_2 = name2.matches(REGEX_IPv4);
            if (IPv4_1 || IPv4_2) {
                return (name1.equals(name2));
            }

            // Determine if the name is an IPv6 address
            IPv6_1 = name1.matches(REGEX_IPv6);
            IPv6_2 = name2.matches(REGEX_IPv6);
            if (IPv6_1 || IPv6_2) {
                return (name1.equals(name2));
            }

            // Determine if the hostname can be compared directly
            if (fqn_idx1 == fqn_idx2) {
                return (name1.equals(name2));
            } else {
                if (fqn_idx1 > 0) {
                    // The first name is fully qualified, the second is not
                    return (name2.equals(name1.substring(0,fqn_idx1)));
                } else if (fqn_idx2 > 0) {
                    // The second name is fully qualified, the first is not
                    return (name1.equals(name2.substring(0,fqn_idx2)));
                }
            }

        }

        return equal;
    }

    /**
     * Determine if the specified host is in the list of trusted hosts.
     *
     * @param   host    Name or address of the host
     * @return  TRUE if the host is in the list
     */
    public boolean isTrusted(String host) {
        boolean trusted = false;

        boolean name = false; // hostname
        boolean fqn  = false; // fully qualified hostname
        boolean IPv4 = false; // IPv4 IP address
        boolean IPv6 = false; // IPv6 IP address

        if ((host != null) && (host.length() > 0) && (trustedHosts != null) && (trustedHosts.length > 0)) {
            // Determine if the host is an IP address or a hostname
            IPv4 = host.matches(REGEX_IPv4); 
            if (!IPv4) {
                IPv6 = host.matches(REGEX_IPv6);
            }
            if (IPv4 || IPv6) {
                name = false;
                fqn = false;
            } else {
                name = true;
                fqn = (host.indexOf('.') > 0);  // the presence of a '.' indicates a domain
            }

            // Check the host against each trusted host in the list
            for (int idx = 0; idx < trustedHosts.length; idx++) {
                debug("Comparing host to trusted host entry: " + host + " == " + trustedHosts[idx] + "?");
                if (areHostnamesEqual(host, trustedHosts[idx])) {
                    debug("Found a match in the list of trusted hosts: " + host);
                    return true;
                }
            }
        } else if ((trustedHosts == null) || (trustedHosts.length == 0)) {
            // If no hosts are defined, assume all hosts are trusted
            debug("The trusted-hosts property is not set.  All hosts will be trusted.");
            trusted = true;
        } else {
            // If the specified host is null, assume we can't trust it
            debug("The specified host cannot be trusted because the host value is null or zero length.");
            trusted = false;
        }

        return trusted; 
    }

    /**
     * Write error statements to the log file.
     *
     * @param  error  Error information
     */
    public void log(ApplicationError error) {
        if (error != null) {
            commonLog.logEntry(this, SecureLog.ERROR, "APP ERROR: " + error.toLogString());
        }
    }


    /**
     * Write debug statements to the log file.
     *
     * @param  msg  Debug message 
     */
    public void debug(String msg) {
        if (commonLog.getLogLevel() == SecureLog.DEBUG) {
            commonLog.logEntry(this, SecureLog.DEBUG, msg);
        }
    }

    /**
     * Write database connection information to the log file.
     *
     * @param  rc   Repository connection
     */
    public void debug(RepositoryConnection rc) {
        if (commonLog.getLogLevel() == SecureLog.DEBUG) {
            StringBuffer sb = new StringBuffer();
            if (rc != null) {
                sb.append(rc.toString());

                // Log database connection info
                if (rc.getConnection() != null) {
                    try {
                        sb.append(", conn=");
                        if (rc.getConnection().isClosed()) {
                            sb.append("closed");
                        } else {
                            sb.append("open");
                            DatabaseMetaData dbmeta = rc.getConnection().getMetaData();
                            if (dbmeta != null) {
                                sb.append(", url=" + dbmeta.getURL());
                            }
                        }
                    } catch (SQLException sqlex) {
                        commonLog.logEntry(this, SecureLog.DEBUG, "Unable to obtain database meta data.");
                    }
                }
                commonLog.logEntry(this, SecureLog.DEBUG, "Repository: " + sb.toString());
            }
        }
    }

    /**
     * Write request level debug information to the log.
     *
     * @param req the HttpServletRequest object (represents the http request)
     */
    public void debug(HttpServletRequest req) {
        if (commonLog.getLogLevel() == SecureLog.DEBUG) {
            // Log the attribute values
            for (Enumeration attrs = req.getAttributeNames(); attrs.hasMoreElements(); /** noop */ ) {
                String name = (String) attrs.nextElement();
                commonLog.logEntry(this, SecureLog.DEBUG, "Request attribute: " + name + "=" + req.getAttribute(name));
            }

            // Log the parameter values
            for (Enumeration params = req.getParameterNames(); params.hasMoreElements(); /** noop */ ) {
                String name = (String) params.nextElement();
                String[] values = req.getParameterValues(name);
                StringBuffer valStr = new StringBuffer();
                for (int idx = 0; idx < values.length; idx++) {
                    if (idx > 0) valStr.append(", ");
                    valStr.append(values[idx]);
                } 
                if (valStr.length() > 0) {
                    commonLog.logEntry(this, SecureLog.DEBUG, "Request parameter: " + name + "=" + valStr.toString());
                }
            }
        }
    }

    /**
     * Execute the selected command.  This method calls the execute method
     * of the command without calling the finalize method.  It is assumed
     * that the finalize method will be called within the application for 
     * the original forwarding command.
     *
     * @param req the HttpServletRequest object (represents the http request)
     * @param res the HttpServletResponse object (represents the response)
     * @param   cmdStr  The command to be executed
     * @return  Command result of the executed command
     * @throws  Application exception if the command cannot be executed
     */
    public CommandResult forwardToCommand(
            HttpServletRequest req, 
            HttpServletResponse res, 
            String cmdStr) 
        throws ApplicationException 
    {
        CommandResult result = null;

        //forward to the appropriate command
        Command command = selectCommand(req, cmdStr);
        if (command != null) {
            commonLog.logEntry(this, SecureLog.DEBUG, "Forwarding to command: " + cmdStr);
            result = command.execute(this, req, res);
        } else {
            throw new ApplicationException(ErrorMap.INVALID_COMMAND, "Unable to select a command.");
        }

        return result;
    }


    /**
     * Parses the command string to determine which command should be 
     * used to handle the request.
     *
     * @param   req         HttpServletRequest object
     * @param   cmdString   Command string
     */
    protected Command selectCommand(HttpServletRequest req, String cmdString) {
        Command command = null;
        if ((cmdString != null) && (cmdString.length() > 0)) {
            String[] searchPath = getSearchPath(req);
            StringTokenizer st = new StringTokenizer(cmdString, "/");

            // Use the last token as the command name
            StringBuffer commandName = new StringBuffer();
            while (st.hasMoreTokens()) {
                commandName.append(st.nextToken());
                if (st.hasMoreTokens()) {
                    commandName.append(".");
                }
            }

            String searchPathStr = "";
            for (int idx = 0; idx < searchPath.length; idx++) {
                searchPathStr = searchPathStr + "/" + searchPath[idx];
            }

            if (searchPath.length > 0) {
                commonLog.logEntry(this, SecureLog.DEBUG, 
                    "Attempting to load the command: command=" + commandName.toString() + ", search path=" + searchPathStr);
                command = appCommands.getCommand(commandName.toString(), searchPath);
                if (command != null) {
                    commonLog.logEntry(this, SecureLog.DEBUG, "Command returned: " + command.getClass().getName());
                } else {
                    commonLog.logEntry(this, SecureLog.DEBUG, "Command could not be located: " + commandName);
                }
            }
        }

        return command;
    }


    /**
     * Parses the request string to determine which command should be 
     * used to handle the request.
     *
     * @param   req     HttpServletRequest object
     */
    protected Command selectCommand(HttpServletRequest req) {
        Command command = null;

        String pathInfo = req.getPathInfo();
        commonLog.logEntry(this, SecureLog.DEBUG, "Path found in request: " + pathInfo);
        return selectCommand(req, pathInfo);

    }


    /**
     * Determines if the specified user has permission to access the specified resource.
     *
     * @param   req         HttpServletRequest object
     */
    public boolean hasPermission(HttpServletRequest req) {
        boolean allow = false;

        // Get the user information from the session
        HttpSession session = req.getSession();
        UserData user = SessionUtility.getLogin(session);
        if (user != null) {
            GroupData group = user.getGroupByName("admin");
            if ((group != null) && (group.getType() == GroupData.ADMIN_GROUP_TYPE)) {
                allow = true;
            }
        }

        return allow;
    }

    /**
     * Authenticates the user by connecting to the database and retrieving
     * the user data.  If the user data is successfully retrieved, the
     * data object will be populated with data and stored in the session.  
     * If the user was not found or an error occurred while attempting to 
     * authenticate, the appropriate ErrorMap error code will be returned.
     *
     * @param   session     HTTP session in which the login information will be stored
     * @param   username    User to be authenticated
     * @param   password    Unencrypted account password to be used for authentication
     *
     * @return  Error generated during authentication, null if no error occurred
     */
    public ApplicationError authenticate(HttpSession session, String username, String password) {
        ApplicationError error = null;
        RepositoryConnection rc = null;
        Connection conn = null;
        try {
            rc = getAccountConnection();
            conn = rc.getConnection();
            LoginTable lt = LoginTable.getInstance();
            UserData account = lt.getUserByName(conn, username);
            if (account != null) {
                boolean passwordOk = account.matchesPassword(password);
                boolean accountOk = account.isActive();
                int failureCount = account.getFailureCount();
                Date failedLogin = account.getFailedLogin();
                Date successfulLogin = account.getSuccessfulLogin();
                if (accountOk && passwordOk) {
                    // Reset the failed login attempts
                    failureCount = 0;
                    account.setSuccessfulLogin(new Date());

                    // Store the data in the session
                    SessionUtility.setLogin(session, account);
                    commonLog.logEntry(this, SecureLog.INFO, "User has successfully authenticated: " + username);
                } else if (!accountOk) {
                    error = getError(ErrorMap.DISABLED_ACCOUNT);

                    // Don't do any kind of logging or updates here
                    // because this could be a Denial Of Service attack

                } else if (failureCount > maxAuthTries) {
                    error = getError(ErrorMap.MAX_LOGIN_TRIES);
                    failureCount++;
                    account.setFailedLogin(new Date());

                    // Deactivate the account
                    account.setStatus(UserData.ACCOUNT_ABUSE);
                    lt.updateStatus(conn, account.getUid(), LoginTable.STATUS_ABUSE);
                    commonLog.logEntry(this, SecureLog.INFO, 
                        "User account has been disabled after " + failureCount + " login attempts: " + 
                        account.getUsername() + " (uid=" + account.getUid() + ")");
                } else {
                    error = getError(ErrorMap.INCORRECT_PASSWORD);
                    failureCount++;
                    account.setFailedLogin(new Date());
                    commonLog.logEntry(this, SecureLog.DEBUG, "Invalid login attempt by " + account.getUsername() + " (uid=" + account.getUid() + ")"); 
                }

                // Keep track of the login attempts
                account.setFailureCount(failureCount);
                if ((failedLogin == null) && (successfulLogin == null)) {
                    lt.addHistory(conn, account);
                    commonLog.logEntry(this, SecureLog.DEBUG, "Creating a new login history entry for " + account.getUsername());
                } else {
                    lt.updateHistory(conn, account);
                    commonLog.logEntry(this, SecureLog.DEBUG, "Updating existing login history entry for " + account.getUsername());
                }
            } else {
                error = getError(ErrorMap.UNKNOWN_USER);
            }
        } catch (ApplicationException aex) {
            error = getError(aex);
        } catch (SQLException sqe) {
            String errorMsg = "Unable to obtain user information from the database: " + username;
            commonLog.logException(this, SecureLog.DEBUG, sqe, errorMsg);
            error = getError(ErrorMap.DATABASE_TRANSACTION_FAILURE);
            error.attachException(sqe);
        } finally {
            repositoryMgr.release(rc); 
        }

        return error;
    }


    /**
     * Loads the application settings from properties files.
     */
    protected void loadSettings() throws ApplicationException {
        // The root directory passed to the BestPropertiesMatch constructor
        // must be a relative path if you wish the resource loader to search
        // the entire search path.  If an absolute or canonical path is specified
        // the loader will search only in that location
        // Load the error information
        String[] classPath = getSearchPath(this.getClass());
        String[] classPrefix = {};

        // Use the servlet context to obtain a real path to the relative directories
        ServletContext context = getServletContext();

        // Load remaining application settings
        rootDir = new File(ROOT_DIR);
        try {
            String dir = getInitParameter("config-directory");
            configDir = new File(dir);
        } catch(Exception ex) {
            configDir = new File(CONFIG_DIR);
//            throw new ApplicationException(ErrorMap.MISSING_CONFIGURATION_ENTRY, ex.getMessage());
        }
        try {
            String dir = getInitParameter("text-directory");
            textDir = new File(dir);
        } catch(Exception ex) {
            textDir = new File(TEXT_DIR);
//            throw new ApplicationException(ErrorMap.MISSING_CONFIGURATION_ENTRY, ex.getMessage());
        }
        try {
            String dir = getInitParameter("www-directory");
            jspDir = new File(dir);
        } catch(Exception ex) {
            jspDir = new File(WEB_DIR);
//            throw new ApplicationException(ErrorMap.MISSING_CONFIGURATION_ENTRY, ex.getMessage());
        }

        // Load the application settings from properties files
        try {
//            String dirConfig = getInitParameter("config-directory");
//            if (dirConfig != null) {
//                dirConfig = context.getRealPath(dirConfig);
//            } else {
//                dirConfig = context.getRealPath(CONFIG_DIR);
//            }
            commonLog.logEntry(this, SecureLog.DEBUG, "Loading settings from: root=" + configDir.toString() + ", file=" + DEFAULT_CONFIG_FILE);
            BestPropertiesMatch globalMatch = new BestPropertiesMatch(
                context.getRealPath(configDir.toString()), 
                DEFAULT_CONFIG_FILE, 
                classPrefix, 
                classPath);
            appSettings = globalMatch.loadProperties();
        } catch(Exception ex) {
            throw new ApplicationException(ErrorMap.MISSING_CONFIGURATION_ENTRY, ex.getMessage());
        }

        // Load the error messages from properties files
        try {
//            String errConfig = getInitParameter("text-directory");
//            if (errConfig != null) {
//                errConfig = context.getRealPath(errConfig);
//            } else {
//                errConfig = context.getRealPath(TEXT_DIR);
//            }
            commonLog.logEntry(this, SecureLog.DEBUG, "Loading settings from: root=" + textDir.toString() + ", file=" + DEFAULT_ERROR_FILE);
            BestPropertiesMatch errorMatch = new BestPropertiesMatch(
                context.getRealPath(textDir.toString()), 
                DEFAULT_ERROR_FILE, 
                classPrefix, 
                classPath);
            errorSettings = errorMatch.loadProperties();
        } catch(Exception ex) {
            throw new ApplicationException(ErrorMap.MISSING_CONFIGURATION_ENTRY, ex.getMessage());
        }
    
        
    }


    /**
     * Determines whether the current session ticket is valid.  If the ticket
     * is invalid, an application error will be returned.  If the ticket is 
     * valid, a NULL will be returned.
     *
     * @param req the HttpServletRequest object (represents the http request)
     */
    public ApplicationError validateTicket(HttpServletRequest req) {
        ApplicationError error = null;

        SessionTicket ticket = HttpUtility.getTicket(req);
        if (ticket != null) {
            //if (ticket.getElapsedTime() > sessionLength) {
            //    error = getError(ErrorMap.EXPIRED_TICKET);
            //}
        }
       
        return error;
    }

    /** 
     * When all else fails, display a fatal error message to indicate that the
     * application cannot continue.
     */
    public static void displayFatalError(HttpServletRequest req, HttpServletResponse res) 
        throws ServletException, IOException 
    {
        // Render the entire error matrix
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();
        String title = "Fatal Application Error";
        out.println("<html>");
        out.println("<head><title>" + title + "</title></head>");
        out.println("<body bgcolor=#FFFFFF>");
        out.println("  <!-- Output generated by Java method: WebApplication.displayFatalError() -->\n");
        out.println("  <center>");
        out.println("    <font size=+1><b>" + title + "</b></font><br>");
        out.println("  </center><p>");
        out.println("A fatal error has occurred, preventing the application ");
        out.println("from servicing your request.  This could be an indication ");
        out.println("that the application is not configured correctly.  Please ");
        out.println("check the logs and attempt to correct the error immediately.");
        out.println("</body>");
        out.println("</html>");
    }

    /**
     * Returns the name of the class without the package information.
     *
     * @param   c       class to be analyzed
     * @return  String  name of the class
     */
    protected static String getAppName(Class c) {
        String classname = c.getName();

        int idx = classname.lastIndexOf(".");
        if ((idx >= 0) && (classname.length() > 1)) {
            return classname.substring(idx + 1);
        } else {
            return classname;
        }

    }
    
    /** 
     * Returns the file system path to the application by transforming
     * the complete class name into a directory path.
     *
     * @param   c       class to be analyzed
     * @return  String  path to the class
     */
    protected static String getAppPath(Class c) {
        String path = c.getName();
        int idx = path.lastIndexOf(".");
        path = path.substring(0, idx);
        path = path.replace('.', File.separatorChar);
        return File.separator + path;
    }


    /**
     * Returns an array of settings which reflect the search order which will
     * be used to locate resources for any derived classes.  The search order
     * is determined by tokenizing the classname.
     * 
     * @param   target  Class from which the search path will be constructed
     */
    protected String[] getSearchPath(Class target) {
        int idx = 0;
        StringTokenizer st = new StringTokenizer(target.getName(), ".");

        String[] path = new String[st.countTokens()];
        while (st.hasMoreTokens()) {
            path[idx] = st.nextToken();
            idx++;
        }

        return path;
    }

    /**
     * Returns an array of settings which reflect the search order which will
     * be used to locate resources for any derived classes.  The search order
     * is determined by extracting information from the user session.  If the
     * user is not authenticated (i.e. no session information is available)
     * then the application defaults will be used.
     */
    protected String[] getSearchPath(HttpServletRequest req) {
        Vector path = new Vector();

        // Get the user information from the session
        HttpSession session = req.getSession();
        UserData user = null;
        if (session != null) {
            user = SessionUtility.getLogin(session);
        }

        // Set the user interface
        path.add(defaultGui);

        // Construct the country and language path component
        Locale locale = null;
        if (user != null) {
            locale = user.getLocale();
        }
        if (locale != null) {
            path.add(locale.getCountry());
            path.add(locale.getLanguage());
        } else {
            path.add(defaultLocale.getCountry());
            path.add(defaultLocale.getLanguage());
        }

        // Construct the group path component
        GroupData group = null;
        if (user != null) {
            group = user.getPrimaryGroup();
        }
        if (group != null) {
            path.add(group.getName());
        }

        // Convert vector to an array
        String[] pathArray = new String[path.size()];
        for (int idx = 0; idx < path.size(); idx++) {
            pathArray[idx] = (String)path.get(idx);
        }
        return pathArray;
    }

    /**
     * Writes directly to the response output stream.
     * This is used to stream files directly to the client, such
     * as a CSV file that the user would expect to download or
     * open in an external application.
     *
     * @param   req     HttpServletRequest object (represents the http request)
     * @param   res     HttpServletResponse object (represents the response)
     * @param   hash    Hashtable of objects rendered using the object toString method 
     */
    public void streamAsSpreadsheet(
            HttpServletRequest req,
            HttpServletResponse res,
            Hashtable hash)
        throws ServletException, IOException
    {
        // spreadsheet content
        List<List<String>> content = new ArrayList<List<String>>(hash.size());

        // Represent each name/value pair as a row in the spreadsheet
        Enumeration hashList = hash.keys();
        while (hashList.hasMoreElements()) {
            Object key = hashList.nextElement();
            Object value = hash.get(key);
            if (key != null) {
                ArrayList<String> row = new ArrayList(2);

                // Column value: name
                row.add(key.toString());

                // Column value: value 
                row.add(value.toString());

                content.add(row);
            }
        }

        // Stream the spreadsheet content to the user
        streamAsSpreadsheet(req, res, content);
    }

    /**
     * Writes directly to the response output stream.
     * This is used to stream files directly to the client, such
     * as a CSV file that the user would expect to download or
     * open in an external application.
     *
     * @param   req     HttpServletRequest object (represents the http request)
     * @param   res     HttpServletResponse object (represents the response)
     * @param   content Multi-dimensional list of objects to render as CSV 
     */
    public void streamAsSpreadsheet(
            HttpServletRequest req, 
            HttpServletResponse res, 
            List<List<String>> content) 
        throws ServletException, IOException
    {
        // Write the CSV content to the output stream
        res.setHeader("content-type", "text/csv");
        res.setHeader("content-disposition", "attachment;filename=\"spreadsheet.csv\"");
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(res.getOutputStream(), "UTF-8"));

        String separator = ",";

        // Iterate through the CSV content
        for (List<String> row : content) {
            for (Iterator<String> iter = row.iterator(); iter.hasNext();) {
                String field = String.valueOf(iter.next()).replace("\"", "\"\"");
                if (field.indexOf(separator) > -1 || field.indexOf('"') > -1) {
                    field = '"' + field + '"';
                }
                writer.append(field);
                if (iter.hasNext()) {
                    writer.append(separator);
                }
            }
            writer.newLine();
        }
        writer.flush();
    } 

    /**
     * Forwards control to a dynamic page such as a JSP or HTML file.
     * If redirection is required so that a new URL appears, use the 
     * response sendRedirect method
     *
     * @param   req     HttpServletRequest object (represents the http request)
     * @param   res     HttpServletResponse object (represents the response)
     * @param   path    Path to the resource which will receive control
     */
    public void forwardToFile(HttpServletRequest req, HttpServletResponse res, String path) 
        throws ServletException, IOException
    {
        String[] classPath = getSearchPath(req);
        String[] classPrefix = {};

        // Use the servlet context to obtain a real path to the relative directories
        ServletContext context = getServletContext();
        File[] matches = BestFileMatch.getRealFileList(
            context.getRealPath(jspDir.toString()), path, classPrefix, classPath);

        // Attempt to locate the resource relative to the servlet path
        String match = null;
        if ((matches != null) && (matches.length > 0)) {
            // Select the most specific resource, which will be the last one in the list
            int idx = matches.length - 1;
            String root = context.getRealPath("/");
            String fullPath = matches[idx].getPath();
            if ((fullPath.length() > root.length()) && fullPath.startsWith(root)) {
                match = fullPath.substring(root.length());
                match = match.replace(File.separatorChar, '/');
                if (!match.startsWith("/")) {
                    match = "/" + match;
                }
            }
        } else {
            commonLog.logEntry(this, SecureLog.DEBUG, "No matching resources found: " + path);
        }

        // Forward control to the resource
        if (match != null) {
            try {
                commonLog.logEntry(this, SecureLog.DEBUG, "Attempting to forward to: " + match);
                RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(match);
                dispatcher.forward(req, res);
            } catch (ServletException se) {
                commonLog.logException(this, SecureLog.ERROR, se, "Target resource failed during processing: " + path);
                displayError(req, res, getError(ErrorMap.APPLICATION_DISPLAY_FAILURE));
            } catch (IOException ioe) {
                commonLog.logException(this, SecureLog.ERROR, ioe, "Unable to open the target resource: " + path);
                displayError(req, res, getError(ErrorMap.APPLICATION_DISPLAY_FAILURE));
            } catch (IllegalStateException ise) {
                commonLog.logException(this, SecureLog.ERROR, ise, "Unable to forward to file: " + path);
                displayFatalError(req, res);
            }
        } else {
            throw new IOException("Unable to locate file: " + path);
        }
    }


    /**
     * Displays an error page to the user.
     *
     * @param   req     HttpServletRequest object (represents the http request)
     * @param   res     HttpServletResponse object (represents the response)
     * @param   error   Error to display to the user
     */
    public void displayError(
            HttpServletRequest req, 
            HttpServletResponse res, 
            ApplicationError error) 
        throws ServletException, IOException
    {
        // Render the entire error matrix
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();
        String title = "Fatal Application Error";
        out.println("<html>");
        out.println("<head><title>" + error.getErrorName() + "</title></head>");
        out.println("<body bgcolor=#FFFFFF>");
        out.println("  <!-- Output generated by Java method: WebApplication.displayError() -->\n");
        out.println("  <center>");
        out.println("    <font size=+1><b>" + error.getErrorName() + "</b></font><br>");
        out.println("  </center><p>");
        out.println(error.toString());
        out.println("</body>");
        out.println("</html>");
    }

    /**
     * Constructs the appropriate application error object based upon the
     * given error code.
     *
     * @param   code    Error code which identifies the error
     */
    public ApplicationError getError(int code) {
        ApplicationError error = null;

        if (errorSettings != null) {
            // Attempt to construct the error from the settings file
            String errorInfo = errorSettings.getProperty("ERROR_" + code);
            if ((errorInfo != null) && (errorInfo.length() > 0)) {
                try {
                    error = ApplicationError.parseLine(code, errorInfo);
                } catch (Exception ex) {
                    error = new ApplicationError(code);
                    error.attachException(ex);
                }
            } else {
                error = new ApplicationError(code);
            }
        } else {
            // Return a generic error
            error = new ApplicationError(code);
        }

        return error;
    }

    /**
     * Constructs the appropriate application error object based upon the
     * given application exception.
     *
     * @param   ex    Exception which identifies the error
     */
    public ApplicationError getError(ApplicationException ex) {
        ApplicationError error = getError(ex.getErrorCode());
        error.attachException(ex);
        return error;
    }

    /**
     * Return the application config value.
     * 
     * @param  name   property name
     * @return Property value
     */
    public String getConfigValue(String name) {
        return appSettings.getProperty(name);
    }

    /**
     * Create a mail message using the app config values.
     */
    public void sendMailMessage(String to, String subject, String text) 
        throws ApplicationException
    {
        // Validate that the "to" address is valid
        if (to != null) {
            try {
                sendPlainMailMessage(InternetAddress.parse(to), null, subject, text);
            } catch (AddressException exTo) {
                throw new ApplicationException(
                    ErrorMap.INVALID_DATABASE_DATA,
                    "Invalid mail recipient: " + to);
            }
        } else {
            throw new ApplicationException(
                ErrorMap.INVALID_DATABASE_DATA,
                "No mail recipient specified.");
        }
    }

    /**
     * Send an e-mail message with a plain text body.
     *
     * @param  to       List of TO addresses
     * @param  cc       List of CC addresses
     * @param  subject  Subject line of the mail message
     * @param  text     Body of the mail message 
     */
    public void sendPlainMailMessage(InternetAddress[] to, InternetAddress[] cc, String subject, String text)
        throws ApplicationException
    {
        sendMailMessage(to, cc, subject, text, "plain");
    }

    /**
     * Send an e-mail message with an HTML body.
     *
     * @param  to       List of TO addresses
     * @param  cc       List of CC addresses
     * @param  subject  Subject line of the mail message
     * @param  text     Body of the mail message 
     */
    public void sendHtmlMailMessage(InternetAddress[] to, InternetAddress[] cc, String subject, String text)
        throws ApplicationException
    {
        sendMailMessage(to, cc, subject, text, "html");
    }

    /**
     * Send an e-mail message with the specified body MIME type.
     *
     * @param  to       List of TO addresses
     * @param  cc       List of CC addresses
     * @param  subject  Subject line of the mail message
     * @param  text     Body of the mail message 
     * @param  format   MIME type used for the message body
     */
    private void sendMailMessage(InternetAddress[] to, InternetAddress[] cc, String subject, String text, String format)
        throws ApplicationException
    {
        String host = appSettings.getProperty("mail.host");
        String from = appSettings.getProperty("mail.from");

        // Validate that the app has a valid "from" address configured
        InternetAddress fromAddr = null;
        if (from != null) {
            try {
                fromAddr = new InternetAddress(from);
            } catch (AddressException exFrom) {
                throw new ApplicationException(
                    ErrorMap.INVALID_CONFIGURATION_ENTRY, 
                    "Invalid mail configuration setting: mail.from");
            }
        } else {
            throw new ApplicationException(
                ErrorMap.MISSING_CONFIGURATION_ENTRY, 
                "Missing mail configuration setting: mail.from");
        }

        // Load the default system properties and override with config values
        Properties mailProps = System.getProperties();
        if (host != null) {
            mailProps.put("mail.smtp.host", host);
        }

        // Create the mail message
        boolean retry = true;
        int sendAttempt = 0;
        InternetAddress[] toList = to;
        InternetAddress[] ccList = cc;
        while (retry) { 
            sendAttempt++;
            try {
                StringBuffer msg = new StringBuffer();
                msg.append("WebApplication.sendMailMessage: ");
                msg.append("attempt=" + sendAttempt);

                Session mailSession = Session.getDefaultInstance(mailProps);
                MimeMessage message = new MimeMessage(mailSession);
                message.setFrom(fromAddr);
                if ((toList != null) && (toList.length > 0)) {
                    msg.append(", to=" + InternetAddress.toString(toList));
                    message.addRecipients(Message.RecipientType.TO, toList);
                }
                if ((ccList != null) && (ccList.length > 0)) {
                    msg.append(", cc=" + InternetAddress.toString(ccList));
                    message.addRecipients(Message.RecipientType.CC, ccList);
                }
                message.setSubject(subject);

                // Use HTML formatting for the body of the message
                message.setText(text, "UTF-8", format);

                // Log the mail attempt
                debug(msg.toString());

                // Send message
                Transport.send(message);
                retry = false;
            } catch (SendFailedException exSend) {
                // Log the invalid attempt
                commonLog.logEntry(this, SecureLog.ERROR, "Attempt " + sendAttempt + " failed to send e-mail: " + exSend.toString());

                // Remove the invalid addresses from the distribution list
                toList = remove(toList, exSend.getInvalidAddresses());
                ccList = remove(ccList, exSend.getInvalidAddresses());

                // Remove the sent addresses from the distribution list
                toList = remove(toList, exSend.getValidSentAddresses());
                ccList = remove(ccList, exSend.getValidSentAddresses());

                // Only retry if there are addresses remaining
                boolean hasTo = ((toList != null) && (toList.length > 0));
                boolean hasCC = ((ccList != null) && (ccList.length > 0));
                retry = (hasTo || hasCC);

                // If there are no "to" recipients, then swap the to and cc list
                if ((!hasTo) && (hasCC)) {
                    toList = ccList;
                    ccList = null;
                    hasTo = true;
                    hasCC = false;
                }
            } catch (MessagingException exMsg) {
                throw new ApplicationException(
                    ErrorMap.MAIL_SEND_FAILURE,
                    "Unable to send mail: " + exMsg.toString());
            }
        }
    } 


    /**
     * Remove the specified e-mail addresses from the list and return the
     * shortened list.
     * 
     * @param   original    Original list
     * @param   remove      List of addresses to remove
     */
    private InternetAddress[] remove(InternetAddress[] original, Address[] remove) {
        // Return immediately if there is nothing to be removed
        if ((original == null) || (original.length == 0) || (remove == null) || (remove.length == 0)) {
            return original;
        }

        Vector<InternetAddress> saved   = new Vector<InternetAddress>();
        Vector<InternetAddress> removed = new Vector<InternetAddress>();

        // Iterate through each address to be removed
        for (int remIdx = 0; remIdx < remove.length; remIdx++) {
            Address rem = remove[remIdx];
            for (int origIdx = 0; origIdx < original.length; origIdx++) {
                // Remove the current address if necessary
                if (rem.equals(original[origIdx])) {
                    // We don't really care about the removed addresses
                    removed.add(original[origIdx]);
                } else {
                    saved.add(original[origIdx]);
                }
            }
        }

        // Return the updated list of addresses
        InternetAddress[] result = null;
        if (saved.size() > 0) {
            result = new InternetAddress[saved.size()];
            for (int idx = 0; idx < saved.size(); idx++) {
                result[idx] = saved.get(idx);
            }
        }
        return result;
    }


    /**
     * Release the repository connection.
     * 
     * @param  rc   Repository connection to be released
     */
    public void releaseRepositoryConnection(RepositoryConnection rc) {
        repositoryMgr.release(rc);
    }

    /**
     * Return the number of active repository connections being
     * managed by the application.
     *
     * @return  Number of active repository connections
     */
    public Hashtable<DataRepository, Integer> getRepositorySize() {
        return repositoryMgr.getConnectionCount();
    }

    /**
     * Return a list of the repositories being managed by the 
     * application.
     *
     * @return  List of repositories
     */
    public Hashtable<String, DataRepository> getRepositoryList() {
        return repositoryMgr.getRepositoryList();
    }

    /**
     * Returns a connection to the account database repository.
     */
    public RepositoryConnection getAccountConnection()
        throws ApplicationException
    {
        return getRepositoryConnection(accountRepository);
    }


    /**
     * Returns a connection to the default database repository.
     * This method should only be used when there is no user data
     * available.
     */
    public RepositoryConnection getRepositoryConnection()
        throws ApplicationException
    {
        return getRepositoryConnection(defaultRepository);
    }

    /**
     * Return a list of invalid repository connections. 
     */
    public Vector<RepositoryConnection> getInvalidConnections() {
        return repositoryMgr.getInvalidConnections();
    }


    /**
     * Returns a connection to the default database repository.
     * This method should only be used when there is no user data
     * available.
     *
     * @param   name    Repository name
     */
    public RepositoryConnection getRepositoryConnection(String name) 
        throws ApplicationException 
    {
        RepositoryConnection conn = null;
        try {
            repositoryMgr.logInvalidConnections();
            conn = repositoryMgr.getDbConnection(name);
            if (conn == null) {
                throw new ApplicationException(ErrorMap.DATABASE_UNAVAILABLE,
                    "Unable to establish a database connection for the repository: " + name);
            }
        } catch (NamingException ne) {
            ApplicationException appex = new ApplicationException(
                 ErrorMap.DATABASE_UNAVAILABLE,
                "Could not perform the JNDI lookup for the database resource.");
            appex.setStackTrace(ne);
            throw appex;
        } catch (SQLException sqe) {
            ApplicationException appex = new ApplicationException(
                 ErrorMap.DATABASE_UNAVAILABLE,
                "Could not retrieve a connection to the database."); 
            appex.setStackTrace(sqe);
            throw appex; 
        }

        return conn;
    }

    /**
     * Returns a connection to the database repository based upon 
     * a specific user.
     *
     * @param   user    UserData which determines the database connection
     */
    public RepositoryConnection getRepositoryConnection(UserData user) 
        throws ApplicationException 
    {
        // Must still implement code for custom repository selection
        return getRepositoryConnection();
    }


    /**
     * Store the current URL in the session as the landing page for the
     * app on the next request.
     *
     */
    public void setLandingPage(HttpServletRequest req) {
        StringBuffer strURL = req.getRequestURL();
        if (req.getQueryString() != null) {
            strURL.append("?" + req.getQueryString());
        }

        // Attempt to create a URL object
        try {
            URL url = new URL(strURL.toString());
            req.setAttribute(UserData.LANDING_PAGE, url);
        } catch (MalformedURLException mfex) {
            // Unable to set the URL
        }
    }

    /**
     * Return the landing page that the app displays immediately following
     * user authentication.  If the user was forwarded to the login page
     * by a protected command, then the user will be sent back to that
     * page after authentication.
     *
     * @return  URL to send the user to following login
     */
    public URL getLandingPage(HttpServletRequest req) {
        URL url = null;

        String landingPage = req.getParameter(UserData.LANDING_PAGE);
        if ((landingPage != null) && (landingPage.length() > 0)) { 
            try {
                url = new URL(landingPage);
            } catch (MalformedURLException mfex) {
                // Unable to parse the URL string
            }
        } else {
            url = (URL) req.getAttribute(UserData.LANDING_PAGE);
        }

        return url;
    }

    /**
     * Return the location of the login page.
     *
     * @return  The default login page to which the user should be sent
     */
    public String getLoginPage() {
        return "login.jsp";
    }

    /**
     * Return the location of the error page.
     *
     * @return  The default error page to which the user should be sent
     */
    public String getErrorPage() {
        return "error.jsp";
    }

    /**
     * Return the location of the user home page.
     *
     * @return  The default home page to which the user should be sent
     */
    public String getHomePage() {
        return "index.jsp";
    }

    /**
     * Return the location of the log file directory as an absolute file
     * path.  The object returned by this method is a copy and will not 
     * reflect subsequent changes.  
     *
     * @return  Log file directory
     */
    public File getLogDir() {
        return logDir.getAbsoluteFile();
    }

    /**
     * Set a global alert message that can be displayed to all users 
     * in the system.
     *
     * @param  msg   Alert message
     */
    public void setAlertMessage(String msg) {
        alertMessage = msg;
    }

    /**
     * Return a global alert message that can be displayed to all
     * users on the system.
     *
     * @return  Global alert message
     */
    public String getAlertMessage() {
        return alertMessage;
    }

    /**
     * Return the absolute path to the web-accessible directory for the
     * application.  This is where the server will look for images, files,
     * and any unprotected resources.  If branding is enabled, the path 
     * path will include language, country, login group, etc.
     *
     * @param   req     Request object containing session information
     * @param   branded Return a fully branded path
     * @return  Web directory
     */
    public File getWebDir(HttpServletRequest req, boolean branded) {
        StringBuffer path = new StringBuffer(getServletContext().getRealPath(""));

        // Obtain the full path for the user
        if (branded) {
            String[] userPath = getSearchPath(req);
            if ((userPath != null) && (userPath.length > 0)) {
                // Strip the first item off the list, we don't want the servlet name
                for (int idx = 0; idx < userPath.length; idx++) {
                    path.append(File.separator + userPath[idx]);
                }
            }
        }

        return new File(path.toString());
    }

    /**
     *
     * @param   req     HttpServletRequest object (represents the http request)
     * @param   res     HttpServletResponse object (represents the response)
     * @param   path    Path to the XML file (relative to the config directory) 
     */
    public Document loadXmlConfig(HttpServletRequest req, HttpServletResponse res, String path)
        throws ServletException, IOException
    {
        Document doc = null;

        String[] classPath = getSearchPath(req);
        String[] classPrefix = {};

        // Use the servlet context to obtain a real path to the relative directories
        ServletContext context = getServletContext();
        File[] matches = BestFileMatch.getRealFileList(
            context.getRealPath(configDir.toString()), path, classPrefix, classPath);

        // Attempt to locate the resource relative to the servlet path
        String match = null;
        if ((matches != null) && (matches.length > 0)) {
            // Select the most specific resource, which will be the last one in the list
            int idx = matches.length - 1;
            String root = context.getRealPath("/");
            String fullPath = matches[idx].getPath();
            if ((fullPath.length() > root.length()) && fullPath.startsWith(root)) {
                match = fullPath.substring(root.length());
                match = match.replace(File.separatorChar, '/');
                if (!match.startsWith("/")) {
                    match = "/" + match;
                }
            }
        } else {
            commonLog.logEntry(this, SecureLog.DEBUG, "No matching XML config file found: " + path);
        }

        // Load the XML file as a DOM file
        if (match != null) {
            try {
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                File xmlFile = new File(context.getRealPath(match));
                doc = db.parse(xmlFile);
            } catch (ParserConfigurationException pe) {
                commonLog.logEntry(this, SecureLog.DEBUG, "Unable to create an XML parser to parse the config file: " + pe.toString());
            } catch (SAXException sex) {
                commonLog.logEntry(this, SecureLog.DEBUG, "Failed to parse XML file: " + match);
            }
        }

        return doc;
    }


    /**
     * Calculate the amount of memory used from the Java Runtime heap.
     * 
     * @return Returns the amount of memory used from the java heap, measured in bytes
     */
    private long getUsedMemory() {
        return runtime.totalMemory() - runtime.freeMemory();
    }

    /**
     * Format the heap value in a more human readable form.
     *
     * @return  Human readable memory string
     */
    private String formatMemory(long bytes) {
        String memory = null;

        DecimalFormat memFormat = new DecimalFormat("#0.00");

        double KB = 1024;
        double MB = KB * 1024;
        double GB = MB * 1024;
        if (Math.abs(bytes) > GB) {
            double memValue = ((double) bytes) / GB; 
            memory = memFormat.format(memValue) + " GB";
        } else if (Math.abs(bytes) > MB) {
            double memValue = ((double) bytes) / MB;
            memory = memFormat.format(memValue) + " MB";
        } else if (Math.abs(bytes) > KB) {
            double memValue = ((double) bytes) / KB;
            memory = memFormat.format(memValue) + " KB";
        } else {
            memory = bytes + " bytes";
        }

        return memory;
    }

}
