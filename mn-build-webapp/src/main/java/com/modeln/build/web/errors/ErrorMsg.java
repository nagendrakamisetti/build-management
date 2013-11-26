/*
 * ErrorMsg.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.web.errors;

import javax.servlet.http.*;
import com.modeln.build.web.errors.*;
import com.modeln.build.web.util.*;

/**
 * The ErrorMsg methods are used to display or manipulate the error
 * information stored within the HTTP response.
 *
 * @version            $Revision: 1.1.1.1 $
 * @author             Shawn Stafford
 *
 */
public class ErrorMsg {

    /**
     * Return the error message as an HTML representation.
     *
     * @param   req     HTTP request
     * @param   res     HTTP response
     *
     * @return  HTML formatted message
     */
    public static final String render(HttpServletRequest req, HttpServletResponse res) {
        StringBuffer html = new StringBuffer();

        ApplicationError error = HttpUtility.getError(req);
        if (error != null) {
            html.append("<table border='0' cellspacing='2' cellpadding='2' align='center'>\n");
            html.append("  <tr>\n");
            html.append("    <td align='right'>Error:</td>\n");
            html.append("    <td align='left'>code=" + error.getErrorCode() + "</td>\n");
            html.append("    <td align='left'>msg=" + error.getErrorMsg() + "</td>\n");
            html.append("  </tr>\n");
            html.append("  <tr>\n");
            html.append("    <td align='right'>Debug:</td>\n");
            html.append("    <td align='left'>name=" + error.getDebugName() + "</td>\n");
            html.append("    <td align='left'>msg=" + error.getDebugMsg() + "</td>\n");
            html.append("  </tr>\n");
            html.append("</table>\n");
        }

        return html.toString();
    }


}
