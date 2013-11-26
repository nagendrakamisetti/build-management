/*
 * Copyright 2000-2003 by Model N, Inc.  All Rights Reserved.
 * 
 * This software is the confidential and proprietary information
 * of Model N, Inc ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you
 * entered into with Model N, Inc.
 */
package com.modeln.build.ant;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * Creates a lock file to ensure that critical ant targets are not executed
 * by multiple users simultaneously.  In order to ensure that the lock is
 * released when the build exits, regardless of the exit status, the Lock
 * task must also implement BuildListener.  If a lock still exists when the
 * build completes, the listener will attempt to remove the lock.
 *
 * @author Shawn Stafford
 */
public final class Lock extends Task implements BuildListener {

    /** Date format used for lock timestamp information */
    public static final SimpleDateFormat timestamp = new SimpleDateFormat();

    /** Timestamp property within the lock file */
    public static final String TIMESTAMP_PROP = "timestamp";

    /** Task property within the lock file */
    public static final String TASK_PROP = "task";

    /** Task property within the lock file */
    public static final String LOCATION_PROP = "location";

    /** Task PID or Key within the lock file */
    public static final String KEY_PROP = "key";


    /** Acquire a lock if one does not exist */
    public static final String ACQUIRE_LOCK = "acquire";

    /** Release a lock if one exists */
    public static final String RELEASE_LOCK = "release";

    /** Display information about a lock if one exists */
    public static final String DISPLAY_LOCK = "display";

    /** Silently clear a lock if one exists */
    public static final String CLEAR_LOCK = "clear";


    /** Create a list of all possible locking actions  */
    private static final String[] actions = { ACQUIRE_LOCK, RELEASE_LOCK, DISPLAY_LOCK, CLEAR_LOCK };

    /** List of actions requiring a key */
    private static final String[] keyed_actions = { ACQUIRE_LOCK, RELEASE_LOCK };


    /** Lock file containing the lock information */
    private File lockFile;

    /** Locking action to perform */
    private String lockAction;

    /** Identifier used to determine whether the caller has access to the lock. */
    private String key;

    /** Determines whither the lock action is enabled by default */
    private boolean actionEnabled = true;


    /**
     * Set the file which is used to maintain the lock.
     *
     * @param   file    Lock file that determines the lock state
     */
    public void setEnabled(boolean enabled) {
        actionEnabled = enabled;
    }


    /**
     * Set the file which is used to maintain the lock.
     *
     * @param   file    Lock file that determines the lock state
     */
    public void setLockFile(File file) {
        lockFile = file;
    }


    /**
     * Set the unique process identifier that corresponds to the task which 
     * issued the lock.  This is generally just a random number that is 
     * created by the calling Ant script and used throughout the script to
     * access the lock.
     *
     * @param   key    Unique value that identifies the calling process
     */
    public void setKey(String key) {
        this.key = key;
    }


    /**
     * Set the locking action that is to be performed.  Aquiring a lock will
     * create a lock file.  Releasing a lock will remove the lock file.
     * Displaying the lock will return the information contained in the 
     * lock file.
     *
     * @param   action      Locking action to perform
     */
    public void setAction(String action) throws BuildException {
        if (isActionValid(action)) {
            action = action.toLowerCase();
            lockAction = action;
        } else {
            throw new BuildException("Invalid locking action: " + action, getLocation());
        }
    }

    /**
     * Determine if the specified locking action is valid.
     *
     * @param   action      Value which is checked against list of valid actions
     */
    private static boolean isActionValid(String action) {
        if ((action != null) && (action.length() > 0)) {
            // Cycle through the list of valid actions
            for (int idx = 0; idx < actions.length; idx++) {
                if (action.equalsIgnoreCase(actions[idx])) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Determine if the specified locking action requires a key.
     *
     * @param   action      Action name which is used to determine key requirements
     */
    private static boolean keyRequired(String action) {
        if ((action != null) && (action.length() > 0)) {
            // Cycle through the list of keyed actions
            for (int idx = 0; idx < keyed_actions.length; idx++) {
                if (action.equalsIgnoreCase(keyed_actions[idx])) {
                    return true;
                }
            }
        }

        return false;
    }


    /**
     * Perform the locking action.
     *
     * @throws  BuildException if the locking action fails
     */
    public void execute() throws BuildException {
        if ((lockAction != null) && (lockAction.length() > 0)) {
            // Ensure that a valid key has been specified
            if ((key == null) && (keyRequired(lockAction))) {
                throw new BuildException("The required KEY attribute is missing.");
            }

            // Determine the correct locking action to perform
            if (!actionEnabled) {
                log("Locking action disabled: " + lockAction);
            } else if (lockAction.equals(ACQUIRE_LOCK)) {
                acquire();
            } else if (lockAction.equals(RELEASE_LOCK)) {
                release();
            } else if (lockAction.equals(DISPLAY_LOCK)) {
                display();
            } else if (lockAction.equals(CLEAR_LOCK)) {
                clear();
            } else {
                throw new BuildException("Invalid locking action: " + lockAction, getLocation());
            }
        } else {
            throw new BuildException("The required ACTION attribute is missing.");
        }
    }

    /**
     * Generate a lock file and populate it with information about the current
     * task.  If a lock already exists, a build exception will be thrown.
     *
     * @throws  BuildException if the lock file cannot be created
     */
    private synchronized void acquire() throws BuildException {
        boolean success = false;

        // Construct the lock information
        Properties props = new Properties();
        props.setProperty(KEY_PROP, key);
        props.setProperty(TASK_PROP, getTaskName());
        props.setProperty(LOCATION_PROP, getLocation().toString());

        // Add a lock event listeners to the project
        project.addBuildListener(this);

        // Determine if it is going to be possible to create a lock file
        File parentDir = lockFile.getParentFile();
        if (parentDir == null) {
            throw new BuildException("Unable to determine the lock file directory: " + lockFile, getLocation());
        }

        FileOutputStream stream = null;
        try {
            // Create any parent directories
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }

            // Acquire the lock
            success = lockFile.createNewFile();
            if (success) {
                stream = new FileOutputStream(lockFile);
                props.setProperty(TIMESTAMP_PROP, timestamp.format(new Date()));
                props.store(stream, "Ant lock file");
            }
        } catch (Exception ex) {
            throw new BuildException("Lock file cannot be created: " + lockFile, ex, getLocation());
        } finally {
            // It is very important that the file is closed 
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (Exception ex) {
                log("Unable to close the lock file: " + lockFile, Project.MSG_DEBUG);
            }
        }

        // Alert the task of a failure
        if (success) {
            log("Lock acquired.");
        } else {
            throw new BuildException("Failed to acquire lock: " + lockFile, getLocation());
        }
    }


    /**
     * Display the lock information to standard out.
     *
     * @throws  BuildException if the lock file is not readable
     */
    private synchronized void display() throws BuildException {
        Properties props = new Properties();
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(lockFile);
            props.load(stream);
            props.list(System.out);
        } catch (Exception ex) {
            throw new BuildException("Unable to read lock file: " + lockFile, ex, getLocation());
        } finally {
            // It is very important that the file is closed 
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (Exception ex) {
                log("Unable to close the lock file: " + lockFile, Project.MSG_DEBUG);
            }
        }
    }


    /**
     * Remove the lock.
     *
     * @throws  BuildException if the lock cannot be released
     */
    private synchronized void release() throws BuildException {
        boolean success = false;
        if (hasValidKey()) {
            success = lockFile.delete();

            // Remove the event listener from the project
            project.removeBuildListener(this);
        } else {
            throw new BuildException("Lock file does not contain matching key: " + key, getLocation());
        }

        if (!success) {
            throw new BuildException("Unable to release lock: " + lockFile, getLocation());
        } else {
            log("Lock released.");
        }
    }

    /**
     * Removes the lock without checking for a valid key and does not 
     * complain if no lock existed.
     */
    private synchronized void clear() {
        if(lockFile.delete()) {
            log("Lock cleared.");
        }
    }

    /**
     * Determine if the current lock request contains the same key value as
     * the value stored in the lock file.  If no lock file exists, the method
     * returns true.
     *
     * @return  TRUE if the key is valid or no lock exists
     */
    private synchronized boolean hasValidKey() {
        boolean valid = false;
        if (lockFile.exists()) {
            Properties props = new Properties();
            FileInputStream stream = null;
            try {
                stream = new FileInputStream(lockFile);
                props.load(stream);
                valid = key.equals(props.getProperty(KEY_PROP));
            } catch (Exception ex) {
                log("Unable to open the lock file: " + lockFile, Project.MSG_DEBUG);
            } finally {
                // It is very important that the file is closed 
                try {
                    if (stream != null) {
                        stream.close();
                    }
                } catch (Exception ex) {
                    log("Unable to close the lock file: " + lockFile, Project.MSG_DEBUG);
                }
            }
        }

        return valid;
    }



    /**
     * Signals that a build has started. This event
     * is fired before any targets have started.
     * 
     * @param event An event with any relevant extra information.
     *              Must not be <code>null</code>.
     */
    public void buildStarted(BuildEvent event) {
    }

    /**
     * Signals that the last target has finished. This event
     * will still be fired if an error occurred during the build.
     * 
     * @param event An event with any relevant extra information.
     *              Must not be <code>null</code>.
     *
     * @see BuildEvent#getException()
     */
    public void buildFinished(BuildEvent event) {
        release();
    }

    /**
     * Signals that a target is starting.
     * 
     * @param event An event with any relevant extra information.
     *              Must not be <code>null</code>.
     *
     * @see BuildEvent#getTarget()
     */
    public void targetStarted(BuildEvent event) {
    }

    /**
     * Signals that a target has finished. This event will
     * still be fired if an error occurred during the build.
     * 
     * @param event An event with any relevant extra information.
     *              Must not be <code>null</code>.
     *
     * @see BuildEvent#getException()
     */
    public void targetFinished(BuildEvent event) {
    }

    /**
     * Signals that a task is starting.
     * 
     * @param event An event with any relevant extra information.
     *              Must not be <code>null</code>.
     *
     * @see BuildEvent#getTask()
     */
    public void taskStarted(BuildEvent event) {
    }

    /**
     * Signals that a task has finished. This event will still
     * be fired if an error occurred during the build.
     *
     * @param event An event with any relevant extra information.
     *              Must not be <code>null</code>.
     *
     * @see BuildEvent#getException()
     */
    public void taskFinished(BuildEvent event) {
    }

    /**
     * Signals a message logging event.
     * 
     * @param event An event with any relevant extra information.
     *              Must not be <code>null</code>.
     *
     * @see BuildEvent#getMessage()
     * @see BuildEvent#getPriority()
     */
    public void messageLogged(BuildEvent event) {
    }

}
