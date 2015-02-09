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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.RegularExpression;
import org.apache.tools.ant.util.regexp.Regexp;

/**
 * Performs regular expression search for matching text of a given file.
 * The matching text is written to the given file.
 *
 * The syntax of the regular expression depends on the implemtation that
 * you choose to use. The system property <code>ant.regexp.regexpimpl</code>
 * will be the classname of the implementation that will be used (the default
 * is <code>org.apache.tools.ant.util.regexp.JakartaOroRegexp</code> and
 * requires the Jakarta Oro Package).
 *
 * <pre>
 * For jdk  &lt;= 1.3, there are two available implementations:
 *   org.apache.tools.ant.util.regexp.JakartaOroRegexp (the default)
 *        Requires  the jakarta-oro package
 *
 *   org.apache.tools.ant.util.regexp.JakartaRegexpRegexp
 *        Requires the jakarta-regexp package
 *
 * For jdk &gt;= 1.4 an additional implementation is available:
 *   org.apache.tools.ant.util.regexp.Jdk14RegexpRegexp
 *        Requires the jdk 1.4 built in regular expression package.
 *
 * Usage:
 *
 *   Call Syntax:
 *
 *     &lt;replaceregexp file="file"
 *                    match="pattern" 
 *                    flags="options"? &gt;
 *       regexp?
 *       fileset*
 *     &lt;/replaceregexp&gt;
 *
 *    NOTE: You must have either the file attribute specified, or at least one fileset subelement
 *    to operation on.  You may not have the file attribute specified if you nest fileset elements
 *    inside this task.  Also, you cannot specify both match and a regular expression subelement at
 *    the same time, nor can you specify the replace attribute and the substitution subelement at
 *    the same time.
 *
 *   Attributes:
 *
 *     file    --&gt; A single file to operation on (mutually exclusive with the fileset subelements)
 *     match   --&gt; The Regular expression to match 
 *     flags   --&gt; The options to give to the replacement 
 *                 i = Case insensitive match
 *
 *  Example:
 *
 *     The following call could be used to obtain all instances of the matching lines 
 *     and place them in a new file.  
 *
 *     &lt;replaceregexp file="test.properties" match="MyProperty=(.*)"/&gt;
 *
 * </pre>
 *
 */
public class SelectRegExp extends Task {

    private File file;
    private File outfile;
    private String flags;

    private Vector<FileSet> filesets;// Keep jdk 1.1 compliant so others can use this
    private Vector<Regexp> regexList;

    /** Default Constructor  */
    public SelectRegExp() {
        super();
        this.file = null;
        this.filesets = new Vector<FileSet>();
        this.regexList = new Vector<Regexp>();
        this.flags = "";
    }


    /**
     * file from which the regular expression should be selected;
     * required unless a nested fileset is supplied.
     */
    public void setFile(File file) {
        this.file = file;
    }

    /**
     * File where the matching text will be appended.
     */
    public void setOutput(File file) {
        this.outfile = file;
    }

    /**
     * the regular expression pattern to match in the file(s);
     * required if no nested &lt;regexp&gt; is used
     */
    public void setMatch(String match) {
        RegularExpression regex = new RegularExpression();
        regex.setPattern(match);
        addConfiguredRegexp(regex);
    }



    /**
     * The flags to use when matching the regular expression.  For more
     * information, consult the Perl5 syntax.
     * <ul>
     *  <li>g : Global replacement.  Replace all occurences found
     *  <li>i : Case Insensitive.  Do not consider case in the match
     *  <li>m : Multiline.  Treat the string as multiple lines of input, 
     *         using "^" and "$" as the start or end of any line, respectively, rather than start or end of string.
     *  <li> s : Singleline.  Treat the string as a single line of input, using
     *        "." to match any character, including a newline, which normally, it would not match.
     *</ul>
     */                     
    public void setFlags(String flags) {
        this.flags = flags;
    }



    /**
     * list files to apply the replacement to
     */
    public void addFileset(FileSet set) {
        filesets.addElement(set);
    }



    /**
     * Adds a set of files to count.
     */
    @SuppressWarnings("deprecation")
	public void addConfiguredRegexp(RegularExpression regex) {
        Regexp regexp = regex.getRegexp(project);
        regexList.add(regexp);
    }


    /** Perform the replace on the entire file  */
    protected void doSelect(File f, int options) throws IOException {
        log("Performing regular expression matching...", Project.MSG_VERBOSE);

        FileReader r = null;
        FileWriter w = null;

        try {
            r = new FileReader(f);
            w = new FileWriter(outfile, true);

            BufferedReader br = new BufferedReader(r);
            BufferedWriter bw = new BufferedWriter(w);
            PrintWriter pw = new PrintWriter(bw);

            String line = null;
            while ((line = br.readLine()) != null) {
                line = getMatch(line, options);
                if (line != null) {
                    pw.println(line);
                    pw.flush();
                }
            }

            r.close();
            r = null;
            w.close();
            w = null;

        } finally {
            try {
                if (r != null) {
                    r.close();
                }
            } catch (Exception e) {
            }

            try {
                if (w != null) {
                    w.close();
                }
            } catch (Exception e) {
            }
        }
    }

    /** 
     * Return the full match to the regular expression.
     *
     * @param   line    String to be examined
     * @param   opts    Regular expression flages
     */
    @SuppressWarnings("unchecked")
	private String getMatch(String line, int opts) {
        Vector<String> matches = null;

        // Compare the line to each regular expression
        Regexp current = null;
        for (int idx = 0; idx < regexList.size(); idx++) {
            current = (Regexp) regexList.get(idx);
            matches = current.getGroups(line, opts);
            if ((matches != null) && (matches.size() > 0)) {
                // log("Match found: " + matches.get(0));
                return (String) matches.get(0);
            }
        }

        // log("Line discarded: " + line, Project.MSG_VERBOSE);
        return null;
    }


    public void execute() throws BuildException {
        if (regexList.size() == 0) {
            throw new BuildException("No expression to match.");
        }

        if (file != null && filesets.size() > 0) {
            throw new BuildException("You cannot supply the 'file' attribute "
                                     + "and filesets at the same time.");
        }

        int options = 0;

        if (flags.indexOf('g') != -1) {
            options |= Regexp.REPLACE_ALL;
        }

        if (flags.indexOf('i') != -1) {
            options |= Regexp.MATCH_CASE_INSENSITIVE;
        }

        if (flags.indexOf('m') != -1) {
            options |= Regexp.MATCH_MULTILINE;
        }

        if (flags.indexOf('s') != -1) {
            options |= Regexp.MATCH_SINGLELINE;
        }

        if (file != null && file.exists()) {
            try {
                doSelect(file, options);
            } catch (IOException e) {
                log("An error occurred processing file: '" 
                    + file.getAbsolutePath() + "': " + e.toString(),
                    Project.MSG_ERR);
            }
        } else if (file != null) {
            log("The following file is missing: '" 
                + file.getAbsolutePath() + "'", Project.MSG_ERR);
        }

        int sz = filesets.size();

        for (int i = 0; i < sz; i++) {
            FileSet fs = (FileSet) (filesets.elementAt(i));
            DirectoryScanner ds = fs.getDirectoryScanner(getProject());

            String files[] = ds.getIncludedFiles();

            for (int j = 0; j < files.length; j++) {
                File f = new File(fs.getDir(getProject()), files[j]);

                if (f.exists()) {
                    try {
                        doSelect(f, options);
                    } catch (Exception e) {
                        log("An error occurred processing file: '" 
                            + f.getAbsolutePath() + "': " + e.toString(),
                            Project.MSG_ERR);
                    }
                } else {
                    log("The following file is missing: '" 
                        + f.getAbsolutePath() + "'", Project.MSG_ERR);
                }
            }
        }
    }

}


