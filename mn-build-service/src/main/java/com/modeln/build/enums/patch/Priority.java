package com.modeln.build.enums.patch;

/**
 * TODO
 * - add documentation.
 * 
 * @author gzussa
 *
 */
public enum Priority {
	LOW("low", 0), 
	
	MEDIUM("medium", 1),
	
	HIGH("high", 2);
	
	private String name;
	
	private int value;

	private Priority(String name, int value) {
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
