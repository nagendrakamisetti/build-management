package com.modeln.build.sourcecontrol;


/**
 * Information about a Perforce changelist object.
 */
public class CMnPerforceCheckIn extends CMnCheckIn {

    /** Name of the Perforce clientspec used to submit the check-in */
    private String client;


    public CMnPerforceCheckIn() {
        super();
    }

    public CMnPerforceCheckIn(State state) {
        super(state);
    }



    /**
     * Return the name of the Perforce clientspec used to submit the changelist.
     *
     * @return  Clientspec name
     */
    public String getClient() {
        return client;
    }

    /**
     * Set the name of the Perforce clientspec used to submit the changelist.
     *
     * @param   name   Clientspec name
     */
    public void setClient(String name) {
        client = name;
    }


}
