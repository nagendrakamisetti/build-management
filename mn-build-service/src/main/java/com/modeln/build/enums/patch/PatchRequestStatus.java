package com.modeln.build.enums.patch;

/**
 * TODO
 * - add documentation.
 * 
 * @author gzussa
 *
 */
public enum PatchRequestStatus {
	SAVED("saved", 0), 
	
	APPROVAL("approval", 1), 
	
	REJECTED("rejected", 2), 
	
	PENDING("pending", 3), 
	
	CANCELED("canceled", 4), 
	
	RUNNING("running", 5), 
	
	BRANCHING("branching", 6), 
	
	BRANCHED("branched", 7), 
	
	BUILDING("building", 8), 
	
	BUILT("built", 9), 
	
	FAILED("failed", 10), 
	
	COMPLETE("complete", 11), 
	
	RELEASE("release", 12);
	
	private String name;
	
	private int value;

	private PatchRequestStatus(String name, int value) {
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
