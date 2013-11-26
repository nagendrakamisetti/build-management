package com.modeln.build.ctrl;

import com.modeln.build.web.errors.ErrorMap;

/**
 * The CMnErrorMap class defines a generic list of error codes which will
 * be used within the Build Management Application.
 * <p>
 *
 * <table>
 *   <tr><td>Build errors:</td><td>2000 - 2999</td></tr>
 *   <tr><td>Service Patch errors:</td><td>3000 - 3999</td></tr>
 * </table>
 *
 * @author             Shawn Stafford
 */
public class CMnErrorMap extends ErrorMap {

    /** The specified build ID is invalid or does not exist */
    public static final int INVALID_BUILD_ID = 2000;

}

