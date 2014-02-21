package com.modeln.build.enums.auth;

/**
 * Encryption enum.
 *  
 * This enum is used to specify which encryption algorithm has been used to encrypt a password.
 * - CRYPT: Use the MySQL ENCRYPT function (Same as the Unix crypt function).
 * - MD5: Use the MD5 message-digest algorithm.
 * - PBKDF2: Use the Password-Based Key Derivation Function 2 algorithm.
 * 
 * @author gzussa
 *
 */
public enum Encryption {
//	UNENCRYPTED("", 0), 
	CRYPT("crypt", 1), 
	MD5("md5", 2), 
	PBKDF2("pbkdf2", 3);
	
	private String name;
	
	private int value;

	private Encryption(String name, int value) {
		this.name = name;
		this.value = value;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

}
