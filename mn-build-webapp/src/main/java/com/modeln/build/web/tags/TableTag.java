/*
 * TableTag.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.web.tags;

import java.util.Vector;

/**
 * The Table object contains the table properties generally used to 
 * construct an HTML table.
 *
 * @version            $Revision: 1.1 $
 * @author             Shawn Stafford
 *
 */
public class TableTag {

    /** No table alignment */
    public static final int NO_ALIGNMENT = 0;

    /** Align the table to the left of the page */
    public static final int ALIGN_LEFT = 1;

    /** Align the table to the left of the page */
    public static final int ALIGN_RIGHT = 3;

    /** Align the cell contents to the top of the cell */
    public static final int VALIGN_TOP = 4;

    /** Align the cell contents to the middle of the cell */
    public static final int VALIGN_MIDDLE = 5;

    /** Align the cell contents to the bottom of the cell */
    public static final int VALIGN_BOTTOM = 6;


    /** Width of the table */
    private Float width;

    /** Height of the table */
    private Float height;

    /** Number of pixels placed between table cells. */
    private int cellspacing = 0;

    /** Number of pixels placed between the edge of the cell and its content */
    private int cellpadding = 0;

    /** Width of the table borders in pixels */
    private int border_width = 0;

    /** Background color of the entire table */
    private String background_color;

    /** Background image to display behind the table content */
    private String background_image;

    /** Stylesheet class to apply to the table */
    private String stylesheet_class;
    
    /** Horizontal alignment of the table on the page */
    private int hz_align = NO_ALIGNMENT;

    /** Vertical alignment of the table on the page */
    private int vt_align = NO_ALIGNMENT;

    /** List of rows within the table */
    private Vector rows;


    /**
     * Construct a new table object with no content.
     */
    public TableTag() {
        rows = new Vector();
    }

    /**
     * Sets the stylesheet class which applies to the table.
     *
     * @param   name    Class name
     */
    public void setStylesheetClass(String name) {
        stylesheet_class = name;
    }

    /**
     * Sets the width of the table.  A relative percentage width is assumed
     * for values greater than zero and less than or equal to one 
     * (<code>0 < width <= 1</code>).  An absolute pixel width is assumed
     * for values which are greater than one (<code>width > 1</code>).
     * Because of this, there is no way for this class to create a table
     * which is exactly one pixel wide.
     *
     * @param   value   Width value
     */
    public void setWidth(Float value) {
        width = value;
    }

    /**
     * Sets the height of the table.  A percentage height relative to the
     * browser window is assumed for values greater than zero and less than 
     * or equal to one (<code>0 &gt; width &lt;= 1</code>).  An absolute pixel 
     * width is assumed for values which are greater than one 
     * (<code>width &gt; 1</code>).   Because of this, there is no way for 
     * this class to create a table which is exactly one pixel high.
     *
     * @param   value   Height value
     */
    public void setHeight(Float value) {
        height = value;
    }

    /** 
     * Sets the background color used in the table.  Do not append a hash
     * character (#) to the beginning of the color string.
     *
     * @param   color   Six-character hexidecimal color
     */
    public void setBackgroundColor(String color) {
        background_color = color;
    }

    /**
     * Sets the number of pixels used as a border around the table cells.
     *
     * @param   pixels  Number of pixels
     */
    public void setBorder(int pixels) {
        border_width = pixels;
    }

    /**
     * Sets the number of pixels used as spacing between table cells.
     *
     * @param   pixels  Number of pixels
     */
    public void setCellspacing(int pixels) {
        cellspacing = pixels;
    }

    /**
     * Sets the number of pixels used as spacing between the edge of the cell
     * and the cell content.
     *
     * @param   pixels  Number of pixels
     */
    public void setCellpadding(int pixels) {
        cellpadding = pixels;
    }

    /**
     * Renders the table element as an HTML string.
     */
    public synchronized String toHtml() {
        StringBuffer html = new StringBuffer();

        // Construct the opening table tag
        html.append("<table");
        html.append(" cellspacing='" + cellspacing + "'");
        html.append(" cellpadding='" + cellpadding + "'");
        html.append(" border='" + border_width + "'");
        if (width != null) {
            if (width.floatValue() > 1) {
                html.append(" width='" + width.intValue() + "'");
            } else if ((width.floatValue() > 0) && (width.floatValue() <= 1)) {
                int size = (int)(width.floatValue() * 100);
                html.append(" width='" + size + "%'");
            } else {
                // Nothing is done if the width == 0
                // We could use this quirk to provide functionality
                // for a 1 pixel wide table
            }
        }
        if (height != null) {
            if (height.floatValue() > 1) {
                html.append(" height='" + height.intValue() + "'");
            } else if ((height.floatValue() > 0) && (height.floatValue() <= 1)) {
                int size = (int)(height.floatValue() * 100);
                html.append(" height='" + size + "%'");
            } else {
                // Nothing is done if the height == 0
                // We could use this quirk to provide functionality
                // for a 1 pixel wide table
            }
        }
        switch (hz_align) {
            case ALIGN_LEFT:    html.append(" align='left'"); break;
            case ALIGN_RIGHT:   html.append(" align='right'"); break;
        }
        switch (vt_align) {
            case VALIGN_TOP:    html.append(" valign='top'"); break;
            case VALIGN_MIDDLE: html.append(" valign='center'"); break;
            case VALIGN_BOTTOM: html.append(" valign='bottom'"); break;
        }
        if ((background_color != null) && (background_color.length() > 0)) {
            html.append(" bgcolor='#" + background_color + "'");
        }
        if ((stylesheet_class != null) && (stylesheet_class.length() > 0)) {
            html.append(" class='" + stylesheet_class + "'");
        }
        if ((background_image != null) && (background_image.length() > 0)) {
            html.append(" background='" + background_image + "'");
        }
        html.append(">\n");

        // Construct the closing table tag
        html.append("</table>\n");

        return html.toString();
    }
}
