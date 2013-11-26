/*
 * TableRowTag.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.web.tags;

import java.util.Vector;

/**
 * The table row object represents a list of cells in an HTML table.  This
 * is generally represented by a TR tag.
 *
 * @version            $Revision: 1.1 $
 * @author             Shawn Stafford
 *
 */
public class TableRowTag {

    /** No cell content alignment */
    public static final int NO_ALIGNMENT = 0;

    /** Align the content to the left of the cell */
    public static final int ALIGN_LEFT = 1;

    /** Align the content to the center of the cell */
    public static final int ALIGN_CENTER = 2;

    /** Align the content to the right of the cell */
    public static final int ALIGN_RIGHT = 3;

    /** Align the cell contents to the top of the cell */
    public static final int VALIGN_TOP = 4;

    /** Align the cell contents to the middle of the cell */
    public static final int VALIGN_MIDDLE = 5;

    /** Align the cell contents to the bottom of the cell */
    public static final int VALIGN_BOTTOM = 6;

    /** Align the cell contents with the baseline of other cell elements */
    public static final int VALIGN_RELATIVE = 7;

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

    /** List of cells within the table */
    private Vector cells;

    /**
     * Construct a new table row with no cells.
     */
    public TableRowTag() {
        cells = new Vector();
    }

    /**
     * Construct a new table row containing the given cells.
     *
     * @param   cells   Cells to be displayed within the row
     */
    public TableRowTag(TableCellTag[] cells) {
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
     * Sets the background color used in the table.  Do not append a hash
     * character (#) to the beginning of the color string.
     *
     * @param   color   Six-character hexidecimal color
     */
    public void setBackgroundColor(String color) {
        background_color = color;
    }

    /**
     * Returns the total number of cells within the row.
     *
     * @return  Number of cells within the row
     */
    public int getCellCount() {
        return cells.size();
    }

    /**
     * Returns the total number of columns spanned by the current row.
     * The column count takes into account the colspan values of each 
     * cell when determing the total number of columns within the row.
     *
     * @return  Number of columns spanned by all cells within the row
     */
    public int getColumnSpan() {
        int colspan = 0;
        
        for (int idx = 0; idx < cells.size(); idx++) {
            colspan = colspan + ((TableCellTag)cells.get(idx)).getColumnSpan();
        }

        return colspan;
    }

    /**
     * Renders the table element as an HTML string.
     */
    public synchronized String toHtml() {
        StringBuffer html = new StringBuffer();
        html.append("<tr");

        html.append(">");

        // Render the individual cells
        for (int idx = 0; idx < cells.size(); idx++) {
            html.append(((TableCellTag)cells.get(idx)).toHtml());
        }

        html.append("</tr>");

        return html.toString();
    }

}
