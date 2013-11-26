/*
 * BestPropertiesMatch.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.common.branding;

import java.io.*;
import java.util.Vector;
import java.net.URL;
import java.util.Properties;

/**
 * The BestPropertiesMatch class contains methods for obtaining the most
 * specific resource for a prioritized list of identifiers.
 *
 * @version            $Revision: 1.1.1.1 $
 * @author             Shawn Stafford
 *
 */
public class BestPropertiesMatch extends BestFileMatch {

    private String      rootDir;
    private String      targetName;
    private String[]    targetList;
    private String[]    pathList;
    private ClassLoader fileLoader;

    /**
     * Constructs an object using the prioritized list of resources.
     * The root directory specified when constructing the resource should be
     * a relative path if resources are to be located effectively in all
     * possible locations.  Specifying an absolute or canonical path will
     * force the resource loader to search only in that location.
     * <p>
     *
     * The order of the resources within the list determines their
     * specificity.  For example, consider a real-world case in which
     * you're searching for something to entertain your baby, preferably
     * a red wooden block that you keep in a toy-chest in the bedroom.  
     * But if the item cannot be found in this specific location, you 
     * may have to broaden your search to other areas in the search path.  
     * Consider a BestMatch object composed
     * with the following target items:
     * <pre>
     *    Target              Path
     *    ===============     =================
     *    [0] = wood          [0] = house
     *    [1] = square        [1] = bedroom
     *    [2] = red           [2] = toy-chest
     *    [3] = small
     * </pre>
     * The first target in the list (wood) is considered the most general
     * identifier.  Subsequent identifiers are combined with their
     * predecessors to construct the most specific resource possible.  
     * Similarly, the search path is combined to create the most specific
     * search path possible.  The search path is then traversed in search
     * of the most specific resource available.  
     * <p>
     *
     * If this list were used to construct a BestMatch object, the list of
     * possible resources which might match would include the following:
     * <pre>
     *    /house/bedroom/toy-chest/wood.square.small.red.TOY
     *    /house/bedroom/toy-chest/wood.square.red.TOY
     *    /house/bedroom/toy-chest/wood.red.TOY
     *    /house/bedroom/toy-chest/red.TOY
     *    /house/bedroom/toy-chest/TOY
     *
     *    /house/bedroom/wood.square.small.red.TOY
     *    /house/bedroom/wood.square.red.TOY
     *    /house/bedroom/wood.red.TOY
     *    /house/bedroom/red.TOY
     *    /house/bedroom/TOY
     *
     *    /house/wood.square.small.red.TOY
     *    /house/wood.square.red.TOY
     *    /house/wood.red.TOY
     *    /house/red.TOY
     *    /house/TOY
     * </pre>
     * The list shown above is ordered from the best match 
     * (the red wooden square in the toy chest) to the least 
     * specific match (some TOY you found in the house).
     *
     * @param   root    root directory for the search path
     * @param   target  Target name
     * @param   prefix  Ordered list of target prefixes
     * @param   path    Ordered list of resources where a target can be found
     */
    public BestPropertiesMatch(String root, String target, String[] prefix, String[] path) {
        fileLoader = ClassLoader.getSystemClassLoader();
        rootDir = root;
        targetName = target;
        targetList = prefix;
        pathList = path;
    }

    
    /**
     * Locates the most specific resource that exists and loads its
     * contents into the Properties object.
     *
     * @return  Properties merged from all available resources
     */
    public Properties loadProperties() throws IOException {
        Properties properties = new Properties();
        return loadProperties(properties);
    }


    /**
     * Locates the most specific resource that exists and loads its
     * contents into the Properties object.  The newly loaded
     * resources will replace any existing resources in the supplied
     * Properties object.
     *
     * @param   properties  existing properties list
     *
     * @return  Properties merged from all available resources
     */
    public Properties loadProperties(Properties properties) throws IOException {
        // Load the property files in reverse
        File[] files = getRealFileList(rootDir, targetName, targetList, pathList);
        for (int idx = 0; idx < files.length; idx++) {
            try {
                properties.load(new FileInputStream(files[idx]));
                return properties;
            } catch (Exception e) {
System.err.println("Exception thrown while trying to load properties file: " + files[idx]);
            }
        }

        return properties;
    }


    /**
     * Locates any resources found in the resource list and merges them into
     * a single Properties object.  The most specific resources will be loaded
     * last so that they will overwrite any of the more generic properties.
     *
     * @return  Properties merged from all available resources
     */
    public Properties loadAllProperties() throws IOException {
        Properties properties = new Properties();
        return loadAllProperties(properties);
    }

    /**
     * Locates any resources found in the resource list and merges them into
     * the given Properties object.  The newly loaded properties will overwrite
     * any properties which existed in the supplied Properties object.
     *
     * @param   properties  existing properties list
     *
     * @return  Properties merged from all available resources
     */
    public Properties loadAllProperties(Properties properties) throws IOException {
        File[] files = getFileList(rootDir, targetName, targetList, pathList);
        for (int idx = 0; idx < files.length; idx++) {
            properties = loadProperties(properties);
        }

        return properties;
    }

}
