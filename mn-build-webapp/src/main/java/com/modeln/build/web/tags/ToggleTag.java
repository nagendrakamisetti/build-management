/**
 * ToggleTag.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.web.tags;
 
import javax.servlet.http.HttpServletRequest;

/**
 * The ToggleTag is a simple check-box that is used to represent a boolean value. 
 *
 * @version            $Revision: 1.0 $
 * @author             Shawn Stafford
 */
public class ToggleTag extends InputTag {

    /**
     * Boolean status of the tag.
     */
    private boolean checked = false;

    /**
     * Signals that no value was encountered and the default is being used.
     */
    private boolean usingDefault = true;

    /**
     * Construct the input tag with the given name.  The tag name
     * identifies the input value(s).
     *
     * @param   name    Name of the input tag
     */
    public ToggleTag(String name) {
        super(name);
    }

    /**
     * Set the toggle status of the tag.
     * 
     * @param   status   TRUE if the toggle is on, FALSE otherwise
     */
    public void setValue(boolean status) {
        checked = status;
    }

    /**
     * Returns the status of the toggle input. 
     *
     * @return  TRUE if the tag is toggled ON, FALSE if the tag is not toggled
     */
    public boolean getValue() {
        return checked;
    }


    /**
     * Looks at the request attributes without setting the field to figure
     * out if a value is available.
     *
     * @param  req      HTTP request
     */
    public boolean isValueAvailable(HttpServletRequest req) {
        return (req.getParameter(getName()) != null);
    }

    /**
     * Sets the toggle status of the field. 
     *
     * @param   req         HTTP request
     */
    public void setValue(HttpServletRequest req) {
        String value = req.getParameter(getName());
        if ((value != null) && (value.length() > 0)) {
            usingDefault = false;
            Boolean result = Boolean.valueOf(value);
            checked = result.booleanValue();
        } else {
            usingDefault = true;
            checked = false;
        }
    }

    /**
     * Returns true if the tag is disabled or enabled and has a value.
     *
     * @return  TRUE if the form element is complete
     */
    public boolean isComplete() {
        if (isDisabled()) {
            return true;
        } else if (!usingDefault) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * Returns the HTML representation of the visible tag object.
     *
     * @return  HTML tag representation
     */
    public String getVisibleTag() {
        StringBuffer html = new StringBuffer();
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
        if (checked) {
            html.append(" CHECKED");
        }
        html.append(" name='" + getName() + "'");
        html.append(" type='checkbox'");
        html.append(" value='true'");
        html.append("/>\n");

        return html.toString();
    }

    /**
     * Returns the HTML representation of the hidden tag object.
     *
     * @return  HTML tag representation
     */
    public String getHiddenTag() {
        StringBuffer html = new StringBuffer();
        if (!isDisabled()) {
            html.append("<input type='hidden' name='" + getName() + "' value='" + checked + "'>\n");
        }
        return html.toString();
    }

}
