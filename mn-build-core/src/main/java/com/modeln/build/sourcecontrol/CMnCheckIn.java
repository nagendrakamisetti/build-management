package com.modeln.build.sourcecontrol;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * This is a base class which represents a single source control check-in.
 * This class should be extended to include functionality or information
 * specific to a source control implementation such as Perforce or git. 
 */
public class CMnCheckIn {

    /** Maximum display length of the check-in ID */
    public static final int FORMATTED_ID_MAXLENGTH = 8;

    /** Check-in status in the source control system */
    public enum State {
        UNKNOWN,
        INVALID,
        PENDING,
        SUBMITTED
    }

    /** Unique identifier within the source control system */
    private String id;

    /** List of files in the checkin */
    private List<CMnFile> files = null;

    /** User who performed the code modification */
    private CMnUser author;

    /** Description of the checkin submitted by the user */
    private String description;

    /** Date when the checkin was commited into source control */
    private Date date;

    /** Status of the checkin in source control */
    private State currentState;

    /** List of related checkins and their relative weight */
    private HashMap<CMnCheckIn, Float> related;

    /**
     * Construct a default check-in object in PENDING state.
     */
    public CMnCheckIn() {
        this(State.PENDING);
    }

    public CMnCheckIn(State state) {
        setCurrentState(state);
        setDate(new Date());
        files = new ArrayList<CMnFile>();
    }


    /**
     * returns the SHA-1 string if the state is either LOCAL_COMMIT or SUBMITTED,
     * or the current branch otherwise
     * @return
     */
    public String getId() {
        return id;
    }


    /**
     * Return a formatted string that can be displayed in the UI.  By default
     * this just returns the ID.  Classes which extend this one may choose to
     * return a formatted or shortened value depending on what is appropriate
     * for the change type.
     *
     * @return  Formatted string
     */
    public String getFormattedId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }

    /**
     * Return the total number of files associated with this check-in.
     *
     * @return Number of files
     */
    public int getFileCount() {
        if (files != null) {
            return files.size();
        } else {
            return 0;
        }
    }

    /**
     * Return the matching file or null if no match is found.
     *
     * @param  filename    Full filename
     * @return File information
     */
    public CMnFile getFile(String filename) {
        CMnFile match = null;

        if (files != null) {
            Iterator iter = files.iterator();
            while (iter.hasNext()) {
                CMnFile current = (CMnFile) iter.next();
                if (filename.equals(current.getFilename())) {
                    return current;
                }
            }
        }

        return match;
    } 

    public List<CMnFile> getFiles() {
        return files;
    }

    public void setFiles(List<CMnFile> files) {
        this.files = files;
    }

    /**
     *
     * @param files path to file (relative from root dir)
     */
    public void addFiles(List<CMnFile> files) {
        for (int i = 0; i < files.size(); i++){
            this.files.add(files.get(i));
        }
    }

    public CMnUser getAuthor() {
        return author;
    }

    public void setAuthor(CMnUser author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public String getDescription(int length) {
        String desc = null;
        if ((description != null) && (length > 0) && (description.length() > length)) {
            return description.substring(0, length - 1);
        }
        return desc;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public State getCurrentState() {
        return currentState;
    }

    public void setCurrentState(State currentState) {
        this.currentState = currentState;
    }

    public HashMap<CMnCheckIn, Float> getRelatedCheckins() {
        return related;
    }

    public void setRelatedCheckins(HashMap<CMnCheckIn, Float> checkins) {
        related = checkins;
    }

    /**
     * Determine if the specified file exists in the check-in.
     *
     * @param  file     File to search for
     * @return TRUE if the file exists in the check-in, false otherwise
     */
    public boolean contains(CMnFile file) {
        boolean match = false;

        if ((files != null) && (file != null)) {
            Iterator fileIter = files.iterator();
            while (fileIter.hasNext()) {
                CMnFile current = (CMnFile) fileIter.next();

                // Determine if the current file matches the target 
                if (current != null) {
                    String name1 = current.getFilename();
                    String name2 = file.getFilename();
                    if (name1.equalsIgnoreCase(name2)) {
                        return true;
                    }
                }
            }
        }

        return match;
    }

    /**
     * Return the list of files that are common between the two check-ins.
     * If no common files are found, null will be returned.
     *
     * @param  checkin   Files to compare
     * @return List of filenames
     */
    public List<String> getCommonFiles(CMnCheckIn checkin) {
        List<String> common = null;

        if ((files != null) && (checkin.getFiles() != null)) {
            Iterator fileIter = checkin.getFiles().iterator();
            while (fileIter.hasNext()) {
                CMnFile file = (CMnFile) fileIter.next();
                if (contains(file)) {

                    // Initialize the empty list of common files
                    if (common == null) {
                        common = new ArrayList<String>();
                    }

                    // Add the common file to the list
                    common.add(file.getFilename());
                }
            }
        }

        return common;
    }


    /**
     * Determine if any of the related check-ins have a score which is
     * equal to or greater than the specified value.
     *
     * @param   target    Target score that the commits must meet or exceed 
     * @return TRUE if any of the commits exceed the target value
     */
    public boolean hasRelatedCheckin(float target) {
        boolean status = false;

        if ((related != null) && (related.size() > 0)) {
            Set<CMnCheckIn> keys = related.keySet();
            Iterator keyIter = keys.iterator();
            while (keyIter.hasNext()) {
                CMnCheckIn checkin = (CMnCheckIn) keyIter.next();
                Float value = (Float) related.get(checkin);
                if (value >= target) {
                    return true;
                }
            }
        }

        return status;
    }

    /**
     * Return the check-in with the highest score, or null if there
     * are not related check-ins.
     *
     * @return Check-in with the highest score
     */
    public CMnCheckIn getRelatedCheckin() {
        CMnCheckIn match = null;
        float target = 0.0f;

        if ((related != null) && (related.size() > 0)) {
            Set<CMnCheckIn> keys = related.keySet();
            Iterator keyIter = keys.iterator();
            while (keyIter.hasNext()) {
                CMnCheckIn checkin = (CMnCheckIn) keyIter.next();
                Float value = (Float) related.get(checkin);
                if (value >= target) {
                    match = checkin;
                    target = value; 
                }
            }
        }

        return match;
    }

    /**
     * Return the score associated with the related check-in.
     *
     * @return Check-in score
     */
    public float getRelatedCheckin(CMnCheckIn target) {
        float match = 0.0f;

        if ((target != null) && (related != null) && (related.size() > 0)) {
            Float value = (Float) related.get(target);
            if (value != null) {
                match = value.floatValue();
            }
        }

        return match;
    }


}

