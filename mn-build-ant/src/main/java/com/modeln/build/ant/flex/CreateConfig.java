/*
 * Copyright 2000-2008 by Model N, Inc.  All Rights Reserved.
 *
 * This software is the confidential and proprietary information
 * of Model N, Inc ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you
 * entered into with Model N, Inc.
 */
package com.modeln.build.ant.flex;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.AbstractFileSet;
import org.apache.tools.ant.types.DirSet;
import org.apache.tools.ant.types.FileSet;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Generate a configuration file used when invoking the flex compiler. 
 *
 * @author Shawn Stafford
 */
public final class CreateConfig extends Task {

    /** Determine whether any path or file references in the config file should be absolute or relative */
    protected boolean useAbsolutePath = false;

    /** Compiler configuration file to create */
    protected File configfile = null;

    /** Base compiler configuration file inherit from */
    protected File basefile = null;

    /** XML representation of the configuration file */
    protected Document config = null;


    /** A list of FileSets that refer to resource bundle property files */
    protected Vector<FileSet> resourceBundles = new Vector<FileSet>();

    /** A list of external libraries to include */
    protected Vector<AbstractFileSet> externalLibraryPath = new Vector<AbstractFileSet>();

    /** A list of library path directories */
    protected Vector<DirSet> libraryPath = new Vector<DirSet>();

    /** A list of runtime library path files */
    protected Vector<FileSet> runtimeLibraryPath = new Vector<FileSet>();


    /**
     * Use absolute paths in the generated configuration file.
     *
     * @param 
     */
    public void setUseAbsolutePath(Boolean enable) {
        useAbsolutePath = enable.booleanValue();
    }

    /**
     * Set the name of the configuration file to generate.
     *
     * @param   file    Configuration file
     */
    public void setFile(File file) {
        configfile = file;
    }

    /**
     * Set the name of the base configuration file that the configration 
     * should inherit from.
     *
     * @param   file    Configuration file
     */
    public void setBaseFile(File file) {
        basefile = file;
    }


    /**
     * List of resource bundle files to include in the config file.
     *
     * @param set List of files
     */
    public void addResourceBundles(FileSet set) {
        resourceBundles.addElement(set);
    }

    /**
     * List of library files or directories to include in the config file.
     *
     * @param set List of files or directories
     */
    public void addExternalLibraryPath(FileSet set) {
        externalLibraryPath.addElement(set);
    }

    /**
     * List of library files or directories to include in the config file.
     *
     * @param set List of files or directories
     */
    public void addRuntimeLibraryPath(FileSet set) {
        runtimeLibraryPath.addElement(set);
    }

    /**
     * List of library files or directories to include in the config file.
     *
     * @param set List of files or directories
     */
    public void addLibraryPath(DirSet set) {
        libraryPath.addElement(set);
    }



    /**
     * Execute the task to generate the compiler config file.
     */
    public void execute() {
        // Construct an XML merge object for generating the final config data
        XmlMergeUtil xmlUtil = new XmlMergeUtil(XmlMergeUtil.MERGE, XmlMergeUtil.OVERWRITE);

        // Construct a configuration data object from the user data
        Document userconfig = null;
        try {
            userconfig = createConfig(); 
        } catch (ParserConfigurationException pex) {
            pex.printStackTrace();
        }

        // Merge the config values with a base config file if specified
        if (basefile != null) {
            try {
                Document baseconfig = xmlUtil.parse(basefile);
                //xmlUtil.print(baseconfig);
                if ((userconfig != null) && (baseconfig != null)) {
                    config = xmlUtil.merge(baseconfig, userconfig);
                } else if (userconfig != null) {
                    config = userconfig;
                } else if (baseconfig != null) {
                    config = baseconfig;
                } else {
                    System.out.println("Unable to construct a configuration file.");
                }
            } catch (Exception ex) {
                System.err.println("Unable to parse configuration file: " + basefile);
                ex.printStackTrace();
            } 
        } else {
            config = userconfig;
        }

        // Write the configuration to a file
        try {
            //xmlUtil.print(config);
            xmlUtil.write(config, configfile);
        } catch (IOException ioex) {
            throw new BuildException("Unable to generate the configuration file: " + configfile);
        }

    }

    /**
     * Create a document element from the file set entries.  Every FileSet item
     * will be represented as a path-element tag within the newly constructed 
     * element.
     *
     * @param  doc      XML document
     * @param  tagname  Name of the XML tag to be created
     * @param  filesets Vector of FileSet objects to be converted to path-element child tags
     */
    private Element createPathElement(Document doc, String tagname, Vector<? extends AbstractFileSet> filesets) {
        Element pathNode = doc.createElement(tagname);
        AbstractFileSet currentSet = null;
        for (int idx = 0; idx < filesets.size(); idx++) {
            currentSet = (AbstractFileSet) filesets.get(idx);
            try {
                Vector<String> files = getFilenames(currentSet);

                // Create a new element for each file
                for (int idxFile = 0; idxFile < files.size(); idxFile++) {
                    Element currentPathElement = doc.createElement("path-element");
                    pathNode.appendChild(currentPathElement);
                    currentPathElement.setTextContent((String) files.get(idxFile));
                }

                if (files.size() < 1) {
                    System.out.println("No files found for the " + tagname + " fileset: dir=" + currentSet.getDir(getProject()));
                }
            } catch (IOException ioex) {
                System.out.println("Unable to process fileset: " + currentSet.getDir(getProject()));
            }

        }

        return pathNode;
    } 


    /**
     * Create an empty configuration.
     */
    private Document createConfig() throws ParserConfigurationException {
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

        // Create the root node
        Element rootNode = doc.createElement("flex-config");
        doc.appendChild(rootNode);

        // Create the compiler node
        Element compilerNode = doc.createElement("compiler");
        rootNode.appendChild(compilerNode);

        // Generate the list of external library path entries
        if (externalLibraryPath.size() > 0) {
            Element extLibPathNode = createPathElement(doc, "external-library-path", externalLibraryPath);
            compilerNode.appendChild(extLibPathNode);
        }

        // Generate the list of library path entries
        if (libraryPath.size() > 0) {
            Element libPathNode = createPathElement(doc, "library-path", libraryPath);
            compilerNode.appendChild(libPathNode);
        }

        // Generate a list of library path entries
        if (runtimeLibraryPath.size() > 0) {
            Element runtimeLibPathNode = createPathElement(doc, "runtime-shared-library-path", runtimeLibraryPath);
            rootNode.appendChild(runtimeLibPathNode);
        }

        // Generate the list of resource bundles
        if (resourceBundles.size() > 0) {
            // Locate the resource bundles on the file system
            Vector<String> bundleList = null;
            try {
                bundleList = getResourceBundleNames();
            } catch (IOException ioex) {
                ioex.printStackTrace();
            }

            // Add the resource bundles to the DOM
            if ((bundleList != null) && (bundleList.size() > 0)) {
                Element resourceBundleNode = doc.createElement("include-resource-bundles");
                rootNode.appendChild(resourceBundleNode);
                for (int idx = 0; idx < bundleList.size(); idx++) {
                    String currentBundle = (String) bundleList.get(idx);
                    if ((currentBundle != null) && (currentBundle.length() > 0)) {
                        Element currentBundleNode = doc.createElement("bundle");
                        resourceBundleNode.appendChild(currentBundleNode);
                        currentBundleNode.setTextContent(currentBundle);
                    }
                }
            }
        }


        return doc;
    }

    /**
     * Obtain a list of files in the fileset.
     *
     * @return Vector list of file names
     */
    private Vector<String> getFilenames(AbstractFileSet fileset) throws IOException {
        Vector<String> files = new Vector<String>();

        DirectoryScanner scanner = fileset.getDirectoryScanner(getProject());
        File basedir = scanner.getBasedir();
        String dirname = basedir.getAbsolutePath();

        // Iterate through each item in the fileset
        String[] filelist = null;
        if (fileset instanceof FileSet) {
            filelist = scanner.getIncludedFiles();
        } else if (fileset instanceof DirSet) {
            filelist = scanner.getIncludedDirectories();
        } else {
            System.out.println("Unable to determine the type of fileset: " + dirname);
            return files;
        }

        for (int idx = 0; idx < filelist.length; idx++) {
            String filename = filelist[idx];

            // Make the file path relative to the fileset directory if necessary 
            if ((filename != null) && (dirname != null) && (dirname.length() > 0)) {
                if (!useAbsolutePath && filename.startsWith(dirname)) {
                    // Remove the directory prefix
                    filename = filename.substring(dirname.length());
                } else if (useAbsolutePath && !filename.startsWith(dirname)) {
                    // Append the directory prefix
                    filename = dirname + File.separator + filename;
                }
            }

            // Add the filename to the list
            if (filename != null) {
                //System.out.println(filename);
                files.add(filename);
            }
        }

        return files;
    }


    /**
     * Obtain the resource bundle names using the list of files in the fileset. 
     * 
     * @return Vector list of resource bundle names
     */
    private Vector<String> getResourceBundleNames() throws IOException {
        // Iterate through each fileset to build the list of resource bundles
        Vector<String> files = new Vector<String>();
        for (int idx = 0; idx < resourceBundles.size(); idx++) {
            FileSet current = (FileSet) resourceBundles.get(idx);
            DirectoryScanner scanner = current.getDirectoryScanner(getProject());
            File basedir = scanner.getBasedir();
            String dirname = basedir.getAbsolutePath();

            // Iterate through each file in the fileset
            String[] includes = scanner.getIncludedFiles();
            for (int idxFile = 0; idxFile < includes.length; idxFile++) {
                // Ensure that the resource bundle is relative to the fileset directory
                String filename = includes[idxFile];
                if ((dirname != null) && (dirname.length() > 0) && (filename.startsWith(dirname))) {
                    filename = filename.substring(dirname.length());
                }

                // Remove the file extension
                String ext = ".properties";
                if (filename.endsWith(ext)) {
                    filename = filename.substring(0, filename.length() - ext.length());
                }

                // Add the converted file to the list of components
                files.add(filename);
            }
        }

        return files;
    }


}

