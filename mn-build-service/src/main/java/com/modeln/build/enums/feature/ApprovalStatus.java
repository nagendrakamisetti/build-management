package com.modeln.build.enums.feature;

/**
 * ApprovalStatus enum.
 * 
 * This enum represents the approval state of a given feature.
 * 
 * @author gzussa
 *
 */
public enum ApprovalStatus {
	APPROVED("approved", 0),
	
	REJECTED("rejected", 1);
	
	private String name;
	
	private int value;

	private ApprovalStatus(String name, int value) {
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
