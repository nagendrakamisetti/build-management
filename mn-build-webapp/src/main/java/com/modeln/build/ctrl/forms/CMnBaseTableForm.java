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
import java.text.SimpleDateFormat;
import javax.servlet.http.HttpServletRequest;
import com.modeln.build.web.tags.TextTag;
import com.modeln.build.web.tags.SelectTag;



/**
 * This provides base functionality for all table-based HTML forms.
 * 
 * @author  Shawn Stafford
 */
public class CMnBaseTableForm extends CMnBaseTestForm {

    /** Form input element specifying the number of allowable search results */
    private static final String[] limitList = {"20", "50", "100"};
    protected SelectTag resultSizeTag = new SelectTag("limit", limitList);


    /**
     * Construct a base form.
     * 
     * @param  url    URL to use when submitting form input
     */
    public CMnBaseTableForm(URL form, URL images) {
        super(form, images);
    }

    /**
     * Configure the base set of input tags.
     */
    private void configureTags() {
        resultSizeTag.setSize(1);
    }

    /**
     * Set the input fields by examining the HTTP request to see if
     * a value was submitted.
     *
     * @param   req     HTTP request
     */
    public void setValues(HttpServletRequest req) {
        resultSizeTag.setValue(req);
    }

    /**
     * Return the maximum number of rows to display in the table.
     *
     * @return Maximum number of rows in the table
     */
    public int getMaxRows() {
        if (resultSizeTag.isComplete()) {
            String[] selection = resultSizeTag.getSelected();
            return Integer.parseInt(selection[0]); 
        } else {
            return Integer.parseInt(limitList[0]);
        }
    } 


}
