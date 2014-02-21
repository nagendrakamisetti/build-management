package com.modeln.build.enums.auth;

/**
 * Title enum.
 * 
 * This enum specify person titles (Mr, Ms and Mrs)
 * 
 * TODO
 * - Add more titles
 * 
 * @author gzussa
 *
 */
public enum Title {
	MR("Mr", 0), 
	MS("Ms", 1), 
	MRS("Mrs", 2);
	
	private String name;
	
	private int value;

	private Title(String name, int value) {
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
