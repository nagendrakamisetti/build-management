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


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;


/**
 * Data object used to represent a test suite in the database. 
 * 
 * @author  Shawn Stafford
 */
public class CMnUnitTestSuite extends CMnTestSuite {

    /** List of options associated with the suite */
    private HashMap _options = new HashMap();

    /**
     * Set the list of options used to construct the suite.
     *
     * @param  options  List of options
     */
    public void setOptions(Map options) {
        _options.putAll(options);
    }

    /**
     * Add an option to the list of JavaDoc test options that are used to
     * construct the suite.  The options are used to classify and group
     * test suites by option type.  Option values are typically either
     * "true" or "false."
     *
     * @param  name     Option name
     * @param  value    Option value
     */
    public void addOption(String name, String value) {
        _options.put(name, value);
    }

    /**
     * Returns the list of options used to construct the test suite.
     *
     * @return List of option name/value pairs
     */
    public Map getOptions() {
        return _options;
    }

    /** 
     * Returns true if the name/value pair of every entry in the 
     * specified set matches the name/value pair in the current list
     * of test suite options.
     *
     * @param  options  List of options to compare to the current suite
     * @return TRUE if the list of options is the same as the current suite
     */
    public boolean optionsEqual(Map options) {
        return _options.equals(options);
    }

    /**
     * Compares each option to the current suite options.  Returns true
     * if the suite contains all of the specified options and if all
     * of the corresponding option values are equal. 
     *
     * @param  options   List of options to check
     * @return TRUE if all options are found in the suite
     */
    public boolean hasAllOptions(Map options) {
        boolean hasAll = true;

        Iterator keys = options.keySet().iterator();
        while (keys.hasNext()) {
            Object opName = keys.next();
            Object opValue = options.get(opName);
            
            // Determine if the suite contains the current option
            if (_options.containsKey(opName)) {
                Object val = _options.get(opName);
                if (!opValue.equals(val)) {
                    hasAll = false;
                }
            } else {
                hasAll = false;
            }
        }

        return hasAll;
    }

    /**
     * Compare each option from the current suite options.  Return the
     * differences between the options.  This is primarily used to 
     * debug the hasAllOptions method. 
     *
     * @param  options   List of options to check
     * @return Formatted text comparing the options 
     */
    public String compareOptions(Map options) {
        StringBuffer diff = new StringBuffer();
        diff.append("Option           \t This       \t Compared To\n");
        diff.append("=================\t ===========\t ===========\n");

        Iterator keys = options.keySet().iterator();
        while (keys.hasNext()) {
            Object opName = keys.next();
            Object opValue = options.get(opName);

            // Determine if the suite contains the current option
            diff.append(opName + "\t " + opValue + "\t ");
            if (_options.containsKey(opName)) {
                Object val = _options.get(opName);
                diff.append(val + "\n");
            } else {
                diff.append("N/A\n");
            }

        }

        return diff.toString();
    }


}
