package com.modeln.build.entity.deploy;

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

import com.modeln.build.enums.build.EventLevel;

/**
 * This entity represents deploy event criterias.
 * Deploy Event criterias are pattern used to parse deploy events in search for specific informations.
 * 
 * This class represents the deploy_event_criteria table.
 * 
 * CREATE TABLE IF NOT EXISTS `deploy_event_criteria` (
 *   `criteria_id` int(10) unsigned NOT NULL auto_increment,
 *   `progress_id` int(10) unsigned NOT NULL default '0',
 *   `event_severity` enum('debug','verbose','information','warning','error') NOT NULL default 'debug',
 *   `criteria_text` varchar(255) NOT NULL default '',
 *   PRIMARY KEY  (`criteria_id`),
 *   KEY `severity_idx` (`event_severity`),
 *   KEY `progress_idx` (`progress_id`)
 * );
 * 
 * Many enum columns have been converted to String on the java side but are still mapped to Java enum through getter and setter.
 * - severity is mapped to enum com.modeln.build.enums.build.EventLevel.
 * 
 * This build_version is of String type in the Java class since build.build_version can't be used as FK since values are not unique. Moreover build_version is not the build table primary key.
 * 
 * Index names have been changed by appending original name with the table table name. This is required by JPA in order to avoid error messages.
 * 
 * TODO
 * - Add build_version and event_stack field/column in db in order to link logs to a specific build.
 * - Todos for BuildEventCriteria also applies here.
 * 
 * @author gzussa
 * 
 */
@Entity
@Table(name="deploy_event_criteria", indexes={
		@Index(name="deploy_event_criteria_severity_idx", columnList="event_severity")/*,
		@Index(name="deploy_event_criteria_build_idx", columnList="build_version")*/})
@Access(AccessType.FIELD)
public class DeployEventCriteria {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="criteria_id")
	private Integer id;
	
//	@Column(name="build_version")
//	@NotNull
//	private String buildVersion;
	
	@Column(name="event_severity")
	@NotNull
	private String severity;
	
//	@Column(name="criteria_group")
//	@NotNull
//	private String group;
	
//	@Column(name="ant_target")
//	@NotNull
//	private String antTarget;
	
	@Column(name="criteria_text")
	@NotNull
	private String text;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

//	public String getBuildVersion() {
//		return buildVersion;
//	}
//
//	public void setBuildVersion(String buildVersion) {
//		this.buildVersion = buildVersion;
//	}

	public EventLevel getSeverity() {
		return EventLevel.valueOf(severity.toUpperCase());
	}

	public void setSeverity(EventLevel severity) {
		this.severity = severity.getName().toLowerCase();
	}

//	public String getGroup() {
//		return group;
//	}
//
//	public void setGroup(String group) {
//		this.group = group;
//	}

//	public String getAntTarget() {
//		return antTarget;
//	}
//
//	public void setAntTarget(String antTarget) {
//		this.antTarget = antTarget;
//	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
