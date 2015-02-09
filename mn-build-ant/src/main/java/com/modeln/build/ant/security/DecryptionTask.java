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

import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.tools.ant.BuildException;


/**
 * Uses symetric encryption algorithms to decrypt an encrypted message.
 *
 * @author Shawn Stafford
 */
public final class DecryptionTask extends BaseTask {


    /** The cipher mode used to encrypt and decrypt messages. */
    private String mode = "CBC";

    /** The encryption algorithm used to perform the encryption. */
    private String algorithm = "DES";

    /** The cipher padding used to pad encrypted message content. */
    private String padding = "PKCS5Padding";


    /** Encryption key used to encrypt or decrypt the data. */
    private String key;

    /** Encryption key prameters used to encrypt or decrypt the data. */
    private String keyParams;

    /** The project property that will store the resulting message value. */
    private String property;

    /** Message content being encrypted or decrypted */
    private String message;



    /**
     * Set the property that the encrypted or decrypted message 
     * will be stored in.
     *
     * @param   property    Property name to be set
     */
     public void setProperty(String property) {
         this.property = property;
     }

    /**
     * Set the algorithm used to generate the encrypted message and key.
     *
     * @param   algorithm    Encryption algorithm to use
     */
     public void setAlgorithm(String algorithm) {
         this.algorithm = algorithm;
     }

    /**
     * Set the key used to encrypt or decrypt the data.
     *
     * @param   key   Encryption key
     */
     public void setKey(String key) {
         this.key = key;
     }

    /**
     * Set the key parameters used to encrypt or decrypt the data.
     *
     * @param   key   Encryption key
     */
     public void setKeyParams(String params) {
         keyParams = params;
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

        // Obtain any encryption algorithm parameters associated with the key
        AlgorithmParameters paramObj = null;
        if (keyParams != null) {
            try {
                paramObj = AlgorithmParameters.getInstance(algorithm);
                paramObj.init(decode(keyParams));
            } catch (IOException ioex) {
                throw new BuildException("Invalid encryption parameters: " + keyParams, getLocation());
            } catch (NoSuchAlgorithmException aex) {
                throw new BuildException("Invalid encryption algorithm: " + algorithm, getLocation());
            }
        }

        SecretKeySpec keySpec = new SecretKeySpec(decode(key), algorithm);
        try {
            Cipher cipher = Cipher.getInstance(algorithm + "/" + mode + "/" + padding);
            if (paramObj != null) {
                cipher.init(Cipher.DECRYPT_MODE, keySpec, paramObj);
            } else {
                cipher.init(Cipher.DECRYPT_MODE, keySpec);
            }
            byte[] decrypted = cipher.doFinal(decode(message));
            getProject().setProperty(property, new String(decrypted));
        } catch (NoSuchAlgorithmException aex) {
            throw new BuildException("Invalid encryption algorithm: " + algorithm, getLocation());
        } catch (NoSuchPaddingException pex) {
            throw new BuildException("Invalid cipher padding: " + algorithm, getLocation());
        } catch (InvalidKeyException kex) {
            throw new BuildException("Unable to initialize decryption algorithm.  " + kex.toString(), getLocation());
        } catch (Exception genex) {
            throw new BuildException("Unable to perform decryption.  " + genex.toString(), getLocation());
        }


    }


}
