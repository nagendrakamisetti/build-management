/**
 * SelectTag.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.web.tags;
 
import java.util.Enumeration;
import java.util.Vector;
import java.util.Hashtable;

/**
 * The select tag corresponds to a list selection box or drop-down
 * box.
 *
 * @version            $Revision: 1.3 $
 * @author             Shawn Stafford
 */
public class SelectTag extends ListTag {

    /**
     * Specifies the number of visible items within the select box.
     */
    private int size = 1;
    
    /**
     * Sets the tab index order when navigating through form elements.
     */
    private int tab_index = 0;

    /**
     * Can be used to specify a unique style sheet identifier or name
     * to the select element.
     */
    private String id;


    /**
     * Construct the tag with only a name.  The list of options will initially
     * be empty.
     *
     * @param   name    Name of the tag
     */
    public SelectTag(String name) {
        super(name);
    }

    /**
     * Class constructor uses hashtable to create the name/value pairs of the
     * options list.  The hashtable key should correspond to the value for each
     * selected item and the hashtable value should correspond to the text 
     * displayed for each item.  This constructor provides no means for ensuring
     * that the elements within the list are ordered.
     */
    public SelectTag(String name, Hashtable list) {
        super(name, list);
    }


    /**
     * Class constructor uses a vector to create the name/value pairs of the
     * options list.  The name and values for each list item will be identical.
     */
    public SelectTag(String name, Vector list) {
        super(name, list);
    }

    /**
     * Class constructor uses an enumeration to create the name/value pairs of the
     * options list.  The name and values for each list item will be identical.
     */
    public SelectTag(String name, Enumeration list) {
        super(name, list);
    }


    /**
     * Class constructor uses an array to create the name/value pairs of the
     * options list.  The name and values for each list item will be identical.
     */
    public SelectTag(String name, String[] list) {
        super(name, list);
    }


    /**
     * Sets the number of rows that will be displayed by the select box.  If
     * a value less than or equal to one is supplied, the box will be displayed
     * as a drop-down box.
     *
     * @param   rows    Number of items to display
     */
    public void setSize(int rows) {
        size = rows;
    }


    /**
     * Returns the HTML representation of the visible tag object.
     *
     * @return  HTML tag representation
     */
    public String getVisibleTag() {
        StringBuffer html = new StringBuffer();

        html.append("\n<select ");
        if (getName() != null) {
            html.append(" name=\"" + getName() + "\"");
        }

        // Number of visible elements
        if (size > 1) {
            html.append(" size=\"" + Integer.toString(size) + "\"");
        }

        // Multiple select allowed
        if (allowMultiple()) { 
            html.append(" MULTIPLE"); 
        }

        // Tab index order
        if (tab_index > 0) {
            html.append(" tabindex=\"" + Integer.toString(tab_index) + "\"");
        }

        // Stylesheet class name
        if (isDisabled()) { 
            html.append(" class='disabled' DISABLED"); 
            html.append(" title='This field is currently disabled'");
        } else {
            if (getSubmitOnChange()) {
                html.append(" onchange='form.submit()'");
            }

            // Tool tip
            if (getToolTip() != null) { 
                html.append(" title=\"" + getToolTip() + "\"");
            }

            if (getStyleClass() != null) {
                html.append(" class=\"" + getStyleClass() + "\"");
            }
        }

        // In-line stylesheet information
        if (getStyle() != null) { 
            html.append(" style=\"" + getStyle() + "\"");
        }

        // Unique tag identifier
        if (id != null) { 
            html.append(" id=\"" + id + "\"");
        }


        // Close the opening select tag
        html.append(">\n");

        // Construct the sorted list of options
        String[] keys = getKeyOrder();
        for (int idx = 0; idx < options.size(); idx++) {
            String current = keys[idx];
            if (current != null) {
                html.append("<option ");
                if (isSelected(current)) {
                    html.append(" SELECTED");
                } else if ((getDefault() != null) && (current.equalsIgnoreCase(getDefault())) && (getSelectionCount() < 1)) {
                    html.append(" SELECTED");
                }
                html.append(" value='" + current + "'>");
                html.append(options.get(current));
                html.append("</option>\n");
            }

        }
 
        html.append("</select>\n");

        return html.toString();
    }

}
