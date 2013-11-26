/*
* Copyright 2000-2003 by Model N, Inc.  All Rights Reserved.
*
* This software is the confidential and proprietary information
* of Model N, Inc ("Confidential Information").  You shall not
* disclose such Confidential Information and shall use it only
* in accordance with the terms of the license agreement you
* entered into with Model N, Inc.
*/
package com.modeln.testfw.reporting;


/**
 * Data object used to represent a single UIT step in the database.  This
 * object provides the object/relational mapping.
 *
 * @author  Shawn Stafford, Karen An
 */
public class CMnDbUitStep extends CMnDbTestData {

    /** Name of the UIT step. */
    private String stepName;


    /**
     * Set the name of the step.
     *
     * @param   name    Name of the step
     */
    public void setStepName(String name) {
        stepName = name;
    }

    /**
     * Return the name of the step.
     *
     * @return  Step name
     */
    public String getStepName() {
        return stepName;
    }


}

