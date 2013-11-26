/*
 * ProtectedCommand.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.web.application;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;

import com.modeln.build.web.errors.*;
import com.modeln.build.common.logging.SessionLog;
import com.modeln.build.common.data.account.*;
import com.modeln.build.web.data.SessionTicket;
import com.modeln.build.web.errors.*;
import com.modeln.build.web.util.*;

/**
 * The ChartCommand provides command methods which allow the
 * WebApplication to display JFreeChart objects found in the
 * user session. 
 * 
 * @version            $Revision: 1.1.1.1 $  
 * @author             Shawn Stafford
 */
public class ChartCommand extends BaseCommand implements Command {


    /**
     * Perform any actions which must be performed before the command
     * can be executed.
     *
     * @param   app     Application which called the command
     * @param   req     HttpServletRequest object
     * @param   res     HttpServletResponse object
     */
    public CommandResult prepare(WebApplication app, HttpServletRequest req, HttpServletResponse res)
        throws ApplicationException
    {
        // Make sure the application is supplied before proceeding
        if (app == null) {
            throw new ApplicationException(
                ErrorMap.INVALID_APPLICATION, "Unable to access the calling application.");
        }

        CommandResult result = new CommandResult();

        // Log the current action
        commonLog = app.getLogInstance();

        return result;
    }


    /**
     * This is the primary method which will be used to perform the command
     * actions.  The application will use this method to service incoming
     * requests.  You must pass a reference to the calling application into
     * the service method to allow callback method calls to be performed.
     *
     * @param   app     Application which called the command
     * @param   req     HttpServletRequest object
     * @param   res     HttpServletResponse object
     */
    public CommandResult execute(WebApplication app, HttpServletRequest req, HttpServletResponse res)
        throws ApplicationException
    {
        // Execute the generic actions for all commands
        CommandResult result = prepare(app, req, res);

        // Execute the actions for the command

        return result;
    }


    /**
     * Perform any actions which must be performed after the command
     * has been executed.  This method is not automatically called at the
     * end of the execute method, so you must explicitly call it within
     * every implementation of the execute method.
     *
     * @param   app     Application which called the command
     * @param   req     HttpServletRequest object
     * @param   res     HttpServletResponse object
     * @param   cmdInfo Command response which determines final actions
     */
    public final void finalize(WebApplication app, HttpServletRequest req, HttpServletResponse res, CommandResult cmdInfo)
        throws ApplicationException
    {
        // Send any pending cookies
        Cookie[] cookies = SessionUtility.getCookies(req.getSession());
        if ((cookies != null) && (cookies.length > 0)) {
            HttpUtility.addCookies(req, res, cookies);
        }

        // Create a timestamp cookie
        HttpUtility.setTicket(req, res, new SessionTicket());

        // Obtain the name of the session object which contains the chart data
        String chartName = req.getParameter("chart");

        // Obtain the height and width for the chart
        int chartHeight = Integer.parseInt(req.getParameter("height"));
        int chartWidth  = Integer.parseInt(req.getParameter("width"));


        // Obtain the chart from the session and display it to the user
        if (cmdInfo.containsError()) {
            throw new ApplicationException(cmdInfo.getError().getErrorCode(), cmdInfo.getError().getErrorMsg());
        } else {
            try {
                JFreeChart chart = (JFreeChart) req.getSession().getAttribute(chartName);
                ChartUtilities.writeChartAsPNG(res.getOutputStream(), chart, chartHeight, chartWidth);
                res.setContentType("image/png");
            } catch (IOException ioex) {
                throw new ApplicationException(ErrorMap.APPLICATION_DISPLAY_FAILURE,
                    "Failed to render chart: " + chartName);
            }

        }

    }


}
