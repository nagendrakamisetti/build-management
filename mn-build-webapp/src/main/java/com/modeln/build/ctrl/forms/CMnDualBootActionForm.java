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

import com.modeln.testfw.reporting.CMnDbHostActionData;

import java.net.URL;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import javax.servlet.http.HttpServletRequest;

import com.modeln.build.web.tags.TextTag;
import com.modeln.build.web.tags.DateTag;
import com.modeln.build.web.tags.OptionTag;
import com.modeln.build.web.tags.SelectTag;
import com.modeln.build.web.tags.TagGroup;



/**
 * The action form provides an HTML interface to the action data object.
 * The class manages transfering the data through the HTTP request.
 * Form data can be rendered in either a read-only or input mode.
 * 
 * @author  Shawn Stafford
 */
public class CMnDualBootActionForm extends CMnBaseForm {

    /** Default title used when displaying a title and border */
	public static final String DEFAULT_TITLE = "Dual Boot Action";

    /** Label used to identify a host action object in the session */
    public static final String ACTION_OBJECT_LABEL = "hostAction";

    /** Label used to identify a host action object in the session */
    public static final String ACTION_ID_LABEL = "actionId";
    
    /** Label used to identify a hostname in the request */
    public static final String ACTION_HOSTNAME_LABEL = "hostname";
    
    /** Label used to identify a type of action object in the request */
    public static final String ACTION_TYPE_LABEL = "actionType";

    /** Label used to identify an action date in the request */
    public static final String ACTION_DATE_LABEL = "actionDate";

    /** Label used to identify the action order in the request */
    public static final String ACTION_ORDER_LABEL = "actionOrder";

    /** Label used to identify an action name in the request */
    public static final String ACTION_NAME_LABEL = "actionId";

    /** Label used to identify action arguments in the request */
    public static final String ACTION_ARGS_LABEL = "actionArgs";
    
    
    /** Action ID field */
    private TextTag actionIdTag = new TextTag(ACTION_ID_LABEL);
    
    /** hostname field */
    private TextTag hostnameTag = new TextTag(ACTION_HOSTNAME_LABEL);

    /** action type field */
    private TextTag actionTypeTag = new TextTag(ACTION_TYPE_LABEL);
    
    /** target execution date field */
    private DateTag actionDateTag = new DateTag(ACTION_DATE_LABEL);
    
    /** execution order field */
    private TextTag actionOrderTag = new TextTag(ACTION_ORDER_LABEL);
    
    /** action name field */
    private TextTag actionNameTag = new TextTag(ACTION_NAME_LABEL);
    
    /** action arguments field */
    private TextTag actionArgsTag = new TextTag(ACTION_ARGS_LABEL);
    



    /**
     * Construct an action form.
     *
     * @param  form   URL to use when submitting form input
     * @param  images URL to use when referencing images
     */
    public CMnDualBootActionForm(URL form, URL images) {
        super(form, images);
        setInputMode(false);

        actionIdTag.setWidth(6);
        //actionIdTag.setDisabled(true);
        
        hostnameTag.setWidth(20);
        //hostnameTag.setDisabled(true);
        
        actionTypeTag.setWidth(20);
        //actionTypeTag.setDisabled(true);

        actionOrderTag.setWidth(2);
        //actionOrderTag.setDisabled(true);

        actionNameTag.setWidth(10);
        //actionNameTag.setDisabled(true);

        actionArgsTag.setWidth(40);
        //actionArgsTag.setDisabled(true);


    }


    /**
     * Set the action data that should be rendered in the form.
     *
     * @param  data   Action data
     */
    public void setValues(CMnDbHostActionData data) {
        if (data != null) {
            // Pull the field data from the object
            actionIdTag.setValue(Integer.toString(data.getId()));
            hostnameTag.setValue(data.getHostname());
            actionTypeTag.setValue(data.getType());
            
            GregorianCalendar actionDate = new GregorianCalendar();
            actionDate.setTime(data.getTargetDate());
            actionDateTag.setDate(actionDate);

            actionOrderTag.setValue(Integer.toString(data.getOrder()));
            actionNameTag.setValue(data.getName());
            actionArgsTag.setValue(data.getArguments());
        } else {
            // Clear all of the field values
            hostnameTag.setValue("");
        }
    }

    /**
     * Set the input fields by examining the HTTP request to see if
     * a value was submitted.
     *
     * @param   req     HTTP request
     */
    public void setValues(HttpServletRequest req) {
    	CMnDbHostActionData data = new CMnDbHostActionData();
    	
    	String id = req.getParameter(ACTION_ID_LABEL);
    	data.setId(Integer.parseInt(id));
    	
    	String hostname = req.getParameter(ACTION_HOSTNAME_LABEL);
    	data.setHostname(hostname);
    	
    	String type = req.getParameter(ACTION_TYPE_LABEL);
    	data.setType(type);
    	
    	//String date = req.getParameter(ACTION_DATE_LABEL);
    	//data.setTargetDate(date);
    	
    	String order = req.getParameter(ACTION_ORDER_LABEL);
    	data.setOrder(Integer.parseInt(order));
    	
    	String name = req.getParameter(ACTION_NAME_LABEL);
    	data.setName(name);
    	
    	String args = req.getParameter(ACTION_ARGS_LABEL);
    	data.setArguments(args);
    	
    	setValues(data);
    }


    /**
     * Obtain the action data from the fields.  The values must first be populated by
     * calling the setValues method.  A null object may be returned if no data was
     * found in the request.
     *
     * @return   Build data found in the request
     */
    public CMnDbHostActionData getValues() {
        CMnDbHostActionData data = new CMnDbHostActionData();
        
        data.setHostname(hostnameTag.getValue());

        return data;
    }
 

    /**
     * Render the build data form.
     */
    public String toString() {
        StringBuffer html = new StringBuffer();

        // Obtain the data from the request
        CMnDbHostActionData data = getValues();
        
        // Don't display the ID value to the user
        actionIdTag.setHidden(true);
        html.append(actionIdTag.toString());

        html.append("<table border=\"0\" cellspacing=\"0\" cellpadding=\"2\" width=\"100%\" bgcolor=\"#FFFFFF\">\n");
        html.append("  <tr><td>Hostname</td><td>" + data.getHostname() + "</td></tr>");
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
