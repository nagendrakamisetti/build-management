package com.modeln.build.enums.auth;

/**
 * Status enum.
 * 
 * This enum corresponds to user account status.
 * - ACTIVE: The user account is active.
 * - INACTIVE: The user account is inactive. The user hasn't logged into the application for some time. 
 * - DELETED: The user account has been deleted. The user can't login to the application anymore.
 * - ABUSE: The user account has been hacked, is unsafe or has shown vulnerabilities that may or is a threat to the system.
 * 
 * @author gzussa
 *
 */
public enum Status {
	ACTIVE("active", 0), 
	INACTIVE("inactive", 1), 
	DELETED("deleted", 2), 
	ABUSE("abuse", 3);
	
	private String name;
	
	private int value;

	private Status(String name, int value) {
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
