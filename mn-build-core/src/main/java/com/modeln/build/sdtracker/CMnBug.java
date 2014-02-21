package com.modeln.build.sdtracker;

import com.modeln.build.sourcecontrol.CMnCheckIn;

import java.lang.String;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;


/**
 * Data object representing a single SDR in the system. 
 *
 * @version      1.0
 * @author       Shawn Stafford (sstafford@modeln.com)
 */
public class CMnBug {

    /** Bug ID (unique primary database key) */
    private Integer id = null;

    /** Bug number (non-unique, may differ from bug ID) */
    private Integer num = null; 

    /** Product vertical */
    private String vertical = null;

    /** Bug title */
    private String title = null;

    /** Bug description */
    private String description = null;

    /** Product release version */
    private String release = null;

    /** Type of fix (defect, enhancement, etc) */
    private String fixType;

    /** Sub-type of fix (data, performance, etc) */
    private String fixSubType;

    /** Bug status */
    private String status = null;

    /** Bug severity */
    private String severity = null;

    /** Bug product area */
    private String productArea = null;

    /** Resolved date */
    private Date resolveDate = null;

    /** List of check-ins associated with the bug */
    private Vector<CMnCheckIn> checkins = null;

    /**
     * Set the ID of the bug.
     *
     * @param  num   Bug ID
     */
    public void setId(int num) {
        id = new Integer(num);
    }

    /**
     * Return the ID of the bug. 
     *
     * @return Bug ID
     */
    public Integer getId() {
        return id;
    }

    /**
     * Set the bug number.  The bug number is a non-unique key which may 
     * differ from the Bug ID if the bug was migrated into the system
     * from a different bug tracking system. 
     *
     * @param  num   Bug number
     */
    public void setNumber(int num) {
        this.num = new Integer(num);
    }

    /**
     * Return the bug number. 
     *
     * @return Bug number
     */
    public Integer getNumber() {
        return num;
    }


    /**
     * Set the product vertical to which the bug belongs. 
     *
     * @param  vert  Product vertical 
     */
    public void setVertical(String vert) {
        vertical = vert;
    }

    /**
     * Return the product vertical. 
     *
     * @return Product vertical 
     */
    public String getVertical() {
        return vertical;
    }


    /**
     * Set the product release version.
     *
     * @param  ver   Release version
     */
    public void setRelease(String ver) {
        release = ver;
    }

    /**
     * Return the product release version.
     *
     * @return  Release version
     */
    public String getRelease() {
        return release;
    }

    /**
     * Set the bug status. 
     *
     * @param  state  Bug status
     */
    public void setStatus(String state) {
        status = state;
    }

    /**
     * Return the bug status. 
     *
     * @return  Bug status 
     */
    public String getStatus() {
        return status;
    }

                       

    /**
     * Set the resolve date.
     *
     * @param   date   Resolve date
     */
    public void setResolveDate(Date date) {
        resolveDate = date;
    }

    /**
     * Return the resolve date.
     *
     * @return Resolved date
     */
    public Date getResolveDate() {
        return resolveDate;
    }


    /**
     * Set the title of the bug.
     *
     * @param  text   Bug title
     */
    public void setTitle(String text) {
        title = text;
    }

    /**
     * Return the title of the bug.
     *
     * @return Bug title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set the description of the bug.
     *
     * @param  text   Bug description
     */
    public void setDescription(String text) {
        description = text;
    }

    /**
     * Return the description of the bug.
     *
     * @return Bug description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the severity of the bug 
     *
     * @param  severity   Severity of the bug 
     */
    public void setSeverity(String severity) {
        this.severity = severity;
    }

    /**
     * Return the severity of the bug. 
     *
     * @return Severity of the bug 
     */
    public String getSeverity() {
        return severity;
    }


    /**
     * Set the type of fix 
     *
     * @param  type   Fix type 
     */
    public void setType(String type) {
        fixType = type;
    }

    /**
     * Return fix type 
     *
     * @return Fix type
     */
    public String getType() {
        return fixType;
    }


    /**
     * Set the sub-type of fix 
     *
     * @param  subtype   Fix subtype 
     */
    public void setSubType(String subtype) {
        fixSubType = subtype;
    }

    /**
     * Return fix sub-type 
     *
     * @return Fix subtype
     */
    public String getSubType() {
        return fixSubType;
    }

    /**
     * Set the product area 
     *
     * @param  area     Product area 
     */
    public void setProductArea(String area) {
        productArea = area;
    }

    /**
     * Return the product area 
     *
     * @return Product area 
     */
    public String getProductArea() {
        return productArea;
    }




    /**
     * Compares the check-in ID to the list of check-ins.
     *
     * @param   id    Check-in ID
     * @return  TRUE if the check-in exists
     */
    public boolean hasCheckIn(String id) {
        if (checkins != null) {
            Iterator iter = checkins.iterator();
            while (iter.hasNext()) {
                CMnCheckIn current = (CMnCheckIn) iter.next();
                if (id.equalsIgnoreCase(current.getId())) {
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * Add the checkin to the list.
     *
     * @param  checkin   CheckIn information
     */
    public void addCheckIn(CMnCheckIn checkin) {
        if (checkins == null) {
            checkins = new Vector<CMnCheckIn>();
        }
        checkins.add(checkin);
    }

    /**
     * Set the list of checkins.
     *
     * @param   list    List of check-ins
     */
    public void setCheckIns(Vector<CMnCheckIn> list) {
        checkins = list;
    }

    /**
     * Returns the list of checkins associated with the bug.
     *
     * @return  List of check-ins
     */
    public Vector<CMnCheckIn> getCheckIns() {
        return checkins;
    }

    /**
     * Determine if the bug contains any check-ins.
     *
     * @return TRUE if there are check-ins associated with the bug, FALSE otherwise
     */
    public boolean hasCheckIns() {
        return ((checkins != null) && (checkins.size() > 0));
    }

}

