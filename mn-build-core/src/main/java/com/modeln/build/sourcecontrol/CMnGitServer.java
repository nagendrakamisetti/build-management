package com.modeln.build.sourcecontrol;

import com.modeln.build.common.data.account.CMnUser;
import com.modeln.build.common.tool.CMnCmdLineTool;
import com.modeln.build.sdtracker.CMnBug;
import com.modeln.build.util.StringUtility;

import java.io.*;
import java.lang.*;
import java.util.*;
import java.util.logging.Logger;

import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.merge.ResolveMerger;
import org.eclipse.jgit.notes.Note;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevObject;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.RevWalkUtils;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import org.omg.CosNaming.NamingContextExtPackage.StringNameHelper;

/**
 * The source control server interface implements the git commands used to
 * perform operations on the source control system.
 *
 * @author Shawn Stafford
 */
public class CMnGitServer implements IMnServer {

    /** Set the diff comparision setting */
    private static final RawTextComparator diffComparator = RawTextComparator.WS_IGNORE_ALL;

    /** Git notes prefix */
    private static final String NOTE_REF = "refs/notes";

    /** Namespace for notes which contain the software defect association */
    private static final String DEFECT_NOTE_REF = "refs/notes/defect";

    /** Delimiter used to separate fields of the merge note */
    private static final String MERGE_NOTE_DELIMITER = ":";

    /** Create a logger for capturing service patch tool output */
    private Logger logger = Logger.getLogger(CMnGitServer.class.getName());

    /** Create a class for displaying git output */
    private ProgressMonitor progressMonitor = new TextProgressMonitor();

    /** Git user account */
    private CredentialsProvider auth = null;

    /** URI of the remote git instance */
    private String origin = null;

    /** Instance of the git repository */
    private Git repository = null;

    /** Revision graph walker */
    private RevWalk revWalk = null;

    /** Determine whether a native git client should be used instead of the jgit API **/
    private boolean useNative = false;

    /** Determine whether to prompt the user for input */
    private boolean interactive = false;


    /** Private inner class which compares the check-ins by date */
    public static final Comparator<CMnCheckIn> DATE_ORDER =
        new Comparator<CMnCheckIn>() {
            public int compare(CMnCheckIn c1, CMnCheckIn c2) {
                if (c1.getCurrentState() == c2.getCurrentState()) {
                    // Sort the dates in ascending order
                    if ((c1.getDate() != null) && (c2.getDate() != null)) {
                        return c1.getDate().compareTo(c2.getDate());
                    } else {
                        return 0;
                    }
                } else if (c1.getCurrentState() == CMnCheckIn.State.SUBMITTED) {
                    return -1;
                } else if (c2.getCurrentState() == CMnCheckIn.State.SUBMITTED) {
                    return 1;
                } else if (c1.getCurrentState() == CMnCheckIn.State.PENDING) {
                    return -1;
                } else if (c2.getCurrentState() == CMnCheckIn.State.PENDING) {
                    return 1;
                } else {
                    return 0;
                }
            }
        };


    /**
     * Construct the git server.
     *
     * @param   username   Git account username
     * @param   password   Git account password
     * @param   uri        Git server connection information
     */
    public CMnGitServer(String username, String password, String uri) {
        boolean hasUsername = false;
        if ((username != null) && (username.trim().length() > 0)) {
            hasUsername = true;
        }

        boolean hasPassword = false;
        if ((password != null) && (password.trim().length() > 0)) {
            hasPassword = true;
        }

        if (hasUsername && hasPassword) {
            auth = new UsernamePasswordCredentialsProvider(username, password);
        }
        origin = uri;
    }

    /**
     * Determine whether the server should prompt the user for input.
     *
     * @param   enabled     Prompt the user for input if necessary
     */
    public void setInteractiveMode(boolean enabled) {
        interactive = enabled;
    }


    /**
     * Determine whether the java API or a native command-line client should
     * be used to access the source control system.
     *
     * @param   enabled     Use the native command-line client if true, use the Java API if false
     */
    public void setNativeMode(boolean enabled) {
        useNative = enabled;
    }


    /**
     * Set up access to the source code repository.
     * On a distributed system such as git, this might include cloning the repository
     * and setting local configuration values such as user and e-mail.
     * On a centralized system such as perforce, this might include setting up a 
     * clientspec.
     *
     * @param   root       Root directory for the source control repository
     */
    public void init(File root) throws Exception {
        Date startDate = new Date();
        Date endDate = null;

        // Clone the repository if it does not already exist
        if (!root.exists()) {
            // Make sure that the parent directory exists before cloning
            File parent = root.getParentFile();
            parent.mkdirs();

            // Clone the repsitory
            clone(root);

        } else {
            InitCommand init = new InitCommand();
            init.setDirectory(root);
            logger.info("Connecting to local repository: " + root.getAbsolutePath());
            repository = init.call();

        }

        // Configure the repository with settings required for patch creation 
        StoredConfig config = repository.getRepository().getConfig();
        config.setString("remote", "origin", "url", origin);
        // Set the diff3 style to ensure that the original change is shown
        //config.setString("merge", "conflictstyle", "diff3");
        config.save();

        // Initialize the git API for retrieving object information
        revWalk = new RevWalk(repository.getRepository());

        // Display the elapsed time for this operation
        CMnCmdLineTool.showElapsedTime(logger, startDate, new Date(), "Repository initialization: ");
    }


    /**
     * Return a list of all bugs and their corresponding check-in IDs.
     *
     * @param   branch     Branch name
     * @param   start      Starting check-in from which the branch was created
     * @return  List of bugs and their corresponding check-in IDs
     */
    public HashMap<String, List<String>> getBugMap(String branch, String start) throws Exception {
        HashMap<String, List<String>> bugs = new HashMap<String, List<String>>();
        Date startDate = new Date();

        // Determine the start and end point of the current branch for walking the object tree
        RevCommit head = revWalk.lookupCommit(repository.getRepository().resolve(branch));
        RevCommit branchPoint = revWalk.lookupCommit(repository.getRepository().resolve(start));
        revWalk.markStart(head);

        logger.info("Creating RevWalk from " + branchPoint.getName() + " to " + head.getName());

        // Iterate through each commit, working backward from the head of the branch
        RevCommit currentCommit = head;
        while (!currentCommit.equals(branchPoint)) {
            // Get the note associated with the current commit
            currentCommit = revWalk.next();
            ShowNoteCommand noteCmd = repository.notesShow();
            noteCmd.setNotesRef(DEFECT_NOTE_REF);
            noteCmd.setObjectId(currentCommit);
            Note note = noteCmd.call();

            // Get the list of bugs associated with the current note
            List<String> bugIds = null;
            if (note != null) {
                bugIds = getBugIds(note);
            }

            // Associate the list of bugs with the commit
            if ((bugIds != null) && (bugIds.size() > 0)) {
                Iterator bugIter = bugIds.iterator();
                while (bugIter.hasNext()) {
                    List<String> commits = null;
                    String bugId = (String) bugIter.next();
                    if (bugs.containsKey(bugId)) {
                        // Add the commit to the list
                        commits = bugs.get(bugId);
                        commits.add(currentCommit.getName());
                    } else {
                        commits = new ArrayList<String>();
                        commits.add(currentCommit.getName());
                        bugs.put(bugId, commits);
                    }
                }
            }
        }

        // Display the elapsed time for this operation
        CMnCmdLineTool.showElapsedTime(logger, startDate, new Date(), "Get bug map: ");

        return bugs;
    }

    /**
     * Return a list of all check-ins and their corresponding bug IDs.
     *
     * @param   branch     Branch name
     * @param   start      Starting check-in from which the branch was created
     * @return  List of check-in IDs and their corresponding bug IDs
     */
    public HashMap<String, List<String>> getCheckInMap(String branch, String start) throws Exception {
        HashMap<String, List<String>> checkins = new HashMap<String, List<String>>();
        Date startDate = new Date();

        // Determine the start and end point of the current branch for walking the object tree
        RevCommit head = revWalk.lookupCommit(repository.getRepository().resolve(branch));
        RevCommit branchPoint = revWalk.lookupCommit(repository.getRepository().resolve(start));
        revWalk.markStart(head);

        logger.info("Creating RevWalk from " + branchPoint.getName() + " to " + head.getName());

        // Iterate through each commit, working backward from the head of the branch
        RevCommit currentCommit = head;
        while (!currentCommit.equals(branchPoint)) {
            // Get the note associated with the current commit
            currentCommit = revWalk.next();
            ShowNoteCommand noteCmd = repository.notesShow();
            noteCmd.setNotesRef(DEFECT_NOTE_REF);
            noteCmd.setObjectId(currentCommit);
            Note note = noteCmd.call();

            // Get the list of bugs associated with the current note
            List<String> bugIds = null;
            if (note != null) {
                bugIds = getBugIds(note);
            }

            // Associate the list of bugs with the commit
            if ((bugIds != null) && (bugIds.size() > 0)) {
                checkins.put(currentCommit.getName(), bugIds);
            }
        }

        // Display the elapsed time for this operation
        CMnCmdLineTool.showElapsedTime(logger, startDate, new Date(), "Get check-in map: ");

        return checkins;
    }

    /**
     * Parse the git note containing SDR numbers associated with the note.
     *
     * @param  note   Git note
     * @return List of bug IDs, or null if none found
     */
    private List<String> getBugIds(Note note) {
        List<String> bugs = new ArrayList<String>();

        ObjectId noteObj = note.getData();
        String noteId = noteObj.getName();
        String noteText = noteObj.toString();
        if (noteText != null) {
            // Parse the delimited list of bug IDs from the note data
            noteText = noteText.trim();
            StringTokenizer st = new StringTokenizer(noteText);
            while (st.hasMoreTokens()) {
                bugs.add(st.nextToken());
            }
        }

        return bugs;
    }

    /**
     * Query the source control system for any information about the bug, such as
     * the list of check-ins or the branch.
     * On a system such as perforce, this would typically query the job information.
     * On a system like git, it would most likely query the notes.
     * This operation is expensive in git because it queries the entire list of
     * git bug notes looking for one that contains the specified bug ID.
     *
     * @param  id     Bug ID
     * @return Information about the specified bug 
     */
    public CMnBug getBug(String id) throws Exception {
        CMnBug bug = null;

        ListNotesCommand cmdNotes = repository.notesList();
        cmdNotes.setNotesRef(DEFECT_NOTE_REF);
        List<Note> notes = cmdNotes.call();
        if ((notes != null) && (notes.size() > 0)) {
            // Iterate through each note to determine if it matches the bug ID
            Iterator noteIter = notes.iterator();
            while (noteIter.hasNext()) {
                Note currentNote = (Note) noteIter.next();
                ObjectId noteObj = currentNote.getData();
                String noteId = noteObj.getName();

                // Iterate through each Bug ID associated with this note
                List<String> bugIds = getBugIds(currentNote);
                if (bugIds != null) {
                    Iterator bugIter = bugIds.iterator();
                    while (bugIter.hasNext()) {
                        String currentId = (String) bugIter.next();
                        // Compare the current bug ID from the note to the target ID
                        if (id.equalsIgnoreCase(currentId)) {
                            boolean addCheckin = true;

                            // Construct the bug object if it does not already exist
                            if (bug == null) {
                                bug = new CMnBug();
                                bug.setId(Integer.valueOf(id));
                            } else {
                                if (!bug.hasCheckIn(noteId)) {
                                    addCheckin = false;
                                }
                            }

                            // Add the git commit information to the bug
                            if (addCheckin) {
                                CMnCheckIn checkin = new CMnCheckIn();
                                checkin.setId(noteId);
                                bug.addCheckIn(checkin);
                            }
                        }
                    }
                }
            }
        }

        // Fetch bug information
        logger.severe("TODO: Implement CMnGitServer.getBug() using git notes");

        return bug;
    }

    /**
     * Return the most recent check-in on the branch
     *
     * @param  branch   Branch name
     * @return Information about the check-in
     */
    public CMnCheckIn getHead(String branch) throws Exception {
        CMnCheckIn checkin = null;
        Date startDate = new Date();

        RevCommit head = revWalk.lookupCommit(repository.getRepository().resolve(branch));
        checkin = convert(head);

        // Display the elapsed time for this operation
        CMnCmdLineTool.showElapsedTime(logger, startDate, new Date(), "Fetch head data: ");

        return checkin;
    }

    /**
     * Query the source control system for any information about the check-in.
     *
     * @param  id     Check-in ID
     * @return Information about the check-in
     */
    public CMnCheckIn getCheckIn(String id) throws Exception {
        CMnCheckIn checkin = null;
        Date startDate = new Date();

        RevCommit revCommit = getCommit(id);
        checkin = convert(revCommit);

        // Display the elapsed time for this operation
        CMnCmdLineTool.showElapsedTime(logger, startDate, new Date(), "Fetch check-in data: ");

        return checkin;
    }

    /**
     * Return a list of all check-ins on the specified branch.
     *
     *
     * @param   branch     Branch name
     * @param   start      Starting check-in on the branch 
     * @param   end        Ending check-in on the branch
     * @return List of check-ins
     */
    public List<CMnCheckIn> getCheckInList(String branch, String start, String end) throws Exception {
        Date startDate = new Date();
        ArrayList<CMnCheckIn> list = new ArrayList<CMnCheckIn>();

        // Determine the start and end point of the current branch for walking the object tree
        RevCommit firstCommit = revWalk.lookupCommit(repository.getRepository().resolve(start));
        RevCommit lastCommit  = revWalk.lookupCommit(repository.getRepository().resolve(end));
        revWalk.reset();

        // Make sure the end and the starting commit can be found
        if ((firstCommit != null) && (lastCommit != null)) {
            // Reset the RevWalk instance before starting the walk
            revWalk.reset();
            revWalk.markStart(lastCommit);

            // Iterate through each commit, working backward from the last commit on the branch 
            boolean keepWalking = true;
            RevCommit currentCommit = lastCommit;
            RevCommit previousCommit = null;
            while (keepWalking) {
                // Stop walking when you reach the first commit
                if (currentCommit.getName().equals(firstCommit.getName())) { 
                    keepWalking = false;
                } else {
                    // Move to the next commit on the branch
                    RevCommit rev = revWalk.next();
                    if (rev != null) {
                        previousCommit = currentCommit;
                        currentCommit = rev;
                        if ((previousCommit != null) && (currentCommit != null)) {
                            CMnCheckIn checkin = convert(currentCommit);
                            list.add(checkin);
                        } else {
                            keepWalking = false;
                        }
                    } else {
                        keepWalking = false;
                    }

                }

            }

        }

        // Display the elapsed time for this operation
        CMnCmdLineTool.showElapsedTime(logger, startDate, new Date(), "Get check-in list: ");

        return list;
    }

    /**
     * Sort the list of commits in chronological order.
     *
     * @param  list   List of commits
     */
    public void sort(List<CMnCheckIn> list) {
        Collections.sort(list, DATE_ORDER);
    }

    /**
     * Clone a remote repository. 
     *
     * @param   root       Root directory for the source control repository
     */
    private void clone(File root) throws Exception {
        if (useNative) {
            cloneNative(root);
        } else {
            cloneApi(root);
        }
    }

    /**
     * Clone a remote repository. 
     *
     * @param   root       Root directory for the source control repository
     */
    private void cloneNative(File root) throws Exception {
        Runtime runtime = Runtime.getRuntime();
        if (root != null) {
            // Determine the parent directory to operate in
            File parentDir = null;
            if (root.getParent() != null) {
                parentDir = new File(root.getParent());
            }

            logger.info("Executing native command: git clone " + origin + " " + root.getAbsolutePath());
            Process proc = runtime.exec("git clone " + origin + " " + root.getAbsolutePath(), null, parentDir);
            int procResult = CMnCmdLineTool.wait(proc, true);

            InitCommand init = new InitCommand();
            init.setDirectory(root);
            logger.info("Connecting to local repository: " + root.getAbsolutePath());
            repository = init.call();

        } else {
            logger.severe("Unable to clone remote repository.  No destination directory specified.");
            System.exit(1);
        }

    }


    /**
     * Clone a remote repository. 
     *
     * @param   root       Root directory for the source control repository
     */
    private void cloneApi(File root) throws Exception {
        if (root != null) {
            Date startDate = new Date();

            CloneCommand clone = new CloneCommand();
            clone.setCredentialsProvider(auth);
            clone.setDirectory(root);
            clone.setTimeout(SERVER_TIMEOUT);
            clone.setURI(origin);
            clone.setProgressMonitor(progressMonitor);

            clone.setNoCheckout(true);
            clone.setBare(false);
            clone.setCloneSubmodules(false);
            clone.setCloneAllBranches(true);

            // equivalent git command:
            // git clone [-b master] uri dir
            logger.info("Cloning the repository: origin=" + origin + ", dest=" + root.getAbsolutePath());
            repository = clone.call();

            // Display the elapsed time for this operation
            CMnCmdLineTool.showElapsedTime(logger, startDate, new Date(), "API clone complete: ");

        } else {
            logger.severe("Unable to clone repository.  No destination directory specified.");
            System.exit(1);
        }

    }


    /**
     * Check out a git branch and make it the active branch.
     *
     * On git this would be a a pull.
     *
     * @param   branch     Source code branch
     */
    private void checkout(String branch) throws Exception {
        if (useNative) {
            checkoutNative(branch);
        } else {
            checkoutApi(branch);
        }
    }

    /**
     * Check out a git branch and make it the active branch.
     *
     * On git this would be a a pull.
     *
     * @param   branch     Source code branch
     */
    private void checkoutNative(String branch) throws Exception {
        if ((branch != null) && (repository != null)) {

            // Determine the git working directory
            File repodir = repository.getRepository().getWorkTree();

            Runtime runtime = Runtime.getRuntime();

            // Attempt to compensate for the fact that migrated branches exist as remote objects
            if (!branchExists(branch)) {
                // Locate the remote branch
                Ref remoteBranch = getRemoteBranch(branch);
                if (remoteBranch != null) {
                    Process proc = runtime.exec("git branch --track " + branch + " origin/" + branch, null, repodir);
                    int procResult = CMnCmdLineTool.wait(proc, true);
                } else {
                    logger.severe("Unable to locate branch for checkout: " + branch);
                    System.exit(1);
                }
            }

            // Checkout the local branch
            if (branchExists(branch)) {
                logger.info("Performing checkout of branch using native executables: " + branch);

                Process proc = runtime.exec("git checkout " + branch, null, repodir);
                int procResult = CMnCmdLineTool.wait(proc, true); 

            } else {
                logger.severe("Local branch does not exist: " + branch);
                System.exit(1);
            }


        } else if (branch != null) {
            logger.severe("Unable to checkout branch.  Git repository has not been initialized.");
        } else {
            logger.severe("No branch value specified.");
        }

    }


    /**
     * Check out a git branch and make it the active branch.
     *
     * On git this would be a a pull.
     *
     * @param   branch     Source code branch
     */
    private void checkoutApi(String branch) throws Exception {
        if ((branch != null) && (repository != null)) {

            // Attempt to compensate for the fact that migrated branches exist as remote objects
            if (!branchExists(branch)) {
                // Locate the remote branch
                Ref remoteBranch = getRemoteBranch(branch);
                if (remoteBranch != null) {
                    Date startDate = new Date();

                    // Track the remote branch as a local branch
                    String remoteBranchName = remoteBranch.getName();
/*
                    String remotePrefix = "refs/remotes/";
                    if (remoteBranchName.startsWith(remotePrefix)) {
                        remoteBranchName = remoteBranchName.substring(remotePrefix.length());
                    }
*/
                    logger.severe("TODO fix initial checkout of remote branches.");
                    logger.severe("Unable to continue with git initialization.");
                    logger.severe("Please perform the following process manually and then proceed:");
                    logger.severe("    git branch --track " + branch + " origin/" + branch);
                    logger.severe("    git checkout " + branch);
                    System.exit(1);

                    logger.info("Tracking remote branch: " + remoteBranchName);
                    CreateBranchCommand branchCmd = repository.branchCreate();
                    branchCmd.setName(branch);
                    branchCmd.setStartPoint(remoteBranchName);
                    branchCmd.setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK);
                    Ref branchRef = branchCmd.call();

                    // Display the elapsed time for this operation
                    CMnCmdLineTool.showElapsedTime(logger, startDate, new Date(), "Created local tracking branch: ");
                } else {
                    logger.severe("Unable to locate branch for checkout: " + branch);
                    System.exit(1);
                }
            }

            // Checkout the local branch
            if (branchExists(branch)) {
                Date startDate = new Date();
                logger.info("Performing checkout of branch using Java API: " + branch);
                logger.severe("TODO fix checkout of existing branches using JGit API.");
                CheckoutCommand checkoutCmd = repository.checkout();
                checkoutCmd.setName(branch);
                checkoutCmd.setCreateBranch(false);
                checkoutCmd.setForce(false);
                Ref branchRef = checkoutCmd.call();

                // Display the elapsed time for this operation
                CMnCmdLineTool.showElapsedTime(logger, startDate, new Date(), "Checkout: ");

            } else {
                logger.severe("Local branch does not exist: " + branch);
                System.exit(1);
            }


        } else if (branch != null) {
            logger.severe("Unable to checkout branch.  Git repository has not been initialized.");
        } else {
            logger.severe("No branch value specified.");
        }

    }

    /**
     * Get the latest copy of the GA source code.
     * On Perforce this would be a sync.
     */
    public void getSource(String branch) throws Exception {
        if (useNative) {
            checkoutNative(branch);
            getSourceNative(branch);
        } else {
            checkoutApi(branch);
            getSourceApi(branch);
        }
    }


    /**
     * Get the latest copy of the GA source code using the native client.
     */
    public void getSourceNative(String branch) throws Exception {
        // Check out the initial branch
        if ((branch != null) && (repository != null)) {
            // Determine the git working directory
            File repodir = repository.getRepository().getWorkTree();

            Runtime runtime = Runtime.getRuntime();

            // Checkout the local branch
            if (branchExists(branch)) {
                logger.info("Fetching latest changes using native executables: " + branch);

                Process proc = runtime.exec("git pull --rebase", null, repodir);
                int procResult = CMnCmdLineTool.wait(proc, true);

            } else {
                logger.severe("Local branch does not exist: " + branch);
                System.exit(1);
            }


        } else if (branch != null) {
            logger.severe("Unable to get source.  Git repository has not been initialized.");
        } else {
            logger.severe("No branch value specified.");
        }
    }

    /**
     * Get the latest copy of the GA source code using the jgit API.
     */
    public void getSourceApi(String branch) throws Exception {
        // Check out the initial branch
        if ((branch != null) && (repository != null)) {
            Date startDate = new Date();

            // Update the repository with the latest changes
            logger.info("Pulling changes from remote repository: " + origin);
            PullCommand pullCmd = repository.pull();
            pullCmd.setProgressMonitor(progressMonitor);
            pullCmd.setCredentialsProvider(auth);
            pullCmd.call();

            // Display the elapsed time for this operation
            CMnCmdLineTool.showElapsedTime(logger, startDate, new Date(), "Check-out and pull: ");

        } else if (branch != null) {
            logger.severe("Unable to get source.  Git repository has not been initialized.");
        } else {
            logger.severe("No branch value specified.");
        }
    }

    /**
     * Create a new branch from the specified source.
     *
     * @param   src        Source branch
     * @param   dest       Branch to be created
     * @param   id         Check-in ID on the source branch to use as the branch point
     */
    public void createBranch(String src, String dest, String id) throws Exception {
        Date startDate = new Date();

        // Switch to the source branch
        getSource(src);

        // Get the object information for that branch point
        RevCommit revCommit = getCommit(id);

        // Create the destination branch
        CreateBranchCommand brCmd = repository.branchCreate();
        brCmd.setName(dest);
        brCmd.setStartPoint(revCommit);
        brCmd.call();

        // Display the elapsed time for this operation
        CMnCmdLineTool.showElapsedTime(logger, startDate, new Date(), "Created branch: ");

    }

    /**
     * Merge the check-in into the destination branch using an API call.
     *
     * @param   src        Source branch
     * @param   dest       Destination branch
     * @param   id         Check-in ID of the change to be merged
     * @return  Result of the merge operation
     */
    public MergeResult mergeApi(String src, String dest, String id) throws Exception {
        MergeResult result = null;

        Date startCherryPick = new Date();
        CherryPickCommand cpCmd = repository.cherryPick();
        logger.info("Attempting to locate commit object " + id);
        cpCmd.include(getCommit(id));
        logger.info("Performing cherry-pick of " + id);
        CherryPickResult cmdResult = cpCmd.call();
        CMnCmdLineTool.showElapsedTime(logger, startCherryPick, new Date(), "Cherry-pick: ");

        // Check the results of the merge operation
        CherryPickResult.CherryPickStatus cpStatus = cmdResult.getStatus();
        switch (cpStatus) {
            case CONFLICTING:
                result = MergeResult.CONFLICT;
                break;
            case OK:
                result = MergeResult.SUCCESS;
                break;
            case FAILED:
                result = MergeResult.FAILURE;
                Date startFailureResults = new Date();
                Map<String, ResolveMerger.MergeFailureReason> failures = cmdResult.getFailingPaths();
                Set<String> keys = failures.keySet();
                Iterator keyIter = keys.iterator();
                while (keyIter.hasNext()) {
                    String key = (String) keyIter.next();
                    ResolveMerger.MergeFailureReason reason = (ResolveMerger.MergeFailureReason) failures.get(key);
                    logger.severe("Merge failure: " + key + ": " + reason);
                }
                CMnCmdLineTool.showElapsedTime(logger, startFailureResults, new Date(), "Fetch failing paths: ");
                break;
        }

        return result;
    }


    /**
     * Merge the check-in into the destination branch using a native git call.
     *
     * @param   src        Source branch
     * @param   dest       Destination branch
     * @param   id         Check-in ID of the change to be merged
     * @return  Result of the merge operation
     */
    public MergeResult mergeNative(String src, String dest, String id) throws Exception {
        Date startDate = new Date();

        MergeResult result = null;

        // Determine the git working directory
        File repodir = repository.getRepository().getWorkTree();

        // Ensure that the current branch is correct before proceeding
        if (!dest.equals(repository.getRepository().getBranch())) {
            checkoutNative(dest);
        }

        // Select a merge strategy for the cherry-pick operation
        // ignore-space-change, ignore-all-space, ignore-space-at-eol

        Runtime runtime = Runtime.getRuntime();
        String strategy = "--strategy=ignore-all-space";
        //Process proc = runtime.exec("git cherry-pick " + strategy + " " + id, null, repodir);
        Process proc = runtime.exec("git cherry-pick " + id, null, repodir);
        int procResult = CMnCmdLineTool.wait(proc, true); 
        if (procResult == 0) {
            result = MergeResult.SUCCESS;
        } else {
            result = MergeResult.CONFLICT;
        }

        // Display the elapsed time for this operation
        CMnCmdLineTool.showElapsedTime(logger, startDate, new Date(), "Native merge complete: ");

        return result;
    }

    /**
     * Fetch the git notes for tracking the merge notes.
     *
     * @param  namespace  Git note namespace to fetch
     */
    public void getMergeNotes(String namespace) throws Exception {
        logger.info("Fetching notes: " + namespace);
        RefSpec notesRef = new RefSpec(namespace + ":" + namespace);
        FetchCommand fetch = repository.fetch();
        fetch.setRefSpecs(notesRef);
        fetch.setProgressMonitor(progressMonitor);
        fetch.call();
        logger.info("Finished fetching notes: " + namespace);
    } 

    /**
     * Generate a text string representing the merge attributes.
     *
     * @param   src        Source branch
     * @param   id         Original object ID of the commit that was merged
     * @return  Merge information represented as a string
     */
    private String formatMergeNote(String src, String id) {
        return src + MERGE_NOTE_DELIMITER + id;
    }

    /**
     * Create a new git note and associate it with the specified commit.
     *
     * @param  namespace Git notes namespace
     * @param  commit    Commit object to attach the note to
     * @param  msg       Note text
     */
    private void addMergeNote(String namespace, RevObject commit, String msg) throws GitAPIException {
        AddNoteCommand addCmd = repository.notesAdd();
        addCmd.setObjectId(commit);
        addCmd.setMessage(msg);
        addCmd.setNotesRef(namespace);
        Note note = addCmd.call();
        if (note != null) {
            logger.info("Merge note has been created: " + note.toString());
        } else {
            logger.severe("Failed to add note to commit: " + commit.getName());
        }

    }



    /**
     * Merge the check-in into the destination branch.
     *
     * @param   src        Source branch
     * @param   dest       Destination branch
     * @param   id         Check-in ID of the change to be merged
     * @return  Result of the merge operation
     */
    public MergeResult merge(String src, String dest, String id) throws Exception {
        Date startDate = new Date();

        MergeResult result = null;

        String notesref = getMergeNotesNamespace(dest);

        // Ensure that the current branch is correct before proceeding
        if (!dest.equals(repository.getRepository().getBranch())) {
            checkout(dest);
        }

        // Keep track of the pre-cherry-pick HEAD
        ObjectId oldHead = repository.getRepository().resolve("HEAD");

        // Perform the cherry-pick to merge the change
        Date startCherryPick = new Date();
        if (useNative) {
            result = mergeNative(src, dest, id);
        } else {
            result = mergeApi(src, dest, id);
        }
        CMnCmdLineTool.showElapsedTime(logger, startCherryPick, new Date(), "Cherry-pick: ");


        // Check the results of the merge operation
        String noteMsg = formatMergeNote(src, id);
        switch (result) {
            case CONFLICT:
                logger.severe("Merge failed due to conflict.  Manually resolve and commit.");
                if (interactive) {
                    boolean saidYes = CMnCmdLineTool.doContinue("Have the conflicts been resolved and committed?");
                    if (saidYes) {
                        try {
                            ObjectId newHead = repository.getRepository().resolve("HEAD");
                            if (oldHead.equals(newHead)) {
                                logger.severe("Head commit has not changed.  Cannot apply notes.");
                            } else { 
                                logger.info("Adding note to HEAD: " + noteMsg);
                                addMergeNote(notesref, revWalk.lookupCommit(newHead), noteMsg);
                                result = MergeResult.SUCCESS;
                            }
                        } catch (GitAPIException gex) {
                            gex.printStackTrace();
                        }
                    } else {
                        logger.severe("User has aborted the merge of commit: " + id);
                    }
                }
                logger.severe("After the change has been committed, run the following command: ");
                logger.severe("   git notes --ref " + notesref + " add -m " + noteMsg);
                break;
            case SUCCESS:
                try {
                    logger.info("Adding note: " + noteMsg);
                    ObjectId newHead = repository.getRepository().resolve("HEAD");
                    addMergeNote(notesref, revWalk.lookupCommit(newHead), noteMsg);
                } catch (GitAPIException gex) {
                    gex.printStackTrace();
                }
                break;
            case FAILURE:
                logger.info("Failed to merge commit: " + id);
                break;

        }

        // Display the elapsed time for this operation
        CMnCmdLineTool.showElapsedTime(logger, startDate, new Date(), "Merge complete: ");

        return result;
    }

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
    public boolean merge(String branch, String alt, List<CMnCheckIn> list, List<CMnCheckIn> existingList) throws Exception {
        Date startDate = new Date();
        boolean diverged = false;

        // Locate the last check-in where the existing branch is valid
        RevCommit lastMerge = null;
        int idx = 0;
        Iterator listIter = list.iterator();
        Iterator existingIter = existingList.iterator();
        while (listIter.hasNext() && existingIter.hasNext()) {
            // Get the two current check-ins
            CMnCheckIn desired = (CMnCheckIn) listIter.next();
            CMnCheckIn existing = (CMnCheckIn) existingIter.next();

            RevCommit desiredCommit = getCommit(desired.getId());
            RevCommit existingCommit = getCommit(existing.getId());

            int depth = RevWalkUtils.count(revWalk, desiredCommit, existingCommit);
            logger.info("Merge depth of " + desired.getId() + ": " + depth);
        }

        // Display the elapsed time for this operation
        CMnCmdLineTool.showElapsedTime(logger, startDate, new Date(), "Merge completed: ");

        return diverged;
    }


    /**
     * Strip out any parts of the commit diff which do not 
     * constitute the file changes themselves.
     *
     * @param  diff   Commit diff
     * @return Diff content
     */
    private String getDiffContent(String diff) {
        String content = diff;
        if (content != null) {
            content = content.replaceAll("(?m)^index.*$", "");
        }
        if (content != null) {
            content = content.replaceAll("(?m)^@@.*$", "");
        }
        if (content != null) {
            content = content.replaceAll("(?m)^diff.*$", "");
        }
        return content;
    }


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
    public float getEquality(CMnCheckIn c1, CMnCheckIn c2) throws Exception {
        // Make sure both commits have files 
        if ((c1.getFiles() == null) || (c2.getFiles() == null)) {
            return RELATIVE_CERTAINTY_NONE;
        }

        // If the number of differences don't match, the commits don't match
        if ((c1.getFiles().size() != c2.getFiles().size())) {
            return RELATIVE_CERTAINTY_NONE;
        } 

        // Use a hashtable to reference the list of differences
        Hashtable hash1 = new Hashtable();
        Hashtable hash2 = new Hashtable();

        // Get the list of diffs for the first commit
        for (CMnFile diff : c1.getFiles()) {
            String key = diff.getOp().name() + " " + diff.getFilename();
            hash1.put(key, diff); 
        }

        // Get the list of diffs for the second commit
        for (CMnFile diff : c2.getFiles()) {
            String key = diff.getOp().name() + " " + diff.getFilename();
            hash2.put(key, diff);
        }

        // Confirm that the diff entries are equal 
        StringBuffer diffBuff1 = new StringBuffer();
        StringBuffer diffBuff2 = new StringBuffer();
        int totalFileCount = 0;
        int sameFileCount = 0; 
        Enumeration keys1 = hash1.keys();
        while (keys1.hasMoreElements()) {
            String key = (String) keys1.nextElement();
            totalFileCount++;
            if (hash2.containsKey(key)) {
                sameFileCount++;
                CMnFile diff1 = (CMnFile) hash1.get(key);
                CMnFile diff2 = (CMnFile) hash2.get(key);
                if (diff1.getDiff() != null) {
                    diffBuff1.append(diff1.getDiff()); 
                }
                if (diff2.getDiff() != null) {
                    diffBuff2.append(diff2.getDiff());
                }
            }
        }
//System.out.println("SSDEBUG Comparing " + c1.getFormattedId() + " to " + c2.getFormattedId());
        float fileMatchWeight = (float) sameFileCount / (float) totalFileCount;
//System.out.println("SSDEBUG File match weight: " + sameFileCount + " / " + totalFileCount + " = " + fileMatchWeight);

        // Compare the actual diff content
        float equality = RELATIVE_CERTAINTY_NONE; 
        String diffstr1 = getDiffContent(diffBuff1.toString());
        String diffstr2 = getDiffContent(diffBuff2.toString());
        if ((diffstr1.length() > 0) && (diffstr2.length() > 0) && diffstr1.equals(diffstr2)) {
            equality = fileMatchWeight * RELATIVE_CERTAINTY_FULL;
//System.out.println("SSDEBUG: Equality value (same diff): " + fileMatchWeight + " * " + RELATIVE_CERTAINTY_FULL + " = " + equality);
        } else if ((diffstr1.length() > 0) && (diffstr2.length() > 0)) {
            float diffEquality = StringUtility.getEquality(diffstr1, diffstr2);
            equality = fileMatchWeight * diffEquality;
//System.out.println("======================= DIFF for " + c1.getFormattedId() + " =======================");
//System.out.println(diffstr1);
//System.out.println("======================= DIFF for " + c2.getFormattedId() + " =======================");
//System.out.println(diffstr2);
//System.out.println("=================================================================");
//System.out.println("SSDEBUG: Equality value: " + fileMatchWeight + " * " + diffEquality + " = " + equality);
//        } else {
//System.out.println("SSDEBUG: length1 = " + diffstr1.length() + ", length2 = " + diffstr2.length());
        }

        return equality;
    }

    /**
     * Determine if the commit has been integrated to the service patch branch.
     * Since it may not be possible to absolutely identify a matching commit,
     * this method attempts to identify all possible matching commits and 
     * return a list of the commits with their corresponding score.
     *
     * @param   src        Source branch
     * @param   dest       Destination branch
     * @param   id         Check-in ID of the change to be merged
     * @return A list of possible matches and their relative score
     */
    public HashMap<CMnCheckIn, Float> isMerged(String src, String dest, String id) throws Exception {
        HashMap<CMnCheckIn, Float> matches = new HashMap<CMnCheckIn, Float>();

        Date startDate = new Date();
        String notesref = getMergeNotesNamespace(dest); 

        // Make sure we reset the RevWalk before searching
        revWalk.reset();

        // Determine the base of the service patch branch
        RevCommit base = getCommit(id);
        CMnCheckIn checkin = convert(base);

        // Determine the tip of the service patch branch
        ObjectId destHead = repository.getRepository().resolve(dest);
        RevCommit tip = revWalk.lookupCommit(destHead);

        // Determine if the check-in was merged into the branch
        boolean mergeResult = revWalk.isMergedInto(base, tip);
        if (mergeResult == true) {
            // Return immediately if the commit is identical in both branches
            matches.put(checkin, new Float(RELATIVE_CERTAINTY_FULL));
            return matches;
        }

        // Double check the entire branch to ensure that the commit does not occur
        // before the service patch branch was created
        revWalk.reset();
        revWalk.setRetainBody(false);
        revWalk.markStart(tip);
        RevCommit thisCommit = revWalk.next();
        while (thisCommit != null) {
            if (id.equalsIgnoreCase(thisCommit.getName())) {
                // Return immediately if the commit SHA is identical in both branches
                matches.put(checkin, new Float(RELATIVE_CERTAINTY_FULL));
                return matches;
            }
            thisCommit = revWalk.next();
        }


        // Double check the result using a combination of 
        // the Gerrit Change ID and git notes
        String targetMsg = formatMergeNote(src, id);
        String commitSubject = null;
        String commitMsg = null;
        String changeId = null;
        try {
            commitSubject = base.getShortMessage();
            commitMsg = base.getFullMessage();
            changeId = CMnGerritCheckIn.parseChangeId(commitMsg);
        } catch (Exception idex) {
            logger.severe("Failed to parse Gerrit Change ID:" + idex.toString());
        }

        // Walk backward from the HEAD to the base of the branch
        revWalk.reset();
        revWalk.setRetainBody(false);
        revWalk.markStart(tip);
        revWalk.markUninteresting(base);
        RevCommit currentCommit = revWalk.next();
        while (currentCommit != null) {
            // Convert the git data object to our own data object
            CMnCheckIn currentCheckin = convert(currentCommit);

            String thisCommitSubject = null;
            String thisCommitMsg = null;
            String thisChangeId = null;
            try {
                thisCommitSubject = currentCommit.getShortMessage();
                thisCommitMsg = currentCommit.getFullMessage();
                thisChangeId = CMnGerritCheckIn.parseChangeId(thisCommitMsg);
            } catch (Exception tidex) {
                logger.severe("Failed to parse Gerrit Change ID: " + tidex.toString());
            }

            // Check the Gerrit Change ID
            if ((changeId != null) && (thisChangeId != null)) {
                if (changeId.trim().equals(thisChangeId.trim())) {
                    // Return immediately if the Gerrit Change ID maches
                    matches.put(currentCheckin, new Float(RELATIVE_CERTAINTY_FULL));
                    return matches;
                }
            }


            // Check the git note 
            ShowNoteCommand noteCmd = repository.notesShow();
            noteCmd.setObjectId(currentCommit);
            noteCmd.setNotesRef(notesref);
            Note note = noteCmd.call();
            if (note != null) {
                String currentMsg = null;
                ObjectLoader loader = repository.getRepository().open(note.getData());
                if (loader.isLarge()) {
                    // use loader.openStream()
                    logger.severe("Terminating program.  Large notes are not supported by this tool.");
                    System.exit(1);
                } else {
                    currentMsg = new String(loader.getCachedBytes());
                    currentMsg = currentMsg.trim();
                    if (targetMsg.contains(currentMsg)) {
                        // Return immediately if a matching git note is found
                        matches.put(currentCheckin, new Float(RELATIVE_CERTAINTY_FULL));
                        return matches;
                    }
                }
            }


            // Determine if the commits affect the same files
            try {
                float commitEqualityValue = getEquality(checkin, currentCheckin);

                // Compare the commit subject
                boolean subjectEquality = false;
                if ((commitSubject != null) && (thisCommitSubject != null)) {
                    if (commitSubject.equals(thisCommitSubject)) {
                        subjectEquality = true;
                    }
                }

                // Compare the commit message
                boolean msgEquality = false;
                if ((commitMsg != null) && (thisCommitMsg != null)) {
                    if (thisCommitMsg.contains(commitMsg.trim())) {
                        msgEquality = true;
                    }
                }

                // Record a potential match based on the commit information 
                // and comparison of the file diff information
                if (subjectEquality && msgEquality && (commitEqualityValue > RELATIVE_CERTAINTY_NONE)) {
                    matches.put(currentCheckin, new Float(commitEqualityValue));
                } else if (commitEqualityValue > RELATIVE_CERTAINTY_HIGH) {
                    // Account for odd cases where the subject or the message don't
                    // quite match, but the diff is still highly similar
                    matches.put(currentCheckin, new Float(commitEqualityValue));
//                } else if (commitEqualityValue > RELATIVE_CERTAINTY_NONE) {
//                    logger.info("SSDEBUG isMerged: subject = " + subjectEquality + ", msg = " + msgEquality + ", commit = " + commitEqualityValue); 
                }
            } catch (Exception ex) {
                logger.severe("Failed to evaluate commit: " + ex.toString());
            }

            currentCommit = revWalk.next();
        }


        // Display the elapsed time for this operation
        CMnCmdLineTool.showElapsedTime(logger, startDate, new Date(), "Check merge status: ");

        return matches;
    }

    /**
     * Return the remote branch reference or null if none exists.
     *
     * @param  branch    name of the branch
     * @return Ref object or null if remote branch does not exist
     */
    private Ref getRemoteBranch(String branch) {
        Ref result = null;

        try {
            Date startDate = new Date();

            // Obtain the list of branches from the repository
            ListBranchCommand lsbCmd = repository.branchList();

            // By default only local branches will be listed
            lsbCmd.setListMode(ListBranchCommand.ListMode.REMOTE);

            // Get the list of branches
            List<Ref> lsr = lsbCmd.call();

            // Determine if any branches match the specified name
            Iterator branchIter = lsr.iterator();
            while (branchIter.hasNext()) {
                Ref branchRef = (Ref) branchIter.next();

                // Strip off the initial part of the repository name
                String refName = branchRef.getName();
                int lastSlashIdx = refName.lastIndexOf("/");
                if ((lastSlashIdx > 0) && (lastSlashIdx < refName.length())) {
                    refName = refName.substring(lastSlashIdx + 1);
                }
                if (refName.equals(branch)) {
                    result = branchRef;
                }
            }

            // Display the elapsed time for this operation
            CMnCmdLineTool.showElapsedTime(logger, startDate, new Date(), "Remote branch query: ");

        } catch (GitAPIException apiex) {
            logger.severe("Failed to get a list of branches.");
        }

        return result;
    }

    /**
     * Determine if the branch already exists.
     *
     * @param branch name of the branch
     * @return TRUE if the branch exists
     */
    public boolean branchExists(String branch) {
        boolean result = false;

        try {
            Date startDate = new Date();

            // Obtain the list of branches from the repository
            ListBranchCommand lsbCmd = repository.branchList();

            // By default only local branches will be listed, which is what we want
            //lsbCmd.setListMode(ListBranchCommand.ListMode.ALL);

            // Get the list of branches
            List<Ref> lsr = lsbCmd.call();

            // Determine if any branches match the specified name
            Iterator branchIter = lsr.iterator();
            while (branchIter.hasNext()) {
                Ref branchRef = (Ref) branchIter.next();

                // Strip off the initial part of the repository name
                String refName = branchRef.getName();
                int lastSlashIdx = refName.lastIndexOf("/");
                if ((lastSlashIdx > 0) && (lastSlashIdx < refName.length())) {
                    refName = refName.substring(lastSlashIdx + 1);
                }
                if (refName.equals(branch)) {
                    result = true;
                }
            }

            // Display the elapsed time for this operation
            CMnCmdLineTool.showElapsedTime(logger, startDate, new Date(), "Branch exists: ");

        } catch (GitAPIException apiex) {
            logger.severe("Failed to get a list of branches.");
        }

        return result;
    }

    /**
     * Return a git commit object.
     *
     * @param   sha   Commit SHA
     * @return  Commit object
     */
    private RevCommit getCommit(String sha) {
        RevCommit commit = null;

        if (repository != null) {
            // Query git for commit information
            ObjectId objId = null;
            try {
                objId = repository.getRepository().resolve(sha);
            } catch (AmbiguousObjectException ambex) {
                logger.severe("Specified commit SHA is not unique: " + sha);
                ambex.printStackTrace();
            } catch (MissingObjectException mex) {
                logger.severe("Unable to locate commit SHA: " + sha);
                mex.printStackTrace();
            } catch (IncorrectObjectTypeException tex) {
                logger.severe("SHA does not refer to a commit object: " + sha);
                tex.printStackTrace();
            } catch (IOException ioex) {
                logger.severe("Failed to read git repository data.");
                ioex.printStackTrace();
            }

            if (objId != null) {
                try {
                    commit = revWalk.parseCommit(objId);
                } catch (MissingObjectException mex) {
                    //logger.severe("Unable to parse commit SHA: " + sha);
                    //mex.printStackTrace();
                } catch (IncorrectObjectTypeException tex) {
                    logger.severe("SHA does not refer to a commit object: " + sha);
                    tex.printStackTrace();
                } catch (IOException ioex) {
                    logger.severe("Failed to read git repository data.");
                    ioex.printStackTrace();
                }

            } else {
                logger.severe("Failed to find git object: " + sha);
            }
        } else {
            logger.severe("Repository is not initialized.  Unable to search for check-in: " + sha);
        }

        return commit;
    }

    /**
     * Parse the branch name to come up with a namespace for the 
     * corresponding git notes.
     *
     * @param  branch   Service patch branch name
     * @return Git notes namespace
     */
    public static String getMergeNotesNamespace(String branch) {
        String namespace = "merge-source";

        if (branch != null) {
            namespace = branch;
        }

        return NOTE_REF + "/" + namespace;
    }


    /**
     * Construct a generic file object from the attributes of a git DiffEntry.
     *
     * @param  diff    DiffEntry data object
     * @return CMnFile data object
     */
    private CMnFile convert(DiffEntry diff) {
        String filename = diff.getNewPath();
        CMnFile.Operation op = null;
        DiffEntry.ChangeType changeType = diff.getChangeType();
        if (changeType == DiffEntry.ChangeType.ADD){
            op = CMnFile.Operation.ADD;
        } else if (changeType == DiffEntry.ChangeType.DELETE){
            op = CMnFile.Operation.DELETE;
        } else if (changeType == DiffEntry.ChangeType.RENAME){
            op = CMnFile.Operation.RENAME;
        } else {
            op = CMnFile.Operation.EDIT;
        }
        CMnFile file = new CMnFile(filename, op);

        // Convert the file diff to a string
        try {
            OutputStream out = new ByteArrayOutputStream();
            DiffFormatter df = new DiffFormatter(out);
            df.setRepository(repository.getRepository());
            df.setDiffComparator(diffComparator);
            df.setDetectRenames(true);
            df.format(diff);
            df.flush();
            file.setDiff(out.toString());
        } catch (IOException ioex) {
        }

        return file;
    }


    /**
     * Construct a generic check-in object from the attributes of a git RevCommit.
     *
     * @param  commit  RevCommit object data
     * @return CMnCheckIn data object
     */
    private CMnCheckIn convert(RevCommit commit) {
        CMnCheckIn checkin = null;

        if (commit != null) {
            checkin = new CMnGitCheckIn();

            // Set the commit SHA
            checkin.setId(commit.getName());

            // Set the commit date
            long commitTime = commit.getCommitTime();
            checkin.setDate(new Date(commitTime * 1000));

            // Parse the commit auth
            // It's possible for the API to throw a null pointer
            // so we need to catch it here to ensure it doesn't fail
            try {
                PersonIdent authorIdent = commit.getCommitterIdent();
            } catch (Exception ex) {
                ex.printStackTrace();
            }


            // Compare the commit against the parent to get a list
            // of modified files
            try {
                RevCommit parent = revWalk.parseCommit(commit.getParent(0).getId());
                List<CMnFile> list = new ArrayList();

                OutputStream out = new ByteArrayOutputStream();
                DiffFormatter df = new DiffFormatter(out);
                df.setRepository(repository.getRepository());
                df.setDiffComparator(diffComparator);
                df.setDetectRenames(true);
                List<DiffEntry> diffs = df.scan(parent.getTree(), commit.getTree());
                for (DiffEntry diff : diffs) {
                    CMnFile file = convert(diff);
                    list.add(file);
                }
                checkin.setFiles(list); 
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return checkin;
    }

}

