/*
 * Copyright 2000-2003 by Model N, Inc.  All Rights Reserved.
 *
 * This software is the confidential and proprietary information
 * of Model N, Inc ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you
 * entered into with Model N, Inc.
 */
package com.modeln.build.ant.diff;

import java.io.BufferedReader;
import java.io.File;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import java.util.List;
import java.util.ArrayList;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.types.PatternSet;
import org.apache.tools.ant.types.selectors.SelectorUtils;


/**
 * Generates a report of all the differences between the contents of two 
 * directories or Jar files.  The task examines each file in the source
 * and destination directory to determine whether it has been added, 
 * deleted, or modified.  If a directory has been specified, the task
 * recursively examines every file under that directory.  If the file
 * is a Jar file, the task examines every file in the Jar file. 
 *
 * @author Shawn Stafford
 */
public final class DiffReport extends Task {

private int modified_to_unchanged=0;

    private static final String STATUS_UNCHANGED = "unchanged";

    private static final String STATUS_ADDED = "added";

    private static final String STATUS_DELETED = "deleted";

    private static final String STATUS_MODIFIED = "modified";

    /** Subdirectory relative to the report directory where old versions of files are stored */
    private static final String SUBDIR_OLD = "old";

    /** Subdirectory relative to the report directory where new versions of files are stored */
    private static final String SUBDIR_NEW = "new";

    /** Default propfile name */
    private static final String DEFAULT_PROPFILE_NAME = "summary.properties";

    private static final int COUNT_TOTAL= 0;
    private static final int COUNT_UNCHANGED= 1;
    private static final int COUNT_ADDED= 2;
    private static final int COUNT_DELETED= 3;
    private static final int COUNT_MODIFIED= 4;

    /** List of file patterns that should be included or excluded from processing */
    private Vector patternsets = new Vector();

    /** Name of the directory where the report files will be generated */
    private File reportDir = null;

    /** Name of the properties file where the file count summary will be generated */
    private File propfile = null;

    /** Name of the directory where the original files will be saved */
    private File oldDir = null;

    /** Name of the directory where the new files will be saved */
    private File newDir = null;

    /** Name of the original Jar file */
    private File oldFile = null;

    /** Name of the updated Jar file */
    private File newFile = null;

    /** Executable for disassembling Java class files */
    private File disassembler = null;

    /** Determines whether the list of files includes unchanged files */
    private boolean showUnchanged = false;

    /** Determines whether to display the link to the textual diffs */
    private boolean showDiffs = true;

    /** Determines whether the list of files includes added files */
    private boolean showAdded = true;

    /** Determines whether the list of files includes deleted files */
    private boolean showDeleted = true;

    /** Determines whether the list of files includes modified files */
    private boolean showModified = true;

    /** Determines whether the Java class diff includes bytecode */
    private boolean showBytecode = false;

    /** Determines whether verbose output should be generated during execution */
    private boolean verbose = false;

    /** Set the maximum number of lines that a file can contain */
    private int maxLines = 10000;

    /** Flag if exceeding the maxLines   Flag is reset for every iteration of the diff loop  */
    private boolean exceedMaxLines = false;

    /** Save a copy of any modified files for reference */
    private boolean saveModified = false;

    /** Save a copy of any new files for reference */
    private boolean saveAdded = false;

    /** Save a copy of any deleted files for reference */
    private boolean saveDeleted = false;

    /** Save a copy of any unchanged files for reference */
    private boolean saveUnchanged = false;

    /** Determines if always generate index.html even when there are no entries to display */
    private boolean alwaysReport = true; 

    /** Save the status summary of the Jar file analysis */ 
    private String _statusSummary = null;
    private List _statusCounts = new ArrayList();

    /**
     * Set the maximum number of lines that a file can contain if processed
     * a textual diff.
     *
     * @param   max   Maximum number of lines
     */
    public void setMaxLines(Integer max) {
        maxLines = max.intValue();
    }

    /**
     * Enable or disable verbose output of status messages during execution. 
     *
     * @param   enabled   True if verbose output should be enabled 
     */
    public void setVerbose(Boolean enabled) {
        verbose = enabled.booleanValue();
    }

    /**
     * Enable or disable the display of unchanged files. 
     *
     * @param   enabled   True if unchanged files should be listed 
     */
    public void setShowUnchanged(Boolean enabled) {
        showUnchanged = enabled.booleanValue();
    }

    /**
     * Enable or disable the link to the diff page.
     *
     * @param   enabled   True if links to diff should be shown
     */
    public void setShowDiffs(Boolean enabled) {
        showDiffs = enabled.booleanValue();
    }

    /**
     * Determines if always generate index.html even when there are no entries to display 
     *
     * @param   enabled   True if should always generate the index.html report page 
     */
    public void setAlwaysReport(Boolean enabled) {
        alwaysReport = enabled.booleanValue();
    }

    /**
     * Enable or disable the display of added files.
     *
     * @param   enabled   True if added files should be listed
     */
    public void setShowAdded(Boolean enabled) {
        showAdded = enabled.booleanValue();
    }

    /**
     * Enable or disable the display of deleted files.
     *
     * @param   enabled   True if added files should be listed
     */
    public void setShowDeleted(Boolean enabled) {
        showDeleted = enabled.booleanValue();
    }

    /**
     * Enable or disable the display of modified files.
     *
     * @param   enabled   True if modified files should be listed
     */
    public void setShowModified(Boolean enabled) {
        showModified = enabled.booleanValue();
    }

    /**
     * Enable or disable the display of bytecode when comparing java classes. 
     *
     * @param   enabled   True if modified files should be listed
     */
    public void setShowBytecode(Boolean enabled) {
        showBytecode = enabled.booleanValue();
    }


    /**
     * Save a copy of the modified files for reference. 
     *
     * @param   enabled   True if modified files should be saved locally 
     */
    public void setSaveModified(Boolean enabled) {
        saveModified = enabled.booleanValue();
    }

    /**
     * Save a copy of the new files for reference.
     *
     * @param   enabled   True if new files should be saved locally
     */
    public void setSaveAdded(Boolean enabled) {
        saveAdded = enabled.booleanValue();
    }

    /**
     * Save a copy of the deleted files for reference.
     *
     * @param   enabled   True if deleted files should be saved locally
     */
    public void setSaveDeleted(Boolean enabled) {
        saveDeleted = enabled.booleanValue();
    }

    /**
     * Save a copy of the unchanged files for reference.
     *
     * @param   enabled   True if unchanged files should be saved locally
     */
    public void setSaveUnchanged(Boolean enabled) {
        saveUnchanged = enabled.booleanValue();
    }



    /**
     * Set the name of the original File or directory being examined. 
     *
     * @param   file    Name of the original file or directory
     */
    public void setOld(File file) {
        oldFile = file;
    }

    /**
     * Set the name of the updated File or directory being examined.
     *
     * @param   file    Name of the updated file or directory
     */
    public void setNew(File file) {
        newFile = file;
    }

    /**
     * Set the directory where the reports should be generated. 
     *
     * @param   dir    Directory where reports will be written 
     */
    public void setReportDir(File dir) {
        reportDir = dir;
        oldDir = new File(dir.getPath() + File.separator + SUBDIR_OLD);
        newDir = new File(dir.getPath() + File.separator + SUBDIR_NEW);
    }

    /**
     * Set the properties file where the file count summary should be written. 
     *
     * @param   file    Properties file where file count summary will be written 
     */
    public void setPropfile(File file) {
        propfile = file;
        try {
            if ( ! propfile.getParentFile().exists()) propfile.getParentFile().mkdirs();
            if ( ! propfile.exists()) propfile.createNewFile();
            if ( propfile.isDirectory()) propfile=new File(file.getPath() + File.separator + DEFAULT_PROPFILE_NAME);
        } catch (Exception ex) {
            System.out.println("Exception while creating propfile: "+propfile.getPath());
            System.out.println("Will use default propfile instead: "+reportDir.getAbsolutePath()+File.separator+DEFAULT_PROPFILE_NAME);
            propfile = null; //set propfile in function execute()
        }
    }

    /**
     * Set the executable used to disassembler Java class files. 
     *
     * @param   exe    Executable used to disassembler Java class files 
     */
    public void setDisassembler(File exe) {
        disassembler = exe;
    }

    /**
     * Add a patternset
     */
    public void addPatternset(PatternSet set) {
        patternsets.addElement(set);
    }


    /**
     * Perform the read operation on the PDF metadata.
     */
    public void execute() throws BuildException {
        // Make sure the disassembler settings are valid
        if ((disassembler != null) && (!disassembler.canRead())) {
            throw new BuildException(
                "Unable to access dissassembler binary: " + disassembler.getAbsolutePath());
        }
        if (showBytecode && (disassembler == null)) {
            throw new BuildException(
                "The 'disassembler' attribute must be specified when 'showBytecode' is enabled."); 
        }

        // Make sure the report directory if valid
        if (reportDir != null) {
            if  (!reportDir.isDirectory()) {
                throw new BuildException(
                    "The 'reportDir' attribute must refer to an existing directory: " + reportDir.getAbsolutePath());
            } else if (!reportDir.canWrite()) {
                throw new BuildException(
                    "Unable to write to the report directory: " + reportDir.getAbsolutePath());
            }
        }

        // Set propfile
        if (propfile == null) {
            propfile = new File(reportDir.getAbsolutePath() + File.separator + DEFAULT_PROPFILE_NAME);
        }

        // Make sure the input files are valid
        if (oldFile == null) {
            throw new BuildException(
                "An 'old' attribute must be specified indicating the original Jar file to be analyzed."); 
        } else if ((!oldFile.isFile()) || (!oldFile.canRead())) {
            throw new BuildException(
                "Unable to access the jar file specified by the 'old' attribute: " + oldFile.getAbsolutePath());
        }
        if (newFile == null) {
            throw new BuildException(
                "An 'new' attribute must be specified indicating the original Jar file to be analyzed.");
        } else if ((!oldFile.isFile()) || (!oldFile.canRead())) {
            throw new BuildException(
                "Unable to access the jar file specified by the 'new' attribute: " + newFile.getAbsolutePath());
        }


        try {
            Hashtable files = compareJars();
            if (reportDir != null) {
                writeStatusSummary(); 
                if (alwaysReport) {
                    generateHtmlReport(files);
                } else {
                    // only generate index.html when there are files to compare and when there are user-defined-type of entries to be displayed
                    if (( ! files.isEmpty()) && hasAnythingToShow(files)) {
                        generateHtmlReport(files);
                    }
                }
            } else {
                System.out.println("No 'reportDir' attribute specified.  Writing results to stdout.");
                generateTextReport(files);
            } 
        } catch (IOException ioex) {
            throw new BuildException("Unable to generate report: " + ioex.getMessage(), ioex);
        } catch (SecurityException sex) {
            throw new BuildException("Unable to access file: " + sex.getMessage(), sex);
        } catch (BuildException bex) {
            throw new BuildException(bex.getMessage(), bex);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new BuildException("Unknown error occurred during report generation.", ex);
        }
    }


    /**
     * Examine the contents of the two jar files and compare the files.
     * The method returns a hashtable containing the combined list of 
     * files and the status of each file indicating whether the file
     * is new, deleted, updated, or unchanged.
     *
     * @return Hashtable of files and their diff status
     */
    private Hashtable compareJars() throws BuildException {
    Hashtable allFiles = null;
        System.out.println("Comparing contents of " + oldFile.getName() + " with " + newFile.getName() + "...");
        try {
            JarFile oldJar = new JarFile(oldFile);
            JarFile newJar = new JarFile(newFile);

            // Get one big list of Jar file entries from both jars
            Enumeration oldFiles = oldJar.entries();
            Enumeration newFiles = newJar.entries();
            allFiles = new Hashtable();
            JarEntry currentEntry = null;
            String currentName = null;
            // First add all the old files and assume they've been deleted
            while (oldFiles.hasMoreElements()) {
                currentEntry = (JarEntry) oldFiles.nextElement();
                currentName = currentEntry.getName();
                if (!currentEntry.isDirectory() && includeFile(currentName)) {
                    allFiles.put(currentName, STATUS_DELETED);
                }
            }
            // Next add all the new files and indicate whether they existed before
            while (newFiles.hasMoreElements()) {
                currentEntry = (JarEntry) newFiles.nextElement();
                currentName = currentEntry.getName();
                if (!currentEntry.isDirectory() && includeFile(currentName)) {
                    if (allFiles.containsKey(currentName)) {
                        allFiles.put(currentName, STATUS_UNCHANGED);
                    } else {
                        allFiles.put(currentName, STATUS_ADDED);
                    }
                }
            }

            // Determine whether existing files have changed based on 
            // first the Jar CRC values and then the diff algorithm
            Enumeration keys = allFiles.keys();
            String currentKey = null;
            String currentValue = null;

            while (keys.hasMoreElements()) {
                currentKey = (String) keys.nextElement();
                currentValue = (String) allFiles.get(currentKey);
                if (currentValue == STATUS_UNCHANGED) {
                    long oldCrc = oldJar.getJarEntry(currentKey).getCrc();
                    long newCrc = newJar.getJarEntry(currentKey).getCrc();
                    if (oldCrc != newCrc) {
                        System.out.println("diffing: "+currentKey);
                        // determine if the file is modified using the diff algorithm
                           if (diffFile(currentKey, null)) {
                            allFiles.put(currentKey, STATUS_MODIFIED);
                        } else {
                            modified_to_unchanged++;
                            System.out.println("  modified_to_unchanged: "+Integer.toString(modified_to_unchanged)+"\n");
                        }
                    }
                }
            }
    
        } catch (IOException ioex) {
            System.out.println("Unable to open Jar file: " + ioex.getMessage());
        } catch (SecurityException sex) {
            System.out.println("Unable to open Jar file: " + sex.getMessage());
        }

        // Display a summary of the jar file analysis
        setStatusSummary(allFiles);
        System.out.println(_statusSummary);

        return allFiles;
    }

    /**
     * Write a summary of the Jar file analysis to reportDir
     */     
    private void writeStatusSummary() throws IOException {
        FileWriter writer = null;
        try {
            writer = new FileWriter(propfile);
            writer.write("count.total="+_statusCounts.get(COUNT_TOTAL)+"\n");
            writer.write("count.unchanged="+_statusCounts.get(COUNT_UNCHANGED)+"\n");
            writer.write("count.added="+_statusCounts.get(COUNT_ADDED)+"\n");
            writer.write("count.deleted="+_statusCounts.get(COUNT_DELETED)+"\n");
            writer.write("count.modified="+_statusCounts.get(COUNT_MODIFIED)+"\n");
        } catch (SecurityException sex) {
            System.out.println("Unable to access file: " + sex.getMessage());
        } finally {
            if (writer != null) writer.close();
        }
        return;
    }
    
    /**
     * Display a summary of the Jar file analysis.
     * 
     * @param  files  List of files analyzed
     * @return Summary information about the file analysis
     */
    private void setStatusSummary(Hashtable files) {
        setStatusCounts(files);
        _statusSummary = _statusCounts.get(COUNT_TOTAL)+ " files analyzed, " + _statusCounts.get(COUNT_MODIFIED) + " modified, " + _statusCounts.get(COUNT_ADDED) + " added, " + _statusCounts.get(COUNT_DELETED) + " deleted, " + _statusCounts.get(COUNT_UNCHANGED) + " unchanged";
    }
    private void setStatusCounts(Hashtable files) {
        // Summarize the results of the Jar analysis
        Collection allValues = files.values();
        Iterator valueIter = allValues.iterator();
        int total = 0;
        int unchanged = 0;
        int added = 0;
        int deleted = 0;
        int modified = 0;
        while (valueIter.hasNext()) {
            String status = (String) valueIter.next();
            total++;
            if (STATUS_UNCHANGED.equals(status)) {
                unchanged++;
            } else if (STATUS_ADDED.equals(status)) {
                added++;
            } else if (STATUS_DELETED.equals(status)) {
                deleted++;
            } else if (STATUS_MODIFIED.equals(status)) {
                modified++;
            }

        }
        _statusCounts.add(COUNT_TOTAL, Integer.toString(total));
        _statusCounts.add(COUNT_UNCHANGED, Integer.toString(unchanged));
        _statusCounts.add(COUNT_ADDED, Integer.toString(added));
        _statusCounts.add(COUNT_DELETED, Integer.toString(deleted));
        _statusCounts.add(COUNT_MODIFIED, Integer.toString(modified));
    }

    /**
     * Return a count of the number of entries which have the given status.
     *
     * @param   files   List of files analyzed
     * @param   status  Status to be tallied
     * @return  Number of files matching the given status
     */
    private int getStatusCount(Hashtable files, String status) {
        // Summarize the results of the Jar analysis
        Collection allValues = files.values();
        Iterator valueIter = allValues.iterator();
        int total = 0;
        while (valueIter.hasNext()) {
            String value = (String) valueIter.next();
            if (status.equals(value)) {
                total++;
            }
        }
        return total;
    }


    /**
     * Generate a text-based report of the differences between each file in the jars.
     *
     * @param   files   Hashtable containing the list of files and status
     */
    private void generateTextReport(Hashtable files) {
        Enumeration keys = files.keys();

        String currentKey = null;
        String currentValue = null;
        while (keys.hasMoreElements()) {
            currentKey = (String) keys.nextElement();
            currentValue = (String) files.get(currentKey);
            System.out.println(currentValue + " " + currentKey);
        }
    }


    /**
     * Generate an HTML report of the differences between each file in the jars.
     *
     * @param   files   Hashtable containing the list of files and status
     */
    private void generateHtmlReport(Hashtable files) throws IOException {
        int fileIndex = 0;

        Enumeration keys = files.keys();
        String reportName = reportDir.getAbsolutePath() + File.separator + "index.html";

        FileWriter writer = null;
        try {
            writer = new FileWriter(reportName);
            writer.write("<html>\n");
            writer.write("<head>\n");
            writer.write("  <style type=\"text/css\">\n");
            writer.write("    TD.filename  { cursor: default; font-family : Verdana, arial, sans-serif; font-size: 11px; background: #FFFFFF; color: #000000; }\n");
            writer.write("    TD.unchanged { cursor: default; font-family : Verdana, arial, sans-serif; font-size: 11px; background: #FFFFFF; color: #000000; }\n");
            writer.write("    TD.added     { cursor: default; font-family : Verdana, arial, sans-serif; font-size: 11px; background: #CCCCFF; color: #000000; }\n");
            writer.write("    TD.deleted   { cursor: default; font-family : Verdana, arial, sans-serif; font-size: 11px; background: #FFCCCC; color: #000000; }\n");
            writer.write("    TD.modified  { cursor: default; font-family : Verdana, arial, sans-serif; font-size: 11px; background: #FFFFCC; color: #000000; }\n");
            writer.write("  </style>\n");
            writer.write("</head>\n");
            writer.write("<body>\n");

            // Display summary information about the file
            writer.write("  <p>\n");
            writer.write("    <b>Comparision of " + oldFile.getName() + " to " + newFile.getName() + "</b><br>\n");
            writer.write("    <b>" + _statusSummary + "</b><br>\n");
            writer.write("  </p>\n");

            // Display each file that has been examined
            if (hasAnythingToShow(files)) {
                writer.write("  <table border=\"1\" cellspacing=\"1\" cellpadding=\"1\" width=\"100%\">\n");
                writer.write("    <tr>\n");
                writer.write("      <td width=\"5%\">Status</td>\n");
                if (saveModified || saveAdded || saveDeleted || saveUnchanged) {
                    writer.write("      <td width=\"2%\">Old</td>\n");
                    writer.write("      <td width=\"2%\">New</td>\n");
                }
                writer.write("      <td>Diff</td>\n");
                writer.write("    </tr>\n");
            }

            String currentKey = null;
            String currentValue = null;
            int modifiedCount = getStatusCount(files, STATUS_MODIFIED);
            int modifiedProgress = 0;
            while (keys.hasMoreElements()) {
                fileIndex++;
                String linkfile = getPaddedValue(fileIndex, 9) + ".html";
                currentKey = (String) keys.nextElement();
                currentValue = (String) files.get(currentKey);

                // Generate a diff file and determine if there were differences
                boolean hasDiffs = false;
                String link = currentKey;
                String oldLink = "&nbsp;";
                String newLink = "&nbsp;";

                if (showDiffs) {
                    if (currentValue == STATUS_MODIFIED) {
                        modifiedProgress++;
                        if (verbose) System.out.println("Performing file diff " + modifiedProgress + " of " + modifiedCount + ": " + currentKey);
                        String note = "";  // Special status notes

                        // Perform a diff based on the type of file
                        hasDiffs = diffFile(currentKey, linkfile);
                        
                        // If the file has no visual diffs, a link won't help the user
                        if (hasDiffs) {
                            if ( exceedMaxLines ) {
                                note = " (File exceeds maximum number of lines allowed. max = "+maxLines+")";
                                link = currentKey + note;
                            } else {
                                link = "<a href=\"" + linkfile + "\">" + stringToHtml(currentKey) + "</a>" + note;
                            }
                            File originalFile = new File(currentKey);
                            oldLink = "<a href=\"" + SUBDIR_OLD + File.separator + originalFile.getPath() + "\">old</a>";
                            newLink = "<a href=\"" + SUBDIR_NEW + File.separator + originalFile.getPath() + "\">new</a>";
                        } else {
                            link = stringToHtml(currentKey) + note;
                        }
                    } else if (saveAdded && (currentValue == STATUS_ADDED)) {
                        link = stringToHtml(currentKey);
                        File originalFile = new File(currentKey);
                        newLink = "<a href=\"" + SUBDIR_NEW + File.separator + originalFile.getPath() + "\">new</a>";
                        extract(new JarFile(newFile), originalFile, newDir);
                    } else if (saveDeleted && (currentValue == STATUS_DELETED)) {
                        link = stringToHtml(currentKey);
                        File originalFile = new File(currentKey);
                        oldLink = "<a href=\"" + SUBDIR_OLD + File.separator + originalFile.getPath() + "\">old</a>";
                        extract(new JarFile(oldFile), originalFile, oldDir);
                    } else if (saveUnchanged && (currentValue == STATUS_UNCHANGED)) {
                        link = stringToHtml(currentKey);
                        File originalFile = new File(currentKey);
                        oldLink = "<a href=\"" + SUBDIR_OLD + File.separator + originalFile.getPath() + "\">old</a>";
                        newLink = "<a href=\"" + SUBDIR_NEW + File.separator + originalFile.getPath() + "\">new</a>";
                        extract(new JarFile(newFile), originalFile, newDir);
                        extract(new JarFile(oldFile), originalFile, oldDir);
                    }  // end of if (currentValue == STATUS_MODIFIED) 
                } //end of if (showDiffs)

                // Determine whether the user wishes this type of entry to be displayed
                if (showStatus(currentValue)) {
                    writer.write("    <tr>\n");
                    writer.write("      <td class=\"" + currentValue + "\">" + currentValue + "</td>\n");
                    if (saveModified || saveAdded || saveDeleted || saveUnchanged) {
                        writer.write("      <td align=\"center\" class=\"" + currentValue + "\">" + oldLink + "</td>\n");
                        writer.write("      <td align=\"center\" class=\"" + currentValue + "\">" + newLink + "</td>\n");
                    }
                    writer.write("      <td class=\"filename\">" + link + "</td>\n");
                    writer.write("    </tr>\n");
                }
            }

            if ( hasAnythingToShow(files)) {
                writer.write("  </table>\n");
            }
            writer.write("</body>\n");
            writer.write("</html>\n");
            System.out.println("HTML report written to " + reportName);
        } finally {
            if (writer != null) writer.close();
        }
    }


    /**
     * Determine if there are any entries to be shown
     *
     * @return True if there are entries to be displayed
     */
    private boolean hasAnythingToShow(Hashtable files) {
        boolean hasThingsToShow=false;
        String currentValue=null;
        Collection allValues = files.values();
        Iterator it = allValues.iterator();
        while(it.hasNext()) {
            currentValue = (String) it.next();
            if (showStatus(currentValue)) {
                hasThingsToShow=true;
            }
        }
        return hasThingsToShow;
    }


    /**
     * Determine if the file is modified using the diff algorithm 
     *
     * @param  file  File to be diff'ed
     * @param  name  Name of the diff output file
     * @return True if the file contains differences
     */
    private boolean diffFile(String file, String name) throws IOException, BuildException {
        boolean hasDiffs = false;
        FileInputStream oldReportInputStream = null;
        FileInputStream newReportInputStream = null;
        InputStream oldInputStream = null;
        InputStream newInputStream = null;
        try {
            // Access the old and new files from the jars
            JarFile oldJar = new JarFile(oldFile);
            JarFile newJar = new JarFile(newFile);

            // call the diff algorithm
            if (file.endsWith(".class")) {
              if (disassembler != null) {
                // Generate the disassembled class file output
                File oldClassReport = new File(reportDir.getAbsolutePath() + File.separator + "old.data");
                File newClassReport = new File(reportDir.getAbsolutePath() + File.separator + "new.data");
                disassemble(oldJar, new File(file), oldClassReport);
                disassemble(newJar, new File(file), newClassReport);

                oldReportInputStream = new FileInputStream(oldClassReport);
                newReportInputStream = new FileInputStream(newClassReport);
                hasDiffs = hasDifferences(oldReportInputStream, newReportInputStream, file, name);

                // Delete the temporary files
                oldClassReport.delete();
                newClassReport.delete();
              } else {
                throw new BuildException(
                "The 'disassembler' attribute must be specified when diffing class files.");
              }
            } else {
              oldInputStream = oldJar.getInputStream(oldJar.getJarEntry(file));
              newInputStream = newJar.getInputStream(newJar.getJarEntry(file));
              hasDiffs = hasDifferences(oldInputStream, newInputStream, file, name);
            }
        } catch (SecurityException sex) {
            System.out.println("Unable to access file: " + sex.getMessage());
        } finally {
            if (oldInputStream != null) oldInputStream.close();
            if (newInputStream != null) newInputStream.close();
            if (oldReportInputStream != null) oldReportInputStream.close();
            if (newReportInputStream != null) newReportInputStream.close();
        }

        return hasDiffs;
    }

    /**
     * Extract the file from the jar file and save to the specified directory.
     *
     * @param  jar     Jar file containing the file
     * @param  file    File to extract from the jar
     * @param  dest    Destination directory where the file should be extracted
     */
    private void extract(JarFile jar, File file, File dest) throws IOException {
        InputStream jarInputStream = null;
        OutputStream outputStream = null;
        FileWriter writer = null;
        int BUFFER = 2048;
        int count = 0;
        byte data[] = new byte[BUFFER];

        String path = file.getPath();
        try {
            // Make sure the jar file separator character is correct even on Windows
            JarEntry jarEntry = jar.getJarEntry(path.replace('\\', '/'));
            File destFile = new File(dest.getAbsolutePath() + File.separator + file.getPath());
            destFile.getParentFile().mkdirs();

            jarInputStream = new BufferedInputStream(jar.getInputStream(jarEntry));
            outputStream = new BufferedOutputStream(new FileOutputStream(destFile.getPath()), BUFFER); 

            while ((count = jarInputStream.read(data, 0, BUFFER)) != -1) {
                outputStream.write(data, 0, count);
            }

        } catch (Exception ex) {
            if (ex.getMessage() != null) {
                System.out.println("Unable to access file: " + path + " - " + ex.getMessage());
            } else {
                System.out.println("Unable to access file: " + path);
            }
        } finally {
            if (jarInputStream != null) jarInputStream.close();
            if (outputStream != null) {
                outputStream.flush();
                outputStream.close();
            }
        }
    }

    /**
     * Disassembles a class file and generates a report file.
     *
     * @param  jar     Jar file containing the class
     * @param  input   Class file to disassemble
     * @param  output  Report file to generate
     */
    private void disassemble(JarFile jar, File input, File output) throws IOException {
        // Convert the file path to a class name
        String classname = input.getPath();
        int suffixIdx = classname.lastIndexOf(".class");
        classname = classname.substring(0, suffixIdx);
        classname = classname.replace(File.separatorChar, '.');
  
        // extract the class file to oldDir.  This is temporary; will be deleted at the end of this function.
        extract(jar, input, oldDir);

        // Construct a list of command-line arguments for javap
        Vector argList = new Vector();
        argList.add(disassembler.getAbsolutePath());
        if (showBytecode) {
            argList.add("-c");
        }
        argList.add("-classpath");
        argList.add(oldDir.getAbsolutePath());
        argList.add(classname);
        String[] args = new String[argList.size()];
        for (int idx = 0; idx < argList.size(); idx++) {
            args[idx] = (String) argList.get(idx);
        }

        int exitValue = 0;
        FileOutputStream outputStream = null;
        InputStream procOut = null;
        InputStream procErr = null;
        try {
            Process proc = Runtime.getRuntime().exec(args);

            // Write the disassembler output to an output file
            procOut = proc.getInputStream();
            outputStream = new FileOutputStream(output);
            int currentByte = 0;
            while (currentByte != -1) {
                currentByte = procOut.read();
                outputStream.write(currentByte);
            }

            // Display errors to the user
            procErr = proc.getErrorStream();
            currentByte = 0;
            while (currentByte != -1) {
                currentByte = procErr.read();
                System.err.print((char)currentByte);
            }
            exitValue = proc.waitFor();
            proc.destroy();
        } catch (InterruptedException intex) {
            System.err.println("Abnormal process termination: " + intex.getMessage());
        } catch (FileNotFoundException nfex) {
            System.err.println("Unable to execute Java disassembler: " + disassembler.getAbsolutePath());
        } finally {
            if (outputStream != null) outputStream.close();
            if (procOut != null) procOut.close();
            if (procErr != null) procErr.close();
        }

        // Delete the temporary class file
        File classFile= new File(oldDir.getAbsolutePath() + File.separator + input.getPath());
        classFile.delete();
    }

    /**
     * Generate and HTML report which indicates the difference between two
     * files. 
     * 
     * The diff algorith was adapted from code written by Robert Sedgewick
     * and Kevin Wayne.  The URL is shown here:
     * http://www.cs.princeton.edu/introcs/96optimization/Diff.java.html
     *
     * @param  older InputStream for reading the old version of the file
     * @param  newer InputStream for reading the new version of the file 
     * @param  file  Name of the original file
     * @param  name  Name of the diff output file
     * @return True if the file contains differences
     */
    private boolean hasDifferences(InputStream older, InputStream newer, String file, String name) throws IOException {
        boolean hasDiffs = false;
        BufferedReader oldReader = null;
        BufferedReader newReader = null;
        exceedMaxLines = false;
        File originalFile = new File(file);
        FileWriter writer = null;
        FileWriter oldWriter = null;
        FileWriter newWriter = null;

        try {
            if (name != null) {
                String reportName = reportDir.getAbsolutePath() + File.separator + name;
                String oldName = oldDir.getAbsolutePath() + File.separator + originalFile.getPath();
                String newName = newDir.getAbsolutePath() + File.separator + originalFile.getPath();
                File oldFile = new File(oldName);
                File newFile = new File(newName);
                writer = new FileWriter(reportName);
                if (saveModified) {
                    // Make sure the parent directories exist so the files can be written
                    oldFile.getParentFile().mkdirs();
                    newFile.getParentFile().mkdirs(); 

                    oldWriter = new FileWriter(oldFile);
                    newWriter = new FileWriter(newFile);
                }
                writer.write("<html>\n");
                writer.write("<head>\n");
                writer.write("  <style type=\"text/css\">\n");
                writer.write("    .unchanged { background: #FFFFFF; color: #000000; }\n");
                writer.write("    .added     { background: #CCCCFF; color: #000000; }\n");
                writer.write("    .deleted   { background: #FFCCCC; color: #000000; }\n");
                writer.write("    .modified  { background: #FFFFCC; color: #000000; }\n");
                writer.write("  </style>\n");
                writer.write("</head>\n");
                writer.write("<body>\n");
                writer.write("<pre>\n");
            }

            oldReader = new BufferedReader(new InputStreamReader(older));
            newReader = new BufferedReader(new InputStreamReader(newer));

            String[] x = new String[maxLines];  // lines in first file
            String[] y = new String[maxLines];  // lines in second file
            int M = 0;                          // number of lines of first file
            int N = 0;                          // number of lines of second file

            // read in input from two files
            String line;
            while ((line = oldReader.readLine()) != null) {
                x[M++] = line;
                if ((saveModified) && (name!=null)) {
                    oldWriter.write(line + "\n");
                }
            }
            while ((line = newReader.readLine()) != null) {
                y[N++] = line;
                if ((saveModified) && (name!=null)) {
                    newWriter.write(line + "\n");
                }
            }

            // opt[i][j] = length of LCS of x[i..M] and y[j..N]
            int[][] opt = new int[M+1][N+1];

            // compute length of LCS and all subproblems via dynamic programming
            for (int i = M-1; i >= 0; i--) {
                for (int j = N-1; j >= 0; j--) {
                    if (x[i].equals(y[j])) {
                        opt[i][j] = opt[i+1][j+1] + 1;
                    } else { 
                        opt[i][j] = Math.max(opt[i+1][j], opt[i][j+1]);
                    }
                }
            }

            // recover LCS itself and print out non-matching lines to standard output
            int i = 0, j = 0;
            while(i < M && j < N) {
                if (x[i].equals(y[j])) {
                    printDiff(writer, "&nbsp; <font class=\"unchanged\">" + stringToHtml(y[j]) + "</font>\n");
                    i++;
                    j++;
                } else if (opt[i+1][j] >= opt[i][j+1]) {
                    printDiff(writer, "&lt; <font class=\"deleted\">" + stringToHtml(x[i++]) + "</font>\n");
                    hasDiffs = true;
                    if (name == null)  break;
                } else {
                    printDiff(writer, "&gt; <font class=\"added\">" + stringToHtml(y[j++]) + "</font>\n");
                    hasDiffs = true;
                    if (name == null)  break;
                }
            }

            // dump out one remainder of one string if the other is exhausted
            if (name != null) {
                while(i < M || j < N) {
                    if (i == M) {
                        printDiff(writer, "&gt; <font class=\"added\">" + stringToHtml(y[j++]) + "</font>\n");
                        hasDiffs = true;
                    } else if (j == N) { 
                        printDiff(writer,"&lt; <font class=\"deleted\">" + stringToHtml(x[i++]) + "</font>\n");
                        hasDiffs = true;
                    }
                }
            } else {
                if (M != N) {
                    hasDiffs = true;
                }
            }
            
        } catch (SecurityException sex) {
            System.out.println("Unable to access file: " + sex.getMessage());
        } catch (ArrayIndexOutOfBoundsException obex) {
            System.out.println("File exceeds maximum number of lines allowed. (max = " + maxLines + ")");
            hasDiffs = true;
            exceedMaxLines = true;
        } finally {
            if (writer != null) {
                printDiff(writer,"</pre>\n");
                printDiff(writer,"</body>\n");
                printDiff(writer,"</html>\n");
                writer.close();
            }
            if (oldWriter != null) oldWriter.close();
            if (newWriter != null) newWriter.close();
            if (oldReader != null) oldReader.close();
            if (newReader != null) newReader.close();
        }

        return hasDiffs;
    }

    private void printDiff(FileWriter writer, String line) {
        if (writer != null) {
            try {
                writer.write(line);
            } catch (IOException ex) {
                System.out.println("IO Excpetion: " + ex.getMessage());
            }    
        }    
    }

    /**
     * Return a string padded with leading zeros.
     *
     * @param  value  Integer value to be formatted
     * @param  width  Number of digits in the resulting string
     * @return String padded with leading zeros
     */
    private String getPaddedValue(int value, int width) {
        StringBuffer str = new StringBuffer();

        // Count the number of digits in the number
        int digits = 1;
        int tempValue = value;
        while (tempValue >= 10) {
            digits++;
            tempValue = tempValue / 10;
        } 

        int pad = width - digits;
        for (int idx = 0; idx < pad; idx++) {
            str.append("0");
        }
        str.append(value);

        return str.toString();
    }


    /**
     * Determine whether the entry should be displayed based on the boolean 
     * settings used to enable or disable output.
     *
     * @param   status    Status of the current entry
     */
    private boolean showStatus(String status) {
        boolean show = false;

        if (STATUS_UNCHANGED.equals(status) && showUnchanged) {
            show = true;
        } else if (STATUS_ADDED.equals(status) && showAdded) {
            show = true;
        } else if (STATUS_DELETED.equals(status) && showDeleted) {
            show = true;
        } else if (STATUS_MODIFIED.equals(status) && showModified) {
            show = true;
        }

        return show;
    }

    /**
     * Replaces any reserved HTML characters with their escaped equivalents.
     * For example, any double quotes (&quot;) would be converted to &amp;quot;
     *
     * @param   str     String containing characters which must be escaped
     * @return  String with HTML escaped characters
     */
    public static String stringToHtml(String str) {
        if (str != null) {
            str = replaceChar(str, '&', "&amp;");
            str = replaceChar(str, '"', "&quot;");
            str = replaceChar(str, '>', "&gt;");
            str = replaceChar(str, '<', "&lt;");
        }

        return str;
    }

    /**
     * Replaces a single character with a string.
     *
     * @param   line    Line of text to be updated
     * @param   ch      character to be replaced
     * @param   str     String to replace the character with
     * @return  String containing the substituted values
     */
    public static String replaceChar(String line, char ch, String str) {
        String newString = "";
        int idxChar;

        while ((line != null) && (line.length() > 0)) {
            idxChar = line.indexOf(ch);
            if (idxChar >= 0) {
                newString = newString + line.substring(0, idxChar) + str;
                // Trim the processed information from the current string
                line = line.substring(idxChar + 1);
            } else {
                // Get the rest of the line when no more characters are found
                newString = newString + line;
                line = null;
            }
        }
        return newString;
    }


    /**
     * Determine if the file should be processed based upon the patternsets.
     * Matching exclude patterns take precedence over matching include patterns.
     *
     * @param   file    File being processed
     * @return  True if the file should be processed
     */
    public boolean includeFile(String file) {
        boolean included = true;

        // Process the pattern sets to determine whether the file matches any patterns
        if (patternsets != null && patternsets.size() > 0) {
            included = false;
            String name = file.replace('/', File.separatorChar).replace('\\', File.separatorChar);

            // Iterate through each pattern set
            for (int v = 0; v < patternsets.size(); v++) {
                PatternSet p = (PatternSet) patternsets.elementAt(v);
                String[] incls = p.getIncludePatterns(getProject());
                if (incls == null || incls.length == 0) {
                    // no include pattern implicitly means includes="**"
                    incls = new String[] {"**"};
                }

                for (int w = 0; w < incls.length; w++) {
                    String pattern = incls[w].replace('/', File.separatorChar).replace('\\', File.separatorChar);
                    if (pattern.endsWith(File.separator)) {
                        pattern += "**";
                    }

                    included = SelectorUtils.matchPath(pattern, name);
                    if (included) {
                        break;
                    }
                }

                if (!included) {
                    break;
                }


                String[] excls = p.getExcludePatterns(getProject());
                if (excls != null) {
                    for (int w = 0; w < excls.length; w++) {
                        String pattern = excls[w].replace('/', File.separatorChar).replace('\\', File.separatorChar);
                        if (pattern.endsWith(File.separator)) {
                            pattern += "**";
                        }
                        included = !(SelectorUtils.matchPath(pattern, name));
                        if (!included) {
                            break;
                        }
                    }
                }
            }
        }

        return included;
    }

}


