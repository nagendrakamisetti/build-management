/**
 * TextTag.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.web.tags;
 
import javax.servlet.http.HttpServletRequest;
import com.modeln.build.util.StringUtility;

/**
 * The TextTag represents a text tag that can accept text input.
 *
 * @version            $Revision: 1.2 $
 * @author             Shawn Stafford
 */
public class TextTag extends InputTag {

    /**
     * Specifies the character width of the input item.
     */
    private int columns = 20;

    /**
     * Specifies the number of rows in the input item.
     */
    private int rows = 1;
    
    /**
     * Maximum number of characters that the field will accept.
     */
    private int maxlength = 255;

    /**
     * The text value that is contained by the tag.
     */
    private String value;

    /**
     * Construct the input tag with the given name.  The tag name
     * identifies the input value(s).
     *
     * @param   name    Name of the input tag
     */
    public TextTag(String name) {
        super(name);
    }


    /**
     * Set the height of the input tag.
     *
     * @param   rows    Number of rows high
     */
    public void setHeight(int rows) {
        this.rows = rows;
    }

    /**
     * Set the width of the input tag.
     *
     * @param   cols    Number of characters wide
     */
    public void setWidth(int cols) {
        columns = cols;
    }

    /**
     * Sets the maximum number of characters that the field will accept.
     */
    public void setMaxLength(int chars) {
        maxlength = chars;
    }


    /**
     * The input field value will be set to the given value.
     *
     * @param   text    Value of the text field
     */
    public void setValue(String text) {
        value = text;
    }

    /**
     * Returns the value directly from the request rather than using the data object. 
     *
     * @param   req    HTTP request
     * @return  Text
     */
    public String getValue(HttpServletRequest req) {
        return req.getParameter(getName());
    }


    /**
     * Returns the text specified by the user.
     *
     * @return  Text
     */
    public String getValue() {
        return value;
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
        setValue(req.getParameter(getName()));
    }


    /**
     * Returns true if the tag is disabled or enabled and has a value.
     *
     * @return  TRUE if the form element is complete
     */
    public boolean isComplete() {
        if (isDisabled()) {
            return true;
        } else if ((value != null) && (value.length() > 0)) {
            return true;
        } else {
            return false;
        }
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
     * Returns the HTML representation of the visible tag object.
     *
     * @return  HTML tag representation
     */
    public String getVisibleTag() {
        StringBuffer html = new StringBuffer();

        // Determine if the tag should be rendered as a textarea or input box
        boolean textarea = (rows > 1);

        // Render the opening tag
        if (textarea) {
            html.append("<textarea name='" + getName() + "'");
            html.append(" rows='" + rows + "'");
            html.append(" cols='" + columns + "'");
            html.append(" wrap='virtual'");
        } else {
            html.append("<input type='text' name='" + getName() + "'");
            html.append(" size='" + columns + "'");
            html.append(" maxlength='" + maxlength + "'");
            if (value != null) {
                html.append(" value='" + value + "'");
            }
        }
        if (isDisabled()) { 
            html.append(" class='disabled' DISABLED"); 
            html.append(" title='This field is currently disabled'");
        } else {
            if (getToolTip() != null) {
                html.append(" title='" + getToolTip() + "'");
            }

            if (getStyleClass() != null) {
                html.append(" class='" + getStyleClass() + "'");
            }
        }
        html.append(">");

        // Render the value
        if (textarea) {
            if (value != null) {
                html.append(StringUtility.stringToHtml(value));
            }
            html.append("</textarea>\n");
        }

        return html.toString();
    }

}
