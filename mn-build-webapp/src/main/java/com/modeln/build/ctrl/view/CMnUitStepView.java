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
import com.modeln.testfw.reporting.CMnDbUitStep;
import com.modeln.build.ctrl.forms.IMnTestForm;

import java.net.URL;
import java.util.Iterator;
import java.util.Vector;
import javax.servlet.http.HttpServletRequest;

/**
 * The build form provides an HTML interface to the UIT step object.
 * The class manages transfering the data through the HTTP request.
 * Form data can be rendered in either a read-only or input mode.
 * 
 * @author  Shawn Stafford
 */
public class CMnUitStepView {

    /** Default title used when displaying a title and border */
    private static final String DEFAULT_TITLE = "Test Steps";

    /** UIT step list */
    private Vector stepList;


    /**
     * Construct a status view.
     *
     * @param  stepList   Vector of tests to display the status
     */
    public CMnUitStepView(Vector stepList) {
    	this.stepList = stepList;
    }

    /**
     * Set the UIT steps that should be display in the view
     *
     * @param  stepList   Vector of tests to display the status
     */
    public void setValues(Vector stepList) {
       	this.stepList = stepList;
    }

    /**
     * Set the input fields by examining the HTTP request to see if
     * a value was submitted.
     *
     * @param   req     HTTP request
     */
    public void setValues(HttpServletRequest req) {
 
        //setValues(stepList);
    }

    /**
     * Obtain the uit steps from the fields.  The values must first be populated by
     * calling the setValues method.  A null object may be returned if no data was
     * found in the request.
     *
     * @return   UIT suite found in the request
     */
    public Vector getValues() {
        return stepList;
    }
 

    /**
     * Render the UIT step info form.
     */
    public String toString() {
        StringBuffer html = new StringBuffer();

        html.append("<table border=\"0\" cellspacing=\"0\" cellpadding=\"1\" width=\"50%\">\n");
        html.append("  <tr><td colspan=\"4\"><h2>"+DEFAULT_TITLE+"</h2></td></tr>\n");
        html.append("  <tr>\n");
        html.append("    <td width=\"15%\" NOWRAP><b><u>Step</u></b></td>\n");
        html.append("    <td width=\"70%\" NOWRAP><b><u>Message</u></b></td>\n");
        html.append("    <td width=\"5%\" NOWRAP><b><u>Time</u></b></td>\n");
        html.append("    <td width=\"5%\" align=\"center\" NOWRAP><b><u>Pass</u></b></td>\n");
        html.append("    <td width=\"5%\" align=\"center\" NOWRAP><b><u>Fail</u></b></td>\n");
        html.append("  </tr>\n");
        
        
        Iterator it = stepList.iterator();
        while (it.hasNext()) {
            CMnDbUitStep currentStep = (CMnDbUitStep) it.next();
            String elapsedTime = Long.toString(currentStep.getElapsedTime() / 1000);
            if (currentStep.getStatus() == CMnDbUit.PASS) {
                html.append("  <tr>\n");
                html.append("    <td>" + currentStep.getStepName() + "</a></td>\n");
                html.append("    <td>" + currentStep.getMessage() + "</a></td>\n");
                html.append("    <td align=\"right\" NOWRAP>" + elapsedTime + " s</td>\n");
                html.append("    <td align=\"center\">X</td>\n");
                html.append("    <td align=\"center\"> </td>\n");
                html.append("  </tr>\n");
            } else if (currentStep.getStatus() == CMnDbUit.SKIP) {
                html.append("  <tr>\n");
                html.append("    <td>" + currentStep.getStepName() + "</a></td>\n");
                html.append("    <td>" + currentStep.getMessage() + "</a></td>\n");
                html.append("    <td align=\"center\" colspan=\"3\">SKIPPED</td>\n");
                html.append("  </tr>\n");
            } else {
                html.append("  <tr>\n");
                html.append("    <td bgcolor=\"#FF9999\">" + currentStep.getStepName() + "</a></td>\n");
                html.append("    <td>" + currentStep.getMessage() + "</a></td>\n");
                html.append("    <td align=\"right\" NOWRAP>" + elapsedTime +" s</td>\n");
        	html.append("    <td align=\"center\"> </td>\n");
        	html.append("    <td align=\"center\" bgcolor=\"#FF3333\">X</td>\n");
                html.append("  </tr>\n");
            }
        }
        
        html.append("</table>\n");
        return html.toString();
    }



}
