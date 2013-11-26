/**
 * ListTag.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.web.tags;
 
import javax.servlet.http.HttpServletRequest;
import com.modeln.build.util.StringUtility;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;

/**
 * The ListTag is an abstract class representing a form element
 * that can contain multiple values, such as a Select or Input
 * tag.
 *
 * @version            $Revision: 1.2 $
 * @author             Shawn Stafford
 */
public abstract class ListTag extends InputTag {

    /**
     * Attribute which allows multiple items to be selected from the select box.
     */
    private boolean multiple = false;

    /**
     * Contains the list of options displayed in this select element.
     */
    protected Hashtable options;

    /**
     * Attempts to retain the order of the keys when they are inserted into
     * the list of options.
     */
    private Vector keyOrder;

    /**
     * Selected elements in the options list
     */
    private Vector selected = new Vector();

    /**
     * Default selection if the selected items list is empty
     */
    private String defaultSelection;

    /** 
     * Determines whether the select options should be sorted.
     */
    private boolean performSort = false;


    /**
     * Default hyperlink used to create links for each list item.
     */
    private String defaultHref;


    /**
     * Construct the tag with only a name.  The list of options will initially
     * be empty.
     *
     * @param   name    Name of the tag
     */
    public ListTag(String name) {
        super(name);
        options = new Hashtable();
        keyOrder = new Vector();
    }

    /**
     * Class constructor uses hashtable to create the name/value pairs of the
     * options list.  The hashtable key should correspond to the value for each
     * selected item and the hashtable value should correspond to the text 
     * displayed for each item.  This constructor provides no means for ensuring
     * that the elements within the list are ordered.
     */
    public ListTag(String name, Hashtable list) {
        super(name);
        options = list;

        // Construct the list order
        keyOrder = new Vector();
        Enumeration keys = list.keys();
        while (keys.hasMoreElements()) {
            keyOrder.add(keys.nextElement());
        }
    }


    /**
     * Class constructor uses a vector to create the name/value pairs of the
     * options list.  The name and values for each list item will be identical.
     */
    public ListTag(String name, Vector list) {
        super(name);
        options = new Hashtable(list.size());
        keyOrder = new Vector(list.size());
        for (int idx = 0; idx < list.size(); idx++) {
            options.put(list.get(idx), list.get(idx));
            keyOrder.add(list.get(idx));
        }
    }

    /**
     * Class constructor uses an enumeration to create the name/value pairs of the
     * options list.  The name and values for each list item will be identical.
     */
    public ListTag(String name, Enumeration list) {
        super(name);
        options = new Hashtable();
        keyOrder = new Vector();
        while (list.hasMoreElements()) {
            Object item = list.nextElement();
            options.put(item, item);
            keyOrder.add(item);
        }
    }


    /**
     * Class constructor uses an array to create the name/value pairs of the
     * options list.  The name and values for each list item will be identical.
     */
    public ListTag(String name, String[] list) {
        super(name);
        options = new Hashtable(list.length);
        keyOrder = new Vector(list.length);
        for (int idx = 0; idx < list.length; idx++) {
            options.put(list[idx], list[idx]);
            keyOrder.add(list[idx]);
        }
    }

    /**
     * Removes all available options from the list and clears the list of
     * selected options.
     */
    public void removeAllOptions() {
        if (options != null)  options.clear();
        if (selected != null) selected.removeAllElements();
        if (keyOrder != null) keyOrder.removeAllElements();
    }

    /**
     * Determine if the list contains the specified option name.
     *
     * @param  value   Submit value for the option
     * @return TRUE if the value exists, false otherwise
     */
    public boolean hasOption(String value) {
        return options.containsKey(value);
    }

    /**
     * Determine if the list contains the specified option value.
     *
     * @param  value   Submit value for the option
     * @return TRUE if the value exists, false otherwise
     */
    public boolean hasValue(String value) {
        return options.containsValue(value);
    }


    /**
     * Return the total number of options in the list.
     *
     * @return number of options in the list
     */
    public int getOptionCount() {
        return options.size();
    }

    /**
     * Add an option to the list.
     *
     * @param  name   Display name for the option
     * @param  value  Submit value for the option
     */
    public void addOption(String name, String value) {
        options.put(value, name);
        keyOrder.add(value);
    }

    /**
     * Return the value associated with the specified option.
     *
     * @param  value  Submit value for the option
     * @return Display name for the option
     */
    public String getOptionName(String value) {
        return (String) options.get(value);
    }

    /**
     * Set the list of available options to the new hashtable values.
     * The list of selected values is cleared whenever the options are changed.
     * 
     * @param   opt     List of available options
     */
    public void setOptions(Hashtable opt) {
        removeAllOptions();
        if (opt != null) {
            options = opt;

            Enumeration keys = opt.keys();
            while (keys.hasMoreElements()) {
                keyOrder.add(keys.nextElement());
            }
        }
    }

    /**
     * Set the list of available options to the new vector values.
     * The list of selected values is cleared whenever the options are changed.
     * 
     * @param   opt     List of available options
     */
    public void setOptions(Vector opt) {
        removeAllOptions();
        if (opt != null) {
            for (int idx = 0; idx < opt.size(); idx++) {
                options.put(opt.get(idx), opt.get(idx));
                keyOrder.add(opt.get(idx));
            }
        }
    }

    /**
     * Set the list of available options to the new enumeration values.
     * The list of selected values is cleared whenever the options are changed.
     * 
     * @param   opt     List of available options
     */
    public void setOptions(Enumeration opt) {
        removeAllOptions();
        while ((opt != null) && opt.hasMoreElements()) {
            Object item = opt.nextElement();
            options.put(item, item);
            keyOrder.add(item);
        }
    }

    /**
     * Set the list of available options to the new array values.
     * The list of selected values is cleared whenever the options are changed.
     * 
     * @param   opt     List of available options
     */
    public void setOptions(String[] opt) {
        removeAllOptions();
        if (opt != null) {
            for (int idx = 0; idx < opt.length; idx++) {
                options.put(opt[idx], opt[idx]);
                keyOrder.add(opt[idx]);
            }
        }
    }

    /**
     * Sorts the list by the displayable option value.
     */
    public void sortByValue() {
        keyOrder = new Vector();

        // Obtain a sorted list of values
        Vector values = StringUtility.sortStringValues(options);

        // Lookup the key that corresponds to each value
        Enumeration list = values.elements();
        while (list.hasMoreElements()) {
            String value = (String) list.nextElement(); 

            // Iterate through the options to find a match for the current value
            Enumeration keys = options.keys();
            while (keys.hasMoreElements()) {
                String key = (String) keys.nextElement();
                if (value.equals((String) options.get(key))) {
                    keyOrder.add(key);
                }
            }
        }
    }

    /**
     * Sorts the list by the key value.
     */
    public void sortByKey() {
        keyOrder = StringUtility.sortStringKeys(options);
    }

    /**
     * Set the order of the keys to ensure that the list is
     * displayed in a specific order.
     *
     * @param   order   Order in which the keys are to be displayed
     */
    public void setKeyOrder(Vector order) {
        keyOrder = order;
    }

    /**
     * Set the order of the keys to ensure that the list is
     * displayed in a specific order.
     *
     * @param   order   Order in which the keys are to be displayed
     */
    public void setKeyOrder(String[] order) {
        if (order != null) {
            keyOrder = new Vector(order.length);
            for (int idx = 0; idx < order.length; idx++) {
                keyOrder.add(order[idx]);
            }
        } else {
            keyOrder = new Vector();
        }
    }


    /**
     * Returns an ordered list of keys.  If sorting is enabled,
     * the list will be sorted.
     *
     * @return  List of keys
     */
    public String[] getKeyOrder() {
        Vector keys;
        if (performSort) {
            keys = StringUtility.sortStringKeys(options);
        } else {
            keys = keyOrder;
        }

        String[] orderedKeys = new String[keys.size()];
        for (int idx = 0; idx < keys.size(); idx++) {
            orderedKeys[idx] = (String)keys.get(idx);
        }

        return orderedKeys;
    }

    /**
     * Enables and disables the sorting of options by value.
     *
     * @param   enable  TRUE if sorting should be performed
     */
    public void setSorting(boolean enable) {
        performSort = enable;
    }

    /**
     * Returns TRUE if the list elements are sorted.
     *
     * @return  TRUE if the list is sorted
     */
    public boolean isSorted() {
        return performSort;
    }


    /**
     * Enables and disables the ability to select multiple list items.
     * If this is disabled, the user will only be allowed to select a
     * single item from the list.
     *
     * @param   status  TRUE if multiple selection should be enabled
     */
    public void setMultiple(boolean enable) {
        multiple = enable;
    }

    /**
     * Returns TRUE if the user can select multiple items.
     *
     * @return  TRUE if multiple items can be selected
     */
    public boolean allowMultiple() {
        return multiple;
    }

    /**
     * Remove all selections.
     */
    public void removeAllSelections() {
        selected.removeAllElements();
    }

    /**
     * Set all items selected.
     */
    public void setAllSelected() {
        // Clear the tag of its selected elements
        selected.removeAllElements();

        Enumeration keys = options.keys();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            if (key != null) {
                selected.add(key);
            }
        }

    }

    /**
     * Setting the selected value will cause the item to be selected when
     * displayed to the user.  The selected value corresponds to the hashtable
     * key.  Any currently selected items will be dropped before the new 
     * key is set to selected.
     *
     * @param   key     Item to be selected 
     */
    public void setSelected(String key) {
        // Clear the tag of its selected elements
        selected.removeAllElements();
        if (key != null) {
            selected.add(key);
        }
    }

    /**
     * Sets the default selection if none has been made.
     *
     * @param   key     Item to be selected 
     */
    public void setDefault(String key) {
        defaultSelection = key;
    }

    /**
     * Returns the default selection.
     *
     * @return  Default key
     */
    public String getDefault() {
        return defaultSelection;
    }

    /**
     * Checks to see of the overall form has been completed successfully.
     *
     * @return  TRUE if the form is complete.
     */
    public boolean isComplete() {
        boolean hasValue = false;

        // The input should be considered complete if disabled
        if (isDisabled()) {
            hasValue = true;
        } else {
            // The form must have a selection or default value
            if (selected.size() > 0) {
                hasValue = true;
            } else {
                hasValue = ((defaultSelection != null) && (defaultSelection.length() > 0));
            }
        }

        return hasValue;
    }

    /**
     * Setting the selected value will cause the item to be selected when
     * displayed to the user.  The selected value corresponds to the hashtable
     * key.  Any currently selected items will be dropped before the new 
     * keys are set to selected.
     *
     * @param   keys    Items to be selected 
     */
    public void setSelected(String[] keys) {
        // Clear the tag of its selected elements
        selected.removeAllElements();

        if (keys != null) {
            for (int idx = 0; idx < keys.length; idx++) {
                selected.add(keys[idx]);
            }
        }
    }

    /**
     * Returns the HTML representation of the hidden tag object.
     *
     * @return  HTML tag representation
     */
    public String getHiddenTag() {
        StringBuffer html = new StringBuffer();

        String current;
        if (selected != null) {
            for (int idx = 0; idx < selected.size(); idx++) {
                current = (String)selected.get(idx);
                html.append("<input type='hidden' name='" + getName() + "' value='");
                if (current != null) {
                    html.append(current);
                }
                html.append("'>\n");
            }
        }

        return html.toString();
    }

    /**
     * Looks at the request attributes without setting the field to figure
     * out if a value is available.
     *
     * @param  req      HTTP request
     */
    public boolean isValueAvailable(HttpServletRequest req) {
        return (req.getParameterValues(getName()) != null);
    }


    /**
     * Sets the selected elements of the list by locating the list parameters
     * in the request object.  Any currently selected items will be dropped
     * before the new keys are selected.
     *
     * @param   req         HTTP request
     */
    public void setValue(HttpServletRequest req) {
        setSelected(req.getParameterValues(getName()));
    }

    /**
     * Adds the selected element to the list if it is not already selected.
     * This method carries the extra overhead of checking the list for
     * existing keys.
     *
     * @param   key     Item to be selected 
     */
    public void addSelected(String key) {
        if ((key != null) && (key.length() > 0) && (!isSelected(key))) {
            selected.add(key);
        }
    }


    /**
     * Returns the total number of selected items 
     *
     * @return  Number of selected items
     */
    public int getSelectionCount() {
        return selected.size();
    }

    /**
     * Returns a list of selected items
     *
     * @return  List of selected items
     */
    public String[] getSelected() {
        String[] list = new String[selected.size()];
        for (int idx = 0; idx < selected.size(); idx++) {
            list[idx] = (String)selected.get(idx);
        }

        return list;
    }

    /**
     * Returns a list of selected labels
     *
     * @return  List of selected labels
     */
    public String[] getSelectedText() {
        String[] list = new String[selected.size()];
        for (int idx = 0; idx < selected.size(); idx++) {
            list[idx] = getText((String)selected.get(idx));
        }

        return list;
    }


    /**
     * Returns true if the value has been selected in the list.
     *
     * @return  TRUE if the value is selected
     */
    public boolean isSelected(String key) {
        boolean found = false;

        String current;
        if (key != null) {
            // Loop through the list of selected items
            for (int idx = 0; idx < selected.size(); idx++) {
                current = (String)selected.get(idx);
                if (key.equalsIgnoreCase(current)) {
                    return true;
                }
            }
        }

        return found;
    }

    /**
     * Returns the printable text that is associated with the key value.
     *
     * @param   key     List item that the text is associated with
     * @return  Printable text
     */
    public String getText(String key) {
        return (String)options.get(key);
    }


    /**
     * Creates a hyperlink around each item in the list.  The hyperlink
     * for each item is differentiated by appending a name/value pair
     * to the link.
     *
     * @param   href  Base URL
     */
    public void setDefaultLink(String href) {
        defaultHref = href;
    }

    /**
     * Returns the default link associated with items in the list.
     *
     * @return  Base URL
     */
    public String getDefaultLink() {
        return defaultHref;
    }


}
