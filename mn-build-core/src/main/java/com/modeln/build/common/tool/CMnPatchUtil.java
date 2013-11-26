/*
 * Copyright 2000-2002 by Model N, Inc.  All Rights Reserved.
 *
 * This software is the confidential and proprietary information
 * of Model N, Inc ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you
 * entered into with Model N, Inc.
 */
package com.modeln.build.common.tool;

import com.modeln.build.common.data.account.CMnEnvironment;
import com.modeln.build.common.data.product.CMnPatch;
import com.modeln.build.common.data.product.CMnPatchFix;

import java.lang.String;
import java.util.StringTokenizer;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import javax.mail.internet.InternetAddress;


/**
 * This utility formats the patch data. 
 */
public class CMnPatchUtil {

    /** Default character used to separate version number strings */
    public static final char DEFAULT_SEPARATOR_CHAR = '-';

    /** Default string used to prefix the name of a service patch (such as "SP") */
    public static final String DEFAULT_NAME_PREFIX = "SP";



    /**
     * Convert the patch data to the command-line string used to
     * invoke the service patch tool.
     *
     * @param   patch     Service patch data
     * @param   scriptdir   Output directory for the configuration scripts
     * @return  Service patch tool command
     */
    public static final String getPatchBuildCmd(CMnPatch patch, String scriptdir) {
        StringBuffer cmd = new StringBuffer();

        if (patch != null) {
            // Set the executable command
            cmd.append("sp.sh ");

            // Use native git commands when possible 
            cmd.append("--exec ");

            // Specify an output directory for the build scripts
            if (scriptdir != null) {
                cmd.append("-s " + scriptdir);
            }

            // Set the patch fixes for the specified branch
            StringBuffer fixstr = new StringBuffer();
            boolean hasFixes = false;
            Vector<CMnPatchFix> fixes = patch.getFixes();
            if ((fixes != null) && (fixes.size() > 0)) {
                Enumeration fixList = fixes.elements();
                while (fixList.hasMoreElements()) {
                    CMnPatchFix fix = (CMnPatchFix) fixList.nextElement();
                    if (hasFixes) {
                        fixstr.append(",");
                    }
                    fixstr.append(fix.getBugId());
                    hasFixes = true;
                }

                if (hasFixes) {
                    cmd.append(" --bugs " + fixstr + " "); 
                }
            }

            // Set the customer name 
            if (patch.getCustomer() != null) {
                String cust = patch.getCustomer().getShortName();
                if (cust != null) {
                    cmd.append(cust.toLowerCase() + " ");
                }
            }

            // Set the environment information
            if (patch.getEnvironment() != null) {
                String envName = patch.getEnvironment().getShortName();
                if ((envName != null) && (envName.trim().length() > 0)) {
                    cmd.append(" --env " + envName.trim() + " ");
                }
            }

            // Set the build version string
            if (patch.getBuild() != null) {
                String ver = patch.getBuild().getBuildVersion();
                cmd.append(ver + " ");
            }

            // Set the patch number
            if (patch.getName() != null) {
                cmd.append(patch.getName().toLowerCase());
            }
        }

        return cmd.toString();
    }

    /**
     * Parse the service patch name to get the numeric portion of the name.
     * This assumes that the name starts with the string "sp" (case
     * insensitive) followed by an integer value.
     *
     * @param   name    Service patch name (i.e. SP13)
     * @return  Service patch number (i.e. 13)
     * @throws NumberFormatException if the name does not contain a valid Integer
     */
    public static final Integer getPatchNumber(String name) throws NumberFormatException {
        Integer result = null;

        int idx = name.toLowerCase().indexOf(DEFAULT_NAME_PREFIX.toLowerCase());
        if (idx >= 0) {
            result = new Integer(name.substring(2));
        }

        return result;
    }

    /**
     * Prepend the patch name prefix to the patch number to produce the name.
     *
     * @param  number   Service patch number (i.e. 13)
     * @return Service patch name (i.e. SP13)
     */
    public static final String getPatchName(Integer number) {
        return DEFAULT_NAME_PREFIX + number;
    }


    /**
     * Obtain the product name from the full version string.
     *
     * @param  version   Version string
     * @return Product name
     */
    public static final String getProductName(String version) {
        String name = null;

        StringTokenizer st = new StringTokenizer(version, String.valueOf(DEFAULT_SEPARATOR_CHAR));
        if (st.countTokens() > 1) {
            String mn = st.nextToken();
            name = st.nextToken();
        } else {
            name = version;
        }

        return name;
    }
    /**
     * Obtain the version number from the full version string.
     *
     * @param  version   Version string
     * @return Version number
     */
    public static final String getVersionNumber(String version) {
        String verNum = null;

        // Try to obtain the numeric version number from the version string
        String shortVersion = getShortVersion(version);
        int idxVerNum = shortVersion.lastIndexOf(DEFAULT_SEPARATOR_CHAR);
        if (idxVerNum > 0) {
            verNum = shortVersion.substring(idxVerNum + 1);
        } else {
            verNum = shortVersion;
        }

        return verNum;
    }

    /**
     * Obtain the shortened version of a version string by trimming off the
     * timestamp.
     *
     * @param  version   Version string
     * @return Shortened version string
     */
    public static final String getShortVersion(String version) {
        String shortVersion = null;

        int lastSeparator = version.lastIndexOf(DEFAULT_SEPARATOR_CHAR);
        if (lastSeparator > 0) {
            shortVersion = version.substring(0, lastSeparator);
        } else {
            shortVersion = version;
        }

        return shortVersion;
    }

    /**
     * Construct the branch name based on the service patch information. 
     * By default, use a branch name of "cust_vernum_spname"
     * If the environment name is not null, use "cust_env_vernum_spname"
     *
     * @param  patch     Service patch information 
     * @return Service patch branch name 
     */
    public static String getBranchName(CMnPatch patch) {
        StringBuffer branch = new StringBuffer();

        // Customer name
        if ((patch.getCustomer() != null) && (patch.getCustomer().getShortName() != null)) {
            String cust = patch.getCustomer().getShortName().trim().toLowerCase();
            if (cust != null) {
                branch.append(cust.trim() + "_");
            }
        }

        // Environment name
        if ((patch.getEnvironment() != null) && (patch.getEnvironment().getShortName() != null)) {
            String env = patch.getEnvironment().getShortName().trim().toLowerCase();
            if (env != null) {
                branch.append(env.trim() + "_");
            }
        }

        // Product version
        if ((patch.getBuild() != null) && (patch.getBuild().getBuildVersion() != null)) {
            String vernum = getVersionNumber(patch.getBuild().getBuildVersion().trim().toLowerCase());
            if (vernum != null) {
                branch.append(vernum.trim() + "_");
            }
        }

        // Patch name
        if (patch.getName() != null) {
            branch.append(patch.getName().trim().toLowerCase());
        }

        return branch.toString();
    }


    /**
     * Manipulate the repository information to obtain git information.
     * Returns an array containing: type, root, branch, id
     *
     * @param  patch     Service patch information
     * @return Repository information
     */
    public static String[] getRepositoryInfo(CMnPatch patch) {
        String scmType   = null;
        String scmRoot   = null;
        String scmBranch = null;
        String scmId     = null;
        if ((patch != null) && (patch.getBuild() != null)) {
            scmType = patch.getBuild().getVersionControlType().toString();
            scmRoot = patch.getBuild().getVersionControlRoot();
            scmId = patch.getBuild().getVersionControlId();

            // Parse the git repository information 
            if (scmType.equalsIgnoreCase("git")) {
                // TODO clean up the build database so we don't have to strip off the branch information 
                int branchIndex = scmRoot.indexOf('/');
                if ((branchIndex > 0) && (branchIndex + 2 < scmRoot.length())) {
                    String repo = scmRoot.substring(0, branchIndex);
                    String branch = scmRoot.substring(branchIndex + 1);
                    scmRoot = repo;
                    scmBranch = branch;
                    if (scmBranch.startsWith("MN")) {
                        scmBranch = scmBranch.substring(2);
                    }
                }
            }
        }

        String[] info = { scmType, scmRoot, scmBranch, scmId };

        return info; 
    }

    /**
     * Create a Jenkins job name for the patch.  The format of the job name 
     * is <CUSTOMER>-<PRODUCT_VERSION>-<PATCH_NAME>.  If the patch data is
     * unavailable, an empty string will be returned.
     *
     * @param  patch    Service patch information
     * @return Job name 
     */
    public static final String getJobName(CMnPatch patch) {
        StringBuffer name = new StringBuffer();

        if (patch != null) {
            if ((patch.getCustomer() != null) && (patch.getBuild() != null)) {
                // Customer name (required)
                name.append(patch.getCustomer().getShortName().toUpperCase());
                name.append("-");

                // Environment name (optional)
                if (patch.getEnvironment() != null) {
                    String envName = patch.getEnvironment().getShortName();
                    if ((envName != null) && (envName.trim().length() > 0)) {
                        name.append(envName.toUpperCase().trim() + "-");
                    }
                }

                // Product version number (required)
                name.append(getVersionNumber(patch.getBuild().getBuildVersion()));
                name.append("-");

                // Service patch name (required)
                name.append(patch.getName().toUpperCase());
            }
        }

        return name.toString().toUpperCase();
    }

    /**
     * Return the Jenkins job name for the patch.  The "short" status is
     * appended to the job name to distinguish it from a full build.
     *
     * @param  patch    Service patch information
     * @return Job name 
     */
    public static final String getJobName(CMnPatch patch, boolean shortbuild) {
        String name = getJobName(patch);
        if (shortbuild) {
            return name + DEFAULT_SEPARATOR_CHAR + "SHORT";
        } else {
            return name;
        }
    }


    /**
     * Obtain a whitespace delimited list of e-mail recipients. 
     *
     * @param   patch   Service patch data
     * @return  E-mail recipients 
     */
    public static final String getNotificationList(CMnPatch patch) {
        StringBuffer recipients = new StringBuffer();

        if ((patch != null) && (patch.getNotifications() != null)) {
            InternetAddress[] list = patch.getCCList();
            for (int idx = 0; idx < list.length; idx++) {
                // Append a delimiter if the list is not empty
                if (recipients.length() > 0) {
                    recipients.append(" ");
                }

                // Append the current e-mail address 
                recipients.append(list[idx].getAddress());
            }
        }

        return recipients.toString();
    }


}

