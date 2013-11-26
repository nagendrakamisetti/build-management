/*
 * CommandTree.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.web.application;

import java.io.*;
import java.util.*;
import javax.swing.tree.*;


/**
 * The CommandTree class constructs a tree of MemberServicesCommand classes
 * which are loaded from class files. The root directory is the location 
 * where the search for command files begins.  The command files are loaded
 * only upon demand, populating the tree nodes as commands are requested.
 * Methods are provided to retrieve a specific command from a node
 * <p>
 * @version            $Revision: 1.1.1.1 $
 * @author             Shawn Stafford
 *
 */
public class CommandTree {

    /** Contents of the command tree */
    private DefaultTreeModel tree;

    /** Root package where the command search begins. */
    private String rootPackage;

    /**
     * Constructs a tree under which all of the commands will be loaded.
     * The tree is initially empty when constructed.  The tree will be
     * populated with commands as they are requested.  The root of the
     * command structure is specified as the package under which all
     * commands can be found.  For example, if E-mail application commands
     * are found in the <code>net.excitehome.ws.email.command</code>
     * package, then this would be the root of the tree.  The path
     * specified when retriving a command will be appended on to this
     * root in order to provide a best match search mechanism for
     * locating the most appropriate command for a given path.
     *
     * @param   root    package under which all commands reside
     */
    public CommandTree(String root) {
        rootPackage = root;
        CommandTreeNode rootNode = new CommandTreeNode("", rootPackage);

        // Construct the tree
        tree = new DefaultTreeModel(rootNode);
    }

    /**
     * Returns a command from the tree.  The search will attempt to find
     * the best match for the requested command by beginning its search
     * at the leaf of the specified path and then traversing back up the
     * tree until a match can be found.  If no match is found, a null
     * will be returned.
     * 
     * @param   name    command name
     * @param   path    path along the tree
     * 
     * @return MemberServicesCommand located by the search
     */
    public Command getCommand(String name, String[] path) {
        Command command = null;

        // Search each node in the path, beginning at the leaf
        CommandTreeNode[] nodeList = getNodesAlongPath(path);
        for (int idx = (nodeList.length - 1); idx >= 0; idx--) {
            command = nodeList[idx].getCommand(name);
            if (command != null) {
                return command;
            } else {
                // Attempt to load the command
                try {
                    command = nodeList[idx].loadCommand(name);
                    if (command != null) return command;
                } catch (Exception ex) {
                }
            }
        }

        return null;
    }

    
    /**
     * Returns the list of nodes found along the given path.  This assumes
     * that the path begins at the root.  If a node does not already exist 
     * along the path, it will be created and returned as part of the list.
     *
     * @param   path    location of the nodes
     * @return  Array of nodes along the path
     */
    public CommandTreeNode[] getNodesAlongPath(String[] path) {
        CommandTreeNode[] nodeList = new CommandTreeNode[path.length + 1];
        nodeList[0] = (CommandTreeNode) tree.getRoot();
        for (int idx = 1; idx < nodeList.length; idx++) {
            nodeList[idx] = getChild(nodeList[idx - 1], path[idx - 1]);
        }

        return nodeList;
    }
    

    /**
     * Returns the child node with the given name.  If the child node
     * does not already exist, it will be created and returned.
     *
     * @param   parent  Parent node
     * @param   name    Name of child node
     * @return  child node
     */
    public CommandTreeNode getChild(CommandTreeNode parent, String name) {
        CommandTreeNode childNode = null;

        // Attempt to locate an existing node
        int childCount = parent.getChildCount();
        for (int idx = 0; idx < childCount; idx++) {
            childNode = (CommandTreeNode) tree.getChild(parent, idx);
            if (name.equals(childNode.getName())) {
                return childNode;
            }
        }

        // Create a new node if no child was found
        String pkgName = parent.getPackage() + "." + name;
        childNode = new CommandTreeNode(name, pkgName);
        parent.add(childNode);

        return childNode;
    }



}
