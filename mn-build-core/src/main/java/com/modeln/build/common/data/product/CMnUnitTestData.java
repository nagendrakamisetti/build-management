/* 
* Copyright 2000-2003 by Model N, Inc.  All Rights Reserved. 
* 
* This software is the confidential and proprietary information 
* of Model N, Inc ("Confidential Information").  You shall not 
* disclose such Confidential Information and shall use it only 
* in accordance with the terms of the license agreement you 
* entered into with Model N, Inc. 
*/
package com.modeln.build.common.data.product;


/**
 * Data object used to represent a single test in the database.  This 
 * object provides the object/relational mapping.
 * 
 * @author  Shawn Stafford
 */
public class CMnUnitTestData extends CMnTestData {


    /** Name of the class containing the test method */
    private String className;

    /** Name of the test method within the class */
    private String methodName;



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


}
