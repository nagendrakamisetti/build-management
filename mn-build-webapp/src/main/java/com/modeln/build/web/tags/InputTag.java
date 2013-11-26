/**
 * InputTag.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.web.tags;
 
import javax.servlet.http.HttpServletRequest;

/**
 * The InputTag is an abstract class representing a form element.
 *
 * @version            $Revision: 1.2 $
 * @author             Shawn Stafford
 */
public abstract class InputTag {

    /**
     * Name of the form element used when the form is submitted.
     */
    private String tag_name;


    /**
     * Sets the tool tip for the form element (Displayed by IE when 
     * the mouse is held over the element)
     */
    private String title = "";

    /**
     * Used to specify a stylesheet class for the input element.
     */
    private String class_name = "";

    /**
     * Used to specify an in-line style setting for the input element.
     */
    private String style = "";

    /**
     * Enables or disables the form element (if the browser supports this)
     */
    private boolean disabled = false;

    /**
     * If the tag is hidden, the input field should not be visible to the user.
     */
    private boolean hidden = false;


    /**
     * This flag creates a javascript call that submits the form whenever 
     * an onChange event occurrs.
     */
    private boolean submitOnChange = false;


    /**
     * Construct the input tag with the given name.  The tag name
     * identifies the input value(s).
     *
     * @param   name    Name of the input tag
     */
    public InputTag(String name) {
        tag_name = name;
    }


    /**
     * Enables and disables the input list.
     *
     * @param   status  TRUE if the element should appear disabled
     */
    public void setDisabled(boolean status) {
        disabled = status;
    }

    /**
     * Returns TRUE if the input element is disabled.
     *
     * @return  TRUE if the tag is disabled
     */
    public boolean isDisabled() {
        return disabled;
    }

    /**
     * Hides the input tag.
     *
     * @param   hide    Hide the input tag if TRUE
     */
    public void setHidden(boolean hide) {
        hidden = hide;
    }

    /**
     * Returns the hidden or visible status of the field.
     *
     * @return  TRUE if the field is hidden
     */
    public boolean isHidden() {
        return hidden;
    }

    /**
     * Force the input element to perform a form submission whenever
     * an onChange event occurrs.
     *
     * @param   enable  TRUE of the form should submit for onChange events
     */
    public void setSubmitOnChange(boolean enable) {
        submitOnChange = enable;
    }

    /**
     * Determines whether the form should be submitted on a change event.
     *
     * @return  TRUE if a submit will be triggered by a change event
     */
    public boolean getSubmitOnChange() {
        return submitOnChange;
    }

    /**
     * Returns the name of the input tag.  The tag name is used to identify
     * the tag values that may be stored in a request or session.
     *
     * @return  Tag name
     */
    public String getName() {
        return tag_name;
    }

    /**
     * Returns the tool tip associated with the tag.
     *
     * @return  Tool tip string
     */
    public String getToolTip() {
        return title;
    }

    /**
     * Sets the tool tip associated with the tag.
     *
     * @param   tip     Text to use as a tip
     */
    public void setToolTip(String tip) {
        title = tip;
    }

    /** 
     * Returns the inline stylesheet entries for the tag
     *
     * @return  Inline stylesheet entry
     */
    public String getStyle() {
        return style;
    }

    /**
     * Set the inline stylesheet for the tag.
     *
     * @param   css     Inline stylesheet entry
     */
    public void setStyle(String css) {
        style = css;
    }

    /**
     * Returns the class identifier associated with the tag. 
     * This is often used to attach stylesheet formatting.
     *
     * @return  Stylesheet class identifier
     */
    public String getStyleClass() {
        return class_name;
    }


    /**
     * Sets the class identifier associated with the tag.
     * This is often used to attach stylesheet formatting.
     *
     * @param   id      Stylesheet class identifier
     */
    public void setStyleClass(String id) {
        class_name = id;
    }

    /**
     * Returns the HTML representation of the Select tag object.
     *
     * @return  HTML representation of the tag
     */
    public String toString() {
        if (hidden) {
            return getHiddenTag();
        } else {
            return getVisibleTag();
        }
    }


    /**
     * Returns the HTML representation of the visible tag object.
     *
     * @return  HTML tag representation
     */
    public abstract String getVisibleTag();

    /**
     * Returns the HTML representation of the hidden tag object.
     *
     * @return  HTML tag representation
     */
    public abstract String getHiddenTag();

    /**
     * Sets the input field value to whatever is found in the request.
     *
     * @param   req         HTTP request
     */
    public abstract void setValue(HttpServletRequest req); 

    /**
     * Looks at the request attributes without setting the field to figure 
     * out if a value is available.
     *
     * @param  req      HTTP request
     */
    public abstract boolean isValueAvailable(HttpServletRequest req);

    /**
     * Checks to see of the tag has been completed successfully.
     *
     * @return  TRUE if the tag is complete.
     */
    public abstract boolean isComplete();

}
