package com.modeln.build.entity.feature;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.modeln.build.validation.constraints.Email;


/**
 * This entity represents feature area. A product is constitute of many features. Features are grouped per area and teams are managing each area.
 * An area correspond to a specific team with a associate email alias.
 * 
 * This class represents the feature_area table. 
 * 
 * CREATE TABLE IF NOT EXISTS `feature_area` (
 *   `area_id` int(10) unsigned NOT NULL auto_increment,
 *   `area_name` varchar(127) NOT NULL,
 *   `area_desc` varchar(255) default NULL,
 *   `email` varchar(255) default NULL,
 *   PRIMARY KEY  (`area_id`),
 *   KEY `area_idx` (`area_name`)
 * );
 * 
 * Index names have been changed by appending original name with the table table name. This is required by JPA in order to avoid error messages.
 * 
 * TODO
 * - Continue documentation.
 * 
 * @author gzussa
 * 
 */
@Entity
@Table(name = "feature_area", indexes = { @Index(name="feature_area_area_idx", columnList="area_name")})
@Access(AccessType.FIELD)
public class FeatureArea {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="area_id")
	private Integer id;
	
	@Column(name="area_name")
	@NotNull
	private String name;
	
	@Column(name="area_desc")
	private String desc;
	
	@Column(name="email")
	@Email
	private String email;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
}
