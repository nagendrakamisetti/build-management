/**
 * OptionTag.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.web.tags;

import java.util.Enumeration;
import java.util.Vector;
import java.util.Hashtable;

/**
 * The OptionTag refers to a collection of radio buttons or
 * check boxes that are grouped by the same tag id.
 *
 * @version            $Revision: 1.2 $
 * @author             Shawn Stafford
 */
public class OptionTag extends ListTag {

    /**
     * Specifies the number of columns used to display the items.
     */
    private int columns = 1;
    
    /**
     * Specifies whether the list should appear vertically
     */
    private boolean vertical = true;

    /**
     * Construct the tag with only a name.  The list of options will initially
     * be empty.
     *
     * @param   name    Name of the tag
     */
    public OptionTag(String name) {
        super(name);
    }

    /**
     * Class constructor uses hashtable to create the name/value pairs of the
     * options list.  The hashtable key should correspond to the value for each
     * selected item and the hashtable value should correspond to the text 
     * displayed for each item.  This constructor provides no means for ensuring
     * that the elements within the list are ordered.
     */
    public OptionTag(String name, Hashtable list) {
        super(name, list);
    }


    /**
     * Class constructor uses a vector to create the name/value pairs of the
     * options list.  The name and values for each list item will be identical.
     */
    public OptionTag(String name, Vector list) {
        super(name, list);
    }

    /**
     * Class constructor uses an enumeration to create the name/value pairs of the
     * options list.  The name and values for each list item will be identical.
     */
    public OptionTag(String name, Enumeration list) {
        super(name, list);
    }

    /**
     * Class constructor uses an array to create the name/value pairs of the
     * options list.  The name and values for each list item will be identical.
     */
    public OptionTag(String name, String[] list) {
        super(name, list);
    }


    /**
     * Returns the HTML representation of the visible tag object.
     *
     * @return  HTML tag representation
     */
    public String getVisibleTag() {
        StringBuffer html = new StringBuffer();

        // Multiple select allowed
        String type;
        if (allowMultiple()) { 
            type = "checkbox";
        } else {
            type = "radio";
        }

        // Construct the sorted list of options
        String[] keys = getKeyOrder();
        for (int idx = 0; idx < options.size(); idx++) {
            html.append("<input ");
            if (isDisabled()) { 
                html.append(" class='disabled' DISABLED"); 
            } else {
                if (getSubmitOnChange()) {
                    html.append(" onclick='form.submit()'");
                }
                if (getStyleClass() != null) {
                    html.append(" class='" + getStyleClass() + "'");
                }
            }
            if (isSelected(keys[idx])) {
                html.append(" CHECKED");
            }
            html.append(" name='" + getName() + "'");
            html.append(" type='" + type + "'");
            html.append(" value='" + keys[idx] + "'");
            html.append("/>");
            if (getDefaultLink() != null) {
                html.append("<a href=\"" + getDefaultLink());
                html.append(getName() + "=" + keys[idx]);
                html.append("\">");
                html.append(options.get(keys[idx]));
                html.append("</a>");
            } else {
                html.append(options.get(keys[idx]));
            }
            if (vertical) {
                html.append("<br>");
            }
            html.append("\n");
        }
 
        return html.toString();
    }

}
