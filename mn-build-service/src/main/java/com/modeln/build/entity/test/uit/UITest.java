package com.modeln.build.entity.test.uit;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import com.modeln.build.enums.test.Status;

/**
 * CREATE TABLE uit (
 *     suite_id        INT UNSIGNED    NOT NULL REFERENCES uit_suite(suite_id),
 *     test_id         INT UNSIGNED    NOT NULL auto_increment,
 *     test_group_name VARCHAR(127),
 *     start_date      DATETIME        NOT NULL DEFAULT '0000-00-00 00:00:00',
 *     end_date        DATETIME        NOT NULL DEFAULT '0000-00-00 00:00:00',
 *     app_username    VARCHAR(127)    NOT NULL,
 *     step_count      INT UNSIGNED    NOT NULL,
 *     success_count   INT UNSIGNED    NOT NULL,
 *     failure_count   INT UNSIGNED    NOT NULL,
 *     message         TEXT,
 *     status          ENUM("PASS", "FAIL", "ERROR", "SKIP", "KILL", "PENDING", "RUNNING", "BLACKLIST") NOT NULL,
 *     INDEX suite_idx(suite_id),
 *     INDEX status_idx(status),
 *     INDEX group_idx(test_group_name),
 *     PRIMARY KEY (test_id)
 * );
 * 
 * CREATE TABLE IF NOT EXISTS `uit` (
 *   `suite_id` int(11) NOT NULL default '0',
 *   `test_id` int(11) NOT NULL auto_increment,
 *   `test_name` varchar(127) NOT NULL default '',
 *   `start_date` datetime NOT NULL default '0000-00-00 00:00:00',
 *   `end_date` datetime NOT NULL default '0000-00-00 00:00:00',
 *   `app_username` varchar(127) NOT NULL default '',
 *   `step_count` int(10) unsigned NOT NULL default '0',
 *   `success_count` int(10) unsigned NOT NULL default '0',
 *   `failure_count` int(10) unsigned NOT NULL default '0',
 *   `message` text,
 *   `status` enum('PASS','FAIL','ERROR','SKIP','KILL','PENDING','RUNNING','BLACKLIST') NOT NULL default 'PASS',
 *   PRIMARY KEY  (`test_id`),
 *   KEY `suite_idx` (`suite_id`),
 *   KEY `status_idx` (`status`)
 * );
 * 
 * Index names have been changed by appending original name with the table table name. This is required by JPA in order to avoid error messages.
 * 
 * @author gzussa
 *
 */

@Entity
@Table(name = "uit", indexes = { 
		@Index(name="uit_suite_idx", columnList="suite_id"),
		/*@Index(name="uit_group_idx", columnList="test_group_name"),*/
		@Index(name="uit_status_idx", columnList="status")})
@Access(AccessType.FIELD)
public class UITest {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="test_id")
	private Integer id;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="suite_id")
//	@NotNull
	private UITestSuite suite;
	
//	@Column(name="test_group_name")
//	private String groupName;
	
	@Column(name="start_date")
	@Temporal(TemporalType.TIMESTAMP)
	@NotNull
	private Date startDate;
	
	@Column(name="end_date")
	@Temporal(TemporalType.TIMESTAMP)
	@NotNull
	private Date endDate;
	
	@Column(name="app_username")
	@NotNull
	private String appUsername;
	
	@Column(name="step_count")
	@NotNull
	private Integer stepCount;
	
	@Column(name="success_count")
	@NotNull
	private Integer successCount;
	
	@Column(name="failure_count")
	@NotNull
	private Integer failureCount;
	
	@Column(name="message")
	@Lob
	private String message;
	
	@Enumerated(EnumType.STRING)
	@NotNull
	private Status status;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public UITestSuite getSuite() {
		return suite;
	}

	public void setSuite(UITestSuite suite) {
		this.suite = suite;
	}

//	public String getGroupName() {
//		return groupName;
//	}
//
//	public void setGroupName(String groupName) {
//		this.groupName = groupName;
//	}

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

	public String getAppUsername() {
		return appUsername;
	}

	public void setAppUsername(String appUsername) {
		this.appUsername = appUsername;
	}

	public Integer getStepCount() {
		return stepCount;
	}

	public void setStepCount(Integer stepCount) {
		this.stepCount = stepCount;
	}

	public Integer getSuccessCount() {
		return successCount;
	}

	public void setSuccessCount(Integer successCount) {
		this.successCount = successCount;
	}

	public Integer getFailureCount() {
		return failureCount;
	}

	public void setFailureCount(Integer failureCount) {
		this.failureCount = failureCount;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}
	
}
