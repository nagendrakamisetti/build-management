package com.modeln.build.enums.test;

/**
 * Status enums
 * 
 * This enums represents the status of a given test.
 * 
 * @author gzussa
 *
 */
public enum Status {

	/** Indicates that the completion status of the test is unknown */
    UNKNOWN("unknown", -1),

    /** Test status to indicate that the test passed */
    PASS("pass", 0),

    /** Test status to indicate that the test failed */
    FAIL("fail", 1),

    /** Test status to indicate that the test encountered an unexpected error */
    ERROR("error", 2),

    /** Test status to indicate that the test was skipped */
    SKIP("skip", 3),

    /** Test status to indicate that the test was killed */
    KILL("kill", 4),

    /** Test status to indicate that the test is running */
    RUNNING("running", 5),

    /** Test status to indicate that the test is pending execution */
    PENDING("pending", 6),

    /** Test status to indicate that test execution was blocked due to blacklisting */
    BLACKLIST("backlist" ,7);
    
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
