/*
* Copyright 2000-2003 by Model N, Inc.  All Rights Reserved.
*
* This software is the confidential and proprietary information
* of Model N, Inc ("Confidential Information").  You shall not
* disclose such Confidential Information and shall use it only
* in accordance with the terms of the license agreement you
* entered into with Model N, Inc.
*/
package com.modeln.build.ctrl.forms;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Vector;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.modeln.build.web.tags.TextTag;
import com.modeln.build.web.tags.DateTag;
import com.modeln.build.web.tags.OptionTag;
import com.modeln.build.web.tags.SelectTag;
import com.modeln.build.web.tags.TagGroup;

import com.modeln.build.common.data.database.CMnQueryData;


/**
 * The database query history form provides an HTML interface for the user to
 * select a query from a list of historical objects. 
 *
 * @author  Shawn Stafford
 */
public class CMnDatabaseQueryHistoryForm extends CMnBaseForm {

    /** Default title used when displaying a title and border */
    private static final String DEFAULT_TITLE = "History";

    /** Label used to identify the id of the query */
    private static final String ID_LABEL = "id";

    /** Label used to identify the type of action to perform */
    public static final String ACTION_LABEL = "action";

    /** Value used to identify a delete action */
    public static final String DELETE_ACTION = "delete";

    /** Value used to identify a run action */
    public static final String RUN_ACTION = "run";

    /** Value used to identify a clear action */
    public static final String CLEAR_ACTION = "clear";


    /** List of valid history actions */
    private static final String[] actions = { RUN_ACTION, DELETE_ACTION, CLEAR_ACTION };

    /** Label used to identify the history data object in the session */
    public static final String HISTORY_OBJECT_LABEL = "QUERY_HISTORY_OBJECT";


    /** History selection field */
    private SelectTag historyTag = new SelectTag(ID_LABEL);

    /** History action field */
    private SelectTag actionTag = new SelectTag(ACTION_LABEL, actions);


    /** List of historical queries */
    private Vector history = null;

    /** Index number of the selected item in the history vector */
    private int selectedIdx = 0;

    /** Indicates whether the user has provided an action */
    private boolean actionSelected = false;

    /**
     * Construct a build form.
     *
     * @param  form   URL to use when submitting form input
     * @param  images URL to use when referencing images
     */
    public CMnDatabaseQueryHistoryForm(URL form, URL images) {
        super(form, images);
        setInputMode(true);

        actionTag.setDefault(RUN_ACTION);
        historyTag.setSize(6);
        historyTag.setSorting(true);
    }


    /**
     * Update the history vectory by adding or removing elements.
     */
    public void updateHistory(HttpServletRequest req) {
        String action = req.getParameter(ACTION_LABEL);
        HttpSession session = req.getSession();
        history = (Vector) session.getAttribute(HISTORY_OBJECT_LABEL);

        // Perform any actions on the history vector if necessary
        if (action != null) {
            actionTag.setSelected(action);
            actionSelected = true;

            // For delete or clear opterations, perform the update now
            if (action.equals(DELETE_ACTION)) {
                if ((history != null) && (history.size() > selectedIdx) && (selectedIdx > 0)) {
                    history.remove(selectedIdx);
                }
                selectedIdx = -1;
                historyTag.removeAllSelections();
            } else if (action.equals(CLEAR_ACTION)) {
                history = new Vector();
                session.setAttribute(HISTORY_OBJECT_LABEL, history);
                selectedIdx = -1;
                historyTag.removeAllSelections();
            }
        } else {
            actionTag.setSelected(RUN_ACTION);
            actionSelected = false;
        }

    }

    /**
     * Set the input fields by examining the HTTP request to see if
     * a value was submitted.
     *
     * @param   req     HTTP request
     */
    public void setValues(HttpServletRequest req) {
        // Collect the user input
        String action = req.getParameter(ACTION_LABEL);
        HttpSession session = req.getSession();
        history = (Vector) session.getAttribute(HISTORY_OBJECT_LABEL);
        String id = req.getParameter(ID_LABEL);
        if (id != null) {
            selectedIdx = 0;
            try {
                int inputIdx = Integer.parseInt(id);
                if (inputIdx < history.size()) {
                    selectedIdx = inputIdx;
                }
            } catch (NumberFormatException ex) {
            }
            historyTag.setSelected(Integer.toString(selectedIdx));
        } else {
            // Indicate that no selection has been made
            selectedIdx = -1;
            historyTag.removeAllSelections();
        }


        // Rebuild the history list from the session data and set the selected item
        if ((history != null) && (history.size() > 0)) {
            Hashtable options = new Hashtable(history.size());
            CMnQueryData data = null;
            for (int idx = 0; idx < history.size(); idx++) {
                data = (CMnQueryData) history.get(idx);
                String name = data.getName();
                if ((name == null) || (name.trim().length() < 1)) {
                    // If the user didn't supply a name, grab the query text
                    name = data.getSQL();
                    if (name != null) {
                        name = name.replaceAll("\\s+", " ");
                        if (name.length() < 1) {
                            name = "unknown";
                        } else if (name.length() > 20) {
                            name = name.substring(0, 19);
                        }
                    }
                }
                options.put(Integer.toString(idx), name);
            }
            historyTag.setOptions(options);

        }
    }


    /**
     * Return TRUE if the user has selected an action.
     *
     * @return TRUE if an action has been selected
     */
    public boolean hasAction() {
        return actionSelected;
    }

    /**
     * Return TRUE if the session has at least one query stored in the history
     * vector. 
     *
     * @return TRUE if a history exists 
     */
    public boolean hasHistory() {
        boolean result = false;
        if ((history != null) && (history.size() > 0)) {
            result = true;
        }

        return result;
    }


    /**
     * Return the list of history data from the session.  If no history exists
     * in the session, a null will be returned.
     *
     * @return List of query objects
     */
    public Vector getHistory() {
        return history;
    }

    /**
     * Obtain the currently selected history item.  The values must first be populated by
     * calling the setValues method.  A null object may be returned if no data was
     * found in the request.
     *
     * @return   Database query data from the history 
     */
    public CMnQueryData getSelected() {
        CMnQueryData data = null;

        if ((selectedIdx >= 0) && (history != null) && (selectedIdx < history.size())) {
            data = (CMnQueryData) history.get(selectedIdx);
        }

        return data;
    }


    /**
     * Render the query history form.
     */
    public String toString() {
        StringBuffer html = new StringBuffer();

        html.append("<table border=\"0\" cellspacing=\"0\" cellpadding=\"2\" width=\"100%\" bgcolor=\"#FFFFFF\">\n");
        html.append("  <tr>\n");
        html.append("    <td valign=\"top\">\n");

        html.append("Action:<br>\n" + actionTag.toString() + "<br>\n");
        html.append("History:<br>\n" + historyTag.toString() + "\n");

        html.append("    </td>\n");
        html.append("  </tr>\n");
        html.append("</table>\n");

        return html.toString();
    }


    /**
     * Create a table which defines a title and border for the contents.
     *
     * @param   title   Text to be placed in the title bar
     * @param   content Content of the table
     */
    public String getTitledBorder(String content) {
        return getTitledBorder(DEFAULT_TITLE, content);
    }

}

