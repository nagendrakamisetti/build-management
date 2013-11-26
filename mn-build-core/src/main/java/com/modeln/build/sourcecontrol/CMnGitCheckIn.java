package com.modeln.build.sourcecontrol;


/**
 * Information about a git commit object.
 */
public class CMnGitCheckIn extends CMnCheckIn {

    /** Name of the git repository */
    private String repository;

    public CMnGitCheckIn() {
        super();
    }

    public CMnGitCheckIn(State state) {
        super(state);
    }


    /**
     * Return the name of the git repository. 
     *
     * @return  Git repository 
     */
    public String getRepository() {
        return repository;
    }

    /**
     * Set the git repository. 
     *
     * @param   repo   Git repository 
     */
    public void setRepository(String repo) {
        repository = repo;
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
        String str = getId();

        // Shorten the SHA to something more UI friendly
        if ((str != null) && (str.length() > 1) && (str.length() > FORMATTED_ID_MAXLENGTH)) {
            str = str.substring(0, FORMATTED_ID_MAXLENGTH - 1);
        }

        // Convert the SHA to upper case 
        if (str != null) {
            str = str.toUpperCase();
        }

        return str;
    }

}
