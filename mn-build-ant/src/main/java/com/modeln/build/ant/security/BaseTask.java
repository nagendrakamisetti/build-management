/*
 * Copyright 2000-2004 by Model N, Inc.  All Rights Reserved.
 * 
 * This software is the confidential and proprietary information
 * of Model N, Inc ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you
 * entered into with Model N, Inc.
 */
package com.modeln.build.ant.security;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;


/**
 * Base task providing generic attributes and methods.
 *
 * @author Shawn Stafford
 */
public class BaseTask extends Task {

    /** Digits used to encode and decode binary data as hexidecimal ASCII characters */
    private static final char hexDigit[] = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    };

    /** Leave message data as unencoded binary data */
    public static final int BINARY = 0;

    /** Encode message data as hexidecimal ASCII text */
    public static final int HEX = 1;


    /** Encoding format used to encode message data */
    private int encoding = HEX;


    /**
     * Set the algorithm used to encode data.  The default encoding scheme 
     * is to encode all binary content as hex encoded ASCII strings.  If no 
     * encoding is desired, the binary encoding type should be used.
     *
     * @param   type   Encoding type (hex, binary)
     */
    public void setEncoding(String type) {
        if (type.equalsIgnoreCase("binary")) {
            encoding = BINARY;
        } else if (type.equalsIgnoreCase("hex")) {
            encoding = HEX;
        } else {
            throw new BuildException("Invalid encoding type: " + type, getLocation());
        }
    }

    /**
     * Use the encoding type to encode the message data and return the
     * encoded data.
     *
     * @param   data   Data to be encoded
     * @return  Encoded binary data
     */
    protected String encode(byte[] data) {
        switch (encoding) {
            case BINARY:
                return new String(data);
            case HEX:
                return bytesToHex(data);
            default:
                return null;
        }
    }

    /**
     * Use the encoding type to decode the message data and return the
     * unencoded binary data.
     *
     * @param   message   Data to be decoded
     * @return  Decoded binary data
     */
    protected byte[] decode(String message) {
        switch (encoding) {
            case BINARY:
                return message.getBytes();
            case HEX:
                return hexToBytes(message);
            default:
                return null;
        }
    }


    /**
     * Conver the byte array to a hexidecimal encoded ASCII string.
     *
     * @param   b   Byte array of data
     * @return  Hexidecimal encoded string
     */
    public static String bytesToHex(byte[] b) {
        StringBuffer buf = new StringBuffer();
        for (int j = 0; j < b.length; j++) {
            buf.append(byteToHex(b[j]));
        }
        return buf.toString();
    }

    /**
     * Conver the byte to a hexidecimal encoded ASCII string.
     *
     * @param   b   Byte of data
     * @return  Hexidecimal encoded string
     */
    public static String byteToHex(byte b) {
        char[] a = { hexDigit[(b >> 4) & 0x0f], hexDigit[b & 0x0f] };
        return new String(a);
    }

    /** 
     * Convert an ASCII hexidecimal encoded string to an array of bytes.
     *
     * @param  hex  Hexidecimal encoded string of data
     * @return Binary array of bytes
     */
    public byte[] hexToBytes(String hex) {
        byte[] data = new byte[hex.length() / 2];
        for (int idx = 0; idx < data.length; idx++) {
            data[idx] = hexToByte(hex.substring(idx * 2, (idx * 2) + 2));
        }
        return data;
    }

    /** 
     * Convert an ASCII hexidecimal encoded string to a single byte.
     *
     * @param  hex  Two character hexidecimal string
     * @return Single byte of data
     */
    public byte hexToByte(String hex) {
        hex = hex.toUpperCase();

        // Obtain the values of the lease and most significant bits
        int lsbVal = 0;
        int msbVal = 0;
        char lsbChar = hex.charAt(1);
        char msbChar = hex.charAt(0);
        for (int idx = 0; idx < hexDigit.length; idx++) {
            if (lsbChar == hexDigit[idx]) {
                lsbVal = idx;
            }
            if (msbChar == hexDigit[idx]) {
                msbVal = idx;
            }
        }

        // Determine the actual value encoded string
        int total = (msbVal * 16) + lsbVal;

        // Add up hex values to total decimal equivalent 
        return (byte) total;
    }

}
