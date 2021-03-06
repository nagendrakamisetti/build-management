/*
 * Copyright 2000-2003 by Model N, Inc.  All Rights Reserved.
 * 
 * This software is the confidential and proprietary information
 * of Model N, Inc ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you
 * entered into with Model N, Inc.
 */
package com.modeln.build.ant.depends;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.types.FileList;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;


/**
 * Constructs a list of resources and the classes that depend on them.
 * This task is primarily used to determine which Jars are required by
 * the specified list of classes.  The task parses each Java file 
 * specified in the fileset.  It then attempts to load any classes 
 * referenced by import statements.  The import classes
 * are then added to a hashtable of required resources.  Once
 * all classes have been processed, a summary of the required resources
 * will be generated by the task.
 *
 * @author Shawn Stafford
 */
public final class DependencyListTask extends Task {


    /**
     * Classpath
     */
    private Path classpath = null;

    /**
     * Stores the list of classes to be checked for dependencies.
     */
    private Vector<DataType> classes = new Vector<DataType>();

    /**
     * List of resources that have dependencies.
     */
    private Hashtable<String, MatchingResource> dependencies = new Hashtable<String, MatchingResource>();

    /**
     * Only track dependencies at the Jar level.
     */
    private boolean restrictToJars = true;

    /**
     * List of packages to be included for processing.
     */
    private Vector<PackageName> packageNames = new Vector<PackageName>();

    /**
     * List of packages to be excluded during processing.
     */
    private Vector<PackageName> excludePackageNames = new Vector<PackageName>();

    /**
     * List of resource locations to consider.
     */
    private Vector<FileResource> resourceRoots = new Vector<FileResource>();

    /** 
     * List of resource locations to exclude.
     */
    private Vector<FileResource> excludeResourceRoots = new Vector<FileResource>();

    /**
     * Enable the output of progress indicators when processing files.
     */
    private boolean displayProgress = false;


    /** 
     * Output file where the summary will be written.
     */
    private File reportFile = null;

    /**
     * Output format of the dependency summary.
     */
    private String reportFormat = "text";

    /**
     * Determine whether the dependency summary should be
     * restricted to only Jar files.
     */
    public void setRestrictToJars(boolean restrict) {
        restrictToJars = restrict;
    }

    /**
     * Enable the display of progress indicators during source code processing.
     */
    public void setDisplayProgress(boolean enable) {
        displayProgress = enable;
    }

    /**
     * Set the classpath to use when looking up a resource.
     * @param classpath to add to any existing classpath
     */
    public void setClasspath(Path classpath) {
        if (this.classpath == null) {
            this.classpath = classpath;
        } else {
            this.classpath.append(classpath);
        }
    }

    /**
     * Add a classpath to use when looking up a resource.
     */
    public Path createClasspath() {
        if (this.classpath == null) {
            this.classpath = new Path(getProject());
        }
        return this.classpath.createPath();
    }

    /**
     * Set the classpath to use when looking up a resource,
     * given as reference to a &lt;path&gt; defined elsewhere
     */
    public void setClasspathRef(Reference r) {
        createClasspath().setRefid(r);
    }

    /**
     * get the classpath used by this <CODE>LoadProperties</CODE>.
     */
    public Path getClasspath() {
        return classpath;
    }

    /**
     * Display the classpath to the user.
     */
    public void printClasspath() {
        final String[] list = classpath.list();
        if (list.length > 0) {
            System.out.println("Classpath: ");
            for (int i = 0; i < list.length; i++) {
                System.out.println("   " + list[i]);
            }
        }
    }

    /**
     * Set of classes to be checked for dependencies.
     *
     * @param set     The set of class files
     */
    public void addFileset(FileSet set) {
        classes.addElement(set);
    }

    /**
     * List of classes to be checked for dependencies.
     *
     * @param list    The list of class files
     */
    public void addFilelist(FileList list) {
        classes.addElement(list);
    }


    /**
     * Set the package names to be processed.
     *
     * @param packages a comma separated list of packages specs
     *        (may include wildcards).
     *
     * @see #addPackage for wildcard information.
     */
    public void setIncludes(String packages) {
        StringTokenizer tok = new StringTokenizer(packages, ",");
        while (tok.hasMoreTokens()) {
            String p = tok.nextToken();
            PackageName pn = new PackageName();
            pn.setName(p);
            addConfiguredInclude(pn);
        }
    }

    /**
     * Add a single package to be processed.
     *
     * If the package name ends with &quot;.*&quot; the dependency task
     * will find and process all subpackages.
     *
     * @param pn the package name, possibly wildcarded.
     */
    public void addConfiguredInclude(PackageName pn) {
        packageNames.addElement(pn);
    }

    /**
     * Set the list of packages to be excluded.
     *
     * @param packages a comma separated list of packages to be excluded.
     *        (may include wildcards).
     */
    public void setExcludes(String packages) {
        StringTokenizer tok = new StringTokenizer(packages, ",");
        while (tok.hasMoreTokens()) {
            String p = tok.nextToken();
            PackageName pn = new PackageName();
            pn.setName(p);
            addConfiguredExclude(pn);
        }
    }

    /**
     * Add a package to be excluded from the javadoc run.
     *
     * @param pn the package name, possibly wildcarded.
     */
    public void addConfiguredExclude(PackageName pn) {
        excludePackageNames.addElement(pn);
    }


    /**
     * Set the list of resource root directories to be included for consideration
     * when determining where to load resources from.
     *
     * @param  dirs  A path-delimited list of directories to be included (no wildcards)
     */
    public void setResourceIncludes(String dirs) {
        StringTokenizer tok = new StringTokenizer(dirs, File.pathSeparator);
        while (tok.hasMoreTokens()) {
            FileResource dir = new FileResource();
            dir.setPath(tok.nextToken());
            addConfiguredResourceInclude(dir);
        }
    }

    /**
     * Adds a resource root directory to be included for consideration when
     * determining the source of resources.
     *
     * @param  dir  Directory under which the resources are located
     */
    public void addConfiguredResourceInclude(FileResource dir) {
        resourceRoots.addElement(dir);
    }

    /**
     * Set the list of resource root directories to be excluded from consideration
     * when determining where to load resources from.
     *
     * @param  dirs  A path-delimited list of directories to be excluded (no wildcards)
     */
    public void setResourceExcludes(String dirs) {
        StringTokenizer tok = new StringTokenizer(dirs, File.pathSeparator);
        while (tok.hasMoreTokens()) {
            FileResource dir = new FileResource();
            dir.setPath(tok.nextToken());
            addConfiguredResourceExclude(dir);
        }
    }

    /**
     * Adds a resource root directory to be excluded from consideration when
     * determining the source of resources.
     *
     * @param  dir  Directory under which the resources are located
     */
    public void addConfiguredResourceExclude(FileResource dir) {
        excludeResourceRoots.addElement(dir);
    }


    /**
     * Set the output format of the dependency summary.  The default
     * format is "text." Valid formats are: text and html.
     */
    public void setReportFormat(String format) {
        if (format.equalsIgnoreCase("HTML") || format.equalsIgnoreCase("TEXT")) {
            reportFormat = format;
        } else {
            throw new BuildException("Invalid report format: " + format);
        }
    }

    /**
     * Set the output file where the dependency information will be written.
     * If no file is specified, output will be sent to STDOUT.
     */
    public void setReportFile(File file) {
        reportFile = file;
    }

    /**
     * Determine if the current import class should be processed 
     * for dependencies.
     *
     * @param  pn  Package name
     */
    private boolean allowImport(PackageName pn) {
        boolean includePackage = true;

        // Iterate through the explicitly included packages
        if ((packageNames != null) && (packageNames.size() > 0)) {
            // If any explicit includes have been specified, then the import
            // must be explicitly allowed
            includePackage = false;

            Enumeration<PackageName> e = packageNames.elements();
            while (e.hasMoreElements()) {
                PackageName currentInclude = (PackageName) e.nextElement();
                if (pn.matches(currentInclude.toString())) {
                    includePackage = true;
                }
            }
        }

        // Iterate through the explicitly excluded packages
        if ((excludePackageNames != null) && (excludePackageNames.size() > 0)) {
            Enumeration<PackageName> e = excludePackageNames.elements();
            while (e.hasMoreElements()) {
                PackageName currentExclude = (PackageName) e.nextElement();
                if (pn.matches(currentExclude.toString())) {
                    includePackage = false;
                }
            }
        }

        return includePackage;
    }

    /**
     * Determine if the current resource URL should be considered
     * as a dependency for an import.
     *
     * @param  resource  Name of the resource for consideration
     */
    private boolean allowResource(URL resource) {
        boolean includeResource = true;
        String resourcePath = resource.getPath();

        // Determine if only Jar files should be considered
        String jarPrefix = "jar:file:";
        String resourceName = resource.toString();
        if (restrictToJars && !resourceName.startsWith(jarPrefix)) {
            return false;
        }

        // Iterate through the explicitly included packages
        if ((resourceRoots != null) && (resourceRoots.size() > 0)) {
            // If any explicit includes have been specified, then the resource
            // must be explicitly allowed
            includeResource = false;

            Enumeration<FileResource> e = resourceRoots.elements();
            while (e.hasMoreElements()) {
                FileResource currentRoot = (FileResource) e.nextElement();
                String rootPath = currentRoot.getPath();
                if (resourcePath.startsWith(rootPath)) {
                    includeResource = true;
                }
            }
        }

        // Iterate through the explicitly excluded packages
        if ((excludeResourceRoots != null) && (excludeResourceRoots.size() > 0)) {
            Enumeration<FileResource> e = excludeResourceRoots.elements();
            while (e.hasMoreElements()) {
                FileResource currentRoot = (FileResource) e.nextElement();
                String rootPath = currentRoot.getPath();
                if (resourcePath.startsWith(rootPath)) {
                    includeResource = false;
                }
            }
        }

        return includeResource;
    }


    /**
     * Iterate through the list of classes and compose the dependency list.
     */
    public void execute() {
        // Verify that there is something to do
        if (classes == null) {
            throw new BuildException("A list of source files must be specified.");
        } else if (classes.size() == 0) {
            throw new BuildException("Unable to find any Java source files for the specified file pattern.");
        }

        printClasspath();

        // Iterate through the list of classes
        for (Enumeration<DataType> e = classes.elements(); e.hasMoreElements(); ) {
            Object o = e.nextElement();
            if (o instanceof Path) {
                Path path = (Path) o;
                parseClasses(null, path.list());

            } else if (o instanceof FileSet) {
                FileSet fileSet = (FileSet) o;
                DirectoryScanner scanner =
                    fileSet.getDirectoryScanner(getProject());
                parseClasses(fileSet.getDir(getProject()),
                              scanner.getIncludedFiles());

            } else if (o instanceof FileList) {
                FileList fileList = (FileList) o;
                parseClasses(fileList.getDir(getProject()),
                              fileList.getFiles(getProject()));
            }
        }

        // Determine the corrout output location for the report
        PrintStream stream = null;
        if (reportFile != null) {
            try {
                stream = new PrintStream(new FileOutputStream(reportFile));
                log("Writing report to: " + reportFile, Project.MSG_INFO);
            } catch (Exception ex) {
                throw new BuildException("Unable to write to report file: " + ex);
            }
        } else {
            stream = System.out;
        }

        // Write the report 
        if (reportFormat.equalsIgnoreCase("HTML")) {
            writeHtmlReport(stream);
        } else {
            writeTextReport(stream);
        }

    }

    /**
     * Generate an HTML report by sending output to the specified output stream.
     */
    private void writeTextReport(PrintStream out) {
        for (Enumeration<String> keys = dependencies.keys(); keys.hasMoreElements(); ) {
            Object key = keys.nextElement();
            out.println(key);
            MatchingResource match = (MatchingResource) dependencies.get(key);
            Iterator<File> classIterator = match.getAllSources().iterator();
            while (classIterator.hasNext()) {
                out.println("   " + classIterator.next());
            }
        }
    }

    /**
     * Generate an HTML report by sending output to the specified output stream.
     */
    private void writeHtmlReport(PrintStream out) {
        out.println("<html>");
        out.println("<head>");
        out.println("  <title>Dependency Report</title>");
        out.println("</head>");
        out.println("<body>");

        // Display a table of contents
        out.println("<h1>Dependencies</h1>");
        out.println("<ul>");
        for (Enumeration<String> keys = dependencies.keys(); keys.hasMoreElements(); ) {
            String key = keys.nextElement();
            out.println("<li><a href=\"#" + key + "\">" + key + "</a></li>");
        }
        out.println("</ul>");

        // Display the dependency information details
        out.println("<h1>Details</h1>");
        for (Enumeration<String> keys = dependencies.keys(); keys.hasMoreElements(); ) {
        	String key = keys.nextElement();
            MatchingResource match = (MatchingResource) dependencies.get(key);

            out.println("<a name=\"" + key + "\"><b>" + key + "</b></a><br>");
            out.println("  <blockquote>");

            // Display the fallback resource
            Iterator<String> fallbackList = match.getFallback().iterator();
            while (fallbackList.hasNext()) {
                out.println("<font color=\"#FF0000\">Fallback resource: " + fallbackList.next() + "</font><br>");
            }

            // Display the list of source files referencing the resource
            out.println("  <pre>");
            Iterator<File> classIterator = match.getAllSources().iterator();
            while (classIterator.hasNext()) {
                out.println(classIterator.next());
            }
            out.println("  </pre>");

            out.println("  </blockquote>");
        }

        out.println("</body>");
        out.println("</html>");
        out.close();
    }


    /**
     * Iterate through the list of files and parse each source file.
     *
     * @param base        Root directory that the files are relative to
     * @param filenames   List of class files
     */
    private void parseClasses(File base, String[] filenames) {
        if (displayProgress) {
            System.out.println("Parsing source files...");
        }

        for (int i = 0; i < filenames.length; ++i) {
            if (displayProgress && ((i%80) == 0)) {
                System.out.print("\n");
            }
            parseClass(new File(base, filenames[i]));
        }

        // Make sure the current progress line is displayed
        if (displayProgress) {
            System.out.print("\n");
            System.out.println("Source file parsing complete.");
        }
    }

    /**
     * Parse the source file to determine which classes are referenced by
     * import statements.
     */
    private void parseClass(File file) {
        if (displayProgress) {
            System.out.print(".");
        }

        log("Processing file: " + file, Project.MSG_DEBUG);
        if (file.exists()) {
            try {
                // Load the import statements from the current source file
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("import ") && (line.indexOf("*") < 0) && (line.indexOf(";") > 0)) {
                        // Obtain the name of the current import
                        String name = line.substring(7, line.indexOf(";")).trim();

                        // Load the imported class if it is 
                        int classIdx = name.lastIndexOf('.');
                        if (classIdx > 0) {
                            String pkgName = name.substring(0, classIdx);
                            PackageName pkg = new PackageName();
                            pkg.setName(pkgName);
                            if (allowImport(pkg)) {
                                String resourceName = name.replace('.', File.separatorChar) + ".class";
                                loadResource(new File(resourceName), file);
                            }
                        }

                    }
                }
            } catch (Exception ex) {
                log("Unable to process source file: " + file, Project.MSG_ERR);
                ex.printStackTrace();
            }
        } else {
            log("File " + file + " does not exist.", Project.MSG_ERR);
        }
    }


    /**
     * Load the specified file using the classloader and add the file to the
     * list of resource dependencies.
     *
     * @param   file   Resource to be loaded by the classloader
     * @param   source Original source file that contained the import statement
     */
    private void loadResource(File file, File source) {
        String resourceName = file.getPath();

        // Make sure the resource name has the slashes in the way that java expects
        resourceName = resourceName.replace(File.separatorChar, '/');

        // Locate the class being imported
        ClassLoader loader = (classpath != null) ? 
            getProject().createClassLoader(classpath) : 
            DependencyListTask.class.getClassLoader();

        try {
            Enumeration<URL> resources = loader.getResources(resourceName);
            URL prev = null;
            URL current = null;
            while (resources.hasMoreElements()) {
                current = (URL) resources.nextElement();
                if (allowResource(current)) {
                    processResource(current, source, prev);
                    prev = current;
                } else {
                    log("Excluding resource: " + current, Project.MSG_DEBUG);
                }
            }
        } catch (IOException ioex) {
            log("Unable to load resource: " + resourceName + ", " + ioex);
        }


    }


    /**
     * Locate the resource and determine all of the possible locations where 
     * the resource might be loaded from.
     */
    public void processResource(URL resource, File source, URL prev) {
        // Use the Jar name only when restricted to Jars
        String resourceMatch = resource.toString();
        String fallback = null;
        if (prev != null) {
            fallback = prev.toString();
        }

        String jarPrefix = "jar:file:";
        if (restrictToJars) {
            // Clean up the resource name
            if (resourceMatch.startsWith(jarPrefix)) {
                int beginIdx = jarPrefix.length();
                int endIdx = resourceMatch.indexOf("!");
                if (endIdx > 0) {
                    resourceMatch = resourceMatch.substring(beginIdx, endIdx);
                }
            }

            // Clean up the fallback name
            if ((fallback != null) && fallback.startsWith(jarPrefix)) {
                int beginIdx = jarPrefix.length();
                int endIdx = fallback.indexOf("!");
                if (endIdx > 0) {
                    fallback = fallback.substring(beginIdx, endIdx);
                }
            }

        }

        // Record the resource
        MatchingResource match = null;
        if (dependencies.containsKey(resourceMatch)) {
            match = (MatchingResource) dependencies.get(resourceMatch);
            match.addSource(source);
            if (fallback != null) {
                match.addFallback(fallback);
            }
        } else {
            match = new MatchingResource();
            match.addSource(source);
            dependencies.put(resourceMatch, match);
        }
    }

    /**
     *  Entry point for starting this task from the command line. 
     *
     * @param  args commandline arguments
     */
    public static void main(String[] args) {
        try {
            DependencyListTask task = new DependencyListTask();
            task.execute();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }



    // ========================================================================
    // Inner static classes
    // ========================================================================

    /**
     * Used to keep track of matching resources.
     */
    public static class MatchingResource {
        /** Resource that is required by an import statement */
        private URL resource;

        /** List of source files that import the resource */
        private HashSet<File> sources = new HashSet<File>();

        /** Resources that would be loaded if the current resource is removed */
        private HashSet<String> fallback = new HashSet<String>();

        public void setResource(URL resource) {
            this.resource = resource;
        }
        public URL getResource() {
            return resource;
        }

        public void addFallback(String fallback) {
            this.fallback.add(fallback);
        }

        /**
         * Returns a collection of URLs that would be used if the current
         * resource were removed.
         */
        public Collection<String> getFallback() {
            return fallback;
        }

        public void addSource(File source) {
            sources.add(source);
        }

        /**
         * Returns a collection of files that import the resource.
         */
        public Collection<File> getAllSources() {
            return sources;
        }
    }


    /**
     * Used to track resources being included or excluded.
     */
    public static class FileResource {
        /** The resource path */
        private String path;

        /**
         * Set the path of the resource
         *
         * @param path the resource path.
         */
        public void setPath(String path) {
            this.path = path.trim();
        }

        /**
         * Get the resource path.
         *
         * @return the resource's path.
         */
        public String getPath() {
            return path;
        }

        /**
         * @see java.lang.Object#toString
         */
        public String toString() {
            return getPath();
        }


    }


    /**
     * Used to track info about the packages to be processed.
     */
    public static class PackageName {
        /** The package name */
        private String name;

        /**
         * Set the name of the package
         *
         * @param name the package name.
         */
        public void setName(String name) {
            this.name = name.trim();
        }

        /**
         * Get the package name.
         *
         * @return the package's name.
         */
        public String getName() {
            return name;
        }

        /**
         * @see java.lang.Object#toString
         */
        public String toString() {
            return getName();
        }

        /**
         * Determine if the Package Name matches the specified pattern.
         * The pattern may be an explicit package name or it may contain
         * wildcard characters.
         */
        public boolean matches(String pattern) {
            boolean matches = false;

            String prefix = null;
            int wildcardIdx = pattern.indexOf("*");
            if (wildcardIdx > 0) {
                prefix = pattern.substring(0, wildcardIdx);
            } else {
                prefix = pattern;
            }
            matches = name.startsWith(prefix);

            return matches;
        }
    }



}
