/**
 * TagGroup.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.web.tags;
 

import java.util.Vector;
import javax.servlet.http.HttpServletRequest;
import com.modeln.build.web.tags.InputTag;

/**
 * A tag group allows a group of tags to be manipulated as a single unit.
 * The most common use of this class is to group a set of tags together 
 * so they can be enabled or disabled by the user. 
 *
 * @version            $Revision: 1.0 $
 * @author             Shawn Stafford
 */
public class TagGroup extends InputTag {

    /** Name used to indentify the tag for enabling or disabling the group. */
    private static final String enableTagName = "Enable";

    /** Determines whether the user has the ability to enable and disable this field. */
    private boolean allowUserDisable = false;


    /** Tag for enabling the input fields. */
    private ToggleTag enableTag;

    /** List of tags in the group. */
    private Vector tags = new Vector();


    /**
     * Construct an empty tag group. 
     *
     * @param   name    Name used to identify the group
     */
    public TagGroup(String name) {
        super(name);
        enableTag = new ToggleTag(getName() + enableTagName);
        setUserDisable(false);
    }


    /**
     * Add a tag to the group.
     * 
     * @param   tag   Input tag
     */
    public void add(InputTag tag) {
        tags.add(tag);
    }

    /**
     * Force the input element to perform a form submission whenever
     * an onChange event occurrs.
     *
     * @param   enable  TRUE of the form should submit for onChange events
     */
    public void setSubmitOnChange(boolean enable) {
        super.setSubmitOnChange(enable);
        enableTag.setSubmitOnChange(enable); 
    }


    /**
     * Determines whether the field can be enabled or disabled by the user.
     *
     * @param   status  TRUE if the field can be disabled by the user
     */
    public void setUserDisable(boolean status) {
        allowUserDisable = status;
        enableTag.setDisabled(!allowUserDisable);
        enableTag.setHidden(!allowUserDisable);
    }

    /**
     * Determine if the user has disabled the field.
     *
     * @return TRUE if the tag was disabled by the user
     */
    public boolean allowUserDisable() {
        return allowUserDisable;
    }


    /**
     * Checks to see of the overall form has been completed successfully.
     *
     * @return  TRUE if the form is complete.
     */
    public boolean isComplete() {
        boolean status = enableTag.getValue();
        InputTag current = null;
        for (int idx = 0; idx < tags.size(); idx++) {
            current = (InputTag) tags.get(idx);
            status = (status && current.isComplete());
            // Bail out early just for efficiency reasons
            if (status == false) {
                return status;
            }
        }
        return status;
    }


    /**
     * Returns the HTML representation of the hidden tag object.
     *
     * @return  HTML tag representation
     */
    public String getHiddenTag() {
        StringBuffer html = new StringBuffer();
        
        if (allowUserDisable) {
            html.append(enableTag.toString() + "\n");
        }
        InputTag current = null;
        for (int idx = 0; idx < tags.size(); idx++) {
            current = (InputTag) tags.get(idx);
            html.append(current.toString() + "\n");
        }

        return html.toString();
    }

    /**
     * Returns the HTML representation of the visible tag object.
     *
     * @return  HTML tag representation
     */
    public String getVisibleTag() {
        StringBuffer html = new StringBuffer();

        // Construct a table containing a month, day, and year element
        html.append("\n<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n");
        html.append("  <tr>\n");

        if (allowUserDisable) {
            html.append("    <td>\n");
            html.append(enableTag.toString() + "\n");
            html.append("    </td>\n");
        }

        InputTag current = null;
        for (int idx = 0; idx < tags.size(); idx++) {
            current = (InputTag) tags.get(idx);
            html.append("    <td>\n");
            html.append(current.toString() + "\n");
            html.append("    </td>\n");
        }

        html.append("  </tr>\n");
        html.append("</table>\n");

        return html.toString();
    }


    /**
     * Enables and disables the input list.
     *
     * @param   status  TRUE if the element should appear disabled
     */
    public void setDisabled(boolean status) {
        super.setDisabled(status);
        enableTag.setDisabled(status);
        InputTag current = null;
        for (int idx = 0; idx < tags.size(); idx++) {
            current = (InputTag) tags.get(idx);
            current.setDisabled(status);
        }
    }


    /**
     * Hides the input tag.
     *
     * @param   hide    Hide the input tag if TRUE
     */
    public void setHidden(boolean hide) {
        super.setHidden(hide);
        enableTag.setHidden(hide);
        InputTag current = null;
        for (int idx = 0; idx < tags.size(); idx++) {
            current = (InputTag) tags.get(idx);
            current.setHidden(hide);
        }
    }

    /**
     * Sets the tool tip associated with the tag.
     *
     * @param   tip     Text to use as a tip
     */
    public void setToolTip(String tip) {
        super.setToolTip(tip);
        enableTag.setToolTip(tip);
    }

    /**
     * Set the inline stylesheet for the tag.
     *
     * @param   css     Inline stylesheet entry
     */
    public void setStyle(String css) {
        super.setStyle(css);
        enableTag.setStyle(css);
    }

    /**
     * Sets the class identifier associated with the tag.
     * This is often used to attach stylesheet formatting.
     *
     * @param   id      Stylesheet class identifier
     */
    public void setStyleClass(String id) {
        super.setStyleClass(id);
        enableTag.setStyleClass(id);
    }


    /**
     * Looks at the request attributes without setting the field to figure
     * out if a value is available.
     *
     * @param  req      HTTP request
     */
    public boolean isValueAvailable(HttpServletRequest req) {
        boolean isAvailable = true;
        if (!enableTag.isValueAvailable(req)) {
            isAvailable = false;
        }
        InputTag current = null;
        for (int idx = 0; idx < tags.size(); idx++) {
            current = (InputTag) tags.get(idx);
            if (!current.isValueAvailable(req)) {
                isAvailable = false;
            }
        }
        return isAvailable; 
    }

    /**
     * Sets the input field value to whatever is found in the request.
     *
     * @param   req         HTTP request
     */
    public void setValue(HttpServletRequest req) {
        enableTag.setValue(req);
        InputTag current = null;
        for (int idx = 0; idx < tags.size(); idx++) {
            current = (InputTag) tags.get(idx);
            current.setValue(req);
        }

        // Enable the group based on the toggle value
        enableGroup(enableTag.getValue());
    }


    /**
     * Enable or disable all elements of the group that are controlled by the 
     * group toggle.
     *
     * @param  status  TRUE if elements should be enabled, FALSE otherwise
     */
    public void enableGroup(boolean status) {
        enableTag.setValue(status);
        InputTag current = null;
        for (int idx = 0; idx < tags.size(); idx++) {
            current = (InputTag) tags.get(idx);
            current.setDisabled(!status);
        }
    }
}

