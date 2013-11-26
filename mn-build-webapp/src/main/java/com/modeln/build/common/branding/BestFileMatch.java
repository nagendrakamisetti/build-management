/*
 * BestFileMatch.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.common.branding;

import java.io.*;
import java.net.URL;
import java.util.*;

/**
 * The BestFileMatch class contains methods for obtaining the most
 * specific resource for a prioritized list of identifiers.  
 * <i>Best matching</i> is a process in which the methods contained
 * within this class attempt to generate a list of possible or actual
 * locations where resources matching the specified criteria can be
 * found on the file system.
 * <p>
 * Configuration files and string files are often stored as plain text
 * files on the file system to allow for portability and ease of 
 * maintenance.  The filename or directory structure can be used to 
 * determine the categorization or specificity of the resource.
 * For example, the file system directory structure is often used 
 * in conjunction with multiple resource files 
 * in order to define default behavior.  While several versions of an 
 * application configuration file may exist on the file system in 
 * various locations, the most specific and appropriate resource is
 * typically located in the deepest directory.  If you were to design
 * an application which required that different configuration settings
 * be loaded depending on the user executing the application and the 
 * day of the week, then the following directory structure would allow
 * you maintain these specialized configurations:
 * <code>
 * /var/myapp/app.conf
 * /var/myapp/fred/app.conf
 * /var/myapp/fred/wednesday/app.conf
 * </code>
 * In this case, the best matching class could be obtain a list of
 * existing or possible matches on the file system based upon the
 * criteria specified.
 *
 * @version            $Revision: 1.1.1.1 $
 * @author             Shawn Stafford
 *
 */
public class BestFileMatch {

    /** Character inserted between the target array items */
    private static final String targetDelimiter = ".";

    /** Character inserted between path elements */
    private static final String pathDelimiter = "/";

    /** TRUE if debugging statements should be displayed */
    private static final boolean debug = false;

    /**
     * A convenience method which can be used when a prefix does not have to
     * be appended.
     * 
     * @param   root    root directory for the search path
     * @param   target  Target name
     * @param   path    Ordered list of resources where a target can be found
     * 
     * @return File[] list of matching resources
     */
    public static File[] getFileList(String root, String target, String[] path) {
        String[] prefix = {};
        return getFileList(root, target, prefix, path);
    }


    /**
     * Returns a list of all possible resources which would be considered
     * as matches.  The most generic resource is the first item in the list
     * and the least generic resource appears as the last item in the list.
     * The list is formatted as if it were a list of files on the filesystem.
     * <b>Files returned by the list are not guaranteed to exist on the file system.</b>
     * <p>
     * The number of files returned will a function of the following:<br>
     * path = { a, b, c } <br>
     * prefix = { x, y, z } <br>
     * file count = (t + 1) * (p + 1) <br>
     * <p>
     * /file
     * /x.file
     * /x.y.file
     * /x.y.z.file
     * <p>
     * /a/file
     * /a/x.file
     * /a/x.y.file
     * /a/x.y.z.file
     * <p>
     * /a/b/file
     * /a/b/x.file
     * /a/b/x.y.file
     * /a/b/x.y.z.file
     * <p>
     * /a/b/c/file
     * /a/b/c/x.file
     * /a/b/c/x.y.file
     * /a/b/c/x.y.z.file
     * <p>
     * 
     * @param   root    root directory for the search path
     * @param   target  Target name
     * @param   prefix  Ordered list of target prefixes
     * @param   path    Ordered list of resources where a target can be found
     * 
     * @return File[] list of matching resources
     */
    public static File[] getFileList(String root, String target, String[] prefix, String[] path) {
        Vector resources = new Vector();

        // The count represents the total number of files which should 
        // ultimately appear in the file list.
        int count = (path.length + 1) * (prefix.length + 1);

        // Construct the resource list
        String strPath = root;
        int resourceIdx = count;
        for (int idxPath = 0; idxPath < (path.length + 1); idxPath++) {
            // Make sure to account for the first path entry (root)
            if (idxPath > 0) {
                strPath = strPath + path[idxPath - 1];
            }

            // Make sure the path delimiter has been appended
            if ((strPath.length() > 0) && !strPath.endsWith(pathDelimiter)) {
                strPath = strPath + pathDelimiter;
            }

            // Append the target prefix
            String strTarget = "";
            for (int idxPrefix = 0; idxPrefix < (prefix.length + 1); idxPrefix++) {
                resourceIdx--;

                // Make sure to account for the first target (no target prefix)
                if (idxPrefix > 0) {
                    strTarget = strTarget + prefix[idxPrefix - 1] + targetDelimiter;
                }

                resources.add(new File(strPath + strTarget + target));
            }

        }

        // Place the file list into an array and return the array
        File[] files = new File[resources.size()];
        for (int idx = 0; idx < resources.size(); idx++) {
            files[idx] = (File) resources.get(idx);
            if (debug) System.err.println("Potential file: " + files[idx].toString());
        }

        return files;
    }


    /**
     * A convenience method which can be used when a prefix does not have to
     * be appended.
     * 
     * @param   root    root directory for the search path
     * @param   target  Target name
     * @param   path    Ordered list of resources where a target can be found
     * 
     * @return  File[] list containing available files
     * @throws  IOException if the classloader fails to access the resource
     */
    public static File[] getRealFileList(String root, String target, String[] path) 
        throws IOException
    {
        String[] prefix = {};
        return getRealFileList(root, target, prefix, path);
    }


    /**
     * Determines which files actually exist on the file system by attempting
     * to load the file resources.  Any resources which are available are 
     * returned as a list of files.  The getFileList method is used to determine
     * the list of possible file locations.
     * 
     * @param   root    root directory for the search path
     * @param   target  Target name
     * @param   prefix  Ordered list of target prefixes
     * @param   path    Ordered list of resources where a target can be found
     * 
     * @return  File[] list containing available files
     * @throws  IOException if the classloader fails to access the resource
     */
    public static File[] getRealFileList(String root, String target, String[] prefix, String[] path) 
        throws IOException
    {
        Vector resources = new Vector();

        // Create a list of all possible files
        // This must be done so that the system loader looks for specific
        // files rather than generic matches.
        File[] candidates = getFileList(root, target, prefix, path);

        // Use the system loader to locate the files
        File[] matches = null;
        ClassLoader fileLoader = ClassLoader.getSystemClassLoader();
        for (int idx = 0; idx < candidates.length; idx++) {
            // Search for all possible files in the classpath
            matches = getFiles(candidates[idx]);
            for (int idxMatch = 0; idxMatch < matches.length; idxMatch++) {
                if (debug) System.err.println("Valid file: " + matches[idxMatch]);
                resources.add(matches[idxMatch]);
            }
        }

        // Place the file list into an array and return the array
        File[] files = new File[resources.size()];
        for (int idx = 0; idx < resources.size(); idx++) {
            files[idx] = (File) resources.get(idx);
        }

        return files;
    }


    /**
     * Uses the system class loader to locate the given file and return its
     * location as a list of File objects.  If no resource can be found, null is 
     * returned.  In order to search the entire classpath for the file, the
     * filename specified should be a relative path, rather than an absolute
     * path.
     *
     * @param   file    Identifies the file (including path) to be loaded
     * @return  File    object found by the class loader
     * @throws  IOException if the classloader fails to access the resource
     */
    public static File[] getFiles(File file) throws IOException {
        ClassLoader fileLoader = ClassLoader.getSystemClassLoader();
        Vector resources = new Vector();

        if (file.isAbsolute()) {
             if (file.exists()) resources.add(file);
        } else {

            // Gather all possible matching resources
            String filename = file.toString();
            filename = filename.replace(File.separatorChar, '/');
            Enumeration list = fileLoader.getResources(filename);
            while (list.hasMoreElements()) {
                resources.add(new File(((URL)list.nextElement()).getFile()));
            }
        }
        

        // Place the file list into an array and return the array
        File[] files = new File[resources.size()];
        for (int idx = 0; idx < resources.size(); idx++) {
            files[idx] = (File) resources.get(idx);
            if (debug) System.err.println("Potential file exists: " + files[idx]);
        }

        return files;
    }
}
