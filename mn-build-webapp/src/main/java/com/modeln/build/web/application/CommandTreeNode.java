/*
 * CommandTreeNode.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.web.application;

import java.util.*;
import javax.swing.tree.*;


/**
 * A CommandTreeNode defines a tree node containing a node name
 * and a list of Classes associated with that node.  If the list
 * of classes is null, that means no classes were found.
 *
 * @version            $Revision: 1.1.1.1 $
 * @author             Shawn Stafford
 *
 */
public class CommandTreeNode extends DefaultMutableTreeNode {

    /** Class list to be loaded for this node. */
    private Hashtable commandList;
    
    /** Name associated with the node. */
    private String nodeName;

    /** Debugging statements will print out when debugging is on. */
    private boolean debug = true;

    /** 
     * Name of the package with which the commands are associated.  This will
     * be used to load the commands based on fully qualified class name.
     */
    private String nodePackage;

    /**
     * Constructs an empty tree node with the given name.
     *
     * @param   name    node name
     * @param   pkg     name of the package associated with this node
     */
    public CommandTreeNode(String name, String pkg) {
        nodeName = name;
        nodePackage = pkg;
        commandList = new Hashtable();
    }


    /**
     * Constructs a tree node with the given name and the given list of
     * commands.
     *
     * @param   name        node name
     * @param   pkg         name of the package associated with this node
     * @param   commands    list of command classes
     */
    public CommandTreeNode(String name, String pkg, Command[] commands) {
        this(name, pkg);

        // populate the hashtable with commands
        for (int idx = 0; idx < commands.length; idx++) {
            addCommand(commands[idx]);
        }
    }


    /**
     * Adds a command to the list for this node.
     *
     * @param   command     Web application command
     */
    public void addCommand(Command command) {
        String name = (command.getClass()).getName();

        // Use only the class name for the key, not the fully qualified name
        int shortNameIdx = name.lastIndexOf(".");
        if ((shortNameIdx >= 0) && (name.length() > shortNameIdx)) {
            name = name.substring(shortNameIdx + 1);
        }

        commandList.put(name, command);
    }
    
    /**
     * Returns the requested command from the node if it exists.
     *
     * @param   command     Command name
     * @return  Command (null if command does not exist)
     */
    public Command getCommand(String command) {
        return (Command)commandList.get(command);
    }

    /**
     * Returns the package associated with this node.  The package
     * name identifies the package to which the commands belong.
     *
     * @return String fully qualified package name.
     */
    public String getPackage() {
        return nodePackage;
    }


    /**
     * Returns the name of the node.
     *
     * @return String node name
     */
    public String getName() {
        return nodeName;
    }


    /**
     * Attempt to load the command class from the file system.
     *
     * @param   name    command name
     * @param   path    path along the tree
     * 
     * @return Command located by the search
     */
    public Command loadCommand(String name) {
        String className = nodePackage + "." + name;
        Command command = null;

System.err.println("********************* CommandTreeNode ***********************");
        try {
            Class commandObj = Class.forName(className);
            if (debug) System.err.println("Class loaded: " + commandObj.getName());
            command = (Command)(commandObj.newInstance());
        } catch (ClassNotFoundException nfe) {
            if (debug) {
                System.err.println("Could not locate class: " + className);
                System.err.println(nfe.toString());
            }
        } catch (IllegalAccessException iae) {
            if (debug) {
                System.err.println("Could not create a new instance of the class: " + className);
                System.err.println(iae.toString());
            }
        } catch (InstantiationException ie) {
            if (debug) {
                System.err.println("Could not create a new instance of the class: " + className);
                System.err.println(ie.toString());
            }
        } catch (ExceptionInInitializerError eie) {
            if (debug) {
                System.err.println("Could not create a new instance of the class: " + className);
                System.err.println(eie.toString());
            }
        } catch (SecurityException se) {
            if (debug) {
                System.err.println("Could not create a new instance of the class: " + className);
                System.err.println(se.toString());
            }
        } catch (ClassCastException ce) {
            if (debug) {
                System.err.println("Class is not a command: " + className);
                System.err.println(ce.toString());
            }
        }

        return command;
    }


}
