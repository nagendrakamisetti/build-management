/*
 * StringUtility.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.util;

import java.util.*;

/**
 * This class contains utility methods which are used to manipulate
 * or verify the state of strings.
 *
 * @version            $Revision: 1.2 $
 * @author             Shawn Stafford
 *
 */
public class StringUtility {

    /** List of characters to use in random string generator */
    private static final String RANDOM_CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";


    private static final String specialCharacters = "\t\r\n\f";

    /** 
     * Return a string of fixed length which is either truncated 
     * or padded with characters.
     * 
     * @param   str     Original string
     * @param   length  Desired length
     * @param   pad     Character to use for padding
     */
    public static String getFixedString(String str, int length, char pad) {
        if (str != null) {
            if (str.length() >= length) {
                return str.substring(0, length - 1);
            } else {
                int addLength = length - str.length();
                char[] array = new char[addLength];
                for (int idx = 0; idx < addLength; idx++) {
                    array[idx] = pad;
                }
                String padding = new String(array);
                return str + padding;
            }
        } else {
            return null;
        }
    }

    /**
     * Determines if the string contains extended ASCII characters
     *
     * @param  str      String to be checked
     * @return boolean  TRUE if the string contains extended characters
     */
    public static boolean isExtendedAsciiString(String name) {
        return isStringGood(name, 255);
    }

    /**
     * Determines if the string contains extended ASCII characters
     *
     * @param  str      String to be checked
     * @return boolean  TRUE if the string contains extended characters
     */
    public static boolean isAsciiString(String name) {
        return isStringGood(name, 127);
    }

    /**
     * Determines if the string contains extended ASCII characters
     *
     * @param  str      String to be checked
     * @return boolean  TRUE if the string contains extended characters
     */
    private static boolean isStringGood(String name, int upperLimit) {
        if ((name == null)||(name.length() == 0)) {
            return true;
        }
    
        for (int i=0; i < name.length(); i++) {
            int j = (int) name.charAt(i);
            if (j > upperLimit) return false;
        }
        return true;
    }


    /*
     * Converts encoded \\uxxxx to unicode chars and changes special 
     * saved chars to their original forms.  This may be necessary
     * when using readers to load Unicode strings from file, since
     * the readers do not attempt to interpret the ASCII input.
     *
     * @param theString ASCII string containing \\uxxxx encoded Unicode characters
     * @return String resulting from Unicoded conversion
     * @throws IllegalArgumentException thrown when an invalid \\uxxxx string is parsed
     */
    public static String asciiToUnicode (String theString) throws IllegalArgumentException {
        char aChar;
        int len = theString.length();
        StringBuffer outBuffer = new StringBuffer(len);

        for(int x=0; x<len; ) {
            aChar = theString.charAt(x++);
            if (aChar == '\\') {
                aChar = theString.charAt(x++);
                if(aChar == 'u') {
                    // Read the xxxx
                    int value=0;
                    for (int i=0; i<4; i++) {
                        aChar = theString.charAt(x++);
                        switch (aChar) {
                          case '0': case '1': case '2': case '3': case '4':
                          case '5': case '6': case '7': case '8': case '9':
                             value = (value << 4) + aChar - '0';
                             break;
                          case 'a': case 'b': case 'c':
                          case 'd': case 'e': case 'f':
                             value = (value << 4) + 10 + aChar - 'a';
                             break;
                          case 'A': case 'B': case 'C':
                          case 'D': case 'E': case 'F':
                             value = (value << 4) + 10 + aChar - 'A';
                             break;
                          default:
                              throw new IllegalArgumentException(
                                           "Malformed \\uxxxx encoding.");
                        }
                    }
                    outBuffer.append((char)value);
                } else {
                    if (aChar == 't') aChar = '\t';
                    else if (aChar == 'r') aChar = '\r';
                    else if (aChar == 'n') aChar = '\n';
                    else if (aChar == 'f') aChar = '\f';
                    outBuffer.append(aChar);
                }
            } else {
                outBuffer.append(aChar);
            }
        }
        return outBuffer.toString();
    }

    /*
     * Converts Unicode characters to encoded \\uxxxx and returns
     * the encoded string.  This is useful when attempting to save
     * Unicode strings to a text-only file.
     *
     * @param theString contains Unicode characters
     * @return String containing \\uxxxx encoded Unicode strings
     */
    public static String unicodeToAscii(String theString) {
        char aChar;
        int len = theString.length();
        StringBuffer outBuffer = new StringBuffer(len*2);

        for(int x=0; x<len; ) {
            aChar = theString.charAt(x++);
            switch(aChar) {
                case '\\':outBuffer.append('\\'); outBuffer.append('\\');
                          continue;
                case '\t':outBuffer.append('\\'); outBuffer.append('t');
                          continue;
                case '\n':outBuffer.append('\\'); outBuffer.append('n');
                          continue;
                case '\r':outBuffer.append('\\'); outBuffer.append('r');
                          continue;
                case '\f':outBuffer.append('\\'); outBuffer.append('f');
                          continue;
                default:
                    if ((aChar < 20) || (aChar > 127)) {
                        outBuffer.append('\\');
                        outBuffer.append('u');
                        outBuffer.append(toHex((aChar >> 12) & 0xF));
                        outBuffer.append(toHex((aChar >> 8) & 0xF));
                        outBuffer.append(toHex((aChar >> 4) & 0xF));
                        outBuffer.append(toHex((aChar >> 0) & 0xF));
                    }
                    else {
                        if (specialCharacters.indexOf(aChar) != -1)
                            outBuffer.append('\\');
                        outBuffer.append(aChar);
                    }
            }
        }
        return outBuffer.toString();
    }


    /**
     * Convert a nibble to a hex character
     * @param nibble the nibble to convert.
     */
    private static char toHex(int nibble) {
        return hexDigit[(nibble & 0xF)];
    }

    /** A table of hex digits */
    private static final char[] hexDigit = {
        '0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'
    };


    /**
     * Searches the line for any variables which should be substituted with 
     * values from the hashtable.  The delimiter string is used to identify
     * variable placeholders within the string.  A delimiter will appear on
     * both sides of a key to indicate that the text between (and including) 
     * the delimiters should be substituted.  To prevent unexpected behavior,
     * hashtable keys which contain the delimiter string will not be
     * substituted.
     *
     * @param   str         String containing possible variable delimiters
     * @param   vars        Hashtable containing variable/value pairs to substitute
     * @param   delimiter   Delimits variables within the string
     */
    public static String substitute(String str, Hashtable vars, String delimiter) {
        String key = null;
        for (Enumeration keys = vars.keys(); keys.hasMoreElements(); ) {
            key = (String) keys.nextElement();
            // Replace the variable in the string
            if ((key != null) && (key.indexOf(delimiter) == -1)) {
                str = replaceVar(str, delimiter + key + delimiter, (String)vars.get(key));
            }
        }

        return str;
    }

    /**
     * Replaces all occurences of a variable within the string.
     *
     * @param   str     String containing the variable to be replaced
     * @param   var     Variable string to be replaced
     * @param   newVal  Value to be substituted for the variable
     *
     * @return  String  final string with all occurances of the variable removed
     */
    public static String replaceVar(String str, String var, String newVal) {
        // Verify that the input parameters are OK
        if ((str != null) && (var != null) && (var.length() > 0)) {
            // Locate the first occurrence of the variable within the string
            int varIndex = str.indexOf(var);
        
            // Replace all instances of the variable
            while ((str != null) && (varIndex >= 0)) {
                str = str.substring(0, varIndex) + newVal + str.substring(varIndex + var.length());

                // Locate the next occurrence of the variable in the string
                varIndex = str.indexOf(var);
            }
        }

        return str;
    }


    /**
     * Replaces any reserved HTML characters with their escaped equivalents.
     * For example, any double quotes (&quot;) would be converted to &amp;quot;
     *
     * @param   str     String containing characters which must be escaped
     * @return  String with HTML escaped characters
     */
    public static String stringToHtml(String str) {
        if (str != null) {
            str = replaceChar(str, '&', "&amp;");
            str = replaceChar(str, '"', "&quot;");
            str = replaceChar(str, '>', "&gt;");
            str = replaceChar(str, '<', "&lt;");
        }

        return str;
    }

    /**
     * Replaces an reserved URL characters with their escaped equivalents.
     * For example, any percent sign (%) would be converted to %25.
     *
     * @param   str     String containing characters which must be escaped
     * @return  String with HTML escaped characters
     */
    public static String stringToURL(String str) {
        if (str != null) {
            str = replaceChar(str, '%', "%25");
        }

        return str;
    }


    /**
     * Replaces a single character with a string.
     *
     * @param   line    Line of text to be updated
     * @param   ch      character to be replaced
     * @param   str     String to replace the character with
     * @return  String containing the substituted values
     */
    public static String replaceChar(String line, char ch, String str) {
        String newString = "";
        int idxChar;

        while ((line != null) && (line.length() > 0)) {
            idxChar = line.indexOf(ch);
            if (idxChar >= 0) {
                newString = newString + line.substring(0, idxChar) + str;
                // Trim the processed information from the current string
                line = line.substring(idxChar + 1);
            } else {
                // Get the rest of the line when no more characters are found
                newString = newString + line;
                line = null;
            }
        }
        return newString;
    }

    /**
     * Insert a String into a vector, maintaing the order.
     *
     * @param key The string to insert.
     * @param into The target sorted vector.
     */
    static void orderedStringInsert(String key, Vector into) {
        int lo  = 0 ;
        int hi  = into.size() - 1 ;
        int idx = -1 ;
        String item = null ;
        int cmp = 0 ;

        if ( hi >= lo ) {
            while ((hi - lo) > 1) {
                idx  = (hi-lo) / 2 + lo ;
                item = (String) into.elementAt(idx) ;
                cmp  = item.compareToIgnoreCase(key) ;
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
                item = (String) into.elementAt(hi) ;
                if (item.equals(key))
                    return ;
                idx = (item.compareToIgnoreCase(key) < 0) ? hi + 1 : hi ;
                break ;
              case 1:
                String loitem = (String) into.elementAt(lo) ;
                String hiitem = (String) into.elementAt(hi) ;
                if ( loitem.equals(key) )
                    return ;
                if ( hiitem.equals(key) )
                    return ;
                if ( key.compareToIgnoreCase(loitem) < 0 ) {
                    idx = lo ;
                } else if ( key.compareToIgnoreCase(hiitem) < 0 ) {
                    idx = hi ;
                } else {
                    idx = hi + 1 ;
                }
                break ;
              default:
                throw new RuntimeException ("implementation bug.") ;
            }
        }
        // Add this key to the vector:
        if ( idx < 0 ) 
            idx = 0 ;
        into.insertElementAt(key, idx) ;
        return ;
    }

    /**
     * Quick sort the given chunk of the array in place.
     *
     * @param array The array to sort.
     * @param lo0 The low bound of the chunk of the array to sort.
     * @param hi0 The high bound of the array to sort.
     */
    static void quickSortStringArray(String array[], int lo0, int hi0) {
        int lo = lo0 ;
        int hi = hi0 ;
        String mid = null ;

        if ( hi0 > lo0 ) {
            mid = array[(lo0+hi0)/2] ;
            while (lo <= hi) {
                while ((lo < hi0) && (array[lo].compareToIgnoreCase(mid) < 0)) 
                    ++lo ;
                while ((hi > lo0) && (array[hi].compareToIgnoreCase(mid) > 0))
                    --hi ;
                if ( lo <= hi ) {
                    String tmp = array[lo] ;
                    array[lo]  = array[hi] ;
                    array[hi]  = tmp ;
                    ++lo ;
                    --hi ;
                }
            }
            if ( lo0 < hi )
                quickSortStringArray(array, lo0, hi) ;
            if ( lo < hi0 )
                quickSortStringArray(array, lo, hi0) ;
        }
    }

    /**
     * Get the keys of this hashtable, sorted.
     *
     * @param h The hashtable whose String keys are wanted.
     */
    public static Vector sortStringKeys(Hashtable h) {
        return sortStringEnumeration(h.keys()) ;
    }

    /**
     * Get the values of this hashtable, sorted.
     *
     * @param h The hashtable whose String keys are wanted.
     */
    public static Vector sortStringValues(Hashtable h) {
        return sortStringEnumeration(h.elements()) ;
    }

    /**
     * Sort the given String enumeration.
     *
     * @param  list  List of strings
     * @return A sorted vector of String.
     */
    public static Vector sortStringEnumeration(Enumeration list) {
        Vector sorted = new Vector() ;
        while ( list.hasMoreElements() ) 
            orderedStringInsert((String) list.nextElement(), sorted) ;
        sorted.trimToSize();
        return sorted ;
    }


    /**
     * Sort the given String array in place.
     *
     * @param array The array of String to sort.
     * @param inplace Sort the array in place if <strong>true</strong>, 
     *    allocate a fresh array for the result otherwise.
     *
     * @return The same array, with string sorted.
     */
    public static String[] sortStringArray(String array[], boolean inplace) {
        String tosort[] = array ;
        if ( ! inplace ) {
            tosort = new String[array.length] ;
            System.arraycopy(array, 0, tosort, 0, array.length) ;
        }
        quickSortStringArray(tosort, 0, tosort.length-1) ;
        return tosort ;
    }


    /**
     * Generate a string of random characters of the given length.
     *
     * @param   len   Length of the random string
     * @return  String of random characters
     */
    public static String getRandomString(int len) {
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(len);
        for( int i = 0; i < len; i++ ) { 
            sb.append( RANDOM_CHARACTERS.charAt( rnd.nextInt(RANDOM_CHARACTERS.length()) ) );
        }
        return sb.toString();
    }

    /**
     * Analyze two strings to determine how much they differ.
     *
     * @param  str1   First string to analyze
     * @param  str2   Second string to analyze
     * @return 1.0 if they are identical
     */
    public static float getEquality(String str1, String str2) {
        String lineDelimiter = System.getProperty("line.separator");
        StringTokenizer st1 = new StringTokenizer(str1, lineDelimiter);
        StringTokenizer st2 = new StringTokenizer(str2, lineDelimiter);

        // Keep track of the line statistics in order to calculate equality
        int linecount1   = 0;  // Total number of lines in the first string
        int linecount2   = 0;  // Total number of lines in the second string
        int samecount    = 0;  // Total number of lines that match
        int addedcount   = 0;  // Total number of lines added
        int deletedcount = 0;  // Total number of lines deleted

        // Count the number of lines in each string
        linecount1 = st1.countTokens();
        linecount2 = st2.countTokens();

        // Define an array to contain the lines in each string
        String[] x = new String[linecount1];   // lines in first string
        String[] y = new String[linecount2];   // lines in second string


        // Keep track of the current line in each string
        int M = 0;     // number of lines of first string
        int N = 0;     // number of lines of second string

        // Break the strings up into an array of lines 
        while (st1.hasMoreTokens()) {
            x[M++] = st1.nextToken();
        }
        while (st2.hasMoreTokens()) {
            y[N++] = st2.nextToken();
        }

        // opt[i][j] = length of LCS of x[i..M] and y[j..N]
        int[][] opt = new int[M+1][N+1];

        // compute length of LCS and all subproblems via dynamic programming
        for (int i = M-1; i >= 0; i--) {
            for (int j = N-1; j >= 0; j--) {
                if (x[i].equals(y[j])) {
                    opt[i][j] = opt[i+1][j+1] + 1;
                } else { 
                    opt[i][j] = Math.max(opt[i+1][j], opt[i][j+1]);
                }
            }
        }

        // recover LCS itself and print out non-matching lines to standard output
        int i = 0, j = 0;
        while(i < M && j < N) {
            // Determine if the current lines differ
            if (x[i].equals(y[j])) {
                // No change: x[i] and y[j]
                samecount++;
                i++;
                j++;
            } else if (opt[i+1][j] >= opt[i][j+1]) {
                deletedcount++;
                i++;
                // Line deleted: x[i]
            } else {
                addedcount++; 
                j++;
                // Line added: y[j]
            }
        }

        // dump out one remainder of one string if the other is exhausted
        while(i < M || j < N) {
            if (i == M) {
                addedcount++;
                j++;
                // Line added: y[j]
            } else if (j == N) { 
                deletedcount++;
                i++;
                // Line deleted: x[i]
            }
        }

        // Calculate a measure of equality using the diff metrics
        int total = addedcount + deletedcount + samecount;
        float equality = (float) samecount / (float) total; 

        return equality; 
    }
}
