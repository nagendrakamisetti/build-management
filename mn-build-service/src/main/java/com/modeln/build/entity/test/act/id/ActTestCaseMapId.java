package com.modeln.build.entity.test.act.id;

/**
 * ActTestCaseMap composite id
 * 
 * @author gzussa
 *
 */
public class ActTestCaseMapId {
	private Integer act;
	
	private String testcase;

	public ActTestCaseMapId(Integer act, String testcase) {
		super();
		this.act = act;
		this.testcase = testcase;
	}

	public Integer getAct() {
		return act;
	}

	public void setAct(Integer act) {
		this.act = act;
	}

	public String getTestcase() {
		return testcase;
	}

	public void setTestcase(String testcase) {
		this.testcase = testcase;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((act == null) ? 0 : act.hashCode());
		result = prime * result
				+ ((testcase == null) ? 0 : testcase.hashCode());
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
		ActTestCaseMapId other = (ActTestCaseMapId) obj;
		if (act == null) {
			if (other.act != null)
				return false;
		} else if (!act.equals(other.act))
			return false;
		if (testcase == null) {
			if (other.testcase != null)
				return false;
		} else if (!testcase.equals(other.testcase))
			return false;
		return true;
	}

}
