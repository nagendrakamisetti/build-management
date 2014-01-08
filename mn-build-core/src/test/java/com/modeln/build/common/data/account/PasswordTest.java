package com.modeln.build.common.data.account;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Tests for {@link Password}.
 *
 * @author sstafford@modeln.com (Shawn Stafford)
 */
@RunWith(JUnit4.class)
public class PasswordTest {

    private static final String UNENCRYPTED_PASSWORD = "123ThisIsATest";
    private static final String INVALID_PASSWORD     = "ThisDoesNotMatch";

    @Test
    public void compareCrypt() {
        String encrypted = Password.getCrypt(UNENCRYPTED_PASSWORD);
        boolean match = false;

        // Ensure that the original password can be matched to the encrypted password
        match = Password.matchesCrypt(UNENCRYPTED_PASSWORD, encrypted);
        assertTrue("Encrypted passwords do not match: encrypted=" + encrypted, match);

        // Ensure that an invalid password is not matched
        match = Password.matchesCrypt(INVALID_PASSWORD, encrypted); 
        assertFalse("Password should be rejected: " + UNENCRYPTED_PASSWORD + " != " + INVALID_PASSWORD, match);
    }

    @Test
    public void compareMD5() {
        String encrypted = Password.getMD5(UNENCRYPTED_PASSWORD);

        // Ensure that the original password can be matched to the encrypted password
        boolean match = Password.matchesMD5(UNENCRYPTED_PASSWORD, encrypted);
        assertTrue("Encrypted passwords do not match: encrypted=" + encrypted, match);

        // Ensure that an invalid password is not matched
        match = Password.matchesMD5(INVALID_PASSWORD, encrypted);            
        assertFalse("Password should be rejected: " + UNENCRYPTED_PASSWORD + " != " + INVALID_PASSWORD, match);
    }

    @Test
    public void comparePBKDF2() {
        String encrypted = Password.getPBKDF2(UNENCRYPTED_PASSWORD);

        // Ensure that the original password can be matched to the encrypted password
        boolean match = Password.matchesPBKDF2(UNENCRYPTED_PASSWORD, encrypted);
        assertTrue("Encrypted passwords do not match: encrypted=" + encrypted, match);

        // Ensure that an invalid password is not matched
        match = Password.matchesPBKDF2(INVALID_PASSWORD, encrypted);  
        assertFalse("Password should be rejected: " + UNENCRYPTED_PASSWORD + " != " + INVALID_PASSWORD, match);
    }

    @Test
    @Ignore
    public void thisIsIgnored() {
    }
}
