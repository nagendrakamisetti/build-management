package com.modeln.build.enums.release;

/**
 * Status enum
 * 
 * This enum represents the status of a given release.
 * 
 * TODO
 * - This enum should be merge with the State enum since Status and State are kind of the same things.
 * 
 * @author gzussa
 *
 */
public enum Status {
	/** Designates that the release is currently active */
	ACTIVE("active", 0),

    /** Designates that the release is no longer available */
    RETIRED("retired", 1);
	
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
