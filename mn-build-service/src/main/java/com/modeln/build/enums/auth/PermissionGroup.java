package com.modeln.build.enums.auth;

/**
 * PermissionGroup enum. 
 * 
 * This enum defined Permission groups. Permission groups define objects a user can have permission over.
 * Permission Groups are defined at the group level but user belong to at least one group.
 * - SELF: Corresponds to permissions a user can have on his own account.
 * - GROUP: Corresponds to permissions a user can have on user that belong to the same group (or child group of his own group).
 * - USER: Corresponds to permissions a user can have on all users.
 * - DEVICE: Corresponds to permission a user can have on listed devices. 
 * 
 * Permission group help define 'Objects' for Access Control list (ACL) authorizations.
 * 
 * @author gzussa
 *
 */
public enum PermissionGroup {
	SELF("self", 0), 
	GROUP("group", 1), 
	USER("user", 2),  
	DEVICE("device", 3); 
	
	private String name;
	
	private int value;

	private PermissionGroup(String name, int value) {
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
