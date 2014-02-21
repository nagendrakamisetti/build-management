package com.modeln.build.enums.build;

/**
 * SupportStatus enum.
 * 
 * This enum represents the support status for a specific build.
 * 
 * @author gzussa
 *
 */
public enum SupportStatus {

    /** Designates a build that is still actively under support */
    ACTIVE("active", 0),

    /** Designates a build that is no longer actively under support */
    INACTIVE("inactive" ,1),

    /** Designates a build that is under extended technical support */
    EXTENDED("extended", 2);
    
    private String name;
	
	private int value;

	private SupportStatus(String name, int value) {
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
