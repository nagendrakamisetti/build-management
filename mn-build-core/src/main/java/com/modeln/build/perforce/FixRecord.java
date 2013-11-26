package com.modeln.build.perforce;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;


/**
 * The Fix class stores the job-changelists information returned from the 
 * com.modeln.build.perforce.Fixes class
 * 
 * @author kan
 *
 */
public class FixRecord {

	/** Log4j */
	private static Logger logger = Logger.getLogger(FixRecord.class.getName());

	/* Job entry such as pdbug[number] of Bugzilla */
	String _job;
	/* Changelist that is assocated with the job */
	String _changelist;
	/* Date that the fix record was created */
	String _date;
	/* Author email of the fix */
	String _author;

	public FixRecord() {
		_job = "";
		_changelist = "";
		_date = "";
		_author = "";
	}
	
	public FixRecord(String job, String changelist, String date, String author) {
		_job = job;
		_changelist = changelist;
		_date = date;
		_author = author;
	}
	
	/**
     * Parse one string of fix result into a FixRecord object 
     * 
     * @param 	result
     * @return	a FixRecord object 
     */
    public FixRecord(String fixString) {
    	String[] record;
    	String separator = "~~";
    	fixString = fixString.replaceAll(" fixed by change ", separator);
    	fixString = fixString.replaceAll(" on ", separator);
    	fixString = fixString.replaceAll(" by ", separator);
    	record = fixString.split(separator);
		_job = record[0];
		_changelist = record[1];
		_date = record[2];
		_author = record[3];
    }
    
    public String toString() {
		return _job+" fixed by change "+_changelist+" on "+_date+" by "+_author;
	}
	
	public void print() {
		logger.info(toString());
	}
	
	public String getJob() {
		return _job; 
	}
	public void setJob(String job) {
		_job = job;
	}
	public String getChangelist() {
		return _changelist; 
	}
	public void setChangelist(String changelist) {
		_changelist = changelist;
	}
	public String getDate() {
		return _date; 
	}
	public void setDate(String date) {
		_date = date;
	}
	public String getAuthor() {
		return _author; 
	}
	public void setAuthor(String author) {
		_author = author;
	}
	
 
}
