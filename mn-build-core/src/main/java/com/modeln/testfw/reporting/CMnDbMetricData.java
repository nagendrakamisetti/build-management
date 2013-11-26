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

import java.util.Date;
import java.util.Vector;
import java.text.SimpleDateFormat;

/**
 * Data object used to represent a build event of a defined type and duration. 
 * 
 * @author  Shawn Stafford
 */
public class CMnDbMetricData {

    /** Metric type undefined */
    public static final int TYPE_UNDEFINED = 0;

    /** Metric type representing compilation and packaging */
    public static final int TYPE_BUILD = 1;

    /** Metric type representing javadoc generation */
    public static final int TYPE_JAVADOC = 2;

    /** Metric type representing content population */
    public static final int TYPE_POPULATE = 3;

    /** Metric type representing dynamic content population using unit test suites */
    public static final int TYPE_POPULATESUITE = 4;

    /** Metric type representing unit test execution */
    public static final int TYPE_UNITTEST = 5;

    /** Metric type representing database migration */
    public static final int TYPE_MIGRATE = 6;

    /** Metric type representing application deployment */
    public static final int TYPE_DEPLOY = 7;


    /** Metric type value identifying unit testing */
    public static final String TYPE_UNITTEST_STR = "unittest";

    /** Metric type value identifying compilation and packaging */
    public static final String TYPE_BUILD_STR = "build";

    /** Metric type value identifying a javadoc generation */
    public static final String TYPE_JAVADOC_STR = "javadoc";

    /** Metric type value identifying database population */
    public static final String TYPE_POPULATE_STR = "populate";

    /** Metric type value identifying dynamic content population through unit tests */
    public static final String TYPE_POPULATESUITE_STR = "populatesuite";

    /** Metric type value identifying database migration */
    public static final String TYPE_MIGRATE_STR = "migrate";

    /** Metric type value identifying application deployment */
    public static final String TYPE_DEPLOY_STR = "deploy";


    /** Timestamp used to prefix every test message line */
    private static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("HH:mm:ss.SSS - ");

    /** Record the metric type */
    private int type = TYPE_UNDEFINED;

    /** Record the starting time of the build event */
    private Date startTime;

    /** Record the completion time of the build event */
    private Date endTime;


    /**
     * Construct a build metric of the specified type.
     */
    public CMnDbMetricData() {
    }

    /**
     * Construct a build metric of the specified type. 
     *
     * @param  type   Type of build metric being described 
     */
    public CMnDbMetricData(int type) {
        this.type = type;
    }


    /**
     * Return the list of all possible metric types.
     *
     * @return  List of metric types
     */
    public static final int[] getAllTypes() {
        int[] types = new int[] {
          TYPE_BUILD,
          TYPE_JAVADOC,
          TYPE_POPULATE,
          TYPE_POPULATESUITE,
          TYPE_UNITTEST,
          TYPE_MIGRATE,
          TYPE_DEPLOY
        };

        return types;
    }

    /**
     * Set the metric type.  Valid types include:
     * <ul>
     *   <li>TYPE_BUILD</li>
     *   <li>TYPE_JAVADOC</li>
     *   <li>TYPE_POPULATE</li>
     *   <li>TYPE_POPULATESUITE</li>
     *   <li>TYPE_UNITTEST</li>
     *   <li>TYPE_MIGRATE</li>
     *   <li>TYPE_DEPLOY</li>
     *   <li>TYPE_UNDEFINED</li>
     * </ul> 
     *
     * @param   type    Metric type 
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * Return the metric type.
     *
     * @return int representing the metric type
     */
    public int getType() {
        return type;
    }

    /**
     * Set the starting time of the build.
     * 
     * @param   date    Starting time
     */
    public void setStartTime(Date date) {
        startTime = date;
    }

    /**
     * Return the starting time of the build.
     * 
     * @return  Starting time
     */
    public Date getStartTime() {
        return startTime;
    }

    /**
     * Set the ending time of the build.
     * 
     * @param   date    Ending time
     */
    public void setEndTime(Date date) {
        endTime = date;
    }

    /**
     * Return the ending time of the build.
     * 
     * @return  Ending time
     */
    public Date getEndTime() {
        return endTime;
    }

    /**
     * Return the amount of time between the start and end time.
     *
     * @return  Elapsed time
     */
    public long getElapsedTime() {
        if ((endTime != null) && (startTime != null)) {
            return endTime.getTime() - startTime.getTime();
        } else {
            return 0;
        }
    }

    /**
     * Return the amount of time between the start and end time.
     * 
     * @return  Elapsed time
     */
    public String getElapsedTimeString() {
        long elapsedMillis = getElapsedTime();
        long elapsedMinutes = elapsedMillis / (1000*60);
        long elapsedHours = elapsedMillis / (1000*60*60);

        String elapsedStr = elapsedMillis + "ms";
        if (elapsedHours > 1) {
            elapsedStr = elapsedHours + "hr " + (elapsedMinutes % 60) + "min";
        } else if (elapsedMinutes > 1) {
            elapsedStr = elapsedMinutes + "min";
        } else {
            elapsedStr = elapsedMillis + "ms";
        }

        return elapsedStr;
    }

    /**
     * Return a text description of the metric type.
     *
     * @return Description of the metric type
     */
    public String getDescription() {
        switch (type) {
          case TYPE_BUILD:         return "Compilation and Packaging";
          case TYPE_UNITTEST:      return "Unit Tests";
          case TYPE_JAVADOC:       return "JavaDoc";
          case TYPE_POPULATE:      return "Static Content Population";
          case TYPE_POPULATESUITE: return "Dynamic Content Population";
          case TYPE_MIGRATE:       return "Database Migration";
          case TYPE_DEPLOY:        return "Application Deployment";
          default: return "Unknown";
        }
    }

    /**
     * Return the type of event specified by the string. 
     *
     * @param   type  String representing a metric type 
     * @return  Metric type
     */
    public static int getMetricType(String type) {
        if (type == null) {
            return TYPE_UNDEFINED;
        } else if (type.equals(TYPE_BUILD_STR)) {
            return TYPE_BUILD;
        } else if (type.equals(TYPE_UNITTEST_STR)) {
            return TYPE_UNITTEST;
        } else if (type.equals(TYPE_JAVADOC_STR)) {
            return TYPE_JAVADOC;
        } else if (type.equals(TYPE_POPULATE_STR)) {
            return TYPE_POPULATE;
        } else if (type.equals(TYPE_POPULATESUITE_STR)) {
            return TYPE_POPULATESUITE;
        } else if (type.equals(TYPE_MIGRATE_STR)) {
            return TYPE_MIGRATE;
        } else if (type.equals(TYPE_DEPLOY_STR)) {
            return TYPE_DEPLOY;
        } else {
            return TYPE_UNDEFINED;
        }
    }

    /**
     * Return the type of event specified by the integer. 
     *
     * @param   type   Int representing a metric type 
     * @return  Metric type
     */
    public static String getMetricType(int type) {
        switch (type) {
          case TYPE_BUILD:         return TYPE_BUILD_STR;
          case TYPE_UNITTEST:      return TYPE_UNITTEST_STR;
          case TYPE_JAVADOC:       return TYPE_JAVADOC_STR;
          case TYPE_POPULATE:      return TYPE_POPULATE_STR;
          case TYPE_POPULATESUITE: return TYPE_POPULATESUITE_STR;
          case TYPE_MIGRATE:       return TYPE_MIGRATE_STR;
          case TYPE_DEPLOY:        return TYPE_DEPLOY_STR;
          default: return "unknown";
        }
    }


}

