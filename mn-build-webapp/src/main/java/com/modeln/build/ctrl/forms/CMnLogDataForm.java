/* 
* Copyright 2000-2003 by Model N, Inc.  All Rights Reserved. 
* 
* This software is the confidential and proprietary information 
* of Model N, Inc ("Confidential Information").  You shall not 
* disclose such Confidential Information and shall use it only 
* in accordance with the terms of the license agreement you 
* entered into with Model N, Inc. 
*/
package com.modeln.build.ctrl.forms;

import com.modeln.build.web.tags.TextTag;


/**
 * The build form provides an HTML interface to the log data object.
 * The class manages transfering the data through the HTTP request.
 * Form data can be rendered in either a read-only or input mode.
 * 
 * @author  Shawn Stafford
 */
public class CMnLogDataForm {
    /** Reference to a log data object. */
    public static final String LOG_OBJECT_LABEL = "logObject";

    /** Name of the log identifier field. */
    public static final String LOG_ID_LABEL = "logId";

    /** Determines whether the form will be rendered with input fields */
    private boolean inputEnabled = false;

    /**
     * Construct a build data form.  When input is enabled, the form
     * will be rendered with input fields.
     *
     * @param    allowInput      Enable form input from the user 
     */
    public CMnLogDataForm(boolean allowInput) {
        inputEnabled = allowInput;
    }


    /**
     * Render the build data form.
     */
    public String toHtml() {
        return "CMnLogDataForm.toHtml() not implemented.";
    }



}
