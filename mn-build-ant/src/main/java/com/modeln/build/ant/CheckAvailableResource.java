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
import java.net.URL;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;



/**
 * Determines if a resource is available to a forked instance of the JVM.
 *
 * @author Shawn Stafford
 */
public final class CheckAvailableResource extends Task {

    /** Indicates that the resource name refers to a class file */
    private static final String TYPE_CLASS = "class";

    /** Indicates that the resource name refers to a file */
    private static final String TYPE_FILE = "file";

    /** Name of the file resource */
    private String filename = null;

    /** Name of the class resource */
    private String classname = null;

    /** Name of the Ant property used to return the value to Ant. */
    private String returnprop = null;

    /** Path to the Java executable. */
    private String executable = null;

    /** Classpath used to locate resources */
    private Path classpath = null;


    /**
     * Set the name of the file resource to be loaded. 
     *
     * @param   file   Path to the file being loaded     
     */
    public void setFile(String file) {
        filename = file;
    }

    /**
     * Set the name of the class to be loaded.
     *
     * @param   name   Class name 
     */
    public void setClass(String name) {
        classname = name;
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
     * Set the classpath to be used when searching for classes and resources.
     *
     * @param classpath an Ant Path object containing the search path.
     */
    public void setClasspath(Path classpath) {
        createClasspath();
        classpath.append(classpath);
    }

    /**
     * Classpath to be used when searching for classes and resources.
     *
     * @return an empty Path instance to be configured by Ant.
     */
    public void createClasspath() {
        // Make sure we append the current path so the forked JVM
        // is able to execute this class
        classpath = (Path) Path.systemClasspath.clone();
    }

    /**
     * Set the classpath by reference.
     *
     * @param r a Reference to a Path instance to be used as the classpath
     *          value.
     */
    public void setClasspathRef(Reference r) {
        createClasspath();
        Path refpath = (Path) r.getReferencedObject();
        classpath.add(refpath);
    }


    /**
     * Obtain the system property value and return as an Ant property. 
     */
    public void execute() throws BuildException {
        // Make sure the user specifies a return property 
        if (returnprop == null) {
            throw new BuildException("A property name must be provided.", getLocation());
        }

        // Don't even bother doing anything if the executable isn't specified
        if ((executable == null) || (executable.length() == 0)) {
            throw new BuildException("The executable attribute must be set if fork is enabled.", getLocation());
        }

        // Figure out what type of resource has been requested
        String type = null;
        String name = null;
        if ((filename != null) && (classname != null)) {
            throw new BuildException("The class and file attributes are mutually exclusive.", getLocation());
        } else if (filename != null) {
            type = TYPE_FILE;
            name = filename;

            // Haven't figured out why, but the getResource method doesn't seem to work
            throw new BuildException("Support for the file attribute has not been implemented yet.", getLocation());

        } else if (classname != null) {
            type = TYPE_CLASS;
            name = classname;
        } else {
            throw new BuildException("A class or file attribute must be specified.", getLocation());
        }

        // Attempt to load the resource
        String value = null;
        try {
            // Make sure we add the Ant classpath, otherwise it won't be able to find the current class
            if (classpath == null) {
                createClasspath();
            }

            String[] cmd = { executable, "-classpath", classpath.toString(), getClass().getName(), type, name };
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
            throw new BuildException("Unable to fork the JVM process: " + executable, sex, getLocation());
        } catch (NullPointerException npex) {
            throw new BuildException("Invalid arguments used when executing the JVM process: " + executable, npex, getLocation());
        } catch (InterruptedException iex) {
            throw new BuildException("The forked process has been interrupted.", iex, getLocation());
        }

        // Return the property value
        getProject().setProperty(returnprop, value);
    }


    /**
     * Process the command line arguments and execute the program
     * accordingly.
     */
    public static void main(String[] args) {
        String type = args[0];
        String name = args[1];

        // Determine if the specified resource is loadable
        boolean loadable = false;
        if ((type != null) && (name != null) && (name.length() > 0)) {
            if (TYPE_FILE.equalsIgnoreCase(type)) {
                loadable = isLoadableResource(name);
            } else if (TYPE_CLASS.equalsIgnoreCase(type)) {
                loadable = isLoadableClass(name);
            }
        }
 
        // Return the property value
        System.out.println(loadable);
    }


    /**
     * Determine if the specified class can be loaded by the current
     * class loader.
     *
     * @param  name    Class name to be loaded
     * @return TRUE if the class can be loaded, FALSE otherwise
     */
    private static boolean isLoadableClass(String name) {
        boolean loadable = false;

        try {
            ClassLoader loader = ClassLoader.getSystemClassLoader();
            loader.loadClass(name);
            loadable = true;
        } catch (ClassNotFoundException nfex) {
            loadable = false;
        } catch (SecurityException sex) {
            loadable = false;
        } catch (IllegalStateException isex) {
            loadable = false;
        } catch (Error e) {
            loadable = false;
        }

        return loadable;
    }

    /**
     * Determine if the specified resource can be loaded by the current
     * class loader.
     *
     * @param  name    Resource to be loaded
     * @return TRUE if the resource can be loaded, FALSE otherwise
     */
    private static boolean isLoadableResource(String name) {
        boolean loadable = false;

        try {
            ClassLoader loader = ClassLoader.getSystemClassLoader();
            URL resource = loader.getResource(name);
            loadable = (resource != null);
        } catch (SecurityException sex) {
            loadable = false;
        } catch (IllegalStateException isex) {
            loadable = false;
        } catch (Error e) {
            loadable = false;
        }

        return loadable;
    }



}
