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

import java.security.InvalidParameterException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.apache.tools.ant.BuildException;

/**
 * Generates a symetric encryption key.
 *
 * @author Shawn Stafford
 */
public final class KeyGeneratorTask extends BaseTask {


    /** The encryption algorithm used to generate the key. */
    private String algorithm = "DES";

    /** The project property that will store the key values. */
    private String property;

    /** Number of bits to use for key generation */
    private int keySize = 512;


    /**
     * Set the property that the keys will be placed in.
     * A ".public" and ".private" extension will be appended
     * to the property name.  This will create two properties
     * to contain both the public and private keys.
     *
     * @param   property    Property name to be set
     */
     public void setProperty(String property) {
         this.property = property;
     }

    /**
     * Set the algorithm used to generate the key.
     *
     * @param   algorithm    Encryption algorithm to use
     */
     public void setAlgorithm(String algorithm) {
         this.algorithm = algorithm;
     }

    /**
     * Set the key size used to generate the key pairs.
     *
     * @param   size    Number of bits to use for key generation
     */
     public void setKeySize(int size) {
         keySize = size;
     }



    /**
     * Generates a public and private key and stores them in properties.
     */
    public void execute() throws BuildException {
        if (property == null) {
            throw new BuildException("Property attribute is required", 
                                     getLocation());
        }

        // Generate a secret key
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance(algorithm);
            keyGen.init(keySize);
            SecretKey key = keyGen.generateKey();
            getProject().setProperty(property, encode(key.getEncoded()));
        } catch (NoSuchAlgorithmException aex) {
            throw new BuildException("Invalid encryption algorithm: " + algorithm, getLocation());
        } catch (InvalidParameterException pex) {
            throw new BuildException("Unable to generate a secret key.  " + pex, getLocation());
        }
    
    }

}
