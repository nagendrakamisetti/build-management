/*
 * PhoneCall.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.common.data.phone;

import java.util.*;


/**
 * The PhoneBill class represents a celluar phone bill
 * including usage summaries and individual calls for
 * the current billing cycle.
 *
 * @version            $Revision: 1.1.1.1 $
 * @author             Shawn Stafford
 *
 */
public class PhoneCall {
    /** Time when the call was placed. */
    private Date timestamp;

    /** Flags the call as an incoming or outgoing call. */
    private boolean incoming;

    /** Length of the call in minutes. */
    private float minutes;

    /** Phone number of the other participant. */
    private PhoneNumber phoneNumber;

    /** Description (usually city and state) of the phone number. */
    private String destination;

    /** Additional costs for the call such as long distance surcharges. */
    private double usageFee;

    /** Cost of the call itself. */
    private double airtimeFee;

    /** Combined total cost of the call. */
    private double totalFee;

    /**
     * Construct a new call object.
     */
    public PhoneCall() {
    }


    /** 
     * Sets the time when the call was placed. 
     * 
     * @param   time    Date and time when the call was initiated
     */
    public void setTime(Date time) {
        timestamp = time;
    }

    /** 
     * Returns the time when the call was placed. 
     * 
     * @return  Date and time when the call was initiated
     */
    public Date getTime() {
        return timestamp;
    }

    /** 
     * Flags the call as an incoming or outgoing call. 
     * 
     * @param   toUser  TRUE if the call was placed by the user, FALSE otherwise
     */
    public void setIncoming(boolean toUser) {
        incoming = toUser;
    }

    /** 
     * Determines whether the call was an incoming or outgoing call. 
     * 
     * @return  TRUE if the call was placed by the user, FALSE otherwise
     */
    public boolean getIncoming() {
        return incoming;
    }

    /** 
     * Set the length of the call in minutes. 
     * 
     * @param   length  Length of the call in minutes
     */
    public void setLength(float min) {
        minutes = min;
    }

    /** 
     * Return the length of the call in minutes. 
     * 
     * @return  length  Length of the call in minutes
     */
    public float getLength() {
        return minutes;
    }


    /** 
     * Set the phone number of the other participant. 
     * 
     * @param   number Phone number
     */
    public void setPhoneNumber(PhoneNumber number) {
        phoneNumber = number;
    }

    /** 
     * Return the phone number of the other participant. 
     * 
     * @return  Phone number
     */
    public PhoneNumber getPhoneNumber() {
        return phoneNumber;
    }

    /** 
     * Set the description (usually city and state) of the phone number. 
     *
     * @param   location    Description of the call destination
     */
    public void setDestination(String location) {
        destination = location;
    }

    /** 
     * Return the description (usually city and state) of the phone number. 
     *
     * @return    Description of the call destination
     */
    public String getDestination() {
        return destination;
    }

    /** 
     * Set additional costs for the call such as long distance surcharges. 
     *
     * @param   cost    Total cost attributed to usage fees
     */
    public void setUsageFee(double cost) {
        usageFee = cost;
    }

    /** 
     * Return additional costs for the call such as long distance surcharges. 
     *
     * @return    Total cost attributed to usage fees
     */
    public double getUsageFee() {
        return usageFee;
    }

    /** 
     * Set the cost of the call airtime itself. 
     * 
     * @param   cost    Total cost of airtime used
     */
    public void setAirtimeFee(double cost) {
        airtimeFee = cost;
    }

    /** 
     * Return the cost of the call airtime itself. 
     * 
     * @return  Total cost of airtime used
     */
    public double getAirtimeFee() {
        return airtimeFee;
    }

    /** 
     * Set the combined total cost of the call. 
     * 
     * @param   cost    Total cost of the call including all fees
     */
    public void setTotalFee(double cost) {
        totalFee = cost;
    }

    /** 
     * Return the combined total cost of the call. 
     * 
     * @return  Total cost of the call including all fees
     */
    public double getTotalFee() {
        return totalFee;
    }

}
