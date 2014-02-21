package com.modeln.build.enums.patch;

/**
 * TODO
 * - add documentation.
 * 
 * @author gzussa
 *
 */
public enum GroupStatus {
	OPTIONAL("optional", 0), 
	RECOMMENDED("recommended", 1), 
	REQUIRED("required", 2);
	
	private String name;
	
	private int value;

	private GroupStatus(String name, int value) {
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
