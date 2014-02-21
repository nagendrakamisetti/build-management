package com.modeln.build.enums.patch;

/**
 * TODO
 * - add documentation.
 * 
 * @author gzussa
 *
 */
public enum NotificationStatus {
	SAVED("saved", 0), 
	APPROVAL("approval", 1),
	REJECTED("rejected", 2), 
	PENDING("pending", 3), 
	CANCELED("canceled", 4),
	RUNNING("running", 5),
	FAILED("failed", 6),
	COMPLETE("complete", 7), 
	RELEASE("release", 8);
	
	private String name;
	
	private int value;

	private NotificationStatus(String name, int value) {
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
