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


/**
 * Specify the form interface for all test input fields.
 *
 * @author  Shawn Stafford
 */
public interface IMnTestForm {

    // ========================================================================
    // The following section contains URL parameter names
    // ========================================================================

    /** Test suite primary key */
    public static final String SUITE_ID_LABEL = "sid";

    /** Test primary key */
    public static final String TEST_ID_LABEL = "tid";

    /** Test suite group ID */
    public static final String GROUP_ID_LABEL = "gid";

    /** Test suite group name */
    public static final String GROUP_NAME_LABEL = "gname";



    // ========================================================================
    // The following section contains session attribute names
    // ========================================================================

    /** Test suite data object */
    public static final String SUITE_OBJECT_LABEL = "SuiteObj";

    /** Test data object */
    public static final String TEST_OBJECT_LABEL = "TestObj";

    /** Test data for the last passing test execution */
    public static final String LASTPASS_OBJECT_LABEL = "LastPassObj";

    /** Test data for the last failing test execution */
    public static final String LASTFAIL_OBJECT_LABEL = "LastFailObj";

    /** List of test executions for the currently viewed test */
    public static final String HISTORY_OBJECT_LABEL = "TestHistoryObj";

}

