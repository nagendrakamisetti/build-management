package com.modeln.build.enums.auth;

/**
 * Role enum.
 * 
 * This enum represents roles.
 * Depending to which role a user is assigned to, it will be granted with access right corresponding to his role.
 * - ADMIN: Role is a system admin role. This role has access right on the entire system.
 * - USER: Role corresponds to application users and devices. Finer grain permissions can be defined for specific user group through Access Control List (ACL authorizations)
 * 
 * Roles help define Role Based Access Control (RBAC) authorizations.
 * 
 * @author gzussa
 *
 */
public enum Role {
	ADMIN("admin", 0), 
	USER("user", 1);

	private String name;
		
	private int value;

	private Role(String name, int value) {
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
