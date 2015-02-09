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

import java.security.KeyFactory;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.apache.tools.ant.BuildException;

/**
 * Uses the message signature and public key to verify that the 
 * message has not been tampered with.
 *
 * @author Shawn Stafford
 */
public final class KeyVerificationTask extends BaseTask {

    /** Encryption key used to encrypt or decrypt the data. */
    private String key;

    /** The encryption algorithm used to generate the signature. */
    private String sigAlgorithm = "SHA1withDSA";

    /** The encryption algorithm used to generate the keys. */
    private String keyAlgorithm = "DSA";

    /** Message signature used to verify the message. */
    private String signature;

    /** The project property that will store the result of the verification */
    private String property;

    /** Message content being signed */
    private String message;

    /**
     * Set the key used to encrypt or decrypt the data.
     *
     * @param   key   Encryption key
     */
     public void setKey(String key) {
         this.key = key;
     }

    /**
     * Set the property that the result of the verification
     * will be stored in.  If no digest value is specified to
     * verify the message, a digest will be generated an returned
     * as the value of this property.
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
     public void setKeyAlgorithm(String algorithm) {
         keyAlgorithm = algorithm;
     }

    /**
     * Set the algorithm used to generate the message signature.
     *
     * @param   algorithm    Encryption algorithm to use
     */
     public void setSigAlgorithm(String algorithm) {
         sigAlgorithm = algorithm;
     }

    /**
     * Set the digital signature used to verify the message.  If no signature
     * is specified, a signature will be generated and returned as the
     * property value.
     *
     * @param   signature   Message signature
     */
     public void setSignature(String signature) {
         this.signature = signature;
     }

    /**
     * Set the message content.
     *
     * @param   text   Message content
     */
    public void setMessage(String text) {
        message = text;
    }


    /**
     * Generates a public and private key and stores them in properties.
     */
    public void execute() throws BuildException {
        if (property == null) {
            throw new BuildException("Property attribute is required", 
                                     getLocation());
        }
        if (message == null) {
            throw new BuildException("Message attribute is required", 
                                     getLocation());
        }
        if (key == null) {
            throw new BuildException("Key attribute is required", 
                                     getLocation());
        }

        Signature sigObject = null;
        try {
            sigObject = Signature.getInstance(sigAlgorithm);
        } catch (NoSuchAlgorithmException aex) {
            throw new BuildException("Invalid signature algorithm: " + sigAlgorithm, getLocation());
        }

        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance(keyAlgorithm);
        } catch (NoSuchAlgorithmException aex) {
            throw new BuildException("Invalid key algorithm: " + keyAlgorithm, getLocation());
        }

        // If a signature has not been provided, return the generated signature
        if (signature == null) {
            // Translate the private key into a Java object
            EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decode(key));
            try {
                PrivateKey keyObj = (PrivateKey) keyFactory.generatePrivate(keySpec);
                sigObject.initSign(keyObj);
                sigObject.update(message.getBytes());
                byte[] sigData = sigObject.sign();
                getProject().setProperty(property, encode(sigData));
            } catch (SignatureException sigex) {
                throw new BuildException("Unable to sign message.  " + sigex.toString(), getLocation());
            } catch (InvalidKeyException kex) {
                throw new BuildException("Invalid encryption key.  " + kex.toString(), getLocation());
            } catch (InvalidKeySpecException spex) {
                throw new BuildException("Invalid encryption key.  " + spex.toString(), getLocation());
            }
        } else {
            // Translate the public key into a Java object
            EncodedKeySpec keySpec = new X509EncodedKeySpec(decode(key));
            try {
                // Return the result of the signature comparison
                PublicKey keyObj = (PublicKey) keyFactory.generatePublic(keySpec);
                sigObject.initVerify(keyObj);
                sigObject.update(message.getBytes());
                boolean validMessage = sigObject.verify(decode(signature));
                getProject().setProperty(property, Boolean.toString(validMessage));
            } catch (SignatureException sigex) {
                throw new BuildException("Unable to verify message.  " + sigex.toString(), getLocation());
            } catch (InvalidKeyException kex) {
                throw new BuildException("Invalid encryption key.  " + kex.toString(), getLocation());
            } catch (InvalidKeySpecException spex) {
                throw new BuildException("Invalid encryption key.  " + spex.toString(), getLocation());
            }
        }
    
    
    }
}
