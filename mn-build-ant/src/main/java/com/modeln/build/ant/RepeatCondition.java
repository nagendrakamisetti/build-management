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

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.condition.Condition;
import org.apache.tools.ant.taskdefs.condition.ConditionBase;

public class RepeatCondition extends ConditionBase {


    /**
     * Evaluate the condition.
     */
    public boolean eval() {
        // Evaluate the first condition in the list
        if (countConditions() == 1) {
            Condition c = (Condition) getConditions().nextElement();
            return c.eval();
        } else {
            return false;
        }
    }

    /**
     * See whether our nested condition holds and set the property.
     *
     * @exception BuildException if an error occurs
     */
    public void execute() throws BuildException {
        if (countConditions() > 1) {
            throw new BuildException("You must not nest more than one "
                + "condition into <repeatcondition>");
        }
        if (countConditions() < 1) {
            throw new BuildException("You must nest a condition into "
                + "<repeatcondition>");
        }
    }

}

