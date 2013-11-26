/*
 * PhoneBill.java
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
public class PhoneBill {
    /** Date when the billing cycle begins */
    private Date dateBegins;

    /** Date when the billing cycle ends */
    private Date dateEnds;

    /** Date when the bill must be paid by */
    private Date dateDue;

    /** Total bill balance, including all itemized amounts */
    private double balance;

    private double previousBalance;
    private double taxes;
    private double discounts;
    private double usageFees;
    private double planFees;

    /** List of phone calls associated with this bill. */
    private Vector calls = new Vector();

    /**
     * Construct a new phone bill.
     */
    protected PhoneBill() {}


    /**
     * Construct a new phone bill.
     *
     * @param   begin   Date when the phone bill begins
     * @param   end     Date when the phone bill ends
     */
    public PhoneBill(Date begin, Date end) {
        dateBegins = begin;
        dateEnds = end;
    }

    /**
     * Sets the bill begin date.
     *
     * @param   date    Begin date
     */
    public void setBeginDate(Date date) {
        dateBegins = date;
    }

    /**
     * Returns the bill begin date.
     *
     * @return    Begin date
     */
    public Date getBeginDate() {
        return dateBegins;
    }

    /**
     * Sets the bill end date.
     *
     * @param   date    End date
     */
    public void setEndDate(Date date) {
        dateEnds = date;
    }

    /**
     * Returns the bill end date.
     *
     * @return    End date
     */
    public Date getEndDate() {
        return dateEnds;
    }

    /**
     * Sets the bill due date.
     *
     * @param   date    Due date
     */
    public void setDueDate(Date date) {
        dateDue = date;
    }

    /**
     * Returns the bill due date.
     *
     * @return    Due date
     */
    public Date getDueDate() {
        return dateDue;
    }


    /**
     * Sets the current bill balance.
     *
     * @param   amount    Total bill balance
     */
    public void setBalance(double amount) {
        balance = amount;
    }

    /**
     * Returns the current bill balance.
     *
     * @return    Total bill balance
     */
    public double getBalance() {
        return balance;
    }


    /**
     * Adds a phone call to the bill.
     */
    public void addCall(PhoneCall call) {
        calls.add(call);
    }
}
