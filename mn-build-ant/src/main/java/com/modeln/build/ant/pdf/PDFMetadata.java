/*
 * Copyright 2000-2003 by Model N, Inc.  All Rights Reserved.
 * 
 * This software is the confidential and proprietary information
 * of Model N, Inc ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you
 * entered into with Model N, Inc.
 */
package com.modeln.build.ant.pdf;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Set;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildEvent;

import com.itextpdf.text.pdf.PdfReader;


/**
 * Reads PDF metadata from the file and save the values in properties. 
 *
 * @author Shawn Stafford
 */
public final class PDFMetadata extends Task {


    /** Name of the PDF file */
    private String filename = null;

    /** Define a prefix string to use when setting metadata properties */
    private String prefix = "";

    /**
     * Set the name of the PDF file where the metadata resides. 
     *
     * @param   name    PDF file name 
     */
    public void setFile(String name) {
        filename = name;
    }

    /**
     * Set the prefix for any PDF metadata properties.
     *
     * @param   str     Property prefix
     */
    public void setPrefix(String str) {
        prefix = str;
    }


    /**
     * Perform the read operation on the PDF metadata. 
     */
    public void execute() throws BuildException {
        try {
            PdfReader pdf = new PdfReader(filename);
            int pagecount = pdf.getNumberOfPages();
            System.out.println("Number of pages: " + pagecount);
            getProject().setProperty(prefix + "PageCount", Integer.toString(pagecount));

            HashMap map = pdf.getInfo(); 
            Set keys = map.keySet();
            Iterator iter = keys.iterator();
            while (iter.hasNext()) {
                Object key = iter.next();
                Object value = map.get(key);
                System.out.println("Found metadata field: " + prefix + key + " = " + value);
                getProject().setProperty(prefix + key, (String) value);
            } 
        } catch (IOException ioex) {
            System.out.println("Unable to open PDF: " + filename); 
        } 
    }



}
