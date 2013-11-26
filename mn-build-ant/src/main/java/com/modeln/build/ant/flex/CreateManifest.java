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


import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;


/**
 * Generate a manifest file from the list of files provided. 
 *
 * @author Shawn Stafford
 */
public final class CreateManifest extends Task {

    /** The fileset is used to specify the list of files to count */
    protected Vector filesets = new Vector();

    /** Manifest file to create */
    protected File manifest = null;

    /** Comma-delimited list of file extensions */
    protected String extensions = "as,mxml"; 

    /**
     * List of files to include in the manifest.
     *
     * @param set List of files
     */
    public void addFileset(FileSet set) {
        filesets.addElement(set);
    }

    /**
     * Set the name of the manifest file. 
     *
     * @param   file    Manifest file to generate 
     */
    public void setManifest(File file) {
        manifest = file;
    }

    /**
     * Set the list of file extensions that should be stripped off when
     * generating the manifest file from a list of files.
     *
     * @param   ext   Comma-delimited list of file extensions
     */
    public void setExtensions(String ext) {
        extensions = ext;
    }


    /**
     * Execute the task to generate the manifest file.
     */
    public void execute() {
        // Iterate through each fileset to build the list of files
        Vector files = new Vector();
        for (int idx = 0; idx < filesets.size(); idx++) {
            FileSet current = (FileSet) filesets.get(idx);
            DirectoryScanner scanner = current.getDirectoryScanner(getProject());
            File basedir = scanner.getBasedir();
            String dirname = basedir.getAbsolutePath();

            // Iterate through each file in the fileset
            String[] includes = scanner.getIncludedFiles();
            for (int idxFile = 0; idxFile < includes.length; idxFile++) {
                // Ensure that the manifest is relative to the fileset directory
                String filename = includes[idxFile];
                if ((dirname != null) && (dirname.length() > 0) && (filename.startsWith(dirname))) {
                    filename = filename.substring(dirname.length());
                }

                // Remove the file extension
                StringTokenizer st = new StringTokenizer(extensions, ",");
                while (st.hasMoreTokens()) {
                    String ext = "." + st.nextToken().trim();
                    if (filename.endsWith(ext)) {
                        filename = filename.substring(0, filename.length() - ext.length());
                    }
                }
                
                // Add the converted file to the list of components
                files.add(filename);
            }
        }

        try {
            if (files.size() > 0) {
                generateManifest(files);
            } else {
                System.out.println("No files found.  Skipping manifest creation.");
            }
        } catch (IOException ioex) {
            throw new BuildException("Unable to generate the manifest file: " + manifest);
        }

    }


    /**
     * Generate a manifest file containing the list of files.
     *
     * @param files  List of files
     */
    public void generateManifest(Vector files) throws IOException {
        FileWriter writer = null;
        try {
            writer = new FileWriter(manifest);
            writer.write("<?xml version=\"1.0\"?>\n");
            writer.write("<componentPackage>\n");
            for (Enumeration list = files.elements(); list.hasMoreElements(); ) {
                String currentFile = (String) list.nextElement();

                // Ensure that we replace the OS file separator character with forward slashes
                if (File.separatorChar == '\\') {
                    currentFile = currentFile.replace('\\', '/');
                }

                // Convert the file name to a class name
                currentFile = currentFile.replace('/', '.');

                // Write the component name to the file
                writer.write("  <component class=\"" + currentFile + "\"/>\n");
            }
            writer.write("</componentPackage>\n");
            System.out.println("Manifest written to " + manifest);
        } finally {
            if (writer != null) writer.close();
        }

    }
}

