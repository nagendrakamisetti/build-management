package com.modeln.build.entity.feature.id;

/**
 * FeatureReview composite id
 * 
 * @author gzussa
 *
 */
public class FeatureReviewId {
	
	private Integer area;
	
	private Integer buildId;

	public FeatureReviewId(Integer area, Integer buildId) {
		super();
		this.area = area;
		this.buildId = buildId;
	}

	public Integer getArea() {
		return area;
	}

	public void setArea(Integer area) {
		this.area = area;
	}

	public Integer getBuildId() {
		return buildId;
	}
	
	public void setBuildId(Integer buildId) {
		this.buildId = buildId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((area == null) ? 0 : area.hashCode());
		result = prime * result + ((buildId == null) ? 0 : buildId.hashCode());
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
		FeatureReviewId other = (FeatureReviewId) obj;
		if (area == null) {
			if (other.area != null)
				return false;
		} else if (!area.equals(other.area))
			return false;
		if (buildId == null) {
			if (other.buildId != null)
				return false;
		} else if (!buildId.equals(other.buildId))
			return false;
		return true;
	}

}
