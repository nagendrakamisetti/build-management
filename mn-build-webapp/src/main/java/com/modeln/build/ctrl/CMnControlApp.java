/*
 * CMnControlApp.java
 *
 */
package com.modeln.build.ctrl;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import oracle.jdbc.driver.OracleDriver;

import com.modeln.build.web.errors.ApplicationException;
import com.modeln.build.common.logging.SecureLog;
import com.modeln.build.web.application.WebApplication;
import com.modeln.build.web.database.RepositoryConnection;


/**
 * The servlet application extends the custom web application.
 * The web application implements an MVC architecture which is
 * responsible for managing the resources, settings, and 
 * command services.
 * 
 * @version            $Revision: 1.3 $  
 * @author             Shawn Stafford
 */
public class CMnControlApp extends WebApplication {


    /** Service Patch repository name */
    protected String patchRepository;

    /** Bug repository name */
    protected String bugRepository;


    /*
     * Initialization routine called by the host Servlet engine.
     * 
     *
     * @param config the object containing the configuration settings
     */
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        setLogDir(getLogDir().getAbsolutePath());
        setLogLevel(getLogLevel());

        patchRepository = appSettings.getProperty("patch.repository");
        commonLog.logEntry(this, SecureLog.INFO, "Adding patch repository: " + patchRepository); 
        addRepository(patchRepository);

        bugRepository = appSettings.getProperty("bug.repository");
        commonLog.logEntry(this, SecureLog.INFO, "Adding bug repository: " + bugRepository);
        addRepository(bugRepository);

        // Load any JDBC drivers not provided by the base web app architecture
        try {
            DriverManager.registerDriver(new OracleDriver());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Returns a connection to the service patch database repository.
     */
    public RepositoryConnection getPatchRepositoryConnection()
        throws ApplicationException
    {
        return getRepositoryConnection(patchRepository);
    }

    /**
     * Returns a connection to the bug database repository.
     */
    public RepositoryConnection getBugRepositoryConnection()
        throws ApplicationException
    {
        return getRepositoryConnection(bugRepository);
    }


    /**
     * Sets the logging level for the application.
     * The logging levels are defined in 
     * com.modeln.build.common.logging.SecureLog.
     *
     * @param   level   Logging level
     */
    public void setLogLevel(String level) {
        super.setLogLevel(level);
    }

    /**
     * Sets the application logging location.
     *
     * @param   dir     Log directory
     */
    public void setLogDir(String dir) {
        super.setLogDir(dir);
    }


}
