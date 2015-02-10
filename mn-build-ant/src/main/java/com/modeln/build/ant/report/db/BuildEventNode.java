/*
 * Copyright 2000-2003 by Model N, Inc.  All Rights Reserved.
 * 
 * This software is the confidential and proprietary information
 * of Model N, Inc ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you
 * entered into with Model N, Inc.
 */
package com.modeln.build.ant.report.db;

import java.util.ArrayList;

import org.apache.tools.ant.BuildEvent;

/**
 * The build event node represents a single node in the build event tree.  An
 * event node contains an EventObject and references to any parents and children
 * that are related to the current node.
 *
 * @author Shawn Stafford
 */
public class BuildEventNode {


    /** 
     * References the parent node in the tree.  If null, the current node
     * is assumed to be a root node in the tree.
     */
    private BuildEventNode parent;

    /**
     * List of children in the tree.
     */
    private ArrayList<BuildEventNode> children = null;

    /** Build event associated with the current node */
    private BuildEvent event;


    /**
     * Construct a new node in the tree and link it to the parent node.
     *
     * @param   event   Build event being stored in the node
     * @param   parent  Reference to the parent node in the event tree
     */
    public BuildEventNode(BuildEvent event, BuildEventNode parent) {
        this.parent = parent;
        this.event = event;
    }

    /** 
     * Return the build event for the current node.
     */
    public BuildEvent getEvent() {
        return event;
    }

    /**
     * Return a reference to the parent of the current node.  If the parent
     * is null, the current node is assumed to be the root of the tree.
     *
     * @return  Parent of the current node
     */
    public BuildEventNode getParent() {
        return parent;
    }

    /**
     * Return a list of children associated with the current node.
     *
     * @return  List of child nodes
     */
    public ArrayList<BuildEventNode> getChildren() {
        return children;
    }

    /**
     * Determine if the child node is a direct descendent of the current node.
     * If the node is a direct descendent, then the child node is returned 
     * (containing the appropriate references to parents and children).  If the
     * node is not a direct descendent, null will be returned.
     *
     * @param   node    Child node to be located
     * @return  Matching node or null if none found
     */
    public BuildEventNode getDirectChild(BuildEventNode node) {
        BuildEventNode child = null;
        for (int idx = 0; idx < children.size(); idx++) {
            if (node.equals((BuildEventNode)children.get(idx))) {
                child = (BuildEventNode)children.get(idx);
            }
        }

        return child;
    }

    /**
     * Locate the child node anywhere below the current node.  If the child 
     * is not a direct descendent of the current node, the tree will be searched
     * to the leaf nodes to locate the child.  If no match is found, a null is
     * returned.
     *
     * @param   node    Child node to be located
     * @return  Matching node or null if none found
     */
    public BuildEventNode findChild(BuildEventNode node) {
        BuildEventNode targetChild = null;
        BuildEventNode currentChild = null;

        for (int idx = 0; idx < children.size(); idx++) {
            currentChild = (BuildEventNode)children.get(idx);

            // Select the current child or continue a depth-first search
            if (node.equals(currentChild)) {
                targetChild = currentChild;
            } else {
                targetChild = currentChild.findChild(node);
            }

            // Exit the recursion if a match is found
            if (targetChild != null) {
                return targetChild;
            }
        }

        return null;
    }

    /**
     * Determines if the current node is equal to the given node.  Two nodes
     * are equal if they have the same build event task, target, and project
     * name.
     */
    public boolean equals(BuildEventNode node) {
        // Bail out early if there's nothing to do
        if (node == null) return false;
        BuildEvent newEvent = node.getEvent();
        if ((event == null) || (newEvent == null)) return false;

        boolean match = false;
        if (event.getTask() != null) {
            // Obtain the information about the current node event
            String nodeEventLocation = event.getTask().getLocation().toString();
            String nodeEventName = event.getTask().getTaskName();

            // Compare the current node information to the new node information
            if (newEvent.getTask() != null) {
                String location = newEvent.getTask().getLocation().toString();
                String name = newEvent.getTask().getTaskName();
                if (location.equals(nodeEventLocation) && name.equals(nodeEventName)) {
                    match = true;
                }
            }
        } else if (event.getTarget() != null) {
            // Obtain the information about the current node event
            String nodeTargetName = event.getTarget().getName();
            String nodeProjectName = event.getProject().getName();

            // Compare the current node information to the new node information
            if (newEvent.getTarget() != null) {
                String targetName = newEvent.getTarget().getName();
                String projectName = newEvent.getProject().getName();
                if (targetName.equals(nodeTargetName) && projectName.equals(nodeProjectName)) {
                    match = true;
                }
            }
        } else if (event.getProject() != null) {
            // Obtain the information about the current node event
            String nodeProjectName = event.getProject().getName();

            // Compare the current node information to the new node information
            if (newEvent.getProject() != null) {
                String projectName = newEvent.getProject().getName();
                if (projectName.equals(nodeProjectName)) {
                    match = true;
                }
            }
        }


        return match;
    }

    /**
     * Conditionally adds a node to the tree if it does not already exist
     * in the current list of children.
     *
     * @param   child   Node to be added
     */
    public void addChild(BuildEventNode child) {
        if (children == null) {
            // Initialize the list of child nodes if it does not exist
            children = new ArrayList<BuildEventNode>();
            children.add(child);
        } else {
            // Determine if the child node already exists
            if (getDirectChild(child) == null) {
                children.add(child);
            }
        }
    }

    /**
     * Obtain the path to the current node by traversing the path to the
     * root node from the bottom up.
     *
     * @return  Path of nodes to the root
     */
    public BuildEventNode[] getPathToRoot() {
        BuildEventNode[] path = null;
        if (parent != null) {
            ArrayList<BuildEventNode> pathList = constructPath();

            // Convert the list to an array
            path = new BuildEventNode[pathList.size()];
            for (int idx = 0; idx < pathList.size(); idx++) {
                path[idx] = (BuildEventNode)pathList.get(idx);
            }
        } else {
            path = new BuildEventNode[1];
            path[0] = this;
        }

        return path;
    }

    /**
     * Insert the current node into the path at a specific position.
     * The length of the path array must be the same as the depth at
     * the current node.
     *
     * @param   path    List of nodes that form a path in the tree
     */
    protected ArrayList<BuildEventNode> constructPath() {
        ArrayList<BuildEventNode> path = null;
        if (parent != null) {
            path = parent.constructPath();
        } else {
            path = new ArrayList<BuildEventNode>();
        }
        path.add(this);

        return path;
    }


    /**
     * Obtain the number of nodes to the root node.  The number of nodes
     * includes the current node, the root node, and all nodes in between.
     *
     * @return  Number of nodes in the path to the root
     */
    public int getDepth() {
        int depth = 0;
        if (parent != null) {
            depth = parent.getDepth() + 1;
        }
        return depth;
    }


    /**
     * Print the contents of the tree to stdout.
     */
    public static void printNode(BuildEventNode node) {
        if ((node != null) && (node.getEvent() != null)) {
            StringBuffer sb = new StringBuffer();
            for (int idx = 0; idx < node.getDepth(); idx++) {
                sb.append("  ");
            }
            System.out.println(sb.toString() + node.getEvent().toString());
            ArrayList<BuildEventNode> nodeChildren = node.getChildren();
            if (nodeChildren != null) {
                for (int idx = 0; idx < nodeChildren.size(); idx++) {
                    printNode((BuildEventNode)nodeChildren.get(idx));
                }
            }
        } else {
            System.out.println("No Event information.");
        }
    }


}
