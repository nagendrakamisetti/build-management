/*
 * GroupTreeNode.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.common.data.account;


import javax.swing.tree.*;

/**
 * The GroupTreeNode represents a buffered node of the group tree.
 * The node has methods to determine whether the node data has been
 * retrieved from the data source or must be retrieved.
 *
 * @version            $Revision: 1.1.1.1 $
 * @author             Shawn Stafford
 */
public class GroupTreeNode extends DefaultMutableTreeNode {

    /** Determines whether the group information is complete at this node level */
    private boolean nodeComplete = false;

    /** Determines whether the node should be displayed as collapsed or expanded. */
    private boolean nodeExpanded = false;

    /**
     * Construct the group tree node the given data.
     *
     * @param   group   Group data for the current node
     */
    public GroupTreeNode(GroupData group) {
        super(group);
    }

    /**
     * Determines whether the node information is complete or must
     * be retrieved from the database.
     *
     * @param   isComplete   TRUE if the node information is complete
     */
    public void setComplete(boolean isComplete) {
        nodeComplete = isComplete;
    }

    /**
     * Returns the status of the node data.  If the data is marked as
     * complete and up-to-date, then the method will return TRUE.
     *
     * @return TRUE if the data is complete and up-to-date
     */
    public boolean getComplete() {
        return nodeComplete;
    }

    /**
     * Determines whether the node should be displayed as
     * expanded to indicate any children it might possess.
     * An incomplete node should never be expanded.
     *
     * @param   isExpanded   TRUE if the node should appear expanded
     */
    public void setExpanded(boolean isExpanded) {
        if (nodeComplete) {
            nodeExpanded = isExpanded;
        }
    }

    /**
     * Returns TRUE if the node should be displayed as expanded.
     * A node cannot be expanded unless it is marked as complete
     * and up-to-date.
     *
     * @return TRUE if the node is expanded
     */
    public boolean getExpanded() {
        return (nodeComplete && nodeExpanded);
    }

    /**
     * Returns the group data associated with the node.
     *
     * @return  Group data contained in the node
     */
    public GroupData getData() {
        return (GroupData)getUserObject();
    }
}
