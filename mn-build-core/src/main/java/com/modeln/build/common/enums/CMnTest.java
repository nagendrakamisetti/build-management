/*
* Copyright 2000-2003 by Model N, Inc.  All Rights Reserved.
*
* This software is the confidential and proprietary information
* of Model N, Inc ("Confidential Information").  You shall not
* disclose such Confidential Information and shall use it only
* in accordance with the terms of the license agreement you
* entered into with Model N, Inc.
*/
package com.modeln.build.common.enums;

/**
 * This class contains enums relating to test data.
 *
 * @author  Shawn Stafford
 */
public class CMnTest {

    /** List of possible test status */
    public static enum Status {
        PASS, FAIL, ERROR, SKIP, KILL, PENDING, RUNNING, BLACKLIST
    }

}


