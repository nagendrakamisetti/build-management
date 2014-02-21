package com.modeln.build.entity.build.id;

public class BuildMetricId {
	private String buildVersion;

	private String activity;

	public BuildMetricId(String buildVersion, String activity) {
		super();
		this.buildVersion = buildVersion;
		this.activity = activity;
	}

	public String getBuildVersion() {
		return buildVersion;
	}

	public void setBuildVersion(String buildVersion) {
		this.buildVersion = buildVersion;
	}

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((activity == null) ? 0 : activity.hashCode());
		result = prime * result
				+ ((buildVersion == null) ? 0 : buildVersion.hashCode());
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
		BuildMetricId other = (BuildMetricId) obj;
		if (activity == null) {
			if (other.activity != null)
				return false;
		} else if (!activity.equals(other.activity))
			return false;
		if (buildVersion == null) {
			if (other.buildVersion != null)
				return false;
		} else if (!buildVersion.equals(other.buildVersion))
			return false;
		return true;
	}
}
