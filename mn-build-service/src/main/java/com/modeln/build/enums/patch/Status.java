package com.modeln.build.enums.patch;

/**
 * TODO
 * - add documentation.
 * 
 * @author gzussa
 *
 */
public enum Status {
	APPROVED("approved", 0), 
	
	REJECTED("rejected", 1);
	
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
