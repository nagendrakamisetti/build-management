/*
 * GroupTree.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.common.data.account;

import javax.swing.tree.*;

/**
 * The GroupTree represents a hierarchical list of groups.
 *
 * @version            $Revision: 1.1.1.1 $
 * @author             Shawn Stafford
 *
 */
public class GroupTree extends DefaultTreeModel {

    /** Group ID parameter used for URL encoding of group data */
    public static final String PARAM_GID = "gid";

    /** Action parameter used for URL encoding of group actions */
    public static final String PARAM_ACTION = "action";

    /** Action value indicating an EXPAND action for a group */
    public static final String ACTION_EXPAND = "expand";

    /** Action value indicating a SELECT action for a group */
    public static final String ACTION_SELECT = "select";

    /** Action value indicating an COLLAPSE action for a group */
    public static final String ACTION_COLLAPSE = "collapse";

    /** Indicates the currently selected node in the tree */
    private GroupTreeNode selected;

    /**
     * Construct the group tree with the given node as root.
     */
    public GroupTree(GroupTreeNode root) {
        super(root);
        selected = root;
    }

    /**
     * Sets the currently selected group.
     */
    public void setSelected(String gid) {
        GroupTreeNode current = (GroupTreeNode)getRoot();
        GroupData data = current.getData();
        if (gid.equals(data.getGid())) {
            selected = current;
        } else {
            selected = getChildById(current, gid);
        }

    }

    /** 
     * Returns the group information for the currently selected node.
     */
    public GroupTreeNode getSelected() {
        return selected;
    }

    /**
     * Performs a depth-first search of the tree to 
     * locate the given group by its group ID.
     *
     * @param   gid     Group ID
     */
    public GroupTreeNode getGroupById(String gid) {
        return getChildById((GroupTreeNode)getRoot(), gid);
    }
    
    /**
     * Performs a depth-first search of the group tree starting
     * at the current node and returns the group matching the given ID.
     * If the child cannot be found, null is returned.
     */
    private GroupTreeNode getChildById(GroupTreeNode root, String gid) {
        GroupTreeNode child = null;
        int childCount = getChildCount(root);

        // Check each child of the current node
        GroupTreeNode currentChild = null;
        GroupData data = null;
        for (int idxChild = 0; idxChild < childCount; idxChild++) {
            currentChild = (GroupTreeNode)getChild(root, idxChild);
            if (currentChild != null) {
                data = currentChild.getData();
                if (gid.equals(data.getGid())) {
                    return currentChild;
                } else {
                    // Recursively search for children
                    child = getChildById(currentChild, gid);
                    if (child != null) return child;
                }
            }
        }

        return child;
    }

    /**
     * Renders the tree using an HTML table.
     *
     * @param   expanded    Path to the image for an expanded node
     * @param   collapsed   Path to the image for a collapsed node
     * @param   url     The root node URL that should be linked to the node name
     */
    public String toHtml(String url, String expanded, String collapsed) {
        StringBuffer html = new StringBuffer("<table border='0' cellspacing='0' cellpadding='0'>");
        html.append(toHtml((GroupTreeNode)getRoot(), url, expanded, collapsed));
        html.append("</table>");

        return html.toString();
    }

    /**
     * Renders the node as a row in an HTML table.
     *
     * @param   root    Current node of the tree
     * @param   expanded    Path to the image for an expanded node
     * @param   collapsed   Path to the image for a collapsed node
     * @param   url     The root node URL that should be linked to the node name
     */
    private String toHtml(GroupTreeNode root, String url, String expanded, String collapsed) {
        StringBuffer html = new StringBuffer();
        int childCount = getChildCount(root);

        // Render the root node
        GroupData rootData = root.getData();
        for (int preCols = 0; preCols < root.getLevel(); preCols++) {
            html.append("<td></td>");
        }

        String preAction = "<a href='" + url + "?" + PARAM_GID + "=" + rootData.getGid() + "+" + PARAM_ACTION + "=";
        String postAction = "'>";
        String preImg = "<img border='0' src='";
        String postImg = "'>";
        if (root.getExpanded()) {
            // The link should cause the expanded node to collapse
            html.append("<td>" + preAction + ACTION_COLLAPSE + postAction + preImg + expanded + postImg + "</a></td>");

            // Render each child of the current node
            for (int idxChild = 0; idxChild < childCount; idxChild++) {
                html.append(toHtml((GroupTreeNode)getChild(root, idxChild), url, expanded, collapsed));
            }
        } else {
            // The link should cause the collapsed node to expand
            html.append("<td>" + preAction + ACTION_EXPAND + postAction + preImg + collapsed + postImg + "</a></td>");
        }

        html.append("<td>" + preAction + ACTION_SELECT + postAction + rootData.getName() + "</a></td>");

        return html.toString();
    }

}
