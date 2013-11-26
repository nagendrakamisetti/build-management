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

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Data object used to represent a single test in the database.  This 
 * object provides the object/relational mapping.
 * 
 * @author  Shawn Stafford
 * @author  Vincent Malley
 */
public class CMnDbBlackListAcceptanceTestData extends CMnDbBlackListTestData {



    /** Name of the file containing the test */
    private String fileName;

    CMnDbBlackListAcceptanceTestData() {
    }

    /** constructor based on a parent class already existing
     * @param data test already existing
     *
     * */
    CMnDbBlackListAcceptanceTestData(CMnDbAcceptanceTestData data){
        super(data);
        fileName = data.getScriptName();
    }

    /**
     * Determines if the specified test matches the current test.
     *
     * @param   test  Test information
     * @return  TRUE if the objects match
     */
    public boolean matches(CMnDbBlackListAcceptanceTestData test) {
        boolean basematch = super.matches(test);

        boolean filematch = false;
        if ((fileName != null) && (test.getFileName() != null)) {
            filematch = fileName.equalsIgnoreCase(test.getFileName());
        } else if ((fileName == null) && (test.getFileName() == null)) {
            filematch = true;
        }

        return (basematch && filematch);
    }


    /**
     * Returns a string representing the display name for the test.
     *
     * @return String representing the name of the test
     */
    public String getDisplayName() {
        if (fileName != null) {
            return fileName;
        } else {
            return null;
        }
    }


    /**
     * Set the name of the test file.
     *
     * @param   name    Name of the file
     */
    public void setFileName(String name) {
        fileName = name;
    }

    /**
     * Return the name of the test file.
     *
     * @return  File name
     */
    public String getFileName() {
        return fileName;
    }

}
