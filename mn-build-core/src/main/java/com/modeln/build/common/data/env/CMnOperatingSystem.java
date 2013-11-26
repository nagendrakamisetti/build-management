package com.modeln.build.common.data.env;


/**
 * The operating system information represensts information about
 * a specific operating system. 
 *
 * @version      1.0
 * @author       Shawn Stafford (sstafford@modeln.com)
 */
public class CMnOperatingSystem {

    /** Identifies an unknown operating system category */
    public static final int CATEGORY_UNKNOWN_TYPE = 0;

    /** Identifies the Linux operating system category */
    public static final int CATEGORY_LINUX_TYPE = 1;

    /** Identifies the Microsoft Windows operating system category */
    public static final int CATEGORY_WINDOWS_TYPE = 2;

    /** Identifies the Sun Solaris operating system category */
    public static final int CATEGORY_SOLARIS_TYPE = 3;



    /** Identifies an unknown operating system category */
    public static final String CATEGORY_UNKNOWN_STR = "other";

    /** Identifies the Linux operating system category */
    public static final String CATEGORY_LINUX_STR = "linux";

    /** Identifies the Microsoft Windows operating system category */
    public static final String CATEGORY_WINDOWS_STR = "windows";

    /** Identifies the Sun Solaris operating system category */
    public static final String CATEGORY_SOLARIS_STR = "solaris";




    /** Auto-generated ID used to identify an operating system  */
    private Integer id;

    /** Operating system category */
    private int category;

    /** Operating system name */
    private String name;

    /** Operating system version */
    private String version;



    /**
     * Set the ID used to look-up the OS information.
     *
     * @param   id   Unique account ID
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Return the OS ID used to look-up the OS information.
     *
     * @return ID for the OS
     */
    public Integer getId() {
        return id;
    }

    /**
     * Set the OS name.
     *
     * @param  text    OS name
     */
    public void setName(String text) {
        name = text;
    }

    /**
     * Return the OS name.
     *
     * @return OS name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the OS version.
     *
     * @param  text    OS version
     */
    public void setVersion(String text) {
        version = text;
    }

    /**
     * Return the OS version.
     *
     * @return OS version
     */
    public String getVersion() {
        return version;
    }



    /**
     * Set the OS category.
     *
     * @param  type    OS category 
     */
    public void setCategory(int type) {
        category = type;
    }


    /**
     * Set the OS category. 
     *
     * @param  type    OS category 
     */
    public void setCategory(String type) {
        category = getCategory(type);
    }

    /**
     * Return the OS category. 
     *
     * @return OS category 
     */
    public int getCategory() {
        return category;
    }


    /**
     * Convert the string to the OS category.
     *
     * @param  type   OS category 
     * @return OS cateogory
     */
    public static int getCategory(String type) {
        int typeValue = CATEGORY_UNKNOWN_TYPE;
        if (type.equals(CATEGORY_LINUX_STR)) {
            typeValue = CATEGORY_LINUX_TYPE;
        } else if (type.equals(CATEGORY_WINDOWS_STR)) {
            typeValue = CATEGORY_WINDOWS_TYPE;
        } else if (type.equals(CATEGORY_SOLARIS_STR)) {
            typeValue = CATEGORY_SOLARIS_TYPE;
        }
        return typeValue;
    }


    /**
     * Convert the OS category type to a string. 
     *
     * @param  type   OS cateogory type
     * @return OS cateogory type
     */
    public static String getCategory(int type) {
        switch (type) {
            case CATEGORY_LINUX_TYPE:     return CATEGORY_LINUX_STR;
            case CATEGORY_WINDOWS_TYPE:   return CATEGORY_WINDOWS_STR;
            case CATEGORY_SOLARIS_TYPE:   return CATEGORY_SOLARIS_STR;
            default: return CATEGORY_UNKNOWN_STR;
        }
    }


}

