/**
 * ButtonTag.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.web.tags;
 
import javax.servlet.http.HttpServletRequest;

/**
 * The ButtonTag represents a form button.
 *
 * @version            $Revision: 1.1 $
 * @author             Shawn Stafford
 */
public class ButtonTag extends InputTag {

    /** Create a submit button */
    public static final int SUBMIT_BUTTON = 0;

    /** Create a reset button */
    public static final int RESET_BUTTON = 2;

    /** Create a generic button */
    public static final int GENERAL_BUTTON = 1;

    /** 
     * Type of button.
     */
    public int button_type = GENERAL_BUTTON;

    /**
     * The text value that is contained by the tag.
     */
    private String value;

    /**
     * Construct the button tag with the given name and label.  
     * The tag name identifies the input value.
     *
     * @param   name    Name of the input tag
     * @param   label   Label shown on the button
     * @param   type    Type of button (submit, reset, or general)
     */
    public ButtonTag(String name, String label, int type) {
        super(name);
        value = label;
        setType(type);
    }

    /**
     * Returns the label on the button.
     *
     * @return  Label of the button
     */
    public String getLabel() {
        return value;
    }

    /**
     * Sets the button type.
     *
     * @param   type    Button type (submit, reset, or general)
     */
    public void setType(int type) {
        button_type = type;
        switch(button_type) {
            case SUBMIT_BUTTON:
                setSubmitOnChange(true);
                break;
            case RESET_BUTTON:
                setSubmitOnChange(false);
                break;
            case GENERAL_BUTTON:
                setSubmitOnChange(false);
                break;
            default:
                setSubmitOnChange(false);
                button_type = GENERAL_BUTTON;
                break;
        }
    }

    /**
     * Returns the HTML representation of the visible tag object.
     *
     * @return  HTML tag representation
     */
    public String getVisibleTag() {
        StringBuffer html = new StringBuffer();

        html.append("<input name='" + getName() + "'");
        switch(button_type) {
            case SUBMIT_BUTTON:
                html.append(" type='submit'");
                break;
            case RESET_BUTTON:
                html.append(" type='reset'");
                break;
            default:
                html.append(" type='button'");
                break;
        }
        if (value != null) {
            html.append(" value='" + value + "'");
        }

        if (isDisabled()) { 
            html.append(" class='disabled' DISABLED"); 
            html.append(" title='This button is currently disabled'");
        } else {
            if (getToolTip() != null) {
                html.append(" title='" + getToolTip() + "'");
            }

            if (getStyleClass() != null) {
                html.append(" class='" + getStyleClass() + "'");
            }
        }
        html.append(">");

        return html.toString();
    }

    /**
     * Returns the HTML representation of the hidden tag object.
     *
     * @return  HTML tag representation
     */
    public String getHiddenTag() {
        StringBuffer html = new StringBuffer();

        html.append("<input type='hidden' name='" + getName() + "' value='");
        if (value != null) {
            html.append(value);
        }
        html.append("'>\n");

        return html.toString();
    }

    /**
     * Checks to see of the tag has been completed successfully.
     *
     * @return  TRUE if the tag is complete.
     */
    public boolean isComplete() {
        return ((value != null) && (value.length() > 0));
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
     * Sets the text field value to whatever is found in the request.
     *
     * @param   req         HTTP request
     */
    public void setValue(HttpServletRequest req) {
        value = req.getParameter(getName());
    }


}
