/*
 * Copyright 2000-2003 by Model N, Inc.  All Rights Reserved.
 * 
 * This software is the confidential and proprietary information
 * of Model N, Inc ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you
 * entered into with Model N, Inc.
 */
package com.modeln.build.ant.report;

import java.util.Hashtable;
import java.util.Vector;

/**
 * Provides a summary of the information associated with a report parse
 * target.
 *
 * @author Shawn Stafford
 */
public class ReportParseTargetSummary {

    /** List of build events that match the current target */
    private Vector<ReportParseEvent> reportEvents = new Vector<ReportParseEvent>();

    /** Hash table containing the list of severity levels and the total count */
    private Hashtable<String, Integer> errorCount = new Hashtable<String, Integer>();

    /** Reference to the parse target that is being summarized */
    private ReportParseTarget parseTarget;

    /**
     * Construct the report summary by analyzing the list of parse events.
     *
     * @param   events      List of parse events to summarize
     */
    public ReportParseTargetSummary(ReportParseTarget target, Vector<ReportParseEvent> events) {
        parseTarget = target;

        ReportParseEvent currentEvent = null;
        for (int idx = 0; idx < events.size(); idx++) {
            // Locate all of the events that correspond to the current target
            currentEvent = (ReportParseEvent) events.get(idx);
            if (currentEvent.getOwner() == target) {
                // Only count the most severe level for the event
                ReportParseCriteria high = currentEvent.getHighestCriteria();
                if (high != null) {
                    addCriteria(high);
                }
                reportEvents.add(currentEvent);
            }
        }

    }


    /**
     * Obtain a list of report levels and the number of occurances logged
     * for the current parse target.  If there are no criteria to summarize,
     * the results will contain no entries.
     *
     * @return  Hashtable containing level occurance counts.
     */
    public Hashtable<String, Integer> getTypeSummary() {
        return errorCount;
    }

    /**
     * Return a list of events that occurred for the current target.
     *
     * @return  List of events.
     */
    public Vector<ReportParseEvent> getEvents() {
        return reportEvents;
    }

    /**
     * Return the target that is being summarized.
     *
     * @return  target
     */
    public ReportParseTarget getTarget() {
        return parseTarget;
    }

    /**
     * Adds the given criteria to the running tally of errors, warnings,
     * and other levels.
     */
    private void addCriteria(ReportParseCriteria criteria) {
        String key = criteria.getType();

        // Determine if an entry exists in the hashtable
        Integer count = (Integer) errorCount.get(key);
        if (count != null) {
            errorCount.put(key, new Integer(count.intValue() + 1));
        } else {
            errorCount.put(key, new Integer(1));
        }
    }

}
