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

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

import org.apache.tools.ant.BuildException;

/**
 * Generates a public and private key pair.
 *
 * @author Shawn Stafford
 */
public final class KeyPairGeneratorTask extends BaseTask {


    /** 
     * Suffix that will be appended to the property name when storing a 
     * public key value.
     */
    public static final String PUBLIC_KEY_SUFFIX = ".public";

    /** 
     * Suffix that will be appended to the property name when storing a 
     * private key value.
     */
    public static final String PRIVATE_KEY_SUFFIX = ".private";

    /** The encryption algorithm used to generate the keys. */
    private String algorithm = "DSA";

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
     * Set the algorithm used to generate the key pairs.
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

        // Generate the public and private keys
        KeyPair keyPair = null;
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance(algorithm);
            keyGen.initialize(keySize);
            keyPair = keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException aex) {
            throw new BuildException("Invalid encryption algorithm: " + algorithm, getLocation());
        }

        // Set the public and private key properties
        String publicKey = getProject().getProperty(property + PUBLIC_KEY_SUFFIX);
        String privateKey = getProject().getProperty(property + PRIVATE_KEY_SUFFIX);
        if (publicKey != null) {
            throw new BuildException(
                "Property already exists and must not be set again: " + 
                property + PUBLIC_KEY_SUFFIX, getLocation());
        } else if (privateKey != null) {
            throw new BuildException(
                "Property already exists and must not be set again: " + 
                property + PRIVATE_KEY_SUFFIX, getLocation());
        } else {
            byte[] publicKeyArray = keyPair.getPublic().getEncoded();
            byte[] privateKeyArray = keyPair.getPrivate().getEncoded();
            publicKey = bytesToHex(publicKeyArray);
            privateKey = bytesToHex(privateKeyArray);
            getProject().setProperty(property + PUBLIC_KEY_SUFFIX, publicKey);
            getProject().setProperty(property + PRIVATE_KEY_SUFFIX, privateKey);
        }

    }

}
