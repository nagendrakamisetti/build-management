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

import com.modeln.testfw.reporting.CMnDbBuildData;
import com.modeln.testfw.reporting.CMnDbBuildStatusNote;

import java.net.URL;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import javax.servlet.http.HttpServletRequest;

import com.modeln.build.web.tags.TextTag;
import com.modeln.build.web.tags.OptionTag;
import com.modeln.build.web.tags.SelectTag;
import com.modeln.build.web.tags.TagGroup;



/**
 * The build form provides an HTML interface to the status note object.
 * The class manages transfering the data through the HTTP request.
 * Form data can be rendered in either a read-only or input mode.
 * 
 * @author  Shawn Stafford
 */
public class CMnBuildStatusNoteForm extends CMnBaseForm implements IMnBuildForm {

    /** Request attribute label for the build status type */
    public static final String BUILD_STATUS_LABEL = CMnBuildStatusForm.STATUS_LABEL;

    /** Request attribute label for the note data object */
    public static final String NOTE_OBJECT_LABEL = "statusNoteObj";

    /** Request attribute label for the note primary key */
    public static final String NOTE_ID_LABEL = "statusNoteId";

    /** Request attribute label for the note status */
    public static final String NOTE_STATUS_LABEL = "statusNoteType";

    /** Request attribute label for the note comments */
    public static final String NOTE_COMMENTS_LABEL = "statusNoteComments";
 
    /** Default title for a bordered title box */
    public static final String DEFAULT_TITLE = "Status Note";


    /** Build changelist field */
    private TextTag buildIdTag = new TextTag(BUILD_ID_LABEL);

    /** Build status field */
    private TextTag buildStatusTag = new TextTag(BUILD_STATUS_LABEL);

    /** Note ID field */
    private TextTag noteIdTag = new TextTag(NOTE_ID_LABEL);

    /** Note status field */
    private OptionTag noteStatusTag = new OptionTag(NOTE_STATUS_LABEL);

    /** Note comments field */
    private TextTag noteCommentsTag = new TextTag(NOTE_COMMENTS_LABEL);


    /** Status data object that has been reconstructed from the form input. */
    private CMnDbBuildStatusNote noteData = null;


    /**
     * Construct a build form.
     *
     * @param  form   URL to use when submitting form input
     * @param  images URL to use when referencing images
     */
    public CMnBuildStatusNoteForm(URL form, URL images) {
        super(form, images);
        setInputMode(false);

        // Don't allow the Note ID to be modified
        buildIdTag.setHidden(true);
        noteIdTag.setHidden(true);
        buildStatusTag.setHidden(true);

        noteCommentsTag.setHeight(15);
        noteCommentsTag.setWidth(60);

        // Set the list of status options
        Hashtable options = new Hashtable(CMnBuildStatusForm.statusKeys.length);
        for (int idx = 0; idx < CMnBuildStatusForm.statusKeys.length; idx++) {
            options.put(CMnBuildStatusForm.statusKeys[idx], CMnBuildStatusForm.statusValues[idx]);
        }
        noteStatusTag.setOptions(options);
        noteStatusTag.setKeyOrder(CMnBuildStatusForm.statusKeys); 
        noteStatusTag.setMultiple(false);
    }

    /**
     * Set the note data that should be rendered in the form.
     *
     * @param  data   Note data
     */
    public void setValues(String buildId, String buildStatus, CMnDbBuildStatusNote data) {
        buildIdTag.setValue(buildId);
        buildStatusTag.setValue(buildStatus);
        noteData = data;
        if (data != null) {
            // Pull the field data from the object
            if (data.getId() > 0) {
                noteIdTag.setValue(Integer.toString(data.getId()));
            } else {
                // Any note ID equal to zero is assumed to be a new note
                noteIdTag.setValue("");
            }
            noteStatusTag.setSelected(CMnBuildStatusForm.statusKeys[data.getBuildStatus()]);
            noteCommentsTag.setValue(data.getComments());
        } else {
            // Clear all of the field values
            buildIdTag.setValue("");
            noteIdTag.setValue("");
            noteStatusTag.removeAllSelections();
            noteStatusTag.setSelected(buildStatus);
            noteCommentsTag.setValue("");
        }
    }

    /**
     * Parse the note status and return the integer equivalent.
     * 
     * @param   status   Status value
     * @return  Status value
     */
    public int parseNoteStatus(String status) {
        int val = 0;
        for (int idx = 0; idx < CMnBuildStatusForm.statusKeys.length; idx++) {
            if (CMnBuildStatusForm.statusKeys[idx].equalsIgnoreCase(status)) {
                val = idx;
            }
        }
        return val;
    }

    /**
     * Set the input fields by examining the HTTP request to see if
     * a value was submitted.
     *
     * @param   req     HTTP request
     */
    public void setValues(HttpServletRequest req) {
        String buildId = req.getParameter(BUILD_ID_LABEL);
        String buildStatus = req.getParameter(BUILD_STATUS_LABEL);

        CMnDbBuildStatusNote data = (CMnDbBuildStatusNote) req.getAttribute(NOTE_OBJECT_LABEL);
        if (data == null) {
            String noteId = req.getParameter(NOTE_ID_LABEL);
            int noteIdVal = 0;
            if ((noteId != null) && (noteId.length() > 0)) {
                noteIdVal = Integer.parseInt(noteId);
            }
            String noteStatus = req.getParameter(NOTE_STATUS_LABEL);
            if ((noteStatus == null) || (noteStatus.length() == 0)) {
                noteStatus = buildStatus;
            }
            String noteComment = req.getParameter(NOTE_COMMENTS_LABEL);
            data = new CMnDbBuildStatusNote(noteIdVal);
            data.setBuildStatus(parseNoteStatus(noteStatus));
            data.setComments(noteComment);
        }

        if (buildId != null) {
            setValues(buildId, buildStatus, data);
        }
    }


    /**
     * Obtain the status notes data from the fields.  The values must first be populated by
     * calling the setValues method.  A null object may be returned if no data was
     * found in the request.
     *
     * @return   Notes data found in the request
     */
    public CMnDbBuildStatusNote getValues() {
        return noteData;
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


    /**
     * Render the build data form.
     */
    public String toString() {
        StringBuffer html = new StringBuffer();

        if (inputEnabled) {
            html.append(buildIdTag.toString());
            html.append(noteIdTag.toString());
            html.append(buildStatusTag.toString());

            html.append("<table border=\"0\" cellspacing=\"0\" cellpadding=\"2\" width=\"100%\" bgcolor=\"#FFFFFF\">\n");

            // Note status
            html.append("  <tr>\n");
            html.append("    <td valign=top width=\"30%\"><b>Note Status:</b></td>\n");
            html.append("    <td valign=top width=\"70%\">");
            html.append(noteStatusTag.toString());
            html.append("</td>\n");
            html.append("  </tr>\n");

            // Note comments
            html.append("  <tr>\n");
            html.append("    <td width=\"30%\"><b>Note Comments:</b></td>\n");
            html.append("    <td width=\"70%\"></td>");
            html.append("  </tr>\n");
            html.append("  <tr>\n");
            html.append("    <td colspan=2>\n");
            html.append(noteCommentsTag.toString());
            html.append("</td>\n");
            html.append("  </tr>\n");

            html.append("</table>\n");
        } else {
            // Note comments
            html.append(noteCommentsTag.getValue());
        }

        return html.toString();
    }


}
