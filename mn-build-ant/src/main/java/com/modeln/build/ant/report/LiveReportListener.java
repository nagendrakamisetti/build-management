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

import java.util.Vector;


/**
 *
 * @author Shawn Stafford
 */
public final class LiveReportListener extends ReportListener {

    /**
     * Construct the listener and watch for the specified warning and error
     * strings.
     *
     * @param   list    List of build targets to scan for text
     */
    public LiveReportListener(Report report, Vector list) {
        super(report, list);
    }
}
