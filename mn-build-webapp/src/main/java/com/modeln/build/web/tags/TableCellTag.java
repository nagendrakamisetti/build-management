/*
 * TableCellTag.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.web.tags;

/**
 * The table cell object represents a single cell in an HTML table.  This
 * is generally represented by a TD or TH tag.
 *
 * @version            $Revision: 1.1 $
 * @author             Shawn Stafford
 *
 */
public class TableCellTag {

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

    /** Width of the table */
    private Float width;

    /** Height of the table */
    private Float height;

    /** Prevents cell content from being broken into multiple lines */
    private boolean nowrap = false;

    /** Number of columns which the current cell should span */
    private int colspan = 1;

    /** Number of rows which the current cell should span */
    private int rowspan = 1;

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

    /** Set the content to be displayed within the cell */
    private String content;


    /**
     * Construct a new table cell containing the given content.
     * The content can contain additional HTML tags if additional
     * mark-up capabilities are required.
     *
     * @param   text    Content to be displayed within the cell
     */
    public TableCellTag(String text) {
        content = text;
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
     * Sets the number of columns spanned by the cell.
     *
     * @param   size    Number of columns spanned
     */
    public void setColumnSpan(int size) {
        colspan = size;
    }

    /**
     * Sets the number of rows spanned by the cell.
     *
     * @param   size    Number of rows spanned
     */
    public void setRowSpan(int size) {
        rowspan = size;
    }

    /**
     * Returns the number of columns spanned by the cell.
     *
     * @return  Number of columns spanned
     */
    public int getColumnSpan() {
        return colspan;
    }

    /**
     * Returns the number of rows spanned by the cell.
     *
     * @return  Number of rows spanned
     */
    public int getRowSpan() {
        return rowspan;
    }

    /**
     * Renders the table element as an HTML string.
     */
    public String toHtml() {
        StringBuffer html = new StringBuffer();
        html.append("<td");
        if (nowrap) {
            html.append(" NOWRAP");
        }
        if (width != null) {
            if (width.floatValue() > 1) {
                html.append(" width='" + width.intValue() + "'");
            } else if ((width.floatValue() > 0) && (width.floatValue() <= 1)) {
                int size = (int)(width.floatValue() * 100);
                html.append(" width='" + size + "%'");
            } else {
                // Nothing is done if the width == 0
                // We could use this quirk to provide functionality
                // for a 1 pixel wide cell
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
                // for a 1 pixel wide cell
            }
        }
        switch (hz_align) {
            case ALIGN_LEFT:    html.append(" align='left'"); break;
            case ALIGN_CENTER:  html.append(" align='center'"); break;
            case ALIGN_RIGHT:   html.append(" align='right'"); break;
        }
        switch (vt_align) {
            case VALIGN_TOP:        html.append(" valign='top'"); break;
            case VALIGN_MIDDLE:     html.append(" valign='center'"); break;
            case VALIGN_BOTTOM:     html.append(" valign='bottom'"); break;
            case VALIGN_RELATIVE:   html.append(" valign='baseline'"); break;
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

        html.append(">" + content + "</td>");

        return html.toString();
    }

}
