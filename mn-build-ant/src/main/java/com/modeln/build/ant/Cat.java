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

import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.IOException;

/**
 * Echo the contents of a file to stdout.
 */
public final class Cat extends Task {

    /** Output stream */
    private OutputStream output = System.out;

    /** File containing the text to be echoed */
    private File inputfile;


    /**
     * File containing the content to be echoed to the output stream. 
     *
     * @param   file    File containing the text to be echoed 
     */
    public void setFile(File file) {
        inputfile = file;
    }


    /**
     * Echo the file contents to the output stream. 
     */
    public void execute() throws BuildException {
        if ((inputfile != null) && (inputfile.canRead())) {
            // Read the contents of the input file and echo to the output stream
            FileInputStream inputstream = null;
            try {
                inputstream = new FileInputStream(inputfile);
                int data = inputstream.read();
                while (data >= 0) {
                    output.write(data); 
                    data = inputstream.read();
                }
            } catch (IOException ioex) {
                throw new BuildException("Unable to read from file: " + inputfile.getAbsolutePath());
            } finally {
                if (inputstream != null) {
                    // Hopefully the condition never occurs where we cannot close the file 
                    try {
                        inputstream.close();
                    } catch (IOException cex) {
                        cex.printStackTrace();
                    }
                }
            }
        } else {
            throw new BuildException("Unable to read file: " + inputfile);
        }
    }


}

