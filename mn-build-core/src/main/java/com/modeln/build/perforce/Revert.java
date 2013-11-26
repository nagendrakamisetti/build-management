/*
 * Copyright 2000-2003 by Model N, Inc.  All Rights Reserved.
 * 
 * This software is the confidential and proprietary information
 * of Model N, Inc ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you
 * entered into with Model N, Inc.
 */
package com.modeln.build.perforce;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;



/**
 * Perform an integrate operation.
 *
 * @author Shawn Stafford
 */
public class Revert {

    /** Log4j */
    private static Logger logger = Logger.getLogger(Revert.class.getName());

    /**
     * Run Perforce "revert" of a client workspace file against its the depot file.
     * The file is only compared if the file is opened for edit.
     * Whitespace changes are ignored.
     * This is equivalent to executing the following p4 command:
     * <blockquote>
     *   p4 revert -db
     * </blockquote>
     *
     * @param   Path of the file to revert
     * @return  boolean indicating if the workspace and the depot files are the same, ignoring white spaces
     */
    public static boolean isSuccessful(String file) throws IllegalArgumentException {
        if ((file==null)||(file=="")) {
                throw new IllegalArgumentException("Missing filename");
        }
        String result = execute("", file);
        if (result==null) {
                logger.debug("Unable to revert file "+file+".\n See /var/tmp/servicepatch.detail.log for details.");
                return false;
        }
        return true;
    }

    /**
     * Discard changes from an opened file 
     *  
     * This is equivalent to executing the following p4 command:
     * <blockquote>
     *   p4 revert [flag] [file]
     * </blockquote>
     *
     * @return  the revert content
     */
    private static String execute(String flag, String file) {
        String cmd, errText, outText;
        boolean success = false;
        CommonResult result= null;
        try {
                if (flag == "") {
                        cmd = "p4 revert "+file;
                } else {
                        cmd = "p4 revert "+flag+" "+file;
                }
            result = Common.exec(cmd, true, true);
            errText = result.getStdErrText();
            if ((errText==null)||(errText.compareTo("")==0)) {
                success = true;
            } else {
                logger.debug(errText.trim());
            }
        } catch (IOException ex) {
            logger.error("Unable to perform the Perforce command.");
            ex.printStackTrace();
        } catch (InterruptedException ex) {
                ex.printStackTrace();
        } catch (RuntimeException ex) {
                ex.printStackTrace();
        }
        /* set returnText */
        if (success) {
                outText = result.getStdOutText();
                if ( outText!=null ) {
                     return outText.trim();
                }
        }
        return null;
    }
 
}
