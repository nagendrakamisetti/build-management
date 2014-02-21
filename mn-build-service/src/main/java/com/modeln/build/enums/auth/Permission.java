package com.modeln.build.enums.auth;

/**
 * Permission enum. 
 * 
 * This enum list every permissions (actions) available. 
 * This enums is used to check authorization permissions on PermissionGroup
 * 
 *  - EDIT:Permission to edit an object entry.
 *  - ADD: Permission to add an object entry.
 *  - DELETE: Permission to delete an object entry.
 *  
 * TODO:
 * - May need to add the VIEW enum for more fine grain access control.
 * 
 * @author gzussa
 *
 */
public enum Permission {
	EDIT("edit", 0), 
	ADD("add", 1), 
	DELETE("delete", 2);
	
	private String name;
	
	private int value;

	private Permission(String name, int value) {
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
