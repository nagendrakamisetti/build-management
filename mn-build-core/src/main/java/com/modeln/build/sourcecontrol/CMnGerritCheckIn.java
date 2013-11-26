package com.modeln.build.sourcecontrol;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class represents a Gerrit check-in. 
 */
public class CMnGerritCheckIn {

    /** String used to identify the start of a Gerrit Change ID */
    public static final String CHANGE_ID_PREFIX = "Change-Id: ";

    /** Length of the Gerrit Change ID */
    public static final int CHANGE_ID_LENGTH = 41;


    /**
     * Parse the git commit message to obtain the Gerrit change ID.
     *
     * @param  msg   Commit message
     * @return Gerrit change ID
     */
    public static String parseChangeId(String msg) {
        String id = null;

        if (msg != null) {
            int idxStart = msg.indexOf(CHANGE_ID_PREFIX);
            int length = CHANGE_ID_PREFIX.length() + CHANGE_ID_LENGTH;
            int idxEnd = idxStart + length;
            if ((idxStart > 0) && (msg.length() > idxEnd))  {
                id = msg.substring(idxStart, idxEnd);
            }
        }

        return id;
    }

}

