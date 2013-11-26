package com.modeln.build.sourcecontrol;

import com.modeln.build.common.data.account.CMnUser;
import com.modeln.build.sdtracker.CMnBug;

import java.io.File;
import java.lang.Exception;
import java.util.HashMap;
import java.util.List;

import java.lang.String;


/**
 * The source control server interface represents the general source control
 * operations that will be used by the service patch tool.
 *
 * @author Shawn Stafford
 */
public interface IMnServer {

    /** Number of milliseconds to wait for a server response */
    public static final int SERVER_TIMEOUT = 5000;

    /** Describe the amount of certainty that two check-ins are completely identical */
    public static final float RELATIVE_CERTAINTY_FULL = 1.0f;

    /** Describe the amount of certainty that two check-ins are completely identical */
    public static final float RELATIVE_CERTAINTY_HIGH = 0.8f;

    /** Describe the amount of certainty that two check-ins are completely identical */
    public static final float RELATIVE_CERTAINTY_MEDIUM = 0.5f;

    /** Describe the amount of certainty that two check-ins are completely identical */
    public static final float RELATIVE_CERTAINTY_LOW = 0.2f;

    /** Describe the amount of certainty that two check-ins are completely identical */
    public static final float RELATIVE_CERTAINTY_NONE = 0.0f;




    /**
     * Result of a merge option.
     *
     * SUCCESS  - All merge operations were successful
     * FAILURE  - At least one merge operation failed
     * CONFLICT - One or more operations require manual conflict resolution
     * ERROR    - An error prevented the merge operation from executing
     */
    public enum MergeResult {
        SUCCESS,
        FAILURE,
        CONFLICT
    }

    /**
     * Determine whether the server should prompt the user for input.
     *
     * @param   enabled     Prompt the user for input if necessary
     */
    public void setInteractiveMode(boolean enabled);

    /**
     * Determine whether the java API or a native command-line client should
     * be used to access the source control system.
     *
     * @param   enabled     Use the native command-line client if true, use the Java API if false
     */
    public void setNativeMode(boolean enabled);

    /**
     * Set up access to the source code repository.
     * On a distributed system such as git, this might include cloning the repository
     * and setting local configuration values such as user and e-mail.
     * On a centralized system such as perforce, this might include setting up a 
     * clientspec.
     *
     * @param  root   Root directory for the source control repository
     */
    public void init(File root) throws Exception;

    /**
     * Return a list of all bugs and their corresponding check-in IDs.
     *
     * @param   branch     Branch name
     * @param   start      Starting check-in from which the branch was created
     * @return  List of bugs and their corresponding check-in IDs
     */
    public HashMap<String, List<String>> getBugMap(String branch, String start) throws Exception;

    /**
     * Return a list of all check-ins and their corresponding bug IDs.
     *
     * @param   branch     Branch name
     * @param   start      Starting check-in from which the branch was created
     * @return  List of check-in IDs and their corresponding bug IDs
     */
    public HashMap<String, List<String>> getCheckInMap(String branch, String start) throws Exception;

    /**
     * Query the source control system for any information about the bug, such as
     * the list of check-ins or the branch. 
     * On a system such as perforce, this would typically query the job information.
     * On a system like git, it would most likely query the notes.
     *
     * @param  id     Bug ID
     * @return Information about the specified bug 
     */
    public CMnBug getBug(String id) throws Exception;

    /**
     * Return the most recent check-in on the branch
     *
     * @param  branch   Branch name
     * @return Information about the check-in
     */
    public CMnCheckIn getHead(String branch) throws Exception;

    /**
     * Query the source control system for any information about the check-in.
     *
     * @param  id     Check-in ID
     * @return Information about the check-in
     */
    public CMnCheckIn getCheckIn(String id) throws Exception;

    /**
     * Return a list of all check-ins on the specified branch.
     *
     * @param   branch     Branch name
     * @param   start      Starting check-in on the branch 
     * @param   end        Ending check-in on the branch 
     * @return List of check-ins
     */
    public List<CMnCheckIn> getCheckInList(String branch, String start, String end) throws Exception;

    /**
     * Sort the list of commits in chronological order.
     *
     * @param  list   List of commits
     */
    public void sort(List<CMnCheckIn> list);

    /**
     * Get the latest copy of the GA source code.
     * On Perforce this would be a sync.
     * On git this would be a a pull.
     *
     * @param   branch     Initial branch
     */
    public void getSource(String branch) throws Exception;

    /**
     * Create a new branch from the specified source.
     *
     * @param   src        Source branch
     * @param   dest       Branch to be created
     * @param   id         Check-in ID on the source branch to use as the branch point
     */
    public void createBranch(String src, String dest, String id) throws Exception;

    /**
     * Determine if the branch already exists.
     *
     * @param branch name of the branch
     * @return TRUE if the branch exists
     */
    public boolean branchExists(String branch);

    /**
     * Determine if the check-ins are identical.
     * Since it is difficult to determine with absolute certainty
     * whether two check-ins are equivalent, this method uses fuzzy
     * logic to quantify the equality.  A value of 0.0 means that
     * the commits are absolutely not equal.  A value of 1.0 means
     * they absolutely are equal.  Values in between represent the
     * relative certainty that the commits are equal.
     * 
     * A pre-defined list of equality values are available to help
     * categorize the results of the equality:
     * RELATIVE_CERTAINTY_FULL
     * RELATIVE_CERTAINTY_HIGH
     * RELATIVE_CERTAINTY_MEDIUM
     * RELATIVE_CERTAINTY_LOW
     * RELATIVE_CERTAINTY_NONE
     *
     * @param   c1    First check-in to compare
     * @param   c2    Second check-in to compare
     * @return 1.0 if the check-ins are identical
     */
    public float getEquality(CMnCheckIn c1, CMnCheckIn c2) throws Exception;

    /**
     * Determine if the commit has been integrated to the service patch branch.
     * Since it is difficult to determine with absolute certainty
     * whether two check-ins are equivalent, this method returns a list
     * of possible matches and their corresponding score for how well they
     * match the specified commit.  This method  uses fuzzy
     * logic to quantify the equality.  A value of 0.0 means that
     * the commit was absolutely not merged.  A value of 1.0 means
     * the commit absolutely was merged.  Values in between represent the
     * relative certainty that the commit has been mereged.
     * 
     * A pre-defined list of values are available to help
     * determine the certainty of the merge:
     * RELATIVE_CERTAINTY_FULL
     * RELATIVE_CERTAINTY_HIGH
     * RELATIVE_CERTAINTY_MEDIUM
     * RELATIVE_CERTAINTY_LOW
     * RELATIVE_CERTAINTY_NONE
     *
     * @param   src        Source branch
     * @param   dest       Destination branch
     * @param   id         Check-in ID of the change to be merged
     * @return List of possible matches 
     */
    public HashMap<CMnCheckIn, Float> isMerged(String src, String dest, String id) throws Exception;

    /**
     * Merge the check-in into the destination branch.
     *
     * @param   src        Source branch
     * @param   dest       Destination branch
     * @param   id         Check-in ID of the change to be merged
     * @return  Result of the merge operation
     */
    public MergeResult merge(String src, String dest, String id) throws Exception;

    /**
     * Merge the list of check-ins into the destination branch.  The list of
     * check-ins supplied to the method constitutes the complete list of
     * check-ins that should exist on the destination branch.  If the
     * destination branch contains any additional check-ins, or order of
     * existing check-ins does not exactly match the order of the supplied
     * list, an alternate branch will be created by branching from the point
     * at which the list diverges from the existing branch.
     *
     * @param   branch     Destination branch
     * @param   alt        Alternate branch created if the list diverges from destination branch
     * @param   list       Complete list of changes to be merged
     * @param   existingList  List of existing check-ins on the branch
     * @return  TRUE if an alternate branch was created
     */
    public boolean merge(String branch, String alt, List<CMnCheckIn> list, List<CMnCheckIn> existingList) throws Exception;

}
