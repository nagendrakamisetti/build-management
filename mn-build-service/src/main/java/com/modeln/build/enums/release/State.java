package com.modeln.build.enums.release;
/**
 * State Enum
 * 
 * This enum represents the State of a specific release.
 * 
 * TODO
 * - This enum should be merge with the Status enum since Status and State are kind of the same things.
 * @author gzussa
 *
 */
public enum State {
	/** Designates that the release type is unknown. */
    UNKNOWN("unknown", 0),

    /** Designates a release that is built nightly. */
    NIGHTLY("nightly", 1),

    /** Designates a release that is built on an incremental basis throughout the day. */
    INCREMENTAL("incremental", 2),

    /** Designates a release that has been designated as stable but not yet released. */
    STABLE("stable", 3),

    /** Designates a release that has been tested and designated for release. */
    RELEASED("released", 4);    
	
	private String name;
	
	private int value;

	private State(String name, int value) {
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
