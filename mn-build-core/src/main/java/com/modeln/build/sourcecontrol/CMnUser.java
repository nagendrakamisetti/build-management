package com.modeln.build.sourcecontrol;

/**
 * Created by IntelliJ IDEA.
 * User: vmalley
 * Date: 10/18/11
 * Time: 11:56 AM
 * To change this template use File | Settings | File Templates.
 */
public class CMnUser {

    private String full_name;

    private String email;

    public CMnUser(String full_name, String email) {
        this.full_name = full_name;
        this.email = email;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
