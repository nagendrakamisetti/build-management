/*
 * Copyright 2000-2010 by Model N, Inc.  All Rights Reserved.
 * 
 * This software is the confidential and proprietary information
 * of Model N, Inc ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you
 * entered into with Model N, Inc.
 */
package com.modeln.build.ant.report.db;

import com.modeln.testfw.reporting.CMnBuildTable;
import com.modeln.testfw.reporting.CMnDbBuildData;
import com.modeln.testfw.reporting.CMnDbHostData;
import com.modeln.testfw.reporting.CMnDbUnitTestData;
import com.modeln.testfw.reporting.CMnDbTestSuite;
import com.modeln.testfw.reporting.CMnUnittestTable;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestCase;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitResultFormatter;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitTest;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitTestRunner;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitVersionHelper;

/**
 * Parses the output of the JUnit Ant task and logs the result to the 
 * build database.
 *
 * @author Shawn Stafford
 */
public class DbJUnitFormatter implements JUnitResultFormatter {

    /** Property name that identifies the JDBC driver */
    private static final String BUILDMGT_JDBC_DRIVER = "reporting.jdbc.driver";

    /** Property name that identifies the JDBC URL for the build management database */
    private static final String BUILDMGT_JDBC_URL = "reporting.jdbc.url";

    /** Property name that identifies the JDBC URL for connecting to the Model N app */
    private static final String MODELN_JDBC_URL = "modeln.jdbc.url";

    /** Property name that identifies the Model N product version */
    private static final String MODELN_VERSION = "modeln.version";

    /** Connection to the audit database */
    private Connection conn = null;

    /** Information about the current build */
    private CMnDbBuildData build;

    /** Primary key to identify a test suite */
    private String suiteId = null;

    /** Information about the current test suite */
    private CMnDbTestSuite suiteData;

    /** Information about the current test */
    private CMnDbUnitTestData testData = new CMnDbUnitTestData();



    /**
     * Formatter for timings.
     */
    private NumberFormat nf = NumberFormat.getInstance();

    /**
     * Timing helper.
     */
    private Hashtable<Test, Long> testStarts = new Hashtable<Test, Long>();

    /**
     * Where to write the log to.
     */
    private OutputStream out;

    /**
     * Helper to store intermediate output.
     */
    private StringWriter inner;

    /**
     * Convenience layer on top of {@link #inner inner}.
     */
    private PrintWriter wri;

    /**
     * Suppress endTest if testcase failed.
     */
    private Hashtable<Test, Boolean> failed = new Hashtable<Test, Boolean>();

    private String systemOutput = null;
    private String systemError = null;

    public DbJUnitFormatter() {
        inner = new StringWriter();
        wri = new PrintWriter(inner);
    }





    //
    // Implements: JUNitResultFormatter.startTestSuite(JUnitTest suite)
    //
    @SuppressWarnings("unchecked")
	public void startTestSuite(JUnitTest suite) throws BuildException {
        // We create a new suite data object for this suite
        suiteData = createSuiteData(suite);
        suiteData.setSuiteName(getSuiteName(suite));

        // Obtain a connection to the build database
        conn = getConnection(System.getProperties());
        if (conn == null) {
            throw new BuildException("Unable to obtain database connection.");
        }

        // Load build data from the database
        String buildVersion = System.getProperty(MODELN_VERSION);
        if ((buildVersion != null) && (buildVersion.length() > 0)) {
            Vector<CMnDbBuildData> builds = null;
            try {
                builds = CMnBuildTable.getBuildsByVersion(conn, buildVersion);
            } catch (Exception ex) {
                throw new BuildException("Unable to obtain a list of builds.");
            }

            // Select a build and save the associated suite data
            if ((builds != null) && (builds.size() > 0)) {
                build = builds.get(0);
            } else {
                throw new BuildException("No builds found for this version: " + buildVersion);
            }

        } else {
            throw new BuildException("Build version property undefined: " + MODELN_VERSION);
        }

        // Add the suite data to the database
        if (build != null) {
            try {
                String buildId = Integer.toString(build.getId());
                suiteId = CMnUnittestTable.getInstance().addSuite(conn, buildId, suiteData);
            } catch (SQLException ex) {
                throw new BuildException("Unable to add suite information to the database.", ex);
            }
        } else {
            throw new BuildException("Unable to obtain build information.");
        }

    }

    //
    // Implements: JUnitResultFormatter.endTestSuite(JUnitTest suite)
    //
    public void endTestSuite(JUnitTest suite) {
        String newLine = System.getProperty("line.separator");

        StringBuffer sb = new StringBuffer();
        if (build != null) {
            sb.append("Build Version: " + build.getBuildVersion() + newLine);
        }
        sb.append("Testsuite: " + suite.getName() + newLine);
        sb.append("Tests run: " + suite.runCount());
        sb.append(", Failures: " + suite.failureCount());
        sb.append(", Errors: " + suite.errorCount());
        sb.append(", Time elapsed: " + nf.format(suite.getRunTime() / 1000.0) + " sec");
        sb.append(newLine);

        // append the err and output streams to the log
        if (systemOutput != null && systemOutput.length() > 0) {
            sb.append("------------- Standard Output ---------------")
                .append(newLine)
                .append(systemOutput)
                .append("------------- ---------------- ---------------")
                .append(newLine);
        }

        if (systemError != null && systemError.length() > 0) {
            sb.append("------------- Standard Error -----------------")
                .append(newLine)
                .append(systemError)
                .append("------------- ---------------- ---------------")
                .append(newLine);
        }

        sb.append(newLine);

        if (out != null) {
            try {
                out.write(sb.toString().getBytes());
                wri.close();
                out.write(inner.toString().getBytes());
                out.flush();
            } catch (IOException ioex) {
                throw new BuildException("Unable to write output", ioex);
            } finally {
                if (out != System.out && out != System.err) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        // ignore
                    }
                }
            }
        }


        // Update the date in the database
        if (conn != null) {
            try {
                Date enddate = new Date();
                CMnUnittestTable.getInstance().setSuiteEndDate(conn, suiteId, enddate);
                suiteData.setEndTime(enddate);
            } catch (SQLException ex) {
                throw new BuildException("Unable to add suite information to the database.", ex);
            }
        } else {
            throw new BuildException("Database connection unavailable.");
        }

    }


    //
    // Implements: JUnitResultFormatter.setOutput(OutputStream out)
    //
    public void setOutput(OutputStream out) {
        this.out = out;
    }


    //
    // Implements: JUnitResultFormatter.setSystemError(String err)
    //
    public void setSystemError(String err) {
        systemError = err;
    }


    //
    // Implements: JUnitResultFormatter.setSystemOutput(OutputStream out)
    //
    public void setSystemOutput(String out) {
        systemOutput = out;
    }

    //
    // Implements: TestListener.addError(Test test, Throwable t)
    //
    public void addError(Test test, Throwable t) {
        formatError("\tCaused an ERROR", test, t);

        testData.addMessage(JUnitTestRunner.getFilteredTrace(t));
        // Record the error status
        testData.updateStatus(CMnDbUnitTestData.ERROR);
    }

    //
    // Implements: TestListener.addFailure(Test test, Throwable t)
    //
    public void addFailure(Test test, Throwable t) {
        formatError("\tFAILED", test, t);

        testData.addMessage(JUnitTestRunner.getFilteredTrace(t));
        // Let updateStatus determine if the test actually failed
        testData.updateStatus(CMnDbUnitTestData.FAIL);
    }       


    //
    // Implements: TestListener.addFailure(Test test, AssertionFailedError t)
    //
    public void addFailure(Test test, AssertionFailedError t) {
        addFailure(test, (Throwable) t);
    }

    //
    // Implements: TestListener.startTest(Test test)
    //
    public void startTest(Test test) {
        testStarts.put(test, new Long(System.currentTimeMillis()));
        failed.put(test, Boolean.FALSE);

        // Construct the database data object
        testData = new CMnDbUnitTestData();
        testData.setClassName(test.getClass().getName());
        if (test instanceof TestCase) {
            testData.setMethodName(((TestCase)test).getName());
        }

        // Set the test start time
        testData.setStartTime(new Date());


        // Make a note if this thread is not running in the main thread
        String threadName = Thread.currentThread().getName();
        if (! threadName.equals("main")) {
            testData.addMessage("THREAD: " + threadName);
        }

    }


    //
    // Implements: TestListener.endTest(Test test)
    //
    public void endTest(Test test) {
        testData.setEndTime(new Date());

        // Write failed tests to the output file
        if (!Boolean.TRUE.equals(failed.get(test))) {
            synchronized (wri) {
                wri.print("Testcase: " + JUnitVersionHelper.getTestCaseName(test));
                Long l = testStarts.get(test);
                double seconds = 0;
                // can be null if an error occurred in setUp
                if (l != null) {
                    seconds = (System.currentTimeMillis() - l.longValue()) / 1000.0;
                }
                wri.println(" took " + nf.format(seconds) + " sec");
            }
        }


        // Let updateStatus determine if the test actually passed
        testData.updateStatus(CMnDbUnitTestData.PASS);

        // Don't bother saving the debug statements if the test passed
        if (testData.getStatus() == CMnDbUnitTestData.PASS) {
            testData.clearMessage();
        }

        // Write the info for the current test to the database
        try {
            CMnUnittestTable.getInstance().addTest(conn, suiteId, testData);
        } catch (Exception ex) {
            System.err.println("Unable to add unit test information to the database.");
            ex.printStackTrace();
        }

    }


    /**
     * Establish a JDBC connection to the build database.
     *
     * @param  props    Ant properties containing JDBC information 
     *
     * @return JDBC connection
     *
     * @throws BuildException if the JDBC properties are not defined or 
     *         a database connection cannot be established
     */
    protected Connection getConnection(Properties props) throws BuildException { 
        Connection conn = null;

        // Obtain the JDBC information from the Ant properties
        String jdbcDriver = null;
        String jdbcUrl = null;
        if (props != null) {
            jdbcDriver = props.getProperty(BUILDMGT_JDBC_DRIVER);
            if ((jdbcDriver == null) || (jdbcDriver.length() == 0)) {
                throw new BuildException("Build Management JDBC Driver property undefined: " + BUILDMGT_JDBC_DRIVER);
            }

            jdbcUrl = props.getProperty(BUILDMGT_JDBC_URL);
            if ((jdbcUrl == null) || (jdbcUrl.length() == 0)) {
                throw new BuildException("Build Management JDBC URL property undefined: " + BUILDMGT_JDBC_URL);
            }
        } else {
            throw new BuildException("Unable to obtain Build Management JDBC properties.");
        }


        // Force the class loader to load the JDBC driver
        try {
            // The newInstance() call is a work around for some 
            // broken Java implementations
            Class.forName(jdbcDriver).newInstance();
        } catch (Exception ex) {
            throw new BuildException("Failed to load the JDBC driver: " + jdbcDriver, ex);
        }

        // Establish a connection to the database
        try {
            conn = DriverManager.getConnection(jdbcUrl);
        } catch (SQLException ex) {
            throw new BuildException("Failed to establish a database connection: url=" + jdbcUrl, ex);
        }

        return conn;
    }


    /**
     * Helper function to create a suite that is initialized with the
     * suite name, start time, host data, and application URL.
     */
    protected CMnDbTestSuite createSuiteData(JUnitTest suite) {
        CMnDbTestSuite result = new CMnDbTestSuite(CMnDbTestSuite.SuiteType.JUNIT);
        result.setStartTime(new Date());
        result.setSuiteName(suite.getName());
        result.setHostData(CMnDbHostData.getHostData());

        // Load the database and host properties from the environment
        String appUrl = System.getProperty(MODELN_JDBC_URL);
        if ((appUrl != null) && (appUrl.length() > 0)) {
            result.setJdbcUrl(appUrl);
        }

        return result;
    }


    /**
     * Return the name of the test suite.  If the suite object does not
     * contain a value in the name field, the name will be derived from
     * the class name. 
     *
     * @param   suite    Test suite information
     * @return  Name of the test suite 
     */
    public String getSuiteName(JUnitTest suite) {
        String suiteName = null;

        String suiteClass = suite.getName();
        String suitePackage = null;

        // If the suite name is not specified, use the class name instead
        if ((suite.getName() != null) && (suite.getName().length() > 0)) {
            int delimIdx = suite.getName().lastIndexOf('.');
            if ((delimIdx > 0) && (delimIdx < suite.getName().length())) {
                suiteClass = suite.getName().substring(delimIdx);
                suitePackage = suite.getName().substring(0, delimIdx);
            } else {
                suitePackage = "";
            }

            suiteName = suite.getName();
        } else {
            suiteClass = suite.getClass().getName();
            suitePackage = suite.getClass().getPackage().getName();

            // Trim off the fully qualified package name
            if ((suitePackage != null) && suiteClass.startsWith(suitePackage) && (suiteClass.length() > suitePackage.length())) {
                suiteName = suite.getName().substring(suitePackage.length() + 1);
            } else {
                suiteName = suite.getName();
            }
        }

        return suiteName;
    }


    /**
     * Format the failure for output.
     *
     * @param  type   Type of error
     * @param  test   Unit test
     * @param  t      Exception thrown by the failure
     */
    private void formatError(String type, Test test, Throwable t) {
        synchronized (wri) {
            if (test != null) {
                failed.put(test, Boolean.TRUE);
            }

            wri.println(type);
            wri.println(t.getMessage());
            String strace = JUnitTestRunner.getFilteredTrace(t);
            wri.print(strace);
            wri.println("");
        }
    }

}

