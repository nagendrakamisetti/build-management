package com.modeln.build.enums.patch;

/**
 * TODO
 * - add documentation.
 * 
 * @author gzussa
 *
 */
public enum CommentVisibility {
	SHOW("show", 0), 
	
	HIDE("hide", 1), 
	
	ADMIN("admin", 2);
	
	private String name;
	
	private int value;

	private CommentVisibility(String name, int value) {
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
