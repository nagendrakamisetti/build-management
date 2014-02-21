package com.modeln.build.entity.patch;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.modeln.build.entity.auth.Group;
import com.modeln.build.enums.patch.PatchStatus;

/**
 * CREATE TABLE patch_approvers (
 *     approver_id     INT UNSIGNED    NOT NULL AUTO_INCREMENT,
 *     build_version   VARCHAR(127)    NOT NULL,
 *     group_id        INT UNSIGNED    NOT NULL REFERENCES login_group,
 *     status          ENUM('approval','complete') NOT NULL DEFAULT 'approval',
 *     INDEX (approver_id)
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
@Table(name = "patch_approvers")
@Access(AccessType.FIELD)
public class PatchApprovers {
	@Id
	@GeneratedValue
	@Column(name="approver_id")
	private Integer id;
	
	@Column(name="build_version")
	@NotNull
	private String buildVersion;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="group_id")
	private Group group;
	
	@Enumerated(EnumType.STRING)
	@NotNull
	private PatchStatus status;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getBuildVersion() {
		return buildVersion;
	}

	public void setBuildVersion(String buildVersion) {
		this.buildVersion = buildVersion;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public PatchStatus getStatus() {
		return status;
	}

	public void setStatus(PatchStatus status) {
		this.status = status;
	}
}
