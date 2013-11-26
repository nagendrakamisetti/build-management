/*
 * PropertyTree.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.common.branding;

import java.io.*;
import java.util.*;
import javax.swing.tree.*;

/**
 * The PropertyTree class constructs a tree of Properties objects which
 * are loaded from the text files containing Properties settings.  The
 * root directory is the location where the search for properties files
 * begins.  Any properties files found under the root directory will be
 * added as nodes in the tree.  
 * <p>
 * Methods are provided to retrieve a specific Properties object from a 
 * node or to create a dynamic Properties object which is composed of all 
 * settings found in the path from the root node.  Properties of the child
 * nodes will overwrite any properties found in the parent node so that
 * the most specific information resides at the leaf nodes and the most
 * general information can be inherited from the parent nodes.
 *
 * @version            $Revision: 1.1.1.1 $
 * @author             Shawn Stafford
 *
 */
public class PropertyTree {

    /** Contents of the properties tree */
    private DefaultTreeModel tree;

    /**
     * Constructs a tree by scanning the root directory (including
     * subdirectories) for any Properties files and loading the 
     * contents of those files into nodes on the tree.  The properties
     * file names are derived from the name of the target class and 
     * the file extension provided.
     *
     * @param   root        directory under which all properties reside
     * @param   target      name of the properties files
     * @param   extension   file extension for the properties files
     */
    public PropertyTree(File root, Class target, String extension) throws IOException {
        StringTokenizer className = new StringTokenizer(target.getName(), ".");
        int fileCount = className.countTokens();
        String[] files = new String[fileCount];
        String filename = "";

        // Create a list of all possible filenames derived from class name
        while (className.hasMoreTokens()) {
            // Make sure a delimiter isn't prepended to the beginning of the string
            if (filename.length() <= 0) {
                filename = className.nextToken();
            } else {
                filename = filename + "." + className.nextToken();
            }

            fileCount--;
            files[fileCount] = filename + extension;
        }

        // Construct the tree
        PropertyTreeNode rootNode = null;
        if ((root != null) && (files.length > 0)) {
            rootNode = loadRootNode(root, files);
            if (rootNode != null) {
                tree = new DefaultTreeModel(rootNode);
            }
        }

        if ((tree == null) || (tree.getRoot() == null)) {
            throw new IOException("Could not construct the root node of the PropertyTree.");
        }

    }

    /**
     * Constructs a tree by scanning the root directory (including
     * subdirectories) for any Properties files and loading the 
     * contents of those files into nodes on the tree.  Only those
     * files which match the target filename will be loaded into
     * the tree.
     *
     * @param   root    directory under which all properties reside
     * @param   target  name of the properties files
     */
    public PropertyTree(File root, String[] targets) throws IOException {
        PropertyTreeNode rootNode = null;
        if ((root != null) && (targets.length > 0)) {
            rootNode = loadRootNode(root, targets);
            if (rootNode != null) {
                tree = new DefaultTreeModel(rootNode);
            }
        }

        if ((tree == null) || (tree.getRoot() == null)) {
            throw new IOException("Could not construct the root node of the PropertyTree.");
        }

    }


    /**
     * Loads the root node for the tree.  This is a special case because
     * the root node must be resolved to an absolute path before any
     * further loading can be done.  Since the root node can be specified
     * as a relative path, the task of this method is to select the most
     * appropriate root if multiple paths exist.
     *
     */
    private PropertyTreeNode loadRootNode(File dir, String[] targets) 
        throws IOException 
    {
        PropertyTreeNode root = null;

        // Make sure you're searching for a valid directory location
        // This is critical because the directory passed to this method may only
        // be a relative directory, in which case it would resolve to the default
        // class location rather than searching for the actual location of the directory

        // If the directory is a relative path, it cannot be guaranteed that the
        // only one match will be found by the class loader within the classpath.
        // Locate all directories within the classpath that match the request
        File[] dirList = BestFileMatch.getFiles(dir);
        Vector nodes = new Vector();
        for (int idxDir = 0; idxDir < dirList.length; idxDir++) {
            if (dirList[idxDir].isDirectory()) {
                PropertyTreeNode currentRoot = loadNode(dirList[idxDir], targets);
                if (currentRoot != null) {
                    nodes.add(currentRoot);
                }
            }
        }

        // Multiple nodes are not allowed, so merge them if they exist
        if (nodes.size() == 1) {
            root = (PropertyTreeNode) nodes.firstElement();
        } else if (nodes.size() == 0) {
            if (dir.toString() == null) {
                throw new IOException("Cannot create the PropertyTreeNode.  Null node name is not allowed.");
            } else {
                root = new PropertyTreeNode(dir.toString());
            }
        } else {
            root = (PropertyTreeNode) nodes.firstElement();
            for (int idx = 1; idx < nodes.size(); idx++) {
                root = mergeTree(root, (PropertyTreeNode) nodes.get(idx));
            }
        }

        return root;
    }


    /**
     * Merges multiple PropertyTreeNodes into a single node.  The first node
     * in the list will have priority when the nodes are merged.
     *
     * @param   a   Node into which the other will be merged
     * @param   b   Node which will disappear once merged
     *
     * @return  PropertyTreeNode merged from multiple root nodes
     */
    private PropertyTreeNode mergeTree(PropertyTreeNode a, PropertyTreeNode b) {
        PropertyTreeNode currentNeighbor = null;
        PropertyTreeNode masterChild = null;

        // Merge the properties of the two nodes
        a.absorbNode(b);

        // Merge any children of the current node with the children of the neighboring tree node
        for (Enumeration children = a.children(); children.hasMoreElements(); ) {
            // Select an existing child from the original list
            masterChild = (PropertyTreeNode) children.nextElement();

            // Select matching children (by name) from neighboring tree
            currentNeighbor = getChild(b, masterChild.getName());;
            if (currentNeighbor != null) {
                masterChild = mergeTree(masterChild, currentNeighbor);
            }
        }

        // Any children that do not exist in the current node must be 
        // added from the levels being merged.
        for (Enumeration children = b.children(); children.hasMoreElements(); ) {
            currentNeighbor = (PropertyTreeNode) children.nextElement();

            // Add new children to the master tree
            masterChild = getChild(a, currentNeighbor.getName());;
            if (masterChild == null) {
                a.add(currentNeighbor);
            }
        }

        return a;
    }

    /**
     * Determines if the file matches the target and loads the properties
     * into a tree node when a target if found.  The node represents a directory
     * in the tree.  The node may contain multiple properties files.
     *
     * @param   dir     directory under which target properties reside
     * @param   targets names of the properties files
     */
    private PropertyTreeNode loadNode(File dir, String[] targets) throws IOException {
        // Construct a node for the current directory
        File[] targetFiles = new File[targets.length];
        for (int idx = 0; idx < targets.length; idx++) {
            targetFiles[idx] = new File(dir, targets[idx]);
        }

        // Process each subdirectory within the current directory
        Vector validNodes = new Vector();
        File[] subFiles = dir.listFiles();
        for (int idxSub = 0; idxSub < subFiles.length; idxSub++) {
            // Make recursive call to load the node of the subdirectory
            if (subFiles[idxSub].isDirectory()) {
                PropertyTreeNode child = loadNode(subFiles[idxSub], targets);
                if (child != null) {
                    validNodes.add(child);
                }
            }
        }

        // Once all subdirectories have been processed, if valid children 
        // have been found, or the current directory contains valid
        // target files, then create the current node and add the children
        PropertyTreeNode current = new PropertyTreeNode(dir.getName(), targetFiles);
        if ((current.countFiles() <= 0) && (validNodes.size() <= 0)) {
            current = null;
        } else {
            // Add the children to the current node
            PropertyTreeNode child = null;
            for (Enumeration nodeList = validNodes.elements(); nodeList.hasMoreElements(); ) {
                child = (PropertyTreeNode)nodeList.nextElement();
                current.add(child);
            }
        }

        return current;
    }


    /**
     * Retrieves a setting from the most specific node possible.  If no value
     * can be found, null is returned.
     *
     * @param   key     Name of the property to be located
     * @param   path    Search path to the suggested setting
     */
    public String getProperty(String key, String[] path) {
        String value = null;

        // Search each node in the path, beginning at the leaf
        PropertyTreeNode[] nodeList = getNodesAlongPath(path);
        for (int idx = (nodeList.length - 1); idx >= 0; idx--) {
            value = nodeList[idx].getProperty(key);
            if (value != null) {
                return value;
            }
        }

        return null;
    }

    /**
     * Returns the list of nodes found along the given path.  This assumes
     * that the path begins at the root.
     *
     * @param   path    location of the nodes
     * @return  Array of nodes along the path
     */
    public PropertyTreeNode[] getNodesAlongPath(String[] path) {
        int nodeCount = path.length + 1;
        boolean childrenExist = true;
        Vector nodeList = new Vector(nodeCount);

        // Start at the root
        PropertyTreeNode parent = (PropertyTreeNode) tree.getRoot();
        nodeList.add(parent);

        // Search for children
        int idxChild = 1;
        PropertyTreeNode child = null;
        while (childrenExist && (idxChild < nodeCount)) {
            child = getChild(parent, path[idxChild - 1]);
            idxChild++;
            if (child == null) {
                childrenExist = false;
            } else {
                nodeList.add(child);
                parent = child;
            }
        }


        // Convert the vector to an array
        PropertyTreeNode[] nodes = new PropertyTreeNode[nodeList.size()];
        for (int idx = 0; idx < nodes.length; idx++) {
            nodes[idx] = (PropertyTreeNode) nodeList.get(idx);
        }

        return nodes;
    }
    

    /**
     * Returns the child node with the given name.
     *
     * @param   parent  Parent node
     * @param   name    Name of child node
     * @return  child node
     */
    public PropertyTreeNode getChild(PropertyTreeNode parent, String name) {
        PropertyTreeNode childNode = null;

        int childCount = parent.getChildCount();
        for (int idx = 0; idx < childCount; idx++) {
            childNode = (PropertyTreeNode) tree.getChild(parent, idx);
            if ((childNode != null) && name.equals(childNode.getName())) {
                return childNode;
            }
        }

        return null;
    }

}
