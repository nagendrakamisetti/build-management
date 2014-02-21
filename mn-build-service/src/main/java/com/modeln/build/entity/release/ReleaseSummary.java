package com.modeln.build.entity.release;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.modeln.build.enums.release.Status;
import com.modeln.build.enums.release.State;

/**
 * This entity represents product releases and the matching build_version prefix or pattern. 
 * This entity helps to group build execution together (per business release).
 * 
 * This class represents the release_summary table. 
 * 
 * CREATE TABLE IF NOT EXISTS `release_summary` (
 *   `summary_id` int(10) unsigned NOT NULL auto_increment,
 *   `summary_order` int(10) unsigned NOT NULL default '0',
 *   `release_name` varchar(127) NOT NULL default '',
 *   `release_type` enum('incremental','nightly','stable','released') NOT NULL default 'incremental',
 *   `release_status` enum('active','retired') NOT NULL default 'active',
 *   `build_version` varchar(127) NOT NULL default '',
 *   `release_text` text,
 *   PRIMARY KEY  (`summary_id`)
 * );
 *
 * Many enum columns have been converted to String on the java side but are still mapped to Java enum through getter and setter.
 * - type is mapped to enum com.modeln.build.enums.release.Type.
 * - status is mapped to enum com.modeln.build.enums.release.Status.
 * 
 * @author gzussa
 *
 */
@Entity
@Table(name="release_summary")
@Access(AccessType.FIELD)
public class ReleaseSummary {
	
	/** Uniquely identifies a release summary. */
	@Id 
	@GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="summary_id")
    private Integer id;

    /** Display order for the release */
	
	@Column(name="summary_order")
	@NotNull
    private int order;

    /** A descriptive, project, or code name for a release.  */
	@Column(name="release_name")
	@NotNull
    private String name;

    /** A text description or message associated with the release */
	@Lob
	@Column(name="release_text")
    private String text;

    /** Designation for the summary table entry. */
	@Column(name="release_type")
	@NotNull
    private String type;

    /** Status of the release. */
	@Column(name="release_status")
	@NotNull
    private String status;

    /** Name of the build version associated with this release. */
	@Column(name="build_version")
	@NotNull
    private String buildVersion;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public State getType() {
		return State.valueOf(type.toUpperCase());
	}

	public void setType(State state) {
		this.type = state.getName().toLowerCase();
	}

	public Status getStatus() {
		return Status.valueOf(status.toUpperCase());
	}

	public void setStatus(Status status) {
		this.status = status.getName().toLowerCase();
	}

	public String getBuildVersion() {
		return buildVersion;
	}

	public void setBuildVersion(String buildVersion) {
		this.buildVersion = buildVersion;
	}

}
