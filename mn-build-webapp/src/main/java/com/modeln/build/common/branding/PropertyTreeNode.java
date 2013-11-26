/*
 * ClassTreeNode.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.common.branding;

import java.io.*;
import java.util.*;
import javax.swing.tree.*;


/**
 * A ClassTreeNode defines a tree node containing a node name
 * and a Properties object which contains Properties for that node.
 * If the Properties object for the node is null, it means that
 * no Properties could be loaded for the current node.
 *
 * @version            $Revision: 1.1.1.1 $
 * @author             Shawn Stafford
 *
 */
public class PropertyTreeNode extends DefaultMutableTreeNode {

    /** Properties associated with this node */
    private Vector properties;

    /** Name associated with the node. */
    private String nodeName;


    /**
     * Constructs an empty node.
     *
     * @param   name    node name
     */
    public PropertyTreeNode(String name) {
        nodeName = name;
        properties = new Vector();
    }

    /**
     * Constructs a tree node with the given name and contents of the
     * given properties files.  If multiple targets are given, the order of
     * the target list will determine in what order the properties are
     * returned.  The first element in the target list will be considered
     * the least generic (most specific) resource and the last element in 
     * the list will be considered the most generic (least specific) 
     * resource.
     *
     * @param   name    node name
     * @param   targets the properties files
     *
     * @throws  IOException when the property file cannot be loaded
     */
    public PropertyTreeNode(String name, File[] targets) 
        throws IOException
    {
        this(name);

        // Load the properties from file
        Properties current = null;
        for (int idx = 0; idx < targets.length; idx++) {
            if (targets[idx].canRead()) {
                current = new Properties();
                current.load(new FileInputStream(targets[idx]));
                properties.add(current);
            }
        }

    }

    /**
     * Returns the name of the child node.
     *
     * @return String name of the child node
     */
    public String getName() {
        return nodeName;
    }

    /**
     * Returns the total number of Properties files associated with the node.
     *
     * @return  int     number of Properties files for this node
     */
    public int countFiles() {
        return properties.size();
    }

    /**
     * Returns the most specific property which can be found in the node.
     * If the property cannot be found, null is returned.
     *
     * @param   key     the property key
     */
    public String getProperty(String key) {
        String value = null;

        for (int idx = 0; idx < properties.size(); idx++) {
            value = ((Properties)properties.get(idx)).getProperty(key);
            if (value != null) return value;
        }

        return value;
    }

    /**
     * Returns the most specific property which can be found in the node.
     * If the property cannot be found, the default value is returned.
     *
     * @param   key          the property key
     * @param   defaultValue the default value to return if none is found
     */
    public String getProperty(String key, String defaultValue) {
        String val = getProperty(key);
        if (val != null) {
            return val;
        } else {
            return defaultValue;
        }
    }

    /**
     * Sets the most specific property which can be found in the node.
     * If the property cannot be found, it will be added to the most
     * specific location.
     *
     * @param   key     the key to be placed into this property list
     */
    public void setProperty(String key, String value) {
        String existingValue = null;

        // Search until you've found the most specific entry
        boolean keyFound = false;
        for (int idx = 0; idx < properties.size(); idx++) {
            existingValue = ((Properties)properties.get(idx)).getProperty(key);

            // Only set the property once when found
            if ((keyFound == false) && (existingValue != null)) {
                ((Properties)properties.get(idx)).setProperty(key, value);
                keyFound = true;
            }
        }

        // If the key did not already exist, create it
        if ((keyFound == false) && (properties.size() > 0)) {
            ((Properties)properties.firstElement()).setProperty(key, value);
        }

    }

    /**
     * Returns the name of the node.
     */
    public String toString() {
        return nodeName;
    }


    /**
     * Returns the list of Properties associated with the node.
     * 
     * @return  Vector  list of Properties
     */
    protected Vector getPropertyList() {
        return properties;
    }

    /**
     * Merges the Properties of the given node into the current node.
     * The Properties existing in the current node will be given priority
     * over the Properties from the given node when they are retrieved
     * using the getProperty method.  However, all Properties will still
     * be preserved once the nodes have been merged.
     *
     * @param   node    Node to be absorbed into the current node
     */
    public void absorbNode(PropertyTreeNode node) {
        // Add the absorbed properties in to the current node
        Vector newProperties = node.getPropertyList();
        for (int idx = 0; idx < newProperties.size(); idx++) {
            properties.add(newProperties.get(idx));
        }
    }

}
