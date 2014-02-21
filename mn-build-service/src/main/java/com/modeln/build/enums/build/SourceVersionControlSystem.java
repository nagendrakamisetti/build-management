package com.modeln.build.enums.build;

/**
 * SourceVersionjControlSystem enum.
 * 
 * This enum represents source version control systems.
 * 
 * @author gzussa
 *
 */
public enum SourceVersionControlSystem {
	 PERFORCE("perforce", 0), 
	 
	 GIT("git", 1);
	 
	 private String name;
		
	 private int value;

	 private SourceVersionControlSystem(String name, int value) {
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
