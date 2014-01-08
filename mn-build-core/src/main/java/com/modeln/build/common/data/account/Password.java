/*
 * Password.java
 *
 * Copyright 2002 by Shawn Stafford (sstafford@modeln.com)
 * All rights reserved.
 */
package com.modeln.build.common.data.account;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.*;
import java.security.*;
import java.security.spec.*;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.apache.commons.codec.binary.Base64;


/**
 * The Password class provides password encryption and comparision
 * methods for the major encryption algorithms such as crypt and MD5.
 * The crypt algorithm is implemented within this class.  
 * Additional encryption algorithms such as MD5 are implemented 
 * within the java.security.MessageDigest class.
 *
 * @version            $Revision: 1.1.1.1 $
 * @author             Shawn Stafford
 */

public class Password {  
    private static final String MD5 = "MD5";

    private static final String SALT_DELIMITER = "@@";

    /**
     * Class constructor for password objects.
     */
    private Password() {}

    /**
     * Encrypt a password for the first time using the crypt algorithm. 
     * A random salt value is chosen for encryption, which means this
     * will produce a randomly encrypted string.
     */
    public static String getCrypt(String password) {  
        Random r = new Random();
        String salts = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789./";
        int maxRand = salts.length() - 1;

        char salt1 = salts.charAt(r.nextInt(maxRand));
        char salt2 = salts.charAt(r.nextInt(maxRand));
        String salt = "" + salt1 + salt2;
        return crypt(password, salt);
    }

    /**
     * Encrypt a password for the first time using the MD5 algorithm.
     * A random salt value is chosen for encryption, which means this
     * will produce a randomly encrypted string.
     */
    public static String getMD5(String password) {  
        try {
            return digest(password, MD5);
        } catch (NoSuchAlgorithmException nsa) {
            //throw new Exception("No such message digest algorithm: " + MD5);
            return "";
        }
    }

    /**
     * Encrypt a password for the first time using the PBKDF2 algorithm.
     * Since a PBKDF2 encrypted password is composed of both a salt and
     * the encrypted password, the method will return a concatenated 
     * string containing both the salt and the encrypted password.
     * The method returns a Base64 encoded string which contains the
     * salt value, a delimiter string, and the encrypted password. 
     * Base64 encoding is used so that the encrypted password can be
     * easily stored in a database or text file. 
     *
     * @param   password    Unencrypted password
     * @return  Base64 encoded string containing both the salt and encrypted password
     */
    public static String getPBKDF2(String password) {
        try {
            // VERY important to use SecureRandom instead of just Random
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");

            // Generate a 8 byte (64 bit) salt as recommended by RSA PKCS5
            byte[] salt = new byte[8];
            random.nextBytes(salt);

            return getPBKDF2(salt, password);
        } catch (NoSuchAlgorithmException nsa) {
            //throw new Exception("No such message digest algorithm: " + PBKDF2);
            return "";
        }
    }

    /**
     * Encrypt a password using the PBKDF2 algorithm and the salt value.
     *
     * @param   salt        Salt used to encrypt the password
     * @param   password    Unencrypted password
     * @return  Base64 encoded string containing both the salt and encrypted password
     */
    public static String getPBKDF2(byte[] salt, String password) {
        try {
            // PBKDF2 with SHA-1 as the hashing algorithm. Note that the NIST
            // specifically names SHA-1 as an acceptable hashing algorithm for PBKDF2
            String algorithm = "PBKDF2WithHmacSHA1";

            // SHA-1 generates 160 bit hashes, so that's what makes sense here
            int derivedKeyLength = 160;

            // Define the number of iterations used to encrypt the password.
            // The NIST recommends at least 1,000 iterations:
            // http://csrc.nist.gov/publications/nistpubs/800-132/nist-sp800-132.pdf
            int iterations = 50000;
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, derivedKeyLength);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(algorithm);
            byte[] encrypted = keyFactory.generateSecret(spec).getEncoded();

            // Encode the binary data using Base64 to make it easier to manage
            byte[] base64salt = Base64.encodeBase64(salt);
            byte[] base64pass = Base64.encodeBase64(encrypted);

            String strSalt = new String(base64salt);
            String strPass = new String(base64pass);
            return strSalt + SALT_DELIMITER + strPass;
        } catch (Exception ex) {
            return "";
        }
    }


    /**
     * Compare the encrypted and unencrypted string using the 
     * crypt algorithm.  The first two characters of the encrypted
     * string will be used as the salt to encrypt the unencrpted
     * string for comparison.
     *
     * @param   unencrypted     Unencrypted password string
     * @param   encrypted       Crypt encrypted password string
     * @return  TRUE if the strings match, FALSE otherwise
     */
    public static boolean matchesCrypt(String unencrypted, String encrypted) {
        boolean matches = false;

        String salt = null;
        if ((encrypted != null) && (encrypted.length() >= 2)) {
            salt = encrypted.substring(0,2);
            String codedPw = crypt(unencrypted, salt);
            matches = codedPw.equals(encrypted);
        }

        return matches;
    }

    /**
     * Compare the encrypted and unencrypted string using the 
     * MD5 algorithm.
     *
     * @param   unencrypted     Unencrypted password string
     * @param   encrypted       MD5 encrypted password string
     * @return  TRUE if the strings match, FALSE otherwise
     */
    public static boolean matchesMD5(String unencrypted, String encrypted) {
        try {
            return digest(unencrypted, MD5).equals(encrypted);
        } catch (NoSuchAlgorithmException nsa) {
            //throw new Exception("No such message digest algorithm: " + MD5);
            return false;
        }
    }

    /**
     * Compare the encrypted and unencrypted string using the
     * PBKDF2 algorithm.  The first 8 characters of the encrypted
     * password string are expected to be the salt value, followed
     * by 160 characters of the encrypted password.
     *
     * @param   unencrypted     Unencrypted password string
     * @param   encrypted       PBKDF2 encrypted password string
     * @return  TRUE if the strings match, FALSE otherwise
     */
    public static boolean matchesPBKDF2(String unencrypted, String encrypted) {
        boolean match = false;

        try {
            // Locate the position of the salt delimiter within the string
            int delimiter = encrypted.indexOf(SALT_DELIMITER);

            // Locate the position of the encrypted password within the string
            int idxPass = delimiter + SALT_DELIMITER.length();

            if ((delimiter > 0) && (idxPass < encrypted.length())) {
                // Split the string at the delimeter between the salt and
                // encrypted password
                String strSalt = encrypted.substring(0, delimiter);
                String strPass = encrypted.substring(idxPass);

                // Compare the encrypted strings 
                if (Base64.isBase64(strSalt) && Base64.isBase64(strPass)) {
                    // Remove the Base64 encoding to get the salt value
                    byte[] salt = Base64.decodeBase64(strSalt);
                    byte[] pass = Base64.decodeBase64(strPass);

                    // Encrypt the unencrypted password so it can be compared
                    String password = getPBKDF2(salt, unencrypted);

                    // Compare the two password values
                    return password.equals(encrypted);
                }
            }
        } catch (Exception ex) {
            return false;
        }

        return match;
    }


    /**
     * Uses the java.security.MessageDigest class to encrypt the
     * string using the specified encryption algorithm.
     *
     * @param   password    unencrypted password string
     * @param   algorithm   Name of the encryption algorithm to be used
     * @return  encrypted string
     */
    private static String digest(String password, String algorithm) 
        throws NoSuchAlgorithmException
    {  
        MessageDigest md = MessageDigest.getInstance(algorithm);

        byte[] input = password.getBytes();
        byte[] output = md.digest(input);
        char[] result = new char[output.length * 2];

        for (int i = 0; i < output.length; i++) {  
            String s = Integer.toHexString(output[i]);
            if (s.length() < 2) s = "0" + s;
            if (s.length() > 2) s = s.substring(s.length() - 2);
            result[2*i] = s.charAt(0);
            result[2*i+1] = s.charAt(1);
        }

        return new String(result);
    }
   
    // This code was discovered at http://www.dynamic.net.au/christos/crypt/,
    // which was mentioned in http://java.sun.com/people/linden/faq_d.html,
    // which resulted from a Google search for: unix crypt Java source.

    private static byte[] InitialTr = {
        58,50,42,34,26,18,10, 2,60,52,44,36,28,20,12, 4,
        62,54,46,38,30,22,14, 6,64,56,48,40,32,24,16, 8,
        57,49,41,33,25,17, 9, 1,59,51,43,35,27,19,11, 3,
        61,53,45,37,29,21,13, 5,63,55,47,39,31,23,15, 7
    };

    private static byte[] FinalTr = {
        40, 8,48,16,56,24,64,32,39, 7,47,15,55,23,63,31,
        38, 6,46,14,54,22,62,30,37, 5,45,13,53,21,61,29,
        36, 4,44,12,52,20,60,28,35, 3,43,11,51,19,59,27,
        34, 2,42,10,50,18,58,26,33, 1,41, 9,49,17,57,25
    };

    private static byte[] swap = {
        33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,
        49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,
         1, 2, 3, 4, 5, 6, 7, 8, 9,10,11,12,13,14,15,16,
        17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32
    };

    private static byte[] KeyTr1 = {
        57,49,41,33,25,17, 9, 1,58,50,42,34,26,18,
        10, 2,59,51,43,35,27,19,11, 3,60,52,44,36,
        63,55,47,39,31,23,15, 7,62,54,46,38,30,22,
        14, 6,61,53,45,37,29,21,13, 5,28,20,12, 4
    };

    private static byte[] KeyTr2 = {
        14,17,11,24, 1, 5, 3,28,15, 6,21,10,
        23,19,12, 4,26, 8,16, 7,27,20,13, 2,
        41,52,31,37,47,55,30,40,51,45,33,48,
        44,49,39,56,34,53,46,42,50,36,29,32
    };

    private static byte[] etr = {
        32, 1, 2, 3, 4, 5, 4, 5, 6, 7, 8, 9,
         8, 9,10,11,12,13,12,13,14,15,16,17,
        16,17,18,19,20,21,20,21,22,23,24,25,
        24,25,26,27,28,29,28,29,30,31,32, 1
    };

    private static byte[] ptr = {
        16, 7,20,21,29,12,28,17, 1,15,23,26, 5,18,31,10,
         2, 8,24,14,32,27, 3, 9,19,13,30, 6,22,11, 4,25
    };

    private static byte s_boxes[][] = {
        {
            14, 4,13, 1, 2,15,11, 8, 3,10, 6,12, 5, 9, 0, 7,
             0,15, 7, 4,14, 2,13, 1,10, 6,12,11, 9, 5, 3, 8,
             4, 1,14, 8,13, 6, 2,11,15,12, 9, 7, 3,10, 5, 0,
            15,12, 8, 2, 4, 9, 1, 7, 5,11, 3,14,10, 0, 6,13
        },

        {
            15, 1, 8,14, 6,11, 3, 4, 9, 7, 2,13,12, 0, 5,10,
             3,13, 4, 7,15, 2, 8,14,12, 0, 1,10, 6, 9,11, 5,
             0,14, 7,11,10, 4,13, 1, 5, 8,12, 6, 9, 3, 2,15,
            13, 8,10, 1, 3,15, 4, 2,11, 6, 7,12, 0, 5,14, 9
        },

        {
            10, 0, 9,14, 6, 3,15, 5, 1,13,12, 7,11, 4, 2, 8,
            13, 7, 0, 9, 3, 4, 6,10, 2, 8, 5,14,12,11,15, 1,
            13, 6, 4, 9, 8,15, 3, 0,11, 1, 2,12, 5,10,14, 7,
             1,10,13, 0, 6, 9, 8, 7, 4,15,14, 3,11, 5, 2,12
        },

        {
             7,13,14, 3, 0, 6, 9,10, 1, 2, 8, 5,11,12, 4,15,
            13, 8,11, 5, 6,15, 0, 3, 4, 7, 2,12, 1,10,14, 9,
            10, 6, 9, 0,12,11, 7,13,15, 1, 3,14, 5, 2, 8, 4,
             3,15, 0, 6,10, 1,13, 8, 9, 4, 5,11,12, 7, 2,14
        },

        {
             2,12, 4, 1, 7,10,11, 6, 8, 5, 3,15,13, 0,14, 9,
            14,11, 2,12, 4, 7,13, 1, 5, 0,15,10, 3, 9, 8, 6,
             4, 2, 1,11,10,13, 7, 8,15, 9,12, 5, 6, 3, 0,14,
            11, 8,12, 7, 1,14, 2,13, 6,15, 0, 9,10, 4, 5, 3
        },

        {
            12, 1,10,15, 9, 2, 6, 8, 0,13, 3, 4,14, 7, 5,11,
            10,15, 4, 2, 7,12, 9, 5, 6, 1,13,14, 0,11, 3, 8,
             9,14,15, 5, 2, 8,12, 3, 7, 0, 4,10, 1,13,11, 6,
             4, 3, 2,12, 9, 5,15,10,11,14, 1, 7, 6, 0, 8,13
        },

        {
             4,11, 2,14,15, 0, 8,13, 3,12, 9, 7, 5,10, 6, 1,
            13, 0,11, 7, 4, 9, 1,10,14, 3, 5,12, 2,15, 8, 6,
             1, 4,11,13,12, 3, 7,14,10,15, 6, 8, 0, 5, 9, 2,
             6,11,13, 8, 1, 4,10, 7, 9, 5, 0,15,14, 2, 3,12
        },

        {
            13, 2, 8, 4, 6,15,11, 1,10, 9, 3,14, 5, 0,12, 7,
             1,15,13, 8,10, 3, 7, 4,12, 5, 6,11, 0,14, 9, 2,
             7,11, 4, 1, 9,12,14, 2, 0, 6,10,13,15, 3, 5, 8,
             2, 1,14, 7, 4,10, 8,13,15,12, 9, 0, 3, 5, 6,11
        },
    };

    private static int rots[] = {
        1,1,2,2,2,2,2,2,1,2,2,2,2,2,2,1
    };

    private static byte[] key = new byte[64] ;
    private static byte[] EP = etr;

    // Support methods start here 

    private static void transpose(byte[] data, byte[] t, int n) {
        byte[] x = new byte[64] ;
        System.arraycopy ( data, 0, x, 0, x.length ) ;

        while ( n-- > 0 ) {
            data[n] = x[t[n]-1] ;
        }
    }

    private static void rotate(byte[] key) {
        byte[] x = new byte[64] ;
        System.arraycopy (key, 0, x, 0, x.length) ;

        for (int i = 0; i < 55; i++) {
            x[i] = x[i+1];
        }
        x[27] = key[0] ;
        x[55] = key[28] ;
        System.arraycopy (x, 0, key, 0, key.length) ;
    }

    private static void f(int i, byte[] key, byte[] a, byte[] x) {
        byte[] e = new byte[64];
        byte[] ikey = new byte[64];
        byte[] y = new byte[64];

        System.arraycopy (a, 0, e, 0, e.length);
        transpose(e, EP, 48);

        for (int j = rots[i]; j > 0; j--) {
            rotate(key) ;
        }

        System.arraycopy(key, 0, ikey, 0, ikey.length) ;
        transpose(ikey, KeyTr2, 48);

        for (int j = 0; j < 48; j++) {
            y[j] = (byte)(e[j] ^ ikey[j]) ;
        }

        for (int j = 0; j < 8; j++) {
            int k = j+1 ;
            int r = 32 * y[6*k - 6] +
                8 * y[6*k - 5] +
                4 * y[6*k - 4] +
                2 * y[6*k - 3] +
                    y[6*k - 2] +
               16 * y[6*k - 1];
            int xb = s_boxes[j][r];

            x[4*k - 4] = (byte)(( xb >> 3 ) & 1);
            x[4*k - 3] = (byte)(( xb >> 2 ) & 1);
            x[4*k - 2] = (byte)(( xb >> 1 ) & 1);
            x[4*k - 1] = (byte)(  xb  & 1 );
        }
        transpose(x, ptr, 32);
    }

    private static void definekey(byte[] k) {
        System.arraycopy(k, 0, key, 0, key.length) ;
        transpose(key, KeyTr1, 56) ;
    }

    private static void encrypt(byte[] blck, int edflag) {
        byte[] p = blck ;

        transpose ( p, InitialTr, 64 ) ;

        for (int i = 15; i >= 0; i--) {
            int j = edflag > 0 ? i : 15 - i;
            byte[] b = new byte[64];
            System.arraycopy(p, 0, b, 0, b.length);
            byte[] x = new byte[64];

            for (int k = 31; k >= 0; k--) {
                p[k] = b[k+32] ;
            }

            f (j, key, p, x);
            for (int k = 31; k >= 0; k--) {
                p[k+32] = (byte)(b[k] ^ x[k]) ;
            }
        }

        transpose ( p, swap, 64 ) ;
        transpose ( p, FinalTr, 64 ) ;
        blck = p ;
    }


    /**
     * Encrypt the string using the crypt algorithm and the 
     * given salt.
     */
    private static String crypt (String password, String salt) {
        char[] pw = password.toCharArray() ;
        char[] saltc = salt.toCharArray() ;
        byte[] pwb = new byte[66] ;
        char[] result = new char[13] ;
        byte[] new_etr = new byte[etr.length] ;
        int n = 0 ;
        int m = 0 ;

        while ((m < pw.length) && (n < 64)) {
            for (int j = 6; j >= 0; j--) {
                pwb[n++] = (byte)((pw[m] >> j) & 1) ;
            }
            m++ ; // Increment pw
            pwb[n++] = 0 ;
        }

        while (n < 64) {
            pwb[n++] = 0 ;
        }

        definekey (pwb);
        for (n = 0; n < 66; n++) {
            pwb[n] = 0 ;
        }

        System.arraycopy (etr, 0, new_etr, 0 , new_etr.length) ;
        EP = new_etr ;

        for (int i = 0; i < 2; i++) {
            char c = saltc[i] ;

            result[i] = c ;
            if (c > 'Z') {
                c -= 6 + 7 + '.';       // c was a lowercase letter
            } else if (c > '9') {
                c -= 7 + '.';           // c was a uppercase letter
            } else {
                c -= '.' ;              // c was a digit, '.' or '/'
            } // now, 0 <= c <= 63

            for (int j = 0; j < 6; j++) {
                if (((c >> j) & 1) == 1) {
                    byte t         = (byte)(6*i + j);
                    byte temp      = new_etr[t];
                    new_etr[t]     = new_etr[t+24];
                    new_etr[t+24]  = temp;
                }
            }
        }

        if (result[1] == 0) {
            result[1] = result[0] ;
        }

        for (int i = 0; i < 25; i++) {
            encrypt ( pwb, 0 ) ;
        }
        EP = etr ;

        m = 2 ;
        n = 0 ;
        while (n < 66) {
            int c = 0 ;

            for (int j = 6; j > 0; j--) {
                c <<= 1 ;
                c |= pwb[n++] ;
            }

            c += '.' ;        // becomes >= '.'
            if ( c > '9' ) {
                c += 7 ;    // not in [./0-9], becomes upper
            }

            if ( c > 'Z' ) {
                c += 6 ;    // not in [A-Z], becomes lower
            }

            result[m++] = (char) c ;
        }
      
        return ( new String ( result ) );
    }
}
