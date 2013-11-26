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
public class CMnDbBlackListUnitTestData extends CMnDbBlackListTestData {



    /** Name of the class containing the test method */
    private String className;

    /** Name of the test method within the class */
    private String methodName;


    CMnDbBlackListUnitTestData() {

    }

        /** constructor based on a parent class already existing
     * @param data test already existing
     *
     * */
    CMnDbBlackListUnitTestData(CMnDbUnitTestData data){
        super(data);
        className = data.getClassName();
        methodName = data.getMethodName();
    }

    /**
     * Determines if the specified test matches the current test.
     *
     * @param   test  Test information
     * @return  TRUE if the objects match
     */
    public boolean matches(CMnDbBlackListUnitTestData test) {
        boolean basematch = super.matches(test);

        boolean classmatch = false;
        if ((className != null) && (test.getClassName() != null)) {
            classmatch = className.equalsIgnoreCase(test.getClassName());
        } else if ((className == null) && (test.getClassName() == null)) {
            classmatch = true;
        }

        boolean methodmatch = false;
        if ((methodName != null) && (test.getMethodName() != null)) {
            methodmatch = methodName.equalsIgnoreCase(test.getMethodName());
        } else if ((methodName == null) && (test.getMethodName() == null)) {
            methodmatch = true;
        }

        return (basematch && classmatch && methodmatch);
    }


    /**
     * Returns a string representing the display name for the test.
     *
     * @return String representing the name of the test
     */
    public String getDisplayName() {
        if ((className != null) && (methodName != null)) {
            return className + "." + methodName;
        } else if (className != null) {
            return className;
        } else if (methodName != null) {
            return methodName;
        } else {
            return null;
        }
    }


    /**
     * Set the name of the test class.
     *
     * @param   name    Name of the class
     */
    public void setClassName(String name) {
        className = name;
    }

    /**
     * Return the name of the test class.
     *
     * @return  Class name
     */
    public String getClassName() {
        return className;
    }

    /**
     * Set the name of the test method.
     *
     * @param   name    Name of the method
     */
    public void setMethodName(String name) {
        methodName = name;
    }

    /**
     * Return the name of the test method.
     *
     * @return    Name of the method
     */
    public String getMethodName() {
        return methodName;
    }


    /**
     * Returns a string representing the display name for the test.
     *
     * @return String representing the name of the test
     */
    public String toString() {
        return getDisplayName() + "@" + super.toString();
    }

}
