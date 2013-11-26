/*
 * BestClassMatch.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.common.branding;

import java.io.*;
import java.util.Vector;

/**
 * The BestClassMatch class contains methods for obtaining the most
 * specific resource for a prioritized list of identifiers.
 *
 * @version            $Revision: 1.1.1.1 $
 * @author             Shawn Stafford
 *
 */
public class BestClassMatch {

    private static final String pathDelimiter = ".";


    /**
     * Returns a list of all possible classes which would be considered
     * as matches.  The most specific resource is the first item in the list
     * and the least specific resource appears as the last item in the list.
     * <b>The list will contain all possible classes, regardless of whether they exist.</b>
     * <p>
     * The number of files returned will a function of the following:<br>
     * root = a.b.c
     * path = { x, y, z } <br>
     * class count = (p + 1) <br>
     * <p>
     * a.b.c.x.y.z.class
     * a.b.c.x.y.class
     * a.b.c.x.class
     * a.b.c.class
     * <p>
     * 
     * @param   root    root package for the class search
     * @param   target  Target class name
     * @param   path    Ordered list of resources where a target can be found
     * 
     * @return String[] list of matching resources
     */
    public static String[] getClassList(String root, String target, String[] path) {
        // The count represents the total number of files which should 
        // ultimately appear in the file list.
        int count = (path.length + 1);
        Vector resources = new Vector(count);

        // Construct the resource list
        String strPath = root;
        int resourceIdx = count;
        for (int idxPath = 0; idxPath < (path.length + 1); idxPath++) {
            // Make sure to account for the first path entry (root)
            if (idxPath > 0) {
                strPath = strPath + path[idxPath - 1];
            }

            // Make sure the path delimiter has been appended
            if ((strPath.length() > 0) && !strPath.endsWith(pathDelimiter)) {
                strPath = strPath + pathDelimiter;
            }

            resources.add(strPath + target);

        }

        // Place the file list into an array and return the array
        String[] files = new String[resources.size()];
        for (int idx = 0; idx < resources.size(); idx++) {
            files[idx] = (String) resources.get(idx);
        }

        return files;
    }


}
