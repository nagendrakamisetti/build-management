package com.modeln.build.enums.build;

/**
 * Status enum.
 * 
 * This enum represents the build status.
 * 
 * @author gzussa
 *
 */
public enum Status {
	
	/** Designates a build that passed the build criteria. */
    PASSING("passing", 0),

    /** Designates a build that was verified on the verification environment. */
    VERIFIED("verified", 1),

    /** Designates a build that was designated as a stable development build. */
    STABLE("stable", 2),

    /** Designates a build that was tested by a QA team */
    TESTED("tested", 3),

    /** Designates a build that was approved for release. */
    RELEASED("released", 4);
    
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
