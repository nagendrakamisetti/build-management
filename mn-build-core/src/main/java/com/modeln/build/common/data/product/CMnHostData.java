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

import java.net.InetAddress;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * Data object used to represent a host environment that is used to create or run 
 * Java code. 
 * 
 * @author  Shawn Stafford
 */
public class CMnHostData {

    /** Username of the account on the host */
    private String username;

    /** Name of the host computer */
    private String hostname;

    /** Version of the JDK used */
    private String jdkVersion;

    /** Distributor of the JDK implementation */
    private String jdkVendor;

    /** Name of the operating system */
    private String osName;

    /** CPU architecture of the operating system */
    private String osArch;

    /** Version of the operating system */
    private String osVersion;

    /**
     * Obtain the host information for the current host.
     *
     * @return Information about the current host.
     */
    public static CMnHostData getHostData() {
        CMnHostData data = new CMnHostData();
        data.setUsername("none");

        try {
            InetAddress host = InetAddress.getLocalHost();
            data.setHostname(host.getHostName());
        } catch (Exception ex) {
            data.setHostname("localhost");
        }

        data.setJdkVersion(System.getProperty("java.version")); 
        data.setJdkVendor(System.getProperty("java.vendor")); 
        data.setOSName(System.getProperty("os.name")); 
        data.setOSArchitecture(System.getProperty("os.arch")); 
        data.setOSVersion(System.getProperty("os.version"));

        return data;
    }

    /**
     * Set the name of the account used to perform the build.
     *
     * @param  name   System account name
     */
    public void setUsername(String name) {
        username = name;
    }

    /**
     * Return the name of the account used to perform the build.
     *
     * @return  System account name
     */
    public String getUsername() {
        return username;
    }

    /**
     * Set the host on which the build was performed.
     *
     * @param  host   Name of the host computer
     */
    public void setHostname(String host) {
        hostname = host;
    }

    /**
     * Return the name of the host on which the build was created.
     *
     * @return  Hostname
     */
    public String getHostname() {
        return hostname;
    }

    /**
     * Set the version of the JDK implementation. 
     *
     * @param  version   JDK version number 
     */
    public void setJdkVersion(String version) {
        jdkVersion = version;
    }

    /**
     * Return the version number of the JDK implementation. 
     *
     * @return  JDK version number 
     */
    public String getJdkVersion() {
        return jdkVersion;
    }

    /**
     * Set the vendor of the JDK implementation.
     *
     * @param  vendor   JDK vendor name 
     */
    public void setJdkVendor(String vendor) {
        jdkVendor = vendor;
    }

    /**
     * Return the vendor of the JDK implementation.
     *
     * @return  JDK vendor name 
     */
    public String getJdkVendor() {
        return jdkVendor;
    }

    /**
     * Set the operating system name. 
     *
     * @param  name   Operating system name 
     */
    public void setOSName(String name) {
        osName = name;
    }

    /**
     * Return the operating system name. 
     *
     * @return  Operating system name 
     */
    public String getOSName() {
        return osName;
    }


    /**
     * Set the operating system CPU architecture.
     *
     * @param  arch   Chip architecture 
     */
    public void setOSArchitecture(String arch) {
        osArch = arch;
    }

    /**
     * Return the operating system CPU architecture.
     *
     * @return  Operating system architecture
     */
    public String getOSArchitecture() {
        return osArch;
    }

    /**
     * Set the operating system version.
     *
     * @param  version   Operating system version
     */
    public void setOSVersion(String version) {
        osVersion = version;
    }

    /**
     * Return the operating system version.
     *
     * @return  Operating system version
     */
    public String getOSVersion() {
        return osVersion;
    }

}
