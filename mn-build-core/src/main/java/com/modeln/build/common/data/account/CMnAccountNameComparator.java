/*
* Copyright 2000-2003 by Model N, Inc.  All Rights Reserved.
*
* This software is the confidential and proprietary information
* of Model N, Inc ("Confidential Information").  You shall not
* disclose such Confidential Information and shall use it only
* in accordance with the terms of the license agreement you
* entered into with Model N, Inc.
*/
package com.modeln.build.common.data.account;

import java.util.Comparator;


/**
 * Comparator for comparing customer data by customer name. 
 *
 * @author  Shawn Stafford
 */
public class CMnAccountNameComparator implements Comparator {

    /**
     * Compare the name of each customer. 
     * Returns 0 if the names of both customers are equal.
     * Returns a negative integer if the first customer name comes before the second one. 
     * Returns a positive integer if the first customer name comes after the second one. 
     */
    public int compare(Object cust1, Object cust2) {
        String name1 = ((CMnAccount) cust1).getName();
        String name2 = ((CMnAccount) cust2).getName();

        return name1.compareTo(name2);
    }

}


