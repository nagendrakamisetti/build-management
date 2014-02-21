package com.modeln.build.entity.deploy;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import com.modeln.build.entity.deploy.id.DeployMetricId;
import com.modeln.build.enums.build.Activity;

/**
 * This entity represents deployed application step achieved during the parralel build execution (After deployment steps). The primary key is a composed id based on the build_version, the machine hostname and the activity columns.
 * In other words, it mean that each of the activities defined by the Activity enum can only be logged once per server. This make sense since this table is only recording serial steps per server but parallel steps on multiple..
 * 
 * This class corresponds to the deploy_metrics table.
 * 
 * 
 * CREATE TABLE IF NOT EXISTS `deploy_metrics` (
 *   `build_version` varchar(127) NOT NULL,
 *   `username` varchar(127) NOT NULL,
 *   `hostname` varchar(127) NOT NULL,
 *   `start_date` datetime NOT NULL default '0000-00-00 00:00:00',
 *   `end_date` datetime NOT NULL default '0000-00-00 00:00:00',
 *   `activity` enum('download','propconfig','oracle.import','app.dbpostimport','app.remove','app.deploy','cognos.stop','cognos.start','web.remove','web.deploy','cognos.deploy') NOT NULL,
 *   PRIMARY KEY  (`build_version`,`hostname`,`activity`),
 *   KEY `activity_idx` (`activity`)
 * );
 * 
 * 
 * Many enum columns have been converted to String on the java side but are still mapped to Java enum through getter and setter.
 * - activity is mapped to enum com.modeln.build.enums.build.Activity.
 * 
 * end_date not null in entity since some values are null (0000-00-00 00:00:00 in database).
 * 
 * Index names have been changed by appending original name with the table table name. This is required by JPA in order to avoid error messages.
 * 
 * TODO
 * - Add build_version and event_stack field/column in db in order to link logs to a specific build.
 * - Todos for BuildMetric also applies here.
 * 
 * @author gzussa
 *
 */
@Entity
@Table(name = "deploy_metrics", indexes = { @Index(name="deploy_metrics_activity_idx", columnList="activity")})
@Access(AccessType.FIELD)
@IdClass(DeployMetricId.class)
public class DeployMetric {
	@Id
	@Column(name="build_version")
	@NotNull
	private String buildVersion;
	
	@Id
	@Column(name="activity")
	@NotNull
	private String activity;
	
	@Column(name="start_date")
	@Temporal(TemporalType.TIMESTAMP)
	@NotNull
	private Date startDate;
	
	@Column(name="end_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date endDate;
	
	@Column(name="username")
	@NotNull
	private String username;
	
	@Column(name="hostname")
	@NotNull
	private String hostname;

	public String getBuildVersion() {
		return buildVersion;
	}

	public void setBuildVersion(String buildVersion) {
		this.buildVersion = buildVersion;
	}

	public Activity getActivity() {
		return Activity.valueOf(activity.toUpperCase());
	}

	public void setActivity(Activity activity) {
		this.activity = activity.getName().toLowerCase();
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

}
