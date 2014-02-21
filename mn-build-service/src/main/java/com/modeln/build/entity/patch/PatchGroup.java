package com.modeln.build.entity.patch;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.modeln.build.enums.patch.GroupStatus;
 
/**
 * CREATE TABLE patch_group (
 *     group_id        INT UNSIGNED   NOT NULL auto_increment,
 *     group_name      VARCHAR(255)   NOT NULL,
 *     group_desc      TEXT,
 *     status          ENUM('optional','recommended','required') NOT NULL DEFAULT 'optional',
 *     build_version   VARCHAR(127),
 *     PRIMARY KEY (group_id)
 * ); 
 * 
 * TODO
 * - Finish documentation
 * - Fix implementation and test!
 * 
 * @author gzussa
 *
 */
@Entity
@Table(name = "patch_group")
@Access(AccessType.FIELD)
public class PatchGroup {
	@Id
	@GeneratedValue
	@Column(name="group_id")
	private Integer id;
	
	@Column(name="group_name")
	@NotNull
	private String name;
	
	@Column(name="group_desc")
	@Lob
	private String description;
	
	@Column(name="status")
	@Enumerated(EnumType.STRING)
	@NotNull
	private GroupStatus status;
	
	@Column(name="build_version")
	private String buildVersion;

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public GroupStatus getStatus() {
		return status;
	}

	public void setStatus(GroupStatus status) {
		this.status = status;
	}

	public String getBuildVersion() {
		return buildVersion;
	}

	public void setBuildVersion(String buildVersion) {
		this.buildVersion = buildVersion;
	}

}
