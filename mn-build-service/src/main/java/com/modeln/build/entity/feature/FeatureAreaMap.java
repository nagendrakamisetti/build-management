package com.modeln.build.entity.feature;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * 
 * This class represents the feature_area_map table. 
 * 
 * CREATE TABLE IF NOT EXISTS `feature_area_map` (
 *   `area_id` int(10) unsigned NOT NULL,
 *   `feature` varchar(127) NOT NULL,
 *   PRIMARY KEY  (`feature`)
 * );
 * 
 * TODO
 * - Continue documentation.
 * 
 * @author gzussa
 *
 */
@Entity
@Table(name = "feature_area_map")
@Access(AccessType.FIELD)
public class FeatureAreaMap {
	@Id
	@Column(name="feature")
	@NotNull
	private String feature;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="area_id")
	@NotNull
	private FeatureArea area;

	public String getFeature() {
		return feature;
	}

	public void setFeature(String feature) {
		this.feature = feature;
	}

	public FeatureArea getArea() {
		return area;
	}

	public void setArea(FeatureArea area) {
		this.area = area;
	}
}
