/*
 * FileUtility.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.util;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import java.io.File;

/**
 * This class implements file manipulation methods.
 *
 * @version            $Revision: 1.1.1.1 $
 * @author             Shawn Stafford
 */
public class FileUtility {

    private static int compare(File file1, File file2) {
        if (file1.isDirectory() && file2.isFile())
            return -1;
        else if (file1.isFile() && file2.isDirectory())
            return 1;
        else return file1.compareTo(file2);
    }

    /**
     * Insert a File into a vector at the correct position within 
     * the sorted list of files.
     * Originally found in package org.w3c.tools.sorter.
     *
     * @param file The File used to sort.
     * @param into The target sorted vector.
     */
    public static void orderedFileInsert(File file, Vector into) {
        int  lo   = 0 ;
        int  hi   = into.size() - 1 ;
        int  idx  = -1 ;
        File item = null ;
        int  cmp  = 0 ;

        if ( hi >= lo ) {
            while ((hi - lo) > 1) {
                idx  = (hi-lo) / 2 + lo ;
                item = (File) into.elementAt(idx) ;
                cmp  = compare(item, file) ;
                if ( cmp == 0 ) {
                    return ;
                } else if ( cmp < 0 ) {
                    lo = idx ;
                } else if ( cmp > 0 ) {
                    hi = idx ;
                }
            }
            switch (hi-lo) {
            case 0:
                item = (File) into.elementAt(hi) ;
                if (item.equals(file))
                    return ;
                idx = (compare(item, file) < 0) ? hi + 1 : hi ;
                break ;
            case 1:
                File loitem = (File) into.elementAt(lo) ;
                File hiitem = (File) into.elementAt(hi) ;
                if ( loitem.equals(file) )
                    return ;
                if ( hiitem.equals(file) )
                    return ;
                if ( compare(file, loitem) < 0 ) {
                    idx = lo ;
                } else if ( compare(file, hiitem) < 0 ) {
                    idx = hi ;
                } else {
                    idx = hi + 1 ;
                }
                break ;
            default:
                throw new RuntimeException ("implementation bug.") ;
            }
        }
        // Add this file to the vector:
        if ( idx < 0 ) 
            idx = 0 ;
        into.insertElementAt(file, idx) ;
        return ;
    }

}
