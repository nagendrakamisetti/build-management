package com.modeln.build.enums.build;

/**
 * EventLevel enum.
 * 
 * This enum represents logs level pushed to the build_event and deploy_event tables.
 * 
 * @author gzussa
 *
 */
public enum EventLevel {
	/** Parse events at the debug level */
    DEBUG("debug", 0),

    /** Parse events at the error level */
    ERROR("error", 1),

    /** Parse events at the info level */
    INFORMATION("information", 2),

    /** Parse events at the verbose level */
    VERBOSE("verbose", 3),

    /** Parse events at the warn level */
    WARNING("warning", 4);
    
    private String name;
	
	private int value;

	private EventLevel(String name, int value) {
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
