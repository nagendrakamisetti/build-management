/*
 * Copyright 2000-2003 by Model N, Inc.  All Rights Reserved.
 * 
 * This software is the confidential and proprietary information
 * of Model N, Inc ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you
 * entered into with Model N, Inc.
 */
package com.modeln.build.ant;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;

/**
 * Obtain the value of a Java system property and store the value 
 * as an Ant property.  This task allows you to fork off a separate
 * JVM so that it can query the system properties under that JVM.
 *
 * @author Shawn Stafford
 */
public final class SystemPropertyTask extends Task {


    /** Name of the system property being returned. */
    private String propname = null;

    /** Default value of the system property returned by the Ant task. */
    private String defaultvalue = "";

    /** Name of the Ant property used to return the value to Ant. */
    private String returnprop = "system.property.value";

    /** Path to the Java executable. */
    private String executable = null;

    /** Boolean flag to indicate that the task should be executed as a forked process. */
    private boolean fork = false;


    /**
     * Set the name of the system property being returned. 
     *
     * @param   name    Name of the system property to query 
     */
    public void setName(String name) {
        propname = name;
    }

    /**
     * Set the name of the Ant property where the value should be saved.
     *
     * @param   name    Name of the ant property to return 
     */
    public void setProperty(String name) {
        returnprop = name;
    }

    /**
     * Set the default value of the system property if none is found. 
     *
     * @param   value    Default property value 
     */
    public void setDefault(String value) {
        defaultvalue = value;
    }

    /**
     * Set the Java executable that should be used to execute the task
     * as a forked process.  This property is only used if the fork
     * attribute is true. 
     *
     * @param   path    Path to the java executable 
     */
    public void setExecutable(String path) {
        executable = path;
    }

    /**
     * Execute the task as a forked process when true, otherwise execute
     * the task within the Ant JVM.  If fork is true, the executable 
     * attribute must also be set.
     *
     * @param   enabled    TRUE if the task should execute in a forked process 
     */
    public void setFork(boolean enabled) {
        fork = enabled;
    }



    /**
     * Obtain the system property value and return as an Ant property. 
     */
    public void execute() throws BuildException {
        String value = null;
        if (fork) {
            value = executeForked();
        } else {
            value = executeLocal();
        }

        // Use the default value if none can be obtained
        if (value == null) {
            value = defaultvalue;
        }

        // Return the property value
        getProject().setProperty(returnprop, value);
    }


    /**
     * Execute the task in the current JVM.
     */
    private String executeLocal() throws BuildException {
        String value = null;
        try {
            value = System.getProperty(propname);
        } catch (SecurityException sex) {
            throw new BuildException("Unable to access the system property: " + propname, sex, getLocation());
        } catch (NullPointerException npex) {
            throw new BuildException("A system property name must be specified.", npex, getLocation());
        } catch (IllegalArgumentException iaex) {
            throw new BuildException("The system property name must not be empty.", iaex, getLocation());
        }

        return value;
    }

    /**
     * Execute the task in a forked JVM instance. 
     */
    private String executeForked() throws BuildException {
        // Don't even bother doing anything if the executable isn't specified
        if ((executable == null) || (executable.length() == 0)) {
            throw new BuildException("The executable attribute must be set if fork is enabled.", getLocation());
        }

        String value = null;
        try {
            // Figure out what the current classpath is so we can find the class to execute
            String classpath = System.getProperty("java.class.path");

            String[] cmd = { executable, "-classpath", classpath, getClass().getName(), propname };
            Process proc = Runtime.getRuntime().exec(cmd);
            int exitValue = proc.waitFor();
            if (exitValue == 0) {
                BufferedReader stdout = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                value = stdout.readLine();
            } else {
                BufferedReader stdout = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
                throw new BuildException(stdout.readLine(), getLocation());
            }
        } catch (IOException ioex) {
            throw new BuildException("Unable to access forked process.", ioex, getLocation());
        } catch (SecurityException sex) {
            throw new BuildException("Unable to access the system property: " + propname, sex, getLocation());
        } catch (NullPointerException npex) {
            throw new BuildException("A system property name must be specified.", npex, getLocation());
        } catch (InterruptedException iex) {
            throw new BuildException("The forked process has been interrupted.", iex, getLocation());
        } catch (IllegalArgumentException iaex) {
            throw new BuildException("The system property name must not be empty.", iaex, getLocation());
        }

        return value;
    }


    /**
     * Process the command line arguments and execute the program
     * accordingly.
     */
    public static void main(String[] args) {
        String propname = args[0];

        // Obtain the property value from the environment
        String value = null;
        try {
            value = System.getProperty(propname);
        } catch (SecurityException sex) {
            sex.printStackTrace(System.out);
            System.err.println("Unable to access the forked system property: " + propname);
            System.exit(1);
        } catch (NullPointerException npex) {
            npex.printStackTrace(System.out);
            System.err.println("A forked system property name must be specified.");
            System.exit(1);
        } catch (IllegalArgumentException iaex) {
            iaex.printStackTrace(System.out);
            System.err.println("The forked system property name must not be empty.");
            System.exit(1);
        }

        // Return the property value
        if (value != null) {
            System.out.println(value);
        } else {
            System.err.println("The forked property value was null: " + propname);
            System.exit(2);
        }
    }


}
