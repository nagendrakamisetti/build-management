/* 
* Copyright 2000-2003 by Model N, Inc.  All Rights Reserved. 
* 
* This software is the confidential and proprietary information 
* of Model N, Inc ("Confidential Information").  You shall not 
* disclose such Confidential Information and shall use it only 
* in accordance with the terms of the license agreement you 
* entered into with Model N, Inc. 
*/
package com.modeln.build.ctrl.view;

import com.modeln.testfw.reporting.CMnDbUit;
import com.modeln.build.ctrl.forms.IMnTestForm;

import java.net.URL;
import java.util.Iterator;
import java.util.Vector;
import javax.servlet.http.HttpServletRequest;

/**
 * The build form provides an HTML interface to the UIT suite object.
 * The class manages transfering the data through the HTTP request.
 * Form data can be rendered in either a read-only or input mode.
 * 
 * @author  Shawn Stafford
 */
public class CMnUitHistoryView {

    /** Default title used when displaying a title and border */
    private static final String DEFAULT_TITLE = "User Interface Test History";

    /** UIT list */
    private Vector testList;

    /** App URL */
    private URL appUrl;


    /**
     * Construct a status view.
     *
     * @param  testList   Vector of tests to display the status
     */
    public CMnUitHistoryView(URL appUrl, Vector testList) {
    	this.appUrl = appUrl;
    	this.testList = testList;
    }

    /**
     * Set the UIT tests that should be display in the view
     *
     * @param  testList   Vector of tests to display the status
     */
    public void setValues(Vector testList) {
       	this.testList = testList;
    }

    /**
     * Set the input fields by examining the HTTP request to see if
     * a value was submitted.
     *
     * @param   req     HTTP request
     */
    public void setValues(HttpServletRequest req) {
 
        //setValues(testList);
    }

    /**
     * Set the UIT URL that points to CMnUitData
     *
     * @param  uitUrl   String of the URL pointing to CMnUitData
     */
    public void setAppUrl(URL appUrl) {
       	this.appUrl = appUrl;
    }

    /**
     * Get the UIT URL that points to CMnUitData
     *
     * @param  uitUrl   String of the URL pointing to CMnUitData
     */
    public String getAppUrl() {
       	return appUrl.toString();
    }

    /**
     * Obtain the uit suite from the fields.  The values must first be populated by
     * calling the setValues method.  A null object may be returned if no data was
     * found in the request.
     *
     * @return   UIT suite found in the request
     */
    public Vector getValues() {
        return testList;
    }
 

    /**
     * Render the UIT suite info form.
     */
    public String toString() {
        StringBuffer html = new StringBuffer();

        html.append("<h2>"+DEFAULT_TITLE+"</h2>");
        
        Iterator it = testList.iterator();
        if (testList != null) {
        	//TODO test
            html.append("number of tests: "+Integer.toString(testList.size()));
            
            html.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"2\">\n");

        	while (it.hasNext()) {
	        	CMnDbUit currentTest = (CMnDbUit)it.next();
	            String currentUrl = getAppUrl() + "?"+IMnTestForm.TEST_ID_LABEL+"=" + currentTest.getId();
	            String status = null;
	            String statusBg = null;
	            switch (currentTest.getStatus()) {
	            case CMnDbUit.PASS: status="PASS"; statusBg= "#FFFFFF"; break;
                    case CMnDbUit.SKIP: status="SKIP"; statusBg= "#CCCCCC"; break;
	            case CMnDbUit.FAIL: status="FAIL"; statusBg= "#FF9999"; break;
	            case CMnDbUit.ERROR: status="ERROR"; statusBg= "#FF9999"; break;
	            default: status = "UNKNOWN"; statusBg = "#EEEEEE";
	            }
	            html.append("  <tr>\n");
	            html.append("    <td bgcolor=\""+statusBg+"\"><a href=\""+currentUrl+"\">"+currentTest.getStartTime() +"</a></td>\n");
	        	html.append("    <td bgcolor=\""+statusBg+"\">"+status+"</td>\n");
	            html.append("  </tr>\n");
	        }
            html.append("</table>\n");
        } else {
            html.append("<b>No results found </b>");
        }
        
        return html.toString();
    }


}
