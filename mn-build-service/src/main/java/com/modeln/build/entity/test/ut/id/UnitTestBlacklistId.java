package com.modeln.build.entity.test.ut.id;

/**
 * UnitTestBlacklist composite id
 * 
 * @author gzussa
 *
 */
public class UnitTestBlacklistId {
	private String testClass;
	
	private String testMethod;
	
	private String versionControlRoot;

	public UnitTestBlacklistId(String testClass, String testMethod,
			String versionControlRoot) {
		super();
		this.testClass = testClass;
		this.testMethod = testMethod;
		this.versionControlRoot = versionControlRoot;
	}

	public String getTestClass() {
		return testClass;
	}

	public void setTestClass(String testClass) {
		this.testClass = testClass;
	}

	public String getTestMethod() {
		return testMethod;
	}

	public void setTestMethod(String testMethod) {
		this.testMethod = testMethod;
	}

	public String getVersionControlRoot() {
		return versionControlRoot;
	}

	public void setVersionControlRoot(String versionControlRoot) {
		this.versionControlRoot = versionControlRoot;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((testClass == null) ? 0 : testClass.hashCode());
		result = prime * result
				+ ((testMethod == null) ? 0 : testMethod.hashCode());
		result = prime
				* result
				+ ((versionControlRoot == null) ? 0 : versionControlRoot
						.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UnitTestBlacklistId other = (UnitTestBlacklistId) obj;
		if (testClass == null) {
			if (other.testClass != null)
				return false;
		} else if (!testClass.equals(other.testClass))
			return false;
		if (testMethod == null) {
			if (other.testMethod != null)
				return false;
		} else if (!testMethod.equals(other.testMethod))
			return false;
		if (versionControlRoot == null) {
			if (other.versionControlRoot != null)
				return false;
		} else if (!versionControlRoot.equals(other.versionControlRoot))
			return false;
		return true;
	}
	
	
}
