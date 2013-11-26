package com.modeln.build.common.data.env;


/**
 * The software information represensts information about
 * a specific piece of software.
 *
 * @version      1.0
 * @author       Shawn Stafford (sstafford@modeln.com)
 */
public class CMnSoftware {

    /** Identifies an unknown software category */
    public static final int CATEGORY_UNKNOWN_TYPE = 0;

    /** Identifies the database software category */
    public static final int CATEGORY_DATABASE_TYPE = 1;

    /** Identifies the application server software category */
    public static final int CATEGORY_APPSERVER_TYPE = 2;

    /** Identifies the web server software category */
    public static final int CATEGORY_WEBSERVER_TYPE = 3;

    /** Identifies the analytics software category */
    public static final int CATEGORY_ANALYTICS_TYPE = 4;

    /** Identifies the reporting software category */
    public static final int CATEGORY_REPORTING_TYPE = 5;



    /** Identifies an unknown software category */
    public static final String CATEGORY_UNKNOWN_STR = "other";

    /** Identifies the database software category */
    public static final String CATEGORY_DATABASE_STR = "database";

    /** Identifies the application server software category */
    public static final String CATEGORY_APPSERVER_STR = "appserver";

    /** Identifies the web server software category */
    public static final String CATEGORY_WEBSERVER_STR = "webserver";

    /** Identifies the analytics software category */
    public static final String CATEGORY_ANALYTICS_STR = "analytics";

    /** Identifies the reporting software category */
    public static final String CATEGORY_REPORTING_STR = "reporting";




    /** Auto-generated ID used to identify an operating system  */
    private Integer id;

    /** Operating system category */
    private int category;

    /** Operating system name */
    private String name;

    /** Operating system version */
    private String version;



    /**
     * Set the ID used to look-up the software information.
     *
     * @param   id   Unique account ID
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Return the software ID used to look-up the software information.
     *
     * @return ID for the software
     */
    public Integer getId() {
        return id;
    }

    /**
     * Set the software name.
     *
     * @param  text    software name
     */
    public void setName(String text) {
        name = text;
    }

    /**
     * Return the software name.
     *
     * @return software name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the software version.
     *
     * @param  text    software version
     */
    public void setVersion(String text) {
        version = text;
    }

    /**
     * Return the software version.
     *
     * @return software version
     */
    public String getVersion() {
        return version;
    }



    /**
     * Set the software category.
     *
     * @param  type    software category 
     */
    public void setCategory(int type) {
        category = type;
    }


    /**
     * Set the software category. 
     *
     * @param  type    software category 
     */
    public void setCategory(String type) {
        category = getCategory(type);
    }

    /**
     * Return the software category. 
     *
     * @return software category 
     */
    public int getCategory() {
        return category;
    }


    /**
     * Convert the string to the software category.
     *
     * @param  type   software category 
     * @return software cateogory
     */
    public static int getCategory(String type) {
        int typeValue = CATEGORY_UNKNOWN_TYPE;
        if (type.equals(CATEGORY_DATABASE_STR)) {
            typeValue = CATEGORY_DATABASE_TYPE;
        } else if (type.equals(CATEGORY_APPSERVER_STR)) {
            typeValue = CATEGORY_APPSERVER_TYPE;
        } else if (type.equals(CATEGORY_WEBSERVER_STR)) {
            typeValue = CATEGORY_WEBSERVER_TYPE;
        } else if (type.equals(CATEGORY_ANALYTICS_STR)) {
            typeValue = CATEGORY_ANALYTICS_TYPE;
        } else if (type.equals(CATEGORY_REPORTING_STR)) {
            typeValue = CATEGORY_REPORTING_TYPE;
        }
        return typeValue;
    }


    /**
     * Convert the software category type to a string. 
     *
     * @param  type   software cateogory type
     * @return software cateogory type
     */
    public static String getCategory(int type) {
        switch (type) {
            case CATEGORY_DATABASE_TYPE:    return CATEGORY_DATABASE_STR;
            case CATEGORY_APPSERVER_TYPE:   return CATEGORY_APPSERVER_STR;
            case CATEGORY_WEBSERVER_TYPE:   return CATEGORY_WEBSERVER_STR;
            case CATEGORY_ANALYTICS_TYPE:   return CATEGORY_ANALYTICS_STR;
            case CATEGORY_REPORTING_TYPE:   return CATEGORY_REPORTING_STR;
            default: return CATEGORY_UNKNOWN_STR;
        }
    }


}

