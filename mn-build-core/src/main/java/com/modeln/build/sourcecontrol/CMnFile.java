package com.modeln.build.sourcecontrol;

import org.eclipse.jgit.diff.DiffEntry;

/**
 * Represent a file in a source control check-in. 
 */
public class CMnFile {

    /** Type of source control operations on the file */
    public enum Operation{
        ADD,
        DELETE,
        RENAME,
        EDIT
    }

    /** Name of the current file */
    private String filename;

    /** Source control operation on the file */
    private Operation operation;

    /** String representing the differences between this version and the previous version */
    private String diffcontent;


    /** 
     * Construct a source control file object.
     *
     * @param file    Filename
     * @param op      Source control file operation
     */
    public CMnFile(String file, Operation op){
        filename = file;
        operation = op;
    }


    public String getFilename() {
        return filename;
    }

    public Operation getOp() {
        return operation;
    }

    /**
     * String representing the differences between this file revision and
     * the previous revision.
     *
     * @param  diff    File differences
     */
    public void setDiff(String diff) {
        diffcontent = diff;
    }

    /**
     * Return a string representing the differences between this file
     * revision and the previous revision.
     *
     * @return File differences
     */
    public String getDiff() {
        return diffcontent;
    }

}
