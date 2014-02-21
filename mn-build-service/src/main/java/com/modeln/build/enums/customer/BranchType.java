package com.modeln.build.enums.customer;

/**
 * TODO
 * - add documentation.
 * 
 * @author gzussa
 *
 */
public enum BranchType {
	CUSTOMER("customer", 0),
	PRODUCT("product", 1);
	
	private String name;
	
	private int value;

	private BranchType(String name, int value) {
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
